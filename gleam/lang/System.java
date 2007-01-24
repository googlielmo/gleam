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

import gleam.util.Report;

/**
 * Scheme runtime support.
 */
public final class System
{
	/** can't instantiate this class */
	private System() {}

	/** the keyword set */
	private static java.util.HashSet kwSet = new java.util.HashSet();

	/** the null environment, as defined in r5rs */
	private static Environment nullEnv = null;

	/** the scheme-report environment, as defined in r5rs */
	private static Environment r5rsEnv = null;

	/** the interaction environment, as defined in r5rs */
	private static Environment intrEnv = null;

	/** the current-input-port */
	private static InputPort cin = null;

	/** the current-output-port */
	private static OutputPort cout = null;

	/** the short-help map */
	private static java.util.HashMap helpComment
		= new java.util.HashMap();

	/** the long-help map */
	private static java.util.HashMap helpDocumentation
		= new java.util.HashMap();

	// links to real bindings
	private static Binding[] nullEnvBindings =
		InitialEnvironments.nullEnvBindings;
	private static Binding[] r5rsEnvBindings =
		InitialEnvironments.r5rsEnvBindings;
	private static Binding[] intrEnvBindings =
		InitialEnvironments.intrEnvBindings;

	// static initializer, executed once after loading class
	static {
		fillKeywordSet();
		createInitialEnvironments();
		fillHelpMaps();
		bindIOPorts();
	}

	/**
	 * binds current I/O ports to system standard I/O
	 */
	private static void bindIOPorts() {
		cin = new InputPort(new java.io.BufferedReader(
			new java.io.InputStreamReader(
			java.lang.System.in)));
		cout = new OutputPort(new java.io.PrintWriter(
			java.lang.System.out, true));
	}

	/**
	 * Creates the three initial environments (null, r5rs, interaction).
	 * Creation date: (03/11/01 10.00.30)
	 */
	private static void createInitialEnvironments() {
		try {
			/*
			 * define null environment
			 */
			nullEnv = new SystemEnvironment(null, SystemEnvironment.NULL);
			for (int i = 0; i < nullEnvBindings.length; ++i) {
				if (nullEnvBindings[i].procedureName !=
				    Binding.NOPROCEDURE) {
					nullEnv.define(
						Symbol.makeSymbol(
						nullEnvBindings[i].name),
						new SyntaxProcedure(
						nullEnvBindings[i].
						procedureName));
				}
			}

			/*
			 * define scheme-report environment
			 */
			r5rsEnv = new SystemEnvironment(nullEnv, SystemEnvironment.R5RS);

			for (int i = 0; i < r5rsEnvBindings.length; ++i) {
				if (r5rsEnvBindings[i].procedureName !=
				    Binding.NOPROCEDURE) {
					r5rsEnv.define(
						Symbol.makeSymbol(
						r5rsEnvBindings[i].name),
						new PrimitiveProcedure(
						r5rsEnvBindings[i].
						procedureName));
				}
			}

			/*
			 * define interaction environment
			 */
			intrEnv = new SystemEnvironment(r5rsEnv, SystemEnvironment.INTR);

			for (int i = 0; i < intrEnvBindings.length; ++i) {
				if (intrEnvBindings[i].procedureName !=
				    Binding.NOPROCEDURE) {
					intrEnv.define(
						Symbol.makeSymbol(
						intrEnvBindings[i].name),
						new PrimitiveProcedure(
						intrEnvBindings[i].
						procedureName));
				}
			}

			/*
			 * add special symbols to interaction environment
			 */
			intrEnv.define(Symbol.ERROBJ, Void.value);
			intrEnv.define(Symbol.CALL_CC, r5rsEnv.lookup(Symbol.CALL_WITH_CURRENT_CONTINUATION ));
		}
		catch (GleamException e) {
			// should never happen
			gleam.util.Report.println(1,
			  "Internal error during environment initialization: "
			  + e.getMessage());
		}
	}

	/**
	 * Installs the Gleam language keyword set.
	 * Creation date: (03/11/01 10.32.05)
	 */
	private static void fillKeywordSet() {
		for (int i = 0; i < nullEnvBindings.length; ++i) {
			if (nullEnvBindings[i].keyword) {
				kwSet.add(Symbol.makeSymbol(nullEnvBindings[i].
					name));
			}
		}
		for (int i = 0; i < r5rsEnvBindings.length; ++i) {
			if (r5rsEnvBindings[i].keyword) {
				kwSet.add(Symbol.makeSymbol(r5rsEnvBindings[i].
					name));
			}
		}
		for (int i = 0; i < intrEnvBindings.length; ++i) {
			if (intrEnvBindings[i].keyword) {
				kwSet.add(Symbol.makeSymbol(intrEnvBindings[i].
					name));
			}
		}
	}

	/**
	 * Installs the help maps.
	 */
	static void fillHelpMaps() {
		for (int i = 0; i < nullEnvBindings.length; ++i) {
			if (nullEnvBindings[i].comment != null) {
				helpComment.put(nullEnvBindings[i].name,
						nullEnvBindings[i].comment);
			}
			if (nullEnvBindings[i].documentation != null) {
				helpDocumentation.put(nullEnvBindings[i].name,
					nullEnvBindings[i].documentation);
			}
		}
		for (int i = 0; i < r5rsEnvBindings.length; ++i) {
			if (r5rsEnvBindings[i].comment != null) {
				helpComment.put(r5rsEnvBindings[i].name,
						r5rsEnvBindings[i].comment);
			}
			if (r5rsEnvBindings[i].documentation != null) {
				helpDocumentation.put(r5rsEnvBindings[i].name,
					r5rsEnvBindings[i].documentation);
			}
		}
		for (int i = 0; i < intrEnvBindings.length; ++i) {
			if (intrEnvBindings[i].comment != null) {
				helpComment.put(intrEnvBindings[i].name,
						intrEnvBindings[i].comment);
			}
			if (intrEnvBindings[i].documentation != null) {
				helpDocumentation.put(intrEnvBindings[i].name,
					intrEnvBindings[i].documentation);
			}
		}
	}

	/**
	 * Gets the comment string for a procedure
	 */
	public static String getHelpComment(String name) {
		return (String) helpComment.get(name);
	}

	/**
	 * Gets the comment string for a procedure
	 */
	public static String getHelpDocumentation(String name) {
		return (String) helpDocumentation.get(name);
	}

	/**
	 * Gets the set of help-enabled procedures
	 */
	public static java.util.Set getHelpNames() {
		return new java.util.TreeSet(
			(java.util.Set) helpDocumentation.keySet());
	}

	public static Environment getInteractionEnv() {
		return intrEnv;
	}

	public static Environment getNullEnv() {
		return nullEnv;
	}

	public static Environment getSchemeReportEnv() {
		return r5rsEnv;
	}

	/** Sets current-input-port */
	public static void setCin(InputPort newcin) {
		cin = newcin;
	}

	/** Sets current-output-port */
	public static void setCout(OutputPort newcout) {
		cout = newcout;
	}

	/** Gets current-input-port */
	public static InputPort getCin() {
		return cin;
	}

	/** Gets current-output-port */
	public static OutputPort getCout() {
		return cout;
	}

	/**
	 * Determines if a given symbol is a keyword.
	 */
	static boolean isKeyword(Symbol s) {
		return kwSet.contains(s);
	}

	/**
	 * Determines if a given object is a variable.
	 * An object is a variable iff it is a symbol but
	 * is not a syntactic keyword.
	 */
	static boolean isVariable(Entity s) {
		return s instanceof Symbol
			; //***FIXME && !isKeyword( (Symbol) s);
	}

	/**
	 * Performs syntactic analysis of special forms.
	 * Creation date: (02/11/2001 12.34.35)
	 */
	public static void analyzeSpecialForm(Pair form) throws GleamException {
		ListIterator it = new ListIterator(form);
		if (!it.hasNext()) {
			throw new GleamException("invalid special form", form);
		}
		// analyze operator itself
		Entity op = it.next();
		it.replace(op.analyze());

		// Variable for form arguments
		Entity arg;

		// These may take no arguments
		if (op == Symbol.AND || op == Symbol.OR) {
			// analyze arguments
			while (it.hasNext()) {
				it.replace(it.next().analyze());
			}
			return;
		}

		// Other special forms have at least an argument, so check for it
		if (!it.hasNext()) {
			throw new GleamException(
				"invalid special form: too few arguments", form);
		}
		arg = it.next();

		if (op == Symbol.QUOTE || op == Symbol.QUASIQUOTE ) {
			// just one datum argument with no syntax analysis, of course!
			if (it.hasNext()) {
				throw new GleamException(
					    "quote: too many arguments", form);
			}
		}
		else if (op == Symbol.LAMBDA) {
			// analyze param list
			if (arg == EmptyList.value || isVariable(arg)) {
				// ok
				it.replace(arg.analyze());
			}
			else if (arg instanceof Pair) {
				// iterate over (possibly improper) list
				ListIterator ait = new ListIterator( (Pair) arg, true);
				java.util.Set paramSet = new java.util.HashSet();
				while (ait.hasNext()) {
					Entity pobj = ait.next();
					if (!isVariable(pobj)) {
						throw new GleamException(
							"lambda: procedure parameter is not a variable",
							form);
					}
					Symbol param = (Symbol) pobj;
					if (paramSet.contains(param)) {
						throw new GleamException(
							"lambda: repeated procedure parameter",
							form);
					}
					paramSet.add(param);
					ait.replace(pobj.analyze());
				}
			}
			else {
				throw new GleamException(
					"lambda: parameter is not a variable nor a variable list",
					form);
			}
			// analyze body
			if (!it.hasNext()) {
				throw new GleamException(
					"lambda: missing procedure body", form);
			}
			while (it.hasNext()) {
				Entity bodyPart = it.next();
				it.replace(bodyPart.analyze());
			}
		}
		else if (op == Symbol.IF) {
			// analyze condition
			it.replace(arg.analyze());
			if (!it.hasNext()) {
				throw new GleamException(
					"if: missing consequence", form);
			}
			// analyze consequence
			arg = it.next();
			it.replace(arg.analyze());
			if (it.hasNext()) {
				// analyze alternative
				arg = it.next();
				it.replace(arg.analyze());
				if (it.hasNext()) {
					throw new GleamException(
						"if: too many arguments", form);
				}
			}
		}
		else if (op == Symbol.SET) {
			if (!isVariable(arg)) {
				throw new GleamException(
					"set!: assignment object is not a variable",
					form);
			}
			if (!it.hasNext()) {
				throw new GleamException(
					"set!: missing assignment value", form);
			}
			it.replace(arg.analyze());
			// analyze assigned value
			arg = it.next();
			it.replace(arg.analyze());
			if (it.hasNext()) {
				throw new GleamException(
					    "set!: too many assignment values",
					    form);
			}
		}
		else if (op == Symbol.BEGIN) {
			// begin is followed by one or more expressions
			it.replace(arg.analyze());
			while (it.hasNext()) {
				it.replace(it.next().analyze());
			}
		}
//		else if (op == Symbol.COND) {
//		}
		else if (op == Symbol.CASE) {
		}
//		else if (op == Symbol.LET || op == Symbol.LETSTAR || op == Symbol.LETREC) {
//		}
		else if (op == Symbol.DO) {
		}
		else if (op == Symbol.DELAY) {
			// delay wants one expression
			it.replace(arg.analyze());
			if (it.hasNext()) {
				throw new GleamException(
					    "delay: too many arguments", form);
			}
		}
		else if (op == Symbol.DEFINE) {
			// analyze variable or function
			boolean isFunction;
			if (arg == EmptyList.value) {
				throw new GleamException(
					"define: invalid function name", form);
				// ok
			}
			else if (isVariable(arg)) {
				isFunction = false;
				it.replace(arg.analyze());
			}
			else if (arg instanceof Pair) {
				isFunction = true;

				// take out function name
				boolean fname = true;

				// iterate over (possibly improper) list
				ListIterator ait = new ListIterator( (Pair) arg, true);
				java.util.Set paramSet = new java.util.HashSet();

				while (ait.hasNext()) {
					Entity pobj = ait.next();
					if (!isVariable(pobj)) {
						if (fname)
							throw new
								GleamException(
								"define: procedure name is not a variable",
								form);
						else
							throw new
								GleamException(
								"define: procedure parameter is not a variable",
								form);
					}
					Symbol param = (Symbol) pobj;
					if (paramSet.contains(param)) {
						throw new GleamException(
							"define: repeated procedure parameter",
							form);
					}
					if (!fname) {
						paramSet.add(param);
					}
					else {
						fname = false;
					}
					ait.replace(pobj.analyze());
				}
			}
			else {
				throw new GleamException(
					"define: definition object is not a variable nor a procedure",
					form);
			}
			// analyze value or procedure body
			if (!it.hasNext()) {
				throw new GleamException(
					    "define: missing definition value",
					    form);
			}
			Entity v = it.next();
			it.replace(v.analyze());
			if (it.hasNext() && !isFunction) {
				throw new GleamException(
					"define: too many definition values",
					form);
			}
			else while (it.hasNext()) {
				Entity bodyPart = it.next();
				it.replace(bodyPart.analyze());
			}
		}
		else {
			gleam.util.Report.println(4, "not a special form!");
		}
	}

	/**
	 * Clones a pair
	 *
	 * @return gleam.lang.Pair
	 * @param p gleam.lang.Pair
	 */
	private static Pair clonePair(Pair p) {
		if (p == EmptyList.value)
			return p;
		else {
			Entity newcar = p.car;
			Entity newcdr = p.cdr;
			if (newcar instanceof Pair)
				newcar = clonePair( (Pair) newcar);
			if (newcdr instanceof Pair)
				newcdr = clonePair( (Pair) newcdr);
			return new Pair(newcar, newcdr);
		}
	}

	/**
	 * Performs optimization of special forms.
	 * Creation date: (14/11/2001 02.19.35)
	 */
	public static void optimizeSpecialForm(Pair form, Environment env) throws
		GleamException {
		/* We operate under the assumption that syntax analysis
		 * has already been performed, so we skip syntax checking.
		 */

		// TODO: remove clonePair!!!
		if (form.cdr instanceof Pair) {
			form.cdr = clonePair( (Pair) form.cdr);
		}

		ListIterator it = new ListIterator(form);
		// optimize operator itself
		Entity op = it.next();
		it.replace(op.optimize(env));

		// form arguments
		Entity arg;

		// These may take no arguments
		if (op == Symbol.AND || op == Symbol.OR) {
			// optimize arguments
			while (it.hasNext()) {
				it.replace(it.next().optimize(env));
			}
			return;
		}

		// Other special forms have at least an argument
		arg = it.next();

		if (op == Symbol.QUOTE) {
			// shall not touch arg, that's the whole point of quote!
		}
		else if (op == Symbol.LAMBDA) {
			// analyze param list

			/* we scan out the defines in lambda body
			 */
			Environment newEnv = createScanOutDefineEnv(form, env);

			/* then we create an augmented environment to hold
			 * the param names with undefined values
			 * for the purpose of optimization only
			 */
			Environment paramEnv = new Environment(newEnv);
			if (arg == EmptyList.value) {
				// ok (but different from Pair below)
			}
			else if (isVariable(arg)) {
				// ok, but we add it to paramEnv
				paramEnv.define( (Symbol) arg, Undefined.value);
			}
			else if (arg instanceof Pair) {
				// iterate over (possibly improper) list
				ListIterator ait = new ListIterator( (Pair) arg, true);
				while (ait.hasNext()) {
					Entity pobj = ait.next();
					paramEnv.define( (Symbol) pobj,
						Undefined.value);
				}
			}
			// optimize body in the new param environment
			// this will leave each use of the parameters
			// untouched (because their names are bound to Undefined)
			while (it.hasNext()) {
				Entity bodyPart = it.next();
				it.replace(bodyPart.optimize(paramEnv));
			}
		}
		else if (op == Symbol.SET) {
			// only optimize expression, not variable name
			it.replace(it.next().optimize(env));
		}
		else if (op == Symbol.BEGIN) {
			Environment newEnv = createScanOutDefineEnv(form, env);
			it.replace(arg.optimize(newEnv));
			while (it.hasNext()) {
				it.replace(it.next().optimize(newEnv));
			}
		}
//		else if (op == Symbol.LET) {
//			// TODO
//		}
//		else if (op == Symbol.LETSTAR) {
//			// TODO
//		}
//		else if (op == Symbol.LETREC) {
//			// TODO
//		}
		else if (op == Symbol.DO) {
			// TODO
		}
		else if (op == Symbol.DELAY) {
			// TODO
		}
		else if (op == Symbol.QUASIQUOTE) {
			// shall not touch arg, as in quote!
		}
		else if (op == Symbol.DEFINE) {
			/* in case this is a procedure
			 * we scan out the defines in lambda body
			 */
			Environment newEnv = createScanOutDefineEnv(form, env);

			/* then we create an augmented environment to hold
			 * the param names with undefined values
			 * for the purpose of optimization only
			 */
			Environment paramEnv = new Environment(newEnv);

			// optimize variable or function
			if (isVariable(arg)) {
				// ok, leave it alone
			}
			else if (arg instanceof Pair) {
				// iterate over (possibly improper) list
				ListIterator ait = new ListIterator( (Pair) arg, true);

				while (ait.hasNext()) {
					Entity pobj = ait.next();
					paramEnv.define( (Symbol) pobj,
						Undefined.value);
				}
			}
			/* optimize value or procedure body
			 *
			 * if this is a procedure:
			 * optimize body in the new param environment--
			 * this will leave each use of the parameters
			 * untouched (because their names are bound to Undefined)
			 */
			while (it.hasNext()) {
				Entity bodyPart = it.next();
				it.replace(bodyPart.optimize(paramEnv));
			}
		}
		else {
			/* Default case for:
			 *	if
			 *	cond
			 *	case
			 *
			 * Just optimize every argument.
			 */
			it.replace(arg.optimize(env));
			while (it.hasNext()) {
				arg = it.next();
				it.replace(arg.optimize(env));
			}
		}
	}

	/**
	 * Creates a new environment for all variables defined within body
	 * to hold Undefined values.
	 */
	static Environment createScanOutDefineEnv(Pair body, Environment env) throws
		GleamException {
		Pair varList = EmptyList.value;
		ListIterator it = new ListIterator(body);
		/* do a scan out for each body part,
		 * appending variables found into varList
		 */
		while (it.hasNext()) {
			Pair partialList = internalScanOut(it.next());
			ListIterator it2 = new ListIterator(partialList);
			while (it2.hasNext()) {
				Entity var = it2.next();
				varList = new Pair(var, varList);
			}
		}
		if (varList == EmptyList.value) {
			return env;
		}
		else {
			Environment retVal = new Environment(env);
			// iterate on varList, binding each var to Undefined
			gleam.util.Report.println(5, "Scanned out: ");
			ListIterator vit = new ListIterator(varList);
			while (vit.hasNext()) {
				Symbol var = (Symbol) vit.next();
				retVal.define(var, Undefined.value);
				gleam.util.Report.println(5, var);
			}
			gleam.util.Report.println(5, "...end of scan-out");
			return retVal;
		}
	}

	private static Pair internalScanOut(Entity bodyPart) throws
		GleamException {
		Pair retVal = EmptyList.value;
		if (! (bodyPart instanceof Pair))
			return retVal;

		Pair bpAsPair = (Pair) bodyPart;

		if (bpAsPair.car == Symbol.DEFINE) {
			Entity obj = ( (Pair) bpAsPair.cdr).car;
			if (obj instanceof Symbol) {
				retVal = new Pair(obj, retVal);
			}
			else if (obj instanceof Pair) {
				retVal = new Pair(
					( (Pair) obj).car, retVal);
			}
		}
		else if (bpAsPair.car == Symbol.BEGIN) {
			ListIterator it = new ListIterator( (Pair) bpAsPair.cdr);
			while (it.hasNext()) {
				Pair is = internalScanOut(it.next());
				if (is != EmptyList.value) {
					ListIterator it2
						= new ListIterator(is);
					while (it2.hasNext()) {
						retVal = new Pair(
							it2.next(), retVal);
					}
				}
			}
		}
		//
		return retVal;
	}

	/**
	 * the Gleam evaluator
	 */
	public static Entity eval(Entity expr, Environment env)
		throws gleam.lang.GleamException {
		expr = expr.analyze().optimize(env);
		Action todo = new ExpressionAction(expr, env, null);
		Continuation program = new Continuation(todo);
		return program.apply(voidList, env, new Continuation());
	}
	
	private static Pair voidList = new Pair(Void.value, EmptyList.value);

	/**
	 * Rewrites an expression once, if it is an use of a syntax rewriter.
	 *
	 * @param expr the expression to rewrite
	 * @param env the environment
	 * @return the rewritten expression, or expr itself if the expression
	 *         was not an use of a syntax rewriter. The returned expression
	 *         may be yet another use of a syntax rewriter.
	 */
	public static Entity rewrite1(Pair expr, Environment env) throws
		GleamException {
		Entity retVal = expr;
		Entity rewriter = null;
		if (expr.car instanceof Symbol) {
			rewriter = env.lookup( (Symbol) expr.car);
		}
		else if (expr.car instanceof Location) {
			rewriter = ( (Location) expr.car).get();
		}

		if (rewriter instanceof SyntaxRewriter) {
			Pair closureapp = new Pair((SyntaxRewriter) rewriter, new Pair(expr, EmptyList.makeEmptyList()));
			retVal = eval(closureapp, env);
			Report.println(2, "Rewritten as: ", retVal);
		}
		return retVal;
	}

}
