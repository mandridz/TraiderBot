package ru.msk.ehome.mining.traiderbot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.msk.ehome.mining.traiderbot.db.MongoDbDriver;
import ru.msk.ehome.mining.traiderbot.entity.ConfigEntity;
import ru.msk.ehome.mining.traiderbot.entity.CurrencyEntity;
import ru.msk.ehome.mining.traiderbot.entity.RankEntity;
import ru.msk.ehome.mining.traiderbot.entity.WalletDbEntity;
import ru.msk.ehome.mining.traiderbot.stock.ExchangeRate;
import ru.msk.ehome.mining.traiderbot.stock.Poloniex;
import ru.msk.ehome.mining.traiderbot.thingspeak.Thingspeak;
import ru.msk.ehome.mining.traiderbot.util.Formatter;

public class TraiderBot {
	private static final String STR_LOG_STARTED = "TraiderBot started";
	private static final String STR_LOG_END_OF_ITERRATION = "End of Iterration";
	private static final String STR_LOG_READ_CONFIG_PROPERTIES = "READ CONFIGURATION PROPERTIES";
	private static final String STR_LOG_CLOSE_OPEN_ORDER = "CLOSE OPEN ORDERS";
	private static final String STR_LOG_CURRENCY_ENTITY_LIST = "MAKE CURRENCY ENTITY LIST";
	private static final String STR_LOG_MAKE_BID_LIST = "MAKE BID-LIST";
	private static final String STR_LOG_GET_RANK_LIST = "Get Rank-List";
	private static final String STR_LOG_MAKE_ASK_LIST = "MAKE ASK-LIST";
	private static final String STR_LOG_SEND_TO_THINGSPEAK = "SEND TO THINGSPEAK";
	private static final String STR_LOG_PLACE_LIMIT_ASK_LIST = "PLACE LIMIT ASK-LIST";
	private static final String STR_LOG_PLACE_LIMIT_BID_LIST = "PLACE LIMIT BID-LIST";

	private static final String STR_LOG_OK = " - COMPLETED";
	private static final String STR_LOG_NOT_CANCELED_ORDER = " - No Canceled";
	private static final String STR_LOG_NO_OPEN_ORDERS = "--- There are no open orders";
	private static final String STR_LOG_NO_WALLETS = "--- There are no wallets";
	private static final String STR_LOG_NO_BID_LIST = "--- There are no currencies for Bid-List";
	private static final String STR_LOG_NO_RANKS = "--- There are no currencies for Rank-List";
	private static final String STR_LOG_NO_RECALCULATED_RANKS = "--- There are no currencies for recalculate Rank-List";
	private static final String STR_LOG_NO_ASK_LIST = "--- There are no currencies for Ask-List";
	private static final String STR_LOG_NORMALIZE_RANK = "Normalize Rank-List";
	private static final String STR_LOG_RECALCULATE_AND_NORMALIZE_RANK = "Recalculate & Normalize ranks";
	private static final String STR_LOG_BTC_ACCOUNT_BALANCE = "BTC Account balance: ";
	private static final String STR_LOG_LIMIT_ORDER = "Limit Order Id: %s - COMPLETED";
	private static final String STR_LOG_NO_ORDERS = "--- No orders";

	private final static Logger LOG = LoggerFactory.getLogger(TraiderBot.class);

	private static Exchange stock = Poloniex.getInstance().getExchange();

	@SuppressWarnings("unchecked")
	private static <E extends Exception> void throwActualException(Exception exception) throws E {
		throw (E) exception;
	}

	public static void main(String[] args) {
		// Чтение параметров из конфигурационного файла
		Formatter.printMsg(STR_LOG_READ_CONFIG_PROPERTIES);
		Formatter.printConfiguration();

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		Runnable periodicTask = new Runnable() {
			public void run() {
				Formatter.printMsg(STR_LOG_STARTED);

				try {
					makeProcessing();
				} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | ExchangeException
						| IOException e) {
					e.printStackTrace();
				}

				Formatter.printMsg(STR_LOG_END_OF_ITERRATION);
			}
		};

		executor.scheduleAtFixedRate(periodicTask, 0, ConfigEntity.getInstance().getShedulerTimeout(),
				TimeUnit.MINUTES);
	}

	private static void makeProcessing() throws NotAvailableFromExchangeException,
			NotYetImplementedForExchangeException, ExchangeException, IOException {
		// Закрытие ордеров на продажу
		Formatter.printMsg(STR_LOG_CLOSE_OPEN_ORDER);
		closeOpenOrders();

		// Формирование списка валют и синхронизация с базой
		Formatter.printMsg(STR_LOG_CURRENCY_ENTITY_LIST);
		List<CurrencyEntity> currencyEntityList = getCurrencyEntityList();

		// Формирование списка валют на продажу
		Formatter.printMsg(STR_LOG_MAKE_ASK_LIST);
		List<CurrencyEntity> bidList = makeAskList(currencyEntityList);

		// Формирование списка валют на покупку
		Formatter.printMsg(STR_LOG_MAKE_BID_LIST);
		List<CurrencyEntity> askList = makeBidList(currencyEntityList);

		// Выставление ордеров на продажу
		Formatter.printMsg(STR_LOG_PLACE_LIMIT_ASK_LIST);
		placeLimitOrders(bidList, OrderType.ASK);

		// Выставление ордеров на покупку
		Formatter.printMsg(STR_LOG_PLACE_LIMIT_BID_LIST);
		placeLimitOrders(askList, OrderType.BID);

		// Отправка данных в Thingspeak
		if (ConfigEntity.getInstance().isSendToThingSpeak()) {
			Formatter.printMsg(STR_LOG_SEND_TO_THINGSPEAK);
			Thingspeak thingspeak = new Thingspeak();
			thingspeak.sendData(getTotalBtcUsdValueMap(currencyEntityList));
		}
	}

	// Закрытие ордеров на прожу
	private static void closeOpenOrders() throws NotAvailableFromExchangeException,
			NotYetImplementedForExchangeException, ExchangeException, IOException {
		TradeService tradeService = stock.getTradeService();

		OpenOrdersParams openOrdersParams = tradeService.createOpenOrdersParams();

		OpenOrders openOrders = tradeService.getOpenOrders(openOrdersParams);

		if (openOrders.getOpenOrders().size() == 0) {
			LOG.info(STR_LOG_NO_OPEN_ORDERS);
		} else {
			openOrders.getOpenOrders().forEach(openOrder -> {
				try {
					boolean isCanceled = tradeService.cancelOrder(openOrder.getId());

					LOG.info(openOrder.toString() + (isCanceled ? STR_LOG_OK : STR_LOG_NOT_CANCELED_ORDER));

				} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | ExchangeException
						| IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	// Список валют в кошельке на бирже
	private static List<CurrencyEntity> getCurrencyEntityList() throws NotAvailableFromExchangeException,
			NotYetImplementedForExchangeException, ExchangeException, IOException {
		List<CurrencyEntity> currencyEntityList = new ArrayList<CurrencyEntity>();

		MongoDbDriver mongo = new MongoDbDriver();

		// Список валют из БД
		List<WalletDbEntity> walletDbEntityList = mongo.getWalletDbEntityList();

		AccountInfo accountInfo = stock.getAccountService().getAccountInfo();

		// Цикл по валютам на бирже
		if (accountInfo.getWallets().size() > 0) {
			accountInfo.getWallets().entrySet().forEach(wallet -> {
				wallet.getValue().getBalances().entrySet().forEach(balance -> {
					CurrencyEntity currencyEntity = new CurrencyEntity();

					Currency currency = balance.getValue().getCurrency();
					BigDecimal value = wallet.getValue().getBalance(currency).getTotal();
					BigDecimal rateBtcUsd = ExchangeRate.getExchangeRate(stock, Currency.USDT, Currency.BTC);

					currencyEntity.setCurrency(currency);
					currencyEntity.setValue(value);

					// Если валюта BTC
					if (currency.equals(Currency.BTC)) {
						// Сумма в USD
						BigDecimal usdValue = value.multiply(rateBtcUsd).setScale(2, RoundingMode.HALF_UP);

						currencyEntity.setBtcValue(value);
						currencyEntity.setUsdValue(usdValue);

						currencyEntityList.add(currencyEntity);

						// Formatter.printCurrencyEntity(currencyEntity);
					} else {
						// Если валюта не BTC и ее баланс больше нуля
						if (value.compareTo(BigDecimal.ZERO) == 1) {
							// Сумма в BTC
							BigDecimal btcValue = ExchangeRate.getExchangeRate(stock, currency, Currency.BTC)
									.multiply(value).setScale(8, RoundingMode.HALF_UP);
							// Сумма в USD
							BigDecimal usdValue = btcValue.multiply(rateBtcUsd).setScale(2, RoundingMode.HALF_UP);

							// Валютная пара
							CurrencyPair currencyPair = stock.getExchangeSymbols().stream()
									.filter(curr -> curr.contains(currency) && curr.contains(Currency.BTC)).findFirst()
									.orElse(null);

							// Поиск текущей валюты в БД
							WalletDbEntity walletDbEntity = walletDbEntityList.stream()
									.filter(walletDb -> walletDb.getCurrency().equals(currency)).findFirst()
									.orElse(null);

							// Инициализация значений стоимости покупки и даты покупки
							BigDecimal purchasePrice = BigDecimal.ZERO;
							LocalDateTime purchaseDt = LocalDateTime.now(ZoneOffset.ofHours(3));

							// Если валюта есть в БД
							if (walletDbEntity != null) {
								purchasePrice = walletDbEntity.getPurchasePrice();
								purchaseDt = walletDbEntity.getPurchaseDt();
							} else {
								purchasePrice = ExchangeRate.getExchangeRate(stock, currency, Currency.BTC);

								// Добавление сущности в базу
								walletDbEntity = new WalletDbEntity();
								walletDbEntity.setCurrency(currency);
								walletDbEntity.setPurchasePrice(purchasePrice);
								walletDbEntity.setPurchaseDt(purchaseDt);

								mongo.save(walletDbEntity);
							}

							try {
								// Основные показатели валюты на бирже
								Ticker ticker = stock.getMarketDataService().getTicker(currencyPair);

								BigDecimal ask = ticker.getAsk();
								BigDecimal bid = ticker.getBid();
								BigDecimal low = ticker.getLow();
								BigDecimal last = ticker.getLast();
								BigDecimal high = ticker.getHigh();
								BigDecimal profitPrice = purchasePrice
										.multiply(BigDecimal.valueOf(ConfigEntity.getInstance().getProfit() / 100 + 1))
										.setScale(8, RoundingMode.HALF_UP);
								BigDecimal realProfit = ask
										.divide(purchasePrice.multiply(BigDecimal.valueOf(.01)), 8,
												RoundingMode.HALF_UP)
										.subtract(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
								currencyEntity.setRealProfit(realProfit);
								long duration = Duration.between(purchaseDt, LocalDateTime.now(ZoneOffset.ofHours(3)))
										.toMillis();

								currencyEntity.setBtcValue(btcValue);
								currencyEntity.setUsdValue(usdValue);
								currencyEntity.setCurrencyPair(currencyPair);
								currencyEntity.setPurchasePrice(purchasePrice);
								currencyEntity.setPurchaseDt(purchaseDt);
								currencyEntity.setAsk(ask);
								currencyEntity.setBid(bid);
								currencyEntity.setLow(low);
								currencyEntity.setLast(last);
								currencyEntity.setHigh(high);
								currencyEntity.setProfitPrice(profitPrice);
								currencyEntity.setRealProfit(realProfit);
								currencyEntity.setDuration(duration);

								currencyEntityList.add(currencyEntity);

								// Formatter.printCurrencyEntity(currencyEntity);
							} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException
									| ExchangeException | IOException e) {
								throwActualException(e);
							}
						}
					}
				});
			});

			// Сортировка по доходности
			currencyEntityList.sort(Comparator.comparing(CurrencyEntity::getRealProfit,
					Comparator.nullsLast(Comparator.naturalOrder())));

			// Очистка БД от проданных валют
			mongo.getWalletDbEntityList().forEach(walletDb -> {
				CurrencyEntity currencyEntity = currencyEntityList.stream()
						.filter(entity -> entity.getCurrency().equals(walletDb.getCurrency())).findFirst().orElse(null);

				if (currencyEntity == null) {
					mongo.delete(walletDb);
				}
			});
		}

		if (currencyEntityList.size() > 0) {
			currencyEntityList.forEach(entity -> Formatter.printCurrencyEntity(entity));
		} else {
			LOG.info(STR_LOG_NO_WALLETS);
		}

		return currencyEntityList;
	}

	// Список ордеров на покупку
	private static List<CurrencyEntity> makeAskList(List<CurrencyEntity> currencyEntityList) {
		List<CurrencyEntity> orderEntityList = new ArrayList<CurrencyEntity>();

		currencyEntityList.forEach(entity -> {
			// Проверка суммы на минимальное значение для ордера
			// Добавление валюты в список ордеров на продажу, если это не BTC
			// if
			// (entity.getBtcValue().compareTo(BigDecimal.valueOf(ConfigEntity.getInstance().getOrderMin()))
			// == 1
			// && entity.getCurrency() != Currency.BTC) {
			if (entity.getCurrency() != Currency.BTC) {
				if (entity.getAsk().compareTo(entity.getProfitPrice()) != -1) {
					orderEntityList.add(entity);

					Formatter.printBid(entity, false);
				} else {
					LocalDateTime nowDt = LocalDateTime.now(ZoneOffset.ofHours(3));
					LocalDateTime oldDt = entity.getPurchaseDt()
							.plus(Period.ofDays(ConfigEntity.getInstance().getTimeLimit()));

					if (nowDt.compareTo(oldDt) > 0) {
						orderEntityList.add(entity);

						Formatter.printBid(entity, true);
					}
				}
			}
		});

		if (orderEntityList.size() == 0) {
			LOG.info(STR_LOG_NO_ASK_LIST);
		}

		return orderEntityList;
	}

	// Формирование Rank-листа
	private static List<RankEntity> getRankEntityList(List<CurrencyEntity> currencyEntityList) {
		List<RankEntity> rankEntityList = new ArrayList<RankEntity>();

		// Получение списка валютных пар с биржи
		List<CurrencyPair> currencyPairList = stock.getExchangeSymbols().stream()
				.filter(currencyPair -> currencyPair.contains(Currency.BTC) && !currencyPair.contains(Currency.USDT))
				.collect(Collectors.toList());

		// Добавление валют в rankEntityList
		currencyPairList.forEach(currencyPair -> {
			try {
				Ticker ticker = stock.getMarketDataService().getTicker(currencyPair);

				// Среднее значение стоимости валюты на бирже на данный момент
				BigDecimal average = ticker.getAsk().add(ticker.getBid()).divide(BigDecimal.valueOf(2), 8,
						RoundingMode.HALF_UP);

				// Добавление валюты в список, стоимость которой > satoshiLimit
				if (average.compareTo(ConfigEntity.getInstance().getSatoshiLimit()) == 1) {
					BigDecimal ask = ticker.getAsk().subtract(BigDecimal.valueOf(0.00000001));
					BigDecimal bid = ticker.getBid().add(BigDecimal.valueOf(0.00000001));
					BigDecimal volume = ticker.getVolume();
					BigDecimal rank = ask.subtract(bid).divide(bid, 8, RoundingMode.HALF_UP).multiply(volume)
							.setScale(0, RoundingMode.HALF_UP);

					rankEntityList.add(new RankEntity(currencyPair, ask, bid, rank));
				}
			} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | ExchangeException
					| IOException e) {
				throwActualException(e);
			}
		});

		if (rankEntityList.size() > 0) {
			// Сортировка списка
			rankEntityList.sort(Comparator.comparing(RankEntity::getRank).reversed());

			// Удаление валюты из списка на продажу, которая есть в кошельке и баланс
			// которой > orderMin
			currencyEntityList.forEach(wallet -> {
				RankEntity rankEntity = rankEntityList.stream()
						.filter(rank -> rank.getCurrencyPair().contains(wallet.getCurrency())).findFirst().orElse(null);

				if (rankEntity != null && (wallet.getBtcValue()
						.compareTo(BigDecimal.valueOf(ConfigEntity.getInstance().getOrderMin())) == 1)) {
					rankEntityList.remove(rankEntity);
				}
			});

			// Сокращение списка до значения currencyPairsLimit
			if (rankEntityList.size() >= ConfigEntity.getInstance().getCurrencyPairsLimit()) {
				BigDecimal minRank = rankEntityList.get(ConfigEntity.getInstance().getCurrencyPairsLimit() - 1)
						.getRank();
				rankEntityList.removeIf(rank -> rank.getRank().compareTo(minRank) == -1);
			}

			// Нормализация ранжирования (приведение к еденице)
			BigDecimal totalRank = rankEntityList.stream().map(RankEntity::getRank)
					.reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(100))
					.setScale(8, RoundingMode.HALF_UP);

			rankEntityList.forEach(rank -> {
				BigDecimal normRank = rank.getRank().divide(totalRank, 8, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(.01)).setScale(2, RoundingMode.HALF_UP);

				rank.setNormRank(normRank);
			});

			rankEntityList.forEach(rank -> Formatter.printRankList(rank));
		} else {
			LOG.info(STR_LOG_NO_RANKS);
		}

		return rankEntityList;
	}

	// Пересчет значений normRank в соответствии с балансом кошелька BTC и учетом
	// минимальной суммы ордера
	private static List<RankEntity> getRecalculatedRankeEntityList(List<RankEntity> rankEntityList,
			BigDecimal btcBalance) {
		List<RankEntity> recalculatedRankEntityList = new ArrayList<RankEntity>();

		rankEntityList.forEach(rank -> {
			// На какую сумму в BTC покупать каждой валюты
			BigDecimal btcValue = btcBalance.multiply(rank.getNormRank().setScale(8, RoundingMode.HALF_UP));

			// Если сумма больше минимального ордера (orderMin)
			if (btcValue.compareTo(BigDecimal.valueOf(ConfigEntity.getInstance().getOrderMin())) == 1) {
				recalculatedRankEntityList.add(rank);
			}
		});

		if (recalculatedRankEntityList.size() > 0) {
			recalculatedRankEntityList.forEach(rank -> LOG.info(rank.toString()));
		} else {
			LOG.info(STR_LOG_NO_RECALCULATED_RANKS);
		}

		BigDecimal totalRank = recalculatedRankEntityList.stream().map(RankEntity::getNormRank)
				.reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(100))
				.setScale(8, RoundingMode.HALF_UP);

		LOG.info(STR_LOG_RECALCULATE_AND_NORMALIZE_RANK);

		recalculatedRankEntityList.forEach(rank -> {
			BigDecimal normRank = rank.getNormRank().divide(totalRank, 8, RoundingMode.HALF_UP)
					.multiply(BigDecimal.valueOf(.01)).setScale(2, RoundingMode.HALF_UP);

			rank.setNormRank(normRank);
		});

		// Если список пустой, а баланс BTC > orderMin, то добавить один элемент,
		// имеющий максимальный normRank и установть normRank в единицу
		if (recalculatedRankEntityList.isEmpty() && !rankEntityList.isEmpty()
				&& btcBalance.compareTo(BigDecimal.valueOf(ConfigEntity.getInstance().getOrderMin())) != -1) {

			RankEntity rankEntity = rankEntityList.get(0);
			rankEntity.setNormRank(BigDecimal.ONE.setScale(0));

			recalculatedRankEntityList.add(rankEntity);
		}

		if (recalculatedRankEntityList.size() > 0) {
			recalculatedRankEntityList.forEach(rank -> Formatter.printRankList(rank));
		} else {
			LOG.info(STR_LOG_NO_RECALCULATED_RANKS);
		}

		return recalculatedRankEntityList;
	}

	// Формирование списка валют на покупку
	private static List<CurrencyEntity> makeBidList(List<CurrencyEntity> currencyEntityList) {
		List<CurrencyEntity> orderEntityList = new ArrayList<CurrencyEntity>();

		// Остаток на счете BTC
		CurrencyEntity btcCurrencyEntity = currencyEntityList.stream()
				.filter(wallet -> wallet.getCurrency().equals(Currency.BTC)).findFirst().orElse(null);

		if (btcCurrencyEntity != null) {
			// Сумма в BTC, но которую можно торговать другими валютами
			BigDecimal btcBalance = btcCurrencyEntity.getValue();

			LOG.info(STR_LOG_BTC_ACCOUNT_BALANCE + btcBalance.stripTrailingZeros().toPlainString());
			Formatter.printEmptyString();

			// Если количество BTC больше уровня orderMin (config)
			if (btcBalance.compareTo(BigDecimal.valueOf(ConfigEntity.getInstance().getOrderMin())) != -1) {
				// Формирование Rank-листа
				LOG.info(STR_LOG_GET_RANK_LIST);
				List<RankEntity> rankEntityList = getRankEntityList(currencyEntityList);

				LOG.info(STR_LOG_NORMALIZE_RANK);

				getRecalculatedRankeEntityList(rankEntityList, btcBalance).forEach(rank -> {
					BigDecimal btcValue = btcBalance.multiply(rank.getNormRank()).setScale(8, RoundingMode.HALF_UP);
					BigDecimal value = btcValue.divide(rank.getBid(), 8, RoundingMode.HALF_UP);
					BigDecimal usdValue = btcValue
							.multiply(ExchangeRate.getExchangeRate(stock, Currency.USDT, Currency.BTC))
							.setScale(2, RoundingMode.HALF_UP);

					CurrencyEntity currencyEntity = new CurrencyEntity();
					currencyEntity.setCurrencyPair(rank.getCurrencyPair());
					currencyEntity.setAsk(rank.getAsk());
					currencyEntity.setBid(rank.getBid());
					currencyEntity.setValue(value);
					currencyEntity.setBtcValue(btcValue);
					currencyEntity.setUsdValue(usdValue);

					orderEntityList.add(currencyEntity);
				});
			}
		}

		if (orderEntityList.size() > 0) {
			orderEntityList.forEach(order -> LOG.info(order.toString()));
		} else {
			LOG.info(STR_LOG_NO_BID_LIST);
		}

		return orderEntityList;
	}

	private static void placeLimitOrders(List<CurrencyEntity> currencyEntityList, OrderType orderType) {
		TradeService tradeService = stock.getTradeService();

		if (currencyEntityList.size() > 0) {
			currencyEntityList.forEach(entity -> {
				Formatter.printLimitOrder(entity);

				BigDecimal limitPrice = orderType == OrderType.ASK ? entity.getAsk() : entity.getBid();
				LimitOrder limitOrder = new LimitOrder(orderType, entity.getValue(), entity.getCurrencyPair(), null,
						null, limitPrice);
				try {
					String limitOrderReturnValue = tradeService.placeLimitOrder(limitOrder);

					Formatter.printEmptyString();

					LOG.info(String.format(STR_LOG_LIMIT_ORDER, limitOrderReturnValue));
				} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | ExchangeException
						| IOException e) {
					e.printStackTrace();
				}
			});
		} else {
			LOG.info(STR_LOG_NO_ORDERS);
		}
	}

	private static Map<Currency, BigDecimal> getTotalBtcUsdValueMap(List<CurrencyEntity> currencyEntityList) {
		Map<Currency, BigDecimal> totalBtcUsdValueMap = new HashMap<Currency, BigDecimal>();

		BigDecimal btcValue = currencyEntityList.stream().map(CurrencyEntity::getBtcValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		BigDecimal usdValue = currencyEntityList.stream().map(CurrencyEntity::getUsdValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		totalBtcUsdValueMap.put(Currency.BTC, btcValue);
		totalBtcUsdValueMap.put(Currency.USD, usdValue);

		return totalBtcUsdValueMap;
	}

}
