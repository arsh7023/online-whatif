package au.org.aurin.wif.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.report.suitability.suitabilityConvertReport;
import au.org.aurin.wif.impl.report.suitability.suitabilityFactorReport;
import au.org.aurin.wif.model.reports.BirtReport;
import au.org.aurin.wif.model.reports.suitability.CategoryItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class BirtController {

  /**
   * The Class BirtController for generating suitability reports using Birt.
   */

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  @Autowired
  private SuitabilityAnalysisReport SuitabilityAnalysisReport;

  private @Autowired
  ApplicationContext servletContext;

  private @Autowired
  BirtReport BirtReport;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(BirtController.class);

  /**
   * Generates suitability scenario html report
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
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/html")
  public @ResponseBody
  String home(final Locale locale, final Model model,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException {

    LOGGER.info("Welcome Birt Report!");
    LOGGER.info(
        "*******>> getBirt Html Report for Suitability Scenario  id ={}", id);

    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    SuitabilityAnalysisReport = new SuitabilityAnalysisReport();
    SuitabilityAnalysisReport = reportService
        .getSuitabilityAnalysisReport(suitabilityScenario);

    // carServiceNew = new carServiceNew(2);
    BirtReport = new BirtReport();
    BirtReport.setProjectName(SuitabilityAnalysisReport.getLabel());
    BirtReport.setScenarioName(SuitabilityAnalysisReport.getScenarioLabel());

    final Set<SuitabilityAnalysisItem> it = SuitabilityAnalysisReport
        .getItems();

    StringBuilder str = new StringBuilder("<property name='data'><list>");
    StringBuilder str1 = new StringBuilder("");
    StringBuilder str2 = new StringBuilder("");
    StringBuilder str3 = new StringBuilder("");
    StringBuilder str4 = new StringBuilder("");
    StringBuilder str5 = new StringBuilder("");
    StringBuilder str6 = new StringBuilder("");
    StringBuilder str7 = new StringBuilder("");
    StringBuilder str8 = new StringBuilder("");
    StringBuilder str9 = new StringBuilder("");

    for (final SuitabilityAnalysisItem s : it) {
      // //System.out.println(s.getTotalArea());
      final Set<CategoryItem> ct = s.getSuitabilityCategories();

      str1 = new StringBuilder("");
      str2 = new StringBuilder("");
      str3 = new StringBuilder("");
      str4 = new StringBuilder("");
      str5 = new StringBuilder("");
      str6 = new StringBuilder("");
      str7 = new StringBuilder("");
      str8 = new StringBuilder("");
      str9 = new StringBuilder("");

      for (final CategoryItem st : ct) {

        if (st.getCategory().equals("Undefined")) {

          str1.append("<list>");
          str1.append("<value>");
          str1.append(st.getCategory());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(st.getScoreRange());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(st.getArea());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(s.getSuitabilityLULabel());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(s.getTotalArea());
          str1.append("</value>");
          str1 = str1.append("</list>");
        }
        if (st.getCategory().equals("Not Developable")) {
          str2.append("<list>");
          str2.append("<value>");
          str2.append(st.getCategory());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(st.getScoreRange());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(st.getArea());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(s.getSuitabilityLULabel());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(s.getTotalArea());
          str2.append("</value>");
          str2.append("</list>");
        }
        if (st.getCategory().equals("Not Convertible")) {
          str3.append("<list>");
          str3.append("<value>");
          str3.append(st.getCategory());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(st.getScoreRange());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(st.getArea());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(s.getSuitabilityLULabel());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(s.getTotalArea());
          str3.append("</value>");
          str3.append("</list>");
        }
        if (st.getCategory().equals("Not Suitable")) {
          str4.append("<list>");
          str4.append("<value>");
          str4.append(st.getCategory());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(st.getScoreRange());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(st.getArea());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(s.getSuitabilityLULabel());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(s.getTotalArea());
          str4.append("</value>");
          str4.append("</list>");
        }
        if (st.getCategory().equals("LOW")) {
          str5.append("<list>");
          str5.append("<value>");
          str5.append(st.getCategory());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(st.getScoreRange());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(st.getArea());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(s.getSuitabilityLULabel());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(s.getTotalArea());
          str5.append("</value>");
          str5.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM_LOW")) {
          str6.append("<list>");
          str6.append("<value>");
          str6.append(st.getCategory());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(st.getScoreRange());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(st.getArea());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(s.getSuitabilityLULabel());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(s.getTotalArea());
          str6.append("</value>");
          str6.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM")) {
          str7.append("<list>");
          str7.append("<value>");
          str7.append(st.getCategory());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(st.getScoreRange());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(st.getArea());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(s.getSuitabilityLULabel());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(s.getTotalArea());
          str7.append("</value>");
          str7.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM_HIGH")) {
          str8.append("<list>");
          str8.append("<value>");
          str8.append(st.getCategory());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(st.getScoreRange());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(st.getArea());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(s.getSuitabilityLULabel());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(s.getTotalArea());
          str8.append("</value>");
          str8.append("</list>");
        }
        if (st.getCategory().equals("HIGH")) {
          str9.append("<list>");
          str9.append("<value>");
          str9.append(st.getCategory());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(st.getScoreRange());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(st.getArea());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(s.getSuitabilityLULabel());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(s.getTotalArea());
          str9.append("</value>");
          str9.append("</list>");
        }

      }
      str.append(str1);
      str.append(str2);
      str.append(str3);
      str.append(str4);
      str.append(str5);
      str.append(str6);
      str.append(str7);
      str.append(str8);
      str.append(str9);

    }
    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/suin.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/suin.xml");

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

    // strxml =
    // "<bean id='BirtReport' class='au.org.aurin.wif.model.reports.BirtReport'>"
    // + "<property name='scenarioName' value='test' />" + "</bean>\n</beans>";

    // +
    // "<property name='data' value=[{ 's1', 'Undefined', '-999', '0' },{ 's1', 'Not Developable', '-100', '0' }]"

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
    // ApplicationContext context = new FileSystemXmlApplicationContext(
    // "//Users/ashamakhy/Documents/ali.xml");
    final ApplicationContext context = new FileSystemXmlApplicationContext("/"
        + filexml.getPath());

    // ///////////////birt

    final StringBuilder mystrnew = new StringBuilder();
    String mystr = "";
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
          "/SpringSampleBIRTViewer.rptdesign"); // SpringSampleBIRTViewer.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);
      // runAndRenderTask.setParameterValues(discoverAndSetParameters(runnable,
      // request));
      final HTMLRenderOption htmlOptions = new HTMLRenderOption();
      htmlOptions.setOutputFormat("html");

      final File file = new File(tempDir + "/suin.html");
      if (!file.exists()) {
        file.createNewFile();
      }

      // htmlOptions.setOutputFileName("/Users/ashamakhy/Documents/alin.html");
      htmlOptions.setOutputFileName(tempDir + "/suin.html");
      // htmlOptions.setOutputFileName("alin.html");
      // // Setting this to true removes html and body tags
      htmlOptions.setEmbeddable(true);

      final String mystrStart = "<html><body>";
      final String mystrEnd = "</body></html>";

      mystr = mystrStart + mystr;
      runAndRenderTask.setRenderOption(htmlOptions);

      runAndRenderTask.run();
      runAndRenderTask.close();

      // File file = new File("alin.html");
      FileInputStream fis = null;

      final FileWriter fw = new FileWriter(tempDir + "/suin2.html");
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
      final File file1 = new File(tempDir + "/suin2.html");
      file1.delete();
      filexml.delete();

      LOGGER.info(
          "*******>> Completed Birt Report for Suitability Scenario  id ={}",
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
   * Generates suitability scenario pdf report
   * 
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/pdf")
  @ResponseBody
  public byte[] getPDF(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException {

    byte[] bytem = null;
    LOGGER.info(
        "*******>> getBirt Report pdf for Suitability Scenario  id ={}", id);

    // SuitabilityScenario suitabilityScenario = suitabilityScenarioService
    // .getSuitabilityScenario("59c979a3ed6fdab3ecf88de7260f3182");

    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    SuitabilityAnalysisReport = new SuitabilityAnalysisReport();
    SuitabilityAnalysisReport = reportService
        .getSuitabilityAnalysisReport(suitabilityScenario);

    // carServiceNew = new carServiceNew(2);
    BirtReport = new BirtReport();
    BirtReport.setProjectName(SuitabilityAnalysisReport.getLabel());
    BirtReport.setScenarioName(SuitabilityAnalysisReport.getScenarioLabel());

    // System.out.println(BirtReport.getProjectName());
    // //System.out.println(SuitabilityAnalysisReport.toString());
    final Set<SuitabilityAnalysisItem> it = SuitabilityAnalysisReport
        .getItems();

    StringBuilder str = new StringBuilder("<property name='data'><list>");
    StringBuilder str1 = new StringBuilder("");
    StringBuilder str2 = new StringBuilder("");
    StringBuilder str3 = new StringBuilder("");
    StringBuilder str4 = new StringBuilder("");
    StringBuilder str5 = new StringBuilder("");
    StringBuilder str6 = new StringBuilder("");
    StringBuilder str7 = new StringBuilder("");
    StringBuilder str8 = new StringBuilder("");
    StringBuilder str9 = new StringBuilder("");

    for (final SuitabilityAnalysisItem s : it) {
      // System.out.println(s.getTotalArea());
      final Set<CategoryItem> ct = s.getSuitabilityCategories();

      str1 = new StringBuilder("");
      str2 = new StringBuilder("");
      str3 = new StringBuilder("");
      str4 = new StringBuilder("");
      str5 = new StringBuilder("");
      str6 = new StringBuilder("");
      str7 = new StringBuilder("");
      str8 = new StringBuilder("");
      str9 = new StringBuilder("");

      for (final CategoryItem st : ct) {

        if (st.getCategory().equals("Undefined")) {

          str1.append("<list>");
          str1.append("<value>");
          str1.append(st.getCategory());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(st.getScoreRange());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(st.getArea());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(s.getSuitabilityLULabel());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(s.getTotalArea());
          str1.append("</value>");
          str1 = str1.append("</list>");
        }
        if (st.getCategory().equals("Not Developable")) {
          str2.append("<list>");
          str2.append("<value>");
          str2.append(st.getCategory());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(st.getScoreRange());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(st.getArea());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(s.getSuitabilityLULabel());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(s.getTotalArea());
          str2.append("</value>");
          str2.append("</list>");
        }
        if (st.getCategory().equals("Not Convertible")) {
          str3.append("<list>");
          str3.append("<value>");
          str3.append(st.getCategory());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(st.getScoreRange());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(st.getArea());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(s.getSuitabilityLULabel());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(s.getTotalArea());
          str3.append("</value>");
          str3.append("</list>");
        }
        if (st.getCategory().equals("Not Suitable")) {
          str4.append("<list>");
          str4.append("<value>");
          str4.append(st.getCategory());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(st.getScoreRange());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(st.getArea());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(s.getSuitabilityLULabel());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(s.getTotalArea());
          str4.append("</value>");
          str4.append("</list>");
        }
        if (st.getCategory().equals("LOW")) {
          str5.append("<list>");
          str5.append("<value>");
          str5.append(st.getCategory());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(st.getScoreRange());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(st.getArea());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(s.getSuitabilityLULabel());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(s.getTotalArea());
          str5.append("</value>");
          str5.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM_LOW")) {
          str6.append("<list>");
          str6.append("<value>");
          str6.append(st.getCategory());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(st.getScoreRange());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(st.getArea());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(s.getSuitabilityLULabel());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(s.getTotalArea());
          str6.append("</value>");
          str6.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM")) {
          str7.append("<list>");
          str7.append("<value>");
          str7.append(st.getCategory());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(st.getScoreRange());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(st.getArea());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(s.getSuitabilityLULabel());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(s.getTotalArea());
          str7.append("</value>");
          str7.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM_HIGH")) {
          str8.append("<list>");
          str8.append("<value>");
          str8.append(st.getCategory());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(st.getScoreRange());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(st.getArea());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(s.getSuitabilityLULabel());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(s.getTotalArea());
          str8.append("</value>");
          str8.append("</list>");
        }
        if (st.getCategory().equals("HIGH")) {
          str9.append("<list>");
          str9.append("<value>");
          str9.append(st.getCategory());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(st.getScoreRange());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(st.getArea());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(s.getSuitabilityLULabel());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(s.getTotalArea());
          str9.append("</value>");
          str9.append("</list>");
        }

      }
      str.append(str1);
      str.append(str2);
      str.append(str3);
      str.append(str4);
      str.append(str5);
      str.append(str6);
      str.append(str7);
      str.append(str8);
      str.append(str9);

    }
    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/suin.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/suin.xml");

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
          "/SpringSampleBIRTViewer.rptdesign"); // SpringSampleBIRTViewer.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/psuin.pdf");
      if (!file.exists()) {
        file.createNewFile();
      }

      final PDFRenderOption pdfOptions = new PDFRenderOption();
      pdfOptions.setOutputFormat("pdf");
      pdfOptions.setOutputFileName(tempDir + "/psuin.pdf");

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
              "*******>> Completed Birt Report pdf for Suitability Scenario  id ={}",
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
   * Generates suitability scenario xls report
   * 
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/xls")
  @ResponseBody
  public byte[] getXLS(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException {

    byte[] bytem = null;

    LOGGER.info(
        "*******>> getBirt Report xls for Suitability Scenario  id ={}", id);

    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    SuitabilityAnalysisReport = new SuitabilityAnalysisReport();
    SuitabilityAnalysisReport = reportService
        .getSuitabilityAnalysisReport(suitabilityScenario);

    BirtReport = new BirtReport();
    BirtReport.setProjectName(SuitabilityAnalysisReport.getLabel());
    BirtReport.setScenarioName(SuitabilityAnalysisReport.getScenarioLabel());

    final Set<SuitabilityAnalysisItem> it = SuitabilityAnalysisReport
        .getItems();

    StringBuilder str = new StringBuilder("<property name='data'><list>");
    StringBuilder str1 = new StringBuilder("");
    StringBuilder str2 = new StringBuilder("");
    StringBuilder str3 = new StringBuilder("");
    StringBuilder str4 = new StringBuilder("");
    StringBuilder str5 = new StringBuilder("");
    StringBuilder str6 = new StringBuilder("");
    StringBuilder str7 = new StringBuilder("");
    StringBuilder str8 = new StringBuilder("");
    StringBuilder str9 = new StringBuilder("");

    for (final SuitabilityAnalysisItem s : it) {
      // System.out.println(s.getTotalArea());
      final Set<CategoryItem> ct = s.getSuitabilityCategories();

      str1 = new StringBuilder("");
      str2 = new StringBuilder("");
      str3 = new StringBuilder("");
      str4 = new StringBuilder("");
      str5 = new StringBuilder("");
      str6 = new StringBuilder("");
      str7 = new StringBuilder("");
      str8 = new StringBuilder("");
      str9 = new StringBuilder("");

      for (final CategoryItem st : ct) {

        if (st.getCategory().equals("Undefined")) {

          str1.append("<list>");
          str1.append("<value>");
          str1.append(st.getCategory());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(st.getScoreRange());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(st.getArea());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(s.getSuitabilityLULabel());
          str1.append("</value>");
          str1.append("<value>");
          str1.append(s.getTotalArea());
          str1.append("</value>");
          str1 = str1.append("</list>");
        }
        if (st.getCategory().equals("Not Developable")) {
          str2.append("<list>");
          str2.append("<value>");
          str2.append(st.getCategory());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(st.getScoreRange());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(st.getArea());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(s.getSuitabilityLULabel());
          str2.append("</value>");
          str2.append("<value>");
          str2.append(s.getTotalArea());
          str2.append("</value>");
          str2.append("</list>");
        }
        if (st.getCategory().equals("Not Convertible")) {
          str3.append("<list>");
          str3.append("<value>");
          str3.append(st.getCategory());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(st.getScoreRange());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(st.getArea());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(s.getSuitabilityLULabel());
          str3.append("</value>");
          str3.append("<value>");
          str3.append(s.getTotalArea());
          str3.append("</value>");
          str3.append("</list>");
        }
        if (st.getCategory().equals("Not Suitable")) {
          str4.append("<list>");
          str4.append("<value>");
          str4.append(st.getCategory());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(st.getScoreRange());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(st.getArea());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(s.getSuitabilityLULabel());
          str4.append("</value>");
          str4.append("<value>");
          str4.append(s.getTotalArea());
          str4.append("</value>");
          str4.append("</list>");
        }
        if (st.getCategory().equals("LOW")) {
          str5.append("<list>");
          str5.append("<value>");
          str5.append(st.getCategory());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(st.getScoreRange());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(st.getArea());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(s.getSuitabilityLULabel());
          str5.append("</value>");
          str5.append("<value>");
          str5.append(s.getTotalArea());
          str5.append("</value>");
          str5.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM_LOW")) {
          str6.append("<list>");
          str6.append("<value>");
          str6.append(st.getCategory());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(st.getScoreRange());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(st.getArea());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(s.getSuitabilityLULabel());
          str6.append("</value>");
          str6.append("<value>");
          str6.append(s.getTotalArea());
          str6.append("</value>");
          str6.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM")) {
          str7.append("<list>");
          str7.append("<value>");
          str7.append(st.getCategory());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(st.getScoreRange());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(st.getArea());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(s.getSuitabilityLULabel());
          str7.append("</value>");
          str7.append("<value>");
          str7.append(s.getTotalArea());
          str7.append("</value>");
          str7.append("</list>");
        }
        if (st.getCategory().equals("MEDIUM_HIGH")) {
          str8.append("<list>");
          str8.append("<value>");
          str8.append(st.getCategory());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(st.getScoreRange());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(st.getArea());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(s.getSuitabilityLULabel());
          str8.append("</value>");
          str8.append("<value>");
          str8.append(s.getTotalArea());
          str8.append("</value>");
          str8.append("</list>");
        }
        if (st.getCategory().equals("HIGH")) {
          str9.append("<list>");
          str9.append("<value>");
          str9.append(st.getCategory());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(st.getScoreRange());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(st.getArea());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(s.getSuitabilityLULabel());
          str9.append("</value>");
          str9.append("<value>");
          str9.append(s.getTotalArea());
          str9.append("</value>");
          str9.append("</list>");
        }

      }
      str.append(str1);
      str.append(str2);
      str.append(str3);
      str.append(str4);
      str.append(str5);
      str.append(str6);
      str.append(str7);
      str.append(str8);
      str.append(str9);

    }
    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/suin.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/suin.xml");

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
    // String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");

      // ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass().getResource(
          "/SpringSampleBIRTViewer.rptdesign"); // SpringSampleBIRTViewer.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/xsuin.xls");
      if (!file.exists()) {
        file.createNewFile();
      }

      final EXCELRenderOption xlsOptions = new EXCELRenderOption();
      xlsOptions.setOutputFormat("xls");
      xlsOptions.setOutputFileName(tempDir + "/xsuin.xls");

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
              "*******>> Completed Birt Report xls for Suitability Scenario  id ={}",
              id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt xls Report error : ={}", e.toString());

    } finally {
      birtEngine.destroy();
    }
    return bytem;

  }

  /**
   * Generates suitability scenario csv report
   * 
   * @param response
   * @param id
   * @return
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/csv")
  @ResponseBody
  public byte[] getCSV(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException {

    byte[] bytem = null;

    LOGGER.info(
        "*******>> getBirt Report csv for Suitability Scenario  id ={}", id);

    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    List<suitabilityFactorReport> listOut = new ArrayList<suitabilityFactorReport>();
    listOut = reportService
        .getSuitabilityCSVAnalysisReport(suitabilityScenario);

    BirtReport = new BirtReport();
    if (listOut.size() > 0) {
      BirtReport.setProjectName(listOut.get(0).getProjectName());
      BirtReport.setScenarioName(listOut.get(0).getScenarioLabel());
    }
    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final suitabilityFactorReport sr : listOut) {

      str.append("<list>");
      str.append("<value>");
      str.append(sr.getLUName());
      str.append("</value>");
      str.append("<value>");
      str.append(sr.getFactorName());
      str.append("</value>");
      str.append("<value>");
      str.append(sr.getFatorValue());
      str.append("</value>");
      str = str.append("</list>");

    }
    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/xfact.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/xfact.xml");

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
    // String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");

      // ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass().getResource(
          "/suitabilityfactor.rptdesign"); // SpringSampleBIRTViewer.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/xfact.xls");
      if (!file.exists()) {
        file.createNewFile();
      }

      final EXCELRenderOption xlsOptions = new EXCELRenderOption();
      xlsOptions.setOutputFormat("xls");
      xlsOptions.setOutputFileName(tempDir + "/xfact.xls");

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
              "*******>> Completed Birt Report csv for Suitability Scenario  id ={}",
              id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt factor csv Report error : ={}", e.toString());

    } finally {
      birtEngine.destroy();
    }
    return bytem;

  }

  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/convert")
  @ResponseBody
  public byte[] getConvert(final HttpServletResponse response,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, IOException {

    byte[] bytem = null;

    LOGGER
        .info(
            "*******>> getBirt Report convert for Suitability Scenario  id ={}",
            id);

    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    List<suitabilityConvertReport> listOut = new ArrayList<suitabilityConvertReport>();
    listOut = reportService
        .getSuitabilityConvertAnalysisReport(suitabilityScenario);

    // SuitabilityAnalysisReport = new SuitabilityAnalysisReport();
    // SuitabilityAnalysisReport = reportService
    // .getSuitabilityAnalysisReport(suitabilityScenario);

    BirtReport = new BirtReport();
    if (listOut.size() > 0) {
      BirtReport.setProjectName(listOut.get(0).getProjectName());
      BirtReport.setScenarioName(listOut.get(0).getScenarioLabel());
    }
    StringBuilder str = new StringBuilder("<property name='data'><list>");

    for (final suitabilityConvertReport sr : listOut) {

      str.append("<list>");
      str.append("<value>");
      str.append(sr.getSuName());
      str.append("</value>");
      str.append("<value>");
      str.append(sr.getLuName());
      str.append("</value>");
      str.append("<value>");
      str.append(sr.getIsConvert());
      str.append("</value>");
      str = str.append("</list>");

    }
    str = str.append("</list>");
    str = str.append("</property>");

    // ///////////////

    final String tempDir = System.getProperty("java.io.tmpdir");
    final File filexml = new File(tempDir + "/xconv.xml");
    if (!filexml.exists()) {
      filexml.createNewFile();
    }
    final FileWriter fwxml = new FileWriter(tempDir + "/xconv.xml");

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
    // String mystr = "";
    IReportEngine birtEngine = null;
    try {
      final EngineConfig config = new EngineConfig();

      // System.out.println(servletContext.getBean("BirtReport").toString());
      final BirtReport cc = (au.org.aurin.wif.model.reports.BirtReport) context
          .getBean("BirtReport");

      // ArrayList<String[]> data = cc.getData();

      config.getAppContext().put(
          EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, context);

      Platform.startup(config);
      final IReportEngineFactory factory = (IReportEngineFactory) Platform
          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
      birtEngine = factory.createReportEngine(config);

      final URL peopleresource = getClass().getResource(
          "/suitabilityconvert.rptdesign"); // SpringSampleBIRTViewer.rptdesign

      IReportRunnable runnable = null;
      runnable = birtEngine.openReportDesign(peopleresource.getFile());
      final IRunAndRenderTask runAndRenderTask = birtEngine
          .createRunAndRenderTask(runnable);

      final File file = new File(tempDir + "/xconv.xls");
      if (!file.exists()) {
        file.createNewFile();
      }

      final EXCELRenderOption xlsOptions = new EXCELRenderOption();
      xlsOptions.setOutputFormat("xls");
      xlsOptions.setOutputFileName(tempDir + "/xconv.xls");

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
              "*******>> Completed Birt Report convert for Suitability Scenario  id ={}",
              id);

    } catch (final Exception e) {
      LOGGER.debug("getBirt convert Report error : ={}", e.toString());

    } finally {
      birtEngine.destroy();
    }
    return bytem;

  }

}
