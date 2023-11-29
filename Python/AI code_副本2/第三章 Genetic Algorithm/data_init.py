import csv
import random

import numpy as np
import pandas as pd


# 数据生成评估函数 原始数据总和最大值168 最小值28
def evaluate():
    for array in np.array(pd.read_csv('data.csv', header=None, engine='python')):
        print(sum(array))


if __name__ == '__main__':
    sample = [[random.randint(1, 25) for j in range(8)] for i in range(28)]
    print(sample)
    with open("data.csv", "w", newline="") as datacsv:
        csvwriter = csv.writer(datacsv, dialect="excel")
        for data in sample:
            csvwriter.writerow([*data[:]])
