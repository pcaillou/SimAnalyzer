package controller;


//AD import clustering.Cluster;
import clustering.Cluster;
import clustering.Clusterer;
import statistic.*;
//AD import clustering.Indexes;
import clustering.WekaClusterer;
import logs.*;
//AD import controller.*;

import java.awt.event.*;

import javax.swing.*;


import netlogo.NetLogoInterface;
import netlogo.NetLogoSimulationController;

//AD import org.nlogo.app.App;
import observer.Observer;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
//AD import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;
import org.ujmp.core.doublematrix.factory.DefaultDenseDoubleMatrix2DFactory;
import org.ujmp.core.doublematrix.factory.DenseDoubleMatrix2DFactory;
import org.ujmp.core.enums.DB;
import org.ujmp.core.enums.FileFormat;
//AD import org.ujmp.core.enums.ValueType;
import org.ujmp.core.exceptions.MatrixException;
import org.ujmp.core.stringmatrix.impl.DefaultDenseStringMatrix2D;

import statistic.DirectObserver;

//AD import statistic.distribution.VariableDistribution;

import com.mysql.jdbc.Connection;
//AD import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import java.awt.*;
import java.io.*;
//import java.lang.reflect.Array;
import java.sql.DriverManager;
//AD import java.sql.ResultSet;
import java.sql.SQLException;
//AD import java.text.DecimalFormat;
import java.util.ArrayList;
//AD import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
//AD import java.util.regex.Matcher;
//AD import java.util.regex.Pattern;


//
//
// RepStat bla
//
//
public class SimAnalyzer extends JFrame
		implements ActionListener, ItemListener
{
	private static final long serialVersionUID = 1L;
	public final static int nbObserverMax=15;
//	public String[] obsnames=new String[nbObserverMax];
	public static String[] obstypes=new String[nbObserverMax];
	public static String[][] obsparams=new String[nbObserverMax][Observer.nbparammax];

	static ArrayList<String> ObsPossibleTypeList;
	public static Map<String,String[]> obsParamNames;
	public static Map<String,String[]> obsParamDefaultValues;
	
	static boolean startnetlogo;
	static boolean startlogs;
	static boolean restartnetlogo;
	static boolean followcluster=true;
	static boolean computehistory=true;
	static boolean doubleclustering=false;
	static boolean vtquali=false;
	static String name;
	public static Integer  clusterstep, totalsteps,updatestep;
	public static  Integer  agcol, timecol, startcol, endcol;
	static SimulationController nlsc;
//	static NetLogoSimulationController nlsc;
//	static LogsSimulationController lsc;
	static Object [] params;
	JMenuBar menubar=new JMenuBar();
	JMenu menup=new JMenu("Project");
	JMenuItem micp=new JMenuItem("Create Projet");
	JMenuItem miop=new JMenuItem("Open Projet");
	static JMenu menug=new JMenu("Get Data");
	JMenuItem mignl=new JMenuItem("Get Online Data From NetLogo");
	JMenuItem migrl=new JMenuItem("Get Offline Data From CSV Log");
	static JMenu menus=new JMenu("Save Data");
	JMenuItem mist=new JMenuItem("Save to Database");
	static String prname;
	static File myDataFile = null;
	static List<WekaClusterer> wcl = new ArrayList<WekaClusterer>();
	JButton jbcreateproject,jbloadproject,jbloadresults;
	public static int tabidintro=0;
	public static int tabidproject=1;	
	public static int tabidobserver=2;	
	public static int tabidclusters=3;	
	public static int tabidoverview=4;	
	public static int tabidoverviewsort=5;	
	public static int tabiddetail=6;
	public static String tabnameproject="Project Config";
	public static String tabttproject="";
	public static Icon tabicproject=null;
	public static String tabnameobserver="Observers Config";
	public static String tabttobserver="";
	public static Icon tabicobserver=null;
	public static String tabnameclusters="Clusters population";
	public static String tabttclusters="";
	public static Icon tabicoclusters=null;
	public static String tabnameoverview="Overview (by name)";
	public static String tabttoverview="";
	public static Icon tabicooverview=null;
	public static String tabnameoverviewsort="Overview (by VT)";
	public static String tabttoverviewsort="";
	public static Icon tabicooverviewsort=null;
	public static JTabbedPane tabbedpane;
	public static JTabbedPane clusterspane;
	public static JPanel evalpane;
	public static SimAnalyzer simanal;
	public static String clustererParametersString;
	public static String[] clustererParameters;
	public static int clustererType;

	// AD /*
	static Vector<Object> vjf = new Vector<Object>();
	static Vector<Object> vc1 = new Vector<Object>();
	static Vector<Object> vc2 = new Vector<Object>();
	static Vector<Object> vcb = new Vector<Object>();
	// */
	// AD static Vector vjf = new Vector();
	// AD static Vector vc1 = new Vector();
	// AD static Vector vc2 = new Vector();
	// AD static Vector vcb = new Vector();
	static int columnCount = 5;
	static JButton jbt1,jbt2,jbt3,jbt4,jbt5;
	static Boolean view = false;
	
	GridBagConstraints gbc=new GridBagConstraints();
	GridBagLayout gb=new GridBagLayout();
	
 SimAnalyzer()
	{		
		super(" SimAnalyzer "); 
		@SuppressWarnings("rawtypes")
		Class c;
		String[] pn,pnd;
		ObsPossibleTypeList=new ArrayList<String>();
		obsParamNames=new HashMap<String,String[]>();
		obsParamDefaultValues=new HashMap<String,String[]>();
		SimAnalyzer.simanal=this;
		
		ObsPossibleTypeList.add("None");		
		obsParamNames.put("None", Observer.ParamNames);
		obsParamDefaultValues.put("None", Observer.DefaultValues);

		
		c=DirectObserver.class;
		pn=DirectObserver.ParamNames;		
		pnd=DirectObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);
		
		c=GlobalObserver.class;
		pn=GlobalObserver.ParamNames;		
		pnd=GlobalObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);

		c=LastObserver.class;
		pn=LastObserver.ParamNames;		
		pnd=LastObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);

		c=SlidingMeanObserver.class;
		pn=SlidingMeanObserver.ParamNames;		
		pnd=SlidingMeanObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);
		
		c=InitParamObserver.class;
		pn=InitParamObserver.ParamNames;		
		pnd=InitParamObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);
		
		c=GraphObserver.class;
		pn=GraphObserver.ParamNames;		
		pnd=GraphObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);
		
		c=GraphDistCalcObserver.class;
		pn=GraphDistCalcObserver.ParamNames;		
		pnd=GraphDistCalcObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);
		
		c=BinObserver.class;
		pn=BinObserver.ParamNames;		
		pnd=BinObserver.DefaultValues;		
		ObsPossibleTypeList.add(c.getName());		
		obsParamNames.put(c.getName(), pn);
		obsParamDefaultValues.put(c.getName(), pnd);
		
		restartnetlogo=false;
//		setJMenuBar(menubar);
		menup.add(micp);
		menup.add(miop);
		micp.addActionListener(this);
		miop.addActionListener(this);
		menug.add(mignl);
		menug.add(migrl);
		mignl.addActionListener(this);
		migrl.addActionListener(this);
		menus.add(mist);	
		mist.addActionListener(this);
		menubar.add(menup);
		menubar.add(menug);
		menubar.add(menus);
		menug.setEnabled(false);
		menus.setEnabled(false);
		
		tabbedpane=new JTabbedPane();
		
		
/*		getContentPane().setLayout(gb);

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gb.setConstraints(tabbedpane,gbc);*/
		getContentPane().add(tabbedpane);
		this.setResizable(true);

		JPanel intropanel=new JPanel();

		JPanel projectpanel=new JPanel();

		JPanel temppanel=new JPanel();
		JTextField jtfinconst=new JTextField("In construction...");
		temppanel.add(jtfinconst);
		
//		tabbedpane.setTabPlacement(tabbedpane.VERTICAL,tabbedpane.LEFT);
//		tabbedpane.setTabLayoutPolicy(tabbedpane.VERTICAL);
		
		this.clusterspane=new JTabbedPane();

		
		tabbedpane.addTab("Presetation", intropanel);
		tabbedpane.addTab(tabnameproject, projectpanel);
		tabbedpane.addTab(tabnameobserver, new JPanel());
		tabbedpane.addTab(tabnameclusters, clusterspane);
		tabbedpane.addTab(tabnameoverview, new JPanel());
		tabbedpane.addTab(tabnameoverviewsort, new JPanel());
		
		jbcreateproject=new JButton("Create new Project");
		jbloadproject=new JButton("Load Project");
		jbloadresults=new JButton("Load Results");
		
		intropanel.add(jbcreateproject);
		intropanel.add(jbloadproject);
		intropanel.add(jbloadresults);
		
		jbcreateproject.addActionListener(this);
		jbloadproject.addActionListener(this);
		jbloadresults.addActionListener(this);
		
		
		
/*		JTextField [] jtf = new JTextField[columnCount]; 
		JCheckBox [] cb = new JCheckBox[columnCount];
		Choice [] c1 = new Choice[columnCount];
	    Choice [] c2 = new Choice[columnCount];

		
		JLabel l1 = new JLabel("variables:");
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
		gb.setConstraints(l1,gbc);
		getContentPane().add(l1);
		
		int count=1;
		for(int i=0;i<columnCount;i++)
		{
			jtf[i] = new JTextField();
			jtf[i].setEditable(false);
			gbc.gridx=count++;
			gbc.gridy=0;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.fill=GridBagConstraints.HORIZONTAL;
			gb.setConstraints(jtf[i],gbc);
			getContentPane().add(jtf[i]);
			vjf.addElement(jtf[i]);
			jtf[i].setEnabled(false);
		}  
		JLabel l2 = new JLabel("Dissociate:");
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
		gb.setConstraints(l2,gbc);
		getContentPane().add(l2);
		count=1;
		for(int i=0;i<columnCount;i++)
		{
			cb[i] = new JCheckBox();
	//		cb[i].setEnabled(true);
	//		cb[i].setSelected(false);
			gbc.gridx=count++;
			gbc.gridy=1;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gb.setConstraints(cb[i],gbc);
			getContentPane().add(cb[i]);
			cb[i].addActionListener(this);
			vcb.addElement(cb[i]);
			cb[i].setEnabled(false);
		}
		JLabel l3 = new JLabel("Agregate:");
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
		gb.setConstraints(l3,gbc);
		getContentPane().add(l3);
		String[] v1 = {"","Average","Maximum","Minimum","Standard deviation"};
		count=1;
		for(int i=0;i<columnCount;i++)
		{
			c1[i] = new Choice();
			for(int j=0;j<v1.length;j++)
				c1[i].add(v1[j]);
			gbc.gridx=count++;
			gbc.gridy=2;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
		//	gbc.fill=GridBagConstraints.HORIZONTAL;
			gb.setConstraints(c1[i],gbc);
			getContentPane().add(c1[i]);
			vc1.addElement(c1[i]);
			c1[i].setEnabled(false);
		} 
		JLabel l4 = new JLabel("Source:");
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
		gb.setConstraints(l4,gbc);
		getContentPane().add(l4);
		count=1;
		for(int i=0;i<columnCount;i++)
		{
			c2[i] = new Choice();
			gbc.gridx=count++;
			gbc.gridy=3;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gb.setConstraints(c2[i],gbc);
			getContentPane().add(c2[i]);
			c2[i].addItemListener(this);	
	    	vc2.addElement(c2[i]);
			c2[i].setEnabled(false);
		}  
		jbt1 = new JButton("Add a column");
		gbc.gridx=0;
		gbc.gridy=4;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gb.setConstraints(jbt1,gbc);
		getContentPane().add(jbt1);
		jbt1.addActionListener(this);
		jbt2 = new JButton("Delete a column");
		gbc.gridx=2;
		gbc.gridy=4;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gb.setConstraints(jbt2,gbc);
		getContentPane().add(jbt2);
		jbt2.addActionListener(this);
		jbt3 = new JButton("Save to file");
		gbc.gridx=0;
		gbc.gridy=5;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gb.setConstraints(jbt3,gbc);
		getContentPane().add(jbt3);
		jbt3.addActionListener(this);
		jbt4 = new JButton("Load from file");
		gbc.gridx=2;
		gbc.gridy=5;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gb.setConstraints(jbt4,gbc);
		getContentPane().add(jbt4);
		jbt4.addActionListener(this);
		jbt5 = new JButton("Generate");
		gbc.gridx=0;
		gbc.gridy=6;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gb.setConstraints(jbt5,gbc);
		getContentPane().add(jbt5);
		jbt5.addActionListener(this);
		jbt1.setEnabled(false);
		jbt2.setEnabled(false);
		jbt3.setEnabled(false);
		jbt4.setEnabled(false);
		jbt5.setEnabled(false);*/
	}
	
	public static Clusterer newClusterer() throws Exception
	{
    	int minClustersNumber = 1;
		int maxClustersNumber = 20;
		int maxIterations = 200;
		Clusterer cl=new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
				, SimAnalyzer.clustererParameters.clone() );
		if (SimAnalyzer.clustererType==1)
		{
			 cl=new WekaClusterer(WekaClusterer.WekaClustererType.SimpleKMeans, false
					, SimAnalyzer.clustererParameters.clone() );
		}
		if (SimAnalyzer.clustererType==2)
		{
			 cl=new WekaClusterer(WekaClusterer.WekaClustererType.DBScan, false
					, SimAnalyzer.clustererParameters.clone() );
		}
		if (SimAnalyzer.clustererType==3)
		{
			 cl=new WekaClusterer(WekaClusterer.WekaClustererType.MakeDensityBasedClusterer, false
					, SimAnalyzer.clustererParameters.clone() );
		}
		return cl;
	}

	public synchronized static void waits() throws Exception
	{
		Thread t=Thread.currentThread();
		while (true)
		{
			synchronized (t)
			{
			t.wait(100);
			}
  		    System.out.print("");
		    if (startnetlogo)
		    {
		    	startnetlogo=false;
		    	int minClustersNumber = 1;
				int maxClustersNumber = 20;
				int maxIterations = 200;
//				WekaClusterer clusterer = new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
//						, "-L", ""+minClustersNumber, "-H", ""+maxClustersNumber, "-I", ""+maxIterations );
	       	    for(int i=0;i<=totalsteps;i++)
	       	    {
		    		if (SimAnalyzer.clustererType==0)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
		    		if (SimAnalyzer.clustererType==1)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.SimpleKMeans, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
		    		if (SimAnalyzer.clustererType==2)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.DBScan, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
		    		if (SimAnalyzer.clustererType==3)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
	       	    }
				params = NetLogoSimulationController.getDefaultParams();
		        params[NetLogoSimulationController.CLUSTERER_INDEX]=wcl;
		        params[NetLogoSimulationController.AGENT_TYPE_INDEX]="Turtles";
		        params[NetLogoSimulationController.MAX_TICKS_INDEX]=totalsteps;
		        params[NetLogoSimulationController.TICKS_BETWEEN_CLUSTERING_INDEX]=clusterstep;
				params[NetLogoSimulationController.MODEL_FILE_NAME_INDEX]=name;
				params[NetLogoSimulationController.SETUP_PROCEDURE_INDEX]="Setup";
				params[NetLogoSimulationController.UPDATE_PROCEDURE_INDEX]="update";
				params[NetLogoSimulationController.VARIANCE_REFRESH_INDEX]=20;
				params[NetLogoSimulationController.IDCOL_INDEX]=agcol;
				params[NetLogoSimulationController.TIMECOL_INDEX]=timecol;
				params[NetLogoSimulationController.STARTCLUSTCOL_INDEX]=startcol;
				params[NetLogoSimulationController.ENDCLUSTCOL_INDEX]=endcol;
		        nlsc = new NetLogoSimulationController();
		        
	            try {
	            	nlsc.runSimulation(params);
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }	   
/*
		        FileReader fr = null;
				try {
					fr = new FileReader("projects/"+prname+".config");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedReader br = new BufferedReader(fr);	
				String l1 = br.readLine();
				String l2 = br.readLine();
				String l3 = br.readLine();
		        FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+prname+".config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					bw.write(l1);
					bw.newLine();
					bw.write(l2);
					bw.newLine();
					bw.write(l3);
					bw.newLine();
					for(int i=0;i<NetLogoSimulationController.DataMatrix.getColumnCount();i++)
					{
						bw.write(NetLogoSimulationController.DataMatrix.getColumnLabel(i));
						bw.write("  ");
					}
					bw.newLine();
					for(int i=0;i<NetLogoSimulationController.DataMatrix.getColumnCount();i++)
					{
						boolean res = true;
                        try{
                            Double.parseDouble(NetLogoSimulationController.DataMatrix.getAsString(0,i));
                        }catch(Exception   ex){
                            res   =   false;
                        }
					    if(res)
					    	bw.write("double ");
					    else 
					        bw.write("string ");
					}
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
		        startnetlogo=false;
		     
		    }
		    if (restartnetlogo)
		    {
	            try {
	            	nlsc.reRunBoucle(params);	
	            	
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }	   
		        restartnetlogo=false;
		    	
		    }

		    if (startlogs)
		    {
		    	int minClustersNumber = 1;
				int maxClustersNumber = 10;
				int maxIterations = 200;
//				WekaClusterer clusterer = new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
//						, "-L", ""+minClustersNumber, "-H", ""+maxClustersNumber, "-I", ""+maxIterations );
	       	  
				for(int i=0;i<=totalsteps;i++)
	       	    {
		    		if (SimAnalyzer.clustererType==0)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
		    		if (SimAnalyzer.clustererType==1)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.SimpleKMeans, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
		    		if (SimAnalyzer.clustererType==2)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.DBScan, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
		    		if (SimAnalyzer.clustererType==3)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
								, SimAnalyzer.clustererParameters.clone()));
		    		}
	       	    }
				params = LogsSimulationController.getDefaultParams();
		        params[LogsSimulationController.CLUSTERER_INDEX]=wcl;
		        params[LogsSimulationController.AGENT_TYPE_INDEX]="Turtles";
		        params[LogsSimulationController.MAX_TICKS_INDEX]=totalsteps;
		        params[LogsSimulationController.TICKS_BETWEEN_CLUSTERING_INDEX]=clusterstep;
				params[LogsSimulationController.MODEL_FILE_NAME_INDEX]=name;
				params[LogsSimulationController.SETUP_PROCEDURE_INDEX]="Setup";
				params[LogsSimulationController.UPDATE_PROCEDURE_INDEX]="update";
				params[LogsSimulationController.VARIANCE_REFRESH_INDEX]=20;
				params[LogsSimulationController.IDCOL_INDEX]=agcol;
				params[LogsSimulationController.TIMECOL_INDEX]=timecol;
				params[LogsSimulationController.STARTCLUSTCOL_INDEX]=startcol;
				params[LogsSimulationController.ENDCLUSTCOL_INDEX]=endcol;
		        nlsc = new LogsSimulationController();
/*		        for(int i=0; i<=totalsteps; i++)
		        {
		    		if (SimAnalyzer.clustererType==0)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
								, SimAnalyzer.clustererParameters));
		    		}
		    		if (SimAnalyzer.clustererType==1)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.SimpleKMeans, false
								, SimAnalyzer.clustererParameters));
		    		}
		    		if (SimAnalyzer.clustererType==2)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.DBScan, false
								, SimAnalyzer.clustererParameters));
		    		}
		    		if (SimAnalyzer.clustererType==3)
		    		{
			        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
								, SimAnalyzer.clustererParameters));
		    		}
		        }
*/		        
	            try {
	            	nlsc.runSimulation(params);
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }	   
				/*
		        FileReader fr = null;
				try {
					fr = new FileReader("projects/"+prname+".config");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedReader br = new BufferedReader(fr);	
				String l1 = br.readLine();
				String l2 = br.readLine();
				String l3 = br.readLine();
		        FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+prname+".config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					bw.write(l1);
					bw.newLine();
					bw.write(l2);
					bw.newLine();
					bw.write(l3);
					bw.newLine();
					for(int i=0;i<SimulationController.DataMatrix.getColumnCount();i++)
					{
						bw.write(SimulationController.DataMatrix.getColumnLabel(i));
						bw.write("  ");
					}
					bw.newLine();
					for(int i=0;i<SimulationController.DataMatrix.getColumnCount();i++)
					{
						boolean res = true;
                        try{
                            Double.parseDouble(SimulationController.DataMatrix.getAsString(0,i));
                        }catch(Exception   ex){
                            res   =   false;
                        }
					    if(res)
					    	bw.write("double ");
					    else 
					        bw.write("string ");
					}
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
		        startlogs=false;
		     
		    }
		}
	}
	
	public void itemStateChanged(ItemEvent e)
    { 
		Object item = e.getItem();
		Object src = e.getSource();			
		for(int i=0;i<columnCount;i++)
		{
			if(src.equals(vc2.elementAt(i)))
			{
				JTextField jf = new JTextField(); 
                jf = (JTextField) vjf.elementAt(i);
                if(i==0)
                	jf.setText(item.toString());
                else
                {
                	int n=1;
                    for(int j=0;j<i;j++)
                    {
                    	Choice ch = new Choice(); 
                        ch = (Choice) vc2.elementAt(j);
                        if(ch.getSelectedItem().equals(item.toString()))
                        {
                        	++n;
                        	jf.setText(item.toString()+n);
                        }           
                    }
                    if(n==1)
                    	jf.setText(item.toString());
                }
			}	
		}		
    }

	@SuppressWarnings("unused")
	public synchronized void actionPerformed(ActionEvent evt)
	{
		Object src = evt.getSource();
		if (src.equals(jbcreateproject))
		{	
			CreateProject p = new CreateProject();
			p.setLocation(500,100);
		    p.pack() ;
		    p.setVisible(true);
		}
		if (src.equals(jbloadproject))
		{
			OpenProject p = new OpenProject();

		}
		if (src.equals(this.jbloadresults))
		{
			JFileChooser fileChooser = new JFileChooser("savedlogs/");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("Choose saved results");
			int ret = fileChooser.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				System.out.println(fileChooser.getSelectedFile().getName());
				System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
				String filename = fileChooser.getName(fileChooser.getSelectedFile());
				String pathname = fileChooser.getSelectedFile().getAbsolutePath();
				try {
/*					if (clbase.nbotherxp==0)
					{
						clbase.nbotherxp++;
						clbase.havglobsm.add(clbase.nbotherxp-1, clbase.avglobsm);
						clbase.hvtestsm.add(clbase.nbotherxp-1, clbase.vtestsm);
						clbase.havgsm.add(clbase.nbotherxp-1, clbase.avgsm);						
					}*/
					Clusterer cl=null;
					try {
						cl = SimAnalyzer.newClusterer();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String nomf = new String(pathname+"/avglobsm.csv");
			//		Matrix nm=clbase.avglobsm.clone();
					Matrix nm=MatrixFactory.importFromFile(nomf);
					Matrix virtualdata=nm.subMatrix(Ret.NEW, 3, 0, nm.getRowCount()-1, nm.getColumnCount()-1).transpose();

					ArrayList<Long> nc=new ArrayList<Long>();
					Matrix tempm=Vtest.mfact.zeros(0, 0);
					Cluster clbase=new Cluster(cl,new Long(0),tempm,nc);
					AgModel agm=new AgModel(cl,clbase);

					Matrix nomc;
					nomf = new String(pathname+"/colnoms.csv");
			//		Matrix nm=clbase.avglobsm.clone();
					nomc=MatrixFactory.importFromFile(nomf);

					
					nomf = new String(pathname+"/avglobsm.csv");
			//		Matrix nm=clbase.avglobsm.clone();
					nm=MatrixFactory.importFromFile(nomf);
					clbase.avglobsm=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.avglobsm.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.avglobsm.setRowLabel(i, nomc.getAsString(i,0));
					nomf = new String(pathname+"/vtestsm.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.vtestsm=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.vtestsm.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.vtestsm.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.vtestsm= (DenseDoubleMatrix2D)((DefaultDenseStringMatrix2D)nm).toDoubleMatrix();
//					clbase.vtestsm=(DenseDoubleMatrix2D) nm;
					nomf = new String(pathname+"/avgsm.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.avgsm=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.avgsm.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.avgsm.setRowLabel(i, nomc.getAsString(i,0));
					nomf = new String(pathname+"/vtestsmdef.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.vtestsmdef=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.vtestsmdef.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.vtestsmdef.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.vtestsm= (DenseDoubleMatrix2D)((DefaultDenseStringMatrix2D)nm).toDoubleMatrix();
//					clbase.vtestsm=(DenseDoubleMatrix2D) nm;
					nomf = new String(pathname+"/avgsmdef.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.avgsmdef=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.avgsmdef.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.avgsmdef.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.avgsm=(DenseDoubleMatrix2D)((DefaultDenseStringMatrix2D)nm).toDoubleMatrix();
//					clbase.avgsm=(DenseDoubleMatrix2D) nm;
					nomf = new String(pathname+"/stderrsm.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.stderrsm=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.stderrsm.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.stderrsm.setRowLabel(i, nomc.getAsString(i,0));
					nomf = new String(pathname+"/stderrsmdef.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.stderrsmdef=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.stderrsmdef.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.stderrsmdef.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.stderrsm=(DenseDoubleMatrix2D)((DefaultDenseStringMatrix2D)nm).toDoubleMatrix();
//					clbase.stderrsm=(DenseDoubleMatrix2D) nm;
					nomf = new String(pathname+"/stdglobsm.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.stdglobsm=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.stdglobsm.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.stdglobsm.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.stdglobsm=(DenseDoubleMatrix2D)((DefaultDenseStringMatrix2D)nm).toDoubleMatrix();
//					clbase.stdglobsm=(DenseDoubleMatrix2D) nm;

					nomf = new String(pathname+"/distribparam.csv");
					nm=MatrixFactory.importFromFile(nomf);
					clbase.distribparams=Vtest.mfact.zeros(nm.getRowCount(), nm.getColumnCount());
					for (int i=0; i<nm.getRowCount(); i++)
						for (int j=0; j<nm.getColumnCount(); j++)
							clbase.distribparams.setAsDouble(nm.getAsDouble(i,j), i,j);
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.distribparams.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.distribparams=(DenseDoubleMatrix2D)((DefaultDenseStringMatrix2D)nm).toDoubleMatrix();
//					clbase.distribparams= (DenseDoubleMatrix2D) nm;
					Matrix nd=nm;
					nomf = new String(pathname+"/davgsm.ser");
					nm=MatrixFactory.importFromFile(FileFormat.SER,nomf);
//					nm.showGUI();
					clbase.davgsm=nm;
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.davgsm.setRowLabel(i, nomc.getAsString(i,0));
					nomf = new String(pathname+"/davglobsm.ser");
					nm=MatrixFactory.importFromFile(FileFormat.SER,nomf);
					clbase.davglobsm=nm;
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.davglobsm.setRowLabel(i, nomc.getAsString(i,0));
					nomf = new String(pathname+"/davgsmdef.ser");
					nm=MatrixFactory.importFromFile(FileFormat.SER,nomf);
					clbase.davgsmdef=nm;
					for (int i=0; i<nm.getRowCount(); i++)
						clbase.davgsmdef.setRowLabel(i, nomc.getAsString(i,0));
//					clbase.avglobsm.showGUI();			
					 virtualdata=clbase.avglobsm.subMatrix(Ret.NEW, 3, 0, clbase.avglobsm.getRowCount()-1, clbase.avglobsm.getColumnCount()-1).transpose();
					SimulationController.updateVariableInfo(virtualdata);
					SimAnalyzer.vtquali=false;
					
	    			FAgModel fag=new FAgModel(agm);
					
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}

		}
		if (src.equals(mignl))
		{
		   ShowProject f = new ShowProject(1);
//		   f.setLocation(500,100);
//	       f.pack() ;
//		   f.setVisible(true);
		}
		if (src.equals(migrl))
		{
			   ShowProject f = new ShowProject(2);
//		   f.setLocation(500,100);
//	       f.pack() ;
//		   f.setVisible(true);
		}
		if (src.equals(mist))
		{
			SaveData s = new SaveData();
			 s.setLocation(500,100);
		     s.pack() ;
			 s.setVisible(true);
		}
		if (src.equals(jbt1))
		{
			++columnCount;
			JTextField jf = new JTextField();
			jf.setEditable(false);
			gbc.gridx=columnCount;
			gbc.gridy=0;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.fill=GridBagConstraints.HORIZONTAL;
			gb.setConstraints(jf,gbc);
			getContentPane().add(jf); 
			vjf.addElement(jf);
			JCheckBox jcb = new JCheckBox();
			gbc.gridx=columnCount;
			gbc.gridy=1;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gb.setConstraints(jcb,gbc);
			getContentPane().add(jcb);
			jcb.addActionListener(this);	
			vcb.addElement(jcb);
			
			String[] v1 = {"","Average","Maximum","Minimum","Standard deviation"};
			Choice ch1 = new Choice();
			for(int j=0;j<v1.length;j++)
				ch1.add(v1[j]);
			gbc.gridx=columnCount;
			gbc.gridy=2;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gb.setConstraints(ch1,gbc);
			getContentPane().add(ch1);
			vc1.addElement(ch1);
			Choice ch2 = new Choice();
			FileReader fr = null;
			try {
				fr = new FileReader("projects/"+prname+".config");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);	
			String l = null;
			for(int j=0;j<4;j++)
				try {
					l = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
			}
			ch2.add(" ");
			String[] v2 = l.split("  ");
			for(int j=0;j<v2.length;j++)
				ch2.add(v2[j]);	
			gbc.gridx=columnCount;
			gbc.gridy=3;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gb.setConstraints(ch2,gbc);
			getContentPane().add(ch2);
			ch2.addItemListener(this);	
			vc2.addElement(ch2);
			this.pack();
		}
		if (src.equals(jbt2))
		{
			--columnCount;
			getContentPane().remove((Component)vjf.elementAt(columnCount));
			vjf.removeElementAt(columnCount);
			getContentPane().remove((Component)vc1.elementAt(columnCount));
			vc1.removeElementAt(columnCount);
			getContentPane().remove((Component)vc2.elementAt(columnCount));
			vc2.removeElementAt(columnCount);
			getContentPane().remove((Component)vcb.elementAt(columnCount));
			vcb.removeElementAt(columnCount);
			this.pack();
		}
		if (src.equals(jbt3))
		{
			File variableFile = new File("variables/"+prname+".txt");
			FileWriter fw = null;
			try {
				variableFile.createNewFile();				
				fw = new FileWriter(variableFile, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedWriter bw = new BufferedWriter(fw);
			try {
				for(int i=0;i<columnCount;i++)
				{
					JTextField jf = new JTextField();
					jf = (JTextField) vjf.elementAt(i);
				    bw.write(jf.getText()+"  ");
				}
				bw.newLine();
				for(int i=0;i<columnCount;i++)
				{
					JCheckBox jcb = new JCheckBox();
					jcb = (JCheckBox) vcb.elementAt(i);
					if(jcb.isSelected())
						bw.write("true"+"  ");
					else
						bw.write("false"+"  ");
				}
				bw.newLine();
				for(int i=0;i<columnCount;i++)
				{
					Choice jc1 = new Choice();
					jc1 = (Choice) vc1.elementAt(i);
					if(jc1.getSelectedItem().isEmpty())
						bw.write("empty  ");
					else
				        bw.write(jc1.getSelectedItem()+"  ");
				}
				bw.newLine();
				for(int i=0;i<columnCount;i++)
				{
					Choice jc2 = new Choice();
					jc2 = (Choice) vc2.elementAt(i);
					if(jc2.getSelectedItem().isEmpty())
						bw.write("empty  ");
					else
				        bw.write(jc2.getSelectedItem()+"  ");
				}
				bw.newLine();
				bw.flush(); 
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		  
		}
		if (src.equals(jbt4))
		{
			FileReader fr = null;
			try {
				fr = new FileReader("variables/"+prname+".txt");
				BufferedReader br = new BufferedReader(fr);	
				String s1 = br.readLine();
				String s2 = br.readLine();
				String s3 = br.readLine();
				String s4 = br.readLine();
				int columnCountOld = columnCount;
				columnCount = s1.split("  ").length;
				if (columnCount<columnCountOld)
				{
					for(int i=columnCountOld-1;i>=columnCount;i--)
					{
						getContentPane().remove((Component)vjf.elementAt(i));
						vjf.removeElementAt(i);
						getContentPane().remove((Component)vc1.elementAt(i));
						vc1.removeElementAt(i);
						getContentPane().remove((Component)vc2.elementAt(i));
						vc2.removeElementAt(i);
						getContentPane().remove((Component)vcb.elementAt(i));
						vcb.removeElementAt(i);
						this.pack();
					}		
				}
				if (columnCount>columnCountOld)
				{
					for(int i=columnCountOld;i<columnCount;i++)
					{
						JTextField jf = new JTextField();
						jf.setEditable(false);
						gbc.gridx=i+1;
						gbc.gridy=0;
						gbc.gridwidth=1;
						gbc.gridheight=1;
						gbc.weightx=10;
						gbc.weighty=10;
						gbc.fill=GridBagConstraints.HORIZONTAL;
						gb.setConstraints(jf,gbc);
						getContentPane().add(jf); 
						vjf.addElement(jf);
						JCheckBox jcb = new JCheckBox();
						gbc.gridx=i+1;
						gbc.gridy=1;
						gbc.gridwidth=1;
						gbc.gridheight=1;
						gbc.weightx=10;
						gbc.weighty=10;
						gb.setConstraints(jcb,gbc);
						getContentPane().add(jcb);
						jcb.addActionListener(this);	
						vcb.addElement(jcb);
						String[] v1 = {"","Average","Maximum","Minimum","Standard deviation"};
						Choice ch1 = new Choice();
						for(int j=0;j<v1.length;j++)
							ch1.add(v1[j]);
						gbc.gridx=i+1;
						gbc.gridy=2;
						gbc.gridwidth=1;
						gbc.gridheight=1;
						gbc.weightx=10;
						gbc.weighty=10;
						gb.setConstraints(ch1,gbc);
						getContentPane().add(ch1);
						vc1.addElement(ch1);						
						Choice ch2 = new Choice();
						FileReader fr2 = null;
						try {
							fr2 = new FileReader("projects/"+prname+".config");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						BufferedReader br2 = new BufferedReader(fr2);	
						String l = null;
						for(int j=0;j<4;j++)
							try {
								l = br2.readLine();
							} catch (IOException e) {
								e.printStackTrace();
						}
						ch2.add("");
						String[] v2 = l.split("  ");
						for(int j=0;j<v2.length;j++)
							ch2.add(v2[j]);	
						gbc.gridx=i+1;
						gbc.gridy=3;
						gbc.gridwidth=1;
						gbc.gridheight=1;
						gbc.weightx=10;
						gbc.weighty=10;
						gb.setConstraints(ch2,gbc);
						getContentPane().add(ch2);
						ch2.addItemListener(this);	
						vc2.addElement(ch2);
						this.pack();
					}		
				}		
				for(int i=0;i<columnCount;i++)
				{
					JTextField jf = new JTextField(); 
	                jf = (JTextField) vjf.elementAt(i);		                
	                jf.setText(s1.split("  ")[i].toString());
					JCheckBox jcb = new JCheckBox();
					jcb = (JCheckBox) vcb.elementAt(i);
					if(s2.split("  ")[i].equals("true"))
					{	
						jcb.setSelected(true);
					}
					Choice jc1 = new Choice();
					jc1 = (Choice) vc1.elementAt(i);
					if(s3.split("  ")[i]=="empty")
						jc1.select("");
					else
				        jc1.select(s3.split("  ")[i]);
				    Choice jc2 = new Choice();
					jc2 = (Choice) vc2.elementAt(i);
					if(s4.split("  ")[i]=="empty")
						jc2.select("");
					else
				        jc2.select(s4.split("  ")[i]);				
				}			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (src.equals(jbt5))
		{
			Connection conn;
			Statement stmt;
			String sql;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/netlogo",
				                                     "root", "123");
				stmt = (Statement) conn.createStatement();
				sql="drop table if exists "+prname+"_reprocess";
                stmt.executeUpdate(sql);
                String [] type = new String[columnCount];
				String s1 = null,s2 = null,s3 = null;
				String [] sdis = new String[columnCount];
				int [] ndis = new int[columnCount];
				int len=0;
				for(int i=0;i<columnCount;i++)
				{
					JTextField jf = new JTextField(); 
	                jf = (JTextField) vjf.elementAt(i);
	                Choice ch1 = new Choice();
	                ch1 = (Choice) vc1.elementAt(i);
	                Choice ch2 = new Choice();
	                ch2 =(Choice) vc2.elementAt(i);
	                String chgs = ch2.getSelectedItem();
	                FileReader fr = null;
					try {
						fr = new FileReader("projects/"+prname+".config");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	                BufferedReader br = new BufferedReader(fr);
	                String l1 = null, l2 = null;
					try {
						for(int j=0; j<4; j++)
							l1 = br.readLine();
						l2 = br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String [] sva = l1.split("  ");
					String [] sta = l2.split(" ");
					for(int j=0;j<sva.length;j++)
					{
						if(chgs.equals(sva[j]))
						{
							if(sta[j].equals("double"))
								type[i] = sta[j];
							else
								type[i] = "varchar(1000)";
						}
					}
	                String ss = ch2.getSelectedItem();
	                if(ch1.getSelectedItem().equals("Average"))
	                	ss = "avg("+ss+")";
	                if(ch1.getSelectedItem().equals("Maximum"))
	                	ss = "max("+ss+")";
	                if(ch1.getSelectedItem().equals("Minimum"))
	                	ss = "min("+ss+")";
	                if(ch1.getSelectedItem().equals("Standard deviation"))
	                	ss = "stddev("+ss+")";
	                JCheckBox jcb = new JCheckBox();
	                jcb = (JCheckBox) vcb.elementAt(i);
	                if(jcb.isSelected())
	                {
	                	sdis[len] = chgs;
	                	ndis[len] = i;
	                	len++;
	                }
	                
	                String sjf = jf.getText();
	                sjf = sjf.replaceAll("/", "_");
	                sjf = sjf.replaceAll(" ", "_");
                    sjf = sjf.replaceAll("-","_");
                    sjf = sjf.replaceAll("\\?","");
                    ss = ss.replaceAll("/", "_");
                    ss = ss.replaceAll(" ", "_");
                    ss = ss.replaceAll("-","_");
                    ss = ss.replaceAll("\\?","");
	                if(i==0)
	                {
	                	if(columnCount!=1)
	                	{
	                		s1 = sjf+" "+type[i]+",";
	                        s2 = ss+",";
	                	}
	                	else
	                	{
	                		s1 = sjf+" "+type[i];
	                	    s2 = ss;
	                	}
	                }
	                else if(i!=columnCount-1 && i!=0)
	                {
	                	s1 += sjf+" "+type[i]+",";
	                	s2 += ss+",";
	                }
	                else
	                {
	                	s1 += sjf+" "+type[i];
	                	s2 += ss;
	                }
	                
				}	
				sql="create table "+prname+"_reprocess ("+s1+")";
				stmt.executeUpdate(sql);
        		if(len==0)
        			sql="insert into "+prname+"_reprocess select "+s2+" from "
                           +prname;
        		else
        		{
        			for(int i=0;i<len;i++)
        			{       			
            			Choice ch2 = new Choice();
    	                ch2 =(Choice) vc2.elementAt(ndis[i]);
    	                String chgs = ch2.getSelectedItem();
        				if(i==0)
        				{
        					if(len!=1)
        						s3 = chgs+",";
    	                	else
    	                		s3 = chgs;        					
        				}
        				else if(i!=len-1 && i!=0)
    	                {
        					s3 += chgs+",";
    	                }
    	                else
    	                {
    	                	s3 += chgs;
    	                }
        			}
        			s3 = s3.replaceAll(" ", "_");
                    s3 = s3.replaceAll("-","_");
                    s3 = s3.replaceAll("\\?","");
        			sql="insert into "+prname+"_reprocess select "+s2+" from "
                         +prname+" group by "+s3;
        		}
                stmt.executeUpdate(sql);
                Matrix m = MatrixFactory.importFromJDBC(DB.MySQL,"localhost",3306,"netlogo",
                		"select * from "+prname+"_reprocess","root","123");
                m.showGUI();				
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (MatrixException e2) {
				e2.printStackTrace();
			}
		}
	}
	
	class CreateProject extends JFrame implements ActionListener
	{	
		private static final long serialVersionUID = 1L;
		
		JLabel l;
		JTextField tf;
		JButton jbt;
		public CreateProject()
		{
			setTitle("Create a new project");  
			setSize(500,350); 
	//		setLocation(200, 150);   
			setResizable(true);    
			JPanel panel = new JPanel();  
			FlowLayout flowLayout = new FlowLayout();  
		    flowLayout.setHgap(10);  
			flowLayout.setVgap(10);  
			panel.setLayout(flowLayout);  
			add(panel, BorderLayout.CENTER); 
			JPanel panel_1 = new JPanel();  
			GridLayout grid = new GridLayout(0, 2);  
			grid.setHgap(10);  
			grid.setVgap(15);  
			panel_1.setLayout(grid);  
			panel.add(panel_1);  
		    l = new JLabel("project name: "); 
		    panel_1.add(l);  
			tf = new JTextField();  
			panel_1.add(tf); 
			jbt = new JButton("create project");   
			panel_1.add(jbt);   
			jbt.addActionListener(this);
			setVisible(true); 
		    
		}
		public synchronized void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			if (src.equals(jbt))
			{
				File project = new File("projects/"+tf.getText()+".config");
				boolean bfile = project.exists();
				if (bfile)
					System.out.println("The project exists. Please create another one!");
				else 
				{
					try {
						project.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					dispose();
					menug.setEnabled(true);
					prname = tf.getText();
				}
			}
			
		}
		
	}
	
	class OpenProject extends JFrame 
	{
		private static final long serialVersionUID = 1L;

		public OpenProject() {
			JFileChooser fileChooser = new JFileChooser("projects/");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setDialogTitle("Open Project");
			int ret = fileChooser.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
				String filename = fileChooser.getName(fileChooser.getSelectedFile());
				prname = filename.split("\\.")[0];
				ShowProject sp = new ShowProject();
				SimAnalyzer.tabbedpane.removeTabAt(tabidproject);
				SimAnalyzer.tabbedpane.insertTab(tabnameproject, tabicproject,sp,tabttproject,tabidproject);
				SimAnalyzer.tabbedpane.setSelectedIndex(tabidproject);
				SimAnalyzer.simanal.pack();
				
			}
	    }
	}
	
	class ObserversParameters extends JFrame implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		int nbobsmax=Observer.nbparammax;
		int idobs;
		ObserversConfig parent;
		JLabel[] l;//, l0,lt,  l1, l2,l2b, l3, l4, l5,  l6,  l7,l8,l9,l10;
		JTextField[] ltf; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
//		JButton[] jbparams; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
//		JComboBox<String>[] lcombo; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
		JButton jbtOK, jbtcancel,jbtdefault;
		public ObserversParameters(ObserversConfig par, int id) 
		{
			parent=par;
			idobs=id;
			params=new String[nbobsmax][Observer.nbparammax];
			setTitle("Observer "+id+" parameters");  
			setSize(500,350); 
			setResizable(true);    
			JPanel panel = new JPanel();  
			FlowLayout flowLayout = new FlowLayout();  
		    flowLayout.setHgap(10);  
			flowLayout.setVgap(10);  
			panel.setLayout(flowLayout);  
			add(panel, BorderLayout.CENTER); 
			JPanel panel_1 = new JPanel();  
			GridLayout grid = new GridLayout(0, 2);  
			grid.setHgap(10);  
			grid.setVgap(15);  
			panel_1.setLayout(grid);  
			panel.add(panel_1);  
			l=new JLabel[nbobsmax];
			ltf=new JTextField[nbobsmax];

			for (int i=0; i<nbobsmax; i++)
			{
				l[i]=new JLabel(""+SimAnalyzer.obsParamNames.get(par.lcombo[idobs].getSelectedItem().toString())[i]);
//				try {
//					l[i]=new JLabel(""+((Observer)(Class.forName(par.lcombo[idobs].getSelectedItem().toString()).newInstance())).ParamNames[i]);
//				} catch (InstantiationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				ltf[i]=new JTextField(parent.params[idobs][i]);
				panel_1.add(l[i]);
				panel_1.add(ltf[i]);
			}
			l[0].setEnabled(false);
			l[1].setEnabled(false);

			jbtOK = new JButton("Ok");   
			panel_1.add(jbtOK);   
			jbtOK.addActionListener(this);
			jbtcancel = new JButton("Cancel");   
			panel_1.add(jbtcancel);   
			jbtcancel.addActionListener(this);
			jbtdefault = new JButton("Default Values");   
			panel_1.add(jbtdefault);   
			jbtdefault.addActionListener(this);
			pack();
			setVisible(true);  			
			
		}

		public void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			@SuppressWarnings("unused")
			Matrix m = NetLogoSimulationController.DataMatrix;
			if (src.equals(jbtcancel))
			{
				dispose();
			}
			if (src.equals(jbtOK))
			{
				dispose();
				for (int i=0; i<nbobsmax; i++)
				{
					parent.params[idobs][i]=ltf[i].getText();
				}
			}
			if (src.equals(jbtdefault))
			{
				for (int i=0; i<nbobsmax; i++)
				{
					ltf[i].setText(SimAnalyzer.obsParamDefaultValues.get(parent.lcombo[idobs].getSelectedItem().toString())[i]);
				}
			}
			
		}
	}
			

	
	class ObserversConfig extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		int nbobsmax=SimAnalyzer.nbObserverMax;
		String nomproj;
		JLabel[] l;//, l0,lt,  l1, l2,l2b, l3, l4, l5,  l6,  l7,l8,l9,l10;
//		JTextField[] ltf; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
		JButton[] jbparams; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
		JComboBox[] lcombo; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
		JButton jbtOK, jbtcancel;
		String[][] params;
		public ObserversConfig(int newtype, String nom) 
		{
			nomproj=nom;
			params=new String[nbobsmax][Observer.nbparammax];
			for (int i=0; i<nbobsmax; i++)
			{
				for (int j=0; j<Observer.nbparammax; j++)
					params[i][j]=new String("");
			}
			params[0][1]="1";

			setTitle("Data Processing - Obervers");  
			setSize(500,350); 
			setResizable(true);    
			JPanel panel = new JPanel();  
			FlowLayout flowLayout = new FlowLayout();  
		    flowLayout.setHgap(10);  
			flowLayout.setVgap(10);  
			panel.setLayout(flowLayout);  
			add(panel, BorderLayout.CENTER); 
			JPanel panel_1 = new JPanel();  
			GridLayout grid = new GridLayout(0, 3);  
			grid.setHgap(10);  
			grid.setVgap(15);  
			panel_1.setLayout(grid);  
			panel.add(panel_1);  
			l=new JLabel[nbobsmax];
//			ltf=new JTextField[nbobsmax];
			lcombo=new JComboBox[nbobsmax];
			jbparams=new JButton[nbobsmax];

			for (int i=0; i<nbobsmax; i++)
			{
				l[i]=new JLabel("OBs ID:"+i);
//				ltf[i]=new JTextField("Obs"+i);
				lcombo[i]=new JComboBox(SimAnalyzer.ObsPossibleTypeList.toArray());
				jbparams[i]=new JButton("Parameters");
				panel_1.add(l[i]);
//				panel_1.add(ltf[i]);
				panel_1.add(lcombo[i]);
				panel_1.add(jbparams[i]);
				jbparams[i].addActionListener(this);
			}
			lcombo[0].setSelectedIndex(1);
			lcombo[1].setSelectedIndex(2);
			lcombo[2].setSelectedIndex(3);
			lcombo[0].setEnabled(false);
			lcombo[1].setEnabled(false);
			lcombo[2].setEnabled(false);
			jbtOK = new JButton("Save");   
			panel_1.add(jbtOK);   
			jbtOK.addActionListener(this);
			jbtcancel = new JButton("Cancel");   
			panel_1.add(jbtcancel);   
			jbtcancel.addActionListener(this);
			pack();
			setVisible(true);  			
			
		}
		
		public ObserversConfig(String nom) 
		{
			this(0,nom);
			FileReader fr = null;
			try {
				fr = new FileReader("projects/"+nomproj+"obs.config");
			BufferedReader br = new BufferedReader(fr);		  
			for (int i=0; i<nbobsmax; i++)
			{
			try {
	//			ltf[i].setText(br.readLine());
				lcombo[i].setSelectedItem(br.readLine());
				SimAnalyzer.obstypes[i]=lcombo[i].getSelectedItem().toString();
				for (int j=0; j<Observer.nbparammax; j++)
				{
					params[i][j]=br.readLine();
					SimAnalyzer.obsparams[i][j]=params[i][j];
					
				}
			} catch (IOException e) {
				System.err.println("Error : " + e.getLocalizedMessage());
				//e.printStackTrace();
			} 
			}
			} catch (IOException e1) {
				System.err.println("Error : " + e1.getLocalizedMessage());
				//e1.printStackTrace();
			}
			
			{
				FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+nomproj+"obs.config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					for (int i=0; i<nbobsmax; i++)
					{
						if (lcombo[i].getSelectedIndex()>=0)
						SimAnalyzer.obstypes[i]=lcombo[i].getSelectedItem().toString();
						else
							SimAnalyzer.obstypes[i]="";
						
						bw.write(SimAnalyzer.obstypes[i]);
						bw.newLine();						
						for (int j=0; j<Observer.nbparammax; j++)
						{
							bw.write(params[i][j]);
							SimAnalyzer.obsparams[i][j]=params[i][j];
							bw.newLine();													
						}
					}
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}		  	        
				menus.setEnabled(true);		  
			}

	    }

		public void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			for (int i=0; i<nbobsmax; i++)
			{
				if (src.equals(jbparams[i]))
				{
					@SuppressWarnings("unused")
					ObserversParameters obp=new ObserversParameters(this,i);
				}
			}
			if (src.equals(jbtcancel))
			{
//				dispose();
			}
			if (src.equals(jbtOK))
			{
				FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+nomproj+"obs.config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					for (int i=0; i<nbobsmax; i++)
					{
						if (lcombo[i].getSelectedIndex()>=0)
						SimAnalyzer.obstypes[i]=lcombo[i].getSelectedItem().toString();
						else
							SimAnalyzer.obstypes[i]="";
						
						bw.write(SimAnalyzer.obstypes[i]);
						bw.newLine();						
						for (int j=0; j<Observer.nbparammax; j++)
						{
							bw.write(params[i][j]);
							SimAnalyzer.obsparams[i][j]=params[i][j];
							bw.newLine();													
						}
					}
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}		  	        
				menus.setEnabled(true);		  
//				dispose();
			}
			
		}
	}
			
	
	class ShowProject extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		JLabel[] l;//, l0,lt,  l1, l2,l2b, l3, l4, l5,  l6,  l7,l8,l9,l10;
		JTextField[] ltf; //l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
		JComboBox lcomb;
		JTextField ltparam;
		int nblab;
		JButton jbtdm, jbtOK, jbtsim, jbtsave,jbtbatch;
		ObserversConfig obsconf;
		public ShowProject(int newtype) 
		{
			myDataFile = new File ("Data/"+prname+".csv");
			setTitle("Show Project");  
			setSize(500,350); 
			setResizable(true);    
			JPanel panel = new JPanel();  
			FlowLayout flowLayout = new FlowLayout();  
		    flowLayout.setHgap(10);  
			flowLayout.setVgap(10);  
			panel.setLayout(flowLayout);  
			add(panel, BorderLayout.CENTER); 
			JPanel panel_1 = new JPanel();  
			GridLayout grid = new GridLayout(0, 2);  
			grid.setHgap(10);  
			grid.setVgap(15);  
			panel_1.setLayout(grid);  
			panel.add(panel_1);  
			 nblab=14;
			l=new JLabel[nblab];
			ltf=new JTextField[nblab];

			l[0] = new JLabel("Project name:");
			ltf[0] = new JTextField(prname);

			int no=1;
		    l[no] = new JLabel("type (1:NeltLogo/2:Logs): "); 
			ltf[no] = new JTextField(""+newtype);
		    no++;
		    l[no] = new JLabel("model/file name: "); 
			ltf[no] = new JTextField("");
		    no++;
		    l[no] = new JLabel("Clustering step: ");  
			ltf[no] = new JTextField("1");
		    no++;
		    l[no] = new JLabel("Update cluster every step: ");  
			ltf[no] = new JTextField("1");
		    no++;
		    l[no] = new JLabel("Total number of step: ");  
			ltf[no] = new JTextField("1");
		    no++;
		    l[no] = new JLabel("Agent column (LOGS): ");  
			ltf[no] = new JTextField("0");
		    no++;
		    l[no] = new JLabel("Time column (LOGS): ");  
			ltf[no] = new JTextField("1");
		    no++;
		    l[no] = new JLabel("First column clustering: ");  
			ltf[no] = new JTextField("-1");
		    no++;
		    l[no] = new JLabel("Last column clustering: ");  
			ltf[no] = new JTextField("-1");
		    no++;
		    l[no] = new JLabel("Double Clustering: ");  
			ltf[no] = new JTextField("0");
		    no++;
		    l[no] = new JLabel("Follow clusters: ");  
			ltf[no] = new JTextField("1");
		    no++;
		    l[no] = new JLabel("Recompute history: ");  
			ltf[no] = new JTextField("1");

			JLabel labc=new JLabel("Clustering algorithm");
			String[] jcitems=new String[4];
			jcitems[0]="XMeans";
			jcitems[1]="SimpleKMeans";
			jcitems[2]="DBScan";
			jcitems[3]="Autre2";
			lcomb=new JComboBox(jcitems);
			lcomb.addActionListener(this);
			
			no++;
		    l[no] = new JLabel("Clusterer parameters: ");  
			ltf[no] = new JTextField("-L1 -H50 -I100");
			ltparam=ltf[no];
			
			for (int i=0; i<nblab; i++)
			{
				panel_1.add(l[i]);
				panel_1.add(ltf[i]);				
			}
			panel_1.add(labc);
			panel_1.add(lcomb);				
			
			jbtsave = new JButton("Start new simulation (and save)");   
			panel_1.add(jbtsave);   
			jbtsave.addActionListener(this);
			jbtsim = new JButton("Start new simulation (no save)");   
			panel_1.add(jbtsim);   
			jbtsim.addActionListener(this);
			jbtdm = new JButton("Observer Config");   
			panel_1.add(jbtdm);   
			jbtdm.addActionListener(this);
			jbtOK = new JButton("Cancel");   
			panel_1.add(jbtOK);   
			jbtOK.addActionListener(this);
			jbtbatch = new JButton("Batch Mode");   
			panel_1.add(jbtbatch);   
			jbtbatch.addActionListener(this);
//			setVisible(true); 
			 obsconf=new ObserversConfig(prname);
			ltf[0].setEditable(false);
//			obsconf.setVisible(false);
			SimAnalyzer.tabbedpane.removeTabAt(SimAnalyzer.tabidobserver);
			SimAnalyzer.tabbedpane.insertTab(tabnameobserver, tabicobserver,obsconf,tabttobserver,tabidobserver);
			SimAnalyzer.tabbedpane.setSelectedIndex(tabidobserver);
			
		}
		public ShowProject() 
		{
			this(0);
			FileReader fr = null;
			try {
				fr = new FileReader("projects/"+prname+".config");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);		  
			for (int i=1; i<nblab; i++)
			{
			try {
				ltf[i].setText(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			}
			try {
				lcomb.setSelectedIndex(Integer.parseInt(br.readLine()));
			} catch (Exception e) {
				lcomb.setSelectedIndex(0);
				ltparam.setText("-L 1 -H 50 -I 100");
				e.printStackTrace();
			} 
	    }

		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			Matrix m = NetLogoSimulationController.DataMatrix;
			if (src.equals(this.lcomb))
			{
				if (lcomb.getSelectedIndex()==0)
					ltparam.setText("-L 1 -H 50 -I 100");
				if (lcomb.getSelectedIndex()==1)
					ltparam.setText("-N 3");
				if (lcomb.getSelectedIndex()==2)
					ltparam.setText("");
				if (lcomb.getSelectedIndex()==3)
					ltparam.setText("");
			}
			if (src.equals(jbtdm))
			{
				menug.setEnabled(true);
				for(int i=0;i<columnCount;i++)
				{
/*					JTextField jf = new JTextField();
					jf = (JTextField) vjf.elementAt(i);
					jf.setEnabled(true);
					JCheckBox jcb = new JCheckBox();
					jcb = (JCheckBox) vcb.elementAt(i);
					jcb.setEnabled(true);
					Choice jc1 = new Choice();
					jc1 = (Choice) vc1.elementAt(i);
					jc1.setEnabled(true);
					Choice jc2 = new Choice();
					jc2 = (Choice) vc2.elementAt(i);
*/					FileReader fr = null;
					try {
						fr = new FileReader("projects/"+prname+".config");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					BufferedReader br = new BufferedReader(fr);	
					String l = null;
					for(int j=0;j<4;j++)
						try {
							l = br.readLine();
						} catch (IOException e2) {
							e2.printStackTrace();
					}
/*					jc2.removeAll();
					jc2.add(" ");
					String[] v2 = l.split("  ");
					for(int j=0;j<v2.length;j++)
						jc2.add(v2[j]);				
					jc2.setEnabled(true);
					jbt1.setEnabled(true);
					jbt2.setEnabled(true);
					jbt3.setEnabled(true);
					jbt4.setEnabled(true);
					jbt5.setEnabled(true);*/
//					this.pack();
					
				}	
			}
			if (src.equals(jbtOK))
			{
//				dispose();
			}
			if (false)
			{
				if(myDataFile.exists()){
					try {
						m = MatrixFactory.importFromFile(myDataFile);
					} catch (MatrixException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                    FileReader fr = null;
					try {
						fr = new FileReader("projects/"+prname+".config");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					BufferedReader br = new BufferedReader(fr);	
					String l = null;
					for(int j=0;j<4;j++)
						try {
							l = br.readLine();
					} catch (IOException e2) {
							e2.printStackTrace();
					}
					String[] v2 = l.split("  ");	
					System.out.println(m.getColumnCount()+" "+v2.length);
                    for(int i=0;i<m.getColumnCount();i++)
                    {
                    	m.setColumnLabel(i,v2[i]);
                    }                
					m.showGUI();
				}
			}
			if (src.equals(jbtdm))
			{
				obsconf.setVisible(true);

//				ObserversConfig obsconf=new ObserversConfig(ltf[0].getText());
			}
			if (src.equals(jbtsave))
			{
/*				name = "models/"+ltf[2].getText();
		        clusterstep = Integer.parseInt(ltf[3].getText());
		        updatestep = Integer.parseInt(ltf[4].getText());
		        totalsteps = (int)(Integer.parseInt(ltf[5].getText()));
		        agcol = Integer.parseInt(ltf[6].getText());
		        timecol = Integer.parseInt(ltf[7].getText());
		        startcol = Integer.parseInt(ltf[8].getText());
		        endcol = Integer.parseInt(ltf[9].getText());
		        followcluster=true;
		        doubleclustering=true;
		        computehistory=true;
		        if (Integer.parseInt(ltf[10].getText())==0) doubleclustering=false;
		        if (Integer.parseInt(ltf[11].getText())==0) followcluster=false;
		        if (Integer.parseInt(ltf[12].getText())==0) computehistory=false;*/
				FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+ltf[0].getText()+".config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					for (int i=1; i<nblab; i++)
					{
						bw.write(ltf[i].getText());
						bw.newLine();						
					}
					bw.write(""+this.lcomb.getSelectedIndex());
					bw.newLine();						
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}		  	        
				menus.setEnabled(true);		  
		}
			
			if (src.equals(jbtsim)|src.equals(jbtsave))
			{
				name = "models/"+ltf[2].getText();
				int typem=Integer.parseInt(ltf[1].getText());
		        clusterstep = Integer.parseInt(ltf[3].getText());
		        updatestep = Integer.parseInt(ltf[4].getText());
		        totalsteps = Integer.parseInt(ltf[5].getText());
		        agcol = Integer.parseInt(ltf[6].getText());
		        timecol = Integer.parseInt(ltf[7].getText());
		        startcol = Integer.parseInt(ltf[8].getText());
		        endcol = Integer.parseInt(ltf[9].getText());
		        followcluster=true;
		        doubleclustering=true;
		        computehistory=true;
		        if (Integer.parseInt(ltf[10].getText())==0) doubleclustering=false;
		        if (Integer.parseInt(ltf[11].getText())==0) followcluster=false;
		        if (Integer.parseInt(ltf[12].getText())==0) computehistory=false;
		        SimAnalyzer.clustererType=lcomb.getSelectedIndex();
		        String delims = "[ ]+";
		        SimAnalyzer.clustererParametersString=ltf[13].getText();
		        SimAnalyzer.clustererParameters=SimAnalyzer.clustererParametersString.split(delims);
		        if (typem==1)
		        {
					startnetlogo=true;
					startlogs=false;
		        }
		        else
		        {
					startnetlogo=false;
					startlogs=true;
		        }
		        	
				menus.setEnabled(true);
				menug.setEnabled(false);
	//			dispose();
/*				for(int i=0;i<columnCount;i++)
				{
					JTextField jf = new JTextField();
					jf = (JTextField) vjf.elementAt(i);
					jf.setEnabled(false);
					JCheckBox jcb = new JCheckBox();
					jcb = (JCheckBox) vcb.elementAt(i);
					jcb.setEnabled(false);
					Choice jc1 = new Choice();
					jc1 = (Choice) vc1.elementAt(i);
					jc1.setEnabled(false);
					Choice jc2 = new Choice();
					jc2 = (Choice) vc2.elementAt(i);
					jc2.setEnabled(false);
					jbt1.setEnabled(false);
					jbt2.setEnabled(false);
					jbt3.setEnabled(false);
					jbt4.setEnabled(false);
					jbt5.setEnabled(false);		
				}*/
			}
			if (src.equals(jbtbatch))
			{
				
				name = "models/"+ltf[2].getText();
				int typem=Integer.parseInt(ltf[1].getText());
		        clusterstep = Integer.parseInt(ltf[3].getText());
		        updatestep = Integer.parseInt(ltf[4].getText());
		        totalsteps = Integer.parseInt(ltf[5].getText());
		        agcol = Integer.parseInt(ltf[6].getText());
		        timecol = Integer.parseInt(ltf[7].getText());
		        startcol = Integer.parseInt(ltf[8].getText());
		        endcol = Integer.parseInt(ltf[9].getText());
		        followcluster=true;
		        doubleclustering=true;
		        computehistory=true;
		        if (Integer.parseInt(ltf[10].getText())==0) doubleclustering=false;
		        if (Integer.parseInt(ltf[11].getText())==0) followcluster=false;
		        if (Integer.parseInt(ltf[12].getText())==0) computehistory=false;
		        SimAnalyzer.clustererType=lcomb.getSelectedIndex();
		        String delims = "[ ]+";
		        SimAnalyzer.clustererParametersString=ltf[13].getText();
		        SimAnalyzer.clustererParameters=SimAnalyzer.clustererParametersString.split(delims);

		        
				JFileChooser fileChooser = new JFileChooser("models/");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.setMultiSelectionEnabled(true);
				fileChooser.setDialogTitle("Choose logs");
				Thread t=Thread.currentThread(); 
				int ret = fileChooser.showOpenDialog(this);
				File[] targets=fileChooser.getSelectedFiles();
				if ((ret == JFileChooser.APPROVE_OPTION)&(targets.length>1)) 
				{
					for (int f=0; f<targets.length; f++)
					{
						System.out.println(targets[f].getName());
						System.out.println(targets[f].getAbsolutePath());
						
						name = targets[f].getAbsolutePath().substring(0, targets[f].getAbsolutePath().length()-4);
				        if (typem==1)
				        {
							startnetlogo=true;
							startlogs=false;
				        }
				        else
				        {
							startnetlogo=false;
							startlogs=true;
				        }
				        while( (startnetlogo==true)|(startlogs==true))
				        {
						synchronized (t)
						{
						try {
							t.wait(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						}
				        }
				        
				        ClusterEval ce=(ClusterEval)SimAnalyzer.evalpane;
				    	File projectd = new File("savedlogs/"+"Batch"+prname+"/");
						boolean nfile = projectd.mkdir();
				        ce.saveTo(""+"Batch"+prname+"/"+targets[f].getName().substring(0, targets[f].getName().length()-4),targets[f].getName().substring(0, targets[f].getName().length()-4));
					}
				}
				

			
			}		
		}
	}
		
	class SaveData extends JFrame implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		
		JLabel l;
		JButton jbtOK;
		public SaveData()
		{
			setTitle("Project "+prname);  
			setSize(500,350); 
	//		setLocation(200, 150);   
			setResizable(true);    
			JPanel panel = new JPanel();  
			FlowLayout flowLayout = new FlowLayout();  
		    flowLayout.setHgap(10);  
			flowLayout.setVgap(10);  
			panel.setLayout(flowLayout);  
			add(panel, BorderLayout.CENTER); 
			GridLayout grid = new GridLayout(0, 1);  
			grid.setHgap(10);  
			grid.setVgap(15);  
			JPanel panel_1 = new JPanel();
			panel_1.setLayout(grid);  
			panel.add(panel_1);
			l = new JLabel("Save data to database?");
			panel_1.add(l);
			jbtOK = new JButton("Ok");   
			panel_1.add(jbtOK);   
			jbtOK.addActionListener(this);
		}
		
		public synchronized void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			if (src.equals(jbtOK))
			{
				Connection conn;
				Statement stmt;
				String sql;
				try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/netlogo",
					                                     "root", "123");
					stmt = (Statement) conn.createStatement();
					sql="drop table if exists "+prname;
                    stmt.executeUpdate(sql);
                    
                    sql="create table "+prname+" (";                   
                    for(int i=0;i<SimulationController.DataMatrix.getColumnCount();i++)
                    {
                        boolean res = true;
                        for(int j=0;j<SimulationController.DataMatrix.getRowCount();j++)
                        {
                        	try{
                        		Double.parseDouble(NetLogoSimulationController.DataMatrix.getAsString(j,i));
                            }catch(Exception   ex){
                                res   =   false;
                            }
                        }
                        
                        String st = SimulationController.DataMatrix.getColumnLabel(i);
                        st = st.replaceAll("/", "_");
                        st = st.replaceAll(" ", "_");
                        st = st.replaceAll("-","_");
                        st = st.replaceAll("\\?","");
                        if(i!=SimulationController.DataMatrix.getColumnCount()-1 )
                        {
                        	if(res)
                        		sql = sql+st+" double, ";
                        	else
                        		sql = sql+st+" varchar(1000), ";
                        }
                        else
                        	if(res)
                        		sql = sql+st+" double)";
                        	else
                        		sql = sql+st+" varchar(1000))";
                    }
                    stmt.executeUpdate(sql);
                    MatrixFactoryExtension.exportToJDBC("jdbc:mysql://localhost:3306/netlogo",
                            "root", "123", SimulationController.DataMatrix, prname);
                    myDataFile = new File("Data/"+prname+".csv");
                    SimulationController.DataMatrix.exportToFile(FileFormat.CSV, myDataFile);
                    SimulationController.DataMatrix.showGUI();	
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (MatrixException e2) {
					e2.printStackTrace();
				} catch (IOException e3) {
					e3.printStackTrace();
				}
				dispose();		
				for(int i=0;i<columnCount;i++)
				{
					JTextField jf = new JTextField();
					jf = (JTextField) vjf.elementAt(i);
					jf.setEnabled(true);
					JCheckBox jcb = new JCheckBox();
					jcb = (JCheckBox) vcb.elementAt(i);
					jcb.setEnabled(true);
					Choice jc1 = new Choice();
					jc1 = (Choice) vc1.elementAt(i);
					jc1.setEnabled(true);
					Choice jc2 = new Choice();
					jc2 = (Choice) vc2.elementAt(i);
					FileReader fr = null;
					try {
						fr = new FileReader("projects/"+prname+".config");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					BufferedReader br = new BufferedReader(fr);	
					String l = null;
					for(int j=0;j<4;j++)
						try {
							l = br.readLine();
						} catch (IOException e2) {
							e2.printStackTrace();
					}
					jc2.removeAll();
					jc2.add(" ");
					String[] v2 = l.split("  ");
					for(int j=0;j<v2.length;j++)
						jc2.add(v2[j]);				
					jc2.setEnabled(true);
					jbt1.setEnabled(true);
					jbt2.setEnabled(true);
					jbt3.setEnabled(true);
					jbt4.setEnabled(true);
					jbt5.setEnabled(true);
					this.pack();
				}
				
			}
			
		}
		
	}
		
	public static void main(String args[]) throws Exception{
        SimAnalyzer frame=new SimAnalyzer() ;
		WindowListener l = new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		};

		frame.addWindowListener(l);
		frame.setLocation(100,10);
        frame.pack() ;
        frame.setVisible(true) ;
        SimAnalyzer.waits(); 
    }

}




