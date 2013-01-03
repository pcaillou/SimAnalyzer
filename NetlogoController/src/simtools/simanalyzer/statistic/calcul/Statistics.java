/**
 * @author Javier Gil-Quijano
 * @email javier[dot]gil-quijano[at]cea.fr
 * CEA / LIST / LIMA - Giff-sur-Yvette, FRANCE
 */

package simtools.simanalyzer.statistic.calcul;

import java.util.Enumeration;
import java.util.HashMap;
//AD import java.util.HashSet;
import java.util.Hashtable;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;

public class Statistics {
	public final static Long FREQ_INDEX = (long)1;
	public final static Long CATEGORIES_INDEX = (long)0;
	public static Double getFrequencyFromHistogram(Matrix histogram, long catIndex){
		return histogram.getAsDouble(FREQ_INDEX, catIndex);
	}
	public static Object getCategoryFromHistogram(Matrix histogram, long catIndex){
		return histogram.getAsObject(CATEGORIES_INDEX, catIndex);
	}

	public static Matrix calcHistogramVariances(Matrix m, Matrix bins){
//		double []variances = new double[(int)m.getColumnCount()];
		Matrix variances = MatrixFactory.fill(0., 1, m.getColumnCount());
		for(int col=0; col<m.getColumnCount(); col++){
			Matrix hist = calcHistogram(m, col, bins.getAsInt(col));
			Matrix var = calcVariance(hist.transpose());
			variances.setAsDouble(var.getAsDouble(0, 1), 0, col) ;
			variances.setColumnLabel(col, m.getColumnLabel(col));
		}
		return variances;
	}
	public static Matrix calcHistogram(Matrix m, int col, int bins){
		if( m.getAsObject(0, col) instanceof Number){
			double [] values = new double[(int)m.getRowCount()];
			for(int i=0; i<m.getRowCount(); i++)
				values[i] = m.getAsDouble(i, col);
			double [][] histogram = Statistics.calcHistogram(values, bins);
			return MatrixFactory.importFromArray(histogram);
		}
		
		Object [] values = new Object[(int)m.getRowCount()];
		for(int i=0; i<m.getRowCount(); i++)
			values[i] = m.getAsObject(0, col);
		Object [][] histogram = Statistics.calcHistogram(values);
		return MatrixFactory.importFromArray(histogram);
	}
	public static Matrix calcVariance(Matrix m){
		return m.var(Ret.NEW,
				Matrix.ROW, true);
	}

	public static Matrix calcMean(Matrix m){
		return m.mean(Ret.NEW,
				Matrix.ROW, true);
	}
	public static Matrix calcStd(Matrix m){
		return m.std(Ret.NEW,
				Matrix.ROW, true);
	}
	
	public static double ENTROPY_QMIN = 0.005;
	public static double ENTROPY_RIS = 0.2;
	public static double ENTROPY_ET = 0.02;
	public static double ENTROPY_BETA = 10;
	/**
	*	This method creates an array containing the normalized values. The result values are drawn in the [0, 1] interval
	*	@param values. Values to be normalized
	*	@return An array of length equal to values.length containing the normalized values
	*/
	public static double [] normalizeValues(double[]values){
		double result[] = new double[values.length];
		double max = values[0];
		double min = values[0];
		for(int i=1; i<values.length; i++){
			if(values[i] > max){
				max = values[i];
			}
			if(values[i] < min){
				min = values[i];
			}
		}
		for(int i=0; i<values.length; i++){
			result[i] = ((values[i]-min)/(max - min));
		}
		return result;
	}
	/**
	*	This procedure calculates the Mu factor in the entropy expression given by Dash et al. i.e. E = (exp(beta*Xij) - exp(0))/(exp(beta*Mu) - exp(0)) using the approximate method given
	*	by the authors
	*	@see Dash et al. 2002 - Feature Selection for Clustering - A filter Solution
	*	@param histogram matrix of 2 x N dimensions, the first column contains the values and the second their frequency (sum of frequencies == 1)
	*	@param entropy_qmin minimal frequency of the first bin to be considered in the calcul
	*	@param entropy_ris Range to be considered in the values to find the max frequency used to find the Mu value
	*	@param entropy_et value small than 1 used replace E in the given equation. It can to be as a percentage of the max entropy where it is suppose to find the optimal center (Mu) of the
	*	entropy function
	*	@param entropy_beta is beta in the equation, is a positive value
	*	@return the value of mu drawn from the [0, 1] interval
	*/
	public static double calcEntropyMu(double [][]histogram, double entropy_qmin, double entropy_ris, double entropy_et, double entropy_beta){
		int i= 0;
//			System.out.println(histogram[1][i] +" =:= " + entropy_qmin);
		for(; histogram[1][i] < entropy_qmin; i++){
//			System.out.println(histogram[1][i] +" =:= " + entropy_qmin);
		}
		//i--;
		double maxHist = histogram[1][i];
		double maxValue = histogram[0][i];
		double initValue = maxValue;
		@SuppressWarnings("unused")
		double limitValue = histogram[0][histogram[0].length -1];
		for(; histogram[0][i] - initValue < (entropy_ris); i++){
			if(maxHist <= histogram[1][i]){
				maxHist = histogram[1][i];
				maxValue = histogram[0][i];
			} 
		}
//		System.out.println("max value before : " + maxValue);
//		maxValue = maxValue / limitValue;
//		System.out.println("max value after : " + maxValue);
		double mu = (Math.log(Math.exp(entropy_beta*maxValue)+entropy_et -1)-Math.log(entropy_et))/entropy_beta;
//		double mu = Math.log(((Math.exp(entropy_beta*maxValue + entropy_et  - 1) - Math.exp(0)) / entropy_et) + Math.exp(0))/Math.log(entropy_beta);
		return mu;
	}
	/**
	*	This procedure calculates the Mu factor in the entropy expression given by Dash et al. i.e. E = (exp(beta*Xij) - exp(0))/(exp(beta*Mu) - exp(0)) using the approximate method given
	*	by the authors. It calls the calcEntropyMu(double [][]histogram, double entropy_qmin, double entropy_ris, double entropy_et, double entropy_beta) method with entropy_qmin set to ENTROPY_QMIN
	*	entropy_ris set to ENTROPY_RIS, entropy_et set to ENTROPY_ET and entropy_beta set to ENTROPY_BETA. This parameters values are the optimal values presented in Dash et al.
	*	@see Dash et al. 2002 - Feature Selection for Clustering - A filter Solution
	*	@see calcEntropyMu(double [][]histogram, double entropy_qmin, double entropy_ris, double entropy_et, double entropy_beta)
	*	@param histogram matrix of 2 x N dimensions, the first column contains the values and the second their frequency (sum of frequencies == 1)
	*	@return the value of mu drawn from the [0, 1] interval
	*/
	public static double calcEntropyMu(double [][]histogram){
		return calcEntropyMu(histogram, ENTROPY_QMIN, ENTROPY_RIS, ENTROPY_ET, ENTROPY_BETA);
	}
	/**
	*	This method calculates the entropy of the given set of values according the entropy's expression given by Dash et al. i.e. 
	*	E(values) = sum(i, Ei) where : 
	*	Ei = (exp(beta*values(i)) - exp(0))/(exp(beta*Mu) - exp(0)) if Values(i) <= Mu  
	*	Ei = (exp(beta*(1-values(i))) - exp(0))/(exp(beta(1-*Mu)) - exp(0)) if Values(i) <= Mu
	*	Values must be normailized and must draw in the interval [0, 1]
	*	@param values array containing the values. Values must be normailized and must draw in the interval [0, 1] 
	*	@param mu a real value drawn from [0, 1] interval 
	*	@param beta a positive real value 
	*	@throws IllegalArgumentException if one of the values or mu are not drawn in the interval [0, 1] or beta is negative or 0
	*	@return the entropy of the values. A real positive value
	*/
	public static double calcEntropy(double [] values, double mu, double beta) throws IllegalArgumentException{
		System.out.println("mu: "+ mu);
		if(beta < 0)
			throw new IllegalArgumentException("beta must be positive");
		if(mu > 1.0 || mu < 0.0)
			throw new IllegalArgumentException("mu must be drawn in the [0, 1] interval");
		double entropy = 0;
		double exp0 = Math.exp(0);
		double div1 = Math.exp(beta*mu) -Math.exp(0);
		double div2 = Math.exp(beta*(1-mu)) -Math.exp(0);
		
		for(int i=0; i<values.length; i++){
			if(values[i] > 1.0 || values[i] < 0.0)
				throw new IllegalArgumentException("values[i] must be drawn in the interval [0, 1], " + values[i] + " is an illegal value");
			if(values[i] <= mu){
				entropy = entropy + (Math.exp(beta*values[i]) - exp0)/div1;
			}
			else{
				entropy = entropy + (Math.exp(beta*(1-values[i])) - exp0)/div2;
			}
		}
		return entropy;
	}
	/**
	*	This method calculates the entropy of the given set of values according the entropy's expression given by Dash et al. i.e. 
	*	E(values) = sum(i, Ei) where : 
	*	Ei = (exp(beta*values(i)) - exp(0))/(exp(beta*Mu) - exp(0)) if Values(i) <= Mu  
	*	Ei = (exp(beta*(1-values(i))) - exp(0))/(exp(beta(1-*Mu)) - exp(0)) if Values(i) <= Mu
	*	Values must be normailized and must draw in the interval [0, 1]
	*	mu is calculated from the histogram of frequencies of the values using the double calcEntropyMu(double [][]histogram) method.
	*	beta is set to ENTROPY_BETA
	*	@see double calcEntropyMu(double [][]histogram)
	*	@param values array containing the values. Values must be normailized and must draw in the interval [0, 1] 
	*	@param histogram matrix of 2 x N dimensions, N is the number of classes considered in the histogram, the first column contains the values and the second 
	*	their frequency (sum of frequencies == 1)
	*	@throws IllegalArgumentException if one of the values is not drawn in the interval [0, 1]
	*	@return the entropy of the values. A real positive value
	*/
	public static double calcEntropy(double [] values, double[][]histogram )throws IllegalArgumentException{
		double mu = calcEntropyMu(histogram);
		return calcEntropy(values, mu, ENTROPY_BETA);
	}
	/**
	*	This method calculates the entropy of the given set of values according the entropy's expression given by Dash et al. i.e. 
	*	E(values) = sum(i, Ei) where : 
	*	Ei = (exp(beta*values(i)) - exp(0))/(exp(beta*Mu) - exp(0)) if Values(i) <= Mu  
	*	Ei = (exp(beta*(1-values(i))) - exp(0))/(exp(beta(1-*Mu)) - exp(0)) if Values(i) <= Mu
	*	Values must be normailized and must draw in the interval [0, 1]
	*	the histogram is calculated using the double[][] calcHistogram(double [] values) method
	*	mu is calculated from the histogram of frequencies of the values using the double calcEntropyMu(double [][]histogram) method.
	*	beta is set to ENTROPY_BETA
	*	@param values array containing the values. Values must be normailized and must draw in the interval [0, 1] 
	*	@throws IllegalArgumentException if one of the values is not drawn in the interval [0, 1]
	*	@return the entropy of the values. A real positive value
	*/
	public static double calcEntropy(double [] values)throws IllegalArgumentException{
		double [][] histogram = calcHistogram(values);
		return calcEntropy(values, histogram);
	}

	/**
	*	Calculates the frequencies of the given array of objects
	*	@param values array of n elements. Values to be used to determine their histogram.
	*	@return the frequencies array where the histogram frequencies will be stocked, it is an array of 2xnclasses. In the first column the objects of every class are stocked
	*	in the second column the frequencies are stocked as Double objects (sum of frequencies == 1). nclasses is the number of different values found in the given array
	*/
	public static Object[][] calcFrequencies(Object [] values){
		Hashtable<Object, Integer> hash = new Hashtable<Object, Integer>();
		
		for(int i=0; i<values.length; i++){
//			System.out.println("values :" + values[i]);
			if(values[i] == null)
				values[i] = "null";
			Integer f = hash.get(values[i]);
			if(f == null){
				f = new Integer(1);
				hash.put(values[i], f);
			}
			else{
				hash.put(values[i], new Integer(1+f.intValue()));
				
			}
		}
		Object hist[][] = new Object[2][hash.size()];
		Enumeration<Object> objects = hash.keys();
		int i=0;
		while(objects.hasMoreElements()){
			hist[0][i] = objects.nextElement();
			hist[1][i] = new Double(((double)hash.get(hist[0][i]).intValue())/values.length);
			i++;
		}
		return hist;
	}

	/**
	*	Calculates the histogram of frequencies of the given array of values
	*	@param values array of n elements. Values to be used to determine their histogram.
	*	@param nclasses a positive integer number. Number of classes to be cosidered in the histogram
	*	@param hist array where the histogram frequencies will be stocked, it is an array of 2xnclasses. In the first column the values of every class are stocked
	*	in the second column the frequencies are stocked (sum of frequencies == 1)
	*	@param min min value in the values array
	*	@param max max value in the values array
	*	@return it returns the hist array containing the histogram
	*/
	private static Object[][] calcHistogram(Object [] values){
		HashMap<Object, Integer>classes = new HashMap<Object, Integer>();
		
		for(Object o: values){
			if(!classes.containsKey(o))
				classes.put(o, classes.size());
		}
		Object[][] hist = new Object[2][classes.size()];
		for(Object o :classes.keySet()){
			int index = classes.get(o);
			hist[0][index] = o;
			hist[1][index] = new Integer(0);
		}
		for(int i=0; i<values.length; i++){
			int index = classes.get(values[i]);
			hist[1][index]= new Integer((Integer)hist[1][index] + 1);
		}
		return hist;
	}

	/**
	*	Calculates the histogram of frequencies of the given array of values
	*	@param values array of n elements. Values to be used to determine their histogram.
	*	@param nclasses a positive integer number. Number of classes to be cosidered in the histogram
	*	@param hist array where the histogram frequencies will be stocked, it is an array of 2xnclasses. In the first column the values of every class are stocked
	*	in the second column the frequencies are stocked (sum of frequencies == 1)
	*	@param min min value in the values array
	*	@param max max value in the values array
	*	@return it returns the hist array containing the histogram
	*/
	private static double[][] calcHistogram(double [] values, int nclasses, double[][] hist, double min, double max){
		double w = max - min;
		double h = w / nclasses;
		int _class =-1;
		double pos = 0, rest = 0;
		
		for(int i=0; i<values.length; i++){
			pos = ((values[i] - min)/(max-min))*(nclasses-1);
			rest = pos - Math.floor(pos);
			pos = pos +((rest<=0.5)?0:1);
			_class = (int)(pos);
//			_class = (int)Math.floor((values[i] - min) / h);
//			_class =(_class < 0)?0:_class;
//			_class = (_class > hist[1].length-1)?hist[1].length-1:_class;
			hist[1][_class] =hist [1][_class] + 1; 
		}
		for(int i=0; i<nclasses; i++){
			hist[1][i] = hist[1][i] / values.length;
			hist[0][i] = (min + (h * (i+0.5)));///(max - min);
		}
		return hist;
	}

	/**
	*	Calculates the histogram of frequencies of the given array of values
	*	@param values array of n elements. Values to be used to determine their histogram.
	*	@param nclasses a positive integer number. Number of classes to be cosidered in the histogram
	*	@return it returns the array where the histogram frequencies have been stocked, it is an array of 2xnclasses. In the first column the values of every class are stocked
	*	in the second column the frequencies are stocked (sum of frequencies == 1)
	*/
	public static double[][] calcHistogram(double [] values, int nclasses){
		return calcHistogram(values, nclasses , new double[2][nclasses]);
	}
	/**
	*	Calculates the histogram of frequencies of the given array of values
	*	@param values array of n elements. Values to be used to determine their histogram.
	*	@param nclasses a positive integer number. Number of classes to be cosidered in the histogram
	*	@param hist array where the histogram frequencies will be stocked, it is an array of 2xnclasses. In the first column the values of every class are stocked
	*	in the second column the frequencies are stocked (sum of frequencies == 1). If the dimensions of hist are not 2xnclasses it throws an IllegalArgumentException.
	*	@throws IllegalArgumentException if the dimensions of hist are not 2xnclasses
	*	@return it returns the hist array containing the histogram
	*/
	public static double[][] calcHistogram(double [] values, int nclasses, double hist[][])throws IllegalArgumentException{
		if(hist.length != 2)
			throw new IllegalArgumentException("hist.length must be equal to 2");
		if(hist[0].length != nclasses ||hist[1].length != nclasses )
			throw new IllegalArgumentException("hist[0].length and hist[1].length must be equal to nclasses");
		double min= values[0], max = values[0];
		hist[1][0] = 0;
		hist[0][0] = -1;
		for(int i=1; i<values.length; i++){
			if(min >values[i])
				min = values[i];
			if(max <values[i])
				max = values[i];
			if(i < nclasses){	
				hist[1][i] = 0;	
				hist[0][i] = -1;	
			}
		}
/**		double w = max - min;
		double h = w / (nclasses -1);
		max = max + (h / 2);
		min = min - (h / 2);**/
		return calcHistogram(values, nclasses, hist, min, max);
	}
	/**
	*	Calculates the histogram of frequencies of the given array of values. The number of classes (nclasses) considered is the square root of the number of values + 1
	*	@param values array of n elements. Values to be used to determine their histogram.
	*	@return it returns the array where the histogram frequencies have been stocked, it is an array of 2xnclasses. In the first column the values of every class are stocked
	*	in the second column the frequencies are stocked (sum of frequencies == 1)
	*/
	public static double[][] calcHistogram(double [] values){
		return calcHistogram(values, (((int)Math.sqrt(values.length))));
	}
	/**
	 *	@param	covMatrix, input covariance matrix a matrix of NxN where N is the number of variables 
	 *	@param	correlationCoffMatrix, The correlation coefficients will be stock in this matrix. It has to be a NxN matrix, where N is the number of variables
	 *	@param	maximalInformationCompressionIndexMatrix, The maximal Information Compression Indexex will be stock in this matrix. It has to be a NxN matrix, where N is the number of variables
	 */
	public static void calcCorrelationMatricesFromCovarianceMatrix(double [][]covMatrix,  double [][]correlationCoffMatrix, double [][] maximalInformationCompressionIndexMatrix)throws IllegalArgumentException{
		if(correlationCoffMatrix.length != covMatrix.length){
			throw new IllegalArgumentException("correlationCoffMatrix.length must be equal to covMatrix.length");
		}
		if(maximalInformationCompressionIndexMatrix.length != covMatrix.length){
			throw new IllegalArgumentException("maximalInformationCompressionIndexMatrix.length must be equal to covMatrix.length");
		}
		double varX, varY, temp, ro;
		
		for(int i=0; i<covMatrix.length; i++){
			if(correlationCoffMatrix[i].length != covMatrix[i].length){
				throw new IllegalArgumentException("correlationCoffMatrix[i].length must be equal to covMatrix[i].length");
			}
			if(maximalInformationCompressionIndexMatrix[i].length != covMatrix[i].length){
				throw new IllegalArgumentException("maximalInformationCompressionIndexMatrix[i].length must be equal to covMatrix[i].length");
			}
			for(int j=i; j<covMatrix.length; j++){
				varX = covMatrix[i][i];
				varY = covMatrix[j][j];
				correlationCoffMatrix[i][j] = covMatrix[i][j]/ Math.sqrt(varX*varY);
				correlationCoffMatrix[j][i] = correlationCoffMatrix[i][j];
				ro = correlationCoffMatrix[i][j];
				temp = varX + varY;
				maximalInformationCompressionIndexMatrix[i][j] = (temp - Math.sqrt((temp*temp) - (4*varX*varY*(1-(ro*ro)))))/2;
				maximalInformationCompressionIndexMatrix[j][i] = maximalInformationCompressionIndexMatrix[i][j];
			}
		}
	}
	
}
