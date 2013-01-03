/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.observer;

import java.util.HashMap;
import java.util.List;

import org.ujmp.core.Matrix;

public abstract class SimulationSensor {
//	public Matrix readDataFromSimulation();
	public abstract Matrix readDataFromSimulation(List<String> varNames, String agentType);
	public abstract List<String> getAvailableVariablesForAgentType(String agentType);
	public abstract List<String> getAvailableAgentTypes();
	public abstract Object getSimulationTimeStamp();
	
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
