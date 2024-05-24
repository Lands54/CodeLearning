sum = 0
for i in range(2,1000000):
    t = 0
    for x in range(2, int(i ** 0.5) + 1):
        if i % x == 0:
            t = 1
            break
    if t == 1:
        continue
    print(i)
    sum += i
print(sum)
