/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.io.demonstrationdata;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * The Class DemonstrationSuitabilityData.
 */
public class DemonstrationSuitabilityData {

  /**
   * Creates the suitability module.
   * 
   * @param project
   *          the project
   * @return the wif project
   * 
   */

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemonstrationSuitabilityData.class);

  public static WifProject createSuitabilityModule(WifProject project) {

    try {
      AllocationLU residentialLowLU = project
          .getExistingLandUseByLabel("Low Density Res.");

      AllocationLU residentialMediumLU = project
          .getExistingLandUseByLabel("Med Density Res.");

      AllocationLU residentialmixedUseLU = project
          .getExistingLandUseByLabel("Mixed Use");

      AllocationLU nursingHomeLU = project
          .getExistingLandUseByLabel("Nursing Home");

      AllocationLU conservationLU = project
          .getExistingLandUseByLabel("Conservation");

      AllocationLU parkAndRecLocalLU = project
          .getExistingLandUseByLabel("Parks & Rec.");

      AllocationLU regionalRetailLU = project
          .getExistingLandUseByLabel("Regional Retail");

      AllocationLU localRetailLU = project
          .getExistingLandUseByLabel("Local Retail");

      AllocationLU officeLU = project.getExistingLandUseByLabel("Office");

      AllocationLU publicSemiPubLU = project
          .getExistingLandUseByLabel("Public/Semi-pub.");

      AllocationLU industrialLU = project
          .getExistingLandUseByLabel("Industrial");

      AllocationLU agricultureLU = project
          .getExistingLandUseByLabel("Agriculture");

      AllocationLU undevelopedLU = project
          .getExistingLandUseByLabel("Undeveloped");

      AllocationLU waterLU = project.getExistingLandUseByLabel("Water");

      AllocationLU rightOfWayLU = project
          .getExistingLandUseByLabel("Right of Way");

      // Creating suitability land uses
      SuitabilityLU residential = project.getSuitabilityLUByName("Residential");

      SuitabilityLU mixedUse = project.getSuitabilityLUByName("Mixed Use");

      SuitabilityLU retail = project.getSuitabilityLUByName("Retail");

      SuitabilityLU office = project.getSuitabilityLUByName("Office");

      SuitabilityLU industrial = project.getSuitabilityLUByName("Industrial");

      SuitabilityLU conservation = project
          .getSuitabilityLUByName("Conservation");

      // Adding factors
      Factor slopes = project.getFactorByLabel("slopes");

      Factor soils = project.getFactorByLabel("Prime Ag. Soils");

      Factor flood = project.getFactorByLabel("100-year flood");

      Factor streams = project.getFactorByLabel("streams");

      Factor access = project.getFactorByLabel("access");

      // Factor types slopes
      FactorType slopeft1 = slopes.getFactorTypeByLabel("<6%");

      FactorType slopeft2 = slopes.getFactorTypeByLabel("6% - <12%");

      FactorType slopeft3 = slopes.getFactorTypeByLabel("12% - <18%");

      FactorType slopeft4 = slopes.getFactorTypeByLabel("18% - <25%");

      FactorType slopeft5 = slopes.getFactorTypeByLabel(">=25%");

      // Factor types access
      FactorType accessft1 = access.getFactorTypeByLabel("Low");

      FactorType accessft2 = access.getFactorTypeByLabel("Medium");

      FactorType accessft3 = access.getFactorTypeByLabel("Medium High");

      FactorType accessft4 = access.getFactorTypeByLabel("High");

      // Factor types soils
      FactorType soilsft1 = soils.getFactorTypeByLabel("Prime Ag.");

      FactorType soilsft2 = soils.getFactorTypeByLabel("Not Prime Ag.");

      // Factor types flood
      FactorType floodft1 = flood.getFactorTypeByLabel("Outside Flood");

      FactorType floodft2 = flood.getFactorTypeByLabel("Inside Flood");

      // Factor types stream
      FactorType streamFt1 = streams.getFactorTypeByLabel("<100'");

      FactorType streamft2 = streams.getFactorTypeByLabel("<100' - <250'");

      FactorType streamft3 = streams.getFactorTypeByLabel("<250' - <500'");

      FactorType streamft4 = streams.getFactorTypeByLabel("Outside buffers");

      // Creating suitability scenarios
      SuitabilityScenario suitabilityScenario = new SuitabilityScenario();
      suitabilityScenario.setLabel("Suburbanization");
      suitabilityScenario.setReady(true);
      suitabilityScenario.setWifProject(project);
      suitabilityScenario.setSuitabilityRules(new HashSet<SuitabilityRule>());
      project.getSuitabilityScenarios().add(suitabilityScenario);

      // Creating suitability rules
      // Residential Suitability Rule
      SuitabilityRule residentialSuitabilityRule = new SuitabilityRule();
      residentialSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
      residentialSuitabilityRule.setSuitabilityLU(residential);
      residentialSuitabilityRule
          .setFactorImportances(new HashSet<FactorImportance>());

      // Slope importance
      FactorImportance residentialImportanceSlope = new FactorImportance();
      residentialImportanceSlope.setFactor(slopes);
      residentialImportanceSlope.setImportance(100.0);
      residentialImportanceSlope
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      residentialImportanceSlope.setSuitabilityRule(residentialSuitabilityRule);

      FactorTypeRating residentialSlopeFtr1 = new FactorTypeRating();
      residentialSlopeFtr1.setFactorType(slopeft1);
      residentialSlopeFtr1.setScore(100.0);

      FactorTypeRating residentialSlopeFtr2 = new FactorTypeRating();
      residentialSlopeFtr2.setFactorType(slopeft2);
      residentialSlopeFtr2.setScore(50.0);

      FactorTypeRating residentialSlopeFtr3 = new FactorTypeRating();
      residentialSlopeFtr3.setFactorType(slopeft3);
      residentialSlopeFtr3.setScore(0.0);

      FactorTypeRating residentialSlopeFtr4 = new FactorTypeRating();
      residentialSlopeFtr4.setFactorType(slopeft4);
      residentialSlopeFtr4.setScore(0.0);

      FactorTypeRating residentialSlopeFtr5 = new FactorTypeRating();
      residentialSlopeFtr5.setFactorType(slopeft5);
      residentialSlopeFtr5.setScore(0.0);

      residentialSlopeFtr1.setFactorImportance(residentialImportanceSlope);
      residentialSlopeFtr2.setFactorImportance(residentialImportanceSlope);
      residentialSlopeFtr3.setFactorImportance(residentialImportanceSlope);
      residentialSlopeFtr4.setFactorImportance(residentialImportanceSlope);
      residentialSlopeFtr5.setFactorImportance(residentialImportanceSlope);

      residentialImportanceSlope.getFactorTypeRatings().add(
          residentialSlopeFtr1);
      residentialImportanceSlope.getFactorTypeRatings().add(
          residentialSlopeFtr2);
      residentialImportanceSlope.getFactorTypeRatings().add(
          residentialSlopeFtr3);
      residentialImportanceSlope.getFactorTypeRatings().add(
          residentialSlopeFtr4);
      residentialImportanceSlope.getFactorTypeRatings().add(
          residentialSlopeFtr5);

      residentialSuitabilityRule.getFactorImportances().add(
          residentialImportanceSlope);

      // residential Access importance
      FactorImportance residentialImportanceAccess = new FactorImportance();
      residentialImportanceAccess.setFactor(access);
      residentialImportanceAccess.setImportance(25.0);
      residentialImportanceAccess
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      residentialImportanceAccess
          .setSuitabilityRule(residentialSuitabilityRule);

      FactorTypeRating residentialAccessFtr1 = new FactorTypeRating();
      residentialAccessFtr1.setFactorType(accessft1);
      residentialAccessFtr1.setScore(25.0);

      FactorTypeRating residentialAccessFtr2 = new FactorTypeRating();
      residentialAccessFtr2.setFactorType(accessft2);
      residentialAccessFtr2.setScore(50.0);

      FactorTypeRating residentialAccessFtr3 = new FactorTypeRating();
      residentialAccessFtr3.setFactorType(accessft3);
      residentialAccessFtr3.setScore(75.0);

      FactorTypeRating residentialAccessFtr4 = new FactorTypeRating();
      residentialAccessFtr4.setFactorType(accessft4);
      residentialAccessFtr4.setScore(100.0);

      residentialAccessFtr1.setFactorImportance(residentialImportanceAccess);
      residentialAccessFtr2.setFactorImportance(residentialImportanceAccess);
      residentialAccessFtr3.setFactorImportance(residentialImportanceAccess);
      residentialAccessFtr4.setFactorImportance(residentialImportanceAccess);

      residentialImportanceAccess.getFactorTypeRatings().add(
          residentialAccessFtr1);
      residentialImportanceAccess.getFactorTypeRatings().add(
          residentialAccessFtr2);
      residentialImportanceAccess.getFactorTypeRatings().add(
          residentialAccessFtr3);
      residentialImportanceAccess.getFactorTypeRatings().add(
          residentialAccessFtr4);

      residentialSuitabilityRule.getFactorImportances().add(
          residentialImportanceAccess);

      // residential Flood importance
      FactorImportance residentialImportanceFlood = new FactorImportance();
      residentialImportanceFlood.setFactor(flood);
      residentialImportanceFlood.setImportance(25.0);
      residentialImportanceFlood
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      residentialImportanceFlood.setSuitabilityRule(residentialSuitabilityRule);

      FactorTypeRating residentialFloodFtr1 = new FactorTypeRating();
      residentialFloodFtr1.setFactorType(floodft1);
      residentialFloodFtr1.setScore(100.0);

      FactorTypeRating residentialFloodFtr2 = new FactorTypeRating();
      residentialFloodFtr2.setFactorType(floodft2);
      residentialFloodFtr2.setScore(0.0);

      residentialFloodFtr1.setFactorImportance(residentialImportanceFlood);
      residentialFloodFtr2.setFactorImportance(residentialImportanceFlood);

      residentialImportanceFlood.getFactorTypeRatings().add(
          residentialFloodFtr1);
      residentialImportanceFlood.getFactorTypeRatings().add(
          residentialFloodFtr2);

      residentialSuitabilityRule.getFactorImportances().add(
          residentialImportanceFlood);

      Set<AllocationLU> convertibleLUsResidential = new HashSet<AllocationLU>();
      convertibleLUsResidential.add(agricultureLU);
      convertibleLUsResidential.add(undevelopedLU);
      residentialSuitabilityRule.setConvertibleLUs(convertibleLUsResidential);
      suitabilityScenario.getSuitabilityRules().add(residentialSuitabilityRule);

      // Mixed Use Suitability Rule
      SuitabilityRule mixedUseSuitabilityRule = new SuitabilityRule();
      mixedUseSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
      mixedUseSuitabilityRule.setSuitabilityLU(mixedUse);
      mixedUseSuitabilityRule
          .setFactorImportances(new HashSet<FactorImportance>());

      // mixedUse Slope importance
      FactorImportance mixedUseImportanceSlope = new FactorImportance();
      mixedUseImportanceSlope.setFactor(slopes);
      mixedUseImportanceSlope.setImportance(100.0);
      mixedUseImportanceSlope
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      mixedUseImportanceSlope.setSuitabilityRule(mixedUseSuitabilityRule);

      FactorTypeRating mixedUseSlopeFtr1 = new FactorTypeRating();
      mixedUseSlopeFtr1.setFactorType(slopeft1);
      mixedUseSlopeFtr1.setScore(100.0);

      FactorTypeRating mixedUseSlopeFtr2 = new FactorTypeRating();
      mixedUseSlopeFtr2.setFactorType(slopeft2);
      mixedUseSlopeFtr2.setScore(25.0);

      FactorTypeRating mixedUseSlopeFtr3 = new FactorTypeRating();
      mixedUseSlopeFtr3.setFactorType(slopeft3);
      mixedUseSlopeFtr3.setScore(0.0);

      FactorTypeRating mixedUseSlopeFtr4 = new FactorTypeRating();
      mixedUseSlopeFtr4.setFactorType(slopeft4);
      mixedUseSlopeFtr4.setScore(0.0);

      FactorTypeRating mixedUseSlopeFtr5 = new FactorTypeRating();
      mixedUseSlopeFtr5.setFactorType(slopeft5);
      mixedUseSlopeFtr5.setScore(0.0);

      mixedUseSlopeFtr1.setFactorImportance(mixedUseImportanceSlope);
      mixedUseSlopeFtr2.setFactorImportance(mixedUseImportanceSlope);
      mixedUseSlopeFtr3.setFactorImportance(mixedUseImportanceSlope);
      mixedUseSlopeFtr4.setFactorImportance(mixedUseImportanceSlope);
      mixedUseSlopeFtr5.setFactorImportance(mixedUseImportanceSlope);

      mixedUseImportanceSlope.getFactorTypeRatings().add(mixedUseSlopeFtr1);
      mixedUseImportanceSlope.getFactorTypeRatings().add(mixedUseSlopeFtr2);
      mixedUseImportanceSlope.getFactorTypeRatings().add(mixedUseSlopeFtr3);
      mixedUseImportanceSlope.getFactorTypeRatings().add(mixedUseSlopeFtr4);
      mixedUseImportanceSlope.getFactorTypeRatings().add(mixedUseSlopeFtr5);

      mixedUseSuitabilityRule.getFactorImportances().add(
          mixedUseImportanceSlope);

      // mixedUse Access importance
      FactorImportance mixedUseImportanceAccess = new FactorImportance();
      mixedUseImportanceAccess.setFactor(access);
      mixedUseImportanceAccess.setImportance(100.0);
      mixedUseImportanceAccess
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      mixedUseImportanceAccess.setSuitabilityRule(mixedUseSuitabilityRule);

      FactorTypeRating mixedUseAccessFtr1 = new FactorTypeRating();
      mixedUseAccessFtr1.setFactorType(accessft1);
      mixedUseAccessFtr1.setScore(0.0);

      FactorTypeRating mixedUseAccessFtr2 = new FactorTypeRating();
      mixedUseAccessFtr2.setFactorType(accessft2);
      mixedUseAccessFtr2.setScore(33.0);

      FactorTypeRating mixedUseAccessFtr3 = new FactorTypeRating();
      mixedUseAccessFtr3.setFactorType(accessft3);
      mixedUseAccessFtr3.setScore(67.0);

      FactorTypeRating mixedUseAccessFtr4 = new FactorTypeRating();
      mixedUseAccessFtr4.setFactorType(accessft4);
      mixedUseAccessFtr4.setScore(100.0);

      mixedUseAccessFtr1.setFactorImportance(mixedUseImportanceAccess);
      mixedUseAccessFtr2.setFactorImportance(mixedUseImportanceAccess);
      mixedUseAccessFtr3.setFactorImportance(mixedUseImportanceAccess);
      mixedUseAccessFtr4.setFactorImportance(mixedUseImportanceAccess);

      mixedUseImportanceAccess.getFactorTypeRatings().add(mixedUseAccessFtr1);
      mixedUseImportanceAccess.getFactorTypeRatings().add(mixedUseAccessFtr2);
      mixedUseImportanceAccess.getFactorTypeRatings().add(mixedUseAccessFtr3);
      mixedUseImportanceAccess.getFactorTypeRatings().add(mixedUseAccessFtr4);

      mixedUseSuitabilityRule.getFactorImportances().add(
          mixedUseImportanceAccess);

      // mixedUse Flood importance
      FactorImportance mixedUseImportanceFlood = new FactorImportance();
      mixedUseImportanceFlood.setFactor(flood);
      mixedUseImportanceFlood.setImportance(100.0);
      mixedUseImportanceFlood
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      mixedUseImportanceFlood.setSuitabilityRule(mixedUseSuitabilityRule);

      FactorTypeRating mixedUseFloodFtr1 = new FactorTypeRating();
      mixedUseFloodFtr1.setFactorType(floodft1);
      mixedUseFloodFtr1.setScore(100.0);

      FactorTypeRating mixedUseFloodFtr2 = new FactorTypeRating();
      mixedUseFloodFtr2.setFactorType(floodft2);
      mixedUseFloodFtr2.setScore(0.0);

      mixedUseFloodFtr1.setFactorImportance(mixedUseImportanceFlood);
      mixedUseFloodFtr2.setFactorImportance(mixedUseImportanceFlood);

      mixedUseImportanceFlood.getFactorTypeRatings().add(mixedUseFloodFtr1);
      mixedUseImportanceFlood.getFactorTypeRatings().add(mixedUseFloodFtr2);

      mixedUseSuitabilityRule.getFactorImportances().add(
          mixedUseImportanceFlood);

      // mixed use conversions
      Set<AllocationLU> convertibleLUsmixedUse = new HashSet<AllocationLU>();
      convertibleLUsmixedUse.add(agricultureLU);
      convertibleLUsmixedUse.add(conservationLU);
      convertibleLUsmixedUse.add(residentialLowLU);
      convertibleLUsmixedUse.add(undevelopedLU);
      mixedUseSuitabilityRule.setConvertibleLUs(convertibleLUsmixedUse);
      suitabilityScenario.getSuitabilityRules().add(mixedUseSuitabilityRule);

      // Retail Suitability Rule
      SuitabilityRule retailSuitabilityRule = new SuitabilityRule();
      retailSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
      retailSuitabilityRule.setSuitabilityLU(retail);
      retailSuitabilityRule
          .setFactorImportances(new HashSet<FactorImportance>());

      // Retail Slope importance
      FactorImportance retailImportanceSlope = new FactorImportance();
      retailImportanceSlope.setFactor(slopes);
      retailImportanceSlope.setImportance(100.0);
      retailImportanceSlope
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      retailImportanceSlope.setSuitabilityRule(retailSuitabilityRule);

      FactorTypeRating retailSlopeFtr1 = new FactorTypeRating();
      retailSlopeFtr1.setFactorType(slopeft1);
      retailSlopeFtr1.setScore(100.0);

      FactorTypeRating retailSlopeFtr2 = new FactorTypeRating();
      retailSlopeFtr2.setFactorType(slopeft2);
      retailSlopeFtr2.setScore(25.0);

      FactorTypeRating retailSlopeFtr3 = new FactorTypeRating();
      retailSlopeFtr3.setFactorType(slopeft3);
      retailSlopeFtr3.setScore(0.0);

      FactorTypeRating retailSlopeFtr4 = new FactorTypeRating();
      retailSlopeFtr4.setFactorType(slopeft4);
      retailSlopeFtr4.setScore(0.0);

      FactorTypeRating retailSlopeFtr5 = new FactorTypeRating();
      retailSlopeFtr5.setFactorType(slopeft5);
      retailSlopeFtr5.setScore(0.0);

      retailSlopeFtr1.setFactorImportance(retailImportanceSlope);
      retailSlopeFtr2.setFactorImportance(retailImportanceSlope);
      retailSlopeFtr3.setFactorImportance(retailImportanceSlope);
      retailSlopeFtr4.setFactorImportance(retailImportanceSlope);
      retailSlopeFtr5.setFactorImportance(retailImportanceSlope);

      retailImportanceSlope.getFactorTypeRatings().add(retailSlopeFtr1);
      retailImportanceSlope.getFactorTypeRatings().add(retailSlopeFtr2);
      retailImportanceSlope.getFactorTypeRatings().add(retailSlopeFtr3);
      retailImportanceSlope.getFactorTypeRatings().add(retailSlopeFtr4);
      retailImportanceSlope.getFactorTypeRatings().add(retailSlopeFtr5);

      retailSuitabilityRule.getFactorImportances().add(retailImportanceSlope);

      // retail Access importance
      FactorImportance retailImportanceAccess = new FactorImportance();
      retailImportanceAccess.setFactor(access);
      retailImportanceAccess.setImportance(100.0);
      retailImportanceAccess
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      retailImportanceAccess.setSuitabilityRule(retailSuitabilityRule);

      FactorTypeRating retailAccessFtr1 = new FactorTypeRating();
      retailAccessFtr1.setFactorType(accessft1);
      retailAccessFtr1.setScore(0.0);

      FactorTypeRating retailAccessFtr2 = new FactorTypeRating();
      retailAccessFtr2.setFactorType(accessft2);
      retailAccessFtr2.setScore(33.0);

      FactorTypeRating retailAccessFtr3 = new FactorTypeRating();
      retailAccessFtr3.setFactorType(accessft3);
      retailAccessFtr3.setScore(67.0);

      FactorTypeRating retailAccessFtr4 = new FactorTypeRating();
      retailAccessFtr4.setFactorType(accessft4);
      retailAccessFtr4.setScore(100.0);

      retailAccessFtr1.setFactorImportance(retailImportanceAccess);
      retailAccessFtr2.setFactorImportance(retailImportanceAccess);
      retailAccessFtr3.setFactorImportance(retailImportanceAccess);
      retailAccessFtr4.setFactorImportance(retailImportanceAccess);

      retailImportanceAccess.getFactorTypeRatings().add(retailAccessFtr1);
      retailImportanceAccess.getFactorTypeRatings().add(retailAccessFtr2);
      retailImportanceAccess.getFactorTypeRatings().add(retailAccessFtr3);
      retailImportanceAccess.getFactorTypeRatings().add(retailAccessFtr4);

      retailSuitabilityRule.getFactorImportances().add(retailImportanceAccess);

      // retail Flood importance
      FactorImportance retailImportanceFlood = new FactorImportance();
      retailImportanceFlood.setFactor(flood);
      retailImportanceFlood.setImportance(100.0);
      retailImportanceFlood
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      retailImportanceFlood.setSuitabilityRule(retailSuitabilityRule);

      FactorTypeRating retailFloodFtr1 = new FactorTypeRating();
      retailFloodFtr1.setFactorType(floodft1);
      retailFloodFtr1.setScore(100.0);

      FactorTypeRating retailFloodFtr2 = new FactorTypeRating();
      retailFloodFtr2.setFactorType(floodft2);
      retailFloodFtr2.setScore(0.0);

      retailFloodFtr1.setFactorImportance(retailImportanceFlood);
      retailFloodFtr2.setFactorImportance(retailImportanceFlood);

      retailImportanceFlood.getFactorTypeRatings().add(retailFloodFtr1);
      retailImportanceFlood.getFactorTypeRatings().add(retailFloodFtr2);

      retailSuitabilityRule.getFactorImportances().add(retailImportanceFlood);

      // retail conversion
      Set<AllocationLU> convertibleLUsretail = new HashSet<AllocationLU>();
      convertibleLUsretail.add(agricultureLU);
      convertibleLUsretail.add(conservationLU);
      convertibleLUsretail.add(residentialLowLU);
      convertibleLUsretail.add(residentialMediumLU);
      convertibleLUsretail.add(undevelopedLU);
      retailSuitabilityRule.setConvertibleLUs(convertibleLUsretail);
      suitabilityScenario.getSuitabilityRules().add(retailSuitabilityRule);

      // Office Suitability Rule
      SuitabilityRule officeSuitabilityRule = new SuitabilityRule();
      officeSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
      officeSuitabilityRule.setSuitabilityLU(office);
      officeSuitabilityRule
          .setFactorImportances(new HashSet<FactorImportance>());

      // office Slope importance
      FactorImportance officeImportanceSlope = new FactorImportance();
      officeImportanceSlope.setFactor(slopes);
      officeImportanceSlope.setImportance(100.0);
      officeImportanceSlope
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      officeImportanceSlope.setSuitabilityRule(officeSuitabilityRule);

      FactorTypeRating officeSlopeFtr1 = new FactorTypeRating();
      officeSlopeFtr1.setFactorType(slopeft1);
      officeSlopeFtr1.setScore(100.0);

      FactorTypeRating officeSlopeFtr2 = new FactorTypeRating();
      officeSlopeFtr2.setFactorType(slopeft2);
      officeSlopeFtr2.setScore(25.0);

      FactorTypeRating officeSlopeFtr3 = new FactorTypeRating();
      officeSlopeFtr3.setFactorType(slopeft3);
      officeSlopeFtr3.setScore(0.0);

      FactorTypeRating officeSlopeFtr4 = new FactorTypeRating();
      officeSlopeFtr4.setFactorType(slopeft4);
      officeSlopeFtr4.setScore(0.0);

      FactorTypeRating officeSlopeFtr5 = new FactorTypeRating();
      officeSlopeFtr5.setFactorType(slopeft5);
      officeSlopeFtr5.setScore(0.0);

      officeSlopeFtr1.setFactorImportance(officeImportanceSlope);
      officeSlopeFtr2.setFactorImportance(officeImportanceSlope);
      officeSlopeFtr3.setFactorImportance(officeImportanceSlope);
      officeSlopeFtr4.setFactorImportance(officeImportanceSlope);
      officeSlopeFtr5.setFactorImportance(officeImportanceSlope);

      officeImportanceSlope.getFactorTypeRatings().add(officeSlopeFtr1);
      officeImportanceSlope.getFactorTypeRatings().add(officeSlopeFtr2);
      officeImportanceSlope.getFactorTypeRatings().add(officeSlopeFtr3);
      officeImportanceSlope.getFactorTypeRatings().add(officeSlopeFtr4);
      officeImportanceSlope.getFactorTypeRatings().add(officeSlopeFtr5);

      officeSuitabilityRule.getFactorImportances().add(officeImportanceSlope);

      // office Access importance
      FactorImportance officeImportanceAccess = new FactorImportance();
      officeImportanceAccess.setFactor(access);
      officeImportanceAccess.setImportance(100.0);
      officeImportanceAccess
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      officeImportanceAccess.setSuitabilityRule(officeSuitabilityRule);

      FactorTypeRating officeAccessFtr1 = new FactorTypeRating();
      officeAccessFtr1.setFactorType(accessft1);
      officeAccessFtr1.setScore(0.0);

      FactorTypeRating officeAccessFtr2 = new FactorTypeRating();
      officeAccessFtr2.setFactorType(accessft2);
      officeAccessFtr2.setScore(33.0);

      FactorTypeRating officeAccessFtr3 = new FactorTypeRating();
      officeAccessFtr3.setFactorType(accessft3);
      officeAccessFtr3.setScore(67.0);

      FactorTypeRating officeAccessFtr4 = new FactorTypeRating();
      officeAccessFtr4.setFactorType(accessft4);
      officeAccessFtr4.setScore(100.0);

      officeAccessFtr1.setFactorImportance(officeImportanceAccess);
      officeAccessFtr2.setFactorImportance(officeImportanceAccess);
      officeAccessFtr3.setFactorImportance(officeImportanceAccess);
      officeAccessFtr4.setFactorImportance(officeImportanceAccess);

      officeImportanceAccess.getFactorTypeRatings().add(officeAccessFtr1);
      officeImportanceAccess.getFactorTypeRatings().add(officeAccessFtr2);
      officeImportanceAccess.getFactorTypeRatings().add(officeAccessFtr3);
      officeImportanceAccess.getFactorTypeRatings().add(officeAccessFtr4);

      officeSuitabilityRule.getFactorImportances().add(officeImportanceAccess);

      // office Flood importance
      FactorImportance officeImportanceFlood = new FactorImportance();
      officeImportanceFlood.setFactor(flood);
      officeImportanceFlood.setImportance(100.0);
      officeImportanceFlood
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      officeImportanceFlood.setSuitabilityRule(officeSuitabilityRule);

      FactorTypeRating officeFloodFtr1 = new FactorTypeRating();
      officeFloodFtr1.setFactorType(floodft1);
      officeFloodFtr1.setScore(100.0);

      FactorTypeRating officeFloodFtr2 = new FactorTypeRating();
      officeFloodFtr2.setFactorType(floodft2);
      officeFloodFtr2.setScore(0.0);

      officeFloodFtr1.setFactorImportance(officeImportanceFlood);
      officeFloodFtr2.setFactorImportance(officeImportanceFlood);

      officeImportanceFlood.getFactorTypeRatings().add(officeFloodFtr1);
      officeImportanceFlood.getFactorTypeRatings().add(officeFloodFtr2);

      officeSuitabilityRule.getFactorImportances().add(officeImportanceFlood);

      // office conversions
      Set<AllocationLU> convertibleLUsoffice = new HashSet<AllocationLU>();
      convertibleLUsoffice.add(agricultureLU);
      convertibleLUsoffice.add(conservationLU);
      convertibleLUsoffice.add(residentialLowLU);
      convertibleLUsoffice.add(residentialMediumLU);
      convertibleLUsoffice.add(undevelopedLU);
      officeSuitabilityRule.setConvertibleLUs(convertibleLUsoffice);
      suitabilityScenario.getSuitabilityRules().add(officeSuitabilityRule);

      // industrial Suitability Rule
      SuitabilityRule industrialSuitabilityRule = new SuitabilityRule();
      industrialSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
      industrialSuitabilityRule.setSuitabilityLU(industrial);
      industrialSuitabilityRule
          .setFactorImportances(new HashSet<FactorImportance>());

      // industrial Slope importance
      FactorImportance industrialImportanceSlope = new FactorImportance();
      industrialImportanceSlope.setFactor(slopes);
      industrialImportanceSlope.setImportance(100.0);
      industrialImportanceSlope
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      industrialImportanceSlope.setSuitabilityRule(industrialSuitabilityRule);

      FactorTypeRating industrialSlopeFtr1 = new FactorTypeRating();
      industrialSlopeFtr1.setFactorType(slopeft1);
      industrialSlopeFtr1.setScore(100.0);

      FactorTypeRating industrialSlopeFtr2 = new FactorTypeRating();
      industrialSlopeFtr2.setFactorType(slopeft2);
      industrialSlopeFtr2.setScore(0.0);

      FactorTypeRating industrialSlopeFtr3 = new FactorTypeRating();
      industrialSlopeFtr3.setFactorType(slopeft3);
      industrialSlopeFtr3.setScore(0.0);

      FactorTypeRating industrialSlopeFtr4 = new FactorTypeRating();
      industrialSlopeFtr4.setFactorType(slopeft4);
      industrialSlopeFtr4.setScore(0.0);

      FactorTypeRating industrialSlopeFtr5 = new FactorTypeRating();
      industrialSlopeFtr5.setFactorType(slopeft5);
      industrialSlopeFtr5.setScore(0.0);

      industrialSlopeFtr1.setFactorImportance(industrialImportanceSlope);
      industrialSlopeFtr2.setFactorImportance(industrialImportanceSlope);
      industrialSlopeFtr3.setFactorImportance(industrialImportanceSlope);
      industrialSlopeFtr4.setFactorImportance(industrialImportanceSlope);
      industrialSlopeFtr5.setFactorImportance(industrialImportanceSlope);

      industrialImportanceSlope.getFactorTypeRatings().add(industrialSlopeFtr1);
      industrialImportanceSlope.getFactorTypeRatings().add(industrialSlopeFtr2);
      industrialImportanceSlope.getFactorTypeRatings().add(industrialSlopeFtr3);
      industrialImportanceSlope.getFactorTypeRatings().add(industrialSlopeFtr4);
      industrialImportanceSlope.getFactorTypeRatings().add(industrialSlopeFtr5);

      industrialSuitabilityRule.getFactorImportances().add(
          industrialImportanceSlope);

      // industrial Access importance
      FactorImportance industrialImportanceAccess = new FactorImportance();
      industrialImportanceAccess.setFactor(access);
      industrialImportanceAccess.setImportance(100.0);
      industrialImportanceAccess
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      industrialImportanceAccess.setSuitabilityRule(industrialSuitabilityRule);

      FactorTypeRating industrialAccessFtr1 = new FactorTypeRating();
      industrialAccessFtr1.setFactorType(accessft1);
      industrialAccessFtr1.setScore(0.0);

      FactorTypeRating industrialAccessFtr2 = new FactorTypeRating();
      industrialAccessFtr2.setFactorType(accessft2);
      industrialAccessFtr2.setScore(50.0);

      FactorTypeRating industrialAccessFtr3 = new FactorTypeRating();
      industrialAccessFtr3.setFactorType(accessft3);
      industrialAccessFtr3.setScore(50.0);

      FactorTypeRating industrialAccessFtr4 = new FactorTypeRating();
      industrialAccessFtr4.setFactorType(accessft4);
      industrialAccessFtr4.setScore(50.0);

      industrialAccessFtr1.setFactorImportance(industrialImportanceAccess);
      industrialAccessFtr2.setFactorImportance(industrialImportanceAccess);
      industrialAccessFtr3.setFactorImportance(industrialImportanceAccess);
      industrialAccessFtr4.setFactorImportance(industrialImportanceAccess);

      industrialImportanceAccess.getFactorTypeRatings().add(
          industrialAccessFtr1);
      industrialImportanceAccess.getFactorTypeRatings().add(
          industrialAccessFtr2);
      industrialImportanceAccess.getFactorTypeRatings().add(
          industrialAccessFtr3);
      industrialImportanceAccess.getFactorTypeRatings().add(
          industrialAccessFtr4);

      industrialSuitabilityRule.getFactorImportances().add(
          industrialImportanceAccess);

      // industrial Flood importance
      FactorImportance industrialImportanceFlood = new FactorImportance();
      industrialImportanceFlood.setFactor(flood);
      industrialImportanceFlood.setImportance(100.0);
      industrialImportanceFlood
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      industrialImportanceFlood.setSuitabilityRule(industrialSuitabilityRule);

      FactorTypeRating industrialFloodFtr1 = new FactorTypeRating();
      industrialFloodFtr1.setFactorType(floodft1);
      industrialFloodFtr1.setScore(100.0);

      FactorTypeRating industrialFloodFtr2 = new FactorTypeRating();
      industrialFloodFtr2.setFactorType(floodft2);
      industrialFloodFtr2.setScore(0.0);

      industrialFloodFtr1.setFactorImportance(industrialImportanceFlood);
      industrialFloodFtr2.setFactorImportance(industrialImportanceFlood);

      industrialImportanceFlood.getFactorTypeRatings().add(industrialFloodFtr1);
      industrialImportanceFlood.getFactorTypeRatings().add(industrialFloodFtr2);

      industrialSuitabilityRule.getFactorImportances().add(
          industrialImportanceFlood);

      // industrial conversions
      Set<AllocationLU> convertibleLUsIndustrial = new HashSet<AllocationLU>();
      convertibleLUsIndustrial.add(agricultureLU);
      convertibleLUsIndustrial.add(conservationLU);
      convertibleLUsIndustrial.add(residentialLowLU);
      convertibleLUsIndustrial.add(residentialMediumLU);
      convertibleLUsIndustrial.add(undevelopedLU);
      industrialSuitabilityRule.setConvertibleLUs(convertibleLUsIndustrial);
      suitabilityScenario.getSuitabilityRules().add(industrialSuitabilityRule);

      // conservation Suitability Rule
      SuitabilityRule conservationSuitabilityRule = new SuitabilityRule();
      conservationSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
      conservationSuitabilityRule.setSuitabilityLU(conservation);
      conservationSuitabilityRule
          .setFactorImportances(new HashSet<FactorImportance>());

      // conservation Slope importance
      FactorImportance conservationImportanceSlope = new FactorImportance();
      conservationImportanceSlope.setFactor(slopes);
      conservationImportanceSlope.setImportance(100.0);
      conservationImportanceSlope
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      conservationImportanceSlope
          .setSuitabilityRule(conservationSuitabilityRule);

      FactorTypeRating conservationSlopeFtr1 = new FactorTypeRating();
      conservationSlopeFtr1.setFactorType(slopeft1);
      conservationSlopeFtr1.setScore(50.0);

      FactorTypeRating conservationSlopeFtr2 = new FactorTypeRating();
      conservationSlopeFtr2.setFactorType(slopeft2);
      conservationSlopeFtr2.setScore(50.0);

      FactorTypeRating conservationSlopeFtr3 = new FactorTypeRating();
      conservationSlopeFtr3.setFactorType(slopeft3);
      conservationSlopeFtr3.setScore(100.0);

      FactorTypeRating conservationSlopeFtr4 = new FactorTypeRating();
      conservationSlopeFtr4.setFactorType(slopeft4);
      conservationSlopeFtr4.setScore(100.0);

      FactorTypeRating conservationSlopeFtr5 = new FactorTypeRating();
      conservationSlopeFtr5.setFactorType(slopeft5);
      conservationSlopeFtr5.setScore(100.0);

      conservationSlopeFtr1.setFactorImportance(conservationImportanceSlope);
      conservationSlopeFtr2.setFactorImportance(conservationImportanceSlope);
      conservationSlopeFtr3.setFactorImportance(conservationImportanceSlope);
      conservationSlopeFtr4.setFactorImportance(conservationImportanceSlope);
      conservationSlopeFtr5.setFactorImportance(conservationImportanceSlope);

      conservationImportanceSlope.getFactorTypeRatings().add(
          conservationSlopeFtr1);
      conservationImportanceSlope.getFactorTypeRatings().add(
          conservationSlopeFtr2);
      conservationImportanceSlope.getFactorTypeRatings().add(
          conservationSlopeFtr3);
      conservationImportanceSlope.getFactorTypeRatings().add(
          conservationSlopeFtr4);
      conservationImportanceSlope.getFactorTypeRatings().add(
          conservationSlopeFtr5);

      conservationSuitabilityRule.getFactorImportances().add(
          conservationImportanceSlope);

      // conservation Flood importance
      FactorImportance conservationImportanceFlood = new FactorImportance();
      conservationImportanceFlood.setFactor(flood);
      conservationImportanceFlood.setImportance(100.0);
      conservationImportanceFlood
          .setFactorTypeRatings(new HashSet<FactorTypeRating>());
      conservationImportanceFlood
          .setSuitabilityRule(conservationSuitabilityRule);
      FactorTypeRating conservationFloodFtr1 = new FactorTypeRating();
      conservationFloodFtr1.setFactorType(floodft1);
      conservationFloodFtr1.setScore(50.0);

      FactorTypeRating conservationFloodFtr2 = new FactorTypeRating();
      conservationFloodFtr2.setFactorType(floodft2);
      conservationFloodFtr2.setScore(100.0);

      conservationFloodFtr1.setFactorImportance(conservationImportanceFlood);
      conservationFloodFtr2.setFactorImportance(conservationImportanceFlood);

      conservationImportanceFlood.getFactorTypeRatings().add(
          conservationFloodFtr1);
      conservationImportanceFlood.getFactorTypeRatings().add(
          conservationFloodFtr2);

      conservationSuitabilityRule.getFactorImportances().add(
          conservationImportanceFlood);

      // conservation conversions
      Set<AllocationLU> convertibleLUsConservation = new HashSet<AllocationLU>();
      convertibleLUsConservation.add(agricultureLU);
      convertibleLUsConservation.add(undevelopedLU);
      conservationSuitabilityRule.setConvertibleLUs(convertibleLUsConservation);
      suitabilityScenario.getSuitabilityRules()
          .add(conservationSuitabilityRule);

      // Adding another importance with weighting 0
      SuitabilityLU suitabilityLU = project
          .getSuitabilityLUByName("Residential");

      SuitabilityRule rule = suitabilityScenario
          .getLandUseConversionBySLUName(suitabilityLU.getLabel());
      FactorImportance factorImportance = new FactorImportance();
      factorImportance.setFactor(streams);
      factorImportance.setImportance(0.0);

      FactorTypeRating ftr1 = new FactorTypeRating();
      ftr1.setFactorType(streamFt1);
      ftr1.setScore(0.0);
      ftr1.setFactorImportance(factorImportance);
      factorImportance.setFactorTypeRatings(new HashSet<FactorTypeRating>());
      factorImportance.getFactorTypeRatings().add(ftr1);

      FactorTypeRating ftr2 = new FactorTypeRating();
      ftr2.setFactorType(streamft2);
      ftr2.setScore(0.0);
      ftr2.setFactorImportance(factorImportance);
      factorImportance.getFactorTypeRatings().add(ftr2);

      FactorTypeRating ftr3 = new FactorTypeRating();
      ftr3.setFactorType(streamft3);
      ftr3.setScore(0.0);
      ftr3.setFactorImportance(factorImportance);
      factorImportance.getFactorTypeRatings().add(ftr3);

      FactorTypeRating ftr4 = new FactorTypeRating();
      ftr4.setFactorType(streamft4);
      ftr4.setScore(0.0);
      ftr4.setFactorImportance(factorImportance);
      factorImportance.getFactorTypeRatings().add(ftr4);

      factorImportance.setSuitabilityRule(rule);
      rule.getFactorImportances().add(factorImportance);

    } catch (WifInvalidInputException e) {
      LOGGER.error("Problem occurred in createSuitabilityModule");
    }
    return project;
  }
}
