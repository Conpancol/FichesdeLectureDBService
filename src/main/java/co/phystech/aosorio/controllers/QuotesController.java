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
import co.phystech.aosorio.models.Quotes;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class QuotesController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(QuotesController.class);

	private static Datastore datastore;

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
			ObjectId id = (ObjectId) keys.getId();

			slf4jLogger.info(id.toString());

			pResponse.status(200);
			return returnMessage.getOkMessage(String.valueOf(id));

		} catch (IOException jpe) {
			jpe.printStackTrace();
			slf4jLogger.debug("Problem adding QUOTE");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding QUOTE");
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
	
	public static Key<Quotes> create(Quotes quote) {
		return datastore.save(quote);
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

}
