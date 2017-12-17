package cn.edu.tsinghua.cs.htm.utils;

/**
 * A quadratic equation
 * @author Haojia Zuo
 *
 */
public class Quadratic {
	
	private double a, b, c;
	
	private double delta;
	
	private int numOfRoots;
	
	public Quadratic(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
		delta = b * b - 4 * a * c;
		if (delta > Constants.epsilon) {
			numOfRoots = 2;
		} else if (delta < -Constants.epsilon) {
			numOfRoots = 0;
		} else {
			numOfRoots = 1;
		}
	}
	
	/**
	 * Number of roots of the equation
	 * @return int: 0, 1, or 2
	 */
	public int numOfRoots() {
		return numOfRoots;
	}
	
	/**
	 * Roots of the equation in the form of Pair
	 * The first element is the smaller one
	 * Two NaNs if no roots or illegal
	 * NaN in the second if degenerates to linear
	 * Two the same if delta == 0
	 * @return small root, big root in Pair<Double, Double>
	 */
	public Pair<Double, Double> getRoots() {
		if (a < Constants.epsilon && a > -Constants.epsilon) {
			if (b < Constants.epsilon && b > -Constants.epsilon) {
				return new Pair<Double, Double>(Double.NaN, Double.NaN);
			}
			double r = - c / b;
			return new Pair<Double, Double>(r, Double.NaN);
		}
		if (numOfRoots == 2) {
			double r1 = (- b - Math.sqrt(delta)) / (2 * a);
			double r2 = (- b + Math.sqrt(delta)) / (2 * a);
			return new Pair<Double, Double>(r1, r2);
		} else if (numOfRoots == 1) {
			double r = - b / (2 * a);
			return new Pair<Double, Double>(r, r);
		} else {
			return new Pair<Double, Double>(Double.NaN, Double.NaN);
		}
	}

}
