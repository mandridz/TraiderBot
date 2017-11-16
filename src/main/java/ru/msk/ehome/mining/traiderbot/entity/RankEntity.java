package ru.msk.ehome.mining.traiderbot.entity;

import java.math.BigDecimal;

import org.knowm.xchange.currency.CurrencyPair;

public class RankEntity {

	private CurrencyPair currencyPair;
	private BigDecimal ask;
	private BigDecimal bid;
	private BigDecimal rank;
	private BigDecimal normRank;

	public RankEntity() {}
	
	public RankEntity(CurrencyPair currencyPair, BigDecimal ask, BigDecimal bid, BigDecimal rank) {
		this.currencyPair = currencyPair;
		this.ask = ask;
		this.bid = bid;
		this.rank = rank;
	}

	public CurrencyPair getCurrencyPair() {
		return currencyPair;
	}

	public void setCurrencyPair(CurrencyPair currencyPair) {
		this.currencyPair = currencyPair;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getRank() {
		return rank;
	}

	public void setRank(BigDecimal rank) {
		this.rank = rank;
	}

	public BigDecimal getNormRank() {
		return normRank;
	}

	public void setNormRank(BigDecimal normRank) {
		this.normRank = normRank;
	}

	@Override
	public String toString() {
		return "RankEntity [currencyPair=" + currencyPair + ", ask=" + ask + ", bid=" + bid + ", rank=" + rank
				+ ", normRank=" + normRank + "]";
	}

}