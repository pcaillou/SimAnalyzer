/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */
package clustering;

import java.lang.reflect.Constructor;

import org.ujmp.core.Matrix;


import weka.clusterers.NumberOfClustersRequestable;
import weka.core.Instance;
import weka.core.Instances;
import clustering.Clusterer;

public class WekaClusterer extends Clusterer {
	public enum WekaClustererType {
		Cobweb, DBScan, EM, FarthestFirst, FilteredClusterer, MakeDensityBasedClusterer, SimpleKMeans, XMeans
	};

	private weka.clusterers.Clusterer wekaClusterer = null;

	private Instances instances = null;

	private WekaClustererType clustererName = null;

	private String[] options = null;

	private boolean discrete = false;

	public WekaClusterer(WekaClustererType classifierName, boolean discrete, String... options)
			throws Exception {
		setLabel("Weka-" + classifierName.name());
		this.clustererName = classifierName;
		this.options = options;
		this.discrete = discrete;
		createAlgorithm();
	}

	private void createAlgorithm() throws Exception {
		Class<?> c = null;
		if (c == null) {
			try {
				c = Class.forName(clustererName.name());
			} catch (ClassNotFoundException e) {
			}
		}
		if (c == null) {
			try {
				c = Class.forName("weka.clusterers." + clustererName.name());
			} catch (ClassNotFoundException e) {
			}
		}

		if (c == null) {
			throw new ClassNotFoundException("class not found: " + clustererName);
		} else {
			Constructor<?> constr = c.getConstructor(new Class[] {});
			wekaClusterer = (weka.clusterers.Clusterer) constr.newInstance(new Object[] {});
			if (options != null || options.length != 0) {
				java.lang.String optionsex[]={"a"};
				Class optcl=options.getClass();
				java.lang.reflect.Method setopt=wekaClusterer.getClass().getMethod("setOptions",optcl);
				String opt=options.toString();
				optionsex[0]=opt;
				try {
					setopt.invoke(wekaClusterer, (Object)options);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				 ((Object) wekaClusterer).setOptions(options);
				//TODO
			}
		}
	}

	public void reset() throws Exception {
		createAlgorithm();
	}


	public Long clusterInstance(Matrix input, Matrix weight, long sampleRow)throws Exception {
		long duration = System.nanoTime();
		double sampleWeight = 1.0;
		Instance instance = WekaWrapper.sampleToInstanceWrapper(input, sampleRow, sampleWeight, discrete);
		instance.setDataset(instances);
		clustertime+=System.nanoTime()-duration;
		return new Long(wekaClusterer.clusterInstance(instance));
	}

	public void setNumberOfClusters(int numberOfClusters) throws Exception {
		if (wekaClusterer instanceof NumberOfClustersRequestable) {
			((NumberOfClustersRequestable) wekaClusterer).setNumClusters(numberOfClusters);
		} else {
			throw new RuntimeException("Cannot set number of Clusters for this Clusterer");
		}
	}

	@Override
	public void buildClusterer(Matrix data, Matrix weight)throws Exception {
		long duration = System.nanoTime();
		Instances instances = WekaWrapper.matrixToInstancesWrapper(data, weight, discrete);
		this.instances = instances;
		wekaClusterer.buildClusterer(instances);
		buildclustertime+=System.nanoTime()-duration;
	//	wekaClusterer.
	}


}
