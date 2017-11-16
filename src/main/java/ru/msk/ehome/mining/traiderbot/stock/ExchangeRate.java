package ru.msk.ehome.mining.traiderbot.stock;

import java.io.IOException;
import java.math.BigDecimal;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public class ExchangeRate {

	public static BigDecimal getExchangeRate(Exchange stock, CurrencyPair currencyPair) {
		BigDecimal result = null;

		try {
			Ticker ticker = stock.getMarketDataService().getTicker(currencyPair);
			
			if (ticker != null) {
				result = ticker.getLast();
			}
		} catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException
				| ExchangeException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static BigDecimal getExchangeRate(Exchange stock, Currency fromCurrency, Currency toCurrency) {
		CurrencyPair currencyPair = stock.getExchangeSymbols().stream()
				.filter(curr -> curr.contains(fromCurrency) && curr.contains(toCurrency)).findFirst().orElse(null);
		
		return currencyPair != null ? getExchangeRate(stock, currencyPair) : null;
	}

}