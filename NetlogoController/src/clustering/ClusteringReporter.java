/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package clustering;

import java.util.ArrayList;
import java.util.List;

import clustering.Cluster;

import observer.Observer;
import reporter.Report;
import reporter.Reporter;

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
