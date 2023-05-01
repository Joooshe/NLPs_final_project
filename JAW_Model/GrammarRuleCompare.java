import java.util.Comparator;

public class GrammarRuleCompare implements Comparator<GrammarRule>{
    public int compare (GrammarRule gRule1, GrammarRule gRule2) {
        if (gRule1.getWeight() >= gRule2.getWeight()) {
            return 1;
        } else {
            return -1;
        }
    }
}
