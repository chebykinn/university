#!/usr/bin/env python3

import sys

import matplotlib.pyplot as plt
import networkx as nx
import numpy as np
from numpy import genfromtxt

data = genfromtxt(sys.argv[1], delimiter=' ')
print(data)


def show_graph(adjacency_matrix):
    rows, cols = np.where(adjacency_matrix == 1)
    edges = zip(rows.tolist(), cols.tolist())
    gr = nx.Graph()
    gr.add_edges_from(edges)
    nx.draw(gr, node_size=500, with_labels=False)
    plt.show()

show_graph(data)
