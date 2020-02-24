package processing.data;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class JSONObject extends JSONNode<JSONObject.PropertyMap> {

  protected JSONObject(PropertyMap value) {
    this(null, value);
  }

  protected JSONObject(String key, PropertyMap value) {
    super(key, value);
  }

  public static JSONObject create(UnaryOperator<PropertyMapBuilder> builder) {
    return create(null, builder);
  }

  public static JSONObject create(String key,
                                  UnaryOperator<PropertyMapBuilder> builder) {
    return new JSONObject(key,
                          builder.apply(new PropertyMapBuilder()).propertyMap);
  }

  @Override public <T> T accept(JSONNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public static class PropertyMap {
    private TreeSet<JSONNode> properties = new TreeSet<>(
      Comparator.comparing(JSONNode::getKey));

    public int size() {
      return properties.size();
    }

    public boolean isEmpty() {
      return properties.isEmpty();
    }

    public boolean containsKey(String key) {
      return properties.parallelStream().anyMatch(n -> n.keyEquals(key));
    }

    public JSONNodeBase get(String key) {
      return properties.parallelStream().filter(n -> n.keyEquals(key))
        .findFirst().get();
    }

    public Set<String> keySet() {
      return properties.parallelStream().map(JSONNodeBase::getKey)
        .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<JSONNodeBase> values() {
      return properties.parallelStream()
        .collect(Collectors.toCollection(ArrayList::new));
    }
  }

  static class PropertyMapBuilder {
    private PropertyMap propertyMap = new PropertyMap();

    public PropertyMapBuilder add(String key,
                                  UnaryOperator<PropertyMapBuilder> context) {
      assertPropertyKeyUnique(key);
      PropertyMap inner = context.apply(new PropertyMapBuilder()).propertyMap;
      addOrDefault(inner.isEmpty() ? null : inner, o -> new JSONObject(key, o));

      return this;
    }

    public <T> PropertyMapBuilder add(String key, T... values) {
      assertPropertyKeyUnique(key);

      addOrDefault(values, v -> new JSONArray(key, v));

      return this;
    }

    public PropertyMapBuilder add(String key, String value) {
      assertPropertyKeyUnique(key);
      addOrDefault(value, v -> new JSONString(key, v));

      return this;
    }

    public PropertyMapBuilder add(String key, Number value) {
      assertPropertyKeyUnique(key);
      addOrDefault(value.doubleValue(), v -> new JSONNumber(key, v));

      return this;
    }

    public PropertyMapBuilder add(String key, Integer value) {
      assertPropertyKeyUnique(key);
      addOrDefault(value, v -> new JSONInteger(key, v));

      return this;
    }

    public PropertyMapBuilder add(String key, boolean value) {
      assertPropertyKeyUnique(key);
      addOrDefault(value, v -> new JSONBoolean(key, v));

      return this;
    }

    private void assertPropertyKeyUnique(String key) {
      if (propertyMap.containsKey(key))
        throw new RuntimeException(
          "Property named '" + key + "' already exists on this object.");
    }

    private <T> void addOrDefault(T value,
                                  Function<T, JSONNode<T>> factory) {
      propertyMap.properties.add(value == null ?
                                   new JSONNull<>(factory.apply(null)) :
                                   factory.apply(value));
    }
  }
}



