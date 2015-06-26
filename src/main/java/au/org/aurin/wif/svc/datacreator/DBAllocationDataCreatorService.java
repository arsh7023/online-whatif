package au.org.aurin.wif.svc.datacreator;

import au.org.aurin.wif.model.WifProject;

/**
 * The Interface DBAllocationDataCreatorService.
 */
public interface DBAllocationDataCreatorService {

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
  public WifProject createAllocationModule(String projectId,
      String suitabilityScenarioId, String demandConfigId,
      String demandScenarioId, String allocationScenarioId) throws Exception;
}
