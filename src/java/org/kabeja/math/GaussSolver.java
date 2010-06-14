/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.kabeja.math;

public class GaussSolver implements Solver {

	public double[] solve(double[][] matrix, double[] b) {
	
		if (matrix.length == matrix[0].length) {
			for (int i = 0; i < matrix.length; i++) {
				for (int m = i+1; m < matrix.length; m++) {
					

					double l = matrix[m][i] / matrix[i][i];
					for (int k = 0; k < matrix[m].length; k++) {
						// substract the above row from the current row
						matrix[m][k] = matrix[m][k] - l * matrix[i][k];
					}
					b[m]=b[m]-l*b[i];		
				}			
			}
		}
	
		double[] r = new double[b.length];
		for(int i=b.length-1;i>=0;i--){
			double sum =0.0;
			for(int k=matrix.length-1;k>i;k--){
				sum +=matrix[i][k]*r[k];
			}
			r[i] = (b[i]-sum)/matrix[i][i];
		}
		
		
		
		return r;
	}
	
	
	public static void printMatrix(double[][] matrix){
		for (int j = 0; j < matrix.length; j++) {
			for (int h = 0; h < matrix[j].length; h++) {
				System.out.print(", " + matrix[j][h]);
			}
			System.out.println();
		}
	}

	
	
	public static void printVector(double[] v){
		for (int j = 0; j < v.length; j++) {
			
				System.out.println("" + v[j]);
			}
			
	}
	public static void main(String[] args) {
		double[][] matrix = new double[][] { { 1.35, -0.35, -1.0, 0, -0.35 },
				{ -0.35, 1.35, 0, 0, 0.35 }, { -1.0, 0, 1.35, 0.35, 0 },
				{ 0, 0, 0.35, 1.35, 0 }, { -0.35, 0.35, 0, 0, 1.35 } };

		double b[] = { 0, 0, 10, -10, 0 };
//		double[][] matrix = new double[][] { 
//				{ 2.0,1.0,1.0 },
//				{ 1.0,3.0,2.0 }, 
//				{ 2.0,2.0,1.0 },};
//
//		double b[] = { 3.0,2.0,2.0 };
        printMatrix(matrix);
		GaussSolver solver = new GaussSolver();
		double[] r= solver.solve(matrix, b);
	
		printVector(r);
		
	}

}
