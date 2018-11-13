package fr.samovar.acme.cadermfs.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.samovar.acme.cadermfs.Result;
import fr.samovar.acme.cadermfs.Session;

/**
 * @author Stephane JEAN
 */
public interface Query
{

   /**
    * Execute this query
    * @param s the connection to the triplestore
    * @return the result
    * @throws Exception
    */
   Result getResult(Session s) throws Exception;

   /**
    * Return wether this query has an empty result or not
    * 
    * @return True is the result of this query is empty
    */
   boolean isFailing(Session session) throws Exception;

   /**
    * Return wether the inverse of query w.r.t the initial query has an empty result or not
    * 
    * @return True is the result of this query is empty
    */
   boolean inverseIsSucceeding(Session session) throws Exception;

   /**
    * Return an MFS of this query (must be failing)
    * 
    * @return an MFS
    * @throws Exception
    */
   Query findAnMFS(Session session) throws Exception;

   /**
    * Remove a random triple pattern from this query
    */
   TriplePattern removeTriplePattern();

   /**
    * Remove the input triple pattern from this query
    * 
    * @param the
    *            triple pattern to remove
    */
   void removeTriplePattern(TriplePattern t);

   /**
    * Return a new query that is the concatenation of this query with the input
    * query
    * 
    * @param queryToConcat
    *            query that must be concatened
    * @return the concatened query
    */
   Query concat(Query queryToConcat);

   /**
    * Add a triple pattern to this query
    * 
    * @param tp
    *            the added triple pattern
    */
   void addTriplePattern(TriplePattern tp);

   /**
    * Compute the larger subqueries that do not include the input MFS
    * 
    * @param mfs
    *            an MFS of the query
    */
   List<Query> computePotentialXSS(Query mfs);

   /**
    * Retrun the triple patterns of the query
    * 
    * @return the triple patterns of the query
    */
   List<TriplePattern> getTriplePatterns();

   /**
    * Compute the set of MFS of this query with LBA
    * 
    * @return the set of MFS of this query
    */
   List<Query> getAllMFSWithLBA() throws Exception;

   public void setALLXSSPerLevel(ArrayList<Query> list) throws Exception;

   public ArrayList<Query> getALLXSSPerLevel() throws Exception;

   public void setALLMFSPerLevel(ArrayList<Query> list) throws Exception;

   public ArrayList<Query> getALLMFSPerLevel() throws Exception;

   /**
    * Run the LBA algorithm. It fills the allMFS and allXSS variable
    * @param session
    * @throws Exception
    */
   public void runLBA(Session session) throws Exception;

   /**
    * Compute the set of XSS of this query with LBA
    * 
    * @return the set of XSS of this query
    */
   List<Query> getAllXSSWithLBA() throws Exception;

   /**
    * Compute the set of MFS of this query with DFS
    * 
    * @return the set of MFS of this query
    */
   List<Query> getAllMFSWithDFS() throws Exception;

   /**
    * Compute the set of XSS of this query with DFS
    * 
    * @return the set of XSS of this query
    */
   List<Query> getAllXSSWithDFS() throws Exception;

   /**
    * Test if the input query is included in this query
    * 
    * @param q
    *            the input query
    * @return True if the input query is included in this query
    */
   boolean includes(Query q);

   /**
    * Test if this query is included in one of the input queries
    * 
    * @param queries
    *            the input queries
    * @return true if this query is included in one of the input queries
    */
   boolean isIncludedInAQueryOf(List<Query> queries);

   /**
    * Test if this query includes one of the input queries
    * @param queries the input queries
    * @return true if this query includes one of the input queries
    */
   public boolean includesAQueryOf(List<Query> queries);

   /**
    * Return a string representing the query in the simple form: ti ^ ... ^ tj
    * w.r.t the initial query
    * 
    * @param initialQuery
    *            the initial failing query
    * @return a string representing the query in the simple form: ti ^ ... ^ tj
    *         w.r.t the initial query
    */
   String toSimpleString(Query initialQuery);

   /**
    * Gives the number of executed queries to compute the MFS/XSS
    * 
    * @return the number of executed queries to compute the MFS/XSS
    */
   int getNbExecutedQuery();

   /**
    * Set the number of executed queries
    * @param n the number of executed queries
    */
   void setNbExecutedQuery(int n);

   /**
    * Number of triple patterns of the query
    * 
    * @return the number of triple patterns of the query
    */
   int getNbTriplePatterns();

   /**
    * Get the query executed on the target platform
    * @return the query executed on the target platform
    */
   String toNativeQuery();

   /**
    * Get all the subqueries of this query
    * @return the subqueries of this query
    */
   List<Query> getSubQueries();

   /**
    * Get all the superqueries of this query
    * @return the superqueries of this query
    */
   List<Query> getSuperQueries();

   /**
    * Return true if this query is empty
    * @return true if this query is empty
    */
   boolean isEmpty();

   /**
    * Return the inverse of this query
    * @param q the query to which the returned query is the inverse
    * @return the inverse of this query
    */
   Query inverseOf(Query q);

   /**
    *  return the similarity with this query
    * @return the similarity with this query
    */
   double getSimilarity(Query q);

   /**
    * Relax this query with the BFS (top-k) algorithm
    * @param k the number of expected answers
    * @return the number of results 
    * @throws Exception
    */
   Set<String> relaxBFS(int k) throws Exception;

   /**
    * Relax this query with the MBS (top-k) algorithm
    * @param k the number of expected answers
    * @return the number of results 
    * @throws Exception
    */
   Set<String> relaxMBS(int k) throws Exception;

   /**
    * Relax this query with the O-MBS (top-k) algorithm
    * @param k the number of expected answers
    * @return the number of results 
    * @throws Exception
    */
   Set<String> relaxOMBS(int k) throws Exception;

   /**
    * Relax this query with the FMBS (top-k) algorithm
    * @param k the number of expected answers
    * @return the number of results 
    * @throws Exception
    */
   Set<String> relaxFMBS(int k) throws Exception;

   /**
    * Return true if this query is a relaxation of the input
    * @param q
    * @return true if this query is a relaxation of the input
    */
   public boolean isRelaxationOf(Query q);

   /**
    * Return the first query of the input list that this query relaxes
    * @param listQuery the input list of queries
    * @return the first query of the input list that this query relaxes
    */
   public Query firstRelaxationOf(List<Query> listQuery);

   TriplePattern removeTriplePattern(int i);
}
