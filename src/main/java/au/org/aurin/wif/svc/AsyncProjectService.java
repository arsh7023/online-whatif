package au.org.aurin.wif.svc;

import java.io.IOException;
import java.util.concurrent.Future;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.io.MiddlewarePersistentException;
import au.org.aurin.wif.exception.io.WifIOException;
import au.org.aurin.wif.exception.validate.InvalidFFNameException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;

/**
 * The Interface AsyncProjectService.
 */
public interface AsyncProjectService {

  /**
   * Setup project async.
   *
   * @param project
   *          the project
   * @param username
   *          the username
   * @return the future
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws InvalidFFNameException
   * @throws InvalidLabelException
   * @throws WifInvalidConfigException
   */
  Future<String> setupProjectAsync(WifProject project, String username)
      throws WifInvalidInputException, DataStoreUnavailableException,
      DataStoreCreationException, WifInvalidConfigException, InvalidLabelException, InvalidFFNameException;

  /**
   * Upload uaz async.
   *
   * @param project
   *          the project
   * @param roleId
   *          the role id
   * @return the future
   * @throws WifIOException
   *           the wif io exception
   * @throws DataStoreCreationException
   * @throws IOException
   * @throws MiddlewarePersistentException
   */
  Future<String> uploadUAZAsync(WifProject project, String roleId)
      throws WifIOException, IOException, DataStoreCreationException,
      MiddlewarePersistentException;
}
