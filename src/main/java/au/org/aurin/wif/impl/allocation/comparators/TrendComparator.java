package au.org.aurin.wif.impl.allocation.comparators;

import java.util.Comparator;

import au.org.aurin.wif.model.demand.PastTrendInfo;

/**
 * The Class TrendComparator.
 */
public class TrendComparator  implements Comparator<PastTrendInfo> {

	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(PastTrendInfo order1, PastTrendInfo order2) {		
			if(order1.getYear() > order2.getYear() ) return 1;
		else if(order1.getYear()  < order2.getYear() ) return -1;
		else return 0;
	}
	
}
