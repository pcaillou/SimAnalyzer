/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;


public abstract class Clusterer {
	private String label;
	public boolean ClusterBuilt=false;
	public List<Cluster> clustinit;
	public List<Clusterer> subclusterer;
	public static long buildclustertime=1;
	public static long clustertime=1;
	
	public void buildClusterer(Matrix data)throws Exception{
		buildClusterer(data, null);
	}
	public Long clusterInstance(Matrix data,long row)throws Exception{
		return clusterInstance(data, null, row);
	}
	public abstract void buildClusterer(Matrix data, Matrix weight)throws Exception;
	
	public abstract Long clusterInstance(Matrix data,Matrix weight, long row)throws Exception;
	public List<Cluster> clusterData(Matrix originalData, Matrix standardizedData)throws Exception{
		return clusterData(originalData, standardizedData, null);
	}

	public List<Cluster> clusterData(Matrix originalData, Matrix standardizedData, Matrix weight)throws Exception{
		ArrayList<Cluster> clusterSet = new ArrayList<Cluster>();
		HashMap<Long, ArrayList<Long>> _clusterSet = new HashMap<Long, ArrayList<Long>>();
		HashMap<Long, ArrayList<Long>> _clusterDataIds = new HashMap<Long, ArrayList<Long>>();
		long idColumn = originalData.getColumnForLabel(Cluster.ID_C_NAME);
		if(idColumn < 0 || idColumn >= originalData.getColumnCount()){
			throw new IllegalArgumentException("Data must contain a column with data id labelled : " + Cluster.ID_C_NAME);
		}
		
		for(long i=0; i<standardizedData.getRowCount(); i++){
			Long clusterIndex = clusterInstance(standardizedData, weight, i);
			ArrayList<Long> clusterList = _clusterSet.get(clusterIndex);
			if(clusterList == null){
				clusterList = new ArrayList<Long>();
				_clusterSet.put(clusterIndex, clusterList);
			}
			clusterList.add(i);
			ArrayList<Long> clusterIds = _clusterDataIds.get(clusterIndex);
			if(clusterIds == null){
				clusterIds = new ArrayList<Long>();
				_clusterDataIds.put(clusterIndex, clusterIds);
			}
			clusterIds.add(originalData.getAsLong(i, idColumn));
		}
		for(Long clusterIndex : _clusterSet.keySet()){
			ArrayList<Long> clusterIds = _clusterDataIds.get(clusterIndex);
			ArrayList<Long> clusterList = _clusterSet.get(clusterIndex);
			Matrix cm = standardizedData.selectRows(Ret.NEW, clusterList);
			for(long column = 0; column < standardizedData.getColumnCount(); column++)
				cm.setColumnLabel(column, standardizedData.getColumnLabel(column));
			Cluster c = new Cluster(this, clusterIndex, cm, clusterIds);
			clusterSet.add(c);
		}
		return clusterSet;
	}
	
	public abstract void reset() throws Exception;
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
