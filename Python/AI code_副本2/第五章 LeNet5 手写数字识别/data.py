# minst数据集的载入

from torch.utils.data import Dataset
import gzip
import os
import numpy as np



# 自定义数据集类型
def load_data(data_folder, data_name, label_name):
    with gzip.open(os.path.join(data_folder, label_name), 'rb') as lbpath:  # 解压文件，rb表示的是读取二进制数据
        y_train = np.frombuffer(lbpath.read(), np.uint8, offset=8)

    with gzip.open(os.path.join(data_folder, data_name), 'rb') as imgpath: # 解压文件，rb表示的是读取二进制数据
        x_train = np.frombuffer(
            imgpath.read(), np.uint8, offset=16).reshape(len(y_train), 28, 28)
    return x_train, y_train

class MyDataset(Dataset):#定义存放数据的类
    def __init__(self, folder, data_name, label_name, transform=None):#读取数据，初始化数据
        (data_set, labels) = load_data(folder, data_name, label_name)
        self.data_set = data_set
        self.labels = labels
        self.transform = transform

    def __getitem__(self, index):#支持下标访问
        img, target = self.data_set[index], int(self.labels[index])
        if self.transform is not None:
            img = self.transform(img)
        return img, target

    def __len__(self):#返回自定义数据集大小，方便后期遍历
        return len(self.data_set)

