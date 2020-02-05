import java.io.BufferedReader;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class NumberClassification {
	
	static List<TrainingImage> fullImageList;
	static List<TrainingImage> images;
	static int ROW = 28;
	static int COL = 7;
	
	
	static List<PerceptronData> functions = new ArrayList<PerceptronData>();
	static List<PerceptronTrainingImage> fullImageListPerceptron = new ArrayList<PerceptronTrainingImage>();
	static List<PerceptronTrainingImage> imagesPerceptron;
	static int PROW = 28;
	static int PCOL = 7;
	
	public static void getTrainingData() throws IOException {
		
		BufferedReader training = new BufferedReader( new FileReader( "trainingimages" ) );
		BufferedReader label = new BufferedReader( new FileReader( "traininglabels" ) );
		String line = training.readLine();
		String value = "";

		fullImageList = new ArrayList<TrainingImage>();
		int spot = -1;
	    while(line != null){
	    	for(int i = 0; i < 28; i++) {
	    		if(i == 0) {
	    			spot++;
	    			value = label.readLine();
		    		//make new entry for this image with value
		    		fullImageList.add(new TrainingImage(Integer.parseInt(value)));
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
		//in first 4 rows
		for(int i = 0; i < 28; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row][i/4] ++;
			}
		}
	}
	
	public static double naiveBayes() throws IOException {
		BufferedReader test = new BufferedReader( new FileReader( "testimages" ) );
		BufferedReader label = new BufferedReader( new FileReader( "testlabels" ) );
		
		String line = test.readLine();
		String value = "";

		
		int l = 0;
		double freq = 0.0;
		double totaltests = 0.0;
		double totalcorrect = 0.0;
		int[][] features = new int[ROW][COL];
		int t = 0;
	    while(line != null){
	    	for(int i = 0; i < 28; i++) {
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
	    	//for each number 1-9 get the max P(x|y)*P(y) ...where y is 1-9
    		//P(x|y) is multiply all prob of each feature given y
    		double max = Double.MIN_VALUE;
    		int number = 0;
    		for(int i = 0; i < 10; i++) {
    			//P(y) ... prob of i occurring in training set
    			freq = getFrequencyOfValue(i);
    			
    			//get prob of each feature given a value i 
    			freq = freq * getFrequencyOfFeatures(i, features);
    			
    			if(freq > max) {
    				max = freq;
    				number = i;
    			}
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
	
	public static void getPercentData(double percent) {
		images = new ArrayList<TrainingImage>(fullImageList);
		if(percent == 1) {
			return;
		}
		
		Random rand = new Random(); 
		for(int i = 0; i <= ((1-percent) * 5000); i ++) {
			int randomIndex = rand.nextInt(images.size()); 
			images.remove(randomIndex);
		}
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
	
	public static void getFeaturesPerceptron(int row, int [][]features, String line) {
		//in first 4 rows
		for(int i = 0; i < 28; i++) {
			if(line.charAt(i) == '#' || line.charAt(i) == '+') {
				features[row/(28/PROW)][i/(28/PCOL)] ++;
			}
		}
	}
	
	public static void getTrainingDataPerceptron() throws IOException {
		BufferedReader training = new BufferedReader( new FileReader( "trainingimages" ) );
		BufferedReader label = new BufferedReader( new FileReader( "traininglabels" ) );
		
		String line = training.readLine();
		String value = "";
		int spot = -1;
	    while(line != null){
	    	for(int i = 0; i < 28; i++) {
	    		if(i == 0) {
	    			spot++;
	    			value = label.readLine();
		    		//make new entry for this image with value
		    		fullImageListPerceptron.add(new PerceptronTrainingImage(Integer.parseInt(value)));
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
	
	public static void getPercentDataPerceptron(double percent) {
		imagesPerceptron = new ArrayList<PerceptronTrainingImage>(fullImageListPerceptron);
		if(percent == 1) {
			return;
		}
		
		Random rand = new Random(); 
		for(int i = 0; i <= ((1-percent) * 5000); i ++) {
			int randomIndex = rand.nextInt(imagesPerceptron.size()); 
			imagesPerceptron.remove(randomIndex);
		}
	}
	
	public static void perceptronTraining(int iterations) throws IOException {
		
		//initialize weights to random small ints
		for(int i = 0; i < 10; i++) {
			functions.add(new PerceptronData());
			functions.get(i).initialize();
		}
		
		int round = 0;
		while(round != iterations) {
			round++;
			
			int s = 0;
			while(s != imagesPerceptron.size()){
		    	//go through each function, get max
		    	int f = 0, number = 0;
		    	int max = Integer.MIN_VALUE;
		    	for(int x = 0; x < 10; x++) {
		    		f = functions.get(x).bias;
		    		for(int i = 0; i < PROW; i++) {
			    		for(int j = 0; j < PCOL; j++) {
			    			f = f + functions.get(x).weights[i][j] * imagesPerceptron.get(s).features[i][j];
			    		}
			    	
			    		if(f > max) {
			    			max = f;
			    			number = x;
			    		}
			    	}
		    	}

		    	if(imagesPerceptron.get(s).label == number) {
	    		}else {
	    			for(int i = 0; i < PROW; i++) {
	    				for(int j = 0; j < PCOL; j++) {
	    					//lower weights of the function that gave max
	    					functions.get(number).weights[i][j] = functions.get(number).weights[i][j] - imagesPerceptron.get(s).features[i][j];
	    					functions.get(number).bias = functions.get(number).bias - 1;
	    					
	    					//increase weights of the function that was supposed to give max
	    					int n = imagesPerceptron.get(s).label;
	    					functions.get(n).weights[i][j] = functions.get(n).weights[i][j] + imagesPerceptron.get(s).features[i][j];
	    					functions.get(n).bias = functions.get(n).bias + 1;
	    				}
	    			}
	    		}
	    		s++;
			}
		}
	}
	
	public static double perceptron() throws IOException {
		
		BufferedReader test = new BufferedReader( new FileReader( "testimages" ) );
		BufferedReader label = new BufferedReader( new FileReader( "testlabels" ) );
		
		String line = test.readLine();
		String value = "";

		
		double totaltests = 0.0;
		double totalcorrect = 0.0;
		int[][] features = new int[PROW][PCOL];
	    while(line != null){
	    	for(int i = 0; i < 28; i++) {
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
	    	int max = Integer.MIN_VALUE;
	    	for(int x = 0; x < 10; x++) {
	    		f = functions.get(x).bias;
	    		for(int i = 0; i < PROW; i++) {
		    		for(int j = 0; j < PCOL; j++) {
		    			f = f + functions.get(x).weights[i][j] * features[i][j];
		    		}
		    		if(f > max) {
		    			max = f;
		    			number = x;
		    		}
		    	}
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
		BufferedReader test = new BufferedReader( new FileReader( "testimages" ) );
		BufferedReader label = new BufferedReader( new FileReader( "testlabels" ) );
		
		String line = test.readLine();
		String value = "";

		
		double totaltests = 0.0;
		double totalcorrect = 0.0;
		int[][] features = new int[PROW][PCOL];
		while(line != null){
	    	for(int i = 0; i < 28; i++) {
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

	public static void printData(double[] data, long[] time, int percent) {
		
		//mean
		double mean = 0;
		long avgtime = 0;
		for(int i = 0; i < data.length; i++) {
			mean = mean + data[i];
			avgtime = avgtime + time[i];
		}
		mean = mean/data.length;
		avgtime = avgtime/time.length;
		System.out.println("Mean of " + percent + "% is " + mean);
		
		//std
		double sd = 0;
		for(int i=0; i < data.length; i++){
		    sd = sd + Math.pow(data[i] - mean, 2);
		}
		System.out.println("Standard deviation of " + percent + "% is " + sd);
		System.out.println("Average elapsed time of " + percent + "% is " + avgtime + " milliseconds");
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException {
		int iterations = 5;
		double data[] = new double[iterations];
		long time[] = new long[iterations];
		
		//NUMBER: NAIVE BAYES
		System.out.println("NUMBER: NAIVE BAYES");
		getTrainingData();
		for(double j = 100; j <= 100; j = j + 10) {
			for(int i = 0; i < iterations; i++) {
				getPercentData(j/100);
				long startTime = System.currentTimeMillis();
				data[i] = naiveBayes();
				time[i] = System.currentTimeMillis() - startTime;
			}
			printData(data, time, (int)j);
		}
		
		
		//NUMBER: PERCEPTRON
		System.out.println("NUMBER: PERCEPTRON");
		getTrainingDataPerceptron();
		for(double j = 100; j <= 100; j = j + 10) {
			for(int i = 0; i < iterations; i++) {
				getPercentDataPerceptron(j/100);
				long startTime = System.currentTimeMillis();
				perceptronTraining(5);
				time[i] = System.currentTimeMillis() - startTime;
				data[i] = perceptron();
			}
			printData(data, time, (int)j);
		}
		
		
		//NUMBER: NEAREST NIEGHBOR
		System.out.println("NUMBER: NEAREST NEIGHBOR");
		getTrainingData();
		for(double j = 100; j <= 100; j = j + 10) {
			for(int i = 0; i < iterations; i++) {
				getPercentData(j/100);
				long startTime = System.currentTimeMillis();
				data[i] = nearestNeighbor();
				time[i] = System.currentTimeMillis() - startTime;
			}
			printData(data, time, (int)j);
		}
		
		
		//FACE: NAIVE BAYES
		System.out.println("FACE: NAIVE BAYES");
		FaceClassification.getTrainingData();
		for(double j = 100; j <= 100; j = j + 10) {
			for(int i = 0; i < iterations; i++) {
				FaceClassification.getPercentData(j/100);
				long startTime = System.currentTimeMillis();
				data[i] = FaceClassification.naiveBayes();
				time[i] = System.currentTimeMillis() - startTime;
			}
			printData(data, time, (int)j);
		}
		
		
		//FACE: PERCEPTRON
		System.out.println("FACE: PERCEPTRON");
		FaceClassification.getTrainingDataPerceptron();
		for(double j = 100; j <= 100; j = j + 10) {
			for(int i = 0; i < iterations; i++) {
				FaceClassification.getPercentDataPerceptron(j/100);
				long startTime = System.currentTimeMillis();
				FaceClassification.perceptronTraining(3);
				time[i] = System.currentTimeMillis() - startTime;
				data[i] = FaceClassification.perceptron();
			}
			printData(data, time, (int)j);
		}


		//FACE: NEAREST NIEGHBOR
		System.out.println("FACE: NEAREST NEIGHBOR");
		FaceClassification.getTrainingData();
		for(double j = 100; j <= 100; j = j + 10) {
			for(int i = 0; i < iterations; i++) {
				FaceClassification.getPercentData(j/100);
				long startTime = System.currentTimeMillis();
				data[i] = FaceClassification.nearestNeighbor();
				time[i] = System.currentTimeMillis() - startTime;
			}
			printData(data, time, (int)j);
		}
	}
	
}
