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
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;
import org.ujmp.core.doublematrix.DoubleMatrix;
import org.ujmp.core.exceptions.MatrixException;

import controller.AgModel;
import controller.SimulationController;
import controller.Vtest;

import statistic.distribution.VariableDistribution;
import statistic.distribution.VariableDistributionFactory;


public class Cluster {
	public static final int NB_INIT_BIN=8;
	public static final int NB_MAX_BIN=10;
	public static final String ID_C_NAME = "Id";
	public static final String CLASS_LABEL_C_NAME = "Class label";
	private HashSet<Long> componentIds = new HashSet<Long>();
	private HashMap<String, VariableDistribution> variableDistributions = new HashMap<String, VariableDistribution>();
	private Long id = (long)-1;
	public double[] vtests;
	public double[] avg;
	public double[] stderr;
	public DenseDoubleMatrix2D vtestsm;
	public DenseDoubleMatrix2D avgsm;
	public DenseDoubleMatrix2D stderrsm;
	public DenseDoubleMatrix2D vtestsmdef;
	public DenseDoubleMatrix2D avgsmdef;
	public DenseDoubleMatrix2D stderrsmdef;
	public DenseDoubleMatrix2D avglobsm;
	public DenseDoubleMatrix2D stdglobsm;
	public static DenseDoubleMatrix2D comdistribparams=Vtest.mfact.zeros(1,1);
	public DenseDoubleMatrix2D distribparams;
	public ArrayList<HashMap<String,Integer>> globDistrib=new ArrayList();
	public ArrayList<HashMap<String,Integer>> popDistrib=new ArrayList();
	public ArrayList<HashMap<String,Integer>> defDistrib=new ArrayList();
//	public DenseDoubleMatrix2D globDistribN;
//	public DenseDoubleMatrix2D popDistribN;
//	public DenseDoubleMatrix2D defDistribN;
	public Matrix davgsm;
	public Matrix davglobsm;
	public Matrix davgsmdef;
	public ArrayList<Matrix> hdavgsm;
	public ArrayList<Matrix> hdavglobsm;
	public ArrayList<Matrix> hdavgsmdef;
	public ArrayList<Matrix> hdistribparams;
	public String[] qvtests;
	public String[] qvtestsshort;
	public ArrayList<String> hname;
	public Matrix qvtestsm;
	public ArrayList<Matrix> hvtestsm;
	public Matrix qavgsm;
	public ArrayList<Matrix> havgsm;
	public Matrix qstderrsm;
	public ArrayList<Matrix> hstderrsm;
	public Matrix qvtestsmdef;
	public Matrix qavgsmdef;
	public Matrix qstderrsmdef;
	public Matrix qavglobsm;
	public ArrayList<Matrix> havglobsm;
	public Matrix qstdglobsm;
	public ArrayList<Matrix> hstdglobsm;
    public List<Long> ticklist = new ArrayList<Long>();
	public AgModel agm;
	public int idtickinit;
	public long tickinit;
	public int nbotherxp;
	
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
		nbotherxp=0;
		this.havglobsm=new ArrayList<Matrix>();
		this.havgsm=new ArrayList<Matrix>();
		this.hvtestsm=new ArrayList<Matrix>();
		this.hstderrsm=new ArrayList<Matrix>();
		this.hstdglobsm=new ArrayList<Matrix>();
		this.hdavgsm=new ArrayList<Matrix>();
		this.hdavglobsm=new ArrayList<Matrix>();
		this.hdavgsmdef=new ArrayList<Matrix>();
		this.hdistribparams=new ArrayList<Matrix>();
		this.hname=new ArrayList<String>();
		// AD ticklist=new ArrayList(); /* code original
		ticklist=new ArrayList<Long>(); // */
		this.update(data, componentsIds);
		agm=new AgModel(cl,this);
	}
	
	public void updatedistrib(Matrix data, ArrayList<VariableDistribution> distrib){		
		distrib.clear();
		long idColumn = data.getColumnForLabel(Cluster.ID_C_NAME);
//		long classLabelColumn = data.getColumnForLabel(Cluster.CLASS_LABEL_C_NAME);
		for(int column =0; column < data.getColumnCount(); column++){
				String label = data.getColumnLabel(column);
				VariableDistribution vd = VariableDistributionFactory.buildDistribution(data, column);
				distrib.set(column, vd);			
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
	
	public void initdistrib(long l)
	{
		if (comdistribparams.getColumnCount()<2)
		{
			comdistribparams=Vtest.mfact.zeros(l,3);
			for (int i=0; i<l; i++)
			{
				comdistribparams.setAsLong(-20, i, 0);
				comdistribparams.setAsLong(0, i, 1);
				comdistribparams.setAsLong(0, i, 2);
			}

//		comdistribparams.showGUI();
		}
		distribparams=(DenseDoubleMatrix2D)comdistribparams.clone();
		distribparams=Vtest.mfact.zeros(l,3);
		for (int i=0; i<l; i++)
		{
			distribparams.setAsLong(-20, i, 0);
			distribparams.setAsLong(0, i, 1);
			distribparams.setAsLong(0, i, 2);
		}
		DenseDoubleMatrix2D nm=Vtest.mfact.zeros(this.NB_MAX_BIN+2,1);
//		this.globDistribN=new ArrayList<Matrix>();
//		this.popDistribN=new ArrayList<Matrix>();
//		this.defDistribN=new ArrayList<Matrix>();
	    this.davgsm=Vtest.mfactm.zeros(l,1);
//	    this.davgsm.showGUI();
	    this.davgsmdef=Vtest.mfactm.zeros(l,1);
	    this.davglobsm=Vtest.mfactm.zeros(l,1);
		for (int i=0; i<l; i++)
		{
	    	this.davgsm.setRowLabel(i,distribparams.getRowLabel(i));
	    	this.davgsmdef.setRowLabel(i,distribparams.getRowLabel(i));
	    	this.davglobsm.setRowLabel(i,distribparams.getRowLabel(i));
//			distribparams.setAsLong(-20, i, 0);
//			distribparams.setAsLong(0, i, 1);
//			distribparams.setAsLong(0, i, 2);
			this.davgsm.setAsMatrix(nm.clone(), i,0);
			this.davgsmdef.setAsMatrix(nm.clone(), i,0);
			this.davglobsm.setAsMatrix(nm.clone(), i,0);
		}
	}
	
	public Matrix reorder(Matrix morig, Matrix mnoms, Matrix refmat, boolean defvalue, Object defval)
	{
		Matrix mres=morig.clone();
		if (morig.getRowCount()!=refmat.getRowCount())
		{
			mres=refmat.clone();
			for (int i=0; i<refmat.getRowCount(); i++)
			{
				int cc=-1;
				if (mnoms.getRowCount()>i)
				if (mnoms.getAsString(i,0).equals(refmat.getRowLabel(i)))
				{
					cc=i;
				}
				if (cc==-1)
				{
					for (int j=0; j<morig.getRowCount(); j++)
					{
						if (mnoms.getAsString(j,0).equals(refmat.getRowLabel(i)))
								{
							System.out.println("Map C"+j+"->C"+i+"("+refmat.getRowLabel(i)+")");
							cc=j;
								}
					}
				}
				if (cc>=0)
				{
					for (int j=0; j<refmat.getColumnCount(); j++)
					{
						if (j<morig.getColumnCount())
						{
							mres.setAsObject(morig.getAsObject(cc,j), i,j);
						}
						if (j>=morig.getColumnCount())
						{
							mres.setAsObject(morig.getAsObject(cc,morig.getColumnCount()-1), i,j);
						}
					}
					
				}
				if (cc<0)
				{
					System.out.println("NoSuchVar C"+i+"n"+refmat.getRowLabel(i));
					if (defvalue)
					for (int j=0; j<refmat.getColumnCount(); j++)
					{
							mres.setAsObject(defval, i,j);
					}
					
				}
					
			}
		}
		if (morig.getColumnCount()<refmat.getColumnCount())
		{
			System.out.println("TimeExtension T"+morig.getColumnCount()+"->"+refmat.getColumnCount());
			for (int i=0; i<refmat.getRowCount(); i++)
			{
				for (long j=morig.getColumnCount(); j<refmat.getColumnCount(); j++)
				{
					mres.setAsObject(mres.getAsObject(i,morig.getColumnCount()-1), i,j);					
				}
			}
			
		}
		return mres;
	}
	
	public void rebin(Matrix morig,Matrix mref, Matrix distriborig, Matrix distribtarget)
	{
		Matrix mres=morig;
		Matrix disto,distref,ndist;
		for (int p=3; p<mref.getRowCount(); p++)
		{

				long fact=distriborig.getAsLong(p,0);
			double unit=Math.pow(2,fact);
			long debnb=distriborig.getAsLong(p,1);
			long nbbin=distriborig.getAsLong(p,2);
			double debvalue=unit*debnb;

			long newfact=distribtarget.getAsLong(p,0);
			double nunit=Math.pow(2,newfact);
			long ndebnb=distribtarget.getAsLong(p,1);
			long newnbbin=distribtarget.getAsLong(p,2);
			double ndebval=nunit*ndebnb;
			
		double ndeb,nfin,deb,fin;
		long nv;
			for (int mt=0; mt<mref.getColumnCount(); mt++)
			{
				distref=mref.getAsMatrix(p,mt);
				disto=morig.getAsMatrix(p,mt);
				ndist=distref.clone();
				long nbtotref=0;
				long nbtoto=0;
				for (int i=0; i<distref.getRowCount(); i++)
				{
					nbtotref=nbtotref+distref.getAsLong(i,0);
					nbtoto=nbtoto+disto.getAsLong(i,0);
				}
				for (long b=1; b<newnbbin; b++)
				{
					nv=0;
					ndeb=ndebval+(b-1)*nunit;
					nfin=ndebval+b*nunit;					
					for (long ob=1; ob<nbbin; ob++)
					{
						deb=debvalue+(ob-1)*unit;
						fin=debvalue+ob*unit;
						if (newfact==fact)
						{
						if ((deb>=ndeb)&(fin<=nfin))
						{
							nv=nv+disto.getAsLong(ob,0);
						}
						}
						if (newfact>fact)
						{
						if ((deb>=ndeb)&(fin<=nfin))
						{
							nv=nv+disto.getAsLong(ob,0);
						}
						}
						if (newfact<fact)
						{
						if ((deb<nfin)&(fin>ndeb))
						{
							nv=nv+disto.getAsLong(ob,0)/(long)Math.pow(2, (fact-newfact));
						}
						}
					}
					try {
						ndist.setAsLong(nv, b,0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
					nv=0;
					ndeb=ndebval;
					nfin=ndebval+newnbbin*nunit;					
					for (long ob=1; ob<nbbin; ob++)
					{
						deb=debvalue+(ob-1)*unit;
						fin=debvalue+ob*unit;
						if (fin<=ndeb)
						{
							nv=ndist.getAsLong(0,0)+disto.getAsLong(ob,0);
							ndist.setAsLong(nv,0,0);
							
						}
						if (deb>nfin)
						{
							nv=ndist.getAsLong(newnbbin+1,0)+disto.getAsLong(ob,0);
							ndist.setAsLong(nv,newnbbin+1,0);
							
						}
					}
					morig.setAsMatrix(ndist,p,mt);

			}
		}

		
		
	}
	

	
	public void mergeandmovebins(int p,long newfact, double newdeb)
	{
		long fact=distribparams.getAsLong(p,0);
		double unit=Math.pow(2,fact);
		long debnb=distribparams.getAsLong(p,1);
		long nbbin=distribparams.getAsLong(p,2);
		double debvalue=unit*debnb;

		double nunit=Math.pow(2,newfact);
		long ndebnb=(long)Math.floor(unit*(double)debnb/nunit);
		double ndebval=nunit*ndebnb;
		
		long newnbbin=nbbin;
		Matrix m=this.davgsm;
		
		if (newfact>fact)
		{
		newnbbin=1;
		m=this.davgsm;
		Matrix sm;
		double ndeb,nfin,deb,fin;
		long nv;
		for (int mn=0; mn<3; mn++)
		{
			for (int mt=0; mt<m.getColumnCount(); mt++)
			{
				sm=m.getAsMatrix(p,mt);
				for (long b=1; b<nbbin; b++)
				{
					nv=0;
					ndeb=ndebval+(b-1)*nunit;
					nfin=ndebval+b*nunit;
					for (long ob=1; ob<nbbin; ob++)
					{
						deb=debvalue+(ob-1)*unit;
						fin=debvalue+ob*unit;
						if ((deb>=ndeb)&(fin<=nfin))
						{
							nv=nv+sm.getAsLong(ob,0);
						}
					}
					sm.setAsLong(nv, b,0);
					if ((nv>0)&(newnbbin<b)) newnbbin=b;
				}
				
			}
			if (mn==0)  m=this.davglobsm;
			if (mn==1)  m=this.davgsmdef;			
		}
		}
		
		long newdebnb=(long)Math.floor(newdeb/nunit);
		if (newdebnb<ndebnb)
		{
			long dif=ndebnb-newdebnb;
			m=this.davgsm;
			Matrix sm;
			long nv;
			for (int mn=0; mn<3; mn++)
			{
				for (int mt=0; mt<m.getColumnCount(); mt++)
				{
					sm=m.getAsMatrix(p,mt);
					for (long b=Math.min(nbbin+dif, this.NB_MAX_BIN); b>dif; b--)
					{
						try {
							nv=sm.getAsLong(b-dif,0);
							sm.setAsLong(nv, b,0);
							if ((nv>0)&(newnbbin<b)) newnbbin=b;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
				if (mn==0)  m=this.davglobsm;
				if (mn==1)  m=this.davgsmdef;			
			}
			
		}
		
		distribparams.setAsLong(newfact, p, 0);
		distribparams.setAsLong(newdebnb, p, 1);
		distribparams.setAsLong(newnbbin, p, 2);
		long cfact=comdistribparams.getAsLong(p,0);
		double cunit=Math.pow(2,fact);
		long cdebnb=comdistribparams.getAsLong(p,1);
		long cnbbin=comdistribparams.getAsLong(p,2);
		double cdebvalue=cunit*cdebnb;
		if ((newfact!=cfact)||(newdebnb!=cdebnb))
		{
			comdistribparams.setAsLong(newfact, p, 0);
			comdistribparams.setAsLong(newdebnb, p, 1);
			comdistribparams.setAsLong(newnbbin, p, 2);			
		}
		
	}
	
	public void addtodistrib(double value, int p, int t, int dist)
	{
		long fact=distribparams.getAsLong(p,0);
		double unit=Math.pow(2,fact);
		long debnb=distribparams.getAsLong(p,1);
		long nbbin=distribparams.getAsLong(p,2);
		double debvalue=unit*debnb;
		long cfact=comdistribparams.getAsLong(p,0);
		double cunit=Math.pow(2,fact);
		long cdebnb=comdistribparams.getAsLong(p,1);
		long cnbbin=comdistribparams.getAsLong(p,2);
		double cdebvalue=cunit*cdebnb;
/*		if ((fact!=cfact)||(debnb!=cdebnb))
		{
			mergeandmovebins(p,cfact,cdebvalue);			
			fact=distribparams.getAsLong(p,0);
			unit=Math.pow(2,fact);
			debnb=distribparams.getAsLong(p,1);
			nbbin=distribparams.getAsLong(p,2);
			debvalue=unit*debnb;
		}*/

		Matrix distm=this.comdistribparams;
	    Matrix distmdef=this.comdistribparams;
		Matrix distmglob=this.comdistribparams;
		if (t<this.davgsm.getColumnCount())
			distm = this.davgsm.getAsMatrix(p,t);
		if (t<this.davgsmdef.getColumnCount())
		distmdef=this.davgsmdef.getAsMatrix(p,t);
		if (t<this.davglobsm.getColumnCount())
			distmglob=this.davglobsm.getAsMatrix(p,t);
	
		long targetbin=0;
		if (nbbin==0)
		{
			nbbin=1;
			if ((value<10)&(value>-10))
			{
				fact=-20;
				unit=Math.pow(2,fact);
				debnb=(long)Math.floor(value/unit);
				nbbin=1;
				targetbin=1;
				debvalue=unit*debnb;
			}
			else
			{
				fact=0;
				unit=Math.pow(2,fact);
				debnb=(long)Math.floor(value);
				nbbin=1;
				targetbin=1;
				debvalue=unit*debnb;
			}
		}
		else
		{
			targetbin=(long)Math.ceil((value-debvalue)/unit)+1;
			if ((targetbin>nbbin)&(targetbin<=this.NB_MAX_BIN))
			{
				nbbin=targetbin;
			}
			if (targetbin>this.NB_MAX_BIN)
			{
				double targetspace=(value-debvalue)/((double)this.NB_INIT_BIN);
				double targetpow=Math.log(targetspace)/Math.log(2);
				long newfact=(long)Math.ceil(targetpow);
				mergeandmovebins(p,newfact,debvalue);
				fact=distribparams.getAsLong(p,0);
				unit=Math.pow(2,fact);
				debnb=distribparams.getAsLong(p,1);
				nbbin=distribparams.getAsLong(p,2);
				debvalue=unit*debnb;
			}
			else
			if (targetbin<1)
			{
				if ((nbbin-targetbin)>this.NB_MAX_BIN)
				{
					double targetspace=(debvalue+nbbin*unit-value)/((double)this.NB_INIT_BIN);
					double targetpow=Math.log(targetspace)/Math.log(2);
					long newfact=(long)Math.ceil(targetpow);
					mergeandmovebins(p,newfact,value);
					fact=distribparams.getAsLong(p,0);
					unit=Math.pow(2,fact);
					debnb=distribparams.getAsLong(p,1);
					nbbin=distribparams.getAsLong(p,2);
					debvalue=unit*debnb;
					
				}
				else
				{
					mergeandmovebins(p,fact,value);
					fact=distribparams.getAsLong(p,0);
					unit=Math.pow(2,fact);
					debnb=distribparams.getAsLong(p,1);
					nbbin=distribparams.getAsLong(p,2);
					debvalue=unit*debnb;					
				}
			}
			
		}
		targetbin=(long)Math.floor((value-debvalue)/unit)+1;

		try {
			if (dist==0) distm.setAsLong(distm.getAsLong(targetbin,0)+1, targetbin, 0);
			if (dist==1) distmdef.setAsLong(distmdef.getAsLong(targetbin,0)+1, targetbin, 0);
			if (dist==2) distmglob.setAsLong(distmglob.getAsLong(targetbin,0)+1, targetbin, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		distribparams.setAsLong(fact, p, 0);
		distribparams.setAsLong(debnb, p, 1);
		distribparams.setAsLong(nbbin, p, 2);
		
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
