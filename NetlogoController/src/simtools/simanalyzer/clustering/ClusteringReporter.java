/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.clustering;

import java.util.ArrayList;
import java.util.List;


import simtools.simanalyzer.clustering.Cluster;
import simtools.simanalyzer.observer.Observer;
import simtools.simanalyzer.reporter.Report;
import simtools.simanalyzer.reporter.Reporter;

public class ClusteringReporter extends Reporter {

	
	public ClusteringReporter(Observer o) {
		super(o);
	}

	@Override
	public void showReport() {
		// TODO Auto-generated method stub

	}
	
	public void addClusterSet(Object stamp, List<Cluster> clusterSet){
		List<Cluster> lastClusterSet = getLastClusterSet();
		Cluster.relateConsecutiveClustersSets(lastClusterSet, clusterSet);
		Report r = new Report(stamp, clusterSet);
		addReport(r);
	}
	
	@SuppressWarnings("unchecked")
	public List<Cluster> getLastClusterSet(){
		Report r = getLastReport();
		if(r == null)
			return new ArrayList<Cluster>();
		
		return (List<Cluster>)r.getValue();
	}
}
