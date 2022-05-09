#include "client/tecnicofs_client_api.h"
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
/*  This test is similar to test1.c from the 1st exercise.
    The main difference is that this one explores the
    client-server architecture of the 2nd exercise. */

int main() {
    char *str = "AAA!";
    char *path0 = "/f1";
    char *path1 = "/f2";
    char buffer[40];
    char *c1 ="c1";
    char *c2 ="c2";
    char *ser ="p1";

    int f;
    ssize_t r;
    
    if(fork() == 0) {
        assert(tfs_mount(c1, ser) == 0);

        f = tfs_open(path0, TFS_O_CREAT);
        assert(f != -1);

        r = tfs_write(f, str, strlen(str));
        assert(r == strlen(str));

        assert(tfs_close(f) != -1);

        f = tfs_open(path0, 0);
        assert(f != -1);

        r = tfs_read(f, buffer, sizeof(buffer) - 1);
        assert(r == strlen(str));

        buffer[r] = '\0';
        assert(strcmp(buffer, str) == 0);

        assert(tfs_close(f) != -1);


        assert(tfs_unmount() == 0);
    } else {
        assert(tfs_mount(c2, ser) == 0);

        f = tfs_open(path1, TFS_O_CREAT);
        assert(f != -1);

        r = tfs_write(f, str, strlen(str));
        assert(r == strlen(str));

        assert(tfs_close(f) != -1);

        f = tfs_open(path1, 0);
        assert(f != -1);

        r = tfs_read(f, buffer, sizeof(buffer) - 1);
        assert(r == strlen(str));

        buffer[r] = '\0';
        assert(strcmp(buffer, str) == 0);

        assert(tfs_close(f) != -1);


        assert(tfs_unmount() == 0);
    }
    
    printf("Successful test.\n");
    return 0;
}
/*
int main(int argc, char **argv) {
    char *str = "AAA!";
    char *path = "/f1";
    char buffer[40];

    int f;
    ssize_t r;

    if (argc < 3) {
        printf("You must provide the following arguments: 'client_pipe_path "
               "server_pipe_path'\n");
        return 1;
    }
    
    assert(tfs_mount(argv[1], argv[2]) == 0);

    f = tfs_open(path, TFS_O_CREAT);
    assert(f != -1);

    r = tfs_write(f, str, strlen(str));
    assert(r == strlen(str));

    assert(tfs_close(f) != -1);

    f = tfs_open(path, 0);
    assert(f != -1);

    r = tfs_read(f, buffer, sizeof(buffer) - 1);
    assert(r == strlen(str));

    buffer[r] = '\0';
    assert(strcmp(buffer, str) == 0);

    assert(tfs_close(f) != -1);

    assert(tfs_shutdown_after_all_closed() == 1);

    assert(tfs_unmount() == 0);
    
    printf("Successful test.\n");
    return 0;
}
*/
