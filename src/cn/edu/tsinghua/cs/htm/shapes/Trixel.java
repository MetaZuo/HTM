package cn.edu.tsinghua.cs.htm.shapes;

import java.util.*;

import cn.edu.tsinghua.cs.htm.utils.Constants;
import cn.edu.tsinghua.cs.htm.utils.HTMid;
import cn.edu.tsinghua.cs.htm.utils.Markup;
import cn.edu.tsinghua.cs.htm.utils.Pair;
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
			break;
		case Negative:
			markup = getMarkupNegative(convex);
			break;
		case Mixed:
			markup = getMarkupMixed(convex);
			break;
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
		
		// If any vertex of convex inside Trixel then Partial
		for (Cartesian vertex : convex.vertices) {
			if (this.contains(vertex)) {
				return Markup.Partial;
			}
		}
		
		// If any middle point of edge inside convex then Partial
		for (int i = 0; i < 3; i++) {
			Cartesian middle = Cartesian.getMiddle(v[i], v[(i + 1) % 3]);
			if (convex.containsStrict(middle)) {
				return Markup.Partial;
			}
		}
		
		// The smallest Halfspace is put at first when constructing convex
		for (Halfspace halfspace : convex.halfspaces) {
			// Are there any intersection between
			// any edge and any Halfspace
			boolean anyIntersection = false;
			
			// Check 3 edges
			for (int i = 0; i < 3; i++) {
				Arc arc = arcs[i];
				ArcInterHS intersect = arc.intersectHalfspace(halfspace);
				
				// This edge intersects with this halfspace
				if (intersect.hasIntersection()) {
					anyIntersection = true;
					
					Pair<Cartesian, Cartesian> intersections =
							intersect.getIntersections();
					
					// If a good intersection, i.e. in all other Halfspaces 
					// then Partial
					if (intersections.a != null) {
						boolean insideAllOthers = true;
						for (Halfspace another : convex.halfspaces) {
							if (another != halfspace &&
									!another.containsLoose(intersections.a)) {
								insideAllOthers = false;
								break;
							}
						}
						if (insideAllOthers) {
							return Markup.Partial;
						}
					}
					
					// Same as above
					if (intersections.b != null) {
						boolean insideAllOthers = true;
						for (Halfspace another : convex.halfspaces) {
							if (another != halfspace &&
									!another.containsLoose(intersections.b)) {
								insideAllOthers = false;
								break;
							}
						}
						if (insideAllOthers) {
							return Markup.Partial;
						}
					}
					
					// Here, both intersections are bad
					// No hurry, we will check the next edge
				}
				
				// Here, perhaps this edge doesn't intersect with Halfspace
				// Also we will check the next edge
			}
			
			// All 3 edges are checked
			// If no intersections with the Halfspace
			// then 2 possibilities:
			// 1) Trixel inside Halfspace
			//    can't judge if intersects with convex
			//    we continue to next Halfspace
			// 2) Trixel outside Halfspace or contains it
			if (!anyIntersection) {
				boolean inside = false;
				for (int i = 0; i < 3; i++) {
					if (halfspace.containsStrict(this.v[i])) {
						inside = true;
						break;
					}
				}
				if (!inside) {
					// Check containing
					Cartesian vertex0 = convex.vertices.get(0);
					Cartesian vertex1 = convex.vertices.get(1);
					Cartesian vertex2 = convex.vertices.get(2);
					Cartesian middle1 = Cartesian.getMiddle(vertex0, vertex1);
					Cartesian middle2 = Cartesian.getMiddle(vertex1, vertex2);
					Cartesian pointInside = Cartesian.getMiddle(middle1, middle2);
					if (this.contains(pointInside)) {
						return Markup.Partial;
					}
					// not containing, then Outside
					return Markup.Outside;
				}
				// Trixel inside Halfspace, continue
			}
			// Or there are intersections but all bad
			// Too difficult to decide, so we also continue
		}
		
		// All Halfspaces are checked
		// No good intersection
		// Perhaps Trixel containing convex but has bad intersections
		// or completely outside
		Cartesian vertex0 = convex.vertices.get(0);
		Cartesian vertex1 = convex.vertices.get(1);
		Cartesian vertex2 = convex.vertices.get(2);
		Cartesian middle1 = Cartesian.getMiddle(vertex0, vertex1);
		Cartesian middle2 = Cartesian.getMiddle(vertex1, vertex2);
		Cartesian pointInside = Cartesian.getMiddle(middle1, middle2);
		if (this.contains(pointInside)) {
			return Markup.Partial;
		}
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
					ArcInterHS intersect = arc.intersectHalfspace(halfspace);
					if (intersect.hasIntersection()) {
						return Markup.Partial;
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
					ArcInterHS intersect = arc.intersectHalfspace(halfspace);
					if (intersect.hasIntersection()) {
						return Markup.Partial;
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
					ArcInterHS intersect = arc.intersectHalfspace(halfspace);
					if (intersect.hasIntersection()) {
						return Markup.Partial;
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
		int onConstraintCount = 0;
		boolean hasAbsolutelyOutside = false;
		for (int i = 0; i < 3; i++) {
			Cartesian corner = v[i];
			if (halfspace.containsStrict(corner)) {
				insideCornersCount++;
			} else if (halfspace.containsLoose(corner)) {
				onConstraintCount++;
			} else {
				hasAbsolutelyOutside = true;
			}
		}
		if (!hasAbsolutelyOutside) {
			insideCornersCount += onConstraintCount;
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
			ArcInterHS intersect = arc.intersectHalfspace(halfspace);
			if (intersect.hasIntersection()) {
				return Markup.Partial;
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
	 * Strategy when handling those corners on constraint is:
	 * If there is any corner completely outside the convex,
	 * then those on constraint will not be counted.
	 * Else, count both inside and on constraint.
	 * e.g. 1 inside and 2 on constraint, return 3
	 * 0 inside, 2 on constraint, 1 outside, return 0
	 * @param convex
	 * @return int: 0, 1, 2 or 3
	 */
	protected int numOfInsideCorners(Convex convex) {
		int insideCornersCount = 0;
		int onConstraintCount = 0;
		boolean hasAbsolutelyOutside = false;
		for (int i = 0; i < 3; i++) {
			Cartesian corner = v[i];
			boolean insideAllStrict = true;
			boolean absolutelyOutside = false;
			for (Halfspace halfspace : convex.halfspaces) {
				if (!halfspace.containsLoose(corner)) {
					hasAbsolutelyOutside = true;
					absolutelyOutside = true;
					insideAllStrict = false;
					break;
				} else if (!halfspace.containsStrict(corner)) {
					insideAllStrict = false;
				}
			}
			if (insideAllStrict) {
				insideCornersCount++;
			} else if (!absolutelyOutside) {
				onConstraintCount++;
			}
		}
		if (!hasAbsolutelyOutside) {
			insideCornersCount += onConstraintCount;
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
	public boolean contains(Cartesian p) {
		Halfspace boundingCircle = getBoundingCircle();
		if(!boundingCircle.containsLoose(p)) {
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
		
		if (cross1.dot(cross2) < Constants.epsilon) {
			return false;
		}
		if (cross2.dot(cross3) < Constants.epsilon) {
			return false;
		}
		if (cross3.dot(cross1) < Constants.epsilon) {
			return false;
		}
		return true;
	}
	
	public HTMid getHTMid() {
		return htmId;
	}
	
}
