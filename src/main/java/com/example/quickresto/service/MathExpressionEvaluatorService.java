package com.example.quickresto.service;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathExpressionEvaluatorService {

    public static int evaluate(String expression) {
        char[] tokens = expression.toCharArray();

        // Стек для операторов и операндов
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            // Пропускаем пробелы
            if (tokens[i] == ' ') {
                continue;
            }

            // Если текущий символ - цифра, получаем полное число
            if (Character.isDigit(tokens[i])) {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length && (Character.isDigit(tokens[i]) || tokens[i] == '.')) {
                    sb.append(tokens[i++]);
                }
                values.push(Integer.parseInt(sb.toString()));
            }

            // Если текущий символ - открывающая скобка, помещаем ее в стек операторов
            else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            }

            // Если текущий символ - закрывающая скобка, вычисляем все внутри скобок
            else if (tokens[i] == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Убираем '(' из стека операторов
            }

            // Если текущий символ - оператор, вычисляем предыдущие операторы, если необходимо
            else if (isOperator(tokens[i])) {
                while (!operators.isEmpty() && hasPrecedence(tokens[i], operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(tokens[i]);
            }
        }

        // Вычисляем оставшиеся операторы
        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        // Результат выражения находится в values
        return values.pop();
    }

    private static boolean isOperator(char token) {
        return token == '+' || token == '-' || token == '*' || token == '/';
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        return (op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-');
    }

    private static int applyOperator(char operator, int b, int a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    public static void main(String[] args) {
//        String expression = "=2 * ( 3 + 5 ) / 4 - 6";
        String input = "Some text (with (nested) brackets) and (another fragment)";
        StringBuilder result = new StringBuilder(input);
        String patternString = "\\([^()]*\\)";
        Pattern pattern = Pattern.compile(patternString);
        while (result.indexOf("(") != -1 ) {
            Matcher matcher = pattern.matcher(result);

            if (matcher.find()) {
                String bracketFragment = matcher.group();
                result = new StringBuilder(matcher.replaceFirst("5"));
                int fragmentLength = bracketFragment.length();

                System.out.println("Found bracket fragment: " + bracketFragment);
                System.out.println("Length of the fragment: " + fragmentLength);
                System.out.println("Result after removal: " + result);
            } else {
                System.out.println("No bracket fragments found.");
            }
        }


//        String expression = "= ( 2 + 3 ) * 4 - 30 / 5";
//        int result = evaluate(expression);
//        System.out.println("Result: " + result);
    }
}

