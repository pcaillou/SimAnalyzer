package simtools.simanalyzer.controller;

import java.awt.BorderLayout;
// AD import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
// AD import java.io.BufferedReader;
// AD import java.io.File;
// AD import java.io.FileReader;
// AD import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
// AD import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
// AD import javax.swing.ScrollPaneLayout;


// AD import observer.SimulationInterface;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;

import simtools.simanalyzer.clustering.Cluster;
// AD import org.ujmp.core.exceptions.MatrixException;
import simtools.simanalyzer.netlogo.NetLogoSimulationController;

// AD import controller.SimAnalyzer.OpenProject;


	
public class oldClusterEval extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	JLabel l1,l2,l3,l4,l5,l6;
	GridBagConstraints gbc=new GridBagConstraints();
	GridBagLayout gb=new GridBagLayout();
	ButtonGroup group = new ButtonGroup();
	List<JRadioButton> jrb1 = new ArrayList<JRadioButton>();
	List<JRadioButton> jrb2 = new ArrayList<JRadioButton>();
	JButton jbHistoryPop = new JButton("History by population");
	JButton jbDefinition = new JButton("History by definition");
	JButton jbs = new JButton("Show matrix");
	JButton jbRI = new JButton("Reproduce initial");
	JButton jbRD = new JButton("Reproduce same period");
	JPanel jp = new JPanel(gb);
	JScrollPane jsp = new JScrollPane(jp);
	Matrix m;

	@SuppressWarnings("deprecation")
	public oldClusterEval(final List<List<Cluster>> cltarray, final List<List<Cluster>> cltarray2, final List<Integer> ticklist, final List<Matrix> MatrixList,List<double[][]> vtestlist, final Matrix concatenatedDataHistory)
	{	
		setTitle("Cluster evaluation");    
		setResizable(true);    
		//getContentPane().setLayout(gb);
		Cluster ct;
		int nm=1;
		int sizeclu=0;
		for(int i=0;i<cltarray.size();i++)
			sizeclu+=cltarray.get(i).size();
		m = MatrixFactory.sparse(sizeclu,concatenatedDataHistory.getColumnCount());
		
		int vtdebx;
		int vtdeby;
		int posx;
		int posy;
		
		for(int i=0;i<cltarray.size();i++)
		{
			for(int j=0;j<cltarray.get(i).size();j++)
			{
				int vt = 0;
				for(int k=0;k<concatenatedDataHistory.getColumnCount();k++)
				{
					
				}
				
			}
			
		}
		
		
		
		for(int i=0;i<cltarray.size();i++)
		{
			if(i!=0)
				nm+=cltarray.get(i-1).size();
			for(int j=0;j<cltarray.get(i).size();j++)
			{
				int vt = 0;
				for(int k=0;k<concatenatedDataHistory.getColumnCount();k++)
				{
					Pattern p = Pattern.compile("T0");
					Matcher m = p.matcher(concatenatedDataHistory.getColumnLabel(k));
					if(!(Double.isNaN(vtestlist.get(i)[j][k])
							|| concatenatedDataHistory.getColumnLabel(k).equals("Id")
							|| concatenatedDataHistory.getColumnLabel(k).equals("Class label")
							|| concatenatedDataHistory.getColumnLabel(k).equals("LABEL-COLOR")
							|| concatenatedDataHistory.getColumnLabel(k).equals("MMId")
							|| concatenatedDataHistory.getColumnLabel(k).equals("MMClass label")	
							|| concatenatedDataHistory.getColumnLabel(k).equals("MMLABEL-COLOR")
							|| m.lookingAt()))
					{
					    vtestlist.get(i)[j][k]=Math.round(vtestlist.get(i)[j][k]*100)/100.0; 
					    if(vtestlist.get(i)[j][k]>2.00 || vtestlist.get(i)[j][k]<-2.00)
					    	vt++;
					}
				}
				ct=cltarray.get(i).get(j);
				ct.agm.calcscores();
				Long score = vt*cltarray.get(i).get(j).getSize();
				l1 = new JLabel("Score: d"+(int)(100*ct.agm.scorestabdesc)+" /p"+(int)(100*ct.agm.scorestabpop)+" ");
				gbc.gridx=j+nm;
				gbc.gridy=0;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
		//		gb.setConstraints(l1,gbc);
		//		getContentPane().add(l1);
				jp.add(l1,gbc);
				m.setAsString(Double.toString(score),(j+nm-1),0);	
				m.setAsString(Double.toString(ct.agm.scorestabdesc),(j+nm-1),0);	
				m.setColumnLabel(0, "ScoreD");
				l2 = new JLabel("Cluster"+(j+nm)+"(t="+ticklist.get(i)+") ");
				gbc.gridx=j+nm;
				gbc.gridy=1;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
		//		gb.setConstraints(l2,gbc);
		//		getContentPane().add(l2);
				jp.add(l2,gbc);
				m.setAsString(Double.toString(j+nm),(j+nm-1),1);
				m.setAsString(Double.toString(ct.agm.scorestabpop),(j+nm-1),1);	
				m.setColumnLabel(1, "ScoreP");
				l3 = new JLabel("nb:"+cltarray.get(i).get(j).getSize());
				gbc.gridx=j+nm;
				gbc.gridy=2;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
			//	gb.setConstraints(l3,gbc);
			//	getContentPane().add(l3);
				jp.add(l3,gbc);
				m.setAsString(Double.toString(cltarray.get(i).get(j).getSize()),(j+nm-1),2);
				m.setColumnLabel(2, "Nb agent");
			}
		}
		int nt=0;
		for(int i=0;i<concatenatedDataHistory.getColumnCount();i++)
		{
			boolean isNaN = true;
			nm = 1;
			for(int j=0;j<cltarray.size();j++)
			{
		        if(j!=0)
		        	nm+=cltarray.get(j-1).size();
				for(int k=0;k<cltarray.get(j).size();k++)
				{
					if(!Double.isNaN(vtestlist.get(j)[k][i]))
					{
						isNaN = false;
						vtestlist.get(j)[k][i]=Math.round(vtestlist.get(j)[k][i]*100)/100.0; 
						l4 = new JLabel(Double.toString(vtestlist.get(j)[k][i]));
						if(vtestlist.get(j)[k][i]>2.00)
							l4.setForeground(Color.blue);
						if(vtestlist.get(j)[k][i]<-2.00)
							l4.setForeground(Color.red);
						gbc.gridx=k+nm;
						gbc.gridy=i+3;
						gbc.gridwidth=1;
						gbc.gridheight=1;
						gbc.weightx=10;
						gbc.weighty=10;
						gbc.anchor=GridBagConstraints.WEST;
				//		gb.setConstraints(l4,gbc);
				//		getContentPane().add(l4);
						jp.add(l4,gbc);
						m.setAsString(l4.getText(),(k+nm-1),i+3);						
					}
				}
			}
			if(!isNaN)
			{
				l5 = new JLabel(concatenatedDataHistory.getColumnLabel(i)+" ");
				gbc.gridx=0;
				gbc.gridy=i+3;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
		//		gb.setConstraints(l5,gbc);
		//		getContentPane().add(l5);
				jp.add(l5,gbc);
				nt = i+3;
				m.setColumnLabel(i+3, l5.getText());
			}
		}
		nm=0;
		for(int i=0;i<cltarray.size();i++)
		{
			if(i!=0)
				nm+=cltarray.get(i-1).size();
			for(int j=0;j<cltarray.get(i).size();j++)
			{
				jrb1.add(j+nm, new JRadioButton());
				gbc.gridx=j+nm+1;
				gbc.gridy=nt+1;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
			//	gb.setConstraints(jrb1.get(j+nm),gbc);
    		//	getContentPane().add(jrb1.get(j+nm));
				jp.add(jrb1.get(j+nm),gbc);
				group.add(jrb1.get(j+nm));
			}
		}
		gbc.gridx=0;
		gbc.gridy=nt+1;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
	//	gb.setConstraints(jb1,gbc);
	//	getContentPane().add(jb1);
		jp.add(jbHistoryPop,gbc);
		jbHistoryPop.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				List<Cluster> lc = new ArrayList<Cluster>();
				List<Integer> tll = new ArrayList<Integer>();
				for(int i=0;i<cltarray.size();i++)
				{
					for(int j=0;j<cltarray.get(i).size();j++)
					{
						lc.add(cltarray.get(i).get(j));
						tll.add(ticklist.get(i));
					}
				}
				for(int i=0;i<jrb1.size();i++)
				{						
					if(jrb1.get(i).isSelected())
					{
						HistoryPo hp = new HistoryPo(lc.get(i), MatrixList, i, tll.get(i), ticklist, concatenatedDataHistory);
    					@SuppressWarnings("unused")
						WindowListener l = new WindowAdapter()
    					{
    						public void windowClosing(WindowEvent e)
    						{
    							System.exit(0);
    						}
    					};
    					hp.setLocation(500,100);
    				    hp.pack() ;
    					hp.setVisible(true);	

    					FAgModel fag=new FAgModel(lc.get(i).agm);
//    					fag.setLocation(100,100);
 //   				    fag.pack() ;
  //  					fag.setVisible(true);
					}
				}
			}
		});
		gbc.gridx=0;
		gbc.gridy=nt+2;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
	//	gb.setConstraints(jb2,gbc);
	//	getContentPane().add(jb2);
		jp.add(jbDefinition,gbc);
		gbc.gridx=0;
		gbc.gridy=nt+4;
		jp.add(jbRI,gbc);
		gbc.gridy=nt+3;
		jp.add(jbRD,gbc);
		for(int i=0;i<cltarray.size();i++)
		{
			nt++;
			jrb2.add(i, new JRadioButton());
			gbc.gridx=1;
			gbc.gridy=nt;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
	//		gb.setConstraints(jrb2.get(i),gbc);
	//		getContentPane().add(jrb2.get(i));
			jp.add(jrb2.get(i),gbc);
			group.add(jrb2.get(i));
			l6 = new JLabel("Clusterer"+i+"(t="+ticklist.get(i)+")");
			gbc.gridx=2;
			gbc.gridy=nt;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
	//		gb.setConstraints(l6,gbc);
    //		getContentPane().add(l6);
			jp.add(l6,gbc);
		}
		jbDefinition.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				List<List<Cluster>> clut = new ArrayList<List<Cluster>>();
				List<Integer> tll = new ArrayList<Integer>();
				List<Matrix> mll = new ArrayList<Matrix>();
				for(int i=0;i<jrb2.size();i++)
				{						
					if(jrb2.get(i).isSelected())
					{
						int n=i;
						int m=i;
						int size=0;
						for(int j=0;j<i;j++)
						{
							size += cltarray.get(j).size();
						}
						for(int j=0;j<MatrixList.size();j++)
						{
							n+=j;
							if(i<=j)
							{
								clut.add(cltarray2.get(n));
								tll.add(ticklist.get(m));
								mll.add(MatrixList.get(m));
								m++;
							}
						}
						HistoryDe hd = new HistoryDe(clut, i, tll, mll,concatenatedDataHistory,size);
    					@SuppressWarnings("unused")
						WindowListener l = new WindowAdapter()
    					{
    						public void windowClosing(WindowEvent e)
    						{
    							System.exit(0);
    						}
    					};
    					hd.setLocation(500,100);
    				    hd.pack() ;
    					hd.setVisible(true);	
					}
				}
			}
		});
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
		jp.add(jbs,gbc);
		jbs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				Matrix mn = null;
				boolean first = true;
 				for(int i=0;i<m.getColumnCount();i++)
				{
					if(!m.selectColumns(Ret.NEW,i).isEmpty())
					{
						if(first)
						{
							first = false;
							mn = m.selectColumns(Ret.NEW, i);
						}
						else
						{
							Matrix mn2 = mn.appendHorizontally(m.selectColumns(Ret.NEW,i));
							mn = mn2.subMatrix(Ret.NEW, 0, 0, mn2.getRowCount()-1, mn2.getColumnCount()-1);
						    mn.setColumnLabel(mn.getColumnCount()-1, m.getColumnLabel(i));
						}
					}
				}
 				mn=mn.transpose(Ret.NEW);
				mn.showGUI();
			}
		});
		getContentPane().add(jsp);
		jbRD.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				try {
					@SuppressWarnings("unused")
					List<Cluster> lc = new ArrayList<Cluster>();
					@SuppressWarnings("unused")
					List<Integer> tll = new ArrayList<Integer>();
					int nj=0;
					for(int i=0;i<cltarray.size();i++)
					{
						for(int j=0;j<cltarray.get(i).size();j++)
						{
							if(jrb1.get(nj).isSelected())
							{
								SimulationController.clustererTarget=SimulationController.wcl.get(i);
								SimulationController.clusterTarget=cltarray.get(i).get(j);
								SimulationController.ResMat=SimulationController.clusterTarget.vtestsm;
								SimulationController.steptarget=i;
								SimulationController.noclusttarget=j;
								SimulationController.typeReRun=0;
								SimulationController.datamatclust=concatenatedDataHistory;
								@SuppressWarnings("unused")
								ReRunParam p = new ReRunParam();
								@SuppressWarnings("unused")
								WindowListener l = new WindowAdapter()
								{
									public void windowClosing(WindowEvent e)
								    {
										System.exit(0);
									}
								};
//								SimAnalyzer.restartnetlogo=true;									
							}
							nj++;
						}
					}
					for(int i=0;i<jrb1.size();i++)
					{						
						if(jrb1.get(i).isSelected())
						{
						}
					}
//					SimAnalyzer.nlsc.reRunInit();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		jbRI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				try {
					@SuppressWarnings("unused")
					List<Cluster> lc = new ArrayList<Cluster>();
					@SuppressWarnings("unused")
					List<Integer> tll = new ArrayList<Integer>();
					int nj=0;
					for(int i=0;i<cltarray.size();i++)
					{
						int nbagt=0;
						for(int j=0;j<cltarray.get(i).size();j++)
						{
							nbagt=nbagt+(int)cltarray.get(i).get(j).getSize();
						}
						for(int j=0;j<cltarray.get(i).size();j++)
						{
							if(jrb1.get(nj).isSelected())
							{
								SimulationController.clustererTarget=SimulationController.wcl.get(i);
								SimulationController.clusterTarget=cltarray.get(i).get(j);
								SimulationController.ResMat=SimulationController.clusterTarget.vtestsm;
								SimulationController.steptarget=i;
								SimulationController.noclusttarget=j;
								SimulationController.typeReRun=1;
								
								SimulationController.clustpopprop=(double)cltarray.get(i).get(j).getSize()/(double)nbagt;
								SimulationController.datamatclust=concatenatedDataHistory;
								@SuppressWarnings("unused")
								ReRunParam p = new ReRunParam();
								@SuppressWarnings("unused")
								WindowListener l = new WindowAdapter()
								{
									public void windowClosing(WindowEvent e)
								    {
										System.exit(0);
									}
								};
//								SimAnalyzer.restartnetlogo=true;									
							}
							nj++;
						}
					}
					for(int i=0;i<jrb1.size();i++)
					{						
						if(jrb1.get(i).isSelected())
						{
						}
					}
//					SimAnalyzer.nlsc.reRunInit();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	       
	}
	
	class HistoryPo extends JFrame
	{
		private static final long serialVersionUID = 1L;
		
		JLabel l1,l2,l3,l4,l5;
		GridBagConstraints gbc=new GridBagConstraints();
		GridBagLayout gb=new GridBagLayout();
		JPanel jp = new JPanel(gb);
		JButton jbs = new JButton("Show matrix");
		JScrollPane jsp = new JScrollPane(jp);
		Matrix m;
		
		@SuppressWarnings("deprecation")
		public HistoryPo(Cluster clu, List<Matrix> ml, int n, int t, List<Integer> tl, Matrix cltmg)
		{
			setTitle("Cluster evolution by population(Cluster"+(n+1)+")");    
			setResizable(true);    
		//	getContentPane().setLayout(gb);
			List<Boolean> isNaN = new ArrayList<Boolean>();
			m = MatrixFactory.sparse(tl.size(),cltmg.getColumnCount());
			for(int i=0;i<cltmg.getColumnCount();i++)
				isNaN.add(i,true);
			for(int i=0;i<tl.size();i++)
			{
				int vt=0;
				if(t<=tl.get(i))
				{
					double[] vtest= Vtest.Vtest(clu,ml.get(i));
					for(int j=0;j<ml.get(i).getColumnCount();j++)
					{
						if(!Double.isNaN(vtest[j]))
						{
							isNaN.set(j,false);
							vtest[j]=Math.round(vtest[j]*100)/100.0;
							Pattern p = Pattern.compile("T0");
							Matcher mt = p.matcher(ml.get(i).getColumnLabel(j));
							if((vtest[j]>2.00 || vtest[j]<-2.00) 
									&& !(ml.get(i).getColumnLabel(j).equals("Id"))
									&& !(ml.get(i).getColumnLabel(j).equals("Class label"))
									&& !(ml.get(i).getColumnLabel(j).equals("LABEL-COLOR"))
									&& !(ml.get(i).getColumnLabel(j).equals("MMId"))
									&& !(ml.get(i).getColumnLabel(j).equals("MMClass label"))
									&& !(ml.get(i).getColumnLabel(j).equals("MMLABEL-COLOR"))
									&& !mt.lookingAt())
								vt++;
							l4 = new JLabel(Double.toString(vtest[j]));
							if(vtest[j]>2.00)
								l4.setForeground(Color.blue);
							if(vtest[j]<-2.00)
								l4.setForeground(Color.red);
							gbc.gridx=i+1;
							gbc.gridy=j+3;
							gbc.gridwidth=1;
							gbc.gridheight=1;
							gbc.weightx=10;
							gbc.weighty=10;
							gbc.anchor=GridBagConstraints.WEST;
					//		gb.setConstraints(l4,gbc);
					//		getContentPane().add(l4);
							jp.add(l4,gbc);
							m.setAsString(l4.getText(), i,j+3);
						}
					}
					Long score = vt*clu.getSize();
					l1 = new JLabel("Score:"+score+" ");
					gbc.gridx=i+1;
					gbc.gridy=0;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
				//	gb.setConstraints(l1,gbc);
				//	getContentPane().add(l1);
					jp.add(l1,gbc);
					m.setAsString(Double.toString(score),i,0);
					m.setColumnLabel(0, "Score");
				}													
				l2 = new JLabel("Cluster"+(n+1)+"(t="+tl.get(i)+") ");
				gbc.gridx=i+1;
				gbc.gridy=1;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
			//	gb.setConstraints(l2,gbc);
			//	getContentPane().add(l2);
				jp.add(l2,gbc);
				m.setAsString(Double.toString(n+1),i,1);
				m.setColumnLabel(1, "Cluster id");
				l3 = new JLabel("nb:"+clu.getSize());
				gbc.gridx=i+1;
				gbc.gridy=2;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
			//	gb.setConstraints(l3,gbc);
			//	getContentPane().add(l3);
				jp.add(l3,gbc);
				m.setAsString(Double.toString(clu.getSize()),i,2);
				m.setColumnLabel(2, "Nb agent");
			}	
			for(int i=0;i<isNaN.size();i++)
			{
				if(!isNaN.get(i))
				{
					l5 = new JLabel(cltmg.getColumnLabel(i)+" ");
					gbc.gridx=0;
					gbc.gridy=i+3;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
				//	gb.setConstraints(l5,gbc);
				//	getContentPane().add(l5);
					jp.add(l5,gbc);
					m.setColumnLabel(i+3, l5.getText());
				}					
			}
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
			jp.add(jbs,gbc);
			jbs.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) {
					Matrix mn = null;
					boolean first = true;
	 				for(int i=0;i<m.getColumnCount();i++)
					{
						if(!m.selectColumns(Ret.NEW,i).isEmpty())
						{
							if(first)
							{
								first = false;
								mn = m.selectColumns(Ret.NEW, i);
							}
							else
							{
								Matrix mn2 = mn.appendHorizontally(m.selectColumns(Ret.NEW,i));
								mn = mn2.subMatrix(Ret.NEW, 0, 0, mn2.getRowCount()-1, mn2.getColumnCount()-1);
							    mn.setColumnLabel(mn.getColumnCount()-1, m.getColumnLabel(i));
							}
						}
					}
	 				mn=mn.transpose(Ret.NEW);
					mn.showGUI();
				}
			});
			this.getContentPane().add(jsp);
		}
	}
	
	class HistoryDe extends JFrame 
	{
		private static final long serialVersionUID = 1L;
		
		JLabel l1,l2,l3,l4,l5;
		GridBagConstraints gbc=new GridBagConstraints();
		GridBagLayout gb=new GridBagLayout();
		JPanel jp = new JPanel(gb);
		JButton jbs = new JButton("Show matrix");
		JScrollPane jsp = new JScrollPane(jp);
		ButtonGroup group = new ButtonGroup();
		List<JRadioButton> jrb = new ArrayList<JRadioButton>(); 
		Matrix m; 
		@SuppressWarnings("deprecation")
		public HistoryDe(List<List<Cluster>> cltarray, int cn, List<Integer> tl, List<Matrix> ml, Matrix cltmg, int size)
		{	
			setTitle("Cluster evolution by definition(Clusterer"+cn+")");    
			setResizable(true);    
		//	getContentPane().setLayout(gb);
			int nm=1;
			List<Boolean> isNaN = new ArrayList<Boolean>();
			int sizem = size;
			for(int i=0;i<cltarray.size();i++)
				sizem+=cltarray.get(i).size();
			m = MatrixFactory.sparse(sizem,cltmg.getColumnCount());
			for(int i=0;i<cltmg.getColumnCount();i++)
				isNaN.add(i,true);
			for(int i=0;i<cltarray.size();i++)
			{
			    if(i!=0)
					nm+=cltarray.get(i-1).size();
				for(int j=0;j<cltarray.get(i).size();j++)
				{
					double[] vtest = Vtest.Vtest(cltarray.get(i).get(j),ml.get(i));
					int vt = 0;
					for(int k=0;k<ml.get(i).getColumnCount();k++)
					{
						if(!Double.isNaN(vtest[k]))
						{
							isNaN.set(k,false);
							vtest[k]=Math.round(vtest[k]*100)/100.0;
							Pattern p = Pattern.compile("T0");
							Matcher mt = p.matcher(ml.get(i).getColumnLabel(k));
							if((vtest[k]>2.00 || vtest[k]<-2.00) 
									&& !(ml.get(i).getColumnLabel(k).equals("Id"))
									&& !(ml.get(i).getColumnLabel(k).equals("Class label"))
									&& !(ml.get(i).getColumnLabel(k).equals("LABEL-COLOR"))
									&& !(ml.get(i).getColumnLabel(k).equals("MMId"))
									&& !(ml.get(i).getColumnLabel(k).equals("MMClass label"))
									&& !(ml.get(i).getColumnLabel(k).equals("MMLABEL-COLOR"))
									&& !mt.lookingAt())
								vt++;
							l4 = new JLabel(Double.toString(vtest[k]));
							if(vtest[k]>2.00)
								l4.setForeground(Color.blue);
							if(vtest[k]<-2.00)
								l4.setForeground(Color.red);
							gbc.gridx=j+nm;
							gbc.gridy=k+3;
							gbc.gridwidth=1;
							gbc.gridheight=1;
							gbc.weightx=10;
							gbc.weighty=10;
							gbc.anchor=GridBagConstraints.WEST;
				//			gb.setConstraints(l4,gbc);
				//			getContentPane().add(l4);
							jp.add(l4,gbc);
							m.setAsString(l4.getText(),(j+nm-1),k+3);							
						}
					}
					
					Long score = vt*cltarray.get(i).get(j).getSize();
					l1 = new JLabel("i"+cltarray.get(i).get(j).getId()+" Score:"+score+" ");
					gbc.gridx=j+nm;
					gbc.gridy=0;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
				//	gb.setConstraints(l1,gbc);
				//	getContentPane().add(l1);
					jp.add(l1,gbc);
					m.setAsString(Double.toString(score),(j+nm-1),0);
					m.setColumnLabel(0, "Score");
					l2 = new JLabel("Cluster"+(j+size+1)+"(t="+tl.get(i)+") ");
					gbc.gridx=j+nm;
					gbc.gridy=1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
				//	gb.setConstraints(l2,gbc);
				//	getContentPane().add(l2);
					jp.add(l2,gbc);
					m.setAsString(Double.toString(j+size+1),(j+nm-1),1);
					m.setColumnLabel(1, "Cluster id");
					l3 = new JLabel("nb:"+cltarray.get(i).get(j).getSize());
					gbc.gridx=j+nm;
					gbc.gridy=2;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
				//	gb.setConstraints(l3,gbc);
				//	getContentPane().add(l3);
					jp.add(l3,gbc);
					m.setAsString(Double.toString(cltarray.get(i).get(j).getSize()),(j+nm-1),2);
					m.setColumnLabel(2, "Nb agent");
				}		
			}
			for(int i=0;i<isNaN.size();i++)
			{
				if(!isNaN.get(i))
				{
					l5 = new JLabel(cltmg.getColumnLabel(i)+" ");
					gbc.gridx=0;
					gbc.gridy=i+3;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
			//		gb.setConstraints(l5,gbc);
			//		getContentPane().add(l5);
					jp.add(l5,gbc);
					m.setColumnLabel(i+3, l5.getText());
				}					
			}
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
			jp.add(jbs,gbc);
			jbs.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) {
					Matrix mn = null;
					boolean first = true;
	 				for(int i=0;i<m.getColumnCount();i++)
					{
						if(!m.selectColumns(Ret.NEW,i).isEmpty())
						{
							if(first)
							{
								first = false;
								mn = m.selectColumns(Ret.NEW, i);
							}
							else
							{
								Matrix mn2 = mn.appendHorizontally(m.selectColumns(Ret.NEW,i));
								mn = mn2.subMatrix(Ret.NEW, 0, 0, mn2.getRowCount()-1, mn2.getColumnCount()-1);
							    mn.setColumnLabel(mn.getColumnCount()-1, m.getColumnLabel(i));
							}
						}
					}
	 				mn=mn.transpose(Ret.NEW);
					mn.showGUI();
				}
			});
			this.getContentPane().add(jsp);
		}	
	}
}
