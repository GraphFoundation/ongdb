/*
 * Copyright (c) 2002-2019 "Neo4j,"
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
package org.neo4j.cypher.internal

import java.util.concurrent.ConcurrentHashMap

import org.neo4j.cypher.internal.planning.CypherPlanner
import org.neo4j.cypher.{CypherPlannerOption, CypherRuntimeOption, CypherUpdateStrategy, CypherVersion}

import scala.collection.JavaConversions._

/**
  * Keeps track of all cypher compilers, and finds the relevant compiler for a preparsed query.
  *
  * @param factory factory to create compilers
  */
class CompilerLibrary(factory: CompilerFactory, executionEngineProvider: () => ExecutionEngine) {

  private val compilers = new ConcurrentHashMap[CompilerKey, Compiler]

  def selectCompiler(cypherVersion: CypherVersion,
                     cypherPlanner: CypherPlannerOption,
                     cypherRuntime: CypherRuntimeOption,
                     cypherUpdateStrategy: CypherUpdateStrategy): Compiler = {
    val key = CompilerKey(cypherPlanner, cypherRuntime, cypherUpdateStrategy)
    compilers.computeIfAbsent(key, ignore => factory.createCompiler(cypherVersion, cypherPlanner, cypherRuntime, cypherUpdateStrategy, executionEngineProvider))
  }

  def clearCaches(): Long = {
    val numClearedEntries =
      compilers.values().collect {
        case c: CypherPlanner => c.clearCaches()
        case c: CypherCurrentCompiler[_] if c.planner.isInstanceOf[CypherPlanner] => c.planner.clearCaches()
      }

    if (numClearedEntries.nonEmpty)
      numClearedEntries.max
    else 0
  }

  case class CompilerKey(cypherPlanner: CypherPlannerOption,
                         cypherRuntime: CypherRuntimeOption,
                         cypherUpdateStrategy: CypherUpdateStrategy)
}
