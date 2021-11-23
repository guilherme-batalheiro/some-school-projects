/*
    * File:  proj1.c
    * Author:  Guilherme Batalheiro
    * Description: Kanban method in C.
    * Detail description:
        This is a project task management system. To accomplish this, the project has been divided 
        into tasks that can be completed in parallel. Tasks are carried out by system users who are 
        responsible for them at the time of execution. Several activities are assigned to the task 
        during its execution. Each activity carries out a specific task.To compare the time spent on
        a task with the time estimated at the start, the total time spent on the task must be 
        counted. As a result, delays in the overall execution of the project can be identified, and 
        if justified, more personnel can be affected.
*/
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#define MAX_INPUT 1000000 /* The maximum number of characters that can be read from stdin. */

#define MAX_TASK 10000 /* The maximum number of tasks */
#define MAX_TASK_CHR 50 + 1 /* The maximum number of char in a task description + '\0' */
#define MAX_USERS 50 /* The maximum number of users */
#define MAX_USERS_CHR 20 + 1 /* The maximum number of char in a user name + '\0' */
#define MAX_ACTIVITY 10 /* The maximum number of activities */
#define MAX_ACTIVITY_CHR 20  + 1 /* The maximum number of char in a user name + '\0' */

#define BIGGEST_CHAR 20  + 1 /* The maximum number of char stored + '\0' */

#define TRUE 1
#define FALSE 0

typedef struct {
    int id;
    char description[MAX_TASK_CHR];
    char user[MAX_USERS_CHR];
    char activity[MAX_ACTIVITY_CHR];
    int duration;
    int start;
} Task;

/* Global variables */
Task tasks[MAX_TASK];
int taskCount = 0;

char users[MAX_USERS][MAX_USERS_CHR];
int userCount = 0;

char activities[MAX_ACTIVITY][MAX_ACTIVITY_CHR] = {"TO DO", "IN PROGRESS", "DONE"};
int activityCount = 3;

int timeCount = 0; 
/* ############### */

int descriptions_sort(Task task1, Task task2){
    return strcmp(task1.description, task2.description) < 0;
}

int start_sort(Task task1, Task task2){
    return task1.start - task2.start < 0;
}

void exch(Task* tasks, int i, int j){
    Task t;
    t = tasks[i];
    tasks[i] = tasks[j];
    tasks[j] = t;
}

int partition(Task a[], int l, int r, int (*operation)(Task, Task)) {
    int i = l-1, j = r, mid = l+(r-l)/2;
    Task v;

    if (operation(a[mid], a[l])) exch(a, mid, l);
    if (operation(a[mid], a[r])) exch(a, mid, r);
    if (operation(a[r], a[l])) exch(a, r, l);

    v = a[r];

    while (i < j) {
        while (operation(a[++i], v));
        while (operation(v, a[--j]))
            if (j == l)
                break;
        if (i < j)
            exch(a, i, j);
    }
    exch(a, i, r);
    return i;
}


void sort(Task a[], int l, int r, int (*operation)(Task, Task)){
    int i;
    if(r <= l) return;
    
    i = partition(a, l, r, operation);
    sort(a, l, i - 1, operation);
    sort(a, i + 1, r, operation);
}

int duplicated(char source[][BIGGEST_CHAR], char* flag, int size){
    /*Check if a string is in a array of strings

        Param source: array of strings
        Param flag: string
        Param size: integer
        Return: integer
    */

    int i;
    for(i = 0; i < size; ++i){
        if(!strcmp(source[i], flag))
            return TRUE;
    }
    return FALSE;
}


void copy_tasks(Task* destiny, Task* source, int sourceSize){
    /*Copy a array of tasks into another

        Param dest: Task array
        Param source: Task array
    */
    int i;
    for(i = 0; i < sourceSize; ++i) destiny[i] = source[i];
}

int filter_by_activity(Task* destiny, Task* source, int sourceCount, char* activity){
    /*Filter a array of tasks based on their activity.
        Filter an array of tasks by their activity, save the new array in destiny, and return the 
        array's size.

        Param destiny: Task array
        Param source: Task array
        Param sourceCount: integer
        Param activity: string
        Return: integer
    */
    
    
    int i, destinyCount = 0;
    for(i = 0; i < sourceCount; ++i) 
        if(!strcmp(source[i].activity, activity))
            destiny[destinyCount++] = source[i];
    return destinyCount;
}

void print_task_array_d_command(Task* array, int arraySize){
    /* Cleans up the code. */
    int i;
    for(i = 0; i < arraySize; ++i) printf("%d %d %s\n", array[i].id, 
                                                        array[i].start, 
                                                        array[i].description);
}

void list_tasks_in_activity(){
    /*list all tasks in a particular activity.
        Print all tasks in a specific activity, line by line, in the order in which they were 
        assigned. If both tasks' start times are the same, print all tasks alphabetically by 
        description, line by line, with the id start time and description.

        Input format: <activity>
        Print format for each task: <id> <start> <description>
    */

    int orderedTaskCount, tmpStartTime, i, rightFlag = -1, leftFlag = 0;
    char input[MAX_INPUT] = {'\0'}, activityInp[MAX_ACTIVITY_CHR] = {'\0'};
    Task orderedTasks[MAX_TASK]; 

    fgets(input, MAX_INPUT, stdin);
    sscanf(input, " %20[^\n]", activityInp);

    if(duplicated(activities, activityInp, activityCount)){
        orderedTaskCount = filter_by_activity(orderedTasks, tasks, taskCount, activityInp);
        sort(orderedTasks, 0, orderedTaskCount - 1, start_sort);
        /* 
           This loop will look for tasks that begin at the same time, set the leftFlag to the first 
           one and the rightFlag to the last one, sort the array by the descriptions' alphabetical 
           order between the flags, and print it.
        */
        tmpStartTime = orderedTasks[0].start;
        for(i = 0; i < orderedTaskCount; ++i){
            if(orderedTasks[i].start != tmpStartTime){
                sort(orderedTasks, leftFlag, rightFlag, descriptions_sort);
                leftFlag = rightFlag + 1;
                tmpStartTime = orderedTasks[i].start;
            }
                rightFlag++;
        }
        sort(orderedTasks, leftFlag, rightFlag, descriptions_sort);
        print_task_array_d_command(orderedTasks, orderedTaskCount);

    } else 
        printf("no such activity\n");
}

int contains_only_capital_letters(char *str){
    /*Check if a string only contain capital characters
        Return TRUE if the string only contains capital letters, otherwise, return FALSE.

        Param str: string
        Return: integer
    */
    int i, strSize = strlen(str);

    for(i = 0; i < strSize; ++i){
        if(str[i] >= 'a' && str[i] <= 'z')
            return FALSE;
    }
    return TRUE;
}

void add_and_list_activities(){
    /* Add an activity or list all activities.
        If stdin contains no characters, print all activities in the order they were created, 
        otherwise add an activity to the global array char activities.

        Input format: <activity>
        Print format: <activity> or nothing if a activity was not created.

    */
    int i;
    char input[MAX_INPUT] = {'\0'}, activityInp[MAX_ACTIVITY_CHR] = {'\0'};

    fgets(input, MAX_INPUT, stdin);

    if(sscanf(input, " %20[^\n]", activityInp) != 1){
        for(i = 0; i < activityCount; ++i)
            printf("%s\n", activities[i]);
    } else {
        if(!duplicated(activities, activityInp, activityCount)){
            if(contains_only_capital_letters(activityInp)){
                if(activityCount < MAX_ACTIVITY){
                    memmove(activities[activityCount++], activityInp, MAX_ACTIVITY_CHR);
                } else
                    printf("too many activities\n");
            } else
                printf("invalid description\n");
        } else
            printf("duplicate activity\n");
    }
}

void move_task(){
    /* Move a task from an activity to another.
        Print the activity duration and time slack if the task has changed to the DONE activity and 
        if isn't already DONE.

        Input format: m <id> <user> <activity>
        Print format: duration=<spent> slack=<slack>
    */
    
    int idInp, duration;
    char input[MAX_INPUT] = {'\0'}, userInp[MAX_USERS_CHR] = {'\0'}, 
         activityInp[MAX_ACTIVITY_CHR] = {'\0'}; 
         

    fgets(input, MAX_INPUT, stdin);
    sscanf(input, "%d %s %20[^\n]", &idInp, userInp, activityInp);

    
    if(idInp > 0 && idInp <= taskCount){
        if(!(strcmp(tasks[idInp - 1].activity, "TO DO") && strcmp(activityInp, "TO DO") == 0)){
            if(duplicated(users, userInp, userCount)){
                if(duplicated(activities, activityInp, activityCount)){
                    strcpy(tasks[idInp - 1].user, userInp);
                    if(strcmp(tasks[idInp - 1].activity, activityInp)){
                        if(!strcmp(tasks[idInp - 1].activity, "TO DO"))
                            tasks[idInp - 1].start = timeCount;
                        if(!strcmp(activityInp, "DONE")){
                            if(!strcmp(tasks[idInp - 1].activity, "TO DO"))
                                duration = 0;
                            else
                                duration = timeCount - tasks[idInp - 1].start;
                            printf("duration=%d slack=%d\n", duration, (duration - tasks[idInp - 1].duration)); 
                        }  
                        strcpy(tasks[idInp - 1].activity,  activityInp);
                    }
                } else
                    printf("no such activity\n");
            } else 
                printf("no such user\n");
        } else
            printf("task already started\n");
    } else
        printf("no such task\n");
}

void add_and_list_users(){
    /* Add a user or list all users.
        If stdin is empty, print all users one by one in the order they were createdotherwise,
        add a user to the global array char users[MAX USERS][MAX USERS CHR].

        Input format: <user>
        Print format: <user> or nothing if a user was not created.
    */
    int i;
    char newUser[MAX_USERS_CHR] = {'\0'};
    char input[MAX_INPUT] = {'\0'};
    
    fgets(input, MAX_INPUT, stdin);

    if(sscanf(input, " %20[^\n]", newUser) != 1){
        for(i = 0; i < userCount; ++i)
            printf("%s\n", users[i]); 
    } else {
        if(!duplicated(users, newUser, userCount)){
            if(userCount < MAX_USERS){
                memmove(users[userCount++], newUser, MAX_USERS_CHR);
            } else 
                printf("too many users\n");
        } else 
            printf("user already exists\n");
    }
}

void advance_time(){
    /* Advances the system time.
        Reads an integer from stdin, advances the system time (global variable timeCount),
        and prints the result.

        Input format: <duration>
        Print format: <systemTime> »timeCount«
    */
    int duration = 0;
    char input[MAX_INPUT] = {'\0'};

    fgets(input, MAX_INPUT, stdin);
    sscanf(input, "%d", &duration);

    if(duration >= 0){
        timeCount += duration;
        printf("%d\n", timeCount);
    } else
        printf("invalid time\n");
}


void print_task_array_l_command(Task* array, int index){
    /* Cleans up the code. */
    printf("%d %s #%d %s\n", array[index].id, array[index].activity, array[index].duration, 
                             array[index].description);

}

void list_tasks(){
    /*list all tasks or specific tasks.  
        If there are no integers in stdin, print all tasks alphabetically, line by line.
        Otherwise, use the ids from stdin to print the tasks.
        
        Input format: [<id> <id> ...]
        Print format for each task: <id> <activity> #<duration> <description>
                    
    */
    int i, id;
    Task orderedTasks[MAX_TASK];
    char *flag, input[MAX_INPUT] = {'\0'};

    fgets(input, MAX_INPUT, stdin);

    if(sscanf(input, "%d", &id) == 1){
        flag = strtok(input, " ");
        while(flag != NULL){
            sscanf(flag, "%d", &id);
            if(id > 0 && id <= taskCount){
                print_task_array_l_command(tasks, id - 1);
            } else 
                printf("%d: no such task\n", id);
            flag = strtok(NULL, " ");
        }
    } else{
        copy_tasks(orderedTasks, tasks, taskCount);
        sort(orderedTasks, 0, taskCount - 1, descriptions_sort);
        for(i = 0; i < taskCount; ++i)
            print_task_array_l_command(orderedTasks, i);
    }
}

int duplicated_description_in_tasks(char* description){
    /*Check if exits a task with this description already

        Param description: string
        Return: integer
    */
    int i;
    for(i = 0; i < taskCount; ++i)
        if(!strcmp(tasks[i].description, description))
            return TRUE;
    return FALSE;
}

void add_task(){
    /*Adds a new task to the system: array Task tasks[MAX_TASK].
        Each task is added with:
            - id: type int (read from stdin)
            - description: type char 50 characters (read from stdin)
            - duration: type int > 0 (read from stdin)
            - the activity "TO DO"

        Input format: <duration> <description>
        Print format: <id> <activity> #<duration> <description>
    */
    int duration;
    char description[MAX_TASK_CHR] = {'\0'}, input[MAX_INPUT] = {'\0'};

    fgets(input, MAX_INPUT, stdin);
    sscanf(input, "%d %50[^\n]", &duration, description);
    
    if(taskCount < MAX_TASK){
        if(!duplicated_description_in_tasks(description)){
            if(duration > 0){

                tasks[taskCount].id = taskCount + 1;
                memmove(tasks[taskCount].description, description, MAX_TASK_CHR);
                tasks[taskCount].duration = duration;
                memmove(tasks[taskCount].activity, "TO DO", 5);
                
                printf("task %d\n", tasks[taskCount++].id);
            } else
                printf("invalid duration\n");
        } else
            printf("duplicate description\n");
    } else
        printf("too many tasks\n");
}

int main(){
    char command;

    while((command = getchar()) != 'q' ){
        switch(command){
            case 't':
                add_task();
                break;

            case 'l':
                list_tasks();
                break;

            case 'n':
                advance_time();
                break;

            case 'u':
                add_and_list_users();
                break;

            case 'm':
                move_task();
                break;

            case 'd':
                list_tasks_in_activity();
                break;

            case 'a':
                add_and_list_activities();
                break;
        }
    }
    return 0;
}