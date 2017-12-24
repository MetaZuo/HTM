package cn.edu.tsinghua.cs.htm;

import cn.edu.tsinghua.cs.htm.operations.Cover;
import cn.edu.tsinghua.cs.htm.utils.ProgramDriver;

public class Main {
	
	public static void main(String[] args) {
		// The project is still under developing.
		
		int exitCode = -1;
		ProgramDriver pgd = new ProgramDriver();
		
		try {
			
			pgd.addClass("Cover", Cover.class,
					"Covers a convex specified by 3 or more 3D points "
					+ "and returns HTMid ranges of covering Trixels.");
			
			pgd.driver(args);
			
			exitCode = 0;
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.exit(exitCode);
	}

}
