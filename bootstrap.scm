;;;;; bootstrap.scm
;;;;;
;;;;; (c) 2001-2007 Guglielmo Nigri <guglielmonigri@yahoo.it>.
;;;;; Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are
;;;;; welcome to redistribute it under certain conditions; see LICENSE.TXT.
;;;;;
;;;;; This file contains portions from
;;;;; prelude.scheme -- UMB Scheme, standard primitives in Scheme.
;;;;; Copyright 1988, 1991 University of Massachusetts

;;;; Error Handling

(define error-abort #f)

(begin (call/cc (lambda (x) (set! error-abort x))) (display "\n")  )

(define (error msg . obj )
	(display "\n*** ") (display msg)
	(if (null? obj)
		(set! __errobj obj)
		(begin (display " ") (display (car obj)) (set! __errobj (car obj)) ))
	(error-abort #f))


;;;; LISTS

(define (null? x) (eq? x '()))

(define (list . elems) elems)

(define (list-copy x)
	(if (pair? x)
		(cons (car x) (list-copy (cdr x)))
		x))

(define (last x)
	(if (pair? x)
		(if (null? (cdr x))
			x
			(last (cdr x)))
		(error "last" x)))

; nconc
; append

;;;; Quasiquotation
;;;; Adapted from "Quasiquotation in Lisp" by Alan Bawden, Brandeis University

(define (tag-backquote? x)
	(if (pair? x)
		(eq? (car x) 'quasiquote)
		#f))

(define (tag-comma? x)
	(if (pair? x)
		(eq? (car x) 'unquote)
		#f))

(define (tag-comma-atsign? x)
	(if (pair? x)
		(eq? (car x) 'unquote-splicing)
		#f))

(define (tag-data x) (car (cdr x)))

(define (qq-expand x)
  (if (tag-comma? x)
	 (tag-data x)
  (if (tag-comma-atsign? x)
	 (error "illegal usage of ,@")
  (if (tag-backquote? x)
	 (qq-expand
		(qq-expand (tag-data x)))
  (if (pair? x)
	 (list 'append
		(qq-expand-list (car x))
		(qq-expand (cdr x)))
	 (list 'quote x))))))

(define (qq-expand-list x)
  (if (tag-comma? x)
	 (list 'list (tag-data x))
  (if (tag-comma-atsign? x)
	 (tag-data x)
  (if (tag-backquote? x)
	 (qq-expand-list
	   (qq-expand (tag-data x)))
  (if (pair? x)
	 (list 'list
	    (list 'append
	      (qq-expand-list (car x))
	      (qq-expand (cdr x))))
	 (list 'quote (list x)))))))

(define quasiquote
	(make-rewriter (lambda (_x)
		(qq-expand (tag-data _x))
		)))


;;;; PRIMITIVE  PROCEDURES

;;; equal?

(define (equal? a b)
	(define (equal-pair? a b)
		(if (equal? (car a) (car b))
			(equal? (cdr a) (cdr b))
			#f))
	(if (pair? a)
		(if (pair? b)
			(equal-pair? a b)
			#f)
		(if (pair? b)
			#f
			(eqv? a b)) ) )

;;; map

(define (map f list)
	(define (map1 f list res)
		(if (null? list)
			res
			(map1 f (cdr list) (append res (cons (f (car list)) '() ))) ))
	(map1 f list '()) )


;;; Pairs and lists.

;; car - cdr compositions  (caar pair) ... (cddddr pair)

(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

(define (caaar x) (car (car (car x))))
(define (caadr x) (car (car (cdr x))))
(define (cadar x) (car (cdr (car x))))
(define (caddr x) (car (cdr (cdr x))))
(define (cdaar x) (cdr (car (car x))))
(define (cdadr x) (cdr (car (cdr x))))
(define (cddar x) (cdr (cdr (car x))))
(define (cdddr x) (cdr (cdr (cdr x))))

(define (caaaar x) (car (car (car (car x)))))
(define (caaadr x) (car (car (car (cdr x)))))
(define (caadar x) (car (car (cdr (car x)))))
(define (caaddr x) (car (car (cdr (cdr x)))))
(define (cadaar x) (car (cdr (car (car x)))))
(define (cadadr x) (car (cdr (car (cdr x)))))
(define (caddar x) (car (cdr (cdr (car x)))))
(define (cadddr x) (car (cdr (cdr (cdr x)))))
(define (cdaaar x) (cdr (car (car (car x)))))
(define (cdaadr x) (cdr (car (car (cdr x)))))
(define (cdadar x) (cdr (car (cdr (car x)))))
(define (cdaddr x) (cdr (car (cdr (cdr x)))))
(define (cddaar x) (cdr (cdr (car (car x)))))
(define (cddadr x) (cdr (cdr (car (cdr x)))))
(define (cdddar x) (cdr (cdr (cdr (car x)))))
(define (cddddr x) (cdr (cdr (cdr (cdr x)))))


;;; (memq   obj list)
;;; (memv   obj list)
;;; (member obj list)

(define (memq obj list)
    (if (null? list) #f
	(if (not (pair? list))
	    (error "2nd arg to memq not a list: " list)
	    (if (eq?  obj (car list)) list
		(memq  obj (cdr list)) ))))


(define (memv obj list)
    (if (null? list) #f
	(if (not (pair? list))
	    (error "2nd arg to memv not a list: " list)
	    (if (eqv?  obj (car list)) list
		(memv  obj (cdr list)) ))))


(define (member obj list)
    (if (null? list) #f
	(if (not (pair? list))
	    (error "2nd arg to member not a list: " list)
	    (if (equal?  obj (car list)) list
		(member  obj (cdr list)) ))))


;;; (assq  obj alist)
;;; (assv  obj alist)
;;; (assoc obj alist)

(define (assq obj alist)
    (if (null? alist) #f
	(if (not (pair? alist))
	    (error "2nd argument to assq not a list: " alist)
	    (if (eq? (caar alist) obj) (car alist)
		(assq obj (cdr alist))))))


(define (assv obj alist)
    (if (null? alist) #f
	(if (not (pair? alist))
	    (error "2nd argument to assv not a list: " alist)
	    (if (eqv? (caar alist) obj) (car alist)
		(assv obj (cdr alist))))))


(define (assoc obj alist)
    (if (null? alist) #f
	(if (not (pair? alist))
	    (error "2nd argument to assoc not a list: " alist)
	    (if (equal? (caar alist) obj) (car alist)
		(assoc obj (cdr alist))))))


;;;; GLEAM MACRO SYSTEM

;;; simple variant of let for use in _defmacro

(define _let1 (make-rewriter
	(lambda (_x)
		`( (lambda (,(caaar (cdr _x))) ,@(cdr (cdr _x))  )
		  ,(cadaar (cdr _x)) ) )))


;;; (_defmacro (key args) ... ) -->
;;; (_define key (make-rewriter (lambda ( z ) ... )))

(define _defmacro (make-rewriter
	(lambda (_x)
		`(define ,(caar (cdr _x)) (make-rewriter
			(lambda (_form) (_let1 (( ,@(cdar (cdr _x)) (cdr _form) ))
				(begin ,@(cdr (cdr _x)) )))))) ))

; (_defmacro (h x) `(help ',(car x)) )

;;; let

; (let ((a b) (c d) ...) ...)
; (let ((x 10) (y 20)) (+ x y)) => 30

(_defmacro (let _x)
	(define decl (car _x))
	(define body (cdr _x))
	`((lambda ,(map car decl) ,@body) ,@(map cadr decl)  ))


;;; let*

; (let* ((a b) (c d) ...) ...)
; (let* ((x 10) (y (+ 1 x))) (+ x y)) => 21

(_defmacro (let* x)
	(if (null? (cdar x))
		`(let ,@x)
		`(let (,(caar x)) (let* ,(cdar x) ,@(cdr x)) )))


;;; gensym

(define (gensym . x) (generate-symbol) )


;;; and

; (and) 	  => #t
; (and e1) 	  => e1
; (and e1 e2 ...) =>
; 	(let ((x e1)
;     	      (thunk (lambda()(and e2...))))
;	    (if x (thunk) x))

(_defmacro (and args)
    (if (null? args) #t
	(if (null? (cdr args)) (car args)
	    (let* ((x (gensym "_x"))
		  (thunk (gensym "_thunk")))
		`(let* ((,x ,(car args))
		       (,thunk (lambda ()
			  (and ,@(cdr args)))))
		    (if ,x (,thunk) ,x))
	    ))))


;;; or

; (or) 	  	 => #f
; (or e1) 	 => e1
; (or e1 e2 ...) =>
; 	(let ((x e1)
;     	      (thunk (lambda()(or e2...))))
;	    (if x x (thunk)))


(_defmacro (or args)
    (if (null? args) #f
	(if (null? (cdr args)) (car args)
	    (let ((x (gensym "_x"))
		  (thunk (gensym "_thunk")))
		`(let ((,x ,(car args))
		       (,thunk (lambda ()
			   (or ,@(cdr args)))))
		    (if ,x ,x (,thunk)))
	    ))))

;;; cond

; (cond) => '()
;
; (cond (else seq)) => (begin seq)
;
; (cond (e1) c2 ...) => (or e1 (cond c2 ...))
;
; (cond (e1 => recipient) c2 ...) =>
;   (let ((t e1)
;         (r (lambda() recipient))
;         (c (lambda() c2 ...)))
;    (if t ((r)t) (c)) )
;
; (cond (e1 seq1) c2 ...) =>
;    (if e1 (begin seq1)
;     (cond c2 ...))
;
;(cond ((assv 'b '((a 1) (b 2))) => cadr) (else #f)) => 2


(_defmacro (cond form)
    (if (null? form) ''()
	(let ((c1 (car form)))
	    (if (not (pair? c1))
		(error "invalid cond syntax: " form)
		(if (eq? (car c1) 'else)
		    `(begin ,@(cdr c1))
		(if (null? (cdr c1))
		    `(or ,(car c1)
			 (cond ,@(cddr form)))
		(if (eq? (cadr c1) '=>)
		  (let ((t (gensym "_t"))
			(r (gensym "_r"))
			(c (gensym "_c")))
		    `(let ((,t ,(car c1))
			   (,r (lambda () ,@(cddr c1)))
			   (,c (lambda () (cond ,@(cdr form)))))
			(if ,t ((,r),t) (,c))) )
		  `(if ,(car c1)
		       (begin ,@(cdr c1))
		       (cond ,@(cdr form)))
		)))))))

;;; apply

(define (set-last-cdr! list elem)
  (if (null? (cdr list))
      (set-cdr! list elem)
      (set-last-cdr! (cdr list) elem)))

(define (_ls accum rest)
      (if (null? (cdr rest))
          (begin (set-last-cdr! accum (car rest)) accum)
          (_ls (append accum (list (car rest))) (cdr rest))))

(define (list* first . rest)
    (if (null? rest)
      first
      (let ((acc (list first)))
        (_ls acc rest))))

(_defmacro (apply _x)
	(let ( (fun (car _x))
		(args (cdr _x)))
	(eval ``(,,fun ,@(list* ,@args)) )))

;;; define-macro

(_defmacro (define-macro _args)
	(let (	(op (caar _args))
		(args (cdar _args))
		(body (cdr _args)))
	(cond
		((pair? args)
			(list '_defmacro (list op '_x) 
				(list 'quasiquote (list 'apply (cons 'lambda (cons args body)) (list 'quote (list 'unquote '_x))) )
			)
		)
		(else
			(cons '_defmacro (cons (list op args) body))
		)
	)))

