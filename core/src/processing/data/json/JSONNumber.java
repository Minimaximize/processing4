package processing.data.json;

public class JSONNumber extends JSONElement<Number> {
  protected JSONNumber(Number value) {
    super(value);
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }
}
