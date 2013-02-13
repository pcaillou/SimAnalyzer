/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic;

import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;


import simtools.simanalyzer.clustering.event.DataEvent;
import simtools.simanalyzer.controller.SimulationController;
import simtools.simanalyzer.observer.SimulationInterface;

public class GlobalObserver extends StatisticalObserver {
	public static String[] ParamNames={"ListenTo","ObservedBy","ColumnFilterName","FilterValue","","","","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","1","","","","","","","","","","","","","","","","","",""};	
	public final static String VarianceEvent = "VarianceEvent";
	Long windowSize = (long)-1;
    Matrix global = null;
	Matrix newglobal = null;
	double lasttime=-1;
	String LABEL_VAR="";
	String VALUE_VAR="";
	HashMap<Object, Matrix> swapMemory = new HashMap<Object, Matrix>();
	public GlobalObserver(SimulationInterface si, long windowSize) {
		super(si);
		this.windowSize = windowSize;
	}

	public GlobalObserver() {
		super();
	}
	public Matrix getGlobalMatrix() {
		return global;
	}
	public void setParams(String[] paramvals)
	{
		try {
			this.LABEL_VAR=paramvals[2];
			this.VALUE_VAR=paramvals[3];
		} catch (NumberFormatException e) {
			System.err.println("Erreur lors de la recuperation des parametres");
			e.printStackTrace();
			}
	}	
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		Double time=(Double)getSimulationInterface().getSimulationSensor().getSimulationTimeStamp();
//		if (time.doubleValue()>lasttime)
		if ((data.getLabel().matches("LastObserver")))
		{
			if (newglobal!=null)
			{
				if (!LABEL_VAR.equals(""))
				{
					long nocol=newglobal.getColumnForLabel(LABEL_VAR);
					if (nocol>-1)
					for (long i=(newglobal.getRowCount()-1); i>=0; i--)
					{
//						String test=newglobal.getAsString(i,nocol);
						try {
							if (newglobal.getAsString(i,nocol).equals(VALUE_VAR))
							{
								newglobal=newglobal.deleteRows(Ret.NEW, i);
							}
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							if (VALUE_VAR.equals("null"))
							newglobal=newglobal.deleteRows(Ret.NEW, i);
						}
							
					}
					
				}
				global=newglobal;
				SimulationController.updateVariableInfo(global);
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
					newglobal.setColumnLabel(nc, data.getColumnLabel(column));
				}
			}
		}
		
	}

}
