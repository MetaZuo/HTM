package cn.edu.tsinghua.cs.htm;

import cn.edu.tsinghua.cs.htm.shapes.Cartesian;
import cn.edu.tsinghua.cs.htm.shapes.Halfspace;

public class Main {
	
	public static void main(String[] args) {
		// The project is still under developing.
		// Nothing here.
		Halfspace h1 = new Halfspace(new Cartesian(0, 0, 1), 0.5);
		Halfspace h2 = new Halfspace(new Cartesian(0, 0, 1), 0.6);
		System.out.println(h1.contains(h2));
	}

}
