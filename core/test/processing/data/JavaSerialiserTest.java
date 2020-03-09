package processing.data;


import org.junit.Test;

import java.io.IOException;
import java.io.Writer;

public class JavaSerialiserTest {

  TestWriter testWriter = new TestWriter();

  JSONObject testObject = JSONObject.create(
    b -> b.add("integer", 1)
      .add("float", 1f)
      .add("string", "string value")
      .add("object",
           o -> o.add("innerInt", 1)
             .add("innerFloat", 1f)
             .add("innerString", "inner String")
             .add("innerObject",
                  j -> j.add("foo", "foo")
                    .add("bar", "bar")))
      .add("someBool", true)
    .add("anArray", 1,2,3,4,5,6,7,8,9));

  @Test public void JavaSerializerString() {
    JSONSerializer sut = new JSONSerializer();
    sut.save(testObject, testWriter);
    System.out.println(testWriter.getSb().toString());
  }

  class TestWriter extends Writer {
    private StringBuilder sb = new StringBuilder();

    public StringBuilder getSb() {
      return sb;
    }

    @Override public void write(char[] chars, int i, int i1) {
      sb.append(chars, i, i1);
    }

    @Override public void flush() {

    }

    @Override public void close() {

    }
  }
}
