#!/usr/bin/env python3

ks = [
    0.000388,
    0.000095,
    0.000001,
]

with open('amdall.csv', 'w+') as f:
    print('p,s14,s17,s20', file=f)
    for p in range(1, 9):
        ss = []
        for k in ks:
            s = 1 / ((k / p) + (1 - k))
            ss.append(s)
        s_list = ','.join(map(str, ss))
        print('{},{}'.format(p, s_list), file=f)


