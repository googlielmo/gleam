/*
 * Copyright (c) 2023 Guglielmo Nigri.  All Rights Reserved.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapAdapterTest
{

    /*   For this test suite, we will instantiate a
         MapAdapter<K, V, K1, V1> where:
             K: Integer -> V: Double
             K1: Character -> V1: String
    */

    static Converter<Integer, Character> keyConverter = new Converter<Integer, Character>()
    {
        @Override
        public Character convert(Integer value)
        {
            // 1 -> '1'
            return (char) ('0' + value);
        }

        @Override
        public Integer invert(Character value)
        {
            // '1' -> 1
            return value - '0';
        }

        @Override
        public Character convertAny(Object value)
        {
            // 1.0 -> '1', 1 -> '1', etc.
            if (value instanceof Number) {
                int i = ((Number) value).intValue() - '0';
                return (char) i;
            }
            else {
                return 0;
            }
        }

        @Override
        public Integer invertAny(Object value)
        {
            // '1' -> 1
            if (value instanceof Character) {
                return (int) (Character) value - '0';
            }
            else {
                return 0;
            }
        }
    };
    static Converter<Double, String> valueConverter = new Converter<Double, String>()
    {
        @Override
        public String convert(Double value)
        {
            // 1.0 -> "1.0"
            return value.toString();
        }

        @Override
        public Double invert(String value)
        {
            // "1.0" -> 1.0
            try {
                return Double.valueOf(value);
            }
            catch (NumberFormatException e) {
                return 0.0;
            }
        }

        @Override
        public String convertAny(Object value)
        {
            // 1.0 -> "1.0", 1 -> "1.0"
            if (value instanceof Number) {
                return Double.valueOf(value.toString()).toString();
            }
            else {
                return "0.0";
            }
        }

        @Override
        public Double invertAny(Object value)
        {
            // "1" -> 1.0, "1.0" -> 1.0, etc.
            try {
                return Double.valueOf(value.toString());
            }
            catch (NumberFormatException e) {
                return 0.0;
            }
        }
    };

    MapAdapter<Integer, Double, Character, String> mapAdapter;

    Map<Integer, Double> underlyingMap;

    @BeforeEach
    void setUp()
    {
        underlyingMap = new HashMap<>();
        mapAdapter = new MapAdapter<>(underlyingMap,
                                      keyConverter,
                                      valueConverter);
        underlyingMap.put(1, 1.0);
    }

    @Test
    void containsKey_whenMapAdapter_thenUnderlyingContainsConvertedKey()
    {
        mapAdapter.put('7', "7.0");

        assertTrue(mapAdapter.containsKey('7'));
        assertTrue(underlyingMap.containsKey(7));
    }

    @Test
    void containsValue_whenMapAdapter_thenUnderlyingContainsConvertedValue()
    {
        mapAdapter.put('7', "7.0");

        assertTrue(mapAdapter.containsValue("7.0"));
        assertTrue(underlyingMap.containsValue(7.0));
    }

    @Test
    void keySet_whenMapAdapter_thenUnderlyingContainsConvertedValue()
    {
        Set<Character> kSet = mapAdapter.keySet();
        Set<Integer> k1Set = underlyingMap.keySet();

        assertEquals(1, kSet.size());
        assertEquals(1, k1Set.size());
        assertTrue(kSet.contains('1'));
        assertTrue(k1Set.contains(1));
    }

    @Test
    void values_whenMapAdapter_thenUnderlyingContainsConvertedValue()
    {
        Collection<String> values = mapAdapter.values();
        Collection<Double> values1 = underlyingMap.values();

        assertEquals(1, values.size());
        assertEquals(1, values1.size());
        assertTrue(values.contains("1.0"));
        assertTrue(values1.contains(1.0));
    }

    @Test
    void keySet_whenMapAdapterChanges_thenUnderlyingChanges()
    {
        Set<Character> kSet = mapAdapter.keySet();
        Set<Integer> k1Set = underlyingMap.keySet();

        kSet.remove('1');

        assertEquals(0, kSet.size());
        assertEquals(0, k1Set.size());
        assertEquals(0, mapAdapter.size());
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void keySet_whenUnderlyingChanges_thenMapAdapterChanges()
    {
        Set<Character> kSet = mapAdapter.keySet();
        Set<Integer> k1Set = underlyingMap.keySet();

        k1Set.remove(1);

        assertEquals(0, kSet.size());
        assertEquals(0, k1Set.size());
        assertEquals(0, mapAdapter.size());
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void values_whenMapAdapterChanges_thenUnderlyingChanges()
    {
        Collection<String> values = mapAdapter.values();
        Collection<Double> values1 = underlyingMap.values();

        values.remove("1.0");

        assertEquals(0, values.size());
        assertEquals(0, values1.size());
        assertEquals(0, mapAdapter.size());
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void values_whenUnderlyingChanges_thenMapAdapterChanges()
    {
        Collection<String> values = mapAdapter.values();
        Collection<Double> values1 = underlyingMap.values();

        values1.remove(1.0);

        assertEquals(0, values.size());
        assertEquals(0, values1.size());
        assertEquals(0, mapAdapter.size());
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void entrySet_whenMapAdapterChanges_thenUnderlyingChanges()
    {
        Set<Map.Entry<Character, String>> entries = mapAdapter.entrySet();
        Set<Map.Entry<Integer, Double>> entries1 = underlyingMap.entrySet();

        Map.Entry<Character, String> next = entries.iterator().next();

        next.setValue("9.9");
        assertEquals("9.9", mapAdapter.get('1'));
        assertEquals(9.9, underlyingMap.get(1));

        entries.remove(next);

        assertEquals(0, entries.size());
        assertEquals(0, entries1.size());
        assertEquals(0, mapAdapter.size());
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void entrySet_whenUnderlyingChanges_thenMapAdapterChanges()
    {
        Set<Map.Entry<Character, String>> entries = mapAdapter.entrySet();
        Set<Map.Entry<Integer, Double>> entries1 = underlyingMap.entrySet();

        Map.Entry<Integer, Double> next = entries1.iterator().next();

        next.setValue(9.9);
        assertEquals("9.9", mapAdapter.get('1'));
        assertEquals(9.9, underlyingMap.get(1));

        entries1.remove(next);

        assertEquals(0, entries.size());
        assertEquals(0, entries1.size());
        assertEquals(0, mapAdapter.size());
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void get_whenMapAdapter_thenContainsExpectedValue()
    {
        assertEquals("1.0", mapAdapter.get('1'));
    }

    @Test
    void put_whenMapAdapterNewValue_thenReturnConvertedOldValue()
    {
        String s = mapAdapter.put('1', "9.9");
        assertEquals("1.0", s);
    }

    @Test
    void put_whenMapAdapterNull_thenReturnNull()
    {
        String s = mapAdapter.put('7', "7.0");
        assertNull(s);
    }

    @Test
    void put_whenUnderlying_thenMapAdapterChanges()
    {
        underlyingMap.put(1, 9.9);
        assertEquals(9.9, underlyingMap.get(1));
        assertEquals("9.9", mapAdapter.get('1'));
    }

    @Test
    void put_whenUnderlyingNewValue_thenSizeChanges()
    {
        underlyingMap.put(7, 7.0);
        assertEquals(2, mapAdapter.size());
    }

    @Test
    void put_whenMapAdapterOverwrite_thenUnderlyingChanges()
    {
        mapAdapter.put('1', "9.9");
        assertEquals("9.9", mapAdapter.get('1'));
        assertEquals(9.9, underlyingMap.get(1));
    }

    @Test
    void put_whenMapAdapterNewValue_thenUnderlyingChanges()
    {
        mapAdapter.put('7', "7.0");
        assertEquals("7.0", mapAdapter.get('7'));
        assertEquals(7.0, underlyingMap.get(7));
    }

    @Test
    void remove_whenUnderlying_thenMapAdapterChanges()
    {
        underlyingMap.remove(1); // present
        underlyingMap.remove(7); // not present

        assertEquals(0, mapAdapter.size());
        assertNull(mapAdapter.get('1'));
    }

    @Test
    void remove_whenUnderlyingWithValue_thenMapAdapterChanges()
    {
        underlyingMap.remove(1, 1.0); // present
        underlyingMap.remove(7, 7.0); // not present

        assertEquals(0, mapAdapter.size());
        assertNull(mapAdapter.get('1'));
    }

    @Test
    void remove_whenMapAdapterWithValue_thenUnderlyingChanges()
    {
        mapAdapter.remove('1', "1.0"); // present
        mapAdapter.remove('7', "7.0"); // not present

        assertEquals(0, underlyingMap.size());
        assertNull(underlyingMap.get(1));
    }

    @Test
    void remove_whenMapAdapter_thenUnderlyingChanges()
    {
        mapAdapter.remove('1'); // present
        mapAdapter.remove('7'); // not present

        assertEquals(0, underlyingMap.size());
        assertNull(underlyingMap.get(1));
    }

    @Test
    void size_isTheSameInUnderlyingAndMapAdapter()
    {
        assertEquals(1, underlyingMap.size());
        assertEquals(1, mapAdapter.size());

        mapAdapter.put('7', "7.7");
        underlyingMap.put(8, 8.8);

        assertEquals(3, mapAdapter.size());
        assertEquals(3, underlyingMap.size());
    }

    @Test
    void putAll_whenMapAdapter_thenUnderlyingChanges()
    {
        Map<Character, String> map = new HashMap<>();
        map.put('7', "7.0");
        map.put('8', "8.0");
        map.put('9', "9.0");

        mapAdapter.putAll(map);

        assertEquals(4, underlyingMap.size());

        assertEquals(7.0, underlyingMap.get(7));
        assertEquals(8.0, underlyingMap.get(8));
        assertEquals(9.0, underlyingMap.get(9));
    }

    @Test
    void putAll_whenUnderlying_thenMapAdapterChanges()
    {
        Map<Integer, Double> map = new HashMap<>();
        map.put(7, 7.0);
        map.put(8, 8.0);
        map.put(9, 9.0);

        underlyingMap.putAll(map);

        assertEquals(4, mapAdapter.size());

        assertEquals("7.0", mapAdapter.get('7'));
        assertEquals("8.0", mapAdapter.get('8'));
        assertEquals("9.0", mapAdapter.get('9'));
    }

    @Test
    void clear_whenUnderlying_thenMapAdapterChanges()
    {
        underlyingMap.clear();
        assertEquals(0, mapAdapter.size());
    }

    @Test
    void clear_whenMapAdapter_thenUnderlyingChanges()
    {
        mapAdapter.clear();
        assertEquals(0, underlyingMap.size());
    }

    @Test
    void isEmpty_whenUnderlying_thenMapAdapterChanges()
    {
        assertFalse(mapAdapter.isEmpty());
        underlyingMap.clear();
        assertTrue(mapAdapter.isEmpty());
    }

    @Test
    void isEmpty_whenMapAdapter_thenUnderlyingChanges()
    {
        assertFalse(underlyingMap.isEmpty());
        mapAdapter.clear();
        assertTrue(underlyingMap.isEmpty());
    }
}