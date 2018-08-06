/**
 * 
 */
package co.phystech.aosorio.controllers;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import co.phystech.aosorio.config.Constants;


/**
 * @author AOSORIO
 *
 */
public class NoSqlController {
	
	private static NoSqlController instance = null;
	private static Morphia morphia = null;
	private static Datastore datastore = null;
	
	protected NoSqlController() {
		
		String dbName = "";
		String dbAddress = "";
			
		CfgController dbConf = new CfgController(Constants.CONFIG_FILE);
		
		dbName = dbConf.getDbName();
		dbAddress = dbConf.getDbAddress();
		
		morphia = new Morphia();
		morphia.mapPackage("co.phystech.aosorio.dbmicrosvc");
		
		MongoClientURI uri = new MongoClientURI(dbAddress);
		MongoClient mongoClient = new MongoClient(uri);
					    
		datastore = morphia.createDatastore(mongoClient, dbName);
		//datastore.ensureIndexes();
		
	}
	
	public static NoSqlController getInstance() {
		if (instance == null) {
			instance = new NoSqlController();
		}
		return instance;
	}

	public Datastore getDatabase() {
		return datastore;
	}

}
