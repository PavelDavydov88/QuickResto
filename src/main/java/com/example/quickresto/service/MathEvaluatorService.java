package com.example.quickresto.service;

import com.example.quickresto.model.Cell;

import java.util.List;

public interface MathEvaluatorService {
    boolean isCellReference(String token);

    String[] parseCellReference(String token);

    boolean isOperator(String token);

    double applyOperator(String operator, double operand1, double operand2);

    String evaluatePostfix(List<String> tokens);

    int getPrecedence(String operator);

    String evaluateFormula(String formula, int rowIndex, int colIndex);

    Cell[][] getTable();

    void updateCell(int row, int col, String value);
}
