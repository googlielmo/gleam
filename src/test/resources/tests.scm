;;;;; tests.scm
;;;;;
;;;;; (c) 2001-2007 Guglielmo Nigri <guglielmonigri@yahoo.it>.
;;;;; Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
;;;;; welcome to redistribute it under certain conditions; see LICENSE.TXT.
;;;;;
;;;;; This file will contain basic system tests

;;;
;;; basic tests:
;;;

1
; 1.0

#f
; #f

(+)
; 0.0

(+ (+ 1))
; 1.0

(quote (a b c))
; (a b c)

'''(a b c d)
; ''(a b c d)

(define inc (lambda (x) (+ 1 x) ) )
;

(inc 10)
; 11.0

(define (inc2 x) (+ 2 x))
;

(inc2 10)
; 12.0

;;; stack fill test

(define (fill x) (if (eqv? x 0) 0 (* 1 (fill (- x 1)))))
;

(fill 10)
; 0

;(fill 500000)
; => OutOfMemory Error


;;; tail recursion test
;;; recursive loop -> iterative loop

(define x #f)
;

(define (loop z)
  (if (eqv? z 0)
    'done
    (begin
      (set! x z)
      (loop (- z 1)))))
;

(loop 10)
; done

(loop 1000000)
; done


;;;
;;; continuation tests:
;;;

(define param #f)
;

(define con1 #f)
;

((call/cc (lambda (x) (set! con1 x) (if param display write)) ) "ciao")
; "ciao"

(con1 display)
; ciao

(define (strange-display x) (display "---") (display x) (display "---") )
;

(strange-display "aha")
; ---aha---

(con1 strange-display)
; ---ciao---


;; interesting continuation test:
;;

(define some-flag #f)
;

(define (my-abortable-proc escape-proc)
  (display "in my-abortable-proc ")
  (if some-flag
      (escape-proc "ABORTED "))
  (display "still in my-abortable-proc ")
  "NOT ABORTED ")
;

(define (my-resumable-proc)
  (display "do-something.. ")
  (display (call-with-current-continuation my-abortable-proc))
  (display "do-some-more.. "))
;

(my-resumable-proc)
; do-something.. in my-abortable-proc still in my-abortable-proc NOT ABORTED do-some-more..

(define some-flag #t)
;

(my-resumable-proc)
; do-something.. in my-abortable-proc ABORTED do-some-more..


;;; new continuation tests:

(define (write-my-args . x) display (cons 'MY-ARGS x))
;

(define (arg x) (begin (display x) x))
;

(arg 5)
; 5

(write-my-args 1 2 3)
;(my-args 1 2 3)

(define con2 #f)
;

(write-my-args 1 2 3 (call-with-current-continuation (lambda (x) (set! con2 x) ) ) 4 5 6 )
;(my-args 1 2 3 #<void> 4 5 6)

(con2 -1)
;(my-args 1 2 3 -1 4 5 6)

(con2 -2)
;(my-args 1 2 3 -2 4 5 6)

(write-my-args	(arg 1) (arg 2) (arg 3) 
		(call-with-current-continuation (lambda (x) (set! con2 x) ) ) 
		(arg 4) (arg 5) (arg 6) )

;6.05.04.03.02.01.0
;(my-args 1.0 2.0 3.0 #<void> 4.0 5.0 6.0)

(con2 -7)
;>3.02.01.0
;(my-args 1.0 2.0 3.0 -7.0 4.0 5.0 6.0)


;;;;;;;;

(define d1 (new 'java.util.Date))
(define d2 d1)
(eq? d2 d1)
;#t
(eqv? d2 d1)
;#t
(define s1 (new 'java.lang.String 'test))
(define s2 (new 'java.lang.String 'test))
(eq? s2 s1)
;#f
(eqv? s2 s1)
;#t


(define a 12)
a
;12.0 
(in-environment (current-environment) a)
;12.0
(in-environment (scheme-report-environment 5) a) 
;*** Unbound variable: a
a
;12.0
(in-environment (null-environment 5) a)
;*** Unbound variable: a
(in-environment (null-environment 5) (define a 77) ) 
a
;12.0
(in-environment (null-environment 5) a)
;77.0
