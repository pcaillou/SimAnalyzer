/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic;


import javax.swing.JFrame;

import org.ujmp.core.Matrix;

import simtools.simanalyzer.clustering.Indexes;
import simtools.simanalyzer.clustering.event.DataEvent;
import simtools.simanalyzer.observer.Observer;
import simtools.simanalyzer.observer.SimulationInterface;
import simtools.simanalyzer.observer.event.ObserverEvent;

public abstract class StatisticalObserver extends Observer {
	
	private Matrix observations = null;
	private int visualizationRefresh = -1;
	private JFrame visualizationFrame = null;
	public StatisticalObserver(){}
	public StatisticalObserver(SimulationInterface si){
		this();
		setSimulationInterface(si);
	}

	public void addObservation(Matrix observation){
		if(observations == null)
			observations = observation;
		else
			observations = observations.appendVertically(observation);
		observation.setLabel(this.getClass().getSimpleName());
		if(visualizationRefresh > 0){
			if(observations.getRowCount() % visualizationRefresh == 0){
				this.updateVisualizationFrame();
			}
		}
	}
	
	public void resetObservations(){
		observations = null;
	}
	
	public Matrix getObservations(){
		return observations;
	}
	public int getVisualizationRefresh() {
		return visualizationRefresh;
	}
	public void setVisualizationRefresh(int visualizationRefresh) {
		this.visualizationRefresh = visualizationRefresh;
	}
	public JFrame getVisualizationFrame() {
		return visualizationFrame;
	}
	private void updateVisualizationFrame() {
		if(this.visualizationFrame != null){
			this.visualizationFrame.dispose();
			this.visualizationFrame = null;
		}
		if(observations != null){
//			this.visualizationFrame = observations.showGUI();
		}
	}
	public Long getObservationsNumber (){
		if(observations == null)
			return (long)0;
		return observations.getRowCount();
	}
	@Override
	public void processEvent(ObserverEvent oe) throws Exception {
		if(oe instanceof DataEvent){
			this.newDataAvailable((DataEvent)oe);
		}
	}
	public abstract void newDataAvailable(DataEvent de) throws Exception;

	@Override
	public void observe(Object... params) throws Exception {
		String agentType = (String)params[Indexes.AGENT_TYPE_INDEX.getIndex()];
		Matrix data = getSimulationInterface().getSimulationSensor().readDataFromSimulation(agentType);
		data.setLabel("simulation data");
		DataEvent de = new DataEvent(data, agentType);
		processEvent(de);
	}

}
