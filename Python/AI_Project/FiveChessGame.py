import copy
from typing import overload
from typing import Type
from scipy.signal import convolve2d

import numpy as np


class Game:
    def __init__(self, initial=None, evaluation=None, round_who=None):
        self.situation = initial
        self.evaluation = evaluation
        self.round = round_who

    def action(self):
        pass

    def evaluate(self):
        pass

    def victory(self):
        pass

    def print_situation(self):
        pass

    def __gt__(self, other):
        return self.evaluation > other.evaluation

    def __lt__(self, other):
        return self.evaluation < other.evaluation

    def __eq__(self, other):
        if other is Game:
            return self.evaluation == other.evaluation
        else:
            return super().__eq__(other)


class FiveChess(Game):
    def __init__(self, initial: np.array = None, evaluation=None, round_who: bool = True, size: tuple = (8, 8)):
        super().__init__(initial, evaluation, round_who)
        self.situation = np.zeros(size) if initial is None else initial
        self.shape = self.situation.shape

    def action(self, position=(0, 0)):
        new_game = copy.deepcopy(self)
        if position == "pass":
            self.round = not self.round
            return new_game
        target = new_game.situation[position[0]][position[1]]
        if target == 0:
            new_game.situation[position[0]][position[1]] = 1 if self.round else -1
            new_game.round = not new_game.round
            return new_game
        else:
            return None

    def evaluate(self):
        self.evaluation = 0
        kernel = np.array([[1, 1, 1, 1, 1]])
        self.evaluation += self._detect(kernel)
        kernel = kernel.T
        self.evaluation += self._detect(kernel)
        kernel = np.diag([1, 1, 1, 1, 1])
        self.evaluation += self._detect(kernel)
        kernel = np.fliplr(kernel)
        self.evaluation += self._detect(kernel)
        return self.evaluation

    def _detect(self, kernel=None):

        if abs(is_victory := self.victory()) == 1:
            match is_victory:
                case 1:
                    self.evaluation = 10000
                    return self.evaluation
                case -1:
                    self.evaluation = -10000
                    return self.evaluation
        kernel = np.array([[1, 1, 1, 1, 1]]) if kernel is None else kernel
        # 1
        evaluated_game = self.situation.copy()
        evaluated_game[evaluated_game == 0] = 1
        detect = convolve2d(evaluated_game, kernel, mode='valid')
        win = np.sum(np.array(detect) == 5)
        # -1
        evaluated_game = self.situation.copy()
        evaluated_game[evaluated_game == 0] = -1
        detect = convolve2d(evaluated_game, kernel, mode='valid')
        lose = np.sum(np.array(detect) == -5)
        # return
        return win - lose

    def victory(self):
        kernel = np.array([[1, 1, 1, 1, 1]])
        detect = convolve2d(self.situation, kernel, mode='valid')
        if 5 in detect:
            return 1
        elif -5 in detect:
            return -1
        kernel = kernel.T
        detect = convolve2d(self.situation, kernel, mode='valid')
        if 5 in detect:
            return 1
        elif -5 in detect:
            return -1
        kernel = np.diag([1, 1, 1, 1, 1])
        detect = convolve2d(self.situation, kernel, mode='valid')
        if 5 in detect:
            return 1
        elif -5 in detect:
            return -1
        kernel = np.fliplr(kernel)
        detect = convolve2d(self.situation, kernel, mode='valid')
        if 5 in detect:
            return 1
        elif -5 in detect:
            return -1
        return 0

    def print_situation(self):
        temp = self.situation.copy().astype(str)
        temp[temp == '0.0'] = ' '
        temp[temp == '1.0'] = 'O'
        temp[temp == '-1.0'] = 'X'
        print(temp)
        return temp


class TreeNode:
    def __init__(self, value=None):
        self.parent = None
        self.children: list[TreeNode] = []
        self.value = value

    def add_child(self, value=None):
        self.children.append(TreeNode(value))

    def get_children(self):
        return self.children.copy()


class GameTree(TreeNode):
    def __init__(self, value: Game = None):
        super().__init__(value)
        self.value: Game = value
        self.children: list[GameTree] = []

    @overload
    def add_child(self, value: Type[Game] = None):
        pass

    @overload
    def add_child(self, value: list[Type[Game]] = None):
        pass

    def add_child(self, value=None):
        if isinstance(value, list):
            for x in value:
                self.add_child(x)
        else:
            child = self.__class__(value)
            child.parent = self
            self.children.append(child)
        return self.children

    def expand(self, times=1):
        pass

    def leaf_evaluate(self):
        if self.children:
            for x in self.children:
                x.leaf_evaluate()
            self.value.evaluation = max(self.children) if self.value.round else min(self.children)
        else:
            self.value.evaluate()

    def move(self, times=1):
        self.expand(times)
        self.leaf_evaluate()
        next_chess: Type[Game] = self.value.evaluation.value
        next_chess.evaluation = None
        return next_chess

    def __gt__(self, other):
        return self.value.evaluation > other.value.evaluation

    def __lt__(self, other):
        return self.value.evaluation < other.value.evaluation

    def __eq__(self, other):
        return self.value.evaluation == other.value.evaluation


class FiveChessTree(GameTree):
    def __init__(self, value: FiveChess = None):
        super().__init__(value)
        self.value: FiveChess = value

    def expand(self, times=1):
        if times <= 0:
            return
        expanded_list = []
        (col, row) = self.value.situation.shape
        for y in range(col):
            for x in range(row):
                new_game = self.value.action((x, y))
                if not (new_game is None):
                    expanded_list.append(new_game)
        self.add_child(expanded_list)
        for child in self.children:
            child.expand(times - 1)


class ABFiveTree(FiveChessTree):
    def __init__(self, value: FiveChess = None):
        super().__init__()
        self.value = value
        self.alphabeta = [-10000, 10000]


if __name__ == '__main__':
    chess = FiveChess(size=(11, 11))
    while 1:
        chess.print_situation()
        while True:
            x = int(input(":y"))
            y = int(input(":x"))
            next = chess.action((x, y))
            if next is not None:
                chess = next
                break
        chess.print_situation()
        chess = FiveChessTree(chess).move(3)
        print(chess.victory())
    print(" ")
