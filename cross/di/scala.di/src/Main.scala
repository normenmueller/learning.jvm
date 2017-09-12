package main

//trait WriterService {
//  def writer(s: String)
//}
//
//trait SimpleWriter extends WriterService {
//  def writer(s: String): Unit = println(s)  
//}
//
//trait ComplexWriter extends WriterService {
//  def writer(s: String): Unit = println("""The string is "%s"""".format(s))
//}
//
//trait WriterComponent { self: WriterService => /* Dependency declaration */ }
//
//object Main extends App {
//  val sw = new WriterComponent with SimpleWriter
//  sw.writer("This is my test")
//  
//  val cw = new WriterComponent with ComplexWriter
//  cw.writer("This is my test")
//}

// a user
case class User(name: String)

//trait UserRepositoryComponent {
//  def userRepository : UserRepository
//
//  trait UserRepository {
//    def findAll: List[User]
//  }
//}

// services
trait UserRepository { def findAll: List[User] }
trait UserRegistry   { def regAll(us: List[User]): Boolean }

// service implementations
trait ORMUserRepository extends UserRepository { override def findAll: List[User] = Nil }
trait ORMUserRegistry   extends UserRegistry   { override def regAll(us: List[User]): Boolean = true }
trait EmptyUserRegistry extends UserRegistry   { override def regAll(us: List[User]): Boolean = false }

// components
trait UserService { self: UserRepository with UserRegistry =>
  def findAll: List[User] = self.findAll
  def regAll(us: List[User]): Boolean = self.regAll(us)
}

// "application contexts"
object ConfigA { val us: UserService = new UserService with ORMUserRepository with ORMUserRegistry }
object ConfigB { val us: UserService = new UserService with ORMUserRepository with EmptyUserRegistry }

object Main extends App {
  ConfigA.us.findAll
  ConfigA.us.regAll(User("A") :: User("B") :: Nil)
  
  ConfigB.us.findAll
  ConfigB.us.regAll(User("A") :: User("B") :: Nil)  
}
