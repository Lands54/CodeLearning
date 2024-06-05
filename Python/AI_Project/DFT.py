import math
import random

import matplotlib.pyplot as plt


def dft(x):
    N = len(x)
    f_list = []
    k_limit = N
    n_limit = N
    for k in range(k_limit):
        temp = complex(0, 0)
        for n in range(n_limit):
            temp += x[n] * pow(math.e, -1 * complex(0, 1) * 2 * math.pi * n * k / N)
        f_list.append(temp)
    return f_list


def idft(x):
    N = len(x)
    x_list = []
    k_limit = N
    n_limit = N
    for n in range(n_limit):
        temp = complex(0, 0)
        for k in range(k_limit):
            temp += 1 / N * x[k] * pow(math.e, complex(0, 1) * 2 * math.pi * n * k / N)
        x_list.append(temp)
    return x_list


def fabs(s: list[complex]):
    x = s.copy()
    for i in range(len(x)):
        x[i] = abs(x[i])
    return x


def fcmul(a, b):
    a = a.copy()
    b = b.copy()
    N = max(len(a), len(b))
    while len(a) < N:
        a.append(complex(0, 0))
    while len(b) < N:
        b.append(complex(0, 0))
    c = []
    for i in range(len(a)):
        c.append(a[i] * b[i])
    return c


N = 1000
plt.figure(figsize=(15, 8))
signal = [complex(n**2, 0) for n in range(100)]
system = [complex(1 / (n + 1e-10), 0) for n in range(N)]

E = dft(signal)
G = dft(system)
C = fcmul(G, E)
c = idft(C)

plt.subplot(2, 3, 1)
plt.plot(fabs(E))
plt.subplot(2, 3, 2)
plt.plot(fabs(G))
plt.subplot(2, 3, 3)
plt.plot(fabs(C))
plt.subplot(2, 3, 4)
plt.plot(fabs(signal))
plt.subplot(2, 3, 5)
plt.plot(fabs(system))
plt.subplot(2, 3, 6)
plt.plot(fabs(c))
plt.show()
