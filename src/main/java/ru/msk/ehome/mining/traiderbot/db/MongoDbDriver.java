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
	
	private MongoDbDriver() {
		morphia = new Morphia();
		morphia.map(WalletDbEntity.class);
		morphia.getMapper().getConverters().addConverter(new CurrencyConverter());
		morphia.getMapper().getConverters().addConverter(new BigDecimalConverter());
		morphia.getMapper().getConverters().addConverter(new LocalDateTimeConverter());
		
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
	    //builder.maxConnectionIdleTime(60000);
	    builder.socketKeepAlive(true);
		
	    MongoClient mongo = new MongoClient("127.0.0.1", builder.build());
		//mongo.setWriteConcern(WriteConcern.JOURNALED);
	    
		ds = morphia.createDatastore(mongo, "cryptobot_db");
		ds.ensureIndexes();
	}
	
	public static class MongoDbDriverHolder {
		public static final MongoDbDriver HOLDER_INSTANCE = new MongoDbDriver();
	}

	public static MongoDbDriver getInstance() {
		return MongoDbDriverHolder.HOLDER_INSTANCE;
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

}
