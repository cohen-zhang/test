package com.zz.app;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author zz
 * 用于各种简单的测试验证
 */
public class AppTest {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    @Test
    public void testApp() {
        App app = new App();
        String result = app.sayHello();
        assertEquals("Hello, World!", result);
    }
}
