/*
 * Copyright (c) 2007-2023 Guglielmo Nigri.  All Rights Reserved.
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
 * Created on January 6, 2007, 11.05
 */

package gleam.lang;

import gleam.library.Primitive;
import gleam.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static gleam.lang.Environment.Kind.INTERACTION_ENV;
import static gleam.lang.Environment.Kind.REPORT_ENV;
import static gleam.lang.JavaObject.makeJavaObject;
import static gleam.util.Logger.Level.DEBUG;
import static gleam.util.Logger.Level.ERROR;
import static java.lang.String.format;

/**
 * The Gleam Scheme Interpreter
 */
public class Interpreter
{

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
    private static final Environment
            nullEnv = new SystemEnvironment();

    /**
     * the scheme-report environment, as defined in r5rs
     */
    private static final Environment
            reportEnv = new SystemEnvironment(nullEnv, REPORT_ENV);

    /**
     * the interaction environment, as defined in r5rs
     */
    private static final Environment
            interactionEnv = new SystemEnvironment(reportEnv, INTERACTION_ENV);

    /**
     * true if bootstrap code already loaded
     */
    private static volatile boolean bootstrapped = false;

    /**
     * the program continuation
     */
    private final Continuation cont = new Continuation();

    /**
     * wrapper for the shared environment
     */
    private final Environment sharedEnv = new Environment(interactionEnv);

    /**
     * the session (top-level) environment; the environment typically used by the application.
     */
    private Environment sessionEnv = new Environment(sharedEnv);

    /**
     * the accumulator register
     */
    private Entity accum = Void.VALUE;

    /**
     * Only used internally by {@link #newInterpreter()}
     */
    private Interpreter() {}

    /**
     * Creates and bootstraps a new Interpreter.
     *
     * @return a Gleam Scheme Interpreter
     *
     * @throws GleamException in case of errors
     */
    public static Interpreter newInterpreter() throws GleamException
    {
        try {
            Interpreter interpreter = new Interpreter();
            logger.log(Logger.Level.DEBUG, () -> format("created Interpreter %s", interpreter));
            Interpreter.bootstrap(interpreter);
            return interpreter;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Gets the comment string for a procedure.
     */
    public static String getHelpComment(String name)
    {
        return helpComment.get(name);
    }

    /**
     * Gets the comment string for a procedure.
     */
    public static String getHelpDocumentation(String name)
    {
        return helpDocumentation.get(name);
    }

    /**
     * Gets the set of help-enabled procedures.
     */
    public static Set<String> getHelpNames()
    {
        return new TreeSet<>(helpDocumentation.keySet());
    }

    /**
     * Loads and executes the bootstrap code for the Gleam Scheme Interpreter.
     *
     * @throws gleam.lang.GleamException in case of errors
     */
    private static synchronized void bootstrap(Interpreter interpreter)
            throws GleamException
    {
        if (bootstrapped) {
            return;
        }
        initEnvironments();
        try (InputStream inputStream =
                     Objects.requireNonNull(
                             interpreter.getClass()
                                        .getResourceAsStream(
                                                "/bootstrap.scm"))) {
            gleam.lang.InputPort bootstrap =
                    new gleam.lang.InputPort(
                            new java.io.BufferedReader(
                                    new java.io.InputStreamReader(inputStream)));
            interpreter.load(bootstrap, getSchemeReportEnv());
            bootstrapped = true;
        }
        catch (IOException e) {
            logger.severe("Gleam cannot bootstrap", e);
            throw new GleamException("Gleam cannot bootstrap", e);
        }
        logger.config("Gleam bootstrapped");
    }

    /**
     * Initialize the system environments (null, report, interaction).
     */
    private static void initEnvironments()
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
            getSchemeReportEnv().define(Symbol.makeSymbol("null"), makeJavaObject(null));
            getSchemeReportEnv().define(Symbol.CALL_CC,
                                        getSchemeReportEnv()
                                                .lookup(Symbol.CALL_WITH_CURRENT_CONTINUATION));
        }
        catch (GleamException e) {
            // should never happen
            logger.log(ERROR,
                       () -> format(
                               "internal error in environment initialization: %s",
                               e.getMessage()));
        }
    }

    /**
     * Imports primitives
     */
    private static void importPrimitives(Primitive[] primitives)
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
    private static void installPrimitive(Environment env, Primitive primitive)
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
     * Loads and executes a Gleam Scheme program from a stream
     *
     * @param reader a <CODE>gleam.lang.InputPort</CODE> representing the program stream
     * @param env    the environment for program execution
     *
     * @throws gleam.lang.GleamException as soon as an error condition is raised, the
     *                                   loading/execution operation will terminate, leaving the
     *                                   environment in a possibly modified state
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

    /**
     * Sets the global shared environment
     *
     * @param env the environment to use
     */
    public void setGlobalEnv(Environment env)
    {
        env.parent = interactionEnv;
        sharedEnv.parent = env;
    }

    /**
     * Gets the current session environment.
     *
     * @return the current session environment
     */
    public Environment getSessionEnv()
    {
        return sessionEnv;
    }

    /**
     * Sets the current session environment
     *
     * @param env the environment to use
     */
    public void setSessionEnv(Environment env)
    {
        env.setParent(sharedEnv);
        this.sessionEnv = env;
    }

    /**
     * Evaluates a Gleam Scheme entity as code in a given environment.
     *
     * @param expr the <CODE>gleam.lang.Entity</CODE> corresponding to a Scheme expression to
     *             evaluate
     * @param env  the environment of evaluation
     *
     * @return the value of the expression
     *
     * @throws gleam.lang.GleamException as soon as an error condition is raised, the
     *                                   loading/execution operation will terminate, leaving the
     *                                   session environment in a possibly modified state
     */
    public Entity eval(Entity expr, Environment env) throws GleamException
    {
        expr = expr.analyze(env).optimize(env);
        cont.beginWith(new ExpressionAction(expr, env));
        execute();
        ExecutionContext context = env.getExecutionContext();
        if (context.isNoisy()) {
            context.getOut().printf("%s\n", accum);
        }
        return accum;
    }

    /**
     * Evaluates a Gleam Scheme entity in the current session environment.
     *
     * @param expr the <CODE>gleam.lang.Entity</CODE> corresponding to a Scheme expression to
     *             evaluate
     *
     * @return the value of the expression
     *
     * @throws gleam.lang.GleamException as soon as an error condition is raised, the
     *                                   loading/execution operation will terminate, leaving the
     *                                   session environment in a possibly modified state
     */
    public Entity eval(Entity expr) throws GleamException
    {
        return eval(expr, getSessionEnv());
    }

    /**
     * Evaluates a Gleam Scheme expression in the current session environment
     *
     * @param expr a String holding an arbitrary Scheme expression
     *
     * @return the value of the expression
     *
     * @throws gleam.lang.GleamException as soon as an error condition is raised, the
     *                                   loading/execution operation will terminate, leaving the
     *                                   session environment in a possibly modified state
     */
    public Entity eval(String expr) throws GleamException
    {
        return eval(new java.io.StringReader(expr));
    }

    /**
     * Evaluates a Gleam Scheme program in the current session environment
     *
     * @param reader a <CODE>java.io.Reader</CODE> representing the program stream
     *
     * @return the return value of the program
     *
     * @throws gleam.lang.GleamException as soon as an error condition is raised, the
     *                                   loading/execution operation will terminate, leaving the
     *                                   session environment in a possibly modified state
     */
    public Entity eval(java.io.Reader reader) throws GleamException
    {
        load(new InputPort(reader), getSessionEnv());
        return accum;
    }

    /**
     * The main loop of program execution. When this method is called, the first action in the
     * current continuation is invoked with the current value of the accumulator register as its
     * argument. When a result is produced, it is stored in the accumulator. Then, the next action
     * in the continuation chain is extracted, and the loop repeats itself until there are no more
     * actions to execute.
     *
     * @throws gleam.lang.GleamException in case of errors
     */
    private void execute() throws GleamException
    {
        Action currentAction = cont.head;
        Entity tmp;
        while (currentAction != null) {
            cont.head = currentAction.next;
            tmp = currentAction.invoke(accum, cont);
            if (tmp != null) {
                accum = tmp;
            }
            currentAction = cont.head;
        }
    }

    /**
     * Clears up the current continuation, e.g., after an error.
     */
    public void clearContinuation()
    {
        this.cont.clear();
    }

    /**
     * Checks whether a given symbol is a keyword.
     */
    static boolean isKeyword(Symbol s)
    {
        return kwSet.contains(s);
    }
}
