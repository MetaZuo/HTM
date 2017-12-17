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
		halfspaces = new ArrayList<Halfspace>();
		vertices = new ArrayList<Cartesian>();
	}
	
	public void buildByVertices(Collection<? extends Cartesian> vertices) {
		this.vertices.addAll(vertices);
		// TODO calculate halfspaces
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
	
}
