/**
a * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 * 2011
 */
package netlogo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

import logs.LogsSimulationController;

import org.nlogo.api.CompilerException;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;

import clustering.Cluster;
import clustering.Clusterer;
import clustering.ClustererObserver;
import controller.SimulationController;
import observer.Observer;
import observer.SimulationInterface;
import statistic.InitParamObserver;
import statistic.LastObserver;
import statistic.SlidingMeanObserver;
import statistic.GlobalObserver;
import statistic.DirectObserver;


public class NetLogoSimulationController extends SimulationController {
	public static Object []getDefaultParams(){
		Object [] params= new Object[13];
		
		params[NetLogoSimulationController.CLUSTERER_INDEX]=null;
		params[NetLogoSimulationController.AGENT_TYPE_INDEX]="Turtles";
		params[NetLogoSimulationController.MAX_TICKS_INDEX]=100;
		params[NetLogoSimulationController.TICKS_BETWEEN_CLUSTERING_INDEX]=10;
		params[NetLogoSimulationController.MODEL_FILE_NAME_INDEX]=null;
		params[NetLogoSimulationController.SETUP_PROCEDURE_INDEX]=null;
		params[NetLogoSimulationController.UPDATE_PROCEDURE_INDEX]=null;
		params[NetLogoSimulationController.VARIANCE_REFRESH_INDEX]=-1;
		params[NetLogoSimulationController.IDCOL_INDEX]=-1;
		params[NetLogoSimulationController.TIMECOL_INDEX]=-1;
		params[NetLogoSimulationController.STARTCLUSTCOL_INDEX]=-1;
		params[NetLogoSimulationController.ENDCLUSTCOL_INDEX]=-1;
		
		return params;
	}
	
	public NetLogoSimulationController(){
		super();
		Sensor = new NetLogoSensor();
	}
	public SimulationInterface initInterface(Object... params)
	{
		NetLogoInterface si = new NetLogoInterface(new NetLogoSensor(), new NetLogoClustersUpdater());
		return si;
		
	}
	
	@SuppressWarnings("null")
	public void runSimulation(Object... params) throws Exception {
		super.runSimulation(params);
	}
	
}
