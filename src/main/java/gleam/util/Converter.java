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

public interface Converter<A, B>
{
    B convert(A value);

    A invert(B value);

    B convertAny(Object value);

    A invertAny(Object value);

    default Converter<B, A> inverseConverter()
    {
        return new InverseConverter<>(this);
    }

    class InverseConverter<A, B> implements Converter<B, A>
    {
        private final Converter<A, B> converter;

        public InverseConverter(Converter<A, B> converter)
        {
            this.converter = converter;
        }

        @Override
        public A convert(B value)
        {
            return converter.invert(value);
        }

        @Override
        public B invert(A value)
        {
            return converter.convert(value);
        }

        @Override
        public A convertAny(Object value)
        {
            return converter.invertAny(value);
        }

        @Override
        public B invertAny(Object value)
        {
            return converter.convertAny(value);
        }

        @Override
        public Converter<A, B> inverseConverter()
        {
            return converter;
        }
    }
}
