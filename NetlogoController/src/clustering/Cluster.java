/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import org.ujmp.core.Matrix;

import controller.AgModel;
import controller.SimulationController;

import statistic.distribution.VariableDistribution;
import statistic.distribution.VariableDistributionFactory;


public class Cluster {
	public static final String ID_C_NAME = "Id";
	public static final String CLASS_LABEL_C_NAME = "Class label";
	private HashSet<Long> componentIds = new HashSet<Long>();
	private HashMap<String, VariableDistribution> variableDistributions = new HashMap<String, VariableDistribution>();
	private Long id = (long)-1;
	public double[] vtests;
	public double[] avg;
	public double[] stderr;
	public Matrix vtestsm;
	public Matrix avgsm;
	public Matrix stderrsm;
	public Matrix vtestsmdef;
	public Matrix avgsmdef;
	public Matrix stderrsmdef;
	public Matrix avglobsm;
	public Matrix stdglobsm;
	public HashMap<String, VariableDistribution> globDistrib = new HashMap<String, VariableDistribution>();
	public HashMap<String, VariableDistribution> popDistrib = new HashMap<String, VariableDistribution>();
	public HashMap<String, VariableDistribution> defDistrib = new HashMap<String, VariableDistribution>();
	public Matrix qvtestsm;
	public Matrix qavgsm;
	public Matrix qstderrsm;
	public Matrix qvtestsmdef;
	public Matrix qavgsmdef;
	public Matrix qstderrsmdef;
	public Matrix qavglobsm;
	public Matrix qstdglobsm;
    public List<Long> ticklist = new ArrayList<Long>();
	public AgModel agm;
	public int idtickinit;
	public long tickinit;
	
	private List<List<Object>>getSortListByCommonComponents(List<Cluster> clusters){
		ArrayList<Object> sortClusters = new ArrayList<Object>();
		ArrayList<Object> commonComponents = new ArrayList<Object>();
		
		for(Cluster c: clusters){
			Double number = ((double)this.getNumberOfCommonComponents(c)) / this.getSize();
			if(sortClusters.size() == 0){
				sortClusters.add(c);
				commonComponents.add(number);
			}
			else{
				boolean found = false;
				for(int i=0; i<sortClusters.size() &&!found; i++){
					if(((Double)commonComponents.get(i)) <= number){
						found = true;
						sortClusters.add(i, c);
						commonComponents.add(i, number);
					}
				}
				if(!found){
					sortClusters.add(c);
					commonComponents.add(number);
				}
					
			}
		}
		
		ArrayList<List<Object>>result = new ArrayList<List<Object>>();
		result.add(sortClusters);
		result.add(commonComponents);
		
		return result;
	}
	public static HashMap<Cluster, Cluster> relateConsecutiveClustersSets(List<Cluster> beforeSet
			,List<Cluster> afterSet){
		HashMap<Cluster, List<List<Object>>> relations = new HashMap<Cluster, List<List<Object>>>();
		HashSet<Cluster> noLinkedBefore = new HashSet<Cluster>();
		noLinkedBefore.addAll(beforeSet);
		HashSet<Cluster> noLinkedAfter = new HashSet<Cluster>();
		noLinkedAfter.addAll(afterSet);
		
		HashMap<Cluster, Cluster> links = new HashMap<Cluster, Cluster>();
		for(Cluster c: beforeSet){
			relations.put(c, c.getSortListByCommonComponents(afterSet));
		}
		long maxGivenId = -1;
		while(relations.size() > 0 && links.size() < beforeSet.size() && links.size() < afterSet.size()){
			//Choose the closest pair, maybe a multicriteria algorithm should be used?
			//by taking into account the size of the clusters and their common percentage elements?
			Cluster best = null;
			Double bestScore = Double.NEGATIVE_INFINITY;
			for(Cluster c: relations.keySet()){
				List<List<Object>> lists = relations.get(c);
				Double score = (Double)lists.get(1).get(0);
				if(score > bestScore){
					bestScore = score;
					best = c;
				}
			}
			List<List<Object>> chosenList = relations.remove(best);
			Cluster clusterLinked = (Cluster)chosenList.get(0).get(0);
			links.put(best, clusterLinked);
			noLinkedBefore.remove(best);
			noLinkedAfter.remove(clusterLinked);
			clusterLinked.setId(best.getId());
			maxGivenId = Math.max(maxGivenId, best.getId());
			//Cleans the relations HashMap by removing the already used after clusters
			HashSet<Cluster> toRemoveFromRelations = new HashSet<Cluster>();
			for(Cluster c: relations.keySet()){
				List<List<Object>> lists = relations.get(c);
				boolean stop = false;
				while(!stop && lists.get(0).size() > 0){
					if(noLinkedAfter.contains(lists.get(0).get(0)))
						stop = true;
					else{
						lists.get(0).remove(0);
						lists.get(1).remove(0);
						
					}
				}
				if(lists.get(0).size() == 0)
					toRemoveFromRelations.add(c);
			}
			for(Cluster c: toRemoveFromRelations){
				relations.remove(c);
			}
		}
		if(noLinkedAfter.size() > 0 && noLinkedBefore.size() > 0){
			throw new IllegalArgumentException("Something is going wrong : noLinkedAfter.size() " + noLinkedAfter.size() 
					+" > 0 and noLinkedBefore.size() " + noLinkedBefore.size()+">0");
			
		}
		
		for(Cluster c : noLinkedAfter){
			maxGivenId++;
			c.setId(maxGivenId);
		}
		return links;
	}
	
	public Cluster(){}
	
	public Cluster(Clusterer cl, Long id, Matrix data, Collection<Long> componentsIds){
		this();
		setId(id);
		tickinit=SimulationController.currenttick;
		idtickinit=0;
		// AD ticklist=new ArrayList(); /* code original
		ticklist=new ArrayList<Long>(); // */
		this.update(data, componentsIds);
		agm=new AgModel(cl,this);
	}
	
	public void updatedistrib(Matrix data, HashMap<String, VariableDistribution> distrib){		
		distrib.clear();
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
//		long classLabelColumn = data.getColumnForLabel(Cluster.CLASS_LABEL_C_NAME);
		for(long column =0; column < data.getColumnCount(); column++){
				String label = data.getColumnLabel(column);
				VariableDistribution vd = VariableDistributionFactory.buildDistribution(data, column);
				distrib.put(label, vd);			
		}
	}
	
	
	public void update(Matrix data, Collection<Long> componentsIds){
		
		this.componentIds.clear();
		this.variableDistributions.clear();
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
		long classLabelColumn = data.getColumnForLabel(Cluster.CLASS_LABEL_C_NAME);
		for(long column =0; column < data.getColumnCount(); column++){
			if(!(column == idColumn
					|| column == classLabelColumn)){
				String label = data.getColumnLabel(column);
				VariableDistribution vd = VariableDistributionFactory.buildDistribution(data, column);
				variableDistributions.put(label, vd);
			}
			
		}
		this.componentIds.addAll(componentsIds);
	}
	
	public Collection<VariableDistribution> getVariableDistributions() {
		return variableDistributions.values();
	}

	public Collection<String> getVariables(){
		return variableDistributions.keySet();
	}

	public VariableDistribution getDistribution(String varName){
		return variableDistributions.get(varName);
	}
	
	public HashSet<Long> getComponentIds() {
		return componentIds;
	}
	
	public void addComponent(Long id){
		componentIds.add(id);
	}
	
	public void addAll(Collection<Long> ids){
		componentIds.addAll(ids);
	}
	public void removeComponent(Long id){
		componentIds.remove(id);
	}
	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getSize(){
		return componentIds.size();
	}
	
	public long getNumberOfCommonComponents(Cluster c){
		long result = 0;
		for(Long component : componentIds){
			if(c.componentIds.contains(component))
				result++;
		}
		return result;
	}

}
