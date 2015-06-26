package au.org.aurin.wif.impl.lsa.scoring;

import java.util.Collection;

import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * <b>LSAScoring.java</b> : includes logic for discovering land suitability
 * analysis
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */

public class LSAScoring {

  /**
   * logger.
   */

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LSAScoring.class);

  /**
   * Calculates suitability score and categories according to a list of
   * suitability factors in a scenario. In the wif Documentations says that: The
   * suitability scores are computed by multiplying the importance weight for
   * each suitability factor by its suitability ratings and summing the
   * products. These scores are used to determine the relative suitability of
   * different locations for each future land use. If a feature is not
   * developable (like Water), it will not be scored, likewise if the feature is
   * not convertable (e.g. is Residential already), or not suitable (on factor
   * rating was marked as 0, i.e. to exclude). products. TODO Algorithm is not
   * optimized and makes A LOT of loops, temporal while we get the logic right!
   * 
   * @param uazFeature
   *          the uaz feature
   * @param uazLandUseValueObj
   *          the uaz land use value obj
   * @param existingLandUses
   *          the existing land uses
   * @param suitabilityScn
   *          the suitability scn
   * @param suitabilityLU
   *          the suitability lu
   * @param project
   *          the project
   * @return ScoredSimpleFeature with the analysis
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * 
   */

  public static Double scoreSuitability(SimpleFeature uazFeature,
      Object uazLandUseValueObj, Collection<AllocationLU> existingLandUses,
      SuitabilityScenario suitabilityScn, SuitabilityLU suitabilityLU,
      WifProject project, Boolean isProductionLevel)
      throws WifInvalidConfigException, WifInvalidInputException {

    Double lsaScore = Double.valueOf(0);
    String uazLandUseValue = "";
    // FIXME we have casting issues,they are not relevant to suitability
    // scoring, and code is replicated?
    if (uazLandUseValueObj instanceof Double) {
      LOGGER.trace("transforming a double value");
      uazLandUseValue = uazLandUseValueObj.toString();
    }
    if (uazLandUseValueObj instanceof Integer) {
      LOGGER.trace("transforming an integer value ");
      uazLandUseValue = uazLandUseValueObj.toString();
    } else if (uazLandUseValueObj instanceof String) {
      LOGGER.trace("string value, no need to transform");
      uazLandUseValue = uazLandUseValueObj.toString();
    } else {
      // TODO checked before entering this function!
      // LOGGER.warn(" the type of the uaz feature for ALU is not recognized,  the analysis may not be correct!");
      LOGGER.trace("assuming the land use value is a string");
      uazLandUseValue = uazLandUseValueObj.toString();
    }
    if (isLandUseNotDevelopable(uazLandUseValue, existingLandUses)) {
      LOGGER.trace("feature is not developable, checking the next UAZ feature");
      lsaScore = project.getSuitabilityConfig().getNotDevelopableScore();
    } else {

      lsaScore = Double.valueOf(0);
      LOGGER.trace("scoring feature for suitabilityLU: {}",
          suitabilityLU.getLabel());
      SuitabilityRule rule = suitabilityScn
          .getLandUseConversionBySLUName(suitabilityLU.getLabel());

      LOGGER.trace("{} has {} conversions", suitabilityLU.getLabel(), rule
          .getConvertibleLUs().size());
      Collection<FactorImportance> factorsImp = rule.getFactorImportances();
      if (!isLandUseConvertable(uazLandUseValue, rule.getConvertibleLUs())) {
        LOGGER.trace("UAZ value of {} is not convertable to {}",
            uazLandUseValue, suitabilityLU.getLabel());
        lsaScore = project.getSuitabilityConfig().getNotConvertableScore();

      } else if (isLandUseNotSuitable(uazFeature, factorsImp)) {
        lsaScore = project.getSuitabilityConfig().getNotSuitableScore();
      } else {
        for (FactorImportance factorImportance : factorsImp) {
          Factor aFactor = factorImportance.getFactor();
          String factorName = aFactor.getFeatureFieldName();
          String factorLabel = aFactor.getLabel();
          Double factorWeight = factorImportance.getImportance();
          LOGGER.trace(" Factor label: {}", factorLabel);
          LOGGER.trace(" Factor UAZ column attribute name: {}", factorName);
          LOGGER.trace("- Factor importance: {}", factorWeight);
          // LOGGER.trace("factor id: {}", factorImportance.getId());

          Object uazFactorValueObj = uazFeature.getAttribute(factorName);
          String uazFactorValue = getStringValue(uazFactorValueObj);

          LOGGER.trace("-= uazFactorValue = {}", uazFactorValue);

          for (FactorTypeRating aFactorRating : factorImportance
              .getFactorTypeRatings()) {

            Object ratingValue = aFactorRating.getFactorType().getValue();
            Double ratingScore = aFactorRating.getScore();
            String ratingLabel = aFactorRating.getFactorType().getLabel();
            LOGGER.trace("--> for factor type label: {}", ratingLabel);
            LOGGER.trace("--> rating UAZ value: {}", ratingValue);

            if (ratingValue.equals(uazFactorValue)) {
              LOGGER.trace("--> the rating is: {}", ratingScore);
              lsaScore += (ratingScore * factorWeight);
              LOGGER.trace("....... current lsaScore is = {}", lsaScore);
            } else {
              LOGGER
                  .trace(
                      "##= the {} rating UAZ value doesn't match the uazFactorValue of {}, checking more...",
                      ratingValue, uazFactorValue);
            }
          }
        }
      }
    }

    if (isProductionLevel) {
      LOGGER.info("********* Final lsa score for {} is: {} ",
          suitabilityLU.getLabel(), lsaScore);
    }
    return lsaScore;
  }

  /**
   * Determines if the land is not suitable, i.e. one factor has a rating of 0
   * TODO IT HAS TO BE DONE HERE BECAUSE OTHERWISE WE WOULD break the for loop
   * in the factors but the algorithm could be better
   * 
   * @param uazFeature
   *          the uaz feature
   * @param factorsImp
   *          the factors imp
   * @return true, if is land use not suitable
   */
  static boolean isLandUseNotSuitable(SimpleFeature uazFeature,
      Collection<FactorImportance> factorsImp) {

    for (FactorImportance fi : factorsImp) {

      Factor aSuitabilityFactor = fi.getFactor();
      String factorName = aSuitabilityFactor.getFeatureFieldName();
      String factorLabel = aSuitabilityFactor.getLabel();
      // TODO Casting issues, USE instanceof!
      Object uazFactorValueObj = uazFeature.getAttribute(factorName);
      String uazFactorValue = getStringValue(uazFactorValueObj);
      for (FactorTypeRating aFactorRating : fi.getFactorTypeRatings()) {
        Object ratingValue = aFactorRating.getFactorType().getValue();
        Double ratingScore = aFactorRating.getScore();
        if (ratingValue.equals(uazFactorValue) && (ratingScore == 0d)
            && (fi.getImportance() != 0)) {
          LOGGER
              .trace(
                  "....{} -> {} is excluded from this suitability land use! (rating is 0) ",
                  factorLabel, aFactorRating.getFactorType().getLabel());
          return true;
        }
      }
    }
    return false;
  }

  /**
   * TODO this dirty, method is to solve casting issuesGets the string value.
   * subject to be better
   * 
   * @param uazFactorValueObj
   *          the uaz factor value obj
   * @return the string value
   */
  static String getStringValue(Object uazFactorValueObj) {
    String uazFactorValue;
    if (uazFactorValueObj instanceof Double) {
      LOGGER.trace("transforming a double value");
      uazFactorValue = uazFactorValueObj.toString();
    }
    if (uazFactorValueObj instanceof Integer) {
      LOGGER.trace("transforming an integer value ");
      uazFactorValue = uazFactorValueObj.toString();
    } else if (uazFactorValueObj instanceof String) {
      LOGGER.trace("string value, no need to transform");
      uazFactorValue = uazFactorValueObj.toString();
    } else {
      // TODO checked before entering this function!
      // LOGGER.warn(" the type of the uaz feature for ALU is not recognized,  the analysis may not be correct!");
      LOGGER.trace("assuming the factor value is a string");
      uazFactorValue = uazFactorValueObj.toString();
    }
    return uazFactorValue;
  }

  /**
   * Determines if the land is convertable, i.e. for Undeveloped land use can be
   * made Residential
   * 
   * @param uazLandUseValue
   *          the uaz land use value
   * @param conversions
   *          .
   * @return true, if is land use convertable
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  static boolean isLandUseConvertable(Object uazLandUseValue,
      Collection<AllocationLU> conversions) throws WifInvalidConfigException {
    if (conversions.size() == 0) {
      throw new WifInvalidConfigException(
          "no convertible ALUs for this land use, pointless Suitability Analysis!?");
    }

    for (AllocationLU aLU : conversions) {
      LOGGER.trace(
          "checking convertability into this SLU of ALU {} / UAZ value {}",
          aLU.getLabel(), aLU.getLabel());
      if (aLU.getFeatureFieldName().equals(uazLandUseValue)) {
        return true;
      }
    }
    return false;

  }

  /**
   * Determines if the land is not developable, i.e. "Water" or "Right of Way"
   * 
   * @param uazLandUseValue
   *          the uaz land use value
   * @param existingLUs
   *          the existing l us
   * @return true, if is land use not developable
   */
  static boolean isLandUseNotDevelopable(Object uazLandUseValue,
      Collection<AllocationLU> existingLUs) {
    for (AllocationLU aCurrLU : existingLUs) {
      if (uazLandUseValue.equals(aCurrLU.getFeatureFieldName())) {
        LOGGER
            .trace(
                " land use UAZ value {}, associated to ALU label: {}, is developable! ",
                uazLandUseValue, aCurrLU.getLabel());
        return aCurrLU.isNotDevelopable();
      }
    }
    LOGGER
        .warn(
            "could not find land use:{} in existing land uses to check if is not developable, assuming is not!",
            uazLandUseValue);
    return true;
  }

}
