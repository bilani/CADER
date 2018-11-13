package fr.samovar.acme.cadermfs.query;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.samovar.acme.cadermfs.Session;

public abstract class AbstractQueryOpt extends AbstractQuery {
	
	Logger log = Logger.getLogger(AbstractQueryOpt.class);

	public AbstractQueryOpt(QueryFactory factory, String query) {
		super(factory, query);
	}
	
	public AbstractQueryOpt(QueryFactory factory, List<TriplePattern> tps) {
	    super(factory,tps);
	}

	/**
	 * Encoding of this query 1011 => t1 ^ t3 ^ t4
	 */
	private BitSet queryAsBitSet;

	private Set<String> variables;

	protected static int nbRepetedQuery;

	public int getNbRepetedQuery() {
		return nbRepetedQuery;
	}

	protected static List<Query> cachedQueries;
	protected static List<Query> failingCachedQueries;

	public Set<String> getVariables() {
		if (variables == null)
			initVariables();
		return variables;
	}

	public void initVariables() {
		variables = new HashSet<String>();
		for (TriplePattern t : triplePatterns) {
			variables.addAll(t.getVariables());
		}
	}

	public BitSet getQueryAsBitSet() {
		if (queryAsBitSet == null)
			initQueryAsBitSet();
		return queryAsBitSet;
	}

	private void initQueryAsBitSet() {
		//log.info("query: " + this.toString());
		queryAsBitSet = new BitSet(nbTriplePatterns);
		for (TriplePattern t : triplePatterns) {
			//System.out.println("triple: " + t + "<--" + initialQuery);
			queryAsBitSet.set(initialQuery.getTriplePatterns().indexOf(t));
		}
	}

	@Override
	public boolean includes(Query q) {
		BitSet qBitSet = (BitSet) ((AbstractQueryOpt) q).getQueryAsBitSet()
				.clone();
		qBitSet.andNot(getQueryAsBitSet());
		return qBitSet.isEmpty();
	}

	public List<Query> getConnectedParts() {
		List<Query> res = new ArrayList<Query>(1);
		// we are sure that the query has at least one TP
		res.add(factory.createQuery("SELECT * WHERE { " + triplePatterns.get(0).toString() + " }"));
		for (int i = 1; i < triplePatterns.size(); i++) {
			int isAlreadyConnected = -1;
			TriplePattern t = triplePatterns.get(i);
			List<Query> resTemp = new ArrayList<Query>(res);
			for (int j = 0; j < resTemp.size(); j++) {
				Query q = resTemp.get(j);
				if (((AbstractQueryOpt) q).isConnectedWith(t)) {
					if (isAlreadyConnected == -1) {
						q.addTriplePattern(t);
						isAlreadyConnected = j;
					} else {
						// merge q with q' with is at the position
						// isAlradyConnected
						res.remove(j);
						res.set(isAlreadyConnected,
								q.concat(res.get(isAlreadyConnected)));
					}
				}
			}
			if (isAlreadyConnected == -1) {
				res.add(factory.createQuery("SELECT * WHERE { " + t.toString() + " }"));
			}
		}
		//log.info("*************res: " + res + "<--");
		return res;
	}

	public boolean isConnectedWith(TriplePattern t) {
		boolean res = false;
		Set<String> queryVariables = getVariables();
		for (String v : t.getVariables()) {
			if (queryVariables.contains(v))
				return true;
		}
		return res;
	}

	@Override
	public boolean isFailingAux(Session session) throws Exception {
	    //System.out.println("test");
		//log.debug(this.toSimpleString(initialQuery));
		List<Query> connectedParts = getConnectedParts();
		boolean isCartesianProduct = (connectedParts.size() > 1);
		boolean res = false;
		for (Query q : connectedParts) {
			//log.debug(q.toSimpleString(initialQuery));
			boolean isSuccessFullByCache = false;
			for (Query qCache : cachedQueries) {
				if (qCache.includes(q)) {
				  //  System.out.println(qCache.toSimpleString(initialQuery));
				    //System.out.println(qCache);
				    //System.out.println("cache success");
					nbRepetedQuery++;
					//log.debug("cache [success]: " + q.toSimpleString(initialQuery));
					if (!isCartesianProduct) {
						return false;
					} else {
						isSuccessFullByCache = true;
						break;
					}
				}
			}
			for (Query qCache : failingCachedQueries) {
				if (q.includes(qCache)) {
					//log.debug("cache [failure]: " + q.toSimpleString(initialQuery));
					nbRepetedQuery++;
					return true;
				}
			}
			if (!isSuccessFullByCache) {
				//log.debug("execution");
				//log.debug(q.toNativeQuery());
				res = executeQuery(q, session);
				//log.debug("fin execution");
				if (res) {
					if (isCartesianProduct) {
						failingCachedQueries.add(q);
					}
					return true;
				} else {
					cachedQueries.add(q);
				}
			}
		}
		return res;
	}

	abstract protected boolean executeQuery(Query q, Session session)
			throws Exception;

	protected void initLBA() {
		nbRepetedQuery = 0;
		cachedQueries = new ArrayList<Query>();
		//System.out.println("cache query empty");
		failingCachedQueries = new ArrayList<Query>();
	}

	public void addTriplePattern(TriplePattern tp) {
		super.addTriplePattern(tp);
		if (variables != null)
			variables.addAll(tp.getVariables());
	}
}
