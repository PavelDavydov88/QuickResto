package com.example.quickresto.controller;

import com.example.quickresto.model.Cell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@Controller
public class TableController {

    //    private int rowIndex;
//    private int colIndex;
    private Cell[][] table = new Cell[4][4];

    @GetMapping("/table")
    public String getTable(Model model) {
        model.addAttribute("table", table);
        return "table";
    }

    @PostMapping("/updateCell/{row}/{col}")
    public String updateCell(@PathVariable int row, @PathVariable int col, @RequestParam String value) {
        table[row][col] = new Cell(value);
        log.info("input value: " + value);
        String formula = table[row][col].getValue();
        if (formula.charAt(0) == '=') {
            String result = evaluateFormula(formula.substring(1), row, col);
            table[row][col] = new Cell(result);
        }
        return "redirect:/table";
    }

    //    @PostMapping("/calculateTable")
    public String calculateTable(Model model) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] != null && table[i][j].getValue().startsWith("=")) {
                    try {
                        String formula = table[i][j].getValue().substring(1);
                        String result = evaluateFormula(formula, i, j);
                        table[i][j] = new Cell(result);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                        table[i][j] = new Cell("Error");
                    }
                }
            }
        }
        model.addAttribute("table", table);
        return "redirect:/table";
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

                // Проверяем, чтобы избежать зацикливания при саморекурсии
                if (row == rowIndex && col == colIndex) {
                    throw new RuntimeException("Circular reference detected at cell: " + token);
                }

                // Рекурсивно вычисляем значение ячейки
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

    private int getPrecedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> 0;
        };
    }

    private String evaluatePostfix(List<String> tokens) {
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
    }


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
}