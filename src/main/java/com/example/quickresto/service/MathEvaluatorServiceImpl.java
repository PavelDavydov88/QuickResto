package com.example.quickresto.service;

import com.example.quickresto.model.Cell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MathEvaluatorServiceImpl implements MathEvaluatorService {

    private final Cell[][] table = new Cell[4][4];

    private boolean isCellReference(String token) {
        return token.matches("[A-Za-z]+\\d+");
    }


    private String[] parseCellReference(String token) {
        return token.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private double applyOperator(String operator, double operand1, double operand2) {
        switch (operator) {
            case "+" -> {
                return operand1 + operand2;
            }
            case "-" -> {
                return operand1 - operand2;
            }
            case "*" -> {
                return operand1 * operand2;
            }
            case "/" -> {
                if (operand2 != 0) {
                    return operand1 / operand2;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private String evaluatePostfix(List<String> tokens) {
        try {
            Deque<String> stack = new ArrayDeque<>();
            for (String token : tokens) {
                if (isOperator(token)) {
                    double operand2 = Double.parseDouble(stack.pop());
                    double operand1 = Double.parseDouble(stack.pop());
                    double result = applyOperator(token, operand1, operand2);
                    stack.push(String.valueOf(result));
                } else {
                    stack.push(token);
                }
            }
            return stack.pop();
        } catch (Exception e) {
            throw new ArithmeticException("Unknown operand");
        }

    }

    private int getPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> 0;
        };
    }

    private String evaluateFormula(String formula, int rowIndex, int colIndex) {
        List<String> tokens = Arrays.asList(formula.split(" "));
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if (isCellReference(token)) {
                String[] coordinates = parseCellReference(token);
                int row = Integer.parseInt(coordinates[1]) - 1;
                int col = coordinates[0].toUpperCase().charAt(0) - 'A';
                if (row == rowIndex && col == colIndex) {
                    throw new RuntimeException("Circular reference detected at cell: " + token);
                }
                String cellValue = evaluateFormula(table[row][col].getValue(), row, col);
                output.add(cellValue);
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && isOperator(stack.peek()) && getPrecedence(token) <= getPrecedence(stack.peek())) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else {
                output.add(token);
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return evaluatePostfix(output);
    }

    @Override
    public Cell[][] getTable() {
        return table;
    }

    @Override
    public void updateCell(int row, int col, String value) {
        try {
            table[row][col] = new Cell(value);
            log.info("input value: " + value);
            StringBuilder text = new StringBuilder(table[row][col].getValue());
            if (!text.isEmpty() && text.charAt(0) == '=') {
                text.delete(0, 0);
                String patternString = "\\([^()]*\\)";
                Pattern pattern = Pattern.compile(patternString);
                while (text.indexOf("(") != -1 && text.indexOf(")") != -1) {
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        String bracketFragment = matcher.group();
                        text = new StringBuilder(matcher.replaceFirst(evaluateFormula(bracketFragment.substring(1, bracketFragment.length() - 1), row, col)));
                    } else {
                        log.info("No bracket fragments found.");
                    }
                }
                String result = evaluateFormula(text.toString(), row, col);
                table[row][col] = new Cell(result);
            }
        } catch (Exception e) {
            table[row][col] = new Cell("Error " + e.getMessage());
        }

    }
}
