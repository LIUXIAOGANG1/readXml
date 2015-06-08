package pipe.gui.undo;

import pipe.dataLayer.Transition;

public class TransitionEndtimeEdit 
extends UndoableEdit{
	   Transition transition;
	   Integer newEndtime;
	   Integer oldEndtime;
	   
	   
	   /** Creates a new instance of placeRateEdit */
	   public TransitionEndtimeEdit(
	           Transition _transition, Integer _oldEndtime, Integer _newEndtime) {
	      transition = _transition;
	      newEndtime = _newEndtime;   
	      oldEndtime = _newEndtime;
	   }

	   
	   /** */
	   public void undo() {
		   transition.setEndtime(oldEndtime);
	     
	   }

	   
	   /** */
	   public void redo() {
	      transition.setEndtime(newEndtime);
	   }
	   

}


