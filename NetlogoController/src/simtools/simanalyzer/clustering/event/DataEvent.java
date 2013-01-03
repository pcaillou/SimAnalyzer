/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package simtools.simanalyzer.clustering.event;


import org.ujmp.core.Matrix;

import simtools.simanalyzer.clustering.Indexes;
import simtools.simanalyzer.observer.event.ObserverEvent;


public class DataEvent extends ObserverEvent {
	private Matrix data;
	private Object[]arguments;
	private DataEvent(){};
	public DataEvent(Matrix data, Object...arguments){
		this();
		this.data = data;
		this.arguments = arguments;
	}
	public Matrix getData() {
		return data;
	}
	public Object[] getArguments() {
		return arguments;
	}
	
	public String getAgentType(){
		return (String)arguments[Indexes.AGENT_TYPE_INDEX.getIndex()];
	}
}
