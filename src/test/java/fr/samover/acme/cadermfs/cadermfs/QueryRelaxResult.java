package fr.samover.acme.cadermfs.cadermfs;

public class QueryRelaxResult extends QueryResult {

	private float tpsMFS;
	private int nbRequeteMFS;
	private int nbRequeteXSS;

	private float mfsTime;
	private float xssTime;
	
	public float getMfsTime() {
		return mfsTime;
	}

	public void setMfsTime(float mfsTime) {
		this.mfsTime = mfsTime;
	}

	public float getXssTime() {
		return xssTime;
	}

	public void setXssTime(float xssTime) {
		this.xssTime = xssTime;
	}

	public void setTpsMFS(float tpsMFS) {
		this.tpsMFS = tpsMFS;
	}

	public float getTpsMFS() {
		return tpsMFS;
	}

	public int getNbRequeteMFS() {
		return nbRequeteMFS;
	}

	public int getNbRequeteXSS() {
		return nbRequeteXSS;
	}

	public QueryRelaxResult(float tps, float tpsMFS, int nbRequete, int nbRequeteMFS) {
		super(tps, nbRequete);
		this.tpsMFS = tpsMFS;
		this.nbRequeteMFS = nbRequeteMFS;
	}

	public QueryRelaxResult(float tps, float tpsMFS, int nbRequete, int nbRequeteMFS, int nbRequeteXSS) {
		super(tps, nbRequete);
		this.tpsMFS = tpsMFS;
		this.nbRequeteMFS = nbRequeteMFS;
		this.nbRequeteXSS = nbRequeteXSS;
	}

	public QueryRelaxResult(float tps, float tpsMFS, float mfsTime, float xssTime, int nbRequete, int nbRequeteMFS,
			int nbRequeteXSS) {
		super(tps, nbRequete);
		this.tpsMFS = tpsMFS;
		this.nbRequeteMFS = nbRequeteMFS;
		this.nbRequeteXSS = nbRequeteXSS;
		this.mfsTime = mfsTime;
		this.xssTime = xssTime;
	}
}