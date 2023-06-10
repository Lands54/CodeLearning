#include<bits/stdc++.h>
using namespace std;
map<int,int>List;
int Stole(int money,int *a,int num);
void Initilize(int &num,int *a);

int main(){
    int num,coin[100],money;
    cout<<"Please enter number of Coins"<<endl;
    cin>>num;
    cout<<"Please enter Stole"<<endl;
    for(int i=0;i<num;i++)
        cin>>coin[i];
    Initilize(num,coin);
    while(true){
        cout<<"Please enter money"<<endl;
        cin>>money;
        cout<<Stole(money,coin,num)<<endl;
        cout<<"____________________"<<endl;
    }
}

int Stole(int money,int a[],int num){
    int clist[num],min=money+10,stl;
    if(List.count(money)==1)
        return List[money];
    for(int i=0;i<num;i++){ 
        if(money-a[i]>=0){
            stl=Stole(money-a[i],a,num);
            if(min>stl){
                min = stl;
            }
        }
    }
    return min+1;
}
void Initilize(int &num,int *a){
    List[0]=0;
    if(num==0){
        List[1]=1;
        List[2]=1;
        List[5]=1;
        List[10]=1;
        a[0]=1;
        a[1]=2;
        a[2]=5;
        a[3]=10;
        num=4;
        return;
    }
    for(int i=0;i<num;i++)
        List[i]=1;
}
