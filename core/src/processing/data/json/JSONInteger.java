package processing.data.json;

public class JSONInteger extends JSONElement<Integer> {
  protected JSONInteger(Integer value) {
    super(value);
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }
}
