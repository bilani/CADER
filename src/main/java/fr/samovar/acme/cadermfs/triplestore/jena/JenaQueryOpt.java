package fr.samovar.acme.cadermfs.triplestore.jena;

import java.util.List;

import fr.samovar.acme.cadermfs.Result;
import fr.samovar.acme.cadermfs.Session;
import fr.samovar.acme.cadermfs.query.AbstractQueryOpt;
import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.QueryFactory;
import fr.samovar.acme.cadermfs.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JenaQueryOpt extends AbstractQueryOpt {

    protected JenaQueryHelper jenaHelper;

    public JenaQueryOpt(QueryFactory factory, String query) {
	super(factory, query);
	this.jenaHelper = new JenaQueryHelper(this);
    }
    
    public JenaQueryOpt(QueryFactory factory, List<TriplePattern> tps) {
	super(factory, tps);
	this.jenaHelper = new JenaQueryHelper(this);
    }

    @Override
    public String toNativeQuery() {
	return jenaHelper.toNativeQuery();
    }

    @Override
    protected boolean executeQuery(Query q, Session session) throws Exception {
	return new JenaQueryHelper(q).executeQuery(session);
    }
    
    @Override
    public Result getResult(Session session) throws Exception {
	return jenaHelper.getResult(session);
    }

}
