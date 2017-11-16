package ru.msk.ehome.mining.traiderbot.db;

import org.knowm.xchange.currency.Currency;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

public class CurrencyConverter extends TypeConverter implements SimpleValueConverter {

	public CurrencyConverter() {
		super(Currency.class);
	}

	@Override
	protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
		return Currency.class.isAssignableFrom(c);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		return value == null ? null : ((Currency) value).getCurrencyCode();
	}

	@Override
	public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo)
			throws MappingException {
		
		return fromDBObject == null ? null : Currency.getInstance(fromDBObject.toString());
	}

}
