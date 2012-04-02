/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package clustering;

public enum Indexes {
	AGENT_TYPE_INDEX(0),
//	NUMBER_CLUSTERS_INDEX(1),
	CLUSTERS_INDEX(1);
	
	private int index;
	
	private Indexes(int index){
		this.index = index;
	}
	public int getIndex(){
		return index;
	}
}
