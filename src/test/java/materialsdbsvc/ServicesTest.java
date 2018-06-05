/**
 * 
 */
package materialsdbsvc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import co.phystech.aosorio.services.StatisticsSvc;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class ServicesTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ServicesTest.class);
	
	@Test
	public void bookCounterTest() {
		
		//Test the book counter service
		Request pRequest = null;
		Response pResponse = null;
		
		JsonObject json = (JsonObject) StatisticsSvc.getBasicStats(pRequest, pResponse);
		
		slf4jLogger.info("Number of books: " + json.get("books"));
		slf4jLogger.info("Number of comments: " + json.get("comments"));
		
		assertTrue(json.has("books"));
		
	}
	
	@Test
	public void countGroupTest() {
		
		//Test the book counter service
		Request pRequest = null;
		Response pResponse = null;
		
		JsonObject json = (JsonObject) StatisticsSvc.getAdvancedStats(pRequest, pResponse);
				
		slf4jLogger.info("Groups " + json.get("groups"));

		assertTrue(true);
		
	}

}
