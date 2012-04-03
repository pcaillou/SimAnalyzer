/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import observer.SimulationSensor;

import org.ujmp.core.Matrix;
//AD import org.ujmp.core.MatrixFactory;


public class LogsSensor extends SimulationSensor {

	public static final String ID_C_NAME = "Id";
	public static final String CLASS_LABEL_C_NAME = "Class label";
	

	public Matrix readDataFromSimulation(List<String> varNames, String agentType) {
		return LogsInterface.SubMatrix;
	}

	public List<String> getAvailableVariablesForAgentType(String agentType) {
		return LogsDataNames.getAvailableVariablesForAgentType(agentType);
	}

	// TODO Generaliser cette methode
	public List<String> getAvailableAgentTypes() {
		List<String> result = new ArrayList<String>();
		result.add(LogsDataNames.TURTLES);
		result.add(LogsDataNames.PATCHES);
		return result;
	}

	public Double getSimulationTimeStamp() {		
		return LogsInterface.getTick();
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
