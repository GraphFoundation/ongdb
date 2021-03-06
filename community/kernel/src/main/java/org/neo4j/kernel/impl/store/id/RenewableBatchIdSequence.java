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
package org.neo4j.kernel.impl.store.id;

import java.util.function.LongConsumer;

import org.neo4j.graphdb.Resource;

import static org.neo4j.kernel.impl.store.id.IdRangeIterator.VALUE_REPRESENTING_NULL;

/**
 * An {@link IdSequence} which does internal batching by using another {@link IdSequence} as source of batches.
 * Meant to be used by a single thread at a time.
 */
public class RenewableBatchIdSequence implements IdSequence, Resource
{
    private final IdSequence source;
    private final int batchSize;
    private final LongConsumer excessIdConsumer;
    private IdSequence currentBatch;
    private boolean closed;

    RenewableBatchIdSequence( IdSequence source, int batchSize, LongConsumer excessIdConsumer )
    {
        this.source = source;
        this.batchSize = batchSize;
        this.excessIdConsumer = excessIdConsumer;
    }

    /**
     * Even if instances are meant to be accessed by a single thread at a time, lifecycle calls
     * can guard for it nonetheless. Only the first call to close will perform close.
     */
    @Override
    public synchronized void close()
    {
        if ( !closed && currentBatch != null )
        {
            long id;
            while ( (id = currentBatch.nextId()) != VALUE_REPRESENTING_NULL )
            {
                excessIdConsumer.accept( id );
            }
            currentBatch = null;
        }
        closed = true;
    }

    @Override
    public long nextId()
    {
        assert !closed;

        long id;
        while ( currentBatch == null || (id = currentBatch.nextId()) == VALUE_REPRESENTING_NULL )
        {
            currentBatch = source.nextIdBatch( batchSize ).iterator();
        }
        return id;
    }

    @Override
    public IdRange nextIdBatch( int size )
    {
        throw new UnsupportedOperationException( "Haven't been needed so far" );
    }
}
