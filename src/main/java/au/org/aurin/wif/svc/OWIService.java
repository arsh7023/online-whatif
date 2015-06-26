package au.org.aurin.wif.svc;

import java.util.Map;

/**
 * The Interface OWIService.
 */
public interface OWIService {

  /**
   * Gets the oWI version.
   * 
   * @return the oWI version
   */
  Map<String, String> getOWIVersion();
}
