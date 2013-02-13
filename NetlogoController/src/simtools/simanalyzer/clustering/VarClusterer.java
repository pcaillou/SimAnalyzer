/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package simtools.simanalyzer.clustering;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.ujmp.core.Matrix;

import simtools.simanalyzer.clustering.Clusterer;


import weka.clusterers.NumberOfClustersRequestable;
import weka.core.Instance;
import weka.core.Instances;

public class VarClusterer extends Clusterer {


	private Instances instances = null;

	private String[] options = null;
	
	private String nomcol=null;

	private boolean discrete = false;
	
	private ArrayList<String> clustvals;
	
	private boolean isnumeric=false;
	
	private int nocol=-1;

	public VarClusterer(String... options)
			throws Exception {
		nomcol=options[0];
		setLabel("Var-" + nomcol);
//		this.clustererName = "Var-" + options;
		this.options = options;
		clustvals=new ArrayList<String>();
	//	this.discrete = discrete;
//		createAlgorithm();
	}


	public void reset() throws Exception {
	}


	public Long clusterInstance(Matrix input, Matrix weight, long sampleRow)throws Exception {
		long duration = System.nanoTime();
		double sampleWeight = 1.0;
		long nocol=input.getColumnForLabel(nomcol);
//		Instance instance = WekaWrapper.sampleToInstanceWrapper(input, sampleRow, sampleWeight, discrete);
//		instance.setDataset(instances);
		clustertime+=System.nanoTime()-duration;
		int res=-1;
		try {
			if (clustvals.contains(input.getAsString(sampleRow,nocol)))
				res=clustvals.indexOf(input.getAsString(sampleRow,nocol));
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			System.out.println("Erreur de generation d'instance (WekaClusterer/clusterinstance)");
//			e.printStackTrace();
		}
		return new Long(res);
	}

	public void setNumberOfClusters(int numberOfClusters) throws Exception {
			throw new RuntimeException("Cannot set number of Clusters for this Clusterer");
	}

	@Override
	public void buildClusterer(Matrix data, Matrix weight)throws Exception {
		long duration = System.nanoTime();
		long nocol=data.getColumnForLabel(nomcol);
		for (int i=0; i<data.getRowCount(); i++)
		{
			String newval=data.getAsString(i,nocol);
			if (!clustvals.contains(newval))
			{
				clustvals.add(newval);
			}
		}
		buildclustertime+=System.nanoTime()-duration;
	//	wekaClusterer.
	}


}
