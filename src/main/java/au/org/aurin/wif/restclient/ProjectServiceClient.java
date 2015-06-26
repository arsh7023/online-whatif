package au.org.aurin.wif.restclient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.ProjectReport;

/**
 * The Interface ProjectServiceClient.
 */
public interface ProjectServiceClient {

  /**
   * Sets the url.
   * 
   * @param url
   *          the new url
   */
  void setUrl(String url);

  /**
   * Gets the version.
   * 
   * @return the version
   */
  Map<String, String> getVersion();

  /**
   * Gets the project.
   * 
   * @param role
   *          the role
   * @param id
   *          the id
   * @return the project
   */
  WifProject getProject(String role, String id);

  /**
   * Update project.
   * 
   * @param role
   *          the role
   * @param id
   *          the id
   * @param project
   *          the project
   */
  void updateProject(String role, String id, WifProject project);

  /**
   * Delete project.
   * 
   * @param role
   *          the role
   * @param id
   *          the id
   */
  void deleteProject(String role, String id);

  /**
   * Creates the project.
   * 
   * @param role
   *          the role
   * @param project
   *          the project
   * @return the string
   */
  String createProject(String role, WifProject project);

  /**
   * Gets the status.
   * 
   * @param role
   *          the role
   * @param id
   *          the id
   * @return the status
   */
  HashMap<String, String> getStatus(String role, String id);

  /**
   * Gets the uAZ attributes.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the uAZ attributes
   */
  List<String> getUAZAttributes(String id, String projectId);

  /**
   * Gets the distinct entries for uaz attribute.
   * 
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @param attr
   *          the attr
   * @return the distinct entries for uaz attribute
   */
  List<String> getDistinctEntriesForUAZAttribute(String roleId, String id,
      String attr);

  /**
   * Finalize uaz.
   * 
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @param factorsList
   *          the factors list
   */
  void finalizeUAZ(String roleId, String id, List<String> factorsList);

  /**
   * Gets the all projects.
   * 
   * @param role
   *          the role
   * @return the all projects
   */
  List<WifProject> getAllProjects(String role);

  /**
   * Gets the project configuration.
   * 
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the project configuration
   */
  WifProject getProjectConfiguration(String roleId, String id);

  /**
   * Gets the project revision.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the project revision
   */
  String getProjectRevision(String roleId, String projectId);

  /**
   * Setup allocation config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param projectOld
   *          the project old
   */
  void setupAllocationConfig(String roleId, String projectId,
      WifProject projectOld);

  /**
   * Restore project.
   * 
   * @param role
   *          the role
   * @param projectReport
   *          the project report
   * @return the string
   */
  String restoreProject(String role, ProjectReport projectReport);

  /**
   * Delete project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param deleteUAZ
   *          the delete uaz
   */
  void deleteProject(String roleId, String projectId, boolean deleteUAZ);

  /**
   * Gets the project full configuration report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the project report
   */
  ProjectReport getProjectReport(String roleId, String projectId);

  /**
   * Upload uaz.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   */
  void uploadUAZ(String roleId, String projectId);

  /**
   * Gets the upload status.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the upload status
   */
  HashMap<String, String> getUploadStatus(String roleId, String projectId);

  /**
   * Upload zip.
   * 
   * @param input
   *          the input
   * @return the string
   */
  String uploadZip(File input);

  /**
   * Download zip.
   * 
   * @param projectId
   *          the project id
   * @return true, if successful
   */
  boolean downloadZip(String roleId, String projectId);
}
