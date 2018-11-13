package fr.samover.acme.cadermfs.cadermfs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.samovar.acme.cadermfs.query.Query;

// a class to get the results of the experiments on the BFS, MBS, MBS-OPT and Full-MFS algorithms
// This class could probably be a superClass of ExpResult
public class ExpRelaxResult {

    private Logger logger = Logger.getLogger(ExpRelaxResult.class);

    private static int ID_TPS = 1;
    private static int ID_TPS_MFS = 2;
    private static int ID_NB_REQUETE = 3;
    private static int ID_NB_REQUETE_MFS = 4;
    private static int ID_NB_REQUETE_XSS = 5;
    private static int ID_TIME_MFS = 6;
    private static int ID_TIME_XSS = 7;

    protected int nbExecutionQuery;

    protected List<Query> listOfQueries;
    protected Map<Query, QueryResult[]> resultsByQuery;

    public ExpRelaxResult(int nbExecutionQuery) {
	super();
	logger.setLevel(Level.DEBUG);
	this.nbExecutionQuery = nbExecutionQuery;
	listOfQueries = new ArrayList<Query>();
	this.resultsByQuery = new HashMap<Query, QueryResult[]>();
    }

    public void addQueryResult(int i, Query q, float tpsRelax,float mfsTime, float xssTime, float tpsMFS, int nbRequeteRelax, int nbRequeteMFS, int nbReueteXSS) {
	QueryResult[] queryResults = resultsByQuery.get(q);
	if (queryResults == null) {
	    queryResults = new QueryResult[nbExecutionQuery];
	    listOfQueries.add(q);
	}
	queryResults[i] = new QueryRelaxResult(tpsRelax,mfsTime, xssTime, tpsMFS, nbRequeteRelax, nbRequeteMFS,nbReueteXSS);
	resultsByQuery.put(q, queryResults);
    }

    public float getMetriqueMoyenAux(Query q, int idMetrique) {
	float res = 0;
	QueryResult[] results = resultsByQuery.get(q);
	if (results != null) {
	    for (int j = 0; j < results.length; j++) {
		if (idMetrique == ID_TPS)
		    res += results[j].getTps();
		else if (idMetrique == ID_NB_REQUETE)
		    res += results[j].getNbRequete();
		else if (idMetrique == ID_TPS_MFS)
		    res += ((QueryRelaxResult)results[j]).getTpsMFS();
		else if (idMetrique == ID_NB_REQUETE_MFS)
		    res += ((QueryRelaxResult)results[j]).getNbRequeteMFS();
		else if (idMetrique == ID_NB_REQUETE_XSS)
           res += ((QueryRelaxResult)results[j]).getNbRequeteXSS();
		else if (idMetrique == ID_TIME_MFS)
	           res += ((QueryRelaxResult)results[j]).getMfsTime();
		else if (idMetrique == ID_TIME_XSS)
	           res += ((QueryRelaxResult)results[j]).getXssTime();
	    }
	}
	return res / nbExecutionQuery;
    }

    public float getTpsMoyen(Query q) {
	return getMetriqueMoyenAux(q, ID_TPS);
    }

    public float getMfsTime(Query q) {
	return getMetriqueMoyenAux(q, ID_TIME_MFS);
    }
    
    public float getXssTime(Query q) {
	return getMetriqueMoyenAux(q, ID_TIME_XSS);
    }
    
    public float getRequeteMoyen(Query q) {
	return getMetriqueMoyenAux(q, ID_NB_REQUETE);
    }
    
    public float getTpsMFSMoyen(Query q) {
	return getMetriqueMoyenAux(q, ID_TPS_MFS);
    }

    public float getRequeteMoyenMFS(Query q) {
	return getMetriqueMoyenAux(q, ID_NB_REQUETE_MFS);
    }
    
    public float getRequeteMoyenXSS(Query q) {
       return getMetriqueMoyenAux(q, ID_NB_REQUETE_XSS);
       }
    @Override
    public String toString() {
	StringBuffer res = new StringBuffer("");
	   for (int i = 0; i < listOfQueries.size(); i++) {
	        Query q = listOfQueries.get(i);
	        res.append("Q"+(i+1) + "\t");
	        Float valTemps = round(getTpsMoyen(q), 2);
	        Float mfsTime = round(getMfsTime(q), 2);
	        Float xssTime = round(getXssTime(q), 2);
	        res.append("valTemps " + valTemps.toString().replace('.', ',') + "\t");
	        Float valTempsMFS = round(getTpsMFSMoyen(q), 2);
	        res.append("valTempsMFS "+ valTempsMFS.toString().replace('.', ',') + "\t");
	        Float totalTemps = valTemps + valTempsMFS;
	        res.append("totalTemps " + totalTemps.toString().replace('.', ',') + "\t");
	        res.append("Time MFS " + mfsTime.toString().replace('.', ',') + "\t");
	        res.append("Time XSS " + xssTime.toString().replace('.', ',') + "\t");
	        int nbRequête = Math.round(getRequeteMoyen(q));
	        res.append("nbRequête: Relaxées " + nbRequête + "\t| ");
	        int nbRequêteMFS = Math.round(getRequeteMoyenMFS(q));
	        res.append("MFS "+nbRequêteMFS + "\t");
	        int nbRequêteXSS = Math.round(getRequeteMoyenXSS(q));
	        res.append("XSS "+nbRequêteXSS + "\t");
	        res.append(("total nb req " + (nbRequête + nbRequêteMFS)));
	        res.append("\n");
	    }
	    return res.toString();
	    }


    public void toFile(String descriExp) throws Exception {
	BufferedWriter fichier = new BufferedWriter(new FileWriter(
		descriExp));
	fichier.write(toString());
	fichier.close();
    }

    /**
     * Round to certain number of decimals
     */
    public static float round(float d, int decimalPlace) {
	BigDecimal bd = new BigDecimal(Float.toString(d));
	bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	return bd.floatValue();
    }

}
