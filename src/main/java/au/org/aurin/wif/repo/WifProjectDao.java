package au.org.aurin.wif.repo;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;

/**
 * The Interface WifProjectDao.
 */
public interface WifProjectDao {

  /**
   * Adds the project.
   * 
   * @param project
   *          the project
   */
  void addProject(WifProject project);

  /**
   * Create the project.
   *
   * @param project the project
   * @return the wif project
   * @throws WifInvalidConfigException 
   * @throws WifInvalidInputException 
   */
  WifProject persistProject(WifProject project) throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Update project.
   *
   * @param project the project
   */
  void updateProject(WifProject project);
  
  /**
   * Find project by id.
   *
   * @param id the id
   * @return the wif project
   */
  WifProject findProjectById(String id);
  
  /**
	 * Delete project.
	 *
	 * @param project the project
	 */
	void deleteProject(WifProject project);
	
	  /**
	   * Gets the all projects.
	   * 
	   * @return the all projects
	   */
	  List<WifProject> getAllProjects();
	
	  /**
	   * Gets the all projects.
	   *
	   * @param role the role
	   * @return the all projects
	   */
	  List<WifProject> getAllProjects(String role);

}
