/**
 * 
 */
package au.org.aurin.wif.model.reports.suitability;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.model.reports.ScenarioReport;

/**
 * The Class SuitabilityAnalysisReport.
 * 
 * @author Marcos Nino-Ruiz Document holder of suitability report information
 */
@Component
public class SuitabilityAnalysisReport extends ScenarioReport implements
    ApplicationContextAware {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2793636824302358781L;
  ApplicationContext context;

  /** The items. */
  private Set<SuitabilityAnalysisItem> items;

  /**
   * Gets the items.
   * 
   * @return the items
   */
  public Set<SuitabilityAnalysisItem> getItems() {
    return items;
  }

  /**
   * Sets the items.
   * 
   * @param items
   *          the items to set
   */
  public void setItems(Set<SuitabilityAnalysisItem> items) {
    this.items = items;
  }

  /**
   * 
   */
  public SuitabilityAnalysisReport() {
    super();
    items = new HashSet<SuitabilityAnalysisItem>();
  }

  public ApplicationContext getContext() {
    return context;
  }

  public void setApplicationContext(ApplicationContext context)
      throws BeansException {
    this.context = context;
  }

}
