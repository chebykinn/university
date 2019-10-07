#include <float.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>

const static int c_a = 567;
const static int c_experiments = 50;

void generate(unsigned int seed, double *p, unsigned int N,
              unsigned int min, unsigned int max) {
    unsigned int i;
    for (i = 0; i < N; i++) {
        p[i] = (rand_r(&seed) % max) + min;
    }
}

void lab_swap(double * lhs, double * rhs) {
    double tmp = *lhs;
    *lhs = *rhs;
    *rhs = tmp;
}

void gnome_sort(double *arr, int n) {
    int i;
    for (i = 0; i + 1 < n; i++) {
        if (arr[i] > arr[i + 1]) {
            lab_swap(&arr[i], &arr[i + 1]);
            if (i != 0) i -= 2;
        }
    }
}

double lab_abs(double v) {
    if (v < 0) {
        return -v;
    }
    return v;
}

double lab_min(double lhs, double rhs) {
    return lhs > rhs ? rhs : lhs;
}

double lab_max(double lhs, double rhs) {
    return lhs < rhs ? rhs : lhs;
}

double lab_cot(double val) {
    return cos(val) / sin(val);
}

double lab_coth(double val) {
    return cosh(val) / sinh(val);
}

void print_array(double *p, unsigned int N) {
    unsigned int i = 0;
    for (i = 0; i < N - 1; i++) {
        printf("%f ", p[i]);
    }
    printf("%f\n", p[N - 1]);
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        return -1;
    }

    struct timeval begin, end;
    const int N = atoi(argv[1]);
    if (N < 0) {
        return -2;
    }

    double * m1 = malloc(sizeof(double) * N);
    double * m2 = malloc(sizeof(double) * N / 2);

    gettimeofday(&begin, NULL);
    double reduced_sum = 0.0;
    unsigned int i;
    for (i = 0; i < c_experiments; i++) {
        // 1. Generate: M1 of N elements, M2 of N/2 elements
        generate(i, m1, N, 1, c_a);
            //puts("M1");
            //print_array(m1, N);
        generate(i, m2, N / 2, c_a, 10 * c_a);
            //puts("M2");
            //print_array(m2, N / 2);
        // 2. Map: coth(sqrt(M1[j])) ; M2[j] = abs(cot(M2[j]))
        unsigned int j;
        for (j = 0; j < N; j++) {
            m1[j] = lab_coth(sqrt(m1[j]));
        }
            //puts("M1 coth");
            //print_array(m1, N);
        //m2[0] = lab_abs(cot(m2[0] [> + 0.0 <]));
        for (j = 0; j < N / 2; j++) {
            m2[j] = lab_abs(lab_cot(m2[j]));
        }
            //puts("M2 abs cot");
            //print_array(m2, N / 2);
        // 3. Merge: M2[j] = max(M1[j], M2[j]) , j e N/2
        for (j = 0; j < N / 2; j++) {
            m2[j] = lab_max(m1[j], m2[j]);
        }
            //puts("max of M1 M2");
            //print_array(m2, N / 2);
        // 4. Sort: gnome_sort(M2, N/2)
        gnome_sort(m2, N/2);
            //puts("sorted");
            //print_array(m2, N / 2);
        // 5. Reduce: 1. min_non_zero(M2)
        //            2. if (((long)(M2[i] / min_non_zero)) & ~(1))
        //                   sum += sin(M2[i])
        double min_non_zero = DBL_MAX;
        for (j = 0; j < N / 2; j++) {
            if (m2[j] != 0) {
                min_non_zero = lab_min(min_non_zero, m2[j]);
            }
        }
            //printf("Min non zero: %f\n", min_non_zero);
        for (j = 0; j < N / 2; j++) {
            if (((long)(m2[j] / min_non_zero)) & ~(1)) {
                reduced_sum += sin(m2[j]);
            }
        }
            //printf("Sum: %e\n", reduced_sum);
    }
    gettimeofday(&end, NULL);
    long delta_ms = 1000 * (end.tv_sec - begin.tv_sec) + (end.tv_usec - begin.tv_usec) / 1000;
    printf("N = %d. milliseconds passed: %ld\n", N, delta_ms);
    printf("N = %d. X=%e\n", N, reduced_sum / c_experiments);

    return 0;
}

