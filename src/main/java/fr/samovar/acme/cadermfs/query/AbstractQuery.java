package fr.samovar.acme.cadermfs.query;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.samovar.acme.cadermfs.Result;
import fr.samovar.acme.cadermfs.Session;

public abstract class AbstractQuery implements Query {

	private Logger log = Logger.getLogger(AbstractQuery.class);

	protected long mfsStartTime;
	protected long mfsEndTime;
	protected long xssStartTime;
	protected long xssEndTime;

	public static final int ALGO_LBA = 1;
	public static final int ALGO_DFS = 2;
	public static final int ALGO_ISHMAEL = 3;

	public static final int SEARCH_MFS = 1;
	public static final int SEARCH_XSS = 2;

	protected String rdfQuery;
	protected ArrayList<Query> MFSListByLevel = new ArrayList<Query>();
	protected ArrayList<Query> XSSListByLevel = new ArrayList<Query>();
	protected QueryFactory factory;
	protected int nbOfExecutedQueries;

	protected List<TriplePattern> triplePatterns;
	protected int nbTriplePatterns;
	protected int nbLevelTriplePatterns;

	protected List<Query> tempLevelMFS;
	protected List<Query> allMFS;
	protected List<Query> localFS = new ArrayList<Query>();
	protected List<Query> localXS = new ArrayList<Query>();
	protected List<Query> allXSS;
	protected Set<Query> localXSSList = new HashSet<Query>();
	protected Set<Query> localMFSList = new HashSet<Query>();
	protected Set<Query> localFSList = new HashSet<Query>();
	protected boolean loop = true;
	protected boolean addToMFS = false;

	// for experiments
	protected static int nbExecutedQuery;
	protected int nbExecutedQueryForMFS;
	protected int nbExecutedQueryForRelax;
	protected float timeForMFS;
	protected float timeForRelax;
	List<List<TriplePattern>> coXSSes = new ArrayList<>();
	public static Query initialQuery;
  
	
	
	public int getNbOfExecutedQueries() {
		return nbOfExecutedQueries;
	}

	public void setNbOfExecutedQueries(int nbOfExecutedQueries) {
		this.nbOfExecutedQueries = nbOfExecutedQueries;
	}

	public int getNbExecutedQuery() {
		return nbExecutedQuery;
	}

	public int getNbExecutedQueryForMFS() {
		return nbExecutedQueryForMFS;
	}

	public int getNbExecutedQueryForRelax() {
		return nbExecutedQueryForRelax;
	}

	public float getTimeForMFS() {
		return timeForMFS;
	}

	public float getTimeForRelax() {
		return timeForRelax;
	}

	public void setNbExecutedQuery(int nbExecutedQuery) {
		AbstractQuery.nbExecutedQuery = nbExecutedQuery;
	}

	public List<TriplePattern> getTriplePatterns() {
		return triplePatterns;
	}

	@Override
	public int getNbTriplePatterns() {
		return nbTriplePatterns;
	}

	public void setLevelNbTriplePatterns(int nbTriplePatterns) {
		this.nbLevelTriplePatterns = nbTriplePatterns;
	}

	public int getLevelNbTriplePatterns() {
		return nbLevelTriplePatterns;
	}

	@Override
	public void setALLXSSPerLevel(ArrayList<Query> list) {
		this.XSSListByLevel = list;
	}

	@Override
	public ArrayList<Query> getALLXSSPerLevel() {
		return this.XSSListByLevel;
	}

	@Override
	public void setALLMFSPerLevel(ArrayList<Query> list) {
		this.MFSListByLevel = list;
	}

	@Override
	public ArrayList<Query> getALLMFSPerLevel() {
		return this.MFSListByLevel;
	}

	public AbstractQuery(QueryFactory factory, String query) {
		this.factory = factory;
		this.rdfQuery = query;
		// System.out.println(rdfQuery);
		decomposeQuery();
		nbTriplePatterns = triplePatterns.size();

	}

	public AbstractQuery(QueryFactory factory, List<TriplePattern> tps) {
		this.factory = factory;
		this.rdfQuery = computeRDFQuery(tps);
		triplePatterns = tps;
		nbTriplePatterns = triplePatterns.size();
	}
	

	public long getMfsStartTime() {
		return mfsStartTime;
	}

	public void setMfsStartTime(long mfsStartTime) {
		this.mfsStartTime = mfsStartTime;
	}

	public long getMfsEndTime() {
		return mfsEndTime;
	}

	public void setMfsEndTime(long mfsEndTime) {
		this.mfsEndTime = mfsEndTime;
	}

	public long getXssStartTime() {
		return xssStartTime;
	}

	public void setXssStartTime(long xssStartTime) {
		this.xssStartTime = xssStartTime;
	}

	public long getXssEndTime() {
		return xssEndTime;
	}

	public void setXssEndTime(long xssEndTime) {
		this.xssEndTime = xssEndTime;
	}

	// decompose a SPARQL Query into a set of triple patterns
	protected void decomposeQuery() {
		triplePatterns = new ArrayList<TriplePattern>();
		if (!rdfQuery.equals("")) {
			int indiceOfTriplePattern = 1;
			int indexOfLeftEmbrace = rdfQuery.indexOf('{');
			int indexOfDot = rdfQuery.indexOf(" . ", indexOfLeftEmbrace);

			while (indexOfDot != -1) {
				triplePatterns.add(new TriplePattern(rdfQuery.substring(indexOfLeftEmbrace + 2, indexOfDot),
						indiceOfTriplePattern));
				indiceOfTriplePattern++;
				indexOfLeftEmbrace = indexOfDot + 1;
				indexOfDot = rdfQuery.indexOf(" . ", indexOfLeftEmbrace);
			}
			triplePatterns.add(new TriplePattern(rdfQuery.substring(indexOfLeftEmbrace + 2, rdfQuery.length() - 2),
					indiceOfTriplePattern));
		}
	}

	protected List<List<TriplePattern>> constructQuery(List<TriplePattern> listPatterns) {
		List<Query> listMFS = new ArrayList<Query>();
		listMFS.addAll(allMFS);
		List<List<TriplePattern>> asnwer = new ArrayList<List<TriplePattern>>();
		List<TriplePattern> initialTriples = initialQuery.getTriplePatterns();
		for (TriplePattern t : initialTriples) {
			if (!listPatterns.contains(t)) {
				List<TriplePattern> tempTriplePatterns = new ArrayList<TriplePattern>();
				tempTriplePatterns.addAll(listPatterns);
				tempTriplePatterns.add(t);
				boolean contains = false;
				for (Query q : listMFS) {
					List<TriplePattern> temp = q.getTriplePatterns();
					if (tempTriplePatterns.containsAll(temp)) {
						contains = true;

					}

				}
				if (!contains)
					asnwer.add(tempTriplePatterns);
			}
		}

		return asnwer;
	}

	protected List<TriplePattern> decomposeQuery(String query) {
		List<TriplePattern> tempTriplePatterns = new ArrayList<TriplePattern>();
		if (!query.equals("")) {
			int indiceOfTriplePattern = 1;
			int indexOfLeftEmbrace = query.indexOf('{');
			int indexOfDot = query.indexOf(" . ", indexOfLeftEmbrace);

			while (indexOfDot != -1) {
				tempTriplePatterns.add(
						new TriplePattern(query.substring(indexOfLeftEmbrace + 2, indexOfDot), indiceOfTriplePattern));
				indiceOfTriplePattern++;
				indexOfLeftEmbrace = indexOfDot + 1;
				indexOfDot = query.indexOf(" . ", indexOfLeftEmbrace);
			}
			tempTriplePatterns.add(new TriplePattern(query.substring(indexOfLeftEmbrace + 2, query.length() - 2),
					indiceOfTriplePattern));
		}
		return tempTriplePatterns;
	}

	@Override
	public String toString() {
		return rdfQuery;
	}

	@Override
	public boolean isEmpty() {
		return nbTriplePatterns == 0;
	}

	@Override
	public boolean isFailing(Session session) throws Exception {
		if (isEmpty())
			return false;
		return isFailingAux(session);
	}

	protected abstract boolean isFailingAux(Session session) throws Exception;

	@Override
	public Query findAnMFS(Session session) throws Exception {
		return findAnMEL(session, SEARCH_MFS);
	}

	// The same algorithm is applied to find an MFS or an XSS
	// only the test change
	public Query findAnMEL(Session session, int typeMEL) throws Exception {
		ArrayList<Query> MFSList = new ArrayList<Query>();
		ArrayList<Query> XSSList = new ArrayList<Query>();
		Query qPrim = factory.createQuery(rdfQuery);
		Query initialQuery = factory.createQuery(rdfQuery);
		Query qStar = factory.createQuery("");
		Query qTemp;
		TriplePattern tp;
		// looping over the number to triplets and removing one each time - query
		// decomposition
		for (int i = nbTriplePatterns - 1; i > -1; i--) {
			tp = qPrim.removeTriplePattern(i);
			// concatenation à Supprimer ==> Q' ^ Q*
			// qTemp = qPrim.concat(qStar);
			triplePatterns.remove(tp);
			qTemp = factory.createQuery(computeRDFQuery(triplePatterns));
			triplePatterns.add(tp);
			// test the query, if it returns an answer it is an XSS alse it is an failing
			// query
			nbOfExecutedQueries++;
			if (!((AbstractQuery) qTemp).testQuery(session, typeMEL)) {
				XSSList.add(qTemp);
				List<Set<TriplePattern>> solution = generateAllSubQueries(qTemp);

				for (Set<TriplePattern> tripletSet : solution) {
					List<TriplePattern> list = new ArrayList<>();
					list.addAll(tripletSet);
					Query q = factory.createQuery(computeRDFQuery(list));
					localXSSList.add(q);
				}
				qStar.addTriplePattern(tp);
			} else {
				MFSList.add(qTemp);
			}
			// set qPrim to the initial query
			qPrim = initialQuery;
		}
		qStar.setALLMFSPerLevel(MFSList);
		qStar.setALLXSSPerLevel(XSSList);
		return qStar;
	}

	public Query findAnMEL(Session session, int typeMEL, String rdfQuery) throws Exception {
		List<TriplePattern> initialTriples = initialQuery.getTriplePatterns();

		ArrayList<Query> FSList = new ArrayList<Query>();
		ArrayList<Query> XSSList = new ArrayList<Query>();
		Query qPrim = factory.createQuery(rdfQuery);
		Query initialQuery = factory.createQuery(rdfQuery);
		Query qStar = factory.createQuery("");
		Query qTemp;
		TriplePattern tp;
		List<TriplePattern> listTriplePatterns = decomposeQuery(rdfQuery);
		List<TriplePattern> tempTriplePatterns = new ArrayList<TriplePattern>();
		tempTriplePatterns.addAll(listTriplePatterns);
		List<Query> allFS = getAllFS();
		// check if the number of triples is 1, then this is the buttom level of the
		// query and we can perform any further decomposition
		if (listTriplePatterns.size() == 1) {

			qTemp = factory.createQuery(computeRDFQuery(listTriplePatterns));
			// a cash list of all the FS and XSS for not recalculate the same query more
			// then once
			// each new query is checked if it exists in the cashe list before testing it
			if (!allFS.contains(qTemp) && !localXSSList.contains(qTemp)) {
				// System.out.println(qTemp);
				nbOfExecutedQueries++;
				if (!((AbstractQuery) qTemp).testQuery(session, typeMEL)) {
					if (qTemp.getNbTriplePatterns() > 0) {

						XSSList.add(qTemp);
						List<TriplePattern> tempTriples = qTemp.getTriplePatterns();
						List<TriplePattern> coXSS = new ArrayList<>();
						for (TriplePattern initialQueryTriples : initialTriples)
							if (!tempTriples.contains(initialQueryTriples)) {
								coXSS.add(initialQueryTriples);
							}
						coXSSes.add(coXSS);
						localXSSList.add(qTemp);
						localXSSList.addAll(qTemp.getSubQueries());
					}
				}
				qPrim = initialQuery;
			}
		} else {
			for (int i = listTriplePatterns.size() - 1; i > -1; i--) {
				tp = qPrim.removeTriplePattern(i);

				tempTriplePatterns.remove(tp);
				qTemp = factory.createQuery(computeRDFQuery(tempTriplePatterns));
				tempTriplePatterns.add(tp);
				if (qTemp.getNbTriplePatterns() == 0)
					continue;
				if (!allFS.contains(qTemp) && !localXSSList.contains(qTemp)) {
					// System.out.println(qTemp);
					nbOfExecutedQueries++;
					if (!((AbstractQuery) qTemp).testQuery(session, typeMEL)) {
						if (qTemp.getNbTriplePatterns() > 0) {
							XSSList.add(qTemp);
							List<TriplePattern> tempTriples = qTemp.getTriplePatterns();
							List<TriplePattern> coXSS = new ArrayList<>();
							for (TriplePattern initialQueryTriples : initialTriples)
								if (!tempTriples.contains(initialQueryTriples)) {
									coXSS.add(initialQueryTriples);
								}
							coXSSes.add(coXSS);
							List<Set<TriplePattern>> solution = generateAllSubQueries(qTemp);

							for (Set<TriplePattern> tripletSet : solution) {
								List<TriplePattern> list = new ArrayList<>();
								list.addAll(tripletSet);
								Query q = factory.createQuery(computeRDFQuery(list));
								localXSSList.add(q);
							}
						}

					} else {
						FSList.add(qTemp);
					}
				}

				qPrim = initialQuery;
			}

		}

		qStar.setALLMFSPerLevel(FSList);
		qStar.setALLXSSPerLevel(XSSList);
		return qStar;

	}

	public Query findAllMFS(Session session, int typeMEL, String rdfQuery) throws Exception {
		List<TriplePattern> initialTriples = initialQuery.getTriplePatterns();

		ArrayList<Query> XSList = new ArrayList<Query>();
		ArrayList<Query> MFSList = new ArrayList<Query>();
		Query qPrim = factory.createQuery(rdfQuery);
		allXSS.add(qPrim);
		Query initialQuery = factory.createQuery(rdfQuery);
		Query qStar = factory.createQuery("");
		Query qTemp;
		TriplePattern tp;
		List<TriplePattern> listTriplePatterns = decomposeQuery(rdfQuery);
		List<TriplePattern> tempTriplePatterns = new ArrayList<TriplePattern>();
		tempTriplePatterns.addAll(listTriplePatterns);
		List<Query> allXS = getAllXS();

		if (listTriplePatterns.size() == initialTriples.size()) {
			for (TriplePattern triplet : listTriplePatterns) {
				List<TriplePattern> temp = new ArrayList<TriplePattern>();
				temp.add(triplet);
				qTemp = factory.createQuery(computeRDFQuery(temp));

				if (qTemp.getNbTriplePatterns() == 0)
					continue;
				if (!allXS.contains(qTemp)) {
					// System.out.println(qTemp);
					nbOfExecutedQueries++;
					if (((AbstractQuery) qTemp).testQuery(session, typeMEL)) {
						if (qTemp.getNbTriplePatterns() > 0) {
							MFSList.add(qTemp);
							allMFS.add(qTemp);
							System.out.println("MFS " + qTemp);
							List<TriplePattern> coXSS = qTemp.getTriplePatterns();
							coXSSes.add(coXSS);
							localMFSList.add(qTemp);
//							List<Set<TriplePattern>> solution = generateAllUpperQueries(qTemp);
//							for (Set<TriplePattern> tripletSet : solution) {
//								List<TriplePattern> list = new ArrayList<>();
//								list.addAll(tripletSet);
//								Query q = factory.createQuery(computeRDFQuery(list));
//								localMFSList.add(q);
//							}
						}

					} else {
						XSList.add(qTemp);
					}
				}
			}
		}

		else {
			List<List<TriplePattern>> constructedPatters = constructQuery(listTriplePatterns);
			for (List<TriplePattern> list : constructedPatters) {
				qTemp = factory.createQuery(computeRDFQuery(list));
				if (qTemp.getNbTriplePatterns() == 0)
					continue;
				if (!allXS.contains(qTemp)) {
					// System.out.println(qTemp);
					nbOfExecutedQueries++;
					if (((AbstractQuery) qTemp).testQuery(session, typeMEL)) {
						if (qTemp.getNbTriplePatterns() > 0) {
							MFSList.add(qTemp);
							allMFS.add(qTemp);
							System.out.println("MFS " + qTemp);
							List<TriplePattern> coXSS = qTemp.getTriplePatterns();
							coXSSes.add(coXSS);
							localMFSList.add(qTemp);
//							List<Set<TriplePattern>> solution = generateAllUpperQueries(qTemp);
//							for (Set<TriplePattern> tripletSet : solution) {
//								List<TriplePattern> tempMFSlist = new ArrayList<>();
//								tempMFSlist.addAll(tripletSet);
//								Query q = factory.createQuery(computeRDFQuery(list));
//								localMFSList.add(q);
//							}

						}

					} else {
						XSList.add(qTemp);
					}
				}
			}

		}
		allXS.addAll(XSList);
		qStar.setALLMFSPerLevel(MFSList);
		qStar.setALLXSSPerLevel(XSList);
		return qStar;

	}

	private boolean checkSuperMFS(Query qTemp) {
		boolean results = true;
		List<TriplePattern> listTriplePatterns = qTemp.getTriplePatterns();
		for (Query mfsQuery : localMFSList) {
			List<TriplePattern> MFSTistTriplePatterns = mfsQuery.getTriplePatterns();
			if (!Collections.disjoint(listTriplePatterns, MFSTistTriplePatterns)) {
				results = false;
			}
		}
		return results;
	}
	
	private List<Set<TriplePattern>> generateAllUpperQueries(Query qTemp) {

		List<Set<TriplePattern>> solution = new ArrayList<Set<TriplePattern>>();
		List<TriplePattern> listTriplePatterns = qTemp.getTriplePatterns();
		List<TriplePattern> coXSS = new ArrayList<>();
		for (TriplePattern initialQueryTriples : initialQuery.getTriplePatterns())
			if (!listTriplePatterns.contains(initialQueryTriples)) {
				coXSS.add(initialQueryTriples);
			}
		int n = coXSS.size();
		int N = (int) Math.pow(2d, Double.valueOf(n));
		for (int i = 1; i < N; i++) {
			Set<TriplePattern> tripletSet = new HashSet<>();
			tripletSet.addAll(listTriplePatterns);
			String code = Integer.toBinaryString(N | i).substring(1);
			for (int j = 0; j < n; j++) {
				if (code.charAt(j) == '1') {
					tripletSet.add(coXSS.get(j));
				}
			}
			solution.add(tripletSet);
		}

		return solution;
	}

	private List<Set<TriplePattern>> generateAllSubQueries(Query qTemp) {
		List<Set<TriplePattern>> solution = new ArrayList<Set<TriplePattern>>();
		List<TriplePattern> listTriplePatterns = qTemp.getTriplePatterns();
		int n = listTriplePatterns.size();
		int N = (int) Math.pow(2d, Double.valueOf(n));
		for (int i = 1; i < N; i++) {
			Set<TriplePattern> tripletSet = new HashSet<>();
			String code = Integer.toBinaryString(N | i).substring(1);
			for (int j = 0; j < n; j++) {
				if (code.charAt(j) == '1') {
					tripletSet.add(listTriplePatterns.get(j));
				}
			}
			solution.add(tripletSet);
		}

		return solution;
	}

	// the test is different if this is an XSS or an MFS
	private boolean testQuery(Session session, int typeMEL) throws Exception {
		if (typeMEL == SEARCH_MFS)
			return isFailing(session);
		else
			return inverseIsSucceeding(session);
	}

	public boolean inverseIsSucceeding(Session session) throws Exception {
		Query inverse = inverseOf(initialQuery);
		// System.out.println("inverse: " + inverse);
		return !inverse.isFailing(session);
	}

	@Override
	public Query inverseOf(Query q) {
		// we assume that the current query is a subset of q
		Query res = factory.createQuery(q.toString());
		for (TriplePattern tp : triplePatterns) {
			res.removeTriplePattern(tp);
		}
		return res;
	}

	@Override
	public TriplePattern removeTriplePattern() {
		// un-comment to test the non-determinism
		int numTriplePattern = 0;
		// if (nbTriplePatterns>1) {
		// Random r = new Random();
		// numTriplePattern = r.nextInt(nbTriplePatterns);
		// }
		TriplePattern res = triplePatterns.remove(numTriplePattern);
		updateQueryAfterRemoveTP();
		return res;
	}

	@Override
	public TriplePattern removeTriplePattern(int numTriplePattern) {
		// un-comment to test the non-determinism
		// int numTriplePattern = 0;
		// if (nbTriplePatterns>1) {
		// Random r = new Random();
		// numTriplePattern = r.nextInt(nbTriplePatterns);
		// }
		TriplePattern res = triplePatterns.remove(numTriplePattern);
		// updateQueryAfterRemoveTP();
		return res;
	}

	@Override
	public void removeTriplePattern(TriplePattern t) {
		triplePatterns.remove(t);
		updateQueryAfterRemoveTP();
	}

	protected void updateQueryAfterRemoveTP() {
		nbTriplePatterns--;
		rdfQuery = computeRDFQuery(triplePatterns);
	}

	private String computeRDFQuery(List<TriplePattern> listTP) {
		String res = "";
		int nbTPs = listTP.size();
		if (nbTPs > 0) {
			res = "SELECT * WHERE { ";
			for (int i = 0; i < nbTPs; i++) {
				if (i > 0)
					res += " . ";
				res += listTP.get(i).toString();
			}
			res += " }";
		}
		return res;
	}

	@Override
	public Query concat(Query queryToConcat) {

		List<TriplePattern> listTP = new ArrayList<TriplePattern>(this.triplePatterns);
		listTP.addAll(queryToConcat.getTriplePatterns());
		return factory.createQuery(computeRDFQuery(listTP));
	}

	@Override
	public void addTriplePattern(TriplePattern tp) {
		triplePatterns.add(tp);
		nbTriplePatterns++;
		if (rdfQuery.equals(""))
			rdfQuery = "SELECT * WHERE { " + tp.toString() + " }";
		else {
			rdfQuery = rdfQuery.substring(0, rdfQuery.length() - 1) + ". " + tp.toString() + " }";
		}
	}

	@Override
	public List<Query> computePotentialXSS(Query mfs) {
		List<Query> res = new ArrayList<Query>();
		if (nbTriplePatterns == 1)
			return res;
		for (TriplePattern t : mfs.getTriplePatterns()) {
			Query q = factory.createQuery(rdfQuery);
			q.removeTriplePattern(t);
			res.add(q);
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((triplePatterns == null) ? 0 : new HashSet<TriplePattern>(triplePatterns).hashCode());
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
		Query other = (Query) obj;
		if (other.getNbTriplePatterns() != this.getNbTriplePatterns()) // same
			// size
			return false;
		if (!this.includesSimple(other)) // and one is included in the other
			return false;
		return true;
	}

	public List<Query> getAllMFS(int algo) throws Exception {
		if (allMFS == null) {
			launchAlgo(algo);
		}
		return allMFS;
	}

	public List<Query> getAllFS() throws Exception {
		return localFS;
	}

	public List<Query> getAllXS() throws Exception {
		return localXS;
	}

	public List<Query> getAllXSS(int algo) throws Exception {
		if (allXSS == null) {
			launchAlgo(algo);
		}
		return allXSS;
	}

	private void launchAlgo(int algo) throws Exception {
		if (algo == ALGO_LBA)
			runLBA();
		else if (algo == ALGO_DFS)
			runDFS();
	}

	@Override
	public List<Query> getAllMFSWithLBA() throws Exception {
		return getAllMFS(ALGO_LBA);
	}

	@Override
	public List<Query> getAllXSSWithLBA() throws Exception {
		return getAllXSS(ALGO_LBA);
	}

	@Override
	public List<Query> getAllMFSWithDFS() throws Exception {
		return getAllMFS(ALGO_DFS);
	}

	@Override
	public List<Query> getAllXSSWithDFS() throws Exception {
		return getAllXSS(ALGO_DFS);
	}

	// choose a random element from the list
	protected Query element(List<Query> queries) {
		// Random r = new Random();
		// int numQuery = r.nextInt(queries.size());
		// return queries.get(numQuery);
		// // j'enlève le non-déterminisme
		return queries.get(0);
	}

	@Override
	public boolean includes(Query q) {
		for (TriplePattern tp : q.getTriplePatterns()) {
			if (!includes(tp))
				return false;
		}
		return true;
	}

	// the opt query uses bitmap so we define a method
	// to avoid using bitmap when needed
	private boolean includesSimple(Query q) {
		for (TriplePattern tp : q.getTriplePatterns()) {
			if (!includes(tp))
				return false;
		}
		return true;
	}

	private boolean includes(TriplePattern t) {
		if (rdfQuery.indexOf(t.toString()) == -1)
			return false;
		return true;
	}

	@Override
	public boolean isIncludedInAQueryOf(List<Query> queries) {
		for (Query q : queries) {
			if (q.includes(this))
				return true;
		}
		return false;
	}

	@Override
	public boolean includesAQueryOf(List<Query> queries) {
		for (Query q : queries) {
			if (this.includesSimple(q))
				return true;
		}
		return false;
	}

	protected void initLBA() {
	}

	protected void runLBA() throws Exception {
		Session session = factory.createSession();
		runLBA(session);
		session.close();
	}

	public void runLBA(Session session, List<Query> knownMFS) throws Exception {
		nbExecutedQuery = 0;
		initialQuery = this;
		initLBA();
		allMFS = new ArrayList<Query>();
		allXSS = new ArrayList<Query>();
		Set<Query> levelFS = new HashSet<Query>();
		tempLevelMFS = new ArrayList<Query>();
		levelFS.add(initialQuery);
		setMfsStartTime(System.currentTimeMillis());
		while (loop) {
			for (Query fs : levelFS) {
				Query MFSQuery = fs;
				Query qLevelStar = findAllMFS(session, SEARCH_MFS, MFSQuery.toNativeQuery());
				queryManipulation(qLevelStar);
			}
			// temLevelMFS size is 0 where the subquery can't be decomposed anymore
			if (tempLevelMFS.size() > 0) {
				levelFS.clear();
				levelFS.addAll(tempLevelMFS);
				tempLevelMFS.clear();
			} else {
				loop = false;
			}
		}
		setMfsEndTime(System.currentTimeMillis());
		System.out.println(coXSSes.size());
		setXssStartTime(System.currentTimeMillis());
		calculateXSS(coXSSes);
		setXssEndTime(System.currentTimeMillis());

		// for (j)
	}

	private void calculateXSS(List<List<TriplePattern>> coXSSes) {
		MinimalHittingSet setCoverMax2Elem = new MinimalHittingSet();
		Set<Set<TriplePattern>> solution = setCoverMax2Elem.minimumHittingSet(coXSSes);
		allXSS.clear();
		for (Set<TriplePattern> tripletSet : solution) {
			List<TriplePattern> initialTriples = new ArrayList<>();
			initialTriples.addAll(initialQuery.getTriplePatterns());
			initialTriples.removeAll(tripletSet);
			Query q = factory.createQuery(computeRDFQuery(initialTriples));
			System.out.println("XSS " + q);
			allXSS.add(q);
		}
		System.out.println("XSS size ---------------" + allXSS.size());
	}

	private void queryManipulation(Query qStar) throws Exception {
		// if the number of XSS in this level is bigger then 0
		// the failing queries will be considered as MFS and the success ones XSS
//		if (qStar.getALLMFSPerLevel().size() > 0) {
//			// allMFS.addAll(qStar.getALLMFSPerLevel());
//			allMFS.addAll(qStar.getALLMFSPerLevel());
//			// this boolean flag is used to prevent the code from looping over the next
//			// level
//			// loop = false;
//		}
		// in the case where the level does not contains an XSS all the failing queries
		// are added
		// to the level FS list
		tempLevelMFS.addAll(qStar.getALLXSSPerLevel());
		// and all the failing queries are added to the allFS list
		// localFS.addAll(qStar.getALLMFSPerLevel());
	}

	public void runLBA(Session session) throws Exception {
		runLBA(session, new ArrayList<Query>());
	}

	public String toSimpleString(List<Query> queries) {
		String res = "";
		for (Query q : queries) {
			res += "\t" + q.toSimpleString(this) + "\n";
		}
		return res;
	}

	// create a query with the triples patterns that have the input positions
	private Query createCorrespondingQuery(List<Integer> pos) {
		List<TriplePattern> tp = new ArrayList<TriplePattern>();
		List<TriplePattern> allTp = getTriplePatterns();
		for (int i = 0; i < pos.size(); i++) {
			tp.add(allTp.get(pos.get(i)));
		}
		return factory.createQuery(tp);
	}

	// get the position of the triple of this query in the input query
	private List<Integer> getIndexOfTriplePattern(Query initialQuery) {
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < triplePatterns.size(); i++) {
			TriplePattern temp = triplePatterns.get(i);
			res.add(initialQuery.getTriplePatterns().indexOf(temp));
		}
		return res;
	}

	@Override
	public String toSimpleString(Query initialQuery) {
		TriplePattern temp;
		String res = "";
		for (int i = 0; i < triplePatterns.size(); i++) {
			if (i > 0)
				res += " ^ ";
			temp = triplePatterns.get(i);
			if (initialQuery != null)
				res += "t" + (initialQuery.getTriplePatterns().indexOf(temp) + 1);
		}
		return res;
	}

	@Override
	public String toNativeQuery() {
		return rdfQuery;
	}

	private boolean isFailingForDFS(Map<Query, Boolean> executedQueries, Session s) throws Exception {
		if (this.equals(initialQuery)) {
			return true;
		}
		Boolean val = executedQueries.get(this);
		if (val == null) {
			val = isFailing(s);
			executedQueries.put(this, val);
		}
		return val;
	}

	public List<Query> getSubQueries() {
		List<Query> res = new ArrayList<Query>();
		for (TriplePattern tp : getTriplePatterns()) {
			Query qNew = factory.createQuery(toString());
			qNew.removeTriplePattern(tp);
			res.add(qNew);
		}
		return res;
	}

	public List<Query> getSuperQueries() {
		List<Query> res = new ArrayList<Query>();
		for (TriplePattern tp : initialQuery.getTriplePatterns()) {
			if (!includes(tp)) {
				Query qNew = factory.createQuery(toString());
				qNew.addTriplePattern(tp);
				res.add(qNew);
			}
		}
		return res;
	}

	public void runDFS() throws Exception {
		allMFS = new ArrayList<Query>();
		allXSS = new ArrayList<Query>();
		nbExecutedQuery = 0;
		initialQuery = this;
		Session session = factory.createSession();
		List<Query> listQuery = new ArrayList<Query>();
		Map<Query, Boolean> executedQueries = new HashMap<Query, Boolean>();
		Map<Query, Boolean> markedQueries = new HashMap<Query, Boolean>();
		listQuery.add(this);
		while (!listQuery.isEmpty()) {
			Query qTemp = listQuery.remove(0);
			// System.out.println("Traitement de "
			// + qTemp.toSimpleString(initialQuery));
			if (!markedQueries.containsKey(qTemp)) {
				markedQueries.put(qTemp, true);
				List<Query> subqueries = qTemp.getSubQueries();
				if (((AbstractQuery) qTemp).isFailingForDFS(executedQueries, session)) {
					// this is a potential MFS
					// System.out.println("potential mfs");
					boolean isMFS = true;
					for (Query subquery : subqueries) {
						if (((AbstractQuery) subquery).isFailingForDFS(executedQueries, session))
							isMFS = false;
					}
					if (isMFS)
						allMFS.add(qTemp);
				} else { // Potential XSS
					List<Query> superqueries = qTemp.getSuperQueries();
					boolean isXSS = true;
					for (Query superquery : superqueries) {
						if (!((AbstractQuery) superquery).isFailingForDFS(executedQueries, session))
							isXSS = false;
					}
					if (isXSS && !qTemp.isEmpty())
						allXSS.add(qTemp);
				}
				listQuery.addAll(0, subqueries);
			}

		}
	}

	// return the similarity with the initial query
	public double getSimilarity(Query q) {
		double res = 1.0;
		for (int i = 0; i < triplePatterns.size(); i++) {
			// System.out.println(q);
			res = res * q.getTriplePatterns().get(i).getSimilarity(triplePatterns.get(i));
		}
		return res;
	}

	class QueryComp implements Comparator<Query> {
		@Override
		public int compare(Query q1, Query q2) {
			// System.out.println(q1);
			// System.out.println(q2);
			if (q1.getSimilarity(initialQuery) <= q2.getSimilarity(initialQuery)) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	// We avoid to fetch the result has it can take a long take for over-relaxed
	// queries
	public Set<String> relaxBFS(int k) throws Exception {
		Session session = factory.createSession();
		long begin = System.currentTimeMillis();
		factory.getStatsOnLubm();
		initialQuery = this;
		nbExecutedQuery = 0;
		nbExecutedQueryForRelax = 0;
		nbExecutedQueryForMFS = 0;
		timeForMFS = (float) 0.0;
		Map<Query, Boolean> isMarked = new HashMap<Query, Boolean>();
		Set<String> res = new LinkedHashSet<String>();
		TreeSet<Query> rq = new TreeSet<Query>(new QueryComp());
		rq.add(this);
		while (!rq.isEmpty() && res.size() < k) {
			Query qPrim = rq.pollFirst();
			if (!qPrim.equals(this)) {
				System.out.println(qPrim.getSimilarity(initialQuery) + " --> " + qPrim);
				Result resultQuery = qPrim.getResult(session);
				// System.out.println("executé");
				nbExecutedQueryForRelax++;
				res.addAll(resultQuery.getNbRow(k));
			}
			List<TriplePattern> tpQprim = qPrim.getTriplePatterns();
			for (int i = 0; i < tpQprim.size(); i++) {
				TriplePattern tp = tpQprim.get(i);
				TriplePattern nextTP = tp.getNextRelaxedTriplePattern();
				if (nextTP != null) { // we can build a child with this TP
					List<TriplePattern> tpQc = new ArrayList<TriplePattern>(tpQprim);
					tpQc.set(i, nextTP);
					Query qC = factory.createQuery(tpQc);
					if (isMarked.get(qC) == null) {
						rq.add(qC);
						isMarked.put(qC, true);
					}
				}
			}
		}
		long end = System.currentTimeMillis();
		timeForRelax = ((float) (end - begin)) / 1000f;
		session.close();
		return res;
	}

	public Set<String> relaxMBS(int k) throws Exception {
		Session session = factory.createSession();
		initialQuery = this;
		factory.getStatsOnLubm();
		nbExecutedQuery = 0;
		nbExecutedQueryForRelax = 0;
		nbExecutedQueryForMFS = 0;
		long begin = System.currentTimeMillis();
		runLBA(session);
		long end = System.currentTimeMillis();
		timeForMFS = ((float) (end - begin)) / 1000f;
		// System.out.println("nb XSS " + allXSS.size());
		// for(Query query : allXSS)
		// {
		// if(query.getNbTriplePatterns() > 0) System.out.println("XSS: " + query);
		// }
		// System.out.println("nb MFS " + allMFS.size() + " comp time: " + timeForMFS);
		// for(Query query : allMFS)
		// {
		// System.out.println("MFS: " + query);
		// }
		System.out.println("Number of executed queries for LBA: " + nbExecutedQuery);
		nbExecutedQueryForMFS = nbExecutedQuery;
		begin = System.currentTimeMillis();
		Map<Query, Boolean> isMarked = new HashMap<Query, Boolean>();
		Map<Query, Boolean> isFailing = new HashMap<Query, Boolean>();
		Set<String> res = new LinkedHashSet<String>();
		TreeSet<Query> rq = new TreeSet<Query>(new QueryComp());
		rq.add(this);
		isFailing.put(this, true);
		end = System.currentTimeMillis();
		timeForRelax = ((float) (end - begin)) / 1000f;
		session.close();
		return res;
	}

	public Set<String> relaxOMBS(int k) throws Exception {
		Session session = factory.createSession();
		initialQuery = this;
		factory.getStatsOnLubm();
		nbExecutedQuery = 0;
		long begin = System.currentTimeMillis();
		runLBA(session);
		long end = System.currentTimeMillis();
		timeForMFS = ((float) (end - begin)) / 1000f;
		// System.out.println("nb MFS " + allMFS.size() + " comp time: "
		// + timeForMFS);
		Map<Query, List<Integer>> positionOfMFS = new HashMap<Query, List<Integer>>();
		for (Query mfs : allMFS) {
			// System.out.println("MFS: " + mfs);
			positionOfMFS.put(mfs, ((AbstractQuery) mfs).getIndexOfTriplePattern(this));
		}
		// System.out.println("Number of executed queries for LBA: "
		// + nbExecutedQuery + " nbMFS: " + allMFS.size());
		nbExecutedQueryForMFS += nbExecutedQuery;
		float timeForMFSInAlgo = (float) 0.0;
		begin = System.currentTimeMillis();
		List<Query> relaxedQueriesProceeded = new ArrayList<Query>();
		relaxedQueriesProceeded.add(this);
		Map<Query, List<Query>> dmfs = new HashMap<Query, List<Query>>();
		dmfs.put(this, allMFS);
		List<Query> failingQueries = new ArrayList<Query>();
		Map<Query, Boolean> isMarked = new HashMap<Query, Boolean>();
		Map<Query, Boolean> isFailing = new HashMap<Query, Boolean>();
		Set<String> res = new LinkedHashSet<String>();
		TreeSet<Query> rq = new TreeSet<Query>(new QueryComp());
		rq.add(this);
		isFailing.put(this, true);
		while (!rq.isEmpty() && res.size() < k) {
			Query qPrim = rq.pollFirst();
			if (isFailing.get(qPrim) == null) { // not failing
				if (!qPrim.includesAQueryOf(failingQueries)) {
					// ((AbstractQuery)qPrim).displayNicely(initialQuery);
					Result resultQuery = qPrim.getResult(session);
					nbExecutedQueryForRelax++;
					List<String> nbResult = resultQuery.getNbRow(k);
					// System.out.println(nbResult.size());
					if (nbResult.size() == 0) {
						List<Query> dmfsQPrim = new ArrayList<Query>();
						List<Query> currentDMFS = dmfs.get(qPrim.firstRelaxationOf(relaxedQueriesProceeded));
						for (Query mfs : currentDMFS) {
							Query newMFS = ((AbstractQuery) qPrim).createCorrespondingQuery(positionOfMFS.get(mfs));
							// System.out.println("--> "+newMFS);
							nbExecutedQueryForMFS++;
							long beginQuery = System.currentTimeMillis();
							if (!newMFS.getResult(session).next()) {
								dmfsQPrim.add(newMFS);
								failingQueries.add(newMFS);
								positionOfMFS.put(newMFS, ((AbstractQuery) newMFS).getIndexOfTriplePattern(qPrim));
							}
							relaxedQueriesProceeded.add(0, qPrim);
							dmfs.put(qPrim, dmfsQPrim);
							long endQuery = System.currentTimeMillis();
							timeForMFSInAlgo += ((float) (endQuery - beginQuery)) / 1000f;
						}
					}
					res.addAll(nbResult);
				}
			}
			List<TriplePattern> tpQprim = qPrim.getTriplePatterns();
			for (int i = 0; i < tpQprim.size(); i++) {
				TriplePattern tp = tpQprim.get(i);
				TriplePattern nextTP = tp.getNextRelaxedTriplePattern();
				if (nextTP != null) { // we can build a child with this TP
					List<TriplePattern> tpQc = new ArrayList<TriplePattern>(tpQprim);
					tpQc.set(i, nextTP);
					Query qC = factory.createQuery(tpQc);
					if (isMarked.get(qC) == null) {
						rq.add(qC);
						isMarked.put(qC, true);
						if (qC.includesAQueryOf(allMFS)) {
							isFailing.put(qC, true);
						}
					}
				}
			}
		}
		end = System.currentTimeMillis();
		timeForRelax = ((float) (end - begin)) / 1000f;
		timeForRelax -= timeForMFSInAlgo;
		timeForMFS += timeForMFSInAlgo;
		session.close();
		return res;
	}

	public Set<String> relaxFMBS(int k) throws Exception {
		Session session = factory.createSession();
		initialQuery = this;
		factory.getStatsOnLubm();
		nbExecutedQuery = 0;
		long begin = System.currentTimeMillis();
		runLBA(session);
		long end = System.currentTimeMillis();
		timeForMFS = ((float) (end - begin)) / 1000f;
		// System.out.println("nb MFS " + allMFS.size() + " comp time: "
		// + timeForMFS);
		Map<Query, List<Integer>> positionOfMFS = new HashMap<Query, List<Integer>>();
		for (Query mfs : allMFS) {
			// System.out.println("MFS: " + mfs);
			positionOfMFS.put(mfs, ((AbstractQuery) mfs).getIndexOfTriplePattern(this));
		}
		// System.out.println("Number of executed queries for LBA: "
		// + nbExecutedQuery + " nbMFS " + allMFS.size());
		nbExecutedQueryForMFS += nbExecutedQuery;
		float timeForMFSInAlgo = (float) 0.0;
		begin = System.currentTimeMillis();
		List<Query> relaxedQueriesProceeded = new ArrayList<Query>();
		relaxedQueriesProceeded.add(this);
		Map<Query, List<Query>> dmfs = new HashMap<Query, List<Query>>();
		dmfs.put(this, allMFS);
		List<Query> failingQueries = new ArrayList<Query>();
		Map<Query, Boolean> isMarked = new HashMap<Query, Boolean>();
		Map<Query, Boolean> isFailing = new HashMap<Query, Boolean>();
		Set<String> res = new LinkedHashSet<String>();
		TreeSet<Query> rq = new TreeSet<Query>(new QueryComp());
		rq.add(this);
		isFailing.put(this, true);
		while (!rq.isEmpty() && res.size() < k) {
			Query qPrim = rq.pollFirst();
			if (isFailing.get(qPrim) == null) { // not failing
				if (!qPrim.includesAQueryOf(failingQueries)) {
					// ((AbstractQuery)qPrim).displayNicely(initialQuery);
					Result resultQuery = qPrim.getResult(session);
					nbExecutedQueryForRelax++;
					List<String> nbResult = resultQuery.getNbRow(k);
					// System.out.println(nbResult.size());
					if (nbResult.size() == 0) {
						List<Query> dmfsQPrim = new ArrayList<Query>();
						List<Query> currentDMFS = dmfs.get(qPrim.firstRelaxationOf(relaxedQueriesProceeded));
						// System.out.println("first relaxation: " +
						// qPrim.firstRelaxationOf(relaxedQueriesProceeded));
						// System.out.println("current dmfs: " + currentDMFS);
						boolean allMFSFailing = true;
						long beginQuery = System.currentTimeMillis();
						for (Query mfs : currentDMFS) {
							Query newMFS = ((AbstractQuery) qPrim).createCorrespondingQuery(positionOfMFS.get(mfs));
							// System.out.println("--> "+newMFS);
							nbExecutedQueryForMFS++;
							if (!newMFS.getResult(session).next()) {
								// we dont use isFailing to avoid using the cache (problem with the bitset
								// optimization)
								dmfsQPrim.add(newMFS);
								failingQueries.add(newMFS);
								positionOfMFS.put(newMFS, ((AbstractQuery) newMFS).getIndexOfTriplePattern(qPrim));
							} else {
								allMFSFailing = false;
							}
						}
						if (!allMFSFailing) {
							// System.out.println("MFS FULL! ");
							nbExecutedQuery = 0;
							// System.out.println("dmfsqprim: " + dmfsQPrim);
							((AbstractQuery) qPrim).runLBA(session, dmfsQPrim);
							initialQuery = this;
							List<Query> mfsQPrim = ((AbstractQuery) qPrim).allMFS;
							nbExecutedQueryForMFS += nbExecutedQuery;
							// System.out.println("vs nb MFS: " + mfsQPrim);
							dmfs.put(qPrim, mfsQPrim);
							for (Query aMFSQPrim : mfsQPrim) {
								failingQueries.add(aMFSQPrim);
								positionOfMFS.put(aMFSQPrim,
										((AbstractQuery) aMFSQPrim).getIndexOfTriplePattern(qPrim));
							}
						} else {
							dmfs.put(qPrim, dmfsQPrim);
						}
						relaxedQueriesProceeded.add(0, qPrim);
						long endQuery = System.currentTimeMillis();
						timeForMFSInAlgo += ((float) (endQuery - beginQuery)) / 1000f;
					}
					res.addAll(nbResult);
				}
			}
			List<TriplePattern> tpQprim = qPrim.getTriplePatterns();
			for (int i = 0; i < tpQprim.size(); i++) {
				TriplePattern tp = tpQprim.get(i);
				TriplePattern nextTP = tp.getNextRelaxedTriplePattern();
				if (nextTP != null) { // we can build a child with this TP
					List<TriplePattern> tpQc = new ArrayList<TriplePattern>(tpQprim);
					tpQc.set(i, nextTP);
					Query qC = factory.createQuery(tpQc);
					if (isMarked.get(qC) == null) {
						rq.add(qC);
						isMarked.put(qC, true);
						if (qC.includesAQueryOf(allMFS)) {
							isFailing.put(qC, true);
						}
					}
				}
			}
		}
		end = System.currentTimeMillis();
		timeForRelax = ((float) (end - begin)) / 1000f;
		timeForRelax -= timeForMFSInAlgo;
		timeForMFS += timeForMFSInAlgo;
		session.close();
		return res;
	}

	// return true if this query is a relaxation of the input
	public boolean isRelaxationOf(Query q) {
		int res = 0;
		List<TriplePattern> tpQ = q.getTriplePatterns();
		for (int i = 0; i < triplePatterns.size(); i++) {
			TriplePattern aTPofQ = tpQ.get(i);
			int levelRelaxation = triplePatterns.get(i).levelRelaxationOf(aTPofQ);
			if (levelRelaxation == -1)
				return false;
			else
				res += levelRelaxation;
		}
		return res > 0;
	}

	// return the first occurence of a query that the current query relaxes
	public Query firstRelaxationOf(List<Query> listQuery) {
		for (int i = 0; i < listQuery.size(); i++) {
			Query currentQuery = listQuery.get(i);
			if (this.isRelaxationOf(currentQuery)) {
				return currentQuery;
			}
		}
		return null;
	}

	public void displayNicely(Query initialQuery) {
		String res = this.toString();
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#memberOf>", "memberOf");
		res = res.replaceAll("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "type");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#UndergraduateStudent>",
				"UndergraduateStudent");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#degreeFrom>", "degreeFrom");
		res = res.replaceAll("<http://www.University822.edu>", "Univ822");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#emailAddress>", "emailAddress");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#advisor>", "advisor");
		res = res.replaceAll("<http://www.Department0.University0.edu/FullProfessor0>", "FullProfessor0");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#takesCourse>", "takesCourse");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#Student>", "Student");
		res = res.replaceAll("<http://swat.cse.lehigh.edu/onto/univ-bench.owl#name>", "name");

		System.out.println(this.getSimilarity(initialQuery) + " --> " + res);
	}
}
