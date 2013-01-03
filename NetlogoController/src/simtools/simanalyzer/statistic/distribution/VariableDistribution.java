package simtools.simanalyzer.statistic.distribution;

/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

import org.ujmp.core.Matrix;

public abstract class VariableDistribution {
	private String label =null;
	
	public VariableDistribution(Matrix data, long columnIndex){
		build(data, columnIndex);
		setLabel(data.getColumnLabel(columnIndex));
	}

	public abstract Object getMainValue();
	public abstract Double getDistance(VariableDistribution vd);
	public abstract void build(Matrix data, long columnIndex);
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
