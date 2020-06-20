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

import gleam.lang.Boolean;
import gleam.lang.Number;
import gleam.lang.*;

/**
 * Primitive operator and procedure implementation library.
 */
public final class Numbers {

    /**
     * Can't instantiate this class
     */
    private Numbers() {
    }

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the initial environments.
     */
    public static Primitive[] primitives = {

    /**
     * -
     * Implements the minus operator.
     */
    new Primitive( "-",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Difference, e.g. (- 7 3); Also negation, e.g. (- x)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        double result = 0.0;
        Entity obj;
        ListIterator it = new ListIterator(args);
        // first assume unary minus
        if (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                result -= ((Number) obj).getDoubleValue();
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }
        /* if it is a real difference make sign adjustment and
         * subtract remaining arguments
         */
        if (it.hasNext()) {
            result = -result;
        }
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                result -= ((Number) obj).getDoubleValue();
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return new Real(result);
    }},

    /**
     * +
     * Implements the plus operator.
     */
    new Primitive( "+",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Addition, e.g (+ 1 2)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        double result = 0.0;
        Entity obj;
        ListIterator it = new ListIterator(args);   
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                result += ((Number) obj).getDoubleValue();
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return new Real(result);
    }},

    /**
     * /
     * Implements the division operator.
     */
    new Primitive( "/",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Division, e.g. (/ 42 7)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        double result = 1.0;
        Entity obj;
        ListIterator it = new ListIterator(args);   
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                result /= ((Number) obj).getDoubleValue();
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return new Real(result);
    }},

    /**
     * *
     * Implements the multiplication operator.
     */
    new Primitive( "*",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Multiplication, e.g. (* 7 9)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        double result = 1.0;
        Entity obj;
        ListIterator it = new ListIterator(args);   
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                result *= ((Number) obj).getDoubleValue();
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return new Real(result);
    }},
    
    /**
     * =
     * Implements the equals operator.
     */
    new Primitive( "=",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Equals comparison, e.g. (= 1 1)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        boolean retVal = true;
        double prev, curr;
        Entity obj;
        ListIterator it = new ListIterator(args);
        // get first argument as prev
        obj = it.next();
        if (obj instanceof Number) {
            prev = ((Number) obj).getDoubleValue();
        }
        else {
            throw new GleamException(this, "argument is not a number", obj);
        }
        
        // follow remaining arguments
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                curr = ((Number) obj).getDoubleValue();
                retVal &= prev == curr;
                prev = curr;
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return Boolean.makeBoolean(retVal);
    }},

    /**
     * &gt;= 
     * Implements the greater than or equals operator.
     */
    new Primitive( ">=",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Greater-than-or-equals comparison, e.g. (>= 1 2)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        boolean retVal = true;
        double prev, curr;
        Entity obj;
        ListIterator it = new ListIterator(args);
        // get first argument as prev
        obj = it.next();
        if (obj instanceof Number) {
            prev = ((Number) obj).getDoubleValue();
        }
        else {
            throw new GleamException(this, "argument is not a number", obj);
        }
        
        // follow remaining arguments
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                curr = ((Number) obj).getDoubleValue();
                retVal &= prev >= curr;
                prev = curr;
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return Boolean.makeBoolean(retVal);
    }},

    /**
     * &lt;=
     * Implements the less than or equals operator.
     */
    new Primitive( "<=",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Less-than-or-equals comparison, e.g. (<= 1 2)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        boolean retVal = true;
        double prev, curr;
        Entity obj;
        ListIterator it = new ListIterator(args);   
        // get first argument as prev
        obj = it.next();
        if (obj instanceof Number) {
            prev = ((Number) obj).getDoubleValue();
        }
        else {
            throw new GleamException(this, "argument is not a number", obj);
        }
        
        // follow remaining arguments
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                curr = ((Number) obj).getDoubleValue();
                retVal &= prev <= curr;
                prev = curr;
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return Boolean.makeBoolean(retVal);
    }},

    /**
     * &gt;
     * Implements the greater than operator.
     */
    new Primitive( ">",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Greater-than comparison, e.g. (> 1 2)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        boolean retVal = true;
        double prev, curr;
        Entity obj;
        ListIterator it = new ListIterator(args);   
        // get first argument as prev
        obj = it.next();
        if (obj instanceof Number) {
            prev = ((Number) obj).getDoubleValue();
        }
        else {
            throw new GleamException(this, "argument is not a number", obj);
        }
        
        // follow remaining arguments
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                curr = ((Number) obj).getDoubleValue();
                retVal &= prev > curr;
                prev = curr;
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return Boolean.makeBoolean(retVal);
    }},

    /**
     * &lt;
     * Implements the less than operator.
     */
    new Primitive( "<",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, Primitive.VAR_ARGS, /* min, max no. of arguments */
        "Less-than comparison, e.g. (< 1 2)",
        null /* doc strings */ ) {
    public Entity applyN(Pair args, Environment env, Continuation cont)
        throws GleamException
    {
        boolean retVal = true;
        double prev, curr;
        Entity obj;
        ListIterator it = new ListIterator(args);   
        // get first argument as prev
        obj = it.next();
        if (obj instanceof Number) {
            prev = ((Number) obj).getDoubleValue();
        }
        else {
            throw new GleamException(this, "argument is not a number", obj);
        }
        
        // follow remaining arguments
        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof Number) {
                curr = ((Number) obj).getDoubleValue();
                retVal &= prev < curr;
                prev = curr;
            }
            else {
                throw new GleamException(this, "argument is not a number", obj);
            }
        }   
        return Boolean.makeBoolean(retVal);
    }},

    /**
     * number?
     * Tests if argument is a number
     */
    new Primitive( "number?",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Returns true if argument is a number, false otherwise",
        "E.g. (number? 3) => #t" /* doc strings */ ) {
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        return Boolean.makeBoolean(arg1 instanceof Number);
    }},
    
    }; // primitives
}