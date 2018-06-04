/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.Materials;
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

	private static ArrayList<ExtMaterials> missingMaterials = new ArrayList<ExtMaterials>();

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
			pResponse.status(200);

			if (keys != null) {
				ObjectId id = (ObjectId) keys.getId();
				slf4jLogger.info(id.toString());

				if (missingMaterials.isEmpty()) {
					return returnMessage.getOkMessage(String.valueOf(id));
				} else {
					return returnMessage.getNotOkMessage(getMissingMaterials());
				}
			} else {
				return returnMessage.getNotOkMessage("RFQ already exists");
			}

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
			return returnMessage.getNotOkMessage("RFQ not found");

		}

	}

	public static Key<RequestForQuotes> create(RequestForQuotes rfq) {

		Query<RequestForQuotes> query = datastore.createQuery(RequestForQuotes.class);
		List<RequestForQuotes> result = query.field("internalCode").equal(rfq.getInternalCode()).asList();

		if (result.isEmpty()) {
			slf4jLogger.info("RFQ not found " + rfq.getInternalCode());
			slf4jLogger.info("Size of material list " + String.valueOf(rfq.getMaterialList().size()));
			// ...check for missing material information and remove materials
			// not in DB
			updateMaterialList(rfq);

			return datastore.save(rfq);
		}

		return null;
	}

	private static void updateMaterialList(RequestForQuotes rfq) {

		slf4jLogger.debug("Entering updater");
		
		missingMaterials.clear();
		
		List<ExtMaterials> materialList = rfq.getMaterialList();
		Iterator<ExtMaterials> itr = materialList.iterator();

		while (itr.hasNext()) {

			ExtMaterials material = itr.next();
			String itemCode = material.getItemcode();

			slf4jLogger.debug("Searching in DB for item " + itemCode);

			List<Materials> result = MaterialsController.read(itemCode);

			if (result.isEmpty()) {
				// Material not found
				missingMaterials.add(material);
			} else {
				// Material found - there should be only one
				material.setCategory(result.get(0).getCategory());
				material.setDescription(result.get(0).getDescription());
				material.setDimensions(result.get(0).getDimensions());
				material.setType(result.get(0).getType());
				slf4jLogger.debug("Incoming list materials updated");
			}

		}

		itr = missingMaterials.iterator();

		while (itr.hasNext()) {

			ExtMaterials material = itr.next();
			boolean status = materialList.remove(material);
			slf4jLogger.debug("Removed material from incoming list " + status);

		}

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

	/**
	 * @return the missingMaterials
	 */
	public static String getMissingMaterials() {

		Iterator<ExtMaterials> itr = missingMaterials.iterator();
		ArrayList<String> missingString = new ArrayList<String>();
		
		while (itr.hasNext()) {
			ExtMaterials material = (ExtMaterials) itr.next();
			missingString.add(material.getItemcode());
		}
		return missingString.toString();

	}

	/**
	 * @param missingMaterials
	 *            the missingMaterials to set
	 */
	public void setMissingMaterials(ArrayList<ExtMaterials> missingMaterials) {
		RequestForQuotesController.missingMaterials = missingMaterials;
	}

}
