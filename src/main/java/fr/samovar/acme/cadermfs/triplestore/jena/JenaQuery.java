package fr.samovar.acme.cadermfs.triplestore.jena;

import java.util.List;

import fr.samovar.acme.cadermfs.Result;
import fr.samovar.acme.cadermfs.Session;
import fr.samovar.acme.cadermfs.query.AbstractQuery;
import fr.samovar.acme.cadermfs.query.QueryFactory;
import fr.samovar.acme.cadermfs.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JenaQuery extends AbstractQuery {

    private JenaQueryHelper helper;

    public JenaQuery(QueryFactory factory, String query) {
	super(factory, query);
	helper = new JenaQueryHelper(this);
    }
    
    public JenaQuery(QueryFactory factory, List<TriplePattern> tps) {
	super(factory, tps);
	helper = new JenaQueryHelper(this);
    }

    @Override
    public boolean isFailingAux(Session session) throws Exception {
	return helper.executeQuery(session);
    }
    
    @Override
    public Result getResult(Session session) throws Exception {
	return helper.getResult(session);
    }

    @Override
    public String toNativeQuery() {
	return helper.toNativeQuery();
    }
}
