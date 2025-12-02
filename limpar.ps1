Write-Host "--- LIMPANDO CACHE ---"
$m2Path = "$env:USERPROFILE\.m2\repository\io\quarkus"
if (Test-Path $m2Path) {
    Remove-Item -Path $m2Path -Recurse -Force -ErrorAction SilentlyContinue
}
./mvnw clean install -U -DskipTests