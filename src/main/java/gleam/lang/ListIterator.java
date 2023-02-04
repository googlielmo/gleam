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
import java.util.NoSuchElementException;

/**
 * List read/write iterator.
 */
public class ListIterator implements Iterator<Entity>
{

    private final boolean allowImproper;

    private boolean isImproper;
    private List pair;
    private List restPair;

    /**
     * Creates an iterator over a proper list.
     */
    public ListIterator(List pair)
    {
        this(pair, false);
    }

    /**
     * Creates an iterator over a (possibly improper) list.
     */
    public ListIterator(List pair, boolean allowImproper)
    {
        this.pair = pair;
        this.allowImproper = allowImproper;
        this.restPair = null;
        this.isImproper = false;
    }

    /**
     * Determines whether there's another object to retrieve from the list.
     */
    public boolean hasNext()
    {
        return pair != EmptyList.VALUE;
    }

    /**
     * Retrieves next object from the list.
     *
     * @return the next Entity in the list
     *
     * @throws NoSuchElementException if no next element is available
     */
    @Override
    public Entity next() throws NoSuchElementException
    {
        final Entity retVal;
        try {
            if (isImproper) {
                retVal = pair.getCdr();
                pair = EmptyList.VALUE;
                if (!allowImproper) {
                    throw (NoSuchElementException) new NoSuchElementException(
                            "improper list").initCause(new ImproperListException(
                            retVal));
                }
            }
            else {
                retVal = pair.getCar();
                restPair = pair;
                if (pair.getCdr() instanceof List) {
                    pair = (List) pair.getCdr();
                }
                else {
                    isImproper = true;
                }
            }
        }
        catch (GleamException e) {
            throw (NoSuchElementException) new NoSuchElementException().initCause(
                    e);
        }
        if (retVal == null) {
            throw new NoSuchElementException("null element");
        }
        return retVal;
    }

    /**
     * Remove operation not supported.
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Unsupported remove operation");
    }

    /**
     * Replaces current value. Must be called after next().
     */
    public void replace(Entity newArg) throws GleamException
    {
        if (newArg == null) {
            throw new GleamException("Unexpected null");
        }
        if (restPair == null) {
            throw new GleamException("No current value to replace", pair);
        }
        if (isImproper && pair == EmptyList.VALUE) {
            restPair.setCdr(newArg);
        }
        else {
            restPair.setCar(newArg);
        }
    }

    /**
     * Returns the remaining portion of the list as a Pair.
     */
    public Entity rest()
    {
        return pair;
    }
}
