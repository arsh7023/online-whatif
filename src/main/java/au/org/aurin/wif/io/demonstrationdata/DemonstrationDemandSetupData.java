/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.io.demonstrationdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.CurrentDemographic;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.EmploymentEntry;
import au.org.aurin.wif.model.demand.EmploymentPastTrendInfo;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.model.demand.ResidentialPastTrendInfo;
import au.org.aurin.wif.model.demand.info.EmploymentCurrentData;
import au.org.aurin.wif.model.demand.info.ResidentialCurrentData;

/**
 * The Class DemonstrationDemandSetupData.
 */
public class DemonstrationDemandSetupData {

  /**
   * Creates the demand setup module.
   * 
   * @param project
   *          the project
   * @return the wif project
   */

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemonstrationDemandSetupData.class);

  public static WifProject createDemandSetupModule(final WifProject project) {

    try {
      final AllocationLU residentialLowLU = project
          .getExistingLandUseByLabel("Low Density Res.");

      final AllocationLU residentialMediumLU = project
          .getExistingLandUseByLabel("Med Density Res.");

      final AllocationLU residentialmixedUseLU = project
          .getExistingLandUseByLabel("Mixed Use");

      final AllocationLU nursingHomeLU = project
          .getExistingLandUseByLabel("Nursing Home");

      final AllocationLU conservationLU = project
          .getExistingLandUseByLabel("Conservation");

      final AllocationLU parkAndRecLocalLU = project
          .getExistingLandUseByLabel("Parks & Rec.");

      final AllocationLU regionalRetailLU = project
          .getExistingLandUseByLabel("Regional Retail");

      final AllocationLU localRetailLU = project
          .getExistingLandUseByLabel("Local Retail");

      final AllocationLU officeLU = project.getExistingLandUseByLabel("Office");

      final AllocationLU publicSemiPubLU = project
          .getExistingLandUseByLabel("Public/Semi-pub.");

      final AllocationLU industrialLU = project
          .getExistingLandUseByLabel("Industrial");

      // User setup data
      DemandConfig demandConfig = new DemandConfig();
      demandConfig.setBaseYear(2005);
      demandConfig.setTotalPopulationFeatureFieldName("UAZ_POP");
      demandConfig.setLocalDemandAreasFeatureFieldName("POLITICAL");
      demandConfig.setGroupQuartersPopulationFeatureFieldName("UAZ_GQ");
      demandConfig.setEnumerationDistrictAreaFeatureFieldName("BG_ACRES");
      demandConfig.setEnumerationDistrictFeatureFieldName("BLK_GRP");
      demandConfig
          .setClippedEnumerationDistrictAreaFeatureFieldName("CLIP_ACRES");
      demandConfig.setNumberOfHouseholdsFeatureFieldName("UAZ_HH");
      demandConfig.setNumberOfHousingUnitsFeatureFieldName("UAZ_HU");

      project.setDemandConfig(demandConfig);
      demandConfig.setWifProject(project);

      // Set allocation area
      residentialLowLU.setTotalArea(5520.86);

      residentialMediumLU.setTotalArea(705.90);

      residentialmixedUseLU.setTotalArea(3.57);

      nursingHomeLU.setTotalArea(7.73);

      conservationLU.setTotalArea(0.0);

      parkAndRecLocalLU.setTotalArea(943.10);

      regionalRetailLU.setTotalArea(330.99);

      localRetailLU.setTotalArea(255.03);

      officeLU.setTotalArea(442.33);

      publicSemiPubLU.setTotalArea(710.20);

      industrialLU.setTotalArea(532.76);

      // Create sector data
      final EmploymentSector sectorRetailTrade = new EmploymentSector();
      sectorRetailTrade.setCode("NAICS 44-45");
      sectorRetailTrade.setLabel("Retail Trade");
      final Set<AllocationLU> associatedLUsA = new HashSet<AllocationLU>();
      sectorRetailTrade.setAssociatedLUs(associatedLUsA);
      sectorRetailTrade.setWifProject(project);

      final EmploymentSector sectorAgricultureHunting = new EmploymentSector();
      sectorAgricultureHunting.setCode("NAICS 11");
      sectorAgricultureHunting.setLabel("Ag/Forest/Fish/Hunt");
      sectorAgricultureHunting.setAssociatedLUs(new HashSet<AllocationLU>());
      sectorAgricultureHunting.setWifProject(project);

      final EmploymentSector sectorRealEstate = new EmploymentSector();
      sectorRealEstate.setCode("NAICS 53");
      sectorRealEstate.setLabel("Real Estate/Rental");
      sectorRealEstate.setAssociatedLUs(new HashSet<AllocationLU>());
      sectorRealEstate.setWifProject(project);

      final EmploymentSector sectorHealthcare = new EmploymentSector();
      sectorHealthcare.setCode("NAICS 62");
      sectorHealthcare.setLabel("Healthcare");
      sectorHealthcare.setAssociatedLUs(new HashSet<AllocationLU>());
      sectorHealthcare.setWifProject(project);

      final EmploymentSector sectorArts = new EmploymentSector();
      sectorArts.setCode("NAICS 71");
      sectorArts.setLabel("Arts/Entertain/Rec.");
      sectorArts.setAssociatedLUs(new HashSet<AllocationLU>());
      sectorArts.setWifProject(project);

      final EmploymentSector sectorFood = new EmploymentSector();
      sectorFood.setCode("NAICS 72");
      sectorFood.setLabel("Accommodation/Food");
      sectorFood.setAssociatedLUs(new HashSet<AllocationLU>());
      sectorFood.setWifProject(project);

      final EmploymentSector sectorMining = new EmploymentSector();
      sectorMining.setCode("NAICS 21");
      sectorMining.setLabel("Mining");
      sectorMining.setAssociatedLUs(new HashSet<AllocationLU>());
      sectorMining.setWifProject(project);

      final EmploymentSector sectorBInformation = new EmploymentSector();
      sectorBInformation.setCode("NAICS 51");
      sectorBInformation.setLabel("Information");
      final Set<AllocationLU> associatedLUsB = new HashSet<AllocationLU>();
      sectorBInformation.setAssociatedLUs(associatedLUsB);
      sectorBInformation.setWifProject(project);

      final EmploymentSector sectorCFinance = new EmploymentSector();
      sectorCFinance.setCode("NAICS 52");
      sectorCFinance.setLabel("Finance/Insurance");
      final Set<AllocationLU> associatedLUsC = new HashSet<AllocationLU>();
      sectorCFinance.setAssociatedLUs(associatedLUsC);
      sectorCFinance.setWifProject(project);

      final EmploymentSector sectorDManagement = new EmploymentSector();
      sectorDManagement.setCode("NAICS 55");
      sectorDManagement.setLabel("Management");
      final Set<AllocationLU> associatedLUsD = new HashSet<AllocationLU>();
      sectorDManagement.setAssociatedLUs(associatedLUsD);
      sectorDManagement.setWifProject(project);

      final EmploymentSector sectorESupport = new EmploymentSector();
      sectorESupport.setCode("NAICS 56");
      sectorESupport.setLabel("Admin/Support");
      final Set<AllocationLU> associatedLUsE = new HashSet<AllocationLU>();
      sectorESupport.setAssociatedLUs(associatedLUsE);
      sectorESupport.setWifProject(project);

      final EmploymentSector sectorFProfessional = new EmploymentSector();
      sectorFProfessional.setCode("NAICS 54");
      sectorFProfessional.setLabel("Professional");
      final Set<AllocationLU> associatedLUsF = new HashSet<AllocationLU>();
      sectorFProfessional.setAssociatedLUs(associatedLUsF);
      sectorFProfessional.setWifProject(project);

      final EmploymentSector sectorGUtilities = new EmploymentSector();
      sectorGUtilities.setCode("NAICS 22");
      sectorGUtilities.setLabel("Utilities");
      final Set<AllocationLU> associatedLUsG = new HashSet<AllocationLU>();
      sectorGUtilities.setAssociatedLUs(associatedLUsG);
      sectorGUtilities.setWifProject(project);

      final EmploymentSector sectorHEducational = new EmploymentSector();
      sectorHEducational.setCode("NAICS 61");
      sectorHEducational.setLabel("Educational Services");
      final Set<AllocationLU> associatedLUsH = new HashSet<AllocationLU>();
      sectorHEducational.setAssociatedLUs(associatedLUsH);
      sectorHEducational.setWifProject(project);

      final EmploymentSector sectorConstruction = new EmploymentSector();
      sectorConstruction.setCode("NAICS 23");
      sectorConstruction.setLabel("Construction");
      final Set<AllocationLU> associatedLUsIn = new HashSet<AllocationLU>();
      sectorConstruction.setAssociatedLUs(associatedLUsIn);
      sectorConstruction.setWifProject(project);

      final EmploymentSector sectorWholesale = new EmploymentSector();
      sectorWholesale.setCode("NAICS ");
      sectorWholesale.setLabel("Wholesale");
      final Set<AllocationLU> associatedLUsIw = new HashSet<AllocationLU>();
      sectorWholesale.setAssociatedLUs(associatedLUsIw);
      sectorWholesale.setWifProject(project);

      final EmploymentSector sectorTransport = new EmploymentSector();
      sectorTransport.setCode("NAICS ");
      sectorTransport.setLabel("Transport/Warehouse");
      final Set<AllocationLU> associatedLUsIt = new HashSet<AllocationLU>();
      sectorTransport.setAssociatedLUs(associatedLUsIt);
      sectorTransport.setWifProject(project);

      final EmploymentSector sectorManufacturing = new EmploymentSector();
      sectorManufacturing.setCode("NAICS ");
      sectorManufacturing.setLabel("Manufacturing");
      final Set<AllocationLU> associatedLUsm = new HashSet<AllocationLU>();
      sectorManufacturing.setAssociatedLUs(associatedLUsm);
      sectorManufacturing.setWifProject(project);

      final EmploymentSector sectorIOther = new EmploymentSector();
      sectorIOther.setCode("NAICS ?");
      sectorIOther.setLabel("Other Services");
      final Set<AllocationLU> associatedLUsI = new HashSet<AllocationLU>();
      sectorIOther.setAssociatedLUs(associatedLUsI);
      sectorIOther.setWifProject(project);

      // add employment sectors
      final Set<EmploymentSector> employmentSectors = new HashSet<EmploymentSector>();
      employmentSectors.add(sectorIOther);
      employmentSectors.add(sectorRetailTrade);
      employmentSectors.add(sectorAgricultureHunting);
      employmentSectors.add(sectorBInformation);
      employmentSectors.add(sectorCFinance);
      employmentSectors.add(sectorDManagement);
      employmentSectors.add(sectorESupport);
      employmentSectors.add(sectorFProfessional);
      employmentSectors.add(sectorGUtilities);
      employmentSectors.add(sectorHEducational);
      employmentSectors.add(sectorRealEstate);
      employmentSectors.add(sectorHealthcare);
      employmentSectors.add(sectorArts);
      employmentSectors.add(sectorFood);
      employmentSectors.add(sectorMining);
      employmentSectors.add(sectorConstruction);
      employmentSectors.add(sectorManufacturing);
      employmentSectors.add(sectorTransport);
      employmentSectors.add(sectorWholesale);
      employmentSectors.add(sectorIOther);
      project.setSectors(employmentSectors);

      // Associating sectors to ALUs
      sectorHEducational.addAssociatedLUs(publicSemiPubLU);
      sectorGUtilities.addAssociatedLUs(publicSemiPubLU);
      publicSemiPubLU.addEmploymentSector(sectorHEducational);
      publicSemiPubLU.addEmploymentSector(sectorGUtilities);
      sectorRetailTrade.addAssociatedLUs(localRetailLU);
      localRetailLU.addEmploymentSector(sectorRetailTrade);

      // associate regional retail LU with retail trade sector
      sectorRetailTrade.addAssociatedLUs(regionalRetailLU);
      regionalRetailLU.addEmploymentSector(sectorRetailTrade);

      // associate Office LU with other services Support,, Management,
      // Professional, Real estate,, Findings, Informationsector, many to
      // many
      // relationship
      // empLUOffice
      sectorBInformation.addAssociatedLUs(officeLU);
      sectorCFinance.addAssociatedLUs(officeLU);
      sectorDManagement.addAssociatedLUs(officeLU);
      sectorESupport.addAssociatedLUs(officeLU);
      sectorFProfessional.addAssociatedLUs(officeLU);
      sectorIOther.addAssociatedLUs(officeLU);

      officeLU.addEmploymentSector(sectorBInformation);
      officeLU.addEmploymentSector(sectorCFinance);
      officeLU.addEmploymentSector(sectorDManagement);
      officeLU.addEmploymentSector(sectorESupport);
      officeLU.addEmploymentSector(sectorFProfessional);
      officeLU.addEmploymentSector(sectorIOther);

      // associate IndustrialLU with educational services And utilities
      // sector,
      // many to many
      // relationship
      sectorManufacturing.addAssociatedLUs(industrialLU);
      sectorGUtilities.addAssociatedLUs(industrialLU);
      sectorTransport.addAssociatedLUs(industrialLU);
      sectorConstruction.addAssociatedLUs(industrialLU);
      sectorESupport.addAssociatedLUs(industrialLU);
      sectorWholesale.addAssociatedLUs(industrialLU);
      sectorDManagement.addAssociatedLUs(industrialLU);
      industrialLU.addEmploymentSector(sectorManufacturing);
      industrialLU.addEmploymentSector(sectorGUtilities);
      industrialLU.addEmploymentSector(sectorTransport);
      industrialLU.addEmploymentSector(sectorConstruction);
      industrialLU.addEmploymentSector(sectorESupport);
      industrialLU.addEmploymentSector(sectorWholesale);
      industrialLU.addEmploymentSector(sectorDManagement);

      // Create local jurisdictions
      final LocalJurisdiction ljcentralCity = new LocalJurisdiction();
      ljcentralCity.setLabel("Central City");

      final LocalJurisdiction ljedgeTownship = new LocalJurisdiction();
      ljedgeTownship.setLabel("Edge Township");

      final LocalJurisdiction ljedgeCity = new LocalJurisdiction();
      ljedgeCity.setLabel("Edge City");

      final Set<LocalJurisdiction> localLocalJurisdictions = new HashSet<LocalJurisdiction>();
      localLocalJurisdictions.add(ljcentralCity);
      localLocalJurisdictions.add(ljedgeTownship);
      localLocalJurisdictions.add(ljedgeCity);
      demandConfig.setLocalJurisdictions(localLocalJurisdictions);

      // Create projections years
      final Projection projection0 = new Projection();
      final Projection projection1 = new Projection();
      final Projection projection2 = new Projection();

      projection0.setLabel("2005");
      projection0.setYear(2005);
      projection0.setWifProject(project);

      projection1.setLabel("2010");
      projection1.setYear(2010);
      projection1.setWifProject(project);

      projection2.setLabel("2015");
      projection2.setYear(2015);
      projection2.setWifProject(project);

      final Set<Projection> projections = new TreeSet<Projection>(
          new YearComparator());
      projections.add(projection0);
      projections.add(projection1);
      projections.add(projection2);

      project.setProjections(projections);

      // Create current demographic information
      final CurrentDemographic currentDemographic = new CurrentDemographic();
      currentDemographic.setLabel("2005");
      currentDemographic.setYear(2005);
      final Set<EmploymentCurrentData> employmentCurrentDatas = new HashSet<EmploymentCurrentData>();
      currentDemographic.setEmploymentCurrentDatas(employmentCurrentDatas);

      demandConfig.setCurrentDemographic(currentDemographic);
      demandConfig.getCurrentDemographic().setTotalPopulation(18523L);
      demandConfig.getCurrentDemographic().setgQPopulation(117L);
      demandConfig.getCurrentDemographic().setHousingUnits(8130L);
      final long totalHousingUnits = demandConfig.getCurrentDemographic()
          .getHousingUnits();
      demandConfig.getCurrentDemographic().setHouseholds(7347.0);
      demandConfig.getCurrentDemographic().setVacantLand(678.46);

      // Residential current demographic data
      final ResidentialCurrentData lowResData = new ResidentialCurrentData();
      lowResData.setNumberOfHousingUnits(5200);
      lowResData.setResidentialLU(residentialLowLU);
      lowResData.setVacancyRate(demandConfig.getCurrentDemographic()
          .getVacancyRate());
      lowResData.setDensity(residentialLowLU.getTotalArea()
          / lowResData.getNumberOfHousingUnits());
      lowResData.setBreakdownDensity((double) lowResData
          .getNumberOfHousingUnits() / (double) totalHousingUnits);
      lowResData.setCurrentDemographic(currentDemographic);

      final ResidentialCurrentData medResData = new ResidentialCurrentData();
      medResData.setNumberOfHousingUnits(2800);
      medResData.setResidentialLU(residentialMediumLU);
      medResData.setVacancyRate(demandConfig.getCurrentDemographic()
          .getVacancyRate());
      medResData.setDensity(residentialMediumLU.getTotalArea()
          / medResData.getNumberOfHousingUnits());
      medResData.setBreakdownDensity((double) medResData
          .getNumberOfHousingUnits() / (double) totalHousingUnits);

      medResData.setCurrentDemographic(currentDemographic);

      final ResidentialCurrentData mixedUseData = new ResidentialCurrentData();
      mixedUseData.setNumberOfHousingUnits(130);
      mixedUseData.setResidentialLU(residentialmixedUseLU);
      mixedUseData.setVacancyRate(demandConfig.getCurrentDemographic()
          .getVacancyRate());
      mixedUseData.setDensity(residentialmixedUseLU.getTotalArea()
          / mixedUseData.getNumberOfHousingUnits());
      mixedUseData.setBreakdownDensity((double) mixedUseData
          .getNumberOfHousingUnits() / (double) totalHousingUnits);
      mixedUseData.setCurrentDemographic(currentDemographic);

      // Employment current demographic data
      final EmploymentCurrentData ecDataRetailTrade = new EmploymentCurrentData();
      ecDataRetailTrade.setEmployees(5771L);
      ecDataRetailTrade.setSector(sectorRetailTrade);
      ecDataRetailTrade.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataRetailTrade);

      final EmploymentCurrentData ecDataAgricultureHunting = new EmploymentCurrentData();
      ecDataAgricultureHunting.setEmployees(80L);
      ecDataAgricultureHunting.setSector(sectorAgricultureHunting);
      ecDataAgricultureHunting.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(
          ecDataAgricultureHunting);

      final EmploymentCurrentData ecRealEstate = new EmploymentCurrentData();
      ecRealEstate.setEmployees(1068L);
      ecRealEstate.setSector(sectorRealEstate);
      ecRealEstate.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecRealEstate);

      final EmploymentCurrentData ecDataHealthcare = new EmploymentCurrentData();
      ecDataHealthcare.setEmployees(2791L);
      ecDataHealthcare.setSector(sectorHealthcare);
      ecDataHealthcare.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataHealthcare);

      final EmploymentCurrentData ecDataArts = new EmploymentCurrentData();
      ecDataArts.setEmployees(1354L);
      ecDataArts.setSector(sectorArts);
      ecDataArts.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataArts);

      final EmploymentCurrentData ecDataFood = new EmploymentCurrentData();
      ecDataFood.setEmployees(5661L);
      ecDataFood.setSector(sectorFood);
      ecDataFood.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataFood);

      final EmploymentCurrentData ecDataMining = new EmploymentCurrentData();
      ecDataMining.setEmployees(6L);
      ecDataMining.setSector(sectorMining);
      ecDataMining.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataMining);

      final EmploymentCurrentData ecDataInformation = new EmploymentCurrentData();
      ecDataInformation.setEmployees(3005L);
      ecDataInformation.setSector(sectorBInformation);
      ecDataInformation.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataInformation);

      final EmploymentCurrentData ecDataFinance = new EmploymentCurrentData();
      ecDataFinance.setEmployees(2945L);
      ecDataFinance.setSector(sectorCFinance);
      ecDataFinance.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataFinance);

      final EmploymentCurrentData ecDataManagement = new EmploymentCurrentData();
      ecDataManagement.setEmployees(3L);
      ecDataManagement.setSector(sectorDManagement);
      ecDataManagement.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataManagement);

      final EmploymentCurrentData ecDataSupport = new EmploymentCurrentData();
      ecDataSupport.setEmployees(1328L);
      ecDataSupport.setSector(sectorESupport);
      ecDataSupport.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataSupport);

      final EmploymentCurrentData ecDataProfessional = new EmploymentCurrentData();
      ecDataProfessional.setEmployees(4685L);
      ecDataProfessional.setSector(sectorFProfessional);
      ecDataProfessional.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataProfessional);

      final EmploymentCurrentData ecDataUtilities = new EmploymentCurrentData();
      ecDataUtilities.setEmployees(63L);
      ecDataUtilities.setSector(sectorGUtilities);
      ecDataUtilities.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataUtilities);

      final EmploymentCurrentData ecDataEducational = new EmploymentCurrentData();
      ecDataEducational.setEmployees(1796L);
      ecDataEducational.setSector(sectorHEducational);
      ecDataEducational.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataEducational);

      final EmploymentCurrentData ecDataConstruction = new EmploymentCurrentData();
      ecDataConstruction.setEmployees(2180L);
      ecDataConstruction.setSector(sectorConstruction);
      ecDataConstruction.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataConstruction);

      final EmploymentCurrentData ecDataWholesale = new EmploymentCurrentData();
      ecDataWholesale.setEmployees(4205L);
      ecDataWholesale.setSector(sectorWholesale);
      ecDataWholesale.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataWholesale);

      final EmploymentCurrentData ecDataTransport = new EmploymentCurrentData();
      ecDataTransport.setEmployees(719L);
      ecDataTransport.setSector(sectorTransport);
      ecDataTransport.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataTransport);

      final EmploymentCurrentData ecDataManufacturing = new EmploymentCurrentData();
      ecDataManufacturing.setEmployees(1164L);
      ecDataManufacturing.setSector(sectorManufacturing);
      ecDataManufacturing.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataManufacturing);

      final EmploymentCurrentData ecDataOther = new EmploymentCurrentData();
      ecDataOther.setEmployees(1736L);
      ecDataOther.setSector(sectorIOther);
      ecDataOther.setCurrentDemographic(currentDemographic);
      currentDemographic.getEmploymentCurrentDatas().add(ecDataOther);

      final Set<ResidentialCurrentData> residentialCurrentDatas = new HashSet<ResidentialCurrentData>();
      currentDemographic.setResidentialCurrentData(residentialCurrentDatas);
      currentDemographic.getResidentialCurrentData().add(lowResData);
      currentDemographic.getResidentialCurrentData().add(medResData);
      currentDemographic.getResidentialCurrentData().add(mixedUseData);
      project.getDemandConfig().setCurrentDemographic(currentDemographic);

      demandConfig = fillPastTrendDemandConfig(demandConfig);
      project.setDemandConfig(demandConfig);

    } catch (final WifInvalidInputException e) {
      LOGGER.error("Problem occurred in createDemandSetupModule");
    }
    return project;
  }

  /**
   * Fill past trend demand config. mmethod that is only for testing purposes
   * 
   * @param demandConfig
   *          the demand config
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public static DemandConfig fillPastTrendDemandConfig(
      final DemandConfig demandConfig) throws WifInvalidInputException {
    // Sectors information
    final EmploymentSector sectorRetailTrade = demandConfig
        .getSectorByLabel("Retail Trade");
    final EmploymentSector sectorAgricultureHunting = demandConfig
        .getSectorByLabel("Ag/Forest/Fish/Hunt");
    final EmploymentSector sectorRealEstate = demandConfig
        .getSectorByLabel("Real Estate/Rental");
    final EmploymentSector sectorHealthcare = demandConfig
        .getSectorByLabel("Healthcare");
    final EmploymentSector sectorArts = demandConfig
        .getSectorByLabel("Arts/Entertain/Rec.");
    final EmploymentSector sectorFood = demandConfig
        .getSectorByLabel("Accommodation/Food");
    final EmploymentSector sectorMining = demandConfig
        .getSectorByLabel("Mining");
    final EmploymentSector sectorBInformation = demandConfig
        .getSectorByLabel("Information");
    final EmploymentSector sectorCFinance = demandConfig
        .getSectorByLabel("Finance/Insurance");
    final EmploymentSector sectorDManagement = demandConfig
        .getSectorByLabel("Management");
    final EmploymentSector sectorESupport = demandConfig
        .getSectorByLabel("Admin/Support");
    final EmploymentSector sectorFProfessional = demandConfig
        .getSectorByLabel("Professional");
    final EmploymentSector sectorGUtilities = demandConfig
        .getSectorByLabel("Utilities");
    final EmploymentSector sectorHEducational = demandConfig
        .getSectorByLabel("Educational Services");
    final EmploymentSector sectorConstruction = demandConfig
        .getSectorByLabel("Construction");
    final EmploymentSector sectorWholesale = demandConfig
        .getSectorByLabel("Wholesale");
    final EmploymentSector sectorTransport = demandConfig
        .getSectorByLabel("Transport/Warehouse");
    final EmploymentSector sectorManufacturing = demandConfig
        .getSectorByLabel("Manufacturing");
    final EmploymentSector sectorIOther = demandConfig
        .getSectorByLabel("Other Services");

    // Past trend residential data
    final ResidentialPastTrendInfo trend1990 = new ResidentialPastTrendInfo();
    trend1990.setYear(1990);
    trend1990.setLabel("1990");
    trend1990.setDemandConfig(demandConfig);
    trend1990.setTotalPopulation(13880L);
    trend1990.setgQPopulation(75L);
    trend1990.setHouseholds(4689L);

    final ResidentialPastTrendInfo trend2000 = new ResidentialPastTrendInfo();
    trend2000.setYear(2000);
    trend2000.setLabel("2000");
    trend2000.setDemandConfig(demandConfig);
    trend2000.setTotalPopulation(16423L);
    trend2000.setgQPopulation(117L);
    trend2000.setHouseholds(6344L);

    final Set<ResidentialPastTrendInfo> residentialPastTrends = new HashSet<ResidentialPastTrendInfo>();
    residentialPastTrends.add(trend1990);
    residentialPastTrends.add(trend2000);
    demandConfig.setResidentialPastTrendInfos(residentialPastTrends);

    // Past trend employment data
    final EmploymentPastTrendInfo trend1998 = new EmploymentPastTrendInfo();
    trend1998.setYear(1998);
    trend1998.setLabel("1998");
    trend1998.setDemandConfig(demandConfig);
    trend1998.setEmploymentEntries(new HashSet<EmploymentEntry>());

    final EmploymentEntry retailTradeEntry = new EmploymentEntry();
    retailTradeEntry.setEmployees(84771L);
    retailTradeEntry.setSector(sectorRetailTrade);
    retailTradeEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(retailTradeEntry);

    final EmploymentEntry agricultureEntry = new EmploymentEntry();
    agricultureEntry.setEmployees(850L);
    agricultureEntry.setSector(sectorAgricultureHunting);
    agricultureEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(agricultureEntry);

    final EmploymentEntry realEstateEntry = new EmploymentEntry();
    realEstateEntry.setEmployees(1182L);
    realEstateEntry.setSector(sectorRealEstate);
    realEstateEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(realEstateEntry);

    final EmploymentEntry healthcareEntry = new EmploymentEntry();
    healthcareEntry.setEmployees(71070L);
    healthcareEntry.setSector(sectorHealthcare);
    healthcareEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(healthcareEntry);

    final EmploymentEntry artsEntry = new EmploymentEntry();
    artsEntry.setEmployees(6573L);
    artsEntry.setSector(sectorArts);
    artsEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(artsEntry);

    final EmploymentEntry foodEntry = new EmploymentEntry();
    foodEntry.setEmployees(56142L);
    foodEntry.setSector(sectorFood);
    foodEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(foodEntry);

    final EmploymentEntry miningEntry = new EmploymentEntry();
    miningEntry.setEmployees(555L);
    miningEntry.setSector(sectorMining);
    miningEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(miningEntry);

    final EmploymentEntry informationEntry = new EmploymentEntry();
    informationEntry.setEmployees(19467L);
    informationEntry.setSector(sectorBInformation);
    informationEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(informationEntry);

    final EmploymentEntry financeEntry = new EmploymentEntry();
    financeEntry.setEmployees(66658L);
    financeEntry.setSector(sectorCFinance);
    financeEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(financeEntry);

    final EmploymentEntry managementEntry = new EmploymentEntry();
    managementEntry.setEmployees(2850L);
    managementEntry.setSector(sectorDManagement);
    managementEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(managementEntry);

    final EmploymentEntry supportEntry = new EmploymentEntry();
    supportEntry.setEmployees(18000L);
    supportEntry.setSector(sectorESupport);
    supportEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(supportEntry);

    final EmploymentEntry professionalEntry = new EmploymentEntry();
    professionalEntry.setEmployees(41542L);
    professionalEntry.setSector(sectorFProfessional);
    professionalEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(professionalEntry);

    final EmploymentEntry utilitiesEntry = new EmploymentEntry();
    utilitiesEntry.setEmployees(400L);
    utilitiesEntry.setSector(sectorGUtilities);
    utilitiesEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(utilitiesEntry);

    final EmploymentEntry educationalEntry = new EmploymentEntry();
    educationalEntry.setEmployees(51000L);
    educationalEntry.setSector(sectorHEducational);
    educationalEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(educationalEntry);

    final EmploymentEntry constructionEntry = new EmploymentEntry();
    constructionEntry.setEmployees(30000L);
    constructionEntry.setSector(sectorConstruction);
    constructionEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(constructionEntry);

    final EmploymentEntry wholesaleEntry = new EmploymentEntry();
    wholesaleEntry.setEmployees(39344L);
    wholesaleEntry.setSector(sectorWholesale);
    wholesaleEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(wholesaleEntry);

    final EmploymentEntry transportEntry = new EmploymentEntry();
    transportEntry.setEmployees(24300L);
    transportEntry.setSector(sectorTransport);
    transportEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(transportEntry);

    final EmploymentEntry manufacturingEntry = new EmploymentEntry();
    manufacturingEntry.setEmployees(55500L);
    manufacturingEntry.setSector(sectorManufacturing);
    manufacturingEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(manufacturingEntry);

    final EmploymentEntry otherEntry = new EmploymentEntry();
    otherEntry.setEmployees(31435L);
    otherEntry.setSector(sectorIOther);
    otherEntry.setEmploymentPastTrendInfo(trend1998);
    trend1998.getEmploymentEntries().add(otherEntry);

    // Trend 2003
    final EmploymentPastTrendInfo trend2003 = new EmploymentPastTrendInfo();
    trend2003.setYear(2003);
    trend2003.setLabel("2003");
    trend2003.setDemandConfig(demandConfig);

    trend2003.setEmploymentEntries(new HashSet<EmploymentEntry>());

    final EmploymentEntry retailTradeEntry2 = new EmploymentEntry();
    retailTradeEntry2.setEmployees(86500L);
    retailTradeEntry2.setSector(sectorRetailTrade);
    retailTradeEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(retailTradeEntry2);

    final EmploymentEntry agricultureEntry2 = new EmploymentEntry();
    agricultureEntry2.setEmployees(800L);
    agricultureEntry2.setSector(sectorAgricultureHunting);
    agricultureEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(agricultureEntry2);

    final EmploymentEntry realEstateEntry2 = new EmploymentEntry();
    realEstateEntry2.setEmployees(12500L);
    realEstateEntry2.setSector(sectorRealEstate);
    realEstateEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(realEstateEntry2);

    final EmploymentEntry healthcareEntry2 = new EmploymentEntry();
    healthcareEntry2.setEmployees(82517L);
    healthcareEntry2.setSector(sectorHealthcare);
    healthcareEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(healthcareEntry2);

    final EmploymentEntry artsEntry2 = new EmploymentEntry();
    artsEntry2.setEmployees(8338L);
    artsEntry2.setSector(sectorArts);
    artsEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(artsEntry2);

    final EmploymentEntry foodEntry2 = new EmploymentEntry();
    foodEntry2.setEmployees(58500L);
    foodEntry2.setSector(sectorFood);
    foodEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(foodEntry2);

    final EmploymentEntry miningEntry2 = new EmploymentEntry();
    miningEntry2.setEmployees(445L);
    miningEntry2.setSector(sectorMining);
    miningEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(miningEntry2);

    final EmploymentEntry informationEntry2 = new EmploymentEntry();
    informationEntry2.setEmployees(19947L);
    informationEntry2.setSector(sectorBInformation);
    informationEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(informationEntry2);

    final EmploymentEntry financeEntry2 = new EmploymentEntry();
    financeEntry2.setEmployees(54000L);
    financeEntry2.setSector(sectorCFinance);
    financeEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(financeEntry2);

    final EmploymentEntry managementEntry2 = new EmploymentEntry();
    managementEntry2.setEmployees(3100L);
    managementEntry2.setSector(sectorDManagement);
    managementEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(managementEntry2);

    final EmploymentEntry supportEntry2 = new EmploymentEntry();
    supportEntry2.setEmployees(18500L);
    supportEntry2.setSector(sectorESupport);
    supportEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(supportEntry2);

    final EmploymentEntry professionalEntry2 = new EmploymentEntry();
    professionalEntry2.setEmployees(43797L);
    professionalEntry2.setSector(sectorFProfessional);
    professionalEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(professionalEntry2);

    final EmploymentEntry utilitiesEntry2 = new EmploymentEntry();
    utilitiesEntry2.setEmployees(450L);
    utilitiesEntry2.setSector(sectorGUtilities);
    utilitiesEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(utilitiesEntry2);

    final EmploymentEntry educationalEntry2 = new EmploymentEntry();
    educationalEntry2.setEmployees(52500L);
    educationalEntry2.setSector(sectorHEducational);
    educationalEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(educationalEntry2);

    final EmploymentEntry constructionEntry2 = new EmploymentEntry();
    constructionEntry2.setEmployees(30200L);
    constructionEntry2.setSector(sectorConstruction);
    constructionEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(constructionEntry2);

    final EmploymentEntry wholesaleEntry2 = new EmploymentEntry();
    wholesaleEntry2.setEmployees(34500L);
    wholesaleEntry2.setSector(sectorWholesale);
    wholesaleEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(wholesaleEntry2);

    final EmploymentEntry transportEntry2 = new EmploymentEntry();
    transportEntry2.setEmployees(22500L);
    transportEntry2.setSector(sectorTransport);
    transportEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(transportEntry2);

    final EmploymentEntry manufacturingEntry2 = new EmploymentEntry();
    manufacturingEntry2.setEmployees(61050L);
    manufacturingEntry2.setSector(sectorManufacturing);
    manufacturingEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(manufacturingEntry2);

    final EmploymentEntry otherEntry2 = new EmploymentEntry();
    otherEntry2.setEmployees(32345L);
    otherEntry2.setSector(sectorIOther);
    otherEntry2.setEmploymentPastTrendInfo(trend2003);
    trend2003.getEmploymentEntries().add(otherEntry2);

    final Set<EmploymentPastTrendInfo> employmentPastTrends = new HashSet<EmploymentPastTrendInfo>();
    employmentPastTrends.add(trend1998);
    employmentPastTrends.add(trend2003);
    demandConfig.setEmploymentPastTrendInfos(employmentPastTrends);
    demandConfig.setIncludeTrends(true);

    return demandConfig;
  }

  /**
   * Creates the user define demand config.
   * 
   * @param localRetailLU
   *          the local retail lu
   * @return the demand config
   */
  public static DemandConfig createUserDefineDemandConfig(
      final AllocationLU localRetailLU) {
    final DemandConfig demandConfig = new DemandConfig();
    // Basic demand setup, UAZ fields
    demandConfig.setBaseYear(2005);
    demandConfig.setTotalPopulationFeatureFieldName("TOTPOP_CY");
    demandConfig.setGroupQuartersPopulationFeatureFieldName("GQPOP_CY");
    demandConfig.setEnumerationDistrictAreaFeatureFieldName("BG_ACRES");
    demandConfig
        .setClippedEnumerationDistrictAreaFeatureFieldName("CLIP_ACRES");
    demandConfig.setNumberOfHouseholdsFeatureFieldName("TOTHH_CY");
    demandConfig.setNumberOfHousingUnitsFeatureFieldName("TOTHU_CY");

    // Create projections years
    final Projection projection0 = new Projection();
    final Projection projection1 = new Projection();
    final Projection projection2 = new Projection();

    projection0.setLabel("2005");
    projection0.setYear(2005);

    projection1.setLabel("2010");
    projection1.setYear(2010);

    projection2.setLabel("2015");
    projection2.setYear(2015);

    final Set<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.add(projection0);
    projections.add(projection1);
    projections.add(projection2);

    demandConfig.setProjections(projections);

    // Add sector
    final EmploymentSector sectorRetailTrade = new EmploymentSector();
    sectorRetailTrade.setCode("NAICS 44-45");
    sectorRetailTrade.setLabel("Retail Trade");
    final Set<AllocationLU> associatedLUsA = new HashSet<AllocationLU>();
    sectorRetailTrade.setAssociatedLUs(associatedLUsA);
    final Map<String, String> associatedALUsMap = new HashMap<String, String>();
    associatedALUsMap.put(localRetailLU.getId(), localRetailLU.getLabel());
    sectorRetailTrade.setAssociatedALUsMap(associatedALUsMap);
    demandConfig.getSectors().add(sectorRetailTrade);

    return demandConfig;
  }

  /**
   * Creates the simple demand config.
   * 
   * @param localRetailLU
   *          the local retail lu
   * @param residentialLowLU
   *          the residential low lu
   * @return the demand config
   */
  public static DemandConfig createSimpleDemandConfig(
      final AllocationLU localRetailLU, final AllocationLU residentialLowLU) {
    final DemandConfig demandConfig = new DemandConfig();
    // Basic demand setup, UAZ fields
    demandConfig.setBaseYear(2005);
    demandConfig.setTotalPopulationFeatureFieldName("TOTPOP_CY");
    demandConfig.setGroupQuartersPopulationFeatureFieldName("GQPOP_CY");
    demandConfig.setEnumerationDistrictAreaFeatureFieldName("BG_ACRES");
    demandConfig
        .setClippedEnumerationDistrictAreaFeatureFieldName("CLIP_ACRES");
    demandConfig.setNumberOfHouseholdsFeatureFieldName("TOTHH_CY");
    demandConfig.setNumberOfHousingUnitsFeatureFieldName("TOTHU_CY");

    // Create projections years
    final Projection projection0 = new Projection();
    final Projection projection1 = new Projection();
    final Projection projection2 = new Projection();

    projection0.setLabel("2005");
    projection0.setYear(2005);

    projection1.setLabel("2010");
    projection1.setYear(2010);

    projection2.setLabel("2015");
    projection2.setYear(2015);

    final Set<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.add(projection0);
    projections.add(projection1);
    projections.add(projection2);

    demandConfig.setProjections(projections);

    // Add sector
    final EmploymentSector sectorRetailTrade = new EmploymentSector();
    sectorRetailTrade.setCode("NAICS 44-45");
    sectorRetailTrade.setLabel("Retail Trade");
    final Set<AllocationLU> associatedLUsA = new HashSet<AllocationLU>();
    sectorRetailTrade.setAssociatedLUs(associatedLUsA);
    final Map<String, String> associatedALUsMap = new HashMap<String, String>();
    associatedALUsMap.put(localRetailLU.getId(), localRetailLU.getLabel());
    sectorRetailTrade.setAssociatedALUsMap(associatedALUsMap);
    demandConfig.getSectors().add(sectorRetailTrade);
    // TODO check if this is really necessary
    // localRetailLU.addSectorLabel(sectorRetailTrade.getLabel());
    final CurrentDemographic currentDemographic = new CurrentDemographic();
    currentDemographic.setLabel("2005");
    currentDemographic.setYear(2005);
    final Set<EmploymentCurrentData> employmentCurrentDatas = new HashSet<EmploymentCurrentData>();
    currentDemographic.setEmploymentCurrentDatas(employmentCurrentDatas);

    demandConfig.setCurrentDemographic(currentDemographic);
    demandConfig.getCurrentDemographic().setTotalPopulation(18523L);
    demandConfig.getCurrentDemographic().setgQPopulation(117L);
    demandConfig.getCurrentDemographic().setHousingUnits(8130L);
    final long totalHousingUnits = demandConfig.getCurrentDemographic()
        .getHousingUnits();
    demandConfig.getCurrentDemographic().setHouseholds(7347.0);
    demandConfig.getCurrentDemographic().setVacantLand(678.46);

    final ResidentialCurrentData lowResData = new ResidentialCurrentData();
    lowResData.setNumberOfHousingUnits(5200);
    lowResData.setResidentialLU(residentialLowLU);
    lowResData.setResidentialLUId(residentialLowLU.getId());
    lowResData.setVacancyRate(demandConfig.getCurrentDemographic()
        .getVacancyRate());
    lowResData.setDensity(residentialLowLU.getTotalArea()
        / lowResData.getNumberOfHousingUnits());
    lowResData.setBreakdownDensity((double) lowResData
        .getNumberOfHousingUnits() / (double) totalHousingUnits);
    lowResData.setCurrentDemographic(currentDemographic);

    final EmploymentCurrentData ecDataRetailTrade = new EmploymentCurrentData();
    ecDataRetailTrade.setEmployees(5771L);
    ecDataRetailTrade.setSector(sectorRetailTrade);
    ecDataRetailTrade.setSectorLabel(sectorRetailTrade.getLabel());
    ecDataRetailTrade.setCurrentDemographic(currentDemographic);
    currentDemographic.getEmploymentCurrentDatas().add(ecDataRetailTrade);

    final Set<ResidentialCurrentData> residentialCurrentDatas = new HashSet<ResidentialCurrentData>();
    currentDemographic.setResidentialCurrentData(residentialCurrentDatas);
    currentDemographic.getResidentialCurrentData().add(lowResData);

    return demandConfig;
  }

}
