import numpy as np


class Perceptron:

    def __init__(self, eta, n_iter, random_state=1):
        self.eta = eta
        self.n_iter = n_iter
        self.random_state = random_state
        self.w = 0.0
        self.b = 0.0
        self.errors = []

    def fit(self, x, y):
        rgen = np.random.RandomState(self.random_state)
        self.w = rgen.normal(0.0, 0.01, x.shape[1])
        self.b = 0.0
        self.errors = []

        for _ in range(self.n_iter):
            error = 0
            for xi, label in zip(x, y):
                update = self.eta * (label - self.predict(xi))
                self.w += update * xi
                self.b += update
                error += int(update != 0.0)
            self.errors.append(error)
        return self

    def net_input(self, x):
        return np.dot(x, self.w) + self.b

    def predict(self, x):
        return np.where(self.net_input(x) >= 0.0, 1, 0)
