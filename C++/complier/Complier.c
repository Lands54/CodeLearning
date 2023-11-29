#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<ctype.h>
#include<stdbool.h>

struct Token_list{
    struct Token* Tokl;
    int length;
};

struct Token{
    char* type;
    char* value;

};

struct Expression{
    struct Expression* parent;
    struct Expression** children;
    char* value;
    char* token;
    int num_of_children;
};

char* read_file(char path[]);
struct Token_list* Init();
void Token_append(struct Token_list* tokn_list, char* value, char* type);
void Code_to_Token_list(struct Token_list* tokn_lst, char* code);
void prtokrn(struct Token_list* tk_lst);
int isAllNum(char* str);
char* Token_detect(char* value);
void E(struct Expression* expr, struct Token* tokn, int *pos);
void Ex(struct Expression* expr, struct Token* tokn, int *pos);
void T(struct Expression* expr, struct Token* tokn, int *pos);
void Tx(struct Expression* expr, struct Token* tokn, int *pos);
void F(struct Expression* expr, struct Token* tokn, int *pos);
void Assignment(struct Expression* expr, struct Token* tokn, int *pos);
void ID(struct Expression* expr, struct Token* tokn, int *pos);
void Num(struct Expression* expr, struct Token* tokn, int *pos);
int Tok_match(struct Token* tokn, char* purpose, int *pos);
int SYM_match(struct Token* tokn, char* purpose, int *pos);
void SYM(struct Expression* expr, struct Token* tokn, char* purpose, int *pos);
struct Expression* E_Node_append(struct Expression* expr, char* token, char* value);
int main(){
    char path[] = "text.c";
    struct Token_list* tokn_lst = (struct Token_list*)malloc(sizeof(struct Token_list));
    struct Expression* expr = (struct Expression*)malloc(sizeof(struct Expression));
    char* code = read_file(path);
    int pos = 0;
    Code_to_Token_list(tokn_lst, code);
    prtokrn(tokn_lst);
    Assignment(expr, tokn_lst->Tokl, &pos);
    Assignment(expr, tokn_lst->Tokl, &pos);
    Assignment(expr, tokn_lst->Tokl, &pos);
    Assignment(expr, tokn_lst->Tokl, &pos);
    printf("");
}

void Token_append(struct Token_list* tokn_list, char* value_, char* type_){
    if(value_[0]=='\0'||value_[0]==' ')return;
    tokn_list->length++;
    tokn_list->Tokl = (struct Token*)realloc(tokn_list->Tokl, tokn_list->length*sizeof(struct Token));
    ((tokn_list->Tokl)+(tokn_list->length)-1)->value = value_;
    ((tokn_list->Tokl)+(tokn_list->length)-1)->type = Token_detect(value_);
    return;
}

char* read_file(char path[]){
    FILE* code_file;
    code_file = fopen(path, "r");
    fseek(code_file, 0, SEEK_END);
    long file_size = ftell(code_file);
    fseek(code_file, 0, SEEK_SET);
    char *code_str = (char*)malloc(file_size + 1);
    fread(code_str, sizeof(char), file_size, code_file);
    code_str[file_size+1] = '\0';
    fclose(code_file);
    return code_str;
}

void Code_to_Token_list(struct Token_list* tokn_lst, char* code){
    char* word = (char*)calloc(20, sizeof(char));
    for(int i=0;code[i]!='\0';i++){
        if(code[i]==' '||code[i]=='+'||code[i]=='-'||code[i]=='*'||code[i]=='/'||code[i]=='('||code[i]==')'||code[i]=='\n'||code[i]=='='||code[i]=='\0'){
                Token_append(tokn_lst, word, word);
                word = (char*)calloc(2, sizeof(char));
                strncat(word, code + i, 1);
                if(code[i+1])
                    Token_append(tokn_lst, word, word);
                    word = (char*)calloc(20, sizeof(char));
        }
        else{
            strncat(word, code + i, 1);
        }  
    }
    free(code);
}

int isAllNum(char* str){
    while(*str!='\0') {
        if (!isdigit(*str)) {
            return 0;
        }
        str++;
    }
    return 1;
}

void prtokrn(struct Token_list* tk_lst){
    for(int i = 0;i<tk_lst->length;i++){
        printf(":%s ", ((tk_lst->Tokl)+i)->type);
        printf(":%s\n", ((tk_lst->Tokl)+i)->value);
    }
}

char* Token_detect(char* value){
    if(value[0]=='\n')
        return "END";
    if(value[0]=='+'||value[0]=='-'||value[0]=='*'||value[0]=='/'||value[0]=='='||value[0]=='('||value[0]==')')
        return "SYM";
    if(isAllNum(value)){
        return "Num";
    }
    else{
        return "ID";
    }
}

struct Expression* E_Node_append(struct Expression* expr, char* token, char* value){
    expr->num_of_children++;
    expr->children  = (struct Expression**)realloc(expr->children, expr->num_of_children * sizeof(struct Expression*));
    expr->children[expr->num_of_children - 1] = (struct Expression*)malloc(sizeof(struct Expression));
    expr->children[expr->num_of_children - 1]->parent = expr;
    expr->children[expr->num_of_children - 1]->token = token;
    expr->children[expr->num_of_children - 1]->value = value;
    return expr->children[expr->num_of_children - 1];
}

void E(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "E", "nonterminal");
    T(expr, tokn, pos);
    Ex(expr, tokn, pos);
}

void Ex(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "Ex", "nonterminal");
    if(SYM_match(tokn, "+", pos)){
        SYM(expr, tokn, "+", pos);
        T(expr, tokn, pos);
        Ex(expr, tokn, pos);
    }
    else if (SYM_match(tokn, "-", pos)){
        SYM(expr, tokn, "-",pos);
        T(expr, tokn, pos);
        Ex(expr, tokn, pos);
    }
    else{
        return;
    }
}

void T(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "T", "nonterminal");
    F(expr, tokn, pos);
    Tx(expr, tokn, pos);
}

void Tx(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "Tx", "nonterminal");
    if(SYM_match(tokn, "*", pos)){
        SYM(expr, tokn, "*", pos);
        F(expr, tokn, pos);
        Tx(expr, tokn, pos);
    }
    else if (SYM_match(tokn, "/", pos)){
        SYM(expr, tokn, "/", pos);
        F(expr, tokn, pos);
        Tx(expr, tokn, pos);
    }
    else{
        return;
    }
}

void F(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "F", "nonterminal");
    if(SYM_match(tokn, "(", pos)){
        SYM(expr, tokn, "(", pos);
        E(expr, tokn, pos);
        SYM(expr, tokn, ")", pos);
    }
    else if(Tok_match(tokn, "ID", pos)){
        ID(expr, tokn, pos);
    }
    else if(Tok_match(tokn, "Num", pos)){
        Num(expr, tokn, pos);
    }
    else{
        printf("error in %p",tokn + *pos);
        exit(1);
    }
    
}

void Assignment(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "Assignment", "nonterminal");
    ID(expr, tokn, pos);
    SYM(expr, tokn, "=", pos);
    E(expr, tokn, pos);
    (*pos)++;
}

void ID(struct Expression* expr, struct Token* tokn, int *pos){
    if(Tok_match(tokn, "ID", pos)){
        struct Token* current_token = tokn + *pos;
        expr = E_Node_append(expr, current_token->value, "ID");
        (*pos)++;
    }
    else{
        printf("error in %p",tokn + *pos);
        exit(1);
    }
    return;
}

void Num(struct Expression* expr, struct Token* tokn, int *pos){
    if(Tok_match(tokn, "Num", pos)){
        struct Token* current_token = tokn + *pos;
        expr = E_Node_append(expr, current_token->value, "Num");
        (*pos)++;
    }
    else{
        printf("error in %p",tokn + *pos);
        exit(1);
    }
    return;
}

int SYM_match(struct Token* tokn, char* purpose, int *pos){
    struct Token* current_token = tokn + *pos;
    if(strcmp(current_token->value, purpose)==0)return 1;
    else return 0;
}

int Tok_match(struct Token* tokn, char* purpose, int *pos){
    struct Token* current_token = tokn + *pos;
    if(strcmp(current_token->type, purpose)==0)return 1;
    else return 0;
}

void SYM(struct Expression* expr, struct Token* tokn, char* purpose, int *pos){
    if(Tok_match(tokn, "SYM", pos) && SYM_match(tokn, purpose, pos)){
        struct Token* current_token = tokn + *pos;
        expr = E_Node_append(expr, current_token->value, "SYM");
        (*pos)++;
    }
    else{
        printf("error in %p",tokn + *pos);
        exit(1);
    }
}