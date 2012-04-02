/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package logs;

import java.util.List;


import org.nlogo.api.AgentException;
import org.nlogo.agent.Turtle;
import org.nlogo.api.CompilerException;

import clustering.SimulationClustersUpdater;
import clustering.Cluster;

public class LogsClustersUpdater extends SimulationClustersUpdater{
	
	//Generaliser cette méthode
	public void updateClustersInSimulation(List<Cluster> clustersSet) {
		long clustersNumber = clustersSet.size();
		for(Cluster c: clustersSet){
			
			Long clusterId = c.getId();
			for(Long id: c.getComponentIds()){
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
