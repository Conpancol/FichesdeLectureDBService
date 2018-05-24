/**
 * 
 */
package co.phystech.aosorio.controllers;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

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
		
		CfgController dbConf = new CfgController(Constants.CONFIG_FILE);
		
		String dbName = dbConf.getDbName();
		
		morphia = new Morphia();
		morphia.mapPackage("co.phystech.aosorio.dbmicrosvc");
		datastore = morphia.createDatastore(new MongoClient(), dbName);
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
