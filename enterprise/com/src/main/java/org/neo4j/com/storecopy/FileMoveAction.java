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
package org.neo4j.com.storecopy;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.neo4j.io.fs.FileHandle;
import org.neo4j.io.pagecache.PageCache;

public interface FileMoveAction
{
    void move( File toDir, CopyOption... copyOptions ) throws IOException;

    File file();

    static FileMoveAction copyViaPageCache( File file, PageCache pageCache )
    {
        return new FileMoveAction()
        {
            @Override
            public void move( File toDir, CopyOption... copyOptions ) throws IOException
            {
                Optional<FileHandle> handle = pageCache.getCachedFileSystem().streamFilesRecursive( file ).findAny();
                boolean directoryExistsInCachedSystem = handle.isPresent();
                if ( directoryExistsInCachedSystem )
                {
                    handle.get().rename( new File( toDir, file.getName() ), copyOptions );
                }
            }

            @Override
            public File file()
            {
                return file;
            }
        };
    }

    static FileMoveAction copyViaFileSystem( File file, File basePath )
    {
        Path base = basePath.toPath();
        return new FileMoveAction()
        {
            @Override
            public void move( File toDir, CopyOption... copyOptions ) throws IOException
            {
                Path originalPath = file.toPath();
                Path relativePath = base.relativize( originalPath );
                Path resolvedPath = toDir.toPath().resolve( relativePath );
                Files.createDirectories( resolvedPath.getParent() );
                Files.copy( originalPath, resolvedPath, copyOptions );
            }

            @Override
            public File file()
            {
                return file;
            }
        };
    }
}
