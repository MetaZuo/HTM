package cn.edu.tsinghua.cs.htm.utils;

import cn.edu.tsinghua.cs.htm.shapes.Cartesian;

/**
 * Convert latitude and longitude to Cartesian
 * @author Haojia Zuo
 *
 */
public class Latlon2Cartesian {
	
	public static Cartesian parse(double latitude, double longitude) {
		double latRadian = Math.PI * latitude / 180.0;
		double lonRadian = Math.PI * longitude / 180.0;
		double x = Constants.scale * Math.cos(latRadian) * Math.cos(lonRadian);
		double y = Constants.scale * Math.cos(latRadian) * Math.sin(lonRadian);
		double z = Constants.scale * Math.sin(latRadian);
		return new Cartesian(x, y, z);
	}

}
