package cn.edu.tsinghua.cs.htm.shapes;

import java.util.*;

import cn.edu.tsinghua.cs.htm.utils.Constants;
import cn.edu.tsinghua.cs.htm.utils.HTMid;
import cn.edu.tsinghua.cs.htm.utils.Markup;
import cn.edu.tsinghua.cs.htm.utils.Pair;
import cn.edu.tsinghua.cs.htm.utils.Quadratic;
import cn.edu.tsinghua.cs.htm.utils.Sign;

/**
 * Triangular mesh on a unit sphere.
 * Three vertexes are assumed to be given in counter-clockwise order.
 * @author Haojia Zuo
 *
 */
public class Trixel {

	protected Cartesian[] v;
	
	protected Arc[] arcs;
	
	protected HTMid htmId;
	
	{
		v = new Cartesian[3];
		arcs = new Arc[3];
	}
	
	public Trixel(Cartesian v0, Cartesian v1, Cartesian v2, HTMid htmId) {
		v[0] = v0;
		v[1] = v1;
		v[2] = v2;
		arcs[0] = new Arc(v[0], v[1]);
		arcs[1] = new Arc(v[1], v[2]);
		arcs[2] = new Arc(v[2], v[0]);
		this.htmId = htmId;
	}
	
	/**
	 * Split a Trixel into 4 children
	 * @return List of 4 child Trixels
	 */
	public List<Trixel> expand() {
		List<Trixel> children = new ArrayList<Trixel>();
		
		Cartesian w0 = Cartesian.getMiddle(v[1], v[2]);
		Cartesian w1 = Cartesian.getMiddle(v[0], v[2]);
		Cartesian w2 = Cartesian.getMiddle(v[0], v[1]);
		
		children.add(new Trixel(v[0], w2, w1, htmId.getChild(0)));
		children.add(new Trixel(v[1], w0, w2, htmId.getChild(1)));
		children.add(new Trixel(v[2], w1, w0, htmId.getChild(2)));
		children.add(new Trixel(w0, w1, w2, htmId.getChild(3)));
		
		return children;
	}
	
	/**
	 * Get the Trixel's space relation to a Convex
	 * @param convex
	 * @return Enumeration type Markup: Full, Partial, Outside or Undefined
	 */
	public Markup getMarkup(Convex convex) {
		Sign convexSign = convex.getSign();
		Markup markup = Markup.Undefined;
		switch (convexSign) {
		case Positive:
		case Zero:
			markup = getMarkupPositive(convex);
		case Negative:
			markup = getMarkupNegative(convex);
		case Mixed:
			markup = getMarkupMixed(convex);
		}
		return markup;
	}
	
	protected Markup getMarkupPositive(Convex convex) {
		if (convex == null) {
			return Markup.Undefined;
		}
		
		if (convex.halfspaces.isEmpty()) {
			return Markup.Undefined;
		}
		
		// Count corners which are inside all the Halfspaces
		int insideCornersCount = numOfInsideCorners(convex);
		
		if (insideCornersCount == 3) {
			return Markup.Full;
		} else if (insideCornersCount > 0) {
			return Markup.Partial;
		}
		
		// Judge if bounding circle overlaps all Halfspaces
		Halfspace boudingCircle = getBoundingCircle();
		for (Halfspace halfspace : convex.halfspaces) {
			if (!halfspace.overlap(boudingCircle)) {
				return Markup.Outside;
			}
		}
		
		// Get the smallest Halfspace
		Halfspace smallestHalfspace = convex.halfspaces.get(0);
		double maxDistance = smallestHalfspace.distance;
		for (Halfspace halfspace : convex.halfspaces) {
			double distance = halfspace.distance;
			if (distance > maxDistance) {
				smallestHalfspace = halfspace;
				maxDistance = distance;
			}
		}
		
		// Find an arc intersecting with the smallest Halfspace
		// if there is one
		Arc arcIntersect = null;
		for (int i = 0; i < 3; i++) {
			Arc arc = arcs[i];
			
			Quadratic quadratic = arc.intersectHalfspace(smallestHalfspace);
			
			if (quadratic.numOfRoots() == 2) {
				Pair<Double, Double> roots = quadratic.getRoots();
				double r1 = roots.a, r2 = roots.b;
				if (r1 > Constants.epsilon && r1 < 1 - Constants.epsilon ||
					r2 > Constants.epsilon && r2 < 1 - Constants.epsilon) {
					arcIntersect = arc;
					break;
				}
			}
			
		}
		
		// There is one arc intersecting with the smallest Halfspace
		// and there exists at least a root in range(0, 1)
		// in other words, the intersection(s) is on the edge of Trixel
		// Then we judge if it is a good intersection
		// that is, on the constraint of the convex
		// If so, Partial.
		if (arcIntersect != null) {
			boolean intersectAll = true;
			for (Halfspace halfspace : convex.halfspaces) {
				if (halfspace == smallestHalfspace) {
					continue;
				}
				// If the intersection(s) is a good one,
				// then for all other bigger Halfspaces,
				// they should all have two roots with the arc's big circle
				Quadratic quadratic = arcIntersect.intersectHalfspace(halfspace);
				if (quadratic.numOfRoots() != 2) {
					intersectAll = false;
					break;
				}
			}
			// Good intersection(s)
			if (intersectAll) {
				return Markup.Partial;
			}
		}
		
		// Late to this stage, the only possibilities are
		// 1) The convex is inside the Trixel
		// 2) Outside but near (bounding circle overlaps with convex)
		for (Cartesian vertex : convex.vertices) {
			// case 1)
			// Actually all vertices should be in Trixel
			// Just in case of double's precision problem
			if (this.contains(vertex)) {
				return Markup.Partial;
			}
		}
		
		// case 2)
		return Markup.Outside;
	}
	
	protected Markup getMarkupNegative(Convex convex) {
		if (convex == null) {
			return Markup.Undefined;
		}
		
		if (convex.halfspaces.isEmpty()) {
			return Markup.Undefined;
		}
		
		// Count corners which are inside all the Halfspaces
		int insideCornersCount = numOfInsideCorners(convex);
		
		if (insideCornersCount == 3) {
			// Still have to judge if (part of) hole in Trixel
			// If any Halfspace's center is inside Trixel, then Partial
			for (Halfspace halfspace : convex.halfspaces) {
				if (this.contains(halfspace.vector)) {
					return Markup.Partial;
				}
			}
			// If any Halfspace intersects any edge, then Partial
			for (int i = 0; i < 3; i++) {
				Arc arc = arcs[i];
				for (Halfspace halfspace : convex.halfspaces) {
					Quadratic quadratic = arc.intersectHalfspace(halfspace);
					if (quadratic.numOfRoots() == 2) {
						Pair<Double, Double> roots = quadratic.getRoots();
						double r1 = roots.a;
						double r2 = roots.b;
						if (r1 > Constants.epsilon && r1 < 1 - Constants.epsilon ||
							r2 > Constants.epsilon && r2 < 1 - Constants.epsilon) {
							return Markup.Partial;
						}	
					}
				}
			}
			// No (part of) hole in Trixel
			return Markup.Full;
		} else if (insideCornersCount > 0) {
			return Markup.Partial;
		} else {
			// All 3 corners are out of the negative convex
			// that is, inside the holes
			// But there might be patches inside Trixel
			// If any Halfspace intersects any edge, then Partial
			// Might mistake Outside as Partial, but just for safety
			for (int i = 0; i < 3; i++) {
				Arc arc = arcs[i];
				for (Halfspace halfspace : convex.halfspaces) {
					Quadratic quadratic = arc.intersectHalfspace(halfspace);
					if (quadratic.numOfRoots() == 2) {
						Pair<Double, Double> roots = quadratic.getRoots();
						double r1 = roots.a;
						double r2 = roots.b;
						if (r1 > Constants.epsilon && r1 < 1 - Constants.epsilon ||
							r2 > Constants.epsilon && r2 < 1 - Constants.epsilon) {
							return Markup.Partial;
						}	
					}
				}
			}
			return Markup.Outside;
		}
	}
	
	protected Markup getMarkupMixed(Convex convex) {
		if (convex == null) {
			return Markup.Undefined;
		}
		
		if (convex.halfspaces.isEmpty()) {
			return Markup.Undefined;
		}
		
		// Count corners which are inside all the Halfspaces
		int insideCornersCount = numOfInsideCorners(convex);
		
		if (insideCornersCount == 3) {
			// If any Halfspace intersects any edge, then Partial
			for (int i = 0; i < 3; i++) {
				Arc arc = arcs[i];
				for (Halfspace halfspace : convex.halfspaces) {
					Quadratic quadratic = arc.intersectHalfspace(halfspace);
					if (quadratic.numOfRoots() == 2) {
						Pair<Double, Double> roots = quadratic.getRoots();
						double r1 = roots.a;
						double r2 = roots.b;
						if (r1 > Constants.epsilon && r1 < 1 - Constants.epsilon ||
							r2 > Constants.epsilon && r2 < 1 - Constants.epsilon) {
							return Markup.Partial;
						}	
					}
				}
			}
			return Markup.Full;
		} else if (insideCornersCount > 0) {
			return Markup.Partial;
		} else {
			// If Trixel outside of any positive Halfspace
			// then certainly outside of the convex
			for (Halfspace halfspace : convex.halfspaces) {
				if (halfspace.getSign() == Sign.Positive &&
					getMarkup(halfspace) == Markup.Outside) {
					return Markup.Outside;
				}
			}
			// All other situations are marked as Partial
			// for the sake of reducing computation
			return Markup.Partial;
		}
	}
	
	/**
	 * Get the Trixel's space relation to a Halfspace
	 * @param halfspace
	 * @return Enumeration type Markup: Full, Partial, Outside or Undefined
	 */
	public Markup getMarkup(Halfspace halfspace) {
		if (halfspace == null) {
			return Markup.Undefined;
		}
		
		int insideCornersCount = 0;
		for (int i = 0; i < 3; i++) {
			Cartesian corner = v[i];
			if (halfspace.contains(corner)) {
				insideCornersCount++;
			}
		}
		
		if (insideCornersCount == 3) {
			return Markup.Full;
		} else if (insideCornersCount > 0) {
			return Markup.Partial;
		}
		
		// If bounding circle not overlaps with Halfspace,
		// then Outside
		Halfspace boundingCircle = getBoundingCircle();
		if (!boundingCircle.overlap(halfspace)) {
			return Markup.Outside;
		}
		
		// If Halfspace intersects any side, then partial
		for (int i = 0; i < 3; i++) {
			Arc arc = arcs[i];
			Quadratic quadratic = arc.intersectHalfspace(halfspace);
			if (quadratic.numOfRoots() == 2) {
				Pair<Double, Double> roots = quadratic.getRoots();
				double r1 = roots.a;
				double r2 = roots.b;
				if (r1 > Constants.epsilon && r1 < 1 - Constants.epsilon ||
					r2 > Constants.epsilon && r2 < 1 - Constants.epsilon) {
					return Markup.Partial;
				}
			}
		}
		
		// Only possibilities:
		// 1) Halfspace inside Trixel
		// 2) Outside, but near (bounding circle overlaps with Halfspace)
		
		// Judge if Halfspace in Trixel
		// by judging if Halfspace's center in Trixel
		if (this.contains(halfspace.vector)) {
			return Markup.Partial;
		}
		
		return Markup.Outside;
	}
	
	/**
	 * Get the number of corners which are inside a Convex
	 * that is, inside all Halfspaces of the Convex
	 * @param convex
	 * @return int: 0, 1, 2 or 3
	 */
	protected int numOfInsideCorners(Convex convex) {
		int insideCornersCount = 0;
		for (int i = 0; i < 3; i++) {
			Cartesian corner = v[i];
			boolean insideAllHalfspaces = true;
			for (Halfspace halfspace : convex.halfspaces) {
				if (!halfspace.contains(corner)) {
					insideAllHalfspaces = false;
					break;
				}
			}
			if (insideAllHalfspaces) {
				insideCornersCount++;
			}
		}
		return insideCornersCount;
	}
	
	/**
	 * Get the Trixel's bounding circle in the form of Halfspace
	 * @return bounding circle as Halfspace object
	 */
	protected Halfspace getBoundingCircle() {
		Cartesian vb = v[1].sub(v[0]).cross(v[2].sub(v[1])).normalize();
		double db = v[0].dot(vb);
		return new Halfspace(vb, db);
	}
	
	/**
	 * Judge if point p in the Trixel
	 * @param p
	 * @return true if p in Trixel
	 */
	protected boolean contains(Cartesian p) {
		Halfspace boundingCircle = getBoundingCircle();
		if(!boundingCircle.contains(p)) {
			return false;
		}
		
		// Get the intersection point
		// of vector p and the Trixel's cutting plane
		double lambda = boundingCircle.distance / (p.dot(boundingCircle.vector));
		Cartesian inPlane = p.multiply(lambda);
		
		// Then it's judging if point in triangular in 2D
		Cartesian pa = v[0].sub(inPlane);
		Cartesian pb = v[1].sub(inPlane);
		Cartesian pc = v[2].sub(inPlane);
		Cartesian cross1 = pa.cross(pb);
		Cartesian cross2 = pb.cross(pc);
		Cartesian cross3 = pc.cross(pa);
		
		if (cross1.dot(cross2) < -Constants.epsilon) {
			return false;
		}
		if (cross2.dot(cross3) < -Constants.epsilon) {
			return false;
		}
		if (cross3.dot(cross1) < -Constants.epsilon) {
			return false;
		}
		return true;
	}
	
	public HTMid getHTMid() {
		return htmId;
	}
	
}
