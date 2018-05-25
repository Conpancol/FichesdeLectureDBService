/**
 * 
 */
package co.phystech.aosorio.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.WriteResult;

import co.phystech.aosorio.models.RequestForQuotes;

/**
 * @author AOSORIO
 *
 */
public class RequestForQuotesController {
	
private Datastore datastore;
	
	public RequestForQuotesController() {
		NoSqlController dbcontroller = NoSqlController.getInstance();
		datastore = dbcontroller.getDatabase();
	}

	public Key<RequestForQuotes> create(RequestForQuotes rfq) {
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
