package ru.msk.ehome.mining.traiderbot.db;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.converters.BigDecimalConverter;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

import ru.msk.ehome.mining.traiderbot.entity.WalletDbEntity;

public class MongoDbDriver {
	
	private Morphia morphia;
	private Datastore ds;
	
	public MongoDbDriver() {
		morphia = new Morphia();
		morphia.map(WalletDbEntity.class);
		morphia.getMapper().getConverters().addConverter(new CurrencyConverter());
		morphia.getMapper().getConverters().addConverter(new BigDecimalConverter());
		morphia.getMapper().getConverters().addConverter(new LocalDateTimeConverter());
		
		MongoClientOptions.Builder options_builder = new MongoClientOptions.Builder();
	    options_builder.maxConnectionIdleTime(60000);
	    MongoClientOptions options = options_builder.build();
		
		ds = morphia.createDatastore(new MongoClient("", options), "cryptobot_db");
		ds.ensureIndexes();
	}
			
	public List<WalletDbEntity> getWalletDbEntityList() {
		Query<WalletDbEntity> query = ds.createQuery(WalletDbEntity.class);

		return query.asList();
	}
	
	public void save(WalletDbEntity walletDbEntity) {
		ds.save(walletDbEntity);
	}
	
	public void delete(WalletDbEntity walletDbEntity) {
		ds.delete(walletDbEntity);
	}
	
	public void close() {
		
	}

}
