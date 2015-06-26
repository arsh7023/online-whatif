package au.org.aurin.wif.io.parsers;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class SuitabilityCouchParser.
 */
@Component
public class SuitabilityCouchParser {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityCouchParser.class);

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /**
   * Parses the suitability scenario.
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @param wifProject
   *          the wif project
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws ParsingException
   *           the parsing exception
   */
  public SuitabilityScenario parseSuitabilityScenario(
      SuitabilityScenario suitabilityScenario, WifProject wifProject)
      throws WifInvalidInputException, ParsingException {
    try {
      Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
          .getSuitabilityRules();
      if (suitabilityRules == null) {
        String msg = "suitabilityScenario with the ID "
            + suitabilityScenario.getId() + " doesn't have rules defined";
        LOGGER.error(msg);
        throw new ParsingException(msg);
      }
      LOGGER.trace("{} has {} suitability rules associated",
          suitabilityScenario.getLabel(), suitabilityRules.size());
      for (SuitabilityRule rule : suitabilityRules) {
        rule = parseSuitabilityRule(rule, wifProject);

      }
      suitabilityScenario.setWifProject(wifProject);
      return suitabilityScenario;
    } catch (Exception e) {
      String id = suitabilityScenario.getId();
      String msg = "parseSuitabilityScenario failed from JSON for id {} ";
      if (e instanceof ParsingException) {
        LOGGER.error(msg, id);
        throw new ParsingException(msg + e.toString());
      } else if (e instanceof NullPointerException) {
        LOGGER.error("NullPointerException " + msg, id);
        throw new ParsingException("NullPointerException " + msg + e.toString());
      } else {
        LOGGER.error(msg, id);
        throw new ParsingException(msg + id);
      }
    }
  }

  /**
   * Parses the suitability rule.
   * 
   * @param rule
   *          the rule
   * @param wifProject
   *          the wif project
   * @return the suitability rule
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  private SuitabilityRule parseSuitabilityRule(SuitabilityRule rule,
      WifProject wifProject) throws WifInvalidInputException {

    Set<String> slukeys = rule.getSuitabilityLUMap().keySet();
    String sluid = slukeys.iterator().next();
    String slulabel = rule.getSuitabilityLUMap().get(sluid);
    LOGGER.trace("looking for slu id {} with label: {} ", sluid, slulabel);

    SuitabilityLU suitabilityLU = wifProject.getSuitabilityLUById(sluid);
    rule.setSuitabilityLU(suitabilityLU);
    LOGGER
        .trace(
            "++++++ suitability LU label: {} has {} convertibles ALU's mappedby the interface, attempting to look for them…",
            suitabilityLU.getLabel(), rule.getConvertibleLUsMap().size());
    rule.setConvertibleLUs(projectParser.parseAllocationLUs(rule
        .getConvertibleLUsMap()));
    Collection<FactorImportance> factorsImp = rule.getFactorImportances();
    LOGGER.trace("Parsing {} factor importances configured for slu: {}...",
        factorsImp.size(), rule.getSuitabilityLU().getLabel());
    for (FactorImportance factorImportance : factorsImp) {
      Set<String> factorkeys = factorImportance.getFactorMap().keySet();
      String factorid = factorkeys.iterator().next();
      String factorlabel = factorImportance.getFactorMap().get(factorid);
      LOGGER.trace("looking for factor id {} with label: {} ", factorid,
          factorlabel);

      Factor factor = wifProject.getFactorById(factorid);
      factorImportance.setFactor(factor);
      LOGGER.trace("--) the importance for this factor is: {}",
          factorImportance.getImportance());
      factorImportance.setSuitabilityRule(rule);
      for (FactorTypeRating aFactorRating : factorImportance
          .getFactorTypeRatings()) {
        Double ratingScore = aFactorRating.getScore();
        Set<String> factortypekeys = aFactorRating.getFactorTypeMap().keySet();
        String factortypeid = factortypekeys.iterator().next();
        String factortypelabel = aFactorRating.getFactorTypeMap().get(
            factortypeid);
        LOGGER.trace("looking for factor type id {} with label: {} ",
            factortypeid, factortypelabel);
        FactorType factorType = factor.getFactorTypeById(factortypeid);

        LOGGER.trace("parsing factor type label: {}", factortypelabel);
        LOGGER.trace("parsing the rating is: {}", ratingScore);
        aFactorRating.setFactorType(factorType);
        aFactorRating.setFactorImportance(factorImportance);
      }
    }
    return rule;
  }

  /**
   * Parses the.
   * 
   * @param suitabilityLU
   *          the suitability lu
   * @return the suitability lu
   */
  public SuitabilityLU parse(SuitabilityLU suitabilityLU) {
    LOGGER.trace("parsing the following suitability lu: "
        + suitabilityLU.getLabel());

    suitabilityLU.setFeatureFieldName(WifKeys.SCORE_SUFFIX
        + suitabilityLU.getLabel().replaceAll(" ", "_").replaceAll("-", "_")
            .toLowerCase());

    if (suitabilityLU.getAssociatedALUsMap() != null) {
      LOGGER
          .trace("++++++ suitability LU label: {} has {} associated ALU's...",
              suitabilityLU.getLabel(), suitabilityLU.getAssociatedALUsMap()
                  .size());
      suitabilityLU.setAssociatedALUs(projectParser
          .parseAllocationLUs(suitabilityLU.getAssociatedALUsMap()));
      Set<AllocationLU> associatedALUs = suitabilityLU.getAssociatedALUs();
      for (AllocationLU allocationLU : associatedALUs) {
        allocationLU.setAssociatedLU(suitabilityLU);
      }
    }
    return suitabilityLU;
  }

}
