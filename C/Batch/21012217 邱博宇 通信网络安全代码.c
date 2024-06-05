#include <stdio.h>
#include <stdlib.h>
#include <Windows.h>

void Tips()
{
	printf("欢迎使用\n输入如下:\n1:结束进程 Kill task\n");
	printf("2:清理垃圾 clean trash\n3:Ban网站 Ban website\n4 关机 Shutdown\n5:添加新用户 Add user\n");
	printf("6:IP-MAC绑定 IP-MAC-Binding\n7:展示网络状态并输出 Show newwork\n8:定时器 Timer\n9:清除DNS缓存 Clean DNS\n");
	printf("10:展示进程列表并输出 Show tasks\n远程使用在命令前加入y add y in front of command to remote\n");
	printf(":\nk:取消关机 Stop Shutdown\nq:退出 Quit\nt:重新展示 Re Show\n");
	printf("ouo!\n");
}

int main()
{
	char code_num[5];//order code
	int pidi=0;//kill process
	FILE *file;//website
	int Which;
	char ip[100];
	char website[100];
	//char web[350];
	char newUser[100];//add user
	char newPassword[100];
	//char UserAdd[300];
	char IP_address[100];//IP-MAC binding
	char MAC_address[100];
	char Program_path[150];
	char Time[10];
	char Name[100];
	char Run[350];
	char Yuanc[20]; 
	printf("Ready to use!");
	Tips();
	while(1)
	{
		scanf("%s",&code_num);
		switch(code_num[0])
		{
			case '1'://kill process of show tasklist
				switch(code_num[1])
				{
					case '0':
						system("tasklist /V > %USERPROFILE%\\Desktop\\process_status.txt");
						system("tasklist /V ?FO CSV > %USERPROFILE%\\Desktop\\process_status.csv");
						printf("File is already output to your desktop"); 
						break;
					case '\0':
						printf("input pid you want to kill");
						scanf("%d",&pidi);
						sprintf(Run,"taskkill /PID %d",pidi);
						system(Run);
						break;
				}
				break;
			case '2'://clear trash
				system("cleanmgr");
				break;
			case '3'://website resign or ban
				file = fopen("C:\\Windows\\System32\\drivers\\etc\\hosts","a");
				if(file == NULL){
					perror("Error opening file");
					return 1;	
				}
				printf("input: 1 to resign,2 to ban website");
				scanf("%d",&Which);
				if(Which == 1){
					printf("which ip you want to resign");
					scanf("%s",&ip);
					printf("resign to which website");
					scanf("%s",&website);
					sprintf(Run,"# Redirect %s to %s\n%s    %s\n",ip,website,ip,website);
					fprintf(file,Run);
				}else if(Which == 2){
					printf("which website you want to ban");
					scanf("%s",&website);
					sprintf(Run,"#Block %s\n127.0.0.1    %s\n",website,website);
					fprintf(file,Run);
				}
				fclose(file);
				system("notepad C:\\Windows\\System32\\drivers\\etc\\hosts"); 
				break;
			case '4'://shutdown
				system("shutdown -s -f -t 10");
				break;
			case '5'://add new user
				printf("input new user name");
				scanf("%s",&newUser);
				printf("input new user password");
				scanf("%s",&newPassword);
				sprintf(Run,"net user %s %s /add",newUser,newPassword);
				system(Run);
				break;
			case '6'://IP-MAC binding
				printf("input IP address");
				scanf("%s",&IP_address);
				printf("input MAC address");
				scanf("%s",&MAC_address);
				sprintf(Run,"arp -s %s %s",IP_address,MAC_address);
				system(Run);
				break;
			case '7'://show net state and output as .txt and .csv
				system("ipconfig /all");
				system("ipconfig /all > %USERPROFILE%\\Desktop\\network_info.txt");
				system("ipconfig /all | findstr /V /C:" " > %USERPROFILE%\\Desktop\\network_info.csv");
				printf("File is already output to your desktop");
				break;
			case '8'://set time to run a program
				printf("input path of the program");
				scanf("%s",&Program_path);
				printf("input time");
				scanf("%s",&Time);
				printf("input task name");
				scanf("%s",&Name);
				sprintf(Run,"schtasks /create /tn %s /tr '%s' /sc once /st %s",Name,Program_path,Time);
				system(Run);
				break;
			case '9'://flushDNS
				system("ipconfig /flushdns");
				break;
			case 'k'://stop shutdown and keep computer on
				system("shutdown -a");
				break;
			case 'q'://quit
				goto out;
				break;
			case 'y'://far
				switch(code_num[1])
				{
					case '1':
						switch(code_num[2])
						{
							case '0':
								printf("input your target ip");
								scanf("%s",&Yuanc);
								sprintf(Run,"psexec \\\\%s tasklist /V > %USERPROFILE%\\Desktop\\process_status.txt",Yuanc);
								system(Run);
								sprintf(Run,"psexec \\\%s tasklist /V ?FO CSV > %USERPROFILE%\\Desktop\\process_status.csv",Yuanc);
								system(Run);
								printf("File is already output to your desktop"); 
								break;
							case '\0':
								printf("input pid you want to kill");
								scanf("%d",&pidi);
								printf("input your target ip");
								scanf("%s",&Yuanc);
								sprintf(Run,"taskkill /s %s /pid %d",Yuanc,pidi);
								system(Run);
								break;
						}
						break;
					case '2':
						printf("input your target ip");
						scanf("%s",&Yuanc);
						sprintf(Run,"psexec \\\\%s cleanmgr /sageset:1 & cleanmgr /sagerun:1",Yuanc);
						system(Run);
						break;
					case '3':
						printf("need to develop,please try self-computer IP-MAC binding");
						break;
					case '4':
						printf("input your target ip");
						scanf("%s",&Yuanc);
						sprintf(Run,"shutdown /s /m \\\\%s /t 0",Yuanc);
						system(Run);
						break;
					case '5':
						printf("input your target ip");
						scanf("%s",&Yuanc);
						printf("input new user name");
						scanf("%s",&newUser);
						printf("input new user password");
						scanf("%s",&newPassword);
						sprintf(Run,"psexec \\\\%s net user %s %s /add",Yuanc,newUser,newPassword);
						system(Run);
						break;
					case '6':
						printf("input IP address");
						scanf("%s",&IP_address);
						printf("input MAC address");
						scanf("%s",&MAC_address);
						sprintf(Run,"arp -s %s %s",IP_address,MAC_address);
						system(Run);
						break;
					case '7':
						printf("input your target ip");
						scanf("%s",&Yuanc);
						sprintf(Run,"psexec \\\\%s ipconfig /all",Yuanc);
						system(Run);
						sprintf(Run,"psexec \\\\%s ipconfig /all > %USERPROFILE%\\Desktop\\ipconfig_output.txt",Yuanc);
						system(Run);
						sprintf(Run,"psexec \\\\%s ipconfig /all | findstr /V /C:" " > %USERPROFILE%\\Desktop\\network_info.csv",Yuanc);
						system(Run);
						printf("File is already output to desktop");
						break;
					case '8':
						printf("input your target ip");
						scanf("%s",&Yuanc);
						printf("input path of the program");
						scanf("%s",&Program_path);
						printf("input time");
						scanf("%s",&Time);
						printf("input task name");
						scanf("%s",&Name);
						sprintf(Run,"psexec \\\\%s schtasks /create /tn %s /tr %s /sc one time /st %s",Yuanc,Name,Program_path,Time);
						system(Run);
						break;
					case '9':
						printf("input your target ip");
						scanf("%s",&Yuanc);
						sprintf(Run,"psexec \\\\%s ipconfig /flushdns",Yuanc);
						system(Run);
						break;
				}
				break;
			case 't'://tips
				Tips();
				break;
		}
	}
	out:
		printf("program over");
	return 0;
} 
