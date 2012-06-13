package statistic;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

import clustering.event.DataEvent;

public class BinObserver extends StatisticalObserver {
long step = 1;
	
	public static String[] ParamNames={"ListenTo","ObservedBy","ColumnName","intervalStart","intervalWidth","intervalQuantity","","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","1","","0","5","4","","","","","","","","","","","","","",""};
	
	/* temporaire le temps d'avoir les vrai constantes de label graph */
	private String LABEL_VAR = "";
	private Double INTERVAL_START = 0d;
	private Double INTERVAL_WIDTH = 0d;
	private Integer INTERVAL_QUANTITY = 0;
	
	public void setParams(String[] paramvals)
	{
		try {
			this.LABEL_VAR=paramvals[2];
			this.INTERVAL_START = Double.valueOf(paramvals[3]);
			this.INTERVAL_WIDTH = Double.valueOf(paramvals[4]);
			this.INTERVAL_QUANTITY = Integer.valueOf(paramvals[5]);
		} catch (NumberFormatException e) {
			System.err.println("Erreur lors de la recuperation des parametres");
			e.printStackTrace();
			}
	}
	
	public BinObserver() {
		super();
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
			if (group < 1 && group > INTERVAL_QUANTITY)
			{
				group = 1;
				System.err.println("BinObserver error : value is out of intervals (group set to 1) : " + data.getAsDouble(i,idColumn) + " - " + INTERVAL_START + "->" + (INTERVAL_START + INTERVAL_QUANTITY * INTERVAL_WIDTH) + " ");
			}
			total[group-1]++;
			result.setAsString("B" + group,i,0);
		}
		
		String gTotal = "";
		for (int i = 0 ; i < INTERVAL_QUANTITY ; i++)
		{
			gTotal += "[B" + (i+1) + " " +  total[i] + "]";
		}
		for (int i = 0 ; i < data.getRowCount() ; i++)
		{
			result.setAsString(gTotal,i,1);
		}
		
		//data.showGUI();
		result.showGUI();
		//pause();
		
		/* on envoie le resultat */
		result.setLabel("BinObserver");
		this.preventListeners(new DataEvent(result, de.getArguments()));
		
		step++;
		
	}
}
