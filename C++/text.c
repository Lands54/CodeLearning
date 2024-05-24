#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>
#include<unistd.h>
#include<stdio.h>
int main(){
    int n;
    char buf[8192];
    while ((n = read(STDIN_FILENO, buf, 8192))>0)
    {
        if(write(STDOUT_FILENO, buf, n) != n){
            printf("e\n");
        }
        if(n<0){
            printf("e\n");
        }
    }
    
}