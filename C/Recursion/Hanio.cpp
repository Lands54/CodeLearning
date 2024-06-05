#include<bits/stdc++.h>
using namespace std;
void Hanio(int num,char A,char B,char C);
int main(){
    int n;
    cout<<"Please enter number"<<endl;
    cin>>n;
    Hanio(n,'A','B','C');
}

void Hanio(int num,char A,char B,char C){
    if(num<=0)
        return;
    Hanio(num-1,A,C,B);
    cout<<A<<"->"<<C<<endl;
    Hanio(num-1,B,A,C);
}
