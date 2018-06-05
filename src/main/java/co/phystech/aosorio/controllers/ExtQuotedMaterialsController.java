/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.models.ExtQuotedMaterials;


/**
 * @author AOSORIO
 *
 */
public class ExtQuotedMaterialsController {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(MaterialsController.class);

	private static Datastore datastore;
	
	public static boolean create(ExtQuotedMaterials material) {
		
		datastore = NoSqlController.getInstance().getDatabase();
		slf4jLogger.debug("saving new extended material");
		
		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);
		
		List<ExtQuotedMaterials> result = query.field("providerId").equal(material.getProviderId()).
				field("itemcode").equal(material.getItemcode()).
				field("quantity").equal(material.getQuantity()).asList();
		
		if( result.isEmpty() ) {
			
			datastore.save(material);
			return true;
			
		} else {		
			//update found material with new one		
			String lastUpdate = new StringBuilder(256).
					append(result.get(0).getUpdateDate()).
					append(",").
					append(material.getUpdateDate()).toString();

			UpdateOperations<ExtQuotedMaterials> ops = createOperations();
			ops.set("unitPrice", material.getUnitPrice());
			ops.set("totalPrice", material.getTotalPrice());
			ops.set("updatedDate", lastUpdate);
			
			UpdateResults upresult = update(result.get(0),ops);
			
			return upresult.getUpdatedExisting();

		}
		
		
	}
	
	private static UpdateResults update(ExtQuotedMaterials material, UpdateOperations<ExtQuotedMaterials> operations) {
		return datastore.update(material, operations);
	}
	
	private static UpdateOperations<ExtQuotedMaterials> createOperations() {
		return datastore.createUpdateOperations(ExtQuotedMaterials.class);
	}
	
}
