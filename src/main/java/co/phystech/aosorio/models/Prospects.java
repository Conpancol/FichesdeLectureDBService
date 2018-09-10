/**
 * 
 */
package co.phystech.aosorio.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author AOSORIO
 *
 */
@Entity("prospects")
public class Prospects extends Contacts {
	
	@Id
	private String prospectId;

}
