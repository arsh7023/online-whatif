/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;
import au.org.aurin.wif.model.demand.data.EmploymentData;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.DensityDemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.PreservationDemandInfo;
import au.org.aurin.wif.model.demand.info.ProjectedDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * <b>RequiredAreaAnalyzer.java</b>
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Component
public class RequiredAreaAnalyzer {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 23463565464653L;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** the ProjectService. */
  @Resource
  private ProjectService projectService;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(RequiredAreaAnalyzer.class);

  /**
   * to handle initialization.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * to handle destroy.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /**
   * Analyse allocation lu demand.
   * 
   * @param allocationLU
   *          the allocation lu
   * @param demandScn
   *          the demand scn
   * @param projections
   *          the projections
   * @param projectedSet
   *          the projected set
   * @param localJurisdictions
   *          the local jurisdictions
   * @return the sets the
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   */
  public Set<AreaRequirement> analyseAllocationLUDemand(
      final AllocationLU allocationLU, final DemandScenario demandScn,
      final TreeSet<Projection> projections,
      final NavigableSet<Projection> projectedSet,
      final Set<LocalJurisdiction> localJurisdictions)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.info("@@@@@@@@@@@ Analyze the demand information for {}",
        allocationLU.getLabel());
    // ///////////////

    // //////////////////////
    DemandInfo demandInfo;
    if (allocationLU.isLocal()) {
      // To avoid substantial repetition of code, because we need analysis per
      // projection
      demandInfo = new DemandInfo();
    } else {
      demandInfo = allocationLU.getDemandInfoByScenario(demandScn);
    }
    for (final Projection projection : projectedSet) {
      LOGGER.debug("calculating the demand for projection year: {}",
          projection.getLabel());
      final ResidentialDemographicData currentDemographic = demandScn
          .getDemographicTrend().getResidentialDemographicData(
              projections.lower(projection));
      LOGGER.info("%%% Current Residential demographic projection ={}",
          projections.lower(projection).getLabel());
      final ResidentialDemographicData nextDemographic = demandScn
          .getDemographicTrend().getResidentialDemographicData(projection);
      Double requiredArea = 0.0;

      if (demandInfo instanceof ResidentialDemandInfo) {
        requiredArea = analyseResidentialInfo(allocationLU,
            (ResidentialDemandInfo) demandInfo, currentDemographic,
            nextDemographic);

      } else if (demandInfo instanceof ProjectedDemandInfo) {
        if (demandInfo instanceof EmploymentDemandInfo) {
          // requiredArea = analyseEmploymentInfo(allocationLU,
          // (EmploymentDemandInfo) demandInfo, demandScn, projections,
          // projection);
          final EmploymentDemandInfo einf = (EmploymentDemandInfo) demandInfo;
          final EmploymentDemographicData currentEmployment = demandScn
              .getDemographicTrend().getEmploymentDemographicData(
                  projections.lower(projection), einf.getSectorLabel());
          LOGGER.info("%%% Current Employment demographic projection ={}",
              projections.lower(projection).getLabel());
          final EmploymentDemographicData nextEmployment = demandScn
              .getDemographicTrend().getEmploymentDemographicData(projection,
                  einf.getSectorLabel());

          requiredArea = analyseEmploymentInfoNew(allocationLU,
              (EmploymentDemandInfo) demandInfo, demandScn, projections,
              projection, currentEmployment, nextEmployment);
        } else // PreservationDemandInfo
        {
          LOGGER.debug("Information for Preservation demand analysis: {}",
              allocationLU.getLabel());
          final PreservationDemandInfo pdinfo = (PreservationDemandInfo) demandInfo;
          requiredArea = pdinfo.getProjectedDataByProjection(projection)
              .getReservedArea();
          LOGGER.debug("projected preserved area is: {}", requiredArea);
        }
      }
      if (allocationLU.isLocal()) {
        LOGGER.debug("Information for local info demand analysis: {}",
            allocationLU.getLabel());
        final Set<LocalAreaRequirement> requirements = analyseLocalLU(
            allocationLU, localJurisdictions, currentDemographic,
            nextDemographic, projection, demandScn);
        for (final LocalAreaRequirement localAreaRequirement : requirements) {
          allocationLU.addAreaRequirement(localAreaRequirement);
          localAreaRequirement.setAllocationLU(allocationLU);
          localAreaRequirement.setDemandScenario(demandScn);
          LOGGER.info(
              "==*==* Total required area for land use  {} for local jurisdiction  "
                  + localAreaRequirement.getLocalJurisdiction().getLabel()
                  + "  in projection " + projection.getLabel() + " is : {}",
              allocationLU.getLabel(), localAreaRequirement.getRequiredArea());
        }
      }

      else { // because local jurisdiction creates more than one area
             // requirement, otherwise:
        AreaRequirement areaRequirement = allocationLU
            .getAreaRequirementIfExists(projection, demandScn);
        if (areaRequirement == null) {
          LOGGER.debug("Area requirement not present, creating 1 for: {}",
              allocationLU.getLabel());
          areaRequirement = new AreaRequirement();
          areaRequirement.setProjection(projection);
          areaRequirement.setDemandScenario(demandScn);
          allocationLU.addAreaRequirement(areaRequirement);
          areaRequirement.setAllocationLU(allocationLU);
        }
        areaRequirement.setRequiredArea(requiredArea);
        LOGGER.info("===== Total required area for land use  {} in projection "
            + projection.getLabel() + " is : {}", allocationLU.getLabel(),
            areaRequirement.getRequiredArea());
      }

    }
    return allocationLU.getAreaRequirements();
  }

  /**
   * Analyse local demand info. The logic only requires information from the
   * global demand config, but the algorithm expects a local demand info to know
   * that it must analyse local jurisdictions TODO It can be done easier * @param
   * allocationLU the allocation lu
   * 
   * @param allocationLU
   *          the allocation lu
   * @param localJurisdictions
   *          the local jurisdictions
   * @param currentDemographic
   *          the current demographic
   * @param nextDemographic
   *          the next demographic
   * @param projection
   *          the projection
   * @param demandScn
   *          the demand scn
   * @return the sets LocalAreaRequirement
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public Set<LocalAreaRequirement> analyseLocalLU(
      final AllocationLU allocationLU,
      final Set<LocalJurisdiction> localJurisdictions,
      final ResidentialDemographicData currentDemographic,
      final ResidentialDemographicData nextDemographic,
      final Projection projection, final DemandScenario demandScn)
      throws WifInvalidInputException {
    LOGGER.debug("Information for a Local demand analysis: {}",
        allocationLU.getLabel());

    double requiredArea = 0.0;

    final Set<LocalAreaRequirement> requirements = new HashSet<LocalAreaRequirement>(
        localJurisdictions.size());
    for (final LocalJurisdiction localJurisdiction : localJurisdictions) {
      if (localJurisdiction.getLocalDatas().size() != 0) {
        final LocalData localData = localJurisdiction.getLocalData(projection);
        LOGGER
            .debug(
                "for local jurisdiction: {} the required density (per 1k habitants): {}",
                localJurisdiction.getLabel(), localData.getRequiredDensity());

        final Long newResidents = nextDemographic.getTotalPopulation()
            - currentDemographic.getTotalPopulation();
        requiredArea += newResidents * (localData.getRequiredDensity() / 1000);
        LocalAreaRequirement areaRequirement = (LocalAreaRequirement) allocationLU
            .getAreaRequirementIfExists(projection, demandScn);
        if (areaRequirement == null) {
          areaRequirement = new LocalAreaRequirement();
          areaRequirement.setProjection(projection);
          areaRequirement.setLocalJurisdiction(localJurisdiction);
        }
        areaRequirement.setRequiredArea(requiredArea);
        requirements.add(areaRequirement);
      } else {
        LOGGER.warn("there is no local demand information for jurisdiction {}",
            localJurisdiction.getLabel());
      }
    }
    return requirements;
  }

  /**
   * Analyse employment info.
   * 
   * @param allocationLU
   *          the allocation lu
   * @param demandInfo
   *          the demand info
   * @param demandScn
   *          the demand scn
   * @param projections
   *          the projections
   * @param projection
   *          the projection
   * @return the double
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public Double analyseEmploymentInfo(final AllocationLU allocationLU,
      final EmploymentDemandInfo demandInfo, final DemandScenario demandScn,
      final TreeSet<Projection> projections, final Projection projection)
      throws WifInvalidInputException {
    final ProjectedData currentData = demandInfo.getProjectedData(projections
        .lower(projection));
    final ProjectedData projectedData = demandInfo.getProjectedData(projection);

    Double requiredArea = 0.0;
    LOGGER.debug("{} is associated with {} sector(s),",
        allocationLU.getLabel(), allocationLU.getEmploymentSectors().size());

    if (allocationLU.getEmploymentSectors().size() == 1) {
      LOGGER.debug(
          "*-1 performing Employment many-to--one demand analysis for: {}",
          allocationLU.getLabel());
      requiredArea = getAreaRequirementPerSector(demandInfo, currentData,
          projectedData, 1.0, 1.0);
    } else {
      LOGGER.debug(
          "*-*  performing Employment Many-to-many demand analysis for: {}",
          allocationLU.getLabel());
      final Set<EmploymentDemandInfo> employmentDemandInfos = demandScn
          .getEmploymentDemandInfoByLU(allocationLU);

      for (final EmploymentDemandInfo edi : employmentDemandInfos) {
        final ProjectedData currentediData = ((ProjectedDemandInfo) edi)
            .getProjectedData(projections.lower(projection));
        final ProjectedData projectedediData = ((ProjectedDemandInfo) edi)
            .getProjectedData(projection);
        requiredArea += getAreaRequirementPerSectorMultiple(edi,
            currentediData, projectedediData, allocationLU.getTotalArea(), 1.0,
            1.0);
      }
      requiredArea = requiredArea / employmentDemandInfos.size();
      LOGGER
          .debug(
              "------>... total required area is {}, adjusted for {} associated sectors",
              requiredArea, employmentDemandInfos.size());
    }
    return requiredArea;
  }

  public Double analyseEmploymentInfoNew(final AllocationLU allocationLU,
      final EmploymentDemandInfo demandInfo, final DemandScenario demandScn,
      final TreeSet<Projection> projections, final Projection projection,
      final EmploymentDemographicData currentEmployment,
      final EmploymentDemographicData nextEmployment)
      throws WifInvalidInputException, WifInvalidConfigException {

    Double requiredArea = 0.0;
    LOGGER.debug("{} is associated with {} sector(s),",
        allocationLU.getLabel(), allocationLU.getEmploymentSectors().size());

    Double futureDensityLU = 0.0;
    for (final DensityDemandInfo dInfo : demandScn.getDensityDemandInfo()) {
      if (dInfo.getLanduseID().equals(allocationLU.getId())) {
        futureDensityLU = dInfo.getFutureDensity();
      }
    }

    if (allocationLU.getEmploymentSectors().size() == 1) {
      LOGGER.debug(
          "*-1 performing Employment many-to--one demand analysis for: {}",
          allocationLU.getLabel());

      // new for claudia
      Double dPercentage = 0.0;

      for (final EmploymentSector emps : allocationLU.getEmploymentSectors()) {

        for (final String landUseKey : emps.getAssociatedALUsPercentage()
            .keySet()) {
          if (allocationLU.getId().equals(landUseKey)) {
            dPercentage = emps.getAssociatedALUsPercentage().get(landUseKey);
          }
        }
      }

      // new for claudia

      requiredArea = getAreaRequirementPerSectorNew(demandInfo,
          currentEmployment, nextEmployment, dPercentage, futureDensityLU);
    } else {
      LOGGER.debug(
          "*-*  performing Employment Many-to-many demand analysis for: {}",
          allocationLU.getLabel());
      final Set<EmploymentDemandInfo> employmentDemandInfos = demandScn
          .getEmploymentDemandInfoByLU(allocationLU);

      for (final EmploymentDemandInfo edi : employmentDemandInfos) {

        for (final EmploymentSector emps : allocationLU.getEmploymentSectors()) {
          if (emps.getLabel().equals(edi.getSectorLabel())) {

            // new for claudia
            Double dPercentage = 0.0;
            for (final String landUseKey : emps.getAssociatedALUsPercentage()
                .keySet()) {
              if (allocationLU.getId().equals(landUseKey)) {
                dPercentage = emps.getAssociatedALUsPercentage()
                    .get(landUseKey);
              }
            }
            // new for claudia

            final EmploymentDemographicData currentEmployment1 = demandScn
                .getDemographicTrend().getEmploymentDemographicData(
                    projections.lower(projection), edi.getSectorLabel());
            LOGGER.info("%%% Current Employment demographic projection ={}",
                projections.lower(projection).getLabel());
            final EmploymentDemographicData nextEmployment1 = demandScn
                .getDemographicTrend().getEmploymentDemographicData(projection,
                    edi.getSectorLabel());

            final WifProject project = projectService.getProject(allocationLU
                .getProjectId());

            // final Double areaByLU = geodataFinder.getAreaByLUNew(project
            // .getSuitabilityConfig().getUnifiedAreaZone(), project
            // .getAreaLabel(), project.getExistingLUAttributeName(),
            // allocationLU.getFeatureFieldName());

            final Double areaByLU = 1.0;
            requiredArea += getAreaRequirementPerSectorMultipleNew(edi,
                currentEmployment1, nextEmployment1, areaByLU, dPercentage,
                futureDensityLU);
          }
        }

      }

      // // commented below since new formula
      // requiredArea = requiredArea / employmentDemandInfos.size();
      LOGGER
          .debug(
              "------>... total required area is {}, adjusted for {} associated sectors",
              requiredArea, employmentDemandInfos.size());
    }
    return requiredArea;
  }

  /**
   * Analyse residential info.
   * 
   * @param allocationLU
   *          the allocation lu
   * @param demandInfo
   *          the demand info
   * @param currentDemographic
   *          the current demographic
   * @param nextDemographic
   *          the next demographic
   * @return the double
   */
  public Double analyseResidentialInfo(final AllocationLU allocationLU,
      final ResidentialDemandInfo demandInfo,
      final ResidentialDemographicData currentDemographic,
      final ResidentialDemographicData nextDemographic) {
    Double requiredArea = 0.0;
    LOGGER.debug("performing a residential demand analysis for: {}",
        allocationLU.getLabel());

    requiredArea = DemandProjector.doResidentialDemandArea(demandInfo,
        currentDemographic, nextDemographic);
    return requiredArea;
  }

  /**
   * Gets the area requirement per sector.
   * 
   * @param edi
   *          the edi
   * @param currentData
   *          the current data
   * @param projectedData
   *          the projected data
   * @return the area requirement per sector
   */
  public Double getAreaRequirementPerSector(final EmploymentDemandInfo edi,
      final ProjectedData currentData, final ProjectedData projectedData,
      final Double dPercentage, final Double futureDensity) {
    LOGGER.debug("this Employment LU is associated with sector: {}", edi
        .getSector().getLabel());
    final EmploymentData currentEmployment = (EmploymentData) currentData;
    final EmploymentData projectedEmployment = (EmploymentData) projectedData;
    return DemandProjector.projectEmploymentDensityDemand(currentEmployment
        .getEmployees().longValue(), projectedEmployment.getEmployees()
        .longValue(), edi.getInfillRate(), edi.getFutureDensity(), Integer
        .valueOf(edi.getSector().getAssociatedLUs().size()), dPercentage,
        futureDensity);
  }

  public Double getAreaRequirementPerSectorNew(final EmploymentDemandInfo edi,
      final EmploymentDemographicData currentData,
      final EmploymentDemographicData projectedData, final Double dPercentage,
      final Double futureDensity) {
    LOGGER.debug("this Employment LU is associated with sector: {}", edi
        .getSector().getLabel());

    return DemandProjector
        .projectEmploymentDensityDemand(currentData.getEmployees().longValue(),
            projectedData.getEmployees().longValue(), edi.getInfillRate(),
            edi.getFutureDensity(),
            Integer.valueOf(edi.getSector().getAssociatedLUs().size()),
            dPercentage, futureDensity);
  }

  /**
   * Gets the area requirement per sector multiple.
   * 
   * @param edi
   *          the edi
   * @param currentData
   *          the current data
   * @param projectedData
   *          the projected data
   * @param landUseArea
   *          the land use area
   * @return the area requirement per sector multiple
   */
  public Double getAreaRequirementPerSectorMultiple(
      final EmploymentDemandInfo edi, final ProjectedData currentData,
      final ProjectedData projectedData, final Double landUseArea,
      final Double dPercentage, final Double futureDensityLU) {
    LOGGER.debug("this Employment LU is associated with sector: {}", edi
        .getSector().getLabel());
    final EmploymentData currentEmployment = (EmploymentData) currentData;
    final EmploymentData projectedEmployment = (EmploymentData) projectedData;

    return DemandProjector.projectEmploymentDensityDemandBySector(
        currentEmployment.getEmployees().longValue(), projectedEmployment
            .getEmployees().longValue(), edi.getInfillRate(), edi
            .getFutureDensity(), landUseArea, dPercentage, futureDensityLU);
  }

  public Double getAreaRequirementPerSectorMultipleNew(
      final EmploymentDemandInfo edi,
      final EmploymentDemographicData currentEmployment,
      final EmploymentDemographicData projectedEmployment,
      final Double landUseArea, final Double dPercentage,
      final Double futureDensityLU) {
    LOGGER.debug("this Employment LU is associated with sector: {}", edi
        .getSector().getLabel());

    return DemandProjector.projectEmploymentDensityDemandBySector(
        currentEmployment.getEmployees().longValue(), projectedEmployment
            .getEmployees().longValue(), edi.getInfillRate(), edi
            .getFutureDensity(), landUseArea, dPercentage, futureDensityLU);
  }
}
