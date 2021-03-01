# Copyright (c) 2018-2020 "Graph Foundation,"
# Graph Foundation, Inc. [https://graphfoundation.org]
#
# This file is part of ONgDB.
#
# ONgDB is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Copyright (c) 2002-2018 "Neo Technology,"
# Network Engine for Objects in Lund AB [http://neotechnology.com]
#
# This file is part of Neo4j.
#
# Neo4j is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.


<#
.SYNOPSIS
Stop a ONgDB Server Windows Service

.DESCRIPTION
Stop a ONgDB Server Windows Service

.PARAMETER ONgDBServer
An object representing a valid ONgDB Server object

.EXAMPLE
Stop-ONgDBServer -ONgDBServer $ServerObject

Stop the ONgDB Windows Windows Service for the ONgDB installation at $ServerObject

.OUTPUTS
System.Int32
0 = Service was stopped and not running
non-zero = an error occured

.NOTES
This function is private to the powershell module

#>
Function Stop-ONgDBServer
{
  [cmdletBinding(SupportsShouldProcess=$true,ConfirmImpact='Medium')]
  param (
    [Parameter(Mandatory=$true,ValueFromPipeline=$true)]
    [PSCustomObject]$ONgDBServer

  )
  
  Begin
  {
  }

  Process
  {
    $ServiceName = Get-ONgDBWindowsServiceName -ONgDBServer $ONgDBServer -ErrorAction Stop

    Write-Verbose "Stopping the service.  This can take some time..."
    $result = Stop-Service -Name $ServiceName -PassThru -ErrorAction Stop
    
    if ($result.Status -eq 'Stopped') {
      Write-Host "ONgDB windows service stopped"
      return 0
    }
    else {
      Write-Host "ONgDB windows was sent the Stop command but is currently $($result.Status)"
      return 2
    }
  }
  
  End
  {
  }
}