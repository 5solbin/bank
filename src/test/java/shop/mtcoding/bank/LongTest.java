package shop.mtcoding.bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongTest {

    @Test
    public void long_test() throws Exception{
        //given
        Long number1 = 1111L;
        Long number2 = 1111L;

        //when
        if (number1.longValue() == number2.longValue()) {
            System.out.println("테스트 : 동일합니다");
        } else {
            System.out.println("테스트 : 동일하지 않습니다");
        }

        //then
    }

    // 부등호를 쓸 때는 long value를 붙이지 않아도 되지만, 등호를 쓸때는 거의 long value를 붙여서 사용해야 한다.
    @Test
    public void long_test2() throws Exception{
        //given
        Long v1 = 100L;
        Long v2 = 200L;

        //when
        if (v1 < v2) {
            System.out.println("테스트 : v1이 작습니다.");
        }

        //then
    }

    @Test
    public void long_test3() throws Exception{
        //given
        Long v1 = 128L;
        Long v2 = 128L;

        //when

        //then
        assertThat(v1).isEqualTo(v2);
    }


}
