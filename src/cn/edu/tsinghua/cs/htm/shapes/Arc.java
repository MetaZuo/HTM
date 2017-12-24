package cn.edu.tsinghua.cs.htm.shapes;

import cn.edu.tsinghua.cs.htm.shapes.Cartesian;
import cn.edu.tsinghua.cs.htm.shapes.Halfspace;

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
	 * Returns an object of ArcInterHS: functional class of intersecting
	 * @param halfspace
	 * @return object of ArcInterHS
	 */
	public ArcInterHS intersectHalfspace(Halfspace halfspace) {
		return new ArcInterHS(this, halfspace);
	}

}
