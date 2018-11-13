package fr.samovar.acme.cadermfs.cfg;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * @author Mickael BARON
 */
@Sources("classpath:triplestores.config")
public interface CADERMfsScore extends Config {

    @Key("jenatdb.repository")
    String jeantdbRepository();
    
    @Key("repository.statistics")
    String repositoryStatistics();
}

