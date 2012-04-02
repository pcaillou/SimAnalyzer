/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netlogo.NetLogoClustersUpdater;
import netlogo.NetLogoInterface;
import netlogo.NetLogoSensor;

import org.nlogo.api.CompilerException;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;

import clustering.Cluster;
import clustering.Clusterer;
import clustering.ClustererObserver;
import clustering.DoubleClustererObserver;

import observer.Observer;
import observer.SimulationInterface;
import observer.SimulationSensor;
import statistic.DirectObserver;
import statistic.GlobalObserver;
import statistic.InitParamObserver;
import statistic.LastObserver;
import statistic.SlidingMeanObserver;

public abstract class SimulationController {
	public static Matrix DataMatrix = null;
	public static SimulationInterface si;
	protected SimulationSensor Sensor;
	public static long currenttick=-1;
	public static ClustererObserver currentco;
	public static int MODEL_FILE_NAME_INDEX = 0;
	public static int CLUSTERER_INDEX = 1;
	public static int MAX_TICKS_INDEX = 2;
	public static int TICKS_BETWEEN_CLUSTERING_INDEX = 3;
	public static int AGENT_TYPE_INDEX = 4;
	public static int SETUP_PROCEDURE_INDEX = 5;
	public static int UPDATE_PROCEDURE_INDEX = 6;
	public static int GLOBAL_VARIABLES_VALUES_INDEX = 7;
	public static int VARIANCE_REFRESH_INDEX = 8;

	public static List<Matrix> input = new ArrayList<Matrix>();
	public static int inputN;
	public static boolean tf = false;

	public static int IDCOL_INDEX = 9;
	public static int TIMECOL_INDEX = 10;
	public static int STARTCLUSTCOL_INDEX = 11;
	public static int ENDCLUSTCOL_INDEX = 12;

	public static int nbagents=0;
	
	public static int typeReRun=0;
	//0 ClustererTarget en steptarget
	//1 Init de ClusterTarget, en steptarget
	//2 Init de ClusterTarget, un run tous les steps
	public static Cluster clusterTarget;
	public static Clusterer clustererTarget;
	public static String glovvarrerun;
	public static int nbsteprerun;
	public static int steptarget;
	public static int noclusttarget;
	public static double clustpopprop=0.5;
	public static double globvarmin=-1;
	public static double globvarmax=-2;
	public static double globvarstep=-1;
	public static double globvarcurrent=0;
	public static Matrix datamatclust;
	public static Matrix StabMat;
	public static boolean[] VClust;
	public static boolean[] VInit;
	public static int nbVInit;
	
	public static Random rand=new Random();
	
	String modelName;
	Integer maxTicks;
	Integer ticksBetweenClustering;
	String agentType;
	String setupProcedure;
	String updateProcedure;
	String globalVariablesString;
	public static List<Clusterer> wcl;
	public static Matrix ResMat;
	
	private List<Observer> observers =new ArrayList<Observer>();

	public SimulationController(){}
	
	public abstract SimulationInterface initInterface(Object... params);

	public void reRunBoucle(Object... params) throws Exception
	{
		int stepglob=0;
       	globvarcurrent=globvarmax;
       	if (globvarmax>globvarmin)
       	{
       		globvarcurrent=(Double)(si.getGlobalVariable(glovvarrerun));
			ResMat.setAsDouble(globvarcurrent, 0,0);
       		globvarcurrent=globvarmin;   		
       	}
		StabMat = MatrixFactory.sparse(ResMat.getRowCount(),1);		    
		StabMat.setRowLabel(0, glovvarrerun);
		StabMat.setRowLabel(2, "size");
   		for (int j=3;j<ResMat.getRowCount(); j++)
	    {
			StabMat.setRowLabel(j, ResMat.getRowLabel(j));
	    }
       	while (globvarcurrent<=globvarmax)
       	{
           	if (globvarmax>globvarmin)
           	{
           		si.setGlobalVariable(glovvarrerun, globvarcurrent);
           	}
       		for (int st=0; st<this.nbsteprerun;st++)
       		{
            	reRunInit(params);	       			
       		}
       		for (int j=2;j<ResMat.getRowCount(); j++)
       		{
    			StabMat.setAsDouble(globvarcurrent, 0,stepglob);
       			double dist=0;
       			double temp=0;
           		for (int st=0; st<this.nbsteprerun;st++)
           		{
           		 temp=Math.abs(ResMat.getAsDouble(j,0)-ResMat.getAsDouble(j,ResMat.getColumnCount()-1-st));	
           		 temp=1-temp/Math.max(Math.abs(ResMat.getAsDouble(j,0)), Math.abs(ResMat.getAsDouble(j,ResMat.getColumnCount()-1-st)));
           		 dist=dist+temp/this.nbsteprerun;
           		}
       			StabMat.setAsDouble(dist, j,stepglob);
       		}
       		StabMat.showGUI();
           	if (globvarmax>globvarmin)
           	{
           		stepglob++;
           		globvarcurrent=globvarcurrent+globvarstep;
				Matrix mn = MatrixFactory.sparse(ResMat.getRowCount(),1);		    
		       	if (globvarcurrent<=globvarmax)
		       	{
           		StabMat=StabMat.appendHorizontally(mn);
		       	}
           	}
           	
           	else globvarcurrent=globvarmax+1;
       	}
		Matrix ResFil=StabMat.copy();
		for (int i=(int)ResFil.getRowCount()-1; i>0; i--)
		{
			if (Double.isNaN(ResFil.getAsDouble(i,0)))
			{
				for (int j=i; j<ResFil.getRowCount()-1; j++)
				{
					ResFil.setRowLabel(j, ResFil.getRowLabel(j+1));
				}
				ResFil=ResFil.deleteRows(Ret.NEW, i);
			}
		}
		ResFil.showGUI();					
		
	}
	
	public void reRunInit(Object... params) throws Exception
	{
		clearObservers();
		
		List<Clusterer>  wclloc = (List<Clusterer>)getParameter(CLUSTERER_INDEX, params);
		
		Long slidingWindowSize = new Long((long)(5.0));
		SlidingMeanObserver smo = new SlidingMeanObserver(si, slidingWindowSize);
		smo.setVisualizationRefresh(-1);
		InitParamObserver ino = new InitParamObserver(si, slidingWindowSize);
		ino.setVisualizationRefresh(-1);

		DirectObserver dio = new DirectObserver(si, slidingWindowSize);
		smo.setVisualizationRefresh(-1);
		LastObserver lasto = new LastObserver(si, slidingWindowSize);
		

		GlobalObserver go = new GlobalObserver(si, slidingWindowSize);
		
		dio.addListener(go);
		dio.addListener(smo);
		dio.addListener(ino);
		smo.addListener(go);
		ino.addListener(go);
		lasto.addListener(go);

		addObserver(dio);
	
		
		try {
			si.init(globalVariablesString, setupProcedure);
            int cltsize =1;      
            Matrix cltmg = null;
            int x = 0;
			System.out.println("ReRun "+typeReRun);
            Cluster targettemp=new Cluster();
            if (typeReRun==1)
            {
            	long[] ids=si.getAgentsId();
            	int nba=ids.length;
            	int nbv=this.clusterTarget.vtests.length;
            	int nbc=(int)(nba*clustpopprop);
				System.out.println("nbc "+nbc+" nbv "+nbv+" nba "+nba);
            	for (int i=0; i<nbc; i++)
            	{
            		long ida=ids[i];
            	    for (int j=0; j<nbv; j++)
            	    {
            	    	if (Math.abs(this.clusterTarget.vtests[j])>2)
            	    	{
//            				Pattern p = Pattern.compile("T0");
//            				Matcher m = p.matcher(datamatclust.getColumnLabel(j));
        					String nv=datamatclust.getColumnLabel(j);
        					System.out.println("nv "+nv+" m "+nv.startsWith("T0"));
 //           				if (m.lookingAt())
            	    		if (nv.startsWith("T0"))
                	    		if (!nv.startsWith("T0Id"))
                    	    		if (!nv.startsWith("T0Class"))
                        	    		if (!nv.startsWith("T0WHO"))
            				{
            					String ni=nv.substring(2);
            					double valv=rand.nextGaussian();
            					valv=valv*Math.sqrt(this.clusterTarget.stderr[j]);
            					valv=valv+this.clusterTarget.avg[j];
            					Object var=si.getAgentVariable((int)ida, ni);
            					if (!ni.contains("-1"))
//            					if(var != null && var instanceof Double)
            					{
                					si.setAgentVariable((int)ida, ni, valv);
                					System.out.println(ni+" v "+valv+" ida "+ida+" avg "+this.clusterTarget.avg[j]+" std "+this.clusterTarget.stderr[j]);
            						
            					}
            					else
            					{
            						if (var!=null)
                					System.out.println(ni+" NO VAR "+var.getClass().toString());
            						if (var==null)
                    					System.out.println(ni+" NO VAR NULL "+ida);
            						
            					}
            					
            				}
            	    		
            	    	}
            	    }
            	    targettemp.addComponent(ida);
            	    
            	}
            }
            List<List<Cluster>> clusterltarray = new ArrayList<List<Cluster>>();
            List<double[][]> vtestlist = new ArrayList<double[][]>();
            List<Integer> ticklist = new ArrayList<Integer>();
            List<Matrix> MatrixList = new ArrayList<Matrix>();
            List<List<Cluster>> clusterltarray2 = new ArrayList<List<Cluster>>();
			for(int tick=0; tick<=maxTicks; tick++){
				currenttick=tick;
				if(tick >=0 && tick % ticksBetweenClustering == 0)
				{
					List<Cluster> clt = new ArrayList<Cluster>();
					int nt=tick/ticksBetweenClustering;

		            List<Observer> obslist = new ArrayList<Observer>();
					for(int i=0;i<=tick/ticksBetweenClustering;i++)
					{
						if(i==0)
							tf = true;
						inputN = i;
						ClustererObserver col = new ClustererObserver(wcl.get(i),si);
						if ((nt==steptarget)&(typeReRun==0))
						{
							col = new ClustererObserver(clustererTarget,si);														
						}
						//col=Clust(by def) de i en tick
						col.setSimulationInterface(si);
						col.name="Obs I"+i+"T"+tick/ticksBetweenClustering;
						go.addListener(col);
						obslist.add(col);
					}
					dio.observe(agentType);
					lasto.observe(agentType);
					for(int i=0;i<=tick/ticksBetweenClustering;i++)
					{
						ClustererObserver col=(ClustererObserver)(obslist.get(i));
						if(i==0)
						tf = true;
						inputN = i;
						if(i==tick/ticksBetweenClustering)
						{
							clusterltarray.add(col.getClusterlist());
							clt = col.getClusterlist();
						}
						clusterltarray2.add(col.getClusterlist());
						go.removeListener(col);
					}
					
					if(tick == 0){
						Matrix data = Sensor.readDataFromSimulation(agentType);
						Matrix ticm=MatrixFactory.fill(new Double(tick), data.getRowCount(),1);
						ticm.setColumnLabel(0, "tick");

						Matrix n2 = data.appendHorizontally(ticm);	
						DataMatrix=n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
						DataMatrix.setColumnLabel(DataMatrix.getColumnCount()-1, "tick");
					}
					else 
					{
						Matrix DataNew = Sensor.readDataFromSimulation(agentType);
						Matrix ticm=MatrixFactory.fill(new Double(tick), DataNew.getRowCount(),1);
						DataNew=DataNew.appendHorizontally(ticm);
						DataMatrix = DataMatrix.appendVertically(DataNew);
					}
					Matrix mg = go.getGlobalMatrix(); 
					MatrixList.add(mg);
					if(tick==maxTicks)
					{
		//				DataMatrix.showGUI();
		//				mg.showGUI();
					}
					if(x==0)
					{
						cltmg = mg;
						x = 1;
					}
					else
						cltmg = cltmg.appendVertically(mg);
					
					double[][] vtest = new double[clt.size()][(int) mg.getColumnCount()];
					for(int i=0;i<clt.size();i++)
					{
						vtest[i]= Vtest.Vtest(clt.get(i),mg);
					}
					ticklist.add(tick);
					vtestlist.add(vtest);	 
//			        ShowClusterlt scl = new ShowClusterlt(clt, tick, cltsize, vtest, mg);
			        cltsize += clt.size(); 
					if ((nt==steptarget)&(typeReRun==0))
					{
						Matrix m=Vtest.VtestM(clt.get(this.noclusttarget),mg);
						m.setAsDouble(this.globvarcurrent, 0,0);
						ResMat=ResMat.appendHorizontally(m);	
//						mn.showGUI();
//						ResMat.showGUI();
						Matrix ResFil=ResMat.copy();
						for (int i=(int)ResFil.getRowCount()-1; i>0; i--)
						{
							if (Double.isNaN(ResFil.getAsDouble(i,0)))
							{
								for (int j=i; j<ResFil.getRowCount()-1; j++)
								{
									ResFil.setRowLabel(j, ResFil.getRowLabel(j+1));
								}
								ResFil=ResFil.deleteRows(Ret.NEW, i);
							}
						}
						ResFil.showGUI();
					}
					if ((nt==steptarget)&(typeReRun==1))
					{
						Matrix m=Vtest.VtestM(targettemp,mg);
						m.setAsDouble(this.globvarcurrent, 0,0);
						ResMat=ResMat.appendHorizontally(m);	
//						mn.showGUI();
//						ResMat.showGUI();
						Matrix ResFil=ResMat.copy();
						for (int i=(int)ResFil.getRowCount()-1; i>0; i--)
						{
							if (Double.isNaN(ResFil.getAsDouble(i,0)))
							{
								for (int j=i; j<ResFil.getRowCount()-1; j++)
								{
									ResFil.setRowLabel(j, ResFil.getRowLabel(j+1));
								}
								ResFil=ResFil.deleteRows(Ret.NEW, i);
							}
						}
						ResFil.showGUI();					
					
					}
			        
/*					WindowListener l = new WindowAdapter()
					{
						public void windowClosing(WindowEvent e)
						{
							 System.exit(0);
						}
					};
					scl.setLocation(500,100);
				    scl.pack() ;
					scl.setVisible(true);*/
				}
					
				si.repeat(1, updateProcedure);
			}	
/*
			if (SimAnalyzer.computehistory)
			{
				Matrix subm;
				int noma=MatrixList.size();
				for(int tick=maxTicks; tick>=0; tick--){
					currenttick=tick;
					if(tick >=0 && tick % ticksBetweenClustering == 0){
						noma--;
						subm=MatrixList.get(noma);
						System.out.println("Compute Hist..."+(tick / ticksBetweenClustering));
						for(int i=0;i<=maxTicks/ticksBetweenClustering;i++)
						{
							ClustererObserver col = new ClustererObserver(wcl.get(i),si);
							if (SimAnalyzer.doubleclustering)
								col = new DoubleClustererObserver(wcl.get(i),si);
							//col=Clust(by def) de i en tick
							col.setSimulationInterface(si);
							col.name="Obs I"+i+"T"+tick/ticksBetweenClustering;
							col.majwithdata(subm);
						}
						
					}
				}
				
			}*/
			
			
			
			/*        ClusterEval ctel = new ClusterEval(clusterltarray,clusterltarray2, ticklist, MatrixList, vtestlist, cltmg); 
			WindowListener l = new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					 System.exit(0);
				}
			};
			ctel.setLocation(500,100);
		    ctel.pack() ;
			ctel.setVisible(true);*/

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (CompilerException e) {
			e.printStackTrace();
		}
		
		
	
	}

	public void runSimulation(Object... params) throws Exception
	{
		clearObservers();
		si = initInterface(params);
		wcl = (List<Clusterer>)getParameter(CLUSTERER_INDEX, params);
		
		
//		Clusterer c = wcl.get(0);
//		ClustererObserver co = new ClustererObserver(c, si);
//		co.setSimulationInterface(si);

//		addObserver(co);

		
		Long slidingWindowSize = new Long((long)5.0);
		SlidingMeanObserver smo = new SlidingMeanObserver(si, slidingWindowSize);
		smo.setVisualizationRefresh(-1);
		InitParamObserver ino = new InitParamObserver(si, slidingWindowSize);
		ino.setVisualizationRefresh(-1);

		DirectObserver dio = new DirectObserver(si, slidingWindowSize);
		smo.setVisualizationRefresh(-1);
		LastObserver lasto = new LastObserver(si, slidingWindowSize);
		
//		smo.addListener(co);

		GlobalObserver go = new GlobalObserver(si, slidingWindowSize);
		
		dio.addListener(go);
		dio.addListener(smo);
		dio.addListener(ino);
		smo.addListener(go);
		ino.addListener(go);
//		go.addListener(co);
		lasto.addListener(go);

//		addObserver(co);
		addObserver(dio);
	
		
		 modelName = (String)getParameter(MODEL_FILE_NAME_INDEX, params);
		 maxTicks = (Integer)getParameter(MAX_TICKS_INDEX, params);
		 ticksBetweenClustering = (Integer)getParameter(TICKS_BETWEEN_CLUSTERING_INDEX, params);
		 ticksBetweenClustering = SimAnalyzer.clusterstep;
		 agentType = (String)getParameter(AGENT_TYPE_INDEX, params);
		 setupProcedure = (String)getParameter(SETUP_PROCEDURE_INDEX, params);
		 updateProcedure = (String)getParameter(UPDATE_PROCEDURE_INDEX, params);
//		Clusterer clusterer = (Clusterer)getParameter(CLUSTERER_INDEX, params);
		 globalVariablesString = (String)getParameter(GLOBAL_VARIABLES_VALUES_INDEX, params);
		try {
			si.launch(modelName);
			si.init(globalVariablesString, setupProcedure);
			si.getGlobalVariableName();
			//NetLogoInterface.createUpdateProcedure("__UPDATE");
            int cltsize =1;      
            Matrix cltmg = null;
            int x = 0;
            List<List<Cluster>> clusterltarray = new ArrayList<List<Cluster>>();
            List<double[][]> vtestlist = new ArrayList<double[][]>();
            List<Integer> ticklist = new ArrayList<Integer>();
            List<Matrix> MatrixList = new ArrayList<Matrix>();
            List<List<Cluster>> clusterltarray2 = new ArrayList<List<Cluster>>();
			for(int tick=0; tick<=maxTicks; tick++){
				currenttick=tick;
				if(tick >=0 && tick % SimAnalyzer.updatestep == 0){						  
//				if(tick >=0 && tick % ticksBetweenClustering == 0){						  
					List<Cluster> clt = new ArrayList<Cluster>();
		            List<Observer> obslist = new ArrayList<Observer>();
					for(int i=0;i<=(tick-tick%ticksBetweenClustering)/ticksBetweenClustering;i++)
					{
						if(i==0)
							tf = true;
						inputN = i;
						ClustererObserver col = new ClustererObserver(wcl.get(i),si);
						if (SimAnalyzer.doubleclustering)
						col = new DoubleClustererObserver(wcl.get(i),si);
						currentco=col;
						//col=Clust(by def) de i en tick
						col.setSimulationInterface(si);
						col.name="Obs I"+i+"T"+tick/ticksBetweenClustering;
						go.addListener(col);
						obslist.add(col);
//						dio.observe(agentType);
/*						for(Observer o:getObservers())	
							if (lasto.getClass().isInstance(o)==false)
							o.observe(agentType);*/
	//					lasto.observe(agentType);
/*						if(i==tick/ticksBetweenClustering)
						{
							clusterltarray.add(col.getClusterlist());
							clt = col.getClusterlist();
						}
						clusterltarray2.add(col.getClusterlist());*/
					}
					dio.observe(agentType);
					lasto.observe(agentType);
					for(int i=0;i<=(tick-tick%ticksBetweenClustering)/ticksBetweenClustering;i++)
					{
						ClustererObserver col=(ClustererObserver)(obslist.get(i));
						if(i==0)
						tf = true;
						inputN = i;
						if(i==tick/ticksBetweenClustering)
						{
							clusterltarray.add(col.getClusterlist());
							clt = col.getClusterlist();
						}
						clusterltarray2.add(col.getClusterlist());
						go.removeListener(col);
					}
					
					Matrix mg = go.getGlobalMatrix(); 
					if(tick == 0){
//					if(tick == ticksBetweenClustering){
						Matrix data = Sensor.readDataFromSimulation(agentType);
						Matrix ticm=MatrixFactory.fill(new Double(tick), data.getRowCount(),1);
						ticm.setColumnLabel(0, "tick");
//						ticm.showGUI();
//						DataMatrix=DataMatrix.appendHorizontally(ticm);

						Matrix n2 = data.appendHorizontally(ticm);	
						DataMatrix=n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
//						for(long column =0; column<data.getColumnCount(); column++ ){
//							long nc=newglobal.getColumnCount()-data.getColumnCount()+column;
//							newglobal.setColumnLabel(nc, data.getColumnLabel(column));}
						DataMatrix.setColumnLabel(DataMatrix.getColumnCount()-1, "tick");
					}
					else 
					{
						Matrix DataNew = Sensor.readDataFromSimulation(agentType);
						Matrix ticm=MatrixFactory.fill(new Double(tick), DataNew.getRowCount(),1);
						DataNew=DataNew.appendHorizontally(ticm);
//						DataMatrix = DataMatrix.appendVertically(DataNew);
					}
					MatrixList.add(mg);
					if(x==0)
					{
						cltmg = mg;
						x = 1;
					}
					else
						cltmg = cltmg.appendVertically(mg);
					if(tick==maxTicks)
					{
						DataMatrix.showGUI();
						cltmg.showGUI();
					}
					
					if(tick >=0 && tick % ticksBetweenClustering == 0){						  
					double[][] vtest = new double[clt.size()][(int) mg.getColumnCount()];
					for(int i=0;i<clt.size();i++)
					{
						vtest[i]= Vtest.Vtest(clt.get(i),mg);
						vtest[i]= Vtest.Vtestinit(clt.get(i),mg);
					}
					ticklist.add(tick);
					vtestlist.add(vtest);	 
			        ShowClusterlt scl = new ShowClusterlt(clt, tick, cltsize, vtest, mg);
			        cltsize += clt.size(); 
			        
					WindowListener l = new WindowAdapter()
					{
						public void windowClosing(WindowEvent e)
						{
							 System.exit(0);
						}
					};
					scl.setLocation(500,100);
				    scl.pack() ;
					scl.setVisible(true);
					}
				}
				si.repeat(1, updateProcedure);
			}
			if (SimAnalyzer.computehistory)
			{
				Matrix subm;
				int noma=MatrixList.size();
				for(int tick=maxTicks; tick>=0; tick--){
					currenttick=tick;
					if(tick >=0 && tick % ticksBetweenClustering == 0){
						noma--;
						subm=MatrixList.get(noma);
						System.out.println("Compute Hist..."+(tick / ticksBetweenClustering));
						for(int i=0;i<=maxTicks/ticksBetweenClustering;i++)
						{
							ClustererObserver col = new ClustererObserver(wcl.get(i),si);
							if (SimAnalyzer.doubleclustering)
								col = new DoubleClustererObserver(wcl.get(i),si);
							//col=Clust(by def) de i en tick
							col.setSimulationInterface(si);
							col.name="Obs I"+i+"T"+tick/ticksBetweenClustering;
							col.majwithdata(subm);
						}
						
					}
				}
				
			}
			
			
			if (SimAnalyzer.followcluster)
			{
	        ClusterEval ctel = new ClusterEval(clusterltarray,clusterltarray2, ticklist, MatrixList, vtestlist, cltmg); 
	        FAgModel fag=new FAgModel(clusterltarray.get(0).get(0).agm);
			WindowListener l = new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					 System.exit(0);
				}
			};
			ctel.setLocation(500,100);
		    ctel.pack() ;
			ctel.setVisible(true);
			fag.setLocation(100,100);
		    fag.pack() ;
			fag.setVisible(true);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (CompilerException e) {
			e.printStackTrace();
		}
		
	}
	
	public Object getParameter(int index, Object... params){
		if(index >=0 && index < params.length)
			return params[index];
		return null;
	}

	public List<Observer> getObservers(){
		return observers;
	}
	
	public void clearObservers(){
		observers.clear();
	}
	public void addObserver(Observer o){
		observers.add(o);
	}
		
}
