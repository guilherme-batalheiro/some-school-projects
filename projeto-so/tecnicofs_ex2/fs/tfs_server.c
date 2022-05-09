#include "operations.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>
#include <stdatomic.h>
#include <errno.h>

#define S                       5
#define LEN_ID                  2
#define OP_CODE_INPUT           "OP_CODE=%d"
#define OP_CODE_SIZE            9
#define MAX_ARG                 40
#define SEP                     " | "
#define CREATED                 1
#define NOT_CREATED             0
#define OP_CODE_SIZE            9
#define CLIENT_PIPE_NAME_SIZE   40

int fserv;
int free_sessions[S];
int sessions_pipes[S];
pthread_t clients_thread[S];
pthread_mutex_t sessions_mutexes[S];
pthread_cond_t sessions_conditions[S];

int tfs_open_files[MAX_OPEN_FILES];

int job_of_thread[S];
char *threads_requestv[S];
int threads_createdv[S];

void server_tfs_read(int session_id) {
    size_t len = 1;
    int file_handle = -1;
    char *request = threads_requestv[session_id];

    memcpy(&file_handle, request, sizeof(int));
    memcpy(&len, request + sizeof(int), sizeof(size_t)); 

    char content[len];
    tfs_read(file_handle, content, len); 

    if(write(sessions_pipes[session_id], content, len) != len)
        exit(1);
}

void server_tfs_write(int session_id) {
    size_t len = 1;
    ssize_t resp = 0;
    int file_handle = -1;
    char *request = threads_requestv[session_id];

    memcpy(&file_handle, request, sizeof(int));
    memcpy(&len, request + sizeof(int), sizeof(size_t)); 
    char content[len];
    memcpy(content, request + sizeof(int) + sizeof(size_t), len);

    resp = tfs_write(file_handle, content, len); 
    //send client succefull information
    if(write(sessions_pipes[session_id], &resp, sizeof(ssize_t)) != sizeof(ssize_t))
        exit(1);
}

void server_tfs_close(int session_id) {
    int file_handle = -1, resp = -1;
    char *request = threads_requestv[session_id];

    memcpy(&file_handle, request, sizeof(int)); 
    resp = tfs_close(file_handle);

    //send client succefull information
    if(write(sessions_pipes[session_id], &resp, sizeof(int)) != sizeof(int))
        exit(1);
}

void server_tfs_open(int session_id) {
    int flag = -1, resp = -1;
    char file_name[MAX_FILE_NAME], 
         *request = threads_requestv[session_id];

    memcpy(file_name, request, MAX_FILE_NAME);
    memcpy(&flag, request + MAX_FILE_NAME, sizeof(int));

    resp = tfs_open(file_name, flag);

    //send client succefull information
    if(write(sessions_pipes[session_id], &resp, sizeof(int)) != sizeof(int))
        exit(1);
}

void server_tfs_shutdown_after_all_close(int session_id) {
    int resp = tfs_destroy_after_all_closed();

    //send client succefull information
    if(write(sessions_pipes[session_id], &resp, sizeof(int)) != sizeof(int))
        exit(1);
}

void *start_client_working_thread(void *arg) {
    int session_id = *((int *)arg);
    pthread_mutex_t *mutex_ptr = &sessions_mutexes[session_id];
    pthread_cond_t *condiction_ptr = &sessions_conditions[session_id];

    while(1) {
        if(pthread_mutex_lock(mutex_ptr) != 0)
            exit(1);
        while(job_of_thread[session_id] == -1) {
            if(pthread_cond_wait(condiction_ptr, mutex_ptr) != 0) 
                exit(1);
        }

        switch(job_of_thread[session_id]){
            case 3: 
                server_tfs_open(session_id);
                break;
            case 4:
                server_tfs_close(session_id);
                break;
            case 5:
                server_tfs_write(session_id);
                break;
            case 6:
                server_tfs_read(session_id);
                break;
            case 7:
                server_tfs_shutdown_after_all_close(session_id);
                break;
            default:
                break;

        } 

        job_of_thread[session_id] = -1;

        if(pthread_mutex_unlock(mutex_ptr) != 0){
            printf("mutec unloxk\n");
            exit(1);
        }
    } 

    return NULL;
}

void give_thread_work(int op_code) {
    printf("start to give job...\n");
    int session_id = -1;
    char sep[strlen(SEP)];

    //read seperator
    if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
        exit(1);
    //read session
    if(read(fserv, &session_id, sizeof(int)) != sizeof(int))
        exit(1);

    //chekc if it is a valid id
    if(session_id < 0 || session_id > S)
        exit(1);

    //check if it is a valid session
    if(free_sessions[session_id] != TAKEN)
        exit(1);

    //send signal and pass input to thread
    if(pthread_mutex_lock(&sessions_mutexes[session_id]) != 0)
        exit(1);

    job_of_thread[session_id] = op_code;

    switch (op_code) {
        case 3:
            printf("gived job 3.\n");
            threads_requestv[session_id] = realloc(threads_requestv[session_id], MAX_FILE_NAME + sizeof(int));
            if(threads_requestv[session_id] == NULL)
                exit(1);
            //read seperator
            if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
                exit(1);
            //read name
            if(read(fserv, threads_requestv[session_id], MAX_FILE_NAME) != MAX_FILE_NAME)
                exit(1);
            printf("name of job 3 -%s-\n", threads_requestv[session_id]);
            //read seperator
            if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
                exit(1);
            //read flag
            if(read(fserv, threads_requestv[session_id] + MAX_FILE_NAME, sizeof(int)) != sizeof(int))
                exit(1);
            printf("flag of job 3 -%d-\n", *((int *)threads_requestv[session_id] + MAX_FILE_NAME));
            break;
        case 4:
            printf("gived job 4.\n");
            threads_requestv[session_id] = realloc(threads_requestv[session_id], sizeof(int));
            if(threads_requestv[session_id] == NULL)
                exit(1);
            //read seperator
            if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
                exit(1);
            //read fhandle
            if(read(fserv, threads_requestv[session_id], sizeof(int)) != sizeof(int))
                exit(1);
            printf("handle in job 4 %d\n", *(int *)threads_requestv[session_id]);
            break;
        case 5:
            printf("gived job 5.\n");
            threads_requestv[session_id] = realloc(threads_requestv[session_id], sizeof(int) + sizeof(size_t));
            if(threads_requestv[session_id] == NULL)
                exit(1);
            //read seperator
            if(read(fserv, sep, strlen(SEP))!= strlen(SEP))
                exit(1);
            //read fhandle
            read(fserv, threads_requestv[session_id], sizeof(int));
            //read seperator
            if(read(fserv, sep, strlen(SEP))!= strlen(SEP))
                exit(1);
            //read len
            size_t len = 0;
            if(read(fserv, &len, sizeof(size_t)) != sizeof(size_t))
                exit(1);
            memcpy(threads_requestv[session_id] + sizeof(int), &len, sizeof(size_t));
            printf("len of job 5 is %ld\n", *(size_t *)(threads_requestv[session_id] + sizeof(int)));
            threads_requestv[session_id] = realloc(threads_requestv[session_id], sizeof(int) + sizeof(size_t) + len);
            //read seperator
            if(read(fserv, sep, strlen(SEP))!= strlen(SEP))
                exit(1);
            //read content
            if(read(fserv, threads_requestv[session_id] + sizeof(int) + sizeof(size_t), len) != len)
                exit(1);
            printf("write content is -%s-\n", threads_requestv[session_id] + sizeof(int) + sizeof(size_t));
            break;
        case 6:
            threads_requestv[session_id] = realloc(threads_requestv[session_id], sizeof(int) + sizeof(size_t));
            if(threads_requestv[session_id] == NULL)
                exit(1);
            //read seperator
            if(read(fserv, sep, strlen(SEP))!= strlen(SEP))
                exit(1);
            //read fhandle
            if(read(fserv, threads_requestv[session_id], sizeof(int)) != sizeof(int))
                exit(1);
            //read seperator
            if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
                exit(1);
            //read len
            if(read(fserv, threads_requestv[session_id] + sizeof(int), sizeof(size_t)) != sizeof(size_t))
                exit(1);
            break;
        case 7:
            //no args
            break;
        default:
            break;
    }
    if(pthread_mutex_unlock(&sessions_mutexes[session_id]) != 0)
        exit(1);
    if(pthread_cond_signal(&sessions_conditions[session_id]) != 0)
        exit(1);
}

void mount_client() {
    int session_id = -1, pipe;
    char client_pipe_name[MAX_ARG] = {0};
    char sep[strlen(SEP)];

    //read seperator
    if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
        exit(1);
    //read clent pipe name
    if(read(fserv, client_pipe_name, CLIENT_PIPE_NAME_SIZE) != CLIENT_PIPE_NAME_SIZE)
        exit(1);

    //find first session FREE
    for(int i = 0; i < S; i++){
        if(free_sessions[i] == FREE) {
            session_id = i;
            free_sessions[i] = TAKEN;
            break;
        }
    }

    //if not found a FREE session return
    if(session_id == -1)
        exit(1);
    printf("seession id = %d\n", session_id);
    //open named pipe to talk with client
    printf("client pipe -%s-\n", client_pipe_name);
    if((pipe = open(client_pipe_name, O_WRONLY)) < 0)
        exit(1);

    //save pipe
    sessions_pipes[session_id] = pipe;

    //check if thread already created
    if(threads_createdv[session_id] == NOT_CREATED) { 
        //init mutex
        if(pthread_mutex_init(&sessions_mutexes[session_id], NULL) != 0)
            exit(1);

        //init condition variable
        if(pthread_cond_init(&sessions_conditions[session_id], NULL) != 0)
            exit(1);

        //create client thread
        if(pthread_create(&clients_thread[session_id], NULL, &start_client_working_thread, &session_id) != 0)
            exit(1);

        threads_createdv[session_id] = CREATED;
    } 

    //send client succefull information
    if(write(pipe, &session_id, sizeof(int)) != sizeof(int))
        exit(1);
    printf("finishing mounting!\n");
}

void unmount_client() {
    int session_id = -1, pipe;
    char sep[strlen(SEP)];
    printf("unmounting ...\n"); 
    //read seperator
    if(read(fserv, sep, strlen(SEP)) != strlen(SEP))
        exit(1);
    //read sension
    if(read(fserv, &session_id, sizeof(int)) != sizeof(int))
        exit(1);

    if(session_id < 0 || session_id > S)
        exit(1);

    //free section
    if(free_sessions[session_id] != TAKEN)
        exit(1);
    free_sessions[session_id] = FREE;

    //get section client pipe
    pipe = sessions_pipes[session_id];

    //send client succefull information
    int res = 0;
    if(write(pipe, &res, sizeof(int)) != sizeof(int))
        exit(1);

    if(close(pipe) != 0)
        exit(1);
    printf("finish unmounting!\n");
}

int main(int argc, char **argv) {
    ssize_t n = 0;
    char op_code_str[OP_CODE_SIZE];
    int op_code = -1;

    if (argc < 2) {
        printf("Please specify the pathname of the server's pipe.\n");
        return 1;
    }

    char *pipename = argv[1];
    printf("Starting TecnicoFS server with pipe called %s\n", pipename);

    for(int i = 0; i < S; i++){
        free_sessions[i] = FREE;
        job_of_thread[0] = -1;
    }

    if(tfs_init() == -1)
        return 1;

    if (unlink(pipename) != 0 && errno != ENOENT) 
        exit(1);

    if (mkfifo(pipename, 0777) < 0)
        exit(1);

    if((fserv = open(pipename, O_RDONLY)) < 0)
        exit(1);

    for(;;) {
        memset(op_code_str, '\0', OP_CODE_SIZE);
        n = read(fserv, op_code_str, OP_CODE_SIZE);
        if(n < 0) break;
        if(n > 0) { 
            //get operation code
            if(sscanf(op_code_str, OP_CODE_INPUT, &op_code) == EOF)
                break;

            //select operation 
            switch(op_code) {
                case 1:
                    mount_client();
                    break;
                case 2:
                    unmount_client();
                    break;
                default:
                    if(op_code < 8 && op_code > 2) give_thread_work(op_code);
                    break;
            }
        }
    }

    return 0;
}

