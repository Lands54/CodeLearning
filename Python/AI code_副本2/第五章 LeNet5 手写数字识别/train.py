# 模型训练

import torch
import torch.nn as nn
from model import LeNet5
from torch.utils.data import DataLoader
from data import MyDataset
import torchvision.transforms as transforms

# 实例化数据集类型
trainDataset = MyDataset('data/MNIST/raw', 'train-images-idx3-ubyte.gz', 'train-labels-idx1-ubyte.gz',
                         transform=transforms.ToTensor())
# 加载训练集
data_train_loader = DataLoader(trainDataset, batch_size=256, shuffle=True, num_workers=0)

# 实例化模型
model = LeNet5()
device = torch.device("cpu") #使用电脑的cpu进行训练
print(device) #打印所用设备名称
model = model.to(device) #将模型搬移到cpu上
model.train() # 切换到训练模式

# 交叉熵损失函数
criterion = nn.CrossEntropyLoss()
# 定义优化器
lr = 0.01
optimize = torch.optim.SGD(model.parameters(), lr=lr, momentum= 0.9, weight_decay=5e-4)

#开始迭代
epochs = 5   #设置迭代次数
train_loss = 0
correct = 0
total = 0
for epoch in range(epochs):
    for batch_idx, (inputs, targets) in enumerate(data_train_loader):

        optimize.zero_grad() #梯度值为零
        inputs = inputs.to('cpu')
        outputs = model(inputs) #传入模型
        outputs = outputs.to(device)
        targets = targets.to(device)
        loss = criterion(outputs, targets) #计算损失
        loss.backward() #反向传播
        optimize.step() #优化器优化

        #计算训练损失
        train_loss += loss.item()
        _, predicted = outputs.max(1) #选取预测值最大的一类
        total += targets.size(0) #统计类别
        correct += predicted.eq(targets).sum().item() #计算正确个数

    #打印结果
    print('epoch:{}'.format(epoch), '------ loss:%.3f | acc:%.3f%%(%d/%d)'%(train_loss/(batch_idx+1),
                                                                            100.*correct/total, correct, total))
# 保存模型
torch.save(model, 'weight/LeNet.pkl')