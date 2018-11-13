package fr.samovar.acme.cadermfs.query;


public abstract class SPARQLQueryHelper implements QueryHelper {
	
	protected Query q;

	public SPARQLQueryHelper(Query q) {
		this.q = q;
	}

	@Override
	public String toNativeQuery() {
		return q.toString();
	}
	
}
