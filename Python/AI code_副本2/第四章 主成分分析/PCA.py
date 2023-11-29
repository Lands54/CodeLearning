# -*- coding:utf-8 -*-
# 导入鸢尾花数据集，调用matplotlib包用于数据的可视化，并加载PCA算法包。
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.datasets import load_iris

import numpy as np
from numpy import linalg


def self_pca(X, k):
    n_samples, n_features = X.shape
    mean = np.array([np.mean(X[:, i]) for i in range(n_features)])
    norm_X = X - mean
    scatter_matrix = np.dot(np.transpose(norm_X), norm_X)
    eig_val, eig_vec = np.linalg.eig(scatter_matrix)
    eig_pairs = [(np.abs(eig_val[i]), eig_vec[:, i]) for i in range(n_features)]
    eig_pairs.sort(reverse=True)
    feature = np.array([ele[1] for ele in eig_pairs[:k]])
    return np.dot(norm_X, np.transpose(feature))


if __name__ == '__main__':
    # 以字典的形式加载鸢尾花数据集，使用y表示数据集中的标签，使用x表示数据集中的属性数据。
    data = load_iris()
    y = data.target
    x = data.data

    # 调用PCA算法进行降维主成分分析
    # 指定主成分个数，即降维后数据维度，降维后的数据保存在reduced_x中。
    pca = PCA(n_components=2)
    reduced_x = pca.fit_transform(x)

    # reduced_x = self_pca(x, 2)

    # 将降维后的数据保存在不同的列表中
    red_x, red_y = [], []
    blue_x, blue_y = [], []
    green_x, green_y = [], []

    for i in range(len(reduced_x)):
        if y[i] == 0:
            red_x.append(reduced_x[i][0])
            red_y.append(reduced_x[i][1])

        elif y[i] == 1:
            blue_x.append(reduced_x[i][0])
            blue_y.append(reduced_x[i][1])

        else:
            green_x.append(reduced_x[i][0])
            green_y.append(reduced_x[i][1])

    # 可视化
    plt.scatter(red_x, red_y, c='r', marker='x')
    plt.scatter(blue_x, blue_y, c='b', marker='D')
    plt.scatter(green_x, green_y, c='g', marker='.')
    plt.show()
