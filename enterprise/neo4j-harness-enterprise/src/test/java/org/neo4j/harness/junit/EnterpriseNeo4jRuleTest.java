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
package org.neo4j.harness.junit;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.harness.extensionpackage.MyEnterpriseUnmanagedExtension;
import org.neo4j.kernel.configuration.Settings;
import org.neo4j.kernel.configuration.ssl.LegacySslPolicyConfig;
import org.neo4j.kernel.impl.enterprise.configuration.OnlineBackupSettings;
import org.neo4j.server.configuration.ServerSettings;
import org.neo4j.test.rule.SuppressOutput;
import org.neo4j.test.server.HTTP;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.neo4j.server.ServerTestUtils.getRelativePath;
import static org.neo4j.server.ServerTestUtils.getSharedTestTemporaryFolder;

public class EnterpriseNeo4jRuleTest
{
    @Rule
    public Neo4jRule neo4j = new EnterpriseNeo4jRule()
            .withConfig( LegacySslPolicyConfig.certificates_directory.name(),
                    getRelativePath( getSharedTestTemporaryFolder(), LegacySslPolicyConfig.certificates_directory ) )
            .withConfig( ServerSettings.script_enabled, Settings.TRUE )
            .withExtension( "/test", MyEnterpriseUnmanagedExtension.class )
            .withConfig( OnlineBackupSettings.online_backup_enabled, Settings.FALSE )
            .withConfig( ServerSettings.script_enabled, Settings.TRUE );

    @Rule
    public SuppressOutput suppressOutput = SuppressOutput.suppressAll();

    @Test
    public void shouldExtensionWork()
    {
        // Given running enterprise server
        String doSomethingUri = neo4j.httpURI().resolve( "test/myExtension/doSomething" ).toString();

        // When I run this test

        // Then
        HTTP.Response response = HTTP.GET( doSomethingUri );
        assertThat( response.status(), equalTo( 234 ) );
    }

    @Test
    public void testPropertyExistenceConstraintCanBeCreated()
    {
        // Given running enterprise server
        String createConstraintUri = neo4j.httpURI().resolve( "test/myExtension/createConstraint" ).toString();

        // When I run this server

        // Then constraint should be created
        HTTP.Response response = HTTP.GET( createConstraintUri );
        assertThat( response.status(), equalTo( HttpStatus.CREATED_201 ) );
    }
}
