package controller;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
// AD import java.util.regex.Matcher;
//AD import java.util.regex.Pattern;


import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;


import clustering.Cluster;

public class ShowClusterlt extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	JLabel l1,l2;
	GridBagConstraints gbc=new GridBagConstraints();
	GridBagLayout gb=new GridBagLayout();
	ButtonGroup group = new ButtonGroup();
	JButton jbt1 = new JButton("Global desc");
	JButton jbt2 = new JButton("Individual desc");
	JPanel jp = new JPanel(gb);
	JScrollPane jsp = new JScrollPane(jp);
	List<JRadioButton> jrb = new ArrayList<JRadioButton>(); 
	public ShowClusterlt(final List<Cluster> clt,final int t,final int n, final double[][] vtest, final Matrix dataMatrix)
	{	
		setTitle("Cluster list");  
	//	setSize(500,350);  
		setResizable(true);    
	//	getContentPane().setLayout(gb);
		for(int i=0;i<clt.size();i++)
		{
			jrb.add(i,new JRadioButton()); 
			gbc.gridx=0;
			gbc.gridy=i;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
	//		gb.setConstraints(jrb.get(i),gbc);
	//		getContentPane().add(jrb.get(i));
			jp.add(jrb.get(i),gbc);
			l1 = new JLabel("Cluster"+(n+i)+"(t="+t+") ");
			gbc.gridx=1;
			gbc.gridy=i;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
	//		gb.setConstraints(l1,gbc);
	//		getContentPane().add(l1);
			jp.add(l1,gbc);
			l2 = new JLabel(clt.get(i).getComponentIds().toString());
			gbc.gridx=2;
			gbc.gridy=i;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
	//		gb.setConstraints(l2,gbc);
	//		getContentPane().add(l2);
			jp.add(l2,gbc);
			group.add(jrb.get(i));
		}
	    gbc.gridx=1;
	    gbc.gridy=clt.size();
	    gbc.gridwidth=1;
	    gbc.gridheight=1;
	    gbc.weightx=10;
	    gbc.weighty=10;
	    gbc.anchor=GridBagConstraints.WEST;
   //   gb.setConstraints(jbt1,gbc);
   //   getContentPane().add(jbt1);
	    jp.add(jbt1,gbc);
	    jbt1.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e) {
	    		GlobalDesc gbd = new GlobalDesc(clt, t, n, vtest, dataMatrix);
				@SuppressWarnings("unused")
				WindowListener l = new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						System.exit(0);
					}
				};
				gbd.setLocation(500,100);
			    gbd.pack() ;
				gbd.setVisible(true);	
	    	}
		});
	    gbc.gridx=1;
	    gbc.gridy=clt.size()+1;
	    gbc.gridwidth=1;
	    gbc.gridheight=1;
	    gbc.weightx=10;
	    gbc.weighty=10;
	    gbc.anchor=GridBagConstraints.WEST;
	//  gb.setConstraints(jbt2,gbc);
	//  getContentPane().add(jbt2);
	    jp.add(jbt2,gbc);
	    jbt2.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e) {
	    		for(int i=0;i<clt.size();i++)
    	    	{
    	    		if(jrb.get(i).isSelected())
    	    		{
    	    			IndividualDesc idd = new IndividualDesc(clt, t, i, n, vtest, dataMatrix);
    					@SuppressWarnings("unused")
						WindowListener l = new WindowAdapter()
    					{
    						public void windowClosing(WindowEvent e)
    						{
    							System.exit(0);
    						}
    					};
    					idd.setLocation(500,100);
    				    idd.pack() ;
    					idd.setVisible(true);	
    	    		}
    	    	}
	    	}
		});
	    getContentPane().add(jsp);
	}
	class GlobalDesc extends JFrame
	{
		private static final long serialVersionUID = 1L;
		
		JLabel l1,l2,l3,l4;
		GridBagConstraints gbc=new GridBagConstraints();
		GridBagLayout gb=new GridBagLayout();
		JPanel jp = new JPanel(gb);
		JButton jbs = new JButton("Show matrix");
		JScrollPane jsp = new JScrollPane(jp);
		ButtonGroup group = new ButtonGroup();
		Matrix mt;
		
		@SuppressWarnings("deprecation")
		public GlobalDesc(List<Cluster> clt, int t, int n, double[][] vtest, Matrix dataMatrix)
		{	
			setTitle("Global cluster descritpiton(Vtest tick = "+t+")");    
			setResizable(true);    
			mt = MatrixFactory.sparse(clt.size(),dataMatrix.getColumnCount());
			for(int i=0;i<clt.size();i++)
			{
				l1 = new JLabel("cluster"+(i+n)+" ");
				gbc.gridx=i+1;
				gbc.gridy=0;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
				jp.add(l1,gbc);
				mt.setAsString(Double.toString(i+n),i,0);
				mt.setColumnLabel(0, "Cluster id");
				l2 = new JLabel("nb:"+clt.get(i).getSize());
				gbc.gridx=i+1;
				gbc.gridy=1;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
				jp.add(l2,gbc);
				mt.setAsString(Double.toString(clt.get(i).getSize()),i,1);
				mt.setColumnLabel(1, "Nb agent");
			}
			for(int i=0;i<dataMatrix.getColumnCount();i++)
			{
				boolean isNaN = true;
				for(int j=0;j<clt.size();j++)
				{
					if(!Double.isNaN(vtest[j][i]))
					{
						isNaN = false;
						vtest[j][i]=Math.round(vtest[j][i]*100)/100.0; 
						l3 = new JLabel(Double.toString(vtest[j][i]));
						if(vtest[j][i]>2.00)
							l3.setForeground(Color.blue);
						if(vtest[j][i]<-2.00)
							l3.setForeground(Color.red);
						gbc.gridx=j+1;
						gbc.gridy=i+2;
						gbc.gridwidth=1;
						gbc.gridheight=1;
						gbc.weightx=10;
						gbc.weighty=10;
						gbc.anchor=GridBagConstraints.WEST;					
						jp.add(l3,gbc);
						mt.setAsString(l3.getText(),j,i+2);
					}
				}
				if(!isNaN)
				{
					l4 = new JLabel(dataMatrix.getColumnLabel(i)+" ");
					gbc.gridx=0;
					gbc.gridy=i+2;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;					
					jp.add(l4,gbc);
                    mt.setColumnLabel(i+2, l4.getText());					
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
	 				for(int i=0;i<mt.getColumnCount();i++)
					{
						if(!mt.selectColumns(Ret.NEW,i).isEmpty())
						{
							if(first)
							{
								first = false;
								mn = mt.selectColumns(Ret.NEW,i);
							}
							else
							{
								Matrix mn2 = mn.appendHorizontally(mt.selectColumns(Ret.NEW,i));
								mn = mn2.subMatrix(Ret.NEW, 0, 0, mn2.getRowCount()-1, mn2.getColumnCount()-1);
							    mn.setColumnLabel(mn.getColumnCount()-1, mt.getColumnLabel(i));
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

	class IndividualDesc extends JFrame
	{
		private static final long serialVersionUID = 1L;
		
		JLabel l1,l2,l3;
		GridBagConstraints gbc=new GridBagConstraints();
		GridBagLayout gb=new GridBagLayout();
		JPanel jp = new JPanel(gb);
		JScrollPane jsp = new JScrollPane(jp);
		ButtonGroup group = new ButtonGroup();
		Matrix mt;
		public IndividualDesc(List<Cluster> clt, int t, int n,int ns, double[][] vtest, Matrix dataMatrix)
		{	
			setTitle("Cluster"+(n+ns)+"(t="+t+")");    
			setResizable(true);    
	//		getContentPane().setLayout(gb);
			l1 = new JLabel("|vtest|>2.00 by variable descending:");
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
	//		gb.setConstraints(l1,gbc);
	//		getContentPane().add(l1);
			jp.add(l1,gbc);
	        double[] nums = new double[(int) dataMatrix.getColumnCount()]; 
	        int[] nb = new int[(int) dataMatrix.getColumnCount()]; 
			int m=0;
	        for(int i=0;i<dataMatrix.getColumnCount();i++)
			{		
				if(!Double.isNaN(vtest[n][i]))
				{
					if(Math.abs(vtest[n][i])>2.00)
					{
						vtest[n][i]=Math.round(vtest[n][i]*100)/100.0;
						nums[m]=vtest[n][i]; 
						nb[m]=i;
						m++;
					}
				}
			}
	        sort(nums,nb,m);
	        for(int i=0;i<m;i++)
	        {
	        	l2 = new JLabel(dataMatrix.getColumnLabel(nb[i])+" ");
				gbc.gridx=0;
				gbc.gridy=i+1;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
	//			gb.setConstraints(l2,gbc);
	//			getContentPane().add(l2);
				jp.add(l2,gbc);
				l3 = new JLabel(Double.toString(nums[i]));
				gbc.gridx=1;
				gbc.gridy=i+1;
				gbc.gridwidth=1;
				gbc.gridheight=1;
				gbc.weightx=10;
				gbc.weighty=10;
				gbc.anchor=GridBagConstraints.WEST;
	//			gb.setConstraints(l3,gbc);
	//			getContentPane().add(l3);
				jp.add(l3,gbc);
	        }
	        this.getContentPane().add(jsp);
	        		
		}
		
		private void sort(double[] nums, int[] nb,int m) {
			for(int i=0;i<m;i++)
			{
				for(int j=i+1;j<m;j++)
				{
					if(nums[i] < nums[j])
					{
						double tem = nums[i];
		                nums[i] = nums[j];
		                nums[j] = tem;
		                int n = nb[i];
		                nb[i] = nb[j];
		                nb[j] = n;
		             }
		        }
		    }
			
		}
	}
}

