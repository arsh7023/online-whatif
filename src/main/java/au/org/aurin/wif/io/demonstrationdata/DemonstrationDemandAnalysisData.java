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
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.model.demand.data.EmploymentData;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.data.PreservedData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.PreservationDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;

/**
 * The Class DemonstrationDemandAnalysisData.
 */
public class DemonstrationDemandAnalysisData {

  /**
   * Creates the demand analysis module.
   * 
   * @param project
   *          the project
   * @return the wif project
   */

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemonstrationDemandAnalysisData.class);

  public static WifProject createDemandAnalysisModule(WifProject project) {

    try {
      AllocationLU residentialLowLU = project
          .getExistingLandUseByLabel("Low Density Res.");

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

      AllocationLU residentialMediumLU = project
          .getExistingLandUseByLabel("Med Density Res.");

      AllocationLU residentialmixedUseLU = project
          .getExistingLandUseByLabel("Mixed Use");

      AllocationLU nursingHomeLU = project
          .getExistingLandUseByLabel("Nursing Home");

      AllocationLU industrialLU = project
          .getExistingLandUseByLabel("Industrial");

      // Sectors information
      EmploymentSector sectorRetailTrade = project
          .getSectorByLabel("Retail Trade");
      EmploymentSector sectorAgricultureHunting = project
          .getSectorByLabel("Ag/Forest/Fish/Hunt");

      EmploymentSector sectorHealthcare = project
          .getSectorByLabel("Healthcare");
      EmploymentSector sectorArts = project
          .getSectorByLabel("Arts/Entertain/Rec.");
      EmploymentSector sectorFood = project
          .getSectorByLabel("Accommodation/Food");
      EmploymentSector sectorMining = project.getSectorByLabel("Mining");
      EmploymentSector sectorBInformation = project
          .getSectorByLabel("Information");
      EmploymentSector sectorCFinance = project
          .getSectorByLabel("Finance/Insurance");
      EmploymentSector sectorDManagement = project
          .getSectorByLabel("Management");
      EmploymentSector sectorESupport = project
          .getSectorByLabel("Admin/Support");
      EmploymentSector sectorFProfessional = project
          .getSectorByLabel("Professional");
      EmploymentSector sectorGUtilities = project.getSectorByLabel("Utilities");
      EmploymentSector sectorHEducational = project
          .getSectorByLabel("Educational Services");
      EmploymentSector sectorConstruction = project
          .getSectorByLabel("Construction");
      EmploymentSector sectorWholesale = project.getSectorByLabel("Wholesale");
      EmploymentSector sectorTransport = project
          .getSectorByLabel("Transport/Warehouse");
      EmploymentSector sectorManufacturing = project
          .getSectorByLabel("Manufacturing");
      EmploymentSector sectorIOther = project
          .getSectorByLabel("Other Services");

      Projection projection0 = project.getProjectionByLabel("2005");
      Projection projection1 = project.getProjectionByLabel("2010");
      Projection projection2 = project.getProjectionByLabel("2015");
      DemographicTrend highGrowthDemographicTrend = project.getDemandConfig()
          .getTrendByLabel("High Growth Trend");

      Set<DemandInfo> demandInfos = new HashSet<DemandInfo>();

      // demand scenario information
      DemandScenario highGrowthDemandScenario = new DemandScenario();
      highGrowthDemandScenario.setFeatureFieldName("High Growth");
      highGrowthDemandScenario.setLabel("High Growth");
      highGrowthDemandScenario.setDemographicTrendLabel("High Growth Trend");
      highGrowthDemandScenario.setWifProject(project);
      highGrowthDemandScenario.setDemographicTrend(highGrowthDemographicTrend);

      // low residential information for demand
      ResidentialDemandInfo rdinfo = new ResidentialDemandInfo();
      rdinfo.setInfillRate(0.0);
      rdinfo.setFutureBreakdownByHType(0.60);
      rdinfo.setCurrentDensity(0.94);
      rdinfo.setFutureDensity(1.0);
      rdinfo.setFutureVacancyRate(0.0963);
      rdinfo.setResidentialLU(residentialLowLU);
      rdinfo.setDemandScenario(highGrowthDemandScenario);

      // Associate residential land use we demand information
      Set<DemandInfo> rsDemandInfos = new HashSet<DemandInfo>();
      residentialLowLU.setDemandInfos(rsDemandInfos);
      residentialLowLU.addDemandInfo(rdinfo);

      // employment information for demand,

      EmploymentDemandInfo edinfoFood = new EmploymentDemandInfo();
      edinfoFood.setSector(sectorFood);
      edinfoFood.setCurrentDensity(7.97);
      edinfoFood.setFutureDensity(7.97);
      edinfoFood.setInfillRate(0.0);
      edinfoFood.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoFood.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoArts = new EmploymentDemandInfo();
      edinfoArts.setSector(sectorArts);
      edinfoArts.setCurrentDensity(1.91);
      edinfoArts.setFutureDensity(1.91);
      edinfoArts.setInfillRate(0.0);
      edinfoArts.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoArts.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoTransport = new EmploymentDemandInfo();
      edinfoTransport.setSector(sectorTransport);
      edinfoTransport.setCurrentDensity(1.91);
      edinfoTransport.setFutureDensity(1.91);
      edinfoTransport.setInfillRate(0.0);
      edinfoTransport.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoTransport.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoManufacturing = new EmploymentDemandInfo();
      edinfoManufacturing.setSector(sectorManufacturing);
      edinfoManufacturing.setCurrentDensity(1.91);
      edinfoManufacturing.setFutureDensity(1.91);
      edinfoManufacturing.setInfillRate(0.0);

      edinfoManufacturing.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoManufacturing.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoConstruction = new EmploymentDemandInfo();
      edinfoConstruction.setSector(sectorConstruction);
      edinfoConstruction.setCurrentDensity(1.91);
      edinfoConstruction.setFutureDensity(1.91);
      edinfoConstruction.setInfillRate(0.0);
      edinfoConstruction.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoConstruction.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoWholesale = new EmploymentDemandInfo();
      edinfoWholesale.setSector(sectorWholesale);
      edinfoWholesale.setCurrentDensity(1.91);
      edinfoWholesale.setFutureDensity(1.91);
      edinfoWholesale.setInfillRate(0.0);
      edinfoWholesale.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoWholesale.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoHealthcare = new EmploymentDemandInfo();
      edinfoHealthcare.setSector(sectorHealthcare);
      edinfoHealthcare.setCurrentDensity(3.93);
      edinfoHealthcare.setFutureDensity(3.93);
      edinfoHealthcare.setInfillRate(0.0);
      edinfoHealthcare.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoHealthcare.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoMining = new EmploymentDemandInfo();
      edinfoMining.setSector(sectorMining);
      edinfoMining.setCurrentDensity(0.0);
      edinfoMining.setFutureDensity(0.01);
      edinfoMining.setInfillRate(0.0);
      edinfoMining.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoMining.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoAG = new EmploymentDemandInfo();
      edinfoAG.setSector(sectorAgricultureHunting);
      edinfoAG.setCurrentDensity(0.01);
      edinfoAG.setFutureDensity(0.01);
      edinfoAG.setInfillRate(0.0);
      edinfoAG.setProjectedDatas(new HashSet<ProjectedData>());
      edinfoAG.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoARetailTrade = new EmploymentDemandInfo();

      edinfoARetailTrade.setSector(sectorRetailTrade);
      edinfoARetailTrade.setCurrentDensity(9.85);
      edinfoARetailTrade.setFutureDensity(9.85);
      edinfoARetailTrade.setInfillRate(0.0);
      Set<ProjectedData> empProysA = new HashSet<ProjectedData>();
      edinfoARetailTrade.setProjectedDatas(empProysA);
      edinfoARetailTrade.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoBInformation = new EmploymentDemandInfo();

      edinfoBInformation.setSector(sectorBInformation);
      edinfoBInformation.setCurrentDensity(6.79);
      edinfoBInformation.setFutureDensity(6.79);
      edinfoBInformation.setInfillRate(0.0);
      Set<ProjectedData> empProysB = new HashSet<ProjectedData>();
      edinfoBInformation.setProjectedDatas(empProysB);
      edinfoBInformation.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoCFinance = new EmploymentDemandInfo();

      edinfoCFinance.setSector(sectorCFinance);
      edinfoCFinance.setCurrentDensity(6.66);
      edinfoCFinance.setFutureDensity(6.66);
      edinfoCFinance.setInfillRate(0.0);
      Set<ProjectedData> empProysC = new HashSet<ProjectedData>();
      edinfoCFinance.setProjectedDatas(empProysC);
      edinfoCFinance.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoDManagement = new EmploymentDemandInfo();

      edinfoDManagement.setSector(sectorDManagement);
      edinfoDManagement.setCurrentDensity(0.0);
      edinfoDManagement.setFutureDensity(0.01);
      edinfoDManagement.setInfillRate(0.0);
      Set<ProjectedData> empProysd = new HashSet<ProjectedData>();
      edinfoDManagement.setProjectedDatas(empProysd);
      edinfoDManagement.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoESupport = new EmploymentDemandInfo();
      edinfoESupport.setSector(sectorESupport);
      edinfoESupport.setCurrentDensity(1.36);
      edinfoESupport.setFutureDensity(1.36);
      edinfoESupport.setInfillRate(0.0);
      Set<ProjectedData> empProyse = new HashSet<ProjectedData>();
      edinfoESupport.setProjectedDatas(empProyse);
      edinfoESupport.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoFProfessional = new EmploymentDemandInfo();

      edinfoFProfessional.setSector(sectorFProfessional);
      edinfoFProfessional.setCurrentDensity(10.59);
      edinfoFProfessional.setFutureDensity(10.59);
      edinfoFProfessional.setInfillRate(0.0);
      Set<ProjectedData> empProysf = new HashSet<ProjectedData>();
      edinfoFProfessional.setProjectedDatas(empProysf);
      edinfoFProfessional.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoGUtilities = new EmploymentDemandInfo();

      edinfoGUtilities.setSector(sectorGUtilities);
      edinfoGUtilities.setCurrentDensity(0.05);
      edinfoGUtilities.setFutureDensity(0.05);
      edinfoGUtilities.setInfillRate(0.0);
      Set<ProjectedData> empProysg = new HashSet<ProjectedData>();
      edinfoGUtilities.setProjectedDatas(empProysg);
      edinfoGUtilities.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoIndustrial = new EmploymentDemandInfo();
      edinfoIndustrial.setSector(sectorHEducational);
      edinfoIndustrial.setCurrentDensity(2.53);
      edinfoIndustrial.setFutureDensity(2.53);
      edinfoIndustrial.setInfillRate(0.0);
      Set<ProjectedData> empProysI = new HashSet<ProjectedData>();
      edinfoIndustrial.setProjectedDatas(empProysI);
      edinfoIndustrial.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoHEducational = new EmploymentDemandInfo();
      edinfoHEducational.setSector(sectorHEducational);
      edinfoHEducational.setCurrentDensity(2.53);
      edinfoHEducational.setFutureDensity(2.53);
      edinfoHEducational.setInfillRate(0.0);
      Set<ProjectedData> empProysh = new HashSet<ProjectedData>();
      edinfoHEducational.setProjectedDatas(empProysh);
      edinfoHEducational.setDemandScenario(highGrowthDemandScenario);

      EmploymentDemandInfo edinfoIOther = new EmploymentDemandInfo();

      edinfoIOther.setSector(sectorIOther);
      edinfoIOther.setCurrentDensity(3.92);
      edinfoIOther.setFutureDensity(3.92);
      edinfoIOther.setInfillRate(0.0);
      Set<ProjectedData> empProysi = new HashSet<ProjectedData>();
      edinfoIOther.setProjectedDatas(empProysi);
      edinfoIOther.setDemandScenario(highGrowthDemandScenario);

      // conservation information demand
      PreservationDemandInfo preservedDInfo = new PreservationDemandInfo();

      Set<ProjectedData> consdp = new HashSet<ProjectedData>();
      preservedDInfo.setProjectedDatas(consdp);
      preservedDInfo.setDemandScenario(highGrowthDemandScenario);
      preservedDInfo.setAllocationLU(conservationLU);
      DemandConfig demandConfig = project.getDemandConfig();

      // Associate employment land use with demand information
      EmploymentData emProjPub0Manufacturing = new EmploymentData();
      emProjPub0Manufacturing.setEmploymentInfo(edinfoManufacturing);
      emProjPub0Manufacturing.setEmployees(1164);
      emProjPub0Manufacturing.setProjection(projection0);
      EmploymentData emProjPub1Manufacturing = new EmploymentData();
      emProjPub1Manufacturing.setEmploymentInfo(edinfoManufacturing);
      emProjPub1Manufacturing.setEmployees(1323);
      emProjPub1Manufacturing.setProjection(projection1);
      EmploymentData emProjPub2Manufacturing = new EmploymentData();
      emProjPub2Manufacturing.setEmploymentInfo(edinfoManufacturing);
      emProjPub2Manufacturing.setEmployees(1448);

      emProjPub2Manufacturing.setProjection(projection2);

      edinfoManufacturing.addProjectedData(emProjPub0Manufacturing);
      edinfoManufacturing.addProjectedData(emProjPub1Manufacturing);
      edinfoManufacturing.addProjectedData(emProjPub2Manufacturing);

      EmploymentData emProjPub0Construction = new EmploymentData();
      emProjPub0Construction.setEmploymentInfo(edinfoConstruction);
      emProjPub0Construction.setEmployees(2180);
      emProjPub0Construction.setProjection(projection0);
      EmploymentData emProjPub1Construction = new EmploymentData();
      emProjPub1Construction.setEmploymentInfo(edinfoConstruction);
      emProjPub1Construction.setEmployees(2477);
      emProjPub1Construction.setProjection(projection1);
      EmploymentData emProjPub2Construction = new EmploymentData();
      emProjPub2Construction.setEmploymentInfo(edinfoConstruction);
      emProjPub2Construction.setEmployees(2713);
      emProjPub2Construction.setProjection(projection2);

      edinfoConstruction.addProjectedData(emProjPub0Construction);
      edinfoConstruction.addProjectedData(emProjPub1Construction);
      edinfoConstruction.addProjectedData(emProjPub2Construction);

      EmploymentData emProjPub0Wholesale = new EmploymentData();
      emProjPub0Wholesale.setEmploymentInfo(edinfoWholesale);
      emProjPub0Wholesale.setEmployees(4205);
      emProjPub0Wholesale.setProjection(projection0);
      EmploymentData emProjPub1Wholesale = new EmploymentData();
      emProjPub1Wholesale.setEmploymentInfo(edinfoWholesale);
      emProjPub1Wholesale.setEmployees(4779);
      emProjPub1Wholesale.setProjection(projection1);
      EmploymentData emProjPub2Wholesale = new EmploymentData();
      emProjPub2Wholesale.setEmploymentInfo(edinfoWholesale);
      emProjPub2Wholesale.setEmployees(5234);
      emProjPub2Wholesale.setProjection(projection2);

      edinfoWholesale.addProjectedData(emProjPub0Wholesale);
      edinfoWholesale.addProjectedData(emProjPub1Wholesale);
      edinfoWholesale.addProjectedData(emProjPub2Wholesale);

      EmploymentData emProjPub0Transport = new EmploymentData();
      emProjPub0Transport.setEmploymentInfo(edinfoTransport);
      emProjPub0Transport.setEmployees(719);
      emProjPub0Transport.setProjection(projection0);
      EmploymentData emProjPub1Transport = new EmploymentData();
      emProjPub1Transport.setEmploymentInfo(edinfoTransport);
      emProjPub1Transport.setEmployees(817);
      emProjPub1Transport.setProjection(projection1);
      EmploymentData emProjPub2Transport = new EmploymentData();
      emProjPub2Transport.setEmploymentInfo(edinfoTransport);
      emProjPub2Transport.setEmployees(894);
      emProjPub2Transport.setProjection(projection2);

      edinfoTransport.addProjectedData(emProjPub0Transport);
      edinfoTransport.addProjectedData(emProjPub1Transport);
      edinfoTransport.addProjectedData(emProjPub2Transport);

      EmploymentData emProjRR = new EmploymentData();
      emProjRR.setEmploymentInfo(edinfoARetailTrade);
      emProjRR.setEmployees(5771);
      emProjRR.setProjection(projection0);
      EmploymentData emProjRR1 = new EmploymentData();
      emProjRR1.setEmploymentInfo(edinfoARetailTrade);
      emProjRR1.setEmployees(6558);
      emProjRR1.setProjection(projection1);
      EmploymentData emProjRR2 = new EmploymentData();
      emProjRR2.setEmploymentInfo(edinfoARetailTrade);
      emProjRR2.setEmployees(7182);
      emProjRR2.setProjection(projection2);

      edinfoARetailTrade.addProjectedData(emProjRR);
      edinfoARetailTrade.addProjectedData(emProjRR1);
      edinfoARetailTrade.addProjectedData(emProjRR2);

      EmploymentData emProjOffice0B = new EmploymentData();
      emProjOffice0B.setEmploymentInfo(edinfoBInformation);
      emProjOffice0B.setEmployees(3005);
      emProjOffice0B.setProjection(projection0);
      EmploymentData emProjOffice1B = new EmploymentData();
      emProjOffice1B.setEmploymentInfo(edinfoBInformation);
      emProjOffice1B.setEmployees(3415);
      emProjOffice1B.setProjection(projection1);
      EmploymentData emProjOffice2B = new EmploymentData();
      emProjOffice2B.setEmploymentInfo(edinfoBInformation);
      emProjOffice2B.setEmployees(3740);
      emProjOffice2B.setProjection(projection2);

      edinfoBInformation.addProjectedData(emProjOffice0B);
      edinfoBInformation.addProjectedData(emProjOffice1B);
      edinfoBInformation.addProjectedData(emProjOffice2B);

      EmploymentData emProjOffice0C = new EmploymentData();
      emProjOffice0C.setEmploymentInfo(edinfoCFinance);
      emProjOffice0C.setEmployees(2945);
      emProjOffice0C.setProjection(projection0);
      EmploymentData emProjOffice1C = new EmploymentData();
      emProjOffice1C.setEmploymentInfo(edinfoCFinance);
      emProjOffice1C.setEmployees(3346);
      emProjOffice1C.setProjection(projection1);
      EmploymentData emProjOffice2C = new EmploymentData();
      emProjOffice2C.setEmploymentInfo(edinfoCFinance);
      emProjOffice2C.setEmployees(3665);
      emProjOffice2C.setProjection(projection2);

      edinfoCFinance.addProjectedData(emProjOffice0C);
      edinfoCFinance.addProjectedData(emProjOffice1C);
      edinfoCFinance.addProjectedData(emProjOffice2C);

      EmploymentData emProj0DManagement = new EmploymentData();
      emProj0DManagement.setEmploymentInfo(edinfoDManagement);
      emProj0DManagement.setEmployees(3);
      emProj0DManagement.setProjection(projection0);
      EmploymentData emProjOffice1D = new EmploymentData();
      emProjOffice1D.setEmploymentInfo(edinfoDManagement);
      emProjOffice1D.setEmployees(3);
      emProjOffice1D.setProjection(projection1);
      EmploymentData emProjOffice2D = new EmploymentData();
      emProjOffice2D.setEmploymentInfo(edinfoDManagement);
      emProjOffice2D.setEmployees(4);
      emProjOffice2D.setProjection(projection2);

      edinfoDManagement.addProjectedData(emProj0DManagement);
      edinfoDManagement.addProjectedData(emProjOffice1D);
      edinfoDManagement.addProjectedData(emProjOffice2D);

      Set<DemandInfo> empDemandInfosLR = new HashSet<DemandInfo>();
      localRetailLU.setDemandInfos(empDemandInfosLR);
      localRetailLU.addDemandInfo(edinfoARetailTrade);

      EmploymentData emProjOffice0E = new EmploymentData();
      emProjOffice0E.setEmploymentInfo(edinfoESupport);
      emProjOffice0E.setEmployees(1328);
      emProjOffice0E.setProjection(projection0);
      EmploymentData emProjOffice1E = new EmploymentData();
      emProjOffice1E.setEmploymentInfo(edinfoESupport);
      emProjOffice1E.setEmployees(1509);
      emProjOffice1E.setProjection(projection1);
      EmploymentData emProjOffice2E = new EmploymentData();
      emProjOffice2E.setEmploymentInfo(edinfoESupport);
      emProjOffice2E.setEmployees(1652);
      emProjOffice2E.setProjection(projection2);

      edinfoESupport.addProjectedData(emProjOffice0E);
      edinfoESupport.addProjectedData(emProjOffice1E);
      edinfoESupport.addProjectedData(emProjOffice2E);

      EmploymentData emProjOffice0F = new EmploymentData();
      emProjOffice0F.setEmploymentInfo(edinfoFProfessional);
      emProjOffice0F.setEmployees(4685);
      emProjOffice0F.setProjection(projection0);
      EmploymentData emProjOffice1F = new EmploymentData();
      emProjOffice1F.setEmploymentInfo(edinfoFProfessional);
      emProjOffice1F.setEmployees(5323);
      emProjOffice1F.setProjection(projection1);
      EmploymentData emProjOffice2F = new EmploymentData();
      emProjOffice2F.setEmploymentInfo(edinfoFProfessional);
      emProjOffice2F.setEmployees(5830);
      emProjOffice2F.setProjection(projection2);

      edinfoFProfessional.addProjectedData(emProjOffice0F);
      edinfoFProfessional.addProjectedData(emProjOffice1F);
      edinfoFProfessional.addProjectedData(emProjOffice2F);

      EmploymentData emProjOffice0I = new EmploymentData();
      emProjOffice0I.setEmploymentInfo(edinfoIOther);
      emProjOffice0I.setEmployees(1736);
      emProjOffice0I.setProjection(projection0);
      EmploymentData emProjOffice1I = new EmploymentData();
      emProjOffice1I.setEmploymentInfo(edinfoIOther);
      emProjOffice1I.setEmployees(1973);
      emProjOffice1I.setProjection(projection1);
      EmploymentData emProjOffice2I = new EmploymentData();
      emProjOffice2I.setEmploymentInfo(edinfoIOther);
      emProjOffice2I.setEmployees(2160);
      emProjOffice2I.setProjection(projection2);

      edinfoIOther.addProjectedData(emProjOffice0I);
      edinfoIOther.addProjectedData(emProjOffice1I);
      edinfoIOther.addProjectedData(emProjOffice2I);

      Set<DemandInfo> empDemandInfosOffice = new HashSet<DemandInfo>();
      officeLU.setDemandInfos(empDemandInfosOffice);
      officeLU.addDemandInfo(edinfoIOther);
      officeLU.addDemandInfo(edinfoFProfessional);
      officeLU.addDemandInfo(edinfoESupport);
      officeLU.addDemandInfo(edinfoDManagement);
      officeLU.addDemandInfo(edinfoCFinance);
      officeLU.addDemandInfo(edinfoBInformation);

      EmploymentData emProjPub0H = new EmploymentData();
      emProjPub0H.setEmploymentInfo(edinfoHEducational);
      emProjPub0H.setEmployees(1796);
      emProjPub0H.setProjection(projection0);
      EmploymentData emProjPub1H = new EmploymentData();
      emProjPub1H.setEmploymentInfo(edinfoHEducational);
      emProjPub1H.setEmployees(2040);
      emProjPub1H.setProjection(projection1);
      EmploymentData emProjPub2H = new EmploymentData();
      emProjPub2H.setEmploymentInfo(edinfoHEducational);
      emProjPub2H.setEmployees(2235);
      emProjPub2H.setProjection(projection2);

      edinfoHEducational.addProjectedData(emProjPub0H);
      edinfoHEducational.addProjectedData(emProjPub1H);
      edinfoHEducational.addProjectedData(emProjPub2H);

      EmploymentData emProjPub0G = new EmploymentData();
      emProjPub0G.setEmploymentInfo(edinfoGUtilities);
      emProjPub0G.setEmployees(63);
      emProjPub0G.setProjection(projection0);
      EmploymentData emProjPub1G = new EmploymentData();
      emProjPub1G.setEmploymentInfo(edinfoGUtilities);
      emProjPub1G.setEmployees(72);
      emProjPub1G.setProjection(projection1);
      EmploymentData emProjPub2G = new EmploymentData();
      emProjPub2G.setEmploymentInfo(edinfoGUtilities);
      emProjPub2G.setEmployees(78);
      emProjPub2G.setProjection(projection2);

      edinfoGUtilities.addProjectedData(emProjPub0G);
      edinfoGUtilities.addProjectedData(emProjPub1G);
      edinfoGUtilities.addProjectedData(emProjPub2G);

      // associate local retail LU with retail trade sector one to many
      // relationship
      Set<DemandInfo> empDemandInfosRR = new HashSet<DemandInfo>();
      regionalRetailLU.setDemandInfos(empDemandInfosRR);
      regionalRetailLU.addDemandInfo(edinfoARetailTrade);

      // associate Pub LU with educational services And utilities sector,
      // many to many
      // relationship

      Set<DemandInfo> empDemandInfosPub = new HashSet<DemandInfo>();
      publicSemiPubLU.setDemandInfos(empDemandInfosPub);
      publicSemiPubLU.addDemandInfo(edinfoHEducational);
      publicSemiPubLU.addDemandInfo(edinfoGUtilities);

      Set<DemandInfo> conDemandInfos = new HashSet<DemandInfo>();
      conservationLU.setDemandInfos(conDemandInfos);
      conservationLU.addDemandInfo(preservedDInfo);

      // Associate preserved land use with demand information
      PreservedData preservedProj0 = new PreservedData();
      preservedProj0.setReservedArea(0.0);
      preservedProj0.setProjection(projection0);
      PreservedData preservedProj1 = new PreservedData();
      preservedProj1.setReservedArea(500.0);
      preservedProj1.setProjection(projection1);
      PreservedData preservedProj2 = new PreservedData();
      preservedProj2.setReservedArea(1000.0);
      preservedProj2.setProjection(projection2);
      preservedDInfo.addProjectedData(preservedProj0);
      preservedDInfo.addProjectedData(preservedProj1);
      preservedDInfo.addProjectedData(preservedProj2);

      // Associate local land use with demand information
      LocalJurisdiction ljcentralCity = demandConfig
          .getLocalJurisdictionByLabel("Central City");

      LocalJurisdiction ljedgeTownship = demandConfig
          .getLocalJurisdictionByLabel("Edge Township");
      ljedgeTownship.setLabel("Edge Township");

      LocalJurisdiction ljedgeCity = demandConfig
          .getLocalJurisdictionByLabel("Edge City");

      LocalData localC0 = new LocalData();
      localC0.setRequiredDensity(0.0);
      localC0.setProjection(projection0);
      localC0.setLocalJurisdiction(ljcentralCity);
      ljcentralCity.addLocalData(localC0);
      highGrowthDemandScenario.getLocalDatas().add(localC0);

      LocalData localC1 = new LocalData();
      localC1.setRequiredDensity(0.0);
      localC1.setProjection(projection1);
      localC1.setLocalJurisdiction(ljcentralCity);
      ljcentralCity.addLocalData(localC1);
      highGrowthDemandScenario.getLocalDatas().add(localC1);

      LocalData localC2 = new LocalData();
      localC2.setRequiredDensity(0.0);
      localC2.setProjection(projection2);
      localC2.setLocalJurisdiction(ljcentralCity);
      ljcentralCity.addLocalData(localC2);
      highGrowthDemandScenario.getLocalDatas().add(localC2);

      LocalData localE0 = new LocalData();
      localE0.setRequiredDensity(13.96);
      localE0.setProjection(projection0);
      localE0.setLocalJurisdiction(ljedgeCity);
      ljedgeCity.addLocalData(localE0);
      highGrowthDemandScenario.getLocalDatas().add(localE0);

      LocalData localE1 = new LocalData();
      localE1.setRequiredDensity(14.0);
      localE1.setProjection(projection1);
      localE1.setLocalJurisdiction(ljedgeCity);
      ljedgeCity.addLocalData(localE1);
      highGrowthDemandScenario.getLocalDatas().add(localE1);

      LocalData localE2 = new LocalData();
      localE2.setRequiredDensity(14.0);
      localE2.setProjection(projection2);
      localE2.setLocalJurisdiction(ljedgeCity);
      ljedgeCity.addLocalData(localE2);
      highGrowthDemandScenario.getLocalDatas().add(localE2);

      LocalData localT0 = new LocalData();
      localT0.setRequiredDensity(57.16);
      localT0.setProjection(projection0);
      localT0.setLocalJurisdiction(ljedgeTownship);
      ljedgeTownship.addLocalData(localT0);
      highGrowthDemandScenario.getLocalDatas().add(localT0);

      LocalData localT1 = new LocalData();
      localT1.setRequiredDensity(0.0);
      localT1.setProjection(projection1);
      localT1.setLocalJurisdiction(ljedgeTownship);
      ljedgeTownship.addLocalData(localT1);
      highGrowthDemandScenario.getLocalDatas().add(localT1);

      LocalData localT2 = new LocalData();
      localT2.setRequiredDensity(0.0);
      localT2.setProjection(projection2);
      localT2.setLocalJurisdiction(ljedgeTownship);
      ljedgeTownship.addLocalData(localT2);
      highGrowthDemandScenario.getLocalDatas().add(localT2);

      demandInfos.add(rdinfo);
      rdinfo.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoARetailTrade);
      edinfoARetailTrade.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoBInformation);
      edinfoBInformation.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoCFinance);
      edinfoCFinance.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoDManagement);
      edinfoDManagement.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoESupport);
      edinfoESupport.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoFProfessional);
      edinfoFProfessional.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoGUtilities);
      edinfoGUtilities.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoHEducational);
      edinfoHEducational.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(edinfoIOther);
      edinfoIOther.setDemandScenario(highGrowthDemandScenario);
      demandInfos.add(preservedDInfo);
      preservedDInfo.setDemandScenario(highGrowthDemandScenario);

      highGrowthDemandScenario.setDemandInfos(demandInfos);

      // Creating area requirements necessary for allocation module
      residentialLowLU.setAreaRequirements(new HashSet<AreaRequirement>());

      AreaRequirement reqLResidential1 = new AreaRequirement();
      reqLResidential1.setProjection(projection1);
      reqLResidential1.setRequiredArea(1459.48);
      reqLResidential1.setDemandScenario(highGrowthDemandScenario);
      reqLResidential1.setAllocationLU(residentialLowLU);
      residentialLowLU.addAreaRequirement(reqLResidential1);

      AreaRequirement reqLResidential2 = new AreaRequirement();
      reqLResidential2.setProjection(projection2);
      reqLResidential2.setRequiredArea(1283.43);
      reqLResidential2.setDemandScenario(highGrowthDemandScenario);
      reqLResidential2.setAllocationLU(residentialLowLU);
      residentialLowLU.addAreaRequirement(reqLResidential2);
      AreaRequirement reqLResidential1m = new AreaRequirement();
      reqLResidential1m.setProjection(projection1);
      reqLResidential1m.setRequiredArea(231.08);
      reqLResidential1m.setDemandScenario(highGrowthDemandScenario);
      reqLResidential1m.setAllocationLU(residentialMediumLU);
      residentialMediumLU.addAreaRequirement(reqLResidential1m);

      AreaRequirement reqLResidential2m = new AreaRequirement();
      reqLResidential2m.setProjection(projection2);
      reqLResidential2m.setRequiredArea(203.21);
      reqLResidential2m.setDemandScenario(highGrowthDemandScenario);
      reqLResidential2m.setAllocationLU(residentialMediumLU);
      residentialMediumLU.addAreaRequirement(reqLResidential2m);

      AreaRequirement reqLResidential1h = new AreaRequirement();
      reqLResidential1h.setProjection(projection1);
      reqLResidential1h.setRequiredArea(1.22);
      reqLResidential1h.setDemandScenario(highGrowthDemandScenario);
      reqLResidential1h.setAllocationLU(residentialmixedUseLU);
      residentialmixedUseLU.addAreaRequirement(reqLResidential1h);

      AreaRequirement reqLResidential2h = new AreaRequirement();
      reqLResidential2h.setProjection(projection2);
      reqLResidential2h.setRequiredArea(1.07);
      reqLResidential2h.setDemandScenario(highGrowthDemandScenario);
      reqLResidential2h.setAllocationLU(residentialmixedUseLU);
      residentialmixedUseLU.addAreaRequirement(reqLResidential2h);

      AreaRequirement reqGQ1 = new AreaRequirement();
      reqGQ1.setProjection(projection1);
      reqGQ1.setRequiredArea(1.91);
      reqGQ1.setDemandScenario(highGrowthDemandScenario);
      reqGQ1.setAllocationLU(nursingHomeLU);
      nursingHomeLU.addAreaRequirement(reqGQ1);

      AreaRequirement reqGQ2 = new AreaRequirement();
      reqGQ2.setProjection(projection2);
      reqGQ2.setRequiredArea(1.65);
      reqGQ2.setDemandScenario(highGrowthDemandScenario);
      reqGQ2.setAllocationLU(nursingHomeLU);
      nursingHomeLU.addAreaRequirement(reqGQ2);

      AreaRequirement areqlretail1 = new AreaRequirement();
      areqlretail1.setProjection(projection1);
      areqlretail1.setRequiredArea(39.93);
      areqlretail1.setDemandScenario(highGrowthDemandScenario);
      areqlretail1.setAllocationLU(localRetailLU);
      localRetailLU.addAreaRequirement(areqlretail1);

      AreaRequirement areqlretail2 = new AreaRequirement();
      areqlretail2.setProjection(projection2);
      areqlretail2.setRequiredArea(31.69);
      areqlretail2.setDemandScenario(highGrowthDemandScenario);
      areqlretail2.setAllocationLU(localRetailLU);
      localRetailLU.addAreaRequirement(areqlretail2);

      AreaRequirement reqrretail1 = new AreaRequirement();
      reqrretail1.setProjection(projection1);
      reqrretail1.setRequiredArea(39.93);
      reqrretail1.setDemandScenario(highGrowthDemandScenario);
      reqrretail1.setDemandScenario(highGrowthDemandScenario);
      reqrretail1.setAllocationLU(regionalRetailLU);
      regionalRetailLU.addAreaRequirement(reqrretail1);

      AreaRequirement areqrretail2 = new AreaRequirement();
      areqrretail2.setProjection(projection2);
      areqrretail2.setRequiredArea(31.69);
      areqrretail2.setDemandScenario(highGrowthDemandScenario);
      areqrretail2.setAllocationLU(regionalRetailLU);
      regionalRetailLU.addAreaRequirement(areqrretail2);

      AreaRequirement reqOffice1 = new AreaRequirement();
      reqOffice1.setProjection(projection1);
      reqOffice1.setRequiredArea(66.54);
      reqOffice1.setDemandScenario(highGrowthDemandScenario);
      reqOffice1.setAllocationLU(officeLU);
      officeLU.addAreaRequirement(reqOffice1);

      AreaRequirement reqOffice2 = new AreaRequirement();
      reqOffice2.setProjection(projection2);
      reqOffice2.setRequiredArea(52.78);
      reqOffice2.setDemandScenario(highGrowthDemandScenario);
      reqOffice2.setAllocationLU(officeLU);
      officeLU.addAreaRequirement(reqOffice2);

      AreaRequirement reqPublic1 = new AreaRequirement();
      reqPublic1.setProjection(projection1);
      reqPublic1.setRequiredArea(96.82);
      reqPublic1.setDemandScenario(highGrowthDemandScenario);
      reqPublic1.setAllocationLU(publicSemiPubLU);
      publicSemiPubLU.addAreaRequirement(reqPublic1);

      AreaRequirement reqPublic2 = new AreaRequirement();
      reqPublic2.setProjection(projection2);
      reqPublic2.setRequiredArea(76.85);
      reqPublic2.setDemandScenario(highGrowthDemandScenario);
      reqPublic2.setAllocationLU(publicSemiPubLU);
      publicSemiPubLU.addAreaRequirement(reqPublic2);

      AreaRequirement reqIndustrial1 = new AreaRequirement();
      reqIndustrial1.setProjection(projection1);
      reqIndustrial1.setRequiredArea(85.23);
      reqIndustrial1.setDemandScenario(highGrowthDemandScenario);
      reqIndustrial1.setAllocationLU(industrialLU);
      industrialLU.addAreaRequirement(reqIndustrial1);

      AreaRequirement reqIndustrial2 = new AreaRequirement();
      reqIndustrial2.setProjection(projection2);
      reqIndustrial2.setRequiredArea(69.18);
      reqIndustrial2.setDemandScenario(highGrowthDemandScenario);
      reqIndustrial2.setAllocationLU(industrialLU);
      industrialLU.addAreaRequirement(reqIndustrial2);

      AreaRequirement reqConservation1 = new AreaRequirement();
      reqConservation1.setProjection(projection1);
      reqConservation1.setRequiredArea(500.0);
      reqConservation1.setDemandScenario(highGrowthDemandScenario);
      reqConservation1.setAllocationLU(conservationLU);
      conservationLU.addAreaRequirement(reqConservation1);

      AreaRequirement reqConservation2 = new AreaRequirement();
      reqConservation2.setProjection(projection2);
      reqConservation2.setRequiredArea(500.0);
      reqConservation2.setDemandScenario(highGrowthDemandScenario);
      reqConservation2.setAllocationLU(conservationLU);
      conservationLU.addAreaRequirement(reqConservation2);
      // TODO Check with expert how local demand is dependent on the projected
      // location of residential land use
      LocalAreaRequirement reqPark1 = new LocalAreaRequirement();
      reqPark1.setProjection(projection1);
      reqPark1.setRequiredArea(57.0);
      reqPark1.setLocalJurisdiction(ljcentralCity);
      reqPark1.setAllocationLU(parkAndRecLocalLU);
      reqPark1.setDemandScenario(highGrowthDemandScenario);
      parkAndRecLocalLU.addAreaRequirement(reqPark1);

      LocalAreaRequirement reqPark2 = new LocalAreaRequirement();
      reqPark2.setProjection(projection2);
      reqPark2.setRequiredArea(30.0);
      reqPark2.setLocalJurisdiction(ljcentralCity);
      reqPark2.setAllocationLU(parkAndRecLocalLU);
      reqPark2.setDemandScenario(highGrowthDemandScenario);
      parkAndRecLocalLU.addAreaRequirement(reqPark2);

      Set<DemandScenario> demandScenarios = new HashSet<DemandScenario>();
      demandScenarios.add(highGrowthDemandScenario);

      project.setDemandScenarios(demandScenarios);

    } catch (WifInvalidInputException e) {
      LOGGER.error("Problem occurred in createDemandAnalysisModule");
    } finally {
    }
    return project;
  }
}
