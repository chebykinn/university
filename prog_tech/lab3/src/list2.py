#!/usr/bin/env python3

from functools import reduce
# 1.
# Вх: список чисел, Возвр: список чисел, где
# повторяющиеся числа урезаны до одного
# пример [0, 2, 2, 3] returns [0, 2, 3].

def rm_adj(nums):
    return reduce(lambda xs, x: xs if x in xs else xs + [x], nums, []);


# 2. Вх: Два списка упорядоченных по возрастанию, Возвр: новый отсортированный объединенный список
def merge(lst1, lst2):
    return sorted(lst1 + lst2);

def test_print(exp, res):
    print("expected:");
    print(exp);
    print("got:");
    print(res);

def test1():
    nums = [0, 0, 0, 0]
    exp  = [0]
    res = rm_adj(nums)
    test_print(exp, res)
    nums = [0, 2, 2, 3]
    exp  = [0, 2, 3]
    res = rm_adj(nums)
    test_print(exp, res)

def test2():
    lst1 = [0, 2, 4, 6]
    lst2 = [1, 3, 5]
    res = merge(lst1, lst2);
    exp = [0, 1, 2, 3, 4, 5, 6];
    test_print(exp, res);


def main():
    test1()
    test2()

if __name__ == '__main__':
  main()
