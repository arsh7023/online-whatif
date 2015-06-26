package au.org.aurin.wif.impl.report.suitability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.lsa.SuitabilityAnalyzer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.reports.suitability.CategoryItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.reports.suitability.SuitabilityCategory;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.reports.suitability.SuitabilityAnalysisReportDao;
import au.org.aurin.wif.svc.report.ReportService;

@Service
@Qualifier("suitabilityReporter")
public class SuitabilityReporter {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1363346734533L;

  /** The suitability analyzer. */
  @Autowired
  private SuitabilityAnalyzer suitabilityAnalyzer;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityReporter.class);

  /** The suitability analysis report dao. */
  @Autowired
  private SuitabilityAnalysisReportDao suitabilityAnalysisReportDao;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  @Autowired
  private SuitabilityAnalysisReport suitabilityAnalysisReport;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.report.ReportService#getSuitabilityAnalysisReport(
   * au.org.aurin.wif.model.suitability.SuitabilityScenario)
   */
  public SuitabilityAnalysisReport getSuitabilityAnalysisReport(
      final SuitabilityScenario suitabilityScenario)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.debug("getSuitabilityAnalysisReport for: {}",
        suitabilityScenario.getLabel());
    SuitabilityAnalysisReport suitabilityAnalysisReport = new SuitabilityAnalysisReport();

    final WifProject project = suitabilityScenario.getWifProject();
    final SuitabilityConfig suitabilityConfig = project.getSuitabilityConfig();

    final String uazDBTable = suitabilityConfig.getUnifiedAreaZone();

    suitabilityAnalysisReport.setReportType(suitabilityScenario.getDocType());
    suitabilityAnalysisReport.setLabel(project.getName());
    suitabilityAnalysisReport.setScenarioLabel(suitabilityScenario.getLabel());
    suitabilityAnalysisReport.setProjectId(suitabilityScenario.getProjectId());

    // new
    final List<String> lstScores = reportService
        .getSuitabilityLUsScores(project.getId());

    final Set<String> scoreColumns = suitabilityConfig.getScoreColumns();
    final Map<String, Integer> scoreRanges = suitabilityAnalyzer
        .generateScoreRanges(suitabilityScenario);
    // Step one: undefined information

    // Step five: categories Low.. High information

    final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    LOGGER.debug("{} has {} suitability rules associated",
        suitabilityScenario.getLabel(), suitabilityRules.size());
    for (final SuitabilityRule rule : suitabilityRules) {

      // ////////////////////////////new
      final Collection<FactorImportance> factorsImp = rule
          .getFactorImportances();

      Double minSum = 0.0;
      Double maxSum = 0.0;
      for (final FactorImportance factorImportance : factorsImp) {
        final Factor aFactor = factorImportance.getFactor();
        final String factorName = aFactor.getFeatureFieldName();
        final String factorLabel = aFactor.getLabel();
        final Double factorWeight = factorImportance.getImportance();
        LOGGER.trace(" Factor label: {}", factorLabel);
        LOGGER.trace(" Factor UAZ column attribute name: {}", factorName);
        LOGGER.trace("- Factor importance: {}", factorWeight);
        // LOGGER.trace("factor id: {}", factorImportance.getId());

        Double minVal = 1000000.0;
        Double maxVal = 0.0;
        Boolean lsw = false;
        for (final FactorTypeRating aFactorRating : factorImportance
            .getFactorTypeRatings()) {

          final Object ratingValue = aFactorRating.getFactorType().getValue();
          final Double ratingScore = aFactorRating.getScore();
          final String ratingLabel = aFactorRating.getFactorType().getLabel();
          if (ratingScore > 0) {
            lsw = true;
            if (ratingScore >= maxVal) {
              maxVal = ratingScore;
            }
            if (ratingScore <= minVal) {
              minVal = ratingScore;
            }
          }
          LOGGER.trace("--> for factor type label: {}", ratingLabel);
          LOGGER.trace("--> rating UAZ value: {}", ratingValue);

        }
        if (minVal == 1000000.0) {
          minVal = 1.0;
        }
        if (maxVal == 0.0) {
          maxVal = 1.0;
        }
        if (lsw == false) {
          minVal = 0.0;
          maxVal = 0.0;
        }
        final Double min1 = factorWeight * minVal;
        minSum = min1 + minSum;
        final Double max1 = factorWeight * maxVal;
        maxSum = max1 + maxSum;

      }
      // ///////////////////////////////

      final SuitabilityCategory[] categories = SuitabilityCategory.values();

      double totalAreaItem = 0;
      final SuitabilityLU suitabilityLU = rule.getSuitabilityLU();
      final Integer totalRange = scoreRanges.get(suitabilityLU
          .getFeatureFieldName());
      double currentRange = 0;
      // commented two lines belowold version
      // double previousRange = 1; // changed from 0 to 1
      // final double rangeStep = totalRange / (double) categories.length;

      // newnewnewnewnwe
      for (final String str : lstScores) {
        final String stVal[] = str.split(",");
        if (stVal[1].equals(suitabilityLU.getFeatureFieldName())) {
          minSum = Double.parseDouble(stVal[2]);
          maxSum = Double.parseDouble(stVal[3]);

          LOGGER.info(suitabilityLU.getFeatureFieldName()
              + ": the min Range is:" + minSum + " and max Range is:" + maxSum);

        }

      }
      // ////////////

      double previousRange = minSum;

      final double rangeStep = (maxSum - minSum) / categories.length;

      LOGGER.debug("suitability LU label: {}", suitabilityLU.getLabel());
      final SuitabilityAnalysisItem item = new SuitabilityAnalysisItem();
      item.setSuitabilityLULabel(suitabilityLU.getLabel());
      final Set<CategoryItem> suitabilityCategories = new HashSet<CategoryItem>();
      // Collection<CategoryItem> suitabilityCategories = new
      // HashSet<CategoryItem>();
      // TODO will repeat the information for each suitability land use, but is
      // the way it is done in the original reports

      // ali - change scoreColumns.iterator().next() --to-->
      // "score_"+suitabilityLU.getLabel()
      final double totalUndefined = geodataFinder.getAreaByScoreRanges(
          uazDBTable, suitabilityLU.getFeatureFieldName(),
          project.getAreaLabel(), suitabilityConfig.getUndefinedScore(),
          suitabilityConfig.getUndefinedScore());

      // double totalUndefined = geodataFinder.getAreaByScoreRanges(uazDBTable,
      // "score_" + suitabilityLU.getFeatureFieldName(), project.getAreaLabel(),
      // suitabilityConfig.getUndefinedScore(),
      // suitabilityConfig.getUndefinedScore());

      final CategoryItem undefinedCategory = new CategoryItem();
      undefinedCategory.setArea(totalUndefined);
      undefinedCategory.setCategory("Undefined");
      undefinedCategory.setScoreRange(suitabilityConfig.getUndefinedScore()
          .toString());
      LOGGER.debug("totalUndefined : {}", totalUndefined);
      // Step two: not developable information
      final double totalNotDeveloped = geodataFinder.getAreaByScoreRanges(
          uazDBTable, suitabilityLU.getFeatureFieldName(),
          project.getAreaLabel(), suitabilityConfig.getNotDevelopableScore(),
          suitabilityConfig.getNotDevelopableScore());
      final CategoryItem notDevelopableCategory = new CategoryItem();
      notDevelopableCategory.setArea(totalNotDeveloped);
      notDevelopableCategory.setCategory("Not Developable");
      notDevelopableCategory.setScoreRange(suitabilityConfig
          .getNotDevelopableScore().toString());
      LOGGER.debug("totalNotDeveloped : {}", totalNotDeveloped);

      // Step three-ali: not Convertible information
      final double totalNotCovertible = geodataFinder.getAreaByScoreRanges(
          uazDBTable, suitabilityLU.getFeatureFieldName(),
          project.getAreaLabel(), suitabilityConfig.getNotConvertableScore(),
          suitabilityConfig.getNotConvertableScore());
      final CategoryItem notConvertableCategory = new CategoryItem();
      notConvertableCategory.setArea(totalNotCovertible);
      notConvertableCategory.setCategory("Not Convertible");
      notConvertableCategory.setScoreRange(suitabilityConfig
          .getNotConvertableScore().toString());
      LOGGER.debug("totalNotCovertible : {}", totalNotCovertible);

      // Step four-ali: not Suitable information
      final double totalNotSuitable = geodataFinder.getAreaByScoreRanges(
          uazDBTable, suitabilityLU.getFeatureFieldName(),
          project.getAreaLabel(), suitabilityConfig.getNotSuitableScore(),
          suitabilityConfig.getNotSuitableScore());
      final CategoryItem notSuitableCategory = new CategoryItem();
      notSuitableCategory.setArea(totalNotSuitable);
      notSuitableCategory.setCategory("Not Suitable");
      notSuitableCategory.setScoreRange(suitabilityConfig.getNotSuitableScore()
          .toString());
      LOGGER.debug("totalNotSuitable : {}", totalNotSuitable);

      suitabilityCategories.add(undefinedCategory);
      suitabilityCategories.add(notDevelopableCategory);
      // ali
      suitabilityCategories.add(notConvertableCategory);
      suitabilityCategories.add(notSuitableCategory);

      for (final SuitabilityCategory category : categories) {
        LOGGER.debug("category for: {}", category.name());

        // new
        if (rangeStep < 1) {
          if (category == SuitabilityCategory.HIGH) {
            currentRange = previousRange + rangeStep;
            final double totalAreaCategory = geodataFinder
                .getAreaByScoreRanges(uazDBTable,
                    suitabilityLU.getFeatureFieldName(),
                    project.getAreaLabel(), previousRange, currentRange);
            LOGGER.debug("previous range : {}", previousRange);
            LOGGER.debug("current range : {}", currentRange);
            LOGGER.debug("totalAreaCategory: {}", totalAreaCategory);
            final CategoryItem categoryItem = new CategoryItem();
            categoryItem.setArea(totalAreaCategory);
            categoryItem.setCategory(category.name());
            categoryItem.setScoreRange(previousRange + " to " + currentRange);
            suitabilityCategories.add(categoryItem);
            // item.setSuitabilityCategories(suitabilityCategories);
            totalAreaItem += totalAreaCategory;
            previousRange = currentRange;
          } else {
            currentRange = previousRange + rangeStep;
            // final double totalAreaCategory = geodataFinder
            // .getAreaByScoreRanges(uazDBTable,
            // suitabilityLU.getFeatureFieldName(),
            // project.getAreaLabel(), previousRange, currentRange);
            // LOGGER.debug("previous range : {}", previousRange);
            // LOGGER.debug("current range : {}", currentRange);
            // LOGGER.debug("totalAreaCategory: {}", totalAreaCategory);
            final CategoryItem categoryItem = new CategoryItem();
            categoryItem.setArea(0.0);
            categoryItem.setCategory(category.name());
            categoryItem.setScoreRange(previousRange + " to " + currentRange);
            suitabilityCategories.add(categoryItem);
            // item.setSuitabilityCategories(suitabilityCategories);
            totalAreaItem += 0.0;
            previousRange = currentRange;
          }
        } else {
          currentRange = previousRange + rangeStep;
          final double totalAreaCategory = geodataFinder.getAreaByScoreRanges(
              uazDBTable, suitabilityLU.getFeatureFieldName(),
              project.getAreaLabel(), previousRange, currentRange);
          LOGGER.debug("previous range : {}", previousRange);
          LOGGER.debug("current range : {}", currentRange);
          LOGGER.debug("totalAreaCategory: {}", totalAreaCategory);
          final CategoryItem categoryItem = new CategoryItem();
          categoryItem.setArea(totalAreaCategory);
          categoryItem.setCategory(category.name());
          categoryItem.setScoreRange(previousRange + " to " + currentRange);
          suitabilityCategories.add(categoryItem);
          // item.setSuitabilityCategories(suitabilityCategories);
          totalAreaItem += totalAreaCategory;
          previousRange = currentRange;
        }
      }
      // ali
      totalAreaItem = totalAreaItem + totalNotCovertible + totalNotDeveloped
          + totalUndefined + totalNotSuitable;
      item.setTotalArea(totalAreaItem);
      item.setSuitabilityCategories(suitabilityCategories);
      suitabilityAnalysisReport.getItems().add(item);
    }
    final SuitabilityAnalysisReport createdSuitabilityAnalysisReport = suitabilityAnalysisReportDao
        .persistSuitabilityAnalysisReport(suitabilityAnalysisReport);
    LOGGER.debug("createdSuitabilityAnalysisReport uuid: "
        + createdSuitabilityAnalysisReport.getId());

    suitabilityAnalysisReport = createdSuitabilityAnalysisReport;
    // return createdSuitabilityAnalysisReport;
    return suitabilityAnalysisReport;
  }

  public List<suitabilityFactorReport> getSuitabilityCSVAnalysisReport(
      final SuitabilityScenario suitabilityScenario) {
    LOGGER.debug("getSuitabilityAnalysisReport for: {}",
        suitabilityScenario.getLabel());

    final List<suitabilityFactorReport> listOut = new ArrayList<suitabilityFactorReport>();
    final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    LOGGER.debug("{} has {} suitability rules associated",
        suitabilityScenario.getLabel(), suitabilityRules.size());

    final WifProject project = suitabilityScenario.getWifProject();

    for (final SuitabilityRule rule : suitabilityRules) {

      // ////////////////////////////new

      // rule.getConvertibleLUs().iterator().next().getLabel();

      final Collection<FactorImportance> factorsImp = rule
          .getFactorImportances();

      for (final FactorImportance factorImportance : factorsImp) {
        final Factor aFactor = factorImportance.getFactor();
        final String factorName = aFactor.getFeatureFieldName();
        final String factorLabel = aFactor.getLabel();
        final Double factorWeight = factorImportance.getImportance();
        LOGGER.trace(" Factor label: {}", factorLabel);
        LOGGER.trace(" Factor UAZ column attribute name: {}", factorName);
        LOGGER.trace("- Factor importance: {}", factorWeight);
        // LOGGER.trace("factor id: {}", factorImportance.getId());

        final suitabilityFactorReport repItem = new suitabilityFactorReport();
        repItem.setLUName(rule.getSuitabilityLU().getLabel());
        repItem.setFactorName(factorLabel);
        repItem.setFatorValue(factorWeight);
        repItem.setIsHead("1");

        repItem.setProjectName(project.getName());
        repItem.setScenarioLabel(suitabilityScenario.getLabel());

        listOut.add(repItem);

        for (final FactorTypeRating aFactorRating : factorImportance
            .getFactorTypeRatings()) {

          final Object ratingValue = aFactorRating.getFactorType().getValue();
          final Double ratingScore = aFactorRating.getScore();
          final String ratingLabel = aFactorRating.getFactorType().getLabel();

          final suitabilityFactorReport repItemNew = new suitabilityFactorReport();
          repItemNew.setLUName(rule.getSuitabilityLU().getLabel());
          repItemNew.setFactorName(factorLabel + "_" + ratingLabel);
          repItemNew.setFatorValue(ratingScore);
          repItemNew.setIsHead("2");

          repItemNew.setProjectName(project.getName());
          repItemNew.setScenarioLabel(suitabilityScenario.getLabel());

          listOut.add(repItemNew);

        }

      }
    }
    return listOut;
  }

  public List<suitabilityConvertReport> getSuitabilityConvertAnalysisReport(
      final SuitabilityScenario suitabilityScenario) {
    LOGGER.debug("getSuitabilityConvertAnalysisReport for: {}",
        suitabilityScenario.getLabel());

    final List<suitabilityConvertReport> listOut = new ArrayList<suitabilityConvertReport>();
    final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    LOGGER.debug("{} has {} suitability rules associated",
        suitabilityScenario.getLabel(), suitabilityRules.size());

    final WifProject project = suitabilityScenario.getWifProject();
    String x = "";
    final ArrayList<String> stList = new ArrayList<String>();
    for (final SuitabilityRule rule : suitabilityRules) {

      // ////////////////////////////new

      for (final AllocationLU allocationLU : rule.getConvertibleLUs()) {
        stList.add(allocationLU.getLabel());
        final suitabilityConvertReport repItem = new suitabilityConvertReport();
        repItem.setSuName(rule.getSuitabilityLU().getLabel());
        x = rule.getSuitabilityLU().getLabel();
        repItem.setLuName(allocationLU.getLabel());
        repItem.setIsConvert("1");
        repItem.setProjectName(project.getName());
        repItem.setScenarioLabel(suitabilityScenario.getLabel());
        listOut.add(repItem);

      }

    }

    for (final AllocationLU allocationLU : project.getAllocationLandUses()) {
      if (!stList.contains(allocationLU.getLabel()) && x != "") {
        final suitabilityConvertReport repItem = new suitabilityConvertReport();
        repItem.setSuName(x);
        repItem.setLuName(allocationLU.getLabel());
        repItem.setIsConvert("");
        repItem.setProjectName(project.getName());
        repItem.setScenarioLabel(suitabilityScenario.getLabel());
        listOut.add(repItem);
      }
    }
    return listOut;
  }
}
