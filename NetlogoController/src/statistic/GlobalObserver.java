/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package statistic;

import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

import observer.SimulationInterface;
import clustering.event.DataEvent;

public class GlobalObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
    Matrix global = null;
	Matrix newglobal = null;
	double lasttime=-1;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public GlobalObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = windowSize;
	}

	public Matrix getGlobalMatrix() {
		return global;
	}
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		Double time=(Double)getSimulationInterface().getSimulationSensor().getSimulationTimeStamp();
//		if (time.doubleValue()>lasttime)
		if ((data.getLabel().matches("LastObserver")))
		{
			if (newglobal!=null)
			{
				global=newglobal;
				this.preventListeners(new DataEvent(global, de.getArguments()));
				newglobal=null;
			}	
			lasttime=time.doubleValue();
//			newglobal=data;
		}
		else
		{
			if(lasttime==-1)
			{
				lasttime=time.doubleValue();				
			}
			if (newglobal==null)
			{
				newglobal=data;
			}
			else
			{
			Matrix n2 = newglobal.appendHorizontally(data);	
			newglobal=n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
			for(long column =0; column<data.getColumnCount(); column++ ){
				long nc=newglobal.getColumnCount()-data.getColumnCount()+column;
				newglobal.setColumnLabel(nc, data.getColumnLabel(column));}
			}
		}
		
	}

}
