package simtools.simanalyzer.logs;

// AD import java.io.File;
//AD import java.io.FileWriter;
//AD import java.io.PrintWriter;
//AD import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//AD import netlogo.NetLogoInterface;

//AD import org.nlogo.agent.Agent;
//AD import org.nlogo.api.AgentException;
//AD import org.nlogo.agent.AgentSet;
//AD import org.nlogo.agent.ArrayAgentSet.Iterator;
//AD import org.nlogo.agent.Patch;
//AD import org.nlogo.agent.Turtle;
//AD import org.nlogo.agent.World;
import org.nlogo.api.AgentVariables;
//AD import org.nlogo.api.Argument;
//AD import org.nlogo.api.Context;
//AD import org.nlogo.api.ExtensionException;

import org.nlogo.api.CompilerException;

public class LogsDataNames {
	
	private static HashMap<String, List<String>> varNames = new HashMap<String, List<String>>() ; 
	
	public static  String TURTLES = "Turtles";
	public static  String PATCHES = "Patches";
	public static HashSet<String> excludedVariables = new HashSet<String>();
	
	public static String ID_VAR_NAME = "WHO";
	public static String TURTLE_COLOR_VAR_NAME = "COLOR";
	static{
		excludedVariables.add(ID_VAR_NAME);
		excludedVariables.add(TURTLE_COLOR_VAR_NAME);
	}
//	public static String PATCH_COLOR_VAR_NAME = "PCOLOR";
	public static List<String> list;
	
	public static List<String> getAvailableVariablesForAgentType(String agentType){
//		return NetLogoInterface.varnames;
		list = varNames.get(agentType);
		if(list == null ){
			if(agentType.equals(TURTLES)){
				list = new ArrayList<String>();
				try {
					for (int index=0; index<LogsInterface.getTurtleVariableCount(); index++)
					{
						if (!list.contains(LogsInterface.getTurtleVariableName(index)))
						{
							String st=LogsInterface.getTurtleVariableName(index);
							list.add(LogsInterface.getTurtleVariableName(index));
							System.out.println("to "+st);
						}
						
					}
					
					
				} catch (CompilerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				varNames.put(LogsDataNames.TURTLES, list);
			}
			else if(agentType.equals(PATCHES)){
				List<String> varsList = Arrays.asList(AgentVariables.getImplicitPatchVariables(true));
				list = new ArrayList<String>();
				for(String s: varsList){
					if(!excludedVariables.contains(s)){
//					System.out.println(s);
						list.add(s);
					}
				}
				try {
					for (int i=0; i<LogsInterface.getPatchVariableCount(); i++)
					{
						if (!varsList.contains(LogsInterface.getPatchVariableName(i)))
						list.add(LogsInterface.getPatchVariableName(i));
						
					}
				} catch (CompilerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				varNames.put(LogsDataNames.PATCHES, list);
			}
		}
		
		return list;
	}
/**	
	public static int mapVarNameToIndex(String agentType, String varName){
		if(agentType.equals(TURTLES)){
			Turtle t = null;
			t.get
//			if(varName)
		}
	}
	**/
}
