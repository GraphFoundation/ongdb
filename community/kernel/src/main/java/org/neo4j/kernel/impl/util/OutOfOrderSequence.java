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
package org.neo4j.kernel.impl.util;

import java.util.concurrent.TimeoutException;

/**
 * The thinking behind an out-of-order sequence is that, to the outside, there's one "last number"
 * which will never be decremented between times of looking at it. It can move in bigger strides
 * than 1 though. That is because multiple threads can {@link #offer(long, long[]) tell} it that a certain number is
 * "done",
 * a number that not necessarily is the previously last one plus one. So if a gap is observed then the number
 * that is the logical next one, whenever that arrives, will move the externally visible number to
 * the highest gap-free number set.
 */
public interface OutOfOrderSequence
{
    /**
     * Offers a number to this sequence.
     *
     * @param number number to offer this sequence
     * @param meta meta data about the number
     * @return {@code true} if highest gap-free number changed as part of this call, otherwise {@code false}.
     */
    boolean offer( long number, long[] meta );

    /**
     * @return the highest number, without its meta data.
     */
    long highestEverSeen();

    /**
     * @return {@code long[]} with the highest offered gap-free number and its meta data.
     */
    long[] get();

    /**
     * Waits for the specified number (gap-free).
     *
     * @param awaitedNumber the awaited number.
     * @param timeoutMillis the maximum time to wait in milliseconds.
     */
    void await( long awaitedNumber, long timeoutMillis ) throws TimeoutException, InterruptedException;

    /**
     * @return the highest gap-free number, without its meta data.
     */
    long getHighestGapFreeNumber();

    /**
     * Used in recovery. I don't like the visibility of this method at all.
     */
    void set( long number, long[] meta );
}
