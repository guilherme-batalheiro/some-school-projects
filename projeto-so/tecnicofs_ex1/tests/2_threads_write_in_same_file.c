#include "../fs/operations.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

#define SIZE 21230

void *process1(void *args){
    
    int fd = *(int *)args;

    char input[SIZE]; 
    memset(input, 'A', SIZE);

    assert(fd != -1);
    assert(tfs_write(fd, input, SIZE) == SIZE);

    tfs_copy_to_external_fs("/out", "out");

    return NULL;
}

void *process2(void *args){
    
    int fd = *(int *)args;

    char input[SIZE]; 
    memset(input, 'B', SIZE);

    assert(fd != -1);
    assert(tfs_write(fd, input, SIZE) == SIZE);

    tfs_copy_to_external_fs("/out", "out");

    return NULL;
}

int main() {
    pthread_t tid[2];

    assert(tfs_init() != -1);

    int fd = tfs_open("/out", TFS_O_CREAT);
    assert(fd != -1);
    assert(tfs_close(fd) != -1);

    fd = tfs_open("/out", 0);

    if (pthread_create(&tid[0], NULL, process1, &fd))
        exit(EXIT_FAILURE);
    if (pthread_create(&tid[1], NULL, process2, &fd))
        exit(EXIT_FAILURE);
    
    pthread_join(tid[0], NULL);
    pthread_join(tid[1], NULL);

    assert(tfs_close(fd) != -1);

    char input1[SIZE];
    memset(input1, 'A', SIZE);
    char input2[SIZE];
    memset(input2, 'B', SIZE);

    char output [SIZE];

    FILE *fp = fopen("out", "r");

    assert(fp != NULL);

    assert(fread(output, sizeof(char), 1, fp) == 1);

    if(output[0] == 'A'){
        assert(fread(output, sizeof(char), SIZE - 1, fp) == SIZE - 1);
        assert(memcmp(input1, output, SIZE - 1) == 0);
        assert(fread(output, sizeof(char), SIZE, fp) == SIZE);
        assert(memcmp(input2, output, SIZE - 1) == 0);
    } else {
        assert(fread(output, sizeof(char), SIZE - 1, fp) == SIZE - 1);
        assert(memcmp(input2, output, SIZE - 1) == 0);
        assert(fread(output, sizeof(char), SIZE, fp) == SIZE);
        assert(memcmp(input1, output, SIZE - 1) == 0);
    }

    assert(fclose(fp) != -1);

    unlink("out");

    printf("Sucessful test\n");

    return 0;
}