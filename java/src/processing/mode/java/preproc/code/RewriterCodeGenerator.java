package processing.mode.java.preproc.code;

import org.antlr.v4.runtime.TokenStreamRewriter;
import processing.app.Preferences;
import processing.core.PApplet;
import processing.mode.java.preproc.PdePreprocessor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;


/**
 * Utility to rewrite code as part of preprocessing.
 */
public class RewriterCodeGenerator {

  private final String indent1;
  private final String indent2;
  private final String indent3;

  /**
   * Create a new rewriter.
   *
   * @param indentSize Number of spaces in the indent.
   */
  public RewriterCodeGenerator(int indentSize) {
    final char[] indentChars = new char[indentSize];
    Arrays.fill(indentChars, ' ');
    indent1 = new String(indentChars);
    indent2 = indent1 + indent1;
    indent3 = indent2 + indent1;
  }

  /**
   * Prepare preface code to wrap sketch code so that it is contained within a proper Java
   * definition.
   *
   * @param headerWriter The writer into which the header should be written.
   * @param params The parameters for the rewrite.
   * @return Information about the completed rewrite.
   */
  public RewriteResult prepareHeader(TokenStreamRewriter headerWriter, RewriteParams params) {

    RewriteResultBuilder resultBuilder = new RewriteResultBuilder();

    PrintWriterWithEditGen decoratedWriter = new PrintWriterWithEditGen(
        headerWriter,
        resultBuilder,
        0,
        true
    );

    writeHeaderContents(decoratedWriter, params, resultBuilder);

    decoratedWriter.finish();

    return resultBuilder.build();
  }

  /**
   * Prepare the footer for a sketch (finishes the constructs introduced in header like class def).
   *
   * @param footerWriter The writer through which the footer should be introduced.
   * @param params The parameters for the rewrite.
   * @param insertPoint The loction at which the footer should be written.
   * @return Information about the completed rewrite.
   */
  public RewriteResult prepareFooter(TokenStreamRewriter footerWriter, RewriteParams params,
        int insertPoint) {

    RewriteResultBuilder resultBuilder = new RewriteResultBuilder();

    PrintWriterWithEditGen decoratedWriter = new PrintWriterWithEditGen(
        footerWriter,
        resultBuilder,
        insertPoint,
        false
    );

    writeFooterContents(decoratedWriter, params, resultBuilder);

    decoratedWriter.finish();

    return resultBuilder.build();
  }

  /**
   * Write the contents of the header using a prebuilt print writer.
   *
   * @param decoratedWriter he writer though which the comment should be introduced.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeHeaderContents(PrintWriterWithEditGen decoratedWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    if (!params.getisTesting()) writePreprocessorComment(decoratedWriter, params, resultBuilder);
    writeImports(decoratedWriter, params, resultBuilder);

    PdePreprocessor.Mode mode = params.getMode();

    boolean requiresClassHeader = mode == PdePreprocessor.Mode.STATIC;
    requiresClassHeader = requiresClassHeader || mode == PdePreprocessor.Mode.ACTIVE;

    boolean requiresStaticSketchHeader = mode == PdePreprocessor.Mode.STATIC;

    if (requiresClassHeader) {
      writeClassHeader(decoratedWriter, params, resultBuilder);
    }

    if (requiresStaticSketchHeader) {
      writeStaticSketchHeader(decoratedWriter, params, resultBuilder);
    }
  }

  /**
   * Write the contents of the footer using a prebuilt print writer.
   *
   * @param decoratedWriter he writer though which the comment should be introduced.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeFooterContents(PrintWriterWithEditGen decoratedWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    decoratedWriter.addEmptyLine();

    PdePreprocessor.Mode mode = params.getMode();

    boolean requiresStaticSketchFooter = mode == PdePreprocessor.Mode.STATIC;
    boolean requiresClassWrap = mode == PdePreprocessor.Mode.STATIC;
    requiresClassWrap = requiresClassWrap || mode == PdePreprocessor.Mode.ACTIVE;

    if (requiresStaticSketchFooter) {
      writeStaticSketchFooter(decoratedWriter, params, resultBuilder);
    }

    if (requiresClassWrap) {
      writeExtraFieldsAndMethods(decoratedWriter, params, resultBuilder);
      if (!params.getFoundMain()) writeMain(decoratedWriter, params, resultBuilder);
      writeClassFooter(decoratedWriter, params, resultBuilder);
    }
  }

  /**
   * Comment out sketch code before it is moved elsewhere in resulting Java.
   *
   * @param headerWriter The writer though which the comment should be introduced.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writePreprocessorComment(PrintWriterWithEditGen headerWriter, RewriteParams params,
    RewriteResultBuilder resultBuilder) {

    String dateStr = new SimpleDateFormat("YYYY-MM-dd").format(new Date());

    String newCode = String.format(
        "/* autogenerated by Processing preprocessor v%s on %s */",
        params.getVersion(),
        dateStr
    );

    headerWriter.addCodeLine(newCode);
  }

  /**
   * Add imports as part of conversion from processing sketch to Java code.
   *
   * @param headerWriter The writer though which the imports should be introduced.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeImports(PrintWriterWithEditGen headerWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    writeImportList(headerWriter, params.getCoreImports(), params, resultBuilder);
    writeImportList(headerWriter, params.getCodeFolderImports(), params, resultBuilder);
    writeImportList(headerWriter, params.getFoundImports(), params, resultBuilder);
    writeImportList(headerWriter, params.getDefaultImports(), params, resultBuilder);
  }

  /**
   * Write a list of imports.
   *
   * @param headerWriter The writer though which the imports should be introduced.
   * @param imports Collection of imports to introduce.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeImportList(PrintWriterWithEditGen headerWriter, List<String> imports, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    writeImportList(headerWriter, imports.toArray(new String[0]), params, resultBuilder);
  }

  /**
   * Write a list of imports.
   *
   * @param headerWriter The writer though which the imports should be introduced.
   * @param imports Collection of imports to introduce.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeImportList(PrintWriterWithEditGen headerWriter, String[] imports,
        RewriteParams params, RewriteResultBuilder resultBuilder) {

    for (String importDecl : imports) {
      headerWriter.addCodeLine("import " + importDecl + ";");
    }
    if (imports.length > 0) {
      headerWriter.addEmptyLine();
    }
  }

  /**
   * Write the prefix which defines the enclosing class for the sketch.
   *
   * @param headerWriter The writer through which the header should be introduced.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeClassHeader(PrintWriterWithEditGen headerWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    headerWriter.addCodeLine("public class " + params.getSketchName() + " extends PApplet {");

    headerWriter.addEmptyLine();
  }

  /**
   * Write the header for a static sketch (no methods).
   *
   * @param headerWriter The writer through which the header should be introduced.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeStaticSketchHeader(PrintWriterWithEditGen headerWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    headerWriter.addCodeLine(indent1 + "public void setup() {");
  }

  /**
   * Write the bottom of the sketch code for static mode.
   *
   * @param footerWriter The footer into which the text should be written.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeStaticSketchFooter(PrintWriterWithEditGen footerWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    footerWriter.addCodeLine(indent2 +   "noLoop();");
    footerWriter.addCodeLine(indent1 + "}");
  }

  /**
   * Write code supporting special functions like size.
   *
   * @param classBodyWriter The writer into which the code should be written. Should be for class
   *    body.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeExtraFieldsAndMethods(PrintWriterWithEditGen classBodyWriter,
        RewriteParams params, RewriteResultBuilder resultBuilder) {

    if (!params.getIsSizeValidInGlobal()) {
      return;
    }

    String settingsOuterTemplate = indent1 + "public void settings() { %s }";

    String settingsInner;
    if (params.getIsSizeFullscreen()) {
      String fullscreenInner = params.getSketchRenderer().orElse("");
      settingsInner = String.format("fullScreen(%s);", fullscreenInner);
    } else {

      if (params.getSketchWidth().isEmpty() || params.getSketchHeight().isEmpty()) {
        return;
      }

      StringJoiner argJoiner = new StringJoiner(",");
      argJoiner.add(params.getSketchWidth().get());
      argJoiner.add(params.getSketchHeight().get());

      if (params.getSketchRenderer().isPresent()) {
        argJoiner.add(params.getSketchRenderer().get());
      }

      settingsInner = String.format("size(%s);", argJoiner.toString());
    }


    String newCode = String.format(settingsOuterTemplate, settingsInner);

    classBodyWriter.addEmptyLine();
    classBodyWriter.addCodeLine(newCode);
  }

  /**
   * Write the main method.
   *
   * @param footerWriter The writer into which the footer should be written.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeMain(PrintWriterWithEditGen footerWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    footerWriter.addEmptyLine();
    footerWriter.addCodeLine(indent1 + "static public void main(String[] passedArgs) {");
    footerWriter.addCode(indent2 +   "String[] appletArgs = new String[] { ");

    { // assemble line with applet args
      if (Preferences.getBoolean("export.application.fullscreen")) {
        footerWriter.addCode("\"" + PApplet.ARGS_FULL_SCREEN + "\", ");

        String bgColor = Preferences.get("run.present.bgcolor");
        footerWriter.addCode("\"" + PApplet.ARGS_BGCOLOR + "=" + bgColor + "\", ");

        if (Preferences.getBoolean("export.application.stop")) {
          String stopColor = Preferences.get("run.present.stop.color");
          footerWriter.addCode("\"" + PApplet.ARGS_STOP_COLOR + "=" + stopColor + "\", ");
        } else {
          footerWriter.addCode("\"" + PApplet.ARGS_HIDE_STOP + "\", ");
        }
      }
      footerWriter.addCode("\"" + params.getSketchName() + "\"");
    }

    footerWriter.addCodeLine(" };");

    footerWriter.addCodeLine(indent2 +   "if (passedArgs != null) {");
    footerWriter.addCodeLine(indent3 +     "PApplet.main(concat(appletArgs, passedArgs));");
    footerWriter.addCodeLine(indent2 +   "} else {");
    footerWriter.addCodeLine(indent3 +     "PApplet.main(appletArgs);");
    footerWriter.addCodeLine(indent2 +   "}");
    footerWriter.addCodeLine(indent1 + "}");
  }

  /**
   * Write the end of the class body for the footer.
   *
   * @param footerWriter The writer into which the footer should be written.
   * @param params The parameters for the rewrite.
   * @param resultBuilder Builder for reporting out results to the caller.
   */
  protected void writeClassFooter(PrintWriterWithEditGen footerWriter, RewriteParams params,
        RewriteResultBuilder resultBuilder) {

    footerWriter.addCodeLine("}");
  }

}
