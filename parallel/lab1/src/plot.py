import matplotlib.pyplot as plt
import pandas as pd

data = pd.read_csv('clang.csv', sep=',', index_col=0)

data.plot(kind='bar')
plt.ylabel('t, ms')
plt.xlabel('N')
plt.title('ICC')

plt.show()
