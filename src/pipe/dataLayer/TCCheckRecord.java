package pipe.dataLayer;

import java.util.ArrayList;

public class TCCheckRecord {
	public ArrayList<CheckRecord> violated = new ArrayList<CheckRecord>();
	public ArrayList<SatisfiedRecord> satisfied = new ArrayList<SatisfiedRecord>();
	public ArrayList<UnknownRecord> unknown = new ArrayList<UnknownRecord>();
	int tcNo;
	boolean SameProcess;

	public TCCheckRecord(int _tcNo, boolean same,
			ArrayList<CheckRecord> _violated,
			ArrayList<SatisfiedRecord> _satisfied,
			ArrayList<UnknownRecord> _unknown) {
		tcNo = _tcNo;
		SameProcess = same;
		violated = _violated;
		satisfied = _satisfied;
		unknown = _unknown;
	}

	public int gettcNo() {
		return tcNo;
	}

	/**
	 * 0 indicates violated 
	 * 1 indicates satisfied
	 *  2 indicates unknown 10 problem
	 * 
	 * @return
	 */
	public int getProperty() {
		int property = 10;
		if (violated.size() == 0 && unknown.size() == 0) {
			property = 1;
		} else if (violated.size() == 0 && unknown.size() != 0) {
			property = 2;
		} else if (violated.size() != 0) {
			property = 0;
		}

		return property;

	}

	public String getText() {
		int property = this.getProperty();
		String s = "Temporal Constraint " + Integer.toString(tcNo + 1);
		if (property == 1) {
			s += " is satisfied.";
			s += "There's no violation.";
		} else if (property == 0) {
			s += " is violated .";
			for (int i = 0; i < violated.size(); i++) {
				s += violated.get(i).getText();
			}
			for (int i = 0; i < satisfied.size(); i++) {
				s += satisfied.get(i).getText();
			}
			for (int i = 0; i < unknown.size(); i++) {
				s += unknown.get(i).getText();

			}
		} else if (property == 2) {
			s += " cannot be decided at this moment.";

			for (int i = 0; i < unknown.size(); i++) {
				s += unknown.get(i).getText();

			}
			for (int i = 0; i < satisfied.size(); i++) {
				s += satisfied.get(i).getText();
			}

		}
		return s;
	}
	public int[] getData(){
		int [] data = new int[3];
		data[0] = violated.size();
		data[1] = satisfied.size();
		data[2] = unknown.size();
		return data;
	}
	

}
