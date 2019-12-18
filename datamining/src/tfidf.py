#!/usr/bin/python

import math
import operator
import sys

import collections

from src.utils import get_lemma_tokens, get_open_files

tf_texts = []


def compute_tfidf_by_files(corpus_by_files):
    def compute_tf(text):
        tf_text = collections.Counter(text)
        for i in tf_text:
            tf_text[i] = tf_text[i]/float(len(text))
        return tf_text

    def compute_idf(word, corpus):
        return math.log10(len(corpus)/sum([1.0 for i in corpus if word in i]))

    tf_idf_by_file = {}

    for name, text in corpus_by_files.items():
        tf_idf_dictionary = {}
        computed_tf = compute_tf(text)

        for word in computed_tf:
            tf_idf_dictionary[word] = computed_tf[word] * compute_idf(word, corpus_by_files.values())

        tf_idf_by_file[name] = tf_idf_dictionary

    return tf_idf_by_file


corpus_by_files = {}

for file in get_open_files(sys.argv):
    text = file.read()

    corpus_by_files[file.name] = get_lemma_tokens(text)

tf_idf_by_files = compute_tfidf_by_files(corpus_by_files)

for file, tf_idf in tf_idf_by_files.items():
    print("\nFILE {}".format(file))
    print(sorted(tf_idf.items(), key=operator.itemgetter(1), reverse=True))