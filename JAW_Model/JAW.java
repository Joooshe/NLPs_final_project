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
        jaw.generateTree("NP");
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

        // System.out.println("PREVIOUS GRAMMAR SET");
        // for (GrammarRule rule : parser.grammarRuleSet.values()) {
        // System.out.println(rule);
        // }

        // Update the grammar rules for each tree
        for (ParseTree tree : this.trainingParseTrees) {
            // System.out.println(tree);

            parser.updateGrammarRules(tree);
        }

        // System.out.println("NEW GRAMMAR SET");
        // for (GrammarRule rule : parser.grammarRuleSet.values()) {
        // System.out.println(rule);
        // }

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
         * Loop through the CKY parser's rhsToGrammarRule map for max element in
         * priority que and get it
         * 
         * Keep doing this until max element is S
         * 
         * Once max element is S, then we create a parse tree and for its children add
         * the children of S
         * 
         * Then, for all the children of the current parse tree: if the lhs of that
         * child is in a set (which we will call
         * path set), then we add that to the parse tree, if not then we go through lhs
         * to grammar rule and add that to the parse tree
         * 
         * We do this until we reach a lexical node
         */

        // PSEUDO CODE
        /*
         * # This code gets us the path
         * lhsPath = HashSet()
         * 
         * while partOfSpeech != "S":
         * lhsSet.add(partOfSpeech)
         * # get the next above layer of the tree by going into the priority queue and
         * getting the max element
         * grammarRule = rhsToGrammarRule.get(partOfSpeech).peek();
         * partOfSpeech = grammarRule.getLhs();
         *
         * lhsSet.add(partOfSpeech)
         * 
         * # make a Q
         * 
         * # keep track of last item in Q
         * 
         * # while until Q is non-empty:
         * - pop from Q and add all rhs rules as children to that rule
         * - if the rhs rule is lexical then we add the rule as a lexical rule and do
         * not add it to the stack
         * 
         * 
         */
        GrammarRule grammarRule;
        HashMap<String, PriorityQueue<GrammarRule>> rhsToGrammarRule = parser.getRhsToGrammarRule();
        // System.out.println("Keys");
        // System.out.println(rhsToGrammarRule.keySet());
        // Initialize the path that we will take up the tree, this path will consiste of
        // the grammar rules we take up the tree
        // We put the grammar rule's sudo hash into the tree so we can find it again
        // when we go back down the tree
        HashMap<String, GrammarRule> path = new HashMap<>();

        while (!partOfSpeech.equals("S")) {
            // Get the grammar rule that is most likely to produce the partOfSpeech on the
            // right hand side
            PriorityQueue<GrammarRule> pQueue = rhsToGrammarRule.get(partOfSpeech);
            if (pQueue != null) {
                grammarRule = pQueue.peek();
            } else {
                System.out.printf("ERROR: Part of speech not fund: %s\n", partOfSpeech);
                break;
            }
            // Get the lhs for that grammar rule (that we will use as the rhs for the next
            // layer of the tree)
            System.out.println(partOfSpeech);
            partOfSpeech = grammarRule.getLhs();
            // Add that part of speech to the path
            path.put(partOfSpeech, grammarRule);
        }
        System.out.println("path:");
        System.out.println(path.keySet());

        /**
         * * # make a Q
         * 
         * # keep track of last item in Q
         * 
         * # while until Q is non-empty:
         * - pop from Q and add all rhs rules as children to that rule
         * - if the rhs rule is lexical then we add the rule as a lexical rule and do
         * not add it to the stack
         * 
         * 
         */

        /**
         * // Create q, add the beginning (S) to the q
         * // This q is for going through the parts of speech in the tree
         * Queue<String> partOfSpeechQ = new Queue
         * partOfSpeechQ.add("S")
         * // Create a qthat will have the same size, this q will be for keeping the
         * Parse
         * // Tree node that corresponds to that part of speech in the tree
         * Queue<ParseTree> parseTreeQ
         * parseTreeQ.add(new ParseTree("S"))
         * 
         * 
         * while (partOfSpeechQ.size() > 0) {
         * // Pop the first item from both qs: lhs and parse tree
         * // Use the lhs to get the grammar rule first checking in path. IF it is in
         * path, we remove it from path
         * // If its not in path, we go through lhs to grammarRule to get the grammar
         * rule
         * // Get the rhs of the grammar rule
         * // Create parse trees based off of the rhs and add to children of current
         * parse tree
         * // Add these parse trees to the parseTreeQ
         * // Add rhs to the partOfSpeechQ
         * }
         */
        // Get the lhs to grammar rule hash map that we will use if the lhs rule is not
        // in the path
        HashMap<String, PriorityQueue<GrammarRule>> lhsToGrammarRule = parser.getLhsToGrammarRule();
        // Create a queue for both partOfSpeech and parseTree nodes (which will be equal
        // sized)
        // These queues will be used to create the parse tree and access the grammar
        // rules that correspond
        // to each part of speech
        // The algorithm works by using the part of speech as a key to access the
        // grammar rules in the hashmap
        // lhsToGrammarRule or path. We then use the grammar rules to create parse tree
        // nodes that we make into
        // children of the current parse tree node.
        LinkedList<String> partOfSpeechQ = new LinkedList<>();
        partOfSpeechQ.add("S");
        LinkedList<ParseTree> parseTreeQ = new LinkedList<>();
        ParseTree head = new ParseTree("S", false);
        parseTreeQ.add(head);
        // Go until the queue is empty. We stop adding to the queue if a node is
        // lexical.
        while (partOfSpeechQ.size() > 0) {
            // System.out.printf("Size: %d\n", partOfSpeechQ.size());
            // System.out.println(partOfSpeechQ);
            String lhs = partOfSpeechQ.remove();
            ParseTree currentNode = parseTreeQ.remove();
            GrammarRule rule;
            // Get the grammar rule
            if (path.containsKey(lhs)) {
                rule = path.get(lhs);
                // System.out.println("Rule from path: " + rule);
                path.remove(lhs);
            } else {
                rule = lhsToGrammarRule.get(lhs).peek();
                // System.out.println("lhs: " + lhs);
                // System.out.println("Rule from lhsToGrammarRule: " + rule);
            }
            // For each rhs in the grammar rule make a corresponding child for the parse
            // tree
            for (String rhsPartOfSpeech : rule.getRhs()) {
                if (rule.isLexical()) {
                    // Create child
                    ParseTree child = new ParseTree(rhsPartOfSpeech, true);
                    // Add as child to current node
                    currentNode.addChild(child);
                } else {
                    ParseTree child = new ParseTree(rhsPartOfSpeech, false);
                    // Add as child to current node
                    currentNode.addChild(child);
                    // Add child to queue
                    partOfSpeechQ.add(rhsPartOfSpeech);
                    parseTreeQ.add(child);
                }
            }
        }

        System.out.println("HEAD");
        System.out.println(head);

        // Keep a running total of the max, use the max for the next step until we get
        // to S

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