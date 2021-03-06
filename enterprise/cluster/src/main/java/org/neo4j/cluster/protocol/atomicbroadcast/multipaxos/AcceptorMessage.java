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
package org.neo4j.cluster.protocol.atomicbroadcast.multipaxos;

import java.io.Serializable;

import org.neo4j.cluster.com.message.MessageType;
import org.neo4j.cluster.protocol.atomicbroadcast.AtomicBroadcastSerializer;
import org.neo4j.cluster.protocol.atomicbroadcast.ObjectStreamFactory;
import org.neo4j.cluster.protocol.atomicbroadcast.Payload;

/**
 * Acceptor state machine messages
 */
public enum AcceptorMessage
        implements MessageType
{
    failure,
    join, leave,
    prepare, // phase 1a/1b
    accept; // phase 2a/2b - timeout if resulting learn is not fast enough

    public static class PrepareState
            implements Serializable
    {
        private static final long serialVersionUID = 7179066752672770593L;

        private long ballot;

        public PrepareState( long ballot )
        {
            this.ballot = ballot;
        }

        public long getBallot()
        {
            return ballot;
        }

        @Override
        public String toString()
        {
            return "PrepareState{" +
                    "ballot=" + ballot +
                    '}';
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            PrepareState that = (PrepareState) o;

            return ballot == that.ballot;
        }

        @Override
        public int hashCode()
        {
            return (int) (ballot ^ (ballot >>> 32));
        }
    }

    public static class AcceptState
            implements Serializable
    {
        private static final long serialVersionUID = -5510569299948660967L;

        private long ballot;
        private Object value;

        public AcceptState( long ballot, Object value )
        {
            this.ballot = ballot;
            this.value = value;
        }

        public long getBallot()
        {
            return ballot;
        }

        public Object getValue()
        {
            return value;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            AcceptState that = (AcceptState) o;

            if ( ballot != that.ballot )
            {
                return false;
            }
            return value != null ? value.equals( that.value ) : that.value == null;
        }

        @Override
        public int hashCode()
        {
            int result = (int) (ballot ^ (ballot >>> 32));
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            Object toStringValue = value;
            if ( toStringValue instanceof Payload )
            {
                try
                {
                    toStringValue = new AtomicBroadcastSerializer( new ObjectStreamFactory(), new ObjectStreamFactory() )
                                    .receive( (Payload) toStringValue );
                }
                catch ( Throwable e )
                {
                    e.printStackTrace();
                }
            }

            return "AcceptState{" + "ballot=" + ballot + ", value=" + toStringValue + "}";
        }
    }
}
