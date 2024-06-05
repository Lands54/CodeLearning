#include<stdio.h>
#include<ctime>
#include<stdlib.h>
void mergesort(int num[],int left,int right);

int main(){
    srand(time(NULL));
    int i,a[100];
    for(i=0;i<100;i++){
        a[i]=rand()%100;
        printf("%d ",a[i]);
    }
    printf("\n");
    mergesort(a,0,99);
    for(int i=0;i<100;i++){
        printf("%d ",a[i]);
    }
}

void mergesort(int num[],int left,int right){
    int temp[right-left+1]; 
    if(left==right) //当数组只有 1 个数值的时候停止
        return;
    int middle =(left+right)/2;
    mergesort(num,left,middle); //递归，将数组不断 2 分求解
    mergesort(num,middle+1,right);
    int i=left,j=middle+1,n=0;
    while(i<=middle&&j<=right){ //比较（L->M）与（M->R），由于（每个单独数组，前面一定小于后面，所以从从左向右比较），将其排序输入暂时数组 temp 
        if(num[j]>num[i]){
            temp[n]=num[i];
            n++;
            i++;
        }
        else{
            temp[n]=num[j];
            n++;
            j++;
        }
    }
    while(i<=middle){ //当一个 MIDDLE 一边的数组处理完后，将剩下的元素全部按顺序导入temp 
        temp[n]=num[i];
        n++;
        i++;
    }
    while(j<=right){
        temp[n]=num[j];
        n++;
        j++;
    }
    for(i = 0;i<n;i++) //将 temp 处理完后的结果导入 num 
        num[left+i]=temp[i];
}
