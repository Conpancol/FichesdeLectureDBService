/**
 * 
 */
package co.phystech.aosorio.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.NoSqlController;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.QuotedMaterials;

/**
 * @author AOSORIO
 *
 */
public class QuotesAnalysisSvc {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(QuotesAnalysisSvc.class);

	private static Datastore datastore;
	
	public static double getMaterialPriceAvg( String providerId, QuotedMaterials quoted ) {
		
		datastore = NoSqlController.getInstance().getDatabase();
		
		String itemcode = quoted.getItemcode();
		
		Query<ExtQuotedMaterials> query = datastore.createQuery(ExtQuotedMaterials.class);
		List<ExtQuotedMaterials> result = query.field("itemcode").equal(itemcode).asList();
		
		Iterator<ExtQuotedMaterials> itr = result.iterator();
		
		ArrayList<Double> prices = new ArrayList<Double>();
		
		while( itr.hasNext() ) {
			
			ExtQuotedMaterials material = itr.next();
			
			if ( material.getProviderId().equals(providerId))
				continue;
			
			double totalUSD = material.getTotalPrice();
			double quantity = material.getQuantity();
			double ratio = totalUSD / quantity;
			
			prices.add(ratio);
			
		}
		
		double average = Utilities.calculateAverage(prices);
		
		slf4jLogger.info("Average: " + String.valueOf(average));
		
		return average;
		
	}
	
	
}
