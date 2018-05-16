package mdpm.di4.cake.sca

// Publish/Subscribe pattern

abstract class SubjectObserver {

  type S <: Subject
  type O <: Observer

  abstract class Subject {
    self: S =>

    private var observers: List[O] = Nil

    def subscribe(o: O): Unit = observers = o :: observers

    def publish: Unit = observers foreach (_.notify(self))
  }

  abstract class Observer {
    self: O =>

    def notify(s: S): Unit
  }

}



