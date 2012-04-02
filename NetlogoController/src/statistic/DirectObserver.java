/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package statistic;

import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation;
import org.ujmp.core.calculation.Calculation.Ret;

import observer.SimulationInterface;
import clustering.event.DataEvent;

public class DirectObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
	Matrix lastMeans = null;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public DirectObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = windowSize;
	}

	@Override
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
//		data .showGUI();
		this.preventListeners(new DataEvent(data, de.getArguments()));
	}

}
