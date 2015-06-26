package au.org.aurin.wif.impl.allocation.comparators;

import java.util.Comparator;

import au.org.aurin.wif.model.Projection;

/**
 * The Class YearComparator.
 */
public class YearComparator  implements Comparator<Projection> {

	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Projection order1, Projection order2) {
		
			if(order1.getYear() > order2.getYear() ) return 1;
		else if(order1.getYear()  < order2.getYear() ) return -1;
		else return 0;
	}
	
}
