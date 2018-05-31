/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
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

	public RequestForQuotesController() {
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
		
			RequestForQuotes newRFQ = mapper.readValue(pRequest.body(), RequestForQuotes.class);
			
			Key<RequestForQuotes> keys = create(newRFQ);
			ObjectId id = (ObjectId) keys.getId();
			
			slf4jLogger.info(id.toString());
			
			pResponse.status(200);
			return returnMessage.getOkMessage(String.valueOf(id));
			
		} catch (IOException jpe) {
			jpe.printStackTrace();
			slf4jLogger.debug("Problem adding fiche");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding RFQ");
		} 
					
	}
	
	public static Object read(Request pRequest, Response pResponse) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		int id = Integer.valueOf(pRequest.params("id"));
		
		slf4jLogger.debug("Parameters: " + id);

		Query<RequestForQuotes> query = datastore.createQuery(RequestForQuotes.class);
		List<RequestForQuotes> result = query.field("internalCode").equal(id).asList();
		
		try {
			
			RequestForQuotes rfq = result.iterator().next();
			pResponse.status(200);
			pResponse.type("application/json");
			return rfq;
			
		} catch (NoSuchElementException ex) {
			
			BackendMessage returnMessage = new BackendMessage();
			slf4jLogger.debug("RFQ not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("No RFQ Found");
			
		}

	}
	
	public static Key<RequestForQuotes> create(RequestForQuotes rfq) {
		return datastore.save(rfq);
	}
	
	public RequestForQuotes read(ObjectId id) {
		return datastore.get(RequestForQuotes.class, id);
	}

	public UpdateResults update(RequestForQuotes rfq, UpdateOperations<RequestForQuotes> operations) {
		return datastore.update(rfq, operations);
	}

	public WriteResult delete(RequestForQuotes rfq) {
		return datastore.delete(rfq);
	}

	public UpdateOperations<RequestForQuotes> createOperations() {
		return datastore.createUpdateOperations(RequestForQuotes.class);
	}

}
