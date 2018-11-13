package fr.samovar.acme.cadermfs.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.samovar.acme.cadermfs.generator.LubmOntology;

/**
 * @author Stephane JEAN
 */
public class TriplePattern {

    private Logger log = Logger.getLogger(TriplePattern.class);

    private String triplePattern;
    private String subject;
    private String predicate;
    private String object;
    private Set<String> variables;
    // indice of this triple pattern in a query, 1 by default
    private int indiceInQuery;

    // List of relaxed triple patterns of this triple pattern
    private List<TriplePattern> relaxedTriplePatterns;
    // Similarity of each relaxedTriplePattern with this triplePattern
    private Map<TriplePattern, Double> simRelaxedTriplePatterns;
    // Next relaxed triple pattern (if t(1) return t(2))
    private TriplePattern nextRelaxedTriplePattern;

    public TriplePattern(String triplePattern, int indiceInQuery) {
	this.indiceInQuery = indiceInQuery;
	this.triplePattern = triplePattern;
	decompose();
    }

    public void decompose() {
	int indexOfFirstSpace = triplePattern.indexOf(' ');
	subject = triplePattern.substring(0, indexOfFirstSpace);
	int indexOfSecondSpace = triplePattern.indexOf(' ',
		indexOfFirstSpace + 1);
	predicate = triplePattern.substring(indexOfFirstSpace + 1,
		indexOfSecondSpace);
	object = triplePattern.substring(indexOfSecondSpace + 1,
		triplePattern.length());
	// log.info("--> " + object + "<--");
	removeSyntax();
    }

    private void removeSyntax() {
	subject = removeSyntax(subject);
	// log.info("pred avant : " + predicate);
	predicate = removeSyntax(predicate);
	// log.info("pred : " + predicate);
	object = removeSyntax(object);
    }

    private String removeSyntax(String input) {
	String res = input;
	if (!isVariable(input)) {
	    res = input.substring(1, input.length() - 1);
	}
	// log.info(res);
	return res;
    }

    public Set<String> getVariables() {
	if (variables == null)
	    initVariables();
	return variables;
    }

    public void initVariables() {
	variables = new HashSet<String>();
	if (isSubjectVariable())
	    variables.add(getVariable(subject));
	if (isPredicateVariable())
	    variables.add(getVariable(predicate));
	if (isObjectVariable())
	    variables.add(getVariable(object));
    }

    private String getVariable(String s) {
	return s.substring(0);
    }

    private boolean isVariable(String s) {
	return s.startsWith("?");
    }

    public boolean isSubjectVariable() {
	return isVariable(subject);
    }

    public boolean isPredicateVariable() {
	return isVariable(predicate);
    }

    public boolean isObjectVariable() {
	return isVariable(object);
    }

    /**
     * Return a String representing the inpulist with the separtor between each
     * element
     * 
     * @param inputList
     *            the input list
     * @param separator
     *            the separator between each element
     * @return the output string
     */
    private String listWithSeparator(List<String> inputList, String separator) {
	String res = "";
	for (int i = 0; i < inputList.size(); i++) {
	    if (i > 0)
		res += separator;
	    res += inputList.get(i);
	}
	return res;
    }

    public String toSQL() {
	String res = "select ";
	List<String> valSelect = new ArrayList<String>();
	List<String> valWhere = new ArrayList<String>();
	if (!isSubjectVariable())
	    valWhere.add("s='" + subject + "'");
	else
	    valSelect.add("s as " + subject.substring(1));
	if (!isPredicateVariable())
	    valWhere.add("p='" + predicate + "'");
	else
	    valSelect.add("p as " + predicate.substring(1));
	if (!isObjectVariable())
	    valWhere.add("o='" + object + "'");
	else
	    valSelect.add("o as " + object.substring(1));
	if (valSelect.isEmpty())
	    res += "*";
	else
	    res += listWithSeparator(valSelect, ", ");
	res += " from t";
	if (!valWhere.isEmpty())
	    res += " where " + listWithSeparator(valWhere, " and ");
	return res;
    }

    // For a star query
    public String toSQL(String variable) {
	String res = "select distinct s from t where p='" + predicate + "'";
	if (!isObjectVariable())
	    res += " and o='" + object + "'";
	return res;
    }

    public String getSubject() {
	return subject;
    }

    public String getPredicate() {
	return predicate;
    }

    public String getObject() {
	return object;
    }

    @Override
    public String toString() {
	return triplePattern;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((triplePattern == null) ? 0 : triplePattern.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TriplePattern other = (TriplePattern) obj;
	if (triplePattern == null) {
	    if (other.triplePattern != null)
		return false;
	} else if (!triplePattern.equals(other.triplePattern))
	    return false;
	return true;
    }
    
    // return the number of relaxation of this triple pattern compared to the input
    // return -1 if this tp is not a relaxation of the input
    public int levelRelaxationOf(TriplePattern t) {
	List<TriplePattern> relaxTPofT = t.getRelaxedTriplePatterns();
	return relaxTPofT.indexOf(this);
    }

    public List<TriplePattern> getRelaxedTriplePatterns() {
	if (relaxedTriplePatterns == null) {
	    computeRelaxedTriplePatterns();
	}
	return relaxedTriplePatterns;
    }

    private void computeRelaxedTriplePatterns() {
	relaxedTriplePatterns = new ArrayList<TriplePattern>();
	simRelaxedTriplePatterns = new HashMap<TriplePattern, Double>();
	List<TriplePattern> t = new ArrayList<TriplePattern>();
	t.add(this); // we do not clone the initial triple pattern t = t(0)
	while (!t.isEmpty()) {
	    TriplePattern ti = t.remove(0);
	    addRelaxedTriplePattern(ti);
	    List<TriplePattern> tpObtainedByRules = ti.applyRules();
	    for (TriplePattern tj : tpObtainedByRules) {
		if (!t.contains(tj)) {
		    t.add(tj);
		}
	    }
	}
	Collections.sort(relaxedTriplePatterns, new TriplePatternComp());
	int nbRelaxedTP = relaxedTriplePatterns.size();
	for (int i = 0; i < nbRelaxedTP; i++) {
	    if (i < nbRelaxedTP - 1)
		relaxedTriplePatterns.get(i).nextRelaxedTriplePattern = relaxedTriplePatterns
			.get(i + 1);
	}
	//System.out.println(relaxedTriplePatterns);
    }

    class TriplePatternComp implements Comparator<TriplePattern> {
	@Override
	public int compare(TriplePattern t1, TriplePattern t2) {
	    Double simT1 = simRelaxedTriplePatterns.get(t1);
	    Double simT2 = simRelaxedTriplePatterns.get(t2);
	    if (simT1 <= simT2) {
		return 1;
	    } else {
		return -1;
	    }
	}

    }

    public Double getSimilarity(TriplePattern ti) {
	if (relaxedTriplePatterns == null) {
	    computeRelaxedTriplePatterns();
	}
	return simRelaxedTriplePatterns.get(ti);
    }

    private void addRelaxedTriplePattern(TriplePattern ti) {
	int nbRelaxedTriplePatterns = relaxedTriplePatterns.size();
	relaxedTriplePatterns.add(ti);
	simRelaxedTriplePatterns.put(ti, computeSimilarity(ti));
    }

    private Double computeSimilarity(TriplePattern ti) {
	return ((double) 1 / 3) * computeSimilarity(subject, ti.subject, 1)
		+ ((double) 1 / 3)
		* computeSimilarity(predicate, ti.predicate, 2)
		+ ((double) 1 / 3) * computeSimilarity(object, ti.object, 3);
	// we slighlty favor the predicate (more important than subject and
	// predicate)
	// return (0.33 * computeSimilarity(subject, ti.subject,1)
	// + 0.34 * computeSimilarity(predicate, ti.predicate,2)
	// + 0.33 * computeSimilarity(object, ti.object,3));
    }

    // pos=1 => subject, pos=2 => predicate, pos=3=>object
    private Double computeSimilarity(String s1, String s2, int pos) {
	if (s1.equals(s2))
	    return 1.0;
	if (!isVariable(s1) && isVariable(s2)) // constant to var
	    return 0.0;
	if (!isVariable(s1) && !isVariable(s2)) {
	    if (pos == 3) { // class superclass
		return (Double) LubmOntology.getInstance().getIcClass(s2)
			/ LubmOntology.getInstance().getIcClass(s1);
	    }
	    if (pos == 2) { // prop superprop
		return (Double) LubmOntology.getInstance().getIcProperty(s2)
			/ LubmOntology.getInstance().getIcProperty(s1);
	    }
	}
	return -1.0; // this is an error
    }

    private List<TriplePattern> applyRules() {
	List<TriplePattern> res = new ArrayList<TriplePattern>();
	int indexOfFirstSpace = triplePattern.indexOf(' ');
	int indexOfSecondSpace = triplePattern.indexOf(' ',
		indexOfFirstSpace + 1);
	// application of rule R1
	if (predicate.equals(LubmOntology.RDF_TYPE) && !isObjectVariable()) {
	    String superClass = LubmOntology.getInstance()
		    .getSuperClass(object);
	    if (superClass != null) {
		res.add(new TriplePattern(triplePattern.substring(0,
			indexOfSecondSpace) + " <" + superClass + ">",
			indiceInQuery));
	    }
	}
	// application of rule R2
	if (!isPredicateVariable()) {
	    String superProp = LubmOntology.getInstance().getSuperProperty(
		    predicate);
	    if (superProp != null) {
		res.add(new TriplePattern(triplePattern.substring(0,
			indexOfFirstSpace)
			+ " <"
			+ superProp
			+ ">"
			+ triplePattern.substring(indexOfSecondSpace,
				triplePattern.length()), indiceInQuery));
	    }
	}
	// application of rule R3
	if (!isSubjectVariable()) {
	    res.add(new TriplePattern("?vg"
		    + indiceInQuery
		    + "1 "
		    + triplePattern.substring(indexOfFirstSpace + 1,
			    triplePattern.length()), indiceInQuery));
	}
	if (!isPredicateVariable()) {
	    res.add(new TriplePattern(triplePattern.substring(0,
		    indexOfFirstSpace)
		    + " ?vg"
		    + indiceInQuery
		    + "2 "
		    + triplePattern.substring(indexOfSecondSpace + 1,
			    triplePattern.length()), indiceInQuery));
	}
	if (!isObjectVariable()) {
	    res.add(new TriplePattern(triplePattern.substring(0,
		    indexOfSecondSpace) + " ?vg" + indiceInQuery + "3",
		    indiceInQuery));
	}
	return res;
    }

    public TriplePattern getNextRelaxedTriplePattern() {
	return nextRelaxedTriplePattern;
    }
}
