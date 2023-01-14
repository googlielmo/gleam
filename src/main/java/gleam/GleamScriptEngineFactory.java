package gleam;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GleamScriptEngineFactory implements ScriptEngineFactory {
    @Override
    public String getEngineName() {
        return "GleamScriptEngine";
    }

    @Override
    public String getEngineVersion() {
        String version = gleam.lang.Interpreter.class.getPackage().getImplementationVersion();
        return version != null ? version : "DEVELOPMENT";
    }

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("scm", "glm");
    }

    @Override
    public List<String> getMimeTypes() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("gleam", "scheme", "GleamSchemeInterpreter");
    }

    @Override
    public String getLanguageName() {
        return "gleam";
    }

    @Override
    public String getLanguageVersion() {
        return "1.0";
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        return null;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "(display " + toDisplay + ")";
    }

    @Override
    public String getProgram(String... statements) {
        return String.join("\n", statements);
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new GleamScriptEngine();
    }
}
