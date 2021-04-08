/*
Trampoline is a program description
In this case, the program description is:
Done if there are no computations to be run
More if there is a recursive function call to be made
*/
sealed trait Trampoline[A]
  
final case class Done[A](value: A) extends Trampoline[A]
  
final case class More[A](call: () => Trampoline[A]) extends Trampoline[A]

val testList = (0 to 1000000).toList

// Now we need to implement the recursive functions:
def even[A](lst: Seq[A]): Trampoline[Boolean] = {
  lst match {
    case Nil => Done(true)
    case x :: xs => More(() => odd(xs))
  }
}

def odd[A](lst: Seq[A]): Trampoline[Boolean] = {
  lst match {
    case Nil => Done(false)
    case x :: xs => More(() => even(xs))
  }
}


val oddEven = even(testList)

// Now we need to implement the `run` function so that we can traverse the ADT

def run[A](program: Trampoline[A]): A = {
    program match {
        case Done(value) => value
        case More(f)     => run(f())
    }
}

// We can separate the `run` function with a resume and a run function
// Left side will hold the More program and right side will hold the done value

def resume[A](t: Trampoline[A]): Either[() => Trampoline[A], A] = t match {
    case Done(value) => Right(value)
    case More(f)     => Left(f)
}

// Now run should look smth like this

def run2[A](t: Trampoline[A]): A = {
    resume(t) match {
        case Right(value) => value
        case Left(more)   => run2(more())
    }
    
}

// We can now add both resume and run functions on the Trampoline trait


sealed trait TrampolineFinal[+A] {
  def resume: Either[() => TrampolineFinal[A], A] = this match {
    case DoneFinal(v) => Right(v)
    case MoreFinal(k) => Left(k)
  }
   
  final def runT: A = resume match {
    case Right(value) => value
    case Left(more) => more().runT
  }  
}

final case class DoneFinal[A](value: A) extends TrampolineFinal[A]
  
final case class MoreFinal[A](call: () => TrampolineFinal[A]) extends TrampolineFinal[A]

