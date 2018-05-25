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

import co.phystech.aosorio.models.Quotes;

/**
 * @author AOSORIO
 *
 */
public class QuotesController {
	
private Datastore datastore;
	
	public QuotesController() {
		NoSqlController dbcontroller = NoSqlController.getInstance();
		datastore = dbcontroller.getDatabase();
	}

	public Key<Quotes> create(Quotes quote) {
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
