/*
 *  Copyright 2021 Magnus Madsen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ca.uwaterloo.flix.language.errors

import ca.uwaterloo.flix.api.Flix
import ca.uwaterloo.flix.language.CompilationMessage
import ca.uwaterloo.flix.language.ast.{SourceLocation, Type}
import ca.uwaterloo.flix.language.fmt.{Audience, FormatType}
import ca.uwaterloo.flix.util.Formatter

/**
  * A common super-type for reification errors.
  */
sealed trait ReificationError extends CompilationMessage {
  val kind: String = "Monomorph Error"
}

object ReificationError {

  private implicit val audience: Audience = Audience.External

  /**
    * An error raised to indicate an internal error in the Monomorpher.
    *
    * @param tpe the problematic Boolean type.
    * @param loc the location of the Boolean type.
    */
  case class UnexpectedNonConstBool(tpe: Type, loc: SourceLocation)(implicit flix: Flix) extends ReificationError {
    def summary: String = "Unexpected type."

    def message(formatter: Formatter): String = {
      import formatter._
      s"""${line(kind, source.name)}
         |>> Unexpected Boolean type: '${red(FormatType.formatType(tpe))}'.
         |
         |${code(loc, "unexpected Boolean type.")}
         |""".stripMargin
    }

    /**
      * Returns a formatted string with helpful suggestions.
      */
    def explain(formatter: Formatter): Option[String] = None
  }
}
