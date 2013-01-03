/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package simtools.simanalyzer.netlogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import org.nlogo.agent.Agent;
import org.nlogo.agent.AgentSet;
// AD import org.nlogo.agent.ArrayAgentSet;
// AD import org.nlogo.agent.ArrayAgentSet.Iterator;
// AD import org.nlogo.agent.Turtle;
import org.nlogo.api.CompilerException;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;

import simtools.simanalyzer.observer.SimulationSensor;


public class NetLogoSensor extends SimulationSensor {

	public static final String ID_C_NAME = "Id";
	public static final String CLASS_LABEL_C_NAME = "Class label";
	public Matrix readDataFromSimulation(List<String> varNames, String agentType) {

		AgentSet set = null;
		try {
			set = NetLogoInterface.getSetOfAgents(agentType);
		} catch (CompilerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object [][] dataarray = new Object[set.count()][varNames.size()+2];
		AgentSet.Iterator t=set.iterator();

		for(int i=0; i<set.count(); i++){
//			Agent agent = set.agent(i);
			Agent agent = t.next();
			if (agent!=null)
			{
			dataarray[i][0] = agent.id;
			//Label
			dataarray[i][1] = -1;
			for(int j=0; j<varNames.size(); j++)
				try {
					dataarray[i][j+2] = 
						NetLogoInterface.getDataForAgent(varNames.get(j), agent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Matrix data = MatrixFactory.importFromArray(dataarray);
		data.setColumnLabel(0, NetLogoSensor.ID_C_NAME);
		data.setColumnLabel(1, NetLogoSensor.CLASS_LABEL_C_NAME);
		for(int j=0; j<varNames.size(); j++)
			data.setColumnLabel(j+2, varNames.get(j).toString());

		return data;
	}

	public List<String> getAvailableVariablesForAgentType(String agentType) {
		return NetLogoDataNames.getAvailableVariablesForAgentType(agentType);
	}

	// TODO Generaliser cette methode
	public List<String> getAvailableAgentTypes() {
		List<String> result = new ArrayList<String>();
		result.add(NetLogoDataNames.TURTLES);
		result.add(NetLogoDataNames.PATCHES);
		return result;
	}

	public Object getSimulationTimeStamp() {
		try {
			return NetLogoInterface.report("ticks");
		} catch (CompilerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public Matrix readDataFromSimulation(String agentType) {
		return readDataFromSimulation(getAvailableVariablesForAgentType(agentType), agentType);
	}

	public HashMap<String, Matrix> readDataFromSimulation( 
			HashMap <String, List<String>> agentTypeAndvarNames){
		HashMap<String, Matrix> result = new HashMap<String, Matrix>();
		
		for(String agentType: agentTypeAndvarNames.keySet()){
			result.put(agentType, readDataFromSimulation(agentTypeAndvarNames.get(agentType), agentType));
		}
		
		return result;
	}

	public HashMap<String, Matrix> readDataFromSimulation(
			List<String> agentTypes) {
		HashMap<String, Matrix> result = new HashMap<String, Matrix>();
		for(String at : agentTypes){
			result.put(at, readDataFromSimulation(at));
		}
		
		return result;
	}

}
