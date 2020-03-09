package processing.data.json;

abstract class JSONElement<T> {
  private T value;

  protected JSONElement(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public JSONElement<T> setValue(T value) {
    this.value = value;
    return this;
  }

  public abstract <U> U accept(JSONVisitor<U> visitor);
}
