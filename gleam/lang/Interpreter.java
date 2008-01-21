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
	
	/** the current program continuation */
	Continuation cont;
	
	/** the accumulator register */
	Entity accum;
	
	/**
	 * the session (top-level) environment;
	 * can be changed by the application.
	 */
	Environment sesnEnv = null;

	/**
	 * true if bootstrap code already loaded
	 */
	private static boolean bootstrapped = false;

	/**
	 * Creates a new instance of Interpreter
	 * @throws gleam.lang.GleamException on any error
	 */
	public Interpreter() throws GleamException {
		cont = new Continuation();
		accum = Void.value;
		/* define session environment */
		setSessionEnv(new Environment(null));
		bootstrap();
	}

	/**
	 * Evaluates a Gleam Scheme expression in the current session environment
	 * @param expr a String holding an arbitrary Scheme expression
	 * @return the value of the expression
	 * @throws gleam.lang.GleamException as soon as an error condition is 
	 *	raised, the loading / execution operation will terminate, 
	 *	leaving the session environment in a possibly modified state
	 */
	public Entity eval(String expr) throws GleamException {
		return eval(new java.io.StringReader(expr));
	}

	/**
	 * Evaluates a Gleam Scheme program in the current session environment
	 * @param reader a <CODE>java.io.Reader</CODE> representing the program 
	 *	stream
	 * @return the return value of the program
	 * @throws gleam.lang.GleamException on any error
	 */
	public Entity eval(java.io.Reader reader) throws GleamException {
		load(new InputPort(reader), sesnEnv);
		return accum;
	}

	/**
	 * Evaluates a Gleam Scheme entity in a given environment
	 * @param expr the <CODE>gleam.lang.Entity</CODE> corresponding to a 
	 *	Scheme expression to evaluate
	 * @param env the environment of evaluation
	 * @throws gleam.lang.GleamException on any error
	 * @return the value of the expression
	 */
	public Entity eval(Entity expr, Environment env) throws GleamException {
		expr = expr.analyze().optimize(env);
		cont.extend(new ExpressionAction(expr, env, null));
		execute();
		return accum;
	}

	/**
	 * Replaces the current continuation with a new one.
	 * This method is used to implement the evaluation of continuations.
	 * Equivalent to a <CODE>goto</CODE> instruction.
	 * @param cont the new current continuation for this <CODE>Interpreter</CODE>
	 */
	public void replaceContinuation(Continuation cont) {
		// in-place copy
		this.cont.action = cont.action;
	}

	/**
	 * The main loop of program execution. 
	 * When this method is called, the first action in the current 
	 * continuation is invoked with the current value of the accumulator 
	 * register as its argument. When a result is produced, it is stored in 
	 * the accumulator. Then the next action in the continuation chain is 
	 * extracted, and the loop repeats itself until there are no more 
	 * actions to execute.
	 * @throws gleam.lang.GleamException on any error
	 */
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

	/**
	 * Loads and executes a Gleam Scheme program from a stream
	 * @param reader a <CODE>gleam.lang.InputPort</CODE> representing the 
	 *	program stream
	 * @param env the environment for program execution
	 * @throws gleam.lang.GleamException as soon as an error condition is 
	 *	raised, the loading / execution operation will terminate, 
	 *	leaving the environment in a possibly modified state
	 */
	public void load(InputPort reader, Environment env) throws GleamException {
		// read
		Entity obj, val;
		while ((obj = reader.read()) != Eof.makeEof()) {
			// eval
			gleam.util.Log.record(4, "load: read object", obj);
			val = eval(obj, env);
			gleam.util.Log.record(4, "load: result is", val);
		}
	}

	/**
	 * Loads and executes the bootstrap code for the Gleam Scheme Interpreter.
	 * @throws gleam.lang.GleamException on any error
	 */
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
	 * @param env the new session environment
	 */
	public void setSessionEnv(Environment env) {
		sesnEnv = env;
		sesnEnv.setInterpreter(this);
		// force link to interaction env
		sesnEnv.parent = System.getInteractionEnv();
	}

	/**
	 * Gets the current session environment
	 * @return the current session environment
	 */
	public Environment getSessionEnv() {
		return sesnEnv;
	}

	/**
	 * Binds a Java object in the current session environment.
	 * A variable with the given name will be bound to the given Java object
	 * in the current session environment
	 * @param name the variable name for the object
	 * @param object the <CODE>java.lang.Object</CODE> to bind to the given 
	 *	name
	 */
	public void bind(String name, java.lang.Object object) {
		sesnEnv.define(Symbol.makeSymbol(name), new JavaObject(object));
	}
}
