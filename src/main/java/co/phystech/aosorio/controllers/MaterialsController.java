/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.WriteResult;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.services.GeneralSvc;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class MaterialsController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(MaterialsController.class);

	private static Datastore datastore;

	public static Object create(Request pRequest, Response pResponse) {

		datastore = NoSqlController.getInstance().getDatabase();

		BackendMessage returnMessage = new BackendMessage();

		pResponse.type("application/json");

		try {

			slf4jLogger.info(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<Materials> newMaterials = mapper.readValue(pRequest.body(),
					new TypeReference<ArrayList<Materials>>() {
					});

			JsonArray keys = create(newMaterials);
			pResponse.status(200);
			return returnMessage.getOkMessage(keys.toString());

		} catch (IOException jpe) {
			jpe.printStackTrace();
			slf4jLogger.debug("Problem adding material list");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding material list");
		}

	}

	public static JsonArray create(ArrayList<Materials> materials) {

		datastore = NoSqlController.getInstance().getDatabase();

		JsonArray jArray = new JsonArray();

		ArrayList<Key<Materials>> addedKeys = new ArrayList<>();

		// ... check if material already exist in DB
		Iterator<Materials> itr = materials.iterator();

		while (itr.hasNext()) {

			JsonObject item_result = new JsonObject();

			Materials material = (Materials) itr.next();

			Query<Materials> query = datastore.createQuery(Materials.class);
			List<Materials> result = query.field("itemcode").equal(material.getItemcode()).asList();

			if (result.isEmpty()) {
				slf4jLogger.info("Material not found " + material.getItemcode());
				Key<Materials> key = create(material);
				addedKeys.add(key);
				item_result.addProperty("itemcode", material.getItemcode());
				item_result.addProperty("status", "Saved");
			} else {
				item_result.addProperty("itemcode", material.getItemcode());
				item_result.addProperty("status", "Already in DB");
			}

			jArray.add(item_result);

		}

		slf4jLogger.info(String.valueOf(addedKeys.size()));

		return jArray;
	}

	public static Key<Materials> create(Materials material) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.save(material);
	}

	public static Object read(Request pRequest, Response pResponse) {

		String id = pRequest.params("id");

		BackendMessage returnMessage = new BackendMessage();

		try {

			Materials material = read(id);
			pResponse.status(200);
			pResponse.type("application/json");
			String resultJson = new Gson().toJson(material);
			return returnMessage.getOkMessage(resultJson);

		} catch (NoSuchElementException ex) {

			slf4jLogger.debug("Material not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Material not found");

		}

	}

	public static Materials read(ObjectId id) {

		datastore = NoSqlController.getInstance().getDatabase();

		return datastore.get(Materials.class, id);

	}

	public static Materials read(String itemCode) throws NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("itemcode").equal(itemCode).asList();

		if (result.isEmpty())
			throw new NoSuchElementException();

		return result.iterator().next();
	}

	public static Object update(Request pRequest, Response pResponse) {

		String id = pRequest.params("id");

		BackendMessage returnMessage = new BackendMessage();

		try {

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<Materials> newMaterials = mapper.readValue(pRequest.body(),
					new TypeReference<ArrayList<Materials>>() {
					});

			Materials modified = newMaterials.iterator().next();

			Key<Materials> keys = update(id, modified);
			ObjectId materialId = (ObjectId) keys.getId();
			Materials material = read(materialId);

			JsonArray jArray = new JsonArray();
			JsonObject result = new JsonObject();
			result.addProperty("itemcode", material.getItemcode());
			result.addProperty("description", material.getDescription());
			result.addProperty("status", "Updated");

			jArray.add(result);

			pResponse.status(200);
			pResponse.type("application/json");
			return returnMessage.getOkMessage(jArray.toString());

		} catch (IOException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem updating Material");

		} catch (NoSuchElementException ex) {

			slf4jLogger.debug("Material not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Material not found");
		}

	}

	private static Key<Materials> update(String id, Materials modified) throws NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		Materials current = read(id);

		if (current == null)
			throw new NoSuchElementException();

		current.setItemcode(modified.getItemcode());
		current.setDescription(modified.getDescription());
		current.setDimensions(modified.getDimensions());
		current.setCategory(modified.getCategory());
		current.setType(modified.getType());

		return create(current);

	}

	public UpdateResults update(Materials material, UpdateOperations<Materials> operations) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.update(material, operations);
	}

	public static WriteResult delete(Materials material) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.delete(material);
	}

	public UpdateOperations<Materials> createOperations() {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.createUpdateOperations(Materials.class);
	}

	public static long count() {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.find(Materials.class).count();
	}

	// ...

	public static Object xcheck(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		pResponse.type("application/json");

		try {

			slf4jLogger.info(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();

			Materials[] materialsList = mapper.readValue(pRequest.body(), Materials[].class);

			JsonArray keys = xchecker(Arrays.asList(materialsList));
			pResponse.status(200);
			return returnMessage.getOkMessage(keys.toString());

		} catch (IOException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem with MATERIALS XChecker");
		}

	}

	public static Object singlexcheck(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		String[] id = pRequest.params("id").split(",");

		slf4jLogger.debug("Parameters: " + id[0]);

		pResponse.type("application/json");

		ArrayList<Materials> materialsList = new ArrayList<Materials>();

		for (int idx = 0; idx < id.length; idx++) {
			Materials material = new Materials();
			material.setItemcode(id[idx]);
			materialsList.add(material);
		}

		JsonArray keys = xchecker(materialsList);
		pResponse.status(200);
		return returnMessage.getOkMessage(keys.toString());

	}

	public static JsonArray xchecker(List<Materials> materialsList) {

		datastore = NoSqlController.getInstance().getDatabase();

		JsonArray jArray = new JsonArray();

		Iterator<Materials> itr = materialsList.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();
			String itemCode = material.getItemcode();

			slf4jLogger.debug("Searching in DB for item " + itemCode);

			JsonObject item_result = new JsonObject();

			try {
			
				Materials result = MaterialsController.read(itemCode);
		
				// Material found - there should be only one
				item_result.addProperty("itemcode", result.getItemcode());
				item_result.addProperty("description", result.getDescription());
				item_result.addProperty("status", "Material found");
				
			} catch( NoSuchElementException ex) {

				// Material not found
				item_result.addProperty("itemcode", material.getItemcode());
				item_result.addProperty("description", "-");
				item_result.addProperty("status", "Material not in DB");
			}

			jArray.add(item_result);
		}

		return jArray;

	}

	public static Object calculateWeight(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		String id = pRequest.params("id");

		pResponse.type("application/json");

		slf4jLogger.info(pRequest.body());

		JsonObject body = new Gson().fromJson(pRequest.body(), JsonObject.class);

		double quantity = body.get("quantity").getAsDouble();
		String units = body.get("units").getAsString();

		JsonObject result = calculateWeight(id, quantity, units);
		pResponse.status(200);
		return returnMessage.getOkMessage(result.toString());

	}

	private static JsonObject calculateWeight(String itemCode, double quantity, String units) {

		datastore = NoSqlController.getInstance().getDatabase();

		slf4jLogger.info("Searching in DB for item " + itemCode);
		slf4jLogger.info("Quantity is set to " + String.valueOf(quantity));
		slf4jLogger.info("Units are " + units);

		JsonObject item_result = new JsonObject();

		try {

			Materials result = MaterialsController.read(itemCode);

			QuotedMaterials material = new QuotedMaterials();
			material.setDescription(result.getDescription());
			material.setDimensions(result.getDimensions());
			material.setCategory(result.getCategory());
			material.setType(result.getType());
			material.setQuantity(quantity);
			material.setUnit(units);

			double weight = GeneralSvc.calculateMaterialWeight(material);

			slf4jLogger.info("Material weight = " + String.valueOf(weight));

			item_result.addProperty("itemcode", result.getItemcode());
			item_result.addProperty("description", result.getDescription());
			item_result.addProperty("quantity", String.format("%.2f", quantity));
			item_result.addProperty("weight", String.format("%.2f", weight));

		} catch (NoSuchElementException ex) {

			slf4jLogger.info(ex.getMessage());

			item_result.addProperty("itemcode", itemCode);
			item_result.addProperty("description", "-");
			item_result.addProperty("quantity", "-");
			item_result.addProperty("weight", "Material not in DB");

		}

		return item_result;

	}

}
