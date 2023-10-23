enum RuleType {
    LEXICAL, UNARY, BINARY
}

/**
 * A class used to store the type of rule in our context free grammar
 * There are 3 types of rules denoted by the RuleType enum above
 * 
 * @author Joshua Garcia-Kimble, William Yang
 */
public class Triplet {

    private String lhs;
    private Double weight;

    private String rhs1;
    private CKYEntry rhs1Child;

    private String rhs2;
    private CKYEntry rhs2Child;

    private RuleType type;

    public Triplet() {
        this.weight = null;
        this.rhs1 = null;
        this.rhs1Child = null;
        this.rhs2 = null;
        this.rhs2Child = null;
        this.type = null;
    }

    // For binary rule
    public Triplet(String lhs, Double weight, CKYEntry rhs1Child, CKYEntry rhs2Child, String rhs1, String rhs2) {
        this.lhs = lhs;
        this.weight = weight;
        this.rhs1 = rhs1;
        this.rhs1Child = rhs1Child;
        this.rhs2 = rhs2;
        this.rhs2Child = rhs2Child;
        this.type = RuleType.BINARY;
    }

    // For unary rule
    public Triplet(String lhs, Double weight, CKYEntry rhs1Child, String rhs1) {
        this.lhs = lhs;
        this.weight = weight;
        this.rhs1 = rhs1;
        this.rhs1Child = rhs1Child;
        this.type = RuleType.UNARY;
    }

    // For lexical rule
    public Triplet(String lhs, Double weight, String rhs1) {
        this.lhs = lhs;
        this.weight = weight;
        this.rhs1 = rhs1;
        this.type = RuleType.LEXICAL;
    }

    public boolean isUnary() {
        switch (this.type) {
            case BINARY:
                return false;
            default:
                return true;
        }
    }

    public boolean isLexical() {
        switch (this.type) {
            case LEXICAL:
                return true;
            default:
                return false;
        }
    }

    public String getLhs() {
        return lhs;
    }

    public Double getWeight() {
        return weight;
    }

    public String getRhs1() {
        return rhs1;
    }

    public CKYEntry getRhs1Child() {
        return rhs1Child;
    }

    public String getRhs2() {
        return rhs2;
    }

    public CKYEntry getRhs2Child() {
        return rhs2Child;
    }

    public RuleType getType() {
        return type;
    }

    public String toString() {
        return lhs + " " + String.valueOf(weight) + " " + rhs1 + " " + rhs2;
    }

}
