package controller;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyPanel extends JPanel implements ActionListener {
	
	HashMap<Integer, Double> tosortmap=new HashMap<Integer, Double>();
	Map<Integer, Double> sortedmap=new HashMap<Integer, Double>();
	ArrayList<ArrayList<JComponent>> lablist=new ArrayList<ArrayList<JComponent>>();
	ArrayList<ArrayList<Double>> vallist=new ArrayList<ArrayList<Double>>();
	ArrayList<JComponent> titlelist=new ArrayList<JComponent>();
	ArrayList<Integer> posydeb=new ArrayList<Integer>();
	ArrayList<Integer> posxdeb=new ArrayList<Integer>();
	GridBagConstraints gb;
	JPanel jp;
	int suivcoly=0;
	int suivcolx=0;
	int currentsort=0;
	int nbcurrentsort=0;
	
	public void cleartab()
	{
		tosortmap=new HashMap<Integer, Double>();
		sortedmap=new HashMap<Integer, Double>();
		lablist=new ArrayList<ArrayList<JComponent>>();
		vallist=new ArrayList<ArrayList<Double>>();
		titlelist=new ArrayList<JComponent>();
		posydeb=new ArrayList<Integer>();
		posxdeb=new ArrayList<Integer>();

		 suivcoly=0;
		 suivcolx=0;
		 currentsort=0;
	}
	
	public void newColTitle(JLabel labo, int posx, int posy, GridBagConstraints gbc,JPanel jpa)
	{
		JButton lab=new JButton(labo.getText());
		lab.setToolTipText(labo.getToolTipText());
		titlelist.add(lab);
		posxdeb.add(posx);
		posydeb.add(posy);
		placenewcomp(posx,posy,gbc,lab,jpa);
		gb=gbc;
		jp=jpa;
		suivcolx=titlelist.size()-1;
		suivcoly=0;
		lab.addActionListener(this);
	}
	
	public void newLine(JComponent lab, Double val, GridBagConstraints gbc,JPanel jpa)
	{
		ArrayList<JComponent> nl=new ArrayList<JComponent>();
		ArrayList<Double> nv=new ArrayList<Double>();
		lablist.add(nl);
		vallist.add(nv);
		nl.add(lab);
		nv.add(val);
		tosortmap.put(nl.size()-1, val);
		placenewcomp(posxdeb.get(0),posydeb.get(0)+lablist.size(),gbc,lab,jpa);
		gb=gbc;
		jp=jpa;
	}
	
	public void newOnSameLine(JComponent lab, Double val, GridBagConstraints gbc,JPanel jpa)
	{
		ArrayList<JComponent> nl=lablist.get(lablist.size()-1);
		ArrayList<Double> nv=vallist.get(vallist.size()-1);
		nl.add(lab);
		nv.add(val);
		placenewcomp(posxdeb.get(0)+nl.size(),posydeb.get(0)+lablist.size(),gbc,lab,jpa);
		gb=gbc;
		jp=jpa;
	}
	
	public void newCol(JComponent lab, Double val, GridBagConstraints gbc,JPanel jpa)
	{
		if (lablist.size()==0)
		{
		ArrayList<JComponent> nl=new ArrayList<JComponent>();
		ArrayList<Double> nv=new ArrayList<Double>();
		lablist.add(nl);
		vallist.add(nv);
		nl.add(lab);
		nv.add(val);
		tosortmap.put(nl.size()-1, val);
		placenewcomp(posxdeb.get(0),posydeb.get(0)+lablist.size(),gbc,lab,jpa);
		}
		else
		{
			ArrayList<JComponent> nl=lablist.get(0);
			ArrayList<Double> nv=vallist.get(0);
			nl.add(lab);
			nv.add(val);
			placenewcomp(posxdeb.get(nl.size()),posydeb.get(0)+1,gbc,lab,jpa);			
		}
		suivcolx=lablist.size()-1;
		suivcoly=1;
		gb=gbc;
		jp=jpa;
	}
	
	public void newOnSameCol(JComponent lab, Double val, GridBagConstraints gbc,JPanel jpa)
	{
		if (lablist.size()>0)
		{
		if (suivcolx==0)
		{
		ArrayList<JComponent> nl=new ArrayList<JComponent>();
		ArrayList<Double> nv=new ArrayList<Double>();
		lablist.add(nl);
		vallist.add(nv);
		nl.add(lab);
		nv.add(val);
		tosortmap.put(nl.size()-1, val);
		placenewcomp(posxdeb.get(0),posydeb.get(0)+lablist.size(),gbc,lab,jpa);
		}
		else
		{
			ArrayList<JComponent> nl=lablist.get(suivcoly);
			ArrayList<Double> nv=vallist.get(suivcoly);
			nl.add(lab);
			nv.add(val);
			try {
				placenewcomp(posxdeb.get(nl.size()-1),posydeb.get(nl.size()-1)+suivcoly+1,gbc,lab,jpa);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		suivcoly++;
		}
		if (lablist.size()==0)
		{
		ArrayList<JComponent> nl=new ArrayList<JComponent>();
		ArrayList<Double> nv=new ArrayList<Double>();
		lablist.add(nl);
		vallist.add(nv);
		nl.add(lab);
		nv.add(val);
		tosortmap.put(nl.size()-1, val);
		placenewcomp(posxdeb.get(0),posydeb.get(0)+lablist.size(),gbc,lab,jpa);
		suivcoly=1;
		}
		gb=gbc;
		jp=jpa;
	}
	
	public void placenewcomp(int posx,int posy, GridBagConstraints gbc,JComponent comp,JPanel jpa)
	{
		gbc.gridx=posx;
		gbc.gridy=posy;
//		gbc.gridwidth=1;
//		gbc.gridheight=1;
		gbc.weightx=10;
		gbc.weighty=10;
		gbc.anchor=GridBagConstraints.WEST;
//		gb.setConstraints(l1,gbc);
//		getContentPane().add(l1);
		jpa.add(comp,gbc);
	}
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator =  new Comparator<K>() {
	        public int compare(K k1, K k2) {
	            int compare = map.get(k2).compareTo(map.get(k1));
	            if (compare == 0) return 1;
	            else return compare;
	        }
	    };
	    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
	
	public LinkedHashMap getSortedMap(HashMap hmap)
	{
	LinkedHashMap map = new LinkedHashMap();
	List mapKeys = new ArrayList(hmap.keySet());
	List mapValues = new ArrayList(hmap.values());
	hmap.clear();
	TreeSet sortedSet = new TreeSet(mapValues);
	Object[] sortedArray = sortedSet.toArray();
	int size = sortedArray.length;
	// a) Ascending sort

	for (int i=0; i<size; i++)
	{

	map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), sortedArray[i]);
	mapValues.set(mapValues.indexOf(sortedArray[i]), null);

	}
	return map;
	}
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object src=arg0.getSource();
		if (this.titlelist.size()>0)
		for (int i=0; i<this.titlelist.size();i++)
		if (src==titlelist.get(i))
		{
			if (i==currentsort)
			{
				nbcurrentsort++;
			}
			else
			{
				currentsort=i;
				nbcurrentsort=0;
			}
			this.tosortmap=new HashMap<Integer, Double>();
			for (int j=0; j<lablist.size(); j++)
			{
				for (int k=0; k<this.titlelist.size();k++)
				{
					jp.remove(lablist.get(j).get(i));
				}

				tosortmap.put(j, vallist.get(j).get(i));				
				if (nbcurrentsort==1)
				{
					tosortmap.put(j, Math.abs(vallist.get(j).get(i)));				
					if ((Math.round(vallist.get(j).get(i)*100000)==0)|(Math.round(vallist.get(j).get(i)*100000)==100000))
					{
						tosortmap.put(j, 0.0);				
						
					}
				}
			}
			sortedmap=sortByValues(tosortmap);				
			
			Iterator<Integer> it = sortedmap.keySet().iterator();
			int ny=1;
			while(it.hasNext())
			{
				int j=it.next();
				for (int k=0; k<this.titlelist.size();k++)
				{
					this.placenewcomp(this.posxdeb.get(k), this.posydeb.get(k)+ny, gb, lablist.get(j).get(k), jp);
				}
				ny++;				
			}
		}
		jp.repaint();
		this.revalidate();
		this.repaint();
			
	}


}
