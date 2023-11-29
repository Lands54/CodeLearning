import csv
import random

old = [
    [1, 41, 94],
    [2, 37, 84],
    [3, 54, 67],
    [4, 25, 62],
    [5, 7, 64],
    [6, 2, 99],
    [7, 68, 58],
    [8, 71, 44],
    [9, 54, 62],
    [10, 83, 69],
    [11, 64, 60],
    [12, 18, 54],
    [13, 22, 60],
    [14, 83, 46],
    [15, 91, 38],
    [16, 25, 38],
    [17, 24, 42],
    [18, 58, 69],
    [19, 71, 71],
    [20, 74, 78],
    [21, 87, 76],
    [22, 18, 40],
    [23, 13, 40],
    [24, 82, 7],
    [25, 62, 32],
    [26, 58, 35],
    [27, 45, 21],
    [28, 41, 26],
    [29, 44, 35],
    [30, 4, 50],
]  # 原默认无向图

if __name__ == '__main__':
    node_num = 30  # 城市节点数量 建议不要太大 默认30
    sample = [[i, random.randint(1, 99), random.randint(1, 99)] for i in range(1, 1+node_num)]  # 数据随机生成
    with open("data.csv", "w", newline="") as datacsv:  # 写入到data.csv文件
        csvwriter = csv.writer(datacsv, dialect="excel")
        for data in sample:
            csvwriter.writerow([data[0], data[1], data[2]])
