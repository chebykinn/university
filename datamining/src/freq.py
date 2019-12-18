#!/usr/bin/python

import operator
import sys

from src.utils import get_lemma_tokens, get_open_files

tf_texts = []
corpus = []


for file in get_open_files(sys.argv):
    text = file.read()

    corpus.append(get_lemma_tokens(text))

map = {}

for document in corpus:
    for word in document:
        if word not in map.keys():
            map[word] = 1
        else:
            map[word] = map[word] + 1

print(sorted(map.items(), key=operator.itemgetter(1), reverse=True))