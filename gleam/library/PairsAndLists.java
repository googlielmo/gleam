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

import gleam.lang.Entity;
import gleam.lang.Boolean;
import gleam.lang.Character;
import gleam.lang.Number;
import gleam.lang.System;
import gleam.lang.Void;

import gleam.lang.*;

/**
 * PAIRS AND LISTS
 * Primitive operator and procedure implementation library.
 */
public final class PairsAndLists {

	/**
	 * Can't instantiate this class
	 */
	private PairsAndLists() {
	}

	/**
	 * car
	 * Takes the first element of a pair.
	 */
	public static Entity gleam_car_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		try {
			return ((Pair) arg1).getCar();
		}
		catch (ClassCastException e) {
			throw new GleamException("car: invalid argument", arg1);
		}
	}

	/**
	 * cdr
	 * Takes the second element of a pair.
	 */
	public static Entity gleam_cdr_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		try {
			return ((Pair) arg1).getCdr();
		}
		catch (ClassCastException e) {
			throw new GleamException("cdr: invalid argument", arg1);
		}
	}

	/**
	 * cons
	 * Creates a new pair.
	 */
	public static Entity gleam_cons_$2(Entity first, Entity second, Environment env, Continuation cont)
		throws GleamException
	{
		return new Pair(first, second);
	}

	/**
	 * append
	 *
	 */
	public static Entity gleam_append(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity sbap = null; // should be a pair (used for error reporting)
		try {
			ListIterator it = new ListIterator(args);
			if (!it.hasNext())
				return EmptyList.makeEmptyList();

			Entity last = it.next();
			Pair ret = new Pair(EmptyList.makeEmptyList(), last);
			Pair lastPair = ret;
			while (it.hasNext()) {
				// get next arg
				Entity curr = it.next();

				if (lastPair.getCdr() instanceof EmptyList) {
					lastPair.setCdr(curr);
				}
				else {
					// shallow copy
					sbap = last;
					last = new Pair( ((Pair) sbap).getCar(), ((Pair) sbap).getCdr());
					lastPair.setCdr(last);

					// append
					sbap = last;
					Pair p = (Pair) sbap;
					while (p.getCdr() != EmptyList.makeEmptyList()) {
						sbap = p.getCdr();
						p = (Pair) sbap;
					}

					// prepare next
					lastPair = p;
					p.setCdr(curr);
				}
				last = curr;
			}
			return ret.getCdr();
		}
		catch (ClassCastException e) {
			throw new GleamException("append: argument is not a list", sbap);
		}
	}

	/**
	 * list
	 * Creates a new list from its arguments.
	 */
	public static Entity gleam_list(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		ListIterator it = new ListIterator(args);
		if (!it.hasNext()) {
			return EmptyList.makeEmptyList();
		}
		Pair l = new Pair(it.next(), EmptyList.makeEmptyList());
		Pair ins = l;
		while (it.hasNext()) {
			Pair nextcons = new Pair(it.next(), EmptyList.makeEmptyList());
			ins.setCdr(nextcons);
			ins = nextcons;
		}
		return l;
	}

	/**
	 * pair?
	 * Tests if argument is a pair
	 */
	public static Entity gleam_pair_p_$1(Entity obj, Environment env, Continuation cont)
		throws GleamException
	{
		return Boolean.makeBoolean((obj instanceof Pair) && !(obj instanceof EmptyList));
	}

	/**
	 * null?
	 * Tests if argument is the empty list
	 */
	public static Entity gleam_null_p_$1(Entity obj, Environment env, Continuation cont)
		throws GleamException
	{
		return Boolean.makeBoolean(obj instanceof EmptyList);
	}

	/**
	 * set-car!
	 * store an object in the car field of a pair.
	 */
	public static Entity gleam_set_car_m_$2(Entity first, Entity second, Environment env, Continuation cont)
		throws GleamException
	{
		if (!(first instanceof Pair))
			throw new GleamException("set-car!: invalid argument", first);

		((Pair) first).setCar(second);
		return Void.makeVoid();
	}

	/**
	 * set-cdr!
	 * store an object in the cdr field of a pair.
	 */
	public static Entity gleam_set_cdr_m_$2(Entity first, Entity second, Environment env, Continuation cont)
		throws GleamException
	{
		if (!(first instanceof Pair))
			throw new GleamException("set-cdr!: invalid arguments", first);

		((Pair) first).setCdr(second);
		return Void.makeVoid();
	}

}
