package processing.data.json;

public class JSONNull<T> extends JSONElement<T> {
  private JSONElement<T> referenceElement;

  protected JSONNull(JSONElement<T> referenceElement) {
    super(null);

    this.referenceElement = referenceElement;
  }

  @Override public JSONElement<T> setValue(T value) {
    return referenceElement.setValue(value);
  }

  public JSONElement<T> getReferenceElement() {
    return referenceElement;
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }
}
