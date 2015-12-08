/*
 *
 */
package au.org.aurin.wif.impl.allocation;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import au.org.aurin.wif.config.GeoServerConfig;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationConfigsException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.ColorALU;
import au.org.aurin.wif.model.allocation.LandUseFunction;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationConfigsService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;

/**
 * The Class AllocationConfigServiceImpl.
 */
@Service
@Qualifier("AllocationConfigsService")
public class AllocationConfigsServiceImpl implements AllocationConfigsService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationConfigsServiceImpl.class);

  /** The Allocation config dao. */
  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The manual demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The geoserver publisher. */
  @Autowired
  private GeoServerRESTPublisher geoserverPublisher;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  @Autowired
  private GeoServerConfig geoserverConfig;

  @Autowired
  private RestTemplate restTemplate;

  /**
   * Inits the.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace("AllocationConfig Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.AllocationConfigService#createAllocationConfig
   * (au.org.aurin.wif.model.Allocation.AllocationConfig, java.lang.String)
   */
  @Override
  public AllocationConfigs createAllocationConfigs(
      final AllocationConfigs AllocationConfigs, final String projectId)
          throws WifInvalidInputException, WifInvalidConfigException,
          IncompleteAllocationConfigsException, ParsingException {
    if (AllocationConfigs == null) {
      LOGGER
      .error("createAllocationConfig failed: AllocationConfig is null or invalid");
      throw new WifInvalidInputException(
          "createAllocationConfig failed: AllocationConfig is null or invalid");
    }

    LOGGER.debug("createAllocationConfig for project  id ={}", projectId);
    WifProject project = projectService.getProject(projectId);
    project = projectParser.parse(project);
    AllocationConfigs.setProjectId(projectId);

    // check for allocationColumnsMap and undeveloped land use column
    if (AllocationConfigs.getAllocationColumnsMap().size() == 0) {
      // final ManualDemandConfig manualdemandConfig = manualdemandConfigService
      // .getManualDemandConfig(project.getId());
      final DemandConfig demandConfig = demandConfigService
          .getDemandConfig(project.getId());

      // for (final Projection projection : manualdemandConfig.getProjections())
      // {
      for (final Projection projection : demandConfig.getProjections()) {
        final String ffName = "ALU_" + projection.getYear();
        LOGGER.trace("Future projection feature field name: {}", ffName);
        AllocationConfigs.getAllocationColumnsMap().put(projection.getLabel(),
            ffName);
      }

      LOGGER.trace("persisting allocation config  for project : {}",
          project.getLabel());

      final Set<AllocationLU> allocationLandUses = project
          .getAllocationLandUses();
      for (final AllocationLU allocationLU : allocationLandUses) {
        if (allocationLU.getAssociatedLU() != null) {
          allocationLU.setAllocationFeatureFieldName(WifKeys.FUTURELU_PREFIX
              + allocationLU.getFeatureFieldName());
          LOGGER.trace("... ALU label: {}, allocationFFName {}",
              allocationLU.getLabel(),
              allocationLU.getAllocationFeatureFieldName());
          allocationLUDao.updateAllocationLU(allocationLU);
        }
        // Automatically adding undeveloped land uses
        if (allocationLU.getLandUseFunction().equals(LandUseFunction.LBCS_9XXX)) {
          AllocationConfigs.getUndevelopedLUsColumns().add(
              allocationLU.getFeatureFieldName());
        }
      }
      final ArrayList<String> columnList = new ArrayList<String>(
          AllocationConfigs.getAllocationColumnsMap().values());
      final String tableName = project.getSuitabilityConfig()
          .getUnifiedAreaZone();
      final Boolean lsw = true;

      if (geodataFinder.expandUAZcolumnsALU(tableName, columnList)) {
        // for reloading geoserver layers
        geoserverPublisher.reload();
      }
    }
    // new claudia
    else {

      final DemandConfig demandConfig = demandConfigService
          .getDemandConfig(project.getId());
      // new claudia 2

      final Set<Entry<String, String>> associatedALUs = AllocationConfigs
          .getAllocationColumnsMap().entrySet();

      final List<String> arrStr = new ArrayList<String>();

      for (final Entry<String, String> entryLU : associatedALUs) {
        Boolean lsw = false;

        for (final Projection projection : demandConfig.getProjections()) {
          if (entryLU.getKey().equals(projection.getLabel())) {
            lsw = true;
          }
        }
        if (lsw == false) {
          arrStr.add(entryLU.getKey());
        }
      }

      for (final String str : arrStr) {
        AllocationConfigs.getAllocationColumnsMap().remove(str);
      }

      // end new2

      if (AllocationConfigs.getAllocationColumnsMap().size() < demandConfig
          .getProjections().size()) {
        for (final Projection projection : demandConfig.getProjections()) {
          if (!AllocationConfigs.getAllocationColumnsMap().containsKey(
              projection.getLabel())) {
            final String ffName = "ALU_" + projection.getYear();
            LOGGER.trace("Future projection feature field name: {}", ffName);
            AllocationConfigs.getAllocationColumnsMap().put(
                projection.getLabel(), ffName);
            geoserverPublisher.reload();
          }

        }

      }

    }
    // end new claudia
    // ///////////////////

    LOGGER.debug("persisting the AllocationConfig for project ={}",
        project.getLabel());
    final AllocationConfigs manualsavedAllocationConfig = AllocationConfigsDao
        .persistAllocationConfigs(AllocationConfigs);
    LOGGER.debug("returning the AllocationConfig with id={}",
        manualsavedAllocationConfig.getId());
    project.setAllocationConfigsId(manualsavedAllocationConfig.getId());
    // TODO Find out if it should be done through the service, but it is much
    // more efficient to
    wifProjectDao.updateProject(project);

    return manualsavedAllocationConfig;
  }

  @Override
  public Boolean CreateStyle(final AllocationConfigs AllocationConfigs,
      final String projectId, final Boolean lsw) {
    try {
      if (AllocationConfigs == null) {
        LOGGER.error("CreateStyle failed: AllocationConfig is null or invalid");
        throw new WifInvalidInputException(
            "CreateStyle failed: AllocationConfig is null or invalid");
      }

      LOGGER.debug("createAllocationConfig for project  id ={}", projectId);
      WifProject project = projectService.getProject(projectId);
      project = projectParser.parse(project);
      AllocationConfigs.setProjectId(projectId);

      // new for geoserver styling and geoserver cache

      if (AllocationConfigs.getColorALUs().size() != 0) {

        String propertyName = "";
        final String opacity = "1";

        propertyName = project.getExistingLUAttributeName();

        // final String[] columnList =
        // AllocationConfigs.getAllocationColumnsMap()
        // .values().toArray(new String[0]);
        // Arrays.sort(columnList);
        // int i = 1;
        // for (final String aluLabel : columnList) {
        // if (i == 1) {
        // propertyName = aluLabel;
        // } else {
        // i = i + 1;
        // }
        // }

        final String styleName = project.getSuitabilityConfig()
            .getUnifiedAreaZone();

        final String potentialStyleStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<sld:StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.0.0\">"
            + "<sld:NamedLayer>"
            + "<sld:Name>"
            + styleName
            + "</sld:Name>"
            + "<sld:UserStyle>"
            + "<sld:Title>SLD Potential Development</sld:Title>"
            + "<sld:FeatureTypeStyle>";

        final String potentialStyleEnd = "</sld:FeatureTypeStyle>"
            + "</sld:UserStyle>" + "</sld:NamedLayer>"
            + "</sld:StyledLayerDescriptor>";
        String NewStyle = "";
        String sld = "";
        for (final ColorALU color : AllocationConfigs.getColorALUs()) {

          final String title = color.getLabel();
          final String colorcode = color.getAssociatedColors();

          NewStyle = "<sld:Rule>" + "<sld:Title>" + title + "</sld:Title>"
              + "<ogc:Filter>" + "<PropertyIsEqualTo>" + "<ogc:PropertyName>"
              + propertyName + "</ogc:PropertyName>" + "<ogc:Literal>" + title
              + "</ogc:Literal>" + "</PropertyIsEqualTo>" + "</ogc:Filter>"
              + "<sld:PolygonSymbolizer>" + "<sld:Fill>"
              + "<sld:CssParameter name=\"fill\">" + colorcode
              + "</sld:CssParameter>" + "<CssParameter name=\"fill-opacity\">"
              + opacity + "</CssParameter>" + "</sld:Fill>"
              + "</sld:PolygonSymbolizer>" + "</sld:Rule>";

          sld = sld + NewStyle;

        }// end for

        sld = potentialStyleStart + sld + potentialStyleEnd;
        LOGGER.info("sld is : " + sld);
        // final String styleName = geoserverConfig.getWorkspace() + ":"
        // + project.getSuitabilityConfig().getUnifiedAreaZone();

        if (lsw == true) {
          geoserverPublisher.publishStyle(sld, styleName);
        } else if (lsw == false) {
          geoserverPublisher.publishStyle(sld, styleName);
          geoserverPublisher.updateStyle(sld, styleName);
        }

        // //geoserverPublisher.publishStyle(sld, styleName);
        // geoserverPublisher.reload();

        // ///////seeding cache

        final HttpHeaders headers = new HttpHeaders();

        final String aut = geoserverConfig.getUserName() + ":"
            + geoserverConfig.getPassword();

        LOGGER.info(aut);

        final String plainCreds = aut; // "admin:geoserver";
        final byte[] plainCredsBytes = plainCreds.getBytes();

        final byte[] base64CredsBytes = org.apache.commons.codec.binary.Base64
            .encodeBase64(plainCredsBytes);
        final String base64Creds = new String(base64CredsBytes);

        final String auth = aut;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset
            .forName("US-ASCII")));
        final String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);

        // headers.add("Accept", "application/json");
        headers.setContentType(MediaType.TEXT_XML);
        // headers.add("Authorization", "Basic " + base64Creds);

        final String styleName2 = geoserverConfig.getWorkspace() + ":"
            + project.getSuitabilityConfig().getUnifiedAreaZone();

        // //////change default style

        final String changeStyle = "<layer><defaultStyle><name>" + styleName
            + "</name></defaultStyle></layer>";
        LOGGER.info(changeStyle);
        final String styleUrl = geoserverConfig.getRestUrl() + "rest/layers/"
            + styleName2 + "/";
        LOGGER.info(styleUrl);
        final HttpEntity<String> entityStyle = new HttpEntity<String>(
            changeStyle, headers);

        final ResponseEntity<String> stStyle = restTemplate.exchange(styleUrl,
            HttpMethod.PUT, entityStyle, String.class);
        LOGGER.info(stStyle.getBody());
        // //////end change default style

        // ///////seeding
        String mydata = "<seedRequest><name>"
            + styleName2
            + "</name><srs><number>900913</number></srs><zoomStart>1</zoomStart><zoomStop>15</zoomStop><format>image/png</format><type>RESEED</type><threadCount>1</threadCount><parameters><entry><string>STYLES</string><string>"
            + styleName + "</string></entry></parameters></seedRequest>";

        mydata = "<seedRequest><name>"
            + styleName2
            + "</name><srs><number>900913</number></srs><zoomStart>1</zoomStart><zoomStop>15</zoomStop><format>image/png</format><type>RESEED</type><threadCount>1</threadCount>"
            + "</seedRequest>";

        LOGGER.info(mydata);

        final String url = geoserverConfig.getRestUrl() + "gwc/rest/seed/"
            + styleName2 + ".xml";

        LOGGER.info(url);

        final HttpEntity<String> entity = new HttpEntity<String>(mydata,
            headers);

        final ResponseEntity<String> st = restTemplate.exchange(url,
            HttpMethod.POST, entity, String.class);
        LOGGER.info(st.getBody());
        // ///////end seeding

        // /////////////////////////////////////////////////////////////////
        // //Httpclient which works as well!
        // final CredentialsProvider credsProvider = new
        // BasicCredentialsProvider();
        //
        // credsProvider.setCredentials(new AuthScope("localhost", 80),
        // new UsernamePasswordCredentials(geoserverConfig.getUserName(),
        // geoserverConfig.getPassword()));
        //
        // try {
        // final String inputXML = mydata;
        //
        // final StringEntity entity1 = new StringEntity(inputXML,
        // ContentType.create("text/xml", Consts.UTF_8));
        //
        // final HttpPost httppost = new HttpPost(url);
        //
        // httppost.setHeader("Content-Type", "text/xml");
        //
        // final CloseableHttpClient client = HttpClients.custom()
        // .setDefaultCredentialsProvider(credsProvider).build();
        //
        // System.out.println("BeforeExecuting request ");
        // httppost.setEntity(entity1);
        //
        // System.out.println("Executing request " + httppost.getRequestLine());
        // final HttpResponse response = client.execute(httppost);
        //
        // try {
        // System.out.println("----------------------------------------");
        // System.out.println(response.getStatusLine());
        // EntityUtils.consume(response.getEntity());
        // } finally {
        // // response.close();
        // }
        // } finally {
        // // client.close();
        // }

        // geoserverPublisher.reload();

        // curl -v -u admin:geoserver -XPOST -H "Content-type: text/xml" -d
        // '<seedRequest><name>whatif:wif_d985e18c9b6a5fceb959c489524bba9d</name><srs><number>900913</number></srs><zoomStart>1</zoomStart><zoomStop>15</zoomStop><format>image/png</format><type>RESEED</type><threadCount>2</threadCount><parameters><entry><string>STYLES</string><string>whatif:wif_d985e18c9b6a5fceb959c489524bba9d</string></entry></parameters></seedRequest>'
        // "http://localhost/geoserver/gwc/rest/seed/whatif:wif_d985e18c9b6a5fceb959c489524bba9d.xml"

      }// end if

    } catch (final Exception e) {
      LOGGER.error(e.toString());
    }

    return null;

  }

  HttpHeaders createHeaders(final String username, final String password) {
    return new HttpHeaders() {
      {
        final String auth = username + ":" + password;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        final String authHeader = "Basic " + new String(encodedAuth);
        set("Authorization", authHeader);
      }
    };
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationConfigService#getAllocationConfig(java.lang
   * .String)
   */
  @Override
  public AllocationConfigs getAllocationConfigs(final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    String AllocationConfigsId = null;
    LOGGER.debug("getting the AllocationConfig for project with ID={}",
        projectId);
    String msg = "illegal argument, the ID " + AllocationConfigsId
        + " supplied doesn't identify a valid AllocationConfig ";
    try {
      WifProject project = projectService.getProjectNoMapping(projectId);
      project = projectParser.parse(project);
      AllocationConfigs AllocationConfigs = null;
      AllocationConfigsId = project.getAllocationConfigsId();

      if (AllocationConfigsId != null) {

        LOGGER.info("getting the AllocationConfig with ID={}",
            AllocationConfigsId);
        AllocationConfigs = AllocationConfigsDao
            .findAllocationConfigsById(AllocationConfigsId);
        if (AllocationConfigs == null) {
          msg = "illegal argument, the AllocationConfigs with the ID "
              + AllocationConfigsId + " supplied was not found ";
          LOGGER.error(msg);
          throw new InvalidEntityIdException(msg);
        } else {
          // AllocationConfigs = AllocationSetupParser.parse(AllocationConfigs,
          // project);
          project.setAllocationConfigs(AllocationConfigs);
          // project = parseALUSectors(AllocationConfig, project);
          AllocationConfigs.setWifProject(project);
        }
      } else {
        project.setAllocationConfigs(AllocationConfigs);
        // AllocationConfigs.setWifProject(project);
        return AllocationConfigs;
        // LOGGER.error(msg);
        // throw new InvalidEntityIdException(msg);
      }
      return AllocationConfigs;

    } catch (final IllegalArgumentException e) {

      LOGGER.error(msg);
      throw new WifInvalidInputException(msg);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationConfigService#updateAllocationConfig(au.
   * org.aurin .wif.model.allocation.AllocationConfig, java.lang.String)
   */
  @Override
  public void updateAllocationConfigs(
      final AllocationConfigs AllocationConfigs, final String projectId)
          throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("updating AllocationConfig: {}, with id: {}",
        AllocationConfigs.getLabel(), AllocationConfigs.getId());
    try {
      final WifProject project = projectService.getProject(projectId);
      if (AllocationConfigs.getProjectId().equals(projectId)) {

        // AllocationConfig = AllocationSetupParser.parse(AllocationConfig,
        // project);
        AllocationConfigs
        .setRevision(AllocationConfigsDao.findAllocationConfigsById(
            AllocationConfigs.getId()).getRevision());
        AllocationConfigsDao.updateAllocationConfigs(AllocationConfigs);
      } else {
        final String msg = "illegal argument, the ID " + projectId
            + " supplied is not associated with this AllocationConfig ";
        LOGGER.error(msg);
        throw new WifInvalidInputException(msg);
      }
    } catch (final IllegalArgumentException e) {

      LOGGER
      .error("illegal argument, the AllocationConfig supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the AllocationConfig supplied is invalid ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationConfigService#deleteAllocationConfig(java
   * .lang.String , java.lang.String)
   */
  @Override
  public void deleteAllocationConfigs(final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the AllocationConfig from project with ID={}",
        projectId);
    try {
      final AllocationConfigs AllocationConfigs = getAllocationConfigs(projectId);
      final WifProject project = wifProjectDao.findProjectById(projectId);
      project.setAllocationConfigsId(null);
      project.setAllocationConfigs(null);
      wifProjectDao.updateProject(project);
      AllocationConfigsDao.deleteAllocationConfigs(AllocationConfigs);
    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + projectId
          + " supplied doesn't identify a valid AllocationConfig ");
      throw new InvalidEntityIdException("illegal argument, the ID "
          + projectId + " supplied doesn't identify a valid AllocationConfig ");
    }
  }



  @Override
  public Boolean CreateStyleDemo(final AllocationConfigs AllocationConfigs,
      final String projectId, final Boolean lsw) {
    try {
      if (AllocationConfigs == null) {
        LOGGER.error("CreateStyle failed: AllocationConfig is null or invalid");
        throw new WifInvalidInputException(
            "CreateStyle failed: AllocationConfig is null or invalid");
      }

      LOGGER.debug("createAllocationConfig for project  id ={}", projectId);
      WifProject project = projectService.getProject(projectId);
      project = projectParser.parse(project);
      AllocationConfigs.setProjectId(projectId);

      Set<ColorALU> sttColor= new HashSet<ColorALU>();
      // new for geoserver styling and geoserver cache
      if (AllocationConfigs.getColorALUs().size() == 0) {

        final String attr = project.getExistingLUAttributeName();

        final SuitabilityConfig suitabilityConfig = project
            .getSuitabilityConfig();
        if (suitabilityConfig != null) {
          final String uazTbl = suitabilityConfig.getUnifiedAreaZone();
          final List<String> lstColor = geodataFinder.getDistinctColorsForALUConfig(uazTbl, attr);
          if (lstColor.size()>0)
          {
            for (final String color:lstColor)
            {
              final String[] st = color.split("@");
              final ColorALU col = new ColorALU();
              col.setLabel(st[0]);
              col.setAssociatedColors(st[1]);
              sttColor.add(col);
            }
          }
        }

      }
      else
      {
        sttColor = AllocationConfigs.getColorALUs();
      }

      //if (AllocationConfigs.getColorALUs().size() != 0) {
      if (sttColor.size() != 0) {

        String propertyName = "";
        final String opacity = "1";

        propertyName = project.getExistingLUAttributeName();
        final String styleName = project.getSuitabilityConfig()
            .getUnifiedAreaZone();

        final String potentialStyleStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<sld:StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.0.0\">"
            + "<sld:NamedLayer>"
            + "<sld:Name>"
            + styleName
            + "</sld:Name>"
            + "<sld:UserStyle>"
            + "<sld:Title>SLD Potential Development</sld:Title>"
            + "<sld:FeatureTypeStyle>";

        final String potentialStyleEnd = "</sld:FeatureTypeStyle>"
            + "</sld:UserStyle>" + "</sld:NamedLayer>"
            + "</sld:StyledLayerDescriptor>";
        String NewStyle = "";
        String sld = "";
        //for (final ColorALU color : AllocationConfigs.getColorALUs()) {
        for (final ColorALU color : sttColor) {

          final String title = color.getLabel();
          final String colorcode = color.getAssociatedColors();

          NewStyle = "<sld:Rule>" + "<sld:Title>" + title + "</sld:Title>"
              + "<ogc:Filter>" + "<PropertyIsEqualTo>" + "<ogc:PropertyName>"
              + propertyName + "</ogc:PropertyName>" + "<ogc:Literal>" + title
              + "</ogc:Literal>" + "</PropertyIsEqualTo>" + "</ogc:Filter>"
              + "<sld:PolygonSymbolizer>" + "<sld:Fill>"
              + "<sld:CssParameter name=\"fill\">" + colorcode
              + "</sld:CssParameter>" + "<CssParameter name=\"fill-opacity\">"
              + opacity + "</CssParameter>" + "</sld:Fill>"
              + "</sld:PolygonSymbolizer>" + "</sld:Rule>";

          sld = sld + NewStyle;

        }// end for

        sld = potentialStyleStart + sld + potentialStyleEnd;
        LOGGER.info("sld is : " + sld);

        final Document doc = DocumentHelper.parseText(sld);
        final StringWriter sw = new StringWriter();
        final OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndent(true);
        format.setIndentSize(3);
        final XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);

        sld=sw.toString();
        LOGGER.info("sld is : " + sld);
        // final String styleName = geoserverConfig.getWorkspace() + ":"
        // + project.getSuitabilityConfig().getUnifiedAreaZone();
        geoserverPublisher.reload();
        if (lsw == true) {
          geoserverPublisher.publishStyle(sld, styleName);
        } else if (lsw == false) {
          geoserverPublisher.publishStyle(sld, styleName);
          geoserverPublisher.updateStyle(sld, styleName);
        }

        // //geoserverPublisher.publishStyle(sld, styleName);
        // geoserverPublisher.reload();

        // ///////seeding cache

        final HttpHeaders headers = new HttpHeaders();

        final String aut = geoserverConfig.getUserName() + ":"
            + geoserverConfig.getPassword();

        LOGGER.info(aut);

        final String plainCreds = aut; // "admin:geoserver";
        final byte[] plainCredsBytes = plainCreds.getBytes();

        final byte[] base64CredsBytes = org.apache.commons.codec.binary.Base64
            .encodeBase64(plainCredsBytes);
        final String base64Creds = new String(base64CredsBytes);

        final String auth = aut;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset
            .forName("US-ASCII")));
        final String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);

        // headers.add("Accept", "application/json");
        headers.setContentType(MediaType.TEXT_XML);
        // headers.add("Authorization", "Basic " + base64Creds);

        final String styleName2 = geoserverConfig.getWorkspace() + ":"
            + project.getSuitabilityConfig().getUnifiedAreaZone();

        // //////change default style

        final String changeStyle = "<layer><defaultStyle><name>" + styleName
            + "</name></defaultStyle></layer>";
        LOGGER.info(changeStyle);
        final String styleUrl = geoserverConfig.getRestUrl() + "rest/layers/"
            + styleName2 + "/";
        LOGGER.info(styleUrl);
        final HttpEntity<String> entityStyle = new HttpEntity<String>(
            changeStyle, headers);

        final ResponseEntity<String> stStyle = restTemplate.exchange(styleUrl,
            HttpMethod.PUT, entityStyle, String.class);
        LOGGER.info(stStyle.getBody());
        // //////end change default style

        // ///////seeding
        String mydata = "<seedRequest><name>"
            + styleName2
            + "</name><srs><number>900913</number></srs><zoomStart>1</zoomStart><zoomStop>15</zoomStop><format>image/png</format><type>RESEED</type><threadCount>1</threadCount><parameters><entry><string>STYLES</string><string>"
            + styleName + "</string></entry></parameters></seedRequest>";

        mydata = "<seedRequest><name>"
            + styleName2
            + "</name><srs><number>900913</number></srs><zoomStart>1</zoomStart><zoomStop>15</zoomStop><format>image/png</format><type>RESEED</type><threadCount>1</threadCount>"
            + "</seedRequest>";

        LOGGER.info(mydata);

        final String url = geoserverConfig.getRestUrl() + "gwc/rest/seed/"
            + styleName2 + ".xml";

        LOGGER.info(url);

        final HttpEntity<String> entity = new HttpEntity<String>(mydata,
            headers);

        final ResponseEntity<String> st = restTemplate.exchange(url,
            HttpMethod.POST, entity, String.class);
        LOGGER.info(st.getBody());
        // ///////end seeding

      }// end if
      // geoserverPublisher.reload();

    } catch (final Exception e) {
      LOGGER.error(e.toString());
    }

    return null;

  }

}
