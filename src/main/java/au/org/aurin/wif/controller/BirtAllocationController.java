package au.org.aurin.wif.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

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
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.BirtReport;
import au.org.aurin.wif.model.reports.allocation.AllocationSimpleAnalysisReport;
import au.org.aurin.wif.model.reports.allocation.AllocationSimpleItemReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class BirtAllocationController {

  /**
   * The Class BirtAllocationController for generating allocation reports using
   * Birt.
   */

  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  @Autowired
  private AllocationSimpleAnalysisReport allocationSimpleAnalysisReport;

  private @Autowired
  ApplicationContext servletContext;

  private @Autowired
  BirtReport BirtReport;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The demand outcome service. */
  @Resource
  private DemandOutcomeService manualdemandScenarioService;

  @Resource
  private DemandConfigService demandConfigService;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(BirtAllocationController.class);


  /**
   * Generated allocation scenario html report
   *
   * @param locale
   * @param model
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/html")
  public @ResponseBody
  String htmlAllocation(final Locale locale, final Model model,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException, IOException {

    LOGGER.info("Welcome Birt Report!");
    LOGGER.info(
        "*******>> getBirt Html Report for Allocation Scenario  id ={}", id);

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    allocationSimpleAnalysisReport = new AllocationSimpleAnalysisReport();
    allocationSimpleAnalysisReport = reportService
        .getAllocationSimpleAnalysisReport(allocationScenario);

    BirtReport = new BirtReport();
    BirtReport.setProjectName(allocationSimpleAnalysisReport.getLabel());
    BirtReport.setScenarioName(allocationScenario.getLabel());
    final Set<AllocationSimpleItemReport> it = allocationSimpleAnalysisReport
        .getAllocationSimpleItemReport();

    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final AllocationSimpleItemReport s : it) {

      str.append("<list>");
      str.append("<value>");
      str.append(s.getLanduseName());
      str.append("</value>");
      str.append("<value>");
      str.append(s.getYear());
      str.append("</value>");
      str.append("<value>");
      str.append((double) Math.round(s.getSumofArea() * 100000) / 100000);
      str.append("</value>");
      str.append("</list>");

    }

    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/alloc.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/alloc.xml");

    // FileWriter fwxml = new FileWriter("/Users/ashamakhy/Documents/ali.xml");
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

      // ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass()
          .getResource("/allocation.rptdesign"); // allocation.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final HTMLRenderOption htmlOptions = new HTMLRenderOption();
      htmlOptions.setOutputFormat("html");

      final File file = new File(tempDir + "/alloc.html");
      if (!file.exists()) {
        file.createNewFile();
      }

      // htmlOptions.setOutputFileName("/Users/ashamakhy/Documents/alin.html");
      htmlOptions.setOutputFileName(tempDir + "/alloc.html");

      htmlOptions.setEmbeddable(true);

      final String mystrStart = "<html><body>";
      final String mystrEnd = "</body></html>";

      mystr = mystrStart + mystr;
      runAndRenderTask.setRenderOption(htmlOptions);

      runAndRenderTask.run();
      runAndRenderTask.close();

      // File file = new File("alin.html");
      FileInputStream fis = null;

      final FileWriter fw = new FileWriter(tempDir + "/alloc2.html");
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
      final File file1 = new File(tempDir + "/alloc2.html");
      file1.delete();
      filexml.delete();

      LOGGER
      .info(
          "*******>> Completed Birt Report for Allocation Scenario  id ={}",
          id);
    } catch (final Exception e) {
      LOGGER.debug("getBirt Report error : ={}", e.toString());
    } finally {
      birtEngine.destroy();
    }

    // ///////////////

    // return mystr;
    return mystrnew.toString();

  }

  /**
   * * Generated allocation scenario pdf report
   *
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/pdf")
  @ResponseBody
  public byte[] getPDFAllocation(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException, IOException {

    byte[] bytem = null;
    LOGGER.info("*******>> getBirt Report pdf for Allocation Scenario  id ={}",
        id);
    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    allocationSimpleAnalysisReport = new AllocationSimpleAnalysisReport();
    allocationSimpleAnalysisReport = reportService
        .getAllocationSimpleAnalysisReport(allocationScenario);

    BirtReport = new BirtReport();
    BirtReport.setProjectName(allocationSimpleAnalysisReport.getLabel());
    BirtReport.setScenarioName(allocationScenario.getLabel());
    final Set<AllocationSimpleItemReport> it = allocationSimpleAnalysisReport
        .getAllocationSimpleItemReport();

    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final AllocationSimpleItemReport s : it) {

      str.append("<list>");
      str.append("<value>");
      str.append(s.getLanduseName());
      str.append("</value>");
      str.append("<value>");
      str.append(s.getYear());
      str.append("</value>");
      str.append("<value>");
      str.append((double) Math.round(s.getSumofArea() * 100000) / 100000);
      str.append("</value>");
      str.append("</list>");

    }

    str = str.append("</list>");
    str = str.append("</property>");
    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/alloc.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/alloc.xml");

    // FileWriter fwxml = new FileWriter("/Users/ashamakhy/Documents/ali.xml");
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
    final String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");
      // System.out.println(cc.getScenarioName().toString());

      // System.out.println(cc.getData().toString());
      final ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass()
          .getResource("/allocation.rptdesign"); // allocation.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/palloc.pdf");
      if (!file.exists()) {
        file.createNewFile();
      }

      final PDFRenderOption pdfOptions = new PDFRenderOption();
      pdfOptions.setOutputFormat("pdf");
      pdfOptions.setOutputFileName(tempDir + "/palloc.pdf");

      pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW,
          IPDFRenderOption.FIT_TO_PAGE_SIZE);
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
          "*******>> Completed Birt Report pdf for Allocation Scenario  id ={}",
          id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt pdf Report error : ={}", e.toString());
    } finally {
      birtEngine.destroy();
    }
    return bytem;
    // /////////////////////////////////////////////////////////////////

  }

  /**
   * Generated allocation scenario xls report
   *
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/xls")
  @ResponseBody
  public byte[] getXLSAllocation(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException, IOException {

    byte[] bytem = null;

    LOGGER.info("*******>> getBirt Report xls for Allocation Scenario  id ={}",
        id);

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    allocationSimpleAnalysisReport = new AllocationSimpleAnalysisReport();
    allocationSimpleAnalysisReport = reportService
        .getAllocationSimpleAnalysisReport(allocationScenario);

    BirtReport = new BirtReport();
    BirtReport.setProjectName(allocationSimpleAnalysisReport.getLabel());
    BirtReport.setScenarioName(allocationScenario.getLabel());
    final Set<AllocationSimpleItemReport> it = allocationSimpleAnalysisReport
        .getAllocationSimpleItemReport();

    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final AllocationSimpleItemReport s : it) {

      str.append("<list>");
      str.append("<value>");
      str.append(s.getLanduseName());
      str.append("</value>");
      str.append("<value>");
      str.append(s.getYear());
      str.append("</value>");
      str.append("<value>");
      str.append((double) Math.round(s.getSumofArea() * 100000) / 100000);
      str.append("</value>");
      str.append("</list>");

    }

    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/alloc.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/alloc.xml");

    // FileWriter fwxml = new FileWriter("/Users/ashamakhy/Documents/ali.xml");
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
    final String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");

      final ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass()
          .getResource("/allocation.rptdesign"); // allocation.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/xalloc.xls");
      if (!file.exists()) {
        file.createNewFile();
      }

      final EXCELRenderOption xlsOptions = new EXCELRenderOption();
      xlsOptions.setOutputFormat("xls");
      xlsOptions.setOutputFileName(tempDir + "/xalloc.xls");

      xlsOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW,
          IPDFRenderOption.FIT_TO_PAGE_SIZE);
      // pdfOptions.setOutputStream(response.getOutputStream());
      runAndRenderTask.setRenderOption(xlsOptions);

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
          "*******>> Completed Birt Report xls for Allocation Scenario  id ={}",
          id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt xls Report error : ={}", e.toString());

    } finally {
      birtEngine.destroy();
    }
    return bytem;

  }

  /////////////////////////////////////////////////////////////////////

  /**
   * Generated allocation scenario pdf new report
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
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/pdfnew")
  @ResponseBody
  public byte[] getPDFAllocationNew(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException, IOException, IncompleteDemandScenarioException {

    byte[] bytem = null;

    LOGGER.info("*******>> getBirt Report pdf for Allocation Scenario  id ={}",
        id);

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    allocationSimpleAnalysisReport = new AllocationSimpleAnalysisReport();
    allocationSimpleAnalysisReport = reportService
        .getAllocationSimpleAnalysisReport(allocationScenario);

    BirtReport = new BirtReport();
    BirtReport.setProjectName(allocationSimpleAnalysisReport.getLabel());
    BirtReport.setScenarioName(allocationScenario.getLabel());
    final Set<AllocationSimpleItemReport> it = allocationSimpleAnalysisReport
        .getAllocationSimpleItemReport();

    ///////////////////////////////////////////

    DemandOutcome manualdemandScn;
    final WifProject project = allocationScenario.getWifProject();

    final String prjID = allocationScenario.getProjectId();

    LOGGER.info("projectId: " + prjID);
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(prjID);

    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(demandConfig.getProjections());
    final Projection current = projections.first();

    final String suitabilityScenarioId = allocationScenario
        .getSuitabilityScenarioId();
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);

    allocationScenario.setSuitabilityScenario(suitabilityScenario);
    final String projectId = suitabilityScenario.getWifProject().getId();

    Set<AreaRequirement> outcome = new HashSet<AreaRequirement>();
    if (allocationScenario.isManual()) {
      final String scenarioID = allocationScenario.getManualdemandScenarioId();

      final List<DemandScenario> listDemand = demandScenarioService
          .getDemandScenarios(projectId);
      Boolean lsw = false;
      for (final DemandScenario dsn : listDemand) {
        if (dsn.getId().equals(scenarioID)) {
          lsw = true;
        }
      }
      if (lsw == false) {
        manualdemandScn = allocationScenario.getManualdemandScenario();
        LOGGER.info("Manual Demand Scenario label: {}",
            manualdemandScn.getLabel());
        outcome = allocationScenario.getManualdemandScenario()
            .getAreaRequirements();
      } else {
        final List<AreaRequirement> outAreas = demandScenarioService
            .getOutcome(scenarioID);
        for (final AreaRequirement areaRequirement : outAreas) {
          areaRequirement.setProjectionLabel(areaRequirement.getProjection()
              .getLabel());
          outcome.add(areaRequirement);
        }
      }
    }




    ///////////////////////////////////////////


    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final AllocationSimpleItemReport s : it) {

      Double Accumulated_Happend = 0.0;
      Accumulated_Happend = (double)Math.round(s.getSumofArea() * 100);
      Accumulated_Happend = Accumulated_Happend/100;


      Double ExpectedAccumulate = 0.0;
      for (final AllocationSimpleItemReport sin : it) {
        if (current.getYear().equals(sin.getYear()))
        {
          if (s.getLanduseName().equals(sin.getLanduseName()))
          {
            ExpectedAccumulate = (double)Math.round(sin.getSumofArea() * 100);
            ExpectedAccumulate = ExpectedAccumulate/100;
          }
        }
      }


      for (final AreaRequirement area: outcome)
      {
        if (area.getAllocationLULabel().equals(s.getLanduseName()))
        {
          if (Integer.valueOf(area.getProjectionLabel()) <= s.getYear())
          {
            Double demand_value1 = 0.0;
            demand_value1 = area.getRequiredArea();
            demand_value1 = (double)Math.round(demand_value1 * 100);
            demand_value1 = demand_value1/100;
            ExpectedAccumulate = ExpectedAccumulate + demand_value1;
          }
        }
      }

      ExpectedAccumulate = (double)Math.round(ExpectedAccumulate * 100);
      ExpectedAccumulate = ExpectedAccumulate/100;


      Double demand_value = 0.0;
      for (final AreaRequirement area: outcome)
      {
        if (area.getAllocationLULabel().equals(s.getLanduseName()))
        {
          if (Integer.valueOf(area.getProjectionLabel()) == s.getYear())
          {

            //demand_value = (double) (Math.round(area.getRequiredArea() * 100000) / 100000);
            demand_value = area.getRequiredArea();
            demand_value = (double)Math.round(demand_value * 100);
            demand_value = demand_value/100;
          }
        }
      }
      ///////////////////////////////////


      str.append("<list>");
      str.append("<value>");
      str.append(s.getLanduseName());
      str.append("</value>");
      str.append("<value>");
      str.append(s.getYear());
      str.append("</value>");
      str.append("<value>");
      str.append(demand_value);
      str.append("</value>");
      str.append("<value>");
      str.append(ExpectedAccumulate);
      str.append("</value>");
      str.append("<value>");
      str.append(Accumulated_Happend);
      str.append("</value>");
      str.append("<value>");
      Double diffvalue = Accumulated_Happend - ExpectedAccumulate;
      diffvalue = (double)Math.round(diffvalue * 100);
      diffvalue = diffvalue/100;
      str.append(diffvalue);
      str.append("</value>");

      str.append("</list>");

    }

    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/alloc.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/alloc.xml");

    // FileWriter fwxml = new FileWriter("/Users/ashamakhy/Documents/ali.xml");
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
    final String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");

      final ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass()
          .getResource("/allocationnewpdf2.rptdesign"); // allocation.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/palloc.pdf");
      if (!file.exists()) {
        file.createNewFile();
      }

      final PDFRenderOption pdfOptions = new PDFRenderOption();
      pdfOptions.setOutputFormat("pdf");
      pdfOptions.setOutputFileName(tempDir + "/palloc.pdf");

      //pdfOptions.setOutputFormat(IPDFRenderOption.OUTPUT_EMITTERID_PDF);

      pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW,
          IPDFRenderOption.FIT_TO_PAGE_SIZE);
      pdfOptions.setOption(IPDFRenderOption.PDF_HYPHENATION, true);
      pdfOptions.setOption(IPDFRenderOption.PDF_TEXT_WRAPPING, true);


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
          "*******>> Completed Birt Report pdf new for Allocation Scenario  id ={}",
          id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt pdf Report error : ={}", e.toString());

    } finally {
      birtEngine.destroy();
    }
    return bytem;

  }



  /**
   * Generated allocation scenario xls report
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
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/xlsnew")
  @ResponseBody
  public byte[] getXLSAllocationNew(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException, IOException, IncompleteDemandScenarioException {

    byte[] bytem = null;

    LOGGER.info("*******>> getBirt Report xls for Allocation Scenario  id ={}",
        id);

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    allocationSimpleAnalysisReport = new AllocationSimpleAnalysisReport();
    allocationSimpleAnalysisReport = reportService
        .getAllocationSimpleAnalysisReport(allocationScenario);

    BirtReport = new BirtReport();
    BirtReport.setProjectName(allocationSimpleAnalysisReport.getLabel());
    BirtReport.setScenarioName(allocationScenario.getLabel());
    final Set<AllocationSimpleItemReport> it = allocationSimpleAnalysisReport
        .getAllocationSimpleItemReport();

    ///////////////////////////////////////////

    DemandOutcome manualdemandScn;
    final WifProject project = allocationScenario.getWifProject();
    final String prjID = allocationScenario.getProjectId();

    LOGGER.info("projectId: " + prjID);
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(prjID);

    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(demandConfig.getProjections());
    final Projection current = projections.first();

    final String suitabilityScenarioId = allocationScenario
        .getSuitabilityScenarioId();
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);

    allocationScenario.setSuitabilityScenario(suitabilityScenario);
    final String projectId = suitabilityScenario.getWifProject().getId();

    Set<AreaRequirement> outcome = new HashSet<AreaRequirement>();
    if (allocationScenario.isManual()) {
      final String scenarioID = allocationScenario.getManualdemandScenarioId();

      final List<DemandScenario> listDemand = demandScenarioService
          .getDemandScenarios(projectId);
      Boolean lsw = false;
      for (final DemandScenario dsn : listDemand) {
        if (dsn.getId().equals(scenarioID)) {
          lsw = true;
        }
      }
      if (lsw == false) {
        manualdemandScn = allocationScenario.getManualdemandScenario();
        LOGGER.info("Manual Demand Scenario label: {}",
            manualdemandScn.getLabel());
        outcome = allocationScenario.getManualdemandScenario()
            .getAreaRequirements();
      } else {
        final List<AreaRequirement> outAreas = demandScenarioService
            .getOutcome(scenarioID);
        for (final AreaRequirement areaRequirement : outAreas) {
          areaRequirement.setProjectionLabel(areaRequirement.getProjection()
              .getLabel());
          outcome.add(areaRequirement);
        }
      }
    }


    ///////////////////////////////////////////


    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final AllocationSimpleItemReport s : it) {

      Double Accumulated_Happend = 0.0;
      Accumulated_Happend = (double)Math.round(s.getSumofArea() * 100);
      Accumulated_Happend = Accumulated_Happend/100;


      /////////////////////////////////////////

      Double ExpectedAccumulate = 0.0;
      for (final AllocationSimpleItemReport sin : it) {
        if (current.getYear().equals(sin.getYear()))
        {
          if (s.getLanduseName().equals(sin.getLanduseName()))
          {
            ExpectedAccumulate = (double)Math.round(sin.getSumofArea() * 100);
            ExpectedAccumulate = ExpectedAccumulate/100;
          }
        }
      }


      for (final AreaRequirement area: outcome)
      {
        if (area.getAllocationLULabel().equals(s.getLanduseName()))
        {
          if (Integer.valueOf(area.getProjectionLabel()) <= s.getYear())
          {
            Double demand_value1 = 0.0;
            demand_value1 = area.getRequiredArea();
            demand_value1 = (double)Math.round(demand_value1 * 100);
            demand_value1 = demand_value1/100;
            ExpectedAccumulate = ExpectedAccumulate + demand_value1;
          }
        }
      }

      /////////////////////////////////
      //      Double alloc_value_Prev = 0.0;
      //
      //      Projection projectionPrev = null;
      //      if (current.getYear().equals(s.getYear()))
      //      {
      //        alloc_value_Prev = alloc_value;
      //      }
      //      else
      //      {
      //
      //        for (final Projection projection: projections)
      //        {
      //          if (projection.getYear().equals(s.getYear()))
      //          {
      //            projectionPrev = projections.lower(projection);
      //            break;
      //          }
      //        }
      //      }
      //
      //
      //      if (projectionPrev != null)
      //      {
      //        for (final AllocationSimpleItemReport sin : it) {
      //          if (projectionPrev.getYear().equals(sin.getYear()))
      //          {
      //            if (s.getLanduseName().equals(sin.getLanduseName()))
      //            {
      //              alloc_value_Prev = (double)Math.round(sin.getSumofArea() * 100);
      //              alloc_value_Prev = alloc_value_Prev/100;
      //            }
      //          }
      //        }
      //      }

      Double demand_value = 0.0;
      for (final AreaRequirement area: outcome)
      {
        if (area.getAllocationLULabel().equals(s.getLanduseName()))
        {
          if (Integer.valueOf(area.getProjectionLabel()) == s.getYear())
          {

            //demand_value = (double) (Math.round(area.getRequiredArea() * 100000) / 100000);
            demand_value = area.getRequiredArea();
            demand_value = (double)Math.round(demand_value * 100);
            demand_value = demand_value/100;
          }
        }
      }
      ///////////////////////////////////


      str.append("<list>");
      str.append("<value>");
      str.append(s.getLanduseName());
      str.append("</value>");
      str.append("<value>");
      str.append(s.getYear());
      str.append("</value>");
      str.append("<value>");
      str.append(demand_value);
      str.append("</value>");
      str.append("<value>");
      str.append(ExpectedAccumulate);
      str.append("</value>");
      str.append("<value>");
      str.append(Accumulated_Happend);
      str.append("</value>");
      str.append("<value>");
      Double diffvalue = Accumulated_Happend - ExpectedAccumulate;
      diffvalue = (double)Math.round(diffvalue * 100);
      diffvalue = diffvalue/100;
      str.append(diffvalue);
      str.append("</value>");

      str.append("</list>");

    }

    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/alloc.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/alloc.xml");

    // FileWriter fwxml = new FileWriter("/Users/ashamakhy/Documents/ali.xml");
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
    final String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");

      final ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      //final URL peopleresource = getClass().getResource("/allocationnew.rptdesign"); // allocation.rptdesign
      final URL peopleresource = getClass().getResource("/allocationnew2.rptdesign"); // allocationnew.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/xalloc.xls");
      if (!file.exists()) {
        file.createNewFile();
      }

      final EXCELRenderOption xlsOptions = new EXCELRenderOption();
      xlsOptions.setOutputFormat("xls");
      xlsOptions.setOutputFileName(tempDir + "/xalloc.xls");


      xlsOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW,
          IPDFRenderOption.FIT_TO_PAGE_SIZE);

      // pdfOptions.setOutputStream(response.getOutputStream());
      runAndRenderTask.setRenderOption(xlsOptions);

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
          "*******>> Completed Birt Report xls for Allocation Scenario  id ={}",
          id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt xls Report error : ={}", e.toString());

    } finally {
      birtEngine.destroy();
    }
    return bytem;

  }





}
