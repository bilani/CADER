package fr.samovar.acme.cadermfs.triplestore.jena;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;

import fr.samovar.acme.cadermfs.Result;
import fr.samovar.acme.cadermfs.Session;
import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.SPARQLQueryHelper;

/**
 * @author Stephane JEAN
 */
public class JenaQueryHelper extends SPARQLQueryHelper {

    public JenaQueryHelper(Query q) {
	super(q);
    }

    @Override
    public boolean executeQuery(Session session) throws Exception {
	String sparqlQueryString = toNativeQuery();
	org.apache.jena.query.Query query = org.apache.jena.query.QueryFactory.create(sparqlQueryString);
	QueryExecution qexec = QueryExecutionFactory.create(query, ((JenaSession) session).getDataset());
	ResultSet results = qexec.execSelect();
	//System.out.println("increase --> " + q.toSimpleString(AbstractQuery.initialQuery) + " *** " + q.toNativeQuery());
	q.setNbExecutedQuery(q.getNbExecutedQuery() + 1);
	boolean res = !results.hasNext();
	qexec.close();
	return res;
    }
    
    @Override
    public Result getResult(Session s) throws Exception {
	QueryExecution qexec = QueryExecutionFactory.create(toNativeQuery(), ((JenaSession) s).getDataset());
	ResultSet results = qexec.execSelect();
	return new JenaResult(results);
    }
}
