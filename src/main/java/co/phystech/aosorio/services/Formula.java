/**
 * 
 */
package co.phystech.aosorio.services;

/**
 * @author AOSORIO
 *
 */
public interface Formula {
	
	String getName();
	
	void addVariable(String name, double x1);
	
	void setSelector(String name, boolean x1);
	
	double eval();
	
	
}
