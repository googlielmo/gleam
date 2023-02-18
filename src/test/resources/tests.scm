;;;;; tests.scm
;;;;;
;;;;; (c) 2001-2022 Guglielmo Nigri <guglielmonigri@yahoo.it>.
;;;;; Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
;;;;; welcome to redistribute it under certain conditions; see LICENSE.TXT.
;;;;;

;;; This file contains basic system tests

(display "tests.scm\n")

;;;
;;; basic tests:
;;;

(assert "a number"
    1
    1
)

(assert "false"
    #f
    #f
)

(assert "(+)"
    0
    (+)
)

(assert "(+ (+ 1))"
    1
    (+ (+ 1))
)

(assert "(quote (a b c))"
    '(a b c)
    (quote (a b c))
)

(assert "'''(a b c d)"
 (quote ''(a b c d))
 '''(a b c d)
)

(assert "define function inc"
    void
    (define inc (lambda (x) (+ 1 x) ) )
)

(assert "inc"
    11
    (inc 10)
)

(define (inc2 x) (+ 2 x))

(assert "inc2"
    12
    (inc2 10)
)

;;; stack fill test

(define (fill x)
    (if (eqv? x 0)
            0
        ;else
            (* 1 (fill (- x 1)))))

(assert "going down the stack"
    0
    (fill 100000)
)

;(assert "down the rabbit hole"
;    0 ; => ...or OutOfMemory Error
;    (fill 50000000)
;)

;;; tail recursion test
;;; recursive loop -> iterative loop

(define x #f)

(define (loop z)
  (if (eqv? z 0)
    'done
    (begin
      (set! x z)
      (loop (- z 1)))))

(assert "tail recursion (quick)"
    'done
    (loop 100000)
)

(assert "tail recursion (slow)"
    'done
    (loop 2000000)
)

;;;
;;; continuation tests:
;;;

(define param #f)

(define con1 #f)

(assert-output "call/cc with write"
    "\"salut\""

    ((call/cc (lambda (x) (set! con1 x) (if param display write)) ) "salut")
)

(assert-output "call/cc with display"
    "salut"
    (con1 display))

(define (strange-display x) (display "---") (display x) (display "---") )

(assert-output "strange-display"
    "---hola---"
    (strange-display "hola")
)

(assert-output "passing strange-display to continuation"
    "---salut---"
    (con1 strange-display)
)

;; interesting continuation test:
;;

(define some-flag #f)

(define (my-abortable-proc escape-proc)
  (display "in my-abortable-proc ")
  (if some-flag
      (escape-proc "ABORTED "))
  (display "still in my-abortable-proc ")
  "NOT ABORTED ")

(define (my-resumable-proc)
  (display "do-something... ")
  (display (call-with-current-continuation my-abortable-proc))
  (display "...all done "))

(assert-output "my-resumable-proc not aborted"
    "do-something... in my-abortable-proc still in my-abortable-proc NOT ABORTED ...all done"
    (my-resumable-proc)
)

(define some-flag #t)

(assert-output "my-resumable-proc (aborting this test)"
    "do-something... in my-abortable-proc ABORTED ...all done"
    (my-resumable-proc)
)
