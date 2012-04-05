package statistic;

import java.util.HashMap;
import org.ujmp.core.Matrix;
import clustering.event.DataEvent;
import observer.SimulationInterface;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

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
		
		//Graph graph = new SingleGraph("test");
		//TODO calcul avec les matrices
		
		
	}

}
