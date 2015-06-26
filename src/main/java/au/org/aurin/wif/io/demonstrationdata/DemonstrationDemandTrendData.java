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
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;

/**
 * The Class DemonstrationDemandTrendData.
 */
public class DemonstrationDemandTrendData {

  /**
   * Creates the demand trend module. It is important that there is information
   * only for local and regional retail
   * 
   * @param project
   *          the project
   * @return the wif project
   */

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemonstrationDemandTrendData.class);

  public static WifProject createDemandTrendModule(WifProject project) {

    try {

      // Sectors information
      EmploymentSector sectorRetailTrade = project
          .getSectorByLabel("Retail Trade");

      // ***************************
      // information per projection
      Projection projection0 = project.getProjectionByLabel("2005");
      // TODO why, aam I using projection that is in the current demographic?
      Projection projection1 = project.getProjectionByLabel("2010");
      Projection projection2 = project.getProjectionByLabel("2015");
      // DemographicTrend info
      DemographicTrend highGrowthDemographicTrend = new DemographicTrend();
      highGrowthDemographicTrend.setLabel("High Growth Trend");
      highGrowthDemographicTrend.setWifProject(project);
      Set<DemographicTrend> dtrends = new HashSet<DemographicTrend>();
      dtrends.add(highGrowthDemographicTrend);
      project.setDemographicTrends(dtrends);

      // Residential demographics data information
      ResidentialDemographicData demog0 = new ResidentialDemographicData();
      demog0.setTotalPopulation(18523L);
      demog0.setgQPopulation(117L);
      demog0.setAverageHouseholdSize(2.51);
      demog0.setHousingUnits(8129L);
      demog0.setProjection(projection0);
      demog0.setDemographicTrend(highGrowthDemographicTrend);

      ResidentialDemographicData demog1 = new ResidentialDemographicData();
      demog1.setTotalPopulation(23149L);
      demog1.setgQPopulation(146L);
      demog1.setAverageHouseholdSize(2.41);
      demog1.setProjection(projection1);
      demog1.setDemographicTrend(highGrowthDemographicTrend);

      ResidentialDemographicData demog2 = new ResidentialDemographicData();
      demog2.setTotalPopulation(27144L);
      demog2.setgQPopulation(171L);
      demog2.setAverageHouseholdSize(2.35);
      demog2.setProjection(projection2);
      demog2.setDemographicTrend(highGrowthDemographicTrend);

      EmploymentDemographicData retailTradeData0 = new EmploymentDemographicData();
      retailTradeData0.setDemographicTrend(highGrowthDemographicTrend);
      retailTradeData0.setSector(sectorRetailTrade);
      retailTradeData0.setProjection(projection0);
      retailTradeData0.setEmployees(5771);

      EmploymentDemographicData retailTradeData1 = new EmploymentDemographicData();
      retailTradeData1.setDemographicTrend(highGrowthDemographicTrend);
      retailTradeData1.setSector(sectorRetailTrade);
      retailTradeData1.setProjection(projection1);
      retailTradeData1.setEmployees(6558);

      EmploymentDemographicData manufacturingData0 = new EmploymentDemographicData();
      manufacturingData0.setDemographicTrend(highGrowthDemographicTrend);
      manufacturingData0.setSector(sectorRetailTrade);
      manufacturingData0.setProjection(projection0);
      manufacturingData0.setEmployees(1164);

      EmploymentDemographicData manufacturingData1 = new EmploymentDemographicData();
      manufacturingData1.setDemographicTrend(highGrowthDemographicTrend);
      manufacturingData1.setSector(sectorRetailTrade);
      manufacturingData1.setProjection(projection1);
      manufacturingData1.setEmployees(1323);

      Set<DemographicData> demographicDatas = new HashSet<DemographicData>();
      demographicDatas.add(demog0);
      demographicDatas.add(demog1);
      demographicDatas.add(demog2);
      demographicDatas.add(retailTradeData0);
      demographicDatas.add(retailTradeData1);
      demographicDatas.add(manufacturingData0);
      demographicDatas.add(manufacturingData1);
      highGrowthDemographicTrend.setDemographicData(demographicDatas);

      Set<DemographicTrend> demographicTrends = new HashSet<DemographicTrend>();
      demographicTrends.add(highGrowthDemographicTrend);

      project.setDemographicTrends(demographicTrends);

    } catch (WifInvalidInputException e) {
      LOGGER.error("Problem occurred in createDemandTrendModule");
    }
    return project;
  }
}
