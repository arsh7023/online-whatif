/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.impl.AllocationLUServiceImpl;
import au.org.aurin.wif.impl.ProjectServiceImpl;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;

/**
 * service test for What If configurations allocationLU.
 * 
 * @author marcosnr
 */
public class AllocationLUServiceTest {

  /** the service. */
  @InjectMocks
  private AllocationLUServiceImpl allocationLUService;

  @Mock
  private CouchWifProjectDao wifProjectDao;

  /** The wif allocationLU dao. */
  @Mock
  private CouchAllocationLUDao allocationLUDao;

  /** The mapper. */
  @Mock
  private CouchMapper mapper;

  /** the service. */
  @Mock
  private ProjectServiceImpl projectService;

  private String newProjectId;

  /** The project. */
  private WifProject project;

  /** The allocationLU label. */
  private final String allocationLULabel = "allocationLUTest474533%##$%%18";

  /** The test id. */
  private String testID;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationLUServiceTest.class);

  @BeforeClass
  public void setup() throws Exception, WifInvalidConfigException {
    MockitoAnnotations.initMocks(this);
    project = new WifProject();
    project.setName("test");
    project.setId("so mighty");
    when(wifProjectDao.persistProject(Matchers.any(WifProject.class)))
        .thenReturn(project);
    when(projectService.getProject(Matchers.any(String.class))).thenReturn(
        project);

    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setWifProject(project);//
    allocationLU.setId("some ID");
    allocationLU.setFeatureFieldName("name");
    when(allocationLUDao.persistAllocationLU(Matchers.any(AllocationLU.class)))
        .thenReturn(allocationLU);
  }

  /**
   * my Adds the allocationLU test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createAllocationLUTest() throws Exception {
    LOGGER.debug("createAllocationLUTest");

    project = wifProjectDao.persistProject(project);

    newProjectId = project.getId().toString();
    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setWifProject(project);//
    allocationLU.setFeatureFieldName("name");

    allocationLU.setLabel(allocationLULabel);
    allocationLUService.createAllocationLU(allocationLU, newProjectId);
    verify(allocationLUDao).persistAllocationLU(
        (AllocationLU) Matchers.anyObject());
  }

}
