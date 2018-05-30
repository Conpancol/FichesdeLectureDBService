package materialsdbsvc;

import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.controllers.CfgController;
import co.phystech.aosorio.controllers.NoSqlController;

public class ConnectionTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ConnectionTest.class);

	@Test
	public void connectNoSql() {

		CfgController dbConf = new CfgController(Constants.CONFIG_FILE);
		
		String dbName = dbConf.getDbName();
		
		Morphia morphia = new Morphia();
		morphia.mapPackage("co.phystech.aosorio.dbmicrosvc");
		Datastore datastore = morphia.createDatastore(new MongoClient(), dbName);
		
		if( datastore != null ) {
			slf4jLogger.info("We are connected - Standard method ");
		}
		
	}
	
	@Test
	public void connectNoSqlController() {

		Datastore datastore = NoSqlController.getInstance().getDatabase();
		
		if( datastore != null ) {
			slf4jLogger.info("We are connected - using NoSqlController");
		}
		
	}

}
