package DataMining;
/*******************************************************************************
 * Copyright (C) 2013 Francois PETITJEAN 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/ 

import java.util.ArrayList;

/**
 * This toy class show the use of DBA.
 * @author Francois Petitjean
 */
public class DBA {
	static final long serialVersionUID = 1L;

	private final static int NIL = -1;
	private final static int DIAGONAL = 0;
	private final static int LEFT = 1;
	private final static int UP = 2;

	/**
	 * This attribute is used in order to initialize only once the matrixes
	 */
	private final static int MAX_SEQ_LENGTH = 2000;

	/**
	 * store the cost of the alignment
	 */
	private static double[][] costMatrix = new double[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];
	
	/**
	 * store the warping path
	 */
	private static int[][] pathMatrix = new int[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];

	/**
	 * Store the length of the optimal path in each cell
	 */
	private static int[][] optimalPathLength = new int[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];


	/**
	 * Dtw Barycenter Averaging (DBA)
	 * @param C average sequence to update
	 * @param sequences set of sequences to average
	 */
	public static void DBA(double[] C, double[][] sequences) {

		final ArrayList<Double>[] tupleAssociation = new ArrayList[C.length];//创建一个List，其中每个元素都是double的arraylist，也就是二元arraylist
		for (int i = 0; i < tupleAssociation.length; i++) {
			tupleAssociation[i] = new ArrayList<Double>(sequences.length);//每个元素开辟一个长度为序列点个数的double的arrayList
		}
		int nbTuplesAverageSeq, i, j, indiceRes;
		double res = 0.0;
		int centerLength = C.length;
		int seqLength;

		for (double[] T : sequences) {//对于每条序列
			seqLength = T.length;//seqLength是该序列的点数，在本例中是20

			costMatrix[0][0] = distanceTo(C[0], T[0]);//平均序列的0号点到对应T序列的0号点的distance^2
			pathMatrix[0][0] = DBA.NIL;//路径设置为-1
			optimalPathLength[0][0] = 0;//最优路径设置为0

			for (i = 1; i < centerLength; i++) {//对于平均序列的每个点，从1号点开始（0号点已经设置好了）
				costMatrix[i][0] = costMatrix[i - 1][0] + distanceTo(C[i], T[0]);
				pathMatrix[i][0] = DBA.UP;
				optimalPathLength[i][0] = i;
			}
			for (j = 1; j < seqLength; j++) {//对于T序列的每个点，同样也是设置三个矩阵的值
				costMatrix[0][j] = costMatrix[0][j - 1] + distanceTo(T[j], C[0]);
				pathMatrix[0][j] = DBA.LEFT;
				optimalPathLength[0][j] = j;
			}

			for (i = 1; i < centerLength; i++) {//对于平均矩阵的每个点
				for (j = 1; j < seqLength; j++) {//遍历T矩阵的每个点
					indiceRes = DBA.ArgMin3(costMatrix[i - 1][j - 1], costMatrix[i][j - 1], costMatrix[i - 1][j]);
					pathMatrix[i][j] = indiceRes;
					switch (indiceRes) {
						case DIAGONAL:
							res = costMatrix[i - 1][j - 1];
							optimalPathLength[i][j] = optimalPathLength[i - 1][j - 1] + 1;
							break;
						case LEFT:
							res = costMatrix[i][j - 1];
							optimalPathLength[i][j] = optimalPathLength[i][j - 1] + 1;
							break;
						case UP:
							res = costMatrix[i - 1][j];
							optimalPathLength[i][j] = optimalPathLength[i - 1][j] + 1;
							break;
					}
					costMatrix[i][j] = res + distanceTo(C[i], T[j]);
				}
			}

			nbTuplesAverageSeq = optimalPathLength[centerLength - 1][seqLength - 1] + 1;

			i = centerLength - 1;
			j = seqLength - 1;

			for (int t = nbTuplesAverageSeq - 1; t >= 0; t--) {
				tupleAssociation[i].add(T[j]);
				switch (pathMatrix[i][j]) {
					case DIAGONAL:
						i = i - 1;
						j = j - 1;
						break;
					case LEFT:
						j = j - 1;
						break;
					case UP:
						i = i - 1;
						break;
				}

			}

		}
System.out.println("hi");
		for (int t = 0; t < centerLength; t++) {//对于average route的每个点

			C[t] = barycenter((tupleAssociation[t].toArray()));
		}
	}


	public static double Min3(final double a, final double b, final double c) {
		if (a < b) {
			if (a < c) {
				return a;
			} else {
				return c;
			}
		} else {
			if (b < c) {
				return b;
			} else {
				return c;
			}
		}
	}

	public static int ArgMin3(final double a, final double b, final double c) {
		if (a < b) {
			if (a < c) {
				return 0;
			} else {
				return 2;
			}
		} else {
			if (b < c) {
				return 1;
			} else {
				return 2;
			}
		}
	}

	public static double distanceTo(double a, double b) {
		return (a - b) * (a - b);
	}


	public static double barycenter(final Object... tab) {
		if (tab.length < 1) {
			throw new RuntimeException("empty double tab");
		}
		double sum = 0.0;
		sum = 0.0;
		for (Object o : tab) {
			sum += ((Double) o);
		}
		return sum / tab.length;
	}

	public static void main(String [] args){
		double [][]sequences = new double[100][];
		//获取100条sequence，每条有20个点
		for(int i=0;i<sequences.length;i++){
			sequences[i] = new double[20];
			for(int j=0;j<sequences[i].length;j++){
				sequences[i][j] = Math.cos(Math.random()*j/20.0*Math.PI) ;
			}
		}
		//averageSequence是一条平均序列，有20个点
		double [] averageSequence = new double[20];
		int choice = (int) Math.random()*100;
		//从100条序列中任意选20个点作为averageSequence的初始值
		for(int j=0;j<averageSequence.length;j++){
			averageSequence[j] = sequences[choice][j] ;
		}
		//对average Sequence进行输出
		System.out.print("[");
		for(int j=0;j<averageSequence.length;j++){
			System.out.print(averageSequence[j]+" ");
		}
		System.out.println("]");
		//调用DBA函数进行averageSequence的更新
		DBA(averageSequence, sequences);
		//对average Sequence进行输出
		System.out.print("[");
		for(int j=0;j<averageSequence.length;j++){
			System.out.print(averageSequence[j]+" ");
		}
		System.out.println("]");
		//调用DBA函数进行averageSequence的更新
		DBA(averageSequence, sequences);
		//对average Sequence进行输出
		System.out.print("[");
		for(int j=0;j<averageSequence.length;j++){
			System.out.print(averageSequence[j]+" ");
		}
		System.out.println("]");
	}
}
