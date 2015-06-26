package au.org.aurin.wif.impl.allocation.comparators;

import java.util.Comparator;

import au.org.aurin.wif.model.suitability.FactorTypeRating;

/**
 * The Class FactorTypeComparator.
 */
public class FactorTypeComparator  implements Comparator<FactorTypeRating> {

	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(FactorTypeRating order1, FactorTypeRating order2) {
		
			if(order1.getFactorType().getNaturalOrder() > order2.getFactorType().getNaturalOrder() ) return 1;
		else if(order1.getFactorType().getNaturalOrder() < order2.getFactorType().getNaturalOrder() ) return -1;
		else return 0;
	}
	
}
