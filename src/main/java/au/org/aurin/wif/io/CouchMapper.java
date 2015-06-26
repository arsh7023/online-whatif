package au.org.aurin.wif.io;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Model2JsonMapped;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.svc.ProjectService;

/**
 * The Class CouchMapper.
 */
@Component
public class CouchMapper {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchMapper.class);

  /**
   * Map project.
   * 
   * @param project
   *          the wif project
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public WifProject map(WifProject project) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.debug("mapping the following project for CouchDB: "
        + project.getLabel());
    Collection<SuitabilityLU> slus = project.getSuitabilityLUs();
    LOGGER.debug("{} has {} slus included", project.getLabel(), slus.size());
    for (SuitabilityLU suitabilityLU : slus) {
      suitabilityLU = mapSuitabilityLU(suitabilityLU);
    }
    Collection<Factor> factors = project.getFactors();
    LOGGER.trace("{} has {} factors included", project.getLabel(),
        factors.size());
    return project;
  }

  /**
   * Map factor types.
   * 
   * @param factorTypes
   *          the factor types
   * @return the map
   */
  public Map<String, String> mapFactorTypes(Set<FactorType> factorTypes) {
    Map<String, String> idLabelMap = new HashMap<String, String>();
    if (factorTypes != null) {
      LOGGER.trace("{} factorTypes , mapping them...", factorTypes.size());
      for (FactorType factorType : factorTypes) {
        idLabelMap.put(factorType.getId(), factorType.getLabel());
      }
    }
    return idLabelMap;
  }

  public Map<String, String> mapIdLabelDoc(Set<Model2JsonMapped> docs) {
    Map<String, String> idLabelMap = new HashMap<String, String>();
    if (docs != null) {
      LOGGER.trace("{} factorTypes , mapping them...", docs.size());
      for (Model2JsonMapped doc : docs) {
        idLabelMap.put(doc.getId(), doc.getLabel());
      }
    }
    return idLabelMap;
  }

  /**
   * Map suitability lu.
   * 
   * @param suitabilityLU
   *          the suitability lu
   * @return the suitability lu
   */
  public SuitabilityLU mapSuitabilityLU(SuitabilityLU suitabilityLU) {

    Set<AllocationLU> associatedALUs = suitabilityLU.getAssociatedALUs();
    if (suitabilityLU.getAssociatedALUs() != null) {
      LOGGER
          .trace(
              "++++++ suitability LU label: {} has {} associated ALU's,  mapping them…",
              suitabilityLU.getLabel(), associatedALUs.size());
      suitabilityLU.setAssociatedALUsMap(mapAllocationLUs(associatedALUs));
    }
    return suitabilityLU;
  }

  /**
   * Map allocation l us.
   * 
   * @param associatedALUs
   *          the associated al us
   * @return the map
   */
  public Map<String, String> mapAllocationLUs(Set<AllocationLU> associatedALUs) {

    Map<String, String> idLabelMap = new HashMap<String, String>();
    for (AllocationLU allocationLU : associatedALUs) {
      idLabelMap.put(allocationLU.getId(), allocationLU.getLabel());
    }
    return idLabelMap;
  }

  /**
   * Map suitability rule.
   * 
   * @param rule
   *          the rule
   * @return the suitability rule
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public SuitabilityRule mapSuitabilityRule(SuitabilityRule rule)
      throws WifInvalidInputException {
    rule.setSuitabilityLUMap(getIdLabelMap(rule.getSuitabilityLU()));
    Set<AllocationLU> conversions = rule.getConvertibleLUs();
    LOGGER
        .trace(
            " mapping to the interface suitability LU label: {} has {} convertibles ALU's, mapping them…",
            rule.getSuitabilityLU().getLabel(), conversions.size());
    rule.setConvertibleLUsMap(mapAllocationLUs(conversions));
    Set<FactorImportance> factorsImp = rule.getFactorImportances();
    LOGGER.trace("Mapping {} factor importances configured for SLU: {}...",
        factorsImp.size(), rule.getSuitabilityLU().getLabel());
    for (FactorImportance factorImportance : factorsImp) {
      factorImportance = mapFactorImportance(factorImportance);
    }
    return rule;
  }

  /**
   * Map factor importance.
   * 
   * @param factorImportance
   *          the factor importance
   * @return the factor importance
   */
  public FactorImportance mapFactorImportance(FactorImportance factorImportance) {
    Factor aFactor = factorImportance.getFactor();
    String factorLabel = aFactor.getLabel();
    Double factorWeight = factorImportance.getImportance();
    LOGGER.trace(" Factor label: {}", factorLabel);
    LOGGER.trace("- Factor importance: {}", factorWeight);
    factorImportance.setFactorMap(getIdLabelMap(aFactor));

    for (FactorTypeRating aFactorRating : factorImportance
        .getFactorTypeRatings()) {
      aFactorRating = mapFactorTypeRating(aFactorRating);
    }
    return factorImportance;
  }

  /**
   * Map factor type rating.
   * 
   * @param aFactorRating
   *          the a factor rating
   * @return the factor type rating
   */
  public FactorTypeRating mapFactorTypeRating(FactorTypeRating aFactorRating) {
    Double ratingScore = aFactorRating.getScore();
    String ratingLabel = aFactorRating.getFactorType().getLabel();
    LOGGER.trace("mapping factor type label: {}", ratingLabel);
    LOGGER.trace("mapping the rating is: {}", ratingScore);
    aFactorRating
        .setFactorTypeMap(getIdLabelMap(aFactorRating.getFactorType()));
    return aFactorRating;
  }

  /**
   * Gets the id label map.
   * 
   * @param element
   *          the element
   * @return the id label map
   */
  public Map<String, String> getIdLabelMap(Model2JsonMapped element) {
    // LOGGER.trace("Mapping element : {} ", element.getLabel());
    HashMap<String, String> idLabelMap = new HashMap<String, String>(2);
    if (element != null) {
      idLabelMap.put(element.getId(), element.getLabel());
    }
    return idLabelMap;
  }
}
