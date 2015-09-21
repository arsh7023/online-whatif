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


      @Test(enabled = false)
      public void importSuitabilityScenariofactorTest() throws Exception {


        final String projectid= "DemonstrationTestID";



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

          final SuitabilityScenario oldSuitabilityScenario = suitabilityScenarioService
              .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);


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
                    lbluse= "Header: " + lbl;
                    lswHeader= true;
                  }
                  else
                  {
                    if (factorName.getStringCellValue().length() <= lbl.length())
                    {
                      lbl =  factorName.getStringCellValue();
                      lbluse= "Header: " + lbl;
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
                          lbluse= "Child:  " +str.substring(lbl.length()+1,k);
                          lswHeader= false;
                          for (final SuitabilityRule sRule: oldSuitabilityScenario.getSuitabilityRules())
                          {
                            if (sRule.getSuitabilityLU().getLabel().equals(headerLU))
                            {

                              final Set<FactorImportance> setfImp = new HashSet<FactorImportance>();
                              for ( final FactorImportance   fImp :sRule.getFactorImportances())
                              {

                                if (fImp.getFactor().getLabel().equals(lbl)) {

                                  final Set<FactorTypeRating> setfRate = new HashSet<FactorTypeRating>();
                                  for (final FactorTypeRating ftr : fImp.getFactorTypeRatings())
                                  {
                                    if (ftr.getFactorType().getLabel().equals(str.substring(lbl.length()+1,k)))
                                    {

                                      ftr.setScore(Double.parseDouble(cell.getStringCellValue()));
                                      logger.info("CCCCC" + ftr.getFactorType().getLabel());
                                    }
                                    setfRate.add(ftr);
                                  }
                                  fImp.setFactorTypeRatings(setfRate);
                                  setfImp.add(fImp);
                                }


                                sRule.setFactorImportances(setfImp);
                                //                                sRule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
                                //                                    sRule.getId()).getRevision());
                                //suitabilityRuleDao.updateSuitabilityRule(sRule);


                              }
                            }
                          }

                        }
                        else
                        {
                          lbl =  factorName.getStringCellValue();
                          lbluse= "Header: " + lbl;
                          lswHeader= true;
                        }
                      }
                      else
                      {
                        lbl =  factorName.getStringCellValue();
                        lbluse= "Header: " + lbl;
                        lswHeader= true;
                      }
                    }
                  }
                }//end for

                if (lswHeader== true)
                {

                  //////////////////////

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
                      Double newvalue=oldImportance.getImportance();
                      if (oldRule.getSuitabilityLU().getLabel().equals(headerLU))
                      {
                        if (oldImportance.getFactor().getLabel().equals(lbluse.substring(8, lbluse.length()))) {
                          if (cell.getCellType() == 1)
                          {

                            newvalue= Double.parseDouble(cell.getStringCellValue());
                          }
                          else if (cell.getCellType() == 0)
                          {

                            newvalue= cell.getNumericCellValue();
                          }
                        }
                        final FactorImportance importance = new FactorImportance();
                        importance.setImportance(newvalue);
                        final String factorLabel = oldImportance.getFactorMap().values().iterator()
                            .next();
                        LOGGER.debug("Restoring factorImportance for {}...", factorLabel);
                        final Factor factor = project.getFactorByLabel(factorLabel);
                        importance.getFactorMap().put(factor.getId(), factor.getLabel());

                        final Set<FactorTypeRating> factorTypeRatings = oldImportance
                            .getFactorTypeRatings();
                        for (final FactorTypeRating oldRating : factorTypeRatings) {
                          final FactorTypeRating rating = new FactorTypeRating();
                          final String ftLabel = oldRating.getFactorTypeMap().values().iterator()
                              .next();
                          rating.setScore(oldRating.getScore());
                          LOGGER.debug(
                              "Restoring factor type importance for {} with score {}...",
                              ftLabel, rating.getScore());
                          final FactorType factorType = factor.getFactorTypeByLabel(ftLabel);
                          rating.getFactorTypeMap().put(factorType.getId(),
                              factorType.getLabel());
                          importance.getFactorTypeRatings().add(rating);
                        }
                        newRule.getFactorImportances().add(importance);

                      }
                    }
                    setRules.add(newRule);
                  }//end for
                  ////////////////////////////////////////////////////////////////////////////////////
                  final SuitabilityScenario restoreSuitabilityScenario = new SuitabilityScenario();
                  restoreSuitabilityScenario.setLabel(oldSuitabilityScenario.getLabel());
                  restoreSuitabilityScenario.setFeatureFieldName(oldSuitabilityScenario
                      .getFeatureFieldName());
                  restoreSuitabilityScenario.setProjectId(project.getId());
                  restoreSuitabilityScenario.setId(oldSuitabilityScenario.getId());
                  restoreSuitabilityScenario.setSuitabilityRules(setRules);
                  suitabilityScenarioService.updateSuitabilityScenario(restoreSuitabilityScenario,
                      projectId);



                  for (final SuitabilityRule oldsRule: oldSuitabilityScenario.getSuitabilityRules())
                  {
                    final SuitabilityRule sRuleNew = new SuitabilityRule();
                    sRuleNew.setSuitabilityLU(oldsRule.getSuitabilityLU());
                    if (oldsRule.getSuitabilityLU().getLabel().equals(headerLU))
                    {
                      final Set<FactorImportance> setfImp = new HashSet<FactorImportance>();
                      //logger.info("YES");

                      for ( final FactorImportance   oldImp :oldsRule.getFactorImportances())
                      {
                        //logger.info("HHHHH" + lbluse.substring(8, lbluse.length()));
                        final FactorImportance newImp = new FactorImportance();
                        newImp.setFactor(oldImp.getFactor());
                        newImp.setImportance(oldImp.getImportance());
                        newImp.setFactorTypeRatings(oldImp.getFactorTypeRatings());
                        newImp.setFactorMap(oldImp.getFactorMap());
                        if (oldImp.getFactor().getLabel().equals(lbluse.substring(8, lbluse.length()))) {

                          if (cell.getCellType() == 1)
                          {


                            logger.info("HHHHHH" + oldImp.getFactor().getLabel() + "==" + Double.parseDouble(cell.getStringCellValue()));
                            oldImp.setImportance(Double.parseDouble(cell.getStringCellValue()));
                            newImp.setImportance(Double.parseDouble(cell.getStringCellValue()));
                          }
                          else if (cell.getCellType() == 0)
                          {

                            logger.info("HHHHHH" + oldImp.getFactor().getLabel() + "==" + cell.getNumericCellValue());
                            oldImp.setImportance(cell.getNumericCellValue());
                            newImp.setImportance(cell.getNumericCellValue());
                          }

                        }
                        setfImp.add(newImp);

                      }
                      oldsRule.setFactorImportances(setfImp);
                      //                      sRule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
                      //                          sRule.getId()).getRevision());
                      //                      suitabilityRuleDao.updateSuitabilityRule(sRule);

                      for ( final FactorImportance   fImp :oldsRule.getFactorImportances())
                      {
                        //logger.info("GGGGGG" + fImp.getFactor().getLabel() + "==" +fImp.getImportance());
                      }

                    }

                  }//end for

                  // oldSuitabilityScenario.setSuitabilityRules(setRules);
                }
                else
                {

                }


                if(cell != null) {
                  // Your code here
                  logger.info(lbluse + " : "+cell.getCellType());
                  if (cell.getCellType() == 1)
                  {

                    logger.info(lbluse + " : "+cell.getStringCellValue());
                  }
                  else
                  {
                    logger.info(lbluse + " : "+cell.getNumericCellValue());
                  }
                }

              }
            }
          }

          //SuitabilityScenario newSuit = suitabilityScenario;

          //          suitabilityScenario.setRevision(suitabilityScenarioDao
          //              .findSuitabilityScenarioById(suitabilityScenario.getId())
          //              .getRevision());
          //          suitabilityScenarioDao.updateSuitabilityScenario(suitabilityScenario);
          //
          //          suitabilityScenarioService.updateSuitabilityScenario(oldSuitabilityScenario,
          //              projectId);






          //Iterate through each rows one by one
          //          final Iterator<Row> rowIterator = sheet.iterator();
          //          while (rowIterator.hasNext())
          //          {
          //            final Row row = rowIterator.next();
          //            //For each row, iterate through all the columns
          //            final Iterator<Cell> cellIterator = row.cellIterator();
          //
          //            while (cellIterator.hasNext())
          //            {
          //              final Cell cell = cellIterator.next();
          //              //Check the cell type and format accordingly
          //              switch (cell.getCellType())
          //              {
          //                case Cell.CELL_TYPE_NUMERIC:
          //                  System.out.print(cell.getNumericCellValue() + "nu");
          //                  break;
          //                case Cell.CELL_TYPE_STRING:
          //                  System.out.print(cell.getStringCellValue() + "st");
          //                  break;
          //              }
          //            }
          //            System.out.println("");

          //}
          file.close();
        }
        catch (final Exception e)
        {
          e.printStackTrace();
        }




        //        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        //            .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
        //        suitabilityScenario.setLabel(suitabilityScenario.getLabel()+"new");
        //
        //        project = projectService
        //            .getProjectConfiguration(suitabilityScenario.getProjectId());
        //
        //
        //        final SuitabilityScenario restoredSuitabilityScenario = suitabilityScenarioService
        //            .restoreSuitabilityScenario(suitabilityScenario, project);
        //        project.getSuitabilityScenariosMap().put(
        //            restoredSuitabilityScenario.getId(),
        //            restoredSuitabilityScenario.getLabel());
        //
        //
        //        wifProjectDao.updateProject(project);
        //
        //        Assert.assertNotNull(suitabilityScenario.getId());
        //        Assert.assertTrue(suitabilityScenario.getReady());
      }//


}
