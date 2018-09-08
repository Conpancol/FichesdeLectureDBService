/**
 * 
 */
package co.phystech.aosorio.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AOSORIO
 *
 */
public class ProvidersPrefix {

	private static final Map<String, String> codePrefix;

	static {

		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("MATERIALS", "MP");
		aMap.put("TRANSPORTERS", "TP");
		codePrefix = Collections.unmodifiableMap(aMap);

	}

	/**
	 * @return the codeprefix
	 */
	public static String getCodeprefix(String key) {
		return codePrefix.get(key);
	}

}
