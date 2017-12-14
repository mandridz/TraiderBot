package ru.msk.ehome.mining.traiderbot.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.knowm.xchange.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.msk.ehome.mining.traiderbot.entity.ConfigEntity;
import ru.msk.ehome.mining.traiderbot.entity.CurrencyEntity;
import ru.msk.ehome.mining.traiderbot.entity.RankEntity;

public class Formatter {
	private static final String STR_LOG_EMPTY = "";
	private static final String STR_LOG_DELIMITER = "------------------------------------------------------------------------";
	private static final String STR_LOG_DOUBLE_DELIMITER_SHORT = "===========================";
	private static final String STR_LOG_DELIMITER_SHORT = "---------------------------";
	private static final String STR_LOG_CURRENCY = "Currency: ";
	private static final String STR_LOG_CURRENCY_PAIR = "Currency Pair: ";
	private static final String STR_LOG_VALUE = "Value:     ";
	private static final String STR_LOG_BTC_VALUE = "BTC Value: ";
	private static final String STR_LOG_USD_VALUE = "USD Value: ";
	private static final String STR_LOG_REAL_PROFIT = "Real Profit: %s%%";
	private static final String STR_LOG_DATE_EXPIRED = "The Date is expired";
	private static final String STR_LOG_PURCHASED_PRICE = "Purchased Price: ";
	private static final String STR_LOG_PROFIT_PRICE = "Profit Price:    ";
	private static final String STR_LOG_LOW       = "low:             ";
	private static final String STR_LOG_BID       = "bid:             ";
	private static final String STR_LOG_ASK       = "ask:             ";
	private static final String STR_LOG_LAST      = "last:            ";
	private static final String STR_LOG_HIGH      = "high:            ";
	private static final String STR_LOG_RANK      = "rank:            ";
	private static final String STR_LOG_NORM_RANK = "norm rank:       ";
	private static final String STR_LOG_PURCHASED_ASK_PROFIT = "PURCHASED/ASK/PROFIT: %s >> %s >> %s (%s%%)";
	private static final String STR_LOG_CURRENCY_DURATION = "Duration: ";
	private static final String STR_LOG_START_BAR = "|";
	private static final String STR_LOG_BAR = "=|";
	private static final String STR_LOG_STOP_BAR = ">> ";

	private static final String STR_LOG_SENDING_TO_THINGSPEAK = "Sending to ThingSpeak...";
	
	private static final String STR_LOG_EXCHANGE_STOCK_NAME = "Exchange Stock Name:  ";
	private static final String STR_LOG_EXCHANGE_STOCK_KEY = "Exchange Stock Key:   ";
	private static final String STR_LOG_IS_SEND_TO_THINGSPEAK = "isSendToThingSpeak:   ";
	private static final String STR_LOG_THINGSPEAK_CHANNEL_ID = "ThingSpeak ChannelId: ";
	private static final String STR_LOG_THINGSPEAK_WRITE_KEY = "ThingSpeak WriteKey:  ";
	private static final String STR_LOG_CURRENCY_PAIRS_LIMIT = "Currency Pairs Limit: ";
	private static final String STR_LOG_PROFIT = "Profit:               ";
	private static final String STR_LOG_ORDER_MIN = "Order Min:            ";
	private static final String STR_LOG_SHEDULER_TIMEPUT = "Sheduler Timeout:     ";
	private static final String STR_LOG_SATOSHI_MIN = "Satoshi Min:          ";
	private static final String STR_LOG_TIME_LIMIT = "TimeLimit:            ";
	private static final String STR_LOG_ON = "ON";
	private static final String STR_LOG_OFF = "OFF";

	private final static Logger LOG = LoggerFactory.getLogger(STR_LOG_EMPTY);
	
	public static void printEmptyString() {
		LOG.info(STR_LOG_EMPTY);
	}

	public static void printSimpleMsgWithShortDelimiter(String msg) {
		LOG.info(msg);
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_EMPTY);
	}

	public static void printSimpleMsgWithDoubleShortDelimiter(String msg) {
		LOG.info(msg);
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
		LOG.info(STR_LOG_EMPTY);
	}

	public static void printMsg(String msg) {
		LOG.info(STR_LOG_DELIMITER);
		LOG.info(msg);
		LOG.info(STR_LOG_DELIMITER);
		LOG.info(STR_LOG_EMPTY);
	}
	
	public static void printConfiguration() {
		ConfigEntity config = ConfigEntity.getInstance();
		
		LOG.info(STR_LOG_EXCHANGE_STOCK_NAME + config.getStockName());
		LOG.info(STR_LOG_EXCHANGE_STOCK_KEY + config.getStockKey());
		printEmptyString();
		LOG.info(STR_LOG_IS_SEND_TO_THINGSPEAK + (config.isSendToThingSpeak() ? STR_LOG_ON : STR_LOG_OFF));
		LOG.info(STR_LOG_THINGSPEAK_CHANNEL_ID + config.getThChannelId());
		LOG.info(STR_LOG_THINGSPEAK_WRITE_KEY + config.getThWriteKey());
		printEmptyString();
		LOG.info(STR_LOG_CURRENCY_PAIRS_LIMIT + config.getCurrencyPairsLimit());
		LOG.info(STR_LOG_PROFIT + config.getProfit());
		LOG.info(STR_LOG_ORDER_MIN + config.getOrderMin());
		LOG.info(STR_LOG_SHEDULER_TIMEPUT + config.getShedulerTimeout());
		LOG.info(STR_LOG_SATOSHI_MIN + config.getSatoshiMin());
		LOG.info(STR_LOG_TIME_LIMIT + config.getTimeLimit());
	}

	public static void printCurrencyEntity(CurrencyEntity currencyEntity) {
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
		LOG.info(STR_LOG_CURRENCY + currencyEntity.getCurrency());
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_VALUE + currencyEntity.getValue().toPlainString());
		LOG.info(STR_LOG_BTC_VALUE + currencyEntity.getBtcValue().toPlainString());
		LOG.info(STR_LOG_USD_VALUE + currencyEntity.getUsdValue());
		LOG.debug(STR_LOG_PURCHASED_PRICE + currencyEntity.getPurchasePrice());
		LOG.debug(STR_LOG_PROFIT_PRICE + currencyEntity.getProfitPrice());
		LOG.debug(STR_LOG_LOW + currencyEntity.getLow());
		LOG.debug(STR_LOG_BID + currencyEntity.getBid());
		LOG.debug(STR_LOG_ASK + currencyEntity.getAsk());
		LOG.debug(STR_LOG_LAST + currencyEntity.getLast());
		LOG.debug(STR_LOG_HIGH + currencyEntity.getHigh());
		LOG.info(STR_LOG_DELIMITER_SHORT);

		if (!currencyEntity.getCurrency().equals(Currency.BTC)) {
			LOG.info(String.format(STR_LOG_PURCHASED_ASK_PROFIT, currencyEntity.getPurchasePrice(),
					currencyEntity.getAsk(), currencyEntity.getProfitPrice(), currencyEntity.getRealProfit()));

			long hours = TimeUnit.MILLISECONDS.toHours(currencyEntity.getDuration());
			String hoursBar = STR_LOG_START_BAR;

			for (int i = 0; i < hours; i++) {
				hoursBar += STR_LOG_BAR;
			}

			hoursBar += STR_LOG_STOP_BAR;

			LOG.info(STR_LOG_CURRENCY_DURATION + hoursBar
					+ DurationFormatUtils.formatDuration(currencyEntity.getDuration(), "d HH:mm:ss"));
			LOG.info(STR_LOG_DELIMITER_SHORT);
		}
	}

	public static void printBid(CurrencyEntity currencyEntity, boolean dateExpired) {
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
		LOG.info(STR_LOG_CURRENCY + currencyEntity.getCurrency());
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_VALUE + currencyEntity.getValue());
		LOG.info(STR_LOG_BTC_VALUE + currencyEntity.getBtcValue());
		LOG.info(STR_LOG_USD_VALUE + currencyEntity.getUsdValue());
		LOG.info(String.format(STR_LOG_REAL_PROFIT, currencyEntity.getRealProfit()));

		if (dateExpired) {
			LOG.info(STR_LOG_DATE_EXPIRED);
		}

		LOG.info(STR_LOG_DELIMITER_SHORT);
	}
	
	public static void printRankList(RankEntity rank) {
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
		LOG.info(STR_LOG_CURRENCY + rank.getCurrencyPair());
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_ASK + rank.getAsk());
		LOG.info(STR_LOG_BID + rank.getBid());
		LOG.info(STR_LOG_RANK + rank.getRank());
		LOG.info(STR_LOG_NORM_RANK + rank.getNormRank());
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_EMPTY);
	}
	
	public static void printOrderList(CurrencyEntity currencyEntity) {
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
		LOG.info(STR_LOG_CURRENCY_PAIR + currencyEntity.getCurrencyPair());
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_VALUE + currencyEntity.getValue());
		LOG.info(STR_LOG_BTC_VALUE + currencyEntity.getBtcValue());
		LOG.info(STR_LOG_USD_VALUE + currencyEntity.getUsdValue());		
		LOG.info(STR_LOG_ASK + currencyEntity.getAsk());
		LOG.info(STR_LOG_BID + currencyEntity.getBid());
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_EMPTY);
	}

	public static void printLimitOrder(CurrencyEntity currencyEntity) {
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
		LOG.info(STR_LOG_CURRENCY + (currencyEntity.getCurrency() == null ? currencyEntity.getCurrencyPair()
				: currencyEntity.getCurrency()));
		LOG.info(STR_LOG_DELIMITER_SHORT);
		LOG.info(STR_LOG_VALUE + currencyEntity.getValue());
		LOG.info(STR_LOG_BTC_VALUE + currencyEntity.getBtcValue());
		LOG.info(STR_LOG_USD_VALUE + currencyEntity.getUsdValue());
		LOG.info(STR_LOG_DELIMITER_SHORT);
	}

	public static void printThingSpeak(String btcValue, String usdValue) {
		LOG.info(STR_LOG_SENDING_TO_THINGSPEAK);
		LOG.info(STR_LOG_BTC_VALUE + btcValue);
		LOG.info(STR_LOG_USD_VALUE + usdValue);
	}

	public static void printDelimiter() {
		LOG.info(STR_LOG_DELIMITER);
	}

	public static void printDoubleDelimiter() {
		LOG.info(STR_LOG_DOUBLE_DELIMITER_SHORT);
	}

}
