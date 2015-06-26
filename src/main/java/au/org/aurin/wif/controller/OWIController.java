package au.org.aurin.wif.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import au.org.aurin.wif.svc.OWIService;
import au.org.aurin.wif.svc.ProjectService;

/**
 * <b>OWIController.java</b> : The MVC Controller that acts on all requests
 * relating to what if API versions.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */

@Controller
@RequestMapping("/")
public class OWIController {
  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(OWIController.class);

  /** the project service. */
  @Resource
  private ProjectService projectService;

  /** the service. */
  @Resource
  private OWIService oWIService;

  /**
   * Gets the API version.
   * 
   * @return the version
   */
  @RequestMapping(method = RequestMethod.GET, value = "/version")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  Map<String, String> getVersion() {

    LOGGER.info("*******>> requesting OWI version ");
    return oWIService.getOWIVersion();
  }
}
