#!/usr/bin/env python3

import re

# 1.
# Вх: строка. Если длина > 3, добавить в конец "ing",
# если в конце нет уже "ing", иначе добавить "ly".
def v(s):
    if re.match(r".+ing$", s):
        return s + "ly"
    if re.match(r".{4,}$", s):
        return s + "ing"
    return s

# 2.
# Вх: строка. Заменить подстроку от 'not' до 'bad'. ('bad' после 'not')
# на 'good'.
# Пример: So 'This music is not so bad!' -> This music is good!

def nb(s):
    return re.sub(r"\bnot\b.*?\bbad\b", "good", s)


def main():
    print("Test v")
    print(v("t"))
    print(v("te"))
    print(v("tes"))
    print(v("test"))
    print(v("ing"))
    print(v("testing"))
    print(v("ingly"))
    print(v("lyly"))
    print("Test nb")
    print(nb("This music is not so bad!"))
    print(nb("words not words words bad bad bad another bad"))
    print(nb("bad not bad not bad not bad"))
    print(nb("notbad nottestbad not test bad"))

if __name__ == '__main__':
    main()
