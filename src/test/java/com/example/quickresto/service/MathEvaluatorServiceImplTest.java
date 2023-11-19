package com.example.quickresto.service;

import com.example.quickresto.model.Cell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MathEvaluatorServiceImplTest {

    private MathEvaluatorService mathEvaluatorService;

    @BeforeEach
    void setUp() {
        mathEvaluatorService = new MathEvaluatorServiceImpl();
        mathEvaluatorService.updateCell(0, 0, "4");
        mathEvaluatorService.updateCell(0, 1, "6");
    }

    @DisplayName("check that the cell has been updated")
    @ParameterizedTest
    @CsvSource({
            "0, 0, '= 5 + 5', '10.0'",
            "0, 0, '= ( 5 + 5 ) * 5', '50.0'",
            "0, 0, '= ( 5 + 5 ) * ( 10 - 8 ) / 2 ', '10.0'",
            "0, 0, '= ( ( 5 + 5 ) * 2 - 1 ) * ( 10 - 8 ) / 2 ', '19.0'",
            "0, 2, '= ( ( A1 + b1 ) * 2 - 1 ) * ( 10 - 8 ) / 2 ', '19.0'",
    })
    void updateCell(int row, int col, String value, String result) {
        mathEvaluatorService.updateCell(row, col, value);
        Cell cell = mathEvaluatorService.getTable()[row][col];
        Assertions.assertEquals(result, cell.getValue());
    }

    @DisplayName("check that the cell will be with Error text")
    @ParameterizedTest
    @CsvSource({
            "0, 0, '= 5 + a', 'Error Unknown operand'",
    })
    void updateCellWithError(int row, int col, String value, String result) {
        mathEvaluatorService.updateCell(row, col, value);
        Cell cell = mathEvaluatorService.getTable()[row][col];
        Assertions.assertEquals(result, cell.getValue());
    }

    @DisplayName("check that the cell will be with text")
    @ParameterizedTest
    @CsvSource({
            "0, 0, ' ( 5 + 5 ) * 5', ' ( 5 + 5 ) * 5'",
    })
    void updateCellWithText(int row, int col, String value, String result) {
        mathEvaluatorService.updateCell(row, col, value);
        Cell cell = mathEvaluatorService.getTable()[row][col];
        Assertions.assertEquals(result, cell.getValue());
    }


}