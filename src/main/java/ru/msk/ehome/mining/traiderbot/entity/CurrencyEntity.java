package ru.msk.ehome.mining.traiderbot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

public class CurrencyEntity {

	private Currency currency;
	private CurrencyPair currencyPair;
	private BigDecimal value;
	private BigDecimal btcValue;
	private BigDecimal usdValue;
	private BigDecimal purchasePrice;
	private BigDecimal profitPrice;
	private BigDecimal low;
	private BigDecimal bid;
	private BigDecimal ask;
	private BigDecimal last;
	private BigDecimal high;
	private BigDecimal realProfit;
	private long duration;
	private LocalDateTime purchaseDt;

	public CurrencyEntity() {
	}

	public CurrencyEntity(Currency currency, CurrencyPair currencyPair, BigDecimal value, BigDecimal btcValue,
			BigDecimal usdValue, BigDecimal purchasePrice, BigDecimal profitPrice, BigDecimal low, BigDecimal bid,
			BigDecimal ask, BigDecimal last, BigDecimal high, BigDecimal realProfit, long duration, LocalDateTime purchaseDt) {
		this.currency = currency;
		this.currencyPair = currencyPair;
		this.value = value;
		this.btcValue = btcValue;
		this.usdValue = usdValue;
		this.purchasePrice = purchasePrice;
		this.profitPrice = profitPrice;
		this.low = low;
		this.bid = bid;
		this.ask = ask;
		this.last = last;
		this.high = high;
		this.realProfit = realProfit;
		this.duration = duration;
		this.purchaseDt = purchaseDt;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public CurrencyPair getCurrencyPair() {
		return currencyPair;
	}

	public void setCurrencyPair(CurrencyPair currencyPair) {
		this.currencyPair = currencyPair;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getBtcValue() {
		return btcValue;
	}

	public void setBtcValue(BigDecimal btcValue) {
		this.btcValue = btcValue;
	}

	public BigDecimal getUsdValue() {
		return usdValue;
	}

	public void setUsdValue(BigDecimal usdValue) {
		this.usdValue = usdValue;
	}

	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public BigDecimal getProfitPrice() {
		return profitPrice;
	}

	public void setProfitPrice(BigDecimal profitPrice) {
		this.profitPrice = profitPrice;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getRealProfit() {
		return realProfit;
	}

	public void setRealProfit(BigDecimal realProfit) {
		this.realProfit = realProfit;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public LocalDateTime getPurchaseDt() {
		return purchaseDt;
	}

	public void setPurchaseDt(LocalDateTime purchaseDt) {
		this.purchaseDt = purchaseDt;
	}

	@Override
	public String toString() {
		return "CurrencyEntity [currency=" + currency + ", currencyPair=" + currencyPair + ", value=" + value
				+ ", btcValue=" + btcValue + ", usdValue=" + usdValue + ", purchasePrice=" + purchasePrice
				+ ", profitPrice=" + profitPrice + ", low=" + low + ", bid=" + bid + ", ask=" + ask + ", last=" + last
				+ ", high=" + high + ", realProfit=" + realProfit + ", duration=" + duration + ", purchaseDt="
				+ purchaseDt + "]";
	}

}
