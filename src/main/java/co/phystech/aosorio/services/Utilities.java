/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.controllers.PipeSchedulesController;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.PipeSchedules;

/**
 * @author AOSORIO
 *
 */

public class Utilities {

	public class Densities {
		String type;
		double density;
	}

	private final static Logger slf4jLogger = LoggerFactory.getLogger(Utilities.class);

	/**
	 * @param material:
	 * @return get from the SCHEDULES table the Outer diameter
	 */
	public static double getODMM(Materials material) {

		double outerDiam = 0.0;

		String schCode = getPipeSchedule(material);
		String schDiameter = getPipeOuterDiameter(material);

		PipeSchedules schedule = PipeSchedulesController.read(schCode, schDiameter);

		if (schedule != null)
			outerDiam = schedule.getOdMM();

		return outerDiam;

	}

	/**
	 * @param material
	 * @return gets from the SCHEDULES table the inner diameter of pipe/tube
	 */
	public static double getIDMM(Materials material) {

		double innerDiam = 0.0;

		String schCode = getPipeSchedule(material);
		String schDiameter = getPipeOuterDiameter(material);

		PipeSchedules schedule = PipeSchedulesController.read(schCode, schDiameter);

		if (schedule != null)
			innerDiam = schedule.getIdMM();

		return innerDiam;

	}

	/**
	 * @param material
	 * @return for a specific materials, searches and returns the PIPE SCHEDULE
	 */
	public static String getPipeSchedule(Materials material) {

		String dimensions = material.getDimensions();

		List<String> dims = Arrays.asList(dimensions.split(","));
		Iterator<String> itrStr = dims.iterator();

		String schedule = null;

		while (itrStr.hasNext()) {

			String element = itrStr.next();
			if (searchPattern("SCH", element))
				schedule = element.replaceAll("\\s", "");

		}

		if (schedule != null && schedule.equals("SCH40S"))
			schedule = "SCHStd";

		return schedule;

	}

	public static String getPipeOuterDiameter(Materials material) {

		String dimensions = material.getDimensions();

		List<String> dims = Arrays.asList(dimensions.split(","));
		Iterator<String> itrStr = dims.iterator();

		String diameter = null;

		while (itrStr.hasNext()) {

			String element = itrStr.next();
			if (searchPattern("\"", element))
				diameter = element.replaceAll("\\s", "");

		}

		return diameter;

	}

	/**
	 * @param material
	 * @return
	 */
	public static double getBarODMM(Materials material) {

		double outerDiam = 0.0;

		String diameter = getPipeOuterDiameter(material);
		outerDiam = parseDimension(diameter);

		return outerDiam;

	}

	public static ArrayList<Double> getPlateDimsMM(Materials material) {

		String dimensions = material.getDimensions();

		ArrayList<String> dimensionsMM = new ArrayList<String>();

		List<String> dimList = Arrays.asList(dimensions.split(","));
		Iterator<String> itrDesc = dimList.iterator();

		String descriptionMM = null;

		while (itrDesc.hasNext()) {
			String description = itrDesc.next();
			if (description.contains("MM"))
				descriptionMM = description;
		}

		dimensionsMM.addAll(Arrays.asList(descriptionMM.split("X")));

		return parseMultipleDimensions(dimensionsMM);

	}

	public static ArrayList<Double> getHollowBarDimsMM(Materials material) {

		String dimensions = material.getDimensions();

		ArrayList<String> dims = new ArrayList<String>();
		List<String> tokens = Arrays.asList(dimensions.split("X"));
		dims.addAll(tokens);

		if (dims.size() == 3)
			dims.remove(2);

		return parseMultipleDimensions(dims);

	}

	public static ArrayList<Double> getBeamDimsINCH(Materials material) {

		String dimensions = material.getDimensions();

		ArrayList<String> dimensionsINCH = new ArrayList<String>();

		List<String> dimList = Arrays.asList(dimensions.split(","));
		Iterator<String> itrDesc = dimList.iterator();

		String descriptionINCH = null;

		while (itrDesc.hasNext()) {
			String description = itrDesc.next();
			if (description.contains("\""))
				descriptionINCH = description;
		}

		dimensionsINCH.addAll(Arrays.asList(descriptionINCH.split("X")));

		return parseMultipleDimensions(dimensionsINCH);

	}

	public static ArrayList<Double> getBeamDimsMM(Materials material) {

		String dimensions = material.getDimensions();

		ArrayList<String> dimensionsMM = new ArrayList<String>();

		List<String> dimList = Arrays.asList(dimensions.split(","));
		Iterator<String> itrDesc = dimList.iterator();

		String descriptionMM = null;

		while (itrDesc.hasNext()) {
			String description = itrDesc.next();
			if (description.contains("MM"))
				descriptionMM = description;
		}

		dimensionsMM.addAll(Arrays.asList(descriptionMM.split("X")));

		return parseMultipleDimensions(dimensionsMM);

	}
	
	public static double getNumberOfPlates(double plateAreaMM2, double quantityM2) {

		double plateAreaM2 = plateAreaMM2 * Constants.UNIT_MM2_to_M2;
		double ratio = quantityM2 / plateAreaM2;

		return Math.round(ratio);
	}

	public static ArrayList<Double> parseMultipleDimensions(ArrayList<String> input) {

		ArrayList<Double> result = new ArrayList<Double>();

		Iterator<String> itrStr = input.iterator();

		while (itrStr.hasNext()) {

			String token = itrStr.next().replaceAll("\\s", "").replaceAll("OD", "").replaceAll("ID", "");
			if (token.contains("MM")) {
				double xx = Double.parseDouble(token.replace("MM", ""));
				result.add(xx);
				slf4jLogger.debug(String.valueOf(xx));
			} else {
				double xx = parseDimension(token);
				result.add(xx);
				slf4jLogger.debug(String.valueOf(xx));
			}

		}

		Collections.sort(result);
		return result;

	}

	/**
	 * @param dimension:
	 *            as in the DB
	 * @return parses the given size in fraction of INCH and returns the value
	 *         in MM
	 */
	public static double parseDimension(String dimension) {

		double result = 0.0;

		dimension = dimension.replaceAll("\"", "");

		FractionFormat ff = new FractionFormat();

		try {
			
			Fraction fraction = ff.parse(dimension);
			result = fraction.doubleValue() * Constants.UNIT_INCH_to_MM;
		
		} catch (MathParseException ex) {

			List<String> dims = Arrays.asList(dimension.split("-"));
			Fraction value_one = ff.parse(dims.get(0));
			Fraction value_two = ff.parse(dims.get(1));
			
			result += value_one.doubleValue() * Constants.UNIT_INCH_to_MM;
			result += value_two.doubleValue() * Constants.UNIT_INCH_to_MM;
			
		}

		return result;

	}

	/**
	 * @param material
	 * @return checks if in the description there is the word HOLLOW
	 */
	public static boolean isHollowBar(Materials material) {

		if (material.getCategory().equals("BAR") && searchPattern("HOLLOW", material.getDescription())) {
			return true;
		}

		return false;
	}

	/**
	 * Generid method that uses REGEX to match a pattern in a string
	 * 
	 * @param str
	 * @param stringToSearch
	 * @return
	 */
	public static boolean searchPattern(String str, String stringToSearch) {

		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(stringToSearch);

		// now try to find at least one match
		if (m.find())
			return true;
		else
			return false;

	}

	/**
	 * generic method that searches for a specific type of material densities in
	 * a json file
	 * 
	 * @param str
	 * @return
	 */
	public static double getDensity(String str) {

		try {

			JsonReader jsonReader = new JsonReader(new FileReader("src/main/resources/materials.json"));
			jsonReader.beginArray();
			Gson gson = new Gson();

			while (jsonReader.hasNext()) {
				Densities item = gson.fromJson(jsonReader, Densities.class);
				if (item.type.equals(str))
					return item.density;
			}

			jsonReader.endArray();
			jsonReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0.0;
	}

}
