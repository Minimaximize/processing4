package processing.data;

interface JSONNodeVisitor<T> {
  T visit(JSONString node);
  T visit(JSONNumber node);
  T visit(JSONInteger node);
  T visit(JSONBoolean node);
  T visit(JSONObject node);
  T visit(JSONArray<?> node);
  T visit(JSONNull<?> node);
}
