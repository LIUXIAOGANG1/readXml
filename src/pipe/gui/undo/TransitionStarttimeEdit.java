package pipe.gui.undo;
import pipe.dataLayer.Transition;

public class TransitionStarttimeEdit 
                extends UndoableEdit{
	   Transition transition;
	   Integer newStarttime;
	   Integer oldStarttime;
	   
	   
	   /** Creates a new instance of placeRateEdit */
	   public TransitionStarttimeEdit(
	           Transition _transition, Integer _oldStarttime, Integer _newStarttime) {
	      transition = _transition;
	      newStarttime = _newStarttime;   
	      oldStarttime = _oldStarttime;
	   }

	   
	   /** */
	   public void undo() {
		   transition.setStarttime(oldStarttime);
	     
	   }

	   
	   /** */
	   public void redo() {
	      transition.setStarttime(newStarttime);
	   }
	   

}
