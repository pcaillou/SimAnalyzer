/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package observer;

import observer.event.ObserverEvent;

public interface ObserverListener {
	public void onEvent(ObserverEvent oe) throws Exception;
}
