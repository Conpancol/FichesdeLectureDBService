/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.models.Fittings;

/**
 * @author AOSORIO
 *
 */
public class FittingsController {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(FittingsController.class);

	private static Datastore datastore;

	public static Fittings read(String category, String schedule, String pipeSize) {

		datastore = NoSqlController.getInstance().getDatabase();
		
		Query<Fittings> query = datastore.createQuery(Fittings.class);
		//... slf4jLogger.info(code + "\t" + schDiameter);
		
		List<Fittings> result = query.field("category").equal(category).
				field("schedule").contains(schedule).field("pipeSize").equal(pipeSize).asList();

		if (result.isEmpty()) {
			return null;
		} else {
			slf4jLogger.debug("Fitting found");
			return result.get(0);
		}
		
	}


}
