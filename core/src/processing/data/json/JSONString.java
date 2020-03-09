package processing.data.json;

public class JSONString extends JSONElement<String> {

  protected JSONString(String value) {
    super(value);
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }
}
