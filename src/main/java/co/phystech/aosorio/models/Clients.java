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
@Entity("clients")
public class Clients extends Contacts {
	
	@Id
	private String clientId;

}
