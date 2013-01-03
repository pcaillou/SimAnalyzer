package simtools.simanalyzer.netlogo;

//AD import java.io.File;
//AD import java.io.FileWriter;
//AD import java.io.PrintWriter;
//AD import java.lang.reflect.Field;
//AD import java.util.ArrayList;
//AD import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//AD import org.nlogo.agent.Agent;
//AD import org.nlogo.api.AgentException;
//AD import org.nlogo.agent.AgentSet;
//AD import org.nlogo.agent.ArrayAgentSet.Iterator;
//AD import org.nlogo.agent.Patch;
//AD import org.nlogo.agent.Turtle;
//AD import org.nlogo.agent.World;
//AD import org.nlogo.api.AgentVariables;
//AD import org.nlogo.api.Argument;
//AD import org.nlogo.api.Context;
//AD import org.nlogo.api.ExtensionException;

//AD import org.nlogo.api.CompilerException;

public class NetLogoDataNames {
	
	@SuppressWarnings("unused")
	private static HashMap<String, List<String>> varNames = new HashMap<String, List<String>>() ; 
	
	public static  String TURTLES = "Turtles";
//	public static  String TURTLES = "Cities";
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
		return NetLogoInterface.varnames;
	//	List<String> list = varNames.get(agentType);
/*		list = varNames.get(agentType);
		if(list == null ){
			if(agentType.equals(TURTLES)){
				List<String> varsList = Arrays.asList(AgentVariables.getImplicitTurtleVariables(true));
				
				int nbimp=varsList.size();
				list = new ArrayList<String>();
				for(String s: varsList){
					if(!excludedVariables.contains(s)){
					System.out.println(s);
						list.add(s);
					}
				}
				try {
					World w = NetLogoInterface.getWorld();

					AgentSet a=w.turtles();					
					if (!a.isEmpty())
					{
					Turtle t=((Turtle)a.iterator().next());
					int n=w.getVariablesArraySize(t, a);
					for (int index=0; index<w.getVariablesArraySize(t, a); index++)
					{
						if (!varsList.contains(w.turtlesOwnNameAt(index)))
						{
							String st=w.turtlesOwnNameAt(index);
							list.add(w.turtlesOwnNameAt(index));
							System.out.println("to "+w.turtlesOwnNameAt(index));
						}
						
					}		
					}
					
					
					List<String> m = new ArrayList<String>(w.getBreeds().keySet());
					for (int i=0; i<m.size(); i++)
					{						
					 a=w.getBreed(m.get(i));
					if (!a.isEmpty())
					{
					Turtle t=((Turtle)a.iterator().next());
					if (t.getVariableCount()>nbimp)
					for (int index=nbimp; index<t.getVariableCount(); index++)
					{
						String o=w.breedsOwnNameAt(a,index);
//						String o=w.linkBreedsOwnNameAt(a, index-nbimp);
					if (!varsList.contains((String)o))
					{
						System.out.println("brv "+o);
						list.add((String)o);
					}
					}
					}
					}/*
					for (int i=0; i<NetLogoInterface.getTurtleVariableCount(); i++)
					{
						if (!varsList.contains(NetLogoInterface.getTurtleVariableName(i)))
						{
							list.add(NetLogoInterface.getTurtleVariableName(i));
						}
						
					}		/
				} catch (CompilerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				varNames.put(NetLogoDataNames.TURTLES, list);
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
					for (int i=0; i<NetLogoInterface.getPatchVariableCount(); i++)
					{
						if (!varsList.contains(NetLogoInterface.getPatchVariableName(i)))
						list.add(NetLogoInterface.getPatchVariableName(i));
						
					}
				} catch (CompilerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				varNames.put(NetLogoDataNames.PATCHES, list);
			}
		}
		
		return list;*/
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
