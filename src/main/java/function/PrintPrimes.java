package function;

public class PrintPrimes {
    private final int numberOfPrimes = 1000;
    private final int linesPerPage = 50;
    private final int columns = 4;

    public void main(String[] args) {
        PrimeGenerator primeGenerator = new PrimeGenerator();
        int[] primes = primeGenerator.generatePrimes(numberOfPrimes);
        new NumberPrinter(linesPerPage, columns).print(primes, numberOfPrimes);
    }

}
