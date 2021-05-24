#ifndef _PROJ2_
#define _PROJ2_

#define MAX_CHAR_INSTRUCT 65536

#define MAX_COMMAND_CHAR 7

#define HELP_COMMAND "help"
#define QUIT_COMMAND "quit"
#define SET_COMMAND "set"
#define PRINT_COMMAND "print"
#define FIND_COMMAND "find"
#define LIST_COMMAND "list"
#define SEARCH_COMMAND "search"
#define DELETE_COMMAND "delete"

#define HELP_MESSAGE HELP_COMMAND ": Imprime os comandos disponíveis.\n"\
                     QUIT_COMMAND ": Termina o programa.\n"\
                     SET_COMMAND ": Adiciona ou modifica o valor a armazenar.\n"\
                     PRINT_COMMAND ": Imprime todos os caminhos e valores.\n"\
                     FIND_COMMAND ": Imprime o valor armazenado.\n"\
                     LIST_COMMAND ": Lista todos os componentes imediatos de um sub-caminho.\n"\
                     SEARCH_COMMAND ": Procura o caminho dado um valor.\n"\
                     DELETE_COMMAND ": Apaga um caminho e todos os subcaminhos.\n"\

#endif