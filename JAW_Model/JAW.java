import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

/**
 * JAW takes:
 * - a filepath to training data. This training data consists of one english
 * sentence pre line that we will try to
 * copy the style of
 * - a file path to a PCFG that we will update based off of the parse trees we
 * get for the english sentences in training data
 * 
 * Takes about 1 minute to run 
 * 
 * @author Joshua Garcia-Kimble, William Yang, Anxin Yi
 */
public class JAW {

    public static void main(String[] args) {
        String dataDirectory = "C:\\Users\\Joshua G-K\\Documents\\College\\Junior Year\\NLPs\\final_projectv2\\NLPs_final_project\\JAW_Model\\Data\\";
        String testSentencesPath = dataDirectory + "test.sentences";
        String exampleDataPath = dataDirectory + "testingDataExample.txt";
        String trainingDataPath = dataDirectory + "trainingData.txt";
        String testingDataPath = dataDirectory + "testingData.txt";
        String examplePCFGPath = dataDirectory + "example.pcfg";
        String fullPCFGPath = dataDirectory + "full.pcfg";
        String academicStyle = dataDirectory + "AcademicStyle.txt";
        String childrensBookStyle = dataDirectory + "ChildrensBookStyle.txt";
        String emptyFile = dataDirectory + "empty.txt";
        JAW jaw = new JAW(academicStyle, childrensBookStyle, fullPCFGPath);
        jaw.evaluateModel("William");
        jaw.generateFromSeedWord("William");

        // String cleanTest = JAW.cleanSentence("Testing, to see if this's cleaned.");
        // System.out.println(cleanTest);
    }

    // Array list to store the parse trees for trainingData
    private ArrayList<ParseTree> trainingParseTrees;

    
    // Array list to store the parse trees for testing
    private ArrayList<ParseTree> testingParseTrees;

    // Create a variable to store our cky parser
    private CKYParser parser;

    /**
     * This construct will read in the training data, clean it, then construct a
     * parse tree for each of them using CKYParser.
     * It will then update the PCFG based on these parse trees using a probabilistic
     * approach.
     */
    public JAW(String trainingData, String testingData, String filePathPCFG) {
        // Initialize variables
        this.parser = new CKYParser(filePathPCFG);
        this.trainingParseTrees = new ArrayList<>();
        this.testingParseTrees = new ArrayList<>();
        
        this.generateParseTrees(parser, trainingParseTrees, trainingData);
        this.generateParseTrees(parser, testingParseTrees, testingData);

        // Update the grammar rules for each tree
        for (ParseTree tree : this.trainingParseTrees) {
            parser.updateGrammarRules(tree);
        }

        System.out.println(this.trainingParseTrees);
    }

    /**
     * Takes in a sentence parser (CKYParser) and a path to the data file (data) and parses all of the 
     * strings on each line in the data file and adds the parseTrees of those strings to parseTreeList
     * @param parser The program to parse a sentence given a context free grammar 
     * @param parseTreeList The array list of parseTrees we want to add the parseTrees of sentences in data to
     * @param data the path to the file with the sentences we want to parse, one sentence per line 
     * @return No return, just adds the parseTrees to parseTreeList
     */
    public void generateParseTrees(CKYParser parser, ArrayList<ParseTree> parseTreeList, String data) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(data));
            String line = reader.readLine();
            String whiteSpaceDelimiter = "\\s+";
            // Adds all stop list items ot a stop list hashset
            while (line != null) {
                // We actually don't clean the sentences because the pcfg is not cleaned
                String cleanedLine = cleanSentence(line);
                System.out.println(cleanedLine);
                // Plug cleaned lined into parser
                ParseTree trainingTree = parser.parseSentence(cleanedLine);

                // Put the resulting parse tree in an array list
                parseTreeList.add(trainingTree);

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Parse Tree List: " + parseTreeList);
    }

    /**
     * This function takes in a seed word and generates a sentence with a similar
     * style to the training data based off
     * of that seed word
     * @param word generates a sentence based off a seed word 
     * @return the sentence we generated 
     */
    public String generateFromSeedWord(String word) {
        // Use a helper to return the most probable part of speech for the given seed
        // word
        HashMap<String, PriorityQueue<GrammarRule>> rhsToGrammarRule = this.parser.getRhsToGrammarRule();
        String partOfSpeech;
        if (rhsToGrammarRule.containsKey(word)) {
            partOfSpeech = rhsToGrammarRule.get(word).peek().getLhs();
        } else {
            partOfSpeech = "NNP";
        }
        // Use a helper function to turn that part of speech into grammar tree
        ParseTree root = this.generateTree(partOfSpeech);
        // Now we must depth first search in order to populate the ends of the grammar tree with 
        // words and return those words as a full string
        LinkedList<ParseTree> parseTreeStack = new LinkedList<>();
        parseTreeStack.add(root);
        // Create thing to store the sentence 
        ArrayList<String> sentence = new ArrayList<>();

        while (parseTreeStack.size() > 0) {
            ParseTree node = parseTreeStack.removeLast();
            ListIterator<ParseTree> iter = node.getChildren().listIterator(node.getChildren().size());

            while (iter.hasPrevious()) {
                ParseTree child = iter.previous();
                if (child.isTerminal()) {
                    sentence.add(child.getLabel());
                } else {
                    parseTreeStack.add(child);
                }  
            }
        }

        System.out.println("SENTENCE GENERATED:");
        System.out.println(sentence);

        return "";
    }

    /**
     * Generates a parse tree based off a partOfSpeech
     * @param partOfSpeech a part of speech that our probabilistic context free grammar recognizes 
     * @return a parse tree based off the partOfSpeech
     */
    public ParseTree generateTree(String partOfSpeech) {
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
        HashMap<String, ArrayList<GrammarRule>> removedRules = new HashMap<>();
        while (partOfSpeechQ.size() > 0) {
            String lhs = partOfSpeechQ.remove();
            ParseTree currentNode = parseTreeQ.remove();
            GrammarRule rule;
            // Get the grammar rule
            if (path.containsKey(lhs)) {
                rule = path.get(lhs);
                path.remove(lhs);
            } else {
                // Removes the rule so we do not run into any infinite loops 
                rule = lhsToGrammarRule.get(lhs).poll();
                if (rule == null) {
                    System.out.println("RULE IS NULL FOR LHS: " + lhs);
                    continue;
                }
                // Ensures we can add the rules back when we are done 
                if (removedRules.containsKey(lhs)) {
                    removedRules.get(lhs).add(rule);
                } else {
                    removedRules.put(lhs, new ArrayList<GrammarRule>());
                    removedRules.get(lhs).add(rule);
                }
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
        // Adds the rules that we removed back 
        for (String lhs : removedRules.keySet()) {
            for (GrammarRule rule : removedRules.get(lhs)) {
                Boolean offer = lhsToGrammarRule.get(lhs).offer(rule);
                if (!offer) {
                    System.out.println("FAILED TO ADD RULE BACK TO GRAMMAR");
                }
            }
        }

        return head;

    }

    /**
     *  Evalution
     */
    public Double calculatePrecisionRecall(ParseTree tree1, ParseTree tree2) {
        // Get x amount of random words and run generateFromSeedWord on each of those
        // words
        int matchedNodes = countMatchedNodes(tree1, tree2);
        int totalNodes1 = countTotalNodes(tree1);
        int totalNodes2 = countTotalNodes(tree2);

        double precision = (double) matchedNodes / totalNodes1;
        double recall = (double) matchedNodes / totalNodes2;

        return (2 * precision * recall) / (precision + recall);
    }


    /**
     * Used for comparing trees
     * @param tree1 a tree to compare
     * @param tree2 the other tree to compare 
     * @return the number of nodes that match between the given trees
     */
    private int countMatchedNodes(ParseTree tree1, ParseTree tree2) {
        if (tree1 == null || tree2 == null || tree1.isTerminal() || tree2.isTerminal()) {
            return 0;
        }

        int count;

        if (tree1.getLabel().equals(tree2.getLabel())) {
            count = 1;
        } else {
            count = 0;
        }

        for (int i = 0; i < Math.min(tree1.getChildren().size(), tree2.getChildren().size()); i++) {
            count += countMatchedNodes(tree1.getChildren().get(i), tree2.getChildren().get(i));
        }
        
        return count;
    }

    private int countTotalNodes(ParseTree tree) {
        if (tree == null || tree.isTerminal()) {
            return 0;
        }
        int count = 1;

        for (ParseTree child: tree.getChildren()) {
            count += countTotalNodes(child);
        }
        return count;
    }

    /**
     * Evaluates the model by taking a seedword and has the model create a parse tree from the seed word, we then
     * compare the parse tree from our model to the parse trees from our training data and print out the precisionRecall
     * @param seedWord the word to start letting our parse tree generate a sentence  
     * @return None, instead it prints out the results
     */
    public void evaluateModel(String seedWord) {
        // Get the tree from the part of speech of the seed word 
        HashMap<String, PriorityQueue<GrammarRule>> rhsToGrammarRule = this.parser.getRhsToGrammarRule();
        String partOfSpeech;
        if (rhsToGrammarRule.containsKey(seedWord)) {
            partOfSpeech = rhsToGrammarRule.get(seedWord).peek().getLhs();
        } else {
            partOfSpeech = "NNP";
        }
        Double trainingDataPrecisionRecall = 0.0;
        ParseTree generatedTree = this.generateTree(partOfSpeech);

        // Get the training data average precision recall
        for (ParseTree parseTree : trainingParseTrees) {
            Double add = this.calculatePrecisionRecall(parseTree, generatedTree);
            if (Double.isNaN(add)) {
                add=0.0;
            }
            trainingDataPrecisionRecall += add;
        }
        trainingDataPrecisionRecall = trainingDataPrecisionRecall / trainingParseTrees.size();
        
        // Get the testing data average precision recall 
        Double testingDataPrecisionRecall = 0.0;
        for (ParseTree parseTree : testingParseTrees) {
            Double add = this.calculatePrecisionRecall(parseTree, generatedTree);
            if (Double.isNaN(add)) {
                add=0.0;
            }
            testingDataPrecisionRecall += add;
        }
        
        testingDataPrecisionRecall = testingDataPrecisionRecall / testingParseTrees.size();

        System.out.printf("trainingData: %f \t testingData: %f\n", trainingDataPrecisionRecall, testingDataPrecisionRecall);
    }
    
    /**
     * Cleans a given word
     * @param word The word to clean, different from clean sentence because this is just for a word
     * @return The cleaned word that is not ready for our model to process
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
     * @param sentence the sentence to clean
     * @return A string that is cleaned up for the model to process
     */
    public static String cleanSentence(String sentence) {
        // Cleans up commas, periods, and words by adding white space
        sentence = sentence.replaceAll("'(\\w)+", " '$1");
        sentence = sentence.replaceAll(", ", " , ");
        sentence = sentence.replaceAll("\\.", " \\.");
        // Gets rid of spaces created from deleting spaces and dashes and such
        sentence = sentence.replaceAll("\\s+", " ");
        return sentence;

    }

}