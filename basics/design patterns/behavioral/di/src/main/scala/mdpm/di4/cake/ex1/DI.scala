package mdpm.di4.cake
package ex1

// Datatypes ------------------------------------------------------------------

case class User(name: String, password: String)

// Discourse ------------------------------------------------------------------

package v1 {

  @Service class UserRepository {

    def authenticate(user: User): User = sys.error("nyi")
    def create(user: User): Unit = sys.error("nyi")
    def delete(user: User): Boolean = sys.error("nyi")

  }

  @Service class UserService {

    // >>>> HARD WIRED DEPENDENCY <<<<
    private val userRepository = new UserRepository

    def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
    def create(name: String, password: String): Unit = userRepository.create(User(name, password))
    def delete(name: String): Unit = userRepository.delete(User(name, ""))

  }
}

package v2 {

  @Component trait UserRepositoryComponent {

    @ProvidedService val userRepository = new UserRepository

    @ServiceImplementation private [UserRepositoryComponent] class UserRepository {

      def authenticate(user: User): User = { println("authenticate %s" format user); user }
      def create(user: User): Unit = println("create %s" format user)
      def delete(user: User): Unit = println("delete %s" format user)

    }
  }

  @Component trait UserServiceComponent { self: UserRepositoryComponent @RequiredService =>

    @ProvidedService val userService = new UserService

    @ServiceImplementation private [UserServiceComponent] class UserService {

      def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
      def create(name: String, password: String): Unit = userRepository.create(User(name, password))
      def delete(name: String): Unit = userRepository.delete(User(name, ""))

    }
  }

  @ComposedComponent object ComponentRegistry extends AnyRef with UserServiceComponent with UserRepositoryComponent

  object Main extends App {
    import ComponentRegistry._

    val u = userService.authenticate("Normen", "foobar")
  }
}

package v3 {

  @Component trait UserRepositoryComponent {

    @ProvidedService val userRepository: UserRepository

    @ServiceImplementation class UserRepository {

      def authenticate(user: User): User = { println("authenticate %s" format user); user }
      def create(user: User): Unit = println("create %s" format user)
      def delete(user: User): Unit = println("delete %s" format user)

    }
  }
  @Component trait UserServiceComponent { self: UserRepositoryComponent @RequiredService =>

    @ProvidedService val userService: UserService

    @ServiceImplementation class UserService {

      def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
      def create(name: String, password: String): Unit = userRepository.create(User(name, password))
      def delete(name: String): Unit = userRepository.delete(User(name, ""))

    }
  }

  @ComposedComponent object ComponentRegistry extends AnyRef with UserServiceComponent with UserRepositoryComponent {
    @ProvidedService val userRepository = new UserRepository @ServiceImplementation
    @ProvidedService val userService = new UserService @ServiceImplementation
  }
}

package v4 {
  @Component trait UserRepositoryComponent {

    @ProvidedService def userRepository: UserRepository = new DefaultUserRepository

    @Service trait UserRepository {
    // <<provided service specification>>

      def authenticate(user: User): User
      def create(user: User)
      def delete(user: User)

    }

    @ServiceImplementation private class DefaultUserRepository extends UserRepository {
      // <<provided default service implementation>>
      override def authenticate(user: User): User = { println("[default] authenticated %s" format user); user }
      override def create(user: User): Unit = println("[default] created %s" format user)
      override def delete(user: User): Unit = println("[default] deleted %s" format user)
    }
  }

  // N.B. Wenn man von einer Komponenten lediglich einen speziellen Service erfordern moechte,
  //  dann empfiehlt es sich abstract type and value members zu verwenden.
  //  (cf. Odersky, 2005, Scalable compoenent abstraction, #11, 1st column, Variants)
  //  Mit selftyping koennen wir nur "ganze" Komponenten anfordern.

  @Component trait UserServiceComponent { self: UserRepositoryComponent @RequiredService =>

    @ProvidedService def userService: UserService = new DefaultUserService

    @Service trait UserService {
    // <<provided service specification>>

      def authenticate(name: String, password: String): User
      def create(name: String, password: String): Unit
      def delete(name: String): Unit

    }

    @ServiceImplementation private class DefaultUserService extends UserService {
      // <<provided default service implementation>>
      override def authenticate(name: String, password: String): User =
        userRepository.authenticate(User(name, password))
      override def create(name: String, password: String): Unit = userRepository.create(User(name, password))
      override def delete(name: String): Unit = userRepository.delete(User(name, ""))

    }
  }

  // alternative components, say, alternative service implementations

  @Component trait SimpleUserRepositoryComponent extends UserRepositoryComponent {
    @ProvidedService override val userRepository = new UserRepository {
      override def authenticate(user: User): User = { println("[simple] authenticated %s" format user); user }
      override def create(user: User) = println("[simple] created %s" format user)
      override def delete(user: User) = println("[simple] deleted %s" format user)
    }
  }

  object DefaultComponentRegistry extends AnyRef with UserServiceComponent with       UserRepositoryComponent
  object  SimpleComponentRegistry extends AnyRef with UserServiceComponent with SimpleUserRepositoryComponent

  object Main extends App {
    DefaultComponentRegistry.userService.authenticate("Normen", "foobar")
    SimpleComponentRegistry.userService.authenticate("Normen", "foobar")
  }

}
