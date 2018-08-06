/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ServerAddress;

/**
 * @author AOSORIO
 *
 */
public class CfgController {

	private static String dbEnv;
	private static String dbType;
	private static String dbServerUrl;
	private static String dbHost;
	private static String dbPort;
	private static String dbName;
	private static String dbAddress;
	private static String dbUser;
	private static String dbPass;

	private static String[] dbReplicaSetIPs;
	private static List<ServerAddress> dbServerAdresses;

	private final static Logger slf4jLogger = LoggerFactory.getLogger(CfgController.class);

	public CfgController(String pConfig) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(ClassLoader.getSystemResource(pConfig).getPath());
			
			slf4jLogger.info("Config file loaded");
			
			// load a properties file
			prop.load(input);

			// get the property value and print it out
			if( System.getenv("DBENV") != null ) {
				dbEnv = System.getenv("DBENV");
			} else
				dbEnv = prop.getProperty("db.env");
			dbType = prop.getProperty("db.type");
			dbName = prop.getProperty("db.name");
			dbHost = prop.getProperty("db.host");
			
			dbServerUrl = prop.getProperty(dbType + ".url") + dbHost;
			dbPort = prop.getProperty(dbType + ".port");
			dbUser = prop.getProperty(dbType + ".user");
			dbPass = prop.getProperty(dbType + ".pass");
						
			if (dbEnv.equals("atlas")) {

				getAtlasConfig();

			} else {
				
				setDbAddress(dbServerUrl + ":" + dbPort + "/" + dbName);
				slf4jLogger.info("Default dbAddress: " + this.getDbAddress());
				
			}

			dbReplicaSetIPs = prop.getProperty("mongo.db.replicasetips").split(",");
			dbServerAdresses = new ArrayList<ServerAddress>();

			for (String ips : dbReplicaSetIPs) {
				dbServerAdresses.add(new ServerAddress(ips, 27017));
			}

		} catch (IOException ex) {

			slf4jLogger.info(ex.getMessage());
			slf4jLogger.info("will use the default values");
			dbEnv = "local";
			dbServerUrl = "localhost";
			dbPort = "27017";
			dbName = "dbname";

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ex) {
					slf4jLogger.info(ex.getMessage());;
				}
			}
		}
	}

	private void getAtlasConfig() throws IOException {

		dbServerUrl = System.getenv("ATLAS_URL");
		dbUser = System.getenv("ATLAS_USER");
		dbPass = System.getenv("ATLAS_PASS");		
		dbServerUrl = dbServerUrl.replace("<USER>", dbUser);
		dbServerUrl = dbServerUrl.replace("<PASSWORD>", dbPass);

		slf4jLogger.debug("DbAddress: " + dbServerUrl);

		setDbAddress(dbServerUrl);
		
	}

	/**
	 * @return the dbServerUrl
	 */
	public String getDbServerUrl() {
		return dbServerUrl;
	}

	/**
	 * @param dbServerUrl
	 *            the dbServerUrl to set
	 */
	public void setDbServerUrl(String dbServerUrl) {
		CfgController.dbServerUrl = dbServerUrl;
	}

	/**
	 * @return the dbPort
	 */
	public String getDbPort() {
		return dbPort;
	}

	/**
	 * @param dbPort
	 *            the dbPort to set
	 */
	public void setDbPort(String dbPort) {
		CfgController.dbPort = dbPort;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(String dbName) {
		CfgController.dbName = dbName;
	}

	/**
	 * @return the dbEnv
	 */
	public String getDbEnv() {
		return dbEnv;
	}

	/**
	 * @param dbEnv
	 *            the dbEnv to set
	 */
	public void setDbEnv(String dbEnv) {
		CfgController.dbEnv = dbEnv;
	}

	/**
	 * @return the dbReplicaSetIPs
	 */
	public String[] getDbReplicaSetIPs() {
		return dbReplicaSetIPs;
	}

	/**
	 * @param dbReplicaSetIPs
	 *            the dbReplicaSetIPs to set
	 */
	public void setDbReplicaSetIPs(String[] dbReplicaSetIPs) {
		CfgController.dbReplicaSetIPs = dbReplicaSetIPs;
	}

	/**
	 * @return the dbServerAdresses
	 */
	public List<ServerAddress> getDbServerAdresses() {
		return dbServerAdresses;
	}

	/**
	 * @param dbServerAdresses
	 *            the dbServerAdresses to set
	 */
	public void setDbServerAdresses(List<ServerAddress> dbServerAdresses) {
		CfgController.dbServerAdresses = dbServerAdresses;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		CfgController.dbType = dbType;
	}

	public String getDbAddress() {
		return dbAddress;
	}

	public void setDbAddress(String dbAddress) {
		CfgController.dbAddress = dbAddress;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		CfgController.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		CfgController.dbPass = dbPass;
	}

}
