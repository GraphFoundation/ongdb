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
package org.neo4j.causalclustering.core.consensus.explorer.action;

import org.junit.Test;

import org.neo4j.causalclustering.core.consensus.RaftMessages.Timeout.Election;
import org.neo4j.causalclustering.core.consensus.RaftMessages.Timeout.Heartbeat;
import org.neo4j.causalclustering.core.consensus.explorer.ClusterState;

import static org.junit.Assert.assertEquals;
import static org.neo4j.causalclustering.identity.RaftTestMember.member;
import static org.neo4j.helpers.collection.Iterators.asSet;

public class OutOfOrderDeliveryTest
{
    @Test
    public void shouldReOrder() throws Exception
    {
        // given
        ClusterState clusterState = new ClusterState( asSet( member( 0 ) ) );
        clusterState.queues.get( member( 0 ) ).add( new Election( member( 0 ) ) );
        clusterState.queues.get( member( 0 ) ).add( new Heartbeat( member( 0 ) ) );

        // when
        ClusterState reOrdered = new OutOfOrderDelivery( member( 0 ) ).advance( clusterState );

        // then
        assertEquals( new Heartbeat( member( 0 ) ), reOrdered.queues.get( member( 0 ) ).poll() );
        assertEquals( new Election( member( 0 ) ), reOrdered.queues.get( member( 0 ) ).poll() );
    }
}
