package au.org.aurin.wif.model.demand.data;


/**
 * The Class ResidentialData.
 */
public class ResidentialData extends ProjectedData {

	 
		 /**
 		 * Instantiates a new residential data.
 		 */
  public ResidentialData() {
    super();
  }

    /**
     * Instantiates a new residential data.
     *
     * @param copy the copy
     */
    public ResidentialData(ResidentialData copy) {
    super(copy);
    this.unchangedArea = copy.getUnchangedArea();
    this.newArea = copy.getNewArea();
  }

    /** The unchanged area. */
 		private Double unchangedArea;

	/** The new area. */
	private Double newArea;

	/**
	 * Sets the new area.
	 *
	 * @param newArea the newArea to set
	 */
	public void setNewArea(Double newArea) {
		this.newArea = newArea;
	}

	/**
	 * Gets the new area.
	 *
	 * @return the newArea
	 */
	public Double getNewArea() {
		return newArea;
	}

	/**
	 * Sets the unchanged area.
	 *
	 * @param unchangedArea the unchangedArea to set
	 */
	public void setUnchangedArea(Double unchangedArea) {
		this.unchangedArea = unchangedArea;
	}

	/**
	 * Gets the unchanged area.
	 *
	 * @return the unchangedArea
	 */
	public Double getUnchangedArea() {
		return unchangedArea;
	}
}
