package statistic;

import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

import clustering.event.DataEvent;

public class BinObserver extends StatisticalObserver {
long step = 1;
	
	public static String[] ParamNames={"ListenTo","ObservedBy","Display [0/1]","ColumnName","intervalStart","intervalWidth","intervalQuantity","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","1","1","","0","5","4","","","","","","","","","","","","",""};
	
	/* temporaire le temps d'avoir les vrai constantes de label graph */
	private String LABEL_VAR = "";
	private boolean DISPLAY = false;
	private Double INTERVAL_START = 0d;
	private Double INTERVAL_WIDTH = 0d;
	private Integer INTERVAL_QUANTITY = 0;
	
	private double[][] dataChart;
	
	public void setParams(String[] paramvals)
	{
		try {
			if (!paramvals[2].equals(""))
			{
				DISPLAY = paramvals[2].equals("1");
			}
			this.LABEL_VAR=paramvals[3];
			this.INTERVAL_START = Double.valueOf(paramvals[4]);
			this.INTERVAL_WIDTH = Double.valueOf(paramvals[5]);
			this.INTERVAL_QUANTITY = Integer.valueOf(paramvals[6]);
			dataChart = new double[INTERVAL_QUANTITY][0];
		} catch (NumberFormatException e) {
			System.err.println("Erreur lors de la recuperation des parametres");
			e.printStackTrace();
			}
	}
	
	public BinObserver() {
		super();
		dataChart = new double[INTERVAL_QUANTITY][0];
	}
	
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		Matrix result = MatrixFactory.zeros(ValueType.STRING, data.getRowCount(), 2);

		long idColumn = data.getColumnForLabel(LABEL_VAR);
		Double value = 0d;
		Integer group = 0;
		int[] total = new int[INTERVAL_QUANTITY];
		
		for (int i = 0 ; i < INTERVAL_QUANTITY ; i++)
		{
			total[i] = 0;
		}
		
		result.setColumnLabel(0, "bin_" + LABEL_VAR);
		result.setColumnLabel(1, "bin_g_" + LABEL_VAR);
		
		for (int i = 0 ; i < data.getRowCount() ; i++)
		{
			value = data.getAsDouble(i,idColumn);
			value -= INTERVAL_START;
			if (value % INTERVAL_WIDTH == 0)
			{
				value -= INTERVAL_WIDTH/10;
			}
			group = (int) (value / INTERVAL_WIDTH) + 1;
			if (group < 1)
			{
				group = 1;
				System.err.println("BinObserver error : value is out of intervals (group set to 1) : " + data.getAsDouble(i,idColumn) + " - " + INTERVAL_START + "->" + (INTERVAL_START + INTERVAL_QUANTITY * INTERVAL_WIDTH) + " ");
			}
			if (group > INTERVAL_QUANTITY)
			{
				group = INTERVAL_QUANTITY;
				System.err.println("BinObserver error : value is out of intervals (group set to 1) : " + data.getAsDouble(i,idColumn) + " - " + INTERVAL_START + "->" + (INTERVAL_START + INTERVAL_QUANTITY * INTERVAL_WIDTH) + " ");
			}
			if (group<10)			
			result.setAsString("B0" + group,i,0);
			if (group>=10)
			result.setAsString("B" + group,i,0);
		}
		
		String gTotal = "";
		for (int i = 0 ; i < INTERVAL_QUANTITY ; i++)
		{
			if (i<9)
			gTotal += "[B0" + (i+1) + " " +  total[i] + "]";
			if (i>=9)
				gTotal += "[B" + (i+1) + " " +  total[i] + "]";
		}
		for (int i = 0 ; i < data.getRowCount() ; i++)
		{
			result.setAsString(gTotal,i,1);
		}
		
		//data.showGUI();
//		result.showGUI();
		//pause();
		
		/* on envoie le resultat */
		result.setLabel("BinObserver");
		
		if (DISPLAY)
		{                
			double[][] newDataChart = new double[INTERVAL_QUANTITY][dataChart[0].length+1];
			for (int i = 0 ; i < dataChart[0].length ; i++)
			{
				for (int j = 0 ; j < dataChart.length ; j++)
				{
					newDataChart[j][i] = dataChart[j][i];
				}
			}
			
			for (int i = 0 ; i < dataChart.length ; i++)
			{
				newDataChart[i][newDataChart[0].length-1] = total[i];
			}
			
			dataChart = newDataChart;
			
			new BinDisplay(dataChart);
			
			
		}
		
		this.preventListeners(new DataEvent(result, de.getArguments()));
		
		step++;
		
	}
	
}

class BinDisplay extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	private static final String title = "Bin chart";
	
	public BinDisplay(double[][] data) {

        super(title);

        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
            "B ", "Step ", data
        );

        // create the chart...
        final JFreeChart chart = createChart(dataset,title);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
        
        pack();
        setVisible(true);

    }

    private JFreeChart createChart(final CategoryDataset dataset,String title) {
            
        final CategoryItemRenderer renderer = new CategoryStepRenderer(true);
        final CategoryAxis domainAxis = new CategoryAxis("Step");
        final ValueAxis rangeAxis = new NumberAxis("");
        final CategoryPlot plot = new CategoryPlot(dataset, domainAxis, rangeAxis, renderer);
        final JFreeChart chart = new JFreeChart(title, plot);
        chart.setBackgroundPaint(Color.white);
        
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLabelAngle(0 * Math.PI / 2.0);
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }

}
