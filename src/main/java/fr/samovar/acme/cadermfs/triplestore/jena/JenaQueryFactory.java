package fr.samovar.acme.cadermfs.triplestore.jena;

import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

import fr.samovar.acme.cadermfs.Session;
import fr.samovar.acme.cadermfs.query.AbstractQueryFactory;
import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.TriplePattern;

/**
 * @author Stephane JEAN
 */
public class JenaQueryFactory extends AbstractQueryFactory {

    @Override
    public Query createQuery(String rdfQuery) {
	return new JenaQuery(this, rdfQuery);
    }

    @Override
    public Query createQuery(List<TriplePattern> tps) {
	return new JenaQuery(this, tps);
    }

    @Override
    public Session createSession() throws Exception {
	//System.out.println(this.getConfig().jeantdbRepository());
	Dataset dataset = TDBFactory
		.createDataset(this.getConfig().jeantdbRepository());
	return new JenaSession(dataset);
    }
}
