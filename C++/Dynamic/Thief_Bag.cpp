#include<bits/stdc++.h>
using namespace std;
map<int,int>List;
int Stole(int Space,int *a,int num);
void Initilize(int &num,int *a,int *value);

int main(){
    int num,ObSpace[100],ObValue[100],Space;
    cout<<"Please enter number of Object(Only Support the speace more huge,the object more valuable)"<<endl;
    cout<<"Enter 0 into default test set"<<endl;
    cin>>num;
    cout<<"Please enter Space and Value"<<endl;
    for(int i=0;i<num;i++){
        cout<<i+1<<":";
        cin>>ObSpace[i];
        cin>>ObValue[i];
        cout<<endl;
    }
    Initilize(num,ObSpace,ObValue);
    while(true){
        cout<<"Please enter Space"<<endl;
        cin>>Space;
        cout<<Stole(Space,ObSpace,num)<<endl;
        cout<<"____________________"<<endl;
    }

}

int Stole(int Space,int a[],int num){
    int clist[num],max=-1,maxi;
    if(List.count(Space)==1)
        return List[Space];
    for(int i=0;i<num;i++){
        if(Space-a[i]>=0 && max<List[a[i]]+Stole(Space-a[i],a,num)){
            max = List[a[i]]+Stole(Space-a[i],a,num);
        }
    }
    List[Space] = max;
    return List[Space];
}

void Initilize(int &num,int *a,int *value){
    List[0]=0;
    if(num==0){
        List[1]=1;
        List[2]=3;
        List[5]=9;
        a[0]=1;
        a[1]=2;
        a[2]=5;
        num=3;
        return;
    }
    for(int i=0;i<num;i++)
        List[a[i]]=value[i];
}
