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

package gleam.lang;

import gleam.util.Logger;

import java.util.Collection;
import java.util.HashSet;

/**
 * Scheme runtime support.
 * <p>
 * Creation date: 03/11/2001
 */
public final class System
{
    private static final Logger logger = Logger.getLogger();

    /** Can't instantiate this class. */
    private System() {}

    /**
     * Checks if a symbol stands for the name of a special form in a given environment.
     *
     * @param symbol the Symbol to check
     * @param env    the Environment
     *
     * @return true is symbol represents a special form
     */
    public static boolean isSpecialForm(Symbol symbol, Environment env)
    {
        Location location = env.getLocationOrNull(symbol);
        boolean isSyntaxProcedure = false;
        boolean isSyntaxRewriter = false;
        if (location != null) {
            Entity proc = location.get();
            isSyntaxProcedure = proc instanceof SyntaxProcedure;
            isSyntaxRewriter = proc instanceof SyntaxRewriter;
        }
        boolean isKeyword = Interpreter.isKeyword(symbol);

        return isKeyword && (isSyntaxProcedure || isSyntaxRewriter);
    }

    /**
     * Performs syntactic analysis of special forms.
     * <p>
     * Creation date: (02/11/2001 12.34.35)
     */
    public static void analyzeSpecialForm(List form,
                                          Environment env) throws GleamException
    {
        ListIterator it = new ListIterator(form);
        if (!it.hasNext()) {
            throw new GleamException("invalid special form", form);
        }
        // analyze operator itself
        Entity op = it.next();
        it.replace(op.analyze(env));

        // Variable for form arguments
        Entity arg;

        // These may take no arguments
        if (op == Symbol.AND || op == Symbol.OR || op == Symbol.HELP) {
            // analyze arguments
            while (it.hasNext()) {
                it.replace(it.next().analyze(env));
            }
            return;
        }

        // Other special forms have at least an argument, so check for it
        if (!it.hasNext()) {
            throw new GleamException(String.format(
                    "invalid special form %s: too few arguments",
                    op), form);
        }
        arg = it.next();

        if (op == Symbol.QUOTE || op == Symbol.QUASIQUOTE) {
            // just one datum argument with no syntax analysis, of course!
            if (it.hasNext()) {
                throw new GleamException("quote: too many arguments", form);
            }
        }
        else if (op == Symbol.LAMBDA) {
            // analyze param list
            if (arg == EmptyList.VALUE || isVariable(arg)) {
                // ok
                it.replace(arg.analyze(env));
            }
            else if (arg instanceof List) {
                // iterate over (possibly improper) list
                ListIterator ait = new ListIterator((List) arg, true);
                Collection<Symbol> paramSet = new HashSet<>();
                while (ait.hasNext()) {
                    Entity pobj = ait.next();
                    if (!isVariable(pobj)) {
                        throw new GleamException(
                                "lambda: procedure parameter is not a variable",
                                form);
                    }
                    Symbol param = (Symbol) pobj;
                    if (paramSet.contains(param)) {
                        throw new GleamException(
                                "lambda: repeated procedure parameter",
                                form);
                    }
                    paramSet.add(param);
                    ait.replace(pobj.analyze(env));
                }
            }
            else {
                throw new GleamException(
                        "lambda: parameter is not a variable nor a variable list",
                        form);
            }
            // analyze body
            if (!it.hasNext()) {
                throw new GleamException("lambda: missing procedure body",
                                         form);
            }
            while (it.hasNext()) {
                Entity bodyPart = it.next();
                it.replace(bodyPart.analyze(env));
            }
        }
        else if (op == Symbol.IF) {
            // analyze condition
            it.replace(arg.analyze(env));
            if (!it.hasNext()) {
                throw new GleamException("if: missing consequence", form);
            }
            // analyze consequence
            arg = it.next();
            it.replace(arg.analyze(env));
            if (it.hasNext()) {
                // analyze alternative
                arg = it.next();
                it.replace(arg.analyze(env));
                if (it.hasNext()) {
                    throw new GleamException("if: too many arguments", form);
                }
            }
        }
        else if (op == Symbol.SET) {
            if (!isVariable(arg)) {
                throw new GleamException(
                        "set!: assignment object is not a variable",
                        form);
            }
            if (!it.hasNext()) {
                throw new GleamException("set!: missing assignment value",
                                         form);
            }
            it.replace(arg.analyze(env));
            // analyze assigned value
            arg = it.next();
            it.replace(arg.analyze(env));
            if (it.hasNext()) {
                throw new GleamException("set!: too many assignment values",
                                         form);
            }
        }
        else if (op == Symbol.BEGIN) {
            // begin is followed by one or more expressions
            it.replace(arg.analyze(env));
            while (it.hasNext()) {
                it.replace(it.next().analyze(env));
            }
        }
        else if (op == Symbol.DEFINE) {
            // analyze variable or function
            boolean isFunction;
            if (arg == EmptyList.VALUE) {
                throw new GleamException("define: invalid function name", form);
            }
            else if (isVariable(arg)) {
                isFunction = false;
                it.replace(arg.analyze(env));
            }
            else if (arg instanceof List) {
                isFunction = true;

                // take out function name
                boolean fname = true;

                // iterate over (possibly improper) list
                ListIterator ait = new ListIterator((List) arg, true);
                Collection<Symbol> paramSet = new HashSet<>();

                while (ait.hasNext()) {
                    Entity pobj = ait.next();
                    if (!isVariable(pobj)) {
                        if (fname) {
                            throw new GleamException(
                                    "define: procedure name is not a variable",
                                    form);
                        }
                        else {
                            throw new GleamException(
                                    "define: procedure parameter is not a variable",
                                    form);
                        }
                    }
                    Symbol param = (Symbol) pobj;
                    if (paramSet.contains(param)) {
                        throw new GleamException(
                                "define: repeated procedure parameter",
                                form);
                    }
                    if (!fname) {
                        paramSet.add(param);
                    }
                    else {
                        fname = false;
                    }
                    ait.replace(pobj.analyze(env));
                }
            }
            else {
                throw new GleamException(
                        "define: definition object is not a variable nor a procedure",
                        form);
            }
            // analyze value or procedure body
            if (!it.hasNext()) {
                throw new GleamException("define: missing definition value",
                                         form);
            }
            Entity v = it.next();
            it.replace(v.analyze(env));
            if (it.hasNext() && !isFunction) {
                throw new GleamException("define: too many definition values",
                                         form);
            }
            else {
                while (it.hasNext()) {
                    Entity bodyPart = it.next();
                    it.replace(bodyPart.analyze(env));
                }
            }
        }
        else {
            logger.debug("analyzeSpecialForm: unknown: ", op);
        }
    }

    /**
     * Determines if a given object is a variable. An object is a variable iff it is a symbol.
     */
    static boolean isVariable(Entity s)
    {
        return s instanceof Symbol;
    }

    /**
     * Deep clones a list
     *
     * @param list List
     *
     * @return List
     */
    private static List cloneList(List list) throws GleamException
    {

        if (list == EmptyList.VALUE) {
            return list;
        }

        return new Pair(clone(list.getCar()), clone(list.getCdr()));
    }

    /**
     * Deep clones an entity. Pairs are cloned as new Pairs, every other value is unchanged in the
     * clone.
     *
     * @param entity an Entity
     *
     * @return the cloned entity
     */
    private static Entity clone(Entity entity) throws GleamException
    {

        if (entity instanceof List) {
            return cloneList((List) entity);
        }

        return entity;
    }
}
