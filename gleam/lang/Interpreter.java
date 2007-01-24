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

/*
 * Interpreter.java
 *
 * Created on 6-jan-2007, 11.05
 *
 */

package gleam.lang;

/**
 * The Gleam Scheme Interpreter
 */
public class Interpreter {
	
	/** the continuation */
	Continuation cont;
	
	/** the accumulator */
	Entity accum;
	
	/**
	 * the session (top-level) environment;
	 * can be changed by the application.
	 */
	Environment sesnEnv = null;

	private static boolean bootstrapped = false;

	/** Creates a new instance of Interpreter */
	public Interpreter() throws GleamException {
		cont = new Continuation();
		accum = Void.value;
		/* define session environment */
		setSessionEnv(new Environment(null));
		bootstrap();
	}

	public Entity eval(String expr) throws GleamException {
		return eval(new java.io.StringReader(expr));
	}

	public Entity eval(java.io.Reader reader) throws GleamException {
		load(new InputPort(reader), sesnEnv);
		return accum;
	}

	public Entity eval(Entity expr, Environment env) throws GleamException {
		expr = expr.analyze().optimize(env);
		cont.extend(new ExpressionAction(expr, env, null));
		execute();
		return accum;
	}

	public void replaceContinuation(Continuation cont) {
		// in-place copy
		this.cont.action = cont.action;
	}

	private void execute() throws GleamException {
		Action currentAction = cont.action;
		Entity tmp;
		while (currentAction != null) {
			tmp = currentAction.invoke(accum, cont);
			if (tmp != null) {
				accum = tmp;
			}
			currentAction = cont.action;
		}
	}

	public void load(InputPort reader, Environment env) throws GleamException {
		// read
		Entity obj, val;
		while ((obj = reader.read()) != Eof.makeEof()) {
			// eval
			gleam.util.Report.println(4, "load: read object");
			gleam.util.Report.println(4, obj);
			val = eval(obj, env);
			gleam.util.Report.println(4, "load: result is");
			gleam.util.Report.println(4, val);
		}
	}

	synchronized private void bootstrap() throws GleamException {
		if (!bootstrapped) {
			gleam.lang.InputPort bootstrap = 
				new gleam.lang.InputPort(
					new java.io.BufferedReader(
						new java.io.InputStreamReader(
							getClass().getResourceAsStream("/bootstrap.scm"))));
			// FIXME the bootstrap takes place in the interaction environment 
			// instead of r5rs, which is wrong, but we need make-rewriter.
			// I should solve this problem, maybe using set! on preallocated
			// r5rs names
			load(bootstrap, gleam.lang.System.getInteractionEnv());
			bootstrapped = true;
		}
	}

	/**
	 * Sets the current session environment.
	 */
	public void setSessionEnv(Environment env) {
		sesnEnv = env;
		sesnEnv.setInterpreter(this);
		// force link to interaction env
		sesnEnv.parent = System.getInteractionEnv();
	}

	/**
	 * Gets the current session environment
	 */
	public Environment getSessionEnv() {
		return sesnEnv;
	}

}
