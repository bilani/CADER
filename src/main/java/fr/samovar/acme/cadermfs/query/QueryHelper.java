package fr.samovar.acme.cadermfs.query;

import fr.samovar.acme.cadermfs.Result;
import fr.samovar.acme.cadermfs.Session;

/**
 * @author Stephane JEAN
 */
public interface QueryHelper {

    String toNativeQuery();

    boolean executeQuery(Session session) throws Exception;
    
    Result getResult(Session session) throws Exception;
}
