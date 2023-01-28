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

import java.util.ArrayList;

/**
 * A list of arguments for a procedure.
 */
public class ArgumentList implements java.io.Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final java.util.ArrayList<Entity> listArgs;
    private final List pairArgs;

    public ArgumentList()
    {
        this.listArgs = new java.util.ArrayList<>();
        this.pairArgs = null;
    }

    public ArgumentList(List args)
    {
        this.listArgs = null;
        this.pairArgs = args;
    }

    /**
     * Sets the value for an argument at a given index
     *
     * @param index the index in this list
     * @param value the value for the argument
     */
    public void set(int index, Entity value)
    {
        assert listArgs != null : "set called on immutable ArgumentList";
        ensureSize(listArgs, index + 1);
        listArgs.set(index, value);
    }

    /**
     * @return List the list of arguments
     */
    public List getArguments()
    {
        return null == listArgs ? pairArgs : j2g(listArgs);
    }

    /**
     * Ensure argument list has at least <code>size</code> elements. Grow the
     * list with Undefined values if necessary.
     *
     * @param listArgs the argument list
     * @param size     minimum needed size
     */
    private void ensureSize(ArrayList<Entity> listArgs, int size)
    {
        int missing = size - listArgs.size();
        listArgs.ensureCapacity(size);
        for (int i = 0; i < missing; ++i) {
            listArgs.add(Undefined.VALUE);
        }
    }

    private List j2g(java.util.List<Entity> lst)
    {
        if (lst.isEmpty()) {
            return EmptyList.VALUE;
        }

        List p = EmptyList.VALUE;
        for (int i = lst.size() - 1; i >= 0; --i) {
            p = new Pair(lst.get(i), p);
        }
        return p;
    }
}
