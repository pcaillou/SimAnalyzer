/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package observer;

import java.util.ArrayList;
import java.util.List;

//AD import org.ujmp.core.Matrix;

import observer.event.ObserverEvent;
import reporter.Reporter;


public abstract class Observer implements ObserverListener{
	public String name="";
	public final static int nbparammax=20;
	public static String[] ParamNames={"ListenTo","ObservedBy","","","","","","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"","","","","","","","","","","","","","","","","","","",""};
	public String[] paramvalues=new String[nbparammax];
	private List<ObserverListener> listeners = new ArrayList<ObserverListener>();
	private SimulationInterface sdr = null;
	
	private List<Reporter> reporters = new ArrayList<Reporter>();
	public void addReporter(Reporter r){
		this.reporters.add(r);
	}
	
	public List<Reporter>getReporters(){
		return reporters;
	}
	
	public void addListener(ObserverListener ol){
		this.listeners.add(ol);
	}
	
	public void removeListener(ObserverListener ol){
		this.listeners.remove(ol);
	}
	
	public List<ObserverListener>getListeners(){
		return listeners;
	}
	
	public void preventListeners(ObserverEvent oe) throws Exception{
		for(ObserverListener ol : listeners)
			ol.onEvent(oe);
	}

	public void setSimulationInterface(SimulationInterface sdr){
		this.sdr = sdr;
	}
	
	public SimulationInterface getSimulationInterface(){
		return sdr;
	}
	public void onEvent(ObserverEvent oe) throws Exception{
		processEvent(oe);
	}
	public abstract void processEvent(ObserverEvent oe) throws Exception;
	
	public abstract void observe(Object...params) throws Exception;
	
	public void setParams(String[] paramvals)
	{
		
	}

}
