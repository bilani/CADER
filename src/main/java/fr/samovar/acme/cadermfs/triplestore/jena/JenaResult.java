package fr.samovar.acme.cadermfs.triplestore.jena;


import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import fr.samovar.acme.cadermfs.Result;

public class JenaResult implements Result {
    
    private ResultSet rset;

    public JenaResult(ResultSet rset) {
	super();
	this.rset = rset;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean next() throws Exception {
	return rset.hasNext();
    }
    
    @Override
    public String getString(int col) throws Exception {
	QuerySolution sol = rset.nextSolution();
	return sol.get("X").toString();
    }
    
    public String[] getString(String[] cols) throws Exception {
	String[] res = new String[cols.length];
	QuerySolution sol = rset.nextSolution();
	for (int i = 0; i < cols.length; i++) {
	    res[i] = sol.get(cols[i]).toString();
	}
	return res;
	
    }
    
    @Override
    public int getNbRow() throws Exception {
	int res = 0;
	while (rset.hasNext()) {
	    res++;
	    rset.nextSolution();
	}
	return res;
    }
        
    @Override
    public List<String> getNbRow(int maxK) throws Exception {
	List<String> res = new ArrayList<String>();
	while (rset.hasNext() && res.size()<=maxK) {
	    res.add(rset.nextSolution().toString());
	}
	return res;
    }

}
