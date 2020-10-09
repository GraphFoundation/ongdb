/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package org.neo4j.cypher.internal.runtime.compiled.codegen.ir.expressions

import org.neo4j.cypher.internal.runtime.compiled.codegen.CodeGenContext
import org.neo4j.cypher.internal.runtime.compiled.codegen.spi.MethodStructure
import org.neo4j.cypher.internal.v3_5.util.symbols._

case class Addition(lhs: CodeGenExpression, rhs: CodeGenExpression) extends CodeGenExpression with BinaryOperator {

  override protected def generator[E](structure: MethodStructure[E])(implicit context: CodeGenContext) = structure.addExpression

  override def nullable(implicit context: CodeGenContext) = lhs.nullable || rhs.nullable

  override def name: String = "add"

  override def codeGenType(implicit context: CodeGenContext) = (lhs.codeGenType.ct, rhs.codeGenType.ct) match {

    // Collections
    case (ListType(left), ListType(right)) =>
      CypherCodeGenType(ListType(left leastUpperBound right), ReferenceType)
    case (ListType(innerType), singleElement) =>
      CypherCodeGenType(ListType(innerType leastUpperBound singleElement), ReferenceType)
    case (singleElement, ListType(innerType)) =>
      CypherCodeGenType(ListType(innerType leastUpperBound singleElement), ReferenceType)

    case (CTAny, _) => CypherCodeGenType(CTAny, ReferenceType)
    case (_, CTAny) => CypherCodeGenType(CTAny, ReferenceType)

    // Strings
    case (CTString, _) => CypherCodeGenType(CTString, ReferenceType)
    case (_, CTString) => CypherCodeGenType(CTString, ReferenceType)

    // Numbers
    case (CTInteger, CTInteger) => CypherCodeGenType(CTInteger, ReferenceType)
    case (Number(_), Number(_)) => CypherCodeGenType(CTFloat, ReferenceType)

    // Runtime we'll figure it out
    case _ => CodeGenType.Any
  }
}
