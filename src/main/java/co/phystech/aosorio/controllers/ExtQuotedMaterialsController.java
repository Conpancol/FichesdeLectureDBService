/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.WriteResult;

import co.phystech.aosorio.models.ExtQuotedMaterials;


/**
 * @author AOSORIO
 *
 */
public class ExtQuotedMaterialsController {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(ExtQuotedMaterialsController.class);

	private static Datastore datastore;
	
	public static boolean create(ExtQuotedMaterials material) {
		
		datastore = NoSqlController.getInstance().getDatabase();
		
		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);
		
		List<ExtQuotedMaterials> result = query.field("providerId").equal(material.getProviderId()).
				field("itemcode").equal(material.getItemcode()).
				field("quantity").equal(material.getQuantity()).asList();
		
		if( result.isEmpty() ) {
			slf4jLogger.info("Quote not found, saving new extended material");
			datastore.save(material);
			return true;
			
		} else {		
			slf4jLogger.info("Quote found, updating material");
			//update found material with new one		
			String lastUpdate = new StringBuilder().
					append(result.get(0).getUpdateDate()).
					append(",").
					append(material.getUpdateDate()).toString();

			UpdateOperations<ExtQuotedMaterials> ops = createOperations();
			ops.set("unitPrice", material.getUnitPrice());
			ops.set("totalPrice", material.getTotalPrice());
			ops.set("updateDate", lastUpdate);
			
			UpdateResults upresult = update(result.get(0),ops);
			
			return upresult.getUpdatedExisting();

		}
				
	}
	
	public static boolean create(List<ExtQuotedMaterials> materials) {
		
		datastore = NoSqlController.getInstance().getDatabase();
		
		Iterable<Key<ExtQuotedMaterials>>  itrKeys = datastore.save(materials);
		List<Key<ExtQuotedMaterials>> target = new ArrayList<Key<ExtQuotedMaterials>>();
		
		itrKeys.forEach(target::add);
		if( materials.size() == target.size())
			return true;
		
		return false;
	}
	
	public static WriteResult delete(ExtQuotedMaterials material) {
		
		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.delete(material);
	}
	
	private static UpdateResults update(ExtQuotedMaterials material, UpdateOperations<ExtQuotedMaterials> operations) {
		return datastore.update(material, operations);
	}
	
	private static UpdateOperations<ExtQuotedMaterials> createOperations() {
		return datastore.createUpdateOperations(ExtQuotedMaterials.class);
	}
	
	public static List<ExtQuotedMaterials> readBy(String field, String category) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);		
		List<ExtQuotedMaterials> result = query.field(field).equal(category).asList();

		return result;

	}

}