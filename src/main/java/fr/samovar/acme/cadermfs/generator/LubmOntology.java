package fr.samovar.acme.cadermfs.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represent the LUBM Ontology Mostly hard-coded for the moment
 * 
 * @author Stéphane Jean
 *
 */

public class LubmOntology {

    // Constants for RDF prefixes
    public static String PREFIX_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static String PREFIX_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static String PREFIX_OWL = "http://www.w3.org/2002/07/owl#";
    public static String RDFS_SUBCLASSOF = PREFIX_RDFS+"subClassOf";
    public static String RDFS_SUBPROPERTYOF = PREFIX_RDFS+"subPropertyOf";
    public static String RDF_TYPE = PREFIX_RDF + "type";
    public static String PREFIX_UB = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#";
    private int nbInstances;
    private int nbTriples;

    // coded as a singleton
    private static LubmOntology instance;

    private Map<String, String> subClassOf;
    private Map<String, Integer> instances;

    private Map<String, String> subPropertyOf;
    private Map<String, Integer> triples;

    private int getNbInstances() {
	return nbInstances;
    }

    private int getNbTriples() {
	return nbTriples;
    }
    
    public void setNbInstances(int nbInstances) {
	this.nbInstances = nbInstances;
    }
    
    public void setNbTriples(int nbTriples) {
	this.nbTriples = nbTriples;
    }

    private LubmOntology() {
	subClassOf = new HashMap<String, String>();
	instances = new HashMap<String, Integer>();
	subPropertyOf = new HashMap<String, String>();
	triples = new HashMap<String, Integer>();
    }

    public void addSuperClass(String aClass, String superClass) {
	subClassOf.put(aClass, superClass);
    }
    
    public void addSuperProperty(String aProp, String superProp) {
	subPropertyOf.put(aProp, superProp);
    }
    
    public void addInstances(String aClass, int nbInstances) {
	instances.put(aClass, nbInstances);
    }

    public String getSuperClass(String aClass) {
	return subClassOf.get(aClass);
    }
    
    public void addTriples(String aProp, int nbTriples) {
	//System.out.println(aProp + " --> " +  nbTriples);
	triples.put(aProp, nbTriples);
    }

    // information content of the class
    public double getIcClass(String aClass) {
	double nbInstanceClass = getNbInstances(aClass);
	// we change slightly the number of instances of the class
	// so that the similarity of a superclass is stronger than replacing it with a variable
//	if (nbInstanceClass == 0) {
//	    nbInstanceClass = 0.5;
//	}
	double res = -Math.log10((double) nbInstanceClass / getNbInstances());
	return res;
    }

    private int getNbInstances(String aClass) {
	Integer res = instances.get(aClass);
	if (res == null) {
	    System.out.println("The class : " + aClass
		    + " does not have any stat");
	    return -1;
	}
	return res;
    }

    public final static LubmOntology getInstance() {
	if (instance == null) {
	    instance = new LubmOntology();
	}
	return instance;
    }

    public String getSuperProperty(String aProp) {
	return subPropertyOf.get(aProp);
    }

    // information content of the property
    public double getIcProperty(String aProp) {
	double res = -Math.log10((double) getNbTriples(aProp) / getNbTriples());
	if (res == 0)
	    res = 0.0001;
	return res;
    }

    private int getNbTriples(String aProp) {
	Integer res = triples.get(aProp);
	if (res == null) {
	    System.out.println("The property: " + aProp
		    + " does not have any stat");
	    return -1;
	}
	return res;
    }

}
