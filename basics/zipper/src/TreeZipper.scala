package zipper

sealed abstract class Tree[+T]
case class Leaf[+T](v: T) extends Tree[T]
case class Node[+T](l: Tree[T], r: Tree[T]) extends Tree[T]

/* Left c t represents the left part of a branch of which the right part was t and whose parent had context c.
 * The Right constructor is similar. Top represents the top of a tree. */
sealed abstract class Context[+T]
case object Top extends Context[Nothing]
case class Left[+T](c: Context[T], t: Tree[T]) extends Context[T]
case class Right[+T](t: Tree[T], c: Context[T]) extends Context[T]

case class TreeZipper[+T](context: Context[T], focus: Tree[T]) {
  def left: TreeZipper[T] = (context,focus) match {
    case (Right(t1,p),t2) => TreeZipper(Left(p,t2),t1)
    case _ => error("cannot go left")
  }
  def right: TreeZipper[T] = (context,focus) match {
    case (Left(p,t2),t1) => TreeZipper(Right(t1,p),t2)
    case _ => error("cannot go right")
  }
  def up: TreeZipper[T] = (context,focus) match {
    case (Left(p,t2),t1) => TreeZipper(p,Node[T](t1,t2))
    case (Right(t1,p),t2) => TreeZipper(p,Node[T](t1,t2))
    case _ => error("cannot go up")
  }
  def down_left: TreeZipper[T] = (context,focus) match {
    case (p,Node(t1,t2)) => TreeZipper(Left(p,t2),t1)
    case _ => error("cannot go down")
  }
  def down_right: TreeZipper[T] = (context,focus) match {
    case (p,Node(t1,t2)) => TreeZipper(Right(t1,p),t2)
    case _ => error("cannot go down")
  }
  def remove: TreeZipper[T] = (context,focus) match {
    case (Left(p,t2),_) => TreeZipper(p,t2)
    case (Right(t1,p),_) => TreeZipper(p,t1)
    case _ => error("cannot remove the root node")
  }
  def replace_subtree[S >: T](tr: Tree[S]): TreeZipper[S] = (context,focus) match {
    case (p,_) => TreeZipper(p,tr)
  }
}

object zip {
  def apply[T](t: Tree[T]) = new TreeZipper(Top,t)
}

object unzip {
  def apply[T](z: TreeZipper[T]) = tree(z)
  private def tree[T](z: TreeZipper[T]): Tree[T] = (z.context,z.focus) match {
    case (Left(p,t1),t2) => tree(TreeZipper(p, Node(t2,t1)))
    case (Right(t1,p),t2) => tree(TreeZipper(p, Node(t1,t2)))
    case (Top,t) => t
  } 
}

object TreeZipperMain {  
  def main(args: Array[String]) {
    val t: Tree[Int] = Node[Int](Node[Int](Leaf[Int](1), Leaf[Int](2)), Node[Int](Leaf[Int](3), Leaf[Int](4)))
    println(t)
    println(unzip(zip(t).down_left.down_right.remove))
    println(unzip(zip(t).down_left.down_right.replace_subtree(Leaf(5))))    
  }  
}
