

# min_max算法 python实现




#先定义Node类,用于构造二叉树
class Node:
    """
    :param value 该节点的值,默认为0
    :param is_max 该节点是否在Max层
    child 是该节点的子节点,是一个Node类的数组
    """
    def __init__(self, value=0, is_max=True):
        self.value = value
        self.is_max = is_max
        self.child = None

    def setChildWithValue(self, childs):
        temp = []
        is_Max = not self.is_max
        for c in childs:
            temp.append(Node(value=c, is_max=is_Max))
        self.child = temp

    def setChildWithNode(self, childs):
        if None is self.child:
            self.child = childs
            return
        for c in childs:
            self.child.append(c)





#定义一个初始函数用于构造树
def init():
    node1 = Node(is_max=False)
    node1.setChildWithValue([60, 63])
    node2 = Node(is_max=False)
    node2.setChildWithValue([15, 58])
    node3 = Node(is_max=True)
    node3.setChildWithNode([node1, node2])
    node1 = Node(is_max=False)
    node1.setChildWithValue([81, 74])
    node2 = Node(is_max=False)
    node2.setChildWithValue([88, 15, 27])
    node4 = Node(is_max=True)
    node4.setChildWithNode([node1, node2])
    node5 = Node(is_max=False)
    node5.setChildWithNode([node3, node4])
    node1 = Node(is_max=False)
    node1.setChildWithValue([20, 92])
    node2 = Node(is_max=False)
    node2.setChildWithValue([9, 62])
    node3 = Node(is_max=True)
    node3.setChildWithNode([node1, node2])
    node1 = Node(is_max=False)
    node1.setChildWithValue([82, 92])
    node2 = Node(is_max=False)
    node2.setChildWithValue([54, 17])
    node4 = Node(is_max=True)
    node4.setChildWithNode([node1, node2])
    node6 = Node(is_max=False)
    node6.setChildWithNode([node3, node4])
    head = Node(is_max=True)
    head.setChildWithNode([node5, node6])
    return head





#minimax
def mini_max(node):
    if node.child is None:
        return node.value

    if not node.is_max:
        best_value = float('inf')
        for c in node.child:
            best_value = min(best_value, mini_max(c))
    else:
        best_value = -float('inf')
        for c in node.child:
            best_value = max(best_value, mini_max(c))
    return best_value





if __name__ == '__main__':
    head = init()

    print(mini_max(head))
    
    







