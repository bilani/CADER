package fr.samovar.acme.cadermfs.query;

import java.util.List;

import fr.samovar.acme.cadermfs.Session;
import fr.samovar.acme.cadermfs.cfg.CADERMfsScore;

/**
 * @author Stephane JEAN
 */
public interface QueryFactory {

    Query createQuery(String rdfQuery);
    
    void getStatsOnLubm() throws Exception;
    
    Query createQuery(List<TriplePattern> tp);

    Session createSession() throws Exception;

    CADERMfsScore getConfig();
}
