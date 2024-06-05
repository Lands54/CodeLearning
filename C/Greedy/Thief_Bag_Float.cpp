#include<bits/stdc++.h>
using namespace std;
map<float,float>List;
void Initilize(float &num,float *a,float *value);
float Greed_Stole(float Space,float Object);

int main(){
    float num,ObSpace[100],ObValue[100],Space;
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
        cout<<Greed_Stole(Space,25)<<endl;
        cout<<"____________________"<<endl;
}
}

float Greed_Stole(float Space,float Object){
    int i=0;
    int v=0;
    while(List.count(Object-i)!=1||Object-i==0){
        i++;
        if(Object-i<=0)
            return 0;
    }
    Object=Object-i;
    while(Space>=Object){
        Space = Space - Object;
        v++;
    }
    return v*List[Object]+Greed_Stole(Space,Object-1);
}

void Initilize(float &num,float *a,float *value){
    List[0]=0;
    if(num==0){
        List[1/7]=1/2;
        List[2/3]=5/3;
        List[5/4]=13/4;
        a[0]=1;
        a[1]=2;
        a[2]=5;
        num=3;
        return;
    }
    for(int i=0;i<num;i++)
        List[a[i]]=value[i];
}
