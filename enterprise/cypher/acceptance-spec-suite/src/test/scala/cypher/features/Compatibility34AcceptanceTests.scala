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
package cypher.features

import java.util

import cypher.features.ScenarioTestHelper.{createTests, printComputedBlacklist}
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.{Disabled, DynamicTest, TestFactory}
import org.neo4j.test.TestEnterpriseGraphDatabaseFactory

class Compatibility34AcceptanceTests extends EnterpriseBaseAcceptanceTest {

  // If you want to only run a specific feature or scenario, go to the BaseAcceptanceTest

  @TestFactory
  def runCompatibility34(): util.Collection[DynamicTest] = {
    createTests(scenarios, Compatibility34TestConfig, new TestEnterpriseGraphDatabaseFactory())
  }

  @Disabled
  def generateBlacklistCompatibility34(): Unit = {
    printComputedBlacklist(scenarios, Compatibility34TestConfig, new TestEnterpriseGraphDatabaseFactory())
    fail("Do not forget to add @Disabled to this method")
  }
}
