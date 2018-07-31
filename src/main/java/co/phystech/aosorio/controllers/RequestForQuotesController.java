/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
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
import com.google.gson.Gson;
import com.mongodb.WriteResult;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.exceptions.AlreadyExistsException;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.RequestForQuotes;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class RequestForQuotesController {

	private static Datastore datastore;

	private final static Logger slf4jLogger = LoggerFactory.getLogger(RequestForQuotesController.class);

	public static Object create(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		pResponse.type("application/json");

		try {

			slf4jLogger.debug(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();

			RequestForQuotes newRFQ = mapper.readValue(pRequest.body(), RequestForQuotes.class);

			create(newRFQ);
			pResponse.status(200);
			return returnMessage.getOkMessage("RFQ Added");

		} catch (IOException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding RFQ");

		} catch (AlreadyExistsException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("RFQ already exists");

		} catch (NoSuchElementException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Item in RFQ not in DB");

		}

	}

	public static Key<RequestForQuotes> create(RequestForQuotes rfq)
			throws AlreadyExistsException, NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<RequestForQuotes> query = datastore.createQuery(RequestForQuotes.class);
		List<RequestForQuotes> result = query.field("internalCode").equal(rfq.getInternalCode()).asList();

		if (result.isEmpty()) {
			slf4jLogger.info("RFQ not found " + rfq.getInternalCode());
			slf4jLogger.info("Size of material list " + String.valueOf(rfq.getMaterialList().size()));

			try {
				// 1. this goes always first
				xcheck(rfq.getMaterialList());
				return datastore.save(rfq);

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

		BackendMessage returnMessage = new BackendMessage();
		
		int id = Integer.valueOf(pRequest.params("id"));

		slf4jLogger.debug("Parameters: " + id);

		Query<RequestForQuotes> query = datastore.createQuery(RequestForQuotes.class);
		List<RequestForQuotes> result = query.field("internalCode").equal(id).asList();

		pResponse.type("application/json");
		
		try {

			RequestForQuotes rfq = result.iterator().next();
			pResponse.status(200);
			
			Gson gson = new Gson();
			String rfq_json = gson.toJson(rfq);			
			return returnMessage.getOkMessage(rfq_json);

		} catch (NoSuchElementException exception) {
			
			slf4jLogger.debug("RFQ not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("RFQ not found");

		}

	}

	public static RequestForQuotes read(ObjectId id) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.get(RequestForQuotes.class, id);
	}

	public static UpdateResults update(RequestForQuotes rfq, UpdateOperations<RequestForQuotes> operations) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.update(rfq, operations);
	}

	public static WriteResult delete(RequestForQuotes rfq) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.delete(rfq);
	}

	public static UpdateOperations<RequestForQuotes> createOperations() {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.createUpdateOperations(RequestForQuotes.class);
	}

	public static void xcheck(List<ExtMaterials> materialList) throws NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);

		Iterator<ExtMaterials> itr = materialList.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			List<Materials> result = query.field("itemcode").equal(material.getItemcode()).asList();

			if (result.isEmpty()) {
				throw new NoSuchElementException();
			}

		}

	}

}
