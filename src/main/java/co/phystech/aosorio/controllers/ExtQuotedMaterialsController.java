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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.WriteResult;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.Materials;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class ExtQuotedMaterialsController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ExtQuotedMaterialsController.class);

	private static Datastore datastore;

	static class Result {

		private String itemCode;
		private int revision;
		private String status;

		public String getItemCode() {
			return itemCode;
		}

		public void setItemCode(String itemCode) {
			this.itemCode = itemCode;
		}

		public int getRevision() {
			return revision;
		}

		public void setRevision(int revision) {
			this.revision = revision;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	}

	public static Object create(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();
		pResponse.type("application/json");

		try {

			slf4jLogger.info(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<ExtQuotedMaterials> newMaterials = mapper.readValue(pRequest.body(),
					new TypeReference<ArrayList<ExtQuotedMaterials>>() {
					});

			Iterator<Result> resultsItr = create(newMaterials).iterator();
			JsonArray jArray = new JsonArray();

			while (resultsItr.hasNext()) {
				Result result = resultsItr.next();
				JsonObject jsonResult = new JsonObject();
				jsonResult.addProperty("itemcode", result.getItemCode());
				jsonResult.addProperty("revision", result.getRevision());
				jsonResult.addProperty("status", result.getStatus());
				jArray.add(jsonResult);
			}

			pResponse.status(200);
			return returnMessage.getOkMessage(jArray.toString());

		} catch (IOException jpe) {
			jpe.printStackTrace();
			slf4jLogger.debug("Problem adding extended quoted material list");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding extended quoted material list");
		}

	}
	
	public static Object read(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();
		pResponse.type("application/json");

		try {

			String id = pRequest.params("id");
			slf4jLogger.info("Parameters: " + id);

			ExtQuotedMaterials quote = read(id);
			pResponse.status(200);
			pResponse.type("application/json");
			String resultJson = new Gson().toJson(quote);
			return returnMessage.getOkMessage(resultJson);

		} catch (NoSuchElementException jpe) {
			
			slf4jLogger.debug("QUOTED material not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("QUOTED material not found");

		}

	}

	public static Object update(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();
				
		pResponse.type("application/json");

		try {

			slf4jLogger.info(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();
			
			String id = pRequest.params("id");
			slf4jLogger.info("Parameters: " + id);
			
			ArrayList<ExtQuotedMaterials> newQuote = mapper.readValue(pRequest.body(),
					new TypeReference<ArrayList<ExtQuotedMaterials>>(){
					});
			
			slf4jLogger.info("data: " + newQuote.toString());
			
			ExtQuotedMaterials modified = newQuote.iterator().next();

			Key<ExtQuotedMaterials> keys = update(id, modified);
			ObjectId quoteId = (ObjectId) keys.getId();
			ExtQuotedMaterials quote = read(quoteId);
			
			JsonArray jArray = new JsonArray();
			JsonObject result = new JsonObject();
			result.addProperty("itemcode", quote.getItemcode());
			result.addProperty("projectId", quote.getProjectId());
			result.addProperty("status", "Updated");
			jArray.add(result);
						
			pResponse.status(200);
			return returnMessage.getOkMessage(jArray.toString());

		} catch (IOException exception) {

			slf4jLogger.info(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
						
			return returnMessage.getNotOkMessage("Problem updating QUOTE");
			
		} catch (NoSuchElementException exception) {

			slf4jLogger.info(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			
			return returnMessage.getNotOkMessage("ITEM not found in DB");

		}

	}
	
	public static ArrayList<Result> create(ArrayList<ExtQuotedMaterials> newMaterials) {

		datastore = NoSqlController.getInstance().getDatabase();

		Iterator<ExtQuotedMaterials> itrMaterial = newMaterials.iterator();

		ArrayList<Result> results = new ArrayList<Result>();

		while (itrMaterial.hasNext()) {

			ExtQuotedMaterials currentMaterial = itrMaterial.next();

			Result resultStatus = new Result();

			try {

				Materials material = MaterialsController.read(currentMaterial.getItemcode());

				currentMaterial.setDescription(material.getDescription());
				currentMaterial.setType(material.getType());
				currentMaterial.setDimensions(material.getDimensions());
				currentMaterial.setCategory(material.getCategory());
				currentMaterial.setCode(material.getCode());

				boolean result = create(currentMaterial);

				if (result) {
					// slf4jLogger.debug("New extended material added");
					resultStatus.setItemCode(currentMaterial.getItemcode());
					resultStatus.setRevision(currentMaterial.getRevision());
					resultStatus.setStatus("Added");
				} else {
					resultStatus.setItemCode(currentMaterial.getItemcode());
					resultStatus.setRevision(currentMaterial.getRevision());
					resultStatus.setStatus("Updated");
				}

			} catch (NoSuchElementException ex) {
				//... slf4jLogger.debug("Material does not exist in DB - check Itemcode");
				resultStatus.setItemCode(currentMaterial.getItemcode());
				resultStatus.setRevision(currentMaterial.getRevision());
				resultStatus.setStatus("Check itemcode");
				
			}

			results.add(resultStatus);

		}

		return results;
	}

	public static boolean create(ExtQuotedMaterials material) {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);

		List<ExtQuotedMaterials> result = query.field("providerId").equal(material.getProviderId())
				.field("itemcode").equal(material.getItemcode())
				.field("quantity").equal(material.getQuantity())
				.field("orderNumber").equal(material.getOrderNumber())
				.field("updateDate").equal(material.getUpdateDate())
				.field("projectId").equal(material.getProjectId())
				.field("revision").equal(material.getRevision()).asList();

		if (result.isEmpty()) {
			slf4jLogger.debug("Quote not found, saving new extended material");
			datastore.save(material);
			return true;

		} else {
			slf4jLogger.info("Quote found, updating material");
			
			// update found material with new one
			//String lastUpdate = new StringBuilder().append(result.get(0).getUpdateDate()).append(",")
			//		.append(material.getUpdateDate()).toString();

			UpdateOperations<ExtQuotedMaterials> ops = createOperations();
			ops.set("unitPrice", material.getUnitPrice());
			ops.set("totalPrice", material.getTotalPrice());
			ops.set("updateDate", material.getUpdateDate());

			UpdateResults upresult = update(result.get(0), ops);

			upresult.getUpdatedExisting();

			return false;

		}

	}

	public static boolean create(List<ExtQuotedMaterials> materials) {

		datastore = NoSqlController.getInstance().getDatabase();

		Iterable<Key<ExtQuotedMaterials>> itrKeys = datastore.save(materials);
		List<Key<ExtQuotedMaterials>> target = new ArrayList<Key<ExtQuotedMaterials>>();

		itrKeys.forEach(target::add);
		if (materials.size() == target.size())
			return true;

		return false;
	}

	public static ExtQuotedMaterials read(ObjectId id) {

		datastore = NoSqlController.getInstance().getDatabase();
	
		return datastore.get(ExtQuotedMaterials.class, id);
	
	}
	
	public static ExtQuotedMaterials read(String itemcode) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);
		List<ExtQuotedMaterials> result = query.field("itemcode").equal(itemcode).asList();
		
		if (result.isEmpty())
			throw new NoSuchElementException();

		return result.iterator().next();
		
	}
	
	public static WriteResult delete(ExtQuotedMaterials material) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.delete(material);
	}

	private static Key<ExtQuotedMaterials> update(String id, ExtQuotedMaterials modified) throws NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		ExtQuotedMaterials current = read(id);

		if (current == null)
			throw new NoSuchElementException();

		current.setUnitPrice(modified.getUnitPrice());
		current.setTotalPrice(modified.getTotalPrice());
		current.setRevision(modified.getRevision());
        
		return update(current);

	}
	
	public static Key<ExtQuotedMaterials> update(ExtQuotedMaterials modified) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.save(modified);
	}
	
	private static UpdateResults update(ExtQuotedMaterials material, UpdateOperations<ExtQuotedMaterials> operations) {
		return datastore.update(material, operations);
	}

	private static UpdateOperations<ExtQuotedMaterials> createOperations() {
		return datastore.createUpdateOperations(ExtQuotedMaterials.class);
	}

	public static List<ExtQuotedMaterials> readBy(String field, String category) {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);
		List<ExtQuotedMaterials> result = query.field(field).equal(category).asList();

		return result;

	}

}