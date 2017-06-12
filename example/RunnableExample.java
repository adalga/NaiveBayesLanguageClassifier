import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import de.daslaboratorium.machinelearning.classifier.Classifier;

public class RunnableExample {

    public static void main(String[] args) throws IOException {
        /*
         * Create a new classifier instance. The context features are
         * Strings and the context will be classified with a String according
         * to the featureset of the context.
         */
        final Classifier<String, String> bayes = new BayesClassifier<String, String>();

        /*
         * The classifier can learn from classifications that are handed over
         * to the learn methods. Imagine a tokenized text as follows. The tokens
         * are the text's features. The category of the text will either be
         * positive or negative.
         */
        String germanTrainingSet = "./example/datasets/deu_50K_sentences.txt";
        String englishTrainingSet = "./example/datasets/eng_50K_sentences.txt";
        String frenchTrainingSet = "./example/datasets/fra_50K_sentences.txt";
        String spanishTrainingSet = "./example/datasets/spa_50K_sentences.txt";

        teachLanguage(bayes, germanTrainingSet, "German");
        teachLanguage(bayes, englishTrainingSet, "English");
        teachLanguage(bayes, frenchTrainingSet, "French");
        teachLanguage(bayes, spanishTrainingSet, "Spanish");

        if(args.length == 0) {
            /*
             * Now that the classifier has "learned" three classifications, it will
             * be able to classify which language does the sentence belong. The classify method returns
             * a Classification Object, that contains the given featureset,
             * classification probability and resulting category.
             */
            String germanTestSet  = "./example/datasets/deu_test_1k.txt";
            String englishTestSet = "./example/datasets/eng_test_1k.txt";
            String frenchTestSet  = "./example/datasets/fra_test_1k.txt";
            String spanishTestSet = "./example/datasets/spa_test_1k.txt";

            double germanAccuracy  = testLanguage(bayes, germanTestSet  , "German"  );
            double englishAccuracy = testLanguage(bayes, englishTestSet , "English" );
            double frenchAccuracy  = testLanguage(bayes, frenchTestSet  , "French"  );
            double spanishAccuracy = testLanguage(bayes, spanishTestSet , "Spanish" );

            System.out.printf("German Language Accuracy  : %% %.4f\n",  (germanAccuracy ) );
            System.out.printf("English Language Accuracy : %% %.4f\n",  (englishAccuracy) );
            System.out.printf("French Language Accuracy  : %% %.4f\n",  (frenchAccuracy) );
            System.out.printf("Spanish Language Accuracy : %% %.4f\n",  (spanishAccuracy) );
        }
        else if(args[0].equals("0")) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Please enter your sentences one by one to classify:");
            while(true) {
                String[] sentence = reader.nextLine().split("\\s");
                if(sentence[0].equals("quit") || sentence[0].equals("exit")){
                    System.out.println("Classifier has been terminated.");
                    break;
                }
                String result = testSentence(bayes, sentence);
                System.out.println("Your sentence is " + "'" + result + "'");
            }
        }
        else if(args[0].equals("1")) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Please enter a path to your input file:");
            String path = reader.nextLine();

            double germanAccuracy  = testLanguage (bayes, path, "German"  );
            double englishAccuracy = testLanguage (bayes, path, "English" );
            double frenchAccuracy  = testLanguage (bayes, path, "French"  );
            double spanishAccuracy = testLanguage (bayes, path, "Spanish" );

            if(germanAccuracy>englishAccuracy && germanAccuracy>frenchAccuracy && germanAccuracy>spanishAccuracy ){
                System.out.println("Your document is consists of mostly '" + "German"  + "' language with accuracy of %" + germanAccuracy*100);
            }
            else if(englishAccuracy>germanAccuracy && englishAccuracy>frenchAccuracy && englishAccuracy>spanishAccuracy ){
                System.out.println("Your document is consists of mostly '" + "English" + "' language with accuracy of %" + englishAccuracy*100);
            }
            else if(frenchAccuracy>englishAccuracy && frenchAccuracy>germanAccuracy && frenchAccuracy>spanishAccuracy ){
                System.out.println("Your document is consists of mostly '" + "French"  + "' language with accuracy of %" + frenchAccuracy*100);
            }
            else if(spanishAccuracy>englishAccuracy && spanishAccuracy>germanAccuracy && spanishAccuracy>frenchAccuracy ){
                System.out.println("Your document is consists of mostly '" + "Spanish"  + "' language with accuracy of %" + spanishAccuracy*100);
            }
        }

    }

    private static void teachLanguage(Classifier<String, String> bayes,
                                        String path,
                                        String languageType) throws IOException {
        int counter = 0;
        FileInputStream fstream = new FileInputStream(path);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null  && counter < 200) {
            counter++;
            String[] tokens = strLine.split(" ");
            bayes.learn(languageType, Arrays.asList(tokens));
        }
    }

    private static double testLanguage(Classifier<String, String> bayes,
                                        String path,
                                        String languageType) throws IOException {
        double trueCounter = 0;
        double falseCounter = 0;
        FileInputStream fstream = new FileInputStream(path);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null  ) {
            String[] tokens = strLine.split(" ");
            if( bayes.classify(Arrays.asList(tokens)).getCategory().equals(languageType)){
                trueCounter++;
            }else{
                falseCounter++;
            }
        }
        return ((trueCounter*100) / (trueCounter+falseCounter));
    }

    private static String testSentence(Classifier<String, String> bayes, String[] sentence) throws IOException {

        String result = bayes.classify(Arrays.asList(sentence)).getCategory();

        if( result.equals("English") ){
            return "English";
        }
        else if( result.equals("German")){
            return "German";
        }
        else if( result.equals("French")){
            return "French";
        }
        else if( result.equals("Spanish")){
            return "Spanish";
        }
        return "NoLanguage";

    }
}
