package au.org.aurin.wif.restclient;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;

/**
 * The Class CouchDBClient.
 */
@Component
public class CouchDBClient {

	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CouchDBClient.class);
//	private String uri = "http://db2.aurin.org.au:5984/whatif/";
	/** The uri. */
private String uri = "http://localhost:5984/whatif/";
	
	/**
	 * Retrieve info.
	 */
	public void retrieveInfo() {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("hotel", "42");
		vars.put("booking", "21");
		RestTemplate restTemplate = new RestTemplate();
		// String result = restTemplate.getForObject(uri, String.class, vars);
		String result = restTemplate.getForObject(uri, String.class);
		LOGGER.info("result: {}", result);

	}
	
	/**
	 * Send projection.
	 *
	 * @param projection the projection
	 */
	public void sendProjection(Projection projection) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(uri, projection,
				Projection.class);
	}
	
	/**
	 * Send project.
	 *
	 * @param project the project
	 */
	public void sendProject(WifProject project) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> couchResponse = new HashMap<String, Object>();
//		couchResponse = restTemplate.postForObject(uri + project.getId(), project,
//				Map.class);
		restTemplate.put(uri + "{id}", project, project.getId());
//		Collection<String> keys = couchResponse.keySet();
//		LOGGER.debug("Size {} ", keys.size());
//
//		for (String id : keys) {
//			LOGGER.debug("Looking for id {} ", id);
//			LOGGER.debug("Parsed {} ", couchResponse.get(id));
//		}

//		restTemplate.postForLocation(uri, project);
	}
	
	
}