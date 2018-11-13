package fr.samovar.acme.cadermfs.query;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;

import fr.samovar.acme.cadermfs.QARSCoreLBAConstants;
import fr.samovar.acme.cadermfs.cfg.CADERMfsScore;
import fr.samovar.acme.cadermfs.generator.LubmOntology;

/**
 * @author Mickael BARON
 */
public abstract class AbstractQueryFactory implements QueryFactory {

    @Override
    public CADERMfsScore getConfig() {
	return ConfigFactory.create(CADERMfsScore.class);
    }

    @Override
    public void getStatsOnLubm() throws Exception{
	String filename = this.getConfig().repositoryStatistics();

	Properties prop = new Properties();
	InputStream input = null;

	try {
	    input = this.getClass().getClassLoader().getResourceAsStream(filename);

	    // load a properties file
	    prop.load(input);

	    for(Object current : prop.keySet()) {
		String currentKey = (String)current;
		
		if (currentKey.startsWith(QARSCoreLBAConstants.SUB_CLASS_OF)) {
		    String key = currentKey.substring(QARSCoreLBAConstants.SUB_CLASS_OF.length() + 1, currentKey.length());
		    LubmOntology.getInstance().addSuperClass(LubmOntology.PREFIX_UB + key, LubmOntology.PREFIX_UB + prop.getProperty(currentKey));
		}
		
		if (currentKey.startsWith(QARSCoreLBAConstants.INSTANCES)) {
		    String key = currentKey.substring(QARSCoreLBAConstants.INSTANCES.length() + 1, currentKey.length());
		    LubmOntology.getInstance().addInstances(LubmOntology.PREFIX_UB + key, Integer.valueOf(prop.getProperty(currentKey)));
		}
		
		if (currentKey.startsWith(QARSCoreLBAConstants.TOTAL_INSTANCES)) {
		    LubmOntology.getInstance().setNbInstances(Integer.valueOf(prop.getProperty(currentKey)));
		}
		
		if (currentKey.startsWith(QARSCoreLBAConstants.SUB_PROPERTY_OF)) {
		    String key = currentKey.substring(QARSCoreLBAConstants.SUB_PROPERTY_OF.length() + 1, currentKey.length());	
		    LubmOntology.getInstance().addSuperProperty(LubmOntology.PREFIX_UB + key, LubmOntology.PREFIX_UB + prop.getProperty(currentKey));
		}
		
		if (currentKey.startsWith(QARSCoreLBAConstants.TRIPLES_BY_PROP)) {
		    String key = currentKey.substring(QARSCoreLBAConstants.TRIPLES_BY_PROP.length() + 1, currentKey.length());
		    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + key, Integer.valueOf(prop.getProperty(currentKey)));
		}
		
		if (currentKey.startsWith(QARSCoreLBAConstants.TOTAL_NUMBER_TRIPLES)) {
		    LubmOntology.getInstance().setNbTriples(Integer.valueOf(prop.getProperty(currentKey)));
		}
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	} finally {
	    if (input != null) {
		try {
		    input.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

   }

}
