import heapq
import random

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


class GA_TSP(object):
    pop_size = 50  # 种群规模
    p_cr = 0.8  # 交叉概率
    p_mu = 0.05  # 变异概率
    constraints = [3, 5, 7, 9, 11, 14]  # 约束条件
    job_num = 28  # job数量
    machine_num = 8  # 机器数量
    elite_number = 20  # 每一代中精英解的数量
    minimize_complete_time = []  # 最小完工时间
    best_solve = []  # 最佳作业排序
    work_time = np.array(pd.read_csv('data.csv', header=None, engine='python'))  # 工作时间表

    def __init__(self, popsize, p_cr, p_mu, iteration, elite_number):
        self.pop_size = popsize
        self.p_cr = p_cr
        self.p_mu = p_mu
        self.iteration = iteration
        self.elite_number = elite_number

    # 初始化种群
    def initial(self):
        pop = []

        for i in range(self.pop_size):
            gene1 = np.arange(1, self.job_num + 1)
            np.random.shuffle(gene1)
            gene1 = gene1.tolist()
            gene2 = random.sample(self.constraints, 3)
            gene1.extend(gene2)
            pop.append(gene1)

        return np.array(pop)

    # 交叉
    def cross_over(self, parent1, parent2):
        child1 = []
        child2 = []

        if np.random.rand() > self.p_cr:
            return np.zeros(self.job_num + 3), np.zeros(self.job_num + 3)
        else:
            index1 = np.random.randint(0, self.job_num)
            index2 = np.random.randint(self.job_num, self.job_num + 3)
            for i in range(index1, self.job_num):
                child1.append(parent2[i])
            for i in range(self.job_num):
                if parent1[i] not in child1:
                    child1.append(parent1[i])
            for i in range(self.job_num, self.job_num + 3):
                child1.append(parent1[i])
            if parent2[index2] not in child1[-3:]:
                child1[index2] = parent2[index2]

            for i in range(index1, self.job_num):
                child2.append(parent1[i])
            for i in range(self.job_num):
                if parent2[i] not in child2:
                    child2.append(parent2[i])
            for i in range(self.job_num, self.job_num + 3):
                child2.append(parent2[i])
            if parent1[index2] not in child2[-3:]:
                child2[index2] = parent1[index2]

            return np.array(child1), np.array(child2)

    # 变异
    def mutate(self, gene):

        if np.random.rand() > self.p_mu:
            return gene
        else:
            index1 = np.random.randint(0, self.job_num)
            index2 = np.random.randint(index1, self.job_num)
            index3 = np.random.randint(self.job_num, self.job_num + 3)
            gene_slice = gene[index1:index2].copy()
            gene_slice_reverse = gene_slice[::-1]

            for i in range(len(gene_slice)):
                gene[index1 + i] = gene_slice_reverse[i]
            constraints_rem = self.constraints.copy()

            for i in gene[-3:]:
                constraints_rem.remove(i)
            gene_insert = random.choice(constraints_rem)
            gene[index3] = gene_insert

            return gene

    # 动态规划求解最大完工时间，计算适应度
    def fitness(self, pop):
        c_time = []
        fitness_all = []

        for i in range(pop.shape[0]):
            pop_cal = pop[i].copy().tolist()
            pop_cal_head = pop_cal[:self.job_num].copy()
            pop_cal_tail = pop_cal[-3:].copy()

            for j in pop_cal_tail:
                pop_cal_head.remove(j)
            ctime_dp = np.zeros((self.machine_num, self.job_num - 3))

            for ii in range(self.job_num - 3):
                for iii in range(ii + 1):
                    ctime_dp[0][ii] = ctime_dp[0][ii] + self.work_time[pop_cal_head[iii] - 1][0]
            for ii in range(1, self.machine_num):
                for iii in range(ii + 1):
                    ctime_dp[ii][0] = ctime_dp[ii][0] + self.work_time[pop_cal_head[0] - 1][iii]
            for ii in range(1, self.job_num - 3):
                for iii in range(1, self.machine_num):
                    ctime_dp[iii][ii] = max(ctime_dp[iii - 1][ii], ctime_dp[iii][ii - 1]) + \
                                        self.work_time[pop_cal_head[ii] - 1][iii]

            c_time.append(ctime_dp[self.machine_num - 1][self.job_num - 4])
            fitness_all.append(1 / ctime_dp[self.machine_num - 1][self.job_num - 4])
        index = c_time.index(min(c_time))
        best_solve_iter = pop[index].copy()

        return min(c_time), fitness_all, best_solve_iter

    # 筛选种群：精英保留法+轮盘赌
    def selection(self, pop, fitness):
        pop_after_select = []
        pop_before_select = pop.copy().tolist()
        index1 = list(map(fitness.index, heapq.nlargest(self.elite_number, fitness)))

        for i in index1:
            pop_after_select.append(pop_before_select[i])
        pop_before_select = [pop_before_select[i] for i in range(len(pop_before_select)) if i not in index1]
        fitness = [fitness[i] for i in range(len(fitness)) if i not in index1]
        probability = np.array(fitness) / np.array(fitness).sum()
        index2 = np.random.choice(np.arange(len(fitness)), size=self.pop_size - self.elite_number,
                                  replace=False, p=probability)

        for i in index2:
            pop_after_select.append(pop_before_select[i])

        return np.array(pop_after_select)

    # 进化 类主函数
    def evolution(self):
        population = self.initial()

        for i in range(self.iteration):  # 进化代数
            for ii in range(self.pop_size):  # 种群规模
                iii = np.random.randint(0, self.pop_size - 1)  # 随机配对进行交叉
                if ii != iii:
                    child1, child2 = self.cross_over(population[ii], population[iii])
                    if child1[0] != 0:  # 变异并纳入种群
                        mut_child1 = self.mutate(child1)
                        mut_child2 = self.mutate(child2)
                        population = np.vstack((population, mut_child1))
                        population = np.vstack((population, mut_child2))

            best_time_iter, fitness_iter, best_solve_iter = self.fitness(population)
            self.minimize_complete_time.append(best_time_iter)
            self.best_solve.append(best_solve_iter)
            population = self.selection(population, fitness_iter)

        return self.minimize_complete_time, self.best_solve


# 绘制甘特图
def gantt(n, m, seq, wt):
    t = np.zeros(n)
    for i in range(m):
        for j in range(n):
            if j == 0:
                t[j] = t[j] + wt[seq[j] - 1][i]
                plt.barh(y=i + 1, left=t[j] - wt[seq[j] - 1][i], height=1, width=wt[seq[j] - 1][i])
            else:
                t[j] = max(t[j], t[j - 1]) + wt[seq[j] - 1][i]
                plt.barh(y=i + 1, left=t[j] - wt[seq[j] - 1][i], height=1, width=wt[seq[j] - 1][i])
            if wt[seq[j] - 1][i] != 0:
                pass

    plt.plot([t[-1], t[-1]], [-0.5, m + 1], color=(0, 0, 0))
    x_tickss = [i for i in range(0, int(t[-1]), int((t[-1] / 4) // 10) * 10)]

    if t[-1] - x_tickss[-1] < (int((t[-1] / 4) // 10) * 10) / 4:
        x_tickss.pop()
    x_tickss.append(t[-1])
    plt.title('n=%d,m=%d,need_time=%.3f' % (n, m, t[-1]))
    plt.xticks(x_tickss)
    plt.show()

    return t


if __name__ == '__main__':
    ga = GA_TSP(40, 0.8, 0.01, 50, 20)  # 80, 0.8, 0.01, 500, 20
    solution_time, solution_schedule = ga.evolution()

    worst_schedule = list(solution_schedule[0].copy())
    worst_schedule_head = worst_schedule[:28]
    worst_schedule_tail = worst_schedule[-3:]
    for i in worst_schedule_tail:
        worst_schedule_head.remove(i)  # 第一代的排班方案

    best_schedule = list(solution_schedule[-1].copy())
    best_schedule_head = best_schedule[:28]
    best_schedule_tail = best_schedule[-3:]
    for i in best_schedule_tail:
        best_schedule_head.remove(i)  # 最后一代的排班方案

    gantt(ga.job_num - 3, ga.machine_num, worst_schedule_head, ga.work_time)
    gantt(ga.job_num - 3, ga.machine_num, best_schedule_head, ga.work_time)
