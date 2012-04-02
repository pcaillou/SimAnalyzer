/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package reporter;

import java.util.ArrayList;
import java.util.List;

import observer.Observer;

public abstract class Reporter {
	private List<Report> history = new ArrayList<Report>();
	private Observer observer = null;
	
	public Reporter(Observer o){
		observer = o;
	}
	
	public void addReport(Report report){
		history.add(report);
		
	}
	
	public List<Report>getReports(){
		return history;
	}

	public Observer getObserver(){
		return observer;
	}
	
	public Report getLastReport(){
		if(history.isEmpty())
			return null;
		return history.get(history.size() - 1);
	}
	
	public abstract void showReport();
}
