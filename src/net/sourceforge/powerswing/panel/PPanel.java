package net.sourceforge.powerswing.panel;


import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;

import net.sourceforge.powerswing.label.PJLabel;
import net.sourceforge.powerswing.label.PJMultiLineLabel;

/**
 * Easy to use GUI builder.
 * 
 * <pre>
 * PowerPanel is a panel which lays out its children in a similar way to how
 * HTML tables are laid out. The children are passed into PowerPanel constructor
 * in an Object array which also contains formatting information.
 * 
 * PowerPanel's constructor takes the following arguments. Arguments in square
 * brackets are optional:
 * 
 * new PowerPanel(int rows, int cols, 
 *                [int vpadding, int hpadding,] 
 *                [String frameTitle,] 
 *                Object[] array, 
 *                [int toppadding, int bottompadding, 
 *                 int leftpadding, int rightpadding])
 * 
 * rows - the number of rows in the object array
 * cols - the number of columns in the object array
 * vpadding - the number of pixels children should be vertically spaced apart. 
 *     Default is 4
 * hpadding - the number of pixels children should be horizontally spaced apart. 
 *     Default is 3
 * frameTitle - if a frameTitle is specified PowerPanel's children are
 *     surrounded by a frame and given a title. Use "" if you want a frame but
 *     no title.
 * array - the object array. Contains the panel's child components and
 *     formatting information. See below.
 * toppadding, bottompadding, leftpadding, rightpadding - the spacing in pixels 
 *     between the edge of the panel and the panels contents. Defaults to 
 *     0, 0, 0, 0 if the panel doesn't have a frame and 0, 4, 3, 3 if the panel 
 *     does have a frame 
 *
 * Here is an example of PowerPanel in use:
 *
 *  
 * </pre>
 * 
 * @author Paul McClave
 */

public class PPanel extends JPanel {
	public PPanel(int rows, int cols, Object[] array) {
		this(rows, cols, 4, 3, null, array, 0, 0, 0, 0, null);
	}

	public PPanel(int rows, int cols, String frameTitle, Object[] array) {
		this(rows, cols, 4, 3, frameTitle, array, 0, 4, 3, 3, null);
	}

	public PPanel(int rows, int cols, Object[] array, int toppadding, int bottompadding, int leftpadding, int rightpadding) {
		this(rows, cols, 4, 3, null, array, leftpadding, rightpadding, toppadding, bottompadding, null);
	}

	public PPanel(int rows, int cols, String frameTitle, Object[] array, int toppadding, int bottompadding, int leftpadding, int rightpadding) {
		this(rows, cols, 4, 3, frameTitle, array, leftpadding, rightpadding, toppadding, bottompadding, null);
	}

	public PPanel(int rows, int cols, int vpadding, int hpadding, Object[] array) {
		this(rows, cols, vpadding, hpadding, null, array, 0, 0, 0, 0, null);
	}

	public PPanel(int rows, int cols, int vpadding, int hpadding, String frameTitle, Object[] array) {
		this(rows, cols, vpadding, hpadding, frameTitle, array, 0, 4, 3, 3, null);
	}

	public PPanel(int rows, int cols, int vpadding, int hpadding, Object[] array, int toppadding, int bottompadding, int leftpadding, int rightpadding) {
		this(rows, cols, vpadding, hpadding, null, array, toppadding, bottompadding, leftpadding, rightpadding, null);
	}
    
    public PPanel(int rows, int cols, int vpadding, int hpadding, Object[] array, int toppadding, int bottompadding, int leftpadding, int rightpadding, Color backgroundColor) {
        this(rows, cols, vpadding, hpadding, null, array, toppadding, bottompadding, leftpadding, rightpadding, backgroundColor);
    }

	public PPanel(int rows, int cols, int vpadding, int hpadding, String frameTitle, Object[] array, int toppadding, int bottompadding, int leftpadding, int rightpadding, Color backgroundColor) {
		ArrayWrapper a = new ArrayWrapper(array, rows, cols);

		if (frameTitle != null) {
			this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(frameTitle), BorderFactory.createEmptyBorder(toppadding, leftpadding, bottompadding, rightpadding)));
		}
		else {
			Border b = BorderFactory.createEmptyBorder(toppadding, leftpadding, bottompadding, rightpadding);
			this.setBorder(b);
		}
        
        if (backgroundColor != null){
            this.setBackground(backgroundColor);
        }

		if (a.length != (rows + 1) * (cols + 1)) {
			throw new IllegalArgumentException("array is of the wrong size");
		}

		double[] columnWeights = new double[cols];
		double[] columnMins = new double[cols];
		boolean zeroCW = true;
		for (int c = 0; c < cols; c++) {
			StringTokenizer tok = new StringTokenizer((String) a.get(-1, c), ",");
			columnMins[c] = Integer.parseInt(tok.nextToken());
			if (tok.hasMoreTokens()) {
				columnWeights[c] = Double.parseDouble(tok.nextToken());
			}
			if (columnWeights[c] != 0) {
				zeroCW = false;
			}
		}

		double[] rowWeights = new double[rows];
		double[] rowMins = new double[rows];
		boolean zeroRW = true;
		for (int r = 0; r < rows; r++) {
			StringTokenizer tok = new StringTokenizer((String) a.get(r, -1), ",");
			rowMins[r] = Integer.parseInt(tok.nextToken());
			if (tok.hasMoreTokens()) {
				rowWeights[r] = Double.parseDouble(tok.nextToken());
			}
			if (rowWeights[r] != 0) {
				zeroRW = false;
			}
		}

		JPanel inner = this;
		if (zeroRW) {
			this.setLayout(new BorderLayout());
			inner = new JPanel();
			this.add(inner, BorderLayout.NORTH);
		}
		if (zeroCW) {
			JPanel outer = inner;
			outer.setLayout(new BorderLayout());
			inner = new JPanel();
			outer.add(inner, BorderLayout.WEST);
		}
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = columnWeights;
		gbl.rowWeights = rowWeights;
		inner.setLayout(gbl);

		ArrayList <Component> labels = new ArrayList <Component> ();
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Object o = a.get(r, c);
				if (o instanceof JComponent) {
					JComponent component = (JComponent) o;
					add(a, r, c, rows, cols, vpadding, hpadding, rowMins, columnMins, inner, gbl, component);
				}
				else if (o instanceof String) {
					String s = (String) o;
					if (s.startsWith("~") || s.startsWith(">") || s.startsWith("v")) {
						String label = "";
						ArrayList <Component> dependancies = new ArrayList <Component> ();
						if (s.startsWith(">") || s.startsWith("v")) {
							int tildaIndex = s.indexOf("~");
							if (s.indexOf("~") == -1) {
								throw new IllegalArgumentException("Elements starting with \">\"'s or \"v\"'s must contain a \"~\"");
							}
							String depList = s.substring(0, tildaIndex);
							dependancies.addAll(getDependancies(depList, a, r, c, rows, cols));
							label = s.substring(tildaIndex + 1);
						}
						else {
							label = s.substring(1);
						}

						if (s.indexOf('\n') == -1) {
							PJLabel jlabel = new PJLabel(label);
							jlabel.addAssociations(dependancies);
							labels.add(jlabel);
							add(a, r, c, rows, cols, vpadding, hpadding, rowMins, columnMins, inner, gbl, jlabel);
						}
						else {
							PJMultiLineLabel mljlabel = new PJMultiLineLabel(label);
							mljlabel.addAssociations(dependancies);
							labels.add(mljlabel);
							add(a, r, c, rows, cols, vpadding, hpadding, rowMins, columnMins, inner, gbl, mljlabel);
						}
					}
					else if (s.equals("") || s.equals("<") || s.equals("^") || s.equals("#")) {
						// do nothing
					}
					else {
						throw new IllegalArgumentException("Elements must be Components, labels, \"<\"'s, \"^\"'s, \"#\"'s or \"\"'s");
					}
				}
				else if (o == null) {
					throw new IllegalArgumentException("Element is null");
				}
				else {
					throw new IllegalArgumentException("Elements must be Components, labels, \"<\"'s, \"^\"'s, \"#\"'s or \"\"'s");
				}
			}
		}
		
		for (Iterator <Component> i = labels.iterator(); i.hasNext();) {
		    Component jl = i.next();
            if (jl instanceof PJLabel){
                ((PJLabel) jl).checkStates();
            }
            else if (jl instanceof PJMultiLineLabel){
                ((PJMultiLineLabel) jl).checkStates();
            }
        }
	}

	/**
	 * @param depList
	 * @return
	 */
	private Collection <Component> getDependancies(String depList, ArrayWrapper a, int r, int c, int rows, int cols) {
		ArrayList <Component> deps = new ArrayList <Component> ();
		StringTokenizer st = new StringTokenizer(depList, ",");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();

			if (t.indexOf(">v") != -1) {
				t = t.replaceAll(">v", ">1v");
			}
			if (t.endsWith(">") || t.endsWith("v")) {
				t += "1";
			}
			if (t.indexOf(">") == -1) {
				t = ">0" + t;
			}
			if (t.indexOf("v") == -1) {
				t += "v0";
			}

			deps.add(getComponent(t, a, r, c, rows, cols));
		}

		return deps;
	}

	private JComponent getComponent(String t, ArrayWrapper a, int r, int c, int rows, int cols) {
		int deltaC = 0;
		int deltaR = 0;

		Pattern p = Pattern.compile(">(.*?)v(.*)");
		Matcher m = p.matcher(t);
		if (m.matches()) {
			deltaC = Integer.parseInt(m.group(1));
			deltaR = Integer.parseInt(m.group(2));
		}
		else {
			throw new IllegalArgumentException("This should never happen. Depency code is broken");
		}

		int newR = r + deltaR;
		int newC = c + deltaC;

		if (newR == r && newC == c) {
			throw new IllegalArgumentException("Dependancy reference cannot reference self");
		}

		if (newR >= rows || newC >= cols) {
			throw new IllegalArgumentException("Dependancy references a location that is out of range");
		}

		Object o = a.get(newR, newC);
		if (o instanceof JComponent && !(o instanceof JLabel)) {
			return (JComponent) o;
		}
		throw new IllegalArgumentException("Dependancy references must reference non-label objects");
	}

	private void add(ArrayWrapper a, int r, int c, int rows, int cols, int vpadding, int hpadding, double[] rowMins, double[] columnMins, JPanel inner, GridBagLayout gbl, JComponent component) {
		// find extents
		int height = 1;
		double minPixHeight = rowMins[r];
		for (int rr = r + 1; rr < rows; rr++) {
			Object oo = a.get(rr, c);
			if (oo instanceof String && oo.equals("^")) {
				height++;
				minPixHeight += rowMins[rr];
			}
			else {
				break;
			}
		}

		int width = 1;
		double minPixWidth = columnMins[c];
		for (int cc = c + 1; cc < cols; cc++) {
			Object oo = a.get(r, cc);
			if (oo instanceof String && oo.equals("<")) {
				width++;
				minPixWidth += columnMins[cc];
			}
			else {
				break;
			}
		}

		// check extents are full of #'s
		for (int rr = r + 1; rr < r + height; rr++) {
			for (int cc = c + 1; cc < c + width; cc++) {
				Object oo = a.get(rr, cc);
				if (!(oo instanceof String) || !oo.equals("#")) {
					throw new IllegalArgumentException("Element " + rr + ", " + cc + " must be \"#\"");
				}
			}
		}

		{
			Dimension min = component.getMinimumSize();
			boolean fixMin = false;

			if (min.getHeight() < minPixHeight) {
				fixMin = true;
			}
			else {
				minPixHeight = min.getHeight();
			}

			if (min.getWidth() < minPixWidth) {
				fixMin = true;
			}
			else {
				minPixWidth = min.getWidth();
			}

			if (fixMin) {
				Dimension dim = new Dimension();
				dim.setSize(minPixWidth, minPixHeight);
				component.setMinimumSize(dim);
			}
		}

		{
			Dimension pref = component.getPreferredSize();
			boolean fixPref = false;

			if (pref.getHeight() < minPixHeight) {
				fixPref = true;
			}
			else {
				minPixHeight = pref.getHeight();
			}

			if (pref.getWidth() < minPixWidth) {
				fixPref = true;
			}
			else {
				minPixWidth = pref.getWidth();
			}

			if (fixPref) {
				Dimension dim = new Dimension();
				dim.setSize(minPixWidth, minPixHeight);
				component.setPreferredSize(dim);
			}
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = r;
		gbc.gridx = c;
		gbc.gridheight = height;
		gbc.gridwidth = width;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(vpadding, hpadding, vpadding, hpadding);
		gbl.setConstraints(component, gbc);
		inner.add(component);
	}

	private class ArrayWrapper {
		public int length;
		public Object[] array;
		public int rows;
		public int cols;

		public Object get(int r, int c) {
			return array[(r + 1) * (cols + 1) + c + 1];
		}

		public ArrayWrapper(Object[] array, int rows, int cols) {
			this.array = array;
			this.length = array.length;
			this.rows = rows;
			this.cols = cols;
		}
	}
}