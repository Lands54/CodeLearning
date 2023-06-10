#include<bits/stdc++.h>
using namespace std;
void outarr(int *a);
void QuickSort(int a[], int l, int r);
void swap(int &a,int &b);

int main(){
    int a[10]={3,6,1,2,7,3,5,2,6,4};
    QuickSort(a,0,10);
    outarr(a);
}

void QuickSort(int a[], int l, int r){
    if (l < r){
        int i,j,x;
        i = l;
        j = r;
        x = a[i];
        while (i < j){
            while(i < j && a[j] > x)
                j--; 
            if(i < j){
                a[i] = a[j];
                i++;
            }
            while(i < j && a[i] < x)
                i++; 
            if(i < j){
                a[j] = a[i];
                j--;
            }
        }
        a[i] = x;
        QuickSort(a, l, i-1); 
        QuickSort(a, i+1, r); 
    }
}

void swap(int &a,int &b){
    int tmp;
    tmp = a;
    a = b;
    b = tmp;
}

void outarr(int *a){
    for(int i=0;i<10;i++){
        cout<<a[i]<<',';
    }
}
