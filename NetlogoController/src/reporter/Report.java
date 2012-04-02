/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package reporter;

public class Report {
	private Object value;
	private Object stamp;
	public Report(Object stamp, Object value){
		this.value = value;
		this.stamp = stamp;
	}
	
	public Object getValue(){
		return value;
	}
	
	public Object getStamp(){
		return stamp;
	}
}
