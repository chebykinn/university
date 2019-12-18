import os

import spacy
import regex


sp = spacy.load('en_core_web_sm')

pos_filter = ['ADP', 'DET', 'NUM', 'PRON', 'PUNCT', 'SYM', 'X', 'SPACE', 'CCONJ']

REGEX = regex.compile(r'[^\p{L}]')


def get_lemma_tokens(text):
    sentence = sp(text)
    tokens = list()
    for s in sentence:
        if s.pos_ not in pos_filter:
            token = REGEX.sub('', s.lemma_.lower())
            if token:
                tokens.append(token)
    return tokens


def get_open_files(argv):
	if len(argv) < 2 or not os.path.isdir(argv[1]):
		print(argv[0] + " <directory>")
		raise SystemExit()

	os.chdir(argv[1])
	for file in os.listdir('.'):
		if not os.path.isfile(file):
			continue

		f = open(file, "r")
		yield f
