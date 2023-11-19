package com.example.quickresto.controller;

import com.example.quickresto.model.Cell;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

@Controller
public class TableControllerOld {

    private Cell[][] table = new Cell[4][4];

    @GetMapping("/tableOld")
    public String getTable(Model model) {
        table = Arrays.stream(table).map(s -> Arrays.stream(s).map(a -> (new Cell("1")))).toArray(Cell[][]::new);
        System.out.println();
        for (Cell[] row :table
             ) {
            for (Cell colomn: row
                 ) {
                System.out.print(colomn.getValue());

            }

        }
        System.out.println();
        model.addAttribute("table", table);
        return "tableOld";
    }

    @PostMapping("/updateCellOld/{row}/{col}")
    public String updateCell(@PathVariable int row, @PathVariable int col, @RequestParam String value) {
        table[row][col] = new Cell(value);
        return "redirect:/tableOld";
    }

    @PostMapping("/calculateTableOld")
    public String calculateTable(Model model) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] != null && table[i][j].getValue().startsWith("=")) {
                    try {
                        String formula = table[i][j].getValue().substring(1);
                        String result = evaluateFormula(formula);
                        table[i][j] = new Cell(result);
                    } catch (Exception e) {
                        table[i][j] = new Cell("Error");
                    }
                }
            }
        }
        model.addAttribute("table", table);
        return "tableOld";
    }

    private String evaluateFormula(String formula) {
        Deque<String> stack = new ArrayDeque<>();
        String[] tokens = formula.split(" ");
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

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private double applyOperator(String operator, double operand1, double operand2) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 != 0) {
                    return operand1 / operand2;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

}
