/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package statistic;

import java.util.ArrayList;
//import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
//import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.ValueType;
import org.ujmp.core.exceptions.MatrixException;

//import observer.SimulationInterface;
import clustering.Cluster;
import clustering.event.DataEvent;

public class GraphDistCalcObserver extends StatisticalObserver {
	public final static String VarianceEvent = "VarianceEvent";
	public static String[] ParamNames={"ListenTo","ObservedBy","GraphNewColumnName","ColumnNameX","ColumnNameY","DistThreshold","ColumnNameStart","ColumnNameEnd","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","4","GraphDist","XCOR","YCOR","1.0","","","","","","","","","","","","","",""};
	Matrix newglobal = null;
	public String NewColName;
	public String ColNameX,ColNameY;
	public int typecol=0;
	public double distlim=0;
	

	public GraphDistCalcObserver() {
		super();
	}
	
	public void setParams(String[] paramvals)
	{
		try {
			this.NewColName=paramvals[2];
			this.ColNameX=paramvals[3];
			this.ColNameY=paramvals[4];
			this.distlim=Double.parseDouble(paramvals[5]);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public double calcdist(Matrix mat, long x, long y, ArrayList<Long> cols)

	{
		double dist=0;
		for (int i=0; i<cols.size(); i++)
		{
			try {
				double d1=mat.getAsDouble(x,cols.get(i));
				double d2=mat.getAsDouble(y,cols.get(i));
				dist=dist+(d1-d2)*(d1-d2);
			} catch (MatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dist=Math.sqrt(dist);
		return dist;
	}

	@SuppressWarnings("all")
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		Matrix distmat;
		int nbcol=2;
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
		ArrayList<Long> idcols=new ArrayList<Long>();

		idcols.add(data.getColumnForLabel(ColNameX));
		idcols.add(data.getColumnForLabel(ColNameY));
		Matrix mn = MatrixFactory.sparse(data.getRowCount(),data.getRowCount());		    
		distmat=MatrixFactory.dense(data.getRowCount(),data.getRowCount());
		for(long agentX=0; agentX<data.getRowCount(); agentX++){
			Object agentId = data.getAsObject(agentX, idColumn);
			for(long agentY=0; agentY<data.getRowCount(); agentY++){
				double dist=calcdist(data,agentX,agentY, idcols);
				distmat.setAsDouble(dist, agentX, agentY);
			}

		}
		newglobal=MatrixFactory.dense(ValueType.OBJECT,data.getRowCount(),2);
		newglobal.setColumnLabel(0, Cluster.ID_C_NAME);
		newglobal.setColumnLabel(1, this.NewColName);
		String graphval; 
		for(long agentX=0; agentX<data.getRowCount(); agentX++){
			Object agentId = data.getAsObject(agentX, idColumn);
			graphval="[";
			graphval=graphval+"["+data.getAsString(agentX,idColumn)+"]:";
			for(long agentY=0; agentY<data.getRowCount(); agentY++){
				if (agentX!=agentY)
				if (distmat.getAsDouble(agentX,agentY)<this.distlim)
				{
					graphval=graphval+"["+data.getAsString(agentX,idColumn)+":"+data.getAsString(agentY,idColumn)+"]:";
				}
					
			}
			graphval=graphval+"]";
			newglobal.setAsObject(data.getAsObject(agentX,idColumn), agentX,0);
			newglobal.setAsString(graphval, agentX,1);

		}
		newglobal.setLabel("Graphdist");
//		newglobal.showGUI();
		this.preventListeners(new DataEvent(newglobal, de.getArguments()));
	}

}
