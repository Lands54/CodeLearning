class ASTNode:
    def __init__(self, value, children=None):
        self.value = value
        self.children = children if children else []

def parseAssignment():
    id_token = match(ID)
    match("=")
    expression = parseE()
    return ASTNode("Assignment", [id_token, expression])

def parseE():
    term = parseT()
    expression_prime = parseE'()
    if expression_prime:
        return ASTNode("E", [term, expression_prime])
    return term

def parseE'():
    if currentToken in ["+", "-"]:
        operator = match(currentToken)
        term = parseT()
        expression_prime = parseE'()
        return ASTNode("E'", [operator, term, expression_prime])
    return None

def parseT():
    factor = parseF()
    term_prime = parseT'()
    if term_prime:
        return ASTNode("T", [factor, term_prime])
    return factor

def parseT'():
    if currentToken in ["*", "/"]:
        operator = match(currentToken)
        factor = parseF()
        term_prime = parseT'()
        return ASTNode("T'", [operator, factor, term_prime])
    return None

def parseF():
    if currentToken == "(":
        match("(")
        expression = parseE()
        match(")")
        return ASTNode("F", [expression])
    elif currentToken == ID:
        return ASTNode("F", [match(ID)])
    elif currentToken == Num:
        return ASTNode("F", [match(Num)])
    else:
        # 处理语法错误
        return None

# 类似地，修改其他解析函数来构建相应的AST节点

# 修改match函数以返回当前标记的信息
def match(expectedToken):
    if currentToken.type == expectedToken:
        consumedToken = currentToken
        consume currentToken
        return consumedToken.value
    else:
        # 处理语法错误
        return None