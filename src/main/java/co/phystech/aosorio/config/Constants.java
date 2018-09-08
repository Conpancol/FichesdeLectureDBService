/**
 * 
 */
package co.phystech.aosorio.config;

/**
 * @author AOSORIO
 *
 */
public class Constants {

	public static final String CONFIG_FILE = "config.properties";
	public static final String CONFIG_DENSITIES_FILE = "materials.json";	
	public static final String CONFIG_COUNTRIES_FILE = "countries.json";
	
	public static final int HTTP_OK = 200;
	public static final int HTTP_BAD_REQUEST = 400;
	
	public static final String LOGIN_SERVICE = "https://rugged-yosemite-61189.herokuapp.com/";
	
	public static final double UNIT_MM_to_M = 0.001;
	public static final double UNIT_KG_o_M3 = 1000.0;
	public static final double UNIT_INCH_to_MM = 25.4;
	public static final double UNIT_MM2_to_M2 = 0.000001;
	
	public static final String[] FITTINGS_LIST = {"ELBOW", "TEE", "REDUCER", "CAP"}; 	
	public static final String[] FITTING_ELBOWS = {"ELBOW,45,LR","ELBOW,90,LR","ELBOW,90,SR"};
	public static final String[] FITTING_REDUCERS = {"REDUCER,CONCENTRIC","REDUCER,ECCENTRIC"};
	public static final String[] FITTING_TEES = {"TEE,EQUAL","TEE,REDUCING"};
	public static final String[] FITTING_STUBENDS = {"STUB-END,SHORT LENGTH,SL"};
	public static final String[] FITTING_CAPS = {"CAP,END-CAP"};

	public static final Integer PROVIDERS_ID_OFFSET = 1000;
	
	
}
