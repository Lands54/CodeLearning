import math
import mat
def dft(x, N):
    f_list = []
    for k in  range(10 * N):
        temp = complex(0, 0)
        for n in range(N):
            if n > len(x) - 1:
                temp += 0
            else:
                temp += x[n] * pow(math.e, -1 * complex(0, 1) * math.pi * n * k / N )
        f_list.append(temp)
    return f_list

signal = [complex(x, 0) for x in range(1,11)]
system = [complex(1, 0) for _ in range(5)]
print(dft(signal, 20))

