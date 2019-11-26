#!/usr/bin/env python3
import matplotlib.pyplot as plt
import pandas as pd
import sys

data = pd.read_csv(sys.argv[1], sep=',', index_col=0)
data2 = data[:-1]

fig = plt.figure()
ax1 = fig.add_subplot(211)
ax2 = fig.add_subplot(212)

plt.ylabel('t, ms')
plt.xlabel('N')
plt.title(sys.argv[2])
data.plot(kind='bar', ax=ax1)

plt.ylabel('t, ms')
plt.xlabel('N')
plt.title(sys.argv[2])
data2.plot(kind='bar', ax=ax2)

plt.subplots_adjust(hspace=0.5)

plt.savefig('{}.png'.format(sys.argv[2]))
# plt.show()
