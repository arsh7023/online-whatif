package au.org.aurin.wif.impl.allocation;

import org.opengis.feature.simple.SimpleFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class AllocationOrder. Utility class to handle the allocation order by suitability priorities
 */
public class AllocationOrder   {

	/** The feature. */
	private SimpleFeature feature;
	
	/** The suitability label. */
	private String suitabilityLabel;
	
	/** The area label. */
	private String areaLabel;
	
	/**
	 * Instantiates a new allocation order.
	 *
	 * @param suitabilityLabel the suitability label
	 * @param areaLabel the area label
	 * @param feature the feature
	 */
	public AllocationOrder(String suitabilityLabel,String areaLabel,SimpleFeature feature){
		this.suitabilityLabel=suitabilityLabel;
		this.areaLabel=areaLabel;
		this.feature=feature;
	}

	/**
	 * Sets the feature.
	 *
	 * @param feature the new feature
	 */
	public void setFeature(SimpleFeature feature) {
		this.feature = feature;
	}


	/**
	 * Gets the feature.
	 *
	 * @return the feature
	 */
	public SimpleFeature getFeature() {
		return feature;
	}


	/**
	 * Sets the suitability label.
	 *
	 * @param label the new suitability label
	 */
	public void setSuitabilityLabel(String label) {
		this.suitabilityLabel = label;
	}


	/**
	 * Gets the suitability label.
	 *
	 * @return the suitability label
	 */
	public String getSuitabilityLabel() {
		return suitabilityLabel;
	}

	/**
	 * Sets the area label.
	 *
	 * @param areaLabel the new area label
	 */
	public void setAreaLabel(String areaLabel) {
		this.areaLabel = areaLabel;
	}

	/**
	 * Gets the area label.
	 *
	 * @return the area label
	 */
	public String getAreaLabel() {
		return areaLabel;
	}
	
}
