/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import co.phystech.aosorio.exceptions.AlreadyExistsException;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.models.Quotes;
import co.phystech.aosorio.services.GeneralSvc;
import co.phystech.aosorio.services.OpenExchangeSvc;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class QuotesController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(QuotesController.class);

	private static Datastore datastore;

	public static Object create(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();
				
		pResponse.type("application/json");

		try {

			slf4jLogger.info(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();

			Quotes newQuote = mapper.readValue(pRequest.body(), Quotes.class);

			create(newQuote);
		
			JsonArray jArray = new JsonArray();
			JsonObject result = new JsonObject();
			result.addProperty("internalCode", newQuote.getInternalCode());
			result.addProperty("externalCode", newQuote.getExternalCode());
			result.addProperty("status", "Added");
			jArray.add(result);
						
			pResponse.status(200);
			return returnMessage.getOkMessage(jArray.toString());

		} catch (IOException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
						
			return returnMessage.getNotOkMessage("Problem adding QUOTE");

		} catch (AlreadyExistsException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			
			return returnMessage.getNotOkMessage("QUOTE (from provider) already exist");
			
		} catch (NoSuchElementException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			
			return returnMessage.getNotOkMessage("ITEM not found in DB");

		}

	}

	public static Key<Quotes> create(Quotes quote) throws AlreadyExistsException, NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Quotes> query = datastore.createQuery(Quotes.class);
		List<Quotes> result = query.field("providerCode").equal(quote.getProviderCode()).asList();

		if (result.isEmpty()) {
			slf4jLogger.info("Quote not found " + quote.getInternalCode());
			slf4jLogger.info("Size of material list " + String.valueOf(quote.getMaterialList().size()));

			try {
				// 1. this goes always first
				xcheck(quote.getMaterialList());
				//
				calculateMaterialWeights(quote);
				// ...save quoted materials in its own collection
				saveQuotedMaterials(quote);

				return datastore.save(quote);

			} catch (NoSuchElementException exception) {

				slf4jLogger.info("Item not found in DB");
				throw exception;
			}

		} else {

			throw new AlreadyExistsException();
		}

	}

	public static Object read(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();
		
		try {

			String id = pRequest.params("id");
			slf4jLogger.info("Parameters: " + id);
			
			Quotes quote = read(id);
			pResponse.status(200);
			pResponse.type("application/json");
			String resultJson = new Gson().toJson(quote);
			return returnMessage.getOkMessage(resultJson);

		} catch (NoSuchElementException ex) {

			slf4jLogger.debug("QUOTE not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("QUOTE not found");

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
			
			ArrayList<Quotes> newQuote = mapper.readValue(pRequest.body(),
					new TypeReference<ArrayList<Quotes>>(){
					});
			
			slf4jLogger.info("data: " + newQuote.toString());
			
			Quotes modified = newQuote.iterator().next();

			Key<Quotes> keys = update(id, modified);
			ObjectId quoteId = (ObjectId) keys.getId();
			Quotes quote = read(quoteId);
			
			JsonArray jArray = new JsonArray();
			JsonObject result = new JsonObject();
			result.addProperty("internalCode", quote.getInternalCode());
			result.addProperty("externalCode", quote.getExternalCode());
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
	
	public static Quotes read(ObjectId id) {

		datastore = NoSqlController.getInstance().getDatabase();
	
		return datastore.get(Quotes.class, id);
	
	}
	
	public static Quotes read(String providerCode) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		Query<Quotes> query = datastore.createQuery(Quotes.class);
		List<Quotes> result = query.field("providerCode").equal(providerCode).asList();
		
		if (result.isEmpty())
			throw new NoSuchElementException();

		return result.iterator().next();
		
	}

	private static Key<Quotes> update(String id, Quotes modified) throws NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		Quotes current = read(id);

		if (current == null)
			throw new NoSuchElementException();

		current.setInternalCode(modified.getInternalCode());
		current.setExternalCode(modified.getExternalCode());
		current.setMaterialList(modified.getMaterialList());
		current.setProviderCode(modified.getProviderCode());
		current.setReceivedDate(modified.getReceivedDate());
        current.setSentDate(modified.getSentDate());
        current.setUser(modified.getUser());
        current.setProviderId(modified.getProviderId());
        current.setProviderName(modified.getProviderName());
        current.setContactName(modified.getContactName());
        current.setIncoterms(modified.getIncoterms());
        current.setEdt(modified.getEdt());
        current.setNote(modified.getNote());

        current.setMaterialList(modified.getMaterialList());
        
		return update(current);

	}
	
	public static Key<Quotes> update(Quotes modified) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.save(modified);
	}
	
	public static UpdateResults update(Quotes quote, UpdateOperations<Quotes> operations) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		return datastore.update(quote, operations);
	
	}

	public static WriteResult delete(Quotes quote) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.delete(quote);
	}

	public static UpdateOperations<Quotes> createOperations() {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.createUpdateOperations(Quotes.class);
	}

	private static void saveQuotedMaterials(Quotes quote) {

		List<QuotedMaterials> materialList = quote.getMaterialList();
		Iterator<QuotedMaterials> itr = materialList.iterator();

		double usdTRM = OpenExchangeSvc.getUSDTRM();

		while (itr.hasNext()) {

			QuotedMaterials material = itr.next();

			ExtQuotedMaterials quotedMaterial = new ExtQuotedMaterials(material);

			quotedMaterial.setProviderId(quote.getProviderId());
			quotedMaterial.setProviderName(quote.getProviderName());

			Date now = new Date();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String updateDate = formatter.format(now);
			quotedMaterial.setUpdateDate(updateDate);
			quotedMaterial.setUsdTRM(usdTRM);
			quotedMaterial.setProjectId(quote.getInternalCode());
			quotedMaterial.setRevision(quote.getRevision());

			ExtQuotedMaterialsController.create(quotedMaterial);

		}

	}

	private static void calculateMaterialWeights(Quotes quote) {

		slf4jLogger.debug("Entering calculateMaterialWeights");

		List<QuotedMaterials> materialList = quote.getMaterialList();
		Iterator<QuotedMaterials> itr = materialList.iterator();

		while (itr.hasNext()) {

			QuotedMaterials material = itr.next();
			String itemCode = material.getItemcode();

			slf4jLogger.debug(itemCode);
			double theoreticalWeight = GeneralSvc.calculateMaterialWeight(material);
			material.setTheoreticalWeight(theoreticalWeight);

		}

	}
	
	private static void xcheck(List<QuotedMaterials> materialList) {
		
		datastore = NoSqlController.getInstance().getDatabase();

		Iterator<QuotedMaterials> itr = materialList.iterator();

		while (itr.hasNext()) {

			QuotedMaterials material = itr.next();
			Query<Materials> query = datastore.createQuery(Materials.class);
			List<Materials> result = query.field("itemcode").equal(material.getItemcode()).asList();

			if (result.isEmpty()) {
				slf4jLogger.info(material.getItemcode());
				throw new NoSuchElementException();
			
			} else {	
			
				material.setDescription(result.get(0).getDescription());
				material.setType(result.get(0).getType());
				material.setCategory(result.get(0).getCategory());				
				material.setDimensions(result.get(0).getDimensions());
				material.setCode(result.get(0).getCode());
			}

		}

	}

}
