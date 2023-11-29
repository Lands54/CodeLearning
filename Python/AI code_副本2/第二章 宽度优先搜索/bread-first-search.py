
#此代码搜索到解则终止

# frontier：边缘。存储未扩展的节点
# explored：探索集。存储的是状态

# 流程：
# 如果边缘为空，则返回失败。
# 否则从边缘中选择一个叶子节点。操作：POP(frontier)
# 将叶子节点的状态放在探索集
# 遍历叶子节点的所有动作
# 每个动作产生子节点
# 如果子节点的状态不在探索集或者边缘，则目标测试：通过返回,
# 失败则放入边缘。
 

# 注意：算法中只有在遍历叶子节点所有动作，即宽度搜索之前，
# 才将叶子节点的状态放入到探索集。
# 在遍历过程中，如果子节点没有通过目标测试，并没有将子节点的状态放入探索集，
# 而是将子节点放在边缘中，准备下一轮基于本子节点的宽度遍历





import pandas as pd
from pandas import Series, DataFrame

# 城市信息：city1 city2 path_cost
_city_info = None
 
 
 
# 节点数据结构
class Node:
    def __init__(self, state, parent, action, path_cost):
        self.state = state
        self.parent = parent
        self.action = action
        self.path_cost = path_cost







def main():
   global _city_info
   import_city_info()

   while True:
       src_city = input('input src city\n')
       print(src_city)
       if src_city=="end":
           print("bye")
           break
       dst_city = input('input dst city\n') 
       print(dst_city)
       
       result = breadth_first_search(src_city, dst_city)
       if not result:
           print('from city: %s to city %s search failure' % (src_city, dst_city))
       else:
           print('from city: %s to city %s search success' % (src_city, dst_city))
           path = []
           while True:
               path.append(result.state)
               if result.parent is None:
                   break
               result = result.parent
           size = len(path)
           for i in range(size):
               if i < size - 1:
                   print('%s->' % path.pop(), end='')
               else:
                   print(path.pop())







def import_city_info():
   global _city_info
   data = [{'city1': 'Oradea', 'city2': 'Zerind', 'path_cost': 71},
           {'city1': 'Oradea', 'city2': 'Sibiu', 'path_cost': 151},
           {'city1': 'Zerind', 'city2': 'Arad', 'path_cost': 75},
           {'city1': 'Arad', 'city2': 'Sibiu', 'path_cost': 140},
           {'city1': 'Arad', 'city2': 'Timisoara', 'path_cost': 118},
           {'city1': 'Timisoara', 'city2': 'Lugoj', 'path_cost': 111},
           {'city1': 'Lugoj', 'city2': 'Mehadia', 'path_cost': 70},
           {'city1': 'Mehadia', 'city2': 'Drobeta', 'path_cost': 75},
           {'city1': 'Drobeta', 'city2': 'Craiova', 'path_cost': 120},
           {'city1': 'Sibiu', 'city2': 'Fagaras', 'path_cost': 99},
           {'city1': 'Sibiu', 'city2': 'Rimnicu Vilcea', 'path_cost': 80},
           {'city1': 'Rimnicu Vilcea', 'city2': 'Craiova', 'path_cost': 146},
           {'city1': 'Rimnicu Vilcea', 'city2': 'Pitesti', 'path_cost': 97},
           {'city1': 'Craiova', 'city2': 'Pitesti', 'path_cost': 138},
           {'city1': 'Fagaras', 'city2': 'Bucharest', 'path_cost': 211},
           {'city1': 'Pitesti', 'city2': 'Bucharest', 'path_cost': 101},
           {'city1': 'Bucharest', 'city2': 'Giurgiu', 'path_cost': 90},
           {'city1': 'Bucharest', 'city2': 'Urziceni', 'path_cost': 85},
           {'city1': 'Urziceni', 'city2': 'Vaslui', 'path_cost': 142},
           {'city1': 'Urziceni', 'city2': 'Hirsova', 'path_cost': 98},
           {'city1': 'Neamt', 'city2': 'Iasi', 'path_cost': 87},
           {'city1': 'Iasi', 'city2': 'Vaslui', 'path_cost': 92},
           {'city1': 'Hirsova', 'city2': 'Eforie', 'path_cost': 86}]

   _city_info = DataFrame(data, columns=['city1', 'city2', 'path_cost'])
   # print(_city_info)


def breadth_first_search(src_state, dst_state):
   global _city_info

   node = Node(src_state, None, None, 0)
   # 目标测试
   if node.state == dst_state:
       return node
   frontier = [node]
   explored = []

   while True:
       if len(frontier) == 0:
           return False
       node = frontier.pop(0)
       explored.append(node.state)
       if node.parent is not None:
           print('deal node:state:%s\tparent state:%s\tpath cost:%d' % (node.state, node.parent.state, node.path_cost))
       else:
           print('deal node:state:%s\tparent state:%s\tpath cost:%d' % (node.state, None, node.path_cost))

       # 遍历子节点
       for i in range(len(_city_info)):
           dst_city = ''
           if _city_info['city1'][i] == node.state:
               dst_city = _city_info['city2'][i]
           elif _city_info['city2'][i] == node.state:
               dst_city = _city_info['city1'][i]
           if dst_city == '':
               continue
           child = Node(dst_city, node, 'go', node.path_cost + _city_info['path_cost'][i])
           print('\tchild node:state:%s path cost:%d' % (child.state, child.path_cost))
           if child.state not in explored and not is_node_in_frontier(frontier, child):
               # 目标测试
               if child.state == dst_state:
                   print('\t\t this child is goal!')
                   return child
               frontier.append(child)
               print('\t\t add child to child')


def is_node_in_frontier(frontier, node):
   for x in frontier:
       if node.state == x.state:
           return True
   return False






if __name__ == '__main__':
   main()







