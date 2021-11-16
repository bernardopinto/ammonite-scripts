// import $ivy.`org.scala-lang:scala-reflect::2.13.3`


    // Notion of values as types!

    import scala.reflect.runtime.universe._

    def show[T](value: T)(implicit tag: TypeTag[T]) = tag.toString.replace("ammonite.$file.TypelevelProgramming", "")
    .replace("reflect.runtime.universe.", "")


    // Natural numbers as types
    // Peano arithmetic
    trait Nat
    class _0 extends Nat
    class Succ[N <: Nat] extends Nat


    type _1 = Succ[_0]
    type _2 = Succ[_1] // Succ[Succ[_0]]
    type _3 = Succ[_2]
    type _4 = Succ[_3]
    type _5 = Succ[_4]

    // _2  < 4 ?

    trait <[A <: Nat, B <: Nat]

    object < {
        implicit def ltBasic[B <: Nat]: <[_0, Succ[B]] = new <[_0, Succ[B]] {}

        implicit def inductive[A <: Nat, B <: Nat](implicit lt: <[A, B],
                                                            tagA: TypeTag[A],
                                                            tagB: TypeTag[B],
                                                            tagLt: TypeTag[<[A, B]]): <[Succ[A], Succ[B]] = {
            println(show(lt))
            // println(show(tagA))
            // println(show(tagB))
            new <[Succ[A], Succ[B]] {}
        }
            

        def apply[A <: Nat, B <: Nat](implicit lt: <[A, B]) = lt
    }
    import <._

    // val i = inductive[_2, _3]

    val comparison: _3 < _5 = <[_3, _5]

    // <.apply[_2, _3] => requires implicit lt: <[_2, _3]

    // _2 = Succ[_1]
    // _3 = Succ[_2]
    // val invalidComparison: _3 < _1 = <[_3, _1] Invalid comparison doesn't compile, uncomment to test



    // print(show(comparison))