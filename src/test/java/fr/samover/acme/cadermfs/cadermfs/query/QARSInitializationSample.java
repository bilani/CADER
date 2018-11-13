package fr.samover.acme.cadermfs.cadermfs.query;

import fr.ensma.lias.qarscore.loader.JenaBulkLoader;

public class QARSInitializationSample {
	public static void main(String[] args) {
		String[] params = new String[5];
		params[0] = "/home/mo/Desktop/relaxation data"; // Data folder
		params[1] = "OWL";
		params[2] = "TDB";
		params[3] = "/home/mo/Desktop/relaxation data/data"; // TDB repository path
		params[4] = "true"; // Enable RDFS entailment
		JenaBulkLoader.main(params);
	}
}
