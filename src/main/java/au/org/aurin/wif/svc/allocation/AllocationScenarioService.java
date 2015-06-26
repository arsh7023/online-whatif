package au.org.aurin.wif.svc.allocation;

import java.net.MalformedURLException;
import java.util.List;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationConfigException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.model.allocation.AllocationScenario;

/**
 * What if? projects future land use, population and employment patterns by
 * allocating the projected land use demands derived from a demand scenario to
 * different locations on the basis of their relative suitability as defined by
 * the assumptions set in the suitability scenario and any allocation controls
 * specified in the control scenario assumptions.
 * 
 * You will be notified if not enough land is available to satisfy the projected
 * demand. If this occurs, you must modify the suitability, demand, or control
 * scenario assumptions until the suitability, demand and control assumptions
 * are consistent. Edit Define Control Scenarios The control scenarios allow you
 * to save the allocation order, land use controls, infrastructure controls, and
 * growth pattern assumption that are defined on the Control scenario
 * assumptions. This allows a set of control assumptions to be used along with a
 * suitability scenario and demand scenario to define an allocation scenario. It
 * comprises the definition of:
 * 
 * Allocation Priorities Infrastructure Controls Land Use Controls Growth
 * Patterns
 * 
 * Allocation Priorities Used to specify:
 * 
 * the order in which projected land use demands are to be allocated the order
 * in which the various land use demands are to be allocated to different
 * locations.
 * 
 * Land Uses Allocation order Can be used to specify the order in which
 * different land use demands are to be satisfied, i.e., the land use demand to
 * be satisfied first, the demand to be satisfied second, and so on. (Ranked
 * list!) Spatial order Specify the order in which different factors are
 * considered in allocating the projected land use demands to different
 * locations. There are currently the following options
 * 
 * Suitability: allocates the projected land use demands to different locations
 * on the basis of their suitability for accommodating each land use (The better
 * the suitability the score, the more likely to be allocated). Growth Pattern:
 * allocates the projected demand on the basis of “growth patterns” specifying
 * the order in which the projected demand should be allocated to different
 * locations. [Look up in the growth pattern layer] Size: allocates the
 * projected demand to UAZs on the basis of their size, starting with the
 * largest suitable UAZ and proceeding to the smallest one. Random: This option
 * allocated the projected demand to different locations in a completely random
 * pattern
 * 
 * Infrastructure Controls Specify the influence which the availability of
 * different kinds of infrastructure will have on the allocation process.
 * 
 * Infrastructure Plans Can be used to select one or more Infrastructure Plans
 * specifying, for instance, the extension of water and sewer service, the
 * phased construction of major roads or the construction of highway
 * interchanges. Infrastructure Required Can be used to specify the relationship
 * between infrastructure service (or proximity) and different land uses.
 * 
 * N/A (for “Not Affected”) is used for land uses whose location is not affected
 * by the availability of a given type of infrastructure. R (for “Required”) is
 * used for land uses that are assumed to require a particular type of land use.
 * X (for “Excluded”) is used for land uses which are assumed to be excluded
 * from areas that are served by, or near to, a particular type of
 * infrastructure
 * 
 * Land Use Controls [Data hardcoded in interface?] Can be used to select the
 * 2030 Land Use plan from the drop down list. If this option is selected, the
 * projected land use demands will only be allocated to locations for which they
 * are planned in the 2030 the land use plan
 * 
 * Defining Growth Patterns [Data hardcoded in interface?] Can be used to select
 * options which will determine the general spatial pattern for future growth.
 * For instance, the Concentric growth option assumes that, other things being
 * equal, the region will grow in a series of concentric rings from Central
 * City, located to the southeast of Edge City. The Radial growth option assumes
 * that, other things being equal, the region will grow in a radial pattern
 * outward from the region’s major roads. Edit Define Allocation Scenarios
 * 
 * An allocation scenario is a triad of a Suitability scenario, that specifies
 * the most suitable sites, a Demand scenario that especifies the projected
 * demand for those sites, and a Control scenario that guides the allocation
 * process (specifies the allocation assumptions between demand and
 * suitability).
 * 
 * Scenario names
 * 
 * The default allocation scenario names created by What if? identify the
 * suitability, demand, and control scenarios that underlie them.
 * 
 * For example, the "Suburbanization – Low Growth – Extend Services" scenario
 * combines the assumptions specified in: (1) the "Suburbanization" suitability
 * scenario; (2) the "Low Growth" demand scenario; and (3) the "Extend Services"
 * control scenario. Edit Compute Allocation
 * 
 * Takes many minutes to produce the following... Edit Allocation results
 * Whether all the information is presented depends on the type of "What If"
 * Analysis Features performed (see next section), which in turns depends on the
 * right information available in the UAZ
 * 
 * projected land use maps: Displays the projected land uses on top of the
 * current land use map, generating a map showing the projected land uses for
 * the selected allocation scenario and projection year. Viewing new development
 * maps:view maps which display the newly allocated land uses in a projection
 * year, without displaying land uses which have not changed. Comparing
 * allocation maps; of two up to four allocation scenarios already computed
 * Viewing allocation reports; Record the projected land uses, residential
 * population and employment for each projection year and for build out for the
 * sub-areas you defined already. It also provides information on the change in
 * the quantity of land, the residential population, and the employment for each
 * projection year for the population and population/employment analysis
 * options. it also reports for each sub area and in each projection year: Total
 * population; Group quarters population; Number of households; Number of
 * housing units; Number of vacant housing units; Vacancy rate; and Average
 * household size. the projected employment for all employment sectors for each
 * sub-area and the change in the employment for each employment sector between
 * projection years for each sub-area Viewing allocation assumptions reports:
 * All the assumptions compiled Viewing sub-area projections: an allocation map
 * of each sub area per projection year
 */
public interface AllocationScenarioService {

  /**
   * Creates the allocation scenario.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @param projectId
   *          the project id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteAllocationScenarioException
   *           the incomplete allocation scenario exception
   * @throws FactoryException
   * @throws GeoServerConfigException
   * @throws DataStoreUnavailableException
   * @throws NoSuchAuthorityCodeException
   * @throws MalformedURLException
   * @throws IllegalArgumentException
   * @throws IncompleteAllocationConfigException
   */
  AllocationScenario createAllocationScenario(
      AllocationScenario allocationScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteAllocationScenarioException,
      IllegalArgumentException, MalformedURLException,
      NoSuchAuthorityCodeException, DataStoreUnavailableException,
      GeoServerConfigException, FactoryException;

  /**
   * Gets the allocation scenario.
   * 
   * @param id
   *          the id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  AllocationScenario getAllocationScenario(String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the allocation scenario.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  AllocationScenario getAllocationScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update allocation scenario.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  void updateAllocationScenario(AllocationScenario allocationScenario,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;

  /**
   * Delete allocation scenario.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  void deleteAllocationScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the allocation scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the allocation scenarios
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<AllocationScenario> getAllocationScenarios(String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the wms information, for allocation for each suitability land use
   * including the chloropleth ranges.
   * 
   * 
   * @param project
   *          the project
   * @return the wms
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws ParsingException
   * @throws WifInvalidConfigException
   */
  WMSOutcome getWMS(String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;
}
