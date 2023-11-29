# 使用图像测试识别手写数字
import torch
import random
from torch.autograd import Variable
from torchvision import datasets, transforms
from torchvision.transforms import ToPILImage
from torch.utils.data import DataLoader

# 数据转化为tensor格式
data_transform = transforms.Compose([transforms.ToTensor()])

# 加载测试数据集
show_dataset = datasets.MNIST(root='./data', train=False, transform=data_transform, download=True)

# 调用net里定义的网络模型LeNet5 将数据模型转到cpu
device = 'cpu'
model = torch.load(
    '../../../Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/6d4d362a18299a87e057ce31b6c4f348/Message/MessageTemp/05c0d15599d4b1a6c753ae92fc07db0d/File/AI code/第五章 LeNet5 手写数字识别/weight/LeNet.pkl')
model = model.to(device)  # 在cpu上进行推理
model.eval()  # 切换到评估模式

# 获取结果
classes = [ "0",  "1", "2",  "3", "4", "5", "6", "7", "8",  "9"]

# 随机加载手写字符识别
# 把tensor转化为图片，方便可视化
show = ToPILImage()

i = random.randint(0, len(show_dataset))
X, y = show_dataset[i][0], show_dataset[i][1]
show(X).show()
X = Variable(torch.unsqueeze(X, dim=0).float(), requires_grad=False).to(device)
with torch.no_grad():
    pred = model(X)
    predicted, actual = classes[torch.argmax(pred[0])], classes[y]
    print(f'figure {str(i)}: predicted: "{predicted}", actual: "{actual}"')

