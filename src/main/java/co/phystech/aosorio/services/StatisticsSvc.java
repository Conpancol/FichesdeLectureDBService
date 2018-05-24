/**
 * 
 */
package co.phystech.aosorio.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class StatisticsSvc {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(StatisticsSvc.class);

	public static Object getBasicStats(Request pRequest, Response pResponse) {

		JsonObject json = new JsonObject();

		json.addProperty("books", 1);
		json.addProperty("comments", 1);

		return json;

	}

	public static Object getAdvancedStats(Request pRequest, Response pResponse) {

		JsonObject json = new JsonObject();

		//json = (JsonObject) countGroups(conn);

		return json;

	}

	
}
