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
package org.neo4j.kernel.stresstests.transaction.checkpoint;

import java.util.function.ToIntFunction;

import org.neo4j.unsafe.impl.batchimport.GeneratingInputIterator;
import org.neo4j.unsafe.impl.batchimport.InputIterable;
import org.neo4j.unsafe.impl.batchimport.cache.NumberArrayFactory;
import org.neo4j.unsafe.impl.batchimport.cache.idmapping.IdMapper;
import org.neo4j.unsafe.impl.batchimport.cache.idmapping.IdMappers;
import org.neo4j.unsafe.impl.batchimport.input.Collector;
import org.neo4j.unsafe.impl.batchimport.input.Collectors;
import org.neo4j.unsafe.impl.batchimport.input.Group;
import org.neo4j.unsafe.impl.batchimport.input.Input;
import org.neo4j.values.storable.Value;

import static org.neo4j.unsafe.impl.batchimport.InputIterable.replayable;
import static org.neo4j.unsafe.impl.batchimport.input.Inputs.knownEstimates;

public class NodeCountInputs implements Input
{
    private static final Object[] properties = new Object[]{
            "a", 10, "b", 10, "c", 10, "d", 10, "e", 10, "f", 10, "g", 10, "h", 10, "i", 10,
            "j", 10, "k", 10, "l", 10, "m", 10, "o", 10, "p", 10, "q", 10, "r", 10, "s", 10
    };
    private static final String[] labels = new String[]{"a", "b", "c", "d"};

    private final long nodeCount;
    private final Collector bad = Collectors.silentBadCollector( 0 );

    public NodeCountInputs( long nodeCount )
    {
        this.nodeCount = nodeCount;
    }

    @Override
    public InputIterable nodes()
    {
        return replayable( () -> new GeneratingInputIterator<>( nodeCount, 1_000, batch -> null,
                (GeneratingInputIterator.Generator<Void>) ( state, visitor, id ) -> {
                    visitor.id( id, Group.GLOBAL );
                    visitor.labels( labels );
                    for ( int i = 0; i < properties.length; i++ )
                    {
                        visitor.property( (String) properties[i++], properties[i] );
                    }
                }, 0 ) );
    }

    @Override
    public InputIterable relationships()
    {
        return GeneratingInputIterator.EMPTY_ITERABLE;
    }

    @Override
    public IdMapper idMapper( NumberArrayFactory numberArrayFactory )
    {
        return IdMappers.actual();
    }

    @Override
    public Collector badCollector()
    {
        return bad;
    }

    @Override
    public Estimates calculateEstimates( ToIntFunction<Value[]> valueSizeCalculator )
    {
        return knownEstimates( nodeCount, 0, nodeCount * properties.length / 2, 0, nodeCount * properties.length / 2 * Long.BYTES, 0,
                nodeCount * labels.length );
    }
}
