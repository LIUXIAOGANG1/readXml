package pipe.dataLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;

import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

import static java.lang.Math.*;
import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pipe.dataLayer.calculations.Queue;
import pipe.gui.CreateGui;
import pipe.gui.Grid;
import pipe.gui.Pipe;
import pipe.gui.widgets.ProgressBar;

/**
 * <b>DataLayer</b> - Encapsulates entire Petri-Net, also contains functions to
 * perform calculations
 * 
 * @see <p>
 *      <a href="..\PNMLSchema\index.html">PNML - Petri-Net XMLSchema
 *      (stNet.xsd)</a>
 * @see </p>
 *      <p>
 *      <a href="uml\DataLayer.png">DataLayer UML</a>
 *      </p>
 * @version 1.0
 * @author James D Bloom
 * 
 * @author David Patterson Jan 2, 2006: Changed the fireRandomTransition method
 *         to give precedence to immediate transitions.
 * 
 * @author Edwin Chung added a boolean attribute to each matrix generated to
 *         prevent them from being created again when they have not been changed
 *         (6th Feb 2007)
 * 
 * @author Ben Kirby Feb 10, 2007: Removed savePNML method and the
 *         createPlaceElement, createAnnotationElement, createArcElement,
 *         createArcPoint, createTransitionElement methods it uses to a separate
 *         DataLayerWriter class, as part of refactoring to remove XML related
 *         actions from the DataLayer class.
 * 
 * @author Ben Kirby Feb 10, 2007: Split loadPNML into two bits. All XML work
 *         (Files, transformers, documents) is done in new PNMLTransformer
 *         class. The calls to actually populate a DataLayer object with the
 *         info contained in the PNML document have been moved to a
 *         createFromPNML method. The DataLayer constructor which previously
 *         used loadPNML has been changed to reflect these modifications. Also
 *         moved getDOM methods to PNMLTranformer class, as getDom is XML
 *         related. Removed getDom() (no arguments) completely as this is not
 *         called anywhere in the application.
 * 
 * @author Will Master Feb 13 2007: Added methods getPlacesCount and
 *         getTransitionsCount to avoid needlessly copying place and transition
 *         arrayLists.
 * 
 * @author Edwin Chung 15th Mar 2007: modified the createFromPNML function so
 *         that DataLayer objects can be created outside GUI
 * 
 * @author Dave Patterson 24 April 2007: Modified the fireRandomTransition
 *         method so it is quicker when there is only one transition to fire
 *         (just fire it, don't get a random variable first). Also, throw a
 *         RuntimeException if a rate less than 1 is detected. The current code
 *         uses the rate as a weight, and a rate such as 0.5 leads to a
 *         condition like that of bug 1699546 where no transition is available
 *         to fire.
 * 
 * @author Dave Patterson 10 May 2007: Modified the fireRandomTransitino method
 *         so it now properly handles fractional weights. There is no
 *         RuntimeException thrown now. The code for timed transitions uses the
 *         same logic, but will soon be changed to use exponentially distributed
 *         times where fractional rates are valid.
 * 
 * @author Barry Kearns August 2007: Added clone functionality and storage of
 *         state groups.
 * 
 **/
public class DataLayer extends Observable implements Cloneable {

	private static Random randomNumber = new Random(); // Random number
														// generator

	/** PNML File Name */
	public String pnmlName = null;

	/** List containing all the Place objects in the Petri-Net */
	private ArrayList placesArray = null;
	/** ArrayList containing all the Transition objects in the Petri-Net */
	private static ArrayList transitionsArray = null;
	/** ArrayList containing all the Arc objects in the Petri-Net */
	private ArrayList arcsArray = null;

	/** ArrayList containing all the Arc objects in the Petri-Net */
	private ArrayList inhibitorsArray = null;

	/**
	 * ArrayList for net-level label objects (as opposed to element-level
	 * labels).
	 */
	private ArrayList labelsArray = null;

	/** ArrayList for marking Parmameters objects. */
	private ArrayList markingParametersArray = null;

	/** ArrayList for rate Parmameters objects. */
	private ArrayList rateParametersArray = null;

	/**
	 * An ArrayList used to point to either the Arc, Place or Transition
	 * ArrayLists when these ArrayLists are being update
	 */
	private ArrayList changeArrayList = null;

	/** Initial Marking Vector */
	private int[] initialMarkingVector = null;
	/** Current Marking Vector */
	private int[] currentMarkingVector = null;
	/** Capacity Matrix */
	private int[] capacityVector = null;
	/** Priority Matrix */
	private int[] priorityVector = null;
	/** Timed Matrix */
	private boolean[] timedVector = null;
	/** Marking Vector Storage used during animation */
	private int[] markingVectorAnimationStorage = null;

	/** Forward Incidence Matrix */
	private PNMatrix forwardsIncidenceMatrix = null;
	/** Backward Incidence Matrix */
	private PNMatrix backwardsIncidenceMatrix = null;
	/** Incidence Matrix */
	private PNMatrix incidenceMatrix = null;

	/** Inhibition Matrix */
	private PNMatrix inhibitionMatrix = null;

	/** Used to determine whether the matrixes have been modified */
	static boolean initialMarkingVectorChanged = true;

	static boolean currentMarkingVectorChanged = true;

	/** X-Axis Scale Value */
	private final int DISPLAY_SCALE_FACTORX = 7; // Scale factors for loading
													// other Petri-Nets (not yet
													// implemented)
	/** Y-Axis Scale Value */
	private final int DISPLAY_SCALE_FACTORY = 7; // Scale factors for loading
													// other Petri-Nets (not yet
													// implemented)
	/** X-Axis Shift Value */
	private final int DISPLAY_SHIFT_FACTORX = 270; // Scale factors for loading
													// other Petri-Nets (not yet
													// implemented)
	/** Y-Axis Shift Value */
	private final int DISPLAY_SHIFT_FACTORY = 120; // Scale factors for loading
													// other Petri-Nets (not yet
													// implemented)

	/**
	 * Hashtable which maps PlaceTransitionObjects to their list of connected
	 * arcs
	 */
	private Hashtable arcsMap = null;

	/**
	 * Hashtable which maps PlaceTransitionObjects to their list of connected
	 * arcs
	 */
	private Hashtable inhibitorsMap = null;

	/**
	 * An ArrayList used store the source / destination state groups associated
	 * with this Petri-Net
	 */
	private ArrayList stateGroups = null;

	private HashSet markingParameterHashSet = new HashSet();

	private HashSet rateParameterHashSet = new HashSet();

	/**
	 * Create Petri-Net object from PNML file with URI pnmlFileName
	 * 
	 * @param pnmlFileName
	 *            Name of PNML File
	 */
	public DataLayer(String pnmlFileName) {

		initializeMatrices();
		PNMLTransformer transform = new PNMLTransformer();
		File temp = new File(pnmlFileName);
		pnmlName = temp.getName();
		createFromPNML(transform.transformPNML(pnmlFileName));
	}

	/**
	 * Create Petri-Net object from pnmlFile
	 * 
	 * @param pnmlFile
	 *            PNML File
	 */
	public DataLayer(File pnmlFile) {
		this(pnmlFile.getAbsolutePath());
	}

	/**
	 * Create empty Petri-Net object
	 */
	public DataLayer() {
		initializeMatrices();
	}

	/**
	 * Method to clone a DataLayer obejct
	 */
	public DataLayer clone() {
		DataLayer newClone = null;
		try {
			newClone = (DataLayer) super.clone();

			newClone.placesArray = deepCopy(placesArray);
			newClone.transitionsArray = deepCopy(transitionsArray);
			newClone.arcsArray = deepCopy(arcsArray);
			newClone.inhibitorsArray = deepCopy(inhibitorsArray);
			// newClone.tokensArray = deepCopy(tokensArray);
			newClone.labelsArray = deepCopy(labelsArray);
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return newClone;
	}

	/**
	 * @param original
	 *            arraylist to be deep copied
	 * @return a clone of the arraylist
	 */
	private static ArrayList deepCopy(ArrayList original) {
		ArrayList result = (ArrayList) original.clone();
		ListIterator listIter = result.listIterator();

		while (listIter.hasNext()) {
			PetriNetObject pnObj = (PetriNetObject) listIter.next();
			listIter.set(pnObj.clone());
		}
		return result;
	}

	/**
	 * Initialize Arrays
	 */
	private void initializeMatrices() {
		placesArray = new ArrayList();
		transitionsArray = new ArrayList();
		arcsArray = new ArrayList();
		inhibitorsArray = new ArrayList();
		labelsArray = new ArrayList();
		stateGroups = new ArrayList();
		markingParametersArray = new ArrayList();
		rateParametersArray = new ArrayList();
		initialMarkingVector = null;
		forwardsIncidenceMatrix = null;
		backwardsIncidenceMatrix = null;
		incidenceMatrix = null;
		inhibitionMatrix = null;
		// 冰雪修改
		this.SP1 = null;
		this.SP2 = null;
		Sources = null;
		Drains = null;

		SPNodeList = new ArrayList<SPNode>();
		SPArcList = new ArrayList<SPArc>();
		// SPNodeListA = new ArrayList<SPNode>();
		// SPNodeListB = new ArrayList<SPNode>();
		PlaceIndex = new HashMap<Integer, Integer>();
		// bingxue 2
		f11 = null;
		;
		f22 = null;
		;
		RC = null;
		;

		// bingxue 3
		Seq = new ArrayList<String>();
		seq = null;
		FF = null;
		FB = null;
		RealPlaces = new HashMap<Integer, Integer>();
		WaitingTime = new HashMap<Integer, Integer>();

		// bingxue 4
		A = new ArrayList<String>();
		B = new ArrayList<String>();

		// may as well do the hashtable here as well
		arcsMap = new Hashtable();
		inhibitorsMap = new Hashtable();

	}

	/**
	 * Add placeInput to the back of the Place ArrayList All observers are
	 * notified of this change (Model-View Architecture)
	 * 
	 * @param placeInput
	 *            Place Object to add
	 */
	private void addPlace(Place placeInput) {
		boolean unique = true;

		if (placeInput != null) {
			if (placeInput.getId() != null && placeInput.getId().length() > 0) {
				for (int i = 0; i < placesArray.size(); i++) {
					if (placeInput.getId().equals(
							((Place) placesArray.get(i)).getId())) {
						unique = false;
					}
				}
			} else {
				String id = null;
				if (placesArray != null && placesArray.size() > 0) {
					int no = placesArray.size();
					// id = "P" + no;
					do {
						// System.out.println("in while loop"); //DBG
						for (int i = 0; i < placesArray.size(); i++) {
							id = "P" + no;
							if (placesArray.get(i) != null) {
								if (id.equals(((Place) placesArray.get(i))
										.getId())) {
									// System.out.println("testing id: " + id);
									// //DBG
									unique = false;
									no++;
								} else {
									unique = true;
								}
							}
						}
					} while (!unique);
				} else {
					id = "P0";
				}

				if (id != null) {
					placeInput.setId(id);
				} else {
					placeInput.setId("error");
				}
			}
			placesArray.add(placeInput);
			setChanged();
			setMatrixChanged();
			// notifyObservers(placeInput.getBounds());
			notifyObservers(placeInput);
		}
	}

	/**
	 * Add labelInput to the back of the AnnotationNote ArrayList All observers
	 * are notified of this change (Model-View Architecture)
	 * 
	 * @param labelInput
	 *            AnnotationNote Object to add
	 */
	private void addAnnotation(AnnotationNote labelInput) {
		boolean unique = true;
		labelsArray.add(labelInput);
		setChanged();
		notifyObservers(labelInput);
	}

	/**
	 * Add markingParameterInput to the back of the Marking Parameter ArrayList
	 * All observers are notified of this change (Model-View Architecture)
	 * 
	 * @param placeInput
	 *            Place Object to add
	 */
	private void addAnnotation(MarkingParameter markingParameterInput) {
		boolean unique = true;
		markingParametersArray.add(markingParameterInput);
		setChanged();
		notifyObservers(markingParameterInput);
	}

	/**
	 * Add rateParameterInput to the back of the Rate Parameter ArrayList All
	 * observers are notified of this change (Model-View Architecture)
	 * 
	 * @param placeInput
	 *            Place Object to add
	 */
	private void addAnnotation(RateParameter rateParameterInput) {
		boolean unique = true;
		rateParametersArray.add(rateParameterInput);
		setChanged();
		notifyObservers(rateParameterInput);
	}

	/**
	 * Add transitionInput to back of the Transition ArrayList All observers are
	 * notified of this change (Model-View Architecture)
	 * 
	 * @param transitionInput
	 *            Transition Object to add
	 */
	private void addTransition(Transition transitionInput) {
		boolean unique = true;

		if (transitionInput != null) {
			if (transitionInput.getId() != null
					&& transitionInput.getId().length() > 0) {
				for (int i = 0; i < transitionsArray.size(); i++) {
					if (transitionInput.getId().equals(
							((Transition) transitionsArray.get(i)).getId())) {
						unique = false;
					}
				}
			} else {
				String id = null;
				if (transitionsArray != null && transitionsArray.size() > 0) {
					int no = transitionsArray.size();
					do {
						// System.out.println("transition while loop");
						for (int i = 0; i < transitionsArray.size(); i++) {
							id = "T" + no;
							if (transitionsArray.get(i) != null) {
								if (id.equals(((Transition) transitionsArray
										.get(i)).getId())) {
									unique = false;
									no++;
								} else {
									unique = true;
								}
							}
						}
					} while (!unique);
				} else {
					id = "T0";
				}

				if (id != null) {
					transitionInput.setId(id);
				} else {
					transitionInput.setId("error");
				}
			}
			transitionsArray.add(transitionInput);
			setChanged();
			setMatrixChanged();
			// notifyObservers(transitionInput.getBounds());
			notifyObservers(transitionInput);
		}
	}

	/**
	 * Add arcInput to back of the Arc ArrayList All observers are notified of
	 * this change (Model-View Architecture)
	 * 
	 * @param arcInput
	 *            Arc Object to add
	 */
	public void addArc(NormalArc arcInput) {
		boolean unique = true;

		if (arcInput != null) {
			if (arcInput.getId() != null && arcInput.getId().length() > 0) {
				for (int i = 0; i < arcsArray.size(); i++) {
					if (arcInput.getId().equals(
							((Arc) arcsArray.get(i)).getId())) {
						unique = false;
					}
				}
			} else {
				String id = null;
				if (arcsArray != null && arcsArray.size() > 0) {
					int no = arcsArray.size();
					do {
						for (int i = 0; i < arcsArray.size(); i++) {
							id = "A" + no;
							if (arcsArray.get(i) != null) {
								if (id.equals(((Arc) arcsArray.get(i)).getId())) {
									unique = false;
									no++;
								} else {
									unique = true;
								}
							}
						}
					} while (!unique);
				} else {
					id = "A0";
				}
				if (id != null) {
					arcInput.setId(id);
				} else {
					arcInput.setId("error");
				}
			}
			arcsArray.add(arcInput);
			addArcToArcsMap(arcInput);

			setChanged();
			setMatrixChanged();
			// notifyObservers(arcInput.getBounds());
			notifyObservers(arcInput);
		}
	}

	/**
	 * Add inhibitorArcInput to back of the InhibitorArc ArrayList All observers
	 * are notified of this change (Model-View Architecture)
	 * 
	 * @param arcInput
	 *            Arc Object to add
	 */
	public void addArc(InhibitorArc inhibitorArcInput) {
		boolean unique = true;

		if (inhibitorArcInput != null) {
			if (inhibitorArcInput.getId() != null
					&& inhibitorArcInput.getId().length() > 0) {
				for (int i = 0; i < inhibitorsArray.size(); i++) {
					if (inhibitorArcInput.getId().equals(
							((Arc) inhibitorsArray.get(i)).getId())) {
						unique = false;
					}
				}
			} else {
				String id = null;
				if (inhibitorsArray != null && inhibitorsArray.size() > 0) {
					int no = inhibitorsArray.size();
					do {
						for (int i = 0; i < inhibitorsArray.size(); i++) {
							id = "I" + no;
							if (inhibitorsArray.get(i) != null) {
								if (id.equals(((Arc) inhibitorsArray.get(i))
										.getId())) {
									unique = false;
									no++;
								} else {
									unique = true;
								}
							}
						}
					} while (!unique);
				} else {
					id = "I0";
				}
				if (id != null) {
					inhibitorArcInput.setId(id);
				} else {
					inhibitorArcInput.setId("error");
				}
			}
			inhibitorsArray.add(inhibitorArcInput);
			addInhibitorArcToInhibitorsMap(inhibitorArcInput);

			setChanged();
			setMatrixChanged();
			// notifyObservers(arcInput.getBounds());
			notifyObservers(inhibitorArcInput);
		}
	}

	/**
	 * Update the arcsMap hashtable to reflect the new arc
	 * 
	 * @param arcInput
	 *            New Arc
	 * */
	private void addArcToArcsMap(NormalArc arcInput) {
		// now we want to add the arc to the list of arcs for it's source and
		// target
		PlaceTransitionObject source = arcInput.getSource();
		PlaceTransitionObject target = arcInput.getTarget();
		ArrayList newList = null;

		if (source != null) {
			// Pete: Place/Transitions now always moveable
			// source.setMovable(false);
			if (arcsMap.get(source) != null) {
				// System.out.println("adding arc to existing list");
				((ArrayList) arcsMap.get(source)).add(arcInput);
			} else {
				// System.out.println("creating new arc list");
				newList = new ArrayList();
				newList.add(arcInput);
				arcsMap.put(source, newList);
			}
		}

		if (target != null) {
			// Pete: Place/Transitions now always moveable
			// target.setMovable(false);
			if (arcsMap.get(target) != null) {
				// System.out.println("adding arc to existing list2");
				((ArrayList) arcsMap.get(target)).add(arcInput);
			} else {
				// System.out.println("creating new arc list2");
				newList = new ArrayList();
				newList.add(arcInput);
				arcsMap.put(target, newList);
			}
		}
		// System.out.println("arcsMap size: " + arcsMap.size());
	}

	/**
	 * Update the inhibitorsMap hashtable to reflect the new inhibitor arc
	 * 
	 * @param arcInput
	 *            New Arc
	 */
	private void addInhibitorArcToInhibitorsMap(InhibitorArc inhibitorArcInput) {
		// now we want to add the inhibitor arc to the list of inhibitor arcs
		// for
		// it's source and target
		PlaceTransitionObject source = inhibitorArcInput.getSource();
		PlaceTransitionObject target = inhibitorArcInput.getTarget();
		ArrayList newList = null;

		if (source != null) {
			if (inhibitorsMap.get(source) != null) {
				((ArrayList) inhibitorsMap.get(source)).add(inhibitorArcInput);
			} else {
				newList = new ArrayList();
				newList.add(inhibitorArcInput);
				inhibitorsMap.put(source, newList);
			}
		}

		if (target != null) {
			if (inhibitorsMap.get(target) != null) {
				((ArrayList) inhibitorsMap.get(target)).add(inhibitorArcInput);
			} else {
				newList = new ArrayList();
				newList.add(inhibitorArcInput);
				inhibitorsMap.put(target, newList);
			}
		}
		// System.out.println("inhibitorsMap size: " + inhibitorsMap.size());
	}

	public void addStateGroup(StateGroup stateGroupInput) {
		boolean unique = true;
		String id = null;
		int no = stateGroups.size();

		// Check if ID is set from PNML file
		if (stateGroupInput.getId() != null
				&& stateGroupInput.getId().length() > 0) {
			id = stateGroupInput.getId();

			// Check if ID is unique
			for (int i = 0; i < stateGroups.size(); i++) {
				if (id.equals(((StateGroup) stateGroups.get(i)).getId())) {
					unique = false;
				}
			}
		} else {
			unique = false;
		}

		// Find a unique ID for the new state group
		if (!unique) {
			id = "SG" + no;
			for (int i = 0; i < stateGroups.size(); i++) {
				// If a matching ID is found, increment id and reset loop
				if (id.equals(((StateGroup) stateGroups.get(i)).getId())) {
					id = "SG" + ++no;
					i = 0;
				}
			}
			stateGroupInput.setId(id);
		}
		stateGroups.add(stateGroupInput);
	}

	/**
	 * Add any PetriNetObject - the object will be added to the appropriate
	 * list. If the object passed in isn't a Transition, Place or Arc nothing
	 * will happen. All observers are notified of this change.
	 * 
	 * @param pnObject
	 *            The PetriNetObject to be added.
	 */
	public void addPetriNetObject(PetriNetObject pnObject) {
		if (setPetriNetObjectArrayList(pnObject)) {
			if (pnObject instanceof NormalArc) {
				addArcToArcsMap((NormalArc) pnObject);
				addArc((NormalArc) pnObject);
			} else if (pnObject instanceof InhibitorArc) {
				addInhibitorArcToInhibitorsMap((InhibitorArc) pnObject);
				addArc((InhibitorArc) pnObject);
			} else if (pnObject instanceof Place) {
				addPlace((Place) pnObject);
			} else if (pnObject instanceof Transition) {
				addTransition((Transition) pnObject);
			} else if (pnObject instanceof AnnotationNote) {
				labelsArray.add(pnObject);
			} else if (pnObject instanceof RateParameter) {
				rateParametersArray.add(pnObject);
				rateParameterHashSet.add(pnObject.getName());
			} else if (pnObject instanceof MarkingParameter) {
				markingParametersArray.add(pnObject);
				markingParameterHashSet.add(pnObject.getName());
			} else { // arrows, other labels.
				changeArrayList.add(pnObject);
				setChanged();
				setMatrixChanged();
				notifyObservers(pnObject);
			}
		}
		// we reset to null so that the wrong ArrayList can't get added to
		changeArrayList = null;
	}

	/**
	 * Removes the specified object from the appropriate ArrayList of objects.
	 * All observers are notified of this change.
	 * 
	 * @param pnObject
	 *            The PetriNetObject to be removed.
	 */
	public void removePetriNetObject(PetriNetObject pnObject) {
		boolean didSomething = false;
		ArrayList attachedArcs = null;

		try {
			if (setPetriNetObjectArrayList(pnObject)) {
				didSomething = changeArrayList.remove(pnObject);
				// we want to remove all attached arcs also
				if (pnObject instanceof PlaceTransitionObject) {

					if ((ArrayList) arcsMap.get(pnObject) != null) {

						// get the list of attached arcs for the object we are
						// removing
						attachedArcs = ((ArrayList) arcsMap.get(pnObject));

						// iterate over all the attached arcs, removing them all
						// Pere: in inverse order!
						// for (int i=0; i < attachedArcs.size(); i++){
						for (int i = attachedArcs.size() - 1; i >= 0; i--) {
							((Arc) attachedArcs.get(i)).delete();
						}
						arcsMap.remove(pnObject);
					}

					if ((ArrayList) inhibitorsMap.get(pnObject) != null) {

						// get the list of attached arcs for the object we are
						// removing
						attachedArcs = ((ArrayList) inhibitorsMap.get(pnObject));

						// iterate over all the attached arcs, removing them all
						// Pere: in inverse order!
						// for (int i=0; i < attachedArcs.size(); i++){
						for (int i = attachedArcs.size() - 1; i >= 0; i--) {
							((Arc) attachedArcs.get(i)).delete();
						}
						inhibitorsMap.remove(pnObject);
					}
				} else if (pnObject instanceof NormalArc) {

					// get source and target of the arc
					PlaceTransitionObject attached = ((Arc) pnObject)
							.getSource();

					if (attached != null) {
						ArrayList a = (ArrayList) arcsMap.get(attached);
						if (a != null) {
							a.remove(pnObject);
						}

						attached.removeFromArc((Arc) pnObject);
						if (attached instanceof Transition) {
							((Transition) attached)
									.removeArcCompareObject((Arc) pnObject);
							attached.updateConnected();
						}
						// attached.updateConnected(); //causing null pointer
						// exceptions (?)
					}

					attached = ((Arc) pnObject).getTarget();
					if (attached != null) {
						if (arcsMap.get(attached) != null) { // causing null
																// pointer
																// exceptions
																// (!)
							((ArrayList) arcsMap.get(attached))
									.remove(pnObject);
						}

						attached.removeToArc((Arc) pnObject);
						if (attached instanceof Transition) {
							((Transition) attached)
									.removeArcCompareObject((Arc) pnObject);
							attached.updateConnected();
						}
						// attached.updateConnected(); //causing null pointer
						// exceptions (?)
					}
				} else if (pnObject instanceof InhibitorArc) {

					// get source and target of the arc
					PlaceTransitionObject attached = ((Arc) pnObject)
							.getSource();

					if (attached != null) {
						ArrayList a = (ArrayList) inhibitorsMap.get(attached);
						if (a != null) {
							a.remove(pnObject);
						}

						attached.removeFromArc((Arc) pnObject);
						if (attached instanceof Transition) {
							((Transition) attached)
									.removeArcCompareObject((Arc) pnObject);
						}
						// attached.updateConnected(); //causing null pointer
						// exceptions (?)
					}

					attached = ((Arc) pnObject).getTarget();

					if (attached != null) {
						if (inhibitorsMap.get(attached) != null) { // causing
																	// null
																	// pointer
																	// exceptions
																	// (!)
							((ArrayList) inhibitorsMap.get(attached))
									.remove(pnObject);
						}

						attached.removeToArc((Arc) pnObject);
						if (attached instanceof Transition) {
							((Transition) attached)
									.removeArcCompareObject((Arc) pnObject);
						}
						// attached.updateConnected(); //causing null pointer
						// exceptions (?)
					}
				} else if (pnObject instanceof MarkingParameter) {
					markingParameterHashSet.remove(pnObject.getName());
				} else if (pnObject instanceof RateParameter) {
					rateParameterHashSet.remove(pnObject.getName());
				}

				if (didSomething) {
					setChanged();
					setMatrixChanged();
					// notifyObservers(pnObject.getBounds());
					notifyObservers(pnObject);
				}
			}
		} catch (NullPointerException npe) {
			System.out.println("NullPointerException [debug]\n"
					+ npe.getMessage());
			throw npe;
		}
		// we reset to null so that the wrong ArrayList can't get added to
		changeArrayList = null;
	}

	/**
	 * This method removes a state group from the arrayList
	 * 
	 * @param SGObject
	 *            The State Group objet to be removed
	 */
	public void removeStateGroup(StateGroup SGObject) {
		stateGroups.remove(SGObject);
	}

	/**
	 * Checks whether a state group with the same name exists already as the
	 * argument * @param stateName
	 * 
	 * @return
	 */
	public boolean stateGroupExistsAlready(String stateName) {
		Iterator<StateGroup> i = stateGroups.iterator();
		while (i.hasNext()) {
			StateGroup stateGroup = i.next();
			String stateGroupName = stateGroup.getName();
			if (stateName.equals(stateGroupName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an iterator for the transitions array. Used by Animator.class to
	 * set all enabled transitions to highlighted
	 */
	public Iterator returnTransitions() {
		return transitionsArray.iterator();
	}

	/**
	 * Sets an internal ArrayList according to the class of the object passed
	 * in.
	 * 
	 * @param pnObject
	 *            The pnObject in question.
	 * @return Returns True if the pnObject is of type Place, Transition or Arc
	 */
	private boolean setPetriNetObjectArrayList(PetriNetObject pnObject) {

		// determine appropriate ArrayList
		if (pnObject instanceof Transition) {
			changeArrayList = transitionsArray;
			return true;
		} else if (pnObject instanceof Place) {
			changeArrayList = placesArray;
			return true;
		} else if (pnObject instanceof NormalArc) {
			changeArrayList = arcsArray;
			return true;
		} else if (pnObject instanceof InhibitorArc) {
			changeArrayList = inhibitorsArray;
			return true;
		} else if (pnObject instanceof AnnotationNote) {
			changeArrayList = labelsArray;
			return true;
		} else if (pnObject instanceof MarkingParameter) {
			changeArrayList = markingParametersArray;
			return true;
		} else if (pnObject instanceof RateParameter) {
			changeArrayList = rateParametersArray;
			return true;
		}
		return false;
	}

	/**
	 * Returns an iterator of all PetriNetObjects - the order of these cannot be
	 * guaranteed.
	 * 
	 * @return An iterator of all PetriNetObjects
	 */
	public Iterator getPetriNetObjects() {
		ArrayList all = new ArrayList(placesArray);
		all.addAll(transitionsArray);
		all.addAll(arcsArray);
		all.addAll(labelsArray);
		// tokensArray removed
		all.addAll(markingParametersArray);
		all.addAll(rateParametersArray);

		return all.iterator();
	}

	public boolean hasPlaceTransitionObjects() {
		return (placesArray.size() + transitionsArray.size()) > 0;
	}

	/**
	 * Creates a Label object from a Label DOM Element
	 * 
	 * @param inputLabelElement
	 *            Input Label DOM Element
	 * @return Label Object
	 */
	private AnnotationNote createAnnotation(Element inputLabelElement) {
		int positionXInput = 0;
		int positionYInput = 0;
		int widthInput = 0;
		int heightInput = 0;
		String text = null;
		boolean borderInput = true;

		String positionXTempStorage = inputLabelElement
				.getAttribute("xPosition");
		String positionYTempStorage = inputLabelElement
				.getAttribute("yPosition");
		String widthTemp = inputLabelElement.getAttribute("w");
		String heightTemp = inputLabelElement.getAttribute("h");
		String textTempStorage = inputLabelElement.getAttribute("txt");
		String borderTemp = inputLabelElement.getAttribute("border");
		String nameTemp = inputLabelElement.getAttribute("name");

		if (positionXTempStorage.length() > 0) {
			positionXInput = Integer.valueOf(positionXTempStorage).intValue()
					* (false ? DISPLAY_SCALE_FACTORX : 1)
					+ (false ? DISPLAY_SHIFT_FACTORX : 1);
		}

		if (positionYTempStorage.length() > 0) {
			positionYInput = Integer.valueOf(positionYTempStorage).intValue()
					* (false ? DISPLAY_SCALE_FACTORX : 1)
					+ (false ? DISPLAY_SHIFT_FACTORX : 1);
		}

		if (widthTemp.length() > 0) {
			widthInput = Integer.valueOf(widthTemp).intValue()
					* (false ? DISPLAY_SCALE_FACTORY : 1)
					+ (false ? DISPLAY_SHIFT_FACTORY : 1);
		}

		if (heightTemp.length() > 0) {
			heightInput = Integer.valueOf(heightTemp).intValue()
					* (false ? DISPLAY_SCALE_FACTORY : 1)
					+ (false ? DISPLAY_SHIFT_FACTORY : 1);
		}

		if (borderTemp.length() > 0) {
			borderInput = Boolean.valueOf(borderTemp).booleanValue();
		} else {
			borderInput = true;
		}

		if (textTempStorage.length() > 0) {
			text = textTempStorage;
		} else {
			text = "";
		}

		return new AnnotationNote(text, positionXInput, positionYInput,
				widthInput, heightInput, borderInput);
	}

	/**
	 * Creates a Parameter object from a Definition DOM Element
	 * 
	 * @param inputDefinitionElement
	 *            Input Label DOM Element
	 * @return Parameter Object
	 */
	private Parameter createParameter(Element inputDefinitionElement) {
		int positionXInput = 0;
		int positionYInput = 0;
		String type = null;
		boolean borderInput = true;

		String positionXTempStorage = inputDefinitionElement
				.getAttribute("positionX");
		String positionYTempStorage = inputDefinitionElement
				.getAttribute("positionY");
		String typeTemp = inputDefinitionElement.getAttribute("type");
		String borderTemp = "true";
		String nameTemp = inputDefinitionElement.getAttribute("name");
		String expressionTemp = inputDefinitionElement
				.getAttribute("expression");

		if (positionXTempStorage.length() > 0) {
			positionXInput = Integer.valueOf(positionXTempStorage).intValue()/* *
																			 * (
																			 * false
																			 * ?
																			 * DISPLAY_SCALE_FACTORX
																			 * :
																			 * 1
																			 * )
																			 * +
																			 * (
																			 * false
																			 * ?
																			 * DISPLAY_SHIFT_FACTORX
																			 * :
																			 * 1
																			 * )
																			 */;
		}

		if (positionYTempStorage.length() > 0) {
			positionYInput = Integer.valueOf(positionYTempStorage).intValue()/* *
																			 * (
																			 * false
																			 * ?
																			 * DISPLAY_SCALE_FACTORX
																			 * :
																			 * 1
																			 * )
																			 * +
																			 * (
																			 * false
																			 * ?
																			 * DISPLAY_SHIFT_FACTORX
																			 * :
																			 * 1
																			 * )
																			 */;
		}

		if (typeTemp.length() > 0) {
			type = typeTemp;
		} else {
			type = "real";
		}

		if (type.equals("real")) {
			rateParameterHashSet.add(nameTemp);
			return new RateParameter(nameTemp,
					Double.parseDouble(expressionTemp), positionXInput,
					positionYInput);
		} else {
			markingParameterHashSet.add(nameTemp);
			return new MarkingParameter(nameTemp,
					Integer.parseInt(expressionTemp), positionXInput,
					positionYInput);
		}
	}

	/**
	 * Creates a Transition object from a Transition DOM Element
	 * 
	 * @param inputTransitionElement
	 *            Input Transition DOM Element
	 * @return Transition Object
	 */
	private Transition createTransition(Element element) {
		double positionXInput = 0;
		double positionYInput = 0;
		String idInput = null;
		String nameInput = null;
		double nameOffsetYInput = 0;
		double nameOffsetXInput = 0;
		double rate = 1.0;
		boolean timedTransition;
		boolean infiniteServer;
		int angle = 0;
		int priority = 1;
		double weight = 1.0;
		// 金龙修改
		int strtime = 0;
		int entime = 0;
		boolean timeInterval = true;
		// 金龙 结束
		String positionXTempStorage = element.getAttribute("positionX");
		String positionYTempStorage = element.getAttribute("positionY");
		String idTempStorage = element.getAttribute("id");
		String nameTempStorage = element.getAttribute("name");
		String nameOffsetXTempStorage = element.getAttribute("nameOffsetX");
		String nameOffsetYTempStorage = element.getAttribute("nameOffsetY");
		String nameRate = element.getAttribute("rate");
		String nameTimed = element.getAttribute("timed");
		String nameInfiniteServer = element.getAttribute("infiniteServer");
		String nameAngle = element.getAttribute("angle");
		String namePriority = element.getAttribute("priority");
		// String nameWeight = element.getAttribute("weight");
		String parameterTempStorage = element.getAttribute("parameter");
		// --------------------------BX-----------------------------------------
		String starttime = element.getAttribute("starttime");
		String endtime = element.getAttribute("endtime");
		// ----------------------------------------------------------------------------
		/*
		 * wjk - a useful little routine to display all attributes of a
		 * transition for (int i=0; ; i++) { Object obj =
		 * inputTransitionElement.getAttributes().item(i); if (obj == null) {
		 * break; } System.out.println("Attribute " + i + " = " +
		 * obj.toString()); }
		 */

		if (nameTimed.length() == 0) {
			timedTransition = false;
		} else if (nameTimed.length() == 5) {
			timedTransition = false;
		} else {
			timedTransition = true;
		}

		infiniteServer = !(nameInfiniteServer.length() == 0 || nameInfiniteServer
				.length() == 5);

		if (positionXTempStorage.length() > 0) {
			positionXInput = Double.valueOf(positionXTempStorage).doubleValue()
					* (false ? Pipe.DISPLAY_SCALE_FACTORX : 1)
					+ (false ? Pipe.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (positionYTempStorage.length() > 0) {
			positionYInput = Double.valueOf(positionYTempStorage).doubleValue()
					* (false ? Pipe.DISPLAY_SCALE_FACTORY : 1)
					+ (false ? Pipe.DISPLAY_SHIFT_FACTORY : 1);
		}

		positionXInput = Grid.getModifiedX(positionXInput);
		positionYInput = Grid.getModifiedY(positionYInput);
		// --------------------------------
		if (starttime.length() != 0) {
			strtime = Integer.valueOf(starttime);
		}
		if (endtime.length() != 0) {
			entime = Integer.valueOf(endtime);
		}
		// -----------------------------------------
		if (idTempStorage.length() > 0) {
			idInput = idTempStorage;
		} else if (nameTempStorage.length() > 0) {
			idInput = nameTempStorage;
		}

		if (nameTempStorage.length() > 0) {
			nameInput = nameTempStorage;
		} else if (idTempStorage.length() > 0) {
			nameInput = idTempStorage;
		}

		if (nameOffsetXTempStorage.length() > 0) {
			nameOffsetXInput = Double.valueOf(nameOffsetXTempStorage)
					.doubleValue();
		}

		if (nameOffsetYTempStorage.length() > 0) {
			nameOffsetYInput = Double.valueOf(nameOffsetYTempStorage)
					.doubleValue();
		}

		if (nameRate.length() == 0) {
			nameRate = "1.0";
		}
		if (nameRate != "1.0") {
			rate = Double.valueOf(nameRate).doubleValue();
		} else {
			rate = 1.0;
		}

		if (nameAngle.length() > 0) {
			angle = Integer.valueOf(nameAngle).intValue();
		}

		if (namePriority.length() > 0) {
			priority = Integer.valueOf(namePriority).intValue();
		}

		Transition transition = new Transition(positionXInput, positionYInput,
				idInput, nameInput, nameOffsetXInput, nameOffsetYInput, rate,
				timedTransition, infiniteServer, angle, priority, strtime,
				entime, timeInterval);

		if (parameterTempStorage.length() > 0) {
			if (existsRateParameter(parameterTempStorage)) {
				for (int i = 0; i < rateParametersArray.size(); i++) {
					if (parameterTempStorage
							.equals(((RateParameter) rateParametersArray.get(i))
									.getName())) {
						transition
								.setRateParameter((RateParameter) rateParametersArray
										.get(i));
					}
				}
			}
		}

		return transition;
	}

	private Place createPlace(Element element) {
		double positionXInput = 0;
		double positionYInput = 0;
		String idInput = null;
		String nameInput = null;
		double nameOffsetYInput = 0;
		double nameOffsetXInput = 0;
		int initialMarkingInput = 0;
		double markingOffsetXInput = 0;
		double markingOffsetYInput = 0;
		int capacityInput = 0;

		String positionXTempStorage = element.getAttribute("positionX");
		String positionYTempStorage = element.getAttribute("positionY");
		String idTempStorage = element.getAttribute("id");
		String nameTempStorage = element.getAttribute("name");
		String nameOffsetXTempStorage = element.getAttribute("nameOffsetX");
		String nameOffsetYTempStorage = element.getAttribute("nameOffsetY");
		String initialMarkingTempStorage = element
				.getAttribute("initialMarking");
		String markingOffsetXTempStorage = element
				.getAttribute("markingOffsetX");
		String markingOffsetYTempStorage = element
				.getAttribute("markingOffsetY");
		String capacityTempStorage = element.getAttribute("capacity");
		String parameterTempStorage = element.getAttribute("parameter");

		if (positionXTempStorage.length() > 0) {
			positionXInput = Double.valueOf(positionXTempStorage).doubleValue()
					* (false ? Pipe.DISPLAY_SCALE_FACTORX : 1)
					+ (false ? Pipe.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (positionYTempStorage.length() > 0) {
			positionYInput = Double.valueOf(positionYTempStorage).doubleValue()
					* (false ? Pipe.DISPLAY_SCALE_FACTORY : 1)
					+ (false ? Pipe.DISPLAY_SHIFT_FACTORY : 1);
		}
		positionXInput = Grid.getModifiedX(positionXInput);
		positionYInput = Grid.getModifiedY(positionYInput);

		if (idTempStorage.length() > 0) {
			idInput = idTempStorage;
		} else if (nameTempStorage.length() > 0) {
			idInput = nameTempStorage;
		}

		if (nameTempStorage.length() > 0) {
			nameInput = nameTempStorage;
		} else if (idTempStorage.length() > 0) {
			nameInput = idTempStorage;
		}

		if (nameOffsetYTempStorage.length() > 0) {
			nameOffsetXInput = Double.valueOf(nameOffsetXTempStorage)
					.doubleValue();
		}
		if (nameOffsetXTempStorage.length() > 0) {
			nameOffsetYInput = Double.valueOf(nameOffsetYTempStorage)
					.doubleValue();
		}

		if (initialMarkingTempStorage.length() > 0) {
			initialMarkingInput = Integer.valueOf(initialMarkingTempStorage)
					.intValue();
		}
		if (markingOffsetXTempStorage.length() > 0) {
			markingOffsetXInput = Double.valueOf(markingOffsetXTempStorage)
					.doubleValue();
		}
		if (markingOffsetYTempStorage.length() > 0) {
			markingOffsetYInput = Double.valueOf(markingOffsetYTempStorage)
					.doubleValue();
		}

		if (capacityTempStorage.length() > 0) {
			capacityInput = Integer.valueOf(capacityTempStorage).intValue();
		}

		Place place = new Place(positionXInput, positionYInput, idInput,
				nameInput, nameOffsetXInput, nameOffsetYInput,
				initialMarkingInput, markingOffsetXInput, markingOffsetYInput,
				capacityInput);

		if (parameterTempStorage.length() > 0) {
			if (existsMarkingParameter(parameterTempStorage)) {
				for (int i = 0; i < markingParametersArray.size(); i++) {
					if (parameterTempStorage
							.equals(((MarkingParameter) markingParametersArray
									.get(i)).getName())) {
						place.setMarkingParameter((MarkingParameter) markingParametersArray
								.get(i));
					}
				}
			}
		}

		return place;
	}

	/**
	 * Creates a Arc object from a Arc DOM Element
	 * 
	 * @param inputArcElement
	 *            Input Arc DOM Element
	 * @return Arc Object
	 */
	private Arc createArc(Element inputArcElement) {
		String idInput = null;
		String sourceInput = null;
		String targetInput = null;
		int weightInput = 1;
		double inscriptionOffsetXInput = 0;
		double inscriptionOffsetYInput = 0;
		double startX = 0;
		double startY = 0;
		double endX = 0;
		double endY = 0;
		boolean taggedArc;

		sourceInput = inputArcElement.getAttribute("source");
		targetInput = inputArcElement.getAttribute("target");
		String idTempStorage = inputArcElement.getAttribute("id");
		String sourceTempStorage = inputArcElement.getAttribute("source");
		String targetTempStorage = inputArcElement.getAttribute("target");
		String inscriptionTempStorage = inputArcElement
				.getAttribute("inscription");
		String taggedTempStorage = inputArcElement.getAttribute("tagged");
		// String inscriptionOffsetXTempStorage =
		// inputArcElement.getAttribute("inscriptionOffsetX");
		// String inscriptionOffsetYTempStorage =
		// inputArcElement.getAttribute("inscriptionOffsetY");

		taggedArc = !(taggedTempStorage.length() == 0 || taggedTempStorage
				.length() == 5);

		if (idTempStorage.length() > 0) {
			idInput = idTempStorage;
		}
		if (sourceTempStorage.length() > 0) {
			sourceInput = sourceTempStorage;
		}
		if (targetTempStorage.length() > 0) {
			targetInput = targetTempStorage;
		}
		if (inscriptionTempStorage.length() > 0) {
			weightInput = Integer
					.valueOf(
							(inputArcElement.getAttribute("inscription") != null ? inputArcElement
									.getAttribute("inscription") : "1"))
					.intValue();
		}

		if (sourceInput.length() > 0) {
			if (getPlaceTransitionObject(sourceInput) != null) {
				// System.out.println("PNMLDATA: sourceInput is not null");
				startX = getPlaceTransitionObject(sourceInput).getPositionX();
				startX += getPlaceTransitionObject(sourceInput)
						.centreOffsetLeft();
				startY = getPlaceTransitionObject(sourceInput).getPositionY();
				startY += getPlaceTransitionObject(sourceInput)
						.centreOffsetTop();
			}
		}
		if (targetInput.length() > 0) {
			if (getPlaceTransitionObject(targetInput) != null) {
				// System.out.println("PNMLDATA: targetInput is not null");
				endX = getPlaceTransitionObject(targetInput).getPositionX();
				endY = getPlaceTransitionObject(targetInput).getPositionY();
			}
		}

		PlaceTransitionObject sourceIn = getPlaceTransitionObject(sourceInput);
		PlaceTransitionObject targetIn = getPlaceTransitionObject(targetInput);

		int aStartx = 0;
		int aStarty = 0;
		
		int aEndx = 0;
		int aEndy = 0;
		if(sourceIn != null){
			// add the insets and offset
			aStartx = sourceIn.getX() + sourceIn.centreOffsetLeft();
			aStarty = sourceIn.getY() + sourceIn.centreOffsetTop();
		}
		
		if(targetIn != null){
			aEndx = targetIn.getX() + targetIn.centreOffsetLeft();
			aEndy = targetIn.getY() + targetIn.centreOffsetTop();
		}



		double _startx = aStartx;
		double _starty = aStarty;
		double _endx = aEndx;
		double _endy = aEndy;
		// TODO

		Arc tempArc;

		String type = "normal"; // default value
		NodeList nl = inputArcElement.getElementsByTagName("type");
		if (nl.getLength() > 0) {
			type = ((Element) (nl.item(0))).getAttribute("type");
		}

		if (type.equals("inhibitor")) {
			tempArc = new InhibitorArc(_startx, _starty, _endx, _endy,
					sourceIn, targetIn, weightInput, idInput);
		} else {
			tempArc = new NormalArc(_startx, _starty, _endx, _endy, sourceIn,
					targetIn, weightInput, idInput, taggedArc);
		}

		if(getPlaceTransitionObject(sourceInput) != null){
			getPlaceTransitionObject(sourceInput).addConnectFrom(tempArc);
		}
		if(getPlaceTransitionObject(targetInput) != null){
			getPlaceTransitionObject(targetInput).addConnectTo(tempArc);
		}

		// **********************************************************************************
		// The following section attempts to load and display arcpath
		// details****************

		// NodeList nodelist = inputArcElement.getChildNodes();
		NodeList nodelist = inputArcElement.getElementsByTagName("arcpath");
		if (nodelist.getLength() > 0) {
			tempArc.getArcPath().purgePathPoints();
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;
					if ("arcpath".equals(element.getNodeName())) {
						String arcTempX = element.getAttribute("x");
						String arcTempY = element.getAttribute("y");
						String arcTempType = element
								.getAttribute("arcPointType");
						float arcPointX = Float.valueOf(arcTempX).floatValue();
						float arcPointY = Float.valueOf(arcTempY).floatValue();
						arcPointX += Pipe.ARC_CONTROL_POINT_CONSTANT + 1;
						arcPointY += Pipe.ARC_CONTROL_POINT_CONSTANT + 1;
						boolean arcPointType = Boolean.valueOf(arcTempType)
								.booleanValue();
						tempArc.getArcPath().addPoint(arcPointX, arcPointY,
								arcPointType);
					}
				}
			}
		}

		// Arc path creation ends
		// here***************************************************************
		// ******************************************************************************************
		return tempArc;
	}

	/**
	 * Creates all Petri-Net Matrixes from current Petri-Net
	 */
	private void createMatrixes() {
		createIncidenceMatrix();
		createInitialMarkingVector();
		createCurrentMarkingVector();
		createInhibitionMatrix();
	}

	/**
	 * Creates Forward Incidence Matrix from current Petri-Net
	 */
	private void createForwardIncidenceMatrix() {
		int placeSize = placesArray.size();
		int transitionSize = transitionsArray.size();

		forwardsIncidenceMatrix = new PNMatrix(placeSize, transitionSize);

		for (int i = 0; i < arcsArray.size(); i++) {
			Arc arc = (Arc) arcsArray.get(i);
			if (arc != null) {
				PetriNetObject pnObject = arc.getTarget();
				if (pnObject != null) {
					if (pnObject instanceof Place) {
						Place place = (Place) pnObject;
						pnObject = arc.getSource();
						if (pnObject != null) {
							if (pnObject instanceof Transition) {
								Transition transition = (Transition) pnObject;
								int transitionNo = getListPosition(transition);
								int placeNo = getListPosition(place);
								try {
									forwardsIncidenceMatrix.set(placeNo,
											transitionNo, arc.getWeight());
								} catch (Exception e) {
									JOptionPane
											.showMessageDialog(null,
													"Problem in forwardsIncidenceMatrix");
									System.out.println("p:" + placeNo + ";t:"
											+ transitionNo + ";w:"
											+ arc.getWeight());
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Creates Backwards Incidence Matrix from current Petri-Net
	 */
	private void createBackwardsIncidenceMatrix() {// Matthew
		int placeSize = placesArray.size();
		int transitionSize = transitionsArray.size();

		backwardsIncidenceMatrix = new PNMatrix(placeSize, transitionSize);

		for (int i = 0; i < arcsArray.size(); i++) {
			Arc arc = (Arc) arcsArray.get(i);
			if (arc != null) {
				PetriNetObject pnObject = arc.getSource();
				if (pnObject != null) {
					if (pnObject instanceof Place) {
						Place place = (Place) pnObject;
						pnObject = arc.getTarget();
						if (pnObject != null) {
							if (pnObject instanceof Transition) {
								Transition transition = (Transition) pnObject;
								int transitionNo = getListPosition(transition);
								int placeNo = getListPosition(place);
								try {
									backwardsIncidenceMatrix.set(placeNo,
											transitionNo, arc.getWeight());
								} catch (Exception e) {
									JOptionPane
											.showMessageDialog(null,
													"Problem in backwardsIncidenceMatrix");
									System.out.println("p:" + placeNo + ";t:"
											+ transitionNo + ";w:"
											+ arc.getWeight());
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Creates Incidence Matrix from current Petri-Net
	 */
	private void createIncidenceMatrix() {
		createForwardIncidenceMatrix();
		createBackwardsIncidenceMatrix();
		incidenceMatrix = new PNMatrix(forwardsIncidenceMatrix);
		incidenceMatrix = incidenceMatrix.minus(backwardsIncidenceMatrix);
		incidenceMatrix.matrixChanged = false;
	}

	/**
	 * Creates Initial Marking Vector from current Petri-Net
	 */
	private void createInitialMarkingVector() {
		int placeSize = placesArray.size();

		initialMarkingVector = new int[placeSize];
		for (int placeNo = 0; placeNo < placeSize; placeNo++) {
			initialMarkingVector[placeNo] = ((Place) placesArray.get(placeNo))
					.getInitialMarking();
		}
	}

	/**
	 * Creates Current Marking Vector from current Petri-Net
	 */
	private void createCurrentMarkingVector() {
		int placeSize = placesArray.size();

		currentMarkingVector = new int[placeSize];
		for (int placeNo = 0; placeNo < placeSize; placeNo++) {
			currentMarkingVector[placeNo] = ((Place) placesArray.get(placeNo))
					.getCurrentMarking();
		}
	}

	/**
	 * Creates Capacity Vector from current Petri-Net
	 */
	private void createCapacityVector() {
		int placeSize = placesArray.size();

		capacityVector = new int[placeSize];
		for (int placeNo = 0; placeNo < placeSize; placeNo++) {
			capacityVector[placeNo] = ((Place) placesArray.get(placeNo))
					.getCapacity();
		}
	}

	/**
	 * Creates Timed Vector from current Petri-Net
	 */
	private void createTimedVector() {
		int transitionSize = transitionsArray.size();

		timedVector = new boolean[transitionSize];
		for (int transitionNo = 0; transitionNo < transitionSize; transitionNo++) {
			timedVector[transitionNo] = ((Transition) transitionsArray
					.get(transitionNo)).isTimed();
		}
	}

	/**
	 * Creates Priority Vector from current Petri-Net
	 */
	private void createPriorityVector() {
		int transitionSize = transitionsArray.size();

		priorityVector = new int[transitionSize];
		for (int transitionNo = 0; transitionNo < transitionSize; transitionNo++) {
			priorityVector[transitionNo] = ((Transition) transitionsArray
					.get(transitionNo)).getPriority();
		}
	}

	/**
	 * Creates Inhibition Matrix from current Petri-Net
	 */
	private void createInhibitionMatrix() {
		int placeSize = placesArray.size();
		int transitionSize = transitionsArray.size();
		inhibitionMatrix = new PNMatrix(placeSize, transitionSize);

		for (int i = 0; i < inhibitorsArray.size(); i++) {
			InhibitorArc inhibitorArc = (InhibitorArc) inhibitorsArray.get(i);
			if (inhibitorArc != null) {
				PetriNetObject pnObject = inhibitorArc.getSource();
				if (pnObject != null) {
					if (pnObject instanceof Place) {
						Place place = (Place) pnObject;
						pnObject = inhibitorArc.getTarget();
						if (pnObject != null) {
							if (pnObject instanceof Transition) {
								Transition transition = (Transition) pnObject;
								int transitionNo = getListPosition(transition);
								int placeNo = getListPosition(place);
								try {
									inhibitionMatrix.set(placeNo, transitionNo,
											inhibitorArc.getWeight());
								} catch (Exception e) {
									JOptionPane.showMessageDialog(null,
											"Problema a inhibitionMatrix");
									System.out.println("p:" + placeNo + ";t:"
											+ transitionNo + ";w:"
											+ inhibitorArc.getWeight());
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Stores Current Marking
	 */
	public void storeState() {
		int placeSize = placesArray.size();
		markingVectorAnimationStorage = new int[placeSize];
		for (int placeNo = 0; placeNo < placeSize; placeNo++) {
			markingVectorAnimationStorage[placeNo] = ((Place) placesArray
					.get(placeNo)).getCurrentMarking();
		}
	}

	/**
	 * Restores To previous Stored Marking
	 */
	public void restoreState() {
		if (markingVectorAnimationStorage != null) {
			int placeSize = placesArray.size();
			for (int placeNo = 0; placeNo < placeSize; placeNo++) {
				Place place = ((Place) placesArray.get(placeNo));
				if (place != null) {
					place.setCurrentMarking(markingVectorAnimationStorage[placeNo]);
					setChanged();
					notifyObservers(place);
					setMatrixChanged();
				}
			}
		}
	}

	/**
	 * Fire a specified transition, no affect if transtions not enabled
	 * 
	 * @param transition
	 *            Reference of specifiec Transition
	 */
	public void fireTransition(Transition transition) {
		if (transition != null) {
			setEnabledTransitions();
			if (transition.isEnabled() && placesArray != null) {
				int transitionNo = transitionsArray.indexOf(transition);
				for (int placeNo = 0; placeNo < placesArray.size(); placeNo++) {
					((Place) placesArray.get(placeNo))
							.setCurrentMarking((currentMarkingVector[placeNo] + incidenceMatrix
									.get(placeNo, transitionNo)));
				}
			}
		}
		setMatrixChanged();
	}

	/**
	 * Fire a random transition, takes rate (probability) of Transitions into
	 * account
	 */
	public Transition fireRandomTransition() {

		setEnabledTransitions();
		// All the enabled transitions are of the same type:
		// a) all are immediate transitions; or
		// b) all are timed transitions.

		ArrayList enabledTransitions = new ArrayList();
		double rate = 0;
		for (int i = 0; i < transitionsArray.size(); i++) {
			Transition transition = (Transition) transitionsArray.get(i);
			if (transition.isEnabled()) {
				enabledTransitions.add(transition);
				rate += transition.getRate();
			}
		}

		// if there is only one enabled transition, return this transition
		if (enabledTransitions.size() == 1) {
			return (Transition) enabledTransitions.get(0);
		}

		double random = randomNumber.nextDouble();
		double x = 0;
		for (int i = 0; i < enabledTransitions.size(); i++) {
			Transition t = (Transition) enabledTransitions.get(i);

			x += t.getRate() / rate;

			if (random < x) {
				return t;
			}
		}

		// no enabled transition found, so no transition can be fired
		return null;
	}

	/**
	 * This method will fire a random transition, and gives precedence to
	 * immediate transitions before considering "timed" transitions. The "rate"
	 * property of the transition is used as a weighting factor so the
	 * probability of selecting a transition is the rate of that transition
	 * divided by the sum of the weights of the other enabled transitions of its
	 * class. The "rate" property can now be used to give priority among several
	 * enabled, immediate transitions, or when there are no enabled, immediate
	 * transitions to give priority among several enabled, "timed" transitions.
	 * 
	 * Note: in spite of the name "timed" there is no probabilistic rate
	 * calculated -- just a weighting factor among similar transitions.
	 * 
	 * Changed by David Patterson Jan 2, 2006
	 * 
	 * Changed by David Patterson Apr 24, 2007 to clean up problems caused by
	 * fractional rates, and to speed up processing when only one transition of
	 * a kind is enabled.
	 * 
	 * Changed by David Patterson May 10, 2007 to properly handle fractional
	 * weights for immeditate transitions.
	 * 
	 * THe same logic is also used for timed transitions until the exponential
	 * distribution is added. When that happens, the code will only be used for
	 * immediate transitions. / public Transition fireRandomTransition() {
	 * Transition result = null; Transition t; setEnabledTransitions(); // int
	 * transitionsSize =
	 * transitionsArray.size()*transitionsArray.size()*transitionsArray.size();
	 * int transitionNo = 0;
	 * 
	 * double rate = 0.0d; double sumOfImmedWeights = 0.0d; double
	 * sumOfTimedWeights = 0.0d; ArrayList timedTransitions = new ArrayList();
	 * // ArrayList<Transition> ArrayList immedTransitions = new ArrayList(); //
	 * ArrayList<Transition>
	 * 
	 * for(transitionNo = 0 ; transitionNo < transitionsArray.size() ;
	 * transitionNo++){ t = (Transition) transitionsArray.get( transitionNo );
	 * rate = t.getRate(); if ( t.isEnabled()) { if ( t.isTimed() ) { // is it a
	 * timed transition timedTransitions.add( t ); sumOfTimedWeights += rate; }
	 * else { // immediate transition immedTransitions.add( t );
	 * sumOfImmedWeights += rate; } } // end of if isEnabled } // end of for
	 * transitionNo
	 * 
	 * // Now, if there are immediate transitions, pick one // next block
	 * changed by David Patterson to fix bug int count =
	 * immedTransitions.size(); switch ( count ) { case 0: // no immediate
	 * transitions break; // skip out case 1: // only one immediate transition
	 * result = (Transition) immedTransitions.get( 0 ); break; // skip out
	 * default: // several immediate transitions double rval = sumOfImmedWeights
	 * * randomNumber.nextDouble(); for ( int index = 0; index < count; index++
	 * ) { t = (Transition) immedTransitions.get( index ); rval -= t.getRate();
	 * if ( rval <= 0.0d ) { result = t; break; } } } if ( result == null ) { //
	 * no immediate transition found count = timedTransitions.size(); // count
	 * of timed, enabled transitions switch( count ) { case 0: // trouble! No
	 * enabled transition found break; case 1: // only one timed transition
	 * result = ( Transition ) timedTransitions.get( 0 ); break; default: //
	 * several timed transitions -- for now, pick one double rval =
	 * sumOfTimedWeights * randomNumber.nextDouble(); for ( int index = 0; index
	 * < count; index++ ) { t = (Transition) timedTransitions.get( index ); rval
	 * -= t.getRate(); if ( rval <= 0.0d ) { result = t; break; } } } }
	 * 
	 * if ( result == null ) { System.out.println(
	 * "no random transition to fire" ); } else { fireTransition(result);
	 * createCurrentMarkingVector(); } resetEnabledTransitions(); return result;
	 * } // end of method fireRandomTransition
	 */

	public void fireTransitionBackwards(Transition transition) {
		if (transition != null) {
			setEnabledTransitionsBackwards();
			if (transition.isEnabled() && placesArray != null) {
				int transitionNo = transitionsArray.indexOf(transition);
				for (int placeNo = 0; placeNo < placesArray.size(); placeNo++) {
					((Place) placesArray.get(placeNo))
							.setCurrentMarking((currentMarkingVector[placeNo] - incidenceMatrix
									.get(placeNo, transitionNo)));
				}
			}
		}
		setMatrixChanged();
	}

	/*
	 * Method not used * / public void fireRandomTransitionBackwards() {
	 * setEnabledTransitionsBackwards(); int transitionsSize =
	 * transitionsArray.size() * transitionsArray.size() *
	 * transitionsArray.size(); int randomTransitionNumber = 0; Transition
	 * randomTransition = null; do { randomTransitionNumber =
	 * randomNumber.nextInt(transitionsArray.size()); randomTransition =
	 * (Transition)transitionsArray.get(randomTransitionNumber);
	 * transitionsSize--; if(transitionsSize <= 0){ break; } } while(!
	 * randomTransition.isEnabled()); fireTransitionBackwards(randomTransition);
	 * // System.out.println("Random Fired Transition Backwards" +
	 * ((Transition)transitionsArray.get(randonTransition)).getId()); }
	 */

	public void resetEnabledTransitions() {
		for (int i = 0; i < transitionsArray.size(); i++) {
			Transition transition = (Transition) transitionsArray.get(i);
			transition.setEnabled(false);
			setChanged();
			notifyObservers(transition);
		}
	}

	/**
	 * Calculate whether a transition is enabled given a specific marking
	 * 
	 * @param DataLayer
	 *            - the net
	 * @param int[] - the marking
	 * @param int - the specific transition to test for enabled status
	 * @return boolean - an array of booleans specifying which transitions are
	 *         enabled in the specified marking
	 */
	public boolean getTransitionEnabledStatus(int[] marking, int transition) {
		int transCount = this.getTransitionsCount();
		int placeCount = this.getPlacesCount();
		boolean[] result = new boolean[transCount];
		int[][] CMinus = this.getBackwardsIncidenceMatrix();

		// initialise matrix to true
		for (int k = 0; k < transCount; k++) {
			result[k] = true;
		}
		for (int i = 0; i < transCount; i++) {
			for (int j = 0; j < placeCount; j++) {
				if (marking[j] < CMinus[j][i]) {
					result[i] = false;
				}
			}
		}
		return result[transition];
	}

	/**
	 * getTransitionEnabledStatusArray() Calculate which transitions are enabled
	 * given a specific marking.
	 * 
	 * @author Matthew Cook (original code), Nadeem Akharware (optimisation)
	 * @author Pere Bonet added inhibitor arcs, place capacities and transition
	 *         priorities
	 * @param int[] the marking
	 * @return boolean[] an array of booleans specifying which transitions are
	 *         enabled in the specified marking
	 */
	public boolean[] getTransitionEnabledStatusArray(int[] marking) {
		return getTransitionEnabledStatusArray(this.getTransitions(), marking,
				this.getBackwardsIncidenceMatrix(),
				this.getForwardsIncidenceMatrix(), this.getInhibitionMatrix(),
				this.getCapacityVector(), this.getPlacesCount(),
				this.getTransitionsCount());
	}

	/**
	 * Determines whether all transitions are enabled and sets the correct value
	 * of the enabled boolean
	 */
	public void setEnabledTransitionsBackwards() {

		if (currentMarkingVectorChanged) {
			createMatrixes();
		}

		boolean[] enabledTransitions = getTransitionEnabledStatusArray(
				this.getTransitions(), this.getCurrentMarkingVector(),
				this.getForwardsIncidenceMatrix(),
				this.getBackwardsIncidenceMatrix(), this.getInhibitionMatrix(),
				this.getCapacityVector(), this.getPlacesCount(),
				this.getTransitionsCount());

		for (int i = 0; i < enabledTransitions.length; i++) {
			Transition transition = (Transition) transitionsArray.get(i);
			if (enabledTransitions[i] != transition.isEnabled()) {
				transition.setEnabled(enabledTransitions[i]);
				setChanged();
				notifyObservers(transition);
			}
		}
	}

	/**
	 * Determines whether all transitions are enabled and sets the correct value
	 * of the enabled boolean
	 */
	public void setEnabledTransitions() {

		if (currentMarkingVectorChanged) {
			createMatrixes();
		}

		boolean[] enabledTransitions = getTransitionEnabledStatusArray(
				this.getTransitions(), this.getCurrentMarkingVector(),
				this.getBackwardsIncidenceMatrix(),
				this.getForwardsIncidenceMatrix(), this.getInhibitionMatrix(),
				this.getCapacityVector(), this.getPlacesCount(),
				this.getTransitionsCount());

		for (int i = 0; i < enabledTransitions.length; i++) {
			Transition transition = (Transition) transitionsArray.get(i);
			if (enabledTransitions[i] != transition.isEnabled()) {
				transition.setEnabled(enabledTransitions[i]);
				setChanged();
				notifyObservers(transition);
			}
		}
	}

	/**
	 * getTransitionEnabledStatusArray() Calculate which transitions are enabled
	 * given a specific marking.
	 * 
	 * @author Matthew Cook (original code), Nadeem Akharware (optimisation)
	 * @author Pere Bonet added inhibitor arcs, place capacities and transition
	 *         priorities
	 * @param int[] the marking
	 * @return boolean[] an array of booleans specifying which transitions are
	 *         enabled in the specified marking
	 */
	private boolean[] getTransitionEnabledStatusArray(
			final Transition[] transArray, final int[] marking,
			final int[][] CMinus, final int[][] CPlus,
			final int[][] inhibition, final int capacities[],
			final int placeCount, final int transitionCount) {

		boolean[] result = new boolean[transitionCount];
		boolean hasTimed = false;
		boolean hasImmediate = false;

		int maxPriority = 0;

		for (int i = 0; i < transitionCount; i++) {
			result[i] = true; // inicialitzam a enabled
			for (int j = 0; j < placeCount; j++) {
				if ((marking[j] < CMinus[j][i]) && (marking[j] != -1)) {
					result[i] = false;
					break;
				}

				// capacities
				if ((capacities[j] > 0)
						&& (marking[j] + CPlus[j][i] - CMinus[j][i] > capacities[j])) {
					// firing this transition would break a capacity restriction
					// so
					// the transition is not enabled
					result[i] = false;
					break;
				}

				// inhibitor arcs
				if (inhibition[j][i] > 0 && marking[j] >= inhibition[j][i]) {
					// an inhibitor arc prevents the firing of this transition
					// so
					// the transition is not enabled
					result[i] = false;
					break;
				}
			}

			// we look for the highest priority of the enabled transitions
			if (result[i] == true) {
				if (transArray[i].isTimed() == true) {
					hasTimed = true;
				} else {
					hasImmediate = true;
					if (transArray[i].getPriority() > maxPriority) {
						maxPriority = transArray[i].getPriority();
					}
				}
			}

		}

		// Now make sure that if any of the enabled transitions are immediate
		// transitions, only they can fire as this must then be a vanishing
		// state.
		// - disable the immediate transitions with lower priority.
		// - disable all timed transitions if there is an immediate transition
		// enabled.
		for (int i = 0; i < transitionCount; i++) {
			if (!transArray[i].isTimed()
					&& transArray[i].getPriority() < maxPriority) {
				result[i] = false;
			}
			if (hasTimed && hasImmediate) {
				if (transArray[i].isTimed() == true) {
					result[i] = false;
				}
			}
		}

		// print("getTransitionEnabledStatusArray: ",result);//debug
		return result;
	}

	/**
	 * Empty all attributes, turn into empty Petri-Net
	 */
	private void emptyPNML() {
		pnmlName = null;
		placesArray = null;
		transitionsArray = null;
		arcsArray = null;
		labelsArray = null;
		markingParametersArray = null;
		rateParametersArray = null;
		changeArrayList = null;
		initialMarkingVector = null;
		forwardsIncidenceMatrix = null;
		backwardsIncidenceMatrix = null;
		incidenceMatrix = null;
		inhibitionMatrix = null;
		arcsMap = null;
		initializeMatrices();
	}

	/**
	 * Get position of Petri-Net Object in ArrayList of given Petri-Net Object's
	 * type
	 * 
	 * @param pnObject
	 *            PlaceTransitionObject to get the position of
	 * @return Position (-1 if not present) of Petri-Net Object in ArrayList of
	 *         given Petri-Net Object's type
	 */
	public int getListPosition(PetriNetObject pnObject) {

		if (setPetriNetObjectArrayList(pnObject)) {
			return changeArrayList.indexOf(pnObject);
		} else {
			return -1;
		}
	}

	/**
	 * Get a List of all the Place objects in the Petri-Net
	 * 
	 * @return A List of all the Place objects
	 */
	public Place[] getPlaces() {
		Place[] returnArray = new Place[placesArray.size()];

		for (int i = 0; i < placesArray.size(); i++) {
			returnArray[i] = (Place) placesArray.get(i);
		}
		return returnArray;
	}

	public int getPlacesCount() {
		if (placesArray == null) {
			return 0;
		} else {
			return placesArray.size();
		}
	}

	/* wjk added 03/10/2007 */
	/**
	 * Get the current marking of the Petri net
	 * 
	 * @return The current marking of the Petri net
	 */
	public int[] getMarking() {
		int[] result = new int[placesArray.size()];

		for (int i = 0; i < placesArray.size(); i++) {
			result[i] = ((Place) placesArray.get(i)).getCurrentMarking();
		}
		return result;
	}

	/**
	 * Get a List of all the net-level NameLabel objects in the Petri-Net
	 * 
	 * @return A List of all the net-level (as opposed to element-specific)
	 *         label objects
	 */
	public AnnotationNote[] getLabels() {
		AnnotationNote[] returnArray = new AnnotationNote[labelsArray.size()];

		for (int i = 0; i < labelsArray.size(); i++) {
			returnArray[i] = (AnnotationNote) labelsArray.get(i);
		}
		return returnArray;
	}

	/**
	 * Get a List of all the marking Parameter objects in the Petri-Net
	 * 
	 * @return A List of all the marking Parameter objects
	 */
	public MarkingParameter[] getMarkingParameters() {
		MarkingParameter[] returnArray = new MarkingParameter[markingParametersArray
				.size()];

		for (int i = 0; i < markingParametersArray.size(); i++) {
			returnArray[i] = (MarkingParameter) markingParametersArray.get(i);
		}
		return returnArray;
	}

	/**
	 * Get a List of all the marking Parameter objects in the Petri-Net
	 * 
	 * @return A List of all the marking Parameter objects
	 */
	public RateParameter[] getRateParameters() {
		RateParameter[] returnArray = new RateParameter[rateParametersArray
				.size()];

		for (int i = 0; i < rateParametersArray.size(); i++) {
			returnArray[i] = (RateParameter) rateParametersArray.get(i);
		}
		return returnArray;
	}

	/**
	 * Get an List of all the Transition objects in the Petri-Net
	 * 
	 * @return An List of all the Transition objects
	 */
	public Transition[] getTransitions() {
		Transition[] returnArray = new Transition[transitionsArray.size()];

		for (int i = 0; i < transitionsArray.size(); i++) {
			returnArray[i] = (Transition) transitionsArray.get(i);
		}
		return returnArray;
	}

	public int getTransitionsCount() {
		if (transitionsArray == null) {
			return 0;
		} else {
			return transitionsArray.size();
		}
	}

	/**
	 * Get an List of all the Arcs objects in the Petri-Net
	 * 
	 * @return An List of all the Arc objects
	 */
	public Arc[] getArcs() {
		Arc[] returnArray = new Arc[arcsArray.size()];

		for (int i = 0; i < arcsArray.size(); i++) {
			returnArray[i] = (Arc) arcsArray.get(i);
		}
		return returnArray;
	}

	/**
	 * Get an List of all the InhibitorArc objects in the Petri-Net
	 * 
	 * @return An List of all the InhibitorArc objects
	 */
	public InhibitorArc[] getInhibitors() {
		InhibitorArc[] returnArray = new InhibitorArc[inhibitorsArray.size()];

		for (int i = 0; i < inhibitorsArray.size(); i++) {
			returnArray[i] = (InhibitorArc) inhibitorsArray.get(i);
		}
		return returnArray;
	}

	/**
	 * Return the Transition called transitionName from the Petri-Net
	 * 
	 * @param transitionID
	 *            ID of Transition object to return
	 * @return The first Transition object found with a name equal to
	 *         transitionName
	 */
	public Transition getTransitionById(String transitionID) {
		Transition returnTransition = null;

		if (transitionsArray != null) {
			if (transitionID != null) {
				for (int i = 0; i < transitionsArray.size(); i++) {
					if (transitionID
							.equalsIgnoreCase(((Transition) transitionsArray
									.get(i)).getId())) {
						returnTransition = (Transition) transitionsArray.get(i);
					}
				}
			}
		}
		return returnTransition;
	}

	/**
	 * Return the Transition called transitionName from the Petri-Net
	 * 
	 * @param transitionName
	 *            Name of Transition object to return
	 * @return The first Transition object found with a name equal to
	 *         transitionName
	 */
	public Transition getTransitionByName(String transitionName) {
		Transition returnTransition = null;

		if (transitionsArray != null) {
			if (transitionName != null) {
				for (int i = 0; i < transitionsArray.size(); i++) {
					if (transitionName
							.equalsIgnoreCase(((Transition) transitionsArray
									.get(i)).getName())) {
						returnTransition = (Transition) transitionsArray.get(i);
					}
				}
			}
		}
		return returnTransition;
	}

	/**
	 * @author Bingxue used for temporal constraint checking
	 */
	public int getTransitionNoByName(String transitionName) {
		int transition = 0;

		if (transitionsArray != null) {
			if (transitionName != null) {
				for (int i = 0; i < transitionsArray.size(); i++) {
					if (transitionName
							.equalsIgnoreCase(((Transition) transitionsArray
									.get(i)).getName())) {
						transition = i;
					}
				}
			}
		}
		return transition;
	}

	/**
	 * Return the Transition called transitionName from the Petri-Net
	 * 
	 * @param transitionNo
	 *            No of Transition object to return
	 * @return The Transition object
	 */
	public Transition getTransition(int transitionNo) {
		Transition returnTransition = null;

		if (transitionsArray != null) {
			if (transitionNo < transitionsArray.size()) {
				returnTransition = (Transition) transitionsArray
						.get(transitionNo);
			}
		}
		return returnTransition;
	}

	/**
	 * Return the Place called placeName from the Petri-Net
	 * 
	 * @param placeId
	 *            ID of Place object to return
	 * @return The first Place object found with id equal to placeId
	 */
	public Place getPlaceById(String placeID) {
		Place returnPlace = null;

		if (placesArray != null) {
			if (placeID != null) {
				for (int i = 0; i < placesArray.size(); i++) {
					if (placeID.equalsIgnoreCase(((Place) placesArray.get(i))
							.getId())) {
						returnPlace = (Place) placesArray.get(i);
					}
				}
			}
		}
		return returnPlace;
	}

	/**
	 * Return the Place called placeName from the Petri-Net
	 * 
	 * @param placeName
	 *            Name of Place object to return
	 * @return The first Place object found with a name equal to placeName
	 */
	public Place getPlaceByName(String placeName) {
		Place returnPlace = null;

		if (placesArray != null) {
			if (placeName != null) {
				for (int i = 0; i < placesArray.size(); i++) {
					if (placeName.equalsIgnoreCase(((Place) placesArray.get(i))
							.getName())) {
						returnPlace = (Place) placesArray.get(i);
					}
				}
			}
		}
		return returnPlace;
	}

	/**
	 * Return the Place called placeName from the Petri-Net
	 * 
	 * @param placeNo
	 *            No of Place object to return
	 * @return The Place object
	 */
	public Place getPlace(int placeNo) {
		Place returnPlace = null;

		if (placesArray != null) {
			if (placeNo < placesArray.size()) {
				returnPlace = (Place) placesArray.get(placeNo);
			}
		}
		return returnPlace;
	}

	/**
	 * Return the PlaceTransitionObject called ptoName from the Petri-Net
	 * 
	 * @param ptoId
	 *            Id of PlaceTransitionObject object to return
	 * @return The first Arc PlaceTransitionObject found with a name equal to
	 *         ptoName
	 */
	public PlaceTransitionObject getPlaceTransitionObject(String ptoId) {
		if (ptoId != null) {
			if (getPlaceById(ptoId) != null) {
				return getPlaceById(ptoId);
			} else if (getTransitionById(ptoId) != null) {
				return getTransitionById(ptoId);
			}
		}
		return null;
	}

	/**
	 * Return the Forward Incidence Matrix for the Petri-Net
	 * 
	 * @return The Forward Incidence Matrix for the Petri-Net
	 */
	public int[][] getForwardsIncidenceMatrix() {
		if (forwardsIncidenceMatrix == null
				|| forwardsIncidenceMatrix.matrixChanged) {
			createForwardIncidenceMatrix();
		}
		return (forwardsIncidenceMatrix != null ? forwardsIncidenceMatrix
				.getArrayCopy() : null);
	}

	/**
	 * Return the Backward Incidence Matrix for the Petri-Net
	 * 
	 * @return The Backward Incidence Matrix for the Petri-Net
	 */
	public int[][] getBackwardsIncidenceMatrix() {
		if (backwardsIncidenceMatrix == null
				|| backwardsIncidenceMatrix.matrixChanged) {
			createBackwardsIncidenceMatrix();
		}
		return (backwardsIncidenceMatrix != null ? backwardsIncidenceMatrix
				.getArrayCopy() : null);
	}

	/**
	 * Return the Incidence Matrix for the Petri-Net
	 * 
	 * @return The Incidence Matrix for the Petri-Net
	 */
	public int[][] getIncidenceMatrix() {
		if (incidenceMatrix == null || incidenceMatrix.matrixChanged) {
			createIncidenceMatrix();
		}
		return (incidenceMatrix != null ? incidenceMatrix.getArrayCopy() : null);
	}

	/**
	 * Return the Incidence Matrix for the Petri-Net
	 * 
	 * @return The Incidence Matrix for the Petri-Net
	 */
	public int[][] getInhibitionMatrix() {
		if (inhibitionMatrix == null || inhibitionMatrix.matrixChanged) {
			createInhibitionMatrix();
		}
		return (inhibitionMatrix != null ? inhibitionMatrix.getArrayCopy()
				: null);
	}

	/**
	 * Return the Initial Marking Vector for the Petri-Net
	 * 
	 * @return The Initial Marking Vector for the Petri-Net
	 */
	public int[] getInitialMarkingVector() {
		if (initialMarkingVectorChanged) {
			createInitialMarkingVector();
		}
		return initialMarkingVector;
	}

	/**
	 * Return the Initial Marking Vector for the Petri-Net
	 * 
	 * @return The Initial Marking Vector for the Petri-Net
	 */
	public int[] getCurrentMarkingVector() {
		if (currentMarkingVectorChanged) {
			createCurrentMarkingVector();
		}
		return currentMarkingVector;
	}

	/**
	 * Return the capacity Matrix for the Petri-Net
	 * 
	 * @return The capacity Matrix for the Petri-Net
	 */
	public int[] getCapacityVector() {
		createCapacityVector();
		return capacityVector;
	}

	/**
	 * Return the capacity Matrix for the Petri-Net
	 * 
	 * @return The capacity Matrix for the Petri-Net
	 */
	public int[] getPriorityVector() {
		createPriorityVector();
		return priorityVector;
	}

	/**
	 * Return the capacity Matrix for the Petri-Net
	 * 
	 * @return The capacity Matrix for the Petri-Net
	 */
	public boolean[] getTimedVector() {
		createTimedVector();
		return timedVector;
	}

	private void setMatrixChanged() {
		if (forwardsIncidenceMatrix != null) {
			forwardsIncidenceMatrix.matrixChanged = true;
		}
		if (backwardsIncidenceMatrix != null) {
			backwardsIncidenceMatrix.matrixChanged = true;
		}
		if (incidenceMatrix != null) {
			incidenceMatrix.matrixChanged = true;
		}
		if (inhibitionMatrix != null) {
			inhibitionMatrix.matrixChanged = true;
		}
		initialMarkingVectorChanged = true;
		currentMarkingVectorChanged = true;
	}

	/**
	 * Create model from transformed PNML file
	 * 
	 * @author Ben Kirby, 10 Feb 2007
	 * @param filename
	 *            URI location of PNML
	 * 
	 * @author Edwin Chung This code is modified so that dataLayer objects can
	 *         be created outside the GUI
	 */
	public void createFromPNML(Document PNMLDoc) {
		emptyPNML();
		Element element = null;
		Node node = null;
		NodeList nodeList = null;

		try {
			nodeList = PNMLDoc.getDocumentElement().getChildNodes();
			if (CreateGui.getApp() != null) {
				// Notifies used to indicate new instances.
				CreateGui.getApp().setMode(Pipe.CREATING);
			}

			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);

				if (node instanceof Element) {
					element = (Element) node;
					if ("labels".equals(element.getNodeName())) {
						addAnnotation(createAnnotation(element));
					} else if ("definition".equals(element.getNodeName())) {
						Note note = createParameter(element);
						if (note instanceof MarkingParameter) {
							addAnnotation((MarkingParameter) note);
						} else if (note instanceof RateParameter) {
							addAnnotation((RateParameter) note);
						}
					} else if ("place".equals(element.getNodeName())) {
						addPlace(createPlace(element));
					} else if ("transition".equals(element.getNodeName())) {
						addTransition(createTransition(element));
					} else if ("arc".equals(element.getNodeName())) {
						Arc newArc = createArc(element);
						if (newArc instanceof InhibitorArc) {
							addArc((InhibitorArc) newArc);
						} else {
							addArc((NormalArc) newArc);
							checkForInverseArc((NormalArc) newArc);
						}
					} else if ("stategroup".equals(element.getNodeName())) {
						addStateGroup(createStateGroup(element));
					} else {
						System.out.println("!" + element.getNodeName());
					}
				}
			}

			if (CreateGui.getApp() != null) {
				CreateGui.getApp().restoreMode();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void createFromPNML2(Document PNMLDoc) {

		Element element = null;
		Node node = null;
		NodeList nodeList = null;

		try {
			nodeList = PNMLDoc.getDocumentElement().getChildNodes();
			if (CreateGui.getApp() != null) {
				// Notifies used to indicate new instances.
				CreateGui.getApp().setMode(Pipe.CREATING);
			}

			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);

				if (node instanceof Element) {
					element = (Element) node;
					if ("labels".equals(element.getNodeName())) {
						addAnnotation(createAnnotation(element));
					} else if ("definition".equals(element.getNodeName())) {
						Note note = createParameter(element);
						if (note instanceof MarkingParameter) {
							addAnnotation((MarkingParameter) note);
						} else if (note instanceof RateParameter) {
							addAnnotation((RateParameter) note);
						}
					} else if ("place".equals(element.getNodeName())) {
						addPlace(createPlace(element));
					} else if ("transition".equals(element.getNodeName())) {
						addTransition(createTransition(element));
					} else if ("arc".equals(element.getNodeName())) {
						Arc newArc = createArc(element);
						if (newArc instanceof InhibitorArc) {
							addArc((InhibitorArc) newArc);
						} else {
							addArc((NormalArc) newArc);
							checkForInverseArc((NormalArc) newArc);
						}
					} else if ("stategroup".equals(element.getNodeName())) {
						addStateGroup(createStateGroup(element));
					} else {
						System.out.println("!" + element.getNodeName());
					}
				}
			}

			if (CreateGui.getApp() != null) {
				CreateGui.getApp().restoreMode();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a StateGroup object from a DOM element
	 * 
	 * @param inputStateGroupElement
	 *            input state group DOM Element
	 * @return StateGroup Object
	 */
	private StateGroup createStateGroup(Element inputStateGroupElement) {
		// Create the state group with name and id
		String id = inputStateGroupElement.getAttribute("id");
		String name = inputStateGroupElement.getAttribute("name");
		StateGroup newGroup = new StateGroup(id, name);

		Node node = null;
		NodeList nodelist = null;
		StringTokenizer tokeniser;
		nodelist = inputStateGroupElement.getChildNodes();

		// If this state group contains states then add them
		if (nodelist.getLength() > 0) {
			for (int i = 1; i < nodelist.getLength() - 1; i++) {
				node = nodelist.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;
					if ("statecondition".equals(element.getNodeName())) {
						// Loads the condition in the form "P0 > 4"
						String condition = element.getAttribute("value");
						// Now we tokenise the elements of the condition
						// (i.e. "P0" ">" "4") to create a state
						tokeniser = new StringTokenizer(condition);
						newGroup.addState(tokeniser.nextToken(),
								tokeniser.nextToken(), tokeniser.nextToken());
					}
				}
			}
		}
		return newGroup;
	}

	public StateGroup[] getStateGroups() {
		StateGroup[] returnArray = new StateGroup[stateGroups.size()];
		for (int i = 0; i < stateGroups.size(); i++) {
			returnArray[i] = (StateGroup) stateGroups.get(i);
		}
		return returnArray;
	}

	/**
	 * Create model from transformed PNML file
	 */
	public void createFromPNML(Document PNMLDoc, ProgressBar progressBar) {
		emptyPNML();
		Element element = null;
		Node node = null;
		NodeList nodeList = null;

		try {
			nodeList = PNMLDoc.getDocumentElement().getChildNodes();
			if (CreateGui.getApp() != null) {
				// Notifies used to indicate new instances.
				CreateGui.getApp().setMode(Pipe.CREATING);
			}
			progressBar.setProgressBar(nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);

				if (node instanceof Element) {
					element = (Element) node;
					if ("labels".equals(element.getNodeName())) {
						addAnnotation(createAnnotation(element));
					} else if ("definition".equals(element.getNodeName())) {
						Note note = createParameter(element);
						if (note instanceof MarkingParameter) {
							addAnnotation((MarkingParameter) note);
						} else if (note instanceof RateParameter) {
							addAnnotation((RateParameter) note);
						}
					} else if ("place".equals(element.getNodeName())) {
						addPlace(createPlace(element));
					} else if ("transition".equals(element.getNodeName())) {
						addTransition(createTransition(element));
					} else if ("arc".equals(element.getNodeName())) {
						Arc newArc = createArc(element);
						if (newArc instanceof InhibitorArc) {
							addArc((InhibitorArc) newArc);
						} else {
							addArc((NormalArc) newArc);
							checkForInverseArc((NormalArc) newArc);
						}
					} else {
						System.out.println("!" + element.getNodeName());
					}
				}
				progressBar.step();
			}

			if (CreateGui.getApp() != null) {
				CreateGui.getApp().restoreMode();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return a URI for the PNML file for the Petri-Net
	 * 
	 * @return A DOM for the Petri-Net
	 */
	public String getURI() {
		return pnmlName;
	}

	/** prints out a brief representation of the dataLayer object */
	public void print() {
		System.out.println("No of Places = " + placesArray.size() + "\"");
		System.out.println("No of Transitions = " + transitionsArray.size()
				+ "\"");
		System.out.println("No of Arcs = " + arcsArray.size() + "\"");
		System.out.println("No of Labels = " + labelsArray.size()
				+ "\" (Model View Controller Design Pattern)");
	}

	public boolean existsMarkingParameter(String name) {
		return markingParameterHashSet.contains(name);
	}

	public boolean existsRateParameter(String name) {
		return rateParameterHashSet.contains(name);
	}

	public boolean changeRateParameter(String oldName, String newName) {
		if (rateParameterHashSet.contains(newName)) {
			return false;
		}
		rateParameterHashSet.remove(oldName);
		rateParameterHashSet.add(newName);
		return true;
	}

	public boolean changeMarkingParameter(String oldName, String newName) {
		if (markingParameterHashSet.contains(newName)) {
			return false;
		}
		markingParameterHashSet.remove(oldName);
		markingParameterHashSet.add(newName);
		return true;
	}

	/**
	 * See if the supplied net has any timed transitions.
	 * 
	 * @param DataLayer
	 * @return boolean
	 * @author Matthew
	 */
	public boolean hasTimedTransitions() {
		Transition[] transitions = this.getTransitions();
		int transCount = transitions.length;

		for (int i = 0; i < transCount; i++) {
			if (transitions[i].isTimed() == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * See if the net has any timed transitions.
	 * 
	 * @return boolean
	 * @author Matthew
	 */
	public boolean hasImmediateTransitions() {
		Transition[] transitions = this.getTransitions();
		int transCount = transitions.length;

		for (int i = 0; i < transCount; i++) {
			if (transitions[i].isTimed() == false) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Work out if a specified marking describes a tangible state. A state is
	 * either tangible (all enabled transitions are timed) or vanishing (there
	 * exists at least one enabled state that is transient, i.e. untimed). If an
	 * immediate transition exists, it will automatically fire before a timed
	 * transition.
	 * 
	 * @param DataLayer
	 *            - the net to be tested
	 * @param int[] - the marking of the net to be tested
	 * @return boolean - is it tangible or not
	 */
	public boolean isTangibleState(int[] marking) {
		Transition[] trans = this.getTransitions();
		int numTrans = trans.length;
		boolean hasTimed = false;
		boolean hasImmediate = false;

		for (int i = 0; i < numTrans; i++) {
			if (this.getTransitionEnabledStatus(marking, i) == true) {
				if (trans[i].isTimed() == true) {
					// If any immediate transtions exist, the state is vanishing
					// as they will fire immediately
					hasTimed = true;
				} else if (trans[i].isTimed() != true) {
					hasImmediate = true;
				}
			}
		}
		return (hasTimed == true && hasImmediate == false);
	}

	private void checkForInverseArc(NormalArc newArc) {
		if(newArc.getSource() != null){
			Iterator iterator = newArc.getSource().getConnectToIterator();

			Arc anArc;
			while (iterator.hasNext()) {
				anArc = (Arc) iterator.next();
				if (anArc.getTarget() == newArc.getSource()
						&& anArc.getSource() == newArc.getTarget()) {
					if (anArc.getClass() == NormalArc.class) {
						if (!newArc.hasInverse()) {
							((NormalArc) anArc).setInverse(newArc, Pipe.JOIN_ARCS);
						}
					}
				}
			}
		}
	}

	public static String getTransitionName(int i) {
		return ((Transition) transitionsArray.get(i)).getName();
	}

	// 金龙 添加两个函数来得到数组中第i个transition的starttime和endtime。
	public static double getTransitionStarttime(int i) {
		return ((Transition) transitionsArray.get(i)).getStarttime();
	}

	public static double getTransitionEndtime(int i) {
		return ((Transition) transitionsArray.get(i)).getEndtime();
	}

	public static int getTransitionSize() {
		return (transitionsArray.size());
	}

	// 金龙 修改结束
	// 冰雪
	public static int getTransitionStartTime(int i) {
		return (int) ((Transition) transitionsArray.get(i)).getStarttime();

	}

	public static int getTransitionEndTime(int i) {
		return (int) ((Transition) transitionsArray.get(i)).getEndtime();
	}

	//
	public ArrayList getTransitionsArray() {
		return transitionsArray;
	}

	/**
	 * 
	 * 
	 * @param i
	 * @param A
	 * @return
	 */

	public boolean existsNo(int i, ArrayList<String> A) {

		int j;
		boolean flag = false;
		for (j = 0; j < A.size(); j++) {
			if (Integer.toString(i).equals(A.get(j))) {
				flag = true;
				break;
			}
		}
		return flag;

	}

	/**
	 * get0Row used for getSequenceQ
	 * 
	 * @author Bingxue
	 * @param P
	 *            [] used for storing the placeNo already zero rows
	 */
	public int getZeroRow(int[][] A, ArrayList<String> P) {
		int i = 0;
		for (i = 0; i < A.length; i++) {
			int sum = 0;
			for (int j = 0; j < A[0].length; j++) {
				sum += A[i][j];
			}
			if (sum == 0 && !existsNo(i, P)) {
				break;
			}
		}
		return i;
	}

	public ArrayList getZeroRows(int[][] A, ArrayList<String> P) {
		ArrayList<String> ZeroRows = new ArrayList<String>();
		int i = 0;
		for (i = 0; i < A.length; i++) {
			ZeroRows.add(Integer.toString(getZeroRow(A, P)));
			P.add(Integer.toString(getZeroRow(A, P)));
		}
		return ZeroRows;

	}

	/**
	 * used for getSequenceQ()
	 * 
	 * @param placeNo
	 * 
	 */

	public void setZeroRow(int[][] A, int placeNo) {
		for (int i = 0; i < A[0].length; i++)
			A[placeNo][i] = 0;

	}

	/**
	 * @author Bingxue
	 * @param int [][] A
	 * @param int Q[] used for storing recorded transition queue
	 * @return allZeroColumnNo
	 */

	public int getZeroColumn(int[][] A, ArrayList<String> Q) {
		int i;

		for (i = 0; i < A[0].length; i++) {
			int sum = 0;
			for (int j = 0; j < A.length; j++) {
				sum += A[j][i];
			}
			if (sum == 0 && !existsNo(i, Q)) {
				break;
			}
		}

		return i;
	}

	/**
	 * 
	 * 
	 * @return
	 */

	public void SetZeroColumn(int[][] A, int transNo) {
		for (int i = 0; i < A.length; i++) {
			A[i][transNo] = 0;
		}

	}

	public boolean Exists(int[] A, int b) {
		boolean flag = false;
		for (int m = 0; m < A.length; m++) {
			if (A[m] == b) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	// 验证是否存在资源约束 且返回资源约束的对数
	public int ExistsRCTranstions(int[][] A, int[][] B) {

		int sumA[] = new int[placesArray.size()];
		int sumB[] = new int[placesArray.size()];
		int count = 0;

		// 求行和
		for (int i = 0; i < A.length; i++) {
			int sum1 = 0;
			int sum2 = 0;
			for (int j = 0; j < A[0].length; j++) {
				sum1 += A[i][j];
				sum2 += B[i][j];
			}
			sumA[i] = sum1;
			sumB[i] = sum2;

		}

		// 比较行和
		for (int k = 0; k < A.length; k++) {
			if (sumA[k] == sumB[k])
				if (sumA[k] == 2) {
					count++;
				}

		}
		return count;

	}

	// 返回有资源约束的变迁 ，并返回有资源冲突的库所号
	public int[][] getRCTranstions(int[][] A, int[][] B) {

		int sumA[] = new int[placesArray.size()];
		int sumB[] = new int[placesArray.size()];
		int count = 0;
		int p[];
		int result[][];

		// 求行和
		for (int i = 0; i < A.length; i++) {
			int sum1 = 0;
			int sum2 = 0;
			for (int j = 0; j < A[0].length; j++) {
				sum1 += A[i][j];
				sum2 += B[i][j];
			}
			sumA[i] = sum1;
			sumB[i] = sum2;

		}

		// 比较行和
		for (int k = 0; k < A.length; k++) {
			if (sumA[k] == sumB[k])
				if (sumA[k] == 2) {
					count++;
				}

		}
		p = new int[count];
		result = new int[count][2];

		// 装载存在资源冲突的库所号
		int k = 0;
		for (int m = 0; m < count; m++) {

			while (k < A.length) {
				if (sumA[k] == sumB[k])
					if (sumA[k] == 2) {
						p[m] = k;
						k++;
						break;
					}
				k++;
			}
			/*
			 * for (int k = 0 ; k < A.length ; k ++){ if(sumA[k] == sumB[k])
			 * if(sumA[k] == 2){ p[m] = k;
			 * 
			 * }
			 * 
			 * 
			 * }
			 */
		}
		// 装载存在资源冲突的变迁号
		for (int m = 0; m < count; m++) {
			int i = 0;

			for (int n = 0; n < 2; n++) {
				int pN = p[m];
				while (i < A[0].length) {
					if (A[pN][i] == 1) {
						result[m][n] = i;
						i++;
						break;

					}
					i++;
				}
			}
		}
		return result;

	}

	public int[] getRcPlaces(int[][] A, int[][] B) {
		int sumA[] = new int[placesArray.size()];
		int sumB[] = new int[placesArray.size()];
		int count = 0;
		int[] p;

		for (int i = 0; i < A.length; i++) {
			int sum1 = 0;
			int sum2 = 0;
			for (int j = 0; j < A[0].length; j++) {
				sum1 += A[i][j];
				sum2 += B[i][j];
			}
			sumA[i] = sum1;
			sumB[i] = sum2;

		}

		// 比较行和
		for (int k = 0; k < A.length; k++) {
			if (sumA[k] == sumB[k])
				if (sumA[k] == 2) {
					count++;
				}

		}
		p = new int[count];
		// result = new int [count][2];

		// 装载存在资源冲突的库所号
		int k = 0;
		for (int m = 0; m < count; m++) {

			while (k < A.length) {
				if (sumA[k] == sumB[k])
					if (sumA[k] == 2) {
						p[m] = k;
						k++;
						break;
					}
				k++;
			}

		}
		return p;

	}

	// 使用之前测试是否包含资源冲突
	public void getNonRcMatrix(int[][] A, int[][] B, int[][] Anew, int[][] Bnew) {
		// int [][] result= null;
		int[] p;
		if (this.ExistsRCTranstions(A, B) == 0) {

			// System.out.print("There's no Constraint");
		} else {
			p = this.getRcPlaces(A, B);
			int[] NonRcPlaces = new int[placesArray.size() - p.length];
			ArrayList<String> hi = new ArrayList<String>(NonRcPlaces.length);

			int i = 0;
			while (i < placesArray.size()) {
				if (!Exists(p, i)) {
					hi.add(String.valueOf(i));
					i++;
				} else {
					i++;
					continue;

				}
			}
			int m = hi.size();
			for (int j = 0; j < hi.size(); j++) {
				NonRcPlaces[j] = Integer.valueOf(hi.get(j));
			}
			int len = NonRcPlaces.length;

			// Anew = new int[len][A[0].length];
			// Bnew = new int[len][B[0].length];
			for (int j = 0; j < len; j++) {
				int PlaceNo = NonRcPlaces[j];
				for (int n = 0; n < A[0].length; n++) {
					Anew[j][n] = A[PlaceNo][n];
					Bnew[j][n] = B[PlaceNo][n];
				}
			}

		}
		return;
		// return result;

	}

	// ----------------Get NonRcPlaces-----------
	public ArrayList<String> getNonRcPlacesNames() {
		int[] p = this.getRcPlaces(f11, f22);
		ArrayList<String> hi = new ArrayList<String>(placesArray.size()
				- p.length);

		int i = 0;
		while (i < placesArray.size()) {
			if (!Exists(p, i)) {
				hi.add(this.getPlace(i).getName());
				i++;
			} else {
				i++;
				continue;

			}
		}
		return hi;

	}

	public ArrayList<String> getAllPlacesNames() {
		ArrayList<String> all = new ArrayList<String>(placesArray.size());
		for (int i = 0; i < placesArray.size(); i++) {
			all.add(getPlace(i).getName());
		}
		return all;
	}

	// 这张map主要维护在剪切掉冲突库所之后的placeNo与实际的PlaceNo之间的映射
	HashMap<Integer, Integer> RealPlaces = null;

	public void getMapofPlaces() {
		ArrayList<String> non = this.getNonRcPlacesNames();
		ArrayList<String> all = this.getAllPlacesNames();
		for (int i = 0; i < non.size(); i++) {
			for (int j = i; j < all.size(); j++) {
				if (non.get(i).equalsIgnoreCase(all.get(j))) {
					RealPlaces.put(i, j);
					break;
				}
			}
		}
	}

	// 根据新的placeNo给出
	public int getRealPlaceNo(int key) {
		return RealPlaces.get(key).intValue();

	}

	public int[] getPrePlacesList(int transition) {

		// PNMatrix FB =
		// backwardsIncidenceMatrix.constructWithCopy(getBackwardsIncidenceMatrix());
		// PNMatrix FF =
		// forwardsIncidenceMatrix.constructWithCopy(getForwardsIncidenceMatrix());
		int[][] f11 = FB.getArray();
		int[][] f22 = FF.getArray();

		int[][] f1;
		int[][] f2;
		int res[];
		int count = this.ExistsRCTranstions(f11, f22);
		if (count != 0) {
			f1 = new int[f11.length - count][f11[0].length];
			f2 = new int[f11.length - count][f11[0].length];
			this.getNonRcMatrix(f11, f22, f1, f2);
		} else {
			f1 = f11;
			f2 = f22;
		}
		int j = 0;
		for (int i = 0; i < f1.length; i++) {
			if (f1[i][transition] == 1) {
				j++;
			}
		}
		res = new int[j];
		j = 0;
		for (int i = 0; i < f1.length; i++) {
			if (f1[i][transition] == 1) {
				res[j++] = i;
			}
		}
		return res;
	}

	public int[] getPosPlacesList(int transition) {

		// PNMatrix FB =
		// backwardsIncidenceMatrix.constructWithCopy(getBackwardsIncidenceMatrix());
		// PNMatrix FF =
		// forwardsIncidenceMatrix.constructWithCopy(getForwardsIncidenceMatrix());
		int[][] f11 = FB.getArray();
		int[][] f22 = FF.getArray();

		int[][] f1;
		int[][] f2;
		int res[];
		int count = this.ExistsRCTranstions(f11, f22);
		if (count != 0) {
			f1 = new int[f11.length - count][f11[0].length];
			f2 = new int[f11.length - count][f11[0].length];
			this.getNonRcMatrix(f11, f22, f1, f2);
		} else {
			f1 = f11;
			f2 = f22;
		}
		int j = 0;
		for (int i = 0; i < f2.length; i++) {
			if (f2[i][transition] == 1) {
				j++;
			}
		}
		res = new int[j];
		j = 0;
		for (int i = 0; i < f2.length; i++) {
			if (f2[i][transition] == 1) {
				res[j++] = i;
			}
		}
		return res;
	}

	private int[][] SP1;
	private int[][] SP2;

	public void createSPMatrix() {
		PNMatrix FB = backwardsIncidenceMatrix
				.constructWithCopy(getBackwardsIncidenceMatrix());
		PNMatrix FF = forwardsIncidenceMatrix
				.constructWithCopy(getForwardsIncidenceMatrix());
		int[][] f11 = FB.getArray();
		int[][] f22 = FF.getArray();

		int count = this.ExistsRCTranstions(f11, f22);
		if (count != 0) {
			SP1 = new int[f11.length - count][f11[0].length];
			SP2 = new int[f11.length - count][f11[0].length];
			this.getNonRcMatrix(f11, f22, SP1, SP2);
		} else {
			SP1 = f11;
			SP2 = f22;
		}

	}

	public int[][] getSP1() {
		if (SP1 == null)
			this.createSPMatrix();
		return this.SP1;
	}

	public int[][] getSP2() {
		if (SP1 == null)
			this.createSPMatrix();
		return this.SP2;
	}

	public int getProcessNo() {
		// int [][] SP1Copy = Arrays.copyOf(SP1, SP1.length);
		// int [][] SP2Copy = Arrays.copyOf(SP2, SP2.length);
		int[][] SP1 = this.getSP1();
		ArrayList<String> R = new ArrayList<String>(4);
		while (this.getZeroRow(SP1, R) != SP1.length) {
			int PlaceNo = getZeroRow(SP1, R);
			R.add(Integer.toString(PlaceNo));
		}
		return R.size();

	}

	public int[] getSourcePlaces() {
		int C = getProcessNo();
		int S[] = new int[C];
		int[][] SP2 = this.getSP2();
		ArrayList<String> R = new ArrayList<String>(4);
		int j = 0;
		while (getZeroRow(this.getSP2(), R) != SP2.length) {
			int PlaceNo = getZeroRow(this.getSP2(), R);
			R.add(Integer.toString(PlaceNo));
			S[j++] = PlaceNo;
		}
		return S;
	}

	public int[] getDrainPlaces() {
		int C = getProcessNo();
		int D[] = new int[C];
		int[][] SP1 = this.getSP1();
		ArrayList<String> R = new ArrayList<String>(4);
		int j = 0;
		while (getZeroRow(SP1, R) != SP1.length) {
			int PlaceNo = getZeroRow(SP1, R);
			R.add(Integer.toString(PlaceNo));
			D[j++] = PlaceNo;
		}
		return D;

	}

	/**
	 * DON'T DELETE RIGHT NOW //--------------------GET SEQUENCE
	 * Q------------------------ public ArrayList<String> getMultiSequence(){
	 * PNMatrix FB =
	 * backwardsIncidenceMatrix.constructWithCopy(getBackwardsIncidenceMatrix
	 * ()); PNMatrix FF =
	 * forwardsIncidenceMatrix.constructWithCopy(getForwardsIncidenceMatrix());
	 * int [][] f1 = FB.getArray(); int [][] f2 = FF.getArray();
	 * this.getSPInit(); ArrayList<ArrayList<String>> pros = new
	 * ArrayList<ArrayList<String>>(ProcessNo); ArrayList<String> Q = new
	 * ArrayList<String>(transitionsArray.size()); ArrayList<String> P = new
	 * ArrayList<String>(placesArray.size()+ProcessNo); for(int i = 0 ; i <
	 * Sources.length ; i++){ P.add(Integer.toString(Sources[i])); }
	 * while(Q.size() < f1[0].length){
	 * 
	 * }
	 * 
	 * 
	 * }
	 */
	private class Process {
		ArrayList<String> Process = new ArrayList<String>(10);

		public Process(ArrayList<String> _Process) {
			Process = _Process;
		}

		public Process() {

		}

		public ArrayList<String> getProcess() {
			return Process;
		}
	}
	public ArrayList<String> tolist(int [] D){
		ArrayList<String> hi = new ArrayList<String>(D.length);
		for(int i = 0 ; i < D.length ; i++){
			hi.add(Integer.toString(D[i]));
		}
		return hi;
	}

	public ArrayList<String> getMultiSequence() {
		Queue pqueue = new Queue();
		Process process = new Process();

		PNMatrix FB = backwardsIncidenceMatrix
				.constructWithCopy(getBackwardsIncidenceMatrix());
		PNMatrix FF = forwardsIncidenceMatrix
				.constructWithCopy(getForwardsIncidenceMatrix());
		int[][] f1 = FB.getArray();
		int[][] f2 = FF.getArray();
		
		int [] D = new int[2];
			D = this.getDrainPlaces();
		/*
		if (Drains.length == 0) {
			this.getSPInit();
		}
		int[] D = new int[Drains.length];
		for (int i = 0; i < Drains.length; i++) {
			D[i] = Drains[i];
		}
		*/
			int [] S = new int [2];	
		 S = this.getSourcePlaces();
		
		
		int placeNo = 0;
		int transNo = 0;
		ArrayList<String> P = new ArrayList<String>(placesArray.size());
		ArrayList<String> Q = new ArrayList<String>(transitionsArray.size());
		ArrayList<String> A1 = new ArrayList<String>(transitionsArray.size());
		ArrayList<String> Dlist = new ArrayList<String>(2);
		ArrayList<String> Slist = new ArrayList<String>(2);
		Dlist = this.tolist(D);
		Slist = this.tolist(S);
		P.addAll(Slist);
		while (Q.size()< f1[0].length){
			
			
			P.remove(0);
			placeNo  = getZeroRow(f2, P);
			while (!this.Exists(D, placeNo)) {
				if (Exists(D, placeNo))
					break;

				do {
					placeNo = this.getZeroRow(f2, P);

					while (placeNo == f2.length) {
						transNo = this.getZeroColumn(f1, Q);
						this.SetZeroColumn(f2, transNo);
						Q.add(Integer.toString(transNo));
						//A1.add(Integer.toString(transNo));
						placeNo = this.getZeroRow(f2, P);

					}

					P.add(Integer.toString(placeNo));

					if (Exists(D, placeNo)) {
						break;
					}

					this.setZeroRow(f1, placeNo);
					transNo = this.getZeroColumn(f1, Q);
				} while (transNo == f1[0].length);

				if (Exists(D, placeNo)) {
					break;
				}

				this.SetZeroColumn(f2, transNo);
				Q.add(Integer.toString(transNo));
				//A1.add(Integer.toString(transNo));

			}
			Dlist.remove(Integer.toString(placeNo));
			if(Dlist.size() == 0){
				break;
			}
			//P.remove(0);
			

			
		}

		return Q;
	}

	// ------------------------GET SEQUENCE----------------------
	ArrayList<String> A = null;
	ArrayList<String> B = null;

	public ArrayList<String> getSequenceQ() {

		PNMatrix FB = backwardsIncidenceMatrix
				.constructWithCopy(getBackwardsIncidenceMatrix());
		PNMatrix FF = forwardsIncidenceMatrix
				.constructWithCopy(getForwardsIncidenceMatrix());
		int[][] f11 = FB.getArray();
		int[][] f22 = FF.getArray();

		int[][] f1;
		int[][] f2;
		int[][] f = this.getSP1();

		int[] p = new int[this.ExistsRCTranstions(f11, f22)];
		int[][] RC = this.getRCTranstions(f11, f22);
		int count = this.ExistsRCTranstions(f11, f22);
		if (count != 0) {
			f1 = new int[f11.length - count][f11[0].length];
			f2 = new int[f11.length - count][f11[0].length];
			this.getNonRcMatrix(f11, f22, f1, f2);
		} else {
			f1 = f11;
			f2 = f22;
		}

		int placeNo = 0;
		int transNo = 0;

		ArrayList<String> Q = new ArrayList<String>(transitionsArray.size());
		ArrayList<String> P = new ArrayList<String>(placesArray.size());
		// ArrayList <String> A = new ArrayList <String> ();
		// ArrayList <String> B = new ArrayList <String> ();
		ArrayList<String> R = new ArrayList<String>(4);
		// ArrayList <String> S = new ArrayList <String> (2);
		// ArrayList <String> D = new ArrayList <String> (2);
		int[] S = new int[2];
		int[] D = new int[2];
		// get SourcePlaceNo
		for (int m = 0; m < 2; m++) {
			S[m] = this.getZeroRow(f2, R);
			R.add(Integer.toString(this.getZeroRow(f2, R)));

		}
		for (int m = 0; m < 2; m++) {
			D[m] = this.getZeroRow(f1, R);
			R.add(Integer.toString(this.getZeroRow(f1, R)));

		}

		P.add(Integer.toString(S[1]));

		// ArrayList <String> R = new ArrayList <String> ();

		while (Q.size() < f1[0].length) {
			while (!this.Exists(D, getZeroRow(f2, P))) {
				if (Exists(D, placeNo))
					break;

				do {
					placeNo = this.getZeroRow(f2, P);

					while (placeNo == f2.length) {
						transNo = this.getZeroColumn(f1, Q);
						this.SetZeroColumn(f2, transNo);
						Q.add(Integer.toString(transNo));
						A.add(Integer.toString(transNo));
						placeNo = this.getZeroRow(f2, P);

					}

					P.add(Integer.toString(placeNo));

					if (Exists(D, placeNo)) {
						break;
					}

					this.setZeroRow(f1, placeNo);
					transNo = this.getZeroColumn(f1, Q);
				} while (transNo == f1[0].length);

				if (Exists(D, placeNo)) {
					break;
				}

				this.SetZeroColumn(f2, transNo);
				Q.add(Integer.toString(transNo));
				A.add(Integer.toString(transNo));

			}
			if (A.size() == 0) {
				System.out.print("Problem Here");
			} else if (A.size() == transitionsArray.size()) {
				break;
			} else {
				P.remove(0);
				placeNo = 0;
				while (!Exists(D, getZeroRow(f2, P))) {
					if (Exists(D, placeNo))
						break;
					do {
						placeNo = this.getZeroRow(f2, P);

						while (placeNo == f2.length) {
							transNo = this.getZeroColumn(f1, Q);
							this.SetZeroColumn(f2, transNo);
							Q.add(Integer.toString(transNo));
							B.add(Integer.toString(transNo));
							placeNo = this.getZeroRow(f2, P);

						}

						P.add(Integer.toString(placeNo));
						if (Exists(D, placeNo))
							break;
						this.setZeroRow(f1, placeNo);
						transNo = this.getZeroColumn(f1, Q);
					} while (transNo == f1[0].length);

					if (Exists(D, placeNo))
						break;
					this.SetZeroColumn(f2, transNo);
					Q.add(Integer.toString(transNo));
					B.add(Integer.toString(transNo));
				}

			}
			

		}

		// 此处开始对所得的A ，B序列进行处理 对有资源冲突的变迁序列进行重新排序
		ArrayList<String> SQ = new ArrayList<String>();
		ArrayList<String> Acopy = new ArrayList<String>();
		ArrayList<String> Bcopy = new ArrayList<String>();
		Acopy.addAll(A);// 浅拷贝
		Bcopy.addAll(B);
		if (count != 0) {
			for (int i = 0; i < RC.length; i++) {
				int toIndex0 = 0;
				int toIndex1 = 0;
				if (Acopy.contains(Integer.toString(RC[i][0]))) {
					toIndex0 = Acopy.indexOf(Integer.toString(RC[i][0]));
					toIndex1 = Bcopy.indexOf(Integer.toString(RC[i][1]));
				} else if (Bcopy.contains(Integer.toString(RC[i][0]))) {
					toIndex0 = Acopy.indexOf(Integer.toString(RC[i][1]));
					toIndex1 = Bcopy.indexOf(Integer.toString(RC[i][0]));
				} else {
					System.out.print("Error here");
				}
				SQ.addAll(Acopy.subList(0, toIndex0));
				SQ.addAll(Bcopy.subList(0, toIndex1));

				SQ.add(Integer.toString(RC[i][0]));
				SQ.add(Integer.toString(RC[i][1]));

				Acopy.removeAll(SQ);
				Bcopy.removeAll(SQ);

			}
			SQ.addAll(Acopy);
			SQ.addAll(Bcopy);
			Q = SQ;

		}
		
		return Q;

	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~用于提供解决方案~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// 此处检测两个变迁号是否在同一过程中
	public boolean SameProcess(int a, int b) {
		boolean flag = false;
		if (A.contains(Integer.toString(a))) { // problem may be here
			if (A.contains(Integer.toString(b))) {
				flag = true;
			}
		} else if (B.contains(Integer.toString(b))) {
			if (B.contains(Integer.toString(b))) {
				flag = true;
			}
		}
		return flag;
	}

	// 返回同一过程中两个变迁号之间的变迁
	public int[] transitionsBetween(int a, int b) {
		int[] res;
		ArrayList<String> resarray = new ArrayList<String>();
		if (A.contains(Integer.toString(a))) {
			int aindex = A.indexOf(Integer.toString(a));
			int bindex = A.indexOf(Integer.toString(b));
			resarray.addAll(A.subList(aindex, bindex));
			// res = this.TransformToArray(resarray);
		} else {
			int aindex = B.indexOf(Integer.toString(a));
			int bindex = B.indexOf(Integer.toString(b));
			resarray.addAll(B.subList(aindex, bindex));

		}
		res = this.TransformToArray(resarray);
		return res;

	}

	// 返回某个过程之前的所有变迁
	public int[] transitionsTo(int a) {
		int[] res;
		int toIndex;
		ArrayList<String> resarray = new ArrayList<String>();
		if (A.contains(Integer.toString(a))) {
			toIndex = A.indexOf(Integer.toString(a));
			resarray.addAll(A.subList(0, toIndex));
		} else {
			toIndex = B.indexOf(Integer.toString(a));
			resarray.addAll(B.subList(0, toIndex));
		}
		res = this.TransformToArray(resarray);
		return res;

	}

	public int[] TransformToArray(ArrayList<String> from) {
		int[] result = new int[from.size()];
		for (int i = 0; i < from.size(); i++) {
			result[i] = Integer.parseInt(from.get(i));

		}
		return result;
	}

	/**
	 * used for caculating waiting time and give solutions maintained by the
	 * method CALRCTimeset key is the transition value is the transition he
	 * waits for
	 */
	HashMap<Integer, Integer> WaitingTime = null;

	// get the maximum waiting time
	public int waitingTime(int[] transitions) {
		int time = 0;
		for (int i = 0; i < transitions.length; i++) {
			if (this.ExistsRCTrans(transitions[i])) {
				if (WaitingTime.containsKey(transitions[i])) {
					int tj = WaitingTime.get(transitions[i]);
					time += this.getTransitionEndTime(tj);
				}
			}
		}

		return time;

	}

	// get the maximum waiting time between transition a and transition b
	public int getWaitingTimeBetween(int a, int b) {
		int time = 0;
		int[] transitions = this.transitionsBetween(a, b);
		time = this.waitingTime(transitions);
		return time;
	}

	// get the maximum waiting time from the beginning to the Index
	public int getWaitingTimeTo(int a) {
		int time = 0;
		int[] transitions = this.transitionsTo(a);
		time = this.waitingTime(transitions);
		return time;
	}
	
	// get the maximun waitiing time
	

	/*
	 * public String getPathToReduce(Path path){ StringBuilder solutions;
	 * 
	 * }
	 */
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~以上用于求得解决方案~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// ------------------------------------------********************************-----------------------
	ArrayList<SPNode> SPNodeList = null;
	// ArrayList <SPNode> SPNodeListA = null;
	// ArrayList <SPNode> SPNodeListB = null;
	ArrayList<SPArc> SPArcList = null;
	int[] Sources;
	int[] Drains;
	int ProcessNo;
	HashMap<Integer, Integer> PlaceIndex = null;
	TimesetOperation TP = new TimesetOperation();
	PathsetOperation PO = new PathsetOperation();

	/*
	 * int ProcessNo = getProcessNo(); int [] Sources = getSourcePlaces(); int
	 * [] Drains = getDrainPlaces(); HashMap <Integer, Integer>PlaceIndex = new
	 * HashMap<Integer,Integer>(this.getSP1().length); TimesetOperation TP = new
	 * TimesetOperation(); // 创建根节点
	 */
	/*
	 * public void SPCaculate(){ int ProcessNo = getProcessNo(); int [] Sources
	 * = getSourcePlaces(); int [] Drains = getDrainPlaces(); HashMap <Integer,
	 * Integer>PlaceIndex = new HashMap<Integer,Integer>(this.getSP1().length);
	 * TimesetOperation TP = new TimesetOperation();
	 * 
	 * }
	 */

	public void getSPInit() {
		ProcessNo = getProcessNo();
		Sources = getSourcePlaces();
		Drains = getDrainPlaces();
		PlaceIndex = new HashMap<Integer, Integer>(this.getSP1().length);
		// TimesetOperation TP = new TimesetOperation();

	}

	public void createRootNode() {
		getSPInit();
		int i = 0;
		while (i < Sources.length) {
			Timeset ts = new Timeset();
			Duration a = new Duration(0, 0);
			ts.addDuration(a);
			Pathset ps = new Pathset();
			ArrayList<String> s = new ArrayList<String>(2);
			ps.pathSet.add(new Path("", s));
			SPNode c = new SPNode(Sources[i], ts, ps);
			SPNodeList.add(c);
			// 维护映射表
			int placeNo = Sources[i];
			PlaceIndex.put(placeNo, SPNodeList.indexOf(c));
			i++;

		}

	}

	// 得到PLaceNo对应的索引

	public int getIndex(int PlaceNo) {
		return PlaceIndex.get(PlaceNo).intValue();
	}

	public boolean ExistsNode(int PlaceNo) {
		return PlaceIndex.containsKey(PlaceNo); // 可能PlaceNo 需由int 转为Integer
	}

	// 返回变迁的使能时间

	public Timeset getEnableTimeset(int transition) {

		int[] PrePlaces = getPrePlacesList(transition);

		Timeset res;
		if (PrePlaces.length == 1) {
			int PlaceNo = PrePlaces[0];
			res = SPNodeList.get(getIndex(PlaceNo)).getTimeset().copy();

		} else {
			Timeset A = SPNodeList.get(getIndex(PrePlaces[0])).getTimeset()
					.copy();
			Timeset B = SPNodeList.get(getIndex(PrePlaces[1])).getTimeset()
					.copy();
			Timeset temp = TP.Multiple(A, B);
			for (int i = 0; i < PrePlaces.length - 2; i++) {
				int q = i + 2;
				Timeset N = new Timeset(SPNodeList.get(getIndex(PrePlaces[q]))
						.getTimeset());
				temp = TP.Multiple(temp, N);
			}
			res = temp;
		}
		return res;
	}

	// -----------------------*************************--------------------------
	int[][] f11;
	int[][] f22;
	int RCcount;
	int[][] RC;
	PNMatrix FB;
	PNMatrix FF;

	// 可考虑只在最前面调用一次
	public void getRCInit() {
		FB = backwardsIncidenceMatrix
				.constructWithCopy(getBackwardsIncidenceMatrix());
		FF = forwardsIncidenceMatrix
				.constructWithCopy(getForwardsIncidenceMatrix());
		f11 = FB.getArray();
		f22 = FF.getArray();
		RCcount = this.ExistsRCTranstions(f11, f22);
		RC = this.getRCTranstions(f11, f22);
		this.getMapofPlaces();
	}

	// 判断是否存在与变迁存在资源冲突的
	public boolean ExistsRCTrans(int transition) {
		boolean flag = false;
		if (RCcount == 0) {
			return flag;
		} else {
			for (int i = 0; i < RC.length; i++) {
				if (this.Exists(RC[i], transition)) {
					flag = true;
				}
			}
		}
		return flag;
	}

	// 返回与给定变迁有资源冲突的变迁号
	public int getRCtrans(int transition) {
		int res = 100;// 返回100说明有问题
		for (int i = 0; i < RC.length; i++) {
			if (this.Exists(RC[i], transition)) {
				for (int j = 0; j < 2; j++) {
					if (RC[i][j] == transition) {
						if (j == 0) {
							res = RC[i][1];
						} else {
							res = RC[i][0];
						}
					}

				}
			}
		}
		return res;
	}

	// Step2 之Case1 之pathset caculation
	public Pathset calPathset(int transNo) {
		int[] p = this.getPrePlacesList(transNo);
		Pathset res = null;
		String transName = this.getTransitionName(transNo);
		if (p.length == 1) {
			int PlaceNo = p[0];
			int index = this.getIndex(PlaceNo);

			Pathset A = new Pathset(SPNodeList.get(index).Pathset);
			A.addTransition(transName);
			res = A;
		} else {
			int count = p.length;

			if (count == 2) {
				int indexA = this.getIndex(p[0]);
				int indexB = this.getIndex(p[1]);
				Pathset A = new Pathset(SPNodeList.get(indexA).getPathset());
				Pathset B = new Pathset(SPNodeList.get(indexB).getPathset());
				Pathset C = PO.CartUnion(A, B);
				res = C;

			} else {
				int indexA = this.getIndex(p[0]);
				int indexB = this.getIndex(p[1]);
				Pathset A = new Pathset(SPNodeList.get(indexA).getPathset());
				Pathset B = new Pathset(SPNodeList.get(indexB).getPathset());
				Pathset temp = PO.CartUnion(A, B);
				for (int i = 0; i < count - 2; i++) {
					int index = getIndex(p[i + 2]);
					Pathset hi = new Pathset(SPNodeList.get(index).getPathset());
					temp = PO.CartUnion(temp, hi);

				}
				res = temp;

			}
			res.addTransition(transName);

		}
		return res;
	}

	// Step 2 之 case 1 之 timeset caculation
	public Timeset calTimeset(int transNo) {

		Timeset en = this.getEnableTimeset(transNo).copy();
		Duration e = this.getTransition(transNo).getDuration();
		en.plusDuration(e);
		return en;

	}

	// -------------------------------------STEP 2 CASE2 ------------------
	// ----------对使能时间进行修正------Brilliant Work Baby------------------------
	public void modifyEnableTime(int transNo, Timeset TiEn, Timeset TjEn) {
		DurOperation DP = new DurOperation();
		int tj = this.getRCtrans(transNo);
		Timeset tjEn = this.getEnableTimeset(tj).copy(); // 副本
		Timeset tiEn = this.getEnableTimeset(transNo).copy();
		TiEn = this.getEnableTimeset(transNo);// 真实指向需要修改的时间集合,任何对它的修改都是真实的引用
		TjEn = this.getEnableTimeset(tj);
		int tiDurNo = tiEn.Timeset.size();
		int tjDurNo = tjEn.Timeset.size();

		if (tiDurNo == 1 && tjDurNo == 1) {
			if (DP.Smaller(TjEn.getDuration(0), TiEn.getDuration(0))) {
				this.modifyDuration(TiEn.getDuration(0), TjEn.getDuration(0),
						tj);
			}
		} else {
			for (int i = 0; i < tiDurNo; i++) {
				for (int j = 0; j < tjDurNo; j++) {
					int n = (i + 1) * (j + 1) - 1;
					if (DP.Smaller(tjEn.getDuration(j), tiEn.getDuration(i))) {
						Duration dur = this.modifyNewDuration(
								tiEn.getDuration(i), tjEn.getDuration(j), tj);
						TiEn.Timeset.set(n, dur);
						Duration unchanged = new Duration(tjEn.getDuration(j));
						TjEn.Timeset.set(n, unchanged);

					} else {
						Duration unchanged = new Duration(tiEn.getDuration(i));
						TiEn.Timeset.set(n, unchanged);
						Duration dur = this.modifyNewDuration(
								tjEn.getDuration(j), tiEn.getDuration(i),
								transNo);
						TjEn.Timeset.set(n, dur);
					}
				}
			}
		}

	}

	// modify a duration
	public void modifyDuration(Duration need, Duration ref, int transitionTj) {
		int a = max(
				ref.getStarttime() + this.getTransitionStartTime(transitionTj),
				need.getStarttime());
		int b = max(ref.getEndtime() + this.getTransitionEndTime(transitionTj),
				need.getEndtime());
		need.setStarttime(a);
		need.setEndtime(b);
	}

	// modify a duration and return it;
	public Duration modifyNewDuration(Duration need, Duration ref,
			int transitionTj) {
		int a = max(
				ref.getStarttime() + this.getTransitionStartTime(transitionTj),
				need.getStarttime());
		int b = max(ref.getEndtime() + this.getTransitionEndTime(transitionTj),
				need.getEndtime());
		Duration res = new Duration(a, b);
		return res;
	}

	public Timeset calRCTimeset(int transition) {
		Timeset ts = new Timeset();
		Timeset TiEn = new Timeset();
		Timeset TjEn = new Timeset();
		Timeset tiEn = null;
		Timeset tjEn = null;
		DurOperation DP = new DurOperation();
		int tj = this.getRCtrans(transition);
		boolean flag = this.needModify(transition);
		if (flag) {

			// this.modifyEnableTime(transition,TiEn,TjEn);-----------------------------

			tjEn = this.getEnableTimeset(tj).copy(); // 副本
			tiEn = this.getEnableTimeset(transition).copy();
			TiEn = this.getEnableTimeset(transition);
			// 真实指向需要修改的时间集合,任何对它的修改都是真实的引用
			TjEn = this.getEnableTimeset(tj);
		} else {
			// int ti = this.getRCtrans(transition);
			tjEn = this.getEnableTimeset(transition).copy();
			tiEn = this.getEnableTimeset(tj).copy();
			TjEn = this.getEnableTimeset(transition);
			TiEn = this.getEnableTimeset(tj);

		}

		int tiDurNo = tiEn.Timeset.size();
		int tjDurNo = tjEn.Timeset.size();
		int count = tiDurNo * tjDurNo;
		// 避免空指针报错
		// TiEn = new Timeset(count);
		// TjEn = new Timeset(count);

		if (tiDurNo == 1 && tjDurNo == 1) {
			if (DP.Smaller(TjEn.getDuration(0), TiEn.getDuration(0))) {
				this.modifyDuration(TiEn.getDuration(0), TjEn.getDuration(0),
						tj);
				// maintain the map
				WaitingTime.put(transition, tj);

			}
		} else {
			TiEn = new Timeset();
			TjEn = new Timeset();
			for (int i = 0; i < tiDurNo; i++) {
				for (int j = 0; j < tjDurNo; j++) {
					int n = (i + 1) * (j + 1) - 1;
					if (DP.Smaller(tjEn.getDuration(j), tiEn.getDuration(i))) {
						Duration dur = this.modifyNewDuration(
								tiEn.getDuration(i), tjEn.getDuration(j), tj);
						TiEn.Timeset.add(n, dur);
						Duration unchanged = new Duration(tjEn.getDuration(j));
						TjEn.Timeset.add(n, unchanged);
						// maintain the map of Waitingtime
						WaitingTime.put(transition, tj);

					} else {
						Duration unchanged = new Duration(tiEn.getDuration(i));
						//TiEn.Timeset.set(n, unchanged);
						TiEn.Timeset.add(n,unchanged);
						Duration dur = this.modifyNewDuration(
								tjEn.getDuration(j), tiEn.getDuration(i),
								transition);
						//TjEn.Timeset.set(n, dur);
						TjEn.Timeset.add(n,dur);
						// maintain the map
						WaitingTime.put(tj, transition);
					}
				}
			}
		}

		// ------------------------------------------------------------------

		if (flag) {

			ts = TiEn;
		} else {
			ts = TjEn;
		}
		ts.plusDuration(this.getTransition(transition).getDuration());
		return ts;

	}

	// 测试是否需要修正 实际上是确定是谁是ti，谁是tj 观察使用可知
	public boolean needModify(int transition) {
		boolean need = false;
		int i = 0;
		for (i = 0; i < seq.length; i++) {
			if (seq[i] == transition) {
				break;
			}
		}
		int RCtrans = this.getRCtrans(transition);
		if (i == seq.length - 1) {
			need = false;
		} else if (seq[i + 1] == RCtrans) {
			need = true;
		} else {
			need = false;
		}
		return need;
	}

	public Pathset calRCPathset(int transition) {
		int tj = this.getRCtrans(transition);
		int[] tiPre = this.getPrePlacesList(transition);
		int[] tjPre = this.getPrePlacesList(tj);
		// 现在只能处理 tiPre.length == 1 和 tjPre.length == 1的情况
		int Aindex = getIndex(tiPre[0]);
		int Bindex = getIndex(tjPre[0]);
		Pathset A = new Pathset(SPNodeList.get(Aindex).getPathset());
		Pathset B = new Pathset(SPNodeList.get(Bindex).getPathset());
		Pathset res = PO.CartInterc(A, B);
		String transName = this.getTransitionName(transition);
		res.addTransition(transName);
		return res;

	}

	// -------------------------STEP 3------------------------------
	public void createNodeArc(int transition) {

		Pathset Ps = null;
		Timeset Ts = null;
		if (this.ExistsRCTrans(transition) == false) {
			Ps = this.calPathset(transition);
			Ts = this.calTimeset(transition);
		} else {
			Ps = this.calRCPathset(transition);
			Ts = this.calRCTimeset(transition);
		}

		int[] post = this.getPosPlacesList(transition);
		int[] pre = this.getPrePlacesList(transition);
		for (int i = 0; i < post.length; i++) {

			// int PlaceNo = this.getRealPlaceNo(post[i]);
			int PlaceNo = post[i];
			if (this.ExistsNode(PlaceNo)) {
				SPNode N = SPNodeList.get(getIndex(PlaceNo));
				Timeset Ts0 = N.getTimeset();
				Pathset Ps0 = N.getPathset();
				N.setPathset(PO.Merge(Ps, Ps0));
				N.setTimeset(TP.Union(Ts, Ts0));

				for (int j = 0; j < pre.length; j++) {
					// int prePlaceNo = this.getRealPlaceNo(pre[j]);
					int prePlaceNo = pre[j];
					SPArc arc = new SPArc(prePlaceNo, PlaceNo, transition);
					SPArcList.add(arc);
				}

			} else {
				SPNode N = new SPNode(PlaceNo, Ts, Ps); // problem may be here
				SPNodeList.add(N);
				PlaceIndex.put(PlaceNo, SPNodeList.indexOf(N));

				for (int j = 0; j < pre.length; j++) {
					// int prePlaceNo = this.getRealPlaceNo(pre[j]);
					int prePlaceNo = pre[j];
					SPArc arc = new SPArc(prePlaceNo, PlaceNo, transition);
					SPArcList.add(arc);
				}

			}
		}
	}

	//
	ArrayList<String> Seq = null;
	int[] seq;

	public void SequenceInit() {
		if (Seq.size() == 0) {
			int [] D2 = new int[2];
			D2 = this.getDrainPlaces();
			if(D2.length>2){
				Seq = this.getMultiSequence();
			}
			else{
				Seq = this.getSequenceQ();
			}
			
		}
		seq = new int[Seq.size()];
		for (int j = 0; j < Seq.size(); j++) {
			seq[j] = Integer.parseInt(Seq.get(j));
		}

	}

	// used for the Graph Legend so it's read only;
	public ArrayList<String> getseqString() {
		ArrayList<String> seqname = new ArrayList<String>();
		this.SequenceInit();// 如果其他使用了则避免重复调用

		for (int i = 0; i < seq.length; i++) {
			seqname.add(this.getTransitionName(i));
		}
		return seqname;
		// 注意只能读取 否则会破坏 if there's time please write a copy of it;
	}

	public void getSPRaw() {
		/*
		 * ArrayList <String > Seq = this.getSequenceQ(); int [] seq = new
		 * int[Seq.size()]; for(int j = 0; j < Seq.size(); j++){ seq [j] =
		 * Integer.parseInt(Seq.get(j)); }
		 */
		this.SequenceInit();
		this.getRCInit();
		// the condition is vital because without it there will be plenty of
		// nodes
		// it assures the SPnodelise and arclist only initialize once;
		if (SPNodeList.size() == 0) {
			this.createRootNode();
		}
		// Transition a =this.getTransition(0);
		// int b = a.getStarttime();
		// the condition's importance as before
		if (SPNodeList.size() == Sources.length) {
			for (int i = 0; i < seq.length; i++) {
				this.createNodeArc(seq[i]);
			}
		}
	}

	// 这一步实现对SPNodeList与arcList的改造，因为之前存的placeNo全部都是 SPMatrix中 剪切掉
	// 冲突资源库所中的placeNo,与placesArray中存的placeNo有一定错位，全部进行修正
	public void createSPNodeList() {
		if (SPNodeList.size() == SP1.length && SP1.length != 0) {
			for (int i = 0; i < SPNodeList.size(); i++) {
				int oldNo = SPNodeList.get(i).getPlaceNo();
				int real = this.getRealPlaceNo(oldNo);
				SPNodeList.get(i).setPlaceNo(real);

			}
			for (int i = 0; i < SPArcList.size(); i++) {
				int oldfrom = SPArcList.get(i).from;
				int oldto = SPArcList.get(i).to;
				int _from = this.getRealPlaceNo(oldfrom);
				int _to = this.getRealPlaceNo(oldto);
				SPArcList.get(i).setFromTo(_from, _to);

			}
		} else {
			System.out.print("Error in getSPNodeList , may not create it");
		}
	}

	// --------------------------------------*****************************------------------------
	public ArrayList getSPNodeList() {
		this.getSPData();
		ArrayList<SPNode> res = new ArrayList<SPNode>();
		for (int i = 0; i < SPNodeList.size(); i++) {
			SPNode N = new SPNode();
			N = SPNodeList.get(i).deepCopy();
			res.add(N);

		}
		return res;
	}

	public ArrayList getSPArcList() {
		this.getSPData();
		ArrayList<SPArc> res = new ArrayList<SPArc>();
		for (int i = 0; i < SPArcList.size(); i++) {
			SPArc arc = new SPArc();
			arc = SPArcList.get(i).copy();
			res.add(arc);
		}
		return res;

	}

	// get both lists in one method

	public void getSPLists(ArrayList<SPNode> nodelist, ArrayList<SPArc> arclist) {
		if (SPNodeList.size() == 0) {
			this.getSPData();
		}
		// if(nodelist.size()<SPNodeList.size()){
		for (int i = 0; i < SPNodeList.size(); i++) {
			SPNode N = new SPNode();
			N = SPNodeList.get(i).deepCopy();
			nodelist.add(N);

		}
		for (int i = 0; i < SPArcList.size(); i++) {
			SPArc arc = new SPArc();
			arc = SPArcList.get(i).copy();
			arclist.add(arc);
		}
		// }
	}

	public void getSPListsData(ArrayList<SPNode> nodelist,
			ArrayList<SPArc> arclist) {

		SPNodeList.clear();
		SPArcList.clear();
		this.getSPData();
		// if(nodelist.size()<SPNodeList.size()){
		for (int i = 0; i < SPNodeList.size(); i++) {
			SPNode N = new SPNode();
			N = SPNodeList.get(i).deepCopy();
			nodelist.add(N);

		}
		for (int i = 0; i < SPArcList.size(); i++) {
			SPArc arc = new SPArc();
			arc = SPArcList.get(i).copy();
			arclist.add(arc);
		}
		// }
	}

	public void getSPData() {
		this.getSPRaw();
		this.createSPNodeList();
	}

	// 返回arc的显示内容
	public String getArcText(int transition) {
		StringBuilder a = new StringBuilder(this.getTransitionName(transition));
		a.append("-[")
				.append(String.valueOf(this.getTransition(transition)
						.getStarttime())).append(",");
		a.append(String.valueOf(this.getTransition(transition).getEndtime()))
				.append("]");
		return a.toString();
	}

	public void test() {

		ArrayList <String> q = new ArrayList<String> (1);
			q = this.getMultiSequence();
		return;

	}

}
