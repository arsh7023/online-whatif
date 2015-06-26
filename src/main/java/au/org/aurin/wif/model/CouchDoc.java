/*
 *
 */
package au.org.aurin.wif.model;

import org.ektorp.support.CouchDbDocument;

public class CouchDoc extends CouchDbDocument {

  /**
   * 
   */
  private static final long serialVersionUID = 861017002159134946L;
  protected final String docType;

  /**
   * Instantiates a new abstract document in couch DB.
   */
  public CouchDoc() {
    super();
    docType = getClass().getSimpleName();
  }

  /**
   * @return the docType
   */
  public String getDocType() {
    return docType;
  }
}
