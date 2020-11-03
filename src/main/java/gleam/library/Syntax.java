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

package gleam.library;

import gleam.lang.AssignmentAction;
import gleam.lang.Closure;
import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.ExpressionAction;
import gleam.lang.GleamException;
import gleam.lang.IfAction;
import gleam.lang.List;
import gleam.lang.ListIterator;
import gleam.lang.Pair;
import gleam.lang.Symbol;
import gleam.lang.SyntaxRewriter;
import gleam.lang.Undefined;
import gleam.lang.Void;

/**
 * Primitive operator and procedure implementation library.
 */
public final class Syntax {

    /**
     * Can't instantiate this class
     */
    private Syntax() {
    }

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the three initial environments.
     */
    public static final Primitive[] primitives = {

    /**
     * quote
     * Returns its argument without evaluation.
     */
    new Primitive( "quote",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Gives its argument unevaluated, e.g. (quote x); 'x",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont) {
        return arg1;
    }},

    /**
     * define
     * Defines a new binding in the environment.
     */
    new Primitive( "define",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Variable or procedure definition, e.g. (define (inc x) (+ x 1))",
        "Can be used at top-level to create a new global variable, "
        +"e.g. (define x 1); or at the beginning of a procedure body "
        +"to create a new local variable." /* doc strings */ ) {
    @Override
    public Entity applyN(List args, Environment env, Continuation cont) throws GleamException {
        try {
            ListIterator it = new ListIterator(args);
            Entity target = it.next();
            Entity value = it.next();
            /* see if it is a variable definition
             * or a disguised lambda
             * note that target is NOT evaluated
             */
            if (target instanceof Symbol) {
                if (it.hasNext()) {
                    throw new GleamException(this, "too many arguments", args);
                }
                Symbol s = (Symbol) target;
                // create binding
                env.define(s, Undefined.value());
                // equivalent to set!
                cont
                        .begin(new ExpressionAction(value, env))
                        .andThen(new AssignmentAction(s, env));

                return null;
            }
            else if (target instanceof Pair) {
                Entity rtarget = ((List) target).getCar();
                Entity params = ((List) target).getCdr();
                Pair body = new Pair(value, it.rest());
                if (rtarget instanceof Symbol) {
                    Symbol s = (Symbol) rtarget;
                    // create binding
                    env.define(s, Undefined.value());
                    // equivalent to set!
                    cont.begin(new AssignmentAction(s, env));

                    return new Closure(params, body, env);
                }
                else {
                    throw new GleamException(this, "invalid procedure name", rtarget);
                }
            }
            else {
                throw new GleamException(this, "invalid form", args);
            }
        }
        catch (ClassCastException e) {
            throw new GleamException(this, "invalid arguments", args);
        }
    }},

    /**
     * lambda
     * Creates a new procedure.
     */
    new Primitive( "lambda",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Creates a procedure, e.g. (lambda (x) (+ x 1))",
        null /* doc strings */ ) {
    @Override
    public Entity applyN(List args, Environment env, Continuation cont) throws GleamException {
        try {
            Entity lambdaParams = args.getCar();
            Pair lambdaBody = (Pair)args.getCdr();
            return new Closure(lambdaParams, lambdaBody, env);
        }
        catch (ClassCastException e) {
            throw new GleamException(this, "invalid procedure definition", args);
        }
    }},

    /**
     * if
     * Conditional expression.
     */
    new Primitive( "if",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        2, 3, /* min, max no. of arguments */
        "Conditional evaluation, e.g. (if (eqv? 1 0) 'hmm 'ok)",
        null /* doc strings */ ) {
    @Override
    public Entity apply3(Entity test, Entity consequent, Entity alternate, Environment env, Continuation cont) {
        if (alternate == null)
            alternate = Void.value();

        cont
                .begin(new ExpressionAction(test, env))
                .andThen(new IfAction(consequent, alternate, env));

        return null;
    }},

    /**
     * set!
     * Assigns a value to a variable
     */
    new Primitive( "set!",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        2, 2, /* min, max no. of arguments */
        "Variable assignment, e.g. (set! x 11)",
        "The variable must be already bound, e.g. with define" /* doc strings */ ) {
    @Override
    public Entity apply2(Entity arg1, Entity obj, Environment env, Continuation cont) throws GleamException {
        try {
            Symbol s = (Symbol) arg1;
            cont
                    .begin(new ExpressionAction(obj, env, null))
                    .andThen(new AssignmentAction(s, env, null));

            return null;
        }
        catch (ClassCastException e) {
            throw new GleamException(this, "argument is not a symbol", arg1);
        }
    }},

    /**
     * begin
     * Evaluates each argument sequentially from left to right.
     * The result of the last evaluation is returned.
     */
    new Primitive( "begin",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Sequential execution, e.g. (begin (first-step) (second-step))",
        null /* doc strings */ ) {
    @Override
    public Entity applyN(List args, Environment env, Continuation cont) {
        // equivalent to the body of a procedure with no arguments
        cont.addCommandSequenceActions(args, env);
        return null;
    }},

    /**
     * case
     * @todo implementation
     */
    new Primitive( "case",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * do
     * @todo implementation
     */
    new Primitive( "do",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * delay
     * @todo implementation
     */
    new Primitive( "delay",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * quasiquote
     * @todo implementation
     */
    new Primitive( "quasiquote",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Gives its argument almost unevaluated, e.g. (quasiquote x); `x",
        "If a comma appears within the argument, the expression following the "
        +"comma is evaluated (\"unquoted\") and its result is inserted into "
        +"the structure instead of the comma and the expression. If a comma "
        +"appears followed immediately by an at-sign (@), then the following "
        +"expression must evaluate to a list; the opening and closing "
        +"parentheses of the list are then \"stripped away\" and the elements "
        +"of the list are inserted in place of the comma at-sign expression "
        +"sequence. (unquote x) is equivalent to ,x and (unquote-splicing x) "
        +"is equivalent to ,@x." /* doc strings */ ) {
    },

    /**
     * else
     * @todo implementation
     */
    new Primitive( "else",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * =>
     * @todo implementation
     */
    new Primitive( "=>",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * let
     * @todo implementation
     */
    new Primitive( "let",
            Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
            1, Primitive.VAR_ARGS, /* min, max no. of arguments */
            null, null /* doc strings */ ) {
    },

    /**
     * let*
     * @todo implementation
     */
    new Primitive( "let*",
            Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
            1, Primitive.VAR_ARGS, /* min, max no. of arguments */
            null, null /* doc strings */ ) {
    },

    /**
     * cond
     * @todo implementation
     */
    new Primitive( "cond",
            Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
            1, Primitive.VAR_ARGS, /* min, max no. of arguments */
            null, null /* doc strings */ ) {
    },

    /**
     * unquote
     * @todo implementation
     */
    new Primitive( "unquote",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        1, 1, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * unquote-splicing
     * @todo implementation
     */
    new Primitive( "unquote-splicing",
        Primitive.NULL_ENV, Primitive.KEYWORD, /* environment, type */
        1, 1, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    },

    /**
     * make-rewriter
     */
    new Primitive( "make-rewriter",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Makes a syntax rewriter, e.g. (make-rewriter (lambda (exp) ...))",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity obj, Environment env, Continuation cont) throws GleamException {
        // evaluate argument: must be a function of exactly one argument
        // obj = obj.eval(env, cont);
        if (!(obj instanceof Closure)) {
            throw new GleamException(this, "argument must be a function of one argument", obj);
        }
        else {
            Closure closure = (Closure) obj;
            // TODO: check closure arity == 1 // FIXME ?
            return new SyntaxRewriter(closure);
        }
    }},

    /**
     * rewrite1
     * @todo implementation
     */
    new Primitive( "rewrite1",
        Primitive.INTR_ENV, Primitive.KEYWORD, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Rewrites an expression applying a syntax rewriter at most once",
        null /* doc strings */ ) {
    },

    }; // primitives
}
