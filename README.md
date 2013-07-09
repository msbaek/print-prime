# print-prime

- Clean Coders Episode 3 Functions PrintPrimes Example
- 이 예제는 복잡한 함수를 2개의 클래스로 Extract Method Object하는 과정입니다.
- master branch에는 제가 수행한 리팩토링이 있습니다.
- 여러분이 직접 해 보시려면 try-yourself branch에서 해 보실 수 있습니다.

# 0. Add Characterization Test
```
public class PrintPrimesTest {
    private PrintStream out;

    @Before
    public void setUp() throws FileNotFoundException {
        out = System.out;
        System.setOut(new PrintStream(new FileOutputStream("lead")));
    }

    @After
    public void tearDown() {
        System.setOut(out);
        new File("lead").delete();
    }

    @Test
    public void
    makeSureMatchesGold() throws IOException {
        new PrintPrimes().main(new String[0]);
        BufferedReader lead = new BufferedReader(new FileReader("lead"));
        BufferedReader gold = new BufferedReader(new FileReader("src/test/java/function/gold"));
        String line;
        while((line = gold.readLine()) != null)
            assertEquals(line, lead.readLine());
        assertEquals(null, lead.readLine());
    }
}
```

# 1. Extract Method Object - PrimePrinterHelper

- 일련의 변수들을 갖는 큰 하나의 함수이다.
- 이런 경우 하나 이상의 클래스가 숨어 있는 것이다.
- PrimePrinterHelper는 적합한 이름은 아니다.
- 후에 코드를 정제하면 더 적합한 이름이 떠 오를 것이다. 그때 변경하자.

# 2. remove static

- static/singleton은 global 변수와 같다
- 의존성 관리가 어렵다.

# 3. extract field

- code block을 extract method로 추출하여 함수를 작게 만들기 전에 함수에 여러곳에서 사용되는 변수들을 필드로 추출한다.
- 이렇게함으로써
	- 추출된 메소드들의 parameter 개수를 최소화할 수 있고
	- 코드 블록을 메소드로 추출시 2개 이상의 변수가 변경되어 IDE에 의한 자동 추출이 안되는 것을 방지할 수 있다.
- 후 필드로 존재할 필요가 없으면 로컬 변수로 변경할 수 있다.

# 4. extract method - printNumbers
- prime number를 계산하는 부분과 이쁘게 리포트를 출력하는 부분으로 나뉜다.

```
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
```

위 코드를 printNumbers로 추출

# 5. change signature printNumbers - add primes, numberOfPrimes as parameters

- 생성된 소수들을 출력하는 printNumbers가 호출될때마다 변경될 수 있는
	- primes(생성된 소수)
	- numberOfPrimes(생성된 소수의 개수)
- 는 파라미터로 처리한다.
- IDE의 extract parameter를 이용한다. 추출할 때 "Replace all occurrences" 옵션을 선택하여 모든 경우에 적용되도록 한다.

# 6. rename parameter primes to numbers
- printNumbers에 전달되는 파라미터이니 numbers가 보다 적절한 이름이다.

# 7. change invoke to return prime numbers
- invoke 메소드를 void에서 primes를 반환하는 int []로 리터 타입을 변경한다.
- invoke에서 printNumbers를 호출하지 말고 main(client)에서 printNumbers를 호출하도록 변경한다.
	- 소수 생성과 소수 출력은 다른 책임이니...

- main 함수가 아래와 같아지도록

```
        PrimePrinterHelper primePrinterHelper = new PrimePrinterHelper();
        int[] primes = primePrinterHelper.invoke();
        primePrinterHelper.printNumbers(primes, numberOfPrimes);
```

# 8. prepare extract method object
- 대상 메소드(printNumbers)에서 사용하는 필드 변수들 중 그 메소드에서만 사용되는 필드 변수들에 대해
	- 파라미터 처리가 필요없는 변수들은 로컬 변수로: 
	- 파라미터 처리가 필요한 변수들은 파라미터로: `linesPerPage, columns`
- intelliJ에서 필드 변수들은 보라색으로 되어 쉽게 필드 변수들에 대한 처리가 가능
- 이 사전 작업을 안 하면 method object(NumberPrinter)의 constructor에 원본 객체(PrimePrinterHelper)가 파라미터로 전달된다.
	- 원본 객체에 정의된 필드 변수들을 method object에서 사용해야 하므로
	
# 9. prepare extract method object
- linesPerPage, columns는 printNumbers에서만 사용된다.
- 이 필드들은 후에 printNumbers가 별도의 클래스로 추출되면 PrimePrinterHelper에 있을 필요가 없다.
- 이 필드들을 PrintPrimes로 이동시킨다.

# 10. extract method object - NumberPrinter

# 11. Move inner to upper level - NumberPrinter
- Move inner to upper level를 실행해서 NumberPrinter를 PrimePrinterHelper의 inner class에서 레벨업해서 PrintPrimes의 inner class로 옮긴다.

# 12. change numbers, numberOfPrimes as invoke's parameters
- numbers, numberOfPrimes는 printNumbers가 호출될때마다 변경 가능한 값이다.
- numbers, numberOfPrimes가 달라졌다고 NumberPrinter를 매번 새로 만드는 것은 비합리적이다.
- numbers, numberOfPrimes를 NumberPrinter의 생성자 파라미터에서 invoke 메소드의 파라미터로 변경한다.

# 13. inline primePrinterHelper.printNumbers
- printNumbers에서 inline을 수행하면 PrimePrinterHelper#printNumbers는 자동으로 삭제되고
- PrintPrimes#main에서 PrimePrinterHelper#printNumbers의 내용이 inline된다.

# 14. rename PrimePrinterHelper to PrimeGenerator
- 의미있는 이름이 생각났으므로... 이름을 변경

# 15. prepare parameterize numberOfPrimes
- numberOfPrimes는 PrintPrimes에 정의된 필드이다.
- PrimeGenerator를 Move inner to upper level을 수행하여 PrimeGenerator.java로 추출하면
- PrimeGenerator가 PrintPrimes에 의존성을 갖는 문제가 생긴다.
- PrintPrimes의 numberOfPrimes를 직접사용하지 않고 numberOfPrimes를 파라미터로 처리하여 이 의존성을 제거해야한다.
- 사전작업으로 numberOfPrimes를 선언때 사용하지 않고 메소드 내에서 사용하도록 변경한다.

# 16. parameterize numberOfPrimes

# 17. extract method - findNextPrime

# 18. Move inner to upper level - PrimeGenerator
- 이 리팩토링을 수행하기 전에 invoke 메소드의 scope을 public으로 변경한다.

# 19. rename meaningfully
- `NumberPrinter#invoke` -> `print`
- `PrimeGenerater#invoke` -> `generatePrimes`