/**
 * 
 */
package co.phystech.aosorio.services;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AOSORIO
 *
 */
public class CubeVolume implements Formula {

	/* (non-Javadoc)
	 * @see co.phystech.aosorio.services.Formula#getName()
	 */
	
	private Map<String, Double> variables = new HashMap<String, Double>();
	
	@Override
	public String getName() {
		return "STRAIGHT BAR, PLATE, STRAIGHT BRICK volume calculation";
	}

	/* (non-Javadoc)
	 * @see co.phystech.aosorio.services.Formula#addVariable(java.lang.String, double)
	 */
	@Override
	public void addVariable(String name, double x1) {
		variables.put(name, x1);
	}

	/* (non-Javadoc)
	 * @see co.phystech.aosorio.services.Formula#eval()
	 */
	@Override
	public double eval() {
		
		double x1 = variables.get("W");
		double x2 = variables.get("H");
		double x3 = variables.get("L");
		
		reset();
		return x1*x2*x3;
	
	}
	
	void reset() {
		variables.clear();
	}

	@Override
	public void setSelector(String name, boolean x1) {
		// TODO Auto-generated method stub
		
	}

}
