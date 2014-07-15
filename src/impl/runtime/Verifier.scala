package impl.runtime

import impl.logic.Symbol.{PredicateSymbol => PSym, VariableSymbol => VSym}
import impl.logic._
import syntax.Symbols._

/**
 * A verifier / type checker.
 */
class Verifier(val program: Program) {

  /**
   * Verifies that the program is safe.
   */
  def verify(): Unit = {

    println("Proof Burdens")
    // TODO: Need better access to lattices
    for ((p, i) <- program.interpretation) {
      i match {
        case Interpretation.LatticeMap(lattice) => {


          println("~~~~~~~~")
          println(genRelation2(lattice.leq).fmt)
          println(getFunction2(lattice.join).fmt)
          println("~~~~~~~~")

          println(reflexivity(p, lattice.leq))
          println(antiSymmetri(p, lattice.leq))

        }
        case _ => // nop
      }
    }

    println()
    println()
  }

  /**
   * Reflexivity: ∀x. x ⊑ x
   */
  def reflexivity(sort: PSym, leq: PSym): String = smt"""
    |;; Reflexivity: ∀x. x ⊑ x
    |(define-fun reflexivity () Bool
    |    (forall ((x $sort))
    |        ($leq x x)))
    """.stripMargin

  /**
   * Anti-symmetri: ∀x, y. x ⊑ y ∧ x ⊒ y ⇒ x = y
   */
  def antiSymmetri(sort: PSym, leq: PSym): String = smt"""
    |;; Anti-symmetri: ∀x, y. x ⊑ y ∧ x ⊒ y ⇒ x = y
    |(define-fun anti-symmetri () Bool
    |    (forall ((x $sort) (y $sort))
    |        (=>
    |            (and ($leq x y)
    |                 ($leq y x))
    |            (= x y))))
    """.stripMargin


  def genDatatype: String = ???

  /**
   * Returns an SMT formula for binary function defined by the given predicate symbol `s`.
   */
  def genRelation2(s: PSym): Declaration = {
    val clauses = program.clauses.filter(_.head.name == s)

    val p = Predicate(s, List(Term.Variable("x"), Term.Variable("y")))
    val formulae = SmtFormula.Disjunction(clauses.map {
      h => Unification.unify(h.head, p, Map.empty[VSym, Term]) match {
        case None => SmtFormula.True // nop
        case Some(env) =>
          if (h.isFact)
            genEnv(env)
          else
            SmtFormula.True // TODO
      }
    })

    Declaration.Relation2(s, "x", "y", formulae)
  }


  def getFunction2(s: PSym): Declaration = {
    val clauses = program.clauses.filter(_.head.name == s)

    val p = Predicate(s, List(Term.Variable("x"), Term.Variable("y"), Term.Variable("z")))
    val formulae = SmtFormula.Disjunction(clauses.map {
      h => Unification.unify(h.head, p, Map.empty[VSym, Term]) match {
        case None => SmtFormula.True // nop
        case Some(env) =>
          if (h.isFact)
            genEnv(env)
          else
            SmtFormula.True // TODO
      }
    })

    Declaration.Function3(s, "x", "y", "z", formulae)
  }


  /**
   * TODO: DOC
   */
  def genEnv(env: Map[VSym, Term]): SmtFormula = SmtFormula.Conjunction(env.toList.map {
    case (v, t) => t match {
      case Term.Bool(true) => SmtFormula.True
      case Term.Variable(_) => SmtFormula.True
      case Term.Constructor0(s) => SmtFormula.Eq(SmtFormula.Variable(v), SmtFormula.Constructor0(s))
    }
  })


  /**
   * An SMT-LIB declaration.
   */
  sealed trait Declaration {
    def fmt: String = this match {
      case Declaration.Relation2(s, var1, var2, formula) => {
        smt"""(define-fun $s (($var1 Sign) ($var2 Sign)) Bool
              |    ${formula.fmt(2)})
           """.stripMargin
      }

      case Declaration.Function3(s, var1, var2, var3, formula) => {
        smt"""(define-fun $s (($var1 Sign) ($var2 Sign) ($var3)) Bool
              |    ${formula.fmt(2)})
           """.stripMargin
      }
    }
  }

  object Declaration {

    // TODO: Need sort
    case class Relation2(name: Symbol.PredicateSymbol, var1: String, var2: String, formula: SmtFormula) extends Declaration

    case class Function3(name: Symbol.PredicateSymbol, var1: String, var2: String, var3: String, formula: SmtFormula) extends Declaration


  }

  /**
   * An SMT-LIB formula.
   */
  sealed trait SmtFormula {
    def fmt(indent: Int): String = this match {
      case SmtFormula.True => "true"
      case SmtFormula.False => "false"
      case SmtFormula.Variable(s) => s.fmt
      case SmtFormula.Constructor0(s) => s.fmt
      case SmtFormula.Conjunction(formulae) =>
        "(and " + formulae.map(_.fmt(indent)).mkString(" ") + ")"
      case SmtFormula.Disjunction(formulae) =>
        "(or \n" + "    " * indent + formulae.map(_.fmt(indent + 1)).mkString("\n" + "     " * indent) + ")"
      case SmtFormula.Eq(lhs, rhs) => "(= " + lhs.fmt(indent) + " " + rhs.fmt(indent) + ")"

    }
  }

  object SmtFormula {

    /**
     * The true literal.
     */
    case object True extends SmtFormula

    /**
     * The false literal.
     */
    case object False extends SmtFormula

    /**
     * A variable.
     */
    case class Variable(v: Symbol.VariableSymbol) extends SmtFormula

    /**
     * A null-ary constructor.
     */
    case class Constructor0(s: Symbol.NamedSymbol) extends SmtFormula

    /**
     * A equality formula.
     */
    case class Eq(left: SmtFormula, right: SmtFormula) extends SmtFormula

    /**
     * A conjunction of formulae.
     */
    case class Conjunction(formulae: List[SmtFormula]) extends SmtFormula

    /**
     * A disjunction of formulae.
     */
    case class Disjunction(formulae: List[SmtFormula]) extends SmtFormula

    /**
     * An implication of antecedent => consequent.
     */
    case class Implication(antecedent: SmtFormula, consequent: SmtFormula) extends SmtFormula

    // TODO
    case class ForAll() extends SmtFormula

    // TODO
    case class Exists() extends SmtFormula

  }

  /**
   * A string interpolator which takes symbols into account.
   */
  implicit class SmtSyntaxInterpolator(sc: StringContext) {
    def smt(args: Any*): String = {
      def format(a: Any): String = a match {
        case x: Symbol => x.fmt
        case x => x.toString
      }

      val pi = sc.parts.iterator
      val ai = args.iterator
      val bldr = new java.lang.StringBuilder(pi.next())
      while (ai.hasNext) {
        bldr append format(ai.next())
        bldr append pi.next()
      }
      bldr.toString
    }
  }

}
