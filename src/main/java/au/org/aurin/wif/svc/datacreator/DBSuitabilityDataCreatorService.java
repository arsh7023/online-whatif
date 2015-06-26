package au.org.aurin.wif.svc.datacreator;

import au.org.aurin.wif.model.WifProject;

/**
 * The Interface DBDataCreatorService.
 */
public interface DBSuitabilityDataCreatorService {

   /**
    * Creates the suitability module.
    *
    * @param projectId the project id
    * @param suitabilityScenarioId the suitability scenario id
    * @return the wif project
    * @throws Exception the exception
    */
  public WifProject createSuitabilityModule(String projectId,
      String suitabilityScenarioId) throws Exception;
}
