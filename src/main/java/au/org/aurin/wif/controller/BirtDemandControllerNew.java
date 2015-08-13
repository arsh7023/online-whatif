package au.org.aurin.wif.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.BirtReport;
import au.org.aurin.wif.model.reports.suitability.CategoryItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class BirtDemandControllerNew {

  /**
   * The Class BirtDemandControllerNew for generating demand reports using Birt.
   */

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  @Autowired
  private SuitabilityAnalysisReport SuitabilityAnalysisReport;

  private @Autowired
  ApplicationContext servletContext;

  private @Autowired
  BirtReport BirtReport;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(BirtDemandControllerNew.class);

  /**
   * Generates demand scenario html report
   * 
   * @param locale
   * @param model
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   * @throws IncompleteDemandScenarioException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios/{id}/htmlOutcome")
  public @ResponseBody
  String htmlDemandOutcome(final Locale locale, final Model model,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException,
      IncompleteDemandScenarioException {

    LOGGER.info("Welcome Birt Report!");
    LOGGER
        .info("*******>> getBirt Html Report for Demand Scenario  id ={}", id);

    // SuitabilityScenario suitabilityScenario = suitabilityScenarioService
    // .getSuitabilityScenario("59c979a3ed6fdab3ecf88de7260f3182");

    final DemandScenario demandScenario = demandScenarioService
        .getDemandScenario(id);

    final WifProject project = demandScenario.getWifProject();

    BirtReport = new BirtReport();
    BirtReport.setProjectName(project.getLabel());
    BirtReport.setScenarioName(demandScenario.getLabel());

    final String demandConfigId = project.getDemandConfigId();
    final DemandConfig demandConfig = demandConfigDao
        .findDemandConfigById(demandConfigId);

    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final Projection projection : demandConfig.getProjections()) {

    }// end for projection
    final List<AreaRequirement> areaRequirements = demandScenarioService
        .getOutcome(id);

    for (final AreaRequirement areaRequirement : areaRequirements) {

      // if (areaRequirement.getProjectionLabel()
      // .equals(projection.getLabel())) {
      str.append("<list>");
      str.append("<value>");
      str.append(areaRequirement.getAllocationLULabel());
      str.append("</value>");
      str.append("<value>");
      str.append(areaRequirement.getRequiredArea());
      str.append("</value>");
      str.append("<value>");
      str.append(areaRequirement.getProjectionLabel());
      str.append("</value>");
      str.append("</list>");
    }

    str = str.append("</list>");
    str = str.append("</property>");

    //
    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/demand.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/demand.xml");

    final BufferedWriter bufferWritterxml = new BufferedWriter(fwxml);

    String strxml = "<?xml version='1.0' encoding='UTF-8'?>\n"
        + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
        + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:tx='http://www.springframework.org/schema/tx'\n"
        + "xmlns:context='http://www.springframework.org/schema/context'\n"
        + "xmlns:task='http://www.springframework.org/schema/task'\n"
        + "xsi:schemaLocation='\n"
        + "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\n"
        + "http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-3.0.xsd\n"
        + "http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd\n"
        + "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd'>\n";
    // + "<context:component-scan base-package='au.org.aurin.wif' />\n";
    bufferWritterxml.write(strxml);

    strxml = "<bean id='BirtReport' class='au.org.aurin.wif.model.reports.BirtReport'>"
        + "<property name='scenarioName' value='"
        + BirtReport.getScenarioName()
        + "' />"
        + "<property name='projectName' value='"
        + BirtReport.getProjectName()
        + "' />" + str + "</bean>\n</beans>";

    bufferWritterxml.write(strxml);
    bufferWritterxml.close();
    fwxml.close();
    final ApplicationContext context = new FileSystemXmlApplicationContext("/"
        + filexml.getPath());

    // ///////////////birt

    final StringBuilder mystrnew = new StringBuilder();
    String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");
      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass().getResource("/demand.rptdesign"); // SpringSampleBIRTViewer.rptdesign
      // TopNPercent.rptdesign
      // System.out.println(peopleresource.getFile());

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);
      // runAndRenderTask.setParameterValues(discoverAndSetParameters(runnable,
      // request));
      final HTMLRenderOption htmlOptions = new HTMLRenderOption();
      htmlOptions.setOutputFormat("html");

      final File file = new File(tempDir + "/demand.html");
      if (!file.exists()) {
        file.createNewFile();
      }

      htmlOptions.setOutputFileName(tempDir + "/demand.html");
      // htmlOptions.setOutputFileName("alin.html");
      // // Setting this to true removes html and body tags
      htmlOptions.setEmbeddable(true);

      String mystrStart = "<html><body>";
      mystrStart = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>";
      mystrStart = mystrStart + "<html xmlns='http://www.w3.org/1999/xhtml'>";
      mystrStart = mystrStart + "<head>";
      mystrStart = mystrStart
          + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head>";

      final String mystrEnd = "</body></html>";

      mystr = mystrStart + mystr;
      runAndRenderTask.setRenderOption(htmlOptions);

      runAndRenderTask.run();
      runAndRenderTask.close();

      // File file = new File("alin.html");
      FileInputStream fis = null;

      final FileWriter fw = new FileWriter(tempDir + "/demand2.html");
      final BufferedWriter bufferWritter = new BufferedWriter(fw);
      bufferWritter.write(mystrStart);

      fis = new FileInputStream(file);
      // //System.out.println("Total file size to read (in bytes) : "
      // + fis.available());

      mystrnew.append(mystr);
      int content;
      while ((content = fis.read()) != -1) {
        // convert to char and display it
        bufferWritter.write((char) content);

        // mystr = mystr + (char) content;
        mystrnew.append((char) content);
      }

      bufferWritter.write(mystrEnd);
      mystr = mystr + mystrEnd;
      mystrnew.append(mystrEnd);
      bufferWritter.close();

      file.delete();
      final File file1 = new File(tempDir + "/demand2.html");
      file1.delete();
      filexml.delete();

      LOGGER.info(
          "*******>> Completed Birt Demand Report for demand Scenario  id ={}", id);
    } catch (final Exception e) {
      LOGGER.debug("getBirt Demand Report error : ={}", e.toString());
    } finally {
      birtEngine.destroy();
    }

    // ///////////////

    return mystrnew.toString();

  }

  /**
   * Generates demand scenario pdf report
   * 
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
 * @throws IncompleteDemandScenarioException 
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios/{id}/pdfOutcome")
  @ResponseBody
  public byte[] getPDFDemandOutcome(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException, IncompleteDemandScenarioException {

    byte[] bytem = null;
    LOGGER.info(
        "*******>> getBirt Demand Report pdf for demand Scenario  id ={}", id);


    final DemandScenario demandScenario = demandScenarioService
        .getDemandScenario(id);

    final WifProject project = demandScenario.getWifProject();

    BirtReport = new BirtReport();
    BirtReport.setProjectName(project.getLabel());
    BirtReport.setScenarioName(demandScenario.getLabel());

    final String demandConfigId = project.getDemandConfigId();
    final DemandConfig demandConfig = demandConfigDao
        .findDemandConfigById(demandConfigId);

    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final Projection projection : demandConfig.getProjections()) {

    }// end for projection
    final List<AreaRequirement> areaRequirements = demandScenarioService
        .getOutcome(id);

    for (final AreaRequirement areaRequirement : areaRequirements) {

      // if (areaRequirement.getProjectionLabel()
      // .equals(projection.getLabel())) {
      str.append("<list>");
      str.append("<value>");
      str.append(areaRequirement.getAllocationLULabel());
      str.append("</value>");
      str.append("<value>");
      str.append(areaRequirement.getRequiredArea());
      str.append("</value>");
      str.append("<value>");
      str.append(areaRequirement.getProjectionLabel());
      str.append("</value>");
      str.append("</list>");
    }

    str = str.append("</list>");
    str = str.append("</property>");

    //
    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/demand.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/demand.xml");

    final BufferedWriter bufferWritterxml = new BufferedWriter(fwxml);

    String strxml = "<?xml version='1.0' encoding='UTF-8'?>\n"
        + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
        + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:tx='http://www.springframework.org/schema/tx'\n"
        + "xmlns:context='http://www.springframework.org/schema/context'\n"
        + "xmlns:task='http://www.springframework.org/schema/task'\n"
        + "xsi:schemaLocation='\n"
        + "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\n"
        + "http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-3.0.xsd\n"
        + "http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd\n"
        + "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd'>\n";
    // + "<context:component-scan base-package='au.org.aurin.wif' />\n";
    bufferWritterxml.write(strxml);

    strxml = "<bean id='BirtReport' class='au.org.aurin.wif.model.reports.BirtReport'>"
        + "<property name='scenarioName' value='"
        + BirtReport.getScenarioName()
        + "' />"
        + "<property name='projectName' value='"
        + BirtReport.getProjectName()
        + "' />" + str + "</bean>\n</beans>";

    bufferWritterxml.write(strxml);
    bufferWritterxml.close();
    fwxml.close();
    final ApplicationContext context = new FileSystemXmlApplicationContext("/"
        + filexml.getPath());

    // ///////////////birt
    // String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");
  
      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass().getResource(
          "/demand.rptdesign"); // SpringSampleBIRTViewer.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/pdemand.pdf");
      if (!file.exists()) {
        file.createNewFile();
      }

      final PDFRenderOption pdfOptions = new PDFRenderOption();
      pdfOptions.setOutputFormat("pdf");
      pdfOptions.setOutputFileName(tempDir + "/pdemand.pdf");

      pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW,
          IPDFRenderOption.FIT_TO_PAGE);
      runAndRenderTask.setRenderOption(pdfOptions);

      runAndRenderTask.run();
      runAndRenderTask.close();
      filexml.delete();

      bytem = org.springframework.util.FileCopyUtils.copyToByteArray(file);

      response.setHeader("Content-Disposition", "attachment; filename=\""
          + file.getName() + "\"");
      response.setContentLength(bytem.length);
      response.setContentType("application/pdf");

      file.delete();
      LOGGER
          .info(
              "*******>> Completed Birt Demand Report pdf for demand Scenario  id ={}",
              id);

    } catch (final Exception e) {
      LOGGER.debug("getBirtDemand pdf Report error : ={}", e.toString());
    } finally {
      birtEngine.destroy();
    }
    return bytem;
    // /////////////////////////////////////////////////////////////////

  }

  /**
   * Generates demand scenario xls report
   * 
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
 * @throws IncompleteDemandScenarioException 
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios/{id}/xlsOutcome")
  @ResponseBody
  public byte[] getXLSDemandOutcome(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException, IncompleteDemandScenarioException {
	  
	    byte[] bytem = null;
	    LOGGER.info(
	        "*******>> getBirt Demand Report xls for demand Scenario  id ={}", id);


	    final DemandScenario demandScenario = demandScenarioService
	        .getDemandScenario(id);

	    final WifProject project = demandScenario.getWifProject();

	    BirtReport = new BirtReport();
	    BirtReport.setProjectName(project.getLabel());
	    BirtReport.setScenarioName(demandScenario.getLabel());

	    final String demandConfigId = project.getDemandConfigId();
	    final DemandConfig demandConfig = demandConfigDao
	        .findDemandConfigById(demandConfigId);

	    StringBuilder str = new StringBuilder("<property name='data'><list>");

	    for (final Projection projection : demandConfig.getProjections()) {

	    }// end for projection
	    final List<AreaRequirement> areaRequirements = demandScenarioService
	        .getOutcome(id);

	    for (final AreaRequirement areaRequirement : areaRequirements) {

	      // if (areaRequirement.getProjectionLabel()
	      // .equals(projection.getLabel())) {
	      str.append("<list>");
	      str.append("<value>");
	      str.append(areaRequirement.getAllocationLULabel());
	      str.append("</value>");
	      str.append("<value>");
	      str.append(areaRequirement.getRequiredArea());
	      str.append("</value>");
	      str.append("<value>");
	      str.append(areaRequirement.getProjectionLabel());
	      str.append("</value>");
	      str.append("</list>");
	    }

	    str = str.append("</list>");
	    str = str.append("</property>");

	    //
	    final String tempDir = System.getProperty("java.io.tmpdir");
	    final File filexml = new File(tempDir + "/demand.xml");
	    if (!filexml.exists()) {
	      filexml.createNewFile();
	    }
	    final FileWriter fwxml = new FileWriter(tempDir + "/demand.xml");

	    final BufferedWriter bufferWritterxml = new BufferedWriter(fwxml);

	    String strxml = "<?xml version='1.0' encoding='UTF-8'?>\n"
	        + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
	        + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:tx='http://www.springframework.org/schema/tx'\n"
	        + "xmlns:context='http://www.springframework.org/schema/context'\n"
	        + "xmlns:task='http://www.springframework.org/schema/task'\n"
	        + "xsi:schemaLocation='\n"
	        + "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\n"
	        + "http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-3.0.xsd\n"
	        + "http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd\n"
	        + "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd'>\n";
	    // + "<context:component-scan base-package='au.org.aurin.wif' />\n";
	    bufferWritterxml.write(strxml);

	    strxml = "<bean id='BirtReport' class='au.org.aurin.wif.model.reports.BirtReport'>"
	        + "<property name='scenarioName' value='"
	        + BirtReport.getScenarioName()
	        + "' />"
	        + "<property name='projectName' value='"
	        + BirtReport.getProjectName()
	        + "' />" + str + "</bean>\n</beans>";

	    bufferWritterxml.write(strxml);
	    bufferWritterxml.close();
	    fwxml.close();
	    final ApplicationContext context = new FileSystemXmlApplicationContext("/"
	        + filexml.getPath());

	    // ///////////////birt
	    // String mystr = "";
	    IReportEngine birtEngine = null;
	    try {
	      final EngineConfig config = new EngineConfig();

	      // System.out.println(servletContext.getBean("BirtReport").toString());
	      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
	          .getBean("BirtReport");
	  
	      config.getAppContext().put(
	          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

	      Platform.startup(config);
	      final IReportEngineFactory factory = (IReportEngineFactory) Platform
	          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
	      birtEngine = factory.createReportEngine(config);

	      final URL peopleresource = getClass().getResource(
	          "/demand.rptdesign"); // SpringSampleBIRTViewer.rptdesign

	      IReportRunnable runnable = null;
	      runnable = birtEngine.openReportDesign(peopleresource.getFile());
	      final IRunAndRenderTask runAndRenderTask = birtEngine
	          .createRunAndRenderTask(runnable);

	      final File file = new File(tempDir + "/xdemand.xls");
	      if (!file.exists()) {
	        file.createNewFile();
	      }

	      final PDFRenderOption pdfOptions = new PDFRenderOption();
	      pdfOptions.setOutputFormat("xls");
	      pdfOptions.setOutputFileName(tempDir + "/xdemand.xls");

	      pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW,
	          IPDFRenderOption.FIT_TO_PAGE);
	      runAndRenderTask.setRenderOption(pdfOptions);

	      runAndRenderTask.run();
	      runAndRenderTask.close();
	      filexml.delete();

	      bytem = org.springframework.util.FileCopyUtils.copyToByteArray(file);

	      response.setHeader("Content-Disposition", "attachment; filename=\""
	          + file.getName() + "\"");
	      response.setContentLength(bytem.length);
	      response.setContentType("application/xls");

	      file.delete();
	      LOGGER
	          .info(
	              "*******>> Completed Birt Demand Report xls for demand Scenario  id ={}",
	              id);

	    } catch (final Exception e) {
	      LOGGER.debug("getBirtDemand xls Report error : ={}", e.toString());
	    } finally {
	      birtEngine.destroy();
	    }
	    return bytem;
	    // /////////////////////////////////////////////////////////////////

  }

}
