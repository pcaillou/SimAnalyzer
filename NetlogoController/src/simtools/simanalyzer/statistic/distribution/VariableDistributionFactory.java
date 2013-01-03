/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic.distribution;

import org.ujmp.core.Matrix;


public class VariableDistributionFactory {
	public static VariableDistribution buildDistribution(Matrix data, long columnData){
		if( data.getAsObject(0, columnData) instanceof Number){
			return new DoubleVariableDistribution(data, columnData);
		}
		return new CategoricalVariableDistribution(data, columnData);
	}
}
