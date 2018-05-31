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
        
        // 이 부분부터 작성하고 위는 자동 완성을 활용한다.
        // given-when-then에서 then부터 작성
        String line;
        while((line = gold.readLine()) != null)
            assertEquals(line, lead.readLine());
        assertEquals(null, lead.readLine());
    }
}
```

# 1. Extract Method Object - PrimePrinterHelper

![Alt text](https://monosnap.com/image/UbIPpLeQGjxK3hXlF9UDfI0MnOapSb.png)

- 일련의 변수들을 갖는 큰 하나의 함수이다.
- 이런 경우 하나 이상의 클래스가 숨어 있는 것이다.
- PrimePrinterHelper는 적합한 이름은 아니다.
- 후에 코드를 정제하면 더 적합한 이름이 떠 오를 것이다. 그때 변경하자.

# 2. remove static

![Alt text](https://monosnap.com/image/zSP44D8y5GfK1Debbig4rXQRjzc2F6.png)

![Alt text](https://monosnap.com/image/84XRynhl7tQBs73rJAHokEyQFy8CyE.png)

- static/singleton은 global 변수와 같다
- 의존성 관리가 어렵다.

# 3. extract field

![Alt text](https://monosnap.com/image/oYaucotE0o5OniKXxxBPzx1nE6BsCP.png)

- code block을 extract method로 추출하여 함수를 작게 만들기 전에 함수에 여러곳에서 사용되는 변수들을 필드로 추출한다.
- 이렇게함으로써
	- 추출된 메소드들의 parameter 개수를 최소화할 수 있고
	- 코드 블록을 메소드로 추출시 2개 이상의 변수가 변경되어 IDE에 의한 자동 추출이 안되는 것을 방지할 수 있다.
- 후에 필드로 존재할 필요가 없으면 로컬 변수로 변경할 수 있다.

# 4. extract method - printNumbers

![Alt text](https://monosnap.com/image/Wb3e0J5JohIhflYx30SK0I1IDBeESg.png)

- prime number를 계산하는 부분과 이쁘게 리포트를 출력하는 부분으로 나뉜다.
- 리포트를 출력하는 부분을 printNumbers로 추출

# 5. convert fields to local variables or parameters

![Alt text](https://monosnap.com/image/udE7NWSDK1Uv4oY4FCd6Pn8ESiWlC5.png)

- printNumbers에서 사용하는 필드 변수들 중 그 메소드에서만 사용되는 필드 변수들에 대해
	- 파라미터 처리가 필요없는 변수들은 로컬 변수로: `pagenumber, pageoffset, linesPerPage, columns, rowoffset, column`
	- 파라미터 처리가 필요한 변수들은 파라미터로: `numberOfPrimes, primes)`
    	- 파라미터 처리는 intellij의 extract parameter이용. 그러면 호출하는 곳도 알아서 수정해 줌
    	- 추출할 때 "Replace all occurrences" 옵션을 선택하여 모든 경우에 적용되도록 한다.
        
       ![Alt text](https://monosnap.com/image/AxPjuGd7D0aGEcy0d6rF6AmPcpXNjh.png) 
        
- intelliJ에서 필드 변수들은 보라색으로 되어 쉽게 필드 변수들에 대한 처리가 가능
- 이 사전 작업을 안 하면 method object(NumberPrinter)의 constructor에 원본 객체(PrimePrinterHelper)가 파라미터로 전달된다.
	- 원본 객체에 정의된 필드 변수들을 method object에서 사용해야 하므로
- PrintPrimeHelper에 정의된 필드 중 printNumbers에서만 사용하는 필드 변수들이 제거되었다(cohesion 상승)

# 6. rename parameter `primes` to `numbers`
- printNumbers에 전달되는 파라미터이니 numbers가 보다 적절한 이름이다.

# 7. change invoke to return prime numbers
- invoke 메소드를 void에서 primes를 반환하는 int []로 리터 타입을 변경한다(CQS 준수).
- invoke에서 printNumbers를 호출하지 말고 main(client)에서 printNumbers를 호출하도록 변경한다.
	- 소수 생성과 소수 출력은 다른 책임이니...

![Alt text](https://monosnap.com/image/SiJUY4ScNrm7uoL77W3fTrehAyZYh6.png)

![Alt text](https://monosnap.com/image/o6nHfUQWUgc01BlYxGBq6orA9NvEir.png)

# 8. extract method object - NumberPrinter

![Alt text](https://monosnap.com/image/YVCsE1eUrkugphelQhAqBlqxMQ90tL.png)

# 9. Move inner to upper level - NumberPrinter
- Move inner to upper level를 두번 실행해서 NumberPrinter를 별도의 클래스로 분리

# 10. change numberOfPrimes, numbers as invoke's parameters instead of constructor's parameters
- numbers, numberOfPrimes는 printNumbers가 호출될때마다 변경 가능한 값이다.
- numbers, numberOfPrimes가 달라졌다고 NumberPrinter를 매번 새로 만드는 것은 비합리적이다.
- numbers, numberOfPrimes를 NumberPrinter의 생성자 파라미터에서 invoke 메소드의 파라미터로 변경한다.

# 11. inline PrintPrimeHelper#printNumbers
- printNumbers에서 inline을 수행하면 PrimePrinterHelper#printNumbers는 자동으로 삭제되고
- PrintPrimes#main에서 PrimePrinterHelper#printNumbers의 내용이 inline된다.

![Alt text](https://monosnap.com/image/8Kc01sTUsve0XyKaV6wNq7WvzbaM3w.png)

![Alt text](https://monosnap.com/image/hsiabjc57ggtnZ2xuAkv8EPMG7U3ct.png)

# 12. rename PrintPrimeHelper to PrimeGenerator, invoke to generatePrimeNumbers
- 의미있는 이름이 생각났으므로... 이름을 변경

![Alt text](https://monosnap.com/image/s0FhiIw4WgfsDBic6GBgz0yjfp7zV1.png)

# 13. prepare parameterize numberOfPrimes
- numberOfPrimes는 PrintPrimes에 정의된 필드이다.
- PrimeGenerator를 Move inner to upper level을 수행하여 PrimeGenerator.java로 추출하면
- PrimeGenerator가 PrintPrimes에 의존성을 갖는 문제가 생긴다.
- PrintPrimes의 numberOfPrimes를 직접사용하지 않고 numberOfPrimes를 파라미터로 처리하여 이 의존성을 제거해야한다.
- 사전작업으로 numberOfPrimes를 선언때 사용하지 않고 메소드 내에서 사용하도록 변경한다.

![Alt text](https://monosnap.com/image/vn9e5uritmaFJSutYMR27yRFoFBSDZ.png)

# 14. extract method - findNextPrime

![Alt text](https://monosnap.com/image/NTFdE9MqJ8uU5IF46NFX0OwU8ehvxM.png)

# 15. Move inner to upper level - PrimeGenerator

# 19. rename meaningfully
- `NumberPrinter#invoke` -> `print`