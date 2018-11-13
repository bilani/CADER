package fr.samovar.acme.cadermfs.triplestore.jena;

import org.apache.jena.query.Dataset;

import fr.samovar.acme.cadermfs.Session;

/**
 * @author Mickael BARON
 */
public class JenaSession implements Session {

    private Dataset dataset;
    
    public JenaSession(Dataset pDataset) {
	this.dataset = pDataset;
    }
    
    public Dataset getDataset() {
	return dataset;
    }
    
    @Override
    public void close() throws Exception {
	if (dataset != null) {
	    dataset.close();
	}
    }
}
