package au.org.aurin.wif.io;

public class jdbcDataStoreConfig {

  private String host;
  private String databaseName;
  private String port;
  private String user;
  private String password;
  private String databaseType;
  private String validateConnection;
  private String schema;

  public jdbcDataStoreConfig() {
  }

  public String getHost() {
    return host;
  }

  public void setHost(final String host) {
    this.host = host;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(final String databaseName) {
    this.databaseName = databaseName;
  }

  public String getPort() {
    return port;
  }

  public void setPort(final String port) {
    this.port = port;
  }

  public String getUser() {
    return user;
  }

  public void setUser(final String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getDatabaseType() {
    return databaseType;
  }

  public void setDatabaseType(final String databaseType) {
    this.databaseType = databaseType;
  }

  public String getValidateConnection() {
    return validateConnection;
  }

  public void setValidateConnection(final String validateConnection) {
    this.validateConnection = validateConnection;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(final String schema) {
    this.schema = schema;
  }

}
