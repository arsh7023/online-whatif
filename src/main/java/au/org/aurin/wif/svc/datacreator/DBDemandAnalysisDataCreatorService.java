package au.org.aurin.wif.svc.datacreator;

import au.org.aurin.wif.model.WifProject;

/**
 * The Interface DBDataCreatorService.
 */
public interface DBDemandAnalysisDataCreatorService {

  /**
   * Creates the setup module.
   * 
   * @param projectId
   *          the project id
   * @param suitabilityScenarioId
   *          the suitability scenario id
   * @param demandConfigId
   *          the demand config id
   * @param demandScenarioId
   *          the demand scenario id
   * @return the wif project
   * @throws Exception
   *           the exception
   */
  public WifProject createDemandAnalysisModule(String projectId,
      String suitabilityScenarioId, String demandConfigId,
      String demandScenarioId, String manualdemandConfigId) throws Exception;
}
