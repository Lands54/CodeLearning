import math


class ArthCode:
    matchingtable : dict[str, (float, float)] = {}

    def __init__(self, symbol : dict[str, float]):
        temp = 0.0;
        for i, j in symbol.items():
            self.matchingtable[i] = (temp, temp := temp + j)

    def code(self, str : [str]):
        Rlow = 0.0
        Rhigh = 1.0
        R = 1.0
        for i in str:
            Rhigh = Rlow + self.matchingtable[i][1] * R
            Rlow = Rlow + self.matchingtable[i][0] * R
            R = Rhigh - Rlow
        return (Rlow + Rhigh) / 2

if __name__ == '__main__':
    d = {"x":0.5, "y":0.5}
    x = ArthCode(d)
    a = x.code(["x", "y", "y", "x"])
    print(a)