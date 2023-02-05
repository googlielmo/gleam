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
- Programming in Gleam Scheme [TBD]
- Using Java from Scheme [TBD]
- Using Scheme from Java [TBD]

### Other docs in this repository

- [Changes](CHANGES.TXT)
- [Current status](STATUS.TXT)
- [Backlog / TODO list](TODO.TXT)
- [Contributing](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [GNU General Public License Version 2](LICENSE.TXT)

---

## How to build and run Gleam

See [BUILD.md](BUILD.md).

## Project history

I started this project in 2001 while looking for a Lisp interpreter to add
scripting capabilities to Java programs.

I learned Lisp (and liked it a lot) during my university days in Pisa,
Italy, and I thought I could augment Java with Lisp. It was around that time
that I discovered Scheme, and I was instantly fascinated by the simplicity
and elegance of the language.
I found out that there were already some good Java implementations of Scheme
out there, but, you know, I have to try my hand at something to grok it! :-)

So I started my simple, slow implementation of an interpreter, always
looking at the R5RS document for reference. One of the first things that I
was curious to implement was first-class continuations. I wondered if there
was a simple way to do those in Java.
In fact, this being an interpreter, it was relatively simple to implement them.
For a compiler, it would be trickier. We'll see in version 2 ;-)
