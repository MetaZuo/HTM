package cn.edu.tsinghua.cs.htm.shapes;

import cn.edu.tsinghua.cs.htm.shapes.Cartesian;
import cn.edu.tsinghua.cs.htm.shapes.Halfspace;
import cn.edu.tsinghua.cs.htm.utils.Quadratic;

/**
 * Spherical arc
 * two points on the sphere linked by part of a great circle
 * Represents an edge of a Trixel
 * @author zhj
 *
 */
public class Arc {
	
	protected Cartesian v1, v2;
	
	protected double cosAngular;
	
	public Arc(Cartesian v1, Cartesian v2) {
		this.v1 = v1;
		this.v2 = v2;
		cosAngular = v1.dot(v2) / v1.length() / v2.length();
	}
	
	/**
	 * Intersect an Arc with a Halfspace
	 * Returns an object of Quadratic - a quadratic equation
	 * Note that the roots might not be actual intersections "on" the edge
	 * Only roots in range (0, 1) are between the two terminals of the arc
	 * while others are on the extending great circle
	 * @param halfspace
	 * @return object of Quadratic
	 */
	public Quadratic intersectHalfspace(Halfspace halfspace) {
		double uSquare = (1 - cosAngular) / (1 + cosAngular);
		double gamma1 = halfspace.vector.dot(v1);
		double gamma2 = halfspace.vector.dot(v2);
		
		double a = -uSquare * (gamma1 + halfspace.distance);
		double b = gamma1 * (uSquare - 1) + gamma2 * (uSquare + 1);
		double c = gamma1 - halfspace.distance;
		
		Quadratic quadratic = new Quadratic(a, b, c);
		return quadratic;
	}

}
