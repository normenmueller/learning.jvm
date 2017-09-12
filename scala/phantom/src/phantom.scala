package phantom

/** The Builder Pattern is an increasingly popular idiom for object creation.
 * 
 *  Traditionally, one of it's shortcomings in relation to simple constructors
 *  is that clients can try to build incomplete objects, by omitting mandatory
 *  parameters, and that error will only show up in runtime.
 *  
 *  Here we'll show how to make this verification statically in Scala.
 *  
 *  So, let's say you want to order a shot of scotch. You'll need to ask for
 *  a few things: the brand of the whiskey, how it should be prepared
 *  (neat, on the rocks or with water) and if you want it doubled.
 *  In addition, you'll probably also ask for a specific kind of glass,
 *  brand and temperature of the water and who knows what else. Limiting the
 *  snobbery to the kind of glass, here is one way to represent the order in scala.
 */
sealed abstract class Preparation
case object Neat       extends Preparation
case object OnTheRocks extends Preparation
case object WithWater  extends Preparation

sealed abstract class Glass
case object Short extends Glass
case object Tall  extends Glass
case object Tulip extends Glass

case class OrderOfScotch(
  val brand: String,
  val mode: Preparation,
  val isDouble: Boolean,
  val glass: Option[Glass]
)

/** A client can instantiate their orders like this
 */
object PhantomEx1 extends App {  
  val normal = new OrderOfScotch("Bobby Runner", OnTheRocks, false, None)
  val snooty = new OrderOfScotch("Glenfoobar", WithWater, false, Option(Tulip));
}

/** This isn't so bad, but it can get annoying to remember the position of each argument,
 * specially if many are optional. There are two traditional ways to circumvent this
 * problem — define telescoping constructors or set the values post-instantiation with
 * accessors — but both idioms have their shortcomings. Recently, in Java circles, it has
 * become popular to use a variant of the GoF Builder pattern. So popular that it is
 * Item 2 in the second edition of Joshua Bloch's Effective Java. A Java-ish implementation
 * in Scala would be something like this:
 */

package a {
class ScotchBuilder {
  type Brand = String
  
  private var theBrand: Option[String] = None
  private var theMode: Option[Preparation] = None
  private var theDoubleStatus: Option[Boolean] = None
  private var theGlass: Option[Glass] = None
  
  /* returning this to enable method chaining. */
  def withBrand(b: Brand) = { theBrand = Some(b); this } 
  def withMode(p: Preparation) = { theMode = Some(p); this }
  def isDouble(b: Boolean) = { theDoubleStatus = Some(b); this }
  def withGlass(g: Glass) = { theGlass = Some(g); this }
  
  def build() = new OrderOfScotch(theBrand.get, theMode.get, theDoubleStatus.get, theGlass);
}}

/** This is almost self-explanatory, the only caveat is that verifying the presence of non-optional
 *  parameters (everything but the glass) is done by the Option.get method. If a field is still None,
 *  an exception will be thrown. Keep this in mind, we'll come back to it later.
 */

/** The var keyword prefixing the fields means that they are mutable references.
 *  Indeed, we mutate them in each of the building methods. We can make it more functional
 *  in the traditional way:
 */

package b {
object BuilderPattern {
  type Brand = String
  
  class ScotchBuilder(theBrand: Option[Brand], theMode: Option[Preparation], theDoubleStatus: Option[Boolean], theGlass: Option[Glass]) {    
    def withBrand(b: Brand) = new ScotchBuilder(Some(b), theMode, theDoubleStatus, theGlass)
    def withMode(p: Preparation) = new ScotchBuilder(theBrand, Some(p), theDoubleStatus, theGlass)
    def isDouble(b: Boolean) = new ScotchBuilder(theBrand, theMode, Some(b), theGlass)
    def withGlass(g: Glass) = new ScotchBuilder(theBrand, theMode, theDoubleStatus, Some(g))
    
    def build() = new OrderOfScotch(theBrand.get, theMode.get, theDoubleStatus.get, theGlass);
  }
  
  def builder = new ScotchBuilder(None, None, None, None)
}}

/** The scotch builder is now enclosed in an object, this is standard practice in Scala to isolate modules.
 *  In this enclosing object we also find a factory method for the builder, which should be called like so:
 */

package b {
object PhantomEx extends App {
  import BuilderPattern._
  
  val order =  builder withBrand("Takes") isDouble(true) withGlass(Tall) withMode(OnTheRocks) build()
}}

/** Looking back at the ScotchBuilder class and it's implementation, it might seem that we just moved
 *  the huge constructor mess from one place (clients) to another (the builder). And yes, that is exactly
 *  what we did. I guess that is the very definition of encapsulation, sweeping the dirt under the rug and
 *  keeping the rug well hidden.
 *  
 *  On the other hand, we haven't gained all the much from this "functionalization"
 *  of our builder; the main failure mode is still present. That is, having clients forget to set mandatory
 *  information, which is a particular concern since we obviously can't fully trust the sobriety of said clients.
 *  
 *  Ideally the type system would prevent this problem, refusing to typecheck a call to build() when any of
 *  the non-optional fields aren't set. That's what we are going to do now.
 *  
 *  One technique, which is very common in Java fluent interfaces, would be to write an interface for each
 *  intermediate state containing only applicable methods. So we would begin with an interface VoidBuilder
 *  having all our withXY() methods but no build() method, and a call to, say, withMode() would return another
 *  interface (maybe BuilderWithMode), and so on, until we call the last withXY() for a mandatory XY, which
 *  would return an interface that finally has the build() method. This technique works, but it requires a
 *  metric buttload of code — for n mandatory fields 2^n interfaces should be created. This could be automated
 *  via code generation, but there is no need for such heroic efforts, we can make the type system work in our
 *  favor by applying some generics magic.
 *  
 *  First, we define two abstract classes:
 */

abstract class TRUE
abstract class FALSE

/** Then, for each mandatory field, we add to our builder a generic parameter:
 */

package c {
class ScotchBuilder[HB, HM, HD](    
  val theBrand: Option[String],
  val theMode: Option[Preparation],
  val theDoubleStatus: Option[Boolean],
  val theGlass: Option[Glass]) {
  /* ... body of the scotch builder .... */
}}

/** Next, have each withXY method pass ScotchBuilder's type parameters as type arguments to the builders
 *  they return. But, and here is where the magic happens, there is a twist on the methods for mandatory
 *  parameters: they should, for their respective generic parameters, pass instead TRUE:
 */

package d {
object BuilderPattern {
  type Brand = String
  
  class ScotchBuilder[HB, HM, HD](
    val theBrand: Option[String],
    val theMode: Option[Preparation],
    val theDoubleStatus: Option[Boolean],
    val theGlass: Option[Glass]
  ) {
    def withBrand(b: String)     = new ScotchBuilder[TRUE, HM, HD](Some(b), theMode, theDoubleStatus, theGlass)
    def withMode(p: Preparation) = new ScotchBuilder[HB, TRUE, HD](theBrand, Some(p), theDoubleStatus, theGlass)
    def isDouble(b: Boolean)     = new ScotchBuilder[HB, HM, TRUE](theBrand, theMode, Some(b), theGlass)
    def withGlass(g: Glass)      = new ScotchBuilder[HB, HM, HD](theBrand, theMode, theDoubleStatus, Some(g))
  }
  
  def builder = new ScotchBuilder(None, None, None, None)
}}

/** The second part of the magic act is to apply the world famous pimp-my-library idiom and move the build()
 *  method to an implicitly created class, which will be anonymous for the sake of simplicity:
 */

package object d {
import BuilderPattern._

implicit def enableBuild(builder: ScotchBuilder[TRUE, TRUE, TRUE]) = new {
  def build() = new OrderOfScotch(builder.theBrand.get, builder.theMode.get, builder.theDoubleStatus.get, builder.theGlass)
}}

/** Note the type of the parameter for this implicit method: `ScotchBuilder[TRUE, TRUE, TRUE]`. This is the point where
 *  we "declare" that we can only build an object if all the mandatory parameters are specified.
 */

package d {
object PhantomEx extends App {
  import BuilderPattern._
  
  builder withBrand("hi") isDouble(false) withGlass(Tall) withMode(Neat) build()
  builder isDouble(false) withBrand("hi") withMode(Neat) withGlass(Tall) build()
  //builder withBrand("hi") isDouble(false) withGlass(Tall)  build()
}}

/** Now, remember those abstract classes TRUE and FALSE?
 *  We never did subclass or instantiate them at any point. This is an idiom named Phantom Types, commonly used in
 *  the ML family of programming languages. Even though this application of phantom types is fairly trivial, we can
 *  glimpse at the power of the mechanism. We have, in fact, codified all 2^n states (one for each combination of
 *  mandatory fields) as types. ScotchBuilder's subtyping relation forms a lattice structure and the enableBuild()
 *  implicit method requires the supremum of the poset (namely, ScotchBuilder[TRUE, TRUE, TRUE]). If the domain
 *  requires, we could specify any other point in the lattice — say we can doll-out a dose of any cheap whiskey if
 *  the brand is not given, this point is represented by ScotchBuilder[_, TRUE, TRUE]. And we can even escape the
 *  lattice structure by using Scala inheritance. Of course, I didn't invent any of this; the idea came to me in
 *  [[http://scholar.google.com.br/scholar?cluster=15738227024751313970][this]] article by Matthew Fluet and Riccardo
 *  Pucella, where they use phantom types to encode subtyping in a language that lacks it.
 */

// ===================
/** This is an alternative implementation. It differs by (1) using abstract members, type members instead of parameters and
 *  type parameters and (2) contains visibility annotations.
 */
package e {
object BuilderPattern {
  sealed abstract class Preparation
  case object Neat       extends Preparation
  case object OnTheRocks extends Preparation
  case object WithWater  extends Preparation

  sealed abstract class Glass
  case object Short extends Glass
  case object Tall  extends Glass
  case object Tulip extends Glass

  case class OrderOfScotch private[BuilderPattern] (
    val brand: String,
    val mode: Preparation,
    val isDouble: Boolean,
    val glass: Option[Glass]
  )

  abstract class TRUE  
  abstract class FALSE

  abstract class ScotchBuilder { self:ScotchBuilder =>
    protected[BuilderPattern] val theBrand: Option[String]
    protected[BuilderPattern] val theMode: Option[Preparation]
    protected[BuilderPattern] val theDoubleStatus: Option[Boolean]
    protected[BuilderPattern] val theGlass: Option[Glass]

    type HAS_BRAND
    type HAS_MODE
    type HAS_DOUBLE_STATUS

    def withBrand(b:String) = new ScotchBuilder {
      protected[BuilderPattern] val theBrand:Option[String] = Some(b)
      protected[BuilderPattern] val theMode:Option[Preparation] = self.theMode
      protected[BuilderPattern] val theDoubleStatus:Option[Boolean] = self.theDoubleStatus
      protected[BuilderPattern] val theGlass:Option[Glass] = self.theGlass

      type HAS_BRAND = TRUE
      type HAS_MODE = self.HAS_MODE
      type HAS_DOUBLE_STATUS = self.HAS_DOUBLE_STATUS
    }

    def withMode(p:Preparation) = new ScotchBuilder {
      protected[BuilderPattern] val theBrand:Option[String] = self.theBrand
      protected[BuilderPattern] val theMode:Option[Preparation] = Some(p)
      protected[BuilderPattern] val theDoubleStatus:Option[Boolean] = self.theDoubleStatus
      protected[BuilderPattern] val theGlass:Option[Glass] = self.theGlass

      type HAS_BRAND = self.HAS_BRAND
      type HAS_MODE = TRUE
      type HAS_DOUBLE_STATUS = self.HAS_DOUBLE_STATUS
    }


    def isDouble(b:Boolean) = new ScotchBuilder {
      protected[BuilderPattern] val theBrand:Option[String] = self.theBrand
      protected[BuilderPattern] val theMode:Option[Preparation] = self.theMode
      protected[BuilderPattern] val theDoubleStatus:Option[Boolean] = Some(b)
      protected[BuilderPattern] val theGlass:Option[Glass] = self.theGlass

      type HAS_BRAND = self.HAS_BRAND
      type HAS_MODE = self.HAS_MODE
      type HAS_DOUBLE_STATUS = TRUE
    }
     
    def withGlass(g:Glass) = new ScotchBuilder {
      protected[BuilderPattern] val theBrand:Option[String] = self.theBrand
      protected[BuilderPattern] val theMode:Option[Preparation] = self.theMode
      protected[BuilderPattern] val theDoubleStatus:Option[Boolean] = self.theDoubleStatus
      protected[BuilderPattern] val theGlass:Option[Glass] = Some(g)

      type HAS_BRAND = self.HAS_BRAND
      type HAS_MODE = self.HAS_MODE
      type HAS_DOUBLE_STATUS = self.HAS_DOUBLE_STATUS
    }

  }

  type CompleteBuilder = ScotchBuilder {
    type HAS_BRAND = TRUE
    type HAS_MODE = TRUE
    type HAS_DOUBLE_STATUS = TRUE
  }

  implicit def enableBuild(builder: CompleteBuilder) = new {
    def build() = 
      new OrderOfScotch(builder.theBrand.get, builder.theMode.get, builder.theDoubleStatus.get, builder.theGlass);
  }

  def builder = new ScotchBuilder {
    protected[BuilderPattern] val theBrand:Option[String] = None
    protected[BuilderPattern] val theMode:Option[Preparation] = None
    protected[BuilderPattern] val theDoubleStatus:Option[Boolean] = None
    protected[BuilderPattern] val theGlass:Option[Glass] = None

    type HAS_BRAND = FALSE
    type HAS_MODE = FALSE
    type HAS_DOUBLE_STATUS = FALSE
  }
}}


