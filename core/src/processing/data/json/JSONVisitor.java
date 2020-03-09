package processing.data.json;

public interface JSONVisitor<U> {
  U visit(JSONString string);
  U visit(JSONNumber number);
  U visit(JSONInteger integer);
  U visit(JSONBoolean bool);
  U visit(JSONNull nullable);
  U visit(JSONObject object);
  U visit(JSONArray array);
}
