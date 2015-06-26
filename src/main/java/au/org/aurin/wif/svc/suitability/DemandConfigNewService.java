package au.org.aurin.wif.svc.suitability;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandConfigNewException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandConfigNew;

/**
 * The Interface DemandConfigNewService. It handles the CRUD operations on
 * DemandConfigNew.
 */
public interface DemandConfigNewService {

  /**
   * Adds the DemandConfigNew.
   * 
   * @param DemandConfigNew
   *          the DemandConfigNew
   * @param projectId
   *          the project id
   * @return the DemandConfigNew
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandConfigNewException
   *           the incomplete demand config exception
   */
  DemandConfigNew createDemandConfigNew(DemandConfigNew DemandConfigNew,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandConfigNewException;

  /**
   * Gets the DemandConfigNew.
   * 
   * @param projectId
   *          the project id
   * @return the DemandConfigNew
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  DemandConfigNew getDemandConfigNew(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update DemandConfigNew.
   * 
   * @param DemandConfigNew
   *          the DemandConfigNew
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateDemandConfigNew(DemandConfigNew DemandConfigNew, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete DemandConfigNew.
   * 
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteDemandConfigNew(String projectId) throws WifInvalidInputException,
      WifInvalidConfigException;

}
