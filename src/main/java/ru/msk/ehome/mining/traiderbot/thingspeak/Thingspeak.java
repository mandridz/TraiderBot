package ru.msk.ehome.mining.traiderbot.thingspeak;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.knowm.xchange.currency.Currency;

import com.mashape.unirest.http.exceptions.UnirestException;

import ru.msk.ehome.mining.traiderbot.entity.ConfigEntity;
import ru.msk.ehome.mining.traiderbot.util.Formatter;

public class Thingspeak {
	Channel channel;
	
	public Thingspeak() {
		channel = new Channel(ConfigEntity.getInstance().getThChannelId(), ConfigEntity.getInstance().getThWriteKey());
	}
	
	public Integer sendData(Map<Currency, BigDecimal> totalBtcUsdValueMap) {
		Integer result = null;
		
		String btcValue = totalBtcUsdValueMap.get(Currency.BTC).setScale(8, RoundingMode.HALF_UP).toPlainString();
		String usdValue = totalBtcUsdValueMap.get(Currency.USD).setScale(2, RoundingMode.HALF_UP).toPlainString();
		
		Entry entry = new Entry();
		
		entry.setField(1, btcValue);
		entry.setField(2, usdValue);
		
		Formatter.printThingSpeak(btcValue, usdValue);
		try {
			result = channel.update(entry);
		} catch (UnirestException | ThingSpeakException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
