package statistic;

import java.util.HashMap;

import org.ujmp.core.Matrix;

import clustering.event.DataEvent;
import observer.SimulationInterface;

public class GraphObserver extends StatisticalObserver  {
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
    Matrix global = null;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	
	public GraphObserver(SimulationInterface si, long _windowSize) {
		super(si);
		windowSize = _windowSize;
	}

	public Matrix getGlobalMatrix() {
		return global;
	}
	
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		global = data;
		
		//TODO calcul avec les matrices
	}

}
