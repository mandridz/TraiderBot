package ru.msk.ehome.mining.traiderbot.stock;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.poloniex.PoloniexExchange;

import ru.msk.ehome.mining.traiderbot.entity.ConfigEntity;

public class Poloniex {
	private Exchange poloniex;

	public Poloniex() {
		ExchangeSpecification exSpec = new PoloniexExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(ConfigEntity.getInstance().getStockName());
		exSpec.setApiKey(ConfigEntity.getInstance().getStockKey());
		exSpec.setSecretKey(ConfigEntity.getInstance().getStockSecret());
		this.poloniex = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}

	public Exchange getExchange() {
		return poloniex;
	}

	public static class PoloniexHolder {
		public static final Poloniex HOLDER_INSTANCE = new Poloniex();
	}

	public static Poloniex getInstance() {
		return PoloniexHolder.HOLDER_INSTANCE;
	}

}