/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sql2o.data.Table;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

/**
 * @author AOSORIO
 *
 */
public class GeneralSvc {
	
	public static String dataToJson(Object data) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			//mapper.enable(SerializationFeature.INDENT_OUTPUT);
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

	/** Setup a temporary area to store files
	 * @param target
	 * @return absolute path to temporary local directory
	 */
	public static String setupTmpDir( String target ) {
		
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
	
    public static List<Map<String, Object>> tableToList(Table t) {
    	
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < t.rows().size(); i++) {
            Map<String,Object> map = new HashMap<>();
            for (int j = 0; j < t.columns().size(); j++) {
                map.put(t.columns().get(j).getName(), t.rows().get(i).getObject(j));
            }
            mapList.add(map);
        }
        return mapList;
    }

}
