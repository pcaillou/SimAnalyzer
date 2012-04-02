/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 * 2011
 */
package logs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

//import Logs.SimAnalyzer.ShowClusterlt;

import netlogo.NetLogoClustersUpdater;
import netlogo.NetLogoSensor;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.DB;
import org.ujmp.core.enums.FileFormat;

import clustering.Cluster;
import clustering.Clusterer;
import clustering.ClustererObserver;
import controller.SimulationController;
import observer.Observer;
import observer.SimulationInterface;
import statistic.LastObserver;
import statistic.SlidingMeanObserver;
import statistic.GlobalObserver;
import statistic.DirectObserver;


public class LogsSimulationController extends SimulationController {
	public static Matrix DataMatrix = null;
	public static List<List<Cluster>> clusterltarray = new ArrayList();
	public static int MODEL_FILE_NAME_INDEX = 0;
	public static int CLUSTERER_INDEX = 1;
	public static int MAX_TICKS_INDEX = 2;
	public static int TICKS_BETWEEN_CLUSTERING_INDEX = 3;
	public static int AGENT_TYPE_INDEX = 4;
	public static int SETUP_PROCEDURE_INDEX = 5;
	public static int UPDATE_PROCEDURE_INDEX = 6;
	public static int GLOBAL_VARIABLES_VALUES_INDEX = 7;
	public static int VARIANCE_REFRESH_INDEX = 8;
	public static int IDCOL_INDEX = 9;
	public static int TIMECOL_INDEX = 10;
	public static int STARTCLUSTCOL_INDEX = 11;
	public static int ENDCLUSTCOL_INDEX = 12;
	private LogsSensor LogsSensor = new LogsSensor();
	public static Object []getDefaultParams(){
		Object [] params= new Object[13];
		
		params[LogsSimulationController.CLUSTERER_INDEX]=null;
		params[LogsSimulationController.AGENT_TYPE_INDEX]="Turtles";
		params[LogsSimulationController.MAX_TICKS_INDEX]=100;
		params[LogsSimulationController.TICKS_BETWEEN_CLUSTERING_INDEX]=10;
		params[LogsSimulationController.MODEL_FILE_NAME_INDEX]=null;
		params[LogsSimulationController.SETUP_PROCEDURE_INDEX]=null;
		params[LogsSimulationController.UPDATE_PROCEDURE_INDEX]=null;
		params[LogsSimulationController.VARIANCE_REFRESH_INDEX]=-1;
		params[LogsSimulationController.IDCOL_INDEX]=-1;
		params[LogsSimulationController.TIMECOL_INDEX]=-1;
		params[LogsSimulationController.STARTCLUSTCOL_INDEX]=-1;
		params[LogsSimulationController.ENDCLUSTCOL_INDEX]=-1;
		
		return params;
	}
	
	public LogsSimulationController(){
		super();
		Sensor = new LogsSensor();
	}

	public SimulationInterface initInterface(Object... params)
	{
		LogsInterface si = new LogsInterface(new LogsSensor(), new LogsClustersUpdater());
		String modelName = (String)getParameter(MODEL_FILE_NAME_INDEX, params);
		Integer maxTicks = (Integer)getParameter(MAX_TICKS_INDEX, params);
		Integer ticksBetweenClustering = (Integer)getParameter(TICKS_BETWEEN_CLUSTERING_INDEX, params);
		String agentType = (String)getParameter(AGENT_TYPE_INDEX, params);
		String setupProcedure = (String)getParameter(SETUP_PROCEDURE_INDEX, params);
		String updateProcedure = (String)getParameter(UPDATE_PROCEDURE_INDEX, params);
		String globalVariablesString = (String)getParameter(GLOBAL_VARIABLES_VALUES_INDEX, params);
		Integer idcol=(Integer)getParameter(IDCOL_INDEX, params);
		Integer idtim=(Integer)getParameter(TIMECOL_INDEX, params);

		LogsInterface.init(modelName,ticksBetweenClustering.intValue(),maxTicks.intValue(),idcol.intValue(),idtim.intValue());
		return si;
		
	}

/*	public void runSimulation(Object... params) throws Exception {
		
		clearObservers();
		SimulationInterface si = new SimulationInterface(new LogsSensor(), new LogsClustersUpdater());


		Clusterer c = (Clusterer)getParameter(CLUSTERER_INDEX, params);
		ClustererObserver co = new ClustererObserver(c, si);
		co.setSimulationInterface(si);

//		addObserver(co);

		
		Long slidingWindowSize = new Long((long)2.0);
		SlidingMeanObserver smo = new SlidingMeanObserver(si, slidingWindowSize);
		smo.setVisualizationRefresh(-1);

		DirectObserver dio = new DirectObserver(si, slidingWindowSize);
		smo.setVisualizationRefresh(-1);
		LastObserver lasto = new LastObserver(si, slidingWindowSize);
		
		
//		smo.addListener(co);

		GlobalObserver go = new GlobalObserver(si, slidingWindowSize);

		dio.addListener(go);
		dio.addListener(smo);
		smo.addListener(go);
		go.addListener(co);
		lasto.addListener(go);

//		addObserver(co);
		addObserver(dio);
		
		try {
			LogsInterface.launch(modelName);
			LogsInterface.init(globalVariablesString, setupProcedure);
			//LogsInterface.createUpdateProcedure("__UPDATE");

			for(int tick=0; tick<=maxTicks; tick++){
				if(tick >0 && tick % ticksBetweenClustering == 0){
					for(Observer o:getObservers())					
						o.observe(agentType);
					lasto.observe(agentType);
				    clusterltarray.add(clustering.ClustererObserver.clusterlist());
				    System.out.println("cls "+clusterltarray.size());
					if(tick == ticksBetweenClustering){
						DataMatrix = LogsSensor.readDataFromSimulation(agentType);
					}
					else 
					{
						DataMatrix = DataMatrix .appendVertically(LogsSensor.readDataFromSimulation(agentType));
					}
				//	DataMatrix .showGUI();
				}
				LogsInterface.repeat(1, updateProcedure);
			}
//		DataMatrix .showGUI();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	*/
}
