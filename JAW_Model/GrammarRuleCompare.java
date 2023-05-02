import java.util.Comparator;

public class GrammarRuleCompare implements Comparator<GrammarRule>{
    // Since java's priority queue is a min heap, we have to return -1 for greater values and 1 for smaller values
    public int compare (GrammarRule gRule1, GrammarRule gRule2) {
        if (gRule1.getWeight() >= gRule2.getWeight()) {
            return -1;
        } else {
            return 1;
        }
    }
}
