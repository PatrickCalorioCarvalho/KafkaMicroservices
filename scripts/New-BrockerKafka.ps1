param([int]$NumeroNos)
Stop-Job Kafka* -ErrorAction Ignore
Remove-Job Kafka* -ErrorAction Ignore
Stop-Job Zookeeper -ErrorAction Ignore
Remove-Job Zookeeper -ErrorAction Ignore
Remove-Item C:\Kafka\config\server* -Force
Remove-Item C:\Kafka\data\* -Recurse -Force
Start-Job -Name Zookeeper -ScriptBlock {Invoke-Expression -Command "C:\Kafka\bin\windows\zookeeper-server-start.bat C:\Kafka\config\zookeeper.properties"}
Start-Sleep -Seconds 30
pushd C:\Kafka\scripts
$ID = 1
$Port = 9091
while(($NumeroNos + 1) -ne $ID)
{
    $config = "C:\Kafka\config\server$ID.properties"
    Copy-Item .\server.properties $config
    (Get-Content $config).replace('**ID_BROKER**', $ID) | Set-Content $config
    (Get-Content $config).replace('**PORT**', $Port) | Set-Content $config
    $script = "C:\Kafka\bin\windows\kafka-server-start.bat $config"
    Start-Job -Name "Kafka$ID" -ScriptBlock {param($scrpit) Invoke-Expression -Command $scrpit } -ArgumentList $script 
    $ID++
    $Port++
}