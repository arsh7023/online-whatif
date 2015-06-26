package au.org.aurin.wif.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AsyncProjectServiceImpl.
 */
@Service
@Qualifier("validatorCRS")
public class ValidatorCRS {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 213432423433L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ValidatorCRS.class);

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
    LOGGER.trace("Service succesfully cleared! ");
  }

  /**
   * Validate, if the metadata of the provided CRS differs from the ones in
   * geotools, falls back to finding the one with the same code. // TODO Find
   * out why in some instances instead of returning null, it will hang!
   * 
   * @param crs
   *          the crs
   * @param msg
   *          the msg
   * @return the coordinate reference system
   * @throws DataStoreCreationException
   *           the data store creation exception
   */
  public CoordinateReferenceSystem validate(CoordinateReferenceSystem crs,
      String msg) throws DataStoreCreationException {
    final String declaredCode = CRS.toSRS(crs);
    String geotoolsCode = "";
    String parsedWKTcode = "";
    LOGGER.info("spatial union crs WKT: {}", crs.toWKT());
    LOGGER
        .info(
            "spatial union CRS id= \"{}\" checking for the validity of it in Geotools...",
            declaredCode);
    try {
      try {
        geotoolsCode = CRS.lookupIdentifier(crs, false);
      } catch (final Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER
            .warn("Got an exception in lookup identifier of original code, defaulting to  declared code...");
        geotoolsCode = declaredCode;
      }
      if (geotoolsCode == null) {
        LOGGER.debug("invalid original code, defaulting to  declared code...");
        geotoolsCode = declaredCode;
      } else {
        LOGGER.debug("geotools found the code of the original crs: {}",
            geotoolsCode);
      }
      final CoordinateReferenceSystem parseWKT = CRS.parseWKT(crs.toWKT());

      parsedWKTcode = CRS.lookupIdentifier(parseWKT, false);
      LOGGER.debug(
          "crs code of parsedWKT : {}, attempting to decode it in geotools...",
          parsedWKTcode);
      if (parsedWKTcode == null) {
        LOGGER
            .debug(" invalid WKT ccode found, defaulting to the original code...");
        parsedWKTcode = declaredCode;
      }
      // Check that the provided CRS can be understood by Geotools
      if (geotoolsCode.equals(parsedWKTcode)) {
        final CoordinateReferenceSystem crsGeootools = CRS
            .decode(parsedWKTcode);
        crs = crsGeootools;
      } else {
        msg = msg
            + "Original crs doesn't have a valid code for geotools, most likely geoserver will fail...";
        LOGGER.error(msg);
        throw new DataStoreCreationException(msg);
      }
      LOGGER.debug("CRS has been validated!!...");

      return crs;
    } catch (final NoSuchAuthorityCodeException e) {
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    } catch (final FactoryException e) {
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    } catch (final Exception e) {
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    }
  }

  /**
   * Validate simple, it just doesn't accept invalid codes
   * 
   * @param crs
   *          the crs
   * @param msg
   *          the msg
   * @return the coordinate reference system
   * @throws DataStoreCreationException
   *           the data store creation exception
   */
  public CoordinateReferenceSystem validateSimple(
      final CoordinateReferenceSystem crs, String msg)
      throws DataStoreCreationException {
    final String declaredCode = CRS.toSRS(crs);
    CoordinateReferenceSystem decodedCrs = null;
    msg = msg + "The CRS provided is invalid in geotools";
    LOGGER.info("spatial union crs WKT: {}", crs.toWKT());
    LOGGER
        .info(
            "spatial union CRS id= \"{}\" checking for the validity of it in Geotools...",
            declaredCode);
    try {
      final String wkt = crs.toWKT();
      final CoordinateReferenceSystem parsedCRS = CRS.parseWKT(wkt);
      LOGGER
          .debug("CRS WKT has been parsed! Looking up the identifier, may take a while...");

      String code = CRS.lookupIdentifier(parsedCRS, true);

      if (code == null) {
        code = CRS.lookupIdentifier(Citations.EPSG, parsedCRS, true);
        if (code == null) {

          final Integer codevalue = CRS.lookupEpsgCode(parsedCRS, true);

          if (codevalue != null) {

            final String str = "EPSG:" + String.valueOf(codevalue);
            decodedCrs = CRS.decode(str);
            LOGGER.warn("Code  found is: {}", str);
          } else {

            decodedCrs = CRS.decode(WifKeys.CRS_DEFAULT);
            LOGGER.warn("Code not found, defaulting to: {}",
                WifKeys.CRS_DEFAULT);
          }
        }
      } else {
        LOGGER.info("Code found: {}", code);

        decodedCrs = CRS.decode(code);

        LOGGER.debug("CRS has been validated to {} : {} !!...", decodedCrs
            .getName().getAuthority(), decodedCrs.getName().getCode());
      }
      return decodedCrs;
    } catch (final NoSuchAuthorityCodeException e) {
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    } catch (final FactoryException e) {
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    } catch (final Exception e) {
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    }
  }

}
