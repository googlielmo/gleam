;;;;; tests.scm
;;;;;
;;;;; (c) 2001-2022 Guglielmo Nigri <guglielmonigri@yahoo.it>.
;;;;; Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
;;;;; welcome to redistribute it under certain conditions; see LICENSE.TXT.

;;;
;;; test utilities:
;;;

(display "test-utilities.scm\n")

(define first car)
(define second cadr)
(define third caddr)
(define fourth cadddr)

;(define undefined undefined)

;(define (equal? x y)
;     (if (and (pair? x) (pair? y))
;            (and (equal? (car x) (car y))
;                  (equal? (cdr x) (cdr y)))
;         ;else
;             (eqv? x y)))

(define void (let ((x 1)) (begin (set! x 2))))

;(define stop #f)
;(call/cc (lambda (x) (set! stop x)))

(define (test-head name)
    (flush) (display "\nRunning test: ")
    (display name) (display "...") (flush))

(define (test-body expected actual)
    (if (equal? expected actual)
                (begin (display "OK\n"))
            ;else
                (begin (display "FAILED: expecting ")
                       (display expected)
                       (display " but was ")
                       (display actual)
                       (display "\n")
                       (flush))))

;Usage: (assert name expected actual)
(_defmacro (assert args)
    `(begin
        (test-head ,(first args))
        (test-body ,(second args) ,(third args))))

;Usage: (assert-value-output name expected-val expected-out actual-val)
(_defmacro (assert-value-output args)
    `(begin
        (test-head ,(first args))
        (flush) (display "\nExpected & actual output: \n")
        (flush) (display ,(third args)) (newline) (flush)
        (define actual ,(fourth args))
        (newline) (flush)
        (test-body ,(second args) actual)
       ))

;Usage: (assert-output name expected actual)
(_defmacro (assert-output args)
    `(assert-value-output ,(first args) void ,(second args) ,(third args)))
