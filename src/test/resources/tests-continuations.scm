;;;;; tests.scm
;;;;;
;;;;; (c) 2001-2022 Guglielmo Nigri <guglielmonigri@yahoo.it>.
;;;;; Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
;;;;; welcome to redistribute it under certain conditions; see LICENSE.TXT.

;;; new continuation tests:

(display "tests-continuations.scm\n")


(define (write-my-args . x) display (cons 'MY-ARGS x))

(define (arg x) (begin (display x) x))

(assert-value-output "arg"
    5.0
    "5.0"
    (arg 5)
)

(assert-output "write-my-args"
    '(MY-ARGS 1 2 3)
    "(MY-ARGS 1 2 3)"
    (write-my-args 1 2 3)
)

(define con2 #f)

(assert "write-my-args with a call/cc"
    `(MY-ARGS 1 2 3 ,void 4 5 6)
    (write-my-args 1 2 3 (call-with-current-continuation (lambda (x) (set! con2 x) ) ) 4 5 6 )
)

(assert "con2 -1"
    '(MY-ARGS 1 2 3 -1 4 5 6)
    (con2 -1)
)

(assert-output "con2 -2"
    "(MY-ARGS 1 2 3 -2 4 5 6)"
    (con2 -2)
)

(assert-value-output "arg + call/cc"
    `(MY-ARGS 1.0 2.0 3.0 ,void 4.0 5.0 6.0)
    "1.02.03.04.05.06.0"
    (write-my-args (arg 1) (arg 2) (arg 3)
        (call-with-current-continuation (lambda (x) (set! con2 x) ) )
        (arg 4) (arg 5) (arg 6) )
)

(assert-value-output "continuation with argument"
    "4.05.06.0"
    `(MY-ARGS 1.0 2.0 3.0 -7.0 4.0 5.0 6.0)
    (con2 -7)
)

;;;;;;;;

(define d1 (new 'java.util.Date))

(define d2 d1)

(assert "java objects eq?"
    #t
    (eq? d2 d1)
)

(assert "java objects eqv?"
    #t
    (eqv? d2 d1)
)


(define s1 (new 'java.lang.String 'test))

(define s2 (new 'java.lang.String 'test))

(assert "java strings eq?"
    #f
    (eq? s2 s1)
)

(assert "java strings eqv?"
    #t
    (eqv? s2 s1)
)

(define a 12)

(assert "eval in current environment"
    12.0
    (in-environment (current-environment) a)
)

;(in-environment (scheme-report-environment 5) a)
;;*** Unbound variable: a

;(in-environment (null-environment 5) a)
;;*** Unbound variable: a

(assert "define in null-env 1"
    12.0
    (begin
        (in-environment (null-environment 5) (define a 77) )
        a
    )
)

(assert "define in null-env 2"
    77.0
    (in-environment (null-environment 5) a)
)

;;;;;;;; side-effects

(define
  (add3 a b c)
    (display #\newline)
    (+ a b c))

(define
  (op)
    (display " op ")
    add3)

(define
 (one)
   (display " one ")
   1)

(define
 (two)
   (display " two ")
   2)

(define
 (three)
   (display " three ")
   3)

(assert-value-output "side effects"
    6.0
    " one  two  three  op"
    ((op) (one) (two) (three))
)
