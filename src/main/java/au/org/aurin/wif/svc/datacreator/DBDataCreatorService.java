package au.org.aurin.wif.svc.datacreator;

import au.org.aurin.wif.model.WifProject;

/**
 * The Interface DBDataCreatorService. Creates the demonstration data for
 * what-if.
 */
public interface DBDataCreatorService {

  /**
   * Creates the allocation module.
   * 
   * @param projectId
   *          the project id
   * @param suitabilityScenarioId
   *          the suitability scenario id
   * @param demandConfigId
   *          the demand config id
   * @param demandScenarioId
   *          the demand scenario id
   * @param allocationScenarioId
   *          the allocation scenario id
   * @return the wif project
   * @throws Exception
   *           the exception
   */
  public WifProject createDemonstrationModule(String projectId,
      String suitabilityScenarioId, String demandConfigId,
      String demandScenarioId, String allocationScenarioId,
      String allocationScenarioReportId, String manualdemandConfigId)
      throws Exception;
}
