/**
 * 
 */
package co.phystech.aosorio.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.WriteResult;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.config.ProvidersPrefix;
import co.phystech.aosorio.exceptions.AlreadyExistsException;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.Providers;
import co.phystech.aosorio.services.Utilities;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class ProvidersController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ProvidersController.class);

	private static Datastore datastore;

	public static Object create(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		pResponse.type("application/json");

		try {

			slf4jLogger.info(pRequest.body());

			ObjectMapper mapper = new ObjectMapper();

			ArrayList<Providers> newProviders = mapper.readValue(pRequest.body(),
					new TypeReference<ArrayList<Providers>>() {
					});

			JsonArray keys = create(newProviders);
			pResponse.status(200);
			return returnMessage.getOkMessage(keys.toString());

		} catch (IOException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding Provider");

		} catch (AlreadyExistsException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Provider already exists");

		} catch (NoSuchElementException exception) {

			slf4jLogger.debug(exception.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Item in Provider not in DB");

		}

	}

	public static JsonArray create(ArrayList<Providers> providers)
			throws AlreadyExistsException, NoSuchElementException {

		datastore = NoSqlController.getInstance().getDatabase();

		JsonArray jArray = new JsonArray();

		ArrayList<Key<Providers>> addedKeys = new ArrayList<>();

		// ... check if material already exist in DB
		Iterator<Providers> itr = providers.iterator();

		while (itr.hasNext()) {

			JsonObject item_result = new JsonObject();

			Providers provider = (Providers) itr.next();

			Providers result = findOneByName(provider);

			if (result == null) {

				slf4jLogger.info("Provider not found ");

				String category = provider.getCategory().toUpperCase();
				String country = provider.getCountry();
				String providerId = ProvidersPrefix.getCodeprefix(category);

				if (providerId == null)
					throw new NoSuchElementException();

				int codeValue = Constants.PROVIDERS_ID_OFFSET + new Integer(count(provider) + 1);

				providerId = providerId.concat(String.valueOf(codeValue));

				provider.setProviderId(providerId);
				provider.setCountryCode(Utilities.getCountryCode(country));

				Key<Providers> key = create(provider);
				addedKeys.add(key);
				item_result.addProperty("providerId", provider.getProviderId());
				item_result.addProperty("providerName", provider.getProviderName());
				item_result.addProperty("status", "Saved");

			} else {

				item_result.addProperty("providerId", result.getProviderId());
				item_result.addProperty("providerName", result.getProviderName());
				item_result.addProperty("status", "Already in DB");
			}

			jArray.add(item_result);

		}

		slf4jLogger.info(String.valueOf(addedKeys.size()));

		return jArray;

	}

	public static Key<Providers> create(Providers provider) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.save(provider);
	}

	public static Object read(Request pRequest, Response pResponse) {

		datastore = NoSqlController.getInstance().getDatabase();

		String id = pRequest.params("id");

		slf4jLogger.debug("Parameters: " + id);

		Query<Providers> query = datastore.createQuery(Providers.class);
		List<Providers> result = query.field("providerId").equal(id).asList();

		try {

			Providers provider = result.iterator().next();
			pResponse.status(200);
			pResponse.type("application/json");
			return provider;

		} catch (NoSuchElementException ex) {

			BackendMessage returnMessage = new BackendMessage();
			slf4jLogger.debug("Provider not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Provider not found");

		}

	}
	
	public static Object readAll(Request pRequest, Response pResponse) {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Providers> query = datastore.find(Providers.class);
		List<Providers> result = query.asList();

		try {

			pResponse.status(200);
			pResponse.type("application/json");
			return result;

		} catch (NoSuchElementException ex) {

			BackendMessage returnMessage = new BackendMessage();
			slf4jLogger.debug("Provider not found");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Provider not found");

		}

	}

	public static Providers read(String id) {

		datastore = NoSqlController.getInstance().getDatabase();

		return datastore.get(Providers.class, id);

	}

	public static WriteResult delete(Providers provider) {

		datastore = NoSqlController.getInstance().getDatabase();
		return datastore.delete(provider);
	}

	private static int count(Providers provider) {

		datastore = NoSqlController.getInstance().getDatabase();

		String category = provider.getCategory().toUpperCase();

		Query<Providers> query = datastore.createQuery(Providers.class);
		List<Providers> result = query.field("category").equal(category).asList();

		slf4jLogger.info("Found providers: " + String.valueOf(result.size()));
		return result.size();

	}

	public static Providers findOneByName(Providers provider) {

		datastore = NoSqlController.getInstance().getDatabase();

		String name = provider.getProviderName();
		String web = provider.getProviderWeb();

		Query<Providers> query = datastore.createQuery(Providers.class);
		List<Providers> result = query.field("providerName").containsIgnoreCase(name).field("providerWeb")
				.containsIgnoreCase(web).asList();

		if (result.isEmpty()) {
			return null;
		} else {
			slf4jLogger.info("Found with name: " + name + " " + String.valueOf(result.size()));
			return result.get(0);
		}

	}

}
