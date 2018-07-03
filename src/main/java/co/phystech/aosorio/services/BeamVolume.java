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
public class BeamVolume implements Formula {

	private Map<String, Double> variables = new HashMap<String, Double>();
	private Map<String, Boolean> selector = new HashMap<String, Boolean>();

	/**
	 * 
	 */
	public BeamVolume() {
		super();
		selector.put("CHANNEL", false);
		selector.put("ANGLE", false);
		selector.put("H-BEAM", false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.phystech.aosorio.services.Formula#getName()
	 */
	@Override
	public String getName() {

		return "BEAM C-CHANNEL BEAM, H-BEAM, I-BEAM";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.phystech.aosorio.services.Formula#addVariable(java.lang.String,
	 * double)
	 */
	@Override
	public void addVariable(String name, double x1) {
		variables.put(name, x1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.phystech.aosorio.services.Formula#eval()
	 */
	@Override
	public double eval() {
		
		double area = 0.0;
		double x0 = variables.get("L");
		
		if (selector.get("CHANNEL")) {
			
			double x1 = variables.get("H");
			double x2 = variables.get("B");
			double x3 = variables.get("s");
			
			reset();
			area = x3 * ((2.0 * x2) + (x1-x3-x3));
			
		} else if (selector.get("ANGLE")) {
			
			double x1 = variables.get("B");
			double x2 = variables.get("C");
			double x3 = variables.get("s");
			
			reset();
			area = x3*(x1+(x2-x3));
		}
		
		return area * x0;
		
	}

	void reset() {
		variables.clear();
	}

	@Override
	public void setSelector(String name, boolean x1) {
		selector.put(name, x1);
	}
}
