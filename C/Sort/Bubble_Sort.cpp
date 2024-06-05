#include<bits/stdc++.h>
using namespace std;
void BubbleSort(int *a);
void swap(int &a,int &b);
void outarr(int *a);
int lenarr(int*a);

int main (){
    int n;
    cout<<"Please enter amount";
    cin>>n;
    int a[n];
    for(int i=0;i<n;i++)
        cin>>a[i];
    BubbleSort(a);
    outarr(a);
    return 0;
}

void BubbleSort(int *a){
    int len=lenarr(a);
    int t;
    int i,j=0;
    for(i=0;i<len;i++){
        for(j=0;j<len-i-1;j++){
            if(a[j]>a[j+1])swap(a[j],a[j+1]);
        }
    }
}

void swap(int &a,int &b){
    int tmp;
    tmp = a;
    a = b;
    b = tmp;
}

void outarr(int *a){
    for(int i=0;a[i]!='\0';i++)
    {
        cout<<a[i]<<',';
    }
}

int lenarr(int *a){
    int sum=0;
    for(int i=0;a[i]!='\0';i++)sum++;
        return sum;
}
