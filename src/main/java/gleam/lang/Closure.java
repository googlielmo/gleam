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

import java.io.PrintWriter;

import static gleam.lang.Entities.cons;
import static gleam.util.Logger.Level.WARNING;

/**
 * Scheme closure. A procedure with a definition environment.
 */
public class Closure extends Procedure
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger();

    protected final Entity param;
    protected final List body;
    protected final Environment definitionEnv;

    /**
     * Constructor.
     */
    public Closure(Entity param, List body, Environment env)
    {
        this.param = param;
        this.body = body;
        this.definitionEnv = env;
    }

    /**
     * Applies this closure.
     */
    @Override
    public Entity apply(List args,
                        Environment env,
                        Continuation cont) throws GleamException
    {
        Environment localenv = new Environment(definitionEnv);
        Entity currparam = param;
        List prev = null;
        boolean dotparam = false;

        /* bind actual (already evaluated) arguments to formals */
        try {
            // for each passed arg
            while (args != EmptyList.VALUE) {
                // get next arg
                Entity obj = args.getCar();

                if (!dotparam) {
                    if (currparam == EmptyList.VALUE) {
                        throw new GleamException("apply: too many arguments",
                                                 this);
                    }
                    else if (currparam instanceof Pair) {
                        // regular case: get param symbol and bind it to argument in local env
                        Entity p = ((Pair) currparam).getCar();
                        if (p instanceof Symbol) {
                            localenv.define((Symbol) p, obj);
                        }
                        else {
                            logger.log(WARNING, "apply: param is not a symbol");
                        }
                        // get next param
                        currparam = ((Pair) currparam).getCdr();
                    }
                    else if (currparam instanceof Symbol) {
                        // varargs case:
                        // we have a "." notation parameter, so we accumulate this and the next
                        // parameters in a list bound to this param in local env
                        prev = cons(obj);
                        localenv.define((Symbol) currparam, prev);
                        dotparam = true;
                    }
                    else {
                        throw new GleamException("apply: invalid formal parameter", currparam);
                    }
                }
                else {
                    // accumulate argument
                    prev.setCdr(cons(obj));
                    prev = (List) prev.getCdr();
                }
                // next argument, please
                args = (List) args.getCdr();
            }
        }
        catch (ClassCastException e) {
            throw new GleamException("apply: improper list", currparam);
        }

        if (currparam instanceof Symbol && !dotparam) {
            // special case:
            // a "." notation parameter taking the empty list
            localenv.define((Symbol) currparam, EmptyList.VALUE);
        }
        else if (currparam != EmptyList.VALUE && !dotparam) {
            throw new GleamException("apply: too few arguments", this);
        }

        // we have bound params, let's eval body
        // by adding to the continuation
        cont.addCommandSequence(body, localenv);
        return null;
    }

    /**
     * Writes a Closure.
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.write("#<procedure");
        if (logger.getLevelValue() < Logger.Level.INFO.getValue()) {
            out.write(" ");
            cons(Symbol.LAMBDA, cons(param, body)).write(out);
        }
        out.write(">");
        return out;
    }

    /**
     * Gets the maximum arity for this closure.
     *
     * @return the max number of arguments, or -1 in case of varargs
     *
     * @throws GleamException in case of errors
     */
    public int getMaxArity() throws GleamException
    {
        int count = 0;
        Entity currParam = param;
        while (currParam instanceof Pair) {
            count++;
            currParam = ((List) currParam).getCdr();
        }
        if (currParam instanceof Symbol) {
            return -1;
        }
        return count;
    }
}
