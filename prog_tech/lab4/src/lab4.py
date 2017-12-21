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
        print(f'No such file: "{filename}".')
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
