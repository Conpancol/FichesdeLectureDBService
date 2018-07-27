package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.phystech.aosorio.controllers.MaterialsController;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.services.GeneralSvc;


public class ResponseTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ResponseTest.class);
	@Test
	public void JsonTests() {
		
		JsonArray jArray = new JsonArray(); 
		JsonObject item_result =  new JsonObject();
			
		item_result.addProperty("100001", "OK");
		
		jArray.add(item_result);
		
		slf4jLogger.info("JSON tests: " + jArray.get(0).toString());
		
		assertEquals("OK", jArray.get(0).getAsJsonObject().get("100001").getAsString());
		
	}

	@Test
	public void BackendMessageWithJsonTests() {
		
		ArrayList<Materials> materials = new ArrayList<Materials>();
		
		Materials material = new Materials();
		material.setItemcode("TEST0001");
		material.setCategory("PLATE");
		material.setDescription("Something");
		material.setType("SX");
		
		materials.add(material);
		
		JsonArray jArray = MaterialsController.create(materials);
		
		BackendMessage returnMessage = new BackendMessage();
		
		Object result = returnMessage.getOkMessage(jArray.toString());
		
		slf4jLogger.info("BackendMessage: " + GeneralSvc.dataToJson(result));
		
		JsonParser parser = new JsonParser();
		JsonObject result_back = parser.parse(GeneralSvc.dataToJson(result)).getAsJsonObject();		
		JsonArray result_value = parser.parse(result_back.get("value").getAsString()).getAsJsonArray();
		
		assertEquals("TEST0001", result_value.get(0).getAsJsonObject().get("itemcode").getAsString());
		
		MaterialsController.delete(material);
		
	}
	
}
