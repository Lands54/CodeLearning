import numpy as np
import matplotlib.pyplot as plt
from sklearn import svm, datasets

# 引用 iris 数据集
iris = datasets.load_iris()
X = iris.data[:, :2]  # 选取前两列作为X参数
y = iris.target  # 采集标签作为y参数
C = 1.0  # SVM regularization parameter

# digits = datasets.load_digits()
# diabetes = datasets.load_diabetes()
# boston = datasets.load_boston()

# 将所得参数进行模型训练
svc = svm.SVC(kernel='linear', C=1, gamma='auto').fit(X, y)
# 建图
x_min, x_max = X[:, 0].min() - 1, X[:, 0].max() + 1
y_min, y_max = X[:, 1].min() - 1, X[:, 1].max() + 1
h = (x_max / x_min) / 100
xx, yy = np.meshgrid(np.arange(x_min, x_max, h),
                     np.arange(y_min, y_max, h))
plt.subplot(1, 1, 1)  # 将显示界面分割成1*1 图形标号为1的网格
Z = svc.predict(np.c_[xx.ravel(), yy.ravel()])  # np.c按行连接两个矩阵,但变量为两个数组，按列连接
Z = Z.reshape(xx.shape)  # 重新构造行列
plt.contourf(xx, yy, Z, cmap=plt.cm.Paired, alpha=0.8)  # 绘制等高线
plt.scatter(X[:, 0], X[:, 1], c=y, cmap=plt.cm.Paired)  # 生成一个scatter散点图。
plt.xlabel('Sepal length')  # x轴标签
plt.ylabel('Sepal width')  # y轴标签
plt.xlim(xx.min(), xx.max())  # 设置x轴的数值显示范围
plt.title('SVC with linear kernel')  # 设置显示图像的名称
plt.savefig('./test1.png')  # 存储图像
plt.show()  # 显示
