import io, re, os
import time
import numpy as np
import pandas as pd
from scipy.fft import fft
from scipy.signal import find_peaks, savgol_filter, lfilter, butter, iirnotch, filtfilt, resample
from matplotlib import pyplot as plt
from sklearn.decomposition import PCA

# 运行环境 Anaconda 主要外部库sklearn

def find_all_csv_files(directory):
    csv_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".csv"):
                csv_files.append(os.path.join(root, file))
    return csv_files


def ffted(data, isLog=False):
    if isLog:
        return 20 * np.log10(np.array(1 + np.abs(fft(data))))[:len(data) // 2]
    return np.array(np.abs(fft(data)))[:len(data) // 2]


def plot(data, length=0, title="Graph"):
    plt.title(title)
    if length == 0:
        plt.plot(data)
    else:
        plt.plot(data[:length])
    plt.show()


def highpass_filter(data, cutoff, fs, order=5):
    nyquist = 0.5 * fs
    normal_cutoff = cutoff / nyquist
    b, a = butter(order, normal_cutoff, btype='high', analog=False)
    y = lfilter(b, a, data)
    return y


def normalize(data, norm=True):
    data = np.array(data)
    std = data.std()
    data = np.array(data - data.mean())
    if data.std() < 10e-9 or not norm:
        return 0 * data
    return data / std


def smoothed(data):
    data = np.array(data)
    return np.array(savgol_filter(data, window_length=50, polyorder=2))


class DataProProcessor:
    def __init__(self, path):
        self.Tn = None
        self.path = path
        self.maxU = None
        self.slip = None
        self.fs = None
        self.data = self.read_data(path)
        self.column = self.data.columns
        self.length = len(self.data[self.column[0]])
        self.splitNum = 0

    def read_data(self, path):
        # 使用 pandas 读取整个文件
        with open(path, 'r') as f:
            lines = f.readlines()

        line2 = lines[1]

        match2 = re.search(r'U\s*=\s*([\d.]+)\s*V\s*Slip\s*=\s*([\d.]+)\s*dt\s*=\s*([\d.]+)\s*s', line2)
        if match2:
            self.maxU = float(match2.group(1))
            self.slip = float(match2.group(2))
            self.fs = int(1 / float(match2.group(3)))

        if match2 is None:
            data = pd.read_csv(io.StringIO('\n'.join(lines)))
            self.fs = 50000
        else:
            data = pd.read_csv(io.StringIO('\n'.join(lines[2:])))
        data = data.drop(data.columns[0], axis=1)
        return data

    def hpf(self, data, f0):
        return np.array(highpass_filter(data, f0, self.fs))

    def nsf(self, data, f0):
        if f0 >= self.fs / 2:
            return data
        b, a = iirnotch(f0, 30, self.fs)
        return filtfilt(b, a, data)

    #fre_imp to adjust min Term
    def getTermPeaks(self, column=None, fre_imp=0.9, minF=30, maxF=40):
        data = None
        if column is None:
            if self.slip is None:
                column = self.column[0]
            else:
                column = self.column[1]
            data = self.data[column]
        # find F
        data = normalize(data)
        data[np.abs(data) > 2] = np.where(data[np.abs(data) > 2] > 0, 2, -2)
        n_data = normalize(data)
        max_fre = 120
        peaks_freq = np.array([])
        fhd = ffted(n_data, True)[(minF * self.length) // self.fs:(maxF * self.length) // self.fs]
        while (peaks_freq.size == 0):
            if max_fre == 0:
                peaks_freq = np.array([self.length * minF // self.fs])
                break
            peaks_freq = find_peaks(fhd, height=max_fre)[0] + (minF * self.length) // self.fs
            max_fre -= 10
        Tn = self.length / peaks_freq.min()
        self.Tn = Tn
        # Tn = min(max(minTn, Tn), maxTn)

        # smooth to find peaks
        sm_data = normalize(smoothed(n_data))
        peaks = find_peaks(-sm_data, distance=Tn * fre_imp, prominence=0.1, height=0)[0]
        return peaks

    def getT(self, peakList, mode=1):
        diff = np.diff(peakList)
        value, count = np.unique(diff, return_counts=True)
        T = [400, 400, 400]
        T[0] = diff.mean()
        T[1] = value[np.argmax(count)]
        T[2] = np.median(diff)
        return T[mode]

    def BeNormalize(self, isNoised=False, std=0.01):
        for i in self.column:
            if isNoised:
                self.data[i] += np.random.normal(0, std, self.data[i].shape)
            self.data[i] = normalize(self.data[i], "speed" not in i)

    def Beflited(self):
        for i in self.column:
            if "torque" in i or "speed" in i:
                continue
            self.data[i] = self.hpf(self.data[i], 180)
            for j in range(1, 4):
                self.data[i] = self.nsf(self.data[i], 60 * j)
                self.data[i] = self.nsf(self.data[i], 50 * j)

    def BeReshape(self, data, peakList, T):
        T = int(T)
        data = np.array(data)
        result = []
        if (data.shape[0] > T + np.diff(peakList).std()):
            for x in np.array_split(data, int(len(data) // T + 1)):
                self.splitNum += 1
                result.append(np.pad(x, ((0, T - x.shape[0]), (0, 0)), 'constant', constant_values=0))
        else:
            result.append(np.resize(data, (int(T), len(data[0]))))
        return result

    def beSliceX(self, peakList, T):
        peakList = np.array(peakList)
        peakList = peakList.tolist()
        peakList.insert(0, 0)
        slices = []
        for i in range(len(peakList) - 1):
            part = self.data[peakList[i]: peakList[i + 1]]
            slices += self.BeReshape(part, peakList, T)
        return np.array(slices)

    def reSample(self, slices, length, isNormalize=True):
        new_slices = []
        for slice in slices:
            slice = resample(np.array(slice), length)
            new_slices.append(slice)
        new_slices = np.array(new_slices)
        if isNormalize:
            # 初始化标准化后的数组
            data_standardized = np.zeros_like(new_slices)
            # 对每个二维切片进行标准化
            for i in range(new_slices.shape[2]):  # 遍历第三个维度的每个切片
                slice = new_slices[:, :, i]  # 取得二维切片
                mean = np.mean(slice)
                std = np.std(slice)
                # 标准化（避免除以零）
                if std > 0:
                    data_standardized[:, :, i] = (slice - mean) / std
                else:
                    data_standardized[:, :, i] = slice - mean
        return np.array(data_standardized)

    def markLabel(self, length):
        length = int(length)
        if "Normal" in self.path or "normal" in self.path:
            return np.repeat([0], length)
        if "bar" in self.path or "torque" in self.path:
            return np.repeat([1], length)
        else:
            return np.repeat([2], length)

    def PCA(self, Slices, isNormalize=True):
        pcaedList = []
        pca = PCA(n_components=6)
        # for i in range(len(self.column)):
        #     plot(np.array(Slices)[:, :, i].T)
        for slice in Slices:
            if self.slip is not None:
                slice += self.slip
            pcaed = np.array(pca.fit_transform(slice))
            if isNormalize:
                pcaed = (pcaed - pcaed.mean(axis=1, keepdims=True)) / pcaed.std(axis=1, keepdims=True)
            pcaedList.append(pcaed)
        pcaedList = np.array(pcaedList)
        return pcaedList

    def changeFS(self, Slices, T, newfs=5000):
        b = newfs / self.fs
        newSlices = []
        for slice in Slices:
            newSlices.append(resample(slice, int(b * T)))
        return np.array(newSlices)

    def mainProcess(self, isLabeledData=False, isSave=False, isPrint=False, isNoised=False, std=0.01, isPCAnom=True,
                    minTweight=20, seqLength=128):
        """
        :param isLabeledData: 是否生成标注数据,如果一个csv文件路径中包括(Normal, normal)则可生成对应label{0} 若没有则检测其csv路径是否包含(bar, torque)则生成对应的label{1} 若都没有则生成label{2} 该模式在训练数据可完全标注正确，在其他情况则不能正确标注，若要使用请符合上述特征
        :param isSave:  是否保存为.npy
        :param isPrint:  是否打印具体细节
        :param isNoised: 是否加入噪声
        :param std: 噪声方差
        :param isPCAnom: PCA后是否再进行标准化
        :param minTweight: 可接受的最小序列长度超参数
        :param seqLength: 生成序列的长度
        :return: [XdataSet, YdataSet]
        """
        YdataSet = None
        start_time = time.time()
        t = self.length / self.fs
        self.Beflited()
        peakList = self.getTermPeaks()
        T = self.getT(peakList)
        T = min(self.fs // minTweight, T)
        self.BeNormalize(isNoised, std)
        XdataSet = self.PCA(self.reSample(self.changeFS(self.beSliceX(peakList, T), T), seqLength, isPCAnom)).astype(
            dtype=np.float32)
        if isLabeledData:
            YdataSet = self.markLabel(len(XdataSet)).astype(np.int8)
        if isPrint:
            print("File Path:", self.path)
            print("Most T:", T)
            print("T Predict:", self.Tn)
            print("Split Num:", self.splitNum)
            print("time per Slices:", t / XdataSet.shape[0])
            print("Slices Shape:", XdataSet.shape)
            print("Cost Time:", time.time() - start_time)
        if isSave:
            np.save(self.path + "X", XdataSet)
            if isLabeledData:
                np.save(self.path + "Y", YdataSet)
        return [XdataSet, YdataSet]


if __name__ == '__main__':
    for path in find_all_csv_files("数据/仿真数据"):
        dpp = DataProProcessor(path)
        dpp.mainProcess(isSave=True, isPrint=True, isNoised=False)
