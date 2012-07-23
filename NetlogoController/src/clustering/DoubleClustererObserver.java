/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 * Notes : Adaptation de la classe ClustererObserver dans le SimulationsMining
 */
package clustering;

import java.util.ArrayList;
// AD import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import observer.SimulationInterface;
// AD import observer.event.ObserverEvent;
// AD import observer.Observer;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

import controller.SimAnalyzer;
import controller.SimulationController;
import controller.Vtest;
/**
import statistic.event.StatisticEvent;

import clustering.cluster.Cluster;
**/
import clustering.event.DataEvent;

public class DoubleClustererObserver extends ClustererObserver {

	 List <Cluster> clusterltint;
	 List <List<Cluster>> clusterlist;
	 List<Long> clustersIds;
	 int nbcint;
	
	public DoubleClustererObserver(Clusterer c, SimulationInterface si){
		super(c,si);
	}
	
	public void usedata(Matrix data) throws Exception {
//		System.out.println("newDataCO "+name);
		// visualize
//		data.showGUI();
		SimulationController.VClust=new boolean[(int)data.getColumnCount()];
		SimulationController.VInit=new boolean[(int)data.getColumnCount()];
		SimulationController.nbVInit=0;
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
		long classLabelColumn = data.getColumnForLabel(Cluster.CLASS_LABEL_C_NAME);
		long labelColorColumn = data.getColumnForLabel("LABEL-COLOR");
		long xcolumn = data.getColumnForLabel("XCOR");
		long ycolumn = data.getColumnForLabel("YCOR");
		if(idColumn < 0 || idColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data id labelled : " + Cluster.ID_C_NAME);
		}
		if(classLabelColumn < 0 || classLabelColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data class (not necessary filled) labelled : " + Cluster.CLASS_LABEL_C_NAME);
		}
		ArrayList<Long> columns = new ArrayList<Long>();
		columns.add(xcolumn);
		columns.add(ycolumn);
//		data.showGUI();
		for(long i=0; i<data.getColumnCount(); i++){
			SimulationController.VClust[(int)i]=false;
			Pattern p = Pattern.compile("T0");
			Matcher m = p.matcher(data.getColumnLabel(i));
			if(!(i == idColumn
					|| i == classLabelColumn 
					|| i == labelColorColumn
					|| m.lookingAt())){
//				if (i==13)
				SimulationController.VInit[(int)i]=true;
				SimulationController.nbVInit++;
				if (SimAnalyzer.startcol>-1)
				{
					if ((i>=SimAnalyzer.startcol)&(i<=SimAnalyzer.endcol))
					{
						columns.add(i);						
						SimulationController.VClust[(int)i]=true;
					}
				}
				else
				{
					columns.add(i);
					SimulationController.VClust[(int)i]=true;
					
				}
			}
		}
		long xcolumndata = data.getColumnForLabel("XCOR");
		long ycolumndata = data.getColumnForLabel("YCOR");
		if (SimAnalyzer.XYfactor!=1)
		for(long i=0; i<data.getRowCount(); i++){
			data.setAsDouble(data.getAsDouble(i,xcolumndata)*SimAnalyzer.XYfactor, i,xcolumndata);
			data.setAsDouble(data.getAsDouble(i,ycolumndata)*SimAnalyzer.XYfactor, i,ycolumndata);
		}
		Matrix input = data.selectColumns(Ret.NEW, columns);
		long xcolumninput = input.getColumnForLabel("XCOR");
		long ycolumninput = input.getColumnForLabel("YCOR");
		ArrayList<Long> columnsint = new ArrayList<Long>();
		columnsint.add(xcolumninput);
		columnsint.add(ycolumninput);
		Matrix inputint=input.selectColumns(Ret.NEW,columnsint);
//		Matrix inputtrans=input.selectColumns(Ret.LINK,columnsint);
//		inputtrans.mtimes(SimAnalyzer.XYfactor);
		input.setLabel("Input");
		// normalization across the samples cannot hurt
		// i.e. zero mean and unit variance for each feature
//		input = input.standardize(Ret.NEW, Matrix.ROW);
//		input.setLabel("Standardized Input");
		// looks different?
		if(controller.SimulationController.tf)
		{
			controller.SimulationController.input.add(input);
			controller.SimulationController.tf = false;
		}
//		if(!CLUSTER_BUILT){
		if(!c.ClusterBuilt){
				Matrix inputbuild = controller.SimulationController.input.get(controller.SimulationController.inputN);
				Matrix inputbuildint=inputbuild.selectColumns(Ret.NEW,columnsint);
				c.buildClusterer(inputbuildint);
				for(int co=0; co<inputbuildint.getColumnCount(); co++){
					System.out.println("coli "+co+" "+inputbuildint.getColumnLabel(co));
					
				}
//			CLUSTER_BUILT = true;
		if((!c.ClusterBuilt)|(SimAnalyzer.followcluster)){
					
			c.ClusterBuilt = true;
			System.out.println("BuildCO "+name);
			clusterltint = c.clusterData(data, inputint);
			
			clustersIds = new ArrayList<Long>();
			for(long i=0;i<data.getRowCount();i++){
				Long clusterId = c.clusterInstance(inputint, i);
//				if (clusterId==-1) clusterId=new Long(0);
				clustersIds.add(clusterId);
				data.setAsLong(clusterId, i, classLabelColumn);
			}

			List<ArrayList<Long>> lrows=new ArrayList<ArrayList<Long>>();
			nbcint=clusterltint.size();
			for (int j=0; j<clusterltint.size(); j++)
			{
				ArrayList<Long> rows = new ArrayList<Long>();
				lrows.add(rows);
			}
			for(int i=0;i<data.getRowCount();i++){
				Long clusterId = data.getAsLong(i,classLabelColumn);
				if (clusterId==-1) clusterId=new Long(0);
				lrows.get(clusterId.intValue()).add((long)i);
			}
			c.subclusterer=new ArrayList<Clusterer>();
			clusterlist=new ArrayList<List<Cluster>>();
			clusterlt=new ArrayList<Cluster>();
			for (int j=0; j<nbcint; j++)
			{
				ArrayList<Long> rows = lrows.get(j);
				Clusterer cl=SimAnalyzer.newClusterer();
				Matrix inputbuildnew = inputbuild.selectRows(Ret.NEW,rows);
				Matrix inputnew = input.selectRows(Ret.NEW,rows);
				Matrix datanew = data.selectRows(Ret.NEW,rows);
				if (inputbuildnew.getRowCount()>0)
				cl.buildClusterer(inputbuildnew);
				cl.ClusterBuilt = true;
				for(int co=0; co<inputbuildnew.getColumnCount(); co++){
					System.out.println("coln "+co+" "+inputbuildnew.getColumnLabel(co));
					
				}
//			System.out.println("BuildCO "+name);
				List<Cluster> clusterltf = cl.clusterData(datanew, inputnew);
				c.subclusterer.add(cl);
				clusterlist.add(clusterltf);
				for (int k=0; k<clusterltf.size(); k++)
				{
					clusterlt.add(clusterltf.get(k));
					clusterltf.get(k).setId((long)clusterlt.size()-1);
				}
			}
			c.clustinit=clusterlt;
		}
			else
			{

				clusterltint = c.clusterData(data, inputint);
				
				for(long i=0;i<data.getRowCount();i++){
					Long clusterId = c.clusterInstance(inputint, i);
					data.setAsLong(clusterId, i, classLabelColumn);
				}

				List<ArrayList<Long>> lrows=new ArrayList<ArrayList<Long>>();
				nbcint=clusterltint.size();
				for (int j=0; j<clusterltint.size(); j++)
				{
					ArrayList<Long> rows = new ArrayList<Long>();
					lrows.add(rows);
				}
				for(long i=0;i<data.getRowCount();i++){
					Long clusterId = data.getAsLong(i,classLabelColumn);
					lrows.get(clusterId.intValue()).add((long)i);
				}
				clusterlist=new ArrayList<List<Cluster>>();
				clusterlt=new ArrayList<Cluster>();
				for (int j=0; j<nbcint; j++)
				{
					ArrayList<Long> rows = lrows.get(j);
					Clusterer cl=c.subclusterer.get(j);
					Matrix inputnew = input.selectRows(Ret.NEW,rows);
					Matrix datanew = data.selectRows(Ret.NEW,rows);
//				System.out.println("BuildCO "+name);
					List<Cluster> clusterltf = cl.clusterData(datanew, inputnew);
					clusterlist.add(clusterltf);
					for (int k=0; k<clusterltf.size(); k++)
					{
						clusterlt.add(clusterltf.get(k));
						System.out.println("t"+SimulationController.currenttick+" cl "+j+"/"+k+" s "+clusterltf.get(k).getComponentIds().size()+" nid "+((long)clusterlt.size()-1));
						clusterltf.get(k).setId((long)clusterlt.size()-1);
					}
				}
								
				if (SimAnalyzer.followcluster)
				for (int i=0; i<c.clustinit.size(); i++)
				{
					boolean found=false;
					for (int j=0; j<clusterlt.size(); j++)
					{
						if (c.clustinit.get(i).getId()==clusterlt.get(j).getId())
						{
							Vtest.Vtestadd(clusterlt.get(j),data, c.clustinit.get(i)); 
							found=true;
							
						}
					}
					if (found==false)
					{
						Vtest.Vtestaddzeropop(c.clustinit.get(i),data); 
						
					}
//					Vtest.Vtestpop(c.clustinit.get(i), data); 
				}
			}
//		List<Cluster> clusterSet = c.clusterData(data, input);
//		this.reporter.addClusterSet(this.getSimulationInterface().getSimulationSensor().getSimulationTimeStamp(), clusterSet);
		
		for(long i=0;i<data.getRowCount();i++){
			Long clusterIdint = c.clusterInstance(inputint, i);
			if (clusterIdint==-1) clusterIdint=new Long(0);
			Long clusterId = c.subclusterer.get(clusterIdint.intValue()).clusterInstance(input, i);
			data.setAsLong(clusterId, i, classLabelColumn);
			data.setAsLong(clusterIdint*1000+clusterId, i, classLabelColumn);
		}
		}
	}

	

	@Override
	public void observe(Object...params) throws Exception {
		String agentType = (String)params[Indexes.AGENT_TYPE_INDEX.getIndex()];
		Matrix data = getSimulationInterface().getSimulationSensor().readDataFromSimulation(agentType);
		data.setLabel("simulation data");
//		if(!shown)
//			data.showGUI();
//		shown = true;
		DataEvent de = new DataEvent(data, agentType);
		processEvent(de);
	}


}
