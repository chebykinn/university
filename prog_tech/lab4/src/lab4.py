"""
Прочитать из файла (имя - параметр командной строки) все слова (разделитель - пробел)

Создать "похожий" словарь, отображающий каждое слово из файла на список всех слов, которые следуют за ним (все варианты).

Список слов может быть в любом порядке и включать повторения, например "and" ["best", "then", "after", "then", ...]

Считаем, что пустая строка предшествует всем словам в файле.

С помощью "похожего" словаря сгенерировать новый текст похожий на оригинал.
Т.е. напечатать слово - посмотреть какое может быть следующим и выбрать случайное.

В качестве теста можно использовать вывод программы как вход.парам. для следующей копии (для первой вход.парам. - файл)

Файл:
He is not what he should be
He is not what he need to be
But at least he is not what he used to be
  (c) Team Coach
"""

import random
import sys
import os


def word_dict(file):
    d = {}
    words = file.read().split()
    for i, word in enumerate(words[:-1]):
        next_word = words[i + 1]
        try:
            d[word].append(next_word)
        except KeyError:
            d[word] = [next_word]
    return d


def main():
    if len(sys.argv) < 2:
        print('usage: python', os.path.basename(__file__), 'filename')
        return
    try:
        with open(sys.argv[1]) as file:
            d = word_dict(file)
    except FileNotFoundError:
        print(f'No such file exists: "{filename}".')
        sys.exit(1)
    current_word = random.choice(list(d.keys()))
    while True:
        print(current_word, end=' ')
        try:
            current_word = random.choice(d[current_word])
        except KeyError:
            break


if __name__ == '__main__':
    main()
