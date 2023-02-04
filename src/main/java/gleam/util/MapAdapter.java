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
import java.util.Map;
import java.util.Set;

public class MapAdapter<K, V, K1, V1> implements Map<K1, V1>
{

    private final Map<K, V> kvMap;
    private final Converter<K, K1> keyConverter;
    private final Converter<V, V1> valueConverter;

    public MapAdapter(Map<K, V> kvMap,
                      Converter<K, K1> keyConverter,
                      Converter<V, V1> valueConverter)
    {
        this.kvMap = kvMap;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public int size()
    {
        return kvMap.size();
    }

    @Override
    public boolean isEmpty()
    {
        return kvMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return kvMap.containsKey(keyConverter.invertAny(key));
    }

    @Override
    public boolean containsValue(Object value)
    {
        return kvMap.containsValue(valueConverter.invertAny(value));
    }

    @Override
    public V1 get(Object key)
    {
        return valueConverter.convert(kvMap.get(keyConverter.invertAny(key)));
    }

    @Override
    public V1 put(K1 key, V1 value)
    {
        K k = keyConverter.invert(key);
        V v = valueConverter.invert(value);
        return valueConverter.convert(kvMap.put(k, v));
    }

    @Override
    public V1 remove(Object key)
    {
        return valueConverter.convert(kvMap.remove(key));
    }

    @Override
    public void putAll(Map<? extends K1, ? extends V1> m)
    {
        m.forEach((k1, v1) -> kvMap.put(keyConverter.invert(k1),
                                        valueConverter.invert(v1)));
    }

    @Override
    public void clear()
    {
        kvMap.clear();
    }

    @Override
    public Set<K1> keySet()
    {
        return new SetAdapter<>(kvMap.keySet(), keyConverter);
    }

    @Override
    public Collection<V1> values()
    {
        return new CollectionAdapter<>(kvMap.values(), valueConverter);
    }

    @Override
    public Set<Entry<K1, V1>> entrySet()
    {
        return new EntrySetAdapter<>(kvMap.entrySet(),
                                     keyConverter,
                                     valueConverter);
    }
}
