#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# 1.
# Вх: список строк, Возвр: кол-во строк
# где строка > 2 символов и первый символ == последнему

def me(slist):
    return len(list(filter(lambda x: len(x) > 2 and x[0] == x[-1], slist)))


# 2.
# Вх: список строк, Возвр: список со строками (упорядочено)
# за искл всех строк начинающихся с 'x', которые попадают в начало списка.
# ['tix', 'xyz', 'apple', 'xacadu', 'aabbbccc'] -> ['xacadu', 'xyz', 'aabbbccc', 'apple', 'tix']
def fx(words):
    return list(sorted(words, key=lambda s: (len(s) != 0 and s[0] != 'x', s)));


# 3.
# Вх: список непустых кортежей,
# Возвр: список сортир по возрастанию последнего элемента в каждом корт.
# [(1, 7), (1, 3), (3, 4, 5), (2, 2)] -> [(2, 2), (1, 3), (3, 4, 5), (1, 7)]
def tu(tuples):
    tuples.sort(key=lambda x: x[-1])
    return tuples

def test(f, data, expected):
    print("in:\n", data);
    res = f(data);
    print("out:\n", res);
    print("expected:\n", expected);
    print("passed" if res == expected else "failed");
    print("");

def main():
    test(me, ["aasda", "aa", "bb", "caa", "abc", "aaa", "bbb", "ccc"], 4)
    test(fx, ['tix', 'xyz', 'apple', 'xacadu', 'aabbbccc'], ['xacadu', 'xyz', 'aabbbccc', 'apple', 'tix'])
    test(tu, [(1, 7), (1, 3), (3, 4, 5), (2, 2)], [(2, 2), (1, 3), (3, 4, 5), (1, 7)]);
    return

if __name__ == '__main__':
    main()
