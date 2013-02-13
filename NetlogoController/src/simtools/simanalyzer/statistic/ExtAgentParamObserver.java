/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
//AD import org.ujmp.core.calculation.Calculation;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;

import simtools.simanalyzer.clustering.event.DataEvent;
import simtools.simanalyzer.observer.SimulationInterface;

public class ExtAgentParamObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	public static String[] ParamNames={"ListenTo","ObservedBy","CSVFileName","","","","","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","1","params.csv","","","","","","","","","","","","","","","","",""};
	Matrix lastMeans = null;
	Matrix allMeans = null;
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public ExtAgentParamObserver(SimulationInterface si, long windowSize) {
		super(si);

	}
	public ExtAgentParamObserver() {
		super();
	}
	public void setParams(String[] paramvals)
	{
        File myDataFile = new File("models/"+paramvals[2]);
	      try {
//	    		Matrix n2 = MatrixFactory.importFromFile(FileFormat.CSV, myDataFile);	
				allMeans=MatrixFactory.importFromFile(FileFormat.CSV, myDataFile);
				for(long column =0; column<allMeans.getColumnCount(); column++ ){
					allMeans.setColumnLabel(column, "FixPar"+allMeans.getAsString(0,column));
				}
			} catch (MatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
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
				for(long agentPRow=0; agentPRow<allMeans.getRowCount(); agentPRow++){
					if (allMeans.getAsInt(agentPRow,0)==data.getAsInt(agentRow,idColumn))
					{
						agparam = allMeans.selectRows(Ret.NEW, agentPRow);
						for(long column =0; column<allMeans.getColumnCount(); column++ ){
							agparam.setColumnLabel(column, "FixPar"+allMeans.getColumnLabel(column));
						}
						swapMemory.put(agentId, agparam);
						
					}
				}
				
			}
			agparam = swapMemory.get(agentId);
			if(agparam == null){
				agparam = allMeans.selectRows(Ret.NEW, 1);
				for(long column =0; column<allMeans.getColumnCount(); column++ ){
					agparam.setColumnLabel(column, "FixPar"+allMeans.getColumnLabel(column));
				}
				for(long column =1; column<allMeans.getColumnCount(); column++ ){
					agparam.setAsObject(null, 0, column);
				}
				swapMemory.put(agentId, agparam);
				
			}
			agparam = swapMemory.get(agentId);
			//Only calculates the mean if there is enough data to do it
	//		if(swap.getRowCount() == windowSize)
			{
//				agparam.setAsObject(null, 0, classLabelColumn);
//				agparam.setAsObject(agentId, 0, idColumn);
				if(means == null){
					means = agparam;
					for(long column =0; column<allMeans.getColumnCount(); column++ ){
						means.setColumnLabel(column, allMeans.getColumnLabel(column));
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
