/*
 * Copyright (c) 2001 Guglielmo Nigri.  All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it would be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Further, this software is distributed without any warranty that it is
 * free of the rightful claim of any third person regarding infringement
 * or the like.  Any license provided herein, whether implied or
 * otherwise, applies only to this software file.  Patent licenses, if
 * any, provided herein do not apply to combinations of this program with
 * other software, or any other product whatsoever.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write the Free Software Foundation, Inc., 59
 * Temple Place - Suite 330, Boston MA 02111-1307, USA.
 *
 * Contact information: Guglielmo Nigri <guglielmonigri@yahoo.it>
 *
 */

/*
 * Interpreter.java
 *
 * Created on 6-jan-2007, 11.05
 *
 */

package gleam.lang;

import gleam.library.Primitive;
import gleam.util.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static gleam.lang.Environment.Kind.*;
import static gleam.util.Logger.Level.*;

/**
 * The Gleam Scheme Interpreter
 */
public class Interpreter {

    static final Symbol INTERPRETER_SYMBOL = Symbol.makeUninternedSymbol("__intp__");

    /** the keyword set */
    private static final Collection<Symbol> kwSet = new HashSet<>();

    /** the short-help map */
    private static final HashMap<String, String> helpComment = new HashMap<>();

    /** the long-help map */
    private static final HashMap<String, String> helpDocumentation = new HashMap<>();

    private static boolean traceEnabled = false;

    /**
     * true if bootstrap code already loaded
     */
    private static boolean bootstrapped = false;

    /** the program continuation */
    private final Continuation cont;

    /** the null environment, as defined in r5rs */
    private final Environment nullEnv = new SystemEnvironment(NULL_ENV);

    /** the scheme-report environment, as defined in r5rs */
    private final Environment reportEnv = new SystemEnvironment(nullEnv, REPORT_ENV);

    /** the interaction environment, as defined in r5rs */
    private final Environment interactionEnv = new SystemEnvironment(reportEnv, INTERACTION_ENV);

    /** the current-input-port */
    private InputPort cin = null;

    /** the current-output-port */
    private OutputPort cout = null;

    /** the accumulator register */
    private Entity accum;

    /**
     * the session (top-level) environment;
     * can be changed by the application.
     */
    private Environment sesnEnv = null;

    /**
     * Private constructor
     * @throws gleam.lang.GleamException on any error
     */
    private Interpreter() throws GleamException {
        initEnvironments();
        bindIOPorts();
        cont = new Continuation();
        accum = Void.value;
        setSessionEnv(interactionEnv, Environment.newEnvironment(cin, cout));
    }

    public static Environment getSessionEnv(Environment env)
            throws GleamException
    {
        return env.getInterpreter().getSessionEnv();
    }

    public static Entity getNullEnv(Environment env)
            throws GleamException
    {
        return env.getInterpreter().getNullEnv();
    }

    public static Entity getSchemeReportEnv(Environment env)
            throws GleamException
    {
        return env.getInterpreter().getSchemeReportEnv();
    }

    public static Entity getInteractionEnv(Environment env)
            throws GleamException
    {
        return env.getInterpreter().getInteractionEnv();
    }

    /**
     * Gets the comment string for a procedure
     */
    public static String getHelpComment(String name) {
        return helpComment.get(name);
    }

    /**
     * Gets the comment string for a procedure
     */
    public static String getHelpDocumentation(String name) {
        return helpDocumentation.get(name);
    }

    /**
     * Gets the set of help-enabled procedures
     */
    public static Set<String> getHelpNames() {
        return new TreeSet<>(helpDocumentation.keySet());
    }

    /**
     * Determines if a given symbol is a keyword.
     */
    static boolean isKeyword(Symbol s) {
        return kwSet.contains(s);
    }

    /**
     * Creates and bootstraps a new Interpreter.
     * @return a Gleam Scheme Interpreter
     *
     * @throws GleamException in case of error
     */
    public static Interpreter newInterpreter()
            throws GleamException
    {
        Interpreter interpreter = new Interpreter();
        Logger.enter(Logger.Level.FINE, String.format("created Interpreter %s", interpreter));
        interpreter.bootstrap();
        Logger.enter(FINE, String.format("bootstrapped Interpreter %s", interpreter));

        return interpreter;
    }

    public static void addForEval(Entity expr, Environment env, Continuation cont) throws GleamException
    {
        expr = expr.analyze(env).optimize(env);
        cont.begin(new ExpressionAction(expr, env, null));
    }

    /**
     * Sets the current session environment.
     * @param currentEnv the current environment
     * @param newSessionEnv the new session environment
     */
    public static void setSessionEnv(Environment currentEnv, Environment newSessionEnv)
            throws GleamException
    {
        Interpreter intp = currentEnv.getInterpreter();
        intp.sesnEnv = newSessionEnv;
        // link to interaction env
        intp.sesnEnv.parent = intp.getInteractionEnv();
    }

    /**
     * @return true if trace is enabled
     */
    public static boolean traceEnabled() {
        return traceEnabled;
    }

    /** enables trace */
    public static void traceOn() {
        traceEnabled = true;
    }

    /** disables trace */
    public static void traceOff() {
        traceEnabled = false;
    }

    /**
     * Imports primitives
     */
    private void importPrimitives(Primitive[] primitives) {
        Environment instEnv;
        for (Primitive primitive : primitives) {
            switch (primitive.definitionEnv) {
                case NULL_ENV:
                    instEnv = getNullEnv();
                    break;

                case REPORT_ENV:
                    instEnv = getSchemeReportEnv();
                    break;

                case INTERACTION_ENV:
                default:
                    instEnv = getInteractionEnv();
            }
            installPrimitive(instEnv, primitive);
        }
    }

    /**
     * Installs a primitive in an environment
     * @param env the environment
     * @param primitive the primitive
     */
    private void installPrimitive(Environment env, Primitive primitive) {
        Symbol name = Symbol.makeSymbol(primitive.getName());
        Procedure proc = primitive.keyword ? new SyntaxProcedure(primitive) : new PrimitiveProcedure(primitive);
        env.define(name, proc);

        if (primitive.keyword)
            kwSet.add(name);

        if (primitive.comment != null)
            helpComment.put(primitive.getName(), primitive.comment);

        if (primitive.documentation != null)
            helpDocumentation.put(primitive.getName(), primitive.documentation);
    }

    /**
     * Initialize the three initial environments (null, report, interaction).
     */
    private void initEnvironments() {
        nullEnv.define(INTERPRETER_SYMBOL, new JavaObject(this));
        try {
            /*
             * import primitives
             */
            importPrimitives(gleam.library.Booleans.primitives);
            importPrimitives(gleam.library.Characters.primitives);
            importPrimitives(gleam.library.ControlFeatures.primitives);
            importPrimitives(gleam.library.Equivalence.primitives);
            importPrimitives(gleam.library.Eval.primitives);
            importPrimitives(gleam.library.Input.primitives);
            importPrimitives(gleam.library.Interaction.primitives);
            importPrimitives(gleam.library.JavaInterface.primitives);
            importPrimitives(gleam.library.Numbers.primitives);
            importPrimitives(gleam.library.Output.primitives);
            importPrimitives(gleam.library.PairsAndLists.primitives);
            importPrimitives(gleam.library.Ports.primitives);
            importPrimitives(gleam.library.Strings.primitives);
            importPrimitives(gleam.library.Symbols.primitives);
            importPrimitives(gleam.library.Syntax.primitives);
            importPrimitives(gleam.library.SystemInterface.primitives);
            importPrimitives(gleam.library.Vectors.primitives);

            /*
             * define special symbols
             */
            getSchemeReportEnv().define(Symbol.ERROBJ, Void.value);
            getSchemeReportEnv().define(Symbol.CALL_CC, getSchemeReportEnv().lookup(Symbol.CALL_WITH_CURRENT_CONTINUATION ));
            getSchemeReportEnv().define(Symbol.makeSymbol("null"), new JavaObject()); // the Java null value
        }
        catch (GleamException e) {
            // should never happen
            Logger.enter(ERROR,
                    "Internal error during environment initialization: "
                            + e.getMessage());
        }
    }

    public Environment getNullEnv()
    {
        return nullEnv;
    }

    public Environment getSchemeReportEnv()
    {
        return reportEnv;
    }

    public Environment getInteractionEnv()
    {
        return interactionEnv;
    }

    /**
     * binds current I/O ports to system standard I/O
     */
    private void bindIOPorts()
    {
        cin = new InputPort(new java.io.BufferedReader(
                new java.io.InputStreamReader(
                        java.lang.System.in)));

        boolean isConsole = java.lang.System.console() != null;
        cout = new OutputPort(java.lang.System.out, isConsole);

        getNullEnv().setIn(cin);
        getNullEnv().setOut(cout);
        getSchemeReportEnv().setIn(cin);
        getSchemeReportEnv().setOut(cout);
        getInteractionEnv().setIn(cin);
        getInteractionEnv().setOut(cout);
    }

    /**
     * Evaluates a Gleam Scheme expression in the current session environment
     * @param expr a String holding an arbitrary Scheme expression
     * @return the value of the expression
     * @throws gleam.lang.GleamException as soon as an error condition is
     *  raised, the loading / execution operation will terminate,
     *  leaving the session environment in a possibly modified state
     */
    public Entity eval(String expr) throws GleamException
    {
        return eval(new java.io.StringReader(expr));
    }

    /**
     * Evaluates a Gleam Scheme program in the current session environment
     * @param reader a <CODE>java.io.Reader</CODE> representing the program
     *  stream
     * @return the return value of the program
     * @throws gleam.lang.GleamException on any error
     */
    public Entity eval(java.io.Reader reader) throws GleamException
    {
        load(new InputPort(reader), getSessionEnv());
        return accum;
    }

    /**
     * Evaluates a Gleam Scheme entity as code in a given environment.
     *
     * @param expr the <CODE>gleam.lang.Entity</CODE> corresponding to a
     *  Scheme expression to evaluate
     * @param env the environment of evaluation
     * @throws gleam.lang.GleamException on any error
     * @return the value of the expression
     */
    public Entity eval(Entity expr, Environment env) throws GleamException
    {
        expr = expr.analyze(env).optimize(env);
        cont.begin(new ExpressionAction(expr, env, null));
        execute();
        return accum;
    }

    /**
     * Replaces the current continuation with a new one.
     * This method is used to implement the evaluation of continuations.
     * Equivalent to a <CODE>goto</CODE> instruction.
     * @param cont the new current continuation for this <CODE>Interpreter</CODE>
     */
    public void replaceContinuation(Continuation cont)
    {
        this.cont.head = cont.head;
    }

    /**
     * Clears up the current continuation, e.g., after an error.
     */
    public void clearContinuation()
    {
        this.cont.clear();
    }

    /**
     * The main loop of program execution.
     * When this method is called, the first action in the current
     * continuation is invoked with the current value of the accumulator
     * register as its argument. When a result is produced, it is stored in
     * the accumulator. Then the next action in the continuation chain is
     * extracted, and the loop repeats itself until there are no more
     * actions to execute.
     * @throws gleam.lang.GleamException on any error
     */
    private void execute() throws GleamException
    {
        Action currentAction = cont.head;
        Entity tmp;
        while (currentAction != null) {
            tmp = currentAction.invoke(accum, cont);
            if (tmp != null) {
                accum = tmp;
            }
            currentAction = cont.head;
        }
    }

    /**
     * Loads and executes a Gleam Scheme program from a stream
     * @param reader a <CODE>gleam.lang.InputPort</CODE> representing the
     *  program stream
     * @param env the environment for program execution
     * @throws gleam.lang.GleamException as soon as an error condition is
     *  raised, the loading / execution operation will terminate,
     *  leaving the environment in a possibly modified state
     */
    public void load(InputPort reader, Environment env) throws GleamException
    {
        // read
        Entity obj;
        Entity val;
        while ((obj = reader.read()) != Eof.value()) {
            // eval
            Logger.enter(FINE, "load: read object", obj);
            val = eval(obj, env);
            Logger.enter(FINE, "load: result is", val);
        }
    }

    /**
     * Loads and executes the bootstrap code for the Gleam Scheme Interpreter.
     * @throws gleam.lang.GleamException on any error
     */
    private synchronized void bootstrap() throws GleamException
    {
        if (!bootstrapped) {
            gleam.lang.InputPort bootstrap =
                new gleam.lang.InputPort(
                    new java.io.BufferedReader(
                        new java.io.InputStreamReader(
                                Objects.requireNonNull(getClass().getResourceAsStream("/bootstrap.scm")))));
            load(bootstrap, getSchemeReportEnv());
            bootstrapped = true;
        }
    }

    /**
     * Gets the current session environment
     * @return the current session environment
     */
    public Environment getSessionEnv()
    {
        return sesnEnv;
    }

    /**
     * Binds a Java object in the current session environment.
     * A variable with the given name will be bound to the given Java object
     * in the current session environment
     * @param name the variable name for the object
     * @param object the <CODE>java.lang.Object</CODE> to bind to the given
     *  name
     */
    public void bind(String name, Object object) {
        getSessionEnv().define(Symbol.makeSymbol(name), new JavaObject(object));
    }

    /** Gets current-input-port */
    public InputPort getCin() {
        return cin;
    }

    /** Sets current-input-port */
    public void setCin(InputPort newcin) {
        cin = newcin;
    }

    /** Gets current-output-port */
    public OutputPort getCout() {
        return cout;
    }

    /** Sets current-output-port */
    public void setCout(OutputPort newcout) {
        cout = newcout;
    }
}
