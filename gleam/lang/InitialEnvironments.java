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
 * Definition of initial environments.
 */
public final class InitialEnvironments {

	/**
	 * Can't instantiate this class
	 */
	private InitialEnvironments() {
	}

	/* The following arrays define the initial bindings.
	 * They are used initially by the System static initializers
	 * to create the first three environments.
	 *
	 * The nullEnvBindings only contains KEYWORD bindings.
	 */
	static Binding[] nullEnvBindings =
		new Binding[]
		{
			// quote
			new Binding(Binding.KEYWORD,
					"quote",
					1, 1, "Syntax.gleam_quote",
					"Gives its argument unevaluated, e.g. (quote x); 'x"),
			// lambda
			new Binding(Binding.KEYWORD,
					"lambda",
					2, Binding.VAR_ARGS, "Syntax.gleam_lambda",
					"Creates a procedure, e.g. (lambda (x) (+ x 1))"),
			// if
			new Binding(Binding.KEYWORD,
					"if",
					2, 3, "Syntax.gleam_if",
					"Conditional evaluation, e.g. (if (eqv? 1 0) 'strange 'ok)"),
			// set!
			new Binding(Binding.KEYWORD,
					"set!",
					2, 2, "Syntax.gleam_set_m",
					"Variable assignment, e.g. (set! x 11)",
					"The variable must be already bound, i.e. with define"),
			// begin
			new Binding(Binding.KEYWORD,
					"begin",
					0, Binding.VAR_ARGS, "Syntax.gleam_begin",
					"Sequential execution, e.g. (begin (first-part) (second-part))"),
//			// cond
//			new Binding(Binding.KEYWORD,
//					"cond",
//					Binding.NO_PROCEDURE),
// FIXME
//			// and
//			new Binding(Binding.KEYWORD,
//					"and",
//					"Syntax.gleam_and",
//					"Logical And operator, e.g. (and (> x 0) (< x 10))",
//					"Evaluates arguments from left to right. If a false value is found, "
//					+"it is returned. Any remaining arguments are not evaluated. The last "
//					+"argument is returned if it evaluates to a true value. "
//					+"Note that in logical context the #f constant is the false value "+
//					"while any other value, including the empty list, counts as true."),
//			// or
//			new Binding(Binding.KEYWORD,
//					"or",
//					"Syntax.gleam_or",
//					"Logical Or operator, e.g. (or (< x 0) (> x 10))",
//					"Evaluates arguments from left to right. If a true value is found, "
//					+"it is returned. Any remaining arguments are not evaluated. The last "
//					+"argument is returned if it evaluates to a false value. "
//					+"Note that in logical context the #f constant is the false value "+
//					"while any other value, including the empty list, counts as true."),
			// case
			new Binding(Binding.KEYWORD,
					"case",
					0, Binding.VAR_ARGS, Binding.NO_PROCEDURE),
//			// let
//			new Binding(Binding.KEYWORD,
//					"let",
//					Binding.NO_PROCEDURE),
//			// let*
//			new Binding(Binding.KEYWORD,
//					"let*", Binding.NO_PROCEDURE),
//			// letrec
//			new Binding(Binding.KEYWORD,
//					"letrec", Binding.NO_PROCEDURE),
			// do
			new Binding(Binding.KEYWORD,
					"do",
					0, Binding.VAR_ARGS, Binding.NO_PROCEDURE),
			// delay
			new Binding(Binding.KEYWORD,
					"delay",
					0, Binding.VAR_ARGS, Binding.NO_PROCEDURE),
			// quasiquote
			new Binding(Binding.KEYWORD,
					"quasiquote",
					1, 1, Binding.NO_PROCEDURE,
					"Gives its argument almost unevaluated, e.g. (quasiquote x); `x",
					"If a comma appears within the argument, the expression following the "
					+"comma is evaluated (\"unquoted\") and its result is inserted into "
					+"the structure instead of the comma and the expression. If a comma "
					+"appears followed immediately by an at-sign (@), then the following "
					+"expression must evaluate to a list; the opening and closing "
					+"parentheses of the list are then \"stripped away\" and the elements "
					+"of the list are inserted in place of the comma at-sign expression "
					+"sequence. (unquote x) is equivalent to ,x and (unquote-splicing x) "
					+"is equivalent to ,@x."),

			// else
			new Binding(Binding.KEYWORD,
					"else",
					0, Binding.VAR_ARGS, Binding.NO_PROCEDURE),
			// =>
			new Binding(Binding.KEYWORD,
					"=>",
					0, Binding.VAR_ARGS, Binding.NO_PROCEDURE),
			// define
			new Binding(Binding.KEYWORD,
					"define",
					2, Binding.VAR_ARGS, "Syntax.gleam_define",
					"Variable or procedure definition, e.g. (define (inc x) (+ x 1))",
					"Can be used at top-level to create a new global variable, "
					+"e.g. (define x 1); or at the beginning of a procedure body "
					+"to create a new local variable."),
			// unquote
			new Binding(Binding.KEYWORD,
					"unquote",
					1, 1, Binding.NO_PROCEDURE),
			// unquote-splicing
			new Binding(Binding.KEYWORD,
					"unquote-splicing",
					1, 1, Binding.NO_PROCEDURE),

		};

	static Binding[] r5rsEnvBindings =
		new Binding[]
		{
			// eq?
			new Binding(Binding.IDENTIFIER,
					"eq?",
					2, 2, "Equivalence.gleam_eq_p",
					"True if two objects are the same in memory, false otherwise",
					"E.g. (eq? 'a 'a) => #t, but (eq? (list 'a) (list 'a)) => #f"),
			// eqv?
			new Binding(Binding.IDENTIFIER,
					"eqv?",
					2, 2, "Equivalence.gleam_eqv_p",
					"True if two objects have equivalent values, false otherwise",
					"E.g. (eqv? 10 10) => #t"),
			// list
			new Binding(Binding.IDENTIFIER,
					"list",
					0, Binding.VAR_ARGS, "PairsAndLists.gleam_list",
					"Creates a new list from its arguments, e.g. (list 1 2 3)"),
			// cons
			new Binding(Binding.IDENTIFIER,
					"cons",
					2, 2, "PairsAndLists.gleam_cons",
					"Creates a new pair, e.g. (cons 1 (cons 2 '(3)))"),
			// car
			new Binding(Binding.IDENTIFIER,
					"car",
					1, 1, "PairsAndLists.gleam_car",
					"Gets first object in a pair, e.g. (car (list 1 2 3))"),
			// cdr
			new Binding(Binding.IDENTIFIER,
					"cdr",
					1, 1, "PairsAndLists.gleam_cdr",
					"Gets second object in a pair, e.g. (cdr (list 1 2 3))"),
			// set-car!
			new Binding(Binding.IDENTIFIER,
					"set-car!",
					2, 2, "PairsAndLists.gleam_set_car_m",
					"Sets car field in a pair, e.g. (set-car! my-pair 1)"),
			// set-cdr!
			new Binding(Binding.IDENTIFIER,
					"set-cdr!",
					2, 2, "PairsAndLists.gleam_set_cdr_m",
					"Sets cdr field in a pair, e.g. (set-cdr! my-pair 2)"),
			// append
			new Binding(Binding.IDENTIFIER,
					"append",
					0, Binding.VAR_ARGS, "PairsAndLists.gleam_append"),
			// +
			new Binding(Binding.IDENTIFIER,
					"+",
					0, Binding.VAR_ARGS, "Numbers.gleam_plus",
					"Addition, e.g (+ 1 2)"),
			// -
			new Binding(Binding.IDENTIFIER,
					"-",
					0, Binding.VAR_ARGS, "Numbers.gleam_difference",
					"Difference, e.g. (- 7 3); Also negation, e.g. (- x)" ),
			// *
			new Binding(Binding.IDENTIFIER,
					"*",
					0, Binding.VAR_ARGS, "Numbers.gleam_times",
					"Multiplication, e.g. (* 7 9)"),
			// /
			new Binding(Binding.IDENTIFIER,
					"/",
					0, Binding.VAR_ARGS, "Numbers.gleam_quotient",
					"Division, e.g. (/ 42 7)"),
			// <
			new Binding(Binding.IDENTIFIER,
					"<",
					2, Binding.VAR_ARGS, "Numbers.gleam_lt",
					"Less-than comparison, e.g. (< 1 2)"),
			// <=
			new Binding(Binding.IDENTIFIER,
					"<=",
					2, Binding.VAR_ARGS, "Numbers.gleam_lte",
					"Less-than-or-equals comparison, e.g. (<= 1 2)"),
			// >
			new Binding(Binding.IDENTIFIER,
					">",
					2, Binding.VAR_ARGS, "Numbers.gleam_gt",
					"Greater-than comparison, e.g. (> 1 2)"),
			// >=
			new Binding(Binding.IDENTIFIER,
					">=",
					2, Binding.VAR_ARGS, "Numbers.gleam_gte",
					"Greater-than-or-equals comparison, e.g. (>= 1 2)"),
			// =
			new Binding(Binding.IDENTIFIER,
					"=",
					2, Binding.VAR_ARGS, "Numbers.gleam_e",
					"Equals comparison, e.g. (= 1 1)"),
			// number?
			new Binding(Binding.IDENTIFIER,
					"number?",
					1, 1, "Numbers.gleam_number_p",
					"Returns true if argument is a number, false otherwise",
					"E.g. (number? 3) => #t"),
			// symbol?
			new Binding(Binding.IDENTIFIER,
					"symbol?",
					1, 1, "Symbols.gleam_symbol_p",
					"Returns true if argument is a symbol, false otherwise",
					"E.g. (symbol? 'sym) => #t"),
			// procedure?
			new Binding(Binding.IDENTIFIER,
					"procedure?",
					1, 1, "ControlFeatures.gleam_procedure_p",
					"Returns true if argument is a procedure, false otherwise",
					"E.g. (procedure? cons) => #t"),
			// boolean?
			new Binding(Binding.IDENTIFIER,
					"boolean?",
					1, 1, "Booleans.gleam_boolean_p",
					"Returns true if argument is a boolean, false otherwise",
					"E.g. (boolean? #f) => #t"),
			// not?
			new Binding(Binding.IDENTIFIER,
					"not",
					1, 1, "Booleans.gleam_not_p",
					"Returns true if argument is false, false otherwise",
					"E.g. (not #f) => #t"),
			// pair?
			new Binding(Binding.IDENTIFIER,
					"pair?",
					1, 1, "PairsAndLists.gleam_pair_p",
					"Returns true if argument is a pair, false otherwise",
					"E.g. (pair? (cons 1 2)) => #t"),
			// string?
			new Binding(Binding.IDENTIFIER,
					"string?",
					1, 1, "Strings.gleam_string_p",
					"Returns true if argument is a string, false otherwise",
					"E.g. (string? \"hello\") => #t"),
			// null?
			new Binding(Binding.IDENTIFIER,
					"null?",
					1, 1, "PairsAndLists.gleam_null_p",
					"Returns true if argument is the empty list, false otherwise",
					"E.g. (null? '()) => #t"),
			// char?
			new Binding(Binding.IDENTIFIER,
					"char?",
					1, 1, "Characters.gleam_char_p",
					"Returns true if argument is a character, false otherwise",
					"E.g. (char? #\\a) => #t"),
			// port?
			new Binding(Binding.IDENTIFIER,
					"port?",
					1, 1, "Ports.gleam_port_p",
					"Returns true if argument is a port, false otherwise",
					"E.g. (port? (current-input-port)) => #t"),
			// input-port?
			new Binding(Binding.IDENTIFIER,
					"input-port?",
					1, 1, "Ports.gleam_input_port_p",
					"Returns true if argument is an input port, false otherwise",
					"E.g. (input-port? (current-input-port)) => #t"),
			// output-port?
			new Binding(Binding.IDENTIFIER,
					"output-port?",
					1, 1, "Ports.gleam_output_port_p",
					"Returns true if argument is an output port, false otherwise",
					"E.g. (output-port? (current-input-port)) => #f"),
			// current-input-port
			new Binding(Binding.IDENTIFIER,
					"current-input-port",
					0, 0, "Ports.gleam_current_input_port",
					"Returns the current input port"),
			// current-output-port
			new Binding(Binding.IDENTIFIER,
					"current-output-port",
					0, 0, "Ports.gleam_current_output_port",
					"Returns the current output port"),
			// load
			new Binding(Binding.IDENTIFIER,
					"load",
					1, 1, "SystemInterface.gleam_load",
					"Loads and executes a source file"),
			// eof-object?
			new Binding(Binding.IDENTIFIER,
					"eof-object?",
					1, 1, "Input.gleam_eof_object_p",
					"Returns true if argument is the EOF object, false otherwise"),
			// read
			new Binding(Binding.IDENTIFIER,
					"read",
					0, 1, "Input.gleam_read",
					"Reads an object from the current or specified input port"),
			// display
			new Binding(Binding.IDENTIFIER,
					"display",
					1, 2, "Output.gleam_display",
					"Writes an object in human-readable form, e.g. (display \"hello\")"),
			// write
			new Binding(Binding.IDENTIFIER,
					"write",
					1, 2, "Output.gleam_write",
					"Writes an object in machine-readable form, e.g. (write \"hello\")"),
			// newline
			new Binding(Binding.IDENTIFIER,
					"newline",
					0, 1, "Output.gleam_newline",
					"Writes an end of line to the current or specified output port"),
			// call-with-current-continuation
			new Binding(Binding.IDENTIFIER,
					"call-with-current-continuation",
					1, 1, "ControlFeatures.gleam_callcc",
					"Calls a procedure with an escape procedure arg.",
					"Also known as call/cc, this operator is both unusual and powerful.\n"+
					"A simple usage pattern of call/cc is to implement exception handling."),
			// eval
			new Binding(Binding.IDENTIFIER,
					"eval",
					1, 2, "Eval.gleam_eval",
					"Evaluates an expression in a given environment",
					"E.g. (eval '(+ 1 2) (interaction-environment)) => 3"),
			// null-environment
			new Binding(Binding.IDENTIFIER,
					"null-environment",
					1, 1, "Eval.gleam_null_environment",
					"Returns a specifier for the null environment",
					"A scheme-report version number must be specified, e.g. (null-environment 5). "+
					"Currently supported versions are 4 and 5"),
			// scheme-report-environment
			new Binding(Binding.IDENTIFIER,
					"scheme-report-environment",
					1, 1, "Eval.gleam_scheme_report_environment",
					"Returns a specifier for the scheme-report environment",
					"A scheme-report version number must be specified, e.g. (scheme-report-environment 5). "+
					"Currently supported versions are 4 and 5"),
			// interaction-environment
			new Binding(Binding.IDENTIFIER,
					"interaction-environment",
					0, 0, "Eval.gleam_interaction_environment",
					"Returns a specifier for the interaction environment"),
			};

	static Binding[] intrEnvBindings =
		new Binding[]
		{
//			// __errobj
//			new Binding(Binding.KEYWORD,
//					"__errobj",
//					Binding.NO_PROCEDURE),
			// help
			new Binding(Binding.IDENTIFIER,
					"help",
					0, 1, "Interaction.gleam_help",
					"Gives a short help on a primitive, e.g. (help 'if)"),
			// verbosity
			new Binding(Binding.IDENTIFIER,
					"verbosity",
					1, 1, "Interaction.gleam_verbosity",
					"Sets verbosity level, 1=standard 2=pedantic, e.g. (verbosity 2)"),
			// save-session
			new Binding(Binding.IDENTIFIER,
					"save-session",
					1, 1, "Interaction.gleam_save_session",
					"Saves current session environment, e.g. (save-session \"file\")"),
			// load-session
			new Binding(Binding.IDENTIFIER,
					"load-session",
					1, 1, "Interaction.gleam_load_session",
					"Loads current session environment, e.g. (load-session \"file\")"),
			// make-rewriter
			new Binding(Binding.IDENTIFIER,
					"make-rewriter",
					1, 1, "Syntax.gleam_make_rewriter",
					"Makes a syntax rewriter, e.g. (make-rewriter (lambda (exp) ...))"),
			// make-rewriter
//			new Binding(Binding.KEYWORD, // FIXME
//					"rewrite1",
//					"Syntax.gleam_rewrite1",
//					"Rewrites an expression applying a syntax rewriter at most once"),
			// generate-symbol
			new Binding(Binding.IDENTIFIER,
					"generate-symbol",
					0, 0, "Symbols.gleam_generate_symbol",
					"Makes a new symbol, e.g. (generate-symbol)"),
			// new
			new Binding(Binding.IDENTIFIER,
					"new",
					1, Binding.VAR_ARGS, "JavaInterface.gleam_new",
					"Makes a new Java object, e.g. (new 'java.util.Date)"),
			// call
			new Binding(Binding.IDENTIFIER,
					"call",
					2, Binding.VAR_ARGS, "JavaInterface.gleam_call",
					"Calls a method on a Java object, e.g. (call 'length (new 'java.lang.String \"test\"))"),
		};
}

