/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package netlogo;

import java.util.List;


import org.nlogo.api.AgentException;
import org.nlogo.agent.Turtle;
import org.nlogo.api.CompilerException;

import controller.SimulationController;

import clustering.SimulationClustersUpdater;
import clustering.Cluster;

public class NetLogoClustersUpdater extends SimulationClustersUpdater{
	
	//Generaliser cette m�thode
	public void updateClustersInSimulation(List<Cluster> clustersSet) {
		long clustersNumber = clustersSet.size();
		for(Cluster c: clustersSet){
			
			Long clusterId = c.getId();
			for(Long id: c.getComponentIds()){
				int color = (int)(((double)org.nlogo.api.Color.getColorNamesArray().length *clusterId)/(clustersNumber+1));
				color =color %org.nlogo.api.Color.getColorNamesArray().length;
SimulationController.si.setAgentVariable(id.intValue(), "COLOR", org.nlogo.api.Color.getColorNumberByIndex(color));
//					Turtle t = NetLogoInterface.getTurtle(id);
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
