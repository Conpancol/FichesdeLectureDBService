/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

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

	public static double getODMM(Materials material) {

		double outerDiam = 0.0;

		String schCode = getPipeSchedule(material);
		String schDiameter = getPipeOuterDiameter(material);

		PipeSchedules schedule = PipeSchedulesController.read(schCode, schDiameter);

		if (schedule != null)
			outerDiam = schedule.getOdMM();

		return outerDiam;

	}

	public static double getIDMM(Materials material) {

		double innerDiam = 0.0;

		String schCode = getPipeSchedule(material);
		String schDiameter = getPipeOuterDiameter(material);

		PipeSchedules schedule = PipeSchedulesController.read(schCode, schDiameter);

		if (schedule != null)
			innerDiam = schedule.getIdMM();

		return innerDiam;

	}

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

	public static boolean searchPattern(String str, String stringToSearch) {

		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(stringToSearch);

		// now try to find at least one match
		if (m.find())
			return true;
		else
			return false;

	}

	public static double getDensity(String str) {

		try {

			JsonReader jsonReader = new JsonReader(new FileReader("src/main/resources/materials.json"));
			jsonReader.beginArray();
			Gson gson = new Gson();

			while (jsonReader.hasNext()) {
				Densities item = gson.fromJson(jsonReader, Densities.class);
				if( item.type.equals(str))
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
