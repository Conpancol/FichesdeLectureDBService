/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.phystech.aosorio.config.Constants;

/**
 * @author AOSORIO
 *
 */
public class OpenExchangeSvc {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(OpenExchangeSvc.class);
	
	private static String oxServer;
	private static String oxKey;
	private static String oxBase;
	private static String oxSymbols;
	
	public static double getUSDTRM() {
		
		serviceConfiguration(Constants.CONFIG_FILE);
		
		slf4jLogger.debug("Exchange rate for USD to COP at " );
		
		StringBuilder builder = new StringBuilder();
		builder.append(oxServer);
		builder.append("?app_id=");
		builder.append(oxKey);
		builder.append("&base=");
		builder.append(oxBase);
		builder.append("&symbols=");
		builder.append(oxSymbols);
		
		try {
			JsonObject json = GeneralSvc.readJsonFromUrl(builder.toString());
			slf4jLogger.debug(json.toString());
			
			JsonParser parser = new JsonParser();
			JsonObject symbols = parser.parse(json.get("rates").toString()).getAsJsonObject();
			
			double value = symbols.get(oxSymbols).getAsDouble();
					
			return value;
			
		} catch (Exception ex) {
			slf4jLogger.debug(ex.getMessage());
		
		}
		
		return 0.0;
	
	}
	
	private static void serviceConfiguration( String pConfig ) {
		
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(pConfig);

			// load a properties file
			prop.load(input);
			
			// get the property value and print it out
				oxServer = prop.getProperty("openxgd.server");
				oxBase = prop.getProperty("openxgd.base");
				oxSymbols = prop.getProperty("openxgd.symbols");
				oxKey = System.getenv("OPENXGD_KEY");
				
		} catch (IOException ex) {
			setOxServer("localhost");
			setOxBase("USD");
			setOxSymbols("COP");
			setOxKey("NA");
			
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * @return the oxServer
	 */
	public static String getOxServer() {
		return oxServer;
	}

	/**
	 * @param oxServer the oxServer to set
	 */
	public static void setOxServer(String oxServer) {
		OpenExchangeSvc.oxServer = oxServer;
	}

	/**
	 * @return the oxKey
	 */
	public static String getOxKey() {
		return oxKey;
	}

	/**
	 * @param oxKey the oxKey to set
	 */
	public static void setOxKey(String oxKey) {
		OpenExchangeSvc.oxKey = oxKey;
	}

	/**
	 * @return the oxBase
	 */
	public static String getOxBase() {
		return oxBase;
	}

	/**
	 * @param oxBase the oxBase to set
	 */
	public static void setOxBase(String oxBase) {
		OpenExchangeSvc.oxBase = oxBase;
	}

	/**
	 * @return the oxSymbols
	 */
	public static String getOxSymbols() {
		return oxSymbols;
	}

	/**
	 * @param oxSymbols the oxSymbols to set
	 */
	public static void setOxSymbols(String oxSymbols) {
		OpenExchangeSvc.oxSymbols = oxSymbols;
	}
		
}
