package learning
package concurrency
package nulls

object Main extends App {
 
  // Boxing/ unboxing
  {
//val x: Integer = 0
//val n: Integer = null
//
//println("x = " + x)
//println("n = " + n)
//
//println("x == n = " + (x == n))
//println("x <= n = " + (x <= n))
//println("x >= n = " + (x >= n))
//println("x <  n = " + (x <  n))
//println("x >  n = " + (x >  n))
//
//println("n == x = " + (n == x))
//println("n <= x = " + (n <= x))
//println("n >= x = " + (n >= x))
//println("n <  x = " + (n <  x))
//println("n >  x = " + (n >  x))
  }

//  println("---")
//
  {
//val x: Integer = 1
//val n: Integer = null
//
//println("x = " + x)
//println("n = " + n)
//
//println("x == n = " + (x == n))
//println("x <= n = " + (x <= n))
//println("x >= n = " + (x >= n))
//println("x <  n = " + (x <  n))
//println("x >  n = " + (x >  n))
//
//println("n == x = " + (n == x))
//println("n <= x = " + (n <= x))
//println("n >= x = " + (n >= x))
//println("n <  x = " + (n <  x))
//println("n >  x = " + (n >  x))
  }
//
//  println("---")
//
//  // Any ref
  {
//val x: String = ""
//val n: String = null
//
//println("x = " + x)
//println("n = " + n)
//
//println("x == n = " + (x == n))
////println("x <= n = " + (x <= n)) //NPE!
////println("x >= n = " + (x >= n)) //NPE!
////println("x <  n = " + (x <  n)) //NPE!
////println("x >  n = " + (x >  n)) //NPE!
//
//println("n == x = " + (n == x))
////println("n <= x = " + (n <= x)) //NPE!
////println("n >= x = " + (n >= x)) //NPE!
////println("n <  x = " + (n <  x)) //NPE!
////println("n >  x = " + (n >  x)) //NPE!
  }
//
//  println("---")
//
// Way to go: Ban `null` from /any/ of your code. Period.
// If you're using a Java library that returns `null`, convert the result to
// . fj.data.Option -- http://www.functionaljava.org/javadoc/4.7/functionaljava/index.html
// or
// . java.util.Optional -- https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
// Note one important rule when working with one of those types:
// . Never call the `get` method.
{
import scala.math.Ordering.Implicits._
val x: Option[Integer] = Option(0)
val n: Option[Integer] = Option(null)

println("x = " + x)
println("n = " + n)

println("x == n = " + (x == n))
println("x <= n = " + (x <= n))
println("x >= n = " + (x >= n))
println("x <  n = " + (x <  n))
println("x >  n = " + (x >  n))

println("n == x = " + (n == x))
println("n <= x = " + (n <= x))
println("n >= x = " + (n >= x))
println("n <  x = " + (n <  x))
println("n >  x = " + (n >  x))
  }

}
