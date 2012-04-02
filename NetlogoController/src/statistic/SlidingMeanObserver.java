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

import controller.SimulationController;

import observer.SimulationInterface;
import clustering.event.DataEvent;

public class SlidingMeanObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
	Matrix lastMeans = null;
	long lasttic=-1;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public SlidingMeanObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = windowSize;
	}

	@Override
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		long idColumn = data.getColumnForLabel(SimulationInterface.ID_C_NAME);
		long classLabelColumn = data.getColumnForLabel(SimulationInterface.CLASS_LABEL_C_NAME);
		long tick=SimulationController.currenttick;
		if(idColumn < 0 || idColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data id labelled : " + SimulationInterface.ID_C_NAME);
		}
		if(classLabelColumn < 0 || classLabelColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data class (not necessary filled) labelled : " + SimulationInterface.CLASS_LABEL_C_NAME);
		}
		Matrix means = null;
		for(long agentRow=0; agentRow<data.getRowCount(); agentRow++){
			Object agentId = data.getAsObject(agentRow, idColumn);
			Matrix swap = swapMemory.get(agentId);
//			data.showGUI();
			Matrix agentDataRow = data.selectRows(Ret.NEW, agentRow);
			if(swap == null){
				swap = agentDataRow;
				for(long column =0; column<data.getColumnCount(); column++ ){
					swap.setColumnLabel(column, "MM"+data.getColumnLabel(column));
				}
				swapMemory.put(agentId, swap);
			}
			else
				if (tick!=lasttic)
				{
{
				if(swap.getRowCount() == windowSize){
					swap = swap.deleteRows(Ret.NEW, 0);
				}
				swap = swap.appendVertically(agentDataRow);
//				swap.showGUI();
				swapMemory.put(agentId, swap);
}
			}
			//Only calculates the mean if there is enough data to do it
	//		if(swap.getRowCount() == windowSize)
			{
				Matrix agentMean = swap.mean(Ret.NEW, Calculation.ROW, true);
				agentMean.setAsObject(null, 0, classLabelColumn);
				agentMean.setAsObject(agentId, 0, idColumn);
				if(means == null){
					means = agentMean;
					for(long column =0; column<data.getColumnCount(); column++ ){
						means.setColumnLabel(column, "MM"+data.getColumnLabel(column));
					}
				}
				else {
					means = means.appendVertically(agentMean);
				}
			}
		}
		if(means != null){
//			means.showGUI();
			this.addObservation(means);
/**			if(lastMeans != null){
				lastMeans.getGUIObject().getFrame().setVisible(false);
			}
			means.showGUI();
			lastMeans = means;**/
//			means.showGUI();
			this.preventListeners(new DataEvent(means, de.getArguments()));
			
		}
		lasttic=tick;
	}

}
