import java.io.BufferedReader;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

//70 rows, 60 cols
public class FaceClassification {

	static List<FaceTrainingImage> fullImageList;
	static List<FaceTrainingImage> images;
	static int ROW = 7;
	static int COL = 6;
		
	static FacePerceptronData functions;
	static List<FacePerceptronTrainingImage> fullImageListPerceptron = new ArrayList<FacePerceptronTrainingImage>();
	static List<FacePerceptronTrainingImage> imagesPerceptron;
	static int PROW = 7;
	static int PCOL = 6;
		
	
	public static void getTrainingData() throws IOException {
		
		BufferedReader training = new BufferedReader( new FileReader( "facedatatrain" ) );
		BufferedReader label = new BufferedReader( new FileReader( "facedatatrainlabels" ) );
		String line = training.readLine();
		String value = "";

		fullImageList = new ArrayList<FaceTrainingImage>();
		int spot = -1;
	    while(line != null){
	    	for(int i = 0; i < 70; i++) {
	    		if(i == 0) {
	    			spot++;
	    			value = label.readLine();
		    		//make new entry for this image with value
		    		fullImageList.add(new FaceTrainingImage(Integer.parseInt(value)));
		    		fullImageList.get(spot).addToFeature(i, line);
		    		line = training.readLine();
	    		}else {
	    			fullImageList.get(spot).addToFeature(i, line);
	    			line = training.readLine();
	    		}
	    	}
	    }
	    training.close();
	    label.close();
	}
	
	public static void getFeatures(int row, int [][]features, String line) {
		
		for(int i = 0; i < 60; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row/(70/ROW)][i/(60/COL)] ++;
			}
		}
	}
	
	public static double naiveBayes() throws IOException {
		BufferedReader test = new BufferedReader( new FileReader( "facedatatest" ) );
		BufferedReader label = new BufferedReader( new FileReader( "facedatatestlabels" ) );
		
		String line = test.readLine();
		String value = "";

		
		int l = 0;
		double freqfalse = 0.0;
		double freqtrue = 0.0;
		double totaltests = 0.0;
		double totalcorrect = 0.0;
		int[][] features = new int[ROW][COL];
		int t = 0;
	    while(line != null){
	    	for(int i = 0; i < 70; i++) {
	    		t++;
	    		if(i == 0) {
	    			features = new int[ROW][COL];
	    			getFeatures(i, features, line);
		    		line = test.readLine();
	    		}else {
	    			getFeatures(i, features, line);
	    			line = test.readLine();
	    		}
	    	}
	    	// get the max P(x|y)*P(y) ...where y is 0 or 1
    		//P(x|y) is multiply all prob of each feature given y
    		
    		freqfalse = getFrequencyOfValue(0);
    		freqfalse = freqfalse * getFrequencyOfFeatures(0, features);
    			
    		freqtrue = getFrequencyOfValue(1);
    		freqtrue = freqtrue * getFrequencyOfFeatures(1, features);
    		
    		int number = 0;
    		if(freqfalse < freqtrue) {
    			number = 1;
    		}
    			
    		value = label.readLine();
    		totaltests++;
    		if(Integer.parseInt(value) == number) {
    			totalcorrect++;
    			//System.out.println("line: " + t);
    			//System.out.println("number: " + number);
    		}	
	    
	    }
	    test.close();
	    label.close();
	    
	    //System.out.println("Naive Bayes percent correct: " + totalcorrect/totaltests);
	    return totalcorrect/totaltests;
		
	}
	
	public static double getFrequencyOfFeatures(int value, int[][] feature) {
		double [][]freq = new double[ROW][COL];
		double total = 0.0;
		
		//go through each image
		for(int i = 0; i < images.size(); i++) {
			//if its the image value I'm computing, increase total
			if(images.get(i).label == value) {
				total++;
				//if the feature = then increase freq of it
				for(int f = 0; f < ROW ; f++) {
					for(int g = 0; g < COL; g++) {
						if(images.get(i).features[f][g] == feature[f][g]) {
							freq[f][g]++;
						}
					}
				}
			}
		}
		
		//now multiply the prob of each feature together
		double prob = 1.0;
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				prob = prob * (freq[i][j]/total);
			}
		}
		
		return prob;
	}
	
	public static double getFrequencyOfValue(int value) {
		double freq = 0.0;
		for(int i = 0; i < images.size(); i++) {
			if(images.get(i).label == value) {
				freq++;
			}
		}
		return freq/images.size();
	}
	
	public static void getTrainingDataPerceptron() throws IOException {
		BufferedReader training = new BufferedReader( new FileReader( "facedatatrain" ) );
		BufferedReader label = new BufferedReader( new FileReader( "facedatatrainlabels" ) );
		
		String line = training.readLine();
		String value = "";
		int spot = -1;
	    while(line != null){
	    	for(int i = 0; i < 70; i++) {
	    		if(i == 0) {
	    			spot++;
	    			value = label.readLine();
		    		//make new entry for this image with value
		    		fullImageListPerceptron.add(new FacePerceptronTrainingImage(Integer.parseInt(value)));
		    		fullImageListPerceptron.get(spot).addToFeature(i, line);
		    		line = training.readLine();
	    		}else {
	    			fullImageListPerceptron.get(spot).addToFeature(i, line);
	    			line = training.readLine();
	    		}
	    	}
	    }
	    training.close();
	    label.close();
	}
	
	public static void getFeaturesPerceptron(int row, int [][]features, String line) {
		for(int i = 0; i < 60; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row/(70/PROW)][i/(60/PCOL)] ++;
			}
		}
	}
	
	public static void getPercentDataPerceptron(double percent) {
		imagesPerceptron = new ArrayList<FacePerceptronTrainingImage>(fullImageListPerceptron);
		if(percent == 1) {
			return;
		}
		
		Random rand = new Random(); 
		for(int i = 0; i <= ((1-percent) * 451); i ++) {
			int randomIndex = rand.nextInt(imagesPerceptron.size()); 
			imagesPerceptron.remove(randomIndex);
		}
	}
	
	public static void perceptronTraining(int iterations) throws IOException {
		
		//initialize weights to random small ints
		functions = new FacePerceptronData();
		functions.initialize();
		
		int round = 0;
		while(round != iterations) {
			round++;
			
			int s = 0;
			while(s != imagesPerceptron.size()){
		    	//go through each function, get max
		    	int number = 0;
		    	int f = functions.bias;
		    	
		    	for(int i = 0; i < PROW; i++) {
			   		for(int j = 0; j < PCOL; j++) {
			   			f = f + functions.weights[i][j] * imagesPerceptron.get(s).features[i][j];
			   		}
			    }
		    	
		    	if(f > 0) {
		    		number = 1;
		    	}

		    	if(imagesPerceptron.get(s).label == number) {
	    		}else {
	    			//wasn't a face but said it was, so lower weights
	    			if(number == 1) {
	    				for(int i = 0; i < PROW; i++) {
		    				for(int j = 0; j < PCOL; j++) {
		    					functions.weights[i][j] = functions.weights[i][j] - imagesPerceptron.get(s).features[i][j];
		    					functions.bias = functions.bias - 1;
		    				}
		    			}
	    			}else {
	    				for(int i = 0; i < PROW; i++) {
		    				for(int j = 0; j < PCOL; j++) {
		    					functions.weights[i][j] = functions.weights[i][j] + imagesPerceptron.get(s).features[i][j];
		    					functions.bias = functions.bias + 1;
		    				}
	    				}
	    			}
	    			
	    		}
	    		s++;
			}
		}
	}
	
	public static double perceptron() throws IOException {
		
		BufferedReader test = new BufferedReader( new FileReader( "facedatatest" ) );
		BufferedReader label = new BufferedReader( new FileReader( "facedatatestlabels" ) );
		
		String line = test.readLine();
		String value = "";

		
		double totaltests = 0.0;
		double totalcorrect = 0.0;
		int[][] features = new int[PROW][PCOL];
	    while(line != null){
	    	for(int i = 0; i < 70; i++) {
	    		if(i == 0) {
	    			features = new int[PROW][PCOL];
	    			getFeaturesPerceptron(i, features, line);
		    		line = test.readLine();
	    		}else {
	    			getFeaturesPerceptron(i, features, line);
	    			line = test.readLine();
	    		}
	    	}
	    	
	    	//get max function
	    	int f = 0, number = 0;
	    	f = functions.bias;
	    	for(int i = 0; i < PROW; i++) {
		   		for(int j = 0; j < PCOL; j++) {
		   			f = f + functions.weights[i][j] * features[i][j];
		   		}
		    }
	    	if(f > 0) {
	    		number = 1;
	    	}
    		value = label.readLine();
    		totaltests++;
    		if(Integer.parseInt(value) == number) {
    			totalcorrect++;
    		}	
	    }
	    test.close();
	    label.close();
	    
	    return totalcorrect/totaltests;
	}
	
	public static double nearestNeighbor() throws IOException {
		BufferedReader test = new BufferedReader( new FileReader( "facedatatest" ) );
		BufferedReader label = new BufferedReader( new FileReader( "facedatatestlabels" ) );
		
		String line = test.readLine();
		String value = "";

		
		double totaltests = 0.0;
		double totalcorrect = 0.0;
		int[][] features = new int[PROW][PCOL];
		while(line != null){
	    	for(int i = 0; i < 70; i++) {
	    		if(i == 0) {
	    			features = new int[PROW][PCOL];
	    			getFeaturesPerceptron(i, features, line);
		    		line = test.readLine();
	    		}else {
	    			getFeaturesPerceptron(i, features, line);
	    			line = test.readLine();
	    		}
	    	}
	    	
	    	int number = smallestDiff(features);
	    	
    		value = label.readLine();
    		totaltests++;
    		if(Integer.parseInt(value) == number) {
    			totalcorrect++;
    		}	
	    }
	    test.close();
	    label.close();
	    
	    return totalcorrect/totaltests;
	}
	
	public static int smallestDiff(int[][] features) {
		
		int diff = 0;
		int min = Integer.MAX_VALUE;
		int nearest = 0;
		for(int i = 0; i < images.size(); i ++) {
			diff = 0;
			for(int r = 0; r < features.length; r++) {
				for(int c = 0; c < features[r].length; c++) {
					diff = diff + Math.abs(images.get(i).features[r][c] - features[r][c]);
				}
			}
			if(diff < min) {
				min = diff;
				nearest = images.get(i).label;
			}
		}
		return nearest;
	}
	
	public static void getPercentData(double percent) {
		images = new ArrayList<FaceTrainingImage>(fullImageList);
		if(percent == 1) {
			return;
		}
		
		Random rand = new Random(); 
		for(int i = 0; i <= ((1-percent) * 451); i ++) {
			int randomIndex = rand.nextInt(images.size()); 
			images.remove(randomIndex);
		}
	}
	
}

	
