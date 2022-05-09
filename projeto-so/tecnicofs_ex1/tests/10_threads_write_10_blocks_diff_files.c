#include "../fs/operations.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

#define COUNT 250
#define SIZE 250

void *process(void *args){
    
    char *path = (char *)args;

    char input[SIZE]; 
    memset(input, path[1], SIZE);

    char output [SIZE];

    int fd = tfs_open(path, TFS_O_CREAT);
    assert(fd != -1);
    for (int i = 0; i < COUNT; i++) {
        assert(tfs_write(fd, input, SIZE) == SIZE);
    }
    assert(tfs_close(fd) != -1);

    fd = tfs_open(path, 0);
    assert(fd != -1 );

    for (int i = 0; i < COUNT; i++) {
        assert(tfs_read(fd, output, SIZE) == SIZE);
        assert (memcmp(input, output, SIZE) == 0);
    }

    tfs_copy_to_external_fs(path, path + 1);

    FILE *fp = fopen(path + 1, "r");

    assert(fp != NULL);

    for (int i = 0; i < COUNT; i++) {
        assert(fread(output, sizeof(char), SIZE, fp) == SIZE);
        assert(memcmp(input, output, SIZE) == 0);
    }

    assert(fclose(fp) != -1);

    unlink(path + 1);

    return NULL;
}

int main() {
    pthread_t tid[10];

    assert(tfs_init() != -1);
    

    char* t1 = "/Athread1";
    char* t2 = "/Bthread2";
    char* t3 = "/Cthread3";
    char* t4 = "/Dthread4";
    char* t5 = "/Ethread5";
    char* t6 = "/Fthread6";
    char* t7 = "/Gthread7";
    char* t8 = "/Hthread8";
    char* t9 = "/Ithread9";
    char* t10 = "/Jthread10";

    if (pthread_create(&tid[0], NULL, process, t1))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[1], NULL, process, t2))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[2], NULL, process, t3))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[3], NULL, process, t4))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[4], NULL, process, t5))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[5], NULL, process, t6))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[6], NULL, process, t7))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[7], NULL, process, t8))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[8], NULL, process, t9))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[9], NULL, process, t10))
        exit(EXIT_FAILURE);
    
    pthread_join(tid[0], NULL);
    pthread_join(tid[1], NULL);
    pthread_join(tid[2], NULL);
    pthread_join(tid[3], NULL);
    pthread_join(tid[4], NULL);
    pthread_join(tid[5], NULL);
    pthread_join(tid[6], NULL);
    pthread_join(tid[7], NULL);
    pthread_join(tid[8], NULL);
    pthread_join(tid[9], NULL);

    printf("Sucessful test\n");

    return 0;
}