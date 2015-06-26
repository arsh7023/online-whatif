package au.org.aurin.wif.impl.allocation.comparators;

import java.util.Comparator;

import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Class SLUOrderComparator.
 */
public class SLUOrderComparator implements Comparator<AllocationLU> {

  /*
   * (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(AllocationLU order1, AllocationLU order2) {

    Integer score1 = order1.getPriority();
    Integer score2 = order2.getPriority();

    if (score1 > score2) {
      return 1;
    } else if (score1 < score2) {
      return -1;
    } else {
      return 0;
    }
  }

}
