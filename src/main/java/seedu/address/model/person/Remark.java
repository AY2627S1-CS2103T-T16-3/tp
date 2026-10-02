package seedu.address.model.person;
import static java.util.Objects.requireNonNull;
/** Represents an optional remark attached to a person. */
public class Remark {
    public final String value;
    public Remark(String remark) { requireNonNull(remark); value = remark; }
    @Override public String toString() { return value; }
    @Override public boolean equals(Object other) { return other instanceof Remark r && value.equals(r.value); }
    @Override public int hashCode() { return value.hashCode(); }
}
