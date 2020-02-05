
public class TrainingImage {
	int label;
	int[][] features = new int[28][7];
	
	public TrainingImage(int label) {
		this.label = label;
	}
	
	public void addToFeature(int row, String line) {
		//in first 4 rows
		for(int i = 0; i < 28; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row][i/4] ++;
			}
		}
	}
}
