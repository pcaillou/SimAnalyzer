/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package simtools.simanalyzer.netlogo;

import java.util.ArrayList;
import java.util.List;

import simtools.simanalyzer.clustering.Cluster;
import simtools.simanalyzer.clustering.SimulationClustersUpdater;
import simtools.simanalyzer.controller.SimAnalyzer;
import simtools.simanalyzer.controller.SimulationController;


// AD import org.nlogo.api.AgentException;
//AD import org.nlogo.agent.Turtle;
//AD import org.nlogo.api.CompilerException;



public class NetLogoClustersUpdater extends SimulationClustersUpdater{
	
	//Generaliser cette m�thode
	public void updateClustersInSimulation(List<Cluster> clustersSet) {
		long clustersNumber = clustersSet.size();
		ArrayList<Long> affectedAgents=new ArrayList<Long>();
		for(Cluster c: clustersSet){
			
			Long clusterId = c.getId();
			for(Long id: c.getComponentIds()){
				int color = (int)(((double)org.nlogo.api.Color.getColorNamesArray().length *clusterId)/(clustersNumber+1));
				color =color %org.nlogo.api.Color.getColorNamesArray().length;
				if (color>=0)
SimulationController.si.setAgentVariable(id.intValue(), "COLOR", org.nlogo.api.Color.getColorNumberByIndex(color));
				else
					SimulationController.si.setAgentVariable(id.intValue(), "COLOR", 139.0);
				affectedAgents.add(id);
//System.out.println("C"+c.getId());
/*if (SimAnalyzer.doubleclustering)
{
	int lab=((Double)SimulationController.si.getAgentVariable(id.intValue(), "COLOR")).intValue();
	if ((lab/1000)>0)
		SimulationController.si.setAgentVariable(id.intValue(), "HEADING", ((double)(lab/1000)));
		
}*/
//					Turtle t = NetLogoInterface.getTurtle(id);
//					t.setVariable(Turtle.VAR_COLOR,  org.nlogo.api.Color.getColorNumberByIndex(color));
				
			}
			
		}
		long[] allagents=SimulationController.si.getAgentsId();
		long clnum=-1;
		for (int i=0; i<allagents.length; i++)
		{
			if (!affectedAgents.contains(allagents[i]))
			{
				System.out.println("Agent "+allagents[i]+" sans cluster");
				int color=-1;
				SimulationController.si.setAgentVariable((int)allagents[i], "COLOR", org.nlogo.api.Color.getColorNumberByIndex(color));
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
