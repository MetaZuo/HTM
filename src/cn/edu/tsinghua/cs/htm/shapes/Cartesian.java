package cn.edu.tsinghua.cs.htm.shapes;

/**
 * Vector in Cartesian coordination
 * @author Haojia Zuo
 *
 */
public class Cartesian {

	protected double x, y, z;
	
	public Cartesian() {
		x = y = z = Double.NaN;
	}
	
	public Cartesian(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Cartesian(Cartesian that) {
		this.x = that.x;
		this.y = that.y;
		this.z = that.z;
	}
	
	public double[] get() {
		double[] xyz = new double[3];
		xyz[0] = x;
		xyz[1] = y;
		xyz[2] = z;
		return xyz;
	}
	
	public double dot(Cartesian that) {
		return (x * that.x + y * that.y + z * that.z);
	}
	
	public Cartesian cross(Cartesian that) {
		return new Cartesian(
				y * that.z - z * that.y,
				z * that.x - x * that.z,
				x * that.y - y * that.x);
	}
	
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public Cartesian add(Cartesian that) {
		return new Cartesian(x + that.x, y + that.y, z + that.z);
	}
	
	public Cartesian sub(Cartesian that) {
		return new Cartesian(x - that.x, y - that.y, z - that.z);
	}
	
	public Cartesian multiply(double stretch) {
		return new Cartesian(x * stretch, y * stretch, z * stretch);
	}
	
	public Cartesian normalize() {
		double len = length();
		return new Cartesian(x / len, y / len, z / len);
	}

	public Cartesian scaleTo(int scale) {
	    double len = length();
	    return new Cartesian(x * scale / len, y * scale / len, z * scale / len);
    }
	
	public static Cartesian getMiddle(Cartesian p1, Cartesian p2) {
		double x = (p1.x + p2.x) / 2;
		double y = (p1.y + p2.y) / 2;
		double z = (p1.z + p2.z) / 2;
		return new Cartesian(x, y, z);
	}
	
	@Override
	public String toString() {
		String str = String.format("Cartesian: (%.3f, %.3f, %.3f)", x, y, z);
		return str;
	}
	
}
