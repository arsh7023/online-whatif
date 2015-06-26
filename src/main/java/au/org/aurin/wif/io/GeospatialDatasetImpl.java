package au.org.aurin.wif.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.factory.Hints;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A representation of a geospatial dataset.
 * 
 * @author Gerson Galang
 */
public class GeospatialDatasetImpl implements GeospatialDataset {

  /** The logger. */
  private static final Log LOGGER = LogFactory
      .getLog(GeospatialDatasetImpl.class);

  protected SimpleFeatureSource featureSource;

  private final DataSource dataSource;

  private final CRSAuthorityFactory crsFactory;

  /**
   * Construct a GeospatialDatasetImpl object.
   * <p>
   * The constructor for this class was given a friendly access to hide how it
   * is implemented and to force users to only use the DataSourceFactory to get
   * instances of the GeospatialDataset implementations.
   * </p>
   * 
   * @param featureSource
   */
  GeospatialDatasetImpl(final DataSource dataSource,
      final SimpleFeatureSource featureSource) {
    this.dataSource = dataSource;
    this.featureSource = featureSource;
    final Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER,
        Boolean.TRUE);

    crsFactory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
  }

  public SimpleFeatureSource getFeatureSource() {
    return featureSource;
  }

  public String getName() {
    return featureSource.getSchema().getTypeName();
  }

  public SimpleFeatureCollection getFeatures(final DatasetQueryParams params)
      throws InvalidDataQueryParamsException, DatasetAccessException {
    final String[] propertyNames = params.getPropertyNamesToReturn();

    final Filter filter = params.getFilter();

    final String srsName = params.getSrsName();

    try {
      final Query query = new Query(getName());
      if (filter != null) {
        query.setFilter(filter);
      }
      if (propertyNames != null && propertyNames.length > 0) {
        query.setPropertyNames(propertyNames);
      }
      if (srsName != null) {
        final CoordinateReferenceSystem crs = crsFactory
            .createCoordinateReferenceSystem(srsName);

        // TODO: do we really need to call both of these methods?
        query.setCoordinateSystem(crs);
        // query.setCoordinateSystemReproject(crs);
      }

      final DataStore dataStore = (DataStore) featureSource.getDataStore();
      if (WfsUtil.isWfsDataStore(dataStore)) {
        final WFSDataStore wfsDataStore = (WFSDataStore) dataStore;

        if (WfsUtil.isWfsVersion1_0_0(wfsDataStore)) {
          // TODO: need to rethink of how to support different versions of WFS
          // again!!!
          LOGGER.info("Using getFeature 1.0.0");

          return ((AbstractGeospatialDataSource) getDataSource()).wfsWorkaround
              .getFeatures_1_0_0(getName(), propertyNames,
                  params.getCQLFilterString(), srsName);
        }
      }
      // use the new way of performing getFeatures for WFS 1.1 and above
      return featureSource.getFeatures(query);
    } catch (final IOException e) {
      LOGGER.error("IOException was thrown while calling getFeatures!", e);
      throw new DatasetAccessException(
          e.getMessage(),
          "A communication exception has occurred while trying to access the remote dataset. Please, try later or notify AURIN if the problem persists",
          e);
    } catch (final NoSuchAuthorityCodeException e) {
      LOGGER.error(
          "NoSuchAuthorityCodeException was thrown while calling getFeatures!",
          e);
      throw new InvalidDataQueryParamsException(e.getMessage(), e);
    } catch (final FactoryException e) {
      LOGGER.error("FactoryException was thrown while calling getFeatures!", e);
      throw new DatasetAccessException(
          e.getMessage(),
          "An internal error occured when trying to access the dataset. Please, notify AURIN about this problem.",
          e);
    }
  }

  public BoundingBox getBoundingBox() {
    try {
      final BoundingBox bbox = featureSource.getBounds();
      return bbox;
    } catch (final IOException e) {
      LOGGER.error("Error while getting the bounding box", e);
      return null;
    }
  }

  public CoordinateReferenceSystem getSRS() {
    return featureSource.getSchema().getCoordinateReferenceSystem();
  }

  public List<DatasetAttribute> getAttributes() {
    // convert the list of AttributeDescriptors into a list of DatasetAttributes
    final List<DatasetAttribute> attributes = new ArrayList<DatasetAttribute>();
    for (final AttributeDescriptor attrDescriptor : getAttributeDescriptors()) {
      attributes.add(new DatasetAttribute(attrDescriptor.getLocalName(),
          ((AttributeTypeImpl) attrDescriptor.getType()).getBinding().getName()
              .toString()));
    }
    return attributes;
  }

  public GeometryDescriptor getGeometryAttribute() {
    return featureSource.getSchema().getGeometryDescriptor();
  }

  public List<AttributeDescriptor> getAttributeDescriptors() {
    return featureSource.getSchema().getAttributeDescriptors();
  }

  public DataCollection getData(final DatasetQueryParams params)
      throws InvalidDataQueryParamsException, DatasetAccessException {
    return new DataCollection(getFeatures(params));
  }

  public DataSource getDataSource() {
    return dataSource;
  }

}
