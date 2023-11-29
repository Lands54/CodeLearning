import matplotlib.pyplot as plt
from sklearn import *
import numpy as np
import plot_decision_regions


iris = datasets.load_iris()
x = iris.data[:, [2, 3]]
y = iris.target
x_train, x_test, y_train, y_test = (
    model_selection.train_test_split(x, y, test_size=0.3, random_state=1, stratify=y)
)
Standard = preprocessing.StandardScaler()
Standard.fit(x_train)
x_train_std = Standard.transform(x_train)
x_test_std = Standard.transform(x_test)
# ppn = linear_model.Perceptron()
# ppn = linear_model.LogisticRegression()
# ppn = svm.SVC(kernel='rbf')
tree_module = tree.DecisionTreeClassifier()
# ppn.fit(x_train_std, y_train)
# y_predict = ppn.predict(x_test_std)
# print("Misclassified %d" % (y_test != y_predict).sum())
# print(ppn.score(x_test_std, y_test))
x_combined = np.vstack((x_train_std, x_test_std))
y_combined = np.hstack((y_train, y_test))
# plot_decision_regions.plot_decision_regions(x_combined, y_combined, ppn)
# tree_module.fit(x_train_std, y_train)
# random_forest = ensemble.RandomForestClassifier()
# random_forest.fit(x_train_std, y_train)
# print(tree_module.predict(x_test_std, y_test))
# tree.plot_tree(tree_module)
pnn = neighbors.KNeighborsClassifier()
pnn.fit(x_train_std, y_train)
plot_decision_regions.plot_decision_regions(x_combined, y_combined, pnn)
plt.show()