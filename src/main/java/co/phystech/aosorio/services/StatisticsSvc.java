/**
 * 
 */
package co.phystech.aosorio.services;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import co.phystech.aosorio.controllers.MaterialsController;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class StatisticsSvc {

	//private final static Logger slf4jLogger = LoggerFactory.getLogger(StatisticsSvc.class);

	public static Object getBasicStats(Request pRequest, Response pResponse) {
		
		long nmaterials = MaterialsController.count();
		
		JsonObject json = new JsonObject();

		json.addProperty("materials", nmaterials);

		return json;

	}

	
}
