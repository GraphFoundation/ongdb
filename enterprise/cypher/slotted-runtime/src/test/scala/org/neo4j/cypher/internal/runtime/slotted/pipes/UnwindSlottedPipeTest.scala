/*
 * Copyright (c) 2018-2020 "Graph Foundation,"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * This file is part of ONgDB Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) as found
 * in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 */
/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.runtime.slotted.pipes

import org.neo4j.cypher.internal.compatibility.v3_4.runtime.SlotConfiguration
import org.neo4j.cypher.internal.runtime.interpreted.QueryStateHelper
import org.neo4j.cypher.internal.runtime.slotted.SlottedExecutionContext
import org.neo4j.cypher.internal.runtime.slotted.expressions.ReferenceFromSlot
import org.neo4j.cypher.internal.util.v3_4.symbols._
import org.neo4j.cypher.internal.util.v3_4.test_helpers.CypherFunSuite
import org.neo4j.values.storable.Values.intValue
import org.neo4j.values.virtual.VirtualValues.list

import scala.collection.JavaConverters._

class UnwindSlottedPipeTest extends CypherFunSuite {

  private def unwindWithInput(data: Traversable[Map[String, Any]]) = {
    val inputPipeline = SlotConfiguration
      .empty
      .newReference("x", nullable = false, CTAny)

    val outputPipeline = inputPipeline
      .copy()
      .newReference("y", nullable = true, CTAny)

    val x = inputPipeline.getReferenceOffsetFor("x")
    val y = outputPipeline.getReferenceOffsetFor("y")

    val source = FakeSlottedPipe(data.toIterator, inputPipeline)
    val unwindPipe = UnwindSlottedPipe(source, ReferenceFromSlot(x), y, outputPipeline)()
    unwindPipe.createResults(QueryStateHelper.empty).map {
      case c: SlottedExecutionContext =>
        Map("x" -> c.getRefAt(x), "y" -> c.getRefAt(y))
    }.toList
  }

  test("should unwind collection of numbers") {
    unwindWithInput(List(Map("x" -> List(1, 2)))) should equal(List(
      Map("y" -> intValue(1), "x" -> list(intValue(1), intValue(2))),
      Map("y" -> intValue(2), "x" -> list(intValue(1), intValue(2)))))
  }

  test("should handle null") {
    unwindWithInput(List(Map("x" -> null))) should equal(List())
  }

  test("should handle collection of collections") {

    val listOfLists = List(
      List(1, 2, 3).asJava,
      List(4, 5, 6).asJava
    ).asJava

    val listValue = list(
      list(intValue(1), intValue(2), intValue(3)),
      list(intValue(4), intValue(5), intValue(6))
    )

    unwindWithInput(List(Map(
      "x" -> listOfLists))) should equal(

      List(
        Map("y" -> list(intValue(1), intValue(2), intValue(3)), "x" -> listValue),
        Map("y" -> list(intValue(4), intValue(5), intValue(6)), "x" -> listValue)))
  }
}
