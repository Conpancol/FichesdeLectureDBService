/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
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
			slf4jLogger.debug("Problem adding fiche");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding RFQ");
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
