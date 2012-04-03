/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package clustering;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.exceptions.MatrixException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaWrapper  {

	private static final long serialVersionUID = 8166335287481996621L;

	public static Instance sampleToInstanceWrapper(Matrix input, long sampleRow, Double sampleWeight, 
			boolean discrete) throws MatrixException {
		Instance instance = new Instance((int) input.getColumnCount());
		if (sampleWeight != null) {
			instance.setWeight(sampleWeight);
		} else {
			instance.setWeight(1.0);
		}
		if (input != null) {
			for (int i = 0; i < input.getColumnCount(); i++) {
				if (discrete) {
					instance.setValue(i, (int) input.getAsDouble(sampleRow, i));
				} else {
					instance.setValue(i, input.getAsDouble(sampleRow, i));
				}
			}
		}
		return instance;
	}
	
	public static Instances matrixToInstancesWrapper(Matrix input, Matrix weights, boolean discrete){
		
		Instances result = new Instances(input.getLabel(), matrixToAttributeInfoWrapper(input, discrete), (int)input
				.getRowCount());

		for (int sampleRow=0; sampleRow< input.getRowCount(); sampleRow++) {
			double sampleWeight = 1.0;
			if(weights != null){
				sampleWeight = weights.getAsFloat(sampleRow, 0);
			}
			result.add(sampleToInstanceWrapper(input, sampleRow, sampleWeight, discrete));
		}
		
		return result;
	}
	
	public static FastVector matrixToAttributeInfoWrapper(Matrix input, boolean discrete) {
		FastVector result = new FastVector();

		Matrix valueCounts = input.max(Ret.NEW, Matrix.ROW).plus(1);
		for (int j = 0; j < input.getColumnCount(); j++) {
			Attribute a = null;
			if (discrete) {
				FastVector values = new FastVector();
				for (int i = 0; i < valueCounts.getAsDouble(0, j); i++) {
					values.addElement("Attribute " + i);
				}
				a = new Attribute(j + "", values);
			} else {
				a = new Attribute(j + "");
			}
			result.addElement(a);
		}
		return result;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
