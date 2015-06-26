package au.org.aurin.wif.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.io.ShapeFile2PostGISCreationException;
import au.org.aurin.wif.exception.io.ZipFileExtractionException;
import au.org.aurin.wif.impl.ValidatorCRS;
import au.org.aurin.wif.svc.WifKeys;

import com.google.common.io.Files;

/**
 * The Class FileToPostgisExporter.
 */

@Component
public class FileToPostgisExporter {

  /** The Constant KB. */
  private static final int KB = 1024;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(FileToPostgisExporter.class);

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;
  /** The exporter. */
  @Autowired
  private DataStoreToPostgisExporter exporter;

  @Resource
  private ValidatorCRS validatorCRS;

  @Autowired
  @Qualifier(value = "wifDataStoreConfig")
  private PostgisDataStoreConfig postgisDataStoreConfig;

  @Autowired
  @Qualifier(value = "myjdbcDataStoreConfig")
  private jdbcDataStoreConfig myjdbcDataStoreConfig;

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);

  }

  public File getZipShp(final String unifiedAreaZone) throws IOException,
      DatabaseFailedException {

    // final SimpleFeatureCollection uazCollection = geodataFinder
    // .getFeatureCollectionfromDB(unifiedAreaZone, Filter.INCLUDE);
    //
    // final String uuid = UUID.randomUUID().toString();
    // // System.getProperty("java.io.tmpdir");
    // final File dir = Files.createTempDir();
    //
    // final File shpFile = new File(dir + "/" + uuid + ".shp");
    // if (!shpFile.exists()) {
    // shpFile.createNewFile();
    // LOGGER.info(shpFile.getAbsolutePath() + " has been created");
    // }
    // // Else throw exception
    // final ShapefileDataStoreFactory dataStoreFactory = new
    // ShapefileDataStoreFactory();
    //
    // final Map<String, Serializable> params = new HashMap<String,
    // Serializable>();
    // params.put("url", shpFile.toURI().toURL());
    // params.put("create spatial index", Boolean.TRUE);
    //
    // final ShapefileDataStore shpDataStore = (ShapefileDataStore)
    // dataStoreFactory
    // .createNewDataStore(params);
    // shpDataStore.createSchema(uazCollection.getSchema());
    // final Transaction transaction = new DefaultTransaction("create");
    //
    // final String typeName = shpDataStore.getTypeNames()[0];
    // LOGGER.info("typeName: " + typeName);
    //
    // final SimpleFeatureSource featureSource = shpDataStore
    // .getFeatureSource(typeName);
    //
    // if (featureSource instanceof FeatureStore) {
    // final SimpleFeatureStore featureStore = (SimpleFeatureStore)
    // featureSource;
    // featureStore.setTransaction(transaction);
    //
    // try {
    //
    // featureStore.addFeatures(uazCollection);
    // transaction.commit();
    //
    // } catch (final Exception problem) {
    // LOGGER.error("Problem occurred in getZipShp");
    // transaction.rollback();
    //
    // } finally {
    // transaction.close();
    // }
    //
    // } else {
    // LOGGER.info(typeName + " does not support read/write access");
    // }
    // LOGGER.info("creating this zip file: " + uuid);
    // final File zipFile = File.createTempFile(uuid, ".zip");
    // ZipUtils.compress(dir.getAbsolutePath(), zipFile);
    // final String dstr = dir.getAbsolutePath();
    //
    // LOGGER.info("this zip file created: " + uuid);
    // final String files[] = dir.list();
    //
    // // since the zip file created, we dont need shapefile.
    // LOGGER.info("Start directory deleting :" + dstr);
    // for (final String temp1 : files) {
    // // construct the file structure
    // final File fileDelete = new File(dir, temp1);
    //
    // // delete
    // fileDelete.delete();
    //
    // }
    //
    // dir.delete();
    // LOGGER.info("directory deleted :" + dstr);
    //
    //
    // return zipFile;

    // //////////
    final StringBuffer output = new StringBuffer();

    final Process p;
    try {
      // p = Runtime
      // .getRuntime()
      // .exec(
      // "/usr/local/bin/pgsql2shp '/Users/ashamakhy/Documents/claudia/perth4' -h db2.aurin.org.au -u whatif  whatif-development wifdemo.wif_d985e18c9b6a5fceb959c48952409ca5");
      // //
      // "cmd pgsql2shp -f '/Users/ashamakhy/Documents/claudia/perth4' -h db2.aurin.org.au -u whatif  whatif-development wifdemo.wif_d985e18c9b6a5fceb959c48952409ca5");

      final String hostname = myjdbcDataStoreConfig.getHost();
      final String username = myjdbcDataStoreConfig.getUser();
      final String dbname = myjdbcDataStoreConfig.getDatabaseName();
      final String dbTable = myjdbcDataStoreConfig.getSchema() + "."
          + unifiedAreaZone;
      final String userpass = myjdbcDataStoreConfig.getPassword();

      final String uuid = UUID.randomUUID().toString();
      final File dir = Files.createTempDir();

      final File shpFile = new File(dir + "/" + uuid + ".shp");
      if (!shpFile.exists()) {
        shpFile.createNewFile();
        LOGGER.info(shpFile.getAbsolutePath() + " has been created");
      }

      final String shpfilename = dir + "/" + uuid + ".shp";
      final String pgsql2shpPath = "/usr/local/bin/pgsql2shp";
      String fcommand = "";
      if (userpass.length() == 0) {
        fcommand = pgsql2shpPath + " -f '" + shpfilename + "' -h " + hostname
            + " -u " + username + " " + dbname + " " + dbTable;
      } else {
        fcommand = pgsql2shpPath + " -f '" + shpfilename + "' -h " + hostname
            + " -u " + username + " -P " + userpass + " " + dbname + " "
            + dbTable;
      }
      LOGGER.info(Integer.toString(userpass.length()));
      LOGGER.info(fcommand);

      final String[] unixcopy = { "/bin/sh", "-c",
          // "/usr/local/bin/pgsql2shp -f '/Users/ashamakhy/Documents/claudia/perthtt' -h db2.aurin.org.au -u whatif  whatif-development wifdemo.wif_d80ef1c0e0841a1850ea154819d692cc"
          // };
          fcommand };

      // final Process p1 = Runtime
      // .getRuntime()
      // .exec(
      // "/usr/local/bin/pgsql2shp -f '/Users/ashamakhy/Documents/claudia/perth6' -h db2.aurin.org.au -u whatif  whatif-development wifdemo.wif_d985e18c9b6a5fceb959c48952409ca5");

      final Process p1 = Runtime.getRuntime().exec(unixcopy);

      p1.waitFor();

      final BufferedReader reader = new BufferedReader(new InputStreamReader(
          p1.getInputStream()));

      String line = "";
      while ((line = reader.readLine()) != null) {
        // output.append(line + "\n");

        LOGGER.info(line + "\n");
      }

      LOGGER.info("creating this zip file: " + uuid);
      final File zipFile = File.createTempFile(uuid, ".zip");
      ZipUtils.compress(dir.getAbsolutePath(), zipFile);
      final String dstr = dir.getAbsolutePath();

      LOGGER.info("this zip file created: " + uuid);
      final String files[] = dir.list();

      // since the zip file created, we dont need shapefile.
      LOGGER.info("Start directory deleting :" + dstr);
      for (final String temp1 : files) {
        // construct the file structure
        final File fileDelete = new File(dir, temp1);

        // delete
        fileDelete.delete();

      }

      dir.delete();
      LOGGER.info("directory deleted :" + dstr);
      LOGGER.info("download done");

      return zipFile;

    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;

    // final File zipFile = File.createTempFile("33dwfdfdwfdfd", ".zip");
    // return zipFile;

    // ////////

  }

  /**
   * Gets the zip file.
   * 
   * @param multipartFile
   *          the multipart file
   * @return the zip file
   * @throws ZipFileExtractionException
   *           the zip file extraction exception
   */
  public File getZipFile(final MultipartFile multipartFile)
      throws ZipFileExtractionException {
    File zipFile = null;
    final String msg = "Unzipping attached file operation failed...";

    try {
      final String filename = multipartFile.getOriginalFilename();

      final InputStream in = multipartFile.getInputStream();
      zipFile = readFile(in, filename);

      File outDir = null;

      outDir = Files.createTempDir();
      ZipUtils.extract(outDir.getAbsolutePath(), zipFile,
          System.getProperty("file.encoding"));
      final File shpFile = getShapeFile(outDir);
      return shpFile;
    } catch (final IOException e) {

      LOGGER.error(msg, e.getMessage());
      throw new ZipFileExtractionException(msg, e);
    }
  }

  /**
   * Zip file to post gis.
   * 
   * @param shpFile
   *          the shp file
   * @return the simple feature collection
   * @throws ShapeFile2PostGISCreationException
   *           the shape file2 post gis creation exception
   */
  public SimpleFeatureCollection zipFileToPostGIS(final File shpFile)
      throws ShapeFile2PostGISCreationException {
    final String msg = "Converting shape file operation failed...";

    try {

      final FileDataStore store = FileDataStoreFinder.getDataStore(shpFile);
      final SimpleFeatureSource featureSource = store.getFeatureSource();
      final SimpleFeatureCollection features = featureSource.getFeatures();
      LOGGER.info("Shape filename = {}", featureSource.getSchema()
          .getTypeName());
      final CoordinateReferenceSystem crs = featureSource.getSchema()
          .getCoordinateReferenceSystem();
      validatorCRS.validateSimple(crs, msg);
      return features;
    } catch (final Exception e) {
      LOGGER.error(msg, e.getMessage());
      throw new ShapeFile2PostGISCreationException(msg, e);
    }
  }

  /**
   * Gets the shape file.
   * 
   * @param outDir
   *          the out dir
   * @return the shape file
   */
  public File getShapeFile(final File outDir) {
    File outFile = null;
    try {
      final File[] list = outDir.listFiles();
      if (list.length < 3) {
        LOGGER
            .error("err: zip contains less than 3 files..[.shx,.shp,.dbf must be present]");
        return null;
      }

      final String names[] = new String[list.length];
      for (int i = 0; i < list.length; i++) {
        names[i] = list[i].getName();
        if (names[i].endsWith(".shp")) {
          outFile = list[i];
          break;
        }

      }
    } catch (final Exception ex) {
      LOGGER.error("", ex);
    }
    return outFile;
  }

  /**
   * Read file.
   * 
   * @param in
   *          the in
   * @param filename
   *          the filename
   * @return the file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private File readFile(final InputStream in, final String filename)
      throws IOException {
    final StringBuffer buf = new StringBuffer();
    buf.append(UUID.randomUUID().toString());
    buf.append("-");
    buf.append(filename);
    final String tmpFilename = buf.toString();

    LOGGER.info("filename: " + tmpFilename);

    File file = null;

    if (filename.endsWith(".zip")) {
      file = File.createTempFile(StringUtils.substringBefore(tmpFilename, "."),
          ".zip");
    } else {
      LOGGER.error("Not acceptable format: " + filename);

      // TODO Throw exception
    }
    LOGGER.info("Temporal file path: " + file.getAbsolutePath());

    final FileOutputStream fos = new FileOutputStream(file, true);

    IOUtils.copyAndCloseInput(in, fos);
    fos.close();

    final long fileSize = file.length();
    final long mega = fileSize / KB / KB;

    LOGGER.info("[" + mega + "MB] data saved in " + file.getAbsolutePath());

    return file;
  }

}
