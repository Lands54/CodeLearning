from collections import Counter
import numpy as np
import torch
import torch.nn as nn
import Module
import DataPreProcess
# 可以直接进行检测的接口，具体使用方法见main部分
# 运行环境 Anaconda 主要外部库torch
class BarBrokenDetect:
    def __init__(self, modulePath):
        """
        :param modulePath: moduleWeightPath(.pth)
        """
        self.mod = Module.LSTMModel(6, 32, 3, 2)
        self.smd = nn.Softmax(dim=1)
        self.mod.load_state_dict(torch.load(modulePath))

    def bar_broken_detect(self, path, isPrint=False, isSave=False, isDetailReturn=False):
        """
        请确保对于试验数据或仿真数据的文件格式与训练数据格式一致\n
        0 Normal\n
        1 BarBroken\n
        2 ElseBroken\n
        :param path: The csv file path you need to detect
        :param isPrint: Do you need Print DataPreProcess Information
        :param isSave:  Do you need to save processed data
        :param isDetailReturn: Decide return value, False will return int, True will return
        list[[(type, CountNumber)+], FrameResultList]
        :return: 0:Normal, 1:BarBroken, 2:ELSE_BROKEN. OR a
        LIST[[(0/1/2, Count), (0/1/2, Count), (0/1/2, Count)], FrameResultList]
        """
        dp = DataPreProcess.DataProProcessor(path)
        temp = dp.mainProcess(isLabeledData=False, isSave=isSave, isPrint=isPrint, isNoised=False)
        x_test = temp[0]
        lengths_test = torch.tensor([len(seq) for seq in x_test], dtype=torch.long)
        t = torch.argmax(self.smd(self.mod.forward(torch.tensor(x_test), lengths_test)), dim=1)
        c = Counter(np.array(t).tolist())
        if isDetailReturn:
            return [c.most_common(3), np.array(t).tolist()]
        return c.most_common(1)[0][0]

if __name__ == '__main__':
    test = BarBrokenDetect(modulePath='lstmfinal.pth')
    # 此处数据为提供的训练数据文件夹
    t = test.bar_broken_detect("数据/试验数据/Normal/torque10_0.csv")
    print("result:", t)
