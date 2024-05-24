#include<stdio.h>
#include<stdlib.h>
#include<string.h>
char* Fun();
int main(int argc, char const *argv[])
{
    char* *x()=Fun;
    printf("%s", x());
    printf("%s", x);
    printf("%s", x);
    return 0;
}
char* Fun(){
    char *c = malloc(100);
    strcat(c,"hello!");
    return c;
}