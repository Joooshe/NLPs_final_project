import java.util.*;

public class CKYEntry {
    // Name for the list size of the CKYEntry list in ruleChildrenMap
    // public static final int CKY_LIST_SIZE = 2;

    /*
     * Hashmap that contains the lhs rule and the weight for it
     * - We add all the lhs rules and their weights to this hashmap.
     * These are the lhs rules and weights needed to do the CKY algorithm
     */
    // private HashMap<String, Double> ruleWeightMap;

    /*
     * Hashmap that contains the lhs rule and the children CKYEntries that
     * made that rule so we can back track to make the parse tree
     */
    // private HashMap<String, CKYEntry[]> ruleChildrenMap;

    /*
     * Hashmap that contains the lhs rule and the rhs that these lhs rule goes to
     * The first rhs rule corresponds ot the first CKY entry and the second rhs rule
     * corresponds to the second CKY entry
     */
    // private HashMap<String, ArrayList<String>> lhsToRhs;

    // Or put all of the above hash maps into a hashmap that maps to a triple
    private HashMap<String, Triplet> rules;
    // So everytime we change the weight for a rule we have to change its CKYEntries
    // to know where its children are

    // Initialize the CKY Entry
    public CKYEntry() {
        // Initialize the maps
        // ruleWeightMap = new HashMap<>();
        // OR use the below
        rules = new HashMap<>();

    }

    // Adds the specified lhs, weight, and two children to the two hashmaps
    // Return true if successfully added
    public boolean addConstituent(String lhs, double weight, CKYEntry leftchild, CKYEntry rightChild, String leftRhs,
            String rightRhs) {
        /*
         * Here, we add the lhs, weight, leftChild, and right child to the map rules
         */
        Triplet triplet = new Triplet(lhs, weight, leftchild, rightChild, leftRhs, rightRhs);
        return this.addConstHelper(triplet, lhs, weight);
    }

    public boolean addConstituent(String lhs, double weight, CKYEntry leftchild, String leftRhs) {
        /*
         * Here, we add the lhs, weight, leftChild, and right child to the map rules
         */
        Triplet triplet = new Triplet(lhs, weight, leftchild, leftRhs);
        return this.addConstHelper(triplet, lhs, weight);
    }

    public boolean addConstituent(String lhs, double weight, String leftRhs) {
        /*
         * Here, we add the lhs, weight, leftChild, and right child to the map rules
         */
        Triplet triplet = new Triplet(lhs, weight, leftRhs);
        return this.addConstHelper(triplet, lhs, weight);
    }

    boolean addConstHelper(Triplet triplet, String lhs, double weight) {
        if (rules.containsKey(lhs)) {
            // We want to check to see which is the minimum and if the new is the minimum
            // then we put it in
            if (weight > rules.get(lhs).getWeight()) {
                rules.put(lhs, triplet);
                return true;
            }
        } else {
            // We just add the rule
            rules.put(lhs, triplet);
            return true;
        }
        return false;
    }

    // // Adds a constitutent for our hashmap and checks for any unary rules
    // public void addUnaryRules(HashMap<String, HashMap<String, Double>>
    // unaryRulesMap) {
    // /*
    // * Algorith:
    // *
    // * - we get ruleWeightMap and ruleChildrenMap
    // * - we loop through the keys of cons
    // * - if a key is already in ruleWeightMap we compare the weight of the
    // * current key to the weight of the key in ruleWeightMap and add the
    // * smaller one in ruleWeightMap using CKYEntry's add function
    // * - else if it is not in ruleWeightMap we add it there using CKYEntry's add
    // * function
    // * - We use CKY Entry's add function whenever we add because this add
    // * function will make sure to update ruleChildrenMap to have the proper
    // * children for the lhs
    // * - after the above loop, for each rule in rule in ruleWeightMap we dfs
    // through
    // * the unary rules
    // * but only continue the recursion if the unary rule hasn't been visited
    // * already, if it has we
    // * end the recursion and move to the next item in the loop. If it hasn't then
    // we
    // * add this rule and its
    // * weight plus the weight of the rule before to the CKYENtry using its add
    // * function and we make the children just
    // * be itself, that way later down the line we can backtrack to itself
    // * - but how do we back track these unary rules? We go through rules and go
    // * through the children in the children'S
    // * list so that even if the same CKY entry is its own child (as in a unary
    // rule
    // * points to itself) it will still be
    // * recorded
    // */
    // // Loop through each lhs in rule in rules and see if there are any matching
    // rules in unaryRulesMap
    // Queue<String> queue = new LinkedList<String>();

    // for (String lhs: this.rules.keySet()) {
    // queue.add(lhs);
    // }

    // while (queue.size() > 0) {
    // // If it has the key then we want to add all the things in it to the queue
    // that pass as true for add constitutent
    // String lhs = queue.poll();
    // Triplet triplet = this.rules.get(lhs);
    // // If the lhs is in our unary rules map as a rhs, then we want to add all of
    // the unary rules to this object
    // if (unaryRulesMap.containsKey(lhs)) {
    // String newRhs = lhs;
    // // Get the second layer which represents all the rules that point to this
    // rule
    // HashMap<String, Double> secondLayer = unaryRulesMap.get(newRhs);
    // for (String newLhs : secondLayer.keySet()) {
    // Double weight = secondLayer.get(newLhs) + triplet.getWeight();
    // boolean inserted = this.addConstituent(newLhs, weight, this, newRhs);
    // if (inserted) {
    // queue.add(newLhs);
    // }
    // }
    // }
    // }
    // }

    // Adds a constitutent for our hashmap and checks for any unary rules
    public void addUnaryRules(HashMap<String, HashMap<String, Double>> unaryRulesMap) {
        /*
         * Algorith:
         * 
         * - we get ruleWeightMap and ruleChildrenMap
         * - we loop through the keys of cons
         * - if a key is already in ruleWeightMap we compare the weight of the
         * current key to the weight of the key in ruleWeightMap and add the
         * smaller one in ruleWeightMap using CKYEntry's add function
         * - else if it is not in ruleWeightMap we add it there using CKYEntry's add
         * function
         * - We use CKY Entry's add function whenever we add because this add
         * function will make sure to update ruleChildrenMap to have the proper
         * children for the lhs
         * - after the above loop, for each rule in rule in ruleWeightMap we dfs through
         * the unary rules
         * but only continue the recursion if the unary rule hasn't been visited
         * already, if it has we
         * end the recursion and move to the next item in the loop. If it hasn't then we
         * add this rule and its
         * weight plus the weight of the rule before to the CKYENtry using its add
         * function and we make the children just
         * be itself, that way later down the line we can backtrack to itself
         * - but how do we back track these unary rules? We go through rules and go
         * through the children in the children'S
         * list so that even if the same CKY entry is its own child (as in a unary rule
         * points to itself) it will still be
         * recorded
         */
        // Loop through each lhs in rule in rules and see if there are any matching
        // rules in unaryRulesMap
        Queue<String> queue = new LinkedList<String>();

        for (String lhs : this.rules.keySet()) {
            queue.add(lhs);
        }

        while (queue.size() > 0) {
            // If it has the key then we want to add all the things in it to the queue that
            // pass as true for add constitutent
            String lhs = queue.poll();
            Triplet triplet = this.rules.get(lhs);
            // If the lhs is in our unary rules map as a rhs, then we want to add all of the
            // unary rules to this object
            if (unaryRulesMap.containsKey(lhs)) {
                String newRhs = lhs;
                // Get the second layer which represents all the rules that point to this rule
                HashMap<String, Double> secondLayer = unaryRulesMap.get(newRhs);
                for (String newLhs : secondLayer.keySet()) {
                    Double weight = secondLayer.get(newLhs) + triplet.getWeight();
                    boolean inserted = this.addConstituent(newLhs, weight, this, newRhs);
                    if (inserted) {
                        queue.add(newLhs);
                    }
                }
            }
        }

    }

    // // Getter method for the ruleWeightMap
    // public HashMap<String, Double> getWeightMap() {
    // return ruleWeightMap;
    // }

    // // Getter method for the ruleChildrenMap
    // public HashMap<String, CKYEntry[]> getChildrenMap() {
    // return ruleChildrenMap;
    // }

    // // Getter method for the children
    // public CKYEntry[] getChildren (String lhs) {
    // return ruleChildrenMap.get(lhs);
    // }
    public boolean isSentence() {
        return this.rules.containsKey("S");
    }

    public Double getWeight(String lhs) {
        return this.rules.get(lhs).getWeight();
    }

    public boolean containsLhs(String lhs) {
        return this.rules.containsKey(lhs);
    }

    public Triplet getTriplet(String lhs) {
        return this.rules.get(lhs);
    }

    public HashMap<String, Triplet> getRules() {
        return this.rules;
    }

    public String toString() {
        return this.rules.toString();
    }

    public static void main(String[] args) {

    }

}
