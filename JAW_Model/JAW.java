import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

/**
 * TODO: 
 * - finish generate tree from seed word, pseudo code is pretty much there, just gotta code it 
 * - then we need to do evaluation and we r done 
 *  - eveluation we do precision recall, so we just need a precision recall helper function 
 *  - then call that helper function on all the training Data trees and average them 
 */

/**
 * JAW takes:
 * - a filepath to training data. This training data consists of one english
 * sentence pre line that we will try to
 * copy the style of
 * - a file path to a PCFG that we will update based off of the parse trees we
 * get for the english sentences in training data
 */
public class JAW {

    public static void main(String[] args) {
        String dataDirectory = "C:\\Users\\Joshua G-K\\Documents\\College\\Junior Year\\NLPs\\final_projectv2\\NLPs_final_project\\JAW_Model\\Data\\";
        String trainingDataPath = dataDirectory + "test.sentences";
        String exampleDataPath = dataDirectory + "testingDataExample.txt";
        String filePathPCFG = dataDirectory + "example.pcfg";
        JAW jaw = new JAW(exampleDataPath, filePathPCFG);
    }

    // Array list to store the parse trees for trainingData
    private ArrayList<ParseTree> trainingParseTrees;

    // Create a variable to store our cky parser
    private CKYParser parser;

    /**
     * This construct will read in the training data, clean it, then construct a
     * parse tree for each of them using CKYParser.
     * It will then update the PCFG based on these parse trees using a probabilistic
     * approach.
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
                String cleanedLine = line;// cleanSentence(line);
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

        for(GrammarRule rule : parser.grammarRuleSet.values()) {
            System.out.println(rule);
        }

        for(ParseTree tree: this.trainingParseTrees) {
            System.out.println(tree);
            
            parser.updateGrammarRules(tree);
        }

        System.out.println("NEW GRAMMAR SET");
        for(GrammarRule rule : parser.grammarRuleSet.values()) {
            System.out.println(rule);
        }

        System.out.println(this.trainingParseTrees);
    }

    /**
     * This function takes in a seed word and generates a sentence with a similar
     * style to the training data based off
     * of that seed word
     */
    public String generateFromSeedWord(String word) {
        // Use a helper to return the most probable part of speech for the given seed
        // word
        // ParseTree parseTree = 
        

        // Use a helper function to turn that part of speech into grammar tree
        
        // Populate the ends of the grammar tree with words and return those words as a
        // full string
        return "";
    }

    public void generateTree(String partOfSpeech) {
        /**
         * 
        Loop through the CKY parser's rhsToGrammarRule map for max element in priority que and get it 

        Keep doing this until max element is S

        Once max element is S, then we create a parse tree and for its children add the children of S 

        Then, for all the children of the current parse tree: if the lhs of that child is in a set (which we will call 
        path set), then we add that to the parse tree, if not then we go through lhs to grammar rule and add that to the parse tree 

        We do this until we reach a lexical node 
         */

        // PSEUDO CODE 
        /*
         * # This code gets us the path 
         * lhsPath = HashSet()
         * 
         * while partOfSpeech != "S":
         *      lhsSet.add(partOfSpeech)
         *      # get the next above layer of the tree by going into the priority queue and getting the max element
         *      grammarRule = rhsToGrammarRule.get(partOfSpeech).peek();
         *      partOfSpeech = grammarRule.getLhs();
         *
         * lhsSet.add(partOfSpeech)
         * 
         * # make a Q 
         * 
         * # keep track of last item in Q  
         * 
         * # while until Q is non-empty:
         *      - pop from Q and add all rhs rules as children to that rule 
         *      - if the rhs rule is lexical then we add the rule as a lexical rule and do not add it to the stack 
         * 
         * # 
         */

        // Keep a running total of the max, use the max for the next step until we get to S 

        // 

    }

    /**
     * 
     */
    public Double evaluteModel() {
        // Get x amount of random words and run generateFromSeedWord on each of those
        // words

        // Use precision recall to compare the grammar tree gotten from
        // generateFromSeedWord to the
        // grammar tree's from our training data and take an average
        // (Probable will need helper functio nfor precision recall)
        return 0.0;
    }

    /**
     * Calculates the average precision recall between one grammar tree and a list
     * of grammar trees that have
     * the same seedPartOfSpeech as that one grammar tree
     */
    public Double getPrecisionRecall(ParseTree tree, String seedPartOfSpeech) {
        return 0.0;
    }

    /**
     * Cleans a given word
     * 
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