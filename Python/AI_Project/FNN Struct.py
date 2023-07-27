import random

import numpy as np
import math
import matplotlib.pyplot as plt


class NeuronLayer:
    def __init__(self, widen):
        self.widen = widen
        self.layer_weighting = np.random.rand(widen, 1).T
        self.layer_bias = np.random.rand(widen, 1)
        self.active_function = "Leaky Relu"
        self.input_data = np.random.rand(widen, 1).T
        self.sum_weighting = np.random.rand(widen, 1).T

    def to_sigmoid(self):
        self.active_function = "sigmoid"

    def to_relu(self):
        self.active_function = "Relu"

    def to_leaky_relu(self):
        self.active_function = "Leaky Relu"

    def to_tanh(self):
        self.active_function = "tanh"

    def change_row(self, number):
        self.layer_weighting = np.random.rand(self.widen, number).T

    def to_inactive(self):
        self.active_function = "Inactive"

    def change_widen(self, widen):
        self.__init__(widen)

    def derivative(self):
        rows, cols = self.sum_weighting.shape
        derivative_matrix = np.empty((rows, cols))
        match self.active_function:
            case "sigmoid":
                for i in range(rows):
                    for j in range(cols):
                        derivative_matrix[i][j] = 1 / (1 + math.exp(-1 * self.sum_weighting[i][j])) * \
                                                   (1 - 1 / (1 + math.exp(-1 * self.sum_weighting[i][j])))
            case "Relu":
                for i in range(rows):
                    for j in range(cols):
                        if self.sum_weighting[i][j] <= 0:
                            derivative_matrix[i][j] = 0
                        else:
                            derivative_matrix[i][j] = 1
            case "Leaky Relu":
                for i in range(rows):
                    for j in range(cols):
                        if self.sum_weighting[i][j] <= 0:
                            derivative_matrix[i][j] = 0.1
                        else:
                            derivative_matrix[i][j] = 1
            case "tanh":
                for i in range(rows):
                    for j in range(cols):
                        derivative_matrix[i][j] = 1 - np.tanh(self.sum_weighting[i][j]) ** 2
            case _:
                derivative_matrix = self.sum_weighting
        return derivative_matrix

    def output(self, input_data):
        self.input_data = input_data
        inactive_output = self.layer_weighting.T @ input_data + self.layer_bias
        self.sum_weighting = inactive_output
        rows, cols = inactive_output.shape
        match self.active_function:
            case "sigmoid":
                for i in range(rows):
                    for j in range(cols):
                        inactive_output[i][j] = 1 / (1 + np.exp(-1 * inactive_output[i][j]))
            case "Relu":
                for i in range(rows):
                    for j in range(cols):
                        inactive_output[i][j] = max(0, inactive_output[i][j])
            case "Leaky Relu":
                for i in range(rows):
                    for j in range(cols):
                        inactive_output[i][j] = max(0.1 * inactive_output[i][j], inactive_output[i][j])
            case "tanh":
                for i in range(rows):
                    for j in range(cols):
                        inactive_output[i][j] = np.tanh(inactive_output[i][j])
            case _:
                pass
        return inactive_output


class FNN:
    def __init__(self, deepen, widen):
        self.deepen = deepen
        self.neuron_net = [NeuronLayer(widen) for _ in range(deepen)]
        self.cost_function = "2x"
        self.purpose = 0.5
        self.error = np.empty(1)
        self.purpose = 0
        self.output = np.empty(1)

    def change_input_widen(self, widen):
        self.neuron_net[0].change_widen(widen)

    def change_output_widen(self, widen):
        self.neuron_net[self.deepen-1].change_widen(widen)

    def change_output_inactive(self):
        self.neuron_net[self.deepen - 1].to_inactive()

    def change_output_sigmoid(self):
        self.neuron_net[self.deepen - 1].to_sigmoid()

    def change_output_tanh(self):
        self.neuron_net[self.deepen - 1].to_tanh()

    def mate(self, input_widen):
        self.neuron_net[0].change_row(input_widen)
        for i in range(self.deepen - 1):
            self.neuron_net[i+1].change_row(self.neuron_net[i].widen)

    def forward(self, input_data):
        calculate_data_in = input_data
        for i in range(self.deepen):
            calculate_data_in = self.neuron_net[i].output(calculate_data_in)
        self.output = calculate_data_in
        return calculate_data_in

    def error_result(self, purpose):
        self.purpose = purpose
        match self.cost_function:
            case "2x":
                self.error = (self.purpose - self.output) ** 2
                return self.error

    def back_forward(self, learning_rate):
        # delta_error = 1
        match self.cost_function:
            case "2x":
                active_output = 2 * (self.output - self.purpose)
                for i in range(self.deepen - 1, -1, -1):
                    inactive_output = active_output * self.neuron_net[i].derivative()
                    delta_w = inactive_output @ self.neuron_net[i].input_data.T
                    delta_bias = inactive_output
                    active_output = self.neuron_net[i].layer_weighting @ inactive_output
                    self.neuron_net[i].layer_weighting += -1 * learning_rate * delta_w.T
                    self.neuron_net[i].layer_bias += -1 * learning_rate * delta_bias

    def train(self, enter_data, purpose_data, learning_rate, max_error, max_learning_times):
        error_list = []
        times_list = []
        for i in range(max_learning_times):
            self.forward(enter_data)
            self.error_result(purpose_data)
            if abs(self.output - purpose_data) < purpose_data * max_error:
                break
            self.back_forward(learning_rate)
            error_list.append(self.error[0][0])
            times_list.append(i)
        # plt.plot(times_list, error_list)
        # plt.show()
        # print(self.output)


if __name__ == '__main__':
    fnn = FNN(3, 4)
    fnn.change_output_widen(1)
    fnn.mate(1)
    fnn.change_output_sigmoid()
    for z in range(200000):
        data_in = np.array([[random.random()]])
        purpose = 0
        if data_in < 0.5:
            purpose = 0
        else:
            purpose = 1
        fnn.train(20 * data_in, purpose, 0.1, 0.000000000000000000001, 1)
    data_list = []
    x_list = []
    for x in np.arange(0.1, 20, 0.01):
        data_list.append(fnn.forward([[x]])[0][0])
        x_list.append(x)
    plt.plot(x_list, data_list)
    plt.show()
    while True:
        data = np.array([[float(input(":"))]])
        print(fnn.forward(data), "real = ", math.sin(data) + 1, abs(fnn.forward(data) - math.sin(data))/math.sin(data))
