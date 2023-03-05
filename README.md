# Gleam Scheme Interpreter

(c) 2001-2023 Guglielmo Nigri
(`guglielmonigri at yahoo.it`, `googlielmo at gmail.com`)

Gleam comes with ABSOLUTELY NO WARRANTY. This is free software, and you are
welcome to redistribute it under certain conditions; see LICENSE.TXT.

## Abstract

Gleam is a Scheme language interpreter written in Java.

Project goals: to support the discovery of Scheme for beginners, simplicity,
R5RS compliance, full integration with the Java platform (call Java from Scheme
and vice versa).

## Contents

- [How to build and run Gleam](#how-to-build-and-run-gleam)
- [Project history](#project-history)
- [Programming in Gleam Scheme](#programming-in-gleam-scheme)
- [Using Java from Scheme](#using-java-from-scheme)
- [Using Scheme from Java](#using-scheme-from-java)

### Other docs in this repository

- [Changes](CHANGES.TXT)
- [Contributing](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [GNU General Public License Version 2](LICENSE.TXT)
- [R5RS implementation status](STATUS.TXT) _(may be out of date)_
- [Backlog / TODO list](TODO.TXT) _(may be out of date)_

---

## How to build and run Gleam

See [BUILD.md](BUILD.md).

## Project history

I started this project in 2001 while looking for a Lisp interpreter to add
scripting capabilities to Java programs.

I learned Lisp (and liked it a lot) during my university days in Pisa, Italy,
and I thought I could augment Java with Lisp. It was around that time that I
discovered Scheme, and I was instantly fascinated by the simplicity and elegance
of the language. I found out that there were already some good Java
implementations of Scheme out there, but, you know, I have to try my hand at
something to grok it! :-)

So I started my simple, slow implementation of an interpreter, always looking at
the R5RS document for reference. One of the first things that I was curious to
implement was first-class continuations. I wondered if there was a simple way to
do those in Java. In fact, this being an interpreter, it was relatively simple
to implement them. For a compiler, it would be trickier. We'll see in version
2 ;-)

## Programming in Gleam Scheme

First, a word of caveat! Please note that Gleam is still being developed and
does not yet fully support the Scheme language as defined in R5RS. This means
that some key features, such as support for strings and arrays, are not yet
fully implemented, and hygienic macros are still in their early stages of
development. However, you can invoke Java from Scheme to work around some of
these limitations (see below). Although it is possible to write non-trivial
programs with Gleam today, it can be a significant undertaking.

If you're interested in Gleam and have the time to invest, you can also
contribute to its development! Check out [CONTRIBUTING.md](CONTRIBUTING.md) and
the next sections to learn how.

## Using Java from Scheme

Gleam provides a convenient way to create new Java objects and call methods on
existing ones from within Scheme. To create a new Java object, use the `new`
function with a fully-qualified class name as the first argument, followed by
the constructor arguments (if any).

For example, to create a new `java.util.Date` object, you can use the following
code:

    (define now (new 'java.util.Date))

To call a method on an existing Java object, use the `call` function with the
method name and the object instance as arguments, followed by the method
arguments (if any).

Here's an example that demonstrates how to call the `toString` method on
the `now` object created earlier:

    (call 'toString now)        ;; "Sun Feb 12 20:26:44 CET 2023"

These features can help you tap into a wide range of libraries and tools
available on the JVM. Here are some more examples:

    ; Create a new object of the java.awt.Point class
    (define p (new 'java.awt.Point 1 2))

    ; Call the setLocation method on the object, passing in two arguments
    (call 'translate p 10 -5)

    ; Call the getLocation method on the object to retrieve its coordinates
    (call 'getLocation p)       ;; java.awt.Point[x=11,y=-3]

By using these functions, you can seamlessly integrate Java and Scheme code,
allowing you to take advantage of both languages' strengths.

## Using Scheme from Java

Interacting with Gleam from Java is simple and can be done in two ways. You can
use an `Interpreter` instance directly or leverage the Java Scripting API
defined by JSR 223. In the next sections, we'll take a quick look at both of
these options.

### Using the Gleam Interpreter

To execute Scheme code, you need an Interpreter instance. You can create one
with:

    Interpreter intp = Interpreter.newInterpreter();

You can create as many interpreters as needed, each with a separate internal
status.

If desired, you can also set up a global environment that will be shared among
multiple interpreter instances. Check out the chapter below, "Using the Java
Scripting API," for further details.

Several flavors of `eval()` let you evaluate Scheme expressions.

    intp.eval("(define x 1.2345)");
    Entity res = intp.eval("x");
    boolean eq = 1.2345 == ((Number) res).doubleValue(); // true

All Scheme objects implement the `Entity` interface. Additionally, numerical
data extends the standard Java `Number` class.

Calling `toString()` on an `Entity` returns a string in a format identical to
what you'd expect when using the Scheme `display` procedure:

    String s = intp.eval("(cdr (list 1 2 3 4))").toString(); // (2 3 4)

To get a string in the same format as used by `write`, use the `toWriteFormat()`
method:

    String d = intp.eval("#\\space").toString();      //  (one space)
    String w = intp.eval("#\\space").toWriteFormat(); // #\space

By default, interactive code, or code submitted to `eval`, is executed in the
session environment, which includes an execution context that holds the default
I/O ports (i.e., _in_, _out_, and _error_ character streams) along with some
special properties. One such property is the `noisy` flag, which, when set to
true, causes the result of each top-level expression to be displayed after
evaluation.

Creating an interactive REPL is simple and requires just a few lines of code to
evaluate expressions from a `java.io.Reader`:

    ExecutionContext context = intp.getSessionEnv().getExecutionContext();
    context.setNoisy(true);
    context.getOut().printf("Welcome to Gleam Scheme!\nSend EOF (^D) to quit\n");
    Reader reader = context.getIn().getReader();
    while (true) {
        try {
            intp.eval(reader);
            break;
        }
        catch (GleamException e) {
            context.getOut().printf("Error: %s\n", e.getMessage());
        }
    }

For your convenience, you can import the static methods defined in
the `Entities` class, such as `cons`, `nil`, and `list`, to create and
manipulate Scheme values more easily. As an example, let's look at a couple of
different ways for constructing a list with three elements: a multiplication
symbol, a real number, and an integer:

    // with cons() and nil()
    List list1 = cons(symbol("*"),
                      cons(real(1.234),
                           cons(integer(2), nil())));           // (* 1.234 2)
    // with list()
    List list2 = list(symbol("*"), real(1.234), integer(2));    // (* 1.234 2)

The resulting lists are identical, and when evaluated with `eval()`, they would
yield a value of `2.468`.

### Using the Java Scripting API

To use the JSR 223 Java Scripting API, ensure that you have `gleam-$VERSION.jar`
in your classpath, then include the following code in your program:

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("gleam");

This creates a `ScriptEngine` that allows you to execute Gleam Scheme code.

You can evaluate a Scheme snippet from a string with `eval`, like this:

    Object value = engine.eval("(+ 2 40)");
    double asDouble = ((Number) value).doubleValue(); // 42.0

Entities returned from Scheme are converted, if necessary, to plain Java objects
such as `String`, `Integer`, or `Double` instances. `Pair` and `EmptyList`
values are converted to the `List` interface. Void or undefined values are
returned as `null`.

The scopes that bind string names to Java objects are represented by
the `Bindings` interface, which is a special kind of Map<String, Object>.

Each engine executes code within a specific `ScriptContext`, which includes the
engine Bindings and the default I/O character streams.

Engines can access both a local engine scope (local to each engine) and a global
one (shared by engines created by the same engine manager).

You can directly manipulate engine scope attributes (i.e., variables)
with `put()` and `get()` methods like this:

    engine.put("attr", 40);
    Object value = engine.eval("(+ 2 attr)");   // 42

You can also get or set attributes via the engine context, which is useful when
you want to manipulate the global scope, for example:

    engine.getContext().setAttribute("global", 30, GLOBAL_SCOPE);
    engine.getContext().setAttribute("local", 12, ENGINE_SCOPE);
    Object value = engine.eval("(+ global local)");   // 42

Implementing a minimal REPL using the Java Scripting API requires only a few
lines of code:

    ScriptContext context = engine.getContext();
    context.setAttribute(CONTEXT_ATTR_NOISY, true, ENGINE_SCOPE);
    PrintWriter writer = new PrintWriter(context.getWriter(), true);
    writer.printf("Welcome to Gleam Scheme!\nSend EOF (^D) to quit\n");
    Reader reader = context.getReader();
    while (true) {
        try {
            engine.eval(reader);
            break;
        }
        catch (ScriptException e) {
            writer.printf(String.format("Error: %s\n", e.getMessage()));
        }
    }

You can set the `noisy` and `traceEnabled` properties in the
underlying `ExecutionContext` with the special attributes `CONTEXT_ATTR_NOISY`
and `CONTEXT_ATTR_TRACE_ENABLED`.
