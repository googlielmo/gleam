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

import static gleam.util.Log.Level.INFO;

/**
 * The Scheme pair, also known as <i>cons</i>.
 * When used as data, the pair is equivalent to a <i>tree</i> data structure.
 * Most often it is used as a degenerate tree to give a <i>list</i>
 * data structure. If evaluated as code, the list is the procedure application.
 */
public class Pair extends Entity
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    Entity car, cdr;
    boolean analyzed;

    public Pair(Entity head, Entity tail)
    {
        car = head;
        cdr = tail;
        analyzed = false;
    }

    public Entity getCar() throws GleamException {
        return car;
    }

    public Entity getCdr() throws GleamException {
        return cdr;
    }

    public void setCar(Entity obj) throws GleamException {
        car = obj;
    }

    public void setCdr(Entity obj) throws GleamException {
        cdr = obj;
    }

    /**
     * Performs syntax analysis on this pair.
     */
    public Entity analyze()
        throws GleamException
    {
        if (analyzed) {
            return this;
        }
        else {
            if (car instanceof Symbol && System.isKeyword((Symbol)car)) {
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
            car = car.analyze();

            /* now process rest of the list (i.e. the form arguments)
             *
             * we can't simply do: cdr = cdr.analyze()
             * but we must traverse the cdr list ourselves at this
             * level; otherwise (f1 f2 f3) would be analyzed as
             * (f1 (f2 (f3)), which is wrong.
             */
            Entity rest = cdr;
            Pair restParent = this;
            while (rest != EmptyList.value) {
                if (rest instanceof Pair) {
                    // this is a proper list
                    Pair restAsPair = (Pair)rest;
                    restParent = restAsPair;
                    restAsPair.car =
                        restAsPair.car.analyze();
                    rest = restAsPair.cdr;
                }
                else {
                    /* this is an improper list
                     * (not necessarily an error: think lambda)
                     *
                     * analyze cdr in place
                     */
                    gleam.util.Log.record(INFO, "dotted pair in analyze... check for correctness");
                    restParent.cdr = rest.analyze();
                    break;
                }
            }
            analyzed = true;
            return this;
        }
    }

    /**
     * Evaluates the procedure call this pair stands for.
     */
    public Entity eval(Environment env, Continuation cont)
        throws GleamException
    {
        ListIterator it = new ListIterator(this);
        // operator
        Entity operator = it.next();
        ArgumentList arglist = new ArgumentList();

        /* check for special forms or syntax rewriters */
        if (operator instanceof Symbol) {
            Entity e = env.lookup( (Symbol) operator);
            if (e instanceof SyntaxRewriter) {
                // call of syntax rewriter followed by evaluation of resulting expression
                Action a = new EvalAction(env, cont.action);
                a = new ProcedureCallAction(arglist, env, a);
                a = new ExpressionAction((SyntaxRewriter) e, env, a);
                cont.action = a;
                // don't evaluate arguments at all!
                // gleam.util.Log.record(2, "THIS: ", this); // DEBUG
                arglist.put(this, 0);
                return null;
            }
            else if (System.isKeyword( (Symbol) operator)) {
                // special procedure call
                Action a = new ProcedureCallAction(arglist, env, cont.action);
                a = new ExpressionAction(operator, env, a);
                cont.action = a;
                // don't evaluate arguments at all!
                arglist.setArguments((Pair)this.cdr);
                return null;
            }
        }
        else if (operator instanceof Location) { // TODO FIXME THIS IS UGLY
            Entity e = ( (Location) operator).get();
            if (e instanceof SyntaxRewriter) {
                // call of syntax rewriter followed by evaluation of resulting expression
                Action a = new EvalAction(env, cont.action);
                a = new ProcedureCallAction(arglist, env, a);
                a = new ExpressionAction((SyntaxRewriter) e, env, a);
                cont.action = a;
                // don't evaluate arguments at all!
                arglist.put(this, 0);
                return null;
            }
        }
        
        /* ok, it's a standard procedure call */
        Action a = new ProcedureCallAction(arglist, env, cont.action);
        a = new ExpressionAction(operator, env, a);
        // evaluate each argument
        int argidx = 0;
        while (it.hasNext()) {
            Entity nextArg = it.next();
            a = new ObtainArgumentAction(arglist, argidx++, a);
            a = new ExpressionAction(nextArg, env, a);
        }
        arglist.ensureSize(argidx);
        cont.action = a;
        return null;
    }

    /**
     * Performs environment optimization on this pair.
     */
    public Entity optimize(Environment env)
        throws GleamException
    {
        /* first check for special forms */
        if (car instanceof Symbol && System.isKeyword( (Symbol) car)) {
            // we have a special form, so let's perform
            // specific optimization
            // -- may change retVal.{car|cdr}
//          System.optimizeSpecialForm(retVal, env);
//          return retVal;
            return this;
        }

        /* if the operator is a syntax rewriter, we must not optimize */
        if ((car instanceof SyntaxRewriter) || 
            (car instanceof Symbol && env.lookup( (Symbol) car) instanceof SyntaxRewriter)) {
            return this;
        }

        /* if the operator is itself an application, then it could
         * potentially result in a syntax rewriter, so no optimization
         * can be performed at this stage
         */
        if (car instanceof Pair) {
            return this;
        }

        /* we must not modify in place, since this pair must remain
         * a valid data structure after optimization (think eval)
         */
        Pair retVal = new Pair(car, cdr);

        /* so we have a simple procedure application:
         * first optimize car, then cdr
         */
        retVal.car = retVal.car.optimize(env);

        /* we can't simply do: retVal.cdr = retVal.cdr.optimize(env)
         * but we must traverse the cdr list ourselves at this level,
         * otherwise (f1 f2 f3) would be optimized as (f1 (f2 (f3)),
         * which is wrong.
         *
         * this also means that we must allocate new pairs for tail
         * here.
         */
        Entity rest = retVal.cdr;
        Pair restParent = retVal;
        while (rest != EmptyList.value) {
            if (rest instanceof Pair) {
                // this is a proper list
                rest = new Pair(
                    ((Pair)rest).car, ((Pair)rest).cdr);
                restParent.cdr = rest;

                Pair restAsPair = (Pair)rest;
                restParent = restAsPair;
                restAsPair.car = restAsPair.car.optimize(env);
                rest = restAsPair.cdr;
            }
            else {
                /* this is an improper list
                 * (not necessarily an error: think lambda)
                 */
                gleam.util.Log.record(INFO, "dotted pair in optimize... check for correctness");
                restParent.cdr = rest.optimize(env);
                break;
            }
        }
        return retVal;
    }

    /**
     * Writes this pair.
     */
    public void write(java.io.PrintWriter out)
    {
        if (car == Symbol.QUOTE
                && !(cdr instanceof EmptyList) 
                && cdr instanceof Pair 
                && ((Pair)cdr).cdr instanceof EmptyList) {
            out.print("'");
            ((Pair)cdr).car.write(out);
        }
        else if (car == Symbol.QUASIQUOTE
                && !(cdr instanceof EmptyList) 
                && cdr instanceof Pair 
                && ((Pair)cdr).cdr instanceof EmptyList) {
            out.print("`");
            ((Pair)cdr).car.write(out);
        }
        else if (car == Symbol.UNQUOTE
                && !(cdr instanceof EmptyList) 
                && cdr instanceof Pair 
                && ((Pair)cdr).cdr instanceof EmptyList) {
            out.print(",");
            ((Pair)cdr).car.write(out);
        }
        else if (car == Symbol.UNQUOTE_SPLICING
                && !(cdr instanceof EmptyList) 
                && cdr instanceof Pair 
                && ((Pair)cdr).cdr instanceof EmptyList) {
            out.print(",@");
            ((Pair)cdr).car.write(out);
        }
        else {
            Pair current = this;
            out.print("(");
            car.write(out);
            while (current.cdr instanceof Pair && !(current.cdr instanceof EmptyList)) {
                current = (Pair)current.cdr;
                out.print(" ");
                current.car.write(out);
            }
            if (!(current.cdr instanceof EmptyList)) {
                out.print(" . ");
                current.cdr.write(out);             
            }
            out.print(")");
        }
    }
}
