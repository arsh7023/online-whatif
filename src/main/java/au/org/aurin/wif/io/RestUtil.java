package au.org.aurin.wif.io;

public final class RestUtil {

  private RestUtil() {
  }

  /**
   * Removes the trailing forward slash from the given URL.
   * 
   * @param url
   * @return
   */
  public static String removeTrailingSlash(final String url) {
    return url.replaceAll("\\/$", "");
  }

}
