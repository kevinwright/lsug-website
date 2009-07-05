package org.lsug.ical4scala

import _root_.scala.reflect.Manifest

/**
 * Created by kevin
 * 21:38:43 on 29-Jun-2009
 */


object POption {
  def apply[T](in: T) = in match {
    case null => PNone
    case x => PSome(x)
  }

  /**
   * Create a Box from the specified Option.
   * @return a Box created from an Option. Full(x) if the Option is Some(x) and Empty otherwise
   */
  def apply[T](in: Option[T]) = in match {
    case Some(x) => PSome(x)
    case _ => PNone
  }

  /**
   * Transform a List with zero or one elements to a Box.
   * @return a Box object containing the head of a List. Full(x) if the List contains at least one element and Empty otherwise.
   */
  def apply[T](in: List[T]) = in match {
    case x :: _ => PSome(x)
    case _ => PNone
  }

  /**
   * Apply the specified PartialFunction to the specified value and return the result
   * in a Full Box; if the pf is undefined at that point return Empty.
   * @param pf the partial function to use to transform the value
   * @param value the value to transform
   * @return a Full box containing the transformed value if pf.isDefinedAt(value); Empty otherwise
   */
  def apply[InType, OutType](pf: PartialFunction[InType, OutType])(value: InType): POption[OutType] =
  if (pf.isDefinedAt(value)) PSome(pf(value)) else PNone

  /**
   * Apply the specified PartialFunction to the specified value and return the result
   * in a Full Box; if the pf is undefined at that point return Empty.
   * @param pf the partial function to use to transform the value
   * @param value the value to transform
   * @return a Full box containing the transformed value if pf.isDefinedAt(value); Empty otherwise
   */
  def apply[InType, OutType](value: InType)(pf: PartialFunction[InType, OutType]): POption[OutType] =
  if (pf.isDefinedAt(value)) PSome(pf(value)) else PNone

  /**
   * This implicit transformation allows one to use a Box as an Iterable
   * @return List(in) if this Box is Full(in); Nil otherwise
   */
  implicit def box2Iterable[T](in: POption[T]): Iterable[T] = in.toList

  /**
   * This implicit transformation allows one to use an Option as a Box.
   * @return a Box object from an Option. Full(in) if the Option is Some(in); Empty otherwise
   */
  implicit def option2Box[T](in: Option[T]): POption[T] = POption(in)

  /**
   * This implicit transformation allows one to use a Box as an Option.
   * @return <code>Some(in)</code> if this Box is <code>Full(in)</code>; None otherwise
   */
  implicit def box2Option[T](in: POption[T]): Option[T] = in.toOption

  /**
   * This method allows one to encapsulate any object in a Box in a null-safe manner,
   * treating null values to Empty
   * @return <code>Full(in)</code> if <code>in</code> is not null; Empty otherwise
   */
  def legacyNullTest[T](in: T): POption[T] = in match {
    case null => PNone
    case _ => PSome(in)
  }

  /**
   * Alias for legacyNullTest.
   * This method allows one to encapsulate any object in a Box in a null-safe manner,
   * returning Empty if the specified value is null.
   * @return Full(in) if <code>in</code> is not null Empty otherwise
   */
  def !![T](in: T): POption[T] = legacyNullTest(in)

  /**
   * Create a Full box containing the specified value if "in" is an instance
   * of the specified class, or Empty otherwise.
   */
  def isA[A, B](in: A, clz: Class[B]): POption[B] =
  (POption !! in).isA(clz)

  /**
   * Create a Full box containing the specified value if <code>in</code> is of
   * type <code>B</code>; Empty otherwise.
   */
  def asA[B](in: T forSome {type T})(implicit m: Manifest[B]): POption[B] =
  (POption !! in).asA[B]
}



/**
 * An Option-esque monad capable of holding a list of parameters
 */
@serializable
sealed abstract class POption[+A] extends Product {
  /** @return true if this contains no value */
  def isEmpty: Boolean

  /** @return true if this contains a value */
  def isDefined: Boolean = !isEmpty

  /** @return the value contained in this if it is full; throw an exception otherwise */
  def open_! : A

  /** @return the value contained in this if it is full; otherwise return the specified default */
  def openOr[B >: A](default: => B): B = default

  /** @return the resulting monad or Empty */
  def map[B](f: A => B): POption[B] = PNone

  /** @return the modified Box or Empty */
  def flatMap[B](f: A => POption[B]): POption[B] = PNone

  /** @return this if it contains a value satisfying the specified predicate; Empty otherwise */
  def filter(p: A => Boolean): POption[A] = this

  /** @return true if this value satisfies the specified predicate */
  def exists(func: A => Boolean): Boolean = false

  /**
   * Perform a side effect by calling the specified function
   * with the value contained in this box.
   */
  def foreach(f: A => Any): Unit = {}

  /**
   * Return a Full[B] if the contents of this Box is an instance of the specified class,
   * otherwise return Empty
   */
  def isA[B](cls: Class[B]): POption[B] = PNone

  /**
   * Return a Full[B] if the contents of this Box is of type <code>B</code>, otherwise return Empty
   */
  def asA[B](implicit m: Manifest[B]): POption[B] = PNone

  /**
   * Return this Box if Full, or the specified alternative if this is Empty
   */
  def or[B >: A](alternative: => POption[B]): POption[B] = alternative

  /**
   * Returns an Iterator over the value contained in this Box
   */
  def elements: Iterator[A] = Iterator.empty

  /**
   * Returns a List of one element if this is Full, or an empty list if Empty.
   */
  def toList: List[A] = Nil

  /**
   * Returns the contents of this box in an Option if this is Full, or
   * None if this is a failure or Empty.
   */
  def toOption: Option[A] = None

  /**
   * This method calls the specified function with the value contained in this Box
   * @return the result of the function or a default value
   */
  def run[T](in: T)(f: (T, A) => T) = in

  /**
   * Perform a side effect by passing this Box to the specified function
   * and return this Box unmodified.
   * @return this Box
   */
  def pass(f: POption[A] => Unit): POption[A] = {f(this) ; this}

  /**
   * Alias for pass
   */
  def $(f: POption[A] => Unit): POption[A] = pass(f)

  /**
   * Determines equality based upon the contents of this Box instead of the box itself.
   * For Full and Empty, this has the expected behavior. Equality in terms of Failure
   * checks for equivalence of failure causes.
   */
  override def equals(other: Any): Boolean = (this, other) match {
    case (PSome(x), PSome(y)) => x == y
    case (PSome(x), y) => x == y
    case (x, y: AnyRef) => x eq y
    case _ => false
  }

  /**
   * Apply the function f1 to the contents of this Box if available; if this
   * is Empty return the specified alternative.
   */
  def choice[B](f1: A => POption[B])(alternative: => POption[B]): POption[B] = this match {
    case PSome(x) => f1(x)
    case _ => alternative
  }

  /**
   * Returns true if the value contained in this box is equal to the specified value.
   */
  def ===[B >: A](to: B): Boolean = false

  /**
   * Equivalent to map(f).or(Full(dflt)).open_!
   */
  def dmap[B](dflt: => B)(f: A => B): B = dflt
}

@serializable
final case class PSome[+A](value: A) extends POption[A] {

  def isEmpty: Boolean = false

  def open_! : A = value

  override def openOr[B >: A](default: => B): B = value

  override def or[B >: A](alternative: => POption[B]): POption[B] = this

  override def exists(func: A => Boolean): Boolean = func(value)

  override def filter(p: A => Boolean): POption[A] = if (p(value)) this else PNone

  override def foreach(f: A => Any): Unit = f(value)

  override def map[B](f: A => B): POption[B] = PSome(f(value))

  override def flatMap[B](f: A => POption[B]): POption[B] = f(value)

  override def elements: Iterator[A] = Iterator.fromValues(value)

  override def toList: List[A] = List(value)

  override def toOption: Option[A] = Some(value)

  override def run[T](in: T)(f: (T, A) => T) = f(in, value)

  override def isA[B](cls: Class[B]): POption[B] = value match {
    case value: AnyRef =>
      if (cls.isAssignableFrom(value.getClass)) PSome(value.asInstanceOf[B])
      else PNone
    case _ => PNone
  }

  override def asA[B](implicit m: Manifest[B]): POption[B] = this.isA(m.erasure).asInstanceOf[POption[B]]

  override def ===[B >: A](to: B): Boolean = value == to

  override def dmap[B](dflt: => B)(f: A => B): B = f(value)
}

@serializable
case object PNone extends PNone[Nothing]

/**
 * The EmptyBox is a Box containing no value.
 */
@serializable
sealed abstract class PNone[+A] extends POption[A] {

  def isEmpty: Boolean = true

  def open_!  = throw new NullPointerException("Trying to open an empty Box")

  override def openOr[B >: A](default: => B): B = default

  override def or[B >: A](alternative: => POption[B]): POption[B] = alternative

  override def filter(p: A => Boolean): POption[A] = this
}