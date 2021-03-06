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
package org.neo4j.server.security.enterprise.auth.plugin.spi;

import org.neo4j.server.security.enterprise.auth.plugin.api.AuthProviderOperations;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthToken;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthenticationException;

/**
 * An authentication provider plugin for the ONgDB enterprise security module.
 *
 * <p>If the configuration setting {@code dbms.security.plugin.authentication_enabled} is set to {@code true},
 * all objects that implements this interface that exists in the class path at ONgDB startup, will be
 * loaded as services.
 *
 * @see AuthPlugin
 * @see AuthorizationPlugin
 */
public interface AuthenticationPlugin extends AuthProviderLifecycle
{
    /**
     * The name of this authentication provider.
     *
     * <p>This name, prepended with the prefix "plugin-", can be used by a client to direct an auth token directly
     * to this authentication provider.
     *
     * @return the name of this authentication provider
     */
    String name();

    /**
     * Should perform authentication of the identity in the given auth token and return an
     * {@link AuthenticationInfo} result if successful.
     * If authentication failed, either {@code null} should be returned,
     * or an {@link AuthenticationException} should be thrown.
     * <p>
     * If authentication caching is enabled, either a {@link CacheableAuthenticationInfo} or a
     * {@link CustomCacheableAuthenticationInfo} should be returned.
     *
     * @return an {@link AuthenticationInfo} object if authentication was successful, otherwise {@code null}
     * @throws AuthenticationException if authentication failed
     *
     * @see org.neo4j.server.security.enterprise.auth.plugin.api.AuthToken
     * @see AuthProviderOperations#setAuthenticationCachingEnabled(boolean)
     */
    AuthenticationInfo authenticate( AuthToken authToken ) throws AuthenticationException;

    abstract class Adapter extends AuthProviderLifecycle.Adapter implements AuthenticationPlugin
    {
        @Override
        public String name()
        {
            return getClass().getName();
        }
    }

    abstract class CachingEnabledAdapter extends AuthProviderLifecycle.Adapter implements AuthenticationPlugin
    {
        @Override
        public String name()
        {
            return getClass().getName();
        }

        @Override
        public void initialize( AuthProviderOperations authProviderOperations )
        {
            authProviderOperations.setAuthenticationCachingEnabled( true );
        }
    }
}
