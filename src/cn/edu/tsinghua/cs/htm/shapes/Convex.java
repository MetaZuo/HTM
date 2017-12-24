package cn.edu.tsinghua.cs.htm.shapes;

import java.util.*;

import cn.edu.tsinghua.cs.htm.utils.Sign;

/**
 * Convex is the intersection of Halfspaces.
 * In this project, we assume that a Convex is a simple connected region.
 * @author Haojia Zuo
 *
 */
public class Convex {
	
	List<Halfspace> halfspaces;
	List<Cartesian> vertices;
	
	public Convex() {
		halfspaces = new LinkedList<Halfspace>();
		vertices = new LinkedList<Cartesian>();
	}
	
	/**
	 * Input vertices in counter-clockwise order
	 * Two neighboring vertices will be connected by a great circle arc
	 * Thus we will build a zero-signed convex
	 * @param vertices in counter-clockwise order
	 */
	public void buildByVertices(Collection<? extends Cartesian> vertices) {
		if (vertices != null) {
			this.vertices.addAll(vertices);
			Iterator<? extends Cartesian> iter = vertices.iterator();
			if (vertices.size() >= 3) {
				Cartesian first = iter.next();
				Cartesian prev = first;
				while (iter.hasNext()) {
					Cartesian temp = iter.next();
					Cartesian v = prev.cross(temp);
					Halfspace halfspace = new Halfspace(v, 0);
					halfspaces.add(halfspace);
					prev = temp;
				}
				Cartesian v = prev.cross(first);
				Halfspace halfspace = new Halfspace(v, 0);
				halfspaces.add(halfspace);
				
				if (getSign() == Sign.Positive) {
					smallestFirst();
				}
			}
		}
	}
	
	public void buildByHalfspaces(Collection<? extends Halfspace> halfspaces) {
		this.halfspaces.addAll(halfspaces);
		// TODO calculate vertices
	}
	
	public void addHalfspace(Halfspace halfspace) {
		this.halfspaces.add(halfspace);
		// TODO update vertices
	}
	
	public void addHalfspaces(Collection<Halfspace> halfspaces) {
		this.halfspaces.addAll(halfspaces);
		// TODO update vertices
	}
	
	public void clear() {
		halfspaces.clear();
		vertices.clear();
	}
	
	public Sign getSign() {
		Sign sign = Sign.Zero;
		for (Halfspace halfspace : halfspaces) {
			Sign aSign = halfspace.getSign();
			switch (aSign) {
			case Positive:
				if (sign == Sign.Zero) {
					sign = Sign.Positive;
				} else if (sign == Sign.Negative) {
					return Sign.Mixed;
				}
				break;
			case Negative:
				if (sign == Sign.Zero) {
					sign = Sign.Negative;
				} else if (sign == Sign.Positive) {
					return Sign.Mixed;
				}
				break;
			case Zero:
			default:
				break;
			}
		}
		return sign;
	}
	
	private void smallestFirst() {
		// Get the smallest Halfspace
		Halfspace smallestHalfspace = halfspaces.get(0);
		double maxDistance = smallestHalfspace.distance;
		for (Halfspace halfspace : halfspaces) {
			double distance = halfspace.distance;
			if (distance > maxDistance) {
				smallestHalfspace = halfspace;
				maxDistance = distance;
			}
		}
		halfspaces.remove(smallestHalfspace);
		halfspaces.add(0, smallestHalfspace);
	}
	
	@Override
	public String toString() {
		String str = "Convex: {";
		for (Halfspace halfspace : halfspaces) {
			str += "\n    " + halfspace.toString();
		}
		str += "\n}";
		return str;
	}
	
}
