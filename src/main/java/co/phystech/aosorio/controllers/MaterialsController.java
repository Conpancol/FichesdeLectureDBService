/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
import java.util.ArrayList;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.WriteResult;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.Materials;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class MaterialsController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(MaterialsController.class);

	private static Datastore datastore;

	public MaterialsController() {
		NoSqlController dbcontroller = NoSqlController.getInstance();
		datastore = dbcontroller.getDatabase();
	}

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

		datastore = NoSqlController.getInstance().getDatabase();

		String id = pRequest.params("id");

		slf4jLogger.debug("Parameters: " + id);

		List<Materials> result = read(id);

		try {

			Materials material = result.iterator().next();
			pResponse.status(200);
			pResponse.type("application/json");
			return material;

		} catch (NoSuchElementException ex) {

			BackendMessage returnMessage = new BackendMessage();
			slf4jLogger.debug("Material not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Material not found");

		}

	}

	public static Materials read(ObjectId id) {

		datastore = NoSqlController.getInstance().getDatabase();

		return datastore.get(Materials.class, id);

	}

	public static List<Materials> read(String itemCode) {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("itemcode").equal(itemCode).asList();

		return result;
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

}
