# Local de Instalacao do Kafka
cd C:\Kafka\ 

# Criar Topico
.\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVO_PEDIDO

# Listar Topicos
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092 

# Enviado Messagem pelo Console para o Topico
.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic LOJA_NOVO_PEDIDO
>pedido0,000
>pedido1,111

# Receber Messagem pelo Console para o Topico(Atual)
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO


# Receber Messagem pelo Console para o Topico(Historico + Atual)
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO --from-beginning
