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
package cypher.features

import java.util.Collection

import cypher.features.ScenarioTestHelper.createTests
import cypher.features.ScenarioTestHelper.printComputedBlacklist
import org.junit.Ignore
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(classOf[JUnitPlatform])
class CostSlottedTCKTests extends TCKTests {

  @TestFactory
  def runCostSlotted(): Collection[DynamicTest] = {
    createTests(scenarios, CostSlottedTestConfig)
  }

  @Ignore
  def generateBlacklistCostSlotted(): Unit = {
    printComputedBlacklist(scenarios, CostSlottedTestConfig)
    fail("Do not forget to add @ignore to this method")
  }

}
