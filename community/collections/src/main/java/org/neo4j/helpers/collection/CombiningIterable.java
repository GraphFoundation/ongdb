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
package org.neo4j.helpers.collection;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Combining one or more {@link Iterable}s, making them look like they were
 * one big iterable. All iteration/combining is done lazily.
 *
 * @param <T> the type of items in the iteration.
 */
public class CombiningIterable<T> implements Iterable<T>
{
    private Iterable<Iterable<T>> iterables;

    @SuppressWarnings( "unchecked" )
    public <INNER extends Iterable<T>> CombiningIterable( Iterable<INNER> iterables )
    {
        this.iterables = (Iterable) iterables;
    }

    @Override
    public Iterator<T> iterator()
    {
        LinkedList<Iterator<T>> iterators = new LinkedList<>();
        for ( Iterable<T> iterable : iterables )
        {
            iterators.add( iterable.iterator() );
        }
        return new CombiningIterator<>( iterators );
    }
}
