
public class FaceTrainingImage {
	int label;
	int[][] features = new int[ROW][COL];
	
	static int ROW = FaceClassification.ROW;
	static int COL = FaceClassification.COL;
	
	public FaceTrainingImage(int label) {
		this.label = label;
	}
	
	public void addToFeature(int row, String line) {
		for(int i = 0; i < 60; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row/(70/ROW)][i/(60/COL)] ++;
			}
		}
	}
}
