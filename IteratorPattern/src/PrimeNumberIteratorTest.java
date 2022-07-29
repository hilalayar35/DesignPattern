import java.util.*;

public class PrimeNumberIteratorTest {

	public static void main(String[] args) {
		int primeNumberCount = 1000;

		// first iteration
		System.out.println("First iteration");
		System.out.println("---------------");
		long startTime = System.nanoTime();
		PrimeNumbers primeNumbers = new PrimeNumbers(primeNumberCount);
		for (Long primeNumber : primeNumbers) {
			System.out.println(primeNumber);
		}
		/*
		 * Iterator<Long> iter = primeNumbers.iterator(); while (iter.hasNext()) {
		 * System.out.println(iter.next()); }
		 */
		long elapsedTime1 = System.nanoTime() - startTime;
		System.out.println();

		// second iteration
		System.out.println("Second iteration");
		System.out.println("----------------");
		startTime = System.nanoTime();
		for (Long primeNumber : primeNumbers) {
			System.out.println(primeNumber);
		}
		long elapsedTime2 = System.nanoTime() - startTime;
		System.out.println();

		System.out.println("Execution time for the first iteration  = " + (elapsedTime1 / 1000000000.0) + " seconds");
		System.out.println("Execution time for the second iteration = " + (elapsedTime2 / 1000000000.0) + " seconds");

		// if you comment System.out.println(primeNumber); in for loops then second
		// execution will be much faster compared to first one
	}
}

// do not create prime numbers initially as an array. since you will iterate over it keeping all in memory is not wise.
// instead create prime numbers on-demand (in each next() method call). this is the essence of Iterator Patterns
class PrimeNumbers implements Iterable<Long> {
	// n is the number of first prime numbers, if n = 10 then iterator will produce 2, 3, 5, 7, 11, 13, 17, 19, 23, and 29 
	private final long n;

	PrimeNumbers(long n) {
		this.n = n;
	}

	@Override
	public Iterator<Long> iterator() {
		// you should create a new instance so that each iterator can run independently (i.e. in different threads)
		return new PrimeNumberIterator();
	}

	// using inner-class for Iterator is a best-practice
	class PrimeNumberIterator implements Iterator<Long> {
		private int currentIndex = 0;
		private long currentNumber = 2;

		@Override
		public boolean hasNext() {
			return (currentIndex < n);
		}

		@Override
		public Long next() {
			if (hasNext())
				return getNextPrimeNumber();
			else
				return null;
		}

		private Long getNextPrimeNumber() {
			while (!isPrime(currentNumber)) {
				currentNumber++;
			}
			long primeNumber = currentNumber;
			
			currentNumber++;
			currentIndex++;
			
			return primeNumber;
		}

		// there are more efficient algorithms to find prime. below method is simple yet quite efficient
		// you can also use simple for loop to detect if a number is prime (aim of this homework is applying Iterator Pattern not finding prime numbers efficiently)
		private boolean isPrime(long number) {
			// 2 and 3 are special cases
			if (number <= 3) {
				return true;
			}

			// (number & 1) == 0 is same as (number % 2) == 0
			if ((number & 1) == 0 || number % 3 == 0) {
				return false;
			}

			for (int i = 5; i * i <= number; i += 6) {
				if ((number % i) == 0 || (number % (i + 2)) == 0) {
					return false;
				}
			}
			return true;
		}

	}

}

