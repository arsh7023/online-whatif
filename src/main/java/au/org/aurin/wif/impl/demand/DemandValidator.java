/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.IncompleteDemandConfigException;
import au.org.aurin.wif.model.demand.CurrentDemographic;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemandConfigurator.
 */
@Component
public class DemandValidator {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 21342673455379L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandValidator.class);

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service successfully cleared! ");
  }

  /**
   * Validate the configuration of the demand set up configuration.
   * 
   * @param demandConfig
   *          the demand config
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public void validate(final DemandConfig demandConfig)
      throws IncompleteDemandConfigException {
    LOGGER.debug("validating demand config...");
    final Integer baseYear = demandConfig.getBaseYear();
    final CurrentDemographic currentDemographic = demandConfig
        .getCurrentDemographic();
    if (currentDemographic == null) {
      throw new IncompleteDemandConfigException("currentDemographic is null!");
    }

    if (demandConfig.getProjections() == null) {
      throw new IncompleteDemandConfigException("projections are null!");
    }
    if (baseYear == null) {
      throw new IncompleteDemandConfigException("baseYear is null!");
    }
    if (demandConfig.getCurrentDemographic().getResidentialCurrentData() == null) {
      throw new IncompleteDemandConfigException(
"ResidentialCurrentDatas is null!");
    }
    if (demandConfig.getResidentialPastTrendInfos() == null) {
      throw new IncompleteDemandConfigException(
          "getResidentialPastTrendInfos is null!");
    }
    if (demandConfig.getResidentialPastTrendInfos().size() < 2) {
      throw new IncompleteDemandConfigException(
          "ResidentialPastTrendInfos is less than 2!");
    }
    if (demandConfig.getProjections() == null) {
      throw new IncompleteDemandConfigException("projections are null!");
    }
    if (demandConfig.getProjections().size() < 2) {
      throw new IncompleteDemandConfigException("projections are less than 2!!");
    }
  }
}
