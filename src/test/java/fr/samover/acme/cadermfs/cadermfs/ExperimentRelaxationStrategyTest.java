package fr.samover.acme.cadermfs.cadermfs;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.samovar.acme.cadermfs.query.AbstractQuery;
import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.triplestore.jena.JenaQueryOptFactory;

/**
 * @author Stéphane JEAN
 */
public class ExperimentRelaxationStrategyTest extends AbstractCADERMfsSCoreTest {
    
    Logger log = Logger.getLogger(ExperimentRelaxationStrategyTest.class);

	private static final String FILE_QUERIES = "queries-eswc2016.test";
    private static final int NB_EXEC = 1;

    private static final int ALGO_BFS = 1;
    private static final int ALGO_MBS = 2;
    private static final int ALGO_OMBS = 3;
    private static final int ALGO_FMBS = 4;
    
    private static final String PLAT_JENA = "JENA";
    private static final String PLAT_VIRTUOSO = "VIRTUOSO";

    /*************************
     * Jena
     *************************/
    
    @Test
    public void testJenaTop50() throws Exception {
//	testJenaSimpleBFSTop50();
	testJenaSimpleMBSTop50();
//	testJenaSimpleOMBSTop50();
//	testJenaSimpleFMBSTop50();
    }
    
    public void testJenaSimpleBFSTop50() throws Exception {
	factory = new JenaQueryOptFactory();
	testSimpleAlgo(ALGO_BFS, 50, PLAT_JENA);
    }
    
    public void testJenaSimpleMBSTop50() throws Exception {
	factory = new JenaQueryOptFactory();
//  factory = new JenaQueryMBANQFactory();
	testSimpleAlgo(ALGO_MBS, 50, PLAT_JENA);
    }
    
    public void testJenaSimpleOMBSTop50() throws Exception {
	factory = new JenaQueryOptFactory();
	testSimpleAlgo(ALGO_OMBS, 50, PLAT_JENA);
    }
    
    public void testJenaSimpleFMBSTop50() throws Exception {
	factory = new JenaQueryOptFactory();
	testSimpleAlgo(ALGO_FMBS, 50, PLAT_JENA);
    }
    
    /*************************
     * Virtuoso
     *************************/
    
    
    /*************************
     * Aux Method
     *************************/

    private float executeAlgo(Query q, int typeAlgo, int k) throws Exception {
	long begin = 0;
	if (typeAlgo == ALGO_BFS) {
	    begin = System.currentTimeMillis();
	    q.relaxBFS(k);
	} else if (typeAlgo == ALGO_MBS) {
	    begin = System.currentTimeMillis();
	    q.relaxMBS(k);
	} else if (typeAlgo == ALGO_OMBS) {
	    begin = System.currentTimeMillis();
	    q.relaxOMBS(k);
	} else if (typeAlgo == ALGO_FMBS) {
	    begin = System.currentTimeMillis();
	    q.relaxFMBS(k);
	}
	long end = System.currentTimeMillis();
	float tps = ((float) (end - begin)) / 1000f;
	return tps;
    }

    private String getNameAlgo(int typeAlgo) {
	if (typeAlgo == ALGO_BFS)
	    return "BFS";
	else if (typeAlgo == ALGO_MBS)
	    return "MBS";
	else if (typeAlgo == ALGO_OMBS)
	    return "O-MBS";
	else if (typeAlgo == ALGO_FMBS)
	    return "F-MBS";
	else
	    return "UNKNOWN";
    }

    private void testSimpleAlgo(int typeAlgo, int topK, String platform) throws Exception {
	List<QueryExplain> newTestResultPairList = this
		.newTestResultPairList("/" + FILE_QUERIES);

	ExpRelaxResult results = new ExpRelaxResult(NB_EXEC);

	for (int i = 0; i < newTestResultPairList.size(); i++) {
	    QueryExplain qExplain = newTestResultPairList.get(i);
	    Query q = qExplain.getQuery();
	    ((AbstractQuery)q).setNbOfExecutedQueries(0);
	    String description = qExplain.getDescription();
	    System.out
		    .println("-----------------------------------------------------------");
	    System.out.println("Query (" + description + "): " + q);
	    System.out
		    .println("-----------------------------------------------------------");
//	    for (int k = 0; k <= NB_EXEC; k++) {
		q = factory.createQuery(q.toString());
		executeAlgo(q, typeAlgo, topK);
//		int nbRequeteRelax = ((AbstractQuery)q).getNbExecutedQueryForMFS();
		int nbRequeteRelax = ((AbstractQuery)q).getNbOfExecutedQueries();
		int nbRequeteMFS = ((AbstractQuery)q).getAllMFS(0).size();
		int nbRequeteXSS = ((AbstractQuery)q).getAllXSS(0).size();
		long mfsTime = (((AbstractQuery)q).getMfsEndTime() - ((AbstractQuery)q).getMfsStartTime());
		long xssTime = (((AbstractQuery)q).getXssEndTime() - ((AbstractQuery)q).getXssStartTime());
		long output = mfsTime + xssTime;
		float tps = output ;
		float tpsMFS = 0;
//		if (k > 0)
		    results.addQueryResult(0, q, tps, tpsMFS,mfsTime, xssTime, nbRequeteRelax, nbRequeteMFS, nbRequeteXSS);
		System.out.println(getNameAlgo(typeAlgo) + " - Time = " + tps + "," + tpsMFS
			+ " NbQueries : nb requete relaxée" + nbRequeteRelax + " | requete MFS: " + nbRequeteMFS);
//	    }
	}
	System.out.println("---------- BILAN ------------------");
	System.out.println(results.toString());
	System.out.println("------------------------------------");

	results.toFile("exp-" + platform + "-" + getNameAlgo(typeAlgo) + "-" + topK + ".csv");
    }
    

}
