/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic;

import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;
//AD import org.ujmp.core.calculation.Calculation;
//AD import org.ujmp.core.calculation.Calculation.Ret;

import simtools.simanalyzer.clustering.event.DataEvent;
import simtools.simanalyzer.observer.SimulationInterface;

public class FilterObserver extends StatisticalObserver {
	public static String[] ParamNames={"ListenTo","ObservedBy","ColumnNotNullName","","","","","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","1","ID","","","","","","","","","","","","","","","","",""};
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
	Matrix lastMeans = null;
	String LABEL_VAR="";
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public FilterObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = windowSize;
	}
	public FilterObserver() {
		super();
	}
	public void setParams(String[] paramvals)
	{
		try {
			this.LABEL_VAR=paramvals[3];
		} catch (NumberFormatException e) {
			System.err.println("Erreur lors de la recuperation des parametres");
			e.printStackTrace();
			}
	}
	@Override
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		long nocol=data.getColumnForLabel(LABEL_VAR);
		if (nocol>-1)
		for (long i=(data.getRowCount()-1); i>=0; i--)
		{
			String test=data.getAsString(i,nocol);
			if (data.getAsString(i,nocol).equals("null"))
			{
				data=data.deleteRows(Ret.ORIG, i);
			}
				
		}
//		data .showGUI();
		this.preventListeners(new DataEvent(data, de.getArguments()));
	}

}
