package ru.msk.ehome.mining.traiderbot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.knowm.xchange.currency.Currency;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "wallet", noClassnameStored = true)
public class WalletDbEntity {
	
	@Id
	private ObjectId id;
	private Currency currency;
	private LocalDateTime purchaseDt;
	private BigDecimal purchasePrice;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public LocalDateTime getPurchaseDt() {
		return purchaseDt;
	}
	
	public void setPurchaseDt(LocalDateTime purchaseDt) {
		this.purchaseDt = purchaseDt;
	}
	
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}
	
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Override
	public String toString() {
		return "WalletDbEntity [id=" + id + ", currency=" + currency + ", purchaseDt=" + purchaseDt + ", purchasePrice="
				+ purchasePrice + "]";
	}
	
}
