package controller;

import java.util.ArrayList;
import java.util.HashMap;

import observer.SimulationInterface;

import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.ValueType;

import statistic.distribution.VariableDistribution;
//AD import org.ujmp.core.exceptions.MatrixException;

import clustering.Cluster;

@SuppressWarnings("deprecation")
public class Vtest{
	public static Matrix VtestM(Cluster clu, Matrix mg)
	{
		double[] vt=Vtestglob(clu,mg,0,null);
		Matrix m = null ;
		m = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    for(int j=0;j<mg.getColumnCount();j++)
	    {
	    	m.setAsDouble(vt[j], j+3, 0);
	    }
    	m.setAsDouble(clu.getSize(), 2, 0);
		return m;
	}
	
	@SuppressWarnings("all")
	public static double[] Vtest(Cluster clu, Matrix mg)
	{
		return Vtestglob(clu,mg,0,null);
	}
	public static double[] Vtestinit(Cluster clu, Matrix mg)
	{
		return Vtestglob(clu,mg,1,null);
		
	}
	public static double[] Vtestadd(Cluster clu, Matrix mg,Cluster cib)
	{
		return Vtestglob(clu,mg,2,cib);
		
	}
	public static double[] Vtestpop(Cluster clu, Matrix mg)
	{
		return Vtestglob(clu,mg,3,clu);
		
	}
	
	public static void updateDistrib(Matrix m, ArrayList<HashMap<String,Integer>> nl)
	{
		nl.clear();
		nl.ensureCapacity((int)m.getColumnCount()+1);
	    for(int j=0;j<m.getColumnCount();j++)
	    {
			if (SimulationController.VQuali[j])
			{
				HashMap nhm=new HashMap<String,Integer>();
				for (int i=0; i<m.getRowCount(); i++)
				{
					if (nhm.containsKey(m.getAsString(i,j)))
					{
						nhm.put(m.getAsString(i,j), (Integer)nhm.get(m.getAsString(i,j))+1);
					}
					else
					{
						nhm.put(m.getAsString(i,j), 1);						
					}
				}
				nl.add(j,nhm);				
			}
			else
			{
				HashMap nhm=new HashMap<String,Integer>();
				nhm.put("numeric", m.getRowCount());
				nl.add(j,nhm);				
				
			}
	    	
	    }
		
	}
	
	public static String genString(HashMap<String,Double> dist,int t)
	{
		String res="";
		String ress="";
		String rest;
		double vmax=1;
		double vmin=-1;
		if (dist==null)
		{
			res="null";
			ress="n";
			
		}
		else
		if (dist.size()>0)
		{
			int n=0;
			int nm=0;
			for (String rep:dist.keySet())
			{
				if (dist.get(rep)>2)
				{
					n++;
					nm++;
					res=res+rep+"("+((int)(dist.get(rep)*100)/(double)100)+")";
					if (dist.get(rep)>vmax)
					{
						vmax=dist.get(rep);
					}
				}
			}
			if (nm>1)
			ress=ress+((int)(vmax*100)/(double)100)+"+/";
			if (nm==1)
				ress=ress+((int)(vmax*100)/(double)100)+"/";
			if (nm==0)
				ress=ress+"-/";
			nm=0;
			for (String rep:dist.keySet())
			{
				if (dist.get(rep)<-2)
				{
					n++;
					nm++;
					res=res+rep+"("+((int)(dist.get(rep)*100)/(double)100)+")";
					if (dist.get(rep)<vmin)
					{
						vmin=dist.get(rep);
					}
				}
			}
			if (nm>1)
				ress=ress+((int)(vmin*100)/(double)100)+"+";
			if (nm==1)
				ress=ress+((int)(vmin*100)/(double)100)+"";
			if (nm==0)
				ress=ress+"-";

			if (n==0) res="--";
				
			
		}
		else
		{
			res="noMod";
			ress="_";
		}
		rest=ress;
		if (t==0)
		rest=res;
		return rest;
	}
	
	public static String[] genString(HashMap<String,Double>[] nl,int s,int t)
	{
		String[] nst=new String[s];
		for (int i=0; i<s;i++)
		{
			nst[i]=genString(nl[i],t);
		}
		return nst;
	}
	
	public static String[] Vtestglobquali(Cluster clu, Matrix mg,int type, Cluster cib)
	{
		Variance varcalc=new Variance();
		Mean meancalc=new Mean();
		RealMatrixImpl rmatm,rmatg;
		long idColumn = mg.getColumnForLabel(SimulationInterface.ID_C_NAME);
		long n = mg.getRowCount();
		long ng = clu.getSize();
		@SuppressWarnings("unused")
		int pops=0;
		double[] va = new double[(int) mg.getColumnCount()];
		double[] vag = new double[(int) mg.getColumnCount()];
		double[] nm = new double[(int) mg.getColumnCount()];
		double[] nmg = new double[(int) mg.getColumnCount()];
//		HashMap<String,VariableDistribution> vdcible=new HashMap<String,VariableDistribution>();
		Matrix m = null ;
//		double[] vtest = new double[(int) mg.getColumnCount()];
		HashMap<String,Double>[] vtest = new HashMap[(int) mg.getColumnCount()];
/*	    for(long component: clu.getComponentIds())
	    {
	    	for(int j=0;j<n;j++)
    		{
	    		if(mg.getAsLong(j,idColumn)==component)
    			{
	    			if(m==null)
	    				m = mg.selectRows(Ret.LINK, j);
	    			else
	    				m = m.appendVertically(mg.selectRows(Ret.LINK, j));
    			}
    		}
	    }*/
		ArrayList<Long> rows = new ArrayList<Long>();
//		data.showGUI();
	    for(long component: clu.getComponentIds())
	    {
	    	for(long j=0;j<n;j++)
    		{
	    		if(mg.getAsLong(j,idColumn)==component)
    			{
	    			rows.add(j);
    			}
    		}
	    }
		m = mg.selectRows(Ret.LINK, rows);

		updateDistrib(mg, clu.globDistrib);
		
	    for(int j=0;j<mg.getColumnCount();j++)
	    {
			if (SimulationController.VQuali[j])
			{
				HashMap nhm=new HashMap<String,Double>();
				vtest[j]=nhm;
				
			}
			else
			{
				HashMap nhm=new HashMap<String,Double>();
				nhm.put("numeric", 0.0);
				vtest[j]=nhm;
				
			}
	    	
	    }
	    if ((rows.size()==0)|(m==null))
	    {
	    	clu.popDistrib=clu.globDistrib;
	    	
	    }
	    else
	    {
			updateDistrib(m, clu.popDistrib);
	    	
	    }
		
	    int nk=rows.size();
	    int ntot=(int)mg.getRowCount();
	    if ((rows.size()==0)|(m==null))
//		    for(int j=0;j<mg.getColumnCount();j++)
	    {
	    	
	    }
	    else
	    for(int j=0;j<mg.getColumnCount();j++)
			if (SimulationController.VQuali[j])
	    {
			HashMap nhm=new HashMap<String,Double>();
			for (String rep:clu.popDistrib.get(j).keySet())
			{
		    	double nj=clu.globDistrib.get(j).get(rep);
				double nkj=clu.popDistrib.get(j).get(rep);
				double Ekj=((double)(nk*nj))/ntot;
				double Skj=nk*((double)(ntot-nk))/(ntot-1)*((double)nj)/n*(1.0-((double)nj)/n);
				double vt=((double)(nkj-Ekj))/Skj;
		        if (vt<0.0000000001) vt=0;
				nhm.put(rep,vt);
				
			}
			vtest[j]=nhm;
	    }
	    if (type==1)
	    {
	    	clu.qvtests=genString(vtest,(int)mg.getColumnCount(),0);
	    	clu.qvtestsshort=genString(vtest,(int)mg.getColumnCount(),1);
			clu.qvtestsm = MatrixFactory.dense(ValueType.OBJECT,mg.getColumnCount()+3,1);		    
	    	clu.qvtestsm.setRowLabel(1, "tick");
	    	clu.qvtestsm.setRowLabel(2, "size");
	    	clu.qvtestsm.setAsDouble(clu.getSize(), 2, 0);
	    	clu.qvtestsm.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.qvtestsm.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.qvtestsm.setAsObject(vtest[j], j+3, 0);
		    }
			clu.qvtestsmdef =MatrixFactory.dense(ValueType.OBJECT,mg.getColumnCount()+3,1);	
	    	clu.qvtestsmdef.setRowLabel(1, "tick");
	    	clu.qvtestsmdef.setRowLabel(2, "size");
	    	clu.qvtestsmdef.setAsDouble(clu.getSize(), 2, 0);
	    	clu.qvtestsmdef.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.qvtestsmdef.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.qvtestsmdef.setAsObject(vtest[j], j+3, 0);
		    }
	    	
	    }
	    if (type==2)
   		if (cib.ticklist.contains(SimulationController.currenttick)==false)
	    {
			Matrix mn = MatrixFactory.dense(ValueType.OBJECT,mg.getColumnCount()+3,1);			    
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	mn.setAsObject(vtest[j], j+3, 0);
		    }
//		    cib.vtestsm=cib.vtestsm.appendVertically(mn);	
	    	mn.setAsDouble(clu.getSize(), 2, 0);
	    	if (SimulationController.currenttick>=cib.ticklist.get(0))
	    	{
	    	cib.qvtestsmdef=MyMatrix.appendHorizontally(cib.qvtestsmdef, mn);
	    	}
	    	else
	    	{
		    	cib.qvtestsmdef=MyMatrix.appendHorizontally(mn,cib.qvtestsmdef,true);
	    		
	    	}

	    }
	    if (type==4)
//	    if (cib.ticklist.contains(SimulationController.currenttick)==false)
	    {
	    	// par pop
//			cib.vtestsm.showGUI();
//	    	cib.ticklist.add(SimulationController.currenttick);
			Matrix mn =MatrixFactory.dense(ValueType.OBJECT,mg.getColumnCount()+3,1);		 
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	mn.setAsObject(vtest[j], j+3, 0);
		    }
//		    cib.vtestsm=cib.vtestsm.appendVertically(mn);	
		    if (m==null)
		    {
		    	mn.setAsDouble(0, 2, 0);
		    	
		    }
		    else
		    {
	    	mn.setAsDouble(clu.getSize(), 2, 0);
		    }
//	    	mn.showGUI();
//			cib.vtestsm.appendHorizontally(mn.selectColumns(Ret.LINK,0));
	    	if (SimulationController.currenttick>=cib.ticklist.get(0))
	    	{
	    	
	    		cib.qvtestsm=MyMatrix.appendHorizontally(cib.qvtestsm, mn);
	    	}
	    	else
	    	{
	    		cib.qvtestsm=MyMatrix.appendHorizontally(mn,cib.qvtestsm,true);
    		
	    	}
//			cib.avgsm.showGUI();
//			cib.avglobsm.showGUI();
//			cib.vtestsm.setSize(cib.vtestsm.getSize(0),cib.vtestsm.getSize(1)+1);
//			cib.vtestsm.showGUI();
//			cib.vtestsmdef.showGUI();
	    }
	    
	    return genString(vtest,(int)mg.getColumnCount(),1);
	}

	public static double[] Vtestglob(Cluster clu, Matrix mg,int type, Cluster cib)
	{
		Vtestglobquali(clu,mg,type,cib);
		Variance varcalc=new Variance();
		Mean meancalc=new Mean();
		RealMatrixImpl rmatm,rmatg;
		long idColumn = mg.getColumnForLabel(SimulationInterface.ID_C_NAME);
		long n = mg.getRowCount();
		long ng = clu.getSize();
		@SuppressWarnings("unused")
		int pops=0;
		double[] va = new double[(int) mg.getColumnCount()];
		double[] vag = new double[(int) mg.getColumnCount()];
		double[] nm = new double[(int) mg.getColumnCount()];
		double[] nmg = new double[(int) mg.getColumnCount()];
		Matrix m = null ;
		double[] vtest = new double[(int) mg.getColumnCount()];
/*	    for(long component: clu.getComponentIds())
	    {
	    	for(int j=0;j<n;j++)
    		{
	    		if(mg.getAsLong(j,idColumn)==component)
    			{
	    			if(m==null)
	    				m = mg.selectRows(Ret.LINK, j);
	    			else
	    				m = m.appendVertically(mg.selectRows(Ret.LINK, j));
    			}
    		}
	    }*/
		ArrayList<Long> rows = new ArrayList<Long>();
//		data.showGUI();
	    for(long component: clu.getComponentIds())
	    {
	    	for(long j=0;j<n;j++)
    		{
	    		if(mg.getAsLong(j,idColumn)==component)
    			{
	    			rows.add(j);
    			}
    		}
	    }
		m = mg.selectRows(Ret.LINK, rows);

	    
	    if ((rows.size()==0)|(m==null))
		    for(int j=0;j<mg.getColumnCount();j++)
	    {
			    rmatg=new RealMatrixImpl(mg.selectColumns(Ret.LINK, j).toDoubleArray());
//		    	double vat = Math.pow(mg.selectColumns(Ret.LINK, j).getStdValue(),2);
		    	va[j] = varcalc.evaluate(rmatg.getColumn(0));
	    	vag[j] = va[j];
//	    	nm[j] = mg.selectColumns(Ret.LINK,j).getMeanValue();
	    	nm[j] = meancalc.evaluate(rmatg.getColumn(0));
	    	nmg[j] = nm[j];
	        vtest[j]=0;
	    	
	    }
	    else
	    for(int j=0;j<mg.getColumnCount();j++)
	    {
		    rmatg=new RealMatrixImpl(mg.selectColumns(Ret.LINK, j).toDoubleArray());
		    rmatm=new RealMatrixImpl(m.selectColumns(Ret.LINK, j).toDoubleArray());
//	    	double vat = Math.pow(mg.selectColumns(Ret.LINK, j).getStdValue(),2);
	    	va[j] = varcalc.evaluate(rmatg.getColumn(0));
//	    	if ((Math.abs(va[j]-vat)/vat>0.00001)&(vat>0.00001)) 	    		System.out.println("VAR ERROR"+va[j]+" / "+vat+" "+mg.selectColumns(Ret.LINK, j)+" / "+mg.selectColumns(Ret.LINK, j).transpose().toDoubleArray()[0]);
//	    	vag[j] = Math.pow(m.selectColumns(Ret.LINK, j).getStdValue(),2);
	    	vag[j] = varcalc.evaluate(rmatm.getColumn(0));
//	    	vag[j] = Math.pow(Array. m.selectColumns(Ret.LINK, j).toDoubleArray().,2);
//	    	nm[j] = mg.selectColumns(Ret.LINK,j).getMeanValue();
	    	nm[j] = meancalc.evaluate(rmatg.getColumn(0));
//	    	nmg[j] = m.selectColumns(Ret.LINK,j).getMeanValue();
	    	nmg[j] = meancalc.evaluate(rmatm.getColumn(0));
	        vtest[j]=(nmg[j]-nm[j])/Math.sqrt((((double)n-(double)ng)/((double)n-1))*((double)va[j]/ng));
	 //       System.out.println("v"+mg.getColumnLabel(j)+" nmg "+nmg[j]+" va "+va[j]+" vag "+vag[j]+" n "+n+" ng "+ng);
	        if (va[j]<0.0000000001) vtest[j]=0;
	    }
	    if (type==1)
	    {
	    	clu.ticklist=new ArrayList<Long>();
	    	clu.ticklist.add(SimulationController.currenttick);
	    	clu.vtests=vtest;
	    	clu.avg=nmg;
	    	clu.stderr=vag;
			clu.vtestsm = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.vtestsm.setRowLabel(1, "tick");
	    	clu.vtestsm.setRowLabel(2, "size");
	    	clu.vtestsm.setAsDouble(clu.getSize(), 2, 0);
	    	clu.vtestsm.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.vtestsm.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.vtestsm.setAsDouble(vtest[j], j+3, 0);
		    }
			clu.avgsm = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.avgsm.setRowLabel(1, "tick");
	    	clu.avgsm.setRowLabel(2, "size");
	    	clu.avgsm.setAsDouble(clu.getSize(), 2, 0);
	    	clu.avgsm.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.avgsm.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.avgsm.setAsDouble(nmg[j], j+3, 0);
		    }
			clu.stderrsm = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.stderrsm.setRowLabel(1, "tick");
	    	clu.stderrsm.setRowLabel(2, "size");
	    	clu.stderrsm.setAsDouble(clu.getSize(), 2, 0);
	    	clu.stderrsm.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.stderrsm.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.stderrsm.setAsDouble(Math.sqrt(vag[j]), j+3, 0);
		    }
			clu.vtestsmdef = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.vtestsmdef.setRowLabel(1, "tick");
	    	clu.vtestsmdef.setRowLabel(2, "size");
	    	clu.vtestsmdef.setAsDouble(clu.getSize(), 2, 0);
	    	clu.vtestsmdef.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.vtestsmdef.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.vtestsmdef.setAsDouble(vtest[j], j+3, 0);
		    }
			clu.avgsmdef = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.avgsmdef.setRowLabel(1, "tick");
	    	clu.avgsmdef.setRowLabel(2, "size");
	    	clu.avgsmdef.setAsDouble(clu.getSize(), 2, 0);
	    	clu.avgsmdef.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.avgsmdef.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.avgsmdef.setAsDouble(nmg[j], j+3, 0);
		    }
			clu.stderrsmdef = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.stderrsmdef.setRowLabel(1, "tick");
	    	clu.stderrsmdef.setRowLabel(2, "size");
	    	clu.stderrsmdef.setAsDouble(clu.getSize(), 2, 0);
	    	clu.stderrsmdef.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.stderrsmdef.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.stderrsmdef.setAsDouble(Math.sqrt(vag[j]), j+3, 0);
		    }
			clu.avglobsm = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.avglobsm.setRowLabel(1, "tick");
	    	clu.avglobsm.setRowLabel(2, "size");
	    	clu.avglobsm.setAsDouble(n, 2, 0);
	    	clu.avglobsm.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.avglobsm.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.avglobsm.setAsDouble(nm[j], j+3, 0);
		    }
			clu.stdglobsm = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
	    	clu.stdglobsm.setRowLabel(1, "tick");
	    	clu.stdglobsm.setRowLabel(2, "size");
	    	clu.stdglobsm.setAsDouble(n, 2, 0);
	    	clu.stdglobsm.setAsDouble(SimulationController.currenttick, 1, 0);
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	clu.stdglobsm.setRowLabel(j+3, mg.getColumnLabel(j));
		    	clu.stdglobsm.setAsDouble(Math.sqrt(va[j]), j+3, 0);
		    }
	    }
	    if (type==2)
   		if (cib.ticklist.contains(SimulationController.currenttick)==false)
	    {
	    	// par def
//			cib.vtestsm.showGUI();
			cib.agm.clustHistDef.add(clu);
	    	cib.ticklist.add(SimulationController.currenttick);
	    	if (SimulationController.currenttick<cib.ticklist.get(0))
	    	{
	    		cib.idtickinit++;
	    	}
			Matrix mn = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mna = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mns = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mnag = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mnsg = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	mn.setAsDouble(vtest[j], j+3, 0);
		    	mna.setAsDouble(nmg[j], j+3, 0);
		    	mns.setAsDouble(Math.sqrt(vag[j]), j+3, 0);
		    	mnag.setAsDouble(nm[j], j+3, 0);
		    	mnsg.setAsDouble(Math.sqrt(va[j]), j+3, 0);
		    }
//		    cib.vtestsm=cib.vtestsm.appendVertically(mn);	
	    	mn.setAsDouble(clu.getSize(), 2, 0);
	    	mna.setAsDouble(clu.getSize(), 2, 0);
	    	mns.setAsDouble(clu.getSize(), 2, 0);
	    	mnag.setAsDouble(n, 2, 0);
	    	mnsg.setAsDouble(n, 2, 0);
	    	if (SimulationController.currenttick>=cib.ticklist.get(0))
	    	{
	    	cib.vtestsmdef=MyMatrix.appendHorizontally(cib.vtestsmdef, mn);
			cib.avgsmdef=MyMatrix.appendHorizontally(cib.avgsmdef,mna);	
			cib.stderrsmdef=MyMatrix.appendHorizontally(cib.stderrsmdef,mns);	
			cib.avglobsm=MyMatrix.appendHorizontally(cib.avglobsm,mnag);	
			cib.stdglobsm=MyMatrix.appendHorizontally(cib.stdglobsm,mnsg);	
	    	}
	    	else
	    	{
		    	cib.vtestsmdef=MyMatrix.appendHorizontally(mn,cib.vtestsmdef,true);
				cib.avgsmdef=MyMatrix.appendHorizontally(mna,cib.avgsmdef,true);	
				cib.stderrsmdef=MyMatrix.appendHorizontally(mns,cib.stderrsmdef,true);	
				cib.avglobsm=MyMatrix.appendHorizontally(mnag,cib.avglobsm,true);	
				cib.stdglobsm=MyMatrix.appendHorizontally(mnsg,cib.stdglobsm,true);	
	    		
	    	}

			Vtestglob(cib,mg,4,cib);
	    }
	    if (type==4)
//	    if (cib.ticklist.contains(SimulationController.currenttick)==false)
	    {
	    	// par pop
//			cib.vtestsm.showGUI();
//	    	cib.ticklist.add(SimulationController.currenttick);
			Matrix mn = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mna = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mns = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mnag = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mnsg = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	mn.setAsDouble(vtest[j], j+3, 0);
		    	mna.setAsDouble(nmg[j], j+3, 0);
		    	mns.setAsDouble(Math.sqrt(vag[j]), j+3, 0);
		    	mnag.setAsDouble(nm[j], j+3, 0);
		    	mnsg.setAsDouble(Math.sqrt(va[j]), j+3, 0);
		    }
//		    cib.vtestsm=cib.vtestsm.appendVertically(mn);	
		    if (m==null)
		    {
		    	mn.setAsDouble(0, 2, 0);
		    	mna.setAsDouble(0, 2, 0);
		    	mns.setAsDouble(0, 2, 0);
		    	
		    }
		    else
		    {
	    	mn.setAsDouble(clu.getSize(), 2, 0);
	    	mna.setAsDouble(clu.getSize(), 2, 0);
	    	mns.setAsDouble(clu.getSize(), 2, 0);
		    }
	    	mnag.setAsDouble(n, 2, 0);
	    	mnsg.setAsDouble(n, 2, 0);
//	    	mn.showGUI();
//			cib.vtestsm.appendHorizontally(mn.selectColumns(Ret.LINK,0));
	    	if (SimulationController.currenttick>=cib.ticklist.get(0))
	    	{
	    	
	    		cib.vtestsm=MyMatrix.appendHorizontally(cib.vtestsm, mn);
	    		cib.avgsm=MyMatrix.appendHorizontally(cib.avgsm,mna);	
	    		cib.stderrsm=MyMatrix.appendHorizontally(cib.stderrsm,mns);	
	    	}
	    	else
	    	{
	    		cib.vtestsm=MyMatrix.appendHorizontally(mn,cib.vtestsm,true);
	    		cib.avgsm=MyMatrix.appendHorizontally(mna,cib.avgsm,true);	
	    		cib.stderrsm=MyMatrix.appendHorizontally(mns,cib.stderrsm,true);	
    		
	    	}
//			cib.avgsm.showGUI();
//			cib.avglobsm.showGUI();
//			cib.vtestsm.setSize(cib.vtestsm.getSize(0),cib.vtestsm.getSize(1)+1);
//			cib.vtestsm.showGUI();
//			cib.vtestsmdef.showGUI();
	    }
	    
	    return vtest;
	}
	@SuppressWarnings("unused")
	public static double[] Vtestaddzeropop(Cluster cib, Matrix mg)
	{
		Variance varcalc=new Variance();
		Mean meancalc=new Mean();
		RealMatrixImpl rmatm,rmatg;
		long idColumn = mg.getColumnForLabel(SimulationInterface.ID_C_NAME);
		long n = mg.getRowCount();
		long ng = 0;
		double[] va = new double[(int) mg.getColumnCount()];
		double[] vag = new double[(int) mg.getColumnCount()];
		double[] nm = new double[(int) mg.getColumnCount()];
		double[] nmg = new double[(int) mg.getColumnCount()];
		Matrix m = null ;
		double[] vtest = new double[(int) mg.getColumnCount()];
	    for(int j=0;j<mg.getColumnCount();j++)
	    {
		    rmatg=new RealMatrixImpl(mg.selectColumns(Ret.LINK, j).toDoubleArray());
//	    	double vat = Math.pow(mg.selectColumns(Ret.LINK, j).getStdValue(),2);
	    	va[j] = varcalc.evaluate(rmatg.getColumn(0));
    	vag[j] = va[j];
//    	nm[j] = mg.selectColumns(Ret.LINK,j).getMeanValue();
    	nm[j] = meancalc.evaluate(rmatg.getColumn(0));
    	nmg[j] = nm[j];
        vtest[j]=0;
/*	    	va[j] = Math.pow(mg.selectColumns(Ret.LINK, j).getStdValue(),2);
	    	vag[j] = va[j];
	    	nm[j] = mg.selectColumns(Ret.LINK,j).getMeanValue();
	    	nmg[j] =nm[j];
	        vtest[j]=0;*/
	    }
   		if (cib.ticklist.contains(SimulationController.currenttick)==false)
	    {
	    	// par def
//			cib.vtestsm.showGUI();
	    	cib.ticklist.add(SimulationController.currenttick);
	    	if (SimulationController.currenttick<cib.ticklist.get(0))
	    	{
	    		cib.idtickinit++;
	    	}
			Matrix mn = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mna = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mns = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mnag = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
			Matrix mnsg = MatrixFactory.sparse(mg.getColumnCount()+3,1);		    
		    for(int j=0;j<mg.getColumnCount();j++)
		    {
		    	mn.setAsDouble(vtest[j], j+3, 0);
		    	mna.setAsDouble(nmg[j], j+3, 0);
		    	mns.setAsDouble(Math.sqrt(vag[j]), j+3, 0);
		    	mnag.setAsDouble(nm[j], j+3, 0);
		    	mnsg.setAsDouble(Math.sqrt(va[j]), j+3, 0);
		    }
//		    cib.vtestsm=cib.vtestsm.appendVertically(mn);	
	    	mn.setAsDouble(0, 2, 0);
	    	mna.setAsDouble(0, 2, 0);
	    	mns.setAsDouble(0, 2, 0);
	    	mnag.setAsDouble(n, 2, 0);
	    	mnsg.setAsDouble(n, 2, 0);
	    	if (SimulationController.currenttick>=cib.ticklist.get(0))
	    	{
	    	cib.vtestsmdef=MyMatrix.appendHorizontally(cib.vtestsmdef, mn);
			cib.avgsmdef=MyMatrix.appendHorizontally(cib.avgsmdef,mna);	
			cib.stderrsmdef=MyMatrix.appendHorizontally(cib.stderrsmdef,mns);	
			cib.avglobsm=MyMatrix.appendHorizontally(cib.avglobsm,mnag);	
			cib.stdglobsm=MyMatrix.appendHorizontally(cib.stdglobsm,mnsg);	
	    	}
	    	else
	    	{
		    	cib.vtestsmdef=MyMatrix.appendHorizontally(mn,cib.vtestsmdef,true);
				cib.avgsmdef=MyMatrix.appendHorizontally(mna,cib.avgsmdef,true);	
				cib.stderrsmdef=MyMatrix.appendHorizontally(mns,cib.stderrsmdef,true);	
				cib.avglobsm=MyMatrix.appendHorizontally(mnag,cib.avglobsm,true);	
				cib.stdglobsm=MyMatrix.appendHorizontally(mnsg,cib.stdglobsm,true);	
	    		
	    	}

			Vtestglob(cib,mg,4,cib);
	    }
	    
	    return vtest;
	}
}

