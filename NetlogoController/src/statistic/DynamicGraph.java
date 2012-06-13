package statistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * Graphe generique ou chaque arc a un poids de base, une date de creation et de suppression
 * 
 * @author Destannes Alexandre
 *
 */
public class DynamicGraph{

	/* 
	 * indique si des donnees comme les matrices d'adjacence ou le tableau des plus court chemin doivent etre concervés pour eviter les calculs redondant 
	 * /!\ peut couter enorement de memoire si laisse a vrai
	 */
	public static final boolean SAVE_LONG_TIME_COMPLEXITY_DATA = true;
	
	public static final String POIDS = "poids";
	public static final String TIME_CREATION = "date_creation";
	public static final String TIME_SUPPRESSION = "date_suppression";
	public static final String GROUP = "group";
	public static final String LABEL = "ui.label";
	
	public static final String CSS = "edge{shape:line;size:1px,1px;fill-mode:plain;fill-color:#7FC6BC;arrow-size:5px,5px;text-size:15px;text-style:bold;text-color:#4A1A2C;}"+
                                "node.a{size: 12px, 12px;fill-mode:dyn-plain;text-size:15px;text-style:bold;text-color:black;fill-color:red;}"+
								"node.b{size: 12px, 12px;fill-mode:dyn-plain;text-size:15px;text-style:bold;text-color:black;fill-color:blue;}"+
								"node.c{size: 12px, 12px;fill-mode:dyn-plain;text-size:15px;text-style:bold;text-color:black;fill-color:green;}"+
								"node.d{size: 12px, 12px;fill-mode:dyn-plain;text-size:15px;text-style:bold;text-color:black;fill-color:purple;}"+
								"node.e{size: 12px, 12px;fill-mode:dyn-plain;text-size:15px;text-style:bold;text-color:black;fill-color:pink;}"+
								"node.f{size: 12px, 12px;fill-mode:dyn-plain;text-size:15px;text-style:bold;text-color:black;fill-color:black;}";
	public static final char LEFT_BRACE =  '[';
	public static final char RIGHT_BRACE = ']';
	public static final char SEPARATOR =   ':';
	
	private HashMap<Long, int[][]> adjacencyMatrixs;
	private HashMap<Long, int[][]> weightsMatrixs;
	private HashMap<Long, int[][]> shortestPathsWeightMatrixs;
	private HashMap<Long, int[][]> shortestPathsMatrixs;
	
	Integer autoGenerateEdgedId;
	
	public boolean directed = true;
	
	protected MultiGraph graph;
	
	/**
	 * Indique si la valeur se trouve dans l'intervalle donne
	 * @param value
	 * @param minInterval
	 * @param maxInterval
	 * @return vrai si value est dans l'intervalle
	 */
	private boolean isInInterval(long value, long minInterval, long maxInterval)
	{
		return value >= minInterval && value <= maxInterval;
	}
	
	/**
	 * Retourne le tableau sous forme de chaine en utilisant le separateur et les encadrant definit dans LEFT_BRACE, RIGHT_BRACE, SEPARATOR
	 * @param tab : la chaine sous forme de tableau {cell1,cell2,cell3,...}
	 * @return la chaine au format "[cell1,cell2,cell3,...]"
	 */
	public static String tabToString(Collection<String> tab)
	{
		String result = LEFT_BRACE + "";
		for(String s : tab)
		{
			if (result.length() > 1)
			{
				result += SEPARATOR;
			}
			result += s;
		}
		result += RIGHT_BRACE;
		
		return result;
	}
	
	/**
	 * Retourne la chaine sous forme de tableau en utilisant le separateur et les encadrant definit dans LEFT_BRACE, RIGHT_BRACE, SEPARATOR
	 * @param tab : la chaine au format "[cell1,cell2,cell3,...]"
	 * @return la chaine sous forme de tableau {cell1,cell2,cell3,...}
	 */
	public static ArrayList<String> stringToTab(String tab)
	{
		Integer level = 0;
		String buffer = "";
		ArrayList<String> result = new ArrayList<String>();
		tab = tab.substring(1, tab.length()-1);
		
		/* on parcourt la chaine pour recuperer toutes les cellules */
		for (char c : tab.toCharArray())
		{
			if (c == LEFT_BRACE)
			{
				level++;
			}
			else if (c == RIGHT_BRACE)
			{
				level--;
			}
			
			if (c == SEPARATOR && level == 0)
			{
				result.add(buffer);
				buffer = "";
			}
			else
			{
				buffer+= c;
			}
		}
		
		if (buffer != "")
		{
			result.add(buffer);
		}
		
		return result;
	}

	/**
	 * Initialise le graphe
	 * @param name : le nom du graphe
	 */
	private void init(String name)
	{
		graph = new MultiGraph(name);
		adjacencyMatrixs = new HashMap<Long, int[][]>();
		shortestPathsWeightMatrixs = new HashMap<Long, int[][]>();
		weightsMatrixs = new HashMap<Long, int[][]>();
		shortestPathsMatrixs = new HashMap<Long, int[][]>();
		autoGenerateEdgedId = 1;
	}
	
	/**
	 * Cree un graphGeneric avec un nom
	 * @param name : le nom du graphe
	 */
	public DynamicGraph(String name) {
		init(name);
	}
	
	/**
	 * Cree un graphGeneric
	 */
	public DynamicGraph() {
		init("no name");
	}

	
	/************************************************************/
	/********************* GETTER ET SETTERS ********************/
	/************************************************************/
	
	/**
	 * Retourne le poids de l'arc demande
	 * @param edge
	 * @return poids de l'arc
	 */
	public int getWeight(Edge edge)
	{
		return (Integer)edge.getAttribute(POIDS);
	}
	
	/**
	 * Met a jour le poids de l'arc donne
	 * @param edge
	 * @param weight
	 */
	public void setWeight(Edge edge, int weight)
	{
		edge.setAttribute(POIDS, weight);
	}
	
	/**
	 * Retourne le groupe de la node demandee
	 * @param node
	 * @return groupe de la node
	 */
	public int getGroup(Node node)
	{
		return node.getAttribute(GROUP);
	}
	
	/**
	 * Met a jour le groupe de la node donnee
	 * Le groupe a pour unique utilitee de differencier les nodes lors de l'affichage
	 * @param node
	 * @param group
	 */
	public void setGroup(Node node, int group)
	{
		node.setAttribute(GROUP, group);
	}
	
	/**
	 * Retourne le temps auquel l'arc a ete cree
	 * @param edge
	 * @return temps auquel l'arc a ete cree
	 */
	public long getTimeCreation(Edge edge)
	{
		return (Long)edge.getAttribute(TIME_CREATION);		
	}

	/**
	 * Retourne le temps auquel l'arc a ete detruit
	 * @param edge
	 * @return temps auquel l'arc a ete detruit
	 */
	public long getTimeDelete(Edge edge)
	{
		return (Long)edge.getAttribute(TIME_SUPPRESSION);		
	}
	
	/**
	 * Met a jout le temps de suppression d'un arc
	 * @param edge
	 * @param timeDelete
	 */
	public void setTimeDelete(Edge edge, long timeDelete)
	{
		long actualTimeDelete = getTimeDelete(edge);
		
		if (actualTimeDelete < getTimeCreation(edge))
		{
			System.err.println("Error : the new delete time is lower than the creation time");
			return;
		}
		
		if (actualTimeDelete >= timeDelete)
		{
			edge.setAttribute(TIME_SUPPRESSION, timeDelete);
		}
		else
		{
			if(!edgesExists(edge.getNode0(), edge.getNode1(),timeDelete,actualTimeDelete))
			{
				edge.setAttribute(TIME_SUPPRESSION, timeDelete);
			}
			else
			{
				System.err.println("Error : cannot increase delete time for this edge, another edge already exists between the actual end and the new end");
			}
		}
	}

	/**
	 * Met a jout le temps de creation d'un arc
	 * @param edge
	 * @param timeCreation
	 */
	public void setTimeCreation(Edge edge, long timeCreation)
	{
		long actualTimeCreation = getTimeCreation(edge);

		
		if (actualTimeCreation > getTimeDelete(edge))
		{
			System.err.println("Error : the new creation time is upper than the delete time");
			return;
		}
		
		if (actualTimeCreation <= timeCreation)
		{
			edge.setAttribute(TIME_CREATION, timeCreation);
		}
		else
		{
			if(!edgesExists(edge.getNode0(), edge.getNode1(),timeCreation,actualTimeCreation))
			{
				edge.setAttribute(TIME_CREATION, timeCreation);
			}
			else
			{
				System.err.println("Error : cannot decrease creation time for this edge, another edge already exists between the actual creation and the new creation");
			}
		}
	}
	
	/**
	 * Retourne l'arc entre les deux nodes donnee, dans le meme sens au temps donne
	 * @param node1 : node source
	 * @param node2 : node cible
	 * @param time
	 * @return l'arc entre node0 et node1 ou null si l'arc n'existe pas
	 */
	public Edge getEdge(String node1, String node2, long time)
	{
		Node n = graph.getNode(node1);
		for (Edge e : n.getEachLeavingEdge())
		{
			if (e.getOpposite(n).getId().equals(node2) && getTimeCreation(e) <= time && getTimeDelete(e) >= time)
			{
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Retourne la node definie par l'id donne
	 * @param id
	 * @return la node demandee
	 */
	public Node getNode(String id)
	{
		return graph.getNode(id);
	}
	
	/**
	 * Retourne la node definie par l'index donne
	 * @param index
	 * @return la node demandee
	 */
	public Node getNode(int index)
	{
		return graph.getNode(index);
	}
	
	/**
	 * Retourne la liste des Nodes du graphe
	 * @return la liste des Nodes du graphe
	 */
	public Iterable<? extends Node> getEachNode()
	{
		return graph.getEachNode();
	}
	
	/**
	 * Retourne le nombre de nodes dans le graphe
	 * @return le nombre de nodes dans le graphe
	 */
	public int getNodeCount()
	{
		return graph.getNodeCount();
	}

	/**
	 * Retourne le nombre d'arcs dans le graphe
	 * @return le nombre d'arcs dans le graphe
	 */
	public int getEdgeCount()
	{
		return graph.getEdgeCount();
	}

	/**
	 * Retourne le nombre d'arcs vivant dans le graphe au temps donne
	 * @param time
	 * @return le nombre d'arcs dans le graphe au temps donne
	 */
	public int getEdgeCount(long time)
	{
		ArrayList<Edge> edges = new ArrayList<Edge>();
		
		for (Edge e : getEachEdge())
		{
			if (isInInterval(time,getTimeCreation(e),getTimeDelete(e)))
			{
				edges.add(e);
			}
		}
		return edges.size();
	}
	
	/**
	 * Retourne la liste des Arcs du graphe
	 * @return la liste des Arcs du graphe
	 */
	public Iterable<? extends Edge> getEachEdge()
	{
		return graph.getEachEdge();
	}
	
	/**
	 * Retourne la liste des Arcs du graphe existant au temps donne
	 * @param time
	 * @return la liste des Arcs du graphe existant au temps donne
	 */
	public Iterable<? extends Edge> getEachEdge(long time)
	{
		ArrayList<Edge> edges = new ArrayList<Edge>();
		
		for (Edge e : getEachEdge())
		{
			if (isInInterval(time,getTimeCreation(e),getTimeDelete(e)))
			{
				edges.add(e);
			}
		}
		return edges;
	}
	
	/**
	 * Retourne la liste des arcs sortant de la node au temps donne
	 * @param node
	 * @param time
	 * @return la liste des arcs sortant au temps donne
	 */
	public Collection<Edge> getLeavingEdges(Node node, long time)
	{
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (Edge e : node.getEachLeavingEdge())
		{
			if (edgeExists(e, time))
			{
				edges.add(e);
			}
		}
		return edges;
	}
	
	/**
	 * Retourne le degree sortant d'un arc au temps donne
	 * @param n
	 * @param time
	 * @return
	 */
	public int getOutDegree(Node node, long time)
	{
		int sum = 0;
		for (int i : getAdjacencyMatrixAt(time)[node.getIndex()])
		{
			sum += i;
		}
		return sum;
	}
	
	/**
	 * Retourne le degre entrant d'un arc au temps donne (temps de calcul plus long que degre sortant)
	 * @param n
	 * @param time
	 * @return le degre entrant d'un arc
	 */
	public int getInDegree(Node node, long time)
	{
		int sum = 0;
		for (int i = getNodeCount()-1 ; i >= 0 ; i-- )
		{
			sum += getAdjacencyMatrixAt(time)[i][node.getIndex()];
		}
		return sum;
	}
	
	/**
	 * Indique si un ou plusieurs arcs existent entre deux nodes dans le sens donne
	 * @param node1 : node source des arcs
	 * @param node2 : node cible des arcs
	 * @return vrai si un arc ou plus existent entre node1 et node2
	 */
	public boolean edgesExists(Node node1, Node node2)
	{
		for (Edge e : node1.getEachLeavingEdge())
		{
			if (e.getOpposite(node1).equals(node2))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Indique si un ou plusieurs arcs existent entre deux nodes dans le sens donne dans l'intervalle donne
	 * @param node1 : node source des arcs
	 * @param node2 : node cible des arcs
	 * @param timeStart : debut de l'intervalle
	 * @param timeEnd : fin de l'intervalle
	 * @return vrai si un arc ou plus existent entre node1 et node2 dans cet intervalle
	 */
	public boolean edgesExists(Node node1, Node node2, long timeStart, long timeEnd)
	{
		for (Edge e : node1.getEachLeavingEdge())
		{
			if (e.getOpposite(node1).equals(node2) 
			&& (isInInterval(timeStart,getTimeCreation(e),getTimeDelete(e))
			||  isInInterval(timeEnd  ,getTimeCreation(e),getTimeDelete(e)))
			)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Indique si un arc existe au temps donne
	 * @param edge
	 * @param time
	 * @return vrai si un arc existe entre node1 et node2 au temps donne
	 */
	public boolean edgeExists(Edge edge, long time)
	{
		return isInInterval(time,getTimeCreation(edge),getTimeDelete(edge));
	}
	
	/**
	 * Transforme l'arc en String
	 * @param e
	 * @return
	 */
	public String edgeToString(Edge e)
	{
		String result = LEFT_BRACE + e.getNode0().getId() + SEPARATOR + e.getNode1().getId();
		
		if (getTimeDelete(e) != Long.MAX_VALUE)
		{
			result += SEPARATOR + String.valueOf(getWeight(e)) + SEPARATOR + String.valueOf(getTimeCreation(e)) + SEPARATOR + String.valueOf(getTimeDelete(e));
		}
		else if (getTimeCreation(e) != 0)
		{
			result += SEPARATOR + String.valueOf(getWeight(e)) + SEPARATOR + String.valueOf(getTimeCreation(e));
		}
		else if (getWeight(e) != 0)
		{
			result += SEPARATOR + String.valueOf(getWeight(e));
		}
		
		result += RIGHT_BRACE;
				
		return result;
	}
	
	/**
	 * Transforme la node en String
	 * @param n
	 * @return
	 */
	public String nodeToString(Node n)
	{
		return LEFT_BRACE + n.getId() + RIGHT_BRACE;
	}
	
	/**
	 * Transforme le graphe en chaine en utilisant le separateur et les encadrant definit dans LEFT_BRACE, RIGHT_BRACE, SEPARATOR
	 * @return la chaine creee
	 */
	public String toString()
	{
		String result = "" + LEFT_BRACE;
		
		for (Node n : graph.getEachNode())
		{
			if (result.length() != 1)
			{
				result += SEPARATOR;
			}
			
			result += nodeToString(n);
		}
		
		for (Edge e : graph.getEachEdge())
		{
			if (result.length() != 1)
			{
				result += SEPARATOR;
			}
			
			result += edgeToString(e);
		}
		result += RIGHT_BRACE;
		
		return result;
	}

	/**
	 * Transforme le graphe en tableau de String, chaque String contenant la declaration d'une node ou d'un arc
	 * @return le tableau creee
	 */
	public Collection<String> toStringArray()
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for (Node n : graph.getEachNode())
		{
			result.add(nodeToString(n));
		}
		
		for (Edge e : graph.getEachEdge())
		{
			result.add(edgeToString(e));
		}
		
		return result;
	}
	
	/************************************************************/
	/************************ MODIFIEURS ************************/
	/************************************************************/
	
	/**
	 * Ajoute un arc avec une source, une cible, un poids, une date de creation et de suppression
	 * @param source : noeud source
	 * @param target : noeud cible
	 * @param timeAdd : temps auquel l'arc a ete cree
	 * @param timeDel : temps auquel l'arc a ete detruit
	 * @param poids : poids de l'arc
	 * @return l'arc cree
	 */
	public Edge addEdge(String source, String target, int poids, long timeAdd, long timeDel)
	{
		/* on cree le noeuds arrivee et source s'il n'existent pas */
		Node nsource = graph.getNode(source),
			 ntarget = graph.getNode(target);
		if (nsource == null)
		{
			nsource = addNode(source);
		}
		if (ntarget == null)
		{
			ntarget = addNode(target);
		} 
		
		/* on verifie qu'un arc n'existe pas deja */
		if (edgesExists(nsource, ntarget,timeAdd,timeDel))
		{ 
			System.err.println("Error : " + source + "->"+ target + " on the interval " +timeAdd + "-" + timeDel + " already contains edges");
			return null;
		}
		
		/* on teste si l'intervalle est coherent */
		if (timeAdd > timeDel)
		{
			System.err.println("Error : " + timeAdd + "-" + timeDel + " is not a valid interval");
			return null;
		}
		
		/* on ajoute l'arc */
		while (graph.getEdge("Arrow" + String.valueOf(autoGenerateEdgedId)) != null)
		{
			autoGenerateEdgedId++;
		}
		Edge e = graph.addEdge("Arrow" + String.valueOf(autoGenerateEdgedId), source, target,directed);
		e.addAttribute(POIDS, poids);
		e.addAttribute(TIME_CREATION, timeAdd);
		e.addAttribute(TIME_SUPPRESSION, timeDel);
		autoGenerateEdgedId++;
		return e;
	}

	/**
	 * Ajoute un arc tout comme addEdge, mais decale les arcs pouvant le gener en decalant leur temps d'ajout / suppression
	 * @param source : noeud source
	 * @param target : noeud cible
	 * @param timeAdd : temps auquel l'arc a ete cree
	 * @param timeDel : temps auquel l'arc a ete detruit
	 * @param poids : poids de l'arc
	 * @return l'arc cree
	 */
	public Edge insertEdge(String source, String target, int poids, long timeAdd, long timeDel)
	{
		Node nsource = getNode(source), ntarget = getNode(target);
		if (nsource == null)
		{
			nsource = addNode(source);
		}
		if (ntarget == null)
		{
			ntarget = addNode(target);
		}
		
		/* on supprime ou decale les autres arcs */
		if (edgesExists(nsource,ntarget,timeAdd,timeAdd))
		{
			boolean restartCheck;
			do
			{
				restartCheck = false; /* un restart est necessaire en cas de suppression d'arc */
				for (Edge e : nsource.getEachLeavingEdge())
				{
					
					if (e.getOpposite(nsource).equals(ntarget) && getTimeDelete(e) >= timeAdd && getTimeCreation(e) <= timeDel)
					{
						if (getTimeDelete(e) <= timeDel)
						{
							if (getTimeCreation(e) >= timeAdd)
							{
								removeEdge(e);
								restartCheck = true;
								break;
							}
							else
							{
								setTimeCreation(e,Math.min(timeAdd-1,getTimeCreation(e)));
								setTimeDelete(e,timeAdd-1);
							}
						}
						else
						{
							if (getTimeCreation(e) >= timeAdd)
							{
								setTimeDelete(e,Math.max(timeDel-1,getTimeDelete(e)));
								setTimeCreation(e,timeDel+1);
							}
							else
							{
								setTimeCreation(e,Math.min(timeAdd-1,getTimeCreation(e)));
								setTimeDelete(e,timeAdd-1);
							}
						}
					}
				}
			}
			while (restartCheck);
		}
		
		return addEdge(source, target, poids, timeAdd, timeDel);
	}
	
	/**
	 * Supprime l'arc demande /!\ attention : ne met pas a jour le temps de suppression mais supprime l'arc du graphe
	 * @param id
	 */
	public void removeEdge(Edge e)
	{
		graph.removeEdge(e);
	}
	
	/**
	 * Ajoute une node au graphe, si la node existe deja, elle n'est pas ajoutee
	 * @param id
	 * @return la node cree ou celle deja existante
	 */
	public Node addNode(String id)
	{
		Node n = graph.getNode(id);
		if (n == null)
		{
			n = graph.addNode(id);
			n.addAttribute(GROUP, 0);
		}
		return n;
	}
	
	/**
	 * Charge les donnees graphes depuis la chaine donne
	 * @param source : chaine contenant les donnes graphes dans le format LEFT_BRACE attr1 SEPARATOR attr2 SEPARATOR ... RIGHT_BRACE
	 * @param weightDefault : poids par defaut si non specifie
	 * @param timeStartDefault : temps de creation par defaut si non specifie
	 * @param timeEndDefault : temps de suppression par defaut si non specifie (ou egal au temps de creation si la valeur lui est inférieure)
	 * @param insert : vrai si les donnes doivent etre inserees, faux si elles doivent etre ajoutees (voir fonction addEdge et insertEdge)
	 */
	public void loadFromString(String source, int weightDefault, long timeStartDefault, long timeEndDefault, boolean insert)
	{
		ArrayList<String> chaines = stringToTab(source);
		
		for(String s : chaines)
		{
			if (s.length() == 0)
			{
				continue;
			}

			ArrayList<String> graphContent = stringToTab(s);
			switch(graphContent.size())
			{
				case 1 : /* cas ou il n'y a que la definition d'une node */
					addNode(graphContent.get(0));
					break;
				case 2 :
					if (insert)
					{
						insertEdge(graphContent.get(0),graphContent.get(1),weightDefault,timeStartDefault,timeEndDefault);
					}
					else
					{
						addEdge(graphContent.get(0),graphContent.get(1),weightDefault,timeStartDefault,timeEndDefault);
					}
					break;
				case 3 :
					if (insert)
					{
						insertEdge(graphContent.get(0),graphContent.get(1),Integer.valueOf(graphContent.get(2)),timeStartDefault,timeEndDefault);
					}
					else
					{
						addEdge(graphContent.get(0),graphContent.get(1),Integer.valueOf(graphContent.get(2)),timeStartDefault,timeEndDefault);
					}
					break;
				case 4 :
					if (Long.valueOf(graphContent.get(3)) > timeEndDefault)
					{
						timeEndDefault = Long.valueOf(graphContent.get(3));
					}
					if (insert)
					{
						insertEdge(graphContent.get(0),graphContent.get(1),Integer.valueOf(graphContent.get(2)),Long.valueOf(graphContent.get(3)),timeEndDefault);
					}
					else
					{
						addEdge(graphContent.get(0),graphContent.get(1),Integer.valueOf(graphContent.get(2)),Long.valueOf(graphContent.get(3)),timeEndDefault);
					}
					break;
				case 5 :
					if (insert)
					{
						insertEdge(graphContent.get(0),graphContent.get(1),Integer.valueOf(graphContent.get(2)),Long.valueOf(graphContent.get(3)),Long.valueOf(graphContent.get(4)));
					}
					else
					{
						addEdge(graphContent.get(0),graphContent.get(1),Integer.valueOf(graphContent.get(2)),Long.valueOf(graphContent.get(3)),Long.valueOf(graphContent.get(4)));
					}
					break;
				default :
					break;
			}
		}
	}
	
	
	/************************************************************/
	/******************** FONCTIONS DE GRAPHE *******************/
	/************************************************************/
	
	/**
	 * Affiche le graphe au temps donne
	 * @param time
	 * @return un Viewer poru le graphe
	 */
	public Viewer displayGraph(long time)
	{
		
		MultiGraph result = new MultiGraph(graph.getId());
		
		result.addAttribute("ui.stylesheet",CSS);
		
		/* ajout des nodes */
		for (Node n : graph.getEachNode())
		{
			Node n2 = result.addNode(n.getId());
			n2.setAttribute(LABEL, n.getId());
			n2.setAttribute("ui.class", String.valueOf((char)('a' + getGroup(n))));
		}
		
		/* ajout des arcs */
		for (Edge e : graph.getEachEdge())
		{
			if (isInInterval(time, getTimeCreation(e), getTimeDelete(e)))
			{
				result.addEdge(String.valueOf(result.getEdgeCount()), e.getNode0().getId(), e.getNode1().getId(),true).setAttribute(LABEL, e.getAttribute(POIDS));
			}
		}
		
		/* on ajoute des couleurs dynamique aux noeuds en fonction du ratio arcs sortant / arcs */
		double ratio = 0;
		for (Node n : result.getEachNode())
		{
			ratio = n.getEdgeSet().size();
			if (ratio > 0)
			{
				ratio = n.getLeavingEdgeSet().size() / ratio;
			}
			n.setAttribute("ui.color",ratio);
		}
		
		return result.display();
	}

	/**
	 * Retourne la matrice d'adjacence au temps donne
	 * @param time
	 * @return la matrice d'adjacence
	 */
	public int[][] getAdjacencyMatrixAt(long time)
	{	
		if (adjacencyMatrixs.containsKey(time) && adjacencyMatrixs.get(time).length == getNodeCount())
		{
			return adjacencyMatrixs.get(time);
		}
		
		int[][] adjacencyMatrix = new int[graph.getNodeCount()][graph.getNodeCount()];
		
		/* on initialise tout a 0 */
		for (int i = adjacencyMatrix.length-1 ; i >= 0  ; i--)
		{
			for (int j = adjacencyMatrix[i].length-1 ; j >= 0  ; j--)
			{
				adjacencyMatrix[i][j] = 0;
			}
		}
		
		/* on ajoute les connexions */
		Iterator<? extends Edge> edges = getEachEdge(time).iterator();
		Edge edge;
		while (edges.hasNext())
		{
			edge = edges.next();
			
			adjacencyMatrix[edge.getNode0().getIndex()][edge.getNode1().getIndex()] = 1;
		}
		
		if (SAVE_LONG_TIME_COMPLEXITY_DATA)
		{
			adjacencyMatrixs.put(time, adjacencyMatrix);
		}
		
		return adjacencyMatrix;
	}
	
	/**
	 * Retourne la matrice des poids du graphe
	 * @param time
	 * @return
	 */
	public int[][] getWeightMatrix(long time)
	{
		if (weightsMatrixs.containsKey(time) && weightsMatrixs.get(time).length == getNodeCount())
		{
			return weightsMatrixs.get(time);
		}
		
		int[][] weightMatrix = new int[graph.getNodeCount()][graph.getNodeCount()];
		
		/* on initialise tout a 0 */
		for (int i = weightMatrix.length-1 ; i >= 0  ; i--)
		{
			for (int j = weightMatrix[i].length-1 ; j >= 0  ; j--)
			{
				weightMatrix[i][j] = Integer.MAX_VALUE;
			}
		}
		
		/* on ajoute les connexions */
		Iterator<? extends Edge> edges = getEachEdge(time).iterator();
		Edge edge;
		while (edges.hasNext())
		{
			edge = edges.next();
			
			weightMatrix[edge.getNode0().getIndex()][edge.getNode1().getIndex()] = getWeight(edge);
		}
		
		if (SAVE_LONG_TIME_COMPLEXITY_DATA)
		{
			weightsMatrixs.put(time, weightMatrix);
		}
		
		return weightMatrix;
	}

	/**
	 * Retourne la matrice des plus courts chemin (chaque case 
	 * @param time
	 * @return
	 */
	public int[][] getShortestPathsMatrixs(long time)
	{
		if (shortestPathsWeightMatrixs.containsKey(time)&& shortestPathsWeightMatrixs.get(time).length == getNodeCount())
		{
			return shortestPathsWeightMatrixs.get(time);
		}
		
		
		int[][] shortestPathsWeightMatrix = getWeightMatrix(time);
		int[][] next = new int[graph.getNodeCount()][graph.getNodeCount()];
		int newPathWeight;
		
		for (int k = getNodeCount() -1 ; k >= 0 ; k--)
		{
			for (int i = getNodeCount() -1 ; i >= 0 ; i--)
			{
				for (int j = getNodeCount() -1 ; j >= 0 ; j--)
				{
					if( shortestPathsWeightMatrix[i][k] != Integer.MAX_VALUE && shortestPathsWeightMatrix[k][j] != Integer.MAX_VALUE)
					{
						newPathWeight = shortestPathsWeightMatrix[i][k]+shortestPathsWeightMatrix[k][j];
						if (newPathWeight < shortestPathsWeightMatrix[i][j])
						{
							shortestPathsWeightMatrix[i][j] = newPathWeight;
							next[i][j] = k;
						}
					}
				}
			}
		}
		
		if (SAVE_LONG_TIME_COMPLEXITY_DATA)
		{
			shortestPathsWeightMatrixs.put(time, shortestPathsWeightMatrix);
			shortestPathsMatrixs.put(time, next);
		}
		
		return next;
	}
	
	/**
	 * Retourne la matrice des poids des plus courts chemins entre chaque node au temps donne
	 * /!\ les poids de la matrice doivent etre > 0 (ce qui n'est pas verifie)
	 * @param time
	 * @return la matrice des poids desplus courts chemins
	 */
	public int[][] getShortestPathWeightMatrix(long time)
	{
		if (shortestPathsWeightMatrixs.containsKey(time) && shortestPathsWeightMatrixs.get(time).length == getNodeCount())
		{
			return shortestPathsWeightMatrixs.get(time);
		}
		
		int[][] shortestPathsWeightMatrix = getWeightMatrix(time);
		int[][] next = new int[graph.getNodeCount()][graph.getNodeCount()];
		int newPathWeight;
		
		for (int k = getNodeCount() -1 ; k >= 0 ; k--)
		{
			for (int i = getNodeCount() -1 ; i >= 0 ; i--)
			{
				for (int j = getNodeCount() -1 ; j >= 0 ; j--)
				{
					if( shortestPathsWeightMatrix[i][k] != Integer.MAX_VALUE && shortestPathsWeightMatrix[k][j] != Integer.MAX_VALUE)
					{
						newPathWeight = shortestPathsWeightMatrix[i][k]+shortestPathsWeightMatrix[k][j];
						if (newPathWeight < shortestPathsWeightMatrix[i][j])
						{
							shortestPathsWeightMatrix[i][j] = newPathWeight;
							next[i][j] = k;
						}
					}
				}
			}
		}
		
		if (SAVE_LONG_TIME_COMPLEXITY_DATA)
		{
			shortestPathsWeightMatrixs.put(time, shortestPathsWeightMatrix);
			shortestPathsMatrixs.put(time, next);
		}
		
		return shortestPathsWeightMatrix;
	}
	
	/**
	 * Retourne la correlation temporelle pour chaque node dans l'intervalle donnee (l'intervalle doit avoir une longueur d'au moins 1)
	 * @param timeBegin
	 * @param timeEnd
	 * @return double[] : tableau mettant en relation l'index de la note et sa correlation
	 */
	public double[] getDynamicCorrelation(long timeBegin, long timeEnd)
	{
		double[] result = new double[graph.getNodeCount()];
		int[][] adjacencyMatrix_T,adjacencyMatrix_Tp1;
		float sumT_Tp1,sumT,sumTp1;
		
		if (timeBegin == timeEnd)
		{
			for (int i = 0 ; i < graph.getNodeCount() ; i++)
			{
				result[i] = 1;
			}
			return result;
		}
		
		for (int i = 0 ; i < graph.getNodeCount() ; i++)
		{
			result[i] = 0;
		}
		
		if (timeBegin >= timeEnd)
		{
			timeBegin += timeEnd;
			timeEnd    = timeBegin-timeEnd;
			timeBegin -= timeEnd;
		}
		
		adjacencyMatrix_Tp1 = getAdjacencyMatrixAt(timeBegin);
		
		for (long k = timeBegin ; k < timeEnd ; k++)
		{
			adjacencyMatrix_T = adjacencyMatrix_Tp1;
			adjacencyMatrix_Tp1 = getAdjacencyMatrixAt(k+1);
			
			for (int i = 0 ; i < graph.getNodeCount() ; i++)
			{			
				sumT_Tp1 = 0;
				sumT = 0;
				sumTp1 = 0;
				
				for (int j = 0 ; j < graph.getNodeCount() ; j++)
				{
					sumT_Tp1 += adjacencyMatrix_T[i][j]*adjacencyMatrix_Tp1[i][j];
					sumT 	 += adjacencyMatrix_T[i][j];
					sumTp1 	 += adjacencyMatrix_Tp1[i][j];
				}
				
				if (sumT != 0 && sumTp1 != 0)
				{
					result[i] += sumT_Tp1 / Math.sqrt(sumT*sumTp1);
				}
			}
		}
		
		for (int i = 0 ; i < graph.getNodeCount() ; i++)
		{
			result[i] /= timeEnd-timeBegin;
		}
		
		return result;
	}
}
