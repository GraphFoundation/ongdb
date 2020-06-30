/*
 * Copyright (c) 2002-2018 "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 *   This file is part of ONgDB.
 *
 *   ONgDB is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.neo4j.cypher.internal.runtime.slotted.expressions

import org.neo4j.cypher.internal.runtime.ExecutionContext
import org.neo4j.cypher.internal.runtime.interpreted.commands.AstNode
import org.neo4j.cypher.internal.runtime.interpreted.commands.expressions.Expression
import org.neo4j.cypher.internal.runtime.interpreted.commands.expressions.Property
import org.neo4j.cypher.internal.runtime.interpreted.commands.values.KeyToken
import org.neo4j.cypher.internal.runtime.interpreted.pipes.QueryState
import org.neo4j.values.AnyValue
import org.neo4j.values.virtual.NodeValue
import org.neo4j.values.virtual.RelationshipValue

case class MaterializedEntityProperty(mapExpr: Expression, propertyKey: KeyToken) extends Expression
                                                                                  with SlottedExpression {

  val property: Property = Property(mapExpr, propertyKey)

  override def apply(ctx: ExecutionContext, state: QueryState): AnyValue = {
    val value: AnyValue = mapExpr.apply(ctx, state)

    value match {
      case value1: NodeValue =>
        value1.properties.get(propertyKey.name)
      case value1: RelationshipValue =>
        value1.properties.get(propertyKey.name)
      case _ =>
        property.apply(ctx, state)
    }

  }

  override def children: Seq[AstNode[_]] = {
    // TODO: Implement
    Seq.empty
  }
}
