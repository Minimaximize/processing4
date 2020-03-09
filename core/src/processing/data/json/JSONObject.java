package processing.data.json;

import java.util.*;
import java.util.stream.Collectors;

public class JSONObject extends JSONElement<JSONObject.PropertyMap> {

  protected JSONObject(PropertyMap value) {
    super(value);
  }

  @Override public <U> U accept(JSONVisitor<U> visitor) {
    return visitor.visit(this);
  }

  public static class PropertyMap implements Map<String, JSONElement<?>> {
    private SortedSet<Member<?>> elements = new TreeSet<>(
      Comparator.comparing(Member::getKey));

    private PropertyMap() {
    }

    @Override public int size() {
      return elements.size();
    }

    @Override public boolean isEmpty() {
      return elements.isEmpty();
    }

    @Override public boolean containsKey(Object o) {
      return elements.parallelStream()
        .anyMatch(element -> element.getKey().equals(o));
    }

    @Override public boolean containsValue(Object o) {
      return elements.parallelStream()
        .anyMatch(element -> element.getValue().equals(o));
    }

    @Override public JSONElement<?> get(Object o) {
      return elements.parallelStream().filter(m -> m.getKey().equals(o))
        .findFirst().map(Member::getValue).get();
    }

    @Override public JSONElement<?> put(String s, JSONElement<?> jsonElement) {
      Member m = new Member<>(s, jsonElement);
      elements.add(m);
      return jsonElement;
    }

    @Override public JSONElement<?> remove(Object o) {
      JSONElement e = get(o);
      elements.remove(o);
      return e;
    }

    @Override public void putAll(
      Map<? extends String, ? extends JSONElement<?>> properties) {
      elements.addAll(
        properties.entrySet()
          .parallelStream()
          .map(e -> new Member<>(e.getKey(), e.getValue()))
          .collect(Collectors.toCollection(TreeSet::new)));
    }

    @Override public void clear() {

    }

    @Override public Set<String> keySet() {
      return null;
    }

    @Override public Collection<JSONElement<?>> values() {
      return null;
    }

    @Override public Set<Entry<String, JSONElement<?>>> entrySet() {
      return;
    }
  }

  public static class Member<T> implements Map.Entry<String, JSONElement<T>> {
    private String key;

    private JSONElement<T> value;

    Member(String key, JSONElement<T> value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public JSONElement<T> getValue() {
      return value;
    }

    @Override public JSONElement<T> setValue(JSONElement<T> jsonElement) {
      value = jsonElement;
      return value;
    }
  }
}
