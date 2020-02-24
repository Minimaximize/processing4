package processing.data;

import java.util.Arrays;
import java.util.List;

public class JSONArray<T> extends JSONNode<T[]> {
  protected JSONArray(String key, T... values) {
    super(key, values);
  }

  @Override public <T> T accept(JSONNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
