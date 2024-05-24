import yfinance as yf
nasdaq_data = yf.download('^IXIC', start='2020-01-01', end='2023-10-01')
your_array = nasdaq_data.Close.values
sum = 0
times = 0
for i in your_array:
    if times < 30 or i < (sum/times) :
        times += 1
        sum += i
print((your_array[-1]*times)/sum)
