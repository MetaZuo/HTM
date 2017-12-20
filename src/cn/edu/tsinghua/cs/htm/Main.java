package cn.edu.tsinghua.cs.htm;

import java.util.*;

import cn.edu.tsinghua.cs.htm.shapes.Cartesian;
import cn.edu.tsinghua.cs.htm.shapes.Convex;

public class Main {
	
	public static void main(String[] args) {
		// The project is still under developing.
		// Nothing here.
		List<Cartesian> vertices = new LinkedList<Cartesian>();
		vertices.add(new Cartesian(0.5, 0, Math.sqrt(3) / 2));
		vertices.add(new Cartesian(1, 0, 0));
		vertices.add(new Cartesian(0, 1, 0));
		vertices.add(new Cartesian(0, 0.5, Math.sqrt(3) / 2));
		Convex convex = new Convex();
		convex.buildByVertices(vertices);
		System.out.println(convex.toString());
	}

}
