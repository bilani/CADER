package fr.samovar.acme.cadermfs;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import fr.samovar.acme.cadermfs.generator.LubmOntology;
import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.QueryFactory;
import fr.samovar.acme.cadermfs.triplestore.jena.JenaQueryFactory;

/**
 * @author Mickael BARON
 */
public class GenerateStatistics {

    private enum TRIPLE_STORE {
	jena    }

    public GenerateStatistics(TRIPLE_STORE pTripleStore) throws Exception {
	QueryFactory currentQueryFactory = null;
	Session currentSession = null;

	switch (pTripleStore) {
	case jena: {
	    currentQueryFactory = new JenaQueryFactory();
	    currentSession = currentQueryFactory.createSession();
	    break;
	}
		default:
	    new RuntimeException();
	}

	Properties prop = new Properties();
	OutputStream output = null;
	output = new FileOutputStream("statisticslubm.properties");

	// Subclassof.
	Query q = currentQueryFactory.createQuery("SELECT ?X ?Y WHERE { ?X <"
		+ LubmOntology.RDFS_SUBCLASSOF + "> ?Y }");
	Result resultQuery = q.getResult(currentSession);
	String[] vars = { "X", "Y" };

	while (resultQuery.next()) {
	    String[] resultRow = resultQuery.getString(vars);
	    String aClass = resultRow[0];
	    String superClass = resultRow[1];
	    final String key = QARSCoreLBAConstants.SUB_CLASS_OF + "."
		    + aClass.replaceFirst(LubmOntology.PREFIX_UB, "");

	    prop.setProperty(key,
		    superClass.replaceFirst(LubmOntology.PREFIX_UB, ""));
	}

	// Instances.
	q = currentQueryFactory
		.createQuery("SELECT ?X WHERE { ?X <" + LubmOntology.RDF_TYPE
			+ "> <" + LubmOntology.PREFIX_OWL + "Class> }");
	resultQuery = q.getResult(currentSession);
	Set<String> totalInstances = new TreeSet<String>();
	while (resultQuery.next()) {
	    String aClass = resultQuery.getString(0);
	    Query qInstances = currentQueryFactory
		    .createQuery("SELECT ?X WHERE { ?X <"
			    + LubmOntology.RDF_TYPE + "> <" + aClass + "> }");
	    Result resultQInstances = qInstances.getResult(currentSession);
	    List<String> instances = new ArrayList<String>();
	    while (resultQInstances.next()) {
		instances.add(resultQInstances.getString(0));
	    }
	    int nbInstances = instances.size();
	    prop.setProperty(
		    QARSCoreLBAConstants.INSTANCES + "."
			    + aClass.replaceFirst(LubmOntology.PREFIX_UB, ""),
		    Integer.toString(nbInstances));
	    totalInstances.addAll(instances);
	}

	// Total Number of instances.
	int totalNbInstances = totalInstances.size();
	prop.setProperty(QARSCoreLBAConstants.TOTAL_INSTANCES,
		Integer.toString(totalNbInstances));

	// Subpropertyof.
	q = currentQueryFactory.createQuery("SELECT ?X ?Y WHERE { ?X <"
		+ LubmOntology.RDFS_SUBPROPERTYOF + "> ?Y }");
	resultQuery = q.getResult(currentSession);
	Set<String> properties = new TreeSet<String>();
	while (resultQuery.next()) {
	    String[] resultRow = resultQuery.getString(vars);
	    String aProp = resultRow[0];
	    String superProp = resultRow[1];
	    properties.add(aProp);
	    properties.add(superProp);
	    prop.setProperty(
		    QARSCoreLBAConstants.SUB_PROPERTY_OF + "."
			    + aProp.replaceFirst(LubmOntology.PREFIX_UB, ""),
		    superProp.replaceFirst(LubmOntology.PREFIX_UB, ""));
	}

	// Triples by prop.
	for (String aProp : properties) {
	    Query qTriples = currentQueryFactory
		    .createQuery("SELECT ?X WHERE { ?X <" + aProp + "> ?Y }");
	    Result resultQTriples = qTriples.getResult(currentSession);
	    int nbTriples = resultQTriples.getNbRow();
	    prop.setProperty(
		    QARSCoreLBAConstants.TRIPLES_BY_PROP + "."
			    + aProp.replaceFirst(LubmOntology.PREFIX_UB, ""),
		    Integer.toString(nbTriples));
	}

	// Total number of triples.
	Query qTriples = currentQueryFactory
		.createQuery("SELECT ?X WHERE { ?X ?Y ?Z }");
	Result resultQTriples = qTriples.getResult(currentSession);
	int nbTriples = resultQTriples.getNbRow();
	prop.setProperty(QARSCoreLBAConstants.TOTAL_NUMBER_TRIPLES,
		Integer.toString(nbTriples));

	prop.store(output, null);
	output.close();
    }

    public static void main(String[] args) throws Exception {
	if (args == null || args.length == 0) {
	    System.err.print(
		    "No triplestore identifier specified: jena, sesame, oracle or virtuoso");
	    return;
	}

	TRIPLE_STORE current = null;
	String tripleStore = args[0].toLowerCase();
	current = TRIPLE_STORE.valueOf(tripleStore);

	if (current == null) {
	    System.err.print(
		    "Bad value, authorized values are jena, sesame, oracle, virtuoso");
	    return;
	}

	new GenerateStatistics(current);
    }
}
