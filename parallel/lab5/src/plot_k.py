#!/usr/bin/env python3

ks = [
    0.8920469,
    0.8873421,
    0.8881582,
    0.8912074,
    0.9443383,
    0.9333607,
    0.9246792,
]

with open('amdall.csv', 'w+') as f:
    print('p,s14,s15,s16,s17,s18,s19,s20', file=f)
    for p in range(1, 9):
        ss = []
        for k in ks:
            s = 1 / ((k / p) + (1 - k))
            ss.append(s)
        s_list = ','.join(map(str, ss))
        print('{},{}'.format(p, s_list), file=f)


