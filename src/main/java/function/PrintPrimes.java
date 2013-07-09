package function;

public class PrintPrimes {
    public void main(String[] args) {
        new PrimePrinterHelper().invoke();
    }

    private class PrimePrinterHelper {
        private final int numberOfPrimes = 1000;
        private final int linesPerPage = 50;
        private final int columns = 4;
        private final int ordmax = 30;
        private final int[] primes = new int[numberOfPrimes + 1];
        private int pagenumber;
        private int pageoffset;
        private int rowoffset;
        private int column;
        private int candidate;
        private int primeIndex;
        private boolean possiblyPrime;
        private int ord;
        private int square;
        private int n;
        private int[] multiples;

        private void invoke() {
            multiples = new int[ordmax + 1];
            candidate = 1;
            primeIndex = 1;
            primes[1] = 2;
            ord = 2;
            square = 9;
            while (primeIndex < numberOfPrimes) {
                do {
                    candidate = candidate + 2;
                    if (candidate == square) {
                        ord = ord + 1;
                        square = primes[ord] * primes[ord];
                        multiples[ord - 1] = candidate;
                    }
                    n = 2;
                    possiblyPrime = true;
                    while (n < ord && possiblyPrime) {
                        while (multiples[n] < candidate)
                            multiples[n] = multiples[n] + primes[n] + primes[n];
                        if (multiples[n] == candidate)
                            possiblyPrime = false;
                        n = n + 1;
                    }
                } while (!possiblyPrime);
                primeIndex = primeIndex + 1;
                primes[primeIndex] = candidate;
            }
            {
                pagenumber = 1;
                pageoffset = 1;
                while (pageoffset <= numberOfPrimes) {
                    System.out.println("The First " + numberOfPrimes +
                            " Prime Numbers --- Page " + pagenumber);
                    System.out.println("");
                    for (rowoffset = pageoffset; rowoffset < pageoffset + linesPerPage; rowoffset++) {
                        for (column = 0; column < columns; column++)
                            if (rowoffset + column * linesPerPage <= numberOfPrimes)
                                System.out.format("%10d", primes[rowoffset + column * linesPerPage]);
                        System.out.println("");
                    }
                    System.out.println("\f");
                    pagenumber = pagenumber + 1;
                    pageoffset = pageoffset + linesPerPage * columns;
                }
            }
        }
    }
}
