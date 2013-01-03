/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package simtools.simanalyzer.logs;

import java.util.List;

import simtools.simanalyzer.clustering.Cluster;
import simtools.simanalyzer.clustering.SimulationClustersUpdater;


// AD import org.nlogo.api.AgentException;
//AD import org.nlogo.agent.Turtle;
//AD import org.nlogo.api.CompilerException;


public class LogsClustersUpdater extends SimulationClustersUpdater{
	
	//Generaliser cette m�thode
	public void updateClustersInSimulation(List<Cluster> clustersSet) {
		long clustersNumber = clustersSet.size();
		for(Cluster c: clustersSet){
			
			Long clusterId = c.getId();
			for(@SuppressWarnings("unused") Long id: c.getComponentIds()){
				int color = (int)(((double)org.nlogo.api.Color.getColorNamesArray().length *clusterId)/(clustersNumber+1));
				color =color %org.nlogo.api.Color.getColorNamesArray().length;
//				System.out.println("Color : " + color + "Max color : " +org.nlogo.api.Color.getColorNamesArray().length);
//					Turtle t = LogsInterface.getTurtle(id);
//					t.setVariable(Turtle.VAR_COLOR,  org.nlogo.api.Color.getColorNumberByIndex(color));
				
			}
			
		}
		
	}

	//TODO Generaliser cette methode
	@Override
	public void updateClustersInSimulation(List<Cluster> clustersSet,
			String agentType) {
		this.updateClustersInSimulation(clustersSet);
	}

}
