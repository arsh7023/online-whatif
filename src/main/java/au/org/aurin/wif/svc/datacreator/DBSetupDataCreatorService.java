package au.org.aurin.wif.svc.datacreator;

import au.org.aurin.wif.model.WifProject;

/**
 * The Interface DBDataCreatorService.
 */
public interface DBSetupDataCreatorService {

  /**
   * Creates the setup module.
   *
   * @param projectId the project id
   * @return the wif project
   * @throws Exception the exception
   */
  public WifProject createSetupModule(String projectId) throws Exception;
}
