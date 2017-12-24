package cn.edu.tsinghua.cs.htm.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ProgramDriver {
	
	Map<String, ProgramDescription> programs;
	
	public ProgramDriver() {
		programs = new HashMap<String, ProgramDescription>();
	}
	
	private static class ProgramDescription {
		
		static final Class<?>[] paramTypes = new Class<?>[] {String[].class};
		
		private Method main;
		
		private String description;
		
		public ProgramDescription(Class<?> mainClass, String description)
				throws NoSuchMethodException, SecurityException {
			this.main = mainClass.getMethod("main", paramTypes);
			this.description = description;
		}
		
		public void invoke(String[] args) throws Throwable {
			try {
				main.invoke(null, new Object[] {args});
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
		
		public String getDescription() {
			return description;
		}
		
	}
	
	private static void printUsage(Map<String, ProgramDescription> programs) {
		System.out.println("Usage:");
		for(Map.Entry<String, ProgramDescription> item : programs.entrySet()) {
			System.out.println("  " + item.getKey() + ": " +
					item.getValue().getDescription());         
		}
	}
	
	public void addClass(String name, Class<?> mainClass, String description)
			throws Throwable {
		programs.put(name, new ProgramDescription(mainClass, description));
	}
	
	public void driver(String[] args) throws Throwable {
		if (args.length == 0) {
			System.out.println("No operation specified.");
			printUsage(programs);
			System.exit(-1);
		}
		
		ProgramDescription pd = programs.get(args[0]);
		if (pd == null) {
			System.out.println("Unknown operation: " + args[0]);
			printUsage(programs);
			System.exit(-1);
		}
		
		String[] newArgs = new String[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			newArgs[i - 1] = args[i];
		}
		
		pd.invoke(newArgs);
	}

}
