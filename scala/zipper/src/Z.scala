package xpp.xm.zipper

sealed abstract class Tree[A]
case class Fork[A](left: Tree[A], right: Tree[A]) extends Tree[A]
case class Leaf[A](value: A) extends Tree[A]

sealed abstract class Cxt[A]
case class Top[A]() extends Cxt[A]
case class L[A](c: Cxt[A], t: Tree[A]) extends Cxt[A]
case class R[A](t: Tree[A], c: Cxt[A]) extends Cxt[A]

sealed case class Loc[A](t: Tree[A], c: Cxt[A])

object Zipper {
  
  def left[A](l: Loc[A]): Loc[A] = l match {
    case Loc(Fork(l,r),c) => Loc(l, L(c,r))
    case _                => error("leaf")
  }
  
  def right[A](l: Loc[A]): Loc[A] = l match {
    case Loc(Fork(l,r),c) => Loc(r, R(l,c))
    case _                => error("leaf")
  }
  
  def top[A](t: Tree[A]): Loc[A]  = Loc(t, Top[A])
  
  def up[A](l: Loc[A]): Loc[A] = l match {
    case Loc(t,L(c,r)) => Loc(Fork(t,r),c)
    case Loc(t,R(l,c)) => Loc(Fork(l,t),c)
    case Loc(t,_)      => error("top")
  }
  
  def upmost[A](l: Loc[A]): Loc[A] = l match {
    case Loc(t, Top()) => l
    case _             => upmost(up(l))
  }
  
  def modify[A](l: Loc[A])(f: Tree[A] => Tree[A]): Loc[A] = l match {
    case Loc(t,c) => Loc(f(t), c)
  }

  def main(args : Array[String]) : Unit = {
    val tl = Fork(Leaf(1), Leaf(2))
    val tr = Fork(Leaf(3), Leaf(4))
    val t = Fork(tl, tr)
    
    val c2 = R(Leaf(1), L(Top[Int], Fork(Leaf(3), Leaf(4))))
    
    assert(Loc(Leaf(2), c2) == (right[Int] _ compose left[Int] compose top[Int])(t))
    
    assert(Fork(Fork(Leaf(1), Leaf(0)), tr) ==
      upmost(
        modify((right[Int] _ compose left[Int] compose top[Int])(t))(_ => Leaf(0))
      ).t
    )
  }
  
}