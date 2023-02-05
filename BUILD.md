Gleam Scheme Interpreter
========================

(c) 2001-2023 Guglielmo Nigri
(`guglielmonigri at yahoo.it`, `googlielmo at gmail.com`)

Gleam comes with ABSOLUTELY NO WARRANTY. This is free software, and you are
welcome to redistribute it under certain conditions; see LICENSE.TXT.

HOW TO BUILD GLEAM
------------------

The Gleam distribution consists of two jar archives:

- `gleam-$VERSION.jar` the interpreter library
- `repl-$VERSION.jar` the interactive interpreter, or read-eval-print loop (REPL)

where `$VERSION` is set to match the project version in pom.xml.

Create the jars with Maven, using the provided wrapper:

    $ ./mvnw -f gleam-all clean package

On success, you'll find the `gleam-$VERSION.jar` archive under the `target` directory, and
`repl-$VERSION.jar` under `repl/target`.

To run the REPL, execute:

    $ java -cp ./target/gleam-$VERSION.jar:./repl/target/repl-$VERSION.jar gleam.repl.Gleam

Alternatively, execute:

    $ ./mvnw -f gleam-all -Pexec install

With this special profile (`-Pexec`) a successful install will drop you in the REPL.
Exit the interactive interpreter to conclude the Maven build.

CREDITS
-------

The old Ant `build.xml` was kindly provided by Jon Rafkind `<workmin at ccs.neu.edu>`.
Thank you, Jon!