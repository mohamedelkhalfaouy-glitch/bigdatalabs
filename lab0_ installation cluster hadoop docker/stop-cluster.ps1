<#
Stop and remove the Hadoop cluster containers and volumes.
Run from PowerShell in this folder.
#>
$compose = Join-Path $PSScriptRoot 'docker-compose.yml'
Write-Host "Stopping Hadoop cluster (file: $compose)"
docker-compose -f $compose down -v
Write-Host "Stopped and removed containers and volumes."
