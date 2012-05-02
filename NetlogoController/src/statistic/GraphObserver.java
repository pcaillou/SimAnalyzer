package statistic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.ValueType;

import controller.SimulationController;
import clustering.event.DataEvent;
import observer.SimulationInterface;

public class GraphObserver extends StatisticalObserver  {
	long step = 1;
	
	public static String[] ParamNames={"ListenTo","ObservedBy","GraphColumnName","time [opt]","weightEdgeDefault [opt]","timeCreationEdgeDefault [opt]","timeDeleteEdgeDefault [opt]","","","","","","","","","","","","",""};
	public static String[] DefaultValues={"0","1","3","","","","","","","","","","","","","","","","",""};
	
	/* temporaire le temps d'avoir les vrai constantes de label graph */
	private String LABEL_GRAPH = "";
	private String LABEL_TIME = "";
	private int WEIGHT_DEFAULT = 1;
	private long TIME_CREATION_DEFAULT = -1;
	private long TIME_DELETE_DEFAULT = -1;
	
	private DynamicGraph graph = new DynamicGraph("GraphObserver");
	
	public static final boolean detailledTimeComputation = false;
	
	/**
	 * Fonction ne servant qu'a mettre en pause le programme
	 * Un appuis sur entree relance le programme
	 */
	@SuppressWarnings("unused")
	private void pause()
	{
		java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		try {
			stdin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setParams(String[] paramvals)
	{
		try {
			this.LABEL_GRAPH=paramvals[2];
			this.LABEL_TIME=paramvals[3];
			if (!paramvals[4].equals(""))
			{
				this.WEIGHT_DEFAULT = Integer.valueOf(paramvals[4]);
			}
			if (!paramvals[5].equals(""))
			{
				this.TIME_CREATION_DEFAULT = Integer.valueOf(paramvals[5]);
			}
			if (!paramvals[6].equals(""))
			{
				this.TIME_DELETE_DEFAULT = Integer.valueOf(paramvals[6]);
			}
		} catch (NumberFormatException e) {
			System.err.println("Erreur lors de la recuperation des parametres");
			e.printStackTrace();
			}
		
	}
	
	public GraphObserver() {
		super();
	}
	
	public Matrix getGraphResult(Matrix data, long idColumn, long graphColumn, long timeColumn,String sufix)
	{
		
		long timeTick = SimulationController.currenttick;
		long rowCount = data.getRowCount();

		Matrix result = MatrixFactory.zeros(ValueType.STRING, rowCount, 11);
		ArrayList<Long> timeList = new ArrayList<Long>();
		
		/* initialisation de la matrice */
		result.setColumnLabel(0, "gr_correlation_" + sufix); 		// correlation avec t-1
		result.setColumnLabel(1, "gr_correlationFromStart" + sufix);// correlation avec t = 0
		result.setColumnLabel(2, "gr_outDegree" + sufix);			// degre sortant
		result.setColumnLabel(3, "gr_inDegree" + sufix);			// degre entrant
		result.setColumnLabel(4, "gr_nbTotalEdges" + sufix);		// nombre total d'arcs
		result.setColumnLabel(5, "gr_diameter" + sufix);			// diametre du graphe
		result.setColumnLabel(6, "gr_radius" + sufix);				// rayon du graphe
		result.setColumnLabel(7, "gr_density" + sufix);				// densite du graphe
		result.setColumnLabel(8, "gr_ratioDensity" + sufix);		// densite du graphe par rapport a t-1
		result.setColumnLabel(9, "gr_indirectConnection" + sufix);	// nombre de noeuds accessible - degre sortant
		result.setColumnLabel(10, "gr_centrality" + sufix);			// centralite d'un noeud par rapport a ceux atteignable
		
		/* on charge dans le graphe les donnees recues et on recupere le temps actuel */
		long actualTime = 0;
		long minTime,maxTime;

		for (long i = 0 ; i < rowCount  ; i++)
		{		
			/* on recupere le temps actuel */
			if (timeColumn == -1)
			{
				actualTime = timeTick;
				if (!timeList.contains(actualTime))
				{
					timeList.add(actualTime);
				}
			}
			else
			{
				actualTime = data.getAsLong(i,timeColumn);
				if (!timeList.contains(actualTime))
				{
					timeList.add(actualTime);
				}
			}
			if (TIME_CREATION_DEFAULT == -1)
			{
				minTime = actualTime;
			}
			else
			{
				minTime = TIME_CREATION_DEFAULT;
			}
			if (TIME_DELETE_DEFAULT == -1)
			{
				maxTime = actualTime;
			}
			else
			{
				maxTime = TIME_DELETE_DEFAULT;
			}
			/* on ajoute les nouveaux noeuds */
			if (graph.getNode(data.getAsString(i,idColumn)) == null)
			{
				graph.addNode(data.getAsString(i,idColumn));
			}
			graph.loadFromString(data.getAsString(i,graphColumn), WEIGHT_DEFAULT, minTime, maxTime, true);
		}
		
		
		/* on calcule les nouvelles donnees */
		
		/* on calcule les tableaux des correlations */
		HashMap<Long,double[]> correlationTabs = new HashMap<Long, double[]>();
		HashMap<Long,double[]> correlationFromStartTabs = new HashMap<Long, double[]>();
		for (long time : timeList)
		{
			correlationTabs.put(time,graph.getDynamicCorrelation(time-1,time));
			correlationFromStartTabs.put(time,graph.getDynamicCorrelation(0,time));
		}
		
		/* on calcule les nombres d'arcs */
		HashMap<Long,Integer> nbTotalEdges = new HashMap<Long, Integer>();
		for (long time : timeList)
		{
			nbTotalEdges.put(time,graph.getEdgeCount(time));
		}
		
		/* on calcule le diametre et le rayon du graphe */
		HashMap<Long,Integer> diameters = new HashMap<Long, Integer>(),
							  radiuss = new HashMap<Long, Integer>();
		for (long time : timeList)
		{
			int diameter = 0, radius = Integer.MAX_VALUE;
			for (int[] t : graph.getShortestPathWeightMatrix(time))
			{
				for (int v : t)
				{
					if (v != Integer.MAX_VALUE)
					{
						if (v > diameter)
						{
							diameter = v;
						}
						if( v < radius)
						{
							radius = v;
						}
					}
				}
			}
			diameters.put(time, diameter);
			radiuss.put(time,radius);
		}
		
		/* on calcule le nombre de degres sortant pour chaque noeud */
		HashMap<Long,Integer[]> outDegrees = new HashMap<Long, Integer[]>();
		HashMap<Long,Integer[]> inDegrees = new HashMap<Long, Integer[]>();
		for (long time : timeList)
		{
			Integer[] outDegree = new Integer[(int)(graph.getNodeCount())];
			Integer[] inDegree = new Integer[(int)(graph.getNodeCount())];
			
			for (int i = (int) (graph.getNodeCount() - 1) ; i >= 0  ; i--)
			{
				outDegree[i] = graph.getOutDegree(graph.getNode(i), time);
				inDegree[i] = graph.getInDegree(graph.getNode(i), time);
				
			}
			
			outDegrees.put(time,outDegree);
			inDegrees.put(time,inDegree);
		}

		/* on calcule la densite et le ratio de densite du graphe */
		HashMap<Long,Double> densitys = new HashMap<Long, Double>();
		for (long time : timeList)
		{
			densitys.put(time, ((double)graph.getEdgeCount(time)) / (double)(graph.getNodeCount() * graph.getNodeCount()));
			if (!densitys.containsKey(time-1))
			{
				densitys.put(time-1, ((double)graph.getEdgeCount(time-1)) / (double)(graph.getNodeCount() * graph.getNodeCount()));
			}
		}
		
		/* on calcule le nombre de connexions indirecte pour chaque noeud */
		HashMap<Long,Integer[]> indirectConnections = new HashMap<Long, Integer[]>();
		for (long time : timeList)
		{
			/* on calcule le nombre de connexions avec tout les noeuds, et on retire le nombre de degres sortant */
			Integer[] indirectConnection = new Integer[graph.getNodeCount()];
			int[][] paths = graph.getShortestPathWeightMatrix(time);
			
			for (int i = indirectConnection.length-1 ; i >= 0  ; i--)
			{
				indirectConnection[i] = 0;
				for (int j : paths[i])
				{
					if (j < Integer.MAX_VALUE)
					{
						indirectConnection[i]++;
					}
				}
				indirectConnection[i] -= graph.getOutDegree(graph.getNode(i), time);
			}
			
			indirectConnections.put(time, indirectConnection);
			
		}
		
		/* on calcule la centralite pour chaque noeud */
		HashMap<Long,Double[]> centralitys = new HashMap<Long, Double[]>();
		for (long time : timeList)
		{
			Double[] centrality = new Double[graph.getNodeCount()];
			int[][] paths = graph.getShortestPathWeightMatrix(time);
			int nbConn = 0;
			
			for (int i = centrality.length-1 ; i >= 0  ; i--)
			{
				centrality[i] = 0.0;
				nbConn = 0;
				for (int j : paths[i])
				{
					if (j < Integer.MAX_VALUE)
					{
						centrality[i] += j;
						nbConn++;
					}
				}
				if (nbConn == 0)
				{
					centrality[i] = 0.0;
				}
				else
				{
					centrality[i] = nbConn /  centrality[i];
				}
			}
			
			centralitys.put(time, centrality);
			
		}
		
		/* on ajoute au resultat les nouvelles donnees */
		for (int i = (int) (rowCount - 1) ; i >= 0  ; i--)
		{
			long time;
			if (timeColumn != -1)
			{
				time = data.getAsLong(i,timeColumn);
			}
			else
			{
				time = timeTick;
			}
			
			int index = graph.getNode(data.getAsString(i,idColumn)).getIndex();
			result.setAsDouble(correlationTabs.get(time)[index], i, 0);
			result.setAsDouble(correlationFromStartTabs.get(time)[index], i, 1);
			result.setAsInt(outDegrees.get(time)[index], i, 2);
			result.setAsInt(inDegrees.get(time)[index], i, 3);
			result.setAsInt(nbTotalEdges.get(time), i, 4);
			result.setAsInt(diameters.get(time), i, 5);
			result.setAsInt(radiuss.get(time), i, 6);
			result.setAsDouble(densitys.get(time), i, 7);
			result.setAsDouble(densitys.get(time) / densitys.get(time-1), i, 8);
			result.setAsInt(indirectConnections.get(time)[index], i, 9);
			result.setAsDouble(centralitys.get(time)[index], i, 10);
		}
		
		for (long l : timeList)
		{
			graph.displayGraph(l);
		}
		
		return result;
	}
	
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		Matrix result = MatrixFactory.zeros(ValueType.STRING, data.getRowCount(), 0);
		long idColumn = data.getColumnForLabel(SimulationInterface.ID_C_NAME),
		     graphColumn = data.getColumnForLabel(LABEL_GRAPH),
		     timeColumn = data.getColumnForLabel(LABEL_TIME);
		
		long duration = System.nanoTime();
				
		/* on met a jour les groupes de chaque node */

		if (SimulationController.MatrixList.size() > 0)
		{
			Matrix m = SimulationController.MatrixList.get(SimulationController.MatrixList.size()-1);
		    long classColumn = m.getColumnForLabel(SimulationInterface.CLASS_LABEL_C_NAME);	
		    long idColumn2 = m.getColumnForLabel(SimulationInterface.ID_C_NAME);
			long max = m.getRowCount();
			for (long i = 0 ; i < max  ; i++)
			{
				graph.setGroup(graph.getNode(m.getAsString(i,idColumn2)),1+m.getAsInt(i,classColumn));
			}
		}
		
		/* on effectue le calcul pour chaque graphe (voir comment recuperer tout les id des graphes) */
		Matrix tmpResult = getGraphResult(data, idColumn, graphColumn, timeColumn,LABEL_GRAPH);
		
		/* on ajoute la matrice au resultat */
		Matrix n2 = result.appendHorizontally(tmpResult);	
		result=n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
		for(long column =0; column<tmpResult.getColumnCount(); column++ ){
			long nc=result.getColumnCount()-tmpResult.getColumnCount()+column;
			result.setColumnLabel(nc, tmpResult.getColumnLabel(column));
		}
		
		/* on calcule le temps qui a ete necessaire */
		duration = System.nanoTime() - duration;
		System.out.println("Graph_" + LABEL_GRAPH + " Step " + step + " realise en " + duration/1000000 + "ms");

		result.setLabel("GraphObserver");

		
		data.showGUI();
		result.showGUI();
		//pause();
		
		/* on envoie le resultat */
		this.preventListeners(new DataEvent(result, de.getArguments()));
		
		step++;
		
	}

}
