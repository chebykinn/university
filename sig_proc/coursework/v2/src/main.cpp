#include <complex>
#include <cmath>
#include <vector>
#include <iostream>

using complex_d = std::complex<double>;

const double PI = 3.141592;

void dft(const std::vector<complex_d> &in, std::vector<complex_d> &out) {
	const size_t S = in.size();
	out.resize(S);

	for(size_t i = 0; i < S; i++) {
		out[i] = 0;
		for(size_t j = 0; j < S; j++) {
			out[i] += in[j] * std::polar<double>(1.0, - 2 * PI * i * j / S);
		}
	}
}

//void dft(const std::vector<complex_d> &input_seq, std::vector<complex_d> &output_seq) {
	//double pi2 = 2.0 * M_PI;
	//double angleTerm, cosineA, sineA;
	//size_t size = input_seq.size();
	//output_seq.resize(size);
	//double invs = 1.0 / size;
	//for(unsigned int y = 0;y < size;y++) {
		//output_seq[y] = 0;
		//for(unsigned int x = 0;x < size;x++) {
			//angleTerm = pi2 * y * x * invs;
			////output_seq[y].real += input_seq[x].real * cosineA - input_seq[x].imag * sineA;
			////output_seq[y].imag += input_seq[x].real * sineA + input_seq[x].imag * cosineA;
			//output_seq[y] += input_seq[x] * std::polar<double>(1.0, angleTerm);
		//}
		////output_seq[y].real *= invs;
		////output_seq[y].imag *= invs;
	//}
//}

int main(int argc, const char *argv[]) {

	std::vector<complex_d> in = {102023,102023,102023,102023};
	std::vector<complex_d> out;
	dft(in, out);
	for(auto const &i : out) {
		std::cout << i << std::endl;
	}
	//std::cout << "2" << std::endl;
	//out.clear();
	//dft(in, out);
	//for(auto const &i : out) {
		//std::cout << i << std::endl;
	//}

	return 0;
}
