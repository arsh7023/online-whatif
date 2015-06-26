/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.demand;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.impl.demand.DemandConfigurator;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenarioNew;
import au.org.aurin.wif.model.demand.datanew.DemandDataNew;
import au.org.aurin.wif.model.demand.datanew.DemandEmpNew;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioNewDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class CreateTrendIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CreateDemandScenarioNewIT extends AbstractTestNGSpringContextTests {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CreateDemandScenarioNewIT.class);

  /** The demand scenario service. */
  @Autowired
  private DemandScenarioService demandScenarioService;

  /** The demand analyzer. */
  @Autowired
  private DemandConfigurator demandConfigurator;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  @Autowired
  private DemandScenarioNewDao demandScenarioNewDao;

  /** The demand scenario id. */
  private String demandScenarioId;

  @Resource
  private DemandConfigService demandConfigService;

  private DemandConfig demandConfig;

  @Resource
  private ProjectService projectService;

  private WifProject project;

  @Test(enabled = true, groups = { "demand" })
  public void createAutomatedScenarioTest() throws Exception,
      WifInvalidConfigException {
    LOGGER.debug("Creating automated demand scenario Test");

    DemandScenarioNew demandScenarioNew = new DemandScenarioNew();
    Set<DemandDataNew> demandDataItems = new HashSet<DemandDataNew>();

    demandScenarioNew.setProjectId("176bf47f1c37cd08814663e76b00843e");
    demandScenarioNew.setLabel("test");

    DemandDataNew demandDataItem = new DemandDataNew();

    demandDataItem.setItemID("i1");
    demandDataItem.setItemLabel("lbl");
    demandDataItem.setItemProjectionName("pp");
    demandDataItem.setItemValue(200.0);
    demandDataItem.setItemYear("2000");
    demandDataItems.add(demandDataItem);

    demandScenarioNew.setDemandDataItems(demandDataItems);

    Set<DemandEmpNew> demandEmpItems = new HashSet<DemandEmpNew>();
    DemandEmpNew demandEmpItem = new DemandEmpNew();

    demandEmpItem.setSectorName("emp1");
    demandEmpItem.setSectorData(demandDataItems);

    demandEmpItems.add(demandEmpItem);
    demandScenarioNew.setDemandEmpItems(demandEmpItems);

    DemandScenarioNew savedScenario = demandScenarioNewDao
        .persistDemandScenarioNew(demandScenarioNew);
    demandScenarioId = savedScenario.getId();
    LOGGER.debug("returning the automatedScenario with id={}",
        savedScenario.getId());
    LOGGER.debug("beginning demand analysis...");

  }

}
