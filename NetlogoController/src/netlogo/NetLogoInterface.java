/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package netlogo;

import java.awt.Color;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
// AD import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
//AD import java.util.concurrent.ExecutorService;
//AD import java.util.concurrent.Executors;
//AD import java.util.concurrent.ThreadPoolExecutor;


import observer.SimulationInterface;
import observer.SimulationSensor;
import observer.SimulationUpdater;

import org.nlogo.agent.Agent;
import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Patch;
import org.nlogo.agent.TreeAgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.*;
import org.nlogo.app.App;
import org.nlogo.nvm.*;
import org.nlogo.window.*;

public class NetLogoInterface extends SimulationInterface {
	public static World myworld;
	// AD public static ArrayList varnames; /* code original
	public static ArrayList<String> varnames; // */
	public NetLogoInterface(SimulationSensor simulationSensor,
			SimulationUpdater simulationUpdater) {
		super(simulationSensor, simulationUpdater);
		// TODO Auto-generated constructor stub
	}

	public static void wait (int n){
        long t0,t1;
        t0=System.currentTimeMillis();
        do{
            t1=System.currentTimeMillis();
        }
        while (t1-t0< n*1000);
}

	public static int getTurtleVariableCount()throws org.nlogo.api.CompilerException{
		return varnames.size();
/*		Program prog=getProgram();
		AgentSet set = getTurtles();
		if(set.isEmpty())
			return -1;
		return ((Turtle)set.iterator().next()).getVariableCount();*/
	}
	/**
	*	@return the name of the turtle variable indexed by the given index
	*/
	public static String getTurtleVariableName(int index)throws org.nlogo.api.CompilerException{
		return (String)varnames.get(index);
/*		World w = getWorld();
		AgentSet set = getTurtles();
		Turtle t=((Turtle)set.iterator().next());
		while(t.getVariableCount()<getTurtleVariableCount())
			t=((Turtle)set.iterator().next());
		Object o=t.getVariable(index);
		return (String)o;*/
			
//		return w.turtlesOwnNameAt(index); 
	}
	/**
	*	@return the name of the turtle variable indexed by the given index
	*/
	public static String getPatchVariableName(int index)throws org.nlogo.api.CompilerException{		
		World w = getWorld();
		return w.patchesOwnNameAt(index); 
		
	}

	public static int getPatchVariableCount()throws org.nlogo.api.CompilerException{
		AgentSet set = getPatches();
		if(set.isEmpty())
			return -1;
		return ((Patch)set.iterator().next()).getVariableCount();
	}
	/**
	*	@return the world of this application
	*/
	public static World getWorld()throws org.nlogo.api.CompilerException{
		return myworld;
/*		AgentSet set = getPatches();
		if(set.isEmpty())
			return null;
		return ((Patch)set.iterator().next()).world();*/
	}
	public static App getApp()throws org.nlogo.api.CompilerException{
		return App.app();
	}
	public static boolean isDoubleTurtleVariable(int index)throws org.nlogo.api.CompilerException{
		return AgentVariables.isDoubleTurtleVariable(index,true);
	}
	public static boolean isDoublePatchVariable(int index)throws org.nlogo.api.CompilerException{
		return AgentVariables.isDoublePatchVariable(index,true);
	}
	/**
	*	@return the index of the turtle's variable called name
	*/
	public static int indexOfTurtleVariable(String name)throws org.nlogo.api.CompilerException{
		return getWorld().turtlesOwnIndexOf(name);
	}
	/**
	*	@return the index of the patch's variable called name
	*/
	public static int indexOfPatchVariable(String name)throws org.nlogo.api.CompilerException{
		return getWorld().patchesOwnIndexOf(name);
	}
	/**
	*	@return a list containing the values of the asked variable for every turtle
	*/
	public static LogoList getValuesOfTurtlesVariable(String varName) throws org.nlogo.api.CompilerException{
		String command = "["+varName + "] of turtles"; 

		return (LogoList)report(command);
	}
	/**
	*	@return a list containing the values of the asked variable for every turtle
	*/
	public static LogoList getValuesOfTurtlesVariable(int varIndex) throws org.nlogo.api.CompilerException{
		return getValuesOfTurtlesVariable(getTurtleVariableName(varIndex)); 
	}
	/**
	*	@return the patch at the x, y position
	*/
	public static Patch getPatch(double x, double y)throws org.nlogo.api.CompilerException{
		return (Patch) App.app().report("patch " + x + " " + y);
	}
	/**
	*	@return the turtle whose id is id
	*/
	public static Turtle getTurtle(long id)throws org.nlogo.api.CompilerException{
//		return (Turtle) App.app.report("City " + id);
		return getWorld().getTurtle(id);
//		return (Turtle) App.app().report("turtle " + id);
	}
	/**
	*	@return a set containing every turtle in the model
	*/
	public static AgentSet getTurtles() throws org.nlogo.api.CompilerException{
//		Object agents = App.app.report("Cities");
			Object agents = App.app().report("turtles");
			return (AgentSet) agents; 
	}
	public  long[] getAgentsId(){
//		Object agents = App.app.report("Cities");
			AgentSet agents;
			long[] ids=new long[1];
			try {
				agents = getTurtles();
				ids=new long[agents.count()];
				int i=0;
				for (org.nlogo.api.Agent ag : agents.agents())
				{
					ids[i]=ag.id();
					i++;
				}
			} catch (CompilerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ids; 
	}
	/**
	*	@return a set containing every patch in the model
	*/
	public static AgentSet getPatches() throws org.nlogo.api.CompilerException{
			Object agents = App.app().report("patches");
			return (AgentSet) agents; 
	}
	
	//TODO Generaliser cette methode
	public static AgentSet getSetOfAgents(String agentType) throws CompilerException{
		if(agentType.equals(NetLogoDataNames.TURTLES))
			return NetLogoInterface.getTurtles();
		else if(agentType.equals(NetLogoDataNames.PATCHES)){
			return NetLogoInterface.getPatches();
		}
		return null;
	}
	
	public static Program getProgram()throws org.nlogo.api.CompilerException{
		return getWorld().program();
	}
	public static GUIWorkspace getWorkspace()throws org.nlogo.api.CompilerException{
		return App.app().workspace();
	}
	/**
	*	@return the value of the given global variable
	*/
	public static Object getGlobalVariable2(final String varName)throws org.nlogo.api.CompilerException{
		return App.app().report(varName);
	}
	/**
	*	Sets a single value to a given global variable
	*/
	public static void setGlobalVariable(final String varName, final String varValue)throws org.nlogo.api.CompilerException{
		command("set " + varName + " " + varValue);
	}
	/**
	 *	executes the command defined by the given string 
	 */
	public static void command(final String _command)throws org.nlogo.api.CompilerException{
		App.app().command(_command);
	}
	/**
	*	@return the result of calling the reporter defined by the _command string
	*/
	public static Object report(final String _command) throws org.nlogo.api.CompilerException{
		return App.app().report(_command);
	}
	/**
	*	Quits NetLogo by exiting the JVM. Asks user for confirmation first if they have unsaved changes. If the user confirms, calls System.exit(0). 
	*/
	public static void quit() throws  org.nlogo.awt.UserCancelException{
		App.app().quit();
	}
	/**
	*	This method initializes the current open model according the given values for the global variables and the setup method called 
	*	@globalVariablesString string containing the names->values of every global variable to initialized before calling the setupProcedure procedure. 
	*	It can be null or empty. It follows the format : "varName0->value0:varName1->value1:...:varNameN->valueN"
	*	@setupProcedure name of the setup procedure (normally it is called "setup"), it can be null
	*/
	public  void init(String globalVariablesString, String setupProcedure) throws  org.nlogo.api.CompilerException{
		Map<String, String> globalVariables = null;
		if(globalVariablesString != null){
			globalVariables = new HashMap<String, String>();
			StringTokenizer st = new StringTokenizer(globalVariablesString, ":"); // ugh
			while(st.hasMoreTokens()){
		        	StringTokenizer varSt = new StringTokenizer(st.nextToken(), "->");
		        	String varName = varSt.nextToken();
		        	String varValue = varSt.nextToken();
		        	globalVariables.put(varName, varValue);
		    }
		}
		init(globalVariables, setupProcedure);
		initvars();


	}
	public static void initvars() throws CompilerException
	{
		AgentSet set = getPatches();
		if(set.isEmpty())
			System.out.println("no World during init!!");
		myworld=((Patch)set.iterator().next()).world();
		varnames=new ArrayList<String>();
		int nbt=getProgram().turtlesOwn().size();
		int startingvar=0;
		if (nbt>0)
		{
			for (int i=startingvar; i<nbt; i++)
			{
				varnames.add(getProgram().turtlesOwn().get(i));
				System.out.println("var:"+getProgram().turtlesOwn().get(i));
			}
		}
		int nbb=getProgram().breeds().size();
		if (nbb>0)
		{
			String brname;
			Iterator<?> it=getProgram().breeds().entrySet().iterator();
			while (it.hasNext())
			{
				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Map.Entry)it.next();
				brname=(String)pairs.getKey();
				System.out.println("breed:"+brname);
				nbt=getProgram().breedsOwn().get(brname).size();
				for (int i=startingvar; i<nbt; i++)
				{
					String nv=getProgram().breedsOwn().get(brname).get(i);
					if (varnames.contains(nv)==false)
					{
					varnames.add(getProgram().breedsOwn().get(brname).get(i));
					System.out.println("var:"+getProgram().breedsOwn().get(brname).get(i));
					}
				}
			}
		}
		
		
	}
	public static Procedure getGoProcedure() throws CompilerException{
        Iterator<?> proc =getWorkspace().getProcedures().entrySet().iterator();
        while(proc.hasNext()){
        	Procedure procedure = (Procedure)proc.next();
        	if(procedure.name.equals("GO") || procedure.name.equals("go"))
        		return procedure;
        }
        return null;
	}
	/*
	public static void createUpdateProcedure(String updateProcName) throws CompilerException{
		Procedure proc = new Procedure(updateProcName, Procedure.Type.COMMAND);
		Procedure goProc = getGoProcedure();
		proc.code = getCommandsOfGoProcedure();
//		proc.
		getWorkspace().getProcedures().put(updateProcName, proc);
		for(Object o : getWorkspace().getProcedures().keySet()){
			System.out.println("Object class : " + o.getClass() + " value : " + o);
			System.out.println("Object class : " + getWorkspace().getProcedures().get(o).getClass() + " value : " + getWorkspace().getProcedures().get(o));

			System.out.println("********************");
		}
	}*/
	public static org.nlogo.nvm.Command[] getCommandsOfGoProcedure() throws CompilerException{
		Procedure goProcedure = getGoProcedure();
		if(goProcedure != null){
			return goProcedure.code;
		}
		return null;
	}
	/**
	*	This method initializes the current open model according the given values for the global variables and the setup method called 
	*	@globalVariables map containing the names->values of every global variable to initialized before calling the setupProcedure procedure. 
	*	It can be null or empty.
	*	@setupProcedure name of the setup procedure (normally it is called "setup"), it can be null
	*/
	public static void init(Map<String, String> globalVariables, String setupProcedure) throws  org.nlogo.api.CompilerException{
			if(globalVariables != null){
				Iterator<String> it = globalVariables.keySet().iterator();
				while(it.hasNext()){
					String varName = it.next();
					String varValue = globalVariables.get(varName);
					setGlobalVariable(varName, varValue);
				}
			}
			if(setupProcedure != null)
				App.app().command(setupProcedure);
	}
	
	public String getGlobalVariableName()
	{
		World w;
		try {
			w = getWorld();
			String vn=w.observer().getVariable(0).toString();					
			System.out.println(vn);
		} catch (CompilerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	public void setGlobalVariable(String nomvar, Object val)
	{
		World w;
		try {
			w = getWorld();
			try {
				w.setObserverVariableByName(nomvar, val);
			} catch (AgentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LogoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (CompilerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public Object getGlobalVariable(String nomvar)
	{
		World w;
		Object res=null;
		try {
			w = getWorld();
			res=w.getObserverVariableByName(nomvar);
		} catch (CompilerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public void setAgentVariable(int agentId, String nomvar, Object val)
	{
		
		    Turtle ag;
			try {
				ag = getTurtle(agentId);
				
				ag.setTurtleOrLinkVariable(nomvar, val);
			} catch (CompilerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AgentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public Object getAgentVariable(int agentId, String nomvar)
	{
		return null;
		
	}
	

	/**
	*	This method initializes the current open model according the given values for the global variables and the "setup" procedure that is called 
	*	@globalVariables map containing the names->values of every global variable to initialized before calling the setupProcedure procedure. 
	*	It can be null or empty.
	*/
	public static void init(Map<String, String> globalVariables) throws  org.nlogo.api.CompilerException{
		init(globalVariables, "setup");
	}
	/**
	*	Opens the model given by fileName. Inits the App.app object.
	*	@fileName a valid path file containing a NetLogo model
	*/
    public  void launch(final String fileName)throws InterruptedException, java.lang.reflect.InvocationTargetException, org.nlogo.api.CompilerException {
        App.main(new String[0]);
        
           EventQueue.invokeAndWait
             ( new Runnable()
                    { public void run() {
                        try {
                          App.app().open(fileName);
                        }
                        catch( java.io.IOException ex ) {
                          ex.printStackTrace();
                        } 
                    } } );
    }
	/**
	*	Calls iterations times the go procedure. Calls the repeat method with the "go" procedure
	*	@iterations a positive integer number
	*/
	public  void iterate(int iterations)throws org.nlogo.api.CompilerException{
		//App.app.command("repeat " + iterations + " [ go ]");
		repeat(iterations, "go");
	}
	/**
	*	Calls iterations times the go procedure
	*	@iterations a positive integer number
	*/
	public  void repeat(int times, String procedure)throws org.nlogo.api.CompilerException{
		App.app().command("repeat " + times + " [ " + procedure + " ]");
	}
	
	public static Object getDataForAgent(String askedData, Agent agent)throws Exception{
		if(askedData.equals("BREED")){
			TreeAgentSet tas = (TreeAgentSet)agent.getTurtleOrLinkVariable(askedData);
			return tas.type().getSimpleName();
		}
		int iof=getWorld().indexOfVariable(agent, askedData);
		if (iof>-1)
			return agent.getVariable(iof);
			
//			return agent.getTurtleOrLinkVariable(askedData);
		else
			return null;
	}
	
	//TODO Generaliser cette methode
	public static Object getDataForTurtle(String askedData, Turtle turtle)throws Exception{
		return turtle.getTurtleOrLinkVariable(askedData);
	}
	public static ArrayList<Object> getDataForTurtle(ArrayList<String> askedData, Turtle turtle)throws Exception{
		ArrayList<Object> result = new ArrayList<Object>();
		for(String aData : askedData){
			result.add(getDataForTurtle(aData, turtle));
		}
		return result;
	}
	public static HashMap<Long, ArrayList<Object>> getDataForTurtles(ArrayList<String> askedData) throws Exception{
		AgentSet set = getTurtles();
		HashMap<Long, ArrayList<Object>>  result = new HashMap<Long, ArrayList<Object>>();
		for(int i=0; i<set.count(); i++){
			Turtle turtle = (Turtle)set.agent(i);
			result.put(turtle.id, getDataForTurtle(askedData, turtle));
			
		}
		return result;
	}

	public static Color createColorForCluster(int clusterId, int maxClusters){
		int grayLevel = (int)(255*(((double)clusterId) / maxClusters));
		Color c = new Color(grayLevel, grayLevel, grayLevel);
		return c;
	}

	public static void updateNetlogoFromClusters(HashMap<Long,Integer> clustersResult, int numClusters)throws org.nlogo.api.CompilerException, org.nlogo.api.AgentException{
		int color = 0;
		for(Long id: clustersResult.keySet()){
			Integer clusterId = clustersResult.get(id);
		//	Color c  = createColorForCluster(clusterId, numClusters);
		//	color = org.nlogo.api.Color.getRGBInt(c.getRed(), c.getGreen(), c.getBlue());
			color = (int)(((double)org.nlogo.api.Color.getColorNamesArray().length *clusterId)/(numClusters+1));
			Turtle t = getTurtle(id);
			t.setVariable(Turtle.VAR_COLOR,  org.nlogo.api.Color.getColorNumberByIndex(color));
		}

	} 

	
	public  void main(String[] argv) {

		if(argv.length != 1){
			System.out.println("Usage : NetLogoLauncher modelFileName");
			System.exit(0);
		}
        try {
			final String file = argv[0];
			launch(file);
			if(file.indexOf("Flocking") != -1){
				Map<String,String> globalVariables = new HashMap<String, String>();
				globalVariables.put("population", "500");
				globalVariables.put("vision", "4");
				globalVariables.put("minimum-separation", "2");
			
				init(globalVariables);
			}
			else{
				init(null);
			}
			iterate(50);
			Iterator<String> implicitVariables = Arrays.asList(AgentVariables.getImplicitTurtleVariables(true)).iterator();
			System.out.println("Init implicit variables");
			while(implicitVariables.hasNext()){
				Object next = implicitVariables.next();
				System.out.println( next+ "(" +next.getClass() +")");
			}
			System.out.println("End implicit variables");
			int color = 10 % org.nlogo.api.Color.getColorNamesArray().length;
//			color = (int)(((double)org.nlogo.api.Color.getColorNamesArray().length *color)/(clusters.size()+1));
			
			Turtle t = getTurtle(0);
			t.setVariable(Turtle.VAR_COLOR,  org.nlogo.api.Color.getColorNumberByIndex(color));
			System.out.println("number predifined variables : " + t.NUMBER_PREDEFINED_VARS);
			for(int i=0; i<t.getVariableCount(); i++){
/**				if(t.isDoubleVariable(i)){
					System.out.println("Double variable");
				}
				if(t.isSpecialVariable(i)){
					System.out.println("Special variable");
				}**/
/*				if(i >= t.NUMBER_PREDEFINED_VARS){
					if(t.isDoubleVariable(i)){
						System.out.println("owned variable : " +t.getTurtleVariable(i).getClass() + " double value : " + t.getTurtleVariableDouble(i));
					}
					else{
						System.out.println("owned variable : " +t.getTurtleVariable(i).getClass());
					}

				} 
				else{
					if(t.isDoubleVariable(i)){
						System.out.println("predefined variable : " +t.getTurtleVariable(i).getClass() + " double value : " + t.getTurtleVariableDouble(i));
					}
					else{
						System.out.println("predefined variable : " +t.getTurtleVariable(i).getClass());
					}
				}*/ //Sup avec new netlogo api
			}
			Patch p = getPatch(0.2, 0.3);
			System.out.println("Patch at 0.2 0.3 :" + p); 
			@SuppressWarnings("unused")
			World w = getTurtle(0).world();
			
			implicitVariables = Arrays.asList(AgentVariables.getImplicitTurtleVariables(true)).iterator();
			System.out.println("From the world : ");
			while(implicitVariables.hasNext()){
				Object next = implicitVariables.next();
				System.out.println( next+ "(" +next.getClass() +")");
			}
			Object vars[] = t.variables;
			System.out.println("variables from t : ");
			for(int i=0; i< vars.length; i++){
				Object next = vars[i];
				String varName = getTurtleVariableName(i);
				System.out.println( varName + "(" +next.getClass() +")" + indexOfTurtleVariable(varName));
			}
			
			vars = p.variables;
			System.out.println("variables from p : ");
			for(int i=0; i< vars.length; i++){
				Object next = vars[i];
				String varName = getPatchVariableName(i);
				System.out.println( varName + "(" +next.getClass() +")" + indexOfPatchVariable(varName));
			}
			System.out.println("values for : " + getTurtleVariableName(0));
			Iterator<?> iterator = getValuesOfTurtlesVariable(0).iterator(); 
			while(iterator.hasNext()){
				System.out.println(iterator.next());
			}
			wait(5);
			
			quit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
	}


	
}
