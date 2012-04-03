/**
a * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 * 2011
 */
package netlogo;

// AD import java.awt.event.WindowAdapter;
//AD import java.awt.event.WindowEvent;
//AD import java.awt.event.WindowListener;
//AD import java.awt.GridBagConstraints;
//AD import java.awt.GridBagLayout;
//AD import java.awt.event.ActionEvent;
//AD import java.awt.event.ActionListener;
//AD import java.io.File;
//AD import java.lang.reflect.InvocationTargetException;
//AD import java.util.ArrayList;
//AD import java.util.List;

//AD import javax.swing.ButtonGroup;
//AD import javax.swing.JButton;
//AD import javax.swing.JFrame;
//AD import javax.swing.JLabel;

//AD import logs.LogsSimulationController;

//AD import org.nlogo.api.CompilerException;
//AD import org.ujmp.core.Matrix;
//AD import org.ujmp.core.MatrixFactory;
//AD import org.ujmp.core.calculation.Calculation.Ret;

//AD import clustering.Cluster;
//AD import clustering.Clusterer;
//AD import clustering.ClustererObserver;
import controller.SimulationController;
//AD import observer.Observer;
import observer.SimulationInterface;
//AD import statistic.InitParamObserver;
//AD import statistic.LastObserver;
//AD import statistic.SlidingMeanObserver;
//AD import statistic.GlobalObserver;
//AD import statistic.DirectObserver;


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
	
	public void runSimulation(Object... params) throws Exception {
		super.runSimulation(params);
	}
	
}
