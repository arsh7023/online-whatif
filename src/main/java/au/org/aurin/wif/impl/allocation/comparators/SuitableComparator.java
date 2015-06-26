package au.org.aurin.wif.impl.allocation.comparators;

import java.util.Comparator;

import au.org.aurin.wif.impl.allocation.AllocationOrder;

/**
 * The Class SuitableComparator.
 */
public class SuitableComparator  implements Comparator<AllocationOrder> {

	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(AllocationOrder order1, AllocationOrder order2) {
		
		Double score1=(Double)order1.getFeature().getAttribute(order1.getSuitabilityLabel());
		Double score2=(Double)order2.getFeature().getAttribute(order2.getSuitabilityLabel());
		
		if(score1 > score2) return -1;
		else if(score1 < score2) return 1;
		else return 0;
	}
	
}
