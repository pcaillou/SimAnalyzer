/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package statistic.distribution;

import java.util.HashMap;
import java.util.HashSet;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;


import statistic.calcul.Statistics;

public class CategoricalVariableDistribution extends VariableDistribution{

	private Matrix distribution = null;
	long mainValueIndex = -1;
	public CategoricalVariableDistribution(Matrix data, long columnIndex){
		super(data, columnIndex);
	}

	@Override
	public Object getMainValue() {
		return Statistics.getCategoryFromHistogram(distribution, mainValueIndex);
	}

	@Override
	public Double getDistance(VariableDistribution vd) {
		if(vd instanceof CategoricalVariableDistribution ){
			CategoricalVariableDistribution cvd = (CategoricalVariableDistribution)vd;
			HashMap<Object, Long> myIndexes = new HashMap<Object, Long>();
			HashMap<Object, Long> cvdIndexes = new HashMap<Object, Long>();
			
			for(long i=0; i<distribution.getColumnCount(); i++){
				myIndexes.put(distribution.getAsObject(1, i), i);
			}
			for(long i=0; i<cvd.distribution.getColumnCount(); i++){
				cvdIndexes.put(cvd.distribution.getAsObject(1, i), i);
			}
			HashSet<Object> allValues = new HashSet<Object>();
			allValues.addAll(myIndexes.keySet());
			allValues.addAll(cvdIndexes.keySet());
			
			double distance = 0.;
			
			for(Object o: allValues){
				Long myIndex = myIndexes.get(o);
				Long cvdIndex = cvdIndexes.get(o);
				if(myIndex == null || cvdIndex == null){
					distance = distance + 1.;
				}
				else{
					double _distance = Statistics.getFrequencyFromHistogram(distribution, myIndex)
					- Statistics.getFrequencyFromHistogram(cvd.distribution, cvdIndex);
					distance = distance + (_distance * _distance);
				}
			}
			return Math.sqrt(distance);
		}
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void build(Matrix data, long columnIndex) {
		Matrix column = data.selectColumns(Ret.NEW, columnIndex);
		Matrix bins = MatrixFactory.fill(10, 1);
		distribution = Statistics.calcHistogramVariances(column, bins); 
		Double maxFrequency = Double.NEGATIVE_INFINITY;
		for(long i=0; i< distribution.getColumnCount(); i++){
			Double frequency = Statistics.getFrequencyFromHistogram(distribution, i);
			if( frequency > maxFrequency){
				maxFrequency = frequency;
				mainValueIndex = i;
			}
		}
	}

}
