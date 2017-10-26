#!/usr/bin/env python3

import re
import sys

def extr_name(filename):
    """
    Вход: nameYYYY.html, Выход: список начинается с года, продолжается имя-ранг в алфавитном порядке.
    '2006', 'Aaliyah 91', Aaron 57', 'Abagail 895', ' и т.д.
    """
    m = re.search(r"name(?P<year>[0-9]{4})", filename)
    if not m: return []
    year = m.group("year");

    list = []

    with open(filename) as f:
        for line in f:
            match = re.search(r"^<tr[^>]*><td>(?P<rank>.*?)</td><td>(?P<male>.*?)</td><td>(?P<female>.*?)</td>$", line)
            if not match: continue
            rank = match.group("rank")
            male = match.group("male")
            female = match.group("female")
            list.append(male + " " + rank)
            list.append(female + " " + rank)

    list.sort()
    list.insert(0, year)
    # for i in list:
        # print(i)
    return list


def main():
    args = sys.argv[1:]
    if not args:
        print('use: [--file] file [file ...]')
        sys.exit(1)
    total_list = []
    for i in args:
        list = extr_name(i)
        if not list: continue
        list.pop(0)
        for name in list:
            print(name.split()[0])

        total_list.extend(list)
    total_list.sort(key=lambda x:int(x.split()[-1]))

    for i in total_list:
        if int(i.split()[-1]) > 10: break
        print(i)

  # для каждого переданного аргументом имени файла, вывести имена  extr_name

  # напечатать ТОП-10 муж и жен имен из всех переданных файлов


if __name__ == '__main__':
    main()
