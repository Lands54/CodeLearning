#include "C8051F020.h"
#include "absacc.h"
#include "data_define.c"
#define   AD      XBYTE[0x2000]
#define   DA      XBYTE[0x4000]
#define   LED_1    XBYTE[0x0000]
#define   LED_2    XBYTE[0x0001]
#define   LED_3    XBYTE[0x0002]
#define   LED_4    XBYTE[0x0003]
#define   TIMER   0x8000																//10
#include "Init_Device.c"
void ReadKey(unsigned char* Now_Key_Reg, unsigned char *mode);
void KeyDecoder(unsigned char *Now_Key_Reg, unsigned char *Now_Data_Reg);
unsigned char Key_board_Action(unsigned char *Now_Key_Reg, unsigned char *Now_Data_Reg, unsigned char *mode);
void Read_Value(unsigned char Value, unsigned char *Is_changable);
void Rewrite(unsigned char Value, unsigned char *position, unsigned char *LED_Reg, unsigned char *Is_changable);
void Display(unsigned char* LED_Reg, unsigned char *Now_Key_Reg, unsigned char Now_Data_Reg);
void Moniter(unsigned char Value, unsigned char *Is_changable, unsigned char *position, unsigned char *LED_Reg, unsigned char *Now_Key_Reg);
void Read_AD(unsigned char *AD_Value);
void AD_Show(unsigned char *AD_Display, unsigned char AD_Value, unsigned char* LED_Reg);
void Write_DA(unsigned char DA_Value);
void Process(unsigned char * LED_Reg, unsigned char *AD_Value, unsigned char *DA_Value);
void CPU(unsigned char *AD_Value, unsigned char *DA_Value, unsigned char *LED_Reg, unsigned char* AD_Display);
unsigned char decoder(unsigned char number);
void delay(void);
void main(void){
	//Key_Board
	unsigned char Now_Key_Reg[2];
	unsigned char Now_Data_Reg = 0xff;
	unsigned char mode = 0x00;
	unsigned char Value = 0;
	//Displayer
	unsigned char LED_Reg[4];
	unsigned char position = 0;
	unsigned char Is_changable = 0;														//20
	//CPU
	unsigned char AD_Value;
	unsigned char DA_Value;
	unsigned char AD_Display[2];
	//main
	Init_Device();
	LED_1=LED_2=LED_3=LED_4=0xff;
	while(1){
		Value = Key_board_Action(Now_Key_Reg, &Now_Data_Reg, &mode);
		Moniter(Value, &Is_changable, &position, LED_Reg, Now_Key_Reg);
		CPU(&AD_Value, &DA_Value, LED_Reg, AD_Display);
	}
}
	//Key_Board
	void ReadKey(unsigned char* Now_Key_Reg, unsigned char *mode){
		unsigned char message = 0xff;
		unsigned char row;
		unsigned char val;
		unsigned char temp;
		for(row=4; row<8; row++){    	
			if(message = (~XBYTE[row] &0x1f)){		
				for(val=0,temp=1; val<5;++val, temp=temp<<1){
			    	if((message&temp)!=0){
						Now_Key_Reg[0] = row-3;
						Now_Key_Reg[1] = val+1;
						*mode = 2;
			       	}
				}
			}
		}																				//40
	}
	
	void KeyDecoder(unsigned char *Now_Key_Reg, unsigned char *Now_Data_Reg){
		unsigned char table[] = {0xFD,
								0xC0,0xF9,0xA4,0xB0,0xFF,
								0x99,0x92,0x82,0xF8,0xFF,
								0x80,0x90,0xFF,0xFF,0xFF,
								0xFF,0xFF,0xFF,0xFF,0xED,
								0xFF,0xFF,0xFF,0xFF,0xFF,};
		int temp = 5 * (Now_Key_Reg[0] - 1) + Now_Key_Reg[1];
		if(temp>0 && temp<21)
			*Now_Data_Reg = table[temp];
		//else 
			//*Now_Data_Reg = 0xCD;
	}

	unsigned char Key_board_Action(unsigned char *Now_Key_Reg, unsigned char *Now_Data_Reg, unsigned char *mode){
		ReadKey(Now_Key_Reg, mode);
		KeyDecoder(Now_Key_Reg, Now_Data_Reg);
		if(*mode == 1){
			*mode = 0;
			return *Now_Data_Reg;	
		}
		if(*mode == 2){
			*mode = 1;
			return 0;
		}
		else{
			*mode = 0;
			return 0;
		}
	}
	//Displayer
	void Read_Value(unsigned char Value, unsigned char *Is_changable){
		if(Value == 0xED){
			*Is_changable = 1;
		}
	}

	void Rewrite(unsigned char Value, unsigned char *position, unsigned char *LED_Reg, unsigned char *Is_changable){
		if(*Is_changable && Value !=0 && Value != 0xED){
			LED_Reg[*position] = Value;
			*position = *position + 1;
			if(*position >= 2){
				*position = 0;
				*Is_changable = 0;
			}
		}
	}
	
	void Display(unsigned char* LED_Reg, unsigned char* Now_Key_Reg, unsigned char Now_Data_Reg){
		LED_1 = LED_Reg[0];
		LED_2 = LED_Reg[1];
		LED_3 = LED_Reg[2];
		LED_4 = LED_Reg[3];
	}
	void Moniter(unsigned char Value, unsigned char *Is_changable, unsigned char *position, unsigned char *LED_Reg,unsigned char *Now_Key_Reg){
		Read_Value(Value, Is_changable);
		Rewrite(Value, position, LED_Reg, Is_changable);
		Display(LED_Reg, Now_Key_Reg);
	}

//CPU
	void Read_AD(unsigned char *AD_Value){
		AD = 1;
		*AD_Value = AD;	
	}

	void AD_Show(unsigned char *AD_Display, unsigned char AD_Value, unsigned char* LED_Reg){
		unsigned char number = 100 * AD_Value / 255;
		unsigned char num[]={0xC0,0xF9,0xA4,0xB0,0x99,0x92,0x82,0xF8,0x80,0x90,0x7f};
		LED_Reg[2] = num[(int)(number/10)];
		LED_Reg[3] = num[(int)(number%10)];

	}
	unsigned char decoder(unsigned char number){
		switch(number){
		case 0xC0: return 0;
		case 0xF9: return 1;
		case 0xA4: return 2;
		case 0xB0: return 3;
		case 0x99: return 4;
		case 0x92: return 5;
		case 0x82: return 6;
		case 0xF8: return 7;
		case 0x80: return 8;
		case 0x90: return 9;
		case 0x7f: return 0x0A;
		default:   return 5;
		}
	}
	void Write_DA(unsigned char DA_Value){
		DA = DA_Value;
	}
	
	void Process(unsigned char *LED_Reg, unsigned char AD_Value, unsigned char *DA_Value){
			unsigned char k = 100;
			unsigned char t = 255 * decoder(LED_Reg[0]) * 10 + decoder(LED_Reg[1]) / 100;
			if(t > AD_Value)*DA_Value = min(k * (t - AD_Value), 127) + 127;
			else *DA_Value = 127 - min((k * AD_Value - t), 127);
			// function min is not in code
	}
	void CPU(unsigned char *AD_Value, unsigned char *DA_Value, unsigned char *LED_Reg, unsigned char* AD_Display){
		Read_AD(AD_Value);
		AD_Show(AD_Display, *AD_Value, LED_Reg);
		Process(LED_Reg, *AD_Value, DA_Value);
		Write_DA(*DA_Value);
	}

	void delay(void){ 
    	uint i;
    	for(i=0;i<TIMER;++i)  i=i;
	}
