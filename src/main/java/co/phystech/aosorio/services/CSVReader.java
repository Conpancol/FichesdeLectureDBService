/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Package: Class: CSVReader CSVReader.java Original Author: @author AOSORIO
 * 
 * Description: This is a simple CSV reader. Input must be in the form of
 * columns, each column separated by a , (comma).
 * 
 * Implementation: Using regex to split correctly the data. Data is return as a
 * Json Array
 * 
 * Created: Feb 29, 2016 Modified and adapted: 29/08/2017
 * 
 */

public class CSVReader {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(CSVReader.class);

	private String inputFile;

	BufferedReader buffer = null;

	String cvsSplitBy = "\t";
	int nrow = 0;
	int ncol = 0;
	boolean fileIsRead = false;

	/**
	 * Constructor - gets the input file string
	 * 
	 * @param infile
	 *            input file (in CSV format)
	 */
	public CSVReader(String infile) {
		inputFile = infile;
		buffer = null;
	}

	/**
	 * This is the core of the class: opens and reads the file data is stored
	 * and converted in a matrix
	 * 
	 * @throws FileNotFoundException
	 *             If file is not found or there is a problem reading it throws
	 *             exception
	 */
	public Object readFile() throws FileNotFoundException {

		JsonArray jsonArray = new JsonArray();
		
		try {

			buffer = new BufferedReader( new  InputStreamReader(new FileInputStream(inputFile),"UTF8"));
			String line;
					
			while ((line = buffer.readLine()) != null) {

				String[] data = line.split(cvsSplitBy, -1);

				ncol = data.length;
			
				JsonObject materialJson = new JsonObject();
				materialJson.addProperty("itemcode", data[1]);
				materialJson.addProperty("descrption", data[2]);
		
				jsonArray.add(materialJson);
				
				nrow++;
			}
			
			slf4jLogger.info(jsonArray.toString());

		} catch (FileNotFoundException e) {
			slf4jLogger.info("File not found, please check!");
			throw new FileNotFoundException();
		} catch (IOException e) {
			slf4jLogger.info("IO error, please check!");
		} finally {
			if (buffer != null) {
				try {
					if (nrow == 0)
						slf4jLogger.info("Current file is empty. Nothing to do.");
					else {
						// System.out.println("End of file reached, success.");
						fileIsRead = true;
					}
					buffer.close();
				} catch (IOException e) {
					slf4jLogger.info("IO error at closing file.");
				}
			}
		}

		return jsonArray;
	}

}
