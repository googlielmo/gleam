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

/**
 * List read/write iterator.
 */
public class ListIterator {
	private Pair pair;
	private Pair parentPair;
	private boolean allowImproper;
	private boolean wasImproper;

	/**
	 * Creates an iterator over a proper list.
	 */
	public ListIterator(Pair pair) {
		this(pair, false);
	}

	/**
	 * Creates an iterator over a (possibly improper) list.
	 */
	public ListIterator(Pair pair, boolean allowImproper) {
		this.pair = pair;
		this.allowImproper = allowImproper;
		this.parentPair = null;
		this.wasImproper = false;
	}

	/**
	 * Determines whether there's another object to retrieve from the list.
	 */
	public boolean hasNext() {
		return pair != EmptyList.value;
	}

	/**
	 * Retrieves next object from the list.
	 */
	public Entity next()
		throws GleamException
	{
		if (!wasImproper) {
			Entity retVal = pair.car;
			parentPair = pair;
			try {
				pair = (Pair)pair.cdr;
			}
			catch (ClassCastException e) {
				wasImproper = true;
			}
			return retVal;
		}
		else {
			Entity retVal = pair.cdr;
			pair = EmptyList.value;
			if (allowImproper) {
				return retVal;
			}
			else {
				throw new ImproperListException(retVal);
			}
		}
	}

	/**
	 * Replaces current object.
	 * Must be called after next().
	 */
	public void replace(Entity newArg)
		throws GleamException
	{
		if (parentPair == null) {
			throw new GleamException(
				"No current object to replace", pair);	
		}
		if (wasImproper && pair == EmptyList.value) {
			parentPair.cdr = newArg;
		}
		else {
			parentPair.car = newArg;
		}
	}
	
	/**
	 * Remove operation currently not supported. 
	 */
	public void remove() {
		throw new UnsupportedOperationException("Remove operation currently not supported");
	}

	/**
	 * Returns the remaining portion of the list as a Pair.
	 */
	public Pair rest() {
		return pair;
	}
}

