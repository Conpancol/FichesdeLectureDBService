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
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.models.QuotedMaterials;
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

			slf4jLogger.debug(String.valueOf(responseCode));

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
			
			slf4jLogger.debug(ex.getLocalizedMessage());
			return null;
		
		}

	}

	public static double calculateMaterialWeight(QuotedMaterials material) {
				
		Supplier<FormulaFactory> formulaFactory = FormulaFactory::new;
		
		if( material.getCategory().equals("PIPE") || material.getCategory().equals("TUBE")) {
			
			Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");

			formula.addVariable("OD", Utilities.getODMM(material)*Constants.UNIT_MM_to_M);
			formula.addVariable("ID", Utilities.getIDMM(material)*Constants.UNIT_MM_to_M);
			formula.addVariable("H" , material.getQuantity());
			
			double volume = formula.eval();
			double density = Utilities.getDensity(material.getType()) * Constants.UNIT_KG_o_M3;

			slf4jLogger.debug("OD " + Utilities.getODMM(material));
			slf4jLogger.debug("ID " + Utilities.getIDMM(material));
			slf4jLogger.debug("H " + String.valueOf(material.getQuantity()));
			slf4jLogger.debug("rho " + String.valueOf(density));
			slf4jLogger.debug("volume " + String.valueOf(volume));
			
			return volume*density;

		} else if ( material.getCategory().contains("PLATE") ) {
			
			Formula formula = formulaFactory.get().getFormula("CUBEVOL");
			
			ArrayList<Double> dims = Utilities.getPlateDimsMM(material);
			
			formula.addVariable("H", dims.get(0)*Constants.UNIT_MM_to_M);
			formula.addVariable("W", dims.get(1)*Constants.UNIT_MM_to_M);
			formula.addVariable("L", dims.get(2)*Constants.UNIT_MM_to_M);
			
			double volume = formula.eval();
			double density = Utilities.getDensity(material.getType()) * Constants.UNIT_KG_o_M3;

			double plateAreaMM2 = dims.get(1)*dims.get(2);
			
			double quantity = 0.0;
			
			if( material.getUnit().equals("EA"))
				quantity = material.getQuantity();
			else if( material.getUnit().equals("M2")) 
				quantity = Utilities.getNumberOfPlates(plateAreaMM2, material.getQuantity());
			
			slf4jLogger.debug("*PLATE*: " + String.valueOf(volume) + " " + String.valueOf(density) + " " + String.valueOf(quantity));
			
			return volume*density*quantity;
			
						
		} else if ( material.getCategory().equals("BAR")) {
			
			Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");
			
			if( Utilities.isHollowBar(material)) {
				
				ArrayList<Double> dims = Utilities.getHollowBarDimsMM(material);
				
				formula.addVariable("ID", dims.get(0)*Constants.UNIT_MM_to_M);
				formula.addVariable("OD", dims.get(1)*Constants.UNIT_MM_to_M);
				formula.addVariable("H" , material.getQuantity());
				
				double volume = formula.eval();
				double density = Utilities.getDensity(material.getType()) * Constants.UNIT_KG_o_M3;

				return volume*density;
								
			} else { 
				
				formula.addVariable("OD", Utilities.getBarODMM(material)*Constants.UNIT_MM_to_M);
				formula.addVariable("H" , material.getQuantity());
				
				double volume = formula.eval();
				double density = Utilities.getDensity(material.getType()) * Constants.UNIT_KG_o_M3;

				return volume*density;
			}
			
		} else if ( material.getCategory().equals("CHANNEL") || material.getCategory().equals("BEAM") ) {
			
			Formula formula = formulaFactory.get().getFormula("BEAMVOL");
			formula.setSelector("CHANNEL", true);
			
			ArrayList<Double> dims = null;
			
			try {
				dims = Utilities.getBeamDimsINCH(material);
			} catch ( NullPointerException ex) {
				dims = Utilities.getBeamDimsMM(material);
			}
			
			formula.addVariable("s", dims.get(0)*Constants.UNIT_MM_to_M);
			formula.addVariable("B", dims.get(1)*Constants.UNIT_MM_to_M);
			formula.addVariable("H", dims.get(2)*Constants.UNIT_MM_to_M);	
			formula.addVariable("L" , material.getQuantity());
			
			double volume = formula.eval();
			double density = Utilities.getDensity(material.getType()) * Constants.UNIT_KG_o_M3;

			return volume*density;
			
		} else if ( material.getCategory().equals("ANGLE")) {
			
			Formula formula = formulaFactory.get().getFormula("BEAMVOL");
			formula.setSelector("ANGLE", true);
			
			ArrayList<Double> dims = null;
			
			try {
				dims = Utilities.getBeamDimsINCH(material);
			} catch ( NullPointerException ex) {
				dims = Utilities.getBeamDimsMM(material);
			}
			
			formula.addVariable("s", dims.get(0)*Constants.UNIT_MM_to_M);
			formula.addVariable("B", dims.get(1)*Constants.UNIT_MM_to_M);
			formula.addVariable("C", dims.get(2)*Constants.UNIT_MM_to_M);	
			formula.addVariable("L" , material.getQuantity());
			
			double volume = formula.eval();
			double density = Utilities.getDensity(material.getType()) * Constants.UNIT_KG_o_M3;

			return volume*density;
						
		} else if (Arrays.asList(Constants.FITTINGS_LIST).contains(material.getCategory())) {
			
			if ( material.getCategory().equals("ELBOW")) {
				
				double weight = Utilities.getFittingWeight(material, Constants.FITTING_ELBOWS, 3);
				double quantity = material.getQuantity();
				return weight*quantity;
			
			}
			
		}
	
		return -1.0;

	}

}
