package statistic;

import java.util.ArrayList;
import java.util.HashMap;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;
import clustering.event.DataEvent;
import observer.SimulationInterface;

// TODO probleme
public class GraphObserver extends StatisticalObserver  {
	Long windowSize = (long)-1;
	long step = 1;
	
	private DynamicGraph graph = new DynamicGraph("GraphObserver");
	
	public static final boolean detailledTimeComputation = false;
	
	public GraphObserver(SimulationInterface si, long _windowSize) {
		super(si);
		windowSize = _windowSize;
	}
	
	public void newDataAvailable(DataEvent de) throws Exception {
		Matrix data = de.getData();
		Matrix result = MatrixFactory.zeros(ValueType.STRING, data.getRowCount(), 10);
		long idColumn = data.getColumnForLabel(SimulationInterface.ID_C_NAME),
		     graphColumn = data.getColumnForLabel("GraphNbVisitToday"),
		     timeColumn = 1; //data.getColumnForLabel("0");
		
		ArrayList<Long> timeList = new ArrayList<Long>();
		
		long duration = System.nanoTime();
		
		
		/* initialisation de la matrice */
		result.setLabel("GraphObserver");
		result.setColumnLabel(0, SimulationInterface.ID_C_NAME);
		result.setColumnLabel(1, "gr_correlation");
		result.setColumnLabel(2, "gr_correlationFromStart");
		result.setColumnLabel(3, "gr_outDegree");
		result.setColumnLabel(4, "gr_inDegree");
		result.setColumnLabel(5, "gr_nbTotalEdges");
		result.setColumnLabel(6, "gr_diameter");
		result.setColumnLabel(7, "gr_radius");
		result.setColumnLabel(8, "gr_density");
		result.setColumnLabel(9, "gr_ratioDensity");
		
		/* on charge dans graphe les donnees recues et on recupere le temps actuel */
		for (long i = data.getRowCount()-1 ; i >= 0  ; i--)
		{
			long actualTime = data.getAsLong(i,timeColumn);
			if (!timeList.contains(actualTime))
			{
				timeList.add(actualTime);
			}
			graph.loadFromString(data.getAsString(i,graphColumn), 0, actualTime, actualTime);
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
		
		
		/* on ajoute au resultat les nouvelles donnees */
		for (int i = (int) (data.getRowCount() - 1) ; i >= 0  ; i--)
		{
			long time = data.getAsLong(i,timeColumn);
			int index = graph.getNode(data.getAsString(i,idColumn)).getIndex();
			result.setAsString(data.getAsString(i,idColumn), i, 0);
			result.setAsDouble(correlationTabs.get(time)[index], i, 1);
			result.setAsDouble(correlationFromStartTabs.get(time)[index], i, 2);
			result.setAsInt(outDegrees.get(time)[index], i, 3);
			result.setAsInt(inDegrees.get(time)[index], i, 4);
			result.setAsInt(nbTotalEdges.get(time), i, 5);
			result.setAsInt(diameters.get(time), i, 6);
			result.setAsInt(radiuss.get(time), i, 7);
			result.setAsDouble(densitys.get(time), i, 8);
			result.setAsDouble(densitys.get(time) / densitys.get(time-1), i, 9);
		}
	
		
		duration = System.nanoTime() - duration;
		System.out.println("Step " + step + " tout realise en " + duration/1000000 + "ms");
		
		data.showGUI();
		result.showGUI();
		//graph.displayGraph(step);
		
		
		/* on envoie le resultat */
		this.preventListeners(new DataEvent(result, de.getArguments()));
		
		step++;
		
	}

}
