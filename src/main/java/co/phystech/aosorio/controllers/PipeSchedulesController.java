package co.phystech.aosorio.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.models.PipeSchedules;

public class PipeSchedulesController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(PipeSchedulesController.class);

	private static Datastore datastore;

	public static PipeSchedules read(String code, String schDiameter) throws NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();
		
		Query<PipeSchedules> query = datastore.createQuery(PipeSchedules.class);
		//... slf4jLogger.info(code + "\t" + schDiameter);
		
		List<PipeSchedules> result = query.field("code").equal(code).field("nps").equal(schDiameter).asList();

		if (result.isEmpty())
			throw new NoSuchElementException("No SCH found on DB");
		else {
			slf4jLogger.debug("Schedule found");
			return result.get(0);
		}

	}
}
