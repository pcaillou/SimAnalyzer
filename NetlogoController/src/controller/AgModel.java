package controller;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import netlogo.NetLogoSimulationController;

import observer.SimulationInterface;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.exceptions.MatrixException;

import controller.SimAnalyzer.OpenProject;


import clustering.Cluster;
import clustering.Clusterer;
public class AgModel 
{
	Clusterer clusterer;
	Cluster clustinit;
//	List<Cluster> clustHistPop;
	public List<Cluster> clustHistDef;
	public double[] scorevar;
	public double[] scorevardef;
	public double[] scorestabdet;
	public double scorestabdesc;
	public double scorestabpop;

	
	public AgModel(Clusterer cler, Cluster cl)
	{
		clustinit=cl;
		clusterer=cler;
		clustHistDef=new ArrayList();
		clustHistDef.add(cl);
		
	}
	
	public void calcscores()
	{
		Matrix mbase,mbaseavg;
		Matrix mtemp;
		double sco,div;
		double scodef,divdef;
		mbase=clustinit.vtestsm;
		mbaseavg=clustinit.avgsm;
		scorevar=new double[(int)mbase.getRowCount()];
		scorestabdet=new double[(int)mbase.getRowCount()];
		scorevardef=new double[(int)mbase.getRowCount()];
		scorestabdesc=0;
		scorestabpop=0;
		for (int i=0; i<3; i++)
		{
			scorevar[i]=0;
			scorevardef[i]=0;
		}
		for (int i=3; i<mbase.getRowCount();i++)
		{
			
			mtemp=mbase.selectRows(Ret.LINK, i);
			sco=mtemp.abs(Ret.LINK).getMeanValue();
			sco=sco*mbaseavg.selectRows(Ret.LINK, i).getStdValue();
			div=mbaseavg.abs(Ret.LINK).selectRows(Ret.LINK, i).getMeanValue();
			if (div>0)
			{
				sco=sco/div;
			}
			else
			{
				sco=0;
			}
			scorevar[i]=sco;
		}
		mbase=clustinit.vtestsmdef;
		mbaseavg=clustinit.avgsmdef;
		for (int i=3; i<mbase.getRowCount();i++)
		{
			mtemp=mbase.selectRows(Ret.LINK, i);
			sco=mtemp.abs(Ret.LINK).getMeanValue();
			sco=sco*mbaseavg.selectRows(Ret.LINK, i).getStdValue();
			div=mbaseavg.abs(Ret.LINK).selectRows(Ret.LINK, i).getMeanValue();
			if (div>0)
			{
				sco=sco/div;
			}
			else
			{
				sco=0;
			}
			scorevardef[i]=sco;
		}
		scorestabdesc=0;
		int nbvarninit;
		for (int i=3; i<mbase.getRowCount();i++)
		{
			scorestabdet[i]=0;
			for (int j=0; j<mbase.getColumnCount();j++)
			{
				scorestabdet[i]+=Math.abs(clustinit.vtestsm.getAsDouble(i,j)-clustinit.vtestsm.getAsDouble(i,clustinit.idtickinit))/Math.max(Math.abs(clustinit.vtestsm.getAsDouble(i,j)),Math.abs(clustinit.vtestsm.getAsDouble(i,clustinit.idtickinit)))/mbase.getColumnCount();
			}
			scorestabdet[i]=1-scorestabdet[i];
			if (!Double.isNaN(scorestabdet[i]))
			{
			scorestabdesc=scorestabdesc+scorestabdet[i]/(mbase.getRowCount()-3);
			}
		}
		scorestabpop=0;
		Cluster nc;
		for (int j=0; j<clustHistDef.size();j++)
		{
			nc=clustHistDef.get(j);
			scorestabpop+=(double)clustinit.getNumberOfCommonComponents(nc)/(double)Math.max(clustinit.getComponentIds().size(), nc.getComponentIds().size());			
		}
		scorestabpop=(double)scorestabpop/clustinit.vtestsm.getColumnCount();


		
		
		long idColumn = mbase.getRowForLabel(Cluster.ID_C_NAME);
		long classLabelColumn = mbase.getRowForLabel(Cluster.CLASS_LABEL_C_NAME);
		long labelColorColumn = mbase.getRowForLabel("LABEL-COLOR");
		for (int i=3; i<mbase.getRowCount();i++)
		{
			Pattern p = Pattern.compile("CLASS_LABEL");
			Matcher m = p.matcher(mbase.getRowLabel(i));
			if((i == idColumn
					|| i == classLabelColumn 
					|| i == labelColorColumn
					|| m.lookingAt())){
				scorevardef[i]=0;
				scorevar[i]=0;
			}
		}

	}
}