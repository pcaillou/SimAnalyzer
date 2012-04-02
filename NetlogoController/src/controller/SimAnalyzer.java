package controller;


import clustering.Cluster;
import clustering.Clusterer;
import clustering.Indexes;
import clustering.WekaClusterer;
import logs.*;
import controller.*;

import java.awt.event.*;

import javax.swing.*;


import netlogo.NetLogoSimulationController;

import org.nlogo.app.App;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.DB;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.enums.ValueType;
import org.ujmp.core.exceptions.MatrixException;

import statistic.distribution.VariableDistribution;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import java.awt.*;
import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//
//
// RepStat
//
//
public class SimAnalyzer extends JFrame
		implements ActionListener, ItemListener
{
	static boolean startnetlogo;
	static boolean startlogs;
	static boolean restartnetlogo;
	static boolean followcluster=true;
	static boolean computehistory=true;
	static boolean doubleclustering=false;
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

	static Vector vjf = new Vector();
	static Vector vc1 = new Vector();
	static Vector vc2 = new Vector();
	static Vector vcb = new Vector();
	static int columnCount = 5;
	static JButton jbt1,jbt2,jbt3,jbt4,jbt5;
	static Boolean view = false;
	
	GridBagConstraints gbc=new GridBagConstraints();
	GridBagLayout gb=new GridBagLayout();
	
	public SimAnalyzer()
	{		
		super(" SimAnalyzer "); 
		restartnetlogo=false;
		setJMenuBar(menubar);
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
		
		JTextField [] jtf = new JTextField[columnCount]; 
		JCheckBox [] cb = new JCheckBox[columnCount];
		Choice [] c1 = new Choice[columnCount];
	    Choice [] c2 = new Choice[columnCount];

		getContentPane().setLayout(gb);
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
		jbt5.setEnabled(false);
	}
	
	public static Clusterer newClusterer() throws Exception
	{
    	int minClustersNumber = 1;
		int maxClustersNumber = 20;
		int maxIterations = 200;
		Clusterer cl=new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
				, "-L", ""+minClustersNumber, "-H", ""+maxClustersNumber, "-I", ""+maxIterations );
		return cl;
	}

	public synchronized static void waits() throws Exception
	{
		while (true)
		{
			
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
	       	    	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
						, "-L", ""+minClustersNumber, "-H", ""+maxClustersNumber, "-I", ""+maxIterations ));
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
	       	    	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
						, "-L", ""+minClustersNumber, "-H", ""+maxClustersNumber, "-I", ""+maxIterations ));
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
		        for(int i=0; i<=totalsteps; i++)
		        {
		        	wcl.add(i, new WekaClusterer(WekaClusterer.WekaClustererType.XMeans, false
							, "-L", ""+minClustersNumber, "-H", ""+maxClustersNumber, "-I", ""+maxIterations));
		        }
		        
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
	
	public synchronized void actionPerformed(ActionEvent evt)
	{
		Object src = evt.getSource();
		if (src.equals(micp))
		{	
			CreateProject p = new CreateProject();
			WindowListener l = new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
			    {
					System.exit(0);
				}
			};
			p.setLocation(500,100);
		    p.pack() ;
		    p.setVisible(true);
		}
		if (src.equals(miop))
		{
			OpenProject p = new OpenProject();
			WindowListener l = new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
			    {
					System.exit(0);
				}
			};

		}
		if (src.equals(mignl))
		{
		   GetData f = new GetData();
		   WindowListener l = new WindowAdapter()
		   {
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
		   };
		   f.setLocation(500,100);
	       f.pack() ;
		   f.setVisible(true);
		}
		if (src.equals(migrl))
		{
		   GetDataLog f = new GetDataLog();
		   WindowListener l = new WindowAdapter()
		   {
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
		   };
		   f.setLocation(500,100);
	       f.pack() ;
		   f.setVisible(true);
		}
		if (src.equals(mist))
		{
			SaveData s = new SaveData();
			 WindowListener l = new WindowAdapter()
			 {
				 public void windowClosing(WindowEvent e)
				 {
					System.exit(0);
				 }
			 };
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
				WindowListener l = new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						System.exit(0);
					}
				};
				sp.setLocation(500,100);
			    sp.pack() ;
				sp.setVisible(true);				
			}
	    }
	}
	
	class ShowProject extends JFrame implements ActionListener
	{
		JLabel l, l0,lt,  l1, l2,l2b, l3, l4, l5,  l6,  l7,l8,l9,l10;
		JTextField l0t, l01, l02, l02b,l03, l04, l05, l06,  l07,l08,l09,l010;
		JButton jbtdm, jbtOK, jbtsim;
		public ShowProject() 
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
			FileReader fr = null;
			try {
				fr = new FileReader("projects/"+prname+".config");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);		  
			l = new JLabel("Project name:");
			panel_1.add(l);
			l0 = new JLabel(prname);
			panel_1.add(l0);
		    lt = new JLabel("type (1:NeltLogo/2:Logs): "); 
			panel_1.add(lt);  
			try {
				l0t = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l0t);  
		    l1 = new JLabel("model/fike name: "); 
			panel_1.add(l1);  
			try {
				l01 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l01);  
		    l2 = new JLabel("Clustering step: ");  
			panel_1.add(l2);  
			try {
				l02 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l02);  
		    l2b = new JLabel("Update cluster every step: ");  
			panel_1.add(l2b);  
			try {
				l02b = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l02b);  
			l3 = new JLabel("Total number of step: ");  
			panel_1.add(l3);  
			try {
				l03 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l03);  
			l4 = new JLabel("Agent column (LOGS): ");  
			panel_1.add(l4);  
			try {
				l04 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l04);  
			l5 = new JLabel("Time column (LOGS): ");  
			panel_1.add(l5);  
			try {
				l05 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l05);  
			l6 = new JLabel("First column clustering: ");  
			panel_1.add(l6);  
			try {
				l06 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l06);  
			l7 = new JLabel("Last column clustering: ");  
			panel_1.add(l7);  
			try {
				l07 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l07);  
			l8 = new JLabel("Double Clustering: ");  
			panel_1.add(l8);  
			try {
				l08 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l08);  
			l9 = new JLabel("Follow clusters: ");  
			panel_1.add(l9);  
			try {
				l09 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l09);  
			l10 = new JLabel("Recompute history: ");  
			panel_1.add(l10);  
			try {
				l010 = new JTextField(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			panel_1.add(l010);  
			jbtdm = new JButton("Data matrix");   
			panel_1.add(jbtdm);   
			jbtdm.addActionListener(this);
			jbtsim = new JButton("Start new simulation");   
			panel_1.add(jbtsim);   
			jbtsim.addActionListener(this);
			jbtOK = new JButton("OK");   
			panel_1.add(jbtOK);   
			jbtOK.addActionListener(this);
			setVisible(true);  			
	    }

		public void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			Matrix m = NetLogoSimulationController.DataMatrix;
			if (src.equals(jbtOK) | src.equals(jbtdm))
			{
				menug.setEnabled(true);
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
			if (src.equals(jbtdm))
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
			if (src.equals(jbtsim))
			{
				name = "models/"+l01.getText();
				int typem=Integer.parseInt(l0t.getText());
		        clusterstep = Integer.parseInt(l02.getText());
		        updatestep = Integer.parseInt(l02b.getText());
		        totalsteps = Integer.parseInt(l03.getText());
		        agcol = Integer.parseInt(l04.getText());
		        timecol = Integer.parseInt(l05.getText());
		        startcol = Integer.parseInt(l06.getText());
		        endcol = Integer.parseInt(l07.getText());
		        followcluster=true;
		        doubleclustering=true;
		        computehistory=true;
		        if (Integer.parseInt(l08.getText())==0) doubleclustering=false;
		        if (Integer.parseInt(l09.getText())==0) followcluster=false;
		        if (Integer.parseInt(l010.getText())==0) computehistory=false;
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
				dispose();
				for(int i=0;i<columnCount;i++)
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
				}
			}
			
		}
	}
		
	class GetData extends JFrame implements ActionListener
	{	
		JLabel l, l0, l1, l2,l2b, l3,l4,l5,l6,l7,l8,l9,l10;
		JTextField tf1, tf2, tf2b,tf3,tf4,tf5,tf6,tf7,tf8,tf9,tf10;
		JButton jbtOK, jbtsim;
		public GetData()
		{
			setTitle("Project "+prname);  
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
			l = new JLabel("Project name:");
			panel_1.add(l);
			l0 = new JLabel(prname);
			panel_1.add(l0);
		    l1 = new JLabel("Netlogo model name: "); 
			panel_1.add(l1);  
			tf1 = new JTextField();  
			panel_1.add(tf1);  
		    l2 = new JLabel("Cluster every n Time step: ");  
			panel_1.add(l2);  
			tf2 = new JTextField();  
			panel_1.add(tf2);
		    l2b = new JLabel("Update cluster every n Time step: ");  
			panel_1.add(l2b);  
			tf2b = new JTextField();  
			panel_1.add(tf2b);
			l3 = new JLabel("End after n total number of the step: ");  
			panel_1.add(l3);  
			tf3 = new JTextField();  
			panel_1.add(tf3);
		    l4 = new JLabel("AgentID column (first=0; LOG ONLY): ");  
			panel_1.add(l4);  
			tf4 = new JTextField("0");  
			panel_1.add(tf4);
			l5 = new JLabel("Time/Tick column (first=0; LOG ONLY): ");  
			panel_1.add(l5);  
			tf5 = new JTextField("0");  
			panel_1.add(tf5);
			l6 = new JLabel("First column for Clustering (first=0; all=-1): ");  
			panel_1.add(l6);  
			tf6 = new JTextField("-1");  
			panel_1.add(tf6);
			l7 = new JLabel("Last column for Clustering (first=0; all=-1): ");  
			panel_1.add(l7);  
			tf7 = new JTextField("-1");  
			panel_1.add(tf7);
			l8 = new JLabel("Double Clustering (no=0; yes=1): ");  
			panel_1.add(l8);  
			tf8 = new JTextField("0");  
			panel_1.add(tf8);
			l9 = new JLabel("Follow clusters (no=0; yes=1): ");  
			panel_1.add(l9);  
			tf9 = new JTextField("1");  
			panel_1.add(tf9);
			l10 = new JLabel("Recompute history (no=0; yes=1): ");  
			panel_1.add(l10);  
			tf10 = new JTextField("1");  
			panel_1.add(tf10);
			jbtOK = new JButton("Save project and start simulation");   
			panel_1.add(jbtOK);   
			jbtOK.addActionListener(this);
			setVisible(true);						
		}
		public synchronized void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			if (src.equals(jbtOK))
			{
				name = "models/"+tf1.getText();
		        clusterstep = Integer.parseInt(tf2.getText());
		        updatestep = Integer.parseInt(tf2b.getText());
		        totalsteps = (int)(Integer.parseInt(tf3.getText())/clusterstep);
		        agcol = Integer.parseInt(tf4.getText());
		        timecol = Integer.parseInt(tf5.getText());
		        startcol = Integer.parseInt(tf6.getText());
		        endcol = Integer.parseInt(tf7.getText());
		        followcluster=true;
		        doubleclustering=true;
		        computehistory=true;
		        if (Integer.parseInt(tf8.getText())==0) doubleclustering=false;
		        if (Integer.parseInt(tf9.getText())==0) followcluster=false;
		        if (Integer.parseInt(tf10.getText())==0) computehistory=false;
				FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+prname+".config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					String typel="1";
					bw.write(typel);
					bw.newLine();
					bw.write(tf1.getText());
					bw.newLine();
					bw.write(tf2.getText());
					bw.newLine();
					bw.write(tf2b.getText());
					bw.newLine();
					bw.write(tf3.getText());
					bw.newLine();
					bw.write(tf4.getText());
					bw.newLine();
					bw.write(tf5.getText());
					bw.newLine();
					bw.write(tf6.getText());
					bw.newLine();
					bw.write(tf7.getText());
					bw.newLine();
					bw.write(tf8.getText());
					bw.newLine();
					bw.write(tf9.getText());
					bw.newLine();
					bw.write(tf10.getText());
					bw.newLine();
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}		  	        
				startnetlogo=true;
				menus.setEnabled(true);		  
				dispose();
			}
			
		}
	}
	
				
		class GetDataLog extends JFrame implements ActionListener
		{	
			JLabel l, l0, l1, l2,l2b, l3,l4,l5,l6,l7,l8,l9,l10;
			JTextField tf1, tf2, tf2b,tf3,tf4,tf5,tf6,tf7,tf8,tf9,tf10;
			JButton jbtOK, jbtsim;
			public GetDataLog()
			{
				setTitle("Project "+prname);  
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
				l = new JLabel("Project name:");
				panel_1.add(l);
				l0 = new JLabel(prname);
				panel_1.add(l0);
			    l1 = new JLabel("Logs file name: "); 
				panel_1.add(l1);  
				tf1 = new JTextField();  
				panel_1.add(tf1);  
			    l2 = new JLabel("Cluster every n Time step: ");  
				panel_1.add(l2);  
				tf2 = new JTextField();  
				panel_1.add(tf2);
			    l2b = new JLabel("Update cluster every n Time step: ");  
				panel_1.add(l2b);  
				tf2b = new JTextField();  
				panel_1.add(tf2b);
				l3 = new JLabel("End after n total number of the step: ");  
				panel_1.add(l3);  
				tf3 = new JTextField();  
				panel_1.add(tf3);
			    l4 = new JLabel("AgentID column (first=0; LOG ONLY): ");  
				panel_1.add(l4);  
				tf4 = new JTextField("0");  
				panel_1.add(tf4);
				l5 = new JLabel("Time/Tick column (first=0; LOG ONLY): ");  
				panel_1.add(l5);  
				tf5 = new JTextField("0");  
				panel_1.add(tf5);
				l6 = new JLabel("First column for Clustering (first=0; all=-1): ");  
				panel_1.add(l6);  
				tf6 = new JTextField("-1");  
				panel_1.add(tf6);
				l7 = new JLabel("Last column for Clustering (first=0; all=-1): ");  
				panel_1.add(l7);  
				tf7 = new JTextField("-1");  
				panel_1.add(tf7);
				l8 = new JLabel("Double Clustering (no=0; yes=1): ");  
				panel_1.add(l8);  
				tf8 = new JTextField("0");  
				panel_1.add(tf8);
				l9 = new JLabel("Follow clusters (no=0; yes=1): ");  
				panel_1.add(l9);  
				tf9 = new JTextField("1");  
				panel_1.add(tf9);
				l10 = new JLabel("Recompute history (no=0; yes=1): ");  
				panel_1.add(l10);  
				tf10 = new JTextField("1");  
				panel_1.add(tf10);
				jbtOK = new JButton("Save project and start simulation");   
				panel_1.add(jbtOK);   
				jbtOK.addActionListener(this);
				setVisible(true);						
			}
					
		public synchronized void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			if (src.equals(jbtOK))
			{
				name = "models/"+tf1.getText();
		        clusterstep = Integer.parseInt(tf2.getText());
		        updatestep = Integer.parseInt(tf2b.getText());
		        totalsteps = (int)(Integer.parseInt(tf3.getText())/clusterstep);
		        agcol = Integer.parseInt(tf4.getText());
		        timecol = Integer.parseInt(tf5.getText());
		        startcol = Integer.parseInt(tf6.getText());
		        endcol = Integer.parseInt(tf7.getText());
		        followcluster=true;
		        doubleclustering=true;
		        computehistory=true;
		        if (Integer.parseInt(tf8.getText())==0) doubleclustering=false;
		        if (Integer.parseInt(tf9.getText())==0) followcluster=false;
		        if (Integer.parseInt(tf10.getText())==0) computehistory=false;
				FileWriter fw = null;
				try {
					fw = new FileWriter("projects/"+prname+".config", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BufferedWriter bw = new BufferedWriter(fw);
				try {
					String typel="2";
					bw.write(typel);
					bw.newLine();
					bw.write(tf1.getText());
					bw.newLine();
					bw.write(tf2.getText());
					bw.newLine();
					bw.write(tf2b.getText());
					bw.newLine();
					bw.write(tf3.getText());
					bw.newLine();
					bw.write(tf4.getText());
					bw.newLine();
					bw.write(tf5.getText());
					bw.newLine();
					bw.write(tf6.getText());
					bw.newLine();
					bw.write(tf7.getText());
					bw.newLine();
					bw.write(tf8.getText());
					bw.newLine();
					bw.write(tf9.getText());
					bw.newLine();
					bw.write(tf10.getText());
					bw.newLine();
					bw.flush(); 
					bw.close();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}		  	        
				startlogs=true;
				menus.setEnabled(true);		  
				dispose();
			}
			
		}
	}
	
	class SaveData extends JFrame implements ActionListener
	{
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




