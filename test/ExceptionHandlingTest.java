package tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlingTest {

    @Test
    void shouldThrowArithmeticException() {
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        }, "Деление на ноль должно приводить к исключению.");
    }

    @Test
    void shouldNotThrowException() {
        assertDoesNotThrow(() -> {
            int result = 10 / 2;
        }, "Этот код не должен выбрасывать исключений.");
    }
}