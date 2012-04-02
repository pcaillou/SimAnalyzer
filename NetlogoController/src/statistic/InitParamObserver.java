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

public class InitParamObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)1;
	Matrix lastMeans = null;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public InitParamObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = (long)1;
	}

	@Override
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		long idColumn = data.getColumnForLabel(SimulationInterface.ID_C_NAME);
		long classLabelColumn = data.getColumnForLabel(SimulationInterface.CLASS_LABEL_C_NAME);
		if(idColumn < 0 || idColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data id labelled : " + SimulationInterface.ID_C_NAME);
		}
		if(classLabelColumn < 0 || classLabelColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data class (not necessary filled) labelled : " + SimulationInterface.CLASS_LABEL_C_NAME);
		}
		Matrix means = null;
		for(long agentRow=0; agentRow<data.getRowCount(); agentRow++){
			Object agentId = data.getAsObject(agentRow, idColumn);
			Matrix agparam = swapMemory.get(agentId);
			Matrix agentDataRow = data.selectRows(Ret.NEW, agentRow);
			if(agparam == null){
				agparam = agentDataRow;
				for(long column =0; column<data.getColumnCount(); column++ ){
					agparam.setColumnLabel(column, "T0"+data.getColumnLabel(column));
				}
				swapMemory.put(agentId, agparam);
			}
			else{
			}
			//Only calculates the mean if there is enough data to do it
	//		if(swap.getRowCount() == windowSize)
			{
				agparam.setAsObject(null, 0, classLabelColumn);
				agparam.setAsObject(agentId, 0, idColumn);
				if(means == null){
					means = agparam;
					for(long column =0; column<data.getColumnCount(); column++ ){
						means.setColumnLabel(column, "T0"+data.getColumnLabel(column));
					}
				}
				else {
					means = means.appendVertically(agparam);
				}
			}
		}
		
		if(means != null){
			this.addObservation(means);
/**			if(lastMeans != null){
				lastMeans.getGUIObject().getFrame().setVisible(false);
			}
			means.showGUI();
			lastMeans = means;**/
//			means.showGUI();
			this.preventListeners(new DataEvent(means, de.getArguments()));
			
		}
	}

}
