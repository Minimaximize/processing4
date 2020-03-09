package processing.data.json;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class JSONArray extends JSONElement<JSONElement<?>[]> implements Iterable<JSONElement<?>> {
  private List<JSONElement<?>> elements;

  public <T> JSONArray(JSONElement<T>... value) {
    super(null);
    elements = Arrays.asList(value);
  }

  @Override public JSONElement<?>[] getValue() {
    return elements.toArray(JSONElement<?>[]::new);
  }

  public JSONElement<?> get(int index) {
    return this.getValue()[index];
  }

  public int size() {
    return this.getValue().length;
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }

  @Override public Iterator<JSONElement<?>> iterator() {
    return Arrays.asList(getValue()).iterator();
  }
}