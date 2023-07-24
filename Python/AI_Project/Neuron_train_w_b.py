import numpy as np
import matplotlib.pyplot as plt


class NeuronUnit:

    input_data = np.random.randn(2, 2)
    weighting = np.random.randn(2, 2)
    b_matrix = np.random.randn(2, 2)
    b = np.random.random(1)

    def __init__(self):
        self.input_data = np.random.randn(2, 2)
        self.weighting = np.random.randn(2, 2)
        self.b_matrix = np.random.randn(2, 2)
        self.b = np.random.random(1)

    def active_function_output(self):
        weighted_sum = self.weighting * self.input_data + self.b
        for i in range(2):
            for j in range(2):
                if weighted_sum[i, j] <= 0:
                    weighted_sum[i, j] *= 0.01
        return weighted_sum

    def inactive_function_output(self):
        weighted_matrix = self.weighting * self.input_data + self.b_matrix
        for i in range(2):
            for j in range(2):
                if weighted_matrix[i, j] <= 0:
                    weighted_matrix[i, j] = 0
        return weighted_matrix

    def weighting_output(self):
        return np.sum(self.weighting * self.input_data) + self.b


def leaky_relu(x):
    if x <= 0:
        return 0.01 * x
    else:
        return x


def delta_leaky_relu(x):
    if x <= 0:
        return 0.01
    else:
        return 1


def relu(x):
    if x <= 0:
        return 0
    else:
        return x


def delta_relu(x):
    for i in range(2):
        for j in range(2):
            if x[i, j] <= 0:
                x[i, j] = 0
            else:
                x[i, j] = 1
    return x


if __name__ == '__main__':
    purpose = 10
    learn_rate = 0.0001
    neuron_net = [NeuronUnit() for _ in range(2)]
    data = np.random.rand(2, 2)
    purpose_error = purpose * 0.0000001
    times = 0
    output = 0
    error_list = []
    times_list = []
    while True:
        # positive calculate
        neuron_net[0].input_data = data
        neuron_net[1].input_data = neuron_net[0].inactive_function_output()
        output = leaky_relu(np.sum(neuron_net[1].weighting_output()))

        # error decision
        error = (output - purpose) ** 2
        if abs(error) < purpose_error or times > 1000000:
            break

        # calculate gradient
        delta_weighting_1 = \
            2 * (output - purpose) * \
            delta_leaky_relu(np.sum(neuron_net[1].weighting_output())) * \
            neuron_net[1].input_data

        delta_b_1 = \
            2 * (output - purpose) * \
            delta_leaky_relu(np.sum(neuron_net[1].weighting_output()))

        delta_weighting_0 = \
            2 * (output - purpose) * \
            delta_leaky_relu(neuron_net[1].weighting_output()) * \
            neuron_net[1].weighting * \
            delta_relu(neuron_net[0].inactive_function_output()) * \
            data

        delta_b_0_matrix = \
            2 * (output - purpose) * \
            delta_leaky_relu(neuron_net[1].weighting_output()) * \
            neuron_net[1].weighting * \
            delta_relu(neuron_net[0].inactive_function_output())

        # update
        neuron_net[1].weighting += - learn_rate * delta_weighting_1
        neuron_net[1].b += - learn_rate * delta_b_1
        neuron_net[0].weighting += - learn_rate * delta_weighting_0
        neuron_net[0].b_matrix += - learn_rate * delta_b_0_matrix
        times += 1
        error_list.append(error)
        times_list.append(times)

    print(neuron_net[0].weighting)
    print(neuron_net[1].weighting)
    print(neuron_net[0].b_matrix)
    print(neuron_net[1].b)
    plt.plot(times_list, error_list)
    plt.show()
    print(output, times)
