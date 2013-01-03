/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic;

import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
//AD import org.ujmp.core.calculation.Calculation;
//AD import org.ujmp.core.calculation.Calculation.Ret;

import simtools.simanalyzer.clustering.Indexes;
import simtools.simanalyzer.clustering.event.DataEvent;
import simtools.simanalyzer.observer.SimulationInterface;

public class LastObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
	Matrix lastMeans = null;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public LastObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = windowSize;
	}
	public LastObserver() {
		super();
	}

	@Override
	public synchronized void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
//		data .showGUI();
		data.setLabel("LastObserver");
		this.preventListeners(new DataEvent(data, de.getArguments()));
	}
	@Override
	public void observe(Object... params) throws Exception {
		String agentType = (String)params[Indexes.AGENT_TYPE_INDEX.getIndex()];
//		Matrix data = getSimulationInterface().getSimulationSensor().readDataFromSimulation(agentType);
		Matrix data = MatrixFactory.emptyMatrix();
        data.setLabel("last observer");
		DataEvent de = new DataEvent(data, agentType);
		processEvent(de);
	}

}
