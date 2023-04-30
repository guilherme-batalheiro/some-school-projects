Adicionámos ao Cliente um array de timeStamps (prev) com tamanho predifinido pelo número de servidores existentes.
Ao enviármos o pedido de operação ao servidor, enviamos também o atributo prev.

O servidor passa a ter 2 timestamps: ValueTS e ReplicaTS, sendo o primeiro para acompanharmos o número de operações executadas e o segundo o número de operações recebidas.

Nas operações de leitura: comparamos o ValueTS com o prev, se o ValueTS for GE, o estado do cliente é anterior ao do servidor, sendo devolvido o valor pretendido. Caso contrário, uma exceção é lançada.

Nas operações de escrita: incrementamos o ReplicaTS ao receber o pedido de operação e guardamo-la na ledger state. Devolvemos um novo timestamp para que o cliente atualize o seu estado. Depois, verificamos se podemos realizar a operação, isto é, se o ValueTS é maior ou igual ao prev recebido. Se sim, a operação é estável, logo, é executada e o ValueTS incrementado. Senão, a operação é instável.

Nas propagações de estado, o servidor verifica se as operações recebidas não se encontram na ledger para evitar que a mesma operação seja realizada duas vezes. Se estas forem estáveis, implementa-as, caso contrário, declara-as instáveis. Por fim, percorre a ledger a fim de implementar operações que se tenham tornado estáveis.
