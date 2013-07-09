package function;

class NumberPrinter {
    private int[] numbers;
    private int numberOfPrimes;
    private int linesPerPage;
    private int columns;

    public NumberPrinter(int[] numbers, int numberOfPrimes, int linesPerPage, int columns) {
        this.numbers = numbers;
        this.numberOfPrimes = numberOfPrimes;
        this.linesPerPage = linesPerPage;
        this.columns = columns;
    }

    public void invoke() {
        int pagenumber = 1;
        int pageoffset = 1;
        while (pageoffset <= numberOfPrimes) {
            System.out.println("The First " + numberOfPrimes +
                    " Prime Numbers --- Page " + pagenumber);
            System.out.println("");
            for (int rowoffset = pageoffset; rowoffset < pageoffset + linesPerPage; rowoffset++) {
                for (int column = 0; column < columns; column++)
                    if (rowoffset + column * linesPerPage <= numberOfPrimes)
                        System.out.format("%10d", numbers[rowoffset + column * linesPerPage]);
                System.out.println("");
            }
            System.out.println("\f");
            pagenumber = pagenumber + 1;
            pageoffset = pageoffset + linesPerPage * columns;
        }
    }
}
