// <PROJECT_NAME> | <MODULE_NAME>
// User: Normen Müller (normen.mueller@gmail.com)
// Date: 8/8/12 | 2:05 PM
package mdpm.di4.cake.sca

package v1 {
  class SymbolTable {
    class Name { }          // name specific operations
    class Type { }          // subclasses of Type and type specific operations
    class Symbol { }        // subclasses of Symbol and symbol specific operations
    object definitions {  } // global definitions
    // other elements
  }

  class ScalaCompiler extends SymbolTable { }
}

package v2 {
  trait Names {
    class Name { }
  }
  // The `Types` class contains a class hierarchy rooted in class `Type` as well as operations that relate to types.
  // It comes with an explicit selftype, which is an intersection type of all classes required by `Types`. Besides
  // `Types` itself, these classes are `Names`, `Symbols`, and `Definitions`. Members of these classes are thus
  // accessible in class `Types`.
  trait Types { self: Types with Names with Symbols with Definitions =>
    class Type { }
  }
  trait Symbols { self: Symbols with Names with Types =>
    class Symbol { }

    class TermSymbol extends Symbol
  }
  trait Definitions { self: Definitions with Names with Symbols =>
    object definitions { }
  }

  class SymbolTable extends Names with Types with Symbols with Definitions

  class ScalaCompiler extends SymbolTable /* with Trees with ... */

  abstract class LogSymbols extends Symbols { self: Symbols with Names with Types =>
    val log: java.io.PrintStream

    /* override */ def newTermSymbol(name: Name): TermSymbol = {
      val x: TermSymbol = null // super.newTermSymbol(name);
      log.println("creating term symbol " + name);
      x
    }

    // similarly for all other symbol creations.
  }
}
