# 亲和力传播聚类
from numpy import unique
from numpy import where
from sklearn.datasets import make_classification
from sklearn.cluster import *
from sklearn.mixture import GaussianMixture
from matplotlib import pyplot


# 定义数据集
X, y = make_classification(n_samples=1000, n_features=2, n_informative=2, n_redundant=0, n_clusters_per_class=1,
                           random_state=3)


def yuanshi():
    for class_value in range(2):
        row_ix = where(y == class_value)
        pyplot.scatter(X[row_ix, 0], X[row_ix, 1])
    pyplot.show()


def qinHeLi():
    model = AffinityPropagation(damping=0.9)
    model.fit(X)
    return model.predict(X)


def juhejulei():
    model = AgglomerativeClustering(n_clusters=2)
    return model.fit_predict(X)


def birch():
    model = Birch(threshold=0.01, n_clusters=2)
    model.fit(X)
    return model.predict(X)


def dbscan():
    model = DBSCAN(eps=0.30, min_samples=9)
    return model.fit_predict(X)


def kmeans():
    model = KMeans(n_clusters=2)
    model.fit(X)
    return model.predict(X)


def minibatchKmeans():
    model = MiniBatchKMeans(n_clusters=2)
    model.fit(X)
    return model.predict(X)


def meanshift():
    model = MeanShift()
    return model.fit_predict(X)


def optics():
    model = OPTICS(eps=0.8, min_samples=10)
    return model.fit_predict(X)


def spectral():
    model = SpectralClustering(n_clusters=2)
    return model.fit_predict(X)


def Gaussian():
    model = GaussianMixture(n_components=2)
    model.fit(X)
    return model.predict(X)


if __name__ == "__main__":
    yuanshi()
    yhat = [0]*10
    yhat[0] = qinHeLi()
    yhat[1] = juhejulei()
    yhat[2] = birch()
    yhat[3] = dbscan()
    yhat[4] = kmeans()
    yhat[5] = minibatchKmeans()
    yhat[6] = meanshift()
    yhat[7] = optics()
    yhat[8] = spectral()
    yhat[9] = Gaussian()

    for item in yhat:
        clusters = unique(item)  # 检索唯一群集
        for cluster in clusters:  # 为每个群集的样本创建散点图
            row_ix = where(item == cluster)  # 获取此群集的示例的行索引
            pyplot.scatter(X[row_ix, 0], X[row_ix, 1])  # 创建这些样本的散布
        pyplot.show()  # 绘制散点图
