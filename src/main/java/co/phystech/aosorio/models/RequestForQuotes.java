/**
 * 
 */
package co.phystech.aosorio.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author AOSORIO
 *
 */
@Entity("rfquotes")
public class RequestForQuotes {
	
	@Id
    private ObjectId id;
	
	private int internalCode;
	
	private int externalCode;
	
	private Date receivedDate;
	
	private Date processedDate;
	
	private Date sentDate;
	
	private String user;
	
	private String sender;
	
	private String company;
	
	@Embedded
	private List<ExtMaterials> materialList = new ArrayList<ExtMaterials>();;

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}

	/**
	 * @return the internalCode
	 */
	public int getInternalCode() {
		return internalCode;
	}

	/**
	 * @param internalCode the internalCode to set
	 */
	public void setInternalCode(int internalCode) {
		this.internalCode = internalCode;
	}

	/**
	 * @return the externalCode
	 */
	public int getExternalCode() {
		return externalCode;
	}

	/**
	 * @param externalCode the externalCode to set
	 */
	public void setExternalCode(int externalCode) {
		this.externalCode = externalCode;
	}

	/**
	 * @return the receivedDate
	 */
	public Date getReceivedDate() {
		return receivedDate;
	}

	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	/**
	 * @return the processedDate
	 */
	public Date getProcessedDate() {
		return processedDate;
	}

	/**
	 * @param processedDate the processedDate to set
	 */
	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

	/**
	 * @return the sentDate
	 */
	public Date getSentDate() {
		return sentDate;
	}

	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the materialList
	 */
	public List<ExtMaterials> getMaterialList() {
		return materialList;
	}

	/**
	 * @param materialList the materialList to set
	 */
	public void setMaterialList(List<ExtMaterials> materialList) {
		this.materialList = materialList;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	
	

}
