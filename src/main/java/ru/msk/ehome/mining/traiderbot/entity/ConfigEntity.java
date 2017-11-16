package ru.msk.ehome.mining.traiderbot.entity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

public class ConfigEntity {
	
	// Имя пользователя для биржи
	private String stockName;
	
	// Ключ для биржи
	private String stockKey;
	
	// Секрет для биржи
	private String stockSecret;
	
	// Выводить информацию в ThingSpeak (0 - нет, 1 - да)
	private Boolean isSendToThingSpeak;
	
	// Номер канала в ThingSpeak
	private Integer thChannelId;
	
	// Write-ключ ThingSpeak
	private String thWriteKey;

	// Минимальный размер ордера в сатоши (Poloniex)
	private Float orderMin;

	// Минимальный профит процентах
	private Float profit;

	// Максимальное количество дней, которое валюта лежит в кошельке (затем валюта
	// продается по рыночной цене)
	private Integer timeLimit;

	// Нижняя граница стоимости валют, которые подбираются для ордеров на покупку
	private Integer satoshiMin;

	// Максимальное количество валют для ордеров на покупку
	private Integer currencyPairsLimit;

	// Интервал запуска программы, в минутах
	private Long shedulerTimeout;
	
	// Какую сумму в BTC-кошельке можно использовать для торговли, в процентах
	private Float btcLimit;

	public ConfigEntity() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");

			prop.load(input);
			
			this.stockName = prop.getProperty("stockName", "");
			this.stockKey = prop.getProperty("stockKey", "");
			this.stockSecret = prop.getProperty("stockSecret", "");
			this.isSendToThingSpeak = !(Integer.valueOf(prop.getProperty("isSendToThingSpeak", "")) == 0);
			this.thChannelId = Integer.valueOf(prop.getProperty("thChannelId", "0"));
			this.thWriteKey = prop.getProperty("thWriteKey", "");
			this.orderMin = Float.valueOf(prop.getProperty("orderMin", "0.005"));
			this.profit = Float.valueOf(prop.getProperty("profit", "2"));
			this.timeLimit = Integer.valueOf(prop.getProperty("timeLimit", "14"));
			this.satoshiMin = Integer.valueOf(prop.getProperty("satoshiMin", "100000"));
			this.currencyPairsLimit = Integer.valueOf(prop.getProperty("currencyPairsLimit", "5"));
			this.shedulerTimeout = Long.valueOf(prop.getProperty("shedulerTimeout", "2"));
			this.btcLimit = Float.valueOf(prop.getProperty("btcLimit", "100"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static class ConfigEntityHolder {
		public static final ConfigEntity HOLDER_INSTANCE = new ConfigEntity();
	}

	public static ConfigEntity getInstance() {
		return ConfigEntityHolder.HOLDER_INSTANCE;
	}

	public String getStockName() {
		return stockName;
	}

	public String getStockKey() {
		return stockKey;
	}

	public String getStockSecret() {
		return stockSecret;
	}

	public Boolean isSendToThingSpeak() {
		return isSendToThingSpeak;
	}

	public Integer getThChannelId() {
		return thChannelId;
	}

	public String getThWriteKey() {
		return thWriteKey;
	}

	public Float getOrderMin() {
		return orderMin;
	}

	public Float getProfit() {
		return profit;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public Integer getSatoshiMin() {
		return satoshiMin;
	}

	public Integer getCurrencyPairsLimit() {
		return currencyPairsLimit;
	}

	public Long getShedulerTimeout() {
		return shedulerTimeout;
	}

	public Float getBtcLimit() {
		
		return btcLimit;
	}

	public BigDecimal getSatoshiLimit() {

		return BigDecimal.valueOf(.00000001).multiply(BigDecimal.valueOf(this.satoshiMin));
	}

	@Override
	public String toString() {
		return "ConfigEntity [stockName=" + stockName + ", stockKey=" + stockKey + ", stockSecret=" + stockSecret
				+ ", isSendToThingSpeak=" + isSendToThingSpeak + ", thChannelId=" + thChannelId + ", thWriteKey="
				+ thWriteKey + ", orderMin=" + orderMin + ", profit=" + profit + ", timeLimit=" + timeLimit
				+ ", satoshiMin=" + satoshiMin + ", currencyPairsLimit=" + currencyPairsLimit + ", shedulerTimeout="
				+ shedulerTimeout + ", btcLimit=" + btcLimit + "]";
	}

}
