import java.io.*;
import java.util.*;

public class CKYParser {
    public static final String NULL_STRING = "NULL";
    public static final Double RECONSTRUCT_FACTOR = 0.9;
    /*
     * Unary rules hashmap
     * - This will map all the Unary rules on the rhs to the rule on the lhs and
     * keep the weights, that
     * way for each entry in a CKY we are able to get the lhs's that go to it along
     * with the weights. We
     * then store this hashmap in the CKY entry.
     * The hashmap will be <rhs, lhs> and lhs will be a list
     */
    public HashMap<String, HashMap<String, Double>> unaryRulesMap;

    /*
     * Lexical rules hashmap
     * - This will map all the lexical words on the rhs to the rule on the lhs and
     * keep the weights, that
     * way for each entry in the diagonal we can quickly map that word to its unary.
     * That is if we see the
     * lexical word we can get the set of all lhs's that could go to that word and
     * their weights
     * The hashmap will be <lexical word, lhs -> weight> where lhs will be a list
     */
    public HashMap<String, HashMap<String, Double>> lexicalRulesMap;

    /*
     * Binary rules hashmap
     * - This will be the same as what we read in from the pcfg. The rhs will be the
     * same.
     * We will do this because looping through all the binary rules to find the
     * matching rules
     * for a cky entry will be faster this way since we
     * can make it have time O(1) * (# of binary rules) by looking through each
     * binary rule and
     * if rhs entry 1 is in one set of the left cky and rhs entry 2 is in one set of
     * the bottom cky,
     * then we can add it to the set for this cky's entries
     */
    public ArrayList<GrammarRule> binaryRules;

    /**
     * Set of grammar rules to keep them all in one place 
     */
    public HashMap<String, GrammarRule> grammarRuleSet;

    /**
     * Stores rhs's mapped to LHS's and a probability, used for JAW model 
     */
    public HashMap<String, PriorityQueue<GrammarRule>> rhsToGrammarRule;

    /**
     * Stores a lhs to grammar rule 
     */
    public HashMap<String, PriorityQueue<GrammarRule>> lhsToGrammarRule;

    /*
     * The algorith for init our PCFG:
     * We read in the PCFG and loop through all the rules.
     * 1. If the rule is a lexical rule then we add it to the lexical rules map,
     * making the key the lexical rule and the value a hashset that contains the
     * lhs.
     * 2. If the rule is a unary rule then we add it to the unary rules map, making
     * the key the rhs rule and the value a hashset that contains the the lhs.
     * (because there can be multiple lhs pointing to the same rhs)
     * 3. If the rule is a binary rule (length of rhs is 2), then we simply get its
     * left hand side and right hand side and put it in a hash map
     * 
     * The algorithm to go through our table
     * 1. First we make the table of CKY entries
     * 2. Then we initialize the diagonals
     * 3. Then we go through the non diagonals
     * 
     * For each CKY entry
     * - We store a hashmap deep copy of the lhs -> weights
     * - We store the two children of this cell as lhs -> two children
     */

    public static void main(String[] args) {

        // String grammarFile = args[0];
        // String sentencesFile = args[1];
        // System.out.println("Success!");
        /// CKYParser parser = new CKYParser(grammarFile);
        // parser.parseFile(sentencesFile);

        // String folderPath = "C:\\Users\\Joshua G-K\\Documents\\College\\Junior
        // Year\\NLPs\\hw4\\data\\";
        // String fileName = "example.pcfg";
        // String sentence1 = "Mary likes John .";
        // String sentence2 = "John codes with John .";
        // String sentence3 = "Mary likes to code .";
        // String sentence4 = "write giant programs .";
        // String sentence5 = "giant programs write John .";

        // CKYParser parser = new CKYParser(folderPath + fileName);
        // parser.parseSentence(sentence1);
        // parser.parseSentence(sentence2);
        // parser.parseSentence(sentence3);
        // parser.parseSentence(sentence4);
        // parser.parseSentence(sentence5);

        String dataDirectory = "C:\\Users\\Joshua G-K\\Documents\\College\\Junior Year\\NLPs\\final_projectv2\\NLPs_final_project\\JAW_Model\\Data\\";
        String filePathPCFG = dataDirectory + "full.pcfg";
        CKYParser parser = new CKYParser(filePathPCFG);
    }

    /**
     * File name to grammar
     */
    public CKYParser(String filename) {
        // Initialize variables
        this.unaryRulesMap = new HashMap<>();
        this.lexicalRulesMap = new HashMap<>();
        this.binaryRules = new ArrayList<>();
        this.rhsToGrammarRule = new HashMap<>();
        this.lhsToGrammarRule = new HashMap<>();
        this.grammarRuleSet = new HashMap<>();
        // Read one line at a time
        // For each line pass it into the grammarRule class to make a grammar rule
        // Then we check for 3 things:
        // If the grammar rule is lexical:
        // We add the rhs as a key to lexicalRulesMap and its value is a hashmap with
        // key as lhs and value as weight
        // If the grammar rule is non-lexical and only has one item in rhs: (this is a
        // unary non-lexical rule)
        // We add the rhs as a key to unaryRulesMap and its value is a hashmap with key
        // as lhs and value as weight
        // Else then the grammar is a non-lexical binary rule:
        // We add the grammar rule to the array list binaryRules
        File file = new File(filename);
        int count = 0;
        try {
            // Make into a parse tree and store in the array list
            String spaceDelimiter = "(\\s)+";
            BufferedReader scanner = new BufferedReader(new FileReader(filename));

            String line = "_";
            while (line != null) {
                line = scanner.readLine();
                if (line == null) {
                    break;
                }
                count +=1;
                GrammarRule grammarRule = new GrammarRule(line);
                this.grammarRuleSet.put(grammarRule.createSudoHash(), grammarRule);
                if (grammarRule.numRhsElements() > 1) {
                    // Adds current grammar rule to binaryRules map 
                    this.binaryRules.add(grammarRule);
                } else if (grammarRule.isLexical()) {
                    HashMap<String, Double> secondLayer = new HashMap<>();
                    secondLayer.put(grammarRule.getLhs(), grammarRule.getWeight());
                    this.lexicalRulesMap.put(grammarRule.getRhs().get(0), secondLayer);
                } else {
                    HashMap<String, Double> secondLayer = new HashMap<>();
                    secondLayer.put(grammarRule.getLhs(), grammarRule.getWeight());
                    this.unaryRulesMap.put(grammarRule.getRhs().get(0), secondLayer);
                }
            }
            // System.out.printf("Count: %d\n", count);
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseFile(String filename) {
        File file = new File(filename);

        try {
            // Make into a parse tree and store in the array list
            String spaceDelimiter = "(\\s)+";
            Scanner scanner = new Scanner(file).useDelimiter(spaceDelimiter);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                this.parseSentence(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ParseTree parseSentence(String sentence) {
        // Split the sentence by white space
        String[] sentenceList = sentence.split("(\\s)+");
        int sentenceWordCount = sentenceList.length;

        // Create a 2D array of wordCount by wordCount
        CKYEntry[][] array = new CKYEntry[sentenceWordCount][sentenceWordCount];
        // Initialize diagonal, for each diagonal entry:
        /*
         * get the word that the diagonal corresponds to
         * use lexicalRulesMap to get all of the lhs rules that apply to each lexical
         * rule
         * deep copy the hashmap of lhs rules - EXPENSIVE
         * store this hashmap in the CKY entry object for this cell using helper method
         * in CKY
         * - this helper method should check to see if any unary rules apply to the
         * rules in the hashmap
         * and if so, we add them to the hashmap for the CKY entry(avoid infinite loops)
         * - make two children null
         * 
         */
        for (int i = 0; i < sentenceWordCount; i++) {
            // Get word
            String word = sentenceList[i];
            // Loop through all the hash maps in lexicalRulesMap[word]
            HashMap<String, Double> secondLayer = lexicalRulesMap.get(word);
            if (secondLayer == null) {
                System.out.println("Second layer null: " + NULL_STRING + " | Word: " + word);
                return null;
            }
            CKYEntry newEntry = new CKYEntry();
            array[i][i] = newEntry;

            for (String lhs : secondLayer.keySet()) {
                Double weight = secondLayer.get(lhs);
                newEntry.addConstituent(lhs, weight, word);
            }
            newEntry.addUnaryRules(this.unaryRulesMap);
            // System.out.println(newEntry);

        }
        // System.out.println();
        // Now we loop through the rest of the entries
        /*
         * Assume we have the while loop working where we look at the right CKY entries
         * to the left and on the bottom
         * - Call the CKY Entries left CKY and bottom CKY
         * - For each CKY entry we go through all left and bottom CKY's
         * - For each pair of left and bottom CKY's we go through all of the binary
         * grammar rules in grammarRule
         * and get the first rhs and second rhs. We see if first rhs is in the hashmap
         * of left CKY and we see if
         * second rhs is in the hasmap for bottom CKY. If both are in the hashmap's then
         * we add the the lhs and
         * weight of this grammar rule to the current CKY entry's hash map. (note the
         * the add function checks for unary rules
         * and will check for max values for the same lhs and only put the value that is
         * higher in the map (less negative)).
         * We will need a hashmap for mapping each lhs rule to the children that make up
         * that rule in the CKY entry. Everytime
         * we add in a new rule to the hashmap we will have to add in the children that
         * made up that new rule.
         * - We do the above every time we run that part of the CKY algorithm
         * 
         */
        for (int c = 1; c < sentenceWordCount; c++) {
            for (int r = c - 1; r >= 0; r--) {
                CKYEntry currentEntry = new CKYEntry();
                array[r][c] = currentEntry;
                for (int k = 0; k < c - r; k++) {
                    // ROW: (r , r+k)
                    int leftR = r;
                    int leftC = r + k;
                    CKYEntry leftEntry = array[leftR][leftC];
                    // COL: (r+1+k ,c)
                    int bottomR = r + 1 + k;
                    int bottomC = c;
                    CKYEntry bottomEntry = array[bottomR][bottomC];

                    // Go through and all the binary rules
                    for (GrammarRule rule : binaryRules) {
                        String leftRhs = rule.getRhs().get(0);
                        String rightRhs = rule.getRhs().get(1);

                        if (leftEntry.containsLhs(leftRhs) && bottomEntry.containsLhs(rightRhs)) {
                            String lhs = rule.getLhs();
                            Double weight = rule.getWeight() + leftEntry.getWeight(leftRhs)
                                    + bottomEntry.getWeight(rightRhs);
                            currentEntry.addConstituent(lhs, weight, leftEntry, bottomEntry, leftRhs, rightRhs);
                        }
                    }
                }
                currentEntry.addUnaryRules(this.unaryRulesMap);
                // System.out.println(currentEntry);
            }
        }
        // Now reconstruct the parse tree
        CKYEntry root = array[0][sentenceWordCount - 1];
        String lhs = "S";
        if (!root.isSentence()) {
            System.out.print("Null string: " + NULL_STRING);
            return null;
        }

        ParseTree finTree = buildTree(root, lhs);

        // System.out.println(finTree + "\t" + root.getWeight("S"));
        return finTree;
    }

    public ParseTree buildTree(CKYEntry node, String lhs) {
        // Get triplet
        Triplet triplet = node.getTriplet(lhs);

        ParseTree parseTreeParent = new ParseTree(triplet.getLhs(), false);

        // If its a terminal triplet we just make a parse
        if (triplet.isLexical()) {
            ParseTree parseTreeChild = new ParseTree(triplet.getRhs1(), true);
            parseTreeParent.addChild(parseTreeChild);
        } else if (triplet.isUnary()) {
            CKYEntry rhs1Child = triplet.getRhs1Child();
            String rhs1 = triplet.getRhs1();
            parseTreeParent.addChild(this.buildTree(rhs1Child, rhs1));
        } else {
            CKYEntry rhs1Child = triplet.getRhs1Child();
            String rhs1 = triplet.getRhs1();
            CKYEntry rhs2Child = triplet.getRhs2Child();
            String rhs2 = triplet.getRhs2();
            parseTreeParent.addChild(this.buildTree(rhs1Child, rhs1));
            parseTreeParent.addChild(this.buildTree(rhs2Child, rhs2));
        }
        // Return
        return parseTreeParent;
    }

    public HashMap<String, PriorityQueue<GrammarRule>> getRhsToGrammarRule() {
        return this.rhsToGrammarRule;
    }

    public HashMap<String, PriorityQueue<GrammarRule>> getLhsToGrammarRule() {
        return this.lhsToGrammarRule;
    }

    public HashMap<String, HashMap<String, Double>> getUnaryRulesMap() {
        return this.unaryRulesMap;
    }

    public void updateGrammarRules(ParseTree tree) {
        if(tree.isTerminal()) {
            return;
        }

        String lhs = tree.getLabel();
        ArrayList<String> rhs = tree.getChildrenLabels();
        GrammarRule constructed_rule = new GrammarRule(lhs, rhs);

        GrammarRule true_rule = this.grammarRuleSet.get(constructed_rule.createSudoHash());
        true_rule.setWeight(true_rule.getWeight()*RECONSTRUCT_FACTOR);

        for(ParseTree t: tree.getChildren()) {
            if(t != null) {
                updateGrammarRules(t);
            }
        }

        // Clear the priority queues and then re-add the rules with their updated weights 
        // Clears the priority queues  
        this.clearPriorityQMap(this.lhsToGrammarRule);
        this.clearPriorityQMap(this.rhsToGrammarRule);
        // Adds the rules with the updates weights for each rhs and lhs
        for (GrammarRule grammarRule : this.grammarRuleSet.values()) {
            // Add current grammar to lhs to grammar rule 
            this.addToPriorityQMap(grammarRule, lhsToGrammarRule, false);
            // Adds current grammar rule to rhs to grammar rule 
            this.addToPriorityQMap(grammarRule, rhsToGrammarRule, true);
        }
    }

    public void addToPriorityQMap(GrammarRule rule, HashMap<String, PriorityQueue<GrammarRule>> sideToGrammarRule, Boolean useRhs)  {
        if (useRhs) {
            if (rule.getRhs().size()>1) {
                String rhs0 = rule.getRhs().get(0);
                if (sideToGrammarRule.containsKey(rhs0)) {
                    sideToGrammarRule.get(rhs0).add(rule);
                } else {
                    sideToGrammarRule.put(rhs0, new PriorityQueue<>(1, new GrammarRuleCompare()));
                    sideToGrammarRule.get(rhs0).add(rule);
                }
                String rhs1 = rule.getRhs().get(1);
                if (sideToGrammarRule.containsKey(rhs1)) {
                    sideToGrammarRule.get(rhs1).add(rule);
                } else {
                    sideToGrammarRule.put(rhs1, new PriorityQueue<>(1, new GrammarRuleCompare()));
                    sideToGrammarRule.get(rhs1).add(rule);
                }
            } else {
                String rhs0 = rule.getRhs().get(0);
                if (sideToGrammarRule.containsKey(rhs0)) {
                    sideToGrammarRule.get(rhs0).add(rule);
                } else {
                    sideToGrammarRule.put(rhs0, new PriorityQueue<>(1, new GrammarRuleCompare()));
                    sideToGrammarRule.get(rhs0).add(rule);
                }
            }
        } else {
            String lhs = rule.getLhs();
            if (sideToGrammarRule.containsKey(lhs)) {
                sideToGrammarRule.get(lhs).add(rule);
            } else {
                sideToGrammarRule.put(lhs, new PriorityQueue<>(1, new GrammarRuleCompare()));
                sideToGrammarRule.get(lhs).add(rule);
            }

        }
        

    }

    public void clearPriorityQMap(HashMap<String, PriorityQueue<GrammarRule>> sideToGrammarRule) {
        for (String key : sideToGrammarRule.keySet()) {
            sideToGrammarRule.get(key).clear();
        }
    }
}