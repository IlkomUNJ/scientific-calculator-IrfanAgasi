package com.example.scientificcalculator

import java.util.*
import kotlin.math.*

class CalculatorCalc {
    companion object {
        private val operators = mapOf(
            "+" to 1,
            "-" to 1,
            "×" to 2,
            "÷" to 2,
            "^" to 3,
            "%" to 4,
            "!" to 5
        )

        private fun isOperator(token: String) = operators.containsKey(token)
        private fun precedence(op: String) = operators[op] ?: -1

        private fun applyOperator(op: String, b: Double, a: Double): Double {
            return when (op) {
                "+" -> a + b
                "-" -> a - b
                "×" -> a * b
                "÷" -> a / b
                "%" -> a * (b / 100)
                "^" -> a.pow(b)
                else -> throw IllegalArgumentException("Unknown operator $op")
            }
        }

        private fun factorial(n: Double): Double {
            if (n < 0) throw IllegalArgumentException("Factorial of negative number")
            if (n % 1 != 0.0) throw IllegalArgumentException("Factorial of non-integer")
            return (1..n.toInt()).fold(1.0) { acc, i -> acc * i }
        }

        private fun applyFunction(func: String, value: Double, isDegree: Boolean): Double {
            return when (func) {
                "sqrt" -> sqrt(value)
                "log" -> log10(value)
                "ln" -> ln(value)
                "sin" -> if (isDegree) sin(Math.toRadians(value)) else sin(value)
                "cos" -> if (isDegree) cos(Math.toRadians(value)) else cos(value)
                "tan" -> if (isDegree) tan(Math.toRadians(value)) else tan(value)
                "arcsin" -> if (isDegree) Math.toDegrees(asin(value)) else asin(value)
                "arccos" -> if (isDegree) Math.toDegrees(acos(value)) else acos(value)
                "arctan" -> if (isDegree) Math.toDegrees(atan(value)) else atan(value)
                "1/x" -> 1 / value
                else -> throw IllegalArgumentException("Unknown function $func")
            }
        }

        fun evaluate(expression: String, isDegree: Boolean = true): String {
            return try {
                val normalized = expression.replace("/", "÷")
                val tokens = tokenize(normalized)
                val rpn = infixToPostfix(tokens)
                val result = evalPostfix(rpn, isDegree)

                if (result.isFinite()) {
                    if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
                } else {
                    if (result.isNaN()) "NaN" else "Infinity"
                }

            } catch (e: Exception) {
                "Error"
            }
        }

        private fun evalPostfix(tokens: List<String>, isDegree: Boolean): Double {
            val stack = Stack<Double>()

            for (token in tokens) {
                when {
                    token.matches(Regex("""-?\d+(\.\d+)?""")) -> stack.push(token.toDouble())
                    isOperator(token) -> {
                        when (token) {
                            "%" -> stack.push(stack.pop() / 100)
                            "!" -> stack.push(factorial(stack.pop()))
                            else -> {
                                val b = stack.pop()
                                val a = stack.pop()
                                stack.push(applyOperator(token, b, a))
                            }
                        }
                    }
                    token in listOf("sqrt","log","ln","sin","cos","tan","arcsin","arccos","arctan","1/x") -> {
                        val a = stack.pop()
                        stack.push(applyFunction(token, a, isDegree))
                    }
                    else -> throw IllegalArgumentException("Unknown token $token")
                }
            }
            if (stack.size != 1) throw IllegalStateException("Invalid expression")
            return stack.pop()
        }

        private fun tokenize(expr: String): List<String> {
            val tokens = mutableListOf<String>()
            var i = 0
            while (i < expr.length) {
                val char = expr[i]

                when {
                    expr.substring(i).startsWith("sqrt") -> { tokens.add("sqrt"); i += 4 }
                    expr.substring(i).startsWith("log") -> { tokens.add("log"); i += 3 }
                    expr.substring(i).startsWith("ln") -> { tokens.add("ln"); i += 2 }
                    expr.substring(i).startsWith("sin") -> { tokens.add("sin"); i += 3 }
                    expr.substring(i).startsWith("cos") -> { tokens.add("cos"); i += 3 }
                    expr.substring(i).startsWith("tan") -> { tokens.add("tan"); i += 3 }
                    expr.substring(i).startsWith("arcsin") -> { tokens.add("arcsin"); i += 6 }
                    expr.substring(i).startsWith("arccos") -> { tokens.add("arccos"); i += 6 }
                    expr.substring(i).startsWith("arctan") -> { tokens.add("arctan"); i += 6 }
                    expr.substring(i).startsWith("1/x") -> { tokens.add("1/x"); i += 3 }

                    char.isDigit() || char == '.' || (char == '-' && (i == 0 || expr[i-1] == '(' || isOperator(expr[i-1].toString()))) -> {
                        var start = i
                        if (char == '-') {
                            i++
                        }

                        while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                            i++
                        }
                        tokens.add(expr.substring(start, i))
                    }

                    char.toString() in listOf("+", "×", "÷", "%", "^", "!", "(", ")") -> {
                        tokens.add(char.toString())
                        i++
                    }

                    char == '-' -> {
                        tokens.add(char.toString())
                        i++
                    }

                    else -> throw IllegalArgumentException("Invalid character in expression: $char")
                }
            }
            return tokens
        }

        private fun infixToPostfix(tokens: List<String>): List<String> {
            val output = mutableListOf<String>()
            val stack = Stack<String>()

            for (token in tokens) {
                when {
                    token.matches(Regex("""-?\d+(\.\d+)?""")) -> output.add(token)

                    token in listOf("sqrt","log","ln","sin","cos","tan","arcsin","arccos","arctan","1/x") -> stack.push(token)

                    isOperator(token) -> {
                        while (stack.isNotEmpty() && isOperator(stack.peek()) && precedence(stack.peek()) >= precedence(token)) {
                            output.add(stack.pop())
                        }
                        stack.push(token)
                    }

                    token == "(" -> stack.push(token)

                    token == ")" -> {
                        while (stack.isNotEmpty() && stack.peek() != "(") output.add(stack.pop())
                        if (stack.isNotEmpty() && stack.peek() == "(") stack.pop()

                        if (stack.isNotEmpty() && stack.peek() in listOf("sqrt","log","ln","sin","cos","tan","arcsin","arccos","arctan","1/x")) {
                            output.add(stack.pop())
                        }
                    }
                }
            }

            while (stack.isNotEmpty()) {
                if (stack.peek() == "(") throw IllegalArgumentException("Mismatched parenthesis")
                output.add(stack.pop())
            }
            return output
        }
    }
}