/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 * Notes : Adaptation de la classe ClustererObserver dans le SimulationsMining
 */
package simtools.simanalyzer.clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

import simtools.simanalyzer.clustering.event.DataEvent;
import simtools.simanalyzer.controller.SimAnalyzer;
import simtools.simanalyzer.controller.SimulationController;
import simtools.simanalyzer.controller.Vtest;
import simtools.simanalyzer.observer.Observer;
import simtools.simanalyzer.observer.SimulationInterface;
import simtools.simanalyzer.observer.event.ObserverEvent;

/**
import statistic.event.StatisticEvent;

import clustering.cluster.Cluster;
**/

public class ClustererObserver extends Observer {

	 List<Cluster> clusterlt;
	 Clusterer c = null;
	 boolean CLUSTER_BUILT = false;
	 ClusteringReporter reporter = new ClusteringReporter(this);
	public ClustererObserver(){
		addReporter(reporter);
	}
	
	public List<Cluster> getClusterlist(){
		return clusterlt;
	}
	
	public ClustererObserver(Clusterer c, SimulationInterface si){
		this();
		this.c = c;
		setSimulationInterface(si);
	}
	@Override
	public void processEvent(ObserverEvent oe) throws Exception {
		if(oe instanceof DataEvent){
			this.newDataAvailable((DataEvent)oe);
		}
/**		else if(oe instanceof StatisticEvent){
			StatisticEvent se = (StatisticEvent)oe;
			this.processEvent(se.getDataEvent());
		}**/
	}
	
	public void usedata(Matrix data) throws Exception
	{
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
		long classLabelColumn = data.getColumnForLabel(Cluster.CLASS_LABEL_C_NAME);
		long labelColorColumn = data.getColumnForLabel("LABEL-COLOR");
		SimulationController.VClust=new boolean[(int)data.getColumnCount()];
		SimulationController.VInit=new boolean[(int)data.getColumnCount()];
		SimulationController.nbVInit=0;
		if(idColumn < 0 || idColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data id labelled : " + Cluster.ID_C_NAME);
		}
		if(classLabelColumn < 0 || classLabelColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data class (not necessary filled) labelled : " + Cluster.CLASS_LABEL_C_NAME);
		}
		ArrayList<Long> columns = new ArrayList<Long>();
//		data.showGUI();
		for(long i=0; i<data.getColumnCount(); i++){
			SimulationController.VClust[(int)i]=false;
			Pattern p = Pattern.compile("T0");
			Pattern p2 = Pattern.compile("WHO");
			Pattern p3 = Pattern.compile("Class label");
			Pattern p5 = Pattern.compile("COLOR");
			Matcher m = p.matcher(data.getColumnLabel(i));
			Matcher m2 = p2.matcher(data.getColumnLabel(i));
			Matcher m3 = p3.matcher(data.getColumnLabel(i));
			Matcher m5 = p5.matcher(data.getColumnLabel(i));
			if(!(i == idColumn
					|| i == classLabelColumn 
					|| i == labelColorColumn
					|| m2.lookingAt()
					|| m3.lookingAt()
					|| m5.lookingAt()
					|| m.lookingAt())){
				SimulationController.VInit[(int)i]=true;
				SimulationController.nbVInit++;
//				if (i==13)
				if (SimAnalyzer.startcol>-1)
				{
					if ((i>=SimAnalyzer.startcol)&(i<=SimAnalyzer.endcol))
					{
						SimulationController.VClust[(int)i]=true;
						columns.add(i);						
					}
				}
				else
				{
					SimulationController.VClust[(int)i]=true;
					columns.add(i);
					
				}
			}
		}
		Matrix input = data.selectColumns(Ret.NEW, columns);
		input.setLabel("Input");
		// normalization across the samples cannot hurt
		// i.e. zero mean and unit variance for each feature
		input = input.standardize(Ret.NEW, Matrix.ROW);
		input.setLabel("Standardized Input");
		// looks different?
		if(simtools.simanalyzer.controller.SimulationController.tf)
		{
			simtools.simanalyzer.controller.SimulationController.input.add(input);
			simtools.simanalyzer.controller.SimulationController.tf = false;
		}
//		if(!CLUSTER_BUILT){
			if(!c.ClusterBuilt){
			c.buildClusterer(simtools.simanalyzer.controller.SimulationController.input.get(simtools.simanalyzer.controller.SimulationController.inputN));
//			controller.SimulationController.input.get(controller.SimulationController.inputN).showGUI();
//			CLUSTER_BUILT = true;
			c.ClusterBuilt = true;
//			System.out.println("BuildCO "+name);
			clusterlt = c.clusterData(data, input);
			c.clustinit=clusterlt;
		}
			else
			{
				clusterlt = c.clusterData(data, input);
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
			HashSet<Long> clustersIds = new HashSet<Long>();
			for(long i=0;i<data.getRowCount();i++){
				Long clusterId = c.clusterInstance(input, i);
				clustersIds.add(clusterId);
				data.setAsLong(clusterId, i, classLabelColumn);
			}
		
	}
	
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
//		System.out.println("newDataCO "+name);
		// visualize
//		data.showGUI();
		
		usedata(data);
		Object params [] = new Object[Indexes.values().length];
		params[Indexes.AGENT_TYPE_INDEX.getIndex()] = de.getAgentType();
		params[Indexes.CLUSTERS_INDEX.getIndex()] = clusterlt;
		if (SimulationController.currentco==this)
		this.getSimulationInterface().getSimulationUpdater().updateSimulationFromData(data, params);
	}

	public void majwithdata(Matrix data) throws Exception {
		usedata(data);
//		Matrix data = de.getData();
//		System.out.println("newDataCO "+name);
		// visualize
//		data.showGUI();
		/*
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
		long classLabelColumn = data.getColumnForLabel(Cluster.CLASS_LABEL_C_NAME);
		long labelColorColumn = data.getColumnForLabel("LABEL-COLOR");
		if(idColumn < 0 || idColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data id labelled : " + Cluster.ID_C_NAME);
		}
		if(classLabelColumn < 0 || classLabelColumn >= data.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data class (not necessary filled) labelled : " + Cluster.CLASS_LABEL_C_NAME);
		}
		ArrayList<Long> columns = new ArrayList<Long>();
//		data.showGUI();
		for(long i=0; i<data.getColumnCount(); i++){
			Pattern p = Pattern.compile("T0");
			Matcher m = p.matcher(data.getColumnLabel(i));
			if(!(i == idColumn
					|| i == classLabelColumn 
					|| i == labelColorColumn
					|| m.lookingAt())){
//				if (i==13)
				if (SimAnalyzer.startcol>-1)
				{
					if ((i>=SimAnalyzer.startcol)&(i<=SimAnalyzer.endcol))
					{
						columns.add(i);						
					}
				}
				else
				{
					columns.add(i);
					
				}
			}
		}
		Matrix input = data.selectColumns(Ret.NEW, columns);
		input.setLabel("Input");
		// normalization across the samples cannot hurt
		// i.e. zero mean and unit variance for each feature
		input = input.standardize(Ret.NEW, Matrix.ROW);
		input.setLabel("Standardized Input");
		// looks different?
		if(controller.SimulationController.tf)
		{
			controller.SimulationController.input.add(input);
			controller.SimulationController.tf = false;
		}
//		if(!CLUSTER_BUILT){
			if(!c.ClusterBuilt){
			c.buildClusterer(controller.SimulationController.input.get(controller.SimulationController.inputN));
//			CLUSTER_BUILT = true;
			c.ClusterBuilt = true;
//			System.out.println("BuildCO "+name);
			clusterlt = c.clusterData(data, input);
			c.clustinit=clusterlt;
		}
			else
			{
				clusterlt = c.clusterData(data, input);
				for (int i=0; i<c.clustinit.size(); i++)
				{
					boolean found=false;
					for (int j=0; j<clusterlt.size(); j++)
					{
						if (c.clustinit.get(i).getId()==clusterlt.get(j).getId())
						{
							Vtest.Vtestadd(clusterlt.get(j),data, c.clustinit.get(i));
//							c.clustinit.get(0).vtestsm.showGUI();
							found=true;
							
						}
					}
						if (found==false)
						{
							Vtest.Vtestaddzeropop(c.clustinit.get(i),data); 
//							c.clustinit.get(0).vtestsm.showGUI();
							
						}
//					Vtest.Vtestpop(c.clustinit.get(i), data); 
				}
			}
//		List<Cluster> clusterSet = c.clusterData(data, input);
//		this.reporter.addClusterSet(this.getSimulationInterface().getSimulationSensor().getSimulationTimeStamp(), clusterSet);
		
		HashSet<Long> clustersIds = new HashSet<Long>();
		for(long i=0;i<data.getRowCount();i++){
			Long clusterId = c.clusterInstance(input, i);
			clustersIds.add(clusterId);
			data.setAsLong(clusterId, i, classLabelColumn);
		}
//		Object params [] = new Object[Indexes.values().length];
//		params[Indexes.AGENT_TYPE_INDEX.getIndex()] = de.getAgentType();
//		params[Indexes.CLUSTERS_INDEX.getIndex()] = clusterlt;
//		this.getSimulationInterface().getSimulationUpdater().updateSimulationFromData(data, params);*/
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
