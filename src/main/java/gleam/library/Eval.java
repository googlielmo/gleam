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

package gleam.library;

import gleam.lang.Boolean;
import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.ExpressionAction;
import gleam.lang.ExpressionInEnvironmentAction;
import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.Number;

import static gleam.lang.Environment.Kind.INTERACTION_ENV;
import static gleam.lang.Environment.Kind.REPORT_ENV;

/**
 * EVAL
 * <p>
 * Primitive operator and procedure implementation library.
 */
public final class Eval
{

    /**
     * This array contains definitions of primitives. It is used by static initializers in
     * gleam.lang.System to populate the initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * eval
             * Evaluates an expression in an environment
             */
            new Primitive("eval",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          2, /* min, max no. of arguments */
                          "Evaluates an expression in a given environment",
                          "E.g. (eval '(+ 1 2) (interaction-environment)) => 3" /* doc strings */)
            {
                @Override
                public Entity apply2(Entity arg1,
                                     Entity arg2,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    Environment eval_env;
                    if (arg2 == null) {
                        eval_env = env;
                    }
                    else {
                        try {
                            eval_env = (Environment) arg2;
                        }
                        catch (ClassCastException e) {
                            throw new GleamException(this,
                                                     "not an environment",
                                                     arg2);
                        }
                    }
                    arg1 = arg1.analyze(env).optimize(eval_env);
                    cont.begin(new ExpressionAction(arg1, eval_env, null));
                    return null;
                }
            },

            /*
             * null-environment
             * Returns the null environment
             */
            new Primitive("null-environment",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns the null environment",
                          "A scheme-report version number must be specified, e.g. (null-environment 5). " + "Currently supported versions are 4 and 5" /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    Number version;
                    try {
                        version = (Number) arg1;
                        if (version.doubleValue() == 4.0 || version.doubleValue() == 5.0) {
                            return Interpreter.getNullEnv();
                        }
                        else {
                            throw new GleamException(this,
                                                     "version not supported",
                                                     version);
                        }
                    }
                    catch (ClassCastException e) {
                        throw new GleamException(this,
                                                 "not a version number",
                                                 arg1);
                    }
                }
            },

            /*
             * scheme-report-environment
             * Returns the scheme-report environment
             */
            new Primitive("scheme-report-environment",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns the scheme-report environment",
                          "A scheme-report version number must be specified, e.g. (scheme-report-environment 5). " + "Currently supported versions are 4 and 5" /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    Number version;
                    try {
                        version = (Number) arg1;
                        if (version.doubleValue() == 4.0 || version.doubleValue() == 5.0) {
                            return Interpreter.getSchemeReportEnv();
                        }
                        else {
                            throw new GleamException(this,
                                                     "version not supported",
                                                     version);
                        }
                    }
                    catch (ClassCastException e) {
                        throw new GleamException(this,
                                                 "not a version number",
                                                 arg1);
                    }
                }
            },

            /*
             * interaction-environment
             * Returns the interaction environment
             */
            new Primitive("interaction-environment",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          0,
                          0, /* min, max no. of arguments */
                          "Returns the interaction (top-level) environment",
                          null /* doc strings */)
            {
                @Override
                public Entity apply0(Environment env, Continuation cont)
                {
                    return Interpreter.getInteractionEnv();
                }
            },

            /*
             * current-environment
             * Returns the current environment
             */
            new Primitive("current-environment",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          0,
                          0, /* min, max no. of arguments */
                          "Returns the current environment",
                          null /* doc strings */)
            {
                @Override
                public Entity apply0(Environment env, Continuation cont)
                {
                    return env;
                }
            },

            /*
             * in-environment
             * Returns the current environment
             */
            new Primitive("in-environment",
                          INTERACTION_ENV,
                          Primitive.KEYWORD, /* environment, type */
                          2,
                          2, /* min, max no. of arguments */
                          "Evaluates an expression in a given environment",
                          "E.g. (in-environment (scheme-report-environment 5) (+ 1 2)) => 3" /* doc strings */)
            {
                @Override
                public Entity apply2(Entity argEnv,
                                     Entity argExpr,
                                     Environment env,
                                     Continuation cont)
                {
                    cont.begin(new ExpressionAction(argEnv,
                                                    env))         // 1) evaluate environment expr
                        .andThen(new ExpressionInEnvironmentAction(argExpr,
                                                                   env)); // 2) evaluate expr in that env

                    return null;
                }
            },

            /*
             * make-environment
             * Returns a new environment
             */
            new Primitive("make-environment",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          0,
                          0, /* min, max no. of arguments */
                          "Creates a new environment",
                          "E.g. (in-environment (make-environment) (begin (define a 7) a)) => 7" /* doc strings */)
            {
                @Override
                public Entity apply0(Environment env, Continuation cont)
                {
                    return new Environment(env);
                }
            },

            /*
             * environment?
             * Returns a new environment
             */
            new Primitive("environment?",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns true if argument is an environment, false otherwise",
                          "E.g. (environment? (scheme-report-environment 5)) => #t" /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg,
                                     Environment env,
                                     Continuation cont)
                {
                    return Boolean.makeBoolean(arg instanceof Environment);
                }
            }

    }; // primitives

    /** Can't instantiate this class. */
    private Eval() {}
}
