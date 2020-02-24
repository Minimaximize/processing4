package processing.data;

public interface JSONNodeBase {
  String getKey();

  <T> T accept(JSONNodeVisitor<T> visitor);

  default boolean keyEquals(String key) {
    return getKey().equals(key);
  }
}

abstract class JSONNode<T> implements JSONNodeBase {
  private final String key;

  private final T value;

  protected JSONNode(String key, T value) {
    this.key = key;
    this.value = value;
  }

  @Override public String getKey() {
    return key;
  }

  public T getValue() {
    return value;
  }
}

class JSONString extends JSONNode<String> {

  protected JSONString(String key, String value) {
    super(key, value);
  }

  @Override public <T> T accept(JSONNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

class JSONNumber extends JSONNode<Double> {

  protected <T extends Number> JSONNumber(String key, T value) {
    super(key, value.doubleValue());
  }

  @Override public <T> T accept(JSONNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

class JSONInteger extends JSONNode<Integer> {

  protected <T extends Number> JSONInteger(String key, T value) {
    super(key, value.intValue());
  }

  @Override public <T> T accept(JSONNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

class JSONBoolean extends JSONNode<Boolean> {

  protected JSONBoolean(String key, Boolean value) {
    super(key, value);
  }

  @Override public <T> T accept(JSONNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

class JSONNull<T extends JSONNodeBase> extends JSONNode {
  private final T innerType;

  JSONNull(T innerType) {
    super(innerType.getKey(), null);
    this.innerType = innerType;
  }

  public T getInnerType() {
    return innerType;
  }

  @Override public <U> U accept(JSONNodeVisitor<U> visitor) {
    return visitor.visit(this);
  }
}