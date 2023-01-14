Gleam Scheme Interpreter
========================

(c) 2001-2023 Guglielmo Nigri
(guglielmonigri at yahoo.it, googlielmo at gmail.com)

Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
welcome to redistribute it under certain conditions; see LICENSE.TXT.

HOW TO BUILD GLEAM
------------------

The Gleam distribution consists of two jar archives:

 - `gleam-VERSION.jar` the interpreter library
 - `repl-VERSION.jar` the interactive interpreter, or read-eval-print loop (REPL)

Create the jars with Maven:

    $ mvn -f gleam-all clean package

On success, you'll find the `gleam-VERSION.jar` archive under the `target` directory, and
`repl-VERSION.jar` under `repl/target`.

To run the REPL, execute:

    $ java -cp ./target/gleam-VERSION.jar:./repl/target/repl-VERSION.jar gleam.repl.Gleam

Alternatively, execute:

    $ mvn -f gleam-all clean integration-test

This will compile the modules and drop you in the REPL for the integration-test phase.
Exit the interactive interpreter to conclude the Maven build.

CREDITS
-------

The old Ant build.xml was kindly provided by Jon Rafkind <workmin@ccs.neu.edu>.
Thank you Jon!