import java.util.*;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class EvaluationSystems{

	private static final float ALPHA = 0.9f;
	private static final float BETA = 3.0f;
	private static final float GAMMA = 0.5f;


	//////////////////////////////////////Breaking Sentences into n-grams///////////////////////////////////////////////
	private static ArrayList<String> nGrams(int n, String str){

		ArrayList<String> ngram = new ArrayList<String>();
		
		String [] words = str.split(" ");
		
		for(int i = 0; i < words.length - n + 1; i++){
			ngram.add(concat(words, i, i+n));
		}

		return ngram;
	}

	private static String concat(String [] words, int start, int end){

		StringBuilder sb = new StringBuilder();

		for(int i = start; i < end; i++){		
			sb.append((i > start ? " " : "") + words[i]);
		}

		return sb.toString();
	}

	///////////////////////////////////////////General methods//////////////////////////////////////////////////////////
	public static float root(float root, float num){

		return (float)Math.pow(Math.E, Math.log(num)/root);

	} 
	private static float numOfMatchingTokens(ArrayList<String> sentence, ArrayList<String> reference){

		float matchingTokens = 0;

		for(String token : sentence){
			if(reference.contains(token))
				matchingTokens+=1;
		}

		return matchingTokens;
	}

	private static float tokenLength(String sentence){

		return sentence.split(" ").length;

	}



	////////////////////////////////////////////BLEU Evaluation Methods///////////////////////////////////////////////////
	private static float [] precision(List<ArrayList<String>> sentence, List<ArrayList<String>> reference){

		float [] precision = new float[4]; 
		float matchingTokens;
		float totalTokens;

		for(ArrayList<String> sNgrams : sentence){
			matchingTokens = 0;
			for(String sNgram : sNgrams){

				for(ArrayList<String> rNgrams : reference){
					if(tokenLength(rNgrams.get(0)) == tokenLength(sNgram))
						if(rNgrams.contains(sNgram))
							matchingTokens+=1;
				}

				if(sNgrams.get(sNgrams.size()-1).equals(sNgram)){

					precision[(int)tokenLength(sNgram)-1] = (matchingTokens / sNgrams.size());

				}
					
			}
		}
		return precision;
	}

	private static float brevityPoint(String sentence, String reference){		// Brevity Point
		return (tokenLength(sentence) / tokenLength(reference) > 1) ? 1 : (tokenLength(sentence) / tokenLength(reference));
	}

	private static float BLEU_finalScore(float [] precision, float breakingPoint){

		float calculatedPrecision = 0;
		
		for(int j=0; j<precision.length; j++){
			calculatedPrecision = (j > 0 ? (calculatedPrecision * precision[j]) : precision[j]);
		}
		
		return root(4, calculatedPrecision) * breakingPoint;
	
	}

	/////////////////////////////////////////////METEOR Evaluation Methods////////////////////////////////////////////////
	private static float precision(ArrayList<String> sentence, ArrayList<String> reference){
		return (numOfMatchingTokens(sentence, reference) / sentence.size());
	}
	
	private static float recall (ArrayList<String> sentence, ArrayList<String> reference){
		return (numOfMatchingTokens(sentence, reference) / reference.size());
	}

	private static float sequence (ArrayList<String> sentence, ArrayList<String> reference){
		
		String matches = "";
		
		for(String token : sentence){
			if(reference.contains(token))
				matches = matches + 1;
			else matches = matches + 0;
		}
		
		return matches.split("0").length;
	
	}

	private static float fragPenalty (ArrayList<String> sentence, ArrayList<String> reference){
		return GAMMA * ( (float)Math.pow( (sequence(sentence, reference)/sentence.size()), BETA));
	}

	private static float f_mean (ArrayList<String> sentence, ArrayList<String> reference){
	
		float prec = precision(sentence, reference); 	//precision
		float rec = recall(sentence, reference);		//recall

		return (prec * rec)/(ALPHA * prec + (1 - ALPHA) * rec);
	
	}

	private static float METEOR_finalScore(float fragPenalty, float f_mean){
		return (1 - fragPenalty) * f_mean;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String [] args){

		List<ArrayList<String>> sentenceInNgrams = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> referenceInNgrams = new ArrayList<ArrayList<String>>();

		BufferedReader sentencesDataSet = null, referecesDataSet = null;

		String sentence = "";
		String reference = "";

		// If making a quick evaluation
		if (args.length != 2){
			 
			System.out.print("Input a sentence: ");
			sentence = System.console().readLine();

			System.out.print("Input a reference translation: ");
			reference = System.console().readLine();
		}
		// If making an evaluation using datasets
		else{
			try {
				sentencesDataSet = new BufferedReader(new FileReader(args[0]));
				referecesDataSet = new BufferedReader(new FileReader(args[1]));
			}
			catch(FileNotFoundException e){
				System.err.println(e.getMessage());
				System.exit(1);
			}

			String temp;
			try {
			
				while ((temp = sentencesDataSet.readLine()) != null){
					sentence = sentence.equals("") ? temp  : (sentence + " " + temp);
				}

				while ((temp = referecesDataSet.readLine()) != null){
					reference = reference.equals("") ? temp  : (reference + " " + temp);
				} 

			}
			catch (IOException ex) {
				System.err.println(ex.getMessage());
				System.exit(2);
			}
		}


		for(int i = 1; i < 5; i++){
			ArrayList<String> temp = nGrams(i, sentence);
			sentenceInNgrams.add(temp);
		}

		for(int i = 1; i < 5; i++){
			ArrayList<String> temp = nGrams(i, reference);
			referenceInNgrams.add(temp);
		}

		//BLEAU Evaluation
		float [] prec = precision(sentenceInNgrams, referenceInNgrams);
		float bp = brevityPoint(sentence, reference);
		System.out.println("The final score for BLEU is: " + BLEU_finalScore(prec, bp));

		//METEOR Evaluation
		float fp = fragPenalty(sentenceInNgrams.get(0), referenceInNgrams.get(0));
		float f = f_mean(sentenceInNgrams.get(0), referenceInNgrams.get(0));
		System.out.println("The final score for METEOR is: " + METEOR_finalScore(fp, f));

	}

}