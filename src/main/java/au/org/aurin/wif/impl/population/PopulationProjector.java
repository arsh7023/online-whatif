package au.org.aurin.wif.impl.population;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * <b>PopulationProjector.java</b> : includes logic for discovering land demand
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class PopulationProjector {

	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PopulationProjector.class);

	/**
	 * Adjust population vacancy.
	 * See Appendix F of What If manual for detailed functionality
	 * @param projected
	 *            the projected
	 * @param vacancyRate
	 *            the vacancy rate
	 * @return the double
	 */
	private static Double adjustPopulationVacancy(Double projected,
			Double vacancyRate) {
		return projected * (1 - vacancyRate);
	}

	/**
	 * Adjust infill.
	 * See Appendix F of What If manual for detailed functionality
	 * @param newHUnits
	 *            the demand
	 * @param infillRate
	 *            the infill rate
	 * @return the double
	 */
	public static Double adjustPopulationInfill(Double newHUnits,
			Double infillRate) {

		return newHUnits / (1 - infillRate);
	}

	/**
	 * Project residential population.
	 * See Appendix F of What If manual for detailed functionality
	 * @param unchangedLandUseArea
	 *            the unchanged land use area
	 * @param newLandUseArea
	 *            the new land use area
	 * @param averageHousehold
	 *            the average household
	 * @param currentResidentialDensity
	 *            the current residential density
	 * @param projectedResidentialDensity
	 *            the projected residential density
	 * @param vacancyRate
	 *            the vacancy rate
	 * @param infillRate
	 *            the infill rate
	 * @param averageHouseHoldSize
	 *            the average house hold size
	 * @return the double
	 */
	public static Double projectResidentialPopulation(
			Double unchangedLandUseArea, Double newLandUseArea,
			Double averageHousehold, Double currentResidentialDensity,
			Double projectedResidentialDensity, Double vacancyRate,
			Double infillRate, Double averageHouseHoldSize) {

		Double currentHUnits = unchangedLandUseArea * currentResidentialDensity;
		LOGGER.debug(" currentHUnits : {}", currentHUnits);

		Double projectedHUnits = newLandUseArea * projectedResidentialDensity;
		LOGGER.debug(" projectedHUnits : {}", projectedHUnits);

		Double newHUnits = currentHUnits + projectedHUnits;
		LOGGER.debug(" newHUnits : {}", newHUnits);

		Double infilledNnewHUnits = adjustPopulationInfill(newHUnits,
				infillRate);
		LOGGER.debug(" infilledNnewHUnits : {}", infilledNnewHUnits);

		Double newHouseHolds = adjustPopulationVacancy(infilledNnewHUnits,
				vacancyRate);
		LOGGER.debug(" newHouseHolds : {}", newHouseHolds);

		Double newPopulation = newHouseHolds * averageHouseHoldSize;
		LOGGER.debug(" newPopulation : {}", newPopulation);

		return newPopulation;
	}

	/**
	 * Project population group quarters.
	 * See Appendix F of What If manual for detailed functionality
	 * @param unchangedLandUseArea
	 *            the unchanged land use area
	 * @param newLandUseArea
	 *            the new land use area
	 * @param currentResidentialDensity
	 *            the current residential density
	 * @param projectedResidentialDensity
	 *            the projected residential density
	 * @return the double
	 */
	public static Double projectPopulationGroupQuarters(
			Double unchangedLandUseArea, Double newLandUseArea,
			Double currentResidentialDensity, Double projectedResidentialDensity) {

		Double currentHUnits = unchangedLandUseArea * currentResidentialDensity;
		LOGGER.debug(" current : {}", currentHUnits);

		Double projectedHUnits = newLandUseArea * projectedResidentialDensity;
		LOGGER.debug(" projected : {}", projectedHUnits);

		Double newGroupQuaters = currentHUnits + projectedHUnits;
		LOGGER.debug(" newGroupQuaters : {}", newGroupQuaters);

		return newGroupQuaters;
	}

}
