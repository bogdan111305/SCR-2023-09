package module1.homework

trait Show[A] {
  def show(a: A): String
}

object Show {
  implicit val showString: Show[String] = (a: String) => a
  implicit val showInt: Show[Int] = (a: Int) => a.toString
  implicit val showBoolean: Show[Boolean] = (a: Boolean) => if (a) "true" else "false"
  implicit def showList[A](implicit showA: Show[A]): Show[List[A]] = (a: List[A]) => a.map(showA.show).mkString("[", ", ", "]")
  implicit def showSet[A](implicit showA: Show[A]): Show[Set[A]] = (a: Set[A]) => a.map(showA.show).mkString("{", ", ", "}")
  def apply[A](implicit show: Show[A]): Show[A] = show
  def fromJvm[A]: Show[A] = (v: A) => v.toString
  def fromFunction[A](showFunc: A => String): Show[A] = (v: A) => showFunc(v)
  implicit class ShowOps[A](val a: A) extends AnyVal {
    def show(implicit showInstance: Show[A]): String = showInstance.show(a)
  }
}

trait Monad[M[_]] {
  def flatMap2[A, B](ma: M[A])(f: A => M[B]): M[B]

  def flatten2[A](m: M[M[A]]): M[A] = flatMap2(m)(identity)
}

object Monad {
  def apply[F[_]](implicit instance: Monad[F]): Monad[F] = instance

  implicit val optionMonad: Monad[Option] = new Monad[Option] {
    override def flatMap2[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)
  }

  implicit val listMonad: Monad[List] = new Monad[List] {
    override def flatMap2[A, B](fa: List[A])(f: A => List[B]): List[B] = fa.flatMap(f)
  }

  implicit val setMonad: Monad[Set] = new Monad[Set] {
    override def flatMap2[A, B](fa: Set[A])(f: A => Set[B]): Set[B] = fa.flatMap(f)
  }
  implicit class MonadOps[A, F[_] : Monad](f: F[F[A]]) {
    def flatten2: F[A] = Monad[F].flatten2(f)
  }
}

