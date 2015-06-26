package au.org.aurin.wif.svc.report;

import java.util.List;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.report.suitability.suitabilityConvertReport;
import au.org.aurin.wif.impl.report.suitability.suitabilityFactorReport;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.model.reports.allocation.AllocationSimpleAnalysisReport;
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * The Interface ReportService.
 */
public interface ReportService {

  /**
   * Gets the suitability analysis report.
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @return the suitability analysis report
   * @throws WifInvalidConfigException
   * @throws WifInvalidInputException
   */
  SuitabilityAnalysisReport getSuitabilityAnalysisReport(
      SuitabilityScenario suitabilityScenario) throws WifInvalidInputException,
      WifInvalidConfigException;

  /**
   * Gets the project report.
   * 
   * @param project
   *          the project
   * @return the project report
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  ProjectReport getProjectReport(WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the demand analysis report.
   * 
   * @param demandScenario
   *          the demand scenario
   * @return the demand analysis report
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  DemandAnalysisReport getDemandAnalysisReport(DemandScenario demandScenario)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandScenarioException;

  /**
   * Gets the allocation analysis report.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @return the allocation analysis report
   * @throws ParsingException
   * @throws WifInvalidConfigException
   * @throws WifInvalidInputException
   */
  AllocationAnalysisReport getAllocationAnalysisReport(
      AllocationScenario allocationScenario) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;

  /**
   * Gets the allocation simple analysis report.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @return the allocation analysis report
   * @throws ParsingException
   * @throws WifInvalidConfigException
   * @throws WifInvalidInputException
   */
  AllocationSimpleAnalysisReport getAllocationSimpleAnalysisReport(
      AllocationScenario allocationScenario) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;

  List<suitabilityFactorReport> getSuitabilityCSVAnalysisReport(
      SuitabilityScenario suitabilityScenario);

  List<suitabilityConvertReport> getSuitabilityConvertAnalysisReport(
      SuitabilityScenario suitabilityScenario);

  List<String> getSuitabilityLUsScores(final String projectID)
      throws WifInvalidInputException, WifInvalidConfigException;

}
