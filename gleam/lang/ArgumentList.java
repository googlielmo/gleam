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

import java.util.*;

public class ArgumentList implements java.io.Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList listArgs;
	private Pair pairArgs;

	public ArgumentList() {
		this.listArgs = new ArrayList();
		this.pairArgs = null;
	}

	public void setArguments(Pair args) {
		this.listArgs = null;
		this.pairArgs = args;
	}

	public void put(Entity obj, int index) {
		ensureSize(index+1);
		listArgs.set(index, obj);
	}

	public void ensureSize(int size) {
		int missing = size - listArgs.size();
		listArgs.ensureCapacity(size);
		for (int i = 0 ; i < missing; ++i) {
			listArgs.add(Undefined.value);
		}
	}

	/**
	 * getArguments
	 *
	 * @return Pair
	 */
	public Pair getArguments() {
		if (pairArgs != null) 
			return pairArgs;
		else
			return j2g(listArgs);
	}
	
	private Pair j2g(List lst) {
		if (lst.size() == 0)
			return EmptyList.value;
		
		Pair p = EmptyList.value;
		for (int i = lst.size() - 1; i >= 0; --i) {
			p = new Pair((Entity)lst.get(i), p);
		}
		return p;
	}
}
