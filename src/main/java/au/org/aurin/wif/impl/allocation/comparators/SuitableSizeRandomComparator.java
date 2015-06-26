package au.org.aurin.wif.impl.allocation.comparators;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aurin.wif.impl.allocation.AllocationOrder;

// 
/**
 * The Class SuitableSizeRandomComparator. Spatial order Specify the order in
 * which different factors are considered in allocating the projected land use
 * demands to different locations. There are currently the following options 
 * • Suitability: allocates the projected land use demands to different locations
 * on the basis of their suitability for accommodating each land use (The better
 * the suitability the score, the more likely to be allocated).
 * 
 *  • Size: allocates the projected demand to UAZs on the basis of their size, starting
 * with the largest suitable UAZ and proceeding to the smallest one. 
 * • Random: This option allocated the projected demand to different locations in a
 * pseudo random pattern
 */
public class SuitableSizeRandomComparator implements Comparator<AllocationOrder> {


	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SuitableSizeRandomComparator.class);/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(AllocationOrder order1, AllocationOrder order2) {

		Double score1 = (Double) order1.getFeature().getAttribute(
				order1.getSuitabilityLabel());
		Double score2 = (Double) order2.getFeature().getAttribute(
				order2.getSuitabilityLabel());
//
//		TODO research why  the priority queue is also ordering  when poll()?
//		LOGGER.debug("comparing between score1= {} and score2 =  {}",
//				score1,score2);
		Double area1 = (Double) order1.getFeature().getAttribute(
				order1.getAreaLabel());
		Double area2 = (Double) order2.getFeature().getAttribute(
				order2.getAreaLabel());

		if (score1 > score2)
			return -1;
		else if (score1 < score2)
			return 1;
		else {
//			LOGGER.debug("suitability scores are equal, now comparing between area1= {} and area2 =  {}",
//					area1,area2);
			if (area1 > area2)
				return -1;
			else if (area1 < area2)
				return 1;
			else {

//				LOGGER.debug("area scores are equal, now getting a random  order  ");
				double random = Math.random();
				if (random > 0.66)
					return -1;
				else if (random > 0.33)
					return 1;
				else
					return 0;
			}
		}
	}

}
