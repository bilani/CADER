package fr.samover.acme.cadermfs.cadermfs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.QueryFactory;

/**
 * @author Mickael BARON
 */
public class AbstractCADERMfsSCoreTest {

    protected QueryFactory factory;
    
    class QueryExplain {

	protected int index;

	protected String description;
	
	protected Query query;

	protected List<Query> mfs;

	protected List<Query> xss;
	
	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public List<Query> getMfs() {
	    return mfs;
	}

	public List<Query> getXss() {
	    return xss;
	}

	public QueryExplain() {
	    this.mfs = new ArrayList<Query>();
	    this.xss = new ArrayList<Query>();
	}

	public Query getQuery() {
	    return query;
	}

	public void setQuery(Query pQuery) {
	    this.query = pQuery;
	}

	public void addMFS(Query mfs) {
	    this.mfs.add(mfs);
	}

	public void addXSS(Query xss) {
	    this.xss.add(xss);
	}

	public void setIndex(int pIndex) {
	    this.index = pIndex;
	}

	public int getIndex() {
	    return this.index;
	}
    }

    protected List<QueryExplain> newTestResultPairList(final String filename) throws IOException {
	final List<QueryExplain> queries = new ArrayList<QueryExplain>();
	final URL fileUrl = AbstractCADERMfsSCoreTest.class.getResource(filename);
	final FileReader file = new FileReader(fileUrl.getFile());
	BufferedReader in = null;
	try {
	    in = new BufferedReader(file);
	    StringBuffer test = null;
	    StringBuffer mfsresult = null;
	    StringBuffer xssresult = null;

	    final Pattern pTest = Pattern.compile("# Test (\\w+) \\((.*)\\)");
	    final Pattern pMFS = Pattern.compile("# MFS (\\w+)");
	    final Pattern pXSS = Pattern.compile("# XSS (\\w+)");

	    String line;
	    int lineNumber = 0;

	    String testNumber = null;
	    String testName = null;
	    StringBuffer curbuf = null;

	    while ((line = in.readLine()) != null) {
		lineNumber++;
		final Matcher mTest = pTest.matcher(line);
		final Matcher mMFS = pMFS.matcher(line);
		final Matcher mXSS = pXSS.matcher(line);
		if (mTest.matches()) { // # Test
		    addTestResultPair(queries, test, mfsresult, xssresult, testNumber, testName);
		    
		    testNumber = mTest.group(1);
		    testName = mTest.group(2);

		    test = new StringBuffer();
		    mfsresult = new StringBuffer();
		    xssresult = new StringBuffer();

		    curbuf = test;
		} else if (mMFS.matches()) { // # Result
		    if (testNumber == null) {
			throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
		    }
		    final String resultNumber = mMFS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException(
				"Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
		    }

		    curbuf = mfsresult;
		} else if (mXSS.matches()) {
		    if (testNumber == null) {
			throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
		    }
		    final String resultNumber = mXSS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException(
				"Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
		    }

		    curbuf = xssresult;
		} else {
		    line = line.trim();
		    if (!line.isEmpty()) {
			curbuf.append(line);
			curbuf.append("\n");			
		    }
		}
	    }

	    addTestResultPair(queries, test, mfsresult, xssresult, testNumber, testName);

	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (final IOException e) {
		}
	    }
	}
	
	return queries;
    }

    private void addTestResultPair(List<QueryExplain> queries, StringBuffer query, StringBuffer mfsResult,
	    StringBuffer xssResult, String number, String description) throws IOException {
	if (query == null || mfsResult == null || xssResult == null) {
	    return;
	}

	QueryExplain currentQuery = new QueryExplain();
	currentQuery.setQuery(this.factory.createQuery(query.toString().trim()));
	currentQuery.setIndex(Integer.valueOf(number));
	currentQuery.setDescription(description.trim());
	
	BufferedReader bufReader = new BufferedReader(new StringReader(mfsResult.toString()));
	String line = null;
	while ((line = bufReader.readLine()) != null) {
	    currentQuery.addMFS(this.factory.createQuery(line.trim()));
	}
	
	bufReader = new BufferedReader(new StringReader(xssResult.toString()));
	line = null;
	while ((line = bufReader.readLine()) != null) {
	    currentQuery.addXSS(this.factory.createQuery(line.trim()));
	}
	
	queries.add(currentQuery);
    }
}
