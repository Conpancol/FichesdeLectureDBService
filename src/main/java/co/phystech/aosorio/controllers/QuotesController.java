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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.WriteResult;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.ExtMaterials;
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
	
	private static ArrayList<QuotedMaterials> missingMaterials = new ArrayList<QuotedMaterials>();

	public QuotesController() {
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

			Quotes newQuote = mapper.readValue(pRequest.body(), Quotes.class);

			Key<Quotes> keys = create(newQuote);
			pResponse.status(200);
			
			if (keys != null) {
				ObjectId id = (ObjectId) keys.getId();
				slf4jLogger.info(id.toString());
				return returnMessage.getOkMessage(String.valueOf(id));
			} else {
				return returnMessage.getNotOkMessage("Quote already exists");
			}

		} catch (IOException jpe) {
			jpe.printStackTrace();
			slf4jLogger.debug("Problem adding QUOTE");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding QUOTE");
		}

	}
	
	public static Key<Quotes> create(Quotes quote) {
	
		Query<Quotes> query = datastore.createQuery(Quotes.class);
		List<Quotes> result = query.field("providerCode").equal(quote.getProviderCode()).asList();

		if (result.isEmpty()) {
			slf4jLogger.info("Quote not found " + quote.getInternalCode());
			slf4jLogger.info("Size of material list " + String.valueOf(quote.getMaterialList().size()));
			
			// ...check for missing material information and remove materials
			// not in DB
			updateMaterialList(quote);
			
			//
			calculateMaterialWeights(quote);
			
			// ...save quoted materials in its own collection
			saveQuotedMaterials(quote);

			return datastore.save(quote);
		}

		return null;

	}


	public static Object read(Request pRequest, Response pResponse) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		String id = pRequest.params("id");
		
		slf4jLogger.debug("Parameters: " + id);

		Query<Quotes> query = datastore.createQuery(Quotes.class);
		List<Quotes> result = query.field("providerCode").equal(id).asList();
		
		try {
			
			Quotes quote = result.iterator().next();
			pResponse.status(200);
			pResponse.type("application/json");
			return quote;
			
		} catch (NoSuchElementException ex) {
			
			BackendMessage returnMessage = new BackendMessage();
			slf4jLogger.debug("Quote not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Quote not found");
			
		}

	}
	
	public Quotes read(ObjectId id) {
		return datastore.get(Quotes.class, id);
	}

	public UpdateResults update(Quotes quote, UpdateOperations<Quotes> operations) {
		return datastore.update(quote, operations);
	}

	public WriteResult delete(Quotes quote) {
		return datastore.delete(quote);
	}

	public UpdateOperations<Quotes> createOperations() {
		return datastore.createUpdateOperations(Quotes.class);
	}
	
	private static void updateMaterialList(Quotes quote) {

		slf4jLogger.debug("Entering updater");
		
		missingMaterials.clear();
		
		List<QuotedMaterials> materialList = quote.getMaterialList();
		Iterator<QuotedMaterials> itr = materialList.iterator();

		while (itr.hasNext()) {

			QuotedMaterials material = itr.next();
			String itemCode = material.getItemcode();

			slf4jLogger.debug("Searching in DB for item " + itemCode);

			List<Materials> result = MaterialsController.read(itemCode);

			if (result.isEmpty()) {
				// Material not found
				missingMaterials.add(material);
			} else {
				// Material found - there should be only one
				material.setCategory(result.get(0).getCategory());
				material.setDescription(result.get(0).getDescription());
				material.setDimensions(result.get(0).getDimensions());
				material.setType(result.get(0).getType());
				slf4jLogger.debug("Incoming list materials updated");
			}

		}

		itr = missingMaterials.iterator();

		while (itr.hasNext()) {

			ExtMaterials material = itr.next();
			boolean status = materialList.remove(material);
			slf4jLogger.debug("Removed material from incoming list " + status);

		}

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
			double theoreticalWeight = GeneralSvc.calculateMaterialWeight( material );
			material.setTheoreticalWeight(theoreticalWeight);
			
			
		}
		
	}

	
}
