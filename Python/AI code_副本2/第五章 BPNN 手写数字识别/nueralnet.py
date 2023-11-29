# coding:utf-8
import numpy as np


class NueraLNet(object):
    def __init__(self, sizes):                                    #对类进行初始化
        self.num_layers = len(sizes)                              #有几层的神经网络
        self.sizes = sizes                                        #保存层数
        self.bias = [np.random.randn(1, y) for y in sizes[1:]]    #bias中的是数组除去输入层，随机产生每层中y个神经元的bias值0-1
        self.weights = [np.random.randn(x, y) for x, y in zip(sizes[:-1], sizes[1:])]  #weights中的是二维数组，其中行代表前一层的节点位置，列代表后一层的节点位置，随机产生每条连线的weight值（0-1）

    def get_result(self, images):                                 #神经网络的前向传播部分
        result = images
        for b, w in zip(self.bias, self.weights):
            result = sigmoid(np.dot(result, w) + b)               #加权求和以及加上bias
        return result                                             #计算后每个神经元的值

    #训练神经网络
    def train_net(self, trainimage, trainresult, traintime, rate=1, minibatch=10, test_image=None, test_result=None):   #trainimage表示训练集的图片，trainresult表示训练集的结果，traintime表示使用次训练集训练的次数（迭代次数）
        for i in range(traintime):                                                                    # Rate是学习率，默认为1，minbatch为小批梯度下降中，每一批量的样本个数，默认为10，test_image为验证集图片，test_result为验证集结果
            minibatchimage = [trainimage[k:k+minibatch] for k in range(0, len(trainimage), minibatch)]                  #按照小样本数量划分训练集
            minibatchresult = [trainresult[k:k+minibatch] for k in range(0, len(trainimage), minibatch)]
            for image, result in zip(minibatchimage, minibatchresult):
                self.update_net(image, result, rate)             #根据每个小样本调用update_net函数更新网络参数
            print("epoch：{0}".format(i+1))                      #输出迭代次数
            if test_image and test_result:                       #判断有没有验证集，有的话调用test_net函数，并查看这一次训练后神经网络监测字体的正确率
                self.test_net(test_image, test_result)

    #更新网络参数
    def update_net(self, training_image, training_result, rate):
        batch_b_error = [np.zeros(b.shape) for b in self.bias]      #根据bias和weights的行列数创建对应的全部元素值为0的空矩阵
        batch_w_error = [np.zeros(w.shape) for w in self.weights]
        for image, result in zip(training_image, training_result):  #反向传播误差
            b_error, w_error = self.get_error(image, result)        #对于批次中的每一个样本，通过get_error函数得到测试这个样本之后权重和偏移量的误差梯度
            batch_b_error = [bbe + be for bbe, be in zip(batch_b_error, b_error)]
            batch_w_error = [bwe + we for bwe, we in zip(batch_w_error, w_error)]
        self.bias = [b - (rate/len(training_image))*bbe for b, bbe in zip(self.bias, batch_b_error)]  #更新数据累加的偏导值更新w和b，这里因为用了小样本，所以rate要除于小样本的长度
        self.weights = [w - (rate/len(training_image))*bwe for w, bwe in zip(self.weights, batch_w_error)]

    #反向传播误差函数get_error
    def get_error(self, image, result):                              #参数为一个样本及其结构
        b_error = [np.zeros(b.shape) for b in self.bias]             #按照类中存储权重和偏移量的变量格式相应声明存取权重和偏移量误差梯度的变量
        w_error = [np.zeros(w.shape) for w in self.weights]
        out_data = [image]                                           #储存每层的神经元的值的矩阵，下面循环会append每层的神经元的值
        in_data = []                                                 #定义列表in_data存储每一个节点的输入值。
        for b, w in zip(self.bias, self.weights):                    #前向传播输入的过程并记录了节点的输入输出值
            in_data.append(np.dot(out_data[-1], w) + b)              #在列表in_data后边添加上一层输出（out_data[-1]）乘以权重加上偏移量，作为这一层的输入
            out_data.append(sigmoid(in_data[-1]))                    #在列表out_data后边添加刚刚的输入（in_data[-1]）通过激励函数后的值，作为这一层节点的输出
        b_error[-1] = sigmoid_prime(in_data[-1]) * (out_data[-1] - result)
        w_error[-1] = np.dot(out_data[-2].transpose(), b_error[-1])
        for l in range(2, self.num_layers):                          #反向传播误差，从倒数第l层开始更新，‘-l'是python中特有的语法表示从倒数第l层开始计算
            b_error[-l] = sigmoid_prime(in_data[-l]) * \
                          np.dot(b_error[-l+1], self.weights[-l+1].transpose())                      #利用‘l+1’层的值来计算’l‘层的值
            w_error[-l] = np.dot(out_data[-l-1].transpose(), b_error[-l])
        return b_error, w_error                                                                      #返回计算好的偏移量和权重的误差梯度

    #测试神经网络正确率
    def test_net(self, test_image, test_result):                    #参数是验证集图片及其结果
        results = [(np.argmax(self.get_result(image)), result)      #获得预测结果，并取其结果中最大值的索引
                   for image, result in zip(test_image, test_result)]  #将最大结果的索引值与验证集结果打包成tuple
        right = sum(int(x == y) for (x, y) in results)              #正确识别个数
        print("正确率：{0}%".format(right*100/ len(test_result)))
        return results

    def test_net0(self, test_image, test_result):
        results = [(np.argmax(self.get_result(image)), result)
                   for image, result in zip(test_image, test_result)]
        # right = sum(int(x == y) for (x, y) in results)
        # print("正确率：{0}/{1}".format(right, len(test_result)))
        print("测试图像真实值：{}，识别值：{}".format(results[0][0], results[0][1]))  #显示测试图像的真实值与识别值
        return results

    #将神经网络的权重和偏移量保存到本地
    def save_training(self):          #使用numpy的savez函数将weights和bias中的numpy数组，打包存到本地的npz文件中。
        np.savez('./datafile/weights.npz', *self.weights)
        np.savez('./datafile/bias.npz', *self.bias)

    #将本地的参数读取到神经网络中
    def read_training(self):
        length = len(self.sizes) - 1
        file_weights = np.load('./datafile/weights.npz')  #使用load函数加载后，将加载后的数组依次添加到神经网络的weights和bias中，即可不用训练神经网络就能进行识别
        file_bias = np.load('./datafile/bias.npz')
        self.weights = []                  #定义空列表
        self.bias = []
        for i in range(length):
            index = "arr_" + str(i)
            self.weights.append(file_weights[index])
            self.bias.append(file_bias[index])

#激活函数
def sigmoid(x):
    return np.longfloat(1.0 / (1.0 + np.exp(-x)))   #sigmoid函数公式1/1+wxp(-x)

#激活函数的导数
def sigmoid_prime(x):
    return sigmoid(x) * (1 - sigmoid(x))  #sigmoid函数导数性质  a(1-a)
