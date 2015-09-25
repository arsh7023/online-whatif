/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.suitability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.impl.datacreator.DBSuitabilityDataCreatorServiceImpl;
import au.org.aurin.wif.impl.report.ConsoleReporter;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.io.WifFileUtils;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.datacreator.DBSetupDataCreatorService;

/**
 * The Class SuitabilityScenarioServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class SuitabilityScenarioServiceIT extends
AbstractTestNGSpringContextTests {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityScenarioServiceIT.class);

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The suitability rule dao. */
  @Autowired
  private SuitabilityRuleDao suitabilityRuleDao;


  /** The project id. */
  private String projectId;

  /** The project. */
  private WifProject project;

  /** The suitability scenario label. */
  private final String suitabilityScenarioLabel = "suitabilityScenarioTest"
      + System.currentTimeMillis();;

      /** The test id. */
      private String testID;

      /** The reporter. */
      @Autowired
      private ConsoleReporter reporter;

      /** The utils. */
      @Autowired
      private WifFileUtils utils;

      /** The creator. */
      @Autowired
      private DBSetupDataCreatorService creator;

      /** The suitability scenario dao. */
      @Autowired
      private SuitabilityScenarioDao suitabilityScenarioDao;

      /** The suitability scenario id. */
      private String suitabilityScenarioId;

      /**
       * Creates the suitability scenario test.
       *
       * @throws Exception
       *           the exception
       */
      @Test(enabled = true, groups = { "setup", "service", "suitability" })
      public void createSuitabilityScenarioTest() throws Exception {

        projectId = "suitabilityProjectTestId" + System.currentTimeMillis();
        suitabilityScenarioId = "suitabilityScenarioTestId"
            + System.currentTimeMillis();
        final WifProject project = creator.createSetupModule(projectId);
        Assert.assertNotNull(project.getId());
        Assert.assertEquals(project.getLabel(), "Demonstration");
        Assert.assertNotNull(project.getCreationDate());
        Assert.assertNotNull(project.getId());
        Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
            .getAssociatedALUs().size());

        // Creating dummy scenario
        SuitabilityScenario suitabilityScenario = DBSuitabilityDataCreatorServiceImpl
            .createSimpleSuitabilityScenario(project, suitabilityScenarioId);

        suitabilityScenario.setLabel(suitabilityScenarioLabel);

        suitabilityScenario = suitabilityScenarioService.createSuitabilityScenario(
            suitabilityScenario, projectId);
        testID = suitabilityScenario.getId();
        Assert.assertNotNull(testID);
      }

      /**
       * Gets the suitability scenario test.
       *
       * @return the suitability scenario test
       * @throws Exception
       *           the exception
       */
      @Test(enabled = true, dependsOnMethods = { "createSuitabilityScenarioTest" }, groups = {
          "setup", "service", "suitability" })
      public void getSuitabilityScenarioTest() throws Exception {

        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(suitabilityScenarioId);
        Assert.assertNotNull(suitabilityScenario.getId());
        Assert.assertTrue(suitabilityScenario.getReady());
      }

      /**
       * Update suitability scenario test.
       *
       * @throws Exception
       *           the exception
       */
      @Test(enabled = true, dependsOnMethods = { "getSuitabilityScenarioTest" }, groups = {
          "setup", "service", "suitability" })
      public void updateSuitabilityScenarioTest() throws Exception {
        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(suitabilityScenarioId);
        System.out
        .println("update suitabilityScenario test, suitabilityScenario label: "
            + suitabilityScenario.getLabel());
        final String oldLabel = suitabilityScenario.getLabel();
        suitabilityScenario.setLabel("new" + suitabilityScenarioLabel);
        suitabilityScenarioService.updateSuitabilityScenario(suitabilityScenario,
            projectId);
        final SuitabilityScenario suitabilityScenario2 = suitabilityScenarioService
            .getSuitabilityScenario(suitabilityScenarioId);
        Assert.assertEquals(suitabilityScenario2.getLabel(), "new"
            + suitabilityScenarioLabel);
        // Reverting change
        suitabilityScenario.setLabel(oldLabel);
        suitabilityScenarioService.updateSuitabilityScenario(suitabilityScenario,
            projectId);
      }

      /**
       * Gets the suitability scenario with project id test.
       *
       * @return the suitability scenario with project id test
       * @throws Exception
       *           the exception
       */
      @Test(enabled = true, dependsOnMethods = { "updateSuitabilityScenarioTest" }, groups = {
          "setup", "service", "suitability" })
      public void getSuitabilityScenarioWithProjectIdTest() throws Exception {
        LOGGER
        .debug("getSuitabilityScenarioWithProjectIdTest, suitabilityScenario id: "
            + testID);
        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(suitabilityScenarioId, projectId);
        Assert.assertNotNull(suitabilityScenario);
      }

      /**
       * Gets the wMS test.
       *
       * @return the wMS test
       * @throws Exception
       *           the exception
       */
      @Test(enabled = true, dependsOnMethods = { "getSuitabilityScenarioWithProjectIdTest" }, groups = {
          "setup", "service", "suitability" })
      public void getWMSTest() throws Exception {
        LOGGER.debug("getWMSTest: {}");
        final WMSOutcome wms = suitabilityScenarioService
            .getWMS(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
        LOGGER.debug("wms.getServerURL() {}", wms.getServerURL());

        LOGGER.debug("wms.getStoreName() {}", wms.getStoreName());
        final Map<String, Integer> scoreColumns = wms.getScoreColumns();
        LOGGER.debug("scoreColumns size: {}", scoreColumns.size());

        for (final String key : scoreColumns.keySet()) {
          LOGGER.debug("column: {}, range {}", key, scoreColumns.get(key));

        }
        LOGGER.debug("getWMSTest: finished");
        Assert.assertEquals(scoreColumns.size(), 6);
        Assert.assertNotNull(wms.getServerURL());
        Assert.assertTrue(scoreColumns.containsKey("score_residential"));
        Assert.assertEquals(scoreColumns.get("score_residential"), new Integer(
            15000));
      }

      /**
       * Delete suitability scenario test.
       *
       * @throws Exception
       *           the exception
       */
      @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "getSuitabilityScenarioWithProjectIdTest" }, groups = {
          "setup", "service", "suitability" }, expectedExceptions = {
              InvalidEntityIdException.class, BadSqlGrammarException.class })
      public void deleteSuitabilityScenarioTest() throws Exception {
        System.out
        .println("delete suitabilityScenario test, suitabilityScenario id: "
            + testID);
        projectService.deleteProject(projectId);
        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(testID, projectId);
      }

      @Test(enabled = false)
      public void duplicateSuitabilityScenarioTest() throws Exception {


        final String projectid= "DemonstrationTestID";

        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
        suitabilityScenario.setLabel(suitabilityScenario.getLabel()+"new");

        project = projectService
            .getProjectConfiguration(suitabilityScenario.getProjectId());


        final SuitabilityScenario restoredSuitabilityScenario = suitabilityScenarioService
            .restoreSuitabilityScenario(suitabilityScenario, project);
        project.getSuitabilityScenariosMap().put(
            restoredSuitabilityScenario.getId(),
            restoredSuitabilityScenario.getLabel());


        wifProjectDao.updateProject(project);

        Assert.assertNotNull(suitabilityScenario.getId());
        Assert.assertTrue(suitabilityScenario.getReady());
      }


      @Test(enabled = true)
      public void importSuitabilityScenariofactorTest() throws Exception {


        final String projectid= "DemonstrationTestID";
        //final String projectid= "d2ee05b32886f857ef1dd727220ffcfe";


        try
        {
          final URL xlURL = getClass().getResource("/xfact.xls");
          final File xlFile = new File(xlURL.getFile());

          //Create Workbook instance holding reference to .xlsx file
          final FileInputStream file = new FileInputStream(xlFile);


          final CSVReader reader = new CSVReader(new FileReader(xlFile.getAbsolutePath()));
          String [] nextLine;
          while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            // System.out.println(nextLine[0]);
          }


          //          final XSSFWorkbook workbook = new XSSFWorkbook(file); //Read the Excel Workbook in a instance object
          //          final XSSFSheet sheet = workbook.getSheetAt(0);
          //          XSSFRow row6;
          //          XSSFRow row;
          //          XSSFCell cell;
          //          XSSFCell factorName;


          final HSSFWorkbook workbook = new HSSFWorkbook(file);
          final HSSFSheet sheet = workbook.getSheetAt(0);
          HSSFRow row6;
          HSSFRow row;
          HSSFCell cell;
          HSSFCell factorName;



          logger.info("hii");

          final Map<String, Double> map = new HashMap<String, Double>();


          int rows; // No of rows
          rows = sheet.getPhysicalNumberOfRows();

          int cols = 0; // No of columns
          int tmp = 0;
          // This trick ensures that we get the data properly even if it doesn't start from first few rows
          for(int i = 6;  i < rows; i++) {
            row = sheet.getRow(i);
            if(row != null) {
              tmp = sheet.getRow(i).getPhysicalNumberOfCells();
              if(tmp > cols) {
                cols = tmp;
              }
            }
          }


          for(int c = 1; c < cols; c++) {


            row6 = sheet.getRow(6);
            cell = row6.getCell(c);

            final String headerLU = cell.getStringCellValue();
            logger.info(headerLU);



            logger.info("********");

            String lbl="";
            String lbluse="";
            for(int r = 8; r < rows; r++) {

              Boolean lswHeader = false;

              row = sheet.getRow(r);
              if(row != null)
              {

                cell = row.getCell((short)c);
                factorName = row.getCell(0);
                lbluse= factorName.getStringCellValue();
                if (!factorName.getStringCellValue().equals(lbl))
                {
                  if (lbl.equals(""))
                  {
                    lbl =  factorName.getStringCellValue();
                    lbluse= "Header:" + lbl;
                    lswHeader= true;
                  }
                  else
                  {
                    if (factorName.getStringCellValue().length() <= lbl.length())
                    {
                      lbl =  factorName.getStringCellValue();
                      lbluse= "Header:" + lbl;
                      lswHeader= true;
                    }
                    else
                    {
                      if (factorName.getStringCellValue().substring(0, lbl.length()).equals(lbl))
                      {
                        int k = lbl.length();
                        final String str = factorName.getStringCellValue();
                        if (str.substring(k, k+1).equals("_"))
                        {
                          k = factorName.getStringCellValue().length();

                          String lname=str.substring(lbl.length()+1,k);
                          lname = lname.replaceAll(",", "");
                          lname = lname.replaceAll("Greater ", ">");
                          lname = lname.replaceAll("Lower ", "<");

                          //lbluse= "Header:" + lbl + ":Child:" +str.substring(lbl.length()+1,k);
                          lbluse= "Header:" + lbl + ":Child:" +lname;
                          lswHeader= false;

                          Double newvalue=0.0;
                          if (cell.getCellType() == 1)
                          {

                            newvalue= Double.parseDouble(cell.getStringCellValue());
                          }
                          else if (cell.getCellType() == 0)
                          {

                            newvalue= cell.getNumericCellValue();
                          }


                        }
                        else
                        {
                          lbl =  factorName.getStringCellValue();
                          lbluse= "Header:" + lbl;
                          lswHeader= true;
                        }
                      }
                      else
                      {
                        lbl =  factorName.getStringCellValue();
                        lbluse= "Header:" + lbl;
                        lswHeader= true;
                      }
                    }
                  }
                }//end for

                if (lswHeader== true)
                {

                }
                else
                {

                }


                if(cell != null) {
                  // Your code here
                  //logger.info(lbluse + " : "+cell.getCellType());
                  if (cell.getCellType() == 1)
                  {

                    logger.info(headerLU+":"+lbluse + ":"+cell.getStringCellValue());
                    map.put(headerLU+":"+lbluse, Double.valueOf(cell.getStringCellValue()));
                  }
                  else
                  {
                    logger.info(headerLU+":"+lbluse + ":"+cell.getNumericCellValue());
                    map.put(headerLU+":"+lbluse, cell.getNumericCellValue());
                  }
                }

              }
            }
          }//end for c


          for (final Map.Entry<String, Double> entry : map.entrySet()) {
            LOGGER.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
            //LOGGER.info("Key : " + entry.getKey().split(":").length);
          }

          //////////////////////////////////////////////////////////////////////////////

          final SuitabilityScenario oldSuitabilityScenario = suitabilityScenarioService
              .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
          //          final SuitabilityScenario oldSuitabilityScenario = suitabilityScenarioService
          //              .getSuitabilityScenario("d2ee05b32886f857ef1dd7272211b38d");


          project = projectService
              .getProjectConfiguration(oldSuitabilityScenario.getProjectId());

          final Set<SuitabilityRule> setRules =  new HashSet<SuitabilityRule>();
          final Set<SuitabilityRule> suitabilityRules = oldSuitabilityScenario
              .getSuitabilityRules();
          for (final SuitabilityRule oldRule : suitabilityRules) {

            final SuitabilityRule newRule = new SuitabilityRule();

            newRule.setId(oldRule.getId());
            final String suitabilityLULabel = oldRule.getSuitabilityLUMap().values()
                .iterator().next();
            // LOGGER.debug("Restoring {} suitabilityLU...", suitabilityLULabel);
            final SuitabilityLU suitabilityLU = project
                .getSuitabilityLUByName(suitabilityLULabel);
            newRule.getSuitabilityLUMap().put(suitabilityLU.getId(),
                suitabilityLU.getLabel());
            final Collection<String> convertibleLUsLabels = oldRule.getConvertibleLUsMap()
                .values();
            for (final String luLabel : convertibleLUsLabels) {
              //LOGGER.debug("Restoring {} convertibleLU...", luLabel);
              final AllocationLU allocationLU = project
                  .getExistingLandUseByLabel(luLabel);
              newRule.getConvertibleLUsMap().put(allocationLU.getId(),
                  allocationLU.getLabel());
            }
            final Set<FactorImportance> factorImportances = oldRule.getFactorImportances();
            for (final FactorImportance oldImportance : factorImportances) {
              final Double newvalue=oldImportance.getImportance();

              final FactorImportance importance = new FactorImportance();

              final String factorLabel = oldImportance.getFactorMap().values().iterator()
                  .next();
              //LOGGER.debug("Restoring factorImportance for {}...", factorLabel);
              final Factor factor = project.getFactorByLabel(factorLabel);
              importance.getFactorMap().put(factor.getId(), factor.getLabel());

              for (final Map.Entry<String, Double> entry : map.entrySet()) {
                //
                //System.out.println("Key : " + entry.getKey().split(":").length);
                if (entry.getKey().split(":").length==3)
                {
                  final String[] str = entry.getKey().split(":");
                  if (str[0].equals(suitabilityLULabel))
                  {
                    if (str[2].equals(factorLabel))
                    {
                      //importance.setImportance(2.0);
                      importance.setImportance(entry.getValue());
                      //LOGGER.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
                    }
                  }
                }
              }

              final Set<FactorTypeRating> factorTypeRatings = oldImportance
                  .getFactorTypeRatings();
              for (final FactorTypeRating oldRating : factorTypeRatings) {
                final FactorTypeRating rating = new FactorTypeRating();
                final String ftLabel = oldRating.getFactorTypeMap().values().iterator()
                    .next();

                for (final Map.Entry<String, Double> entry : map.entrySet()) {
                  if (entry.getKey().split(":").length==5)
                  {
                    final String[] str = entry.getKey().split(":");
                    if (str[0].equals(suitabilityLULabel))
                    {
                      if (str[2].equals(factorLabel))
                      {

                        if (str[4].equals(ftLabel))
                        {
                          //rating.setScore(2.0);
                          rating.setScore(entry.getValue());
                          //LOGGER.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
                          if (suitabilityLULabel.equals("Retail"))
                          {
                            if (factorLabel.equals("streams"))
                            {
                              int cc=0;
                              cc=2;
                              if (ftLabel.equals("Outside buffers"))
                              {

                              }
                            }
                          }

                          if (suitabilityLULabel.equals("Retail") && factorLabel.equals("streams") && ftLabel.equals("Outside buffers"))
                          {
                            logger.info("YYEEEEESSSS: " + entry.getValue());
                            //Retail:Header:streams:Child:Outside buffers Value : 100.0
                          }


                        }

                      }
                    }
                  }
                }

                final FactorType factorType = factor.getFactorTypeByLabel(ftLabel);
                rating.getFactorTypeMap().put(factorType.getId(),
                    factorType.getLabel());
                importance.getFactorTypeRatings().add(rating);
              }
              newRule.getFactorImportances().add(importance);


              newRule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
                  oldRule.getId()).getRevision());
              LOGGER.info("updating the newrule with Rev={}", newRule.getRevision());
              suitabilityRuleDao.updateSuitabilityRule(newRule);

              newRule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
                  oldRule.getId()).getRevision());
              setRules.add(newRule);

            }

          }

          oldSuitabilityScenario.setSuitabilityRules(setRules);
          oldSuitabilityScenario.setRevision(suitabilityScenarioDao
              .findSuitabilityScenarioById(oldSuitabilityScenario.getId())
              .getRevision());
          LOGGER.info("updating the suitabilityScenario with Rev={}",
              oldSuitabilityScenario.getRevision());
          suitabilityScenarioService.updateSuitabilityScenario(oldSuitabilityScenario,
              projectId);

          //////////////////////////////////////////////////////////////////////////////

          file.close();
        }
        catch (final Exception e)
        {
          e.printStackTrace();
        }



      }
}
