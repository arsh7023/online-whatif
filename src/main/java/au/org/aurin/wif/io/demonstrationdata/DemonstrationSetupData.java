/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.io.demonstrationdata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.LandUseFunction;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemonstrationSetupData.
 */
public class DemonstrationSetupData {

  /**
   * Creates the project.
   * 
   * @return the wif project
   */
  public static WifProject createProject() {
    WifProject project = new WifProject();

    // Creating basic project
    project.setName("Demonstration");
    project.setOriginalUnits("metric");
    project.setRoleOwner("aurin");
    project.setAnalysisOption("Land Use/Population/Employment Analysis");
    project.setOriginalUnits("m.k.s.");
    project.setSrs("EPSG:102723");
    project.setGeometryColumnName("the_geom");

    project.setExistingLUAttributeName("ELU");
    project.setStudyArea("Ohio/ Edge City");
    project.setBbox("[1769115, 749019, 1803970, 790850]");
    project.setReady(true);
    project.setAreaLabel(WifKeys.DEFAULT_AREA_COLUMN_NAME);

    // it is commented so that tests do not have to create a UAZ every time they
    // run
    // project.setSetupCompleted(true);

    // Creating Suitability Config
    SuitabilityConfig suitabilityConfig = new SuitabilityConfig();
    suitabilityConfig.setWifProject(project);
    project.setSuitabilityConfig(suitabilityConfig);
    project.getSuitabilityConfig().setUnifiedAreaZone("uaz_demonstration");
    project.getSuitabilityConfig().setNotDevelopableScore(
        WifKeys.NOT_DEVELOPABLE_SCORE);
    project.getSuitabilityConfig().setNotSuitableScore(
        WifKeys.NOT_SUITABLE_SCORE);
    project.getSuitabilityConfig().setNotConvertableScore(
        WifKeys.NOT_CONVERTABLE_SCORE);
    project.getSuitabilityConfig().setUndefinedScore(WifKeys.UNDEFINED_SCORE);
    Set<String> suitabilityColumns = new HashSet<String>(Arrays.asList(
        "FACTOR_1", "FACTOR_2", "FACTOR_3", "FACTOR_4", "FACTOR_5", "FACTOR_6",
        "ELU", "UAZ_AREA", "UAZ_POP"));
    Set<String> scoreColumns = new HashSet<String>(Arrays.asList("SCORE_1",
        "SCORE_2", "SCORE_3", "SCORE_4", "SCORE_5", "SCORE_6"));
    Set<String> suitabilityCategories = new HashSet<String>(
        WifKeys.DEFAULT_SUITABILITY_CATEGORIES);
    project.getSuitabilityConfig().setSuitabilityColumns(suitabilityColumns);
    project.getSuitabilityConfig().setScoreColumns(scoreColumns);
    return project;
  }

  /**
   * Creates the setup module.
   * 
   * @param project
   *          the project
   * @return the wif project
   */
  public static WifProject createSetupModule(WifProject project) {

    // Creating allocation land uses
    AllocationLU residentialLowLU = new AllocationLU();
    residentialLowLU.setLabel("Low Density Res.");
    residentialLowLU.setNewPreservation(false);
    residentialLowLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    residentialLowLU.setFeatureFieldName("1.0");
    residentialLowLU.setWifProject(project);
    project.getAllocationLandUses().add(residentialLowLU);

    AllocationLU residentialMediumLU = new AllocationLU();
    residentialMediumLU.setLabel("Med Density Res.");
    residentialMediumLU.setNewPreservation(false);
    residentialMediumLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    residentialMediumLU.setFeatureFieldName("2.0");
    residentialMediumLU.setWifProject(project);
    project.getAllocationLandUses().add(residentialMediumLU);

    AllocationLU residentialmixedUseLU = new AllocationLU();
    residentialmixedUseLU.setLabel("Mixed Use");
    residentialmixedUseLU.setNewPreservation(false);
    residentialmixedUseLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    residentialmixedUseLU.setFeatureFieldName("3.0");
    residentialmixedUseLU.setWifProject(project);
    project.getAllocationLandUses().add(residentialmixedUseLU);

    AllocationLU nursingHomeLU = new AllocationLU();
    nursingHomeLU.setLabel("Nursing Home");
    nursingHomeLU.setGroupQuarters(true);
    nursingHomeLU.setNewPreservation(false);
    nursingHomeLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    nursingHomeLU.setFeatureFieldName("4.0");
    nursingHomeLU.setWifProject(project);
    project.getAllocationLandUses().add(nursingHomeLU);

    AllocationLU conservationLU = new AllocationLU();
    conservationLU.setLabel("Conservation");
    conservationLU.setNewPreservation(true);
    conservationLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    conservationLU.setFeatureFieldName("83.0");
    conservationLU.setWifProject(project);
    project.getAllocationLandUses().add(conservationLU);

    AllocationLU parkAndRecLocalLU = new AllocationLU();
    parkAndRecLocalLU.setLabel("Parks & Rec.");
    parkAndRecLocalLU.setLocal(true);
    parkAndRecLocalLU.setNewPreservation(false);
    parkAndRecLocalLU.setLandUseFunction(LandUseFunction.LBCS_5XXX);
    parkAndRecLocalLU.setFeatureFieldName("41.0");
    parkAndRecLocalLU.setWifProject(project);
    project.getAllocationLandUses().add(parkAndRecLocalLU);

    AllocationLU regionalRetailLU = new AllocationLU();
    regionalRetailLU.setLabel("Regional Retail");
    regionalRetailLU.setNewPreservation(false);
    regionalRetailLU.setLandUseFunction(LandUseFunction.LBCS_2XXX);
    regionalRetailLU.setFeatureFieldName("13.0");
    regionalRetailLU.setWifProject(project);
    project.getAllocationLandUses().add(regionalRetailLU);

    AllocationLU localRetailLU = new AllocationLU();
    localRetailLU.setLabel("Local Retail");
    localRetailLU.setNewPreservation(false);
    localRetailLU.setLandUseFunction(LandUseFunction.LBCS_2XXX);
    localRetailLU.setFeatureFieldName("11.0");
    localRetailLU.setWifProject(project);
    project.getAllocationLandUses().add(localRetailLU);

    AllocationLU officeLU = new AllocationLU();
    officeLU.setLabel("Office");
    officeLU.setNewPreservation(false);
    officeLU.setLandUseFunction(LandUseFunction.LBCS_2XXX);
    officeLU.setFeatureFieldName("12.0");
    officeLU.setWifProject(project);
    project.getAllocationLandUses().add(officeLU);

    AllocationLU publicSemiPubLU = new AllocationLU();
    publicSemiPubLU.setLabel("Public/Semi-pub.");
    publicSemiPubLU.setNewPreservation(false);
    publicSemiPubLU.setLandUseFunction(LandUseFunction.LBCS_5XXX);
    publicSemiPubLU.setFeatureFieldName("51.0");
    publicSemiPubLU.setWifProject(project);
    project.getAllocationLandUses().add(publicSemiPubLU);

    AllocationLU industrialLU = new AllocationLU();
    industrialLU.setLabel("Industrial");
    industrialLU.setNewPreservation(false);
    industrialLU.setLandUseFunction(LandUseFunction.LBCS_3XXX);
    industrialLU.setFeatureFieldName("21.0");
    industrialLU.setWifProject(project);
    project.getAllocationLandUses().add(industrialLU);

    AllocationLU agricultureLU = new AllocationLU();
    agricultureLU.setLabel("Agriculture");
    agricultureLU.setLandUseFunction(LandUseFunction.LBCS_9XXX);
    agricultureLU.setFeatureFieldName("82.0");
    agricultureLU.setWifProject(project);
    project.getAllocationLandUses().add(agricultureLU);

    AllocationLU undevelopedLU = new AllocationLU();
    undevelopedLU.setLabel("Undeveloped");
    undevelopedLU.setLandUseFunction(LandUseFunction.LBCS_9XXX);
    undevelopedLU.setFeatureFieldName("81.0");
    undevelopedLU.setWifProject(project);
    project.getAllocationLandUses().add(undevelopedLU);

    AllocationLU waterLU = new AllocationLU();
    waterLU.setLabel("Water");
    waterLU.setNotDevelopable(true);
    waterLU.setLandUseFunction(LandUseFunction.NOT_DEVELOPABLE_OR_UNDEFINED);
    waterLU.setFeatureFieldName("92.0");
    waterLU.setWifProject(project);
    project.getAllocationLandUses().add(waterLU);

    AllocationLU rightOfWayLU = new AllocationLU();
    rightOfWayLU.setLabel("Right of Way");
    rightOfWayLU
        .setLandUseFunction(LandUseFunction.NOT_DEVELOPABLE_OR_UNDEFINED);
    rightOfWayLU.setNotDevelopable(true);
    rightOfWayLU.setFeatureFieldName("91.0");
    rightOfWayLU.setWifProject(project);
    project.getAllocationLandUses().add(rightOfWayLU);

    // Creating suitability land uses
    SuitabilityLU residential = new SuitabilityLU();
    residential.setFeatureFieldName("SCORE_1");
    residential.setLabel("Residential");
    residential.setWifProject(project);
    residential.setAssociatedALUs(new HashSet<AllocationLU>());
    residential.getAssociatedALUs().add(residentialLowLU);
    residential.getAssociatedALUs().add(residentialMediumLU);
    residential.getAssociatedALUs().add(residentialmixedUseLU);
    residential.getAssociatedALUs().add(nursingHomeLU);
    project.getSuitabilityLUs().add(residential);

    SuitabilityLU mixedUse = new SuitabilityLU();
    mixedUse.setFeatureFieldName("SCORE_2");
    mixedUse.setLabel("Mixed Use");
    mixedUse.setWifProject(project);
    mixedUse.setAssociatedALUs(new HashSet<AllocationLU>());
    mixedUse.getAssociatedALUs().add(residentialmixedUseLU);
    project.getSuitabilityLUs().add(mixedUse);

    SuitabilityLU retail = new SuitabilityLU();
    retail.setFeatureFieldName("SCORE_3");
    retail.setLabel("Retail");
    retail.setWifProject(project);
    retail.setAssociatedALUs(new HashSet<AllocationLU>());
    retail.getAssociatedALUs().add(localRetailLU);
    retail.getAssociatedALUs().add(regionalRetailLU);
    project.getSuitabilityLUs().add(retail);

    SuitabilityLU office = new SuitabilityLU();
    office.setFeatureFieldName("SCORE_4");
    office.setLabel("Office");
    office.setWifProject(project);
    office.setAssociatedALUs(new HashSet<AllocationLU>());
    office.getAssociatedALUs().add(officeLU);
    office.getAssociatedALUs().add(publicSemiPubLU);
    project.getSuitabilityLUs().add(office);

    SuitabilityLU industrial = new SuitabilityLU();
    industrial.setFeatureFieldName("SCORE_5");
    industrial.setLabel("Industrial");
    industrial.setWifProject(project);
    industrial.setAssociatedALUs(new HashSet<AllocationLU>());
    industrial.getAssociatedALUs().add(industrialLU);
    project.getSuitabilityLUs().add(industrial);

    SuitabilityLU conservation = new SuitabilityLU();
    conservation.setFeatureFieldName("SCORE_6");
    conservation.setLabel("Conservation");
    conservation.setWifProject(project);
    conservation.setAssociatedALUs(new HashSet<AllocationLU>());
    conservation.getAssociatedALUs().add(conservationLU);
    conservation.getAssociatedALUs().add(parkAndRecLocalLU);
    project.getSuitabilityLUs().add(conservation);

    // Adding factors
    Factor slopes = new Factor();
    slopes.setLabel("slopes");
    slopes.setFeatureFieldName("FACTOR_1");
    slopes.setWifProject(project);
    project.getFactors().add(slopes);

    Factor soils = new Factor();
    soils.setLabel("Prime Ag. Soils");
    soils.setFeatureFieldName("FACTOR_2");
    soils.setWifProject(project);
    project.getFactors().add(soils);

    Factor flood = new Factor();
    flood.setLabel("100-year flood");
    flood.setFeatureFieldName("FACTOR_4");
    flood.setWifProject(project);
    project.getFactors().add(flood);

    Factor streams = new Factor();
    streams.setLabel("streams");
    streams.setFeatureFieldName("FACTOR_6");
    streams.setWifProject(project);
    project.getFactors().add(streams);

    Factor access = new Factor();
    access.setLabel("access");
    access.setFeatureFieldName("FACTOR_7");
    access.setWifProject(project);
    project.getFactors().add(access);

    // Factor types slopes
    FactorType slopeft1 = new FactorType();
    slopeft1.setLabel("<6%");
    slopeft1.setNaturalOrder(1);
    slopeft1.setValue("1.0");
    slopeft1.setFactor(slopes);

    FactorType slopeft2 = new FactorType();
    slopeft2.setLabel("6% - <12%");
    slopeft2.setNaturalOrder(2);
    slopeft2.setValue("2.0");
    slopeft2.setFactor(slopes);

    FactorType slopeft3 = new FactorType();
    slopeft3.setLabel("12% - <18%");
    slopeft3.setNaturalOrder(3);
    slopeft3.setValue("3.0");
    slopeft3.setFactor(slopes);

    FactorType slopeft4 = new FactorType();
    slopeft4.setLabel("18% - <25%");
    slopeft4.setNaturalOrder(4);
    slopeft4.setValue("4.0");
    slopeft4.setFactor(slopes);

    FactorType slopeft5 = new FactorType();
    slopeft5.setLabel(">=25%");
    slopeft5.setNaturalOrder(5);
    slopeft5.setValue("5.0");
    slopeft5.setFactor(slopes);

    slopes.setFactorTypes(new HashSet<FactorType>());
    slopes.addFactorType(slopeft1);
    slopes.addFactorType(slopeft2);
    slopes.addFactorType(slopeft3);
    slopes.addFactorType(slopeft4);
    slopes.addFactorType(slopeft5);

    // Factor types access
    FactorType accessft1 = new FactorType();
    accessft1.setLabel("Low");
    accessft1.setNaturalOrder(1);
    accessft1.setValue("1.0");
    accessft1.setFactor(access);

    FactorType accessft2 = new FactorType();
    accessft2.setLabel("Medium");
    accessft2.setNaturalOrder(2);
    accessft2.setValue("2.0");
    accessft2.setFactor(access);

    FactorType accessft3 = new FactorType();
    accessft3.setLabel("Medium High");
    accessft3.setNaturalOrder(3);
    accessft3.setValue("3.0");
    accessft3.setFactor(access);

    FactorType accessft4 = new FactorType();
    accessft4.setLabel("High");
    accessft4.setNaturalOrder(4);
    accessft4.setValue("4.0");
    accessft4.setFactor(access);

    access.setFactorTypes(new HashSet<FactorType>());
    access.addFactorType(accessft1);
    access.addFactorType(accessft2);
    access.addFactorType(accessft3);
    access.addFactorType(accessft4);

    // Factor types soils
    FactorType soilsft1 = new FactorType();
    soilsft1.setLabel("Prime Ag.");
    soilsft1.setNaturalOrder(1);
    soilsft1.setValue("1.0");
    soilsft1.setFactor(soils);

    FactorType soilsft2 = new FactorType();
    soilsft2.setLabel("Not Prime Ag.");
    soilsft2.setNaturalOrder(2);
    soilsft2.setValue("2.0"); // TODO why in the original SQL it was also
                              // 1.0??
    soilsft2.setFactor(soils);

    soils.setFactorTypes(new HashSet<FactorType>());
    soils.addFactorType(soilsft1);
    soils.addFactorType(soilsft2);

    // Factor types flood
    FactorType floodft1 = new FactorType();
    floodft1.setLabel("Outside Flood");
    floodft1.setNaturalOrder(1);
    floodft1.setValue("1.0");
    floodft1.setFactor(flood);

    FactorType floodft2 = new FactorType();
    floodft2.setLabel("Inside Flood");
    floodft2.setNaturalOrder(2);
    floodft2.setValue("2.0");
    floodft2.setFactor(flood);

    flood.setFactorTypes(new HashSet<FactorType>());
    flood.addFactorType(floodft1);
    flood.addFactorType(floodft2);

    // Factor types stream
    FactorType streamFt1 = new FactorType();
    streamFt1.setLabel("<100'");
    streamFt1.setNaturalOrder(1);
    streamFt1.setValue("1.0");
    streamFt1.setFactor(streams);

    FactorType streamft2 = new FactorType();
    streamft2.setLabel("<100' - <250'");
    streamft2.setNaturalOrder(2);
    streamft2.setValue("2.0");
    streamft2.setFactor(streams);

    FactorType streamft3 = new FactorType();
    streamft3.setLabel("<250' - <500'");
    streamft3.setNaturalOrder(3);
    streamft3.setValue("3.0");
    streamft3.setFactor(streams);

    FactorType streamft4 = new FactorType();
    streamft4.setLabel("Outside buffers");
    streamft4.setNaturalOrder(4);
    streamft4.setValue("-99.0");
    streamft4.setFactor(streams);

    streams.setFactorTypes(new HashSet<FactorType>());
    streams.addFactorType(streamFt1);
    streams.addFactorType(streamft2);
    streams.addFactorType(streamft3);
    streams.addFactorType(streamft4);

    return project;
  }
}
