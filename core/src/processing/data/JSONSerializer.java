package processing.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JSONSerializer {

  public boolean save(JSONObject root, Writer writer) {
    try (writer) {
      writer.write(root.accept(new JSONStringifier()).toString());
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  static class JSONStringifier implements JSONNodeVisitor<StringBuilder> {
    private StringBuilder stringBuilder = new StringBuilder();

    @Override public StringBuilder visit(JSONString node) {
      return propertyString(node).append('"').append(node.getValue())
        .append('"');
    }

    @Override public StringBuilder visit(JSONNumber node) {
      return propertyString(node).append(node.getValue().toString());
    }

    @Override public StringBuilder visit(JSONInteger node) {
      return propertyString(node).append(node.getValue().toString());
    }

    @Override public StringBuilder visit(JSONBoolean node) {
      return propertyString(node).append(node.getValue().toString());
    }

    @Override public StringBuilder visit(JSONNull<?> node) {
      return propertyString(node).append("null");
    }

    @Override public StringBuilder visit(JSONObject node) {
      if (node.getKey() != null)
        propertyString(node);

      return stringBuilder.append("{ ").append(
        node.getValue().values().parallelStream()
          .map(n -> n.accept(new JSONStringifier()))
          .collect(Collectors.joining(", "))).append(" }");
    }

    @Override public StringBuilder visit(JSONArray<?> node) {
      if (node.getKey() != null)
        propertyString(node);

      return stringBuilder.append("[").append(
        Arrays.stream(node.getValue()).map(n -> ((Object) n).toString())
          .collect(Collectors.joining(", "))).append("]");
    }

    private StringBuilder propertyString(JSONNodeBase node) {
      return stringBuilder.append('"').append(node.getKey()).append('"')
        .append(" : ");
    }
  }
}
