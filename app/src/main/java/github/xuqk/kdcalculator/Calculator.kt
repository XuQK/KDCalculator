package github.xuqk.kdcalculator

import java.math.BigDecimal
import java.util.Stack

@Throws(Exception::class)
private fun doubleCal(a1: String, a2: String, operator: Char): String {
    when (operator) {
        '+' -> return BigDecimal(a1).plus(BigDecimal(a2)).toString()
        '-' -> return BigDecimal(a1).minus(BigDecimal(a2)).toString()
        'x' -> return BigDecimal(a1).multiply(BigDecimal(a2)).toString()
        '÷' -> return BigDecimal(a1).divide(BigDecimal(a2)).toString()
        else -> {}
    }
    throw Exception("illegal operator!")
}

@Throws(java.lang.Exception::class)
private fun getPriority(s: String?): Int {
    if (s == null) return 0
    when (s) {
        "(" -> return 1
        "+" -> {
            return 2
        }
        "-" -> return 2
        "x" -> {
            return 3
        }
        "÷" -> return 3
        else -> {}
    }
    throw java.lang.Exception("illegal operator!")
}

@Throws(java.lang.Exception::class)
fun getResult(expr: List<String>): String {
    /*数字栈*/
    val number: Stack<String> = Stack<String>()
    /*符号栈*/
    val operator: Stack<String> = Stack<String>()
    // 在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断
    operator.push(null)

    expr.forEach { temp ->
        if (temp in calculateKeys) {
            when (temp) {
                "(" -> {
                    //遇到左括号，直接入符号栈
                    operator.push(temp)
                }
                ")" -> {
                    //遇到右括号，"符号栈弹栈取栈顶符号b，数字栈弹栈取栈顶数字a1，数字栈弹栈取栈顶数字a2，计算a2 b a1 ,将结果压入数字栈"，重复引号步骤至取栈顶为左括号，将左括号弹出
                    var b: String
                    while (operator.pop().also { b = it } != "(") {
                        val a1: String = number.pop()
                        val a2: String = number.pop()
                        number.push(doubleCal(a2, a1, b[0]))
                    }
                }
                else -> { //遇到运算符，满足该运算符的优先级大于栈顶元素的优先级压栈；否则计算后压栈
                    while (getPriority(temp) <= getPriority(operator.peek())) {
                        val a1: String = number.pop()
                        val a2: String = number.pop()
                        val b: String = operator.pop()
                        number.push(doubleCal(a2, a1, b[0]))
                    }
                    operator.push(temp)
                }
            }
        } else {
            number.push(temp)
        }
    }

    while (operator.peek() != null) { //遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
        val a1: String = number.pop()
        val a2: String = number.pop()
        val b: String = operator.pop()
        number.push(doubleCal(a2, a1, b[0]))
    }
    return number.pop()
}

val keys = listOf(
    "(",
    ")",
    "C",
    "÷",
    "7",
    "8",
    "9",
    "x",
    "4",
    "5",
    "6",
    "-",
    "1",
    "2",
    "3",
    "+",
    "0",
    ".",
    "D",
    "="
)

val digitalKeys = listOf(
    "7",
    "8",
    "9",
    "4",
    "5",
    "6",
    "1",
    "2",
    "3",
    "0",
    ".",
)

val functionKeys = listOf(
    "C",
    "D",
)

val calculateKeys = listOf(
    "(",
    ")",
    "÷",
    "x",
    "-",
    "+",
)

