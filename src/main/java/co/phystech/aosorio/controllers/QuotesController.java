/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
			pResponse.status(200);

			return returnMessage.getOkMessage("OK");

		} catch (IOException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding QUOTE");

		} catch (AlreadyExistsException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("QUOTE already exists");

		} catch (NoSuchElementException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Item in QUOTE not in DB");

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

	public static Quotes read(ObjectId id) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.get(Quotes.class, id);
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
			}

		}

	}

}
