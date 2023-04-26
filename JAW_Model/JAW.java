import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

/**
 * JAW takes:
 * -  a filepath to training data. This training data consists of one english sentence pre line that we will try to 
 * copy the style of
 * - a file path to a PCFG that we will update based off of the parse trees we get for the english sentences in training data
 */
public class JAW {
    
    public static void main(String[] args) {
        String dataDirectory = "C:\\Users\\Joshua G-K\\Documents\\College\\Junior Year\\NLPs\\final_projectv2\\NLPs_final_project\\JAW_Model\\Data\\";
        String trainingDataPath = dataDirectory + "trainingData.txt";
        String filePathPCFG = dataDirectory + "full.pcfg";
        JAW jaw = new JAW(trainingDataPath, filePathPCFG);
    }

    // Array list to store the parse trees for trainingData
    private ArrayList<ParseTree> trainingParseTrees;

    // Create a variable to store our cky parser
    private CKYParser parser;

    /**
     * This construct will read in the training data, clean it, then construct a parse tree for each of them using CKYParser. 
     * It will then update the PCFG based on these parse trees using a probabilistic approach.
     */
    public JAW(String trainingData, String filePathPCFG) {
        // Initialize variables 
        System.out.println(filePathPCFG);
        this.parser = new CKYParser(filePathPCFG);
        this.trainingParseTrees = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(trainingData));
            String line = reader.readLine();
            String whiteSpaceDelimiter = "\\s+";
            // Adds all stop list items ot a stop list hashset
            while (line != null) {
                // Clean line 
                String cleanedLine = line;//cleanSentence(line);
                System.out.println(cleanedLine);
                // Plug cleaned lined into parser 
                ParseTree trainingTree = parser.parseSentence(cleanedLine);

                // Put the resulting parse tree in an array list
                trainingParseTrees.add(trainingTree);
                
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(this.trainingParseTrees);
    }

    /**
     * This function takes in a seed word and generates a sentence with a similar style to the training data based off 
     * of that seed word
     */
    public String generateFromSeedWord(String word) {
        // Use a helper to return the most probable part of speech for the given seed word

        // Use a helper function to turn that part of speech into grammar tree

        // Populate the ends of the grammar tree with words and return those words as a full string
        return "";
    }

    /**
     * 
     */
    public Double evaluteModel() {
        // Get x amount of random words and run generateFromSeedWord on each of those words 

            // Use precision recall to compare the grammar tree gotten from generateFromSeedWord to the
            // grammar tree's from our training data and take an average 
            // (Probable will need helper functio nfor precision recall)
        return 0.0;
    }

    /**
     * Calculates the average precision recall between one grammar tree and a list of grammar trees that have
     * the same seedPartOfSpeech as that one grammar tree 
     */
    public Double getPrecisionRecall(ParseTree tree, String seedPartOfSpeech) {
        return 0.0;
    }

    /**
     * Cleans a given word 
     * @param word the word to clean 
     */
    public static String cleanWord(String word) {
        // Gets rid of all accent marks on letters and makes it normal letter
        word = Normalizer.normalize(word, Normalizer.Form.NFD);
        // Part of getting rid of accent marks
        word = word.replaceAll("\\p{M}", "");
        // Gets rid of spaces created from deleting spaces and dashes and such
        word = word.replaceAll("\\s+", "\\s");
        // Makes word lower case
        word = word.toLowerCase();
        // Gets rid of all non-alphabetic words 
        word = word.replaceAll("[^\\w]", "");
        return word;
    }

    /**
     * Cleans a sentence by repeatedly calling cleanWord 
     * 
     */
    public static String cleanSentence(String sentence) {
        // Gets rid of all accent marks on letters and makes it normal letter
        sentence = Normalizer.normalize(sentence, Normalizer.Form.NFD);
        // Part of getting rid of accent marks
        sentence = sentence.replaceAll("\\p{M}", "");
        // Gets rid of spaces created from deleting spaces and dashes and such
        sentence = sentence.replaceAll("\\s+", " ");
        // Makes word lower case
        sentence = sentence.toLowerCase();
        // Gets rid of all non-alphabetic words 
        sentence = sentence.replaceAll("[^\\w\\s]", "");
        return sentence;

    }

}