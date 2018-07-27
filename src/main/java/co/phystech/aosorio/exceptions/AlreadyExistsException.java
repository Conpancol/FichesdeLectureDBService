/**
 * 
 */
package co.phystech.aosorio.exceptions;

/**
 * @author AOSORIO
 *
 */
public class AlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String errorMessage = "Document already exists in DB";
	
	public AlreadyExistsException() {
		super(errorMessage);
	}
	
	public AlreadyExistsException(String message) {
		super(message);
	}
	

}
