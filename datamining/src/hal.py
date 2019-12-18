import sys

import math
from scipy.sparse import lil_matrix
import operator
# sorting map
import numpy as np
# local imports
from sklearn.metrics.pairwise import cosine_similarity

from src.utils import get_open_files, get_lemma_tokens

WINDOW_SIZE = 5
LOG_BASE = 2
TOP_WORD_OCCUR = 3000
TOP_RESULTS = 10

file_to_tokens = {}

for file in get_open_files(sys.argv):
    text = file.read()
    file_to_tokens[file.name] = get_lemma_tokens(text)

############################ METHODS


# build scipy sparse matrix which represents ccoocurencest matrix
def build_sparse_matrix(voc, indexes):
	voc_size = len(voc)
	mat = lil_matrix((voc_size, voc_size), dtype=float)
	for key, value in voc.items():
		for key2, value2 in value.items():
			mat[indexes[key], indexes[key2]] = value2
	return mat


# compute cosine similarity between pivot matrix and all others words matrix
def compute_results(indexes, context_word, sparse_matrix):
	# idnex of pivot word in sparse matrix
	ind = indexes[context_word]
	# convert sparse matrix row to long array for pivot
	mat = sparse_matrix[:, ind]
	results = {}
	# iterate through vocabulary
	for key, ind2 in indexes.items():
		# convert sparse matrix row to long array for current item
		mat2 = sparse_matrix[:, ind2]
		results[key] = cosine_similarity(np.transpose(mat), np.transpose(mat2), dense_output=False)
	return results


# select only TOP most frequented words
def select_top_occurs(train_data, occ):
	for i in range(len(train_data)):
		for j in range(len(train_data[i])):
			if train_data[i][j] in occ:
				occ[train_data[i][j]] += 1
			else:
				occ[train_data[i][j]] = 1

	sorted_occ = sorted(occ.items(), key=operator.itemgetter(1), reverse=True)
	sorted_occ = sorted_occ[0:TOP_WORD_OCCUR]
	sorted_occ = dict(sorted_occ)
	smaller_data = []
	for i in range(len(train_data)):
		reduced_sentence = []
		for j in range(len(train_data[i])):
			if train_data[i][j] in sorted_occ:
				reduced_sentence.append(train_data[i][j])
		if len(reduced_sentence) > 0:
			smaller_data.append(reduced_sentence)
	occ = sorted_occ
	train_data = smaller_data
	return train_data


# print results
def print_results(res):
	# convert data from sparse matrix into single var
	for key, value in res.items():
		res[key] = value.toarray().flatten()[0]
	top_results = sorted(res.items(), key=operator.itemgetter(1), reverse=True)
	top_results = top_results[1:TOP_RESULTS+1]
	for i in range(len(top_results)):
		print("('%s'," % top_results[i][0] + " %0.3f), " % top_results[i][1], end='')
	print()


# find counts of neighbours for building coocurency matrix
def run_hal(train_data):
	for i in range(len(train_data)):
		sentence_size = len(train_data[i])
		for j in range(sentence_size):
			key = train_data[i][j]
			# compute start of sliding window
			start_ind = 0
			if j - WINDOW_SIZE >= 0:
				start_ind = j - WINDOW_SIZE
			# compute end of sliding window
			end_ind = sentence_size
			if j + WINDOW_SIZE + 1 <= sentence_size:
				end_ind = j + WINDOW_SIZE + 1
			for index in range(start_ind, end_ind, 1):
				if j != index:
					weight = 1 / abs(j - index)
					idf = math.log((num_words / occ[train_data[i][index]]), LOG_BASE)
					if train_data[i][index] in voc[key]:
						voc[key][train_data[i][index]] += weight * idf
					else:
						voc[key][train_data[i][index]] = 0


############################ MAIN

for file_name, tokens in file_to_tokens.items():
	occ = {}
	train_data = select_top_occurs([tokens], occ)
	# create vocabulary and map of occurrences for computing IDF
	voc = {}
	indexes = {}
	counter = 0
	num_words = 0
	for i in range(len(train_data)):
		for j in range(len(train_data[i])):
			num_words += 1
			voc[train_data[i][j]] = {}
			if train_data[i][j] not in indexes:
				indexes[train_data[i][j]] = counter
				counter += 1
	# process HAL
	run_hal(train_data)
	# building sparse matrix - we save memory space
	sparse_matrix = build_sparse_matrix(voc, indexes)
	# compute cosine similarity for pivot
	context_word = 'god'
	res = compute_results(indexes, context_word, sparse_matrix)
	# print results
	print("{}: ".format(file_name), end='')
	print_results(res)