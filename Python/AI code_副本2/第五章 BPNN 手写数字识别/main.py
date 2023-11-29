# coding: utf-8
from decodeMinist import *
from nueralnet import *
import random

#解析文件
train_images = decode_idx3_ubyte(train_images_idx3_ubyte_file)
train_labels = decode_idx1_ubyte(train_labels_idx1_ubyte_file)
test_images = decode_idx3_ubyte(test_images_idx3_ubyte_file)
test_labels = decode_idx1_ubyte(test_labels_idx1_ubyte_file)

trainingimages = [(im / 255).reshape(1, 784) for im in train_images]  # 归一化   将0-255的像素值转化为0.0-1.0范围内的实数，将原有数组转化为一个1行784列的新数组
traininglabels = [vectorized_result(int(i)) for i in train_labels]
testimages = [(im / 255).reshape(1, 784) for im in test_images]       # 将原有数组转化为一个1行784列的新数组
testlabels = [l for l in test_labels]           #第l层的测试集标签

i = random.randint(0, 9999)                     #在 [0,9999] 范围内随机生成一个整数
testimages_test = list((test_images[i]/255).reshape(1, 784))          #更改数组的形状，使其成为一个1*784数组，形成列表
testlabels_test = list([test_labels[i]])

# 训练
# print(type(traininglabels[0][0][0]))
net = NueraLNet([28 * 28, 30, 10])              #输入层28*28=784个输入数据，中间层30，输出层10
net.train_net(trainingimages, traininglabels, 3, 5, 10, testimages, testlabels)      #训练次数3次，学习率为5，小批梯度下降中每一批量的样本个数为10

# 测试
net.save_training()    #将神经网络的权重和偏移量保存到本地
net.read_training()    #将本地的参数读取到神经网络中
net.test_net(testimages, testlabels)   #测试神经网络正确率
print("\n")

#验证集
print("离线测试")
# 显示图像
image = Image.fromarray((test_images[i]).reshape(28, 28))  # array转换成image，将产生的test_image[i]测试图片数组改成28行28列
image.show()        #显示图片
net.test_net0(testimages_test, testlabels_test)   #显示测试图像的真实值与识别值
print("end")

# from PIL import Image
# import numpy as np



