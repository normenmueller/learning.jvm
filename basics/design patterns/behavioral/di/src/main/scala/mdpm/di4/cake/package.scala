package mdpm.di4

package object cake {

  // Metadata -------------------------------------------------------------------

  sealed class Component extends scala.annotation.StaticAnnotation
  final class ComposedComponent extends Component

  sealed class Service extends scala.annotation.StaticAnnotation
  final class ProvidedService extends Service
  final class RequiredService extends Service

  sealed class Implementation extends scala.annotation.StaticAnnotation
  final class ServiceImplementation extends Implementation

}
