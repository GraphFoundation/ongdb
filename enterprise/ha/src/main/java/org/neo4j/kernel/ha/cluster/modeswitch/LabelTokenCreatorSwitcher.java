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
package org.neo4j.kernel.ha.cluster.modeswitch;

import java.util.function.Supplier;

import org.neo4j.internal.kernel.api.Kernel;
import org.neo4j.kernel.ha.DelegateInvocationHandler;
import org.neo4j.kernel.ha.SlaveLabelTokenCreator;
import org.neo4j.kernel.ha.com.RequestContextFactory;
import org.neo4j.kernel.ha.com.master.Master;
import org.neo4j.kernel.impl.core.DefaultLabelIdCreator;
import org.neo4j.kernel.impl.core.TokenCreator;
import org.neo4j.kernel.impl.store.id.IdGeneratorFactory;

public class LabelTokenCreatorSwitcher extends AbstractComponentSwitcher<TokenCreator>
{
    private final DelegateInvocationHandler<Master> master;
    private final RequestContextFactory requestContextFactory;
    private final Supplier<Kernel> kernelSupplier;
    private final IdGeneratorFactory idGeneratorFactory;

    public LabelTokenCreatorSwitcher( DelegateInvocationHandler<TokenCreator> delegate,
            DelegateInvocationHandler<Master> master, RequestContextFactory requestContextFactory,
            Supplier<Kernel> kernelSupplier, IdGeneratorFactory idGeneratorFactory )
    {
        super( delegate );
        this.master = master;
        this.requestContextFactory = requestContextFactory;
        this.kernelSupplier = kernelSupplier;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    @Override
    protected TokenCreator getMasterImpl()
    {
        return new DefaultLabelIdCreator( kernelSupplier, idGeneratorFactory );
    }

    @Override
    protected TokenCreator getSlaveImpl()
    {
        return new SlaveLabelTokenCreator( master.cement(), requestContextFactory );
    }
}
