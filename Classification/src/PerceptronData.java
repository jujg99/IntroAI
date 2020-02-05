import java.util.Random;

public class PerceptronData {
	int weights[][] = new int [PROW][PCOL];
	int bias;
	
	static int PROW = NumberClassification.PROW;
	static int PCOL = NumberClassification.PCOL;
	
	
	public void initialize() {
		Random r = new Random();
		for(int i = 0; i < PROW; i++) {
			for(int j = 0; j < PCOL; j++) {
				weights[i][j] = r.nextInt(20) + 5;
			}
		}
		bias = r.nextInt(20) + 5;
	}
}
