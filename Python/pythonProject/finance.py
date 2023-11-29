import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import yfinance as yf
def update(frame):
    # 使用FFT进行变换
    fft_result = np.fft.fft(frames[frame, :])

    # 将FFT结果进行fftshift以在频率轴上对称显示
    shifted_fft_result = fft_result
    # 清空当前图表，准备下一帧的绘制
    plt.clf()

    # 绘制原始序列
    plt.subplot(2, 1, 1)
    plt.plot(frames[frame, :], color='blue')
    plt.title('Original Signal')
    plt.xlabel('Sample')
    plt.ylabel('Amplitude')

    # 绘制FFT结果图
    plt.subplot(2, 1, 2)
    plt.scatter(range(len(shifted_fft_result)),20 * np.log10(np.abs(shifted_fft_result)), color='orange')
    plt.title(f'FFT Result - Frame {frame + 1}')
    plt.ylim(0,200)
    plt.xlabel('Frequency Bin')
    plt.ylabel('Log Magnitude')

# 示例用法
nasdaq_data = yf.download('^IXIC', start='2012-01-01', end='2023-10-01')
your_array = nasdaq_data.Close.values
num_frames = 20
frame_size = len(your_array) // num_frames
hop_size = frame_size

# 初始化一个数组来存储每个帧的FFT结果
frames = np.empty((num_frames, frame_size), dtype=np.complex128)

# 分帧
for i in range(num_frames):
    start = i * hop_size
    end = start + frame_size
    frames[i, :] = your_array[start:end]

# 创建动画
fig, ax = plt.subplots(figsize=(8, 6))
animation = FuncAnimation(fig, update, frames=num_frames, interval=1000, repeat=True)

# 显示动画
plt.show()
# 示例用法

