package processing.data.json;

public class JSONBoolean extends JSONElement<Boolean> {
  protected JSONBoolean(Boolean value) {
    super(value);
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }
}
