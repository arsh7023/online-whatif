package au.org.aurin.wif.model.allocation;

import au.org.aurin.wif.impl.population.PopulationProjector;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;


/**
 * The Class Forecast.
 */
public class Forecast {
	
	/** The id. @uml.property name="id" */
	private Integer id;
	
	/** The associated lu. */
	private AllocationLU forecastLU;
	
	/** The projection. */
	private Projection projection;

	/** The projected demographics. */
	private ResidentialDemographicData projectedDemographics;

	/** The source demographics. */
	private ResidentialDemographicData sourceDemographics;
	
	/** The demand scenario. */
	private DemandScenario demandScenario;

	/** The new area. */
	private Double newArea;

	/** The unchanged area. */
	private Double unchangedArea;
	
	/** The project population. */
	private Double projectPopulation;
	
	/**
	 * Sets the projection.
	 * 
	 * @param projection
	 *            the projection to set
	 */
	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	/**
	 * Gets the projection.
	 * 
	 * @return the projection
	 */
	public Projection getProjection() {
		return projection;
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
	 * Sets the new area.
	 * 
	 * @param newArea
	 *            the newArea to set
	 */
	public void setNewArea(Double newArea) {
		this.newArea = newArea;
	}

	/**
	 * Gets the unchanged area.
	 * 
	 * @return the unchangedArea
	 */
	public Double getUnchangedArea() {
		return unchangedArea;
	}

	/**
	 * Sets the unchanged area.
	 * 
	 * @param unchangedArea
	 *            the unchangedArea to set
	 */
	public void setUnchangedArea(Double unchangedArea) {
		this.unchangedArea = unchangedArea;
	}

	/**
	 * Gets the source demographics.
	 * 
	 * @return the sourceDemographics
	 */
	public ResidentialDemographicData getSourceDemographics() {
		return sourceDemographics;
	}

	/**
	 * Sets the source demographics.
	 * 
	 * @param demographicData
	 *            the new source demographics
	 */
	public void setSourceDemographics(ResidentialDemographicData demographicData) {
		this.sourceDemographics = demographicData;
	}

	/**
	 * Sets the projected demographics.
	 * 
	 * @param projectedDemographics
	 *            the projectedDemographics to set
	 */
	public void setProjectedDemographics(ResidentialDemographicData projectedDemographics) {
		this.projectedDemographics = projectedDemographics;
	}

	/**
	 * Gets the projected demographics.
	 * 
	 * @return the projectedDemographics
	 */
	public ResidentialDemographicData getProjectedDemographics() {
		return projectedDemographics;
	}
	

	/**
	 * Sets the project population.
	 *
	 * @param projectPopulation the projectPopulation to set
	 */
	public void setProjectPopulation(Double projectPopulation) {
		this.projectPopulation = projectPopulation;
	}

	/**
	 * Gets the project population.
	 *
	 * @return the projectPopulation
	 */
	public Double getProjectPopulation() {
		return projectPopulation;
	}
	

	/**
	 * Do projection.
	 *
	 * @param unchangedLandUseArea the unchanged land use area
	 * @param newLandUseArea the new land use area
	 * @param residentialDemandInfo the residential demand info
	 * @param currentDemographic the current demographic
	 */
	public void doProjection(Double unchangedLandUseArea,
			Double newLandUseArea, ResidentialDemandInfo residentialDemandInfo,
			ResidentialDemographicData currentDemographic) {
		this.setSourceDemographics(currentDemographic);
		this.setUnchangedArea(unchangedLandUseArea);
		this.setNewArea(newLandUseArea);
//		this.setDemandInfo(residentialDemandInfo);
		Double projectResidentialPopulation = PopulationProjector
				.projectResidentialPopulation(unchangedLandUseArea,
						newLandUseArea, currentDemographic.getHouseholds(),
						residentialDemandInfo.getCurrentDensity(),
						residentialDemandInfo.getFutureDensity(),
						residentialDemandInfo.getFutureVacancyRate(),
						residentialDemandInfo.getInfillRate(),
						currentDemographic.getAverageHouseholdSize());
		this.setProjectPopulation(projectResidentialPopulation);
	}

	/**
	 * Sets the demand scenario.
	 *
	 * @param demandScenario the demandScenario to set
	 */
	public void setDemandScenario(DemandScenario demandScenario) {
		this.demandScenario = demandScenario;
	}

	/**
	 * Gets the demand scenario.
	 *
	 * @return the demandScenario
	 */
	public DemandScenario getDemandScenario() {
		return demandScenario;
	}



}
