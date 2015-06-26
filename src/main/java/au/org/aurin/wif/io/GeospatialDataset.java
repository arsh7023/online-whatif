package au.org.aurin.wif.io;

import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 
 * @author Gerson Galang
 */
public interface GeospatialDataset extends Dataset {

  SimpleFeatureSource getFeatureSource();

  SimpleFeatureCollection getFeatures(DatasetQueryParams params)
      throws InvalidDataQueryParamsException, DatasetAccessException;

  BoundingBox getBoundingBox();

  CoordinateReferenceSystem getSRS();

  GeometryDescriptor getGeometryAttribute();

  List<AttributeDescriptor> getAttributeDescriptors();
}
