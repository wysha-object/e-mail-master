package cn.com.wysha.e_mail_master.valid;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class DomainValidTests {
    @Test
    public void validatorTest(){
        String[] valid = new String[]{
                "google.com.hk",
                "github.com",
                "www.yeah.net",
                "quic.nginx.org",
                "nginx.org"
        };
        String[] invalid = new String[]{
                ".google.com",
                "xx hub.com",
                "www##.net",
                "q!x.org",
                "ng\norg"
        };

        TestObj testObj = new TestObj();

        for (String s : valid){
            testObj.domain = s;
            Assert.isTrue(
                    Validation.buildDefaultValidatorFactory().getValidator().validate(testObj).isEmpty(),
                    ""
            );
        }
        for (String s : invalid){
            testObj.domain = s;
            Assert.isTrue(
                    !Validation.buildDefaultValidatorFactory().getValidator().validate(testObj).isEmpty(),
                    ""
            );
        }
    }

    private static class TestObj {
        @DomainValid
        String domain;
    }
}
