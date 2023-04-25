/**
 * JAW takes:
 * -  a filepath to training data. This training data consists of one english sentence pre line that we will try to 
 * copy the style of
 * - a file path to a PCFG that we will update based off of the parse trees we get for the english sentences in training data
 */
public class JAW {
    
    /**
     * This construct will read in the training data, clean it, then construct a parse tree for each of them using CKYParser. 
     * It will then update the PCFG based on these parse trees using a probabilistic approach.
     */
    public JAW(String trainingData, String PCFG) {

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
    }

    /**
     * Calculates the average precision recall between one grammar tree and a list of grammar trees that have
     * the same seedPartOfSpeech as that one grammar tree 
     */
    public Double getPrecisionRecall(ParseTree tree, String seedPartOfSpeech) {
        return 0.0;
    }


}