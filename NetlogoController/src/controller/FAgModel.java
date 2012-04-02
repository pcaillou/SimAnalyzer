package controller;

import java.awt.BorderLayout;
import java.awt.Choice;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import netlogo.NetLogoSimulationController;
import controller.SimAnalyzer;

import observer.SimulationInterface;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.exceptions.MatrixException;

import clustering.Cluster;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import weka.core.matrix.Maths;

public class FAgModel extends JFrame implements ActionListener
{
	JLabel l1,l2,l3,l4,l5,l6;
	GridBagConstraints gbcg=new GridBagConstraints();
	GridBagLayout gbg=new GridBagLayout();
	JPanel jpg = new JPanel(gbg);
	GridBagConstraints gbcs=new GridBagConstraints();
	GridBagLayout gbs=new GridBagLayout();
	JPanel jps = new JPanel(gbs);
	JCheckBox jcheckVariance;
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
	JFreeChart chartg;
	ChartPanel chartPanelg;
	JScrollPane jsp = new JScrollPane(jpg);
	List<JRadioButton> jrbx = new ArrayList<JRadioButton>();
	List<JRadioButton> jrby = new ArrayList<JRadioButton>();
	List<Integer> jrbi = new ArrayList<Integer>();
	Matrix m;
	AgModel agm;
	Cluster clbase;
	Matrix mbase;
	Matrix mbasedef;
	int nbstep;
	int v1,v2;
	int colbase;
	boolean created=false;
	
	public FAgModel(AgModel agmd)
	{	
		agm=agmd;
		agm.calcscores();
		setTitle("Cluster evaluation");    
		setResizable(true);    
		//getContentPane().setLayout(gb);
		int nm=1;
		int size=0;
		int nx=0;
		int ny=0;
		clbase=agm.clustinit;
		colbase=clbase.idtickinit;
		mbase=clbase.vtestsm;
		mbasedef=clbase.vtestsmdef;
		nbstep=(int)mbase.getColumnCount();
		double vtd,vtd2,vtd3;
		String vg;
		
		//	getContentPane().setLayout(gb);
			List<Boolean> isNaN = new ArrayList<Boolean>();
			m = MatrixFactory.sparse(mbase.getRowCount(),mbase.getColumnCount());
			for(int i=0;i<mbase.getRowCount();i++)
				isNaN.add(i,true);
			for(int i=3;i<mbase.getRowCount();i++)
			{
				int vt=0;
				if(!Double.isNaN(mbase.getAsDouble(i,colbase)))
				{
					isNaN.set(i,false);
					nx=0;
					
					l4 = new JLabel(mbase.getRowLabel(i));
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					//		gb.setConstraints(l4,gbc);
					//		getContentPane().add(l4);
					jp.add(l4,gbc);
					m.setColumnLabel(i+3, l4.getText());
					nx++;

					
					vtd=Math.round(mbase.getAsDouble(i,colbase)*100)/100.0;
					Pattern p = Pattern.compile("T0");
//							Matcher mt = p.matcher(ml.get(i).getColumnLabel(j));
					if((vtd>2.00 || vtd<-2.00)) 
/*									&& !(ml.get(i).getColumnLabel(j).equals("Id"))
									&& !(ml.get(i).getColumnLabel(j).equals("Class label"))
									&& !(ml.get(i).getColumnLabel(j).equals("LABEL-COLOR"))
									&& !(ml.get(i).getColumnLabel(j).equals("MMId"))
									&& !(ml.get(i).getColumnLabel(j).equals("MMClass label"))
									&& !(ml.get(i).getColumnLabel(j).equals("MMLABEL-COLOR"))
									&& !mt.lookingAt())*/
						vt++;
					l4 = new JLabel(" "+Double.toString(vtd));
					if(vtd>2.00)
						l4.setForeground(Color.blue);
					if(vtd<-2.00)
						l4.setForeground(Color.red);
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					//		gb.setConstraints(l4,gbc);
					//		getContentPane().add(l4);
					jp.add(l4,gbc);
					nx++;
					//graph
					nx++;
					
					JRadioButton jbb=new JRadioButton();
					jbb.addActionListener(this);
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					jrbx.add(jbb);
					jrbi.add(i);
					jp.add(jbb,gbc);
										
					nx++;

					jbb=new JRadioButton();
					jbb.addActionListener(this);
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					jrby.add(jbb);
					jp.add(jbb,gbc);
					nx++;



					m.setAsString(l4.getText(), i,2);

					vtd=Math.round(clbase.avgsm.getAsDouble(i,colbase)*100)/100.0;
					vtd2=Math.round(clbase.stderrsm.getAsDouble(i,colbase)*100)/100.0;
					vg=new String(""+vtd+" ("+vtd2+") ");
					l4 = new JLabel(vg);
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					jp.add(l4,gbc);
					m.setAsString(l4.getText(), i,3);
					nx++;

					vtd=Math.round(clbase.avglobsm.getAsDouble(i,colbase)*100)/100.0;
					vtd2=Math.round(clbase.stdglobsm.getAsDouble(i,colbase)*100)/100.0;
					vg=new String(""+vtd+" ("+vtd2+") ");
					l4 = new JLabel(vg);
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					jp.add(l4,gbc);
					m.setAsString(l4.getText(), i,4);
					nx++;
					
					vtd=Math.round(agm.scorevar[i]*100)/100.0;
					vtd2=agm.clustinit.avgsm.selectRows(Ret.LINK, i).getStdValue();
					vtd2=Math.round(vtd2*100)/100.0;
					vtd3=agm.clustinit.avgsm.abs(Ret.LINK).selectRows(Ret.LINK, i).getMeanValue();
					vtd3=Math.round(vtd3*100)/100.0;
					l4 = new JLabel(Double.toString(vtd)+"(ev*"+vtd2+"/"+vtd3+") ");
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					jp.add(l4,gbc);
					m.setAsString(l4.getText(), i,5);
					nx++;

					vtd=Math.round(agm.scorevardef[i]*100)/100.0;
					vtd2=agm.clustinit.avgsmdef.selectRows(Ret.LINK, i).getStdValue();
					vtd2=Math.round(vtd2*100)/100.0;
					vtd3=agm.clustinit.avgsmdef.abs(Ret.LINK).selectRows(Ret.LINK, i).getMeanValue();
					vtd3=Math.round(vtd3*100)/100.0;
					l4 = new JLabel(Double.toString(vtd)+"(ev*"+vtd2+"/"+vtd3+") ");
					gbc.gridx=nx;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
					jp.add(l4,gbc);
					m.setAsString(l4.getText(), i,6);
					nx++;


					DefaultCategoryDataset dataset = new DefaultCategoryDataset();

					// row keys...
					String series1 = "";

					// column keys...
					String category1 = "1";
					String category2 = "2";

					for(int j=0;j<mbase.getColumnCount();j++)
					{
						series1=new String("c"+j);
						dataset.addValue(1, series1, category1);						
						
					}
					for(int j=0;j<mbase.getColumnCount();j++)
					{
						series1=new String("c"+j+nbstep);
						dataset.addValue(1, series1, category2);						
						
					}
					
					JFreeChart chart = ChartFactory.createStackedBarChart("", // chart
							// title
							"", // domain axis label
							"", // range axis label
							dataset, // data
							PlotOrientation.HORIZONTAL, // orientation
							false, // include legend
							true, // tooltips?
							false // URLs?
							);

					CategoryPlot plot = (CategoryPlot) chart.getPlot();
					plot.setOutlineVisible(false);
					plot.getDomainAxis().setVisible(false);
					plot.getRangeAxis().setVisible(false);
					
					for(int j=0;j<mbase.getColumnCount();j++)
					{
						CategoryItemRenderer renderer = plot.getRenderer();
						double val=mbase.getAsDouble(i,j);
						renderer.setSeriesPaint(j, Color.white);
						if (val>2)
							renderer.setSeriesPaint(j, new Color(200-(int)(Math.min(200,(val-2)*20)),200-(int)(Math.min(200,(val-2)*20)),255));
						if (val<-2)
							renderer.setSeriesPaint(j, new Color(255,200-(int)(Math.min(200,(-val-2)*20)),200-(int)(Math.min(200,(-val-2)*20))));

						val=mbasedef.getAsDouble(i,j);
						renderer.setSeriesPaint(j+nbstep, Color.white);
						if (val>2)
						renderer.setSeriesPaint(j+nbstep, new Color(200-(int)(Math.min(200,(val-2)*20)),200-(int)(Math.min(200,(val-2)*20)),255));
						if (val<-2)
						renderer.setSeriesPaint(j+nbstep, new Color(255,200-(int)(Math.min(200,(-val-2)*20)),200-(int)(Math.min(200,(-val-2)*20))));
						
					}
					chart.setBorderVisible(false);
					
					ChartPanel chartPanel = new ChartPanel(chart);
			        chartPanel.setPreferredSize(new java.awt.Dimension(100, 25));
			        
					JPanel monPanel = new JPanel(new BorderLayout());
					monPanel.add(chartPanel, BorderLayout.CENTER);
					
					gbc.gridx=2;
					gbc.gridy=i+1;
					gbc.gridwidth=1;
					gbc.gridheight=1;
					gbc.weightx=10;
					gbc.weighty=10;
					gbc.anchor=GridBagConstraints.WEST;
				//	gb.setConstraints(l1,gbc);
				//	getContentPane().add(l1);
					jp.add(monPanel,gbc);

				
				}					
			}

			
			l1 = new JLabel("Tick:"+mbase.getAsDouble(1,colbase)+" ");
			gbc.gridx=0;
			gbc.gridy=2;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
		//	gb.setConstraints(l1,gbc);
		//	getContentPane().add(l1);
			jp.add(l1,gbc);
//			m.setAsString(Double.toString(score),i,0);
			m.setColumnLabel(0, "Tick");

			l1 = new JLabel("StabilityPop: "+((double)((int)(agm.scorestabpop*10000)))/100+"%");
			
			gbc.gridx=2;
			gbc.gridy=2;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
		//	gb.setConstraints(l1,gbc);
		//	getContentPane().add(l1);
			jp.add(l1,gbc);
			
			l1 = new JLabel("StabilityDesc: "+((double)((int)(agm.scorestabdesc*10000)))/100+"%");
			
			gbc.gridx=2;
			gbc.gridy=1;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
		//	gb.setConstraints(l1,gbc);
		//	getContentPane().add(l1);
			jp.add(l1,gbc);
			
			l3 = new JLabel("nb:"+mbase.getAsDouble(2,colbase));
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
	//	gb.setConstraints(l3,gbc);
	//	getContentPane().add(l3);
		jp.add(l3,gbc);
//		m.setAsString(Double.toString(mbase.getAsDouble(3,1)),i,2);
		m.setColumnLabel(2, "Nb agent");
			
			
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.gridwidth=1;
			gbc.gridheight=1;
			gbc.weightx=10;
			gbc.weighty=10;
			gbc.anchor=GridBagConstraints.WEST;
			jp.add(jbs,gbc);

			// SCORES
			gbcs.gridwidth=1;
			gbcs.gridheight=1;
			gbcs.weightx=10;
			gbcs.weighty=10;
			gbcs.anchor=GridBagConstraints.WEST;

			
			jcheckVariance=new JCheckBox("Show Standard Error");
			jcheckVariance.addActionListener(this);
			jcheckVariance.setSelected(false);
			nx=0;
			ny=0;
			gbcs.gridx=nx;
			gbcs.gridy=ny;
			jps.add(jcheckVariance,gbcs);
			ny++;
			

			
			//PLOT
			
			//Choix var
			v1=3;
			v2=3;
			double sc1=0;
			double sc2=0;
			for(int i=4;i<mbase.getRowCount();i++)
			{
				if ((agm.scorevar[i]+agm.scorevardef[i])>sc1)
				{
					v1=i;
					sc1=agm.scorevar[i]+agm.scorevardef[i];
				}
				else
				{
					if ((agm.scorevar[i]+agm.scorevardef[i])>sc2)
					{
						v2=i;
						sc2=agm.scorevar[i]+agm.scorevardef[i];
						
					}
					
				}
				
			}

			redrawgraph();

			gbcg.gridx=1;
			gbcg.gridy=0;
			gbcg.gridwidth=1;
			gbcg.gridheight=2;
			gbcg.weightx=10;
			gbcg.weighty=10;
			gbcg.anchor=GridBagConstraints.WEST;
			jpg.add(jp,gbcg);

			
			
			gbcg.gridx=0;
			gbcg.gridy=1;
			gbcg.gridwidth=1;
			gbcg.gridheight=1;
			gbcg.weightx=10;
			gbcg.weighty=10;
			gbcg.anchor=GridBagConstraints.WEST;
			jpg.add(jps,gbcg);

			
			//			setContentPane(monPanel);

			this.getContentPane().add(jsp);

			
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

			
			
	}
	public void redrawgraph()
	{
		System.out.println("red "+v1+"/"+v2);
		//Plot
		
		if (v1!=v2)
		{
		
        XYIntervalSeries series1 = new XYIntervalSeries("ByExtension");
        XYIntervalSeries series2 = new XYIntervalSeries("ByIntension");
        XYIntervalSeries series3 = new XYIntervalSeries("Avg");
        
		// column keys...
		String category1 = "1";
		String category2 = "2";

		for(int j=0;j<mbase.getColumnCount();j++)
		{
				series1.add(clbase.avgsm.getAsDouble(v1,j),clbase.avgsm.getAsDouble(v1,j)-clbase.stderrsm.getAsDouble(v1,j),clbase.avgsm.getAsDouble(v1,j)+clbase.stderrsm.getAsDouble(v1,j),clbase.avgsm.getAsDouble(v2,j),clbase.avgsm.getAsDouble(v2,j)-clbase.stderrsm.getAsDouble(v2,j),clbase.avgsm.getAsDouble(v2,j)+clbase.stderrsm.getAsDouble(v2,j));
				if (clbase.avgsmdef.getAsDouble(2,j)>0)
				series2.add(clbase.avgsmdef.getAsDouble(v1,j),clbase.avgsmdef.getAsDouble(v1,j)-clbase.stderrsmdef.getAsDouble(v1,j),clbase.avgsmdef.getAsDouble(v1,j)+clbase.stderrsmdef.getAsDouble(v1,j),clbase.avgsmdef.getAsDouble(v2,j),clbase.avgsmdef.getAsDouble(v2,j)-clbase.stderrsmdef.getAsDouble(v2,j),clbase.avgsmdef.getAsDouble(v2,j)+clbase.stderrsmdef.getAsDouble(v2,j));
				series3.add(clbase.avglobsm.getAsDouble(v1,j),clbase.avglobsm.getAsDouble(v1,j)-clbase.stdglobsm.getAsDouble(v1,j),clbase.avglobsm.getAsDouble(v1,j)+clbase.stdglobsm.getAsDouble(v1,j),clbase.avglobsm.getAsDouble(v2,j),clbase.avglobsm.getAsDouble(v2,j)-clbase.stdglobsm.getAsDouble(v2,j),clbase.avglobsm.getAsDouble(v2,j)+clbase.stdglobsm.getAsDouble(v2,j));				

//			series2.add(clbase.avgsmdef.getAsDouble(v1,j),clbase.avgsmdef.getAsDouble(v2,j));
//			series3.add(clbase.avglobsm.getAsDouble(v1,j),clbase.avglobsm.getAsDouble(v2,j));
			
		}
//        XYSeriesCollection dataset = new XYSeriesCollection();
        XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        
        chartg = ChartFactory.createXYLineChart(
        		"",
                mbase.getRowLabel(v1),
                mbase.getRowLabel(v2),
             
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );

        XYPlot plot = (XYPlot) chartg.getPlot();
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        XYErrorRenderer renderer = new XYErrorRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true);        
        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, true);        
        renderer.setDrawSeriesLineAsPath(true);
        if (this.jcheckVariance.isSelected())
        {
        	renderer.setDrawXError(true);
        	renderer.setDrawYError(true);
        }
        else
        {
        	renderer.setDrawXError(false);
        	renderer.setDrawYError(false);        	
        }
        
//        renderer.
        plot.setRenderer(renderer);
//		plot.setOutlineVisible(false);
		plot.getDomainAxis().setVisible(true);
		plot.getRangeAxis().setVisible(true);
		plot.getDomainAxis().setAutoRange(true);
		plot.getRangeAxis().setAutoRange(true);
		plot.setBackgroundPaint(Color.WHITE);			
//		chart.setBorderVisible(false);
		}
		if (v1==v2)
		{

			 DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
			 String series1 = new String("ByExtension");
		     String series2 = new String("ByIntension");
		     String series3 = new String("Avg");

				for(int j=0;j<mbase.getColumnCount();j++)
				{
					result.add(clbase.avgsm.getAsDouble(v1,j),clbase.stderrsm.getAsDouble(v1,j),series1,new String(""+j));
					result.add(clbase.avgsmdef.getAsDouble(v1,j),clbase.stderrsmdef.getAsDouble(v1,j),series2,new String(""+j));
					result.add(clbase.avglobsm.getAsDouble(v1,j),clbase.stdglobsm.getAsDouble(v1,j),series3,new String(""+j));
					
				}
		     
		     

		         CategoryAxis xAxis = new CategoryAxis("");
		         xAxis.setCategoryMargin(0.5d);
		         ValueAxis yAxis = new NumberAxis(mbase.getRowLabel(v1));

		        // define the plot
		         StatisticalLineAndShapeRenderer renderer = new StatisticalLineAndShapeRenderer();
		         CategoryPlot plot = new CategoryPlot(result, xAxis, yAxis, renderer);

		        chartg = new JFreeChart("",
		                                          plot);
		        plot.setBackgroundPaint(Color.WHITE);			
//			chart.setBorderVisible(false);
			
		}
		
		
        if (!created)
        {
		chartPanelg = new ChartPanel(chartg);
		created=true;
        chartPanelg.setPreferredSize(new java.awt.Dimension(400, 400));
        
    	JPanel monPanel = new JPanel(new BorderLayout());        	
		monPanel.add(chartPanelg, BorderLayout.NORTH);
		
		gbcg.gridx=0;
		gbcg.gridy=0;
		gbcg.gridwidth=1;
		gbcg.gridheight=1;
		gbcg.weightx=10;
		gbcg.weighty=10;
		gbcg.anchor=GridBagConstraints.NORTHWEST;

		jpg.add(monPanel,gbcg);
        }
        else
        {
        chartPanelg.setChart(chartg);	
        }
        this.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object src=arg0.getSource();
		if (src==jcheckVariance)
		{
			redrawgraph();
			this.repaint();
			
		}
		for (int i=0; i<jrbx.size(); i++)
		{
			if (src==jrbx.get(i))
			{
				v1=jrbi.get(i).intValue();
				redrawgraph();
				this.repaint();
			}
			if (src==jrby.get(i))
			{
				v2=jrbi.get(i).intValue();
				redrawgraph();
				this.repaint();
			}
		}
		
	}
}

	
