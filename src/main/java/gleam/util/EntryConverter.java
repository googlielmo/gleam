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

import java.util.Map;

public class EntryConverter<K, V, K1, V1> implements Converter<Map.Entry<K, V>, Map.Entry<K1, V1>> {
    private final Converter<K, K1> keyConverter;
    private final Converter<V, V1> valueConverter;

    public EntryConverter(Converter<K, K1> keyConverter,
                          Converter<V, V1> valueConverter)
    {
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public Map.Entry<K1, V1> convert(Map.Entry<K, V> entry) {
        return new ConverterEntry<>(entry, keyConverter, valueConverter);
    }

    @Override
    public Map.Entry<K, V> invert(Map.Entry<K1, V1> entry) {
        return new ConverterEntry<>(entry, keyConverter.inverseConverter(), valueConverter.inverseConverter());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map.Entry<K1, V1> convertAny(Object value) {
        return convert((Map.Entry<K, V>) value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map.Entry<K, V> invertAny(Object value) {
        return invert((Map.Entry<K1, V1>) value);
    }

    private static class ConverterEntry<EK, EV, EK1, EV1> implements Map.Entry<EK1, EV1> {

        private final Map.Entry<EK, EV> underlyingEntry;
        private final Converter<EK, EK1> keyConverter;
        private final Converter<EV, EV1> valueConverter;

        public ConverterEntry(Map.Entry<EK, EV> underlyingEntry, Converter<EK, EK1> keyConverter, Converter<EV, EV1> valueConverter) {
            this.underlyingEntry = underlyingEntry;
            this.keyConverter = keyConverter;
            this.valueConverter = valueConverter;
        }

        @Override
        public EK1 getKey() {
            return keyConverter.convert(underlyingEntry.getKey());
        }

        @Override
        public EV1 getValue() {
            return valueConverter.convert(underlyingEntry.getValue());
        }

        @Override
        public EV1 setValue(EV1 value) {
            EV1 prev = valueConverter.convert(underlyingEntry.getValue());
            this.underlyingEntry.setValue(valueConverter.invert(value));
            return prev;
        }
    }
}
