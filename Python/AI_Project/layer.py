import random
import heapq


class Node:
    def __init__(self, state=None, deepen=0, frm=""):
        if state is None:
            state = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0]
        self.state = state
        self.cost_value = 0
        self.expanded_state = []
        self.deepen = deepen
        self.frm = frm
        self.cost_calculate()

    def __lt__(self, other):
        return self.cost_value < other.cost_value

    def cost_calculate(self):
        self.cost_value = self.deepen
        right_state = [1, 2, 3, 4,
                       5, 6, 7, 8,
                       9, 10, 11, 12,
                       13, 14, 15, 0]
        if self.state == right_state:
            self.cost_value = -1
            return
        for i in range(16):
            idxn = self.state.index(i)
            idxt = right_state.index(i)
            self.cost_value += 2 * (abs((idxn // 4) - (idxt // 4)) + abs((idxn % 4) - (idxt % 4)))

    def expand(self):
        idx = self.state.index(0)
        zero_pos = [idx // 4, idx % 4]
        for i in range(4):
            match i:
                case 0:
                    new_pos = 4 * (zero_pos[0] + 1) + zero_pos[1]
                    if 0 <= new_pos <= 15:
                        temp = self.swap(idx, new_pos)
                        self.expanded_state.append(Node(temp, self.deepen + 1, self.frm + "down "))
                case 1:
                    new_pos = 4 * (zero_pos[0] - 1) + zero_pos[1]
                    if 0 <= new_pos <= 15:
                        temp = self.swap(idx, new_pos)
                        self.expanded_state.append(Node(temp, self.deepen + 1, self.frm + "up "))
                case 2:
                    new_pos = 4 * (zero_pos[0]) + zero_pos[1] + 1
                    if 0 <= new_pos <= 15 and zero_pos[1] != 3:
                        temp = self.swap(idx, new_pos)
                        self.expanded_state.append(Node(temp, self.deepen + 1, self.frm + "right "))
                case 3:
                    new_pos = 4 * (zero_pos[0]) + zero_pos[1] - 1
                    if 0 <= new_pos <= 15 and zero_pos[1] != 0:
                        temp = self.swap(idx, new_pos)
                        self.expanded_state.append(Node(temp, self.deepen + 1, self.frm + "left "))
        return self.expanded_state

    def swap(self, a: int, b: int) -> list:
        new_state = self.state.copy()
        temp = new_state[a]
        new_state[a] = new_state[b]
        new_state[b] = temp
        return new_state

    def read_cost(self):
        return self.cost_value

    def random_create(self, times, seed=0):
        random.seed(seed)
        right_state = [1, 2, 3, 4,
                       5, 6, 7, 8,
                       9, 10, 11, 12,
                       13, 14, 15, 0]
        result = Node(right_state)
        for i in range(times):
            temp = result.expand()
            result = temp[random.randint(0, len(temp) - 1)]
        result.deepen = 0
        result.cost_calculate()
        result.frm = ""
        return result

    def do(self, action: str):
        self.print_state()
        num = 0
        for i in action.split():
            num += 1
            temp = self.expand()
            for node in temp:
                if i[-2] == node.frm[-3]:
                    self.state = node.state
            print("step", num)
            self.print_state()
        return self

    def print_state(self):
        for idx, i in enumerate(self.state):
            print("%3d" % i, end="")
            if (idx + 1) % 4 == 0:
                print("")
        print("")


class a_Star:

    def __init__(self, initial_state: Node):
        self.open_list: list[Node] = []
        self.close_list = set()
        heapq.heappush(self.open_list, initial_state)

    def search(self):
        time = 0
        if self.open_list[0].read_cost() == -1:
            return self.open_list[0]
        while True:
            time += 1
            if time > 10000000:
                break
            temp = heapq.heappop(self.open_list)
            new_node = temp.expand()
            for node in new_node:
                state = tuple(node.state)
                if state not in self.close_list:
                    self.close_list.add(state)
                    heapq.heappush(self.open_list, node)
                    if node.read_cost() == -1:
                        return node
        return Node(frm="not found")


if __name__ == '__main__':
    seed = random.randint(0, 10000)
    initial_node = Node().random_create(50000, seed)
    initial_st = initial_node.state
    print("initial state is:")
    initial_node.print_state()
    optim = a_Star(initial_node)
    result = optim.search()
    print("solution is:")
    print("close node:", len(optim.close_list))
    print("number of step:", len(result.frm.split()), "\n", result.frm)
    print("try to rebuild")
    Node(initial_st).do(result.frm)
