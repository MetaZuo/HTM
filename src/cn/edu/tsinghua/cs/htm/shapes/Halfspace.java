package cn.edu.tsinghua.cs.htm.shapes;

import cn.edu.tsinghua.cs.htm.utils.Constants;
import cn.edu.tsinghua.cs.htm.utils.Sign;

/**
 * A circular region on a unit sphere, defined by
 * 1. a center-oriented unit vector orthogonal to the constraint plane;  
 * 2. a double representing the distance between the center and the plane,
 * which can be positive, negative or zero.
 * @author Haojia Zuo
 *
 */
public class Halfspace {

	Cartesian vector;
	double distance;
	
	public Halfspace(Cartesian vector, double distance) {
		this.vector = vector.normalize();
		this.distance = distance;
	}
	
	/**
	 * Judge if two Halfspaces overlap
	 * "Overlap" means intersect, contain or identical
	 * simply speaking, not exclusive
	 * @param that another Halfspace
	 * @return true if overlap
	 */
	public boolean overlap(Halfspace that) {
		return (!exclusive(that));
	}
	
	/**
	 * Judge if two Halfspaces are mutually exclusive
	 * @param that another Halfspace
	 * @return true if exclusive
	 */
	public boolean exclusive(Halfspace that) {
		double halfAngularThis = Math.acos(distance);
		double halfAngularThat = Math.acos(that.distance);
		double normsRelative = Math.acos(vector.dot(that.vector));
		return (normsRelative >= halfAngularThis + halfAngularThat);
	}
	
	/**
	 * Judge if this Halfspace contains another one
	 * @param that another Halfspace
	 * @return true if this contains that
	 */
	public boolean contains(Halfspace that) {
		double halfAngularThis = Math.acos(distance);
		double halfAngularThat = Math.acos(that.distance);
		double normsRelative = Math.acos(vector.dot(that.vector));
		
		return (halfAngularThis - halfAngularThat >= normsRelative);
	}
	
	/**
	 * Test whether a point in inside the Halfspace
	 * @param p Cartesian point to test
	 * @return true if inside
	 */
	public boolean contains(Cartesian p) {
		 return (vector.dot(p) > distance + Constants.epsilon);
	}
	
	/**
	 * Get the sign of the Halfspace
	 * Defined by the sign of distance
	 * @return Enumeration type Sign: Positive, Negative or Zero
	 */
	public Sign getSign() {
		if (distance > Constants.epsilon) {
			return Sign.Positive;
		} else if (distance < -Constants.epsilon) {
			return Sign.Negative;
		} else {
			return Sign.Zero;
		}
	}
	
}
