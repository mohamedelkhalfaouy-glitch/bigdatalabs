<#
Start the Hadoop cluster using docker-compose.
Run this script from PowerShell. It uses the script folder to find docker-compose.yml.
#>
$compose = Join-Path $PSScriptRoot 'docker-compose.yml'
Write-Host "Starting Hadoop cluster with docker-compose (file: $compose)"
docker-compose -f $compose up -d
Write-Host "Done. Check 'docker ps' and the Namenode UI at http://localhost:9870"
