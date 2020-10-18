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

package gleam.lang;

import java.util.Iterator;

import static gleam.util.Log.Level.INFO;

/**
 * The Scheme pair, also known as <i>cons</i>.
 * When used as data, the pair is equivalent to a <i>tree</i> data structure.
 * Most often, it is used as a degenerate tree to implement a <i>list</i>
 * data structure.
 * If evaluated as code, the list represents the procedure application.
 */
public class Pair extends AbstractEntity implements List {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Entity car;
    private Entity cdr;
    boolean analyzed;

    public Pair(Entity head, Entity tail)
    {
        this.car = head;
        this.cdr = tail;
        this.analyzed = false;
    }

    @Override
    public Entity getCar() {
        return car;
    }

    @Override
    public Entity getCdr() {
        return cdr;
    }

    @Override
    public void setCar(Entity obj) {
        car = obj;
    }

    @Override
    public void setCdr(Entity obj) {
        cdr = obj;
    }

    /**
     * Performs syntax analysis on this pair.
     */
    @Override
    public Entity analyze()
        throws GleamException
    {
        if (!analyzed) {
            if (getCar() instanceof Symbol && System.isKeyword((Symbol) getCar())) {
                /* we have a special form, so let's
                 * perform syntax analysis
                 * -- may change car, cdr
                 */
                System.analyzeSpecialForm(this);
                analyzed = true;
                return this;
            }

            /* we have a procedure application
             * first car, then cdr
             */
            setCar(getCar().analyze());

            /* now process rest of the list (i.e. the form arguments)
             *
             * we can't simply do: cdr = cdr.analyze()
             * but we must traverse the cdr list ourselves at this
             * level; otherwise (f1 f2 f3) would be analyzed as
             * (f1 (f2 (f3)), which is wrong.
             */
            Entity rest = getCdr();
            List restParent = this;
            while (rest != EmptyList.value) {
                if (rest instanceof List) {
                    // this is a proper list
                    List restAsPair = (List) rest;
                    restParent = restAsPair;
                    restAsPair.setCar(restAsPair.getCar().analyze());
                    rest = restAsPair.getCdr();
                } else {
                    /* this is an improper list
                     * (not necessarily an error: think lambda)
                     *
                     * analyze cdr in place
                     */
                    gleam.util.Log.enter(INFO, "dotted pair in analyze... check for correctness");
                    restParent.setCdr(rest.analyze());
                    break;
                }
            }
            analyzed = true;
        }
        return this;
    }

    /**
     * Evaluates the procedure call this pair stands for.
     */
    @Override
    public Entity eval(Environment env, Continuation cont)
        throws GleamException
    {
        ListIterator it = new ListIterator(this);
        // operator
        Entity operator = it.next();

        /* check for special forms or syntax rewriters */
        if (operator instanceof Symbol) {
            Entity e = env.lookup( (Symbol) operator);
            if (e instanceof SyntaxRewriter) {
                // call of syntax rewriter, will be followed by evaluation of resulting expression
                rewriteAndEval((SyntaxRewriter) e, new ArgumentList(), env, cont);
                return null;
            }
            else if (e instanceof SyntaxProcedure) {
                // special procedure call
                // don't evaluate arguments at all!
                cont
                        .begin(new ExpressionAction(operator, env))
                        .andThen(new ProcedureCallAction(new ArgumentList((List) this.getCdr()), env));

                return null;
            }
        }
        else if (operator instanceof Location) {
            Entity e = ( (Location) operator).get();
            if (e instanceof SyntaxRewriter) {
                // call of syntax rewriter, will be followed by evaluation of resulting expression
                rewriteAndEval((SyntaxRewriter) e, new ArgumentList(), env, cont);
                return null;
            }
        }

        /* we have a regular procedure call */
        ArgumentList argList = new ArgumentList();
        Action action = cont.beginSequence();

        // evaluate each argument
        int argidx = 0;
        while (it.hasNext()) {
            Entity nextArg = it.next();
            action = action
                    .andThen(new ExpressionAction(nextArg, env))
                    .andThen(new ObtainArgumentAction(argList, argidx++));
        }
        argList.ensureSize(argidx);
        action
                .andThen(new ExpressionAction(operator, env))
                .andThen(new ProcedureCallAction(argList, env));

        cont.endSequence();
        return null;
    }

    private void rewriteAndEval(SyntaxRewriter syntaxRewriter, ArgumentList args, Environment env, Continuation cont) {
        cont
                .begin(new ExpressionAction(syntaxRewriter, env))
                .andThen(new ProcedureCallAction(args, env))
                .andThen(new EvalAction(env));
        // pass this pair, not evaluated
        args.set(0, this);
    }

    /**
     * Performs environment optimization on this pair.
     */
    @Override
    public Entity optimize(Environment env)
        throws GleamException
    {
        /* first check for special forms */
        if (getCar() instanceof Symbol && System.isKeyword( (Symbol) getCar())) {
            // we have a special form, so let's perform
            // specific optimization
            // -- may change retVal.{car|cdr}
//          System.optimizeSpecialForm(retVal, env);
//          return retVal;
            return this;
        }

        /* if the operator is a syntax rewriter, we must not optimize */
        if ((getCar() instanceof SyntaxRewriter) ||
            (getCar() instanceof Symbol && env.lookup( (Symbol) getCar()) instanceof SyntaxRewriter)) {
            return this;
        }

        /* if the operator is itself an application, then it could
         * potentially result in a syntax rewriter, so no optimization
         * can be performed at this stage
         */
        if (getCar() instanceof Pair) {
            return this;
        }

        /* we must not modify in place, since this pair must remain
         * a valid data structure after optimization (think eval)
         */
        Pair retVal = new Pair(getCar(), getCdr());

        /* so we have a simple procedure application:
         * first optimize car, then cdr
         */
        retVal.setCar(retVal.getCar().optimize(env));

        /* we can't simply do: retVal.cdr = retVal.cdr.optimize(env)
         * but we must traverse the cdr list ourselves at this level,
         * otherwise (f1 f2 f3) would be optimized as (f1 (f2 (f3)),
         * which is wrong.
         *
         * this also means that we must allocate new pairs for tail
         * here.
         */
        Entity rest = retVal.getCdr();
        List restParent = retVal;
        while (rest != EmptyList.value) {
            if (rest instanceof List) {
                // this is a proper list
                Pair restAsPair = (Pair) rest;
                rest = new Pair(restAsPair.getCar(), restAsPair.getCdr());
                restParent.setCdr(rest);
                restParent = restAsPair;
                restAsPair.setCar(restAsPair.getCar().optimize(env));
                rest = restAsPair.getCdr();
            }
            else {
                /* this is an improper list
                 * (not necessarily an error: e.g., lambda)
                 */
                gleam.util.Log.enter(INFO, "dotted pair in optimize... check for correctness");
                restParent.setCdr(rest.optimize(env));
                break;
            }
        }
        return retVal;
    }

    /**
     * Writes this pair.
     */
    @Override
    public void write(java.io.PrintWriter out) {
        if (getCar() == Symbol.QUOTE
                && !(getCdr() instanceof EmptyList)
                && getCdr() instanceof Pair
                && ((Pair) getCdr()).getCdr() instanceof EmptyList) {
            out.print("'");
            ((Pair) getCdr()).getCar().write(out);
        }
        else if (getCar() == Symbol.QUASIQUOTE
                && !(getCdr() instanceof EmptyList)
                && getCdr() instanceof Pair
                && ((Pair) getCdr()).getCdr() instanceof EmptyList) {
            out.print("`");
            ((Pair) getCdr()).getCar().write(out);
        }
        else if (getCar() == Symbol.UNQUOTE
                && !(getCdr() instanceof EmptyList)
                && getCdr() instanceof Pair
                && ((Pair) getCdr()).getCdr() instanceof EmptyList) {
            out.print(",");
            ((Pair) getCdr()).getCar().write(out);
        }
        else if (getCar() == Symbol.UNQUOTE_SPLICING
                && !(getCdr() instanceof EmptyList)
                && getCdr() instanceof Pair
                && ((Pair) getCdr()).getCdr() instanceof EmptyList) {
            out.print(",@");
            ((Pair) getCdr()).getCar().write(out);
        }
        else {
            Pair current = this;
            out.print("(");
            getCar().write(out);
            while (current.getCdr() instanceof Pair && !(current.getCdr() instanceof EmptyList)) {
                current = (Pair) current.getCdr();
                out.print(" ");
                if ((current.getCar() == null))
                    out.print("ERROR");
                else
                    current.getCar().write(out);
            }
            if (!(current.getCdr() instanceof EmptyList)) {
                out.print(" . ");
                current.getCdr().write(out);
            }
            out.print(")");
        }
    }

    /**
     * Returns an iterator over elements of this List.
     *
     * @return an Entity iterator.
     */
    @Override
    public Iterator<Entity> iterator() {
        return new ListIterator(this);
    }
}
