package org.kabeja.math;

public interface Solver {

	/**
	 * Solve the given linear algebraic system and return the 
	 * result
	 * @param matrix
	 * @param b
	 * @return
	 */
	public double[] solve(double[][] matrix,double[] b);
	
	
}
