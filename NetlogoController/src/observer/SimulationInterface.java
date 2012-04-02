/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package observer;

public class SimulationInterface {
	public static String ID_C_NAME = "Id";
	public static final String CLASS_LABEL_C_NAME = "Class label";
	private SimulationSensor simulationSensor = null;
	private SimulationUpdater simulationUpdater = null;
	public SimulationInterface(SimulationSensor simulationSensor, SimulationUpdater simulationUpdater){
		setSimulationSensor(simulationSensor);
		setSimulationUpdater(simulationUpdater);
	}

	public void setGlobalVariable(String nomvar, Object val)
	{
		
	}
	
	public Object getGlobalVariable(String nomvar)
	{
		return 0;
		
	}
	
	public String getGlobalVariableName()
	{
		return "";
	}
	
	public void setAgentVariable(int agentId, String nomvar, Object val)
	{
		
	}
	
	public Object getAgentVariable(int agentId, String nomvar)
	{
		return null;
		
	}
	
	public  long[] getAgentsId()
	{
		return null;
		
	}
	public  int getNbAgents()
	{
		return getAgentsId().length;
		
	}
	
	public SimulationSensor getSimulationSensor() {
		return simulationSensor;
	}

	public void setSimulationSensor(SimulationSensor simulationSensor) {
		this.simulationSensor = simulationSensor;
	}
	
	public SimulationUpdater getSimulationUpdater() {
		return simulationUpdater;
	}

	public void setSimulationUpdater(SimulationUpdater simulationUpdater) {
		this.simulationUpdater = simulationUpdater;
	}

	public  void launch(String modelName)throws InterruptedException, java.lang.reflect.InvocationTargetException, org.nlogo.api.CompilerException 
	{
	}

	public  void init(String globalVariablesString, String setupProcedure) throws  org.nlogo.api.CompilerException {
		// TODO Auto-generated method stub
		
	}

	public  void repeat(int i, String updateProcedure)throws org.nlogo.api.CompilerException {
		// TODO Auto-generated method stub
		
	}
}
