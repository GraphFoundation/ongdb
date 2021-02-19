/*
 * Copyright (c) 2018-2020 "Graph Foundation,"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * This file is part of ONgDB.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiler.v3_4.planner

import java.util

import org.neo4j.cypher.internal.compiler.v3_4.CypherCompilerConfiguration
import org.neo4j.cypher.internal.frontend.v3_4.semantics.SemanticTable
import org.neo4j.cypher.internal.planner.v3_4.spi.{GraphStatistics, StatisticsCompletingGraphStatistics}
import org.neo4j.cypher.internal.util.v3_4.{LabelId, PropertyKeyId, RelTypeId}
import org.neo4j.helpers.collection.{Pair, Visitable}
import org.neo4j.kernel.impl.util.dbstructure.{DbStructureCollector, DbStructureLookup, DbStructureVisitor}

import scala.collection.JavaConverters._
import scala.collection.mutable

case class DbStructureLogicalPlanningConfiguration(cypherCompilerConfig: CypherCompilerConfiguration) {

  def apply(visitable: Visitable[DbStructureVisitor]): LogicalPlanningConfiguration = {
    val collector = new DbStructureCollector
    visitable.accept(collector)
    val lookup = collector.lookup()
    apply(lookup, new DbStructureGraphStatistics(lookup))
  }

  def apply(lookup: DbStructureLookup, underlyingStatistics: GraphStatistics): LogicalPlanningConfiguration = {
    val resolvedLabels: mutable.Map[String, LabelId] = resolveTokens(lookup.labels())(LabelId)
    val resolvedPropertyKeys = resolveTokens(lookup.properties())(PropertyKeyId)
    val resolvedRelTypeNames = resolveTokens(lookup.relationshipTypes())(RelTypeId)

    new RealLogicalPlanningConfiguration(cypherCompilerConfig) {

      override def updateSemanticTableWithTokens(table: SemanticTable) = {
        resolvedPropertyKeys.foreach { case (keyName, keyId) => table.resolvedPropertyKeyNames.put(keyName, PropertyKeyId(keyId)) }
        resolvedLabels.foreach{ case (keyName, keyId) => table.resolvedLabelNames.put(keyName, LabelId(keyId)) }
        resolvedRelTypeNames.foreach{ case (keyName, keyId) => table.resolvedRelTypeNames.put(keyName, RelTypeId(keyId))}
        table
      }

      override val graphStatistics: GraphStatistics =
        new StatisticsCompletingGraphStatistics(underlyingStatistics)

      override val indexes: Set[(String, Seq[String])] = indexSet(lookup.knownIndices())
      override val knownLabels: Set[String] = resolvedLabels.keys.toSet
      override val labelsById: Map[Int, String] = resolvedLabels.map(pair => (pair._2.id -> pair._1)).toMap
      override val uniqueIndexes: Set[(String, Seq[String])] = indexSet(lookup.knownUniqueIndices())
    }
  }

  private def indexSet(indices: util.Iterator[Pair[String, Array[String]]]): Set[(String, Seq[String])] =
    indices.asScala.map { pair => pair.first() -> pair.other().to[Seq] }.toSet

  private def resolveTokens[T](iterator: util.Iterator[Pair[Integer, String]])(f: Int => T): mutable.Map[String, T] = {
    val builder = mutable.Map.newBuilder[String, T]
    while (iterator.hasNext) {
      val next = iterator.next()
      builder += next.other() -> f(next.first())
    }
    builder.result()
  }
}
