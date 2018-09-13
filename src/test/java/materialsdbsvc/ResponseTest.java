package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Key;
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
	
	public static final String itemcode = "TEST0001";
	public static final String description = "TUBE, CS, 1\"";
	public static final String type = "CS";
	public static final String category = "TUBE";
	public static final String dimensions = "1\"";
	
	private static ObjectId materialId;
	
	@BeforeClass
	public static void beforeClass() { 
		
		Materials material = new Materials();

		material.setItemcode(itemcode);
		material.setDescription(description);
		material.setType(type);
		material.setCategory(category);
		material.setDimensions(dimensions);

		Key<Materials> keys = MaterialsController.create(material);
		
		materialId = (ObjectId) keys.getId();
		
	}
	
	@AfterClass
	public static void afterClass() {
		
		Materials material = MaterialsController.read(materialId);
		
		MaterialsController.delete(material);

	}
	
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
		
		Materials material = MaterialsController.read(materialId);
		
		materials.add(material);
		
		JsonArray jArray = MaterialsController.create(materials);
		
		BackendMessage returnMessage = new BackendMessage();
		
		Object result = returnMessage.getOkMessage(jArray.toString());
		
		slf4jLogger.info("BackendMessage: " + GeneralSvc.dataToJson(result));
		
		JsonParser parser = new JsonParser();
		JsonObject result_back = parser.parse(GeneralSvc.dataToJson(result)).getAsJsonObject();		
		JsonArray result_value = parser.parse(result_back.get("value").getAsString()).getAsJsonArray();
		
		slf4jLogger.info("BackendMessageWithJsonTests: " + jArray.get(0).toString());
		
		assertEquals("TEST0001", result_value.get(0).getAsJsonObject().get("itemcode").getAsString());
		
		
	}
	
	@Test
	public void XCheckerTests() {
		
		ArrayList<Materials> materials = new ArrayList<Materials>();
		
		Materials material = MaterialsController.read(materialId);
		
		materials.add(material);
		
		JsonArray result_value = MaterialsController.xchecker(materials);
		
		slf4jLogger.info("XCheckerTests: " + result_value.get(0).getAsJsonObject().get("description").getAsString());
		
		assertEquals("TEST0001", result_value.get(0).getAsJsonObject().get("itemcode").getAsString());
		
	}
	
	@Test
	public void ReaderTests() { 
		
		Materials materials = MaterialsController.read(itemcode);
		
		if( !(materials == null) ) 
			slf4jLogger.info("ReaderTests: " + materials.getItemcode());
			
		assertEquals("TEST0001",materials.getItemcode());


	}
	
}
