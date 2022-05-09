#include "tecnicofs_client_api.h"
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>

#define SEPERATOR " | "

int sension_id = -1;
int pipe_handle_server = -1;
int pipe_handle_client = -1;

size_t add_separator_to_message(void **mes, size_t size_mes) { 
    (*mes) = realloc((*mes), size_mes + strlen(SEPERATOR));
    if(mes == NULL)
        exit(1);
    memcpy((*mes) + size_mes, SEPERATOR, strlen(SEPERATOR));

    return strlen(SEPERATOR); 
}

int tfs_mount(char const *client_pipe_path, char const *server_pipe_path) {
    char const op_code[] = "OP_CODE=1";
    void* mes = NULL;
    size_t size_mes = 0;

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    size_mes += add_separator_to_message(&mes, size_mes); 

    //add client pipe path
    mes = realloc(mes, size_mes + 40);
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, client_pipe_path, strlen(client_pipe_path));
    size_mes += 40;

    //create client pipe
    unlink(client_pipe_path);
    if (mkfifo(client_pipe_path, 0777) < 0)
        exit(1);
    //send message to server
    if((pipe_handle_server = open(server_pipe_path, O_WRONLY)) < 0)
        exit(1);
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1); 
    //read response from server    
    if((pipe_handle_client = open(client_pipe_path, O_RDONLY)) < 0)
        exit(1);
    if(read(pipe_handle_client, &sension_id, sizeof(int)) != sizeof(int))
        exit(1);

    return 0;
}

int tfs_unmount() {
    char const op_code[] = "OP_CODE=2";
    void* mes = NULL;
    size_t size_mes = 0;
    int resp = -1;

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    size_mes += add_separator_to_message(&mes, size_mes); 
    //add sension_id
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &sension_id, sizeof(int));
    size_mes += sizeof(int); 

    //send message to server
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1);
    //read response from server    
    if(read(pipe_handle_client, &resp, sizeof(int)) != sizeof(int))
        exit(1); 

    return 0;
}

int tfs_open(char const *name, int flags) {
    char const op_code[] = "OP_CODE=3";
    void* mes = NULL;
    size_t size_mes = 0;
    int resp = -1; 

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    //add sesion_id
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &sension_id, sizeof(int));
    size_mes += sizeof(int); 

    //add name
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + 40);
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, name, strlen(mes));
    size_mes += 40;

    //add flags
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &flags, sizeof(int));
    size_mes += sizeof(int);

    //send message to server
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1);

    //read response from server    
    if(read(pipe_handle_client, &resp, sizeof(int)) != sizeof(int))
        exit(1); 

    return resp;
}

int tfs_close(int fhandle) {
    char const op_code[] = "OP_CODE=4";
    void* mes = NULL;
    size_t size_mes = 0;
    int resp = -1; 

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    //add sesion_id
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &sension_id, sizeof(int));
    size_mes += sizeof(int); 

    //add fhandle
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &fhandle, sizeof(int));
    size_mes += sizeof(int); 

    //send message to server
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1);

    //read response from server    
    if(read(pipe_handle_client, &resp, sizeof(int)) != sizeof(int))
        exit(1); 

    return resp;
}

ssize_t tfs_write(int fhandle, void const *buffer, size_t len) {
    char const op_code[] = "OP_CODE=5";
    void* mes = NULL;
    size_t size_mes = 0;
    ssize_t resp = -1; 

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    //add sesion_id
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &sension_id, sizeof(int));
    size_mes += sizeof(int); 

    //add fhandle
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &fhandle, sizeof(int));
    size_mes += sizeof(int); 

    //add len
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(size_t));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &len, sizeof(size_t));
    size_mes += sizeof(size_t);

    //add buffer
    size_mes += add_separator_to_message(&mes, size_mes);
    mes = realloc(mes, size_mes + len);
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, buffer, len);
    size_mes += len;

    //send message to server
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1);

    //read response from server    
    if(read(pipe_handle_client, &resp, sizeof(ssize_t)) != sizeof(ssize_t))
        exit(1); 

    return resp;
}

ssize_t tfs_read(int fhandle, void *buffer, size_t len) {
    char const op_code[] = "OP_CODE=6";
    void* mes = NULL;
    size_t size_mes = 0;

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    //add sesion_id
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &sension_id, sizeof(int));
    size_mes += sizeof(int); 

    //add fhandle
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &fhandle, sizeof(int));
    size_mes += sizeof(int); 

    //add len
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(size_t));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &len, sizeof(size_t));
    size_mes += sizeof(size_t);

    //send message to server
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1);

    //read response from server    
    char resp[len];
    if(read(pipe_handle_client, &resp, len) != len)
        exit(1);
    memcpy(buffer, resp, len);

    return (int) strlen(resp);
}

int tfs_shutdown_after_all_closed() {
    char const op_code[] = "OP_CODE=7";
    void* mes = NULL;
    size_t size_mes = 0;

    //create message to server
    //add OP_CODE
    mes = realloc(mes, strlen(op_code)); 
    if(mes == NULL)
        exit(1);
    memcpy(mes, op_code, strlen(op_code));
    size_mes += strlen(op_code);

    //add sesion_id
    size_mes += add_separator_to_message(&mes, size_mes); 
    mes = realloc(mes, size_mes + sizeof(int));
    if(mes == NULL)
        exit(1);
    memcpy(mes + size_mes, &sension_id, sizeof(int));
    size_mes += sizeof(int); 

    //send message to server
    if(write(pipe_handle_server, mes, size_mes) != size_mes)
        exit(1);

    //read response from server    
    int resp;
    if(read(pipe_handle_client, &resp, sizeof(int)) != sizeof(int))
        exit(1);

    return resp;
}   

