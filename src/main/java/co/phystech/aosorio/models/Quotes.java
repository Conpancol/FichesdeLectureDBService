package co.phystech.aosorio.models;

import java.sql.Date;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;

public class Quotes {
	
	@Id
    private ObjectId id;
	
	private int internalCode;
	
	private int externalCode;
	
	private String providerCode;
	
	private Date receivedDate;
	
	private Date processedDate;
	
	private Date sentDate;
	
	private String user;
	
	private String providerName;
	
	@Embedded
	private ArrayList<QuotedMaterials> materialList;

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
	 * @return the providerCode
	 */
	public String getProviderCode() {
		return providerCode;
	}

	/**
	 * @param providerCode the providerCode to set
	 */
	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
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
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	/**
	 * @return the materialList
	 */
	public ArrayList<QuotedMaterials> getMaterialList() {
		return materialList;
	}

	/**
	 * @param materialList the materialList to set
	 */
	public void setMaterialList(ArrayList<QuotedMaterials> materialList) {
		this.materialList = materialList;
	}
	
	

}
