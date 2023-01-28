/*
 * Copyright (c) 2001-2023 Guglielmo Nigri.  All Rights Reserved.
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

import static gleam.lang.Environment.Kind.INTERACTION_ENV;
import static gleam.lang.Environment.Kind.REPORT_ENV;
import static gleam.util.Logger.Level.CONFIG;
import static gleam.util.Logger.Level.DEBUG;
import static gleam.util.Logger.Level.ERROR;


/**
 * The Gleam Scheme Interpreter
 */
public class Interpreter
{

    static final Symbol INTERPRETER_SYMBOL = Symbol.makeUninternedSymbol("__intp__");

    private static final Logger logger = Logger.getLogger();

    /**
     * the keyword set
     */
    private static final Collection<Symbol> kwSet = new HashSet<>();

    /**
     * the short-help map
     */
    private static final HashMap<String, String> helpComment = new HashMap<>();

    /**
     * the long-help map
     */
    private static final HashMap<String, String> helpDocumentation = new HashMap<>();

    /**
     * the null environment, as defined in r5rs
     */
    private static final Environment nullEnv = new SystemEnvironment();

    /**
     * the scheme-report environment, as defined in r5rs
     */
    private static final Environment reportEnv = new SystemEnvironment(nullEnv, REPORT_ENV);

    /**
     * the interaction environment, as defined in r5rs
     */
    private static final Environment interactionEnv = new SystemEnvironment(reportEnv, INTERACTION_ENV);

    /**
     * the global shared environment
     */
    private static Environment globalEnv = new Environment(interactionEnv);

    /**
     * true if bootstrap code already loaded
     */
    private static volatile boolean bootstrapped = false;
    /**
     * the program continuation
     */
    private final Continuation cont = new Continuation();
    /**
     * the session (top-level) environment; typically the environment used by
     * the application.
     */
    private Environment sessionEnv = null;

    private boolean traceEnabled = false;

    /**
     * the current-input-port
     */
    private InputPort cin = null;

    /**
     * the current-output-port
     */
    private OutputPort cout = null;
    /**
     * the accumulator register
     */
    private Entity accum = Void.VALUE;

    /**
     * Private constructor
     */
    private Interpreter()
    {
        initEnvironments();
        bindIOPorts();

        setSessionEnv(Environment.newEnvironment(cin, cout));
    }

    public static Environment getNullEnv()
    {
        return nullEnv;
    }

    public static Environment getSchemeReportEnv()
    {
        return reportEnv;
    }

    public static Environment getInteractionEnv()
    {
        return interactionEnv;
    }



    public static void setGlobalEnv(Interpreter intp, Environment globalEnv)
    {
        globalEnv.setParent(interactionEnv);
        intp.sessionEnv.setParent(globalEnv);
        Interpreter.globalEnv = globalEnv;
    }

    /**
     * Gets the comment string for a procedure
     */
    public static String getHelpComment(String name)
    {
        return helpComment.get(name);
    }

    /**
     * Gets the comment string for a procedure
     */
    public static String getHelpDocumentation(String name)
    {
        return helpDocumentation.get(name);
    }

    /**
     * Gets the set of help-enabled procedures
     */
    public static Set<String> getHelpNames()
    {
        return new TreeSet<>(helpDocumentation.keySet());
    }

    /**
     * Determines if a given symbol is a keyword.
     */
    static boolean isKeyword(Symbol s)
    {
        return kwSet.contains(s);
    }

    /**
     * Creates and bootstraps a new Interpreter.
     *
     * @return a Gleam Scheme Interpreter
     *
     * @throws GleamException in case of error
     */
    public static Interpreter newInterpreter() throws GleamException
    {
        try {
            Interpreter interpreter = new Interpreter();
            logger.log(Logger.Level.DEBUG, () -> String.format("created Interpreter %s", interpreter));
            Interpreter.bootstrap(interpreter);
            return interpreter;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void addForEval(Entity expr, Environment env, Continuation cont) throws GleamException
    {
        expr = expr.analyze(env).optimize(env);
        cont.begin(new ExpressionAction(expr, env, null));
    }

    /**
     * Loads and executes the bootstrap code for the Gleam Scheme Interpreter.
     *
     * @throws gleam.lang.GleamException on any error
     */
    private static synchronized void bootstrap(Interpreter interpreter) throws GleamException
    {
        if (bootstrapped) {
            return;
        }
        gleam.lang.InputPort bootstrap = new gleam.lang.InputPort(new java.io.BufferedReader(new java.io.InputStreamReader(Objects.requireNonNull(interpreter.getClass()
                                                                                                                                                             .getResourceAsStream("/bootstrap.scm")))));
        interpreter.load(bootstrap, getSchemeReportEnv());
        bootstrapped = true;
        logger.log(CONFIG, "Gleam bootstrapped");
    }

    /**
     * @return true if trace is enabled
     */
    public boolean traceEnabled()
    {
        return traceEnabled;
    }

    /**
     * enables trace
     */
    public void traceOn()
    {
        traceEnabled = true;
    }

    /**
     * disables trace
     */
    public void traceOff()
    {
        traceEnabled = false;
    }

    /**
     * Evaluates a Gleam Scheme expression in the current session environment
     *
     * @param expr a String holding an arbitrary Scheme expression
     *
     * @return the value of the expression
     *
     * @throws gleam.lang.GleamException as soon as an error condition is
     *                                   raised, the loading / execution
     *                                   operation will terminate, leaving the
     *                                   session environment in a possibly
     *                                   modified state
     */
    public Entity eval(String expr) throws GleamException
    {
        return eval(new java.io.StringReader(expr));
    }

    /**
     * Evaluates a Gleam Scheme program in the current session environment
     *
     * @param reader a <CODE>java.io.Reader</CODE> representing the program
     *               stream
     *
     * @return the return value of the program
     *
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
     * @param expr the <CODE>gleam.lang.Entity</CODE> corresponding to a Scheme
     *             expression to evaluate
     * @param env  the environment of evaluation
     *
     * @return the value of the expression
     *
     * @throws gleam.lang.GleamException on any error
     */
    public Entity eval(Entity expr, Environment env) throws GleamException
    {
        expr = expr.analyze(env).optimize(env);
        cont.begin(new ExpressionAction(expr, env, null));
        execute();
        return accum;
    }

    /**
     * Clears up the current continuation, e.g., after an error.
     */
    public void clearContinuation()
    {
        this.cont.clear();
    }

    /**
     * Loads and executes a Gleam Scheme program from a stream
     *
     * @param reader a <CODE>gleam.lang.InputPort</CODE> representing the
     *               program stream
     * @param env    the environment for program execution
     *
     * @throws gleam.lang.GleamException as soon as an error condition is
     *                                   raised, the loading / execution
     *                                   operation will terminate, leaving the
     *                                   environment in a possibly modified
     *                                   state
     */
    public void load(InputPort reader, Environment env) throws GleamException
    {
        // read
        Entity obj;
        Entity val;
        while ((obj = reader.read()) != Eof.VALUE) {
            // eval
            logger.log(DEBUG, "load: read object", obj);
            val = eval(obj, env);
            logger.log(DEBUG, "load: result is", val);
        }
    }

    /**
     * Gets the current session environment
     *
     * @return the current session environment
     */
    public Environment getSessionEnv()
    {
        return sessionEnv;
    }

    public void setSessionEnv(Environment sessionEnv)
    {
        sessionEnv.setParent(globalEnv);
        sessionEnv.define(INTERPRETER_SYMBOL, new JavaObject(this));
        this.sessionEnv = sessionEnv;
    }

    /**
     * Gets current-input-port
     */
    public InputPort getCin()
    {
        return cin;
    }

    /**
     * Sets current-input-port
     */
    public void setCin(InputPort newcin)
    {
        cin = newcin;
        sessionEnv.setIn(newcin);
    }

    /**
     * Gets current-output-port
     */
    public OutputPort getCout()
    {
        return cout;
    }

    /**
     * Sets current-output-port
     */
    public void setCout(OutputPort newcout)
    {
        cout = newcout;
        sessionEnv.setOut(newcout);
    }

    /**
     * Imports primitives
     */
    private void importPrimitives(Primitive[] primitives)
    {
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
     *
     * @param env       the environment
     * @param primitive the primitive
     */
    private void installPrimitive(Environment env, Primitive primitive)
    {
        Symbol name = Symbol.makeSymbol(primitive.getName());
        Procedure proc = primitive.keyword
                         ? new SyntaxProcedure(primitive)
                         : new PrimitiveProcedure(primitive);
        env.define(name, proc);

        if (primitive.keyword) {
            kwSet.add(name);
        }

        if (primitive.comment != null) {
            helpComment.put(primitive.getName(), primitive.comment);
        }

        if (primitive.documentation != null) {
            helpDocumentation.put(primitive.getName(), primitive.documentation);
        }
    }

    /**
     * Initialize the three initial environments (null, report, interaction).
     */
    private void initEnvironments()
    {
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
            getSchemeReportEnv().define(Symbol.ERROBJ, Void.VALUE);
            getSchemeReportEnv().define(Symbol.CALL_CC, getSchemeReportEnv().lookup(Symbol.CALL_WITH_CURRENT_CONTINUATION));
            getSchemeReportEnv().define(Symbol.makeSymbol("null"), new JavaObject()); // the Java null value
        } catch (GleamException e) {
            // should never happen
            logger.log(ERROR, () -> String.format("Internal error during environment initialization: %s", e.getMessage()));
        }
    }

    /**
     * binds current I/O ports to system standard I/O
     */
    private void bindIOPorts()
    {
        cin = new InputPort(new java.io.BufferedReader(new java.io.InputStreamReader(java.lang.System.in)));

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
     * The main loop of program execution. When this method is called, the first
     * action in the current continuation is invoked with the current value of
     * the accumulator register as its argument. When a result is produced, it
     * is stored in the accumulator. Then the next action in the continuation
     * chain is extracted, and the loop repeats itself until there are no more
     * actions to execute.
     *
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
}
