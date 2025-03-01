import time
from collections import Counter
import torch
import torch.nn as nn
from scipy.interpolate import interp1d
import DataPreProcess
import Module
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.colors as mcolors
# 运行环境 Anaconda 主要外部库torch
# 对训练集数据进行分析的代码，具体见moduleTest()与main部分

def plot_2d_array_equal(arrays):
    # 找到最长的数组长度
    max_length = max(len(arr) for arr in arrays)
    def interpolate_array(arr, target_length):
        if len(arr) == target_length:
            return arr
        x = np.arange(len(arr))
        x_new = np.linspace(0, len(arr) - 1, target_length)
        f = interp1d(x, arr, kind='nearest', fill_value='extrapolate')
        return f(x_new)
    interpolated_arrays = [interpolate_array(arr, max_length) for arr in arrays]
    matrix = np.vstack(interpolated_arrays)
    norm = mcolors.Normalize(vmin=np.min(matrix), vmax=np.max(matrix))
    plt.imshow(matrix.astype(np.int8), cmap=mcolors.ListedColormap(['red', 'orange', 'yellow']), norm=norm, interpolation='nearest', aspect='auto')
    plt.colorbar()
    plt.show()


def moduleTest(path, modulePath='lstmfinal.pth', isNoised=False, std=0.1, isShowPreProcessDetail=False):
    """
    如果一个csv文件路径中包括(Normal, normal)则可生成对应label{0}\n
    若没有则检测其csv路径是否包含(bar, torque)则生成对应的label{1}\n
    若都没有则生成label{2}\n
    该模式在训练数据可完全标注正确，在其他情况则不能正确标注，若要使用请符合上述特征
    :param path:  csv !Folder! you need to detect(递归访问该文件夹下并检测所有的csv)
    :param modulePath: ModuleWeightFilePath(.pth)
    :param isNoised: Testing with Noise
    :param std: NoiseSTD(need to isNoised=True)
    :param isShowPreProcessDetail: ShowPreProcessDetail
    """
    start_time = time.time()
    total_length = 0
    mat = np.zeros((3, 3))
    mod = Module.LSTMModel(6, 32, 3, 2)
    result = []
    mod.load_state_dict(torch.load('lstmfinal.pth'))
    for path in DataPreProcess.find_all_csv_files(path):
        dp = DataPreProcess.DataProProcessor(path)
        total_length += dp.length
        temp = dp.mainProcess(isLabeledData=True, isSave=False, isPrint=isShowPreProcessDetail, isNoised=isNoised, std=std)
        x_test = temp[0]
        y_test = temp[1]
        lengths_test = torch.tensor([len(seq) for seq in x_test], dtype=torch.long)
        smd = nn.Softmax(dim=1)
        t = torch.argmax(smd(mod.forward(torch.tensor(x_test), lengths_test)), dim=1)
        result.append(np.array(t).tolist())
        c = Counter(np.array(t).tolist())
        if c.most_common(1)[0][0] != y_test[0]:
            print("Wrong", path)
        for x in c.most_common(3):
            print(path, x[0], "|", y_test[0], "=", x[1] / len(x_test), "|", x[1], "|", len(x_test))
        mat[y_test[0], c.most_common(1)[0][0]] += 1
        print("\n")
    print(mat)
    print("Total Cost Time:", time.time() - start_time)
    print("Total Data:", total_length)
    plot_2d_array_equal(result)


if __name__ == '__main__':
    # 会递归访问该文件夹下所有的csv
    moduleTest(path="数据", modulePath='lstmfinal.pth', isNoised=False, std=0)
