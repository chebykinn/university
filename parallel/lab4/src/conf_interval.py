import numpy as np
import scipy.stats


def mean_confidence_interval(data, confidence=0.95):
    a = 1.0 * np.array(data)
    n = len(a)
    m, se = np.mean(a), scipy.stats.sem(a)
    h = se * scipy.stats.t.ppf((1 + confidence) / 2., n-1)
    return m, m-h, m+h

print(mean_confidence_interval([
    5.867710,
    5.210982,
    7.319177,
    5.468525,
    6.209804,
    5.842089,
    5.930700,
    6.526367,
    4.636695,
    8.345919,
]))
