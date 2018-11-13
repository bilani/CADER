package fr.samovar.acme.cadermfs.triplestore.jena;

import java.util.List;

import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JenaQueryOptFactory extends JenaQueryFactory {

    @Override
    public Query createQuery(String rdfQuery) {
	return new JenaQueryOpt(this, rdfQuery);
    }
    
    @Override
    public Query createQuery(List<TriplePattern> tps) {
	return new JenaQueryOpt(this, tps);
    }

}
