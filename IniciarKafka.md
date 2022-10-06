# Local de Instalacao do Kafka
cd C:\Kafka\

# Primeiro Terminar
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Segundo Terminar
.\bin\windows\kafka-server-start.bat .\config\server.properties

# Descrever Topicos
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --describe

# Alterar Topico 
.\bin\windows\kafka-topics.bat --alter --bootstrap-server localhost:9092 --topic ECOMMERCER_NEW_ORDER --partitions 3


# Visualizar Tarefas dos Consumidores
.\bin\windows\kafka-consumer-groups.bat --bootstrap-server localhost:9092 --describe --all-groups


# Alterando Diretorio original(temporario) do zookeeper e kafka

## No Arquivo \config\zookeeper.properties, alterar propriedade dataDir=/tmp/zookeeper para dataDir=Seu/Diretorio/zookeeper

## No Arquivo \config\server.properties, alterar propriedade log.dirs=/tmp/kafka-logs para log.dirs=Seu/Diretorio/kafka-logs