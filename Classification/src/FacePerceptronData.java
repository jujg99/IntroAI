import java.util.Random;

public class FacePerceptronData {
	int weights[][] = new int [PROW][PCOL];
	int bias;
	
	static int PROW = FaceClassification.PROW;
	static int PCOL = FaceClassification.PCOL;
	
	
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
