package fr.samover.acme.cadermfs.cadermfs;


public class QueryResult {
	
	private float tps;
	
	private int nbRequete;

	public QueryResult(float tps, int nbRequete) {
		super();
		this.tps = tps;
		this.nbRequete = nbRequete;
	}

	public float getTps() {
		return tps;
	}

	public int getNbRequete() {
		return nbRequete;
	}
	
	

}
