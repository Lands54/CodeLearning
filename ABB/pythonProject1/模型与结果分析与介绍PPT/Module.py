import random
import DataPreProcess
import numpy as np
import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.utils.rnn as rnn_utils

# 运行环境 Anaconda 主要外部库torch

def build_mat(X, Y, mat):
    for i in range(len(X)):
        mat[Y[i]][X[i]] += 1

class LSTMModel(nn.Module):
    def __init__(self, input_size, hidden_size, output_size, num_layers=1):
        super(LSTMModel, self).__init__()
        self.hidden_size = hidden_size
        self.num_layers = num_layers

        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)
        self.fc = nn.Linear(hidden_size, output_size)

    def forward(self, x, lengths):
        h_0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size, dtype=torch.float32).to(x.device)
        c_0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size, dtype=torch.float32).to(x.device)

        packed_input = rnn_utils.pack_padded_sequence(x, lengths, batch_first=True, enforce_sorted=False)
        packed_out, _ = self.lstm(packed_input, (h_0, c_0))

        out, _ = rnn_utils.pad_packed_sequence(packed_out, batch_first=True)
        out = self.fc(out[torch.arange(out.size(0)), lengths - 1, :])
        return out


def train_and_save_model(x_train, y_train, x_test, y_test, batch_size, num_epochs, learning_rate, model_save_path):
    # 计算每个序列的长度
    lengths_train = torch.tensor([len(seq) for seq in x_train], dtype=torch.long)
    lengths_test = torch.tensor([len(seq) for seq in x_test], dtype=torch.long)

    # 将数据转换为张量
    padded_sequences_train = rnn_utils.pad_sequence([torch.tensor(seq, dtype=torch.float32) for seq in x_train],
                                                    batch_first=True)
    padded_sequences_test = rnn_utils.pad_sequence([torch.tensor(seq, dtype=torch.float32) for seq in x_test],
                                                   batch_first=True)

    labels_train = torch.tensor(y_train, dtype=torch.long)
    labels_test = torch.tensor(y_test, dtype=torch.long)

    # 创建数据加载器
    train_dataset = torch.utils.data.TensorDataset(padded_sequences_train, labels_train, lengths_train)
    train_loader = torch.utils.data.DataLoader(dataset=train_dataset, batch_size=batch_size, shuffle=True)

    test_dataset = torch.utils.data.TensorDataset(padded_sequences_test, labels_test, lengths_test)
    test_loader = torch.utils.data.DataLoader(dataset=test_dataset, batch_size=batch_size, shuffle=False)

    # 模型参数
    input_size = padded_sequences_train.size(2)  # 输入特征维度
    hidden_size = 32
    output_size = 3 # 类别数量
    num_layers = 2

    # 创建模型
    model = LSTMModel(input_size=input_size, hidden_size=hidden_size, output_size=output_size, num_layers=num_layers)
    model.load_state_dict(torch.load(model_save_path))
    # 检查是否有可用的 GPU
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    print(f'Using device: {device}')

    model = model.to(device)

    # 定义损失函数和优化器
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=learning_rate)

    #训练模型
    for epoch in range(num_epochs):
        model.train()
        running_loss = 0.0
        correct = 0
        total = 0
        mat = np.zeros((3, 3))
        for inputs, labels, lengths in train_loader:
            inputs, labels, lengths = inputs.to(device), labels.to(device), lengths.to(device)

            # 前向传播
            outputs = model(inputs, lengths)
            loss = criterion(outputs, labels)
            # 反向传播和优化
            optimizer.zero_grad()
            loss.backward()
            optimizer.step()

            running_loss += loss.item()
            _, predicted = torch.max(outputs.data, 1)
            build_mat(predicted, labels, mat)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()

        print(mat)
        epoch_loss = running_loss / len(train_loader)
        epoch_accuracy = correct / (total + 10e-9)
        print(f'Epoch [{epoch + 1}/{num_epochs}], Loss: {epoch_loss:.4f}, Accuracy: {epoch_accuracy:.4f}')
        torch.save(model.state_dict(), model_save_path)
        print(f'Model saved to {model_save_path}')
    # 测试模型
    # model.eval()
    # testresult = []
    # mat = np.zeros((3, 3))
    # for inputs, labels, lengths in test_loader:
    #     inputs, labels, lengths = inputs.to(device), labels.to(device), lengths.to(device)
    #     outputs = model(inputs, lengths)
    #     softmax = nn.Softmax(dim=1)
    #     p = softmax(outputs)
    #     result = torch.argmax(p, dim=1)
    #     testresult.append([result, labels])
    #     build_mat(result, labels, mat)
    # with open("TestResult", 'wb') as f:
    #     pickle.dump(testresult, f)
    # print(testresult)
    # 保存模型



if __name__ == '__main__':
    np.set_printoptions(precision=8, suppress=True)
    for i in range(1000):
        name = DataPreProcess.find_all_csv_files('数据')
        x_train = []
        y_train = []
        for path in name:
            dp = DataPreProcess.DataProProcessor(path)
            std = max((random.uniform(-0.500, 0.500)), 0)
            data = dp.mainProcess(isSave=False, isNoised=False, std=std)
            x_train += data[0].tolist()
            y_train += data[1].tolist()
        n = 1000
        indices = np.arange(len(x_train))
        selected_indices = np.random.choice(indices, size=n, replace=False)
        np.sort(selected_indices)
        # 从数据集中删除这些索引
        TestSetX = [x_train[i] for i in selected_indices]
        TestSetY = [y_train[i] for i in selected_indices]

        remaining_indices = np.delete(indices, selected_indices)

        TrainSetX = x_train
        TrainSetY = y_train
        # with open("TrainTest(X, Y)", 'wb') as f:
        #     pickle.dump([TrainSetX, TrainSetY, TestSetX, TestSetY], f)
        # print("Data Saved")
        # dat = []
        # with open("TrainTest(X, Y)", 'rb') as f:
        #     dat = pickle.load(f)
        # TrainSetX = dat[0]
        # TrainSetY = dat[1]
        # TestSetX = dat[2]
        # TestSetY = dat[3]
        # print("Data Loaded:", np.array(TrainSetX).shape)
        train_and_save_model(
            x_train=TrainSetX,
            y_train=TrainSetY,
            x_test=TestSetX,
            y_test=TestSetY,
            batch_size=32,
            num_epochs=1,
            learning_rate=0.0001,
            model_save_path='trainHistort/lstmfinal.pth'
        )