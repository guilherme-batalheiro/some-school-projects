; **********************************************************************
; grupo nº 28
; Guilherme Batalheiro 99075
; João Carvalho 99091
; **********************************************************************

TERMINADOR          EQU 0FFH       ; é utilizado para a função saber quando parar                             
DISPLAYS            EQU 0A000H     ; endereço dos displays de 7 segmentos (periférico POUT-1)
DEFINE_LINHA        EQU 600AH      ; endereço do comando para definir a linha
DEFINE_COLUNA       EQU 600CH      ; endereço do comando para definir a coluna
DEFINE_PIXEL        EQU 601AH      ; endereço do comando para escrever um pixel
APAGA_ECRAS         EQU 6002H      ; endereço para apagar todos os ecras 
APAGA_AVISO         EQU 6040H      ; endereço para apagar o aviso do ecra 
DEFINE_COR_CANETA   EQU 6014H      ; endereço para definir a cor da caneta
SELECIONAR_ECRA     EQU 6004H      ; Seleciona o ecrã especificado
ESCONDE_ECRA        EQU 6006H      ; endereço para apagar um ecra 
MOSTRAR_ECRA        EQU 6008H      ; endereço para mostrar um ecra 
APAGAR_ECRA_ESPC    EQU 6000H      ; apaga ecra especifico
REPRODUZIR_S_VIDEO  EQU 605CH      ; permite reproducao de de som e video
TERMINAR_SOM        EQU 6066H      ; acaba de reproduzir o som 
SELECIONAR_FUNDO    EQU 6042H      ; seleciona o ecra de fundo 
APAGAR_SENARIO      EQU 6044H      ; apaga a imagem de fundo
MOSTRAR_SENARIO     EQU 6046H      ; mostrar cenaruio
NAVE_LINHA          EQU 26         ; número da linha que a nave começa
COR_DA_NAVE         EQU 0FFF0H      
COR_DE_LONGE        EQU 0F00FH
COR_DO_NAVE_INIMIGA EQU 0FF00H
COR_DO_ASTEROIDE    EQU 0F0F0H
COR_DO_MISSIL       EQU 0F0FFH
COR_DA_EXPLOSAO     EQU 0F0FFH
DELAY               EQU 7FFFH       ; valor usado para implementar um atraso temporal
MASCARA_INICIAL     EQU 01H         ; mascara (0000 0001)
TEC_LIN             EQU 0C000H      ; endereço das linhas do teclado (periférico POUT-2)
TEC_COL             EQU 0E000H      ; endereço das colunas do teclado (periférico PIN)
ECRA_DOT            EQU 15          ; numero do ecra onde se vai encontrar o primeiro tamanho de ovni     
ECRA_MISSIL         EQU 14          ; numero do ecra do missil
TECLA_MISSIL        EQU 1
RANGE_DO_MISSIL     EQU 10          ; linha onde o missil desaparece
NUMERO_DE_OBJETOS   EQU 6           ; fazemos o numero de objetos*2  
COLUNA_DO_MEIO      EQU 30

; *********************************************************************************
; * Dados
; *********************************************************************************
PLACE     1000H
pilha:    TABLE 100H                

SP_inicial:

tab:
    WORD rot_int_0                  ; rotina de atendimento da interrupção 0
    WORD rot_int_1                  ; rotina de atendimento da interrupção 1
    WORD rot_int_2                  ; rotina de atendimento da interrupção 2


PLACE     2000H
contador_alt_atraso:
    WORD DELAY                       ; contador_alt usado para gerar o atraso

evento_int:
    WORD 0                           ; rotina de atendimento da interrupção 0  
    WORD 0                           ; rotina de atendimento da interrupção 1  
    WORD 0                           ; rotina de atendimento da interrupção 2  

linha_que_estamos:
    WORD 1                           ; 1 porque começamos na primeira linha é usado na rotina teclado

tecla_premida:
    WORD 0                           ; indica que tecla foi permida

tecla_s:
    WORD 0                           ; indica se alguma tecla foi permida 0 se nenhuma tecla for permida

nave_desenho:
    STRING 5H, 4H, 0EH, 1FH, 4H, 0AH, TERMINADOR  ; string com o desenho da nave

nave_coluna:
    WORD  30                         ; fica com a coluna atual da nave 

nave_apagador_esquerda:
    STRING 5H, 2H, 7H, 0FH, 2H, 5H, TERMINADOR ; string com o desenho nova posicao da nave

nave_apagador_direita:
    STRING 5H, 8H, 1CH, 1EH, 8H, 14H, TERMINADOR  ;string com o desenho nova posicao da nave

dot:
    STRING 1H, 1H, TERMINADOR

asteroide_desenho_1:                  ;desenhos da evoluçao do asteroide
    STRING 2H, 3H, 3H, TERMINADOR
asteroide_desenho_2:   
    STRING 3H, 2H, 7H, 2H, TERMINADOR
asteroide_desenho_3:
    STRING 4H, 6H, 0FH, 0FH, 6H, TERMINADOR
asteroide_desenho_4:
    STRING 5H, 0EH, 1FH, 1FH, 1FH, 0EH, TERMINADOR

asteroide_desenho:                    ; tabela com os desenhos do asteroide
    WORD 0                      
    WORD asteroide_desenho_1
    WORD asteroide_desenho_2
    WORD asteroide_desenho_3
    WORD asteroide_desenho_4

nave_inimiga_desenho_1:               ; desenhos da evoluçao da nave inimiga
    STRING 2H, 3H, 3H, TERMINADOR
nave_inimiga_desenho_2:   
    STRING 3H, 5H, 2H, 5H, TERMINADOR
nave_inimiga_desenho_3:
    STRING 4H, 9H, 6H, 6H, 9H, TERMINADOR
nave_inimiga_desenho_4:
    STRING 5H, 11H, 0AH, 4H, 0AH, 11H, TERMINADOR

nave_inimiga_desenho:                 ; tabela com os desenhos da nave inimiga
    WORD 0                      
    WORD nave_inimiga_desenho_1
    WORD nave_inimiga_desenho_2
    WORD nave_inimiga_desenho_3
    WORD nave_inimiga_desenho_4

explosao_desenho:                    ; desnho da explosão
    STRING 5H, 0AH, 15H, 0AH, 15H, 0AH, TERMINADOR

; obj_1 meio
obj_1_posicoes:
    STRING 00, 00, 02, 30, 05, 30, 09, 30, 14, 30, 20, 30, 26, 30
obj_1:
    WORD 1                      ; o ecra deste objeto
    WORD 0                      ; a posicao 0 - 6
    WORD obj_1_posicoes         ; as posicoes do objeto
    WORD 0                      ; tamanho do objeto
    WORD 1                      ; se e nave inimiga(1) ou asteroide(0) 

; obj_2 esquerda
obj_2_posicoes:
    STRING 00, 00, 02, 28, 05, 25, 09, 22, 14, 18, 20, 14, 26, 10
obj_2:
    WORD 2                      ; o ecra deste objeto
    WORD 0                      ; a posicao 0 - 6
    WORD obj_2_posicoes         ; as posicoes do objeto
    WORD 0                      ; tamanho do objeto
    WORD 1                      ; se e nave inimiga(1) ou asteroide(0)

; obj_3 direita
obj_3_posicoes:
    STRING 00, 00, 02, 32, 05, 35, 09, 39, 14, 43, 20, 47, 26, 51
obj_3:
    WORD 3                      ; o ecra deste objeto
    WORD 0                      ; a posicao 0 - 6
    WORD obj_3_posicoes         ; as posicoes do objeto
    WORD 0                      ; tamanho do objeto
    WORD 0                      ; se e nave inimiga(1) ou asteroide(0)   

obj_lista:
    WORD obj_1
    WORD obj_2
    WORD obj_3 

missil_obj:
    WORD 0                      ; 0 se nao existe ja 1 se ja existir
    WORD 0                      ; coluna do missil
    WORD 26                     ; linha do missil

contador_alt:                   ; vai ser utelizado para ver aliatorio
    WORD 0

energia_valor:
    WORD 100                    ; valor atual da energia

perdeu_var:
    WORD 0                      ; diz se o jogador perdeu ou não


inicio:
PLACE 0
    MOV  BTE, tab               ; inicializa BTE (registo de Base da Tabela de Exceções)
    MOV  SP, SP_inicial         ; inicializa SP para a palavra a seguir

    MOV   R0, APAGA_ECRAS
    MOV   [R0], R1              ; apaga todos os pixels de todos os ecrãs (o valor de R1 não é relevante)

    MOV  R0, APAGA_AVISO
    MOV  [R0], R1               ; apaga o aviso de nenhum cenário selecionado (o valor de R1 não é relevante)

    EI0                         ; permite interrupções 0
    EI1                         ; permite interrupções 1
    EI2                         ; permite interrupções 2
    EI                          ; permite interrupções (geral)
    
    CALL inicio_ecra

ciclo:    
    CALL teclado                ; rotina responsavel por verificar o teclado 
    CALL nave                   ; rotina responsavel pelo movimento da nave 
    CALL colisoes               ; rotina responsavel pelas explosoes no jogo
    CALL npc                    ; rotina responsavel pelo movimento dos ovnis 
    CALL missil                 ; rotina responsavel pelo missil
    CALL energia                ; rotina responsavel pelo missil da nave       
    CALL pausa                  ; rotina responsavel pela pausa e despausa do jogo
    CALL termina                ; rotina responsavel por verificar se o jogador quer acabar o jogo

    MOV R0, perdeu_var          ; ve se o jogador perdeu
    MOV R1, [R0]
    MOV R2, 1
    CMP R2, R1                  ; se 1 o jogador perdeu , se 0 nao perdeu
    JNZ  out
    CALL perdeu_ecra            ; coloca o ecra de derrota
out:JMP ciclo

fim: JMP fim

; **********************************************************************
; Rotinas de interrupção 
; **********************************************************************

; **********************************************************************
; ROT_INT_0 - Rotina de atendimento da interrupção 0
;           - altera na memoria para 1 em caso de uma rotina acontecer
; **********************************************************************
rot_int_0:
    PUSH R0
    PUSH R1
    MOV  R0, evento_int
    MOV  R1, 1                  ; assinala que houve uma interrupção 0
    MOV  [R0], R1              
    POP  R1
    POP  R0
    RFE

; **********************************************************************
; ROT_INT_1 - Rotina de atendimento da interrupção 1
;           - altera na memoria para 1 em caso de uma rotina acontecer
; **********************************************************************

rot_int_1:
    PUSH R0
    PUSH R1

    MOV  R0, evento_int
    ADD  R0, 2

    MOV  R1, 1                  ; assinala que houve uma interrupção 1
    MOV  [R0], R1

    POP  R1
    POP  R0
    RFE

; **********************************************************************
; ROT_INT_2 - Rotina de atendimento da interrupção 2
;           - altera na memoria para 1 em caso de uma rotina acontecer
; **********************************************************************

rot_int_2:
    PUSH R0
    PUSH R1

    MOV  R0, evento_int
    ADD  R0, 4

    MOV  R1, 1                  ; assinala que houve uma interrupção 2
    MOV  [R0], R1
 
    POP  R1
    POP  R0
    RFE

; **********************************************************************
; TERMINA - Termina o jogo
; Argumentos: Nenhum
; **********************************************************************
termina:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com o valor da tecla que foi permida
    MOV R0, 14                  ; 14 == E
    CMP R1, R0                  ; ve se a tecla C foi permida se for sair do loop
    JNZ sair_termina

    MOV R0, APAGA_ECRAS
    MOV [R0], R1                ; apaga todos os pixels de todos os ecrãs
    
    MOV R0, MOSTRAR_SENARIO
    MOV R1, 4                   ; mostra o fundo numero 4 
    MOV [R0], R1
    
    CALL ha_tecla               ; esperar que se largue a tecla

loop_termina:
    CALL teclado
    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com o valor da tecla que foi permida
    MOV R0, 14                  
    CMP R1, R0                  ; ve se a tecla E foi permida se for sair do loop
    JNZ loop_termina

    CALL ha_tecla               ; esperar que se largue a tecla

    MOV R0, APAGAR_SENARIO
    MOV R1, 4                   ; apaga o fundo numero 4 
    MOV [R0], R1
    
    CALL inicia_jogo
    
sair_termina:
    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; PAUSA - Poem o jogo em pausa
; Argumentos: Nenhum
; **********************************************************************

pausa:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com o valor da tecla que foi permida
    MOV R0, 13                  ; 13 == D
    CMP R1, R0                  ; ve se a tecla D foi permida se for sair do loop
    JNZ sair_pausa
    
    MOV R0, MOSTRAR_SENARIO
    MOV R1, 3                   ; mostra o fundo numero 3 
    MOV [R0], R1
    
    CALL ha_tecla               ; esperar que se largue a tecla

loop_pausa:
    CALL teclado
    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com o valor da tecla que foi permida
    MOV R0, 13                  
    CMP R1, R0                  ; ve se a tecla D foi permida se for sair do loop
    JNZ loop_pausa

    CALL ha_tecla               ; esperar que se largue a tecla

    MOV R0, APAGAR_SENARIO
    MOV R1, 3                   ; apaga o numero do fundo 3
    MOV [R0], R1
 
sair_pausa:
    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; INICIO_ECRA - Mostrar o ecra do inicio
; Argumentos: Nenhum
; **********************************************************************
    
inicio_ecra:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, APAGA_ECRAS
    MOV [R0], R1                ; apaga todos os pixels de todos os ecrãs 

    MOV R0, SELECIONAR_FUNDO
    MOV R1, 1                   ; mostra o fundo numero 1 
    MOV [R0], R1

loop_ini:
    CALL atraso
    CALL teclado                ; chama a rotina teclado para verificar qual tecla foi premida 
    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com o valor da tecla que foi permida
    MOV R0, 12                  ; 12 == C 
    CMP R1, R0                  ; ve se a tecla C foi permida se for sair do loop
    JNZ loop_ini

    CALL ha_tecla               ; esperar que se largue a tecla

    CALL inicia_jogo            ; inicia o jogo depois de pressionado C

    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; PERDEU_ECRA - Mostrar o ecra de perder
; Argumentos: Nenhum
; **********************************************************************
perdeu_ecra:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, APAGA_ECRAS
    MOV [R0], R1                ; apaga todos os pixels de todos os ecrãs

    MOV R0, SELECIONAR_FUNDO
    MOV R1, 0
    MOV [R0], R1                ; mostrar fundo numero 0 
    loop_perdeu_ecra:
    CALL atraso
    CALL teclado
    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com o valor da tecla que foi permida
    MOV R0, 12
    CMP R1, R0                  ; ve se a tecla C foi permida se for sair do loop
    JNZ loop_ini

    CALL ha_tecla               ; esperar que se largue a tecla

    CALL inicia_jogo

    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; INICIO_JOGO - Responavel por reiniciar o jogo
; Argumentos: Nenhum
; **********************************************************************
inicia_jogo:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, perdeu_var
    MOV R1, 0
    MOV [R0], R1                ; a variavel que diz se o jogador perdeu fica a 0

    MOV R0, obj_lista
    MOV R2, 0
loop_list_obj:                  ; loop para por todos os objetos com os valores do inicio
    CALL c_aliatorio            ; coloca em R1 um valor (0 ou 1 )se for nave inimiga ou asteroide
    MOV R1, [R0]                ; R1 fica com o objeto

    MOV R3, 0
    ADD R1, 2                   ; passar para o parametro que tem o nº da posicao
    MOV [R1], R3                ; passar para 0

    ADD R1, 4                   ; passar para o parametro que tem o nº do tamanho
    MOV [R1], R3                ; passar para 0
    
    ADD R0, 2
    ADD R2, 2
    CMP R2, NUMERO_DE_OBJETOS   ; verifica se todos os objetos ja foram tratados
    JNZ loop_list_obj

    MOV R0, nave_coluna
    MOV R4, COLUNA_DO_MEIO      ; R4 é utelizado pois CALL desenha_objeto recebe a coluna em R4
    MOV [R0], R4                ; por a nave no meio

    MOV R0, SELECIONAR_ECRA     
    MOV R1, 0                   ; seleciona o ecra 0 onde se encontra a nave
    MOV [R0], R1

    MOV R0, DEFINE_COR_CANETA
    MOV R1, COR_DA_NAVE         ; muda a cor da caneta 
    MOV [R0], R1

    MOV R3, NAVE_LINHA          ; R3 é utilizado pois CALL desenha_objeto recebe a linha em R3
    MOV R1, nave_desenho
    CALL desenha_objeto         ; desenhar a nave no  meio

    MOV R0, energia_valor
    MOV R1, 100                   
    MOV [R0], R1                ; inicializa valor da energia a 100

    CALL esc_display            ; atualizar o valor no display

    MOV  R0, evento_int
    ADD  R0, 4
    MOV  R1, 0                  ; por as iterações a 0 para que não diminua logo quando começamos o jogo
    MOV  [R0], R1
 
    MOV R0, SELECIONAR_FUNDO
    MOV R1, 2                   ; mostrat o fundo 2 ( fundo de jogo)
    MOV [R0], R1                 
    
    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; PERDEU - Responsavel alteral a variavel que diz se perdemos
; Argumentos: Nenhum
; **********************************************************************

perdeu:
    PUSH R0
    PUSH R1

    MOV R0, perdeu_var
    MOV R1, 1                   ; muda R1 para 1, ou seja , o jogador perdeu
    MOV [R0], R1
    
    POP  R1
    POP  R0
    RET

; **********************************************************************
; ENERGIA - Responsavel por diminuir a energia quando a rotina aciona
; Argumentos: Nenhum
; **********************************************************************

energia:
    PUSH R0
    PUSH R1
    PUSH R2

    MOV R0, evento_int
    ADD R0, 4
    MOV R1, 1
    MOV R2, [R0]
    CMP R2, R1                  ; ver se a interrupção foi acionada
    JNZ sair_energia            ; se nao sai
    
    MOV R1, 0
    MOV [R0], R1                ; por a rotina a 0

    MOV R0, energia_valor
    MOV R1, [R0]                ; R1 fica com valor de energia atual
    SUB R1, 5                   ; decrementa 5 à enrgia
    MOV [R0], R1                ; guardamos o novo valor
    MOV R2, 0
    CMP R1, R2                  ; ver se o jogador perdeu por pontos
    JGT sair_energia
    CALL perdeu                 ; se a energia for menor que 0 o jogador perdeu


sair_energia:
    CALL esc_display            ; atualizar os displays

    POP R2
    POP R1
    POP R0
    RET

; **********************************************************************
; ESC_DISPLAY - Atualiza o valor da energia nos display
; Argumentos: Nenhum
; **********************************************************************

esc_display:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4
    PUSH R5

    MOV R2, energia_valor
    MOV R0, [R2]                ; vai buscar o valor que esta nos displays para atualiza-lo
    MOV R1, 03E8H        
    MOV R2, 0           
    MOV R3, 000AH               ; valores auxiliares
converte:                       ; loop que converte de hexadecimal para decimal
    MOD R0, R1                  ; retirar o ultimo digito que convertemos 
    DIV R1, R3           
    MOV R5, R0          
    DIV R5, R1                  ; obter o digito que queremos
    SHL R2, 4
    OR  R2, R5                  ; adicionamos o ultimo digito
    CMP R1, 1            
    JNZ converte                ; formula para converter hexadecimal para decimal

    MOV R1, DISPLAYS            ; endereço do periférico dos displays
    MOV [R1], R2                ; atualiza nos displays

    POP R5
    POP R4
    POP R3
    POP R2
    POP R1
    POP R0
    RET

; **********************************************************************
; MISSIL - Responsavel pelo missil   
; Argumentos: Nenhum
; **********************************************************************

missil:
    CALL missil_criac           ; cria missil
    CALL missil_des             ; e desenha-o no ecra 
    RET

missil_des:                     ; desenha o missil e é responsavel por atualizar as sua posicoes
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4

    MOV  R0, evento_int
    MOV  R1, 2
    ADD  R0, R1                 ; adicionamos  2 par ver a segunda interrupcao

    MOV  R1, [R0]
    MOV  R0, 1                
    CMP  R1, R0                 ; vê se a interrupção acionou
    JNZ  sair_missil_des
    
    MOV  R0, evento_int
    ADD  R0, 2                  
    MOV  R1, 0 
    MOV [R0], R1                ; pomos a segunda interrupcao a 0

    MOV R0, missil_obj          ; R0 fica com o objeto do missil
    MOV R1, [R0]                ; fica com a informaçao de se existe missil
    MOV R2, 0
    CMP R1, R2                  ; se existe missel desenhamo-lo
    JZ sair_missil_des

    MOV R0, SELECIONAR_ECRA     ; seleciona o ecra do missil 
    MOV R1, ECRA_MISSIL
    MOV [R0], R1

    MOV R0, MOSTRAR_ECRA       ; mostra o ecra do missil 
    MOV R1, ECRA_MISSIL
    MOV [R0], R1

    MOV R0, APAGAR_ECRA_ESPC
    MOV [R0], R1                ; apagar o missil anterior

    MOV R0, DEFINE_COR_CANETA
    MOV R1, COR_DO_MISSIL       ; caneta fica com a cor do missil
    MOV [R0], R1

    MOV R0, missil_obj          ; R0 fica com o objeto missil
    
    ADD R0, 2                   ; passar para a informacao seguinda que consiste na coluna do missil                 

    MOV R4, [R0]                ; R4 fica com a coluna

    ADD R0, 2                   ; passar para a informacao seguinda que consiste na linha 

    MOV R3, [R0]                ; passar para a informacao seguinda que consiste na linha 
    SUB R3, 2                   ; R3 linha
    MOV [R0], R3                ; guardar o novo valor do missil 
    
    MOV R1, RANGE_DO_MISSIL
    CMP R3, R1                  ; ver se ja chegou ao limite
    
    JLE apagar_missil    

    MOV R1, dot                  ; desenha o missil
    CALL desenha_objeto
    JMP sair_missil_des

apagar_missil:
    MOV R0, missil_obj          ; R0 fica com o objeto missil
    MOV R1, 0                   ; apaga o missil
    MOV [R0], R1

    ADD R0, 4                   ; passar parra o proximo paranto da variavel que consiste na linha
    MOV R1, NAVE_LINHA
    MOV [R0], R1                ; é igual à linha da nave

sair_missil_des:
    POP R4
    POP R3
    POP R2
    POP R1
    POP R0
    RET

missil_criac:                   ; cria o missil
    PUSH R0
    PUSH R1
    PUSH R2

    MOV R0, tecla_s
    MOV R1, [R0]
    CMP R1, 0                   ; ve se foi permida alguma tecla
    JZ sair_missil_criac

    MOV R0, tecla_premida
    MOV R1, [R0]                ; R1 fica com a tecla que foi permida

    MOV R0, TECLA_MISSIL        ; ver se a tecla que dispara o missil é a premida
    CMP R1, R0
    JNZ sair_missil_criac       ; sai se não for

    MOV R0, missil_obj
    MOV R1, [R0]                ; R1  fica com a informaçao de se ja existe um missel ou nao

    CMP R1, 0                   
    JNZ sair_missil_criac       ; se ja existir sair não criar um
    
    MOV R1, 1                   
    MOV [R0], R1                ; atualizar missil_obj a dizer que agora existe um missil

    ADD R0, 2                   ; passar para o proximo paranto da variavel missil_obj que nos indica a coluna

    MOV R1, nave_coluna         ; zona da memoria onde está a coluna da nave
    MOV R2, [R1]
    ADD R2, 2                   ; adicionamos dois para que o missil saia pelo meio da nave
    MOV [R0], R2                ; o missel passa a ter a coluna da nave


    MOV R0, TERMINAR_SOM
    MOV R1, 1                    ; som da explosao 
    MOV [R0], R1                

    MOV R0, REPRODUZIR_S_VIDEO
    MOV R1, 1                     ; reproduz o som 1  
    MOV [R0], R1           

    MOV R2, energia_valor
    MOV R1, [R2]                ; R1 fica com o valor atual da energia
    MOV R3, 5                   
    CMP R1, R3                  ; vimos se a criação deste missil vai fazer com que o jogador perca
    JLE  perdeu_miss
    SUB R1, 5                   ; diminuir a energia por 5 por ter disparado
    MOV [R2], R1                ; atualizar a energia
    CALL esc_display            ; atualizar a energia nos display
    JMP sair_missil_criac
perdeu_miss:
    MOV R1, 0                   ; verifica se perdeu por causa do disparo do missil (energia a 0)
    MOV [R2], R1                ; por o contador da a energia a 0
    CALL esc_display            ; atualizar a energia nos display
    CALL perdeu                  ; rotina de derrota

sair_missil_criac:
    POP R2
    POP R1
    POP R0
    RET

; **********************************************************************
; COLISOES - Responsavel pelas colisoes ao longo do jogo 
; Argumentos: Nenhum  
; **********************************************************************

colisoes:
    CALL colisao_ovni         ; verifica se ha colisoes 
    CALL colisao_missil
    RET

colisao_missil:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4
    PUSH R5
    PUSH R6
    PUSH R7
    PUSH R8
    PUSH R9
    PUSH R10
    
    MOV R0, missil_obj         ; R0 fica com o objeto missil
    MOV R1, [R0]               ; se R1 for 1 entao existe um  missil no ecra 
    CMP R1, 0
    JZ colisao_missil_sair

    ADD R0, 2                  ; passar para o proximo parametro do obj que consiste na coluna atual do missil
    MOV R1, [R0]               ; R1 fica com a coluna do missel para depois ser comparada com o objeto

    ADD R0, 2                  ; passar para o proximo parametro do obj que consiste na linha atual do missil
    MOV R2, [R0]               ; R2 fica com a linha do missel para depois ser comparada com o objeto

    MOV R3, obj_lista
    MOV R4, 0
loop_obj_missil:
    MOV R5, [R3]               ; R5 fica com o objeto que estamos a considerar

    ADD R5, 2                  ; passar para o parametro que consite na posicao
    MOV R6, [R5]               ; R6 fica com a posicao

    ADD R5, 2                  ; passar para o parametro que consite no string das posicoes
    MOV R7, [R5]               ; R7 fica com o STRING

    MOV R8, 2
    MUL R6, R8                 

    ADD R7, R6                 ; R8 fica com a posicoe onde se encotra a linha
    MOVB R6, [R7]              ; R6 fica com a linha do obj

    MOV R9, R6
    
    MOV R8, R2    
    
    CALL dentro_m              ; verifiva se a linha do missil esta dentro da area dos outros objetos 
    CMP R6, 1 
    JNZ n_coli_m

    ADD R7, 1                  ; R7 fica com a posicao da string onde se encotra a linha
    MOVB R6, [R7]              ; R6 fica com a linha do obj
    
    MOV R10, R6

    MOV R8, R1                ; verifiva se a linha do missil esta dentro da area dos outros objetos
    CALL dentro_m
    CMP R6, 1
    JNZ n_coli_m               ; se nao sai 
    SUB R5, 4
    CALL explosao             ; se ha colisao, chama a rotina de explosao 

    MOV R0, 8 
    ADD R5, R0

    MOV R0, [R5]
    MOV R1, 0
    CMP R1, R0
    JZ  colisao_missil_sair     ; ver se e um asteroide  

    MOV R2, energia_valor       ; atualiza o valor do display
    MOV R1, [R2]
    MOV R3, 95                    ; verifica se a energia esta a 95
    CMP R1, R3                    ; se sim adiciona 5 (fica a 100 e sai) 
    JLE normal_mis
    MOV R1, 100
    MOV [R2], R1
    CALL esc_display    
    JMP colisao_missil_sair
normal_mis:                      ;se energia nao e 95 entao adiciona 5 e sai 
    MOV R3, 5
    ADD R1, R3
    MOV [R2], R1
    JMP colisao_missil_sair

n_coli_m:                        ;loop que verifa se todos os objetos foram tratados
    ADD R3, 2
    ADD R4, 2
    CMP R4, NUMERO_DE_OBJETOS    
    JNZ loop_obj_missil

 colisao_missil_sair:
    POP R10
    POP R9
    POP R8
    POP R7
    POP R6
    POP R5
    POP R4
    POP R3
    POP R2
    POP R1
    POP R0
    RET

dentro_m:                         ;loop que verifica se o missil esta dentro do "espaco" de outros objetos
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, 4
    ADD R0, R6

    CMP R8, R0
    JLE more_m
    MOV R6, 0
    JMP sair_dento
more_m:
    CMP R8, R6
    JGE coli_m
    MOV R6, 0
    JMP sair_dento
coli_m:
    MOV R6, 1

sair_dento_m:
    POP R3
    POP R2
    POP R1
    POP R0
    RET

explosao:                       ; desenha a explosao no ecra. Recebe o obj em R5, a linha em R9 e coluna em R10
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4
    PUSH R5
    PUSH R6
    PUSH R7

    MOV R0, TERMINAR_SOM
    MOV R1,0                      ; seleciona o som 0 
    MOV [R0], R1                  

    MOV R0, REPRODUZIR_S_VIDEO
    MOV R1,0                      ; permite a reprodução do som 0 
    MOV [R0], R1

    MOV R1, [R5]                ; R1 fica com o ecra

    MOV R0, APAGAR_ECRA_ESPC
    MOV [R0], R1                ; apagar os pixeis do ecra

    MOV R0, SELECIONAR_ECRA
    MOV [R0], R1                ; seleciona o ecra

    MOV R0, DEFINE_COR_CANETA
    MOV R1, COR_DA_EXPLOSAO      ; caneta fica com a cor da explosão
    MOV [R0], R1
    
    ADD R5, 2                   ;  passar para a posicao
    MOV R1, 7
    MOV [R5], R1    

    MOV R3, R9
    MOV R4, R10

    MOV R1, explosao_desenho      ; R1 fica com o desenho da explosao

    CALL desenha_objeto            ; desnha explosao

    MOV R1, ECRA_MISSIL
    MOV R0, APAGAR_ECRA_ESPC    ; apagar o ecra do missil
    MOV [R0], R1               

    MOV R0, missil_obj          ; R0 fica com o objeto missil
    MOV R1, 0
    MOV [R0], R1

    MOV R1, 2
    ADD R0, R1                  ; passar parra o proximo parametro da variavel que consiste na coluna
    MOV R2, 0
    MOV [R0], R2

    ADD R0, R1                  ; passar parra o proximo parametro da variavel que consiste na linha
    MOV R2, 26
    MOV [R0], R2    

    POP R7
    POP R6
    POP R5
    POP R4
    POP R3
    POP R2
    POP R1
    POP R0
    RET

colisao_ovni:                   ;  responsavel por ver se uma nave colide com um ovni
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4
    PUSH R5
    PUSH R6
    PUSH R7
    PUSH R8
    PUSH R9
    PUSH R10
    PUSH R11

    MOV R5, nave_coluna          ; R5 fica com a colun atual da nave 

    MOV R0, obj_lista
    MOV R1, 0
loop_obj:
    MOV R2, [R0]                 ; R2 fica com o objeto
    CALL obter_posic             ; devolve as posicoes do objeto no R3 e R4
    MOV R11, R2
    MOV R9, R3
    MOV R10, R4

    MOV R2, 26
    CMP R2, R3
    JNZ n_coli
    MOV R2, [R5]
    CALL dentro                 ; ve se o pixel do canto superior esquerdo da nave esta entre os pixeis laterais do objeto recebe R4, R2, devolve R5 1 se sim 0 se nao
    MOV R7, R6                  ; R7 fica com o valor de dentro 
    MOV R6, 4
    ADD R2, R6
    CALL dentro                 ; ve se o pixel do canto superior direito da nave esta entre os pixeis laterais do objeto recebe R4, R2, devolve R5 1 se sim 0 se nao
    OR R6, R7                   ; ver se pelo menos um dos pixeis ou seja o da esquerda e do da direita estava entre ovni
    CMP R6, 1 
    JNZ n_coli                   ; se nao sai da rotina de colisao 
    MOV R2, [R0]
    MOV R3, 8
    ADD R2, R3                  ; vamos para o parametro que tem a informacao de se foi nave inimiga ou asteroide dai 8
    MOV R3, [R2]
    MOV R5, R11
    CALL explosao                ; se colidiu realiza explosao 
    CMP R3, 1
    JNZ asteroide
    CALL perdeu                    ; rotina de derrota (jogador perdeu)
    JMP sair_loop_obj 
asteroide:
    MOV R2, energia_valor       ; atualiza valor no display se ha colisao com asteroide 
    MOV R1, [R2]                 ; se ha colisao atualiza a energia nos display
    MOV R3, 90        
    CMP R1, R3
    JLE normal_ast
    MOV R1, 100
    MOV [R2], R1
    CALL esc_display            ; rotina de escrita nos displays
    JMP sair_loop_obj            
normal_ast:                     
    MOV R3, 10
    ADD R1, R3                   ; soma 10 a energia se houver colisao com asteroide
    MOV [R2], R1
    JMP sair_loop_obj

n_coli:                         ;loop que verifica a colisao com todos os objetos no ecra 
    ADD R0, 2
    ADD R1, 2
    CMP R1, 6                   ; ja vimos todos os objetos??
    JNZ loop_obj

sair_loop_obj:
    POP R11
    POP R10
    POP R9
    POP R8
    POP R7
    POP R6
    POP R5
    POP R4
    POP R3
    POP R2
    POP R1
    POP R0
    RET

obter_posic:                    ; devolve as posicoes de um dado obj que recebe no R0
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R5

    ADD  R2, 2          

    MOV  R1, [R2]               ; R1 fica com o numero da posicao do objeto
    ADD  R2, 2                  ; passamos para o proximo parametro que equivale ao STRING das posicoes

    MOV  R3, 2
    MUL  R1, R3

    MOV  R5, [R2]               ; R5 fica com o STRING das posicoes
    ADD  R5, R1                 ; para sabermos a cordenada da linhas
    MOVB R3, [R5]
    ADD  R5, 1
    MOVB R4, [R5]               ; fica com a cordenada da colunas

    POP  R5
    POP  R2
    POP  R1
    POP  R0
    RET

dentro:                         ; ve se o pixel do canto superior da nave esta entre os pixeis laterais do objeto recebe R4e R2 .Devolve R5 com 1 se sim, 0 se nao
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R3, 4
    ADD R3, R4

    CMP R2, R3
    JLE more
    MOV R6, 0
    JMP sair_dento
more:
    CMP R2, R4
    JGE coli
    MOV R6, 0
    JMP sair_dento
coli:
    MOV R6, 1

sair_dento:
    POP R3
    POP R2
    POP R1
    POP R0
    RET

; **********************************************************************
; NPC - Desenha os tres objetos e é acionado com interrupcoes    
; Argumentos: Nenhum
; **********************************************************************
npc:
    PUSH R0
    PUSH R1
    PUSH R2

    MOV R0, evento_int
    MOV R1, 1
    MOV R2, [R0]
    CMP R2, R1                  ; ver se a rotina foi acionada
    JNZ sai_npcs                ; se nao sai
    MOV R1, 0
    MOV [R0], R1

    MOV R0, obj_lista
    MOV R2, 0
loop_npc:
    CALL c_aliatorio            ; determina os objetos de forma aleatoria 
    MOV R1, [R0]
    CALL objetos                ; desenha o objeto

    ADD R0, 2
    ADD R2, 2
    CMP R2, NUMERO_DE_OBJETOS    ; ja vimos todos os objetos??
    JNZ loop_npc                 ; se nao volta a fazer o loop
    
sai_npcs:
    POP R2
    POP R1
    POP R0
    RET

; **********************************************************************
; OBJETOS - Desenha um dos tres objetos
; Argumentos:   R1 - Objeto     
; **********************************************************************
desenha_dot:                     ; auxiliar para desenhar o ponto recebe o objeto em R1           
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4

    MOV R0, 2
    SUB R1, R0                  ; para passarmos ao parametro do objeto e sabermos o seu ecra  

    MOV R2, [R1]                ; R2 fica com o ecra

    MOV R0, SELECIONAR_ECRA
    MOV [R0], R2                ; seleciona o ecra a mostrar

    MOV R0, DEFINE_COR_CANETA
    MOV R1, COR_DE_LONGE        ; caneta fica com a cor dos objetos ao longe 
    MOV [R0], R1

    MOV R1, dot                 ; desenhar o ponto
    MOV R3, 0                   ; linha
    MOV R4, 30                  ; coluna do meio
    CALL desenha_objeto         ; desenhar o objeto

    MOV R0, MOSTRAR_ECRA
    MOV [R0], R2                ; seleciona o ecra a mostrar

    POP R4
    POP R3
    POP R2  
    POP R1
    POP R0
    RET

objetos:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4
    PUSH R5
    PUSH R6
    PUSH R7

    MOV R0, SELECIONAR_ECRA
    MOV R2, [R1]
    MOV [R0], R2                ; selecionamos o ecra do objeto

    MOV R0, APAGAR_ECRA_ESPC
    MOV [R0], R2                ; apagar os pixeis do ecra

    ADD R1, 2                   ; passamos para o proximo parametro da variavel, o objeto que equivale a posicao

    MOV R0, 5
    MOV R2, [R1]
    CMP R2, R0                  ; ver se é o fundo do ecra, ou seja, a ultima posicao
    JLE normal 
se_fundo:
    MOV R2, 0
    MOV [R1], R2                ; atualizar a posicao para 0

    CALL desenha_dot            ; recebe o objeto em R1

    ADD R1, 4                   ; passamos para o proximo parametro da variavel objeto, que equivale ao tamanho dai 4 para saltarmos as posicoes
    MOV R2, 0
    MOV [R1], R2                ; por o tamanho igual a 0

    ADD R1, 2                   ; passamos para o proximo paranto da variavel objeto, que equivale a dizer se é aasteroide ou nave-inimiga
    CALL aliatorio

    JMP sair_objeto


normal:
    MOV R2, [R1]
    CMP R2, 0                   ; ver se é a primeira opcao para apagrmos o ponto
    JNZ nao_e_o_primeiro 

    MOV R0, ESCONDE_ECRA
    MOV R3, ECRA_DOT            ; apaga o ecra do ponto
    MOV [R0], R3                ; apaga o dot


nao_e_o_primeiro:                

    ADD R2, 1
    MOV [R1], R2                ; atualizar para a posicao seguinte

    MOV R3, 2
    MUL R2, R3

    ADD R1, 2                   ; passamos para o proximo paranto da variavel objeto que equivale a  STRING das posicoes
    MOV R5, [R1]                ; R5 fica com o STRING das posicoes
    ADD R5, R2                  ; para sabermos a cordenada da linha
    MOVB R3, [R5]
    ADD R5, 1
    MOVB R4, [R5]               ; fica com a cordenada da coluna

    ADD R1, 2                   ; passamos para o proximo paranto da variavel objeto, que equivale ao tamanho
    MOV R2, [R1]
    MOV R0, 4
    CMP R2, R0
    JZ  nao_aumentar_tamanho
    ADD R2, 1
    MOV [R1], R2                ; atualizar o novo tamanho da nave
nao_aumentar_tamanho:

    MOV R7, R2
    
    MOV R5, 2
    MUL R2, R5                  ; esta multiplicacao serve para depois se ver o desenho 

    ADD R1, 2                   ; passamos para o proximo parametro da variavel objeto, que equivale a ver se é nave ou asteroide
    MOV R5, [R1]                ; por o valor em R5 ,pois percisamos do valor de R2

    MOV R6, asteroide_desenho

    MOV R0, DEFINE_COR_CANETA
    MOV R1, COR_DO_ASTEROIDE     ; caneta fica com  a cor do asteroide
    MOV [R0], R1


    CMP R5, 1                    ; verifica se é asteroide ou nave-inimiga
                                 ; se for 1 é nave inimiga. 0 equivale a um asteroide
    JNZ nao_e_asteroide          ; se nao for  asteroide salta       
    MOV R6, nave_inimiga_desenho

    MOV R1, COR_DO_NAVE_INIMIGA
    MOV [R0], R1                 ; caneta fica com a cor da nave-inimiga

nao_e_asteroide:
    MOV R5, 1
    CMP R7, R5 
    JNZ nao_e_pri_tam            ; ve se nao é o perimeiro tamanho. Se for, seleciona a cor de longe
    MOV R1, COR_DE_LONGE
    MOV [R0], R1

nao_e_pri_tam:                   ; verifica se é o primeiro tamanho dos objetos
    ADD R6, R2
    MOV R1, [R6]

    CALL desenha_objeto          ; desnha o objeto pretendido

sair_objeto:
    
    POP   R7
    POP   R6
    POP   R5
    POP   R4
    POP   R3
    POP   R2
    POP   R1
    POP   R0
    RET

; **********************************************************************
; CONTADOR_ALIATORIO - vai alterando o valor para depois ser usado para  
;                      a escolha entre ovni e nave-inimiga
; Argumentos: Nenhum
; **********************************************************************
c_aliatorio:
    PUSH R0
    PUSH R1

    MOV R0, contador_alt
    MOV R1, [R0]
    ADD R1, 1
    MOV [R0], R1             ; incrementamos o contador_alt para depois decidirmos se é nave ou asteroide

    POP R1
    POP R0
    RET

; **********************************************************************
; ALIATÓRIO - Recebe um objeto e diz se é asteroide ou nave inimiga
; Argumentos:   R1 - Objeto            
; **********************************************************************

aliatorio:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R0, contador_alt
    MOV R2, [R0]            ; R2 fica com o valor do contador

    MOV R0, 1
    AND R0, R2              ; R0 fica com o bit nº1
    MOV R3, 2
    AND R3, R2              ; R3 fica com o bit nº2
    ADD R0, R3              ; R0 fica com os dois bits

    CMP R0, 0
    JNZ nav_inimiga
    MOV [R1], R0            ; diz que a proxima nave é uma asteroide
    JMP sair_aliatorio
nav_inimiga:
    MOV R2, 1
    MOV [R1], R2            ; diz que a proxima nave é um asteroide

sair_aliatorio:
    POP R3
    POP R2
    POP R1
    POP R0
    RET



; **********************************************************************
; DESENHA_OBJETO - Desenha qualquer objeto no ecrâ
;
; Argumentos:   R1 - Objeto         
;               R3 - Coluna
;               R4 - Linha
; **********************************************************************
desenha_objeto:
    PUSH  R0
    PUSH  R1
    PUSH  R2
    PUSH  R3
    PUSH  R4
    PUSH  R5

    MOVB  R5, [R1]                 
    MOV   R2, R5                  ; R2 dois fica com o primeiro valor da STRING que consiste no tamanho do objeto
    ADD   R1, 1                   ; proximo elemento da string

le_elemento:
    MOVB  R5, [R1]                ; R5 fica com o elemento da string
    MOV   R0, TERMINADOR
    CMP   R5, R0                  ; verificar se já chegamos ao fim da tabela
    JZ    sair_desenha_objeto     ; se sim, saimos da rotina
    CALL  desenha_linha           ; desenha a linha lida da tabela
    ADD   R1, 1                   ; proximo elemento da string
    JMP   le_elemento             ; repetir até chegar ao fim da tabela
sair_desenha_objeto:

    POP   R5
    POP   R4
    POP   R3
    POP   R2
    POP   R1
    POP   R0
    RET

desenha_linha:
    PUSH  R4
    PUSH  R6
    PUSH  R7
    PUSH  R5
    PUSH  R9
    PUSH  R2

    MOV   R7, MASCARA_INICIAL      ; inicializa a mascara (0000 0001)
le_bit:
    CMP   R2, 0                    ; ver se ja vimos todos os elementos do valor
    JZ    sair_desenha_linha
    MOV   R6, R5                   ; variavel auxiliar com o valor lido da tabela
    AND   R6, R7                   ; aplicar a mascara
    CALL  desenha_pixel
    SHL   R7, 1
    SUB   R2, 1                    ; subtrair 1
    JMP   le_bit

sair_desenha_linha:
    ADD   R3,  1                   ; proxima linha

    POP   R2
    POP   R9
    POP   R5
    POP   R7
    POP   R6
    POP   R4
    RET

desenha_pixel:
    PUSH  R0

    CMP R6, 0
    JZ desenha
    MOV R6, 1

desenha:
    MOV  R0, DEFINE_LINHA
    MOV  [R0], R3           ; seleciona a linha

    MOV  R0, DEFINE_COLUNA
    MOV  [R0], R4           ; seleciona a coluna

    MOV  R0, DEFINE_PIXEL
    MOV  [R0], R6           ; escreve o pixel com a cor da caneta na linha e coluna selecionadas

    ADD R4, 1               ; proxima coluna

    POP  R0
    RET

; **********************************************************************
; NAVE - Processo que e responsavel pelo movimento da nave.
; Argumentos: Nenhum
; **********************************************************************
nave:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
    PUSH R4

    MOV R0, SELECIONAR_ECRA
    MOV R1, 0               ; seleciona o ecra 0 
    MOV [R0], R1

    MOV R0, DEFINE_COR_CANETA
    MOV R1, COR_DA_NAVE     ; caneta fica com a cor da nave 
    MOV [R0], R1

    MOV R0, tecla_s
    MOV R1, [R0]            
    CMP R1, 0               ; ve se foi permida uma tecla
    JZ sai_nave

    MOV R0, tecla_premida
    MOV R1, [R0]            ; R1 fica com o valor da tecla que foi permida

    MOV R3, NAVE_LINHA      ; linha da nave
    MOV R0, nave_coluna     ;coluna da nave 

esquerda:
    CMP R1, 0               ; ve se foi a tecla 0 que foi permida
    JNZ direita
    MOV R4, [R0]            ; R4 fica com a localizacao da nave
    CMP R4, 0               ; ve se  ja estivermos encostados as laterais 
    JZ  sai_nave 
    MOV R1, nave_apagador_esquerda
    CALL desenha_objeto     ; desenhar objeto nave 
    SUB R4, 1
    MOV [R0], R4            ; atualizar a localizacao da nave, mais especificamente a coluna
    MOV R1, nave_desenho
    CALL desenha_objeto     ; desenhar objeto
    CALL atraso             ; chama o atraso 
    JMP  sai_nave
direita:                    ;movimeno da nave para a direita 
    CMP R1, 2               ; ve se foi a tecla 2 que foi permida
    JNZ sai_nave            ; se nao sai
    MOV R4, [R0]            ; R4 fica com a localizacao da nave
    MOV R1, 59
    CMP R4, R1              ; ve se ja estivermos encostados as laterais 
    JZ  sai_nave
    MOV R1, nave_apagador_direita
    CALL desenha_objeto     ; desenhar objeto
    ADD R4, 1
    MOV [R0], R4            ; atualizar a localizacao da nave, mais especificamente a coluna
    MOV R1, nave_desenho
    CALL desenha_objeto     ; desenhar objeto
    CALL atraso             ; chama atraso 
    JMP  sai_nave

sai_nave:
    POP  R4
    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; TECLADO - Processo que deteta quando se carrega numa tecla do teclado.
; Argumentos: Nenhum
; **********************************************************************
teclado:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3

    MOV R2, tecla_s             
    MOV R0, 0                   
    MOV [R2], R0                ; mudar o lugar da memoria que diz se alguma tecla foi permida
    
    MOV  R2, linha_que_estamos  
    MOV  R1, [R2]               ; valor da linha onde estamos
    MOV  R2, TEC_LIN            ; endereço do periférico das linhas
    MOV  R3, TEC_COL            ; endereço do periférico das colunas
    
    MOVB [R2], R1               ; escrever no periférico de saída (linhas)
    MOVB R0, [R3]               ; ler do periférico de entrada (colunas)

    CMP  R0, 0                  ; há tecla premida?
    JZ   sai_teclado            ; se nenhuma tecla premida, repete

    MOV  R2, tecla_s            
    MOV  R3, 1
    MOV  [R2] , R3              ; mudar o luagr da memoria que para dizer que uma tecla foi permida

    MOV  R2, 0
indentificador_linha:           ; loop para ver a linha
    ADD  R2, 1                  ; vai dando "track" ao numero de 0
    SHR  R1, 1                  ; 
    JNZ  indentificador_linha
    SUB  R2, 1                  ; até aqui contou os zeros tira-se um porque se contou um a mais
    MOV  R1, R2                 ; atualiza o valor de R1 com o n de linha (0, 1 ,2, 3) 
    
    MOV  R2, 0                  ; voltar a por o registo a 0
indentificador_coluna:          ; loop para ver a coluna
    ADD  R2, 1
    SHR  R0, 1              
    JNZ  indentificador_coluna
    SUB  R2, 1                  ; até aqui contou os zeros tira-se um porque se contou um a mais
    MOV  R0, R2                 ; atualiza o valor de R0 com o n da coluna (0, 1 ,2, 3) 

    MOV  R2, 4                  ; começa a determinar que tecla foi primida ultilizando a formula (4 * linha + coluna)
    MUL  R1, R2                 
    ADD  R0, R1                 ; R0 fica com o valor da tecla premida

sai_teclado:
    MOV  R1, tecla_premida      
    MOV  [R1], R0               ; atualiza na variável a informação sobre se houve ou não tecla premida

    MOV  R1, linha_que_estamos   ; atualiza o valor da tecla preimda 
    MOV  R0, [R1]
    SHL  R0, 1
    MOV  R2, 16
    CMP  R0, R2
    JNZ  OUT
    MOV  R0, 1
OUT:
    MOV  [R1], R0               ; R1 fica com o valor da tecla premida              

    POP  R3
    POP  R2
    POP  R1
    POP  R0
    RET

; **********************************************************************
; HA_TECLA - Espera que uma tecla deixe de ser permida.
; **********************************************************************

ha_tecla:
    PUSH R0
    PUSH R1
    PUSH R2

    MOV  R0, TEC_LIN      ; R1 e R0 ficam com a coluna e linha respetivamente 
    MOV  R1, TEC_COL

loop_ha_tecla:
    MOV  R2, 8          
    MOVB [R0], R2       ; escrever no periférico de saída (linhas)
    MOVB R2, [R1]       ; ler do periférico de entrada (colunas)
    CMP  R2, 0          ; há tecla premida?
    JNZ  loop_ha_tecla  ; se ainda houver uma tecla premida, espera até não haver

    POP R2
    POP R1
    POP R0
    RET

; **********************************************************************
; ATRASO - Faz DELAY iterações, para implementar um atraso no tempo,
;          de forma não bloqueante.
; Argumentos: Nenhum
; Saidas:     R1 - Se 0, o atraso chegou ao fim
; **********************************************************************
atraso:
    PUSH R2
    PUSH R3
    MOV  R3, contador_alt_atraso    ; contador_alt, cujo valor vai ser mostrado nos displays
    MOV  R1, [R3]                   ; obtém valor do contador_alt do atraso
    SUB  R1, 1
    MOV  [R3], R1                   ; atualiza valor do contador_alt do atraso
    JNZ  sai
    MOV  R2, DELAY
    MOV  [R3], R2                   ; volta a colocar o valor inicial no contador_alt do atraso
sai:
    POP  R3
    POP  R2
    RET