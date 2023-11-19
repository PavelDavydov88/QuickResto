package com.example.quickresto.service;

import com.example.quickresto.model.Cell;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MathEvaluatorServiceImplTest {

    MathEvaluatorService mathEvaluatorService = new MathEvaluatorServiceImpl();

    Cell[][] table;
    @BeforeEach
    void beforeAll() {
      table = new Cell[4][4];
    }

    @DisplayName("check that cell is reference")
    @ParameterizedTest
    @CsvSource({
            "'123', false",
            "'A1', true",
    })
    void isCellReference(String token, boolean exceptedValue) {
        Assertions.assertEquals(exceptedValue, mathEvaluatorService.isCellReference(token));
    }

    @Test
    void parseCellReference() {
    }

    @Test
    void isOperator() {
    }

    @Test
    void applyOperator() {
    }

    @Test
    void evaluatePostfix() {
    }

    @Test
    void getPrecedence() {
    }

    @Test
    void evaluateFormula() {
    }

    @Test
    void getTable() {
    }

    @Test
    void updateCell() {
    }
}