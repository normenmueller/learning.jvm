package mdpm.di4.cake

// scalastyle:off
/** '''Resources'''
 * <ul>
 *   <li>[[file:///Users/nmueller/Google%20Drive/Documents/Odersky/2005/Scalable%20component%20abstractions/Odersky%20-%202005%20-%20Scalable%20component%20abstractions.pdf Odersky, Scalable Component Abstraction]]</li>
 *   <li>[[file:///Users/nmueller/Google%20Drive/Documents/Boner/2008/Dependency%20Injection/Boner%20-%202008%20-%20Dependency%20Injection.pdf Boner, Dependency Injection]]</li>
 * </ul>
 *
 * '''Introduction'''
 *
 * An important requirement for components is that they are ''reusable''; that is, that they should be applicable in
 * contexts other that the one in which they have been developed.
 *
 * Generally, one requires that component reuse should be possible without modifiying a component's source code. To
 * enable safe reuse, a component needs to have ''interfaces'' for '''provided''' as well as for '''required'''
 * services through which interactions with other components occur. To enable flexible reuse in new contexts, a
 * component should also minimize "hard links" to specific other components which it requires for functioning.
 *
 * For example, let's consider the following [[mdpm.di.v1.UserRepository]] (DAO) and [[mdpm.di.v1.UserService]]
 * implementation:
 *
 * {{{
 * @Service class UserRepository {
 *   def authenticate(user: User): User = ...
 *   def create(user: User) = ...
 *   def delete(user: User) = ...
 * }
 *
 * @Service class UserService {
 *   private val userRepository = new UserRepository // <<< hard wired dependency
 *   def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
 *   def create(name: String, password: String): User = userRepository.create(User(name, password))
 *   def delete(name: String): Unit = userRepository.delete(User(name, ""))
 * }
 * }}}
 *
 * Here we can see that we are referencing an instance of the [[mdpm.di.v1.UserRepository]]. This is the dependency
 * that we would like to have injected for us, that is, we want to remove the "hard link".
 *
 * Unfortunately most mainstream languages (e.g. Java, C#) lack the capability to abstract over the services that are
 * required ([[mdpm.di.v1.UserService]] requires [[mdpm.di.v1.UserRepository]]). Consequently, most software modules
 * are written with hard references to required modules. It is then not possible to reuse a module in a new context
 * that refines or refactors some of those required modules (e.g. reusing [[mdpm.di.v1.UserService]] within a new
 * context refining ''UserRepository'').
 *
 * '''Goal'''
 * <blockquote>
 *   Ideally, it should be possible to lift an arbitrary system of software components with static data and hard
 *   references, resulting in a system with the same structure, but with neither static data nor hard references. The
 *   result of such a lifting should create components that are first-class values.
 * </blockquote>
 *
 * '''Solution'''
 * <ul>
 *   <li>''Abstract type members''<br/> provide a flexible way to '''abstract over concrete types''' of components</li>
 *
 *   <li>''Selftype annotations''<br/> allow one to attach a programmer defined type to `this`. This turns out to be
 *   a convenient way to '''express required services''' of a component at the level where it connects with other
 *   components.</li>
 *
 *   <li>''Modular mixin composition''<br/> provides a flexible way to '''compose components''' and component
 *   types</li>
 * </ul>
 *
 * '''Scala's constructs for type abstraction'''
 *
 * There are two principal forms of abstraction in programming languages:
 * <ul>
 *   <li>parameterization --> FP</li>
 *   <li>abstract members --> OO</li>
 * </ul>
 *
 * Scala supports both styles of abstraction uniformly for types as well as values. The utilized language features are
 * ''abstract type member'', ''abstract value member'', ''path-dependent types'', ''type selection'', ''singleton
 * types'', and ''parameter bounds''.
 *
 * '''Scala's constructs for class composition'''
 *
 * Mixin class composition in Scala is a fusion of the object-oriented, linear mixin composition and the more
 * symmetric approaches of mixin modules and traits. The utilized language features are ''class linearization'',
 * ''membership'', ''super calls''.
 *
 * '''Scala's constructs for dependency declaration'''
 *
 * Scala's selftype annotations provide an alternative way [to mixin-class composition] of associating a class with
 * an abstract type. Note, selftypes turned out to be the key construct for lifting static systems to component-based
 * systems.
 *
 * `=>` These constructs for type abstraction, composition, and dependency declaration form the basis of
 * '''service-oriented software component model'''.
 *
 * '''Service-Oriented Component Model'''
 *
 * _Software components are units of computation that provide a well-defined set of services_. Typically, a software
 * component is not self-contained, i.e., its service implementations rely on a set of required services provided by
 * other cooperating components.
 *
 * In our model
 * <table border="1" width="100%" cellspacing="10">
 *   <tr><td>Software components</td><td>correspond to</td><td>classes / traits</td></tr>
 *   <tr><td>Concrete members of a class</td><td>represent provided</td><td>services</td></tr>
 *   <tr><td>Abstract members of a class</td><td>represent required</td><td>services</td></tr>
 *   <tr><td>Component composition</td><td>is based on</td><td>mixins</td></tr>
 * </table>
 *
 * So, back to our `UserRepository` example, let's a create respective software components (recall, software
 * _components_ are units of computation that ''provide'' a well-defined set of _services_).  `UserRepository` is a
 * service. So, in order to stick to the definition of terms, we need a _software component_ that provides this
 * service. In addition, recall the fact, that in our model, software components correspond to classes/ traits
 * (e.g. [[mdpm.di.v2.UserRepositoryComponent]]) and concrete members (e.g. `userRepository`) represent
 * _provided services_.
 *
 * {{{
 * @Component trait UserRepositoryComponent {
 *   @ProvidedService val userRepository = new UserRepository
 *
 *   @ServiceImplementation private [UserRepositoryComponent] class UserRepository {
 *     def authenticate(user: User): User = { println("authenticate %s" format user); user }
 *     def create(user: User) = println("create %s" format user)
 *     def delete(user: User) = println("delete %s" format user)
 *   }
 * }
 * }}}
 *
 * Now let's look at the `UserService`, the user of the repository. In order to declare that we would like to
 * have the `userRepository` instance injected in the `UserService` we will first do what we did with the
 * repository above; wrap the it in an enclosing (namespace) trait (a software component, say;
 * [[mdpm.di.v2.UserServiceComponent]]) and use a selftype annotation to declare our need for the `UserRepository`
 * service.
 *
 * {{{
 * @Component trait UserServiceComponent { self: UserRepositoryComponent @RequiredService =>
 *   @ProvidedService val userService = new UserService
 *
 *   @ServiceImplementation private [UserServiceComponent] class UserService {
 *     def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
 *     def create(name: String, password: String): User = { val user = User(name, password); userRepository.create(User(name, password)); user }
 *     def delete(name: String): Unit = userRepository.delete(User(name, ""))
 *   }
 * }
 * }}}
 *
 * Now we have declared the `UserRepository` dependency. What is left is the actual wiring. In order to do that the
 * only thing we need to do is to merge/join the different namespaces (software components) into one single
 * application (or module) namespace. This is done by creating a ''module'' object composed of all our components.
 * When we do that all wiring is happening automatically.
 *
 * N.B. An _object_ whose primary purpose is giving its members a namespace is sometimes called a ''module''.
 *
 * {{{
 *   @ComposedComponent object ComponentRegistry extends AnyRef
 *     with UserServiceComponent
 *     with UserRepositoryComponent
 *
 *   object Main extends App {
 *     import ComponentRegistry._
 *
 *     userService.authenticate("Normen", "foobar")
 *   }
 * }}}
 *
 * So far so good? Well, no. We have strong coupling between the service implementation and its creation,
 * the wiring configuration is scattered all over our code base; utterly inflexible.
 *
 * Instead of instantiating the services in their enclosing component trait, let's change it to an abstract
 * member field.
 *
 * {{{
 * @Component trait UserRepositoryComponent {
 *   @ProvidedService val userRepository: UserRepository
 *
 *   @ServiceImplementation class UserRepository {
 *     def authenticate(user: User): User = { println("authenticate %s" format user); user }
 *     def create(user: User) = println("create %s" format user)
 *     def delete(user: User) = println("delete %s" format user)
 *   }
 * }
 * @Component trait UserServiceComponent { self: UserRepositoryComponent @RequiredService =>
 *   @ProvidedService val userService: UserService
 *
 *   @ServiceImplementation class UserService {
 *     def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
 *     def create(name: String, password: String): User = { val user = User(name, password); userRepository.create(User(name, password)); user }
 *     def delete(name: String): Unit = userRepository.delete(User(name, ""))
 *   }
 * }
 * }}}
 *
 * Now, we can move the instantiation (and configuration) of the services to the `ComponentRegistry` module.
 * _The neat thing is that we can here switch between different implementations of the services_.
 *
 * {{{
 * @ComposedComponent object ComponentRegistry extends AnyRef
 *   with UserServiceComponent
 *   with UserRepositoryComponent {
 *     @ProvidedService val userRepository  = new UserRepository @ServiceImplementation
 *     @ProvidedService val userService     = new UserService    @ServiceImplementation
 * }
 * }}}
 *
 * Finally we demonstrate Scala's fully mighty in implementing a '''Service-Oriented Component Model'''
 *
 * {{{
 * @Component trait UserRepositoryComponent {
 *   @ProvidedService def userRepository: UserRepository = new DefaultUserRepository  *
 *   // provided service specification
 *   @Service trait UserRepository {
 *     def authenticate(user: User): User
 *     def create(user: User)
 *     def delete(user: User)
 *   }
 *   // provided default service implementation
 *   @ServiceImplementation private class DefaultUserRepository extends UserRepository {
 *     override def authenticate(user: User): User = { println("[default] authenticated %s" format user); user }
 *     override def create(user: User) = println("[default] created %s" format user)
 *     override def delete(user: User) = println("[default] deleted %s" format user)
 *   }
 * }
 * @Component trait UserServiceComponent { self: UserRepositoryComponent @RequiredService =>
 *   @ProvidedService def userService: UserService = new DefaultUserService
 *   // provided service specification
 *   @Service trait UserService {
 *     def authenticate(name: String, password: String): User
 *     def create(name: String, password: String): User
 *     def delete(name: String): Unit
 *   }
 *   // provided default service implementation
 *   @ServiceImplementation private class DefaultUserService extends UserService {
 *     override def authenticate(name: String, password: String): User = userRepository.authenticate(User(name, password))
 *     override def create(name: String, password: String): User = { val user = User(name, password); userRepository.create(User(name, password)); user }
 *     override def delete(name: String): Unit = userRepository.delete(User(name, ""))
 *   }
 * }
 * // an alternative component, say, alternative service implementations
 * @Component trait SimpleUserRepositoryComponent extends UserRepositoryComponent {
 *   @ProvidedService override val userRepository = new UserRepository {
 *     override def authenticate(user: User): User = { println("[simple] authenticated %s" format user); user }
 *     override def create(user: User) = println("[simple] created %s" format user)
 *     override def delete(user: User) = println("[simple] deleted %s" format user)
 *   }
 * }
 *
 * object DefaultComponentRegistry extends AnyRef with UserServiceComponent with UserRepositoryComponent
 *
 * object SimpleComponentRegistry extends AnyRef with UserServiceComponent with SimpleUserRepositoryComponent
 *
 * object Main extends App {
 *   DefaultComponentRegistry.userService.authenticate("Normen", "foobar")
 *   SimpleComponentRegistry.userService.authenticate("Normen", "foobar")
 * }
 * }}}
 *
 * Author: Normen Müller (normen.mueller@gmail.com) | Date: 8/7/12 11:12 AM
 */
// scalastyle:on
package object ex1
