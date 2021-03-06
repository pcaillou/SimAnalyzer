package controller;

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

import netlogo.NetLogoSimulationController;

// AD import observer.SimulationInterface;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
// AD import org.ujmp.core.exceptions.MatrixException;

// AD import controller.SimAnalyzer.OpenProject;


import clustering.Cluster;

public class ClusterEval extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	JLabel l1,l2,l3,l4,l5,l6;
	GridBagConstraints gbc=new GridBagConstraints();
	GridBagLayout gb=new GridBagLayout();
	ButtonGroup group = new ButtonGroup();
	List<JRadioButton> jrb1 = new ArrayList<JRadioButton>();
	List<JRadioButton> jrb2 = new ArrayList<JRadioButton>();
	JButton jb1 = new JButton("History by population");
	JButton jb2 = new JButton("History by definition");
	JButton jbs = new JButton("Show matrix");
	JButton jbRI = new JButton("Reproduce initial");
	JButton jbRD = new JButton("Reproduce same period");
	JPanel jp = new JPanel(gb);
	JScrollPane jsp = new JScrollPane(jp);
	Matrix m;
	@SuppressWarnings("deprecation")
	public ClusterEval(final List<List<Cluster>> cltarray, final List<List<Cluster>> cltarray2, final List<Integer> tl, final List<Matrix> ml,List<double[][]> vtest, final Matrix cltmg)
	{	
		setTitle("Cluster evaluation");    
		setResizable(true);    
		//getContentPane().setLayout(gb);
		Cluster ct;
		int nm=1;
		int size=0;
		for(int i=0;i<cltarray.size();i++)
			size+=cltarray.get(i).size();
		m = MatrixFactory.sparse(size,cltmg.getColumnCount());
		for(int i=0;i<cltarray.size();i++)
		{
			if(i!=0)
				nm+=cltarray.get(i-1).size();
			for(int j=0;j<cltarray.get(i).size();j++)
			{
				int vt = 0;
				for(int k=0;k<cltmg.getColumnCount();k++)
				{
					Pattern p = Pattern.compile("T0");
					Matcher m = p.matcher(cltmg.getColumnLabel(k));
					if(!(Double.isNaN(vtest.get(i)[j][k])
							|| cltmg.getColumnLabel(k).equals("Id")
							|| cltmg.getColumnLabel(k).equals("Class label")
							|| cltmg.getColumnLabel(k).equals("LABEL-COLOR")
							|| cltmg.getColumnLabel(k).equals("MMId")
							|| cltmg.getColumnLabel(k).equals("MMClass label")	
							|| cltmg.getColumnLabel(k).equals("MMLABEL-COLOR")
							|| m.lookingAt()))
					{
					    vtest.get(i)[j][k]=Math.round(vtest.get(i)[j][k]*100)/100.0; 
					    if(vtest.get(i)[j][k]>2.00 || vtest.get(i)[j][k]<-2.00)
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
				l2 = new JLabel("Cluster"+(j+nm)+"(t="+tl.get(i)+") ");
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
		for(int i=0;i<cltmg.getColumnCount();i++)
		{
			boolean isNaN = true;
			nm = 1;
			for(int j=0;j<cltarray.size();j++)
			{
		        if(j!=0)
		        	nm+=cltarray.get(j-1).size();
				for(int k=0;k<cltarray.get(j).size();k++)
				{
					if(!Double.isNaN(vtest.get(j)[k][i]))
					{
						isNaN = false;
						vtest.get(j)[k][i]=Math.round(vtest.get(j)[k][i]*100)/100.0; 
						l4 = new JLabel(Double.toString(vtest.get(j)[k][i]));
						if(vtest.get(j)[k][i]>2.00)
							l4.setForeground(Color.blue);
						if(vtest.get(j)[k][i]<-2.00)
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
		jp.add(jb1,gbc);
		jb1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				List<Cluster> lc = new ArrayList<Cluster>();
				List<Integer> tll = new ArrayList<Integer>();
				for(int i=0;i<cltarray.size();i++)
				{
					for(int j=0;j<cltarray.get(i).size();j++)
					{
						lc.add(cltarray.get(i).get(j));
						tll.add(tl.get(i));
					}
				}
				for(int i=0;i<jrb1.size();i++)
				{						
					if(jrb1.get(i).isSelected())
					{
						HistoryPo hp = new HistoryPo(lc.get(i), ml, i, tll.get(i), tl, cltmg);
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
    					fag.setLocation(100,100);
    				    fag.pack() ;
    					fag.setVisible(true);
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
		jp.add(jb2,gbc);
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
			l6 = new JLabel("Clusterer"+i+"(t="+tl.get(i)+")");
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
		jb2.addActionListener(new ActionListener()
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
						for(int j=0;j<ml.size();j++)
						{
							n+=j;
							if(i<=j)
							{
								clut.add(cltarray2.get(n));
								tll.add(tl.get(m));
								mll.add(ml.get(m));
								m++;
							}
						}
						HistoryDe hd = new HistoryDe(clut, i, tll, mll,cltmg,size);
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
								SimulationController.datamatclust=cltmg;
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
								SimulationController.datamatclust=cltmg;
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
class ReRunParam extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	JLabel l, l0,lt,  l1, l2, l3, l4, l5,  l6,  l7;
	JTextField l0t, lgname, lvmin, lvmax, lvstep, lnbxp, l06,  l07;
	JButton jbCancel, jbtOK;
	public ReRunParam() 
	{
		setTitle("ReRun Parameters");  
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
			l0t = new JTextField("");
		l = new JLabel("Global parameter Name:");
	    lt = new JLabel("type (1:NeltLogo/2:Logs): "); 
//			l0t = new JTextField(br.readLine());
	    l1 = new JLabel("Global parameter name: "); 
		panel_1.add(l1);  
		lgname = new JTextField("");
		panel_1.add(lgname);  
	    l2 = new JLabel("Param min: ");  
		panel_1.add(l2);  
			lvmin = new JTextField("0");
		panel_1.add(lvmin);  
		l3 = new JLabel("Param Max: ");  
		panel_1.add(l3);  
			lvmax = new JTextField("0");
		panel_1.add(lvmax);  
		l4 = new JLabel("Param step: ");  
		panel_1.add(l4);  
			lvstep = new JTextField("1");
		panel_1.add(lvstep);  
		l5 = new JLabel("Xp each step: ");  
		panel_1.add(l5);  
			lnbxp = new JTextField("1");
		panel_1.add(lnbxp);  
		l6 = new JLabel("First column clustering: ");  
//		panel_1.add(l6);  
		//	l06 = new JTextField(br.readLine());
//		panel_1.add(l06);  
		l7 = new JLabel("Last column clustering: ");  
//		panel_1.add(l7);  
//			l07 = new JTextField(br.readLine());
//		panel_1.add(l07);  
		jbCancel = new JButton("Concel");   
		panel_1.add(jbCancel);   
		jbCancel.addActionListener(this);
		jbtOK = new JButton("OK");   
		panel_1.add(jbtOK);   
		jbtOK.addActionListener(this);
		setVisible(true);  			
    }

	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		@SuppressWarnings("unused")
		Matrix m = NetLogoSimulationController.DataMatrix;
		if (src.equals(jbCancel))
		{
			dispose();
		}
		if (src.equals(jbtOK))
		{
			SimulationController.glovvarrerun=lgname.getText();
			SimulationController.globvarmin=Double.parseDouble(lvmin.getText());
			SimulationController.globvarmax=Double.parseDouble(lvmax.getText());
			SimulationController.globvarstep=Double.parseDouble(lvstep.getText());
			SimulationController.nbsteprerun=Integer.parseInt(lnbxp.getText());
			SimulationController.ResMat.setAsDouble(SimulationController.globvarcurrent, 0,0);
	    	SimulationController.ResMat.setRowLabel(0, lgname.getText());
	    	SimulationController.ResMat.setRowLabel(2, "size");
		SimAnalyzer.restartnetlogo=true;									
			dispose();
			
		}
		
	}
}
	
