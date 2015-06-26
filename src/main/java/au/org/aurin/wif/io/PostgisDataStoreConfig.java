package au.org.aurin.wif.io;

import static org.geotools.data.postgis.PostgisNGDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.DBTYPE;
import static org.geotools.jdbc.JDBCDataStoreFactory.FETCHSIZE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.SCHEMA;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;
import static org.geotools.jdbc.JDBCDataStoreFactory.VALIDATECONN;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store the Postgis data store configuration. This makes
 * it easy to iniatialise the Postgis configuration as a bean in spring and
 * provide the getDataStoreParams as an accessor method for the instatiation of
 * PostgisDataSource object.
 * 
 * @author Gerson Galang
 */
public class PostgisDataStoreConfig implements DataStoreConfig {
  private final Map<String, Object> dataStoreParams = new HashMap<String, Object>();

  public PostgisDataStoreConfig() {
    // set default values on the data store params
    // TODO put the values here in a Constants class
    dataStoreParams.put(PORT.key, "5432");
    dataStoreParams.put(DBTYPE.key, "postgis");
    dataStoreParams.put(VALIDATECONN.key, true);
    dataStoreParams.put(FETCHSIZE.key, 1000);
  }

  public void setHost(final String host) {
    dataStoreParams.put(HOST.key, host);
  }

  public void setDatabaseName(final String databaseName) {
    dataStoreParams.put(DATABASE.key, databaseName);
  }

  public void setPort(final String port) {
    dataStoreParams.put(PORT.key, port);
  }

  public void setUser(final String user) {
    dataStoreParams.put(USER.key, user);
  }

  public void setPassword(final String password) {
    dataStoreParams.put(PASSWD.key, password);
  }

  public void setDatabaseType(final String databaseType) {
    dataStoreParams.put(DBTYPE.key, databaseType);
  }

  public void setSchema(final String schema) {
    dataStoreParams.put(SCHEMA.key, schema);
  }

  public void setValidateConnection(final boolean validateConnection) {
    dataStoreParams.put(VALIDATECONN.key, Boolean.valueOf(validateConnection));
  }

  public Map<String, Object> getDataStoreParams() {
    return this.dataStoreParams;
  }
}
