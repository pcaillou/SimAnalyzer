/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package observer;

import org.ujmp.core.Matrix;

public abstract class SimulationUpdater {
	public abstract void updateSimulationFromData(Matrix data, Object...params);
}
