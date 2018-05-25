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

import co.phystech.aosorio.models.Materials;

/**
 * @author AOSORIO
 *
 */
public class MaterialsController {

	private Datastore datastore;
	
	public MaterialsController() {
		NoSqlController dbcontroller = NoSqlController.getInstance();
		datastore = dbcontroller.getDatabase();
	}

	public Key<Materials> create(Materials material) {
		return datastore.save(material);
	}

	public Materials read(ObjectId id) {
		return datastore.get(Materials.class, id);
	}

	public UpdateResults update(Materials material, UpdateOperations<Materials> operations) {
		return datastore.update(material, operations);
	}

	public WriteResult delete(Materials material) {
		return datastore.delete(material);
	}

	public UpdateOperations<Materials> createOperations() {
		return datastore.createUpdateOperations(Materials.class);
	}

	
}
