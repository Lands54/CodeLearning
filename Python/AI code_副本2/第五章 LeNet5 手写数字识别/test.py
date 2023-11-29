# 测试数据集测试模型

import torch
import torch.nn as nn
from data import MyDataset
from torchvision.transforms import transforms
from torch.utils.data import DataLoader

# 实例化测试数据集
# 实例化数据集类型
testDataset = MyDataset('data/MNIST/raw', 't10k-images-idx3-ubyte.gz', 't10k-labels-idx1-ubyte.gz',
                        transform=transforms.ToTensor())
data_test_loader = DataLoader(testDataset, batch_size=256, shuffle=True, num_workers=0)

# 加载模型
model = torch.load('./weight/LeNet.pkl')
device = torch.device( "cpu") # 检查cpu可用性
model = model.to(device)  # 在cpu上进行推理
model.eval()  # 切换到评估模式

criterion = nn.CrossEntropyLoss()  # 交叉熵损失函数

# 开始测试
train_loss = 0
correct = 0
total = 0
with torch.no_grad():
    for batch_idx, (inputs, targets) in enumerate(data_test_loader):
        inputs = inputs.to('cpu')
        outputs = model(inputs)  # 传入模型
        outputs = outputs.to(device)
        targets = targets.to(device)
        loss = criterion(outputs, targets)  # 计算损失

        # 计算训练损失
        train_loss += loss.item()
        _, predicted = outputs.max(1)  # 选取预测值最大的一类
        total += targets.size(0)  # 统计类别
        correct += predicted.eq(targets).sum().item()  # 计算正确个数

    # 打印结果
    print(
        '------ loss:%.3f | acc:%.3f%%(%d/%d)' % (train_loss / (batch_idx + 1), 100. * correct / total, correct, total))
