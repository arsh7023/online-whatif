package au.org.aurin.wif.svc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.validate.IncompleteSuitabilityLUConfigException;
import au.org.aurin.wif.exception.validate.UAZAlreadyCreatedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.ProjectReport;

/**
 * The Interface ProjectService.
 */
public interface ProjectService {

  /**
   * Gets the project.
   *
   * @param pId
   *          the id
   * @return the project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  WifProject getProject(String pId) throws WifInvalidInputException,
  WifInvalidConfigException;

  /**
   * Gets the project no mapping.
   *
   * @param pId
   *          the id
   * @return the project no mapping
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  WifProject getProjectNoMapping(String pId) throws WifInvalidInputException,
  WifInvalidConfigException;

  /**
   * Gets the project.
   *
   * @param role
   *          the role
   * @param id
   *          the id
   * @return the project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  WifProject getProject(String role, String id)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Creates the project.
   *
   * @param project
   *          the project
   * @param username
   *          the username
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  WifProject createProject(WifProject project, String username)
      throws WifInvalidInputException, DataStoreUnavailableException,
      WifInvalidConfigException;

  /**
   * Gets the project configuration.
   *
   * @param id
   *          the id
   * @return the project configuration
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  WifProject getProjectConfiguration(String id)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete project.
   *
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteProject(String id) throws WifInvalidInputException,
  WifInvalidConfigException;

  /**
   * Delete project.
   *
   * @param id
   *          the id
   * @param deleteUAZ
   *          the delete uaz
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteProject(String id, Boolean deleteUAZ)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Convert union to uaz.
   *
   * @param id
   *          the id
   * @param optionalColumns
   *          the optional columns
   * @param roleId
   *          the role id
   * @return the boolean
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws UAZAlreadyCreatedException
   *           the uAZ already created exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteSuitabilityLUConfigException
   *           the incomplete suitability lu config exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws FactoryException
   *           the factory exception
   * @throws GeoServerConfigException
   * @throws DataStoreCreationException
   */
  Boolean convertUnionToUAZ(String id, List<String> optionalColumns,
      String roleId) throws WifInvalidInputException,
  UAZAlreadyCreatedException, WifInvalidConfigException,
  IncompleteSuitabilityLUConfigException, NoSuchAuthorityCodeException,
  DataStoreUnavailableException, FactoryException,
  GeoServerConfigException, DataStoreCreationException;

  /**
   * Update project.
   *
   * @param project
   *          the project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateProject(WifProject project) throws WifInvalidInputException,
  WifInvalidConfigException;

  /**
   * Gets the all projects.
   *
   * @param role
   *          the role
   * @return the all projects
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<WifProject> getAllProjects(String role) throws WifInvalidInputException;

  /**
   * Setup allocation config.
   *
   * @param project
   *          the project
   * @return true, if successful
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  public boolean setupAllocationConfig(WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Setup Manual allocation config. IN manual allocation config we just asking
   * area requirenments. It needed projection and for each projection area
   * required for specific land use
   *
   * @param project
   *          the project
   * @return true, if successful
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws FactoryException
   * @throws GeoServerConfigException
   * @throws DataStoreUnavailableException
   * @throws NoSuchAuthorityCodeException
   * @throws MalformedURLException
   * @throws IllegalArgumentException
   */
  public WifProject setupManualAllocationConfig(WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IllegalArgumentException, MalformedURLException,
      NoSuchAuthorityCodeException, DataStoreUnavailableException,
      GeoServerConfigException, FactoryException;

  /**
   * Restore project configuration.
   *
   * @param projectReport
   *          the project report
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   */
  WifProject restoreProjectConfiguration(ProjectReport projectReport)
      throws WifInvalidInputException, WifInvalidConfigException, ParsingException;

  /**
   * Purge project taht could have been created by the as synchronous process
   * but it failed
   *
   * @param id
   *          the id
   * @throws InvalidEntityIdException
   */
  void purgeProject(String id) throws InvalidEntityIdException;

  File getProjectZipUAZ(String id) throws WifInvalidInputException,
  WifInvalidConfigException, IOException, DatabaseFailedException;

  Boolean PublishWMSLayer( String tableName,
      CoordinateReferenceSystem crs, String projectID);

}
