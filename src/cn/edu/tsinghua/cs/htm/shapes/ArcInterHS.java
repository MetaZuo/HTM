package cn.edu.tsinghua.cs.htm.shapes;

import cn.edu.tsinghua.cs.htm.utils.Constants;
import cn.edu.tsinghua.cs.htm.utils.Pair;
import cn.edu.tsinghua.cs.htm.utils.Quadratic;

public class ArcInterHS {
	
	private Cartesian arcVertex1, arcVertex2;
	
	private Quadratic quadratic;
	
	private double uSquare;
	
	private Pair<Double, Double> rootOnEdge;
	
	private Pair<Cartesian, Cartesian> intersections;
	
	public ArcInterHS(Arc arc, Halfspace halfspace) {
		this.arcVertex1 = arc.v1;
		this.arcVertex2 = arc.v2;
		
		double uSquare = (1 - arc.cosAngular) / (1 + arc.cosAngular);
		double gamma1 = halfspace.vector.dot(this.arcVertex1);
		double gamma2 = halfspace.vector.dot(this.arcVertex2);
		
		double a = -uSquare * (gamma1 + halfspace.distance);
		double b = gamma1 * (uSquare - 1) + gamma2 * (uSquare + 1);
		double c = gamma1 - halfspace.distance;
		
		this.quadratic = new Quadratic(a, b, c);
		this.uSquare = uSquare;
		this.rootOnEdge = new Pair<Double, Double>(null, null);
		this.intersections = null;
	}
	
	public boolean hasIntersection() {
		if (quadratic.isQuadratic() && quadratic.numOfRoots() == 2) {
			Pair<Double, Double> rootPair = quadratic.getRoots();
			double r1 = rootPair.a;
			double r2 = rootPair.b;
			if (r1 > Constants.epsilon && r1 < 1 - Constants.epsilon) {
				rootOnEdge.a = r1;
				return true;
			}
			if (r2 > Constants.epsilon && r2 < 1 - Constants.epsilon) {
				rootOnEdge.b = r2;
				return true;
			}
		}
		return false;
	}
	
	public Pair<Cartesian, Cartesian> getIntersections() {
		if (intersections != null) {
			return copyIntersections();
		}
		if (hasIntersection()) {
			intersections = new Pair<Cartesian, Cartesian>(null, null);
			if (rootOnEdge.a != null) {
				intersections.a = rootToCartesian(rootOnEdge.a);
			}
			if (rootOnEdge.b != null) {
				intersections.b = rootToCartesian(rootOnEdge.b);
			}
			return copyIntersections();
		}
		return null;
	}
	
	private Pair<Cartesian, Cartesian> copyIntersections() {
		if (intersections != null) {
			Cartesian inter1 = null, inter2 = null;
			if (intersections.a != null) {
				inter1 = new Cartesian(intersections.a);
			}
			if (intersections.b != null) {
				inter2 = new Cartesian(intersections.b);
			}
			return new Pair<Cartesian, Cartesian>(inter1, inter2);
		}
		return null;
	}
	
	private Cartesian rootToCartesian(double root) {
		Cartesian intersection = arcVertex1
				.multiply(1 + (uSquare * (1 - root) - 1) * root)
				.add(arcVertex2.multiply(root * (1 + uSquare)));
		return intersection;
	}

}
