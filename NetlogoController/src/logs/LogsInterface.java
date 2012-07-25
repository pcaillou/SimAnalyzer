/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package logs;

//AD import java.awt.Color;
//AD import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
//AD import java.util.ArrayList;
//AD import java.util.Arrays;
import java.util.HashMap;
//AD import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
//AD import java.util.concurrent.ExecutorService;
//AD import java.util.concurrent.Executors;
//AD import java.util.concurrent.ThreadPoolExecutor;

//AD import netlogo.NetLogoSimulationController;

import observer.SimulationInterface;
import observer.SimulationSensor;
import observer.SimulationUpdater;

//AD import org.nlogo.api.CompilerException;
import org.ujmp.core.Matrix;
//AD import org.ujmp.core.doublematrix.*;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
//AD import org.ujmp.core.enums.DB;
import org.ujmp.core.enums.FileFormat;
//AD import org.ujmp.core.enums.ValueType;
import org.ujmp.core.exceptions.MatrixException;

import controller.SimulationController;



public class LogsInterface extends SimulationInterface{
	public LogsInterface(SimulationSensor simulationSensor,
			SimulationUpdater simulationUpdater) {
		super(simulationSensor, simulationUpdater);
		// TODO Auto-generated constructor stub
	}
	public static Matrix GlobalMatrix = null;
	public static Matrix SubMatrix = null;
	public static int IDcol;
	public static int Timecol;
	public static double tickact;
	public static double steptick;
	public static long rowact;

	public static void wait (int n){
        long t0,t1;
        t0=System.currentTimeMillis();
        do{
            t1=System.currentTimeMillis();
        }
        while (t1-t0< n*1000);
}

	public static int getTurtleVariableCount()throws org.nlogo.api.CompilerException{
		return (int)GlobalMatrix.getColumnCount();
	}
	/**
	*	@return the name of the turtle variable indexed by the given index
	*/
	public static String getTurtleVariableName(int index)throws org.nlogo.api.CompilerException{
		return GlobalMatrix.getColumnLabel(index);
	}
	/**
	*	@return the name of the turtle variable indexed by the given index
	*/
	public static String getPatchVariableName(int index)throws org.nlogo.api.CompilerException{		
		return GlobalMatrix.getColumnLabel(index);
		
	}

	public static int getPatchVariableCount()throws org.nlogo.api.CompilerException{
		return (int)GlobalMatrix.getColumnCount();
	}
	/**
	*	@return the world of this application
	*/

	/**
	*	@return the index of the turtle's variable called name
	*/
	/**
	*	@return the index of the patch's variable called name
	*/
	/**
	*	@return a list containing the values of the asked variable for every turtle
	*/
	/**
	*	@return a list containing the values of the asked variable for every turtle
	*/
	/**
	*	@return the patch at the x, y position
	*/
	/**
	*	@return the turtle whose id is id
	*/
	/**
	*	@return a set containing every turtle in the model
	*/
	/**
	*	@return a set containing every patch in the model
	*/
	
	//TODO Generaliser cette methode
	
	/**
	*	@return the value of the given global variable
	*/
	public static Object getGlobalVariable2(final String varName)throws org.nlogo.api.CompilerException{
		return null;
	}
	/**
	*	Sets a single value to a given global variable
	*/
	/**
	 *	executes the command defined by the given string 
	 */
	/**
	*	@return the result of calling the reporter defined by the _command string
	*/
	/**
	*	Quits Logs by exiting the JVM. Asks user for confirmation first if they have unsaved changes. If the user confirms, calls System.exit(0). 
	*/
	/**
	*	This method initializes the current open model according the given values for the global variables and the setup method called 
	*	@globalVariablesString string containing the names->values of every global variable to initialized before calling the setupProcedure procedure. 
	*	It can be null or empty. It follows the format : "varName0->value0:varName1->value1:...:varNameN->valueN"
	*	@setupProcedure name of the setup procedure (normally it is called "setup"), it can be null
	*/
	
	public static void init(String filename, int tdeb, int tstep, int icol, int tcol)
	{
        File myDataFile = new File(filename+".csv");
        Timecol=tcol;
        IDcol=icol;
        try {
//    		Matrix n2 = MatrixFactory.importFromFile(FileFormat.CSV, myDataFile);	
			GlobalMatrix=MatrixFactory.importFromFile(FileFormat.CSV, myDataFile);
		} catch (MatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Matrix ticm=MatrixFactory.fill(new Double(0), GlobalMatrix.getRowCount(),1);

		Matrix n2 = GlobalMatrix.appendHorizontally(ticm);	
		GlobalMatrix=n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
		
		for(long column =0; column<GlobalMatrix.getColumnCount(); column++ ){
//			long nc=newglobal.getColumnCount()-data.getColumnCount()+column;
			GlobalMatrix.setColumnLabel(column, GlobalMatrix.getAsString(0,column));}
		GlobalMatrix.setColumnLabel(GlobalMatrix.getColumnCount()-1, SimulationInterface.CLASS_LABEL_C_NAME);
		GlobalMatrix=GlobalMatrix.subMatrix(Ret.NEW, 1, 0, GlobalMatrix.getRowCount()-1, GlobalMatrix.getColumnCount()-1);
//		SimulationInterface.ID_C_NAME=GlobalMatrix.getColumnLabel(icol);
		GlobalMatrix.setColumnLabel(icol, SimulationInterface.ID_C_NAME);
		
		//		Matrix dm=GlobalMatrix.selectColumns(Ret.NEW, Timecol).convert(ValueType.DOUBLE);
//		dm.sortrows(Ret.ORIG, 0, true);
//		dm.showGUI();
//		GlobalMatrix=GlobalMatrix.appendHorizontally(dm);
//		GlobalMatrix.sortrows(Ret.ORIG, GlobalMatrix.getColumnCount()-1, true);
		GlobalMatrix.showGUI();	
//		Matrix n=GlobalMatrix.sortrows(Ret.NEW, GlobalMatrix.getColumnCount()-1, true);
//		n.showGUI();
		
	    double min=GlobalMatrix.getAsDouble(0,Timecol);
	    int k=0;
	    tickact=min;
	    steptick=tdeb;
	    while(k<GlobalMatrix.getRowCount())
	        {
	            if(GlobalMatrix.getAsDouble(k,Timecol)>=min+steptick)
	                break;
	            k++;
	        }
	    rowact=k;
	    SubMatrix=GlobalMatrix.subMatrix(Ret.LINK, (long)0,(long)0,(long)(k-1),GlobalMatrix.getColumnCount()-1);
	    SubMatrix.showGUI();
		
		
	}
	
	public void init(String globalVariablesString, String setupProcedure) throws  org.nlogo.api.CompilerException{
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

	}
	
	/**
	*	This method initializes the current open model according the given values for the global variables and the setup method called 
	*	@globalVariables map containing the names->values of every global variable to initialized before calling the setupProcedure procedure. 
	*	It can be null or empty.
	*	@setupProcedure name of the setup procedure (normally it is called "setup"), it can be null
	*/
	public static void init(Map<String, String> globalVariables, String setupProcedure) throws  org.nlogo.api.CompilerException{
	}
	/**
	*	This method initializes the current open model according the given values for the global variables and the "setup" procedure that is called 
	*	@globalVariables map containing the names->values of every global variable to initialized before calling the setupProcedure procedure. 
	*	It can be null or empty.
	*/
	public  void init(Map<String, String> globalVariables) throws  org.nlogo.api.CompilerException{
	}
	/**
	*	Opens the model given by fileName. Inits the App.app object.
	*	@fileName a valid path file containing a Logs model
	*/
    public  void launch(final String fileName)throws InterruptedException, java.lang.reflect.InvocationTargetException, org.nlogo.api.CompilerException {
    	
    }
	/**
	*	Calls iterations times the go procedure. Calls the repeat method with the "go" procedure
	*	@iterations a positive integer number
	*/
	public  void iterate(int iterations)throws org.nlogo.api.CompilerException{
	}
	
	public static Double getTick()
	{
		
		return SubMatrix.getAsDouble(Timecol,0);
	}
	/**
	*	Calls iterations times the go procedure
	*	@iterations a positive integer number
	*/
	public  void repeat(int times, String procedure)throws org.nlogo.api.CompilerException{
	    long k=rowact;
//	    tickact=tickact+steptick;
	    tickact=tickact+1;
	    while(k<GlobalMatrix.getRowCount())
	        {
//	            if(GlobalMatrix.getAsDouble(k,Timecol)>tickact+steptick)
		            if(GlobalMatrix.getAsDouble(k,Timecol)>=tickact+1)
	                break;
	            k++;
	        }
	    SubMatrix=GlobalMatrix.subMatrix(Ret.LINK, (long)rowact,(long)0,(long)(k-1),GlobalMatrix.getColumnCount()-1);
//	    SubMatrix.showGUI();
	    rowact=k;
	    System.out.println("Time:"+SubMatrix.getAsDouble(0,Timecol)+"/"+SubMatrix.getAsDouble(1,Timecol));
	    if ((SubMatrix.getRowCount()==0)||(SubMatrix.getAsDouble(0,Timecol)!=SubMatrix.getAsDouble(1,Timecol)))
	    	SimulationController.maxTicks=(int) SimulationController.currenttick;
	}
	


	
}
