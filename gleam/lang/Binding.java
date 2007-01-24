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
 * Initial binding descriptor.
 */
final class Binding {
	boolean keyword;
	String	name;
	String	procedureName;
	String	comment;
	String	documentation;
	
	static final boolean KEYWORD = true;
	static final boolean IDENTIFIER = false;
	static final String NOPROCEDURE = null;
	
	/**
	 * Binding constructor without comment or documentation.
	 */
	Binding(boolean keyword, String name, String procedureName) {
		this(keyword, name, procedureName, "No documentation defined",
			null);
	}

	/**
	 * Binding constructor with comment.
	 */
	Binding(boolean keyword, String name, String procedureName,
			String comment) {
		this(keyword, name, procedureName, comment, null);
	}

	/**
	 * Binding constructor with comment and documentation.
	 */
	Binding(boolean keyword, String name, String procedureName,
			String comment, String documentation) {
		this.keyword = keyword;
		this.name = name;
		this.procedureName = procedureName;
		this.comment = comment;
		if (documentation != null) {
			this.documentation = comment + "\n" + documentation;
		}
		else {
			this.documentation = comment;
		}
	}
}
