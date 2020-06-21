# Gleam Scheme Interpreter

(c) 2001-2020 Guglielmo Nigri <guglielmonigri@yahoo.it>.
Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
welcome to redistribute it under certain conditions; see LICENSE.TXT.

## Abstract

Gleam is a simple Scheme language interpreter written in Java. 

Project goals: to support the discovery of Scheme for beginners, simplicity, 
R5RS compliance, full integration with the Java platform (call Java from Scheme 
and vice versa).

## Contents

- [Project background](#Project background)
- [Programming in Gleam Scheme]() 'TODO'
- [Using Java from Scheme]()
- [Using Scheme from Java]()

### Other docs in this repository

- [Changes](CHANGES.TXT)
- [Building and running Gleam Scheme](BUILD.TXT)
- [Current status](STATUS.TXT)
- [Backlog / TODO list](TODO.TXT)
- [Contributing](CONTRIBUTING.md)

---

## Project background
This is a project that I started in 2001 while looking for a Lisp interpreter to
give scripting capabilities to Java programs. I already knew Lisp (and liked it
a lot) from my university days in Pisa, Italy. Then I discovered Scheme, and I
was instantly fascinated by the simplicity and elegance of the language. I found
out that there were already some good Java implementations of Scheme, but, you
know, I have to try my hand at something to really understand it ;-) So I
started my simple, slow implementation of an interpreter, always looking at the
R5RS document for reference. One of the first things that I was curious to
implement were first-class continuations. I wondered if there was a simple way
to do those in Java. In fact, this being an interpreter, it was relatively easy
to emulate them. For a compiler I think it would be quite tricky. We'll see when
we reach version 2 ;-)
