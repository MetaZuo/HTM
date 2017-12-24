package cn.edu.tsinghua.cs.htm.utils;


public class Pair<T1, T2> {
	
	public T1 a;
	public T2 b;
	
	public Pair(T1 a, T2 b) {
		this.a = a;
		this.b = b;
	}
	
	public Pair(Pair<T1, T2> that) {
		this.a = that.a;
		this.b = that.b;
	}

}
