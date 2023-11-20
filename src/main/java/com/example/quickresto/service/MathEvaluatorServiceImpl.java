package com.example.quickresto.service;

import com.example.quickresto.model.Cell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сервиса расчета значения ячейки
 */
@Slf4j
@Service
public class MathEvaluatorServiceImpl implements MathEvaluatorService {

    private final Cell[][] table = new Cell[4][4];

    /**
     * Метод возращает таблицу с ячейками
     *
     * @return двумерный массив элементов Cell
     */
    @Override
    public Cell[][] getTable() {
        return table;
    }

    /**
     * Метод обновления значения ячейки
     *
     * @param row   значение ряда таблицы
     * @param col   значение столбца таблицы
     * @param value новое значение ячейки
     */
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

    /**
     * Метод проверки ссылки на ячейку
     *
     * @param token элемент проверки
     * @return true если проверяемый элемент является ссылкой на ячейку
     */
    private boolean isCellReference(String token) {
        return token.matches("[A-Za-z]+\\d+");
    }

    /**
     * Метод вычисления координат ячейки
     *
     * @param token элемент для парсинга
     * @return массив из двух элементов, с координатами ряда и столбца
     */
    private String[] parseCellReference(String token) {
        return token.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    }

    /**
     * Метод проверки на арифметический знак
     *
     * @param token элемент для проверки
     * @return true если элемент является арифметическим знаком
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    /**
     * Метод выполнения арифметической операции
     *
     * @param operator арифметический знак
     * @param operand1 операнд
     * @param operand2 операнд
     * @return значение арифметической операции
     */
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

    /**
     * Метод последовательного рачсета математического выражения
     *
     * @param tokens последовательный список из арифметических знаков и операндов
     * @return значение расчета математического выражения
     */
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

    /**
     * Метод определения приоритера арифметической операции
     *
     * @param operator арифметический знак
     * @return численный приоритет
     */
    private int getPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> 0;
        };
    }

    /**
     * Метод для обработки формулы математического выражения
     *
     * @param formula  формула математического выражения
     * @param rowIndex ряд ячейки с формулой
     * @param colIndex колонка ячейки с формулой
     * @return значение расчета формулы математического выражения
     */
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
}
