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

package gleam.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CollectionAdapter<V, V1> implements Collection<V1>
{
    private final Collection<V> vCollection;
    private final Converter<V, V1> converter;

    public CollectionAdapter(Collection<V> vCollection, Converter<V, V1> converter)
    {
        this.vCollection = vCollection;
        this.converter = converter;
    }

    @Override
    public int size()
    {
        return vCollection.size();
    }

    @Override
    public boolean isEmpty()
    {
        return vCollection.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return vCollection.contains(converter.invertAny(o));
    }

    @Override
    public Iterator<V1> iterator()
    {
        return vCollection.stream().map(converter::convert).iterator();
    }

    @Override
    public Object[] toArray()
    {
        return vCollection.stream().map(converter::convert).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return vCollection.stream()
                          .map(converter::convert)
                          .collect(Collectors.toList())
                          .toArray(a);
    }

    @Override
    public boolean add(V1 v1)
    {
        return vCollection.add(converter.invert(v1));
    }

    @Override
    public boolean remove(Object o)
    {
        return vCollection.remove(converter.invertAny(o));
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        final AtomicBoolean contained = new AtomicBoolean(true);
        c.forEach(value -> contained.set(contained.get() && vCollection.contains(converter.invertAny(value))));
        return contained.get();
    }

    @Override
    public boolean addAll(Collection<? extends V1> c)
    {
        final AtomicBoolean changed = new AtomicBoolean(false);
        c.forEach(value -> changed.set(changed.get() || vCollection.add(converter.invert(value))));
        return changed.get();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        final AtomicBoolean changed = new AtomicBoolean(false);
        c.forEach(value -> {
            boolean removed = vCollection.remove(converter.invertAny(value));
            changed.set(changed.get() || removed);
        });
        return changed.get();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        final AtomicBoolean changed = new AtomicBoolean(false);
        vCollection.forEach(value -> {
            if (!c.contains(converter.convertAny(value))) {
                boolean removed = vCollection.remove(value);
                changed.set(changed.get() || removed);
            }
        });
        return changed.get();
    }

    @Override
    public void clear()
    {
        vCollection.clear();
    }
}
