/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic.distribution;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

public class DoubleVariableDistribution extends VariableDistribution {

	private Double mainValue = 0.;
	private Double std = 0.;
	public DoubleVariableDistribution(Matrix data, long columnIndex){
		super(data, columnIndex);
	}
	@Override
	public Object getMainValue() {
		return mainValue;
	}

	public Double getMean(){
		return mainValue;
	}
	public Double getStd(){
		return std;
	}
	//TODO To keep into account a distance between distributions
	@Override
	public Double getDistance(VariableDistribution vd) {
		if(vd instanceof DoubleVariableDistribution ){
			return Math.abs(((Double)getMainValue()) - ((Double)vd.getMainValue()));
		}
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void build(Matrix data, long columnIndex) {
		Matrix column = data.selectColumns(Ret.NEW, columnIndex);
		mainValue = column.getMeanValue();
		std = column.getStdValue();
	}

}
