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
public class CylinderVolume implements Formula {

	/* (non-Javadoc)
	 * @see co.phystech.aosorio.services.Formula#getName()
	 */
	
	private Map<String, Double> variables = new HashMap<String, Double>();
	
	boolean hasInnerDiameter = false;
	
	@Override
	public String getName() {
		return "PIPE, TUBE, BAR volume calculation";
	}

	@Override
	public void addVariable(String name, double x) {
		variables.put(name, x);
		if( name.equalsIgnoreCase("ID"))
			hasInnerDiameter = true;
	}

	@Override
	public double eval() {
		
		double x1 = variables.get("OD");
		double x2 = variables.get("H");
		
		if (hasInnerDiameter) {
			
			double x3 = variables.get("ID");			
			double r1 = x1/2.0;
			double r2 = x3/2.0;	
			reset();
			return Math.PI * x2* ((r1*r1)-(r2*r2));
			
		} else {
			
			double r1 = x1/2.0;
			reset();
			return Math.PI * x2* (r1*r1);
		}
		
	}

	void reset() {
		variables.clear();
		hasInnerDiameter = false;
	}

	@Override
	public void setSelector(String name, boolean x1) {
		// TODO Auto-generated method stub
		
	}
	
}
