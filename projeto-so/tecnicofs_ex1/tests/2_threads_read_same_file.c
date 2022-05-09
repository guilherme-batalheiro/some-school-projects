#include "../fs/operations.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

#define SIZE 21230

void *process1(){
    int fd = tfs_open("/out", TFS_O_CREAT);
    assert(fd != -1);

    char input[SIZE];
    memset(input, 'A', SIZE);

    char output[SIZE];

    tfs_read(fd, output, SIZE);

    assert(tfs_close(fd) != -1);

    return NULL;
}

void *process2(){
    int fd = tfs_open("/out", TFS_O_CREAT);
    assert(fd != -1);

    char input[SIZE];
    memset(input, 'A', SIZE);

    char output[SIZE];

    tfs_read(fd, output, SIZE);

    assert(tfs_close(fd) != -1);

    return NULL;
}

int main() {
    pthread_t tid[2];

    assert(tfs_init() != -1);

    char input[SIZE]; 
    memset(input, 'A', SIZE);

    int fd = tfs_open("/out", TFS_O_CREAT);
    assert(fd != -1);

    assert(tfs_write(fd, input, SIZE) == SIZE);
    assert(tfs_close(fd) != -1);

    if (pthread_create(&tid[0], NULL, process1, NULL))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[1], NULL, process2, NULL))
        exit(EXIT_FAILURE);
    
    pthread_join(tid[0], NULL);
    pthread_join(tid[1], NULL);

    printf("Sucessful test\n");

    return 0;
}