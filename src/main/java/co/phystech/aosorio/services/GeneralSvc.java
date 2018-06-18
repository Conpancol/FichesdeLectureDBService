/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import spark.ResponseTransformer;

/**
 * @author AOSORIO
 *
 */
public class GeneralSvc {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(GeneralSvc.class);

	public static String dataToJson(Object data) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			// mapper.enable(SerializationFeature.INDENT_OUTPUT);
			StringWriter sw = new StringWriter();
			mapper.writeValue(sw, data);
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException("IOException from a StringWriter?");
		}
	}

	public static ResponseTransformer json() {

		return GeneralSvc::dataToJson;
	}

	/**
	 * Setup a temporary area to store files
	 * 
	 * @param target
	 * @return absolute path to temporary local directory
	 */
	public static String setupTmpDir(String target) {

		ArrayList<String> localStorageEnv = new ArrayList<String>();

		localStorageEnv.add("LOCAL_TMP_PATH_ENV");
		localStorageEnv.add("TMP");
		localStorageEnv.add("HOME");

		Iterator<String> itrPath = localStorageEnv.iterator();

		boolean found = false;

		File tmpDir = null;

		while (itrPath.hasNext()) {
			String testPath = itrPath.next();
			String value = System.getenv(testPath);
			if (value != null) {
				tmpDir = new File(value + target);
				tmpDir.mkdir();
				found = true;
				break;
			}
		}

		if (!found) {
			tmpDir = new File(target);
		}

		return tmpDir.getAbsolutePath();

	}

	public static JsonObject readJsonFromUrl(String url) throws IOException, JsonParseException {

		URL urlObject = null;
		HttpURLConnection request = null;

		try {

			urlObject = new URL(url);
			request = (HttpURLConnection) urlObject.openConnection();
			request.setRequestMethod("GET");
			request.setReadTimeout(15 * 1000);
			request.connect();

			int responseCode = request.getResponseCode();

			slf4jLogger.info(String.valueOf(responseCode));

			BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));

			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = rd.readLine()) != null) {
				response.append(inputLine);
			}
			rd.close();
			
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(response.toString()).getAsJsonObject();

			return json;

		} catch (JsonSyntaxException ex) {
			
			slf4jLogger.info(ex.getLocalizedMessage());
			return null;
		
		}

	}

}
