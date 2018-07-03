/**
 * 
 */
package co.phystech.aosorio.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author AOSORIO
 *
 */
public class FormulaFactory {
	
	final static Map<String, Supplier<Formula>> map = new HashMap<>();
	
	static {
	    map.put("CYLINDERVOL", CylinderVolume::new);
	    map.put("CUBEVOL", CubeVolume::new);
	    map.put("BEAMVOL", BeamVolume::new);
	  } 
	
	  public Formula getFormula(String formulaType){
	     Supplier<Formula> formula = map.get(formulaType.toUpperCase());
	     if(formula != null) {
	       return formula.get();
	     }
	     throw new IllegalArgumentException("No such shape " + formulaType.toUpperCase());
	  }
	

}
