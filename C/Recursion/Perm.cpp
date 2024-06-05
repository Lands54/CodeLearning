#include<bits/stdc++.h>
using namespace std;
void perm(char *arr, int begin);
void swap(char &a,char &b);
int lenarr(char *a);

int main(){
    char list[100];
    cout<<"Please enter string"<<endl;
    cin>>list;
    perm(list,0);
}

void perm(char *arr, int begin)
{
    int end=lenarr(arr)-1;
    if (begin == end){
        cout<<arr<<endl;
        return;
    }
    for (int i = begin; i <= end; i++){
        swap(arr[begin], arr[i]); 
        perm(arr, begin + 1); 
        swap(arr[begin], arr[i]); 
    }
}

void swap(char &a,char &b){
    char tmp;
    tmp = a;
    a = b;
    b = tmp;
}

int lenarr(char *a){
    int sum=0;
    for(int i=0;a[i]!=0;i++)sum++;
        return sum;
}
