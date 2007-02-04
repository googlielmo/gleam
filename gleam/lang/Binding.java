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
	int minArgs, maxArgs;
	
	// constant values for keyword field
	static final boolean KEYWORD = true; // binding for a language keyword
	static final boolean IDENTIFIER = false; // binding for an identifier
	
	// constant value for procedureName field
	static final String NO_PROCEDURE = null; // no procedure is defined

	// constant value for maxArgs
	static final int VAR_ARGS = -1; // variable number of arguments
	
	/**
	 * Binding constructor without comment or documentation.
	 */
	Binding(boolean keyword, String name, int minArgs, int maxArgs, String procedureName) {
		this(keyword, name, minArgs, maxArgs, procedureName, "No documentation defined",
			null);
	}

	/**
	 * Binding constructor with comment.
	 */
	Binding(boolean keyword, String name, int minArgs, int maxArgs, String procedureName,
			String comment) {
		this(keyword, name, minArgs, maxArgs, procedureName, comment, null);
	}

	/**
	 * Binding constructor with comment and documentation.
	 */
	Binding(boolean keyword, String name, int minArgs, int maxArgs, String procedureName,
			String comment, String documentation) {
		this.keyword = keyword;
		this.name = name;
		this.minArgs = minArgs;
		this.maxArgs = maxArgs;
		this.procedureName = procedureName;
		if (comment != null)
			this.comment = comment;
		else
			this.comment = "No documentation defined";
		
		if (documentation != null)
			this.documentation = this.comment + "\n" + documentation;
		else
			this.documentation = this.comment;

	}

}
