#include <iostream>
#include <string>
#include <ctime>
#include <vector>
#include <thread>

int n = 10;
int DEFAULT_THREADS = 2;
long double sum = 0;
std::vector<unsigned long> calculatedFactorials;
std::vector<long double> threadsSum;

long double factorial(int n)
{
	calculatedFactorials[0] = 1;
	if (n == 0)
		return 1;
	if (calculatedFactorials[n] != 0)
		return calculatedFactorials[n];
	else
	{
		calculatedFactorials[n] = n * factorial(n - 1);
	}

	return calculatedFactorials[n];
}

void calc(int threadCount, int threadI, int threadWork)
{
	for (long double i = threadI; i <= n; i += threadCount)
	{
		long double nom = 2 * i + 1;
		long double denom = factorial(2 * i);
		threadsSum[threadI] += nom / denom;
	}
	std::cout << "thread number " << threadI + 1 << " calculated sum=" << threadsSum[threadI] << "\n";
}

int main() 
{
	calculatedFactorials.resize(120000);
	int threadCount = DEFAULT_THREADS;
	threadsSum.resize(threadCount);
	std::vector<std::thread> threads;
	auto startTime = std::chrono::high_resolution_clock::now();

	std::cout << "Main thread started.\nNumber of threads to be started: " << threadCount << "\n";
	for (int i = 0; i < threadCount; ++i)
	{
		std::cout << "Thread number " << i + 1 << " is starting\n";
		threads.push_back(std::thread(calc,threadCount, i, n/threadCount));
	}

	for (int i = 0; i < threadCount; i++)
	{
		threads[i].join();
	}

	long double e = 0.0;
	for (int i = 0; i < threadCount; i++)
	{
		e = e + threadsSum[i];
	}
	std::cout << e;
	auto endTime = std::chrono::high_resolution_clock::now();
	auto duration = endTime - startTime;
	std::cout << "\nMain thread ended.\nTotal execution time was ";
	std:: cout << std::chrono::duration<double, std::milli>(endTime - startTime).count();
	std::cout << std::endl;

	return system("pause");
}