
public class FacePerceptronTrainingImage {
	int label;
	int[][] features = new int[PROW][PCOL];
	
	static int PROW = FaceClassification.PROW;
	static int PCOL = FaceClassification.PCOL;
	
	public FacePerceptronTrainingImage(int label) {
		this.label = label;
	}
	
	public void addToFeature(int row, String line) {
		for(int i = 0; i < 60; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row/(70/PROW)][i/(60/PCOL)] ++;
			}
		}
	}
}
