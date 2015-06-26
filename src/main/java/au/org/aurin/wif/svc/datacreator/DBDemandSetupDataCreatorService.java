package au.org.aurin.wif.svc.datacreator;

import au.org.aurin.wif.model.WifProject;

/**
 * The Interface DBDataCreatorService.
 */
public interface DBDemandSetupDataCreatorService {

  /**
   * Creates the setup module.
   * 
   * @param projectId
   *          the project id
   * @param suitabilityScenarioId
   *          the suitability scenario id
   * @param demandConfigId
   *          the demand config id
   * @return the wif project
   * @throws Exception
   *           the exception
   */
  public WifProject createDemandSetupModule(String projectId,
      String suitabilityScenarioId, String demandConfigId,
      String manualdemandConfigId) throws Exception;
}
