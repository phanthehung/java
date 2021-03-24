package com.example.purchase.Util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;


public class RandomStringGeneratorTest {

    @InjectMocks
    private RandomStringGenerator generator;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }


    @ParameterizedTest
    @MethodSource
    public void testGenerateString(int targetStringLength) {
        String actual = generator.generateString(targetStringLength);
        Assertions.assertEquals(targetStringLength, actual.length());
    }

    private static List<Integer> testGenerateString() {
        return Arrays.asList(5, 7, 2, 8, 3, 56);
    }
}
