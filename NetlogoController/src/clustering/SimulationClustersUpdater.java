/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package clustering;

import java.util.List;

import org.ujmp.core.Matrix;
import observer.SimulationUpdater;

public abstract class SimulationClustersUpdater extends SimulationUpdater{
	@SuppressWarnings("unchecked")
	@Override
	public void updateSimulationFromData(Matrix data, Object... params) {
//		updateClustersInSimulation(data, (String)params[Indexes.AGENT_TYPE_INDEX.getIndex()],(Long)params[Indexes.NUMBER_CLUSTERS_INDEX.getIndex()]);
		updateClustersInSimulation((List<Cluster>)params[Indexes.CLUSTERS_INDEX.getIndex()], (String)params[Indexes.AGENT_TYPE_INDEX.getIndex()]);
	}
//	public abstract void updateClustersInSimulation(Matrix data, String agentType, long clustersNumber);
	public abstract void updateClustersInSimulation(List<Cluster> clustersSet, String agentType);
}
