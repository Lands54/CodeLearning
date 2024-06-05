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

struct Memory{
    char** key;
    int* value;
    int length;
};
char* read_file(char path[]);
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
void epcilon(struct Expression* expr);
void print_expr(struct Expression* expr);
void Link(struct Expression* expr_1, struct Expression* expr_2);
struct Expression* disLink(struct Expression* expr_1, int index);
void replant(struct Expression* expr_1, int index);
struct Expression* skip_1_Node(struct Expression* expr);
void zip_tree(struct Expression* expr);
struct Expression* Program_Expression_Init(char * prgrm);
void SYM_Rebuild_Tree(struct Expression* expr);
void Program_rebuild_tree(struct Expression* prgrm);
int Is_simply(struct Expression* expr);
void simplify_AST(struct Expression* prgrm);
void print_AST(struct Expression* expr, int size);
int parent_deep(struct Expression* expr);
void remove_scope_epc(struct Expression* prgrm);
void swap_expr(struct Expression* expr);
void bubble(struct Expression* expr);
int Num_exec(struct Memory* verb_tbl, struct Expression* prgrm);
int* Adr_exec(struct Memory* verb_tbl, struct Expression* prgrm);
int verb_find(struct Memory* verb_lst, char* verb_name);
int* verb_create(struct Memory* verb_lst, char* verb_name);
struct Memory* exec(struct Expression* prgrm);
void Show_Memory(struct Memory* verb_tbl);
int main(int argc, char* argv[]){
    char* path;
    char* code;
    int tok_pos = 0;
    struct Token_list* tokn_lst = (struct Token_list*)malloc(sizeof(struct Token_list));
    struct Expression* prgrm = Program_Expression_Init("Program");
    if(argc<2){
        printf(":no input files run test.q\n");
        path = "test.q";
    }
    else{
        path = argv[1];
    }
    code = read_file(path);
    Code_to_Token_list(tokn_lst, code);
    while(tokn_lst->length > tok_pos){
       Assignment(prgrm, tokn_lst->Tokl, &tok_pos);
    }
    simplify_AST(prgrm);
    // prtokrn(tokn_lst);
    // print_AST(prgrm, 10 + tok_pos);
    Show_Memory(exec(prgrm));
}
void Show_Memory(struct Memory* verb_tbl){
    for(int i = 0;i<verb_tbl->length;i++){
        printf("\nname:%.5s|value:%d", verb_tbl->key[i], verb_tbl->value[i]);
    }
    printf("\n\n");
}
struct Memory* exec(struct Expression* prgrm){
    struct Memory* verb_tbl = (struct Memory*)malloc(sizeof(struct Memory));
    Num_exec(verb_tbl, prgrm);
    return verb_tbl;
}
int Num_exec(struct Memory* verb_tbl, struct Expression* prgrm){
    char* tokn = prgrm->token;
    char* vale = prgrm->value;
    if(!strcmp(tokn, "ID")){
        return verb_find(verb_tbl, vale);
    }
    else if(!strcmp(tokn, "Num")){
        return atoi(vale);
    }
    else if(!strcmp(tokn, "SYM")){
        if(!strcmp(vale, "=")){
            int num = Num_exec(verb_tbl, prgrm->children[1]);
            *Adr_exec(verb_tbl, prgrm->children[0]) = num;
            return num;
        }
        else if(!strcmp(vale, "+")){
            int num_1 = Num_exec(verb_tbl, prgrm->children[0]);
            int num_2 = Num_exec(verb_tbl, prgrm->children[1]);
            return num_1 + num_2;
        }
        else if(!strcmp(vale, "-")){
            int num_1 = Num_exec(verb_tbl, prgrm->children[0]);
            int num_2 = Num_exec(verb_tbl, prgrm->children[1]);
            return num_1 - num_2;
        }
        else if(!strcmp(vale, "*")){
            int num_1 = Num_exec(verb_tbl, prgrm->children[0]);
            int num_2 = Num_exec(verb_tbl, prgrm->children[1]);
            return num_1 * num_2;
        }
        else if(!strcmp(vale, "/")){
            int num_1 = Num_exec(verb_tbl, prgrm->children[0]);
            int num_2 = Num_exec(verb_tbl, prgrm->children[1]);
            return num_1 / num_2;
        }
        else{
            printf("unknow symbol\n");
            return 0;
        }
    }
    else{
        for(int i = 0;i<prgrm->num_of_children;i++){
            Num_exec(verb_tbl, prgrm->children[i]);
        }
        return 0;
    }
}
int* Adr_exec(struct Memory* verb_tbl, struct Expression* prgrm){
    char *verb_name = prgrm->value;
    return verb_create(verb_tbl, verb_name);
}
int* verb_create(struct Memory* verb_lst, char* verb_name){
    for(int i = 0; i<verb_lst->length; i++){
        if(!strcmp(verb_lst->key[i], verb_name)){
            return (verb_lst->value)+i;
        }
    }
    verb_lst->length++;
    verb_lst->key = (char**)realloc(verb_lst->key, verb_lst->length * sizeof(char*));
    verb_lst->value = (int*)realloc(verb_lst->value, verb_lst->length * sizeof(int));
    verb_lst->key[verb_lst->length - 1] = verb_name;
    return (verb_lst->value) + verb_lst->length - 1;
}

int verb_find(struct Memory* verb_lst, char* verb_name){
    for(int i = 0; i<verb_lst->length; i++){
        if(!strcmp(verb_lst->key[i], verb_name)){
            return verb_lst->value[i];
        }
    }
    printf("%s is not defined\n", verb_name);
    exit(1);
}

void simplify_AST(struct Expression* prgrm){
    zip_tree(prgrm);
    remove_scope_epc(prgrm);
    zip_tree(prgrm);
    Program_rebuild_tree(prgrm);
    zip_tree(prgrm);
    bubble(prgrm);
    zip_tree(prgrm);
    if(!Is_simply(prgrm))exit(2);
}
void swap_expr(struct Expression* expr){
    char* temp;
    temp = expr->parent->token;
    expr->parent->token = expr->token;
    expr->token = temp;
    temp = expr->parent->value;
    expr->parent->value = expr->value;
    expr->value = temp;
}
void bubble(struct Expression* expr){
    for(int i = 0;i < expr->num_of_children;i++){
        if(expr->parent != NULL && !strcmp(expr->parent->token, "nonterminal") && !strcmp(expr->token, "SYM")){
            swap_expr(expr);
        }
        bubble(expr->children[i]);
    }
}
void remove_scope_epc(struct Expression* prgrm){
    for(int i = 0;i<prgrm->num_of_children;i++){
        remove_scope_epc(prgrm->children[i]);
        if(!strcmp(prgrm->children[i]->value, "(") || !strcmp(prgrm->children[i]->value, ")")|| !strcmp(prgrm->children[i]->value, "epc")){
            free(disLink(prgrm, i));
            i--;
        }
    }
}
struct Expression* Program_Expression_Init(char * prgrm){
    struct Expression* pro = (struct Expression*)malloc(sizeof(struct Expression));
    pro->token = prgrm;
    pro->value = prgrm;
    pro->num_of_children = 0;
    return pro;
}
void Token_append(struct Token_list* tokn_list, char* value_, char* type_){
    if(value_[0]=='\0'||value_[0]==' ')return;
    if(tokn_list->length>1)
        if(!strcmp(((tokn_list->Tokl)+(tokn_list->length)-1)->type, "END") && !strcmp(value_, "\n"))return;
    if(tokn_list->length < 1)
        if(!strcmp(value_, "\n"))return;
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
                if(tokn_lst->length>1)
                if(!strcmp(tokn_lst->Tokl[tokn_lst->length-1].type, "SYM") && code[i]=='-'){
                    word = (char*)calloc(20, sizeof(char));
                    strncat(word, code + i, 1);
                    continue;
                }
                word = (char*)calloc(2, sizeof(char));
                strncat(word, code + i, 1);
                Token_append(tokn_lst, word, word);
                word = (char*)calloc(20, sizeof(char));
                if(code[i+1]=='\0'){
                    Token_append(tokn_lst, "\n", "\n");
                }
        }
        else{
            strncat(word, code + i, 1);
            if(code[i+1]=='\0'){
                Token_append(tokn_lst, word, word);
                Token_append(tokn_lst, "\n", "\n");
            }
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
    if(value[0]=='\n'||value[0]=='\0')
        return "END";
    if(atoi(value)!=0 || isAllNum(value)){
        return "Num";
    }
    if(value[0]=='+'||value[0]=='-'||value[0]=='*'||value[0]=='/'||value[0]=='='||value[0]=='('||value[0]==')')
        return "SYM";
    else{
        return "ID";
    }
}

void Link(struct Expression* expr_1, struct Expression* expr_2){
    if(expr_1 == expr_2)return;
    expr_1->num_of_children++;
    expr_1->children  = (struct Expression**)realloc(expr_1->children, expr_1->num_of_children * sizeof(struct Expression*));
    expr_1->children[expr_1->num_of_children - 1] = expr_2;
    expr_2->parent = expr_1;
}
struct Expression* disLink(struct Expression* expr_1, int index){
    struct Expression* pop_node = expr_1->children[index];
    for(int j = index;j<expr_1->num_of_children - 1;j++){
        expr_1->children[j] = expr_1->children[j + 1];
    }
    expr_1->num_of_children--;
    expr_1->children  = (struct Expression**)realloc(expr_1->children, expr_1->num_of_children * sizeof(struct Expression*));
    return pop_node;
}

void replant(struct Expression* expr_1, int index){
    struct Expression* keep = expr_1->children[index];
    int i = 0;
    while(expr_1->num_of_children > 1){
        if(expr_1->children[i] == keep){
            i++;
            continue;
        }
        Link(keep, disLink(expr_1, i));
    }
}

void SYM_Rebuild_Tree(struct Expression* expr){
    for(int i = 0; i<expr->num_of_children;i++){
        if((!strcmp(expr->children[i]->token, "SYM") && strcmp(expr->token, "SYM")!=0)){
            replant(expr, i);
            break;
        }
    }
    for(int i = 0; i<expr->num_of_children; i++){
        SYM_Rebuild_Tree(expr->children[i]);
    }
}
void Program_rebuild_tree(struct Expression* prgrm){
    for(int i = 0;i<prgrm->num_of_children;i++){
        SYM_Rebuild_Tree(prgrm->children[i]);
    }
}
struct Expression* skip_1_Node(struct Expression* expr){
    if(expr->num_of_children == 1 && (!strcmp(expr->token, "nonterminal"))){
        struct Expression* child = expr->children[0];
        free(expr);
        return skip_1_Node(child);
    }
    else{
        return expr;
    }
}
void zip_tree(struct Expression* expr){
    for(int i = 0;i<expr->num_of_children;i++){
        expr->children[i] = skip_1_Node(expr->children[i]);
        expr->children[i]->parent = expr;
        zip_tree(expr->children[i]);
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
    expr = E_Node_append(expr, "nonterminal", "E");
    T(expr, tokn, pos);
    Ex(expr, tokn, pos);
}

void Ex(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "nonterminal", "Ex");
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
        epcilon(expr);
        return;
    }
}

void T(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "nonterminal", "T");
    F(expr, tokn, pos);
    Tx(expr, tokn, pos);
}

void Tx(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "nonterminal", "Tx");
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
        epcilon(expr);
        return;
    }
}

void F(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "nonterminal", "F");
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
        printf("error in %s", (tokn + *pos)->value);
        exit(1);
    }
    
}
void epcilon(struct Expression* expr){
    expr = E_Node_append(expr, "epcilon", "epc");
}

void Assignment(struct Expression* expr, struct Token* tokn, int *pos){
    expr = E_Node_append(expr, "starter", "Assignment");
    ID(expr, tokn, pos);
    SYM(expr, tokn, "=", pos);
    E(expr, tokn, pos);
    (*pos)++;
}

void ID(struct Expression* expr, struct Token* tokn, int *pos){
    if(Tok_match(tokn, "ID", pos)){
        struct Token* current_token = tokn + *pos;
        expr = E_Node_append(expr, "ID", current_token->value);
        (*pos)++;
    }
    else{
        printf("error in %s",(tokn + *pos)->value);
        exit(1);
    }
    return;
}

void Num(struct Expression* expr, struct Token* tokn, int *pos){
    if(Tok_match(tokn, "Num", pos)){
        struct Token* current_token = tokn + *pos;
        expr = E_Node_append(expr, "Num", current_token->value);
        (*pos)++;
    }
    else{
        printf("error in %s",(tokn + *pos)->value);
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
        expr = E_Node_append(expr, "SYM", current_token->value);
        (*pos)++;
    }
    else{
        printf("error in %s",(tokn + *pos)->value);
        exit(1);
    }
}

void print_expr(struct Expression* expr){
    printf("%s ", expr->value);
    for(int i = 0; i<expr->num_of_children; i++){
        print_expr(expr->children[i]);
    }
}

int Is_simply(struct Expression* expr){
    if(!strcmp(expr->token, "epcilon")||!strcmp(expr->token, "nonterminal"))return 0;
    for(int i = 0; i<expr->num_of_children; i++){
        if(Is_simply(expr->children[i])==0)return 0;
    }
    return 1;
}

void print_AST(struct Expression* expr, int size){
    struct Expression** queue = (struct Expression**)malloc(1000 + size * sizeof(struct Expression*));
    int flag = 0;
    int head = 0;
    int has_chld = 0;
    int now_deep = 0;
    queue[0] = expr;
    flag++;
    while(1){
        has_chld = 0;
        for(int i = 0;i<queue[head]->num_of_children ;i++){
            has_chld = 1;
            queue[flag] = queue[head]->children[i];
            flag++;
            flag = flag % (size - 1); 
        }
        if(has_chld == 1 ){
            queue[flag] = Program_Expression_Init("|");
            flag++;
            flag = flag % (size - 1); 
        }
        if(strcmp(queue[head]->token, "|") != 0 ){
            if(parent_deep(queue[head]) != now_deep){
                printf("\n");
                now_deep++;
            }
        }
        if(strcmp(queue[head]->token, "|") != 0 && queue[head]->parent != NULL){
            printf("%2s", queue[head]->parent->value);
        }
        if(strcmp(queue[head]->token, "|") != 0 )printf("->%3s#", queue[head]->value);
        else{
            printf("|");
        }
        head++;
        head = head % (size - 1);
        if(head == flag)break;
    }
}

int parent_deep(struct Expression* expr){
    int deep = 0;
    while(1){
        if(!strcmp(expr->token, "Program"))break;
        expr = expr->parent;
        deep++;
    }
    return deep;
}