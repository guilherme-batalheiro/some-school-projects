#include "operations.h"
#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int tfs_init() {
    state_init();

    /* create root inode */
    int root = inode_create(T_DIRECTORY);
    if (root != ROOT_DIR_INUM) {
        return -1;
    }

    return 0;
}

int tfs_destroy() {
    state_destroy();
    return 0;
}

static bool valid_pathname(char const *name) {
    return name != NULL && strlen(name) > 1 && name[0] == '/';
}

int tfs_lookup(char const *name) {
    if (!valid_pathname(name)) {
        return -1;
    }

    // skip the initial '/' character
    name++;

    return find_in_dir(ROOT_DIR_INUM, name);
}

int tfs_open(char const *name, int flags) {
    int inum;
    size_t offset;

    /* Checks if the path name is valid */
    if (!valid_pathname(name)) {
        return -1;
    }

    inum = tfs_lookup(name);
    if (inum >= 0) {
        /* The file already exists */
        inode_t *inode = inode_get(inum);
        if (inode == NULL) {
            return -1;
        }

        pthread_rwlock_wrlock(&inode->rwlock);
        /* Trucate (if requested) */
        if (flags & TFS_O_TRUNC) {
            if (inode->i_size > 0) {
                if (data_block_free(inode->i_data_blocks[0]) == -1) {
                    pthread_rwlock_unlock(&inode->rwlock);
                    return -1;
                }
                inode->i_size = 0;
            }
        }
        /* Determine initial offset */
        if (flags & TFS_O_APPEND) {
            offset = inode->i_size;
        } else {
            offset = 0;
        }
        pthread_rwlock_unlock(&inode->rwlock);
    } else if (flags & TFS_O_CREAT) {
        /* The file doesn't exist; the flags specify that it should be created*/
        /* Create inode */
        inum = inode_create(T_FILE);
        if (inum == -1) {
            return -1;
        }
        /* Add entry in the root directory */
        if (add_dir_entry(ROOT_DIR_INUM, inum, name + 1) == -1) {
            inode_delete(inum);
            return -1;
        }
        offset = 0;
    } else {
        return -1;
    }

    /* Finally, add entry to the open file table and
     * return the corresponding handle */
    return add_to_open_file_table(inum, offset);

    /* Note: for simplification, if file was created with TFS_O_CREAT and there
     * is an error adding an entry to the open file table, the file is not
     * opened but it remains created */
}

int tfs_close(int fhandle) { return remove_from_open_file_table(fhandle); }

ssize_t tfs_write(int fhandle, void const *buffer, size_t to_write) {
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) {
        return -1;
    }

    /* From the open file table entry, we get the inode */
    inode_t *inode = inode_get(file->of_inumber);
    if (inode == NULL) {
        return -1;
    }

    pthread_rwlock_wrlock(&inode->rwlock);

    /* Save what we wanted to write */
    size_t goal_to_write = to_write;

    /* Index of the current block */
    size_t current_block_index = file->of_offset / BLOCK_SIZE;

    while (to_write != 0 && file->of_offset <= MAX_FILE_CONTENT) {
        size_t offset_in_block = file->of_offset % BLOCK_SIZE;

        /* Determine how many bytes to write in the current block */
        size_t to_write_in_current_block = to_write;
        if (to_write + offset_in_block > BLOCK_SIZE) {
            /* Write what we can inside the current block */
            to_write_in_current_block = BLOCK_SIZE - offset_in_block;
        }

        if (current_block_index == INODE_BLOCK_NUMBER &&
            inode->i_data_blocks[current_block_index] == -1) {
            /* Initialize the current block and set all the addresses to -1 if
             * it is the last block (block storing the addresses of other
             * blocks) and it hasn't been initialized yet */
            inode->i_data_blocks[current_block_index] = data_block_alloc();
            void *block =
                data_block_get(inode->i_data_blocks[current_block_index]);
            if (block == NULL) {
                pthread_rwlock_unlock(&inode->rwlock);
                pthread_mutex_unlock(&file->mutex_lock);
                return -1;
            }
            memset(block, -1, INODE_BLOCK_MAX * sizeof(int));
        }

        /* Determine the block id */
        int block_id = -1;
        if (current_block_index >= INODE_BLOCK_NUMBER) {
            void *block =
                data_block_get(inode->i_data_blocks[INODE_BLOCK_NUMBER]);
            if (block == NULL) {
                pthread_rwlock_unlock(&inode->rwlock);
                pthread_mutex_unlock(&file->mutex_lock);
                return -1;
            }

            size_t inside_block_index =
                current_block_index - INODE_BLOCK_NUMBER;

            memcpy(&block_id, block + inside_block_index * sizeof(int),
                   sizeof(int));
            if (block_id == -1) {
                block_id = data_block_alloc();
                if (block_id == -1) {
                    pthread_rwlock_unlock(&inode->rwlock);
                    pthread_mutex_unlock(&file->mutex_lock);
                    return -1;
                }
                memcpy(block + inside_block_index * sizeof(int), &block_id,
                       sizeof(int));
            }
        } else {
            block_id = inode->i_data_blocks[current_block_index];
            if (block_id == -1) {
                block_id = data_block_alloc();
                if (block_id == -1) {
                    pthread_rwlock_unlock(&inode->rwlock);
                    pthread_mutex_unlock(&file->mutex_lock);
                    return -1;
                }
                inode->i_data_blocks[current_block_index] = block_id;
            }
        }

        void *block = NULL;
        block = data_block_get(block_id);
        if (block == NULL) {
            pthread_rwlock_unlock(&inode->rwlock);
            pthread_mutex_unlock(&file->mutex_lock);
            return -1;
        }

        /* Perform the actual write */
        memcpy(block + offset_in_block, buffer + (goal_to_write - to_write),
               to_write_in_current_block);

        /* The offset associated with the file handle is
         * incremented accordingly */
        file->of_offset += to_write_in_current_block;
        if (file->of_offset > inode->i_size) {
            inode->i_size = file->of_offset;
        }

        /* Remove what has already been written */
        to_write -= to_write_in_current_block;
        current_block_index =
            file->of_offset / BLOCK_SIZE; /* index of the current block. */
    }

    pthread_rwlock_unlock(&inode->rwlock);
    pthread_mutex_unlock(&file->mutex_lock);
    return (ssize_t)(goal_to_write - to_write);
}

ssize_t tfs_read(int fhandle, void *buffer, size_t len) {
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) {
        return -1;
    }
    
    /* From the open file table entry, we get the inode */
    inode_t *inode = inode_get(file->of_inumber);
    if (inode == NULL) {
        return -1;
    }

    pthread_rwlock_rdlock(&inode->rwlock);

    size_t to_read = inode->i_size - file->of_offset;
    if (to_read > len) {
        to_read = len;
    }

    size_t goal_to_read = to_read;

    /* Index of the current block */
    size_t current_block_index = file->of_offset / BLOCK_SIZE;

    while (to_read != 0 && file->of_offset < inode->i_size) {
        size_t offset_in_block =
            file->of_offset - current_block_index * BLOCK_SIZE;

        /* Determine how many bytes to read in the current block */
        size_t to_read_in_block = to_read;
        if (to_read + offset_in_block > BLOCK_SIZE) {
            /* Read what we can inside the current block */
            to_read_in_block = BLOCK_SIZE - offset_in_block;
        }

        /* Determine the block id */
        int block_id = -1;
        if (current_block_index >= INODE_BLOCK_NUMBER) {
            /* If the current block is in the last block that have the
             * reference to other blocks. */
            void *block =
                data_block_get(inode->i_data_blocks[INODE_BLOCK_NUMBER]);
            if (block == NULL) {
                pthread_rwlock_unlock(&inode->rwlock);
                pthread_mutex_unlock(&file->mutex_lock);
                return -1;
            }

            size_t inside_block_index =
                current_block_index - INODE_BLOCK_NUMBER;

            memcpy(&block_id, block + inside_block_index * sizeof(int),
                   sizeof(int));
        } else {
            block_id = inode->i_data_blocks[current_block_index];
        }

        void *block = data_block_get(block_id);
        if (block == NULL) {
            pthread_rwlock_unlock(&inode->rwlock);
            pthread_mutex_unlock(&file->mutex_lock);
            return -1;
        }

        /* Perform the actual read. */
        memcpy(buffer + (goal_to_read - to_read), block + offset_in_block,
               to_read_in_block);

        /* The offset associated with the file handle is
         * incremented accordingly */
        file->of_offset += to_read_in_block;
        if (file->of_offset > inode->i_size) {
            inode->i_size = file->of_offset;
        }

        /* Remove the data we already read. */
        to_read -= to_read_in_block;
        current_block_index = file->of_offset / BLOCK_SIZE;
    }

    pthread_rwlock_unlock(&inode->rwlock);
    pthread_mutex_unlock(&file->mutex_lock);
    return (ssize_t)(goal_to_read - to_read);
}

int tfs_copy_to_external_fs(char const *source_path, char const *dest_path) {
    int file = tfs_open(source_path, 0); // opens tfs file
    if (file == -1)
        return -1;

    /* creates an empty local file for writing. If a file with the same name
    already exists its content is erased and the file is considered as a new
    empty file */
    FILE *file_to_write = fopen(dest_path, "w");
    if (file_to_write == NULL)
        return -1;

    ssize_t read = 0;
    size_t already_read = 0; // we will need an offset because
                             // sizeof(buffer) = 8 (sizeof(char *ptr) = 8)
    int blocks = 1;

    char *buffer = NULL;

    do {
        buffer = realloc(
            buffer,
            (size_t)(blocks *
                     BLOCK_SIZE)); // extends the buffer <BLOCK_SIZE> Bytes
        memset(buffer + already_read, 0,
               sizeof(char) *
                   BLOCK_SIZE); // cleans the extended part of the memory zone
        //            ^  buffer offset so we are not overriting on the
        //            buffer
        read = tfs_read(file, buffer + already_read,
                        BLOCK_SIZE); // reads another <BLOCK_SIZE>
        if (read == -1)
            return -1;

        already_read += (size_t)read;
        blocks++;
    } while (read != 0); // stops when tfs_read reads 0 characters

    /* writes all the content on the buffer to the destination file */
    if (fwrite(buffer, sizeof(char), already_read, file_to_write) == -1)
        return -1;

    /* close the files */
    if (tfs_close(file) == -1)
        return -1;
    if (fclose(file_to_write) != 0)
        return -1;

    free(buffer);

    return 0;
}