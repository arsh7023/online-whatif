package au.org.aurin.wif.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ref:
 * http://www.aviransplace.com/2008/01/08/make-http-post-or-get-request-from
 * -java/
 */
public class HttpRequestPoster {
  /** The logger. */
  private static final Log LOGGER = LogFactory.getLog(HttpRequestPoster.class);

  /**
   * Sends an authenticated HTTP GET request to a url.
   * 
   * @param username
   *          - The username to authenticate
   * @param password
   *          - The password to authenticate
   * @param endpoint
   *          - The URL of the server. (Example: " http://www.yahoo.com/search")
   * @param requestParameters
   *          - all the request parameters (Example: "param1=val1&param2=val2").
   *          Note: This method will add the question mark (?) to the request -
   *          DO NOT add it yourself
   * @return - The response from the end point
   */
  public static String sendGetRequest(final String username,
      final String password, final String endpoint,
      final String requestParameters) throws DatasetAccessException {
    String result = null;
    if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
      // Send a GET request to the servlet
      try {
        // Construct data
        final StringBuffer data = new StringBuffer();
        // Send data
        String urlStr = endpoint;
        if (requestParameters != null && requestParameters.length() > 0) {
          urlStr += "?" + requestParameters;
        }
        LOGGER.debug("HTTP GET Request: " + urlStr);
        final URL url = new URL(urlStr);
        final URLConnection conn = url.openConnection();
        // let's set the timeout to 90seconds
        conn.setConnectTimeout(90000);
        conn.setReadTimeout(90000);
        if (username != null && password != null) {
          Authenticator.setDefault(new HttpAuthenticator(username, password));
        }
        // Get the response
        final BufferedReader rd = new BufferedReader(new InputStreamReader(
            conn.getInputStream()));
        final StringBuffer sb = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
          sb.append(line);
        }
        rd.close();
        // TODO check mememory footprint
        result = sb.toString();
      } catch (final Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new DatasetAccessException(
            "Exception was thrown while accessing the datasource",
            "We are unable to connect to the remote dataset. Please, try later or notify AURIN if the problem persists",
            e);
      }
    }
    return result;
  }

  /**
   * Sends an HTTP GET request to a url.
   * 
   * @param endpoint
   *          - The URL of the server. (Example: " http://www.yahoo.com/search")
   * @param requestParameters
   *          - all the request parameters (Example: "param1=val1&param2=val2").
   *          Note: This method will add the question mark (?) to the request -
   *          DO NOT add it yourself
   * @return - The response from the end point
   */
  public static String sendGetRequest(final String endpoint,
      final String requestParameters) throws DatasetAccessException {
    return sendGetRequest(null, null, endpoint, requestParameters);
  }

  /**
   * Reads data from the data reader and posts it to a server via POST request.
   * 
   * @param username
   *          - The username to authenticate
   * @param password
   *          - The password to authenticate
   * @param data
   *          - The data you want to send
   * @param endpoint
   *          - The server's address
   * @param output
   *          - writes the server's response to output.
   * 
   * @throws DatasetAccessException
   */
  public static void postData(final Reader data, final URL endpoint,
      final Writer output) throws DatasetAccessException {
    postData(null, null, data, endpoint, output);
  }

  /**
   * Reads data from the data reader and posts it to a server via POST request.
   * 
   * @param data
   *          - The data you want to send
   * @param endpoint
   *          - The server's address
   * @param output
   *          - writes the server's response to output.
   * 
   * @throws DatasetAccessException
   */
  public static void postData(final String username, final String password,
      final Reader data, final URL endpoint, final Writer output)
      throws DatasetAccessException {
    HttpURLConnection urlc = null;
    try {
      urlc = (HttpURLConnection) endpoint.openConnection();
      // TODO: verify that this bit of code works...
      if (username != null && password != null) {
        Authenticator.setDefault(new HttpAuthenticator(username, password));
      }
      try {
        urlc.setRequestMethod("POST");
      } catch (final ProtocolException e) {
        throw new DatasetAccessException(
            "Shouldn't happen: HttpURLConnection doesn't support POST??", e);
      }
      urlc.setDoOutput(true);
      urlc.setDoInput(true);
      urlc.setUseCaches(false);
      urlc.setAllowUserInteraction(false);
      urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
      final OutputStream out = urlc.getOutputStream();
      try {
        final Writer writer = new OutputStreamWriter(out, "UTF-8");
        pipe(data, writer);
        writer.close();
      } catch (final IOException e) {
        throw new DatasetAccessException("IOException while posting data", e);
      } finally {
        if (out != null) {
          out.close();
        }
      }
      final InputStream in = urlc.getInputStream();
      try {
        final Reader reader = new InputStreamReader(in);
        pipe(reader, output);
        reader.close();
      } catch (final IOException e) {
        throw new DatasetAccessException("IOException while reading response",
            e);
      } finally {
        if (in != null) {
          in.close();
        }
      }
    } catch (final IOException e) {
      throw new DatasetAccessException(
          "Connection error (is server running at " + endpoint + " ?): " + e);
    } finally {
      if (urlc != null) {
        urlc.disconnect();
      }
    }
  }

  /**
   * Pipes everything from the reader to the writer via a buffer
   */
  private static void pipe(final Reader reader, final Writer writer)
      throws IOException {
    final char[] buf = new char[1024];
    int read = 0;
    while ((read = reader.read(buf)) >= 0) {
      writer.write(buf, 0, read);
    }
    writer.flush();
  }

  protected static class HttpAuthenticator extends Authenticator {
    private final String username, password;

    public HttpAuthenticator(final String user, final String pwd) {
      username = user;
      password = pwd;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(username, password.toCharArray());
    }
  }
}
