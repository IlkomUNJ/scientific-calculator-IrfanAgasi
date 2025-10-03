package com.example.scientificcalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scientificcalculator.ui.theme.ScientificCalculatorTheme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Calculator(
    modifier: Modifier = Modifier.background(color = Color(0xff0c0b1a))
) {
    val list3 = listOf(
        "C", "⌫", "%", "÷",
        7, 8, 9, "×",
        4, 5, 6, "-",
        1, 2, 3, "+",
        "⇋", 0, ".", "="
    )

    var displayText by remember { mutableStateOf("") }
    var isScientificOpen by remember { mutableStateOf(false) }
    var isDegreeMode by remember { mutableStateOf(true) }

    val scientificList = listOf(
        "1/x", "x!", "sin", "cos", "tan",
        "x^y", "sqrt", "arcsin", "arccos", "arctan",
        "log", "ln", ")", "RAD", "DEG"
    )

    val buttonWidth = if (isScientificOpen) 100.dp else 100.dp
    val buttonHeight = if (isScientificOpen) 70.dp else 100.dp
    val roundCorner = if (isScientificOpen) 16 else 24


    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalculatorDisplay(modifier = modifier, text = displayText)

        if (isScientificOpen) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp),
                maxItemsInEachRow = 5,
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in scientificList) {
                    CalculatorButton(
                        textButton = i,
                        textColor = when (i) {
                            "DEG" -> if (isDegreeMode) Color.White else Color(0xff1fbfaa)
                            "RAD" -> if (!isDegreeMode) Color.White else Color(0xff1fbfaa)
                            else -> Color(0xff1fbfaa)
                        },
                        buttonColor = when (i) {
                            "DEG" -> if (isDegreeMode) Color(0xff126e69) else Color(0xff0c2a36)
                            "RAD" -> if (!isDegreeMode) Color(0xff126e69) else Color(0xff0c2a36)
                            else -> Color(0xff0c2a36)
                        },
                        buttonAction = {
                            when (i) {
                                "1/x" -> {
                                    val regex = Regex("""(\d+(\.\d+)?|\([^()]*\))$""")
                                    val match = regex.find(displayText)
                                    if (match != null) {
                                        val lastNumber = match.value
                                        displayText = displayText.dropLast(lastNumber.length)
                                        displayText += "1/($lastNumber)"
                                    } else {
                                        displayText += "1/"
                                    }
                                }

                                "x!" -> if (displayText.isNotEmpty() && displayText.last()
                                        .isDigit()
                                ) displayText += "!"

                                "x^y" -> if (displayText.isNotEmpty() && displayText.last()
                                        .isDigit()
                                ) displayText += "^"
                                "sqrt" -> displayText += "sqrt("
                                "log" -> displayText += "log("
                                "ln" -> displayText += "ln("
                                "sin" -> displayText += "sin("
                                "cos" -> displayText += "cos("
                                "tan" -> displayText += "tan("
                                "arcsin" -> displayText += "arcsin("
                                "arccos" -> displayText += "arccos("
                                "arctan" -> displayText += "arctan("
                                ")" -> if (displayText.isNotEmpty() && displayText.last()
                                    .isDigit()
                                ) displayText += ")"
                                "DEG" -> isDegreeMode = true
                                "RAD" -> isDegreeMode = false
                            }
                        },
                        fontSize = 18,
                        width = 80.dp,
                        height = 50.dp,
                        roundCorner = 12
                    )
                }
            }
        }

        FlowRow(modifier = modifier.padding(bottom = 20.dp), maxItemsInEachRow = 4) {
            for (i in list3) {
                when (i) {
                    is Int -> CalculatorButton(
                        textButton = i,
                        buttonAction = { displayText += i.toString() },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    "." -> CalculatorButton(
                        textButton = i,
                        buttonAction = {
                            if (displayText.isNotEmpty()) {
                                val lastChar = displayText.last()
                                if (lastChar.isDigit()) {
                                    val lastNumber = displayText.takeLastWhile { it.isDigit() || it == '.' }
                                    if (!lastNumber.contains(".")) displayText += "."
                                }
                            }
                        },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    "C" -> CalculatorButton(
                        textButton = i,
                        textColor = Color(0xff4283ff),
                        buttonColor = Color(0xff244078),
                        buttonAction = { displayText = "" },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    "⌫" -> CalculatorButton(
                        textButton = i,
                        textColor = Color(0xff4283ff),
                        buttonColor = Color(0xff244078),
                        buttonAction = { if (displayText.isNotEmpty()) displayText = displayText.dropLast(1) },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    "=" -> CalculatorButton(
                        textButton = i,
                        buttonColor = Color(0xff3c77e8),
                        buttonAction = {
                            displayText = CalculatorCalc.evaluate(displayText, isDegreeMode)
                        },
                        width = buttonWidth,
                        height = buttonHeight
                    )
                    "⇋" -> CalculatorButton(
                        textButton = i,
                        textColor = Color(0xff1fbfaa),
                        buttonColor = Color(0xff0c2a36),
                        buttonAction = { isScientificOpen = !isScientificOpen },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    "%" -> CalculatorButton(
                        textButton = i,
                        textColor = Color(0xff4283ff),
                        buttonColor = Color(0xff244078),
                        buttonAction = {
                            if (displayText.isNotEmpty() && displayText.last().isDigit()) {
                                displayText += "%"
                            }
                        },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    in listOf("+", "-", "×", "÷") -> CalculatorButton(
                        textButton = i,
                        textColor = Color(0xff4283ff),
                        buttonColor = Color(0xff244078),
                        buttonAction = {
                            if (displayText.isEmpty() && i == "-") {
                                displayText += i
                            } else if (displayText.isNotEmpty()) {
                                val lastChar = displayText.last().toString()
                                if (lastChar in listOf("+", "-", "×", "÷", "(")) {
                                    if (i == "-" && lastChar != "-") {
                                        displayText += i
                                    } else if (i != "-") {
                                        displayText = displayText.dropLast(1) + i
                                    }
                                } else {
                                    displayText += i
                                }
                            }
                        },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                    else -> CalculatorButton(
                        textButton = i,
                        buttonAction = { displayText += i.toString() },
                        width = buttonWidth,
                        height = buttonHeight,
                        roundCorner = roundCorner
                    )
                }
            }
        }
    }
}


@Composable
fun CalculatorButton(
    textButton: Any,
    textColor: Color = Color.White,
    fontSize: Int = 26,
    roundCorner: Int = 24,
    buttonColor: Color = Color(0xff192d54),
    buttonAction: (Any) -> Unit,
    width: Dp = 100.dp,
    height: Dp = 100.dp
) {
    ElevatedButton(
        shape = RoundedCornerShape(roundCorner.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        modifier = Modifier
            .width(width)
            .height(height)
            .padding(4.dp),
        onClick = { buttonAction(textButton) },
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = textButton.toString(),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CalculatorDisplay(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White
) {
    val operators = listOf(
        "+", "-", "×", "÷", "%", "!", "^",
        "sqrt", "log", "ln",
        "sin", "cos", "tan",
        "arcsin", "arccos", "arctan"
    )

    val styledText = buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            var matched = false
            for (op in operators.sortedByDescending { it.length }) {
                if (text.startsWith(op, i)) {
                    withStyle(style = SpanStyle(color = Color(0xff3c77e8))) {
                        append(op)
                    }
                    i += op.length
                    matched = true
                    break
                }
            }
            if (!matched) {
                withStyle(style = SpanStyle(color = textColor)) {
                    append(text[i])
                }
                i++
            }
        }
    }

    Column(
        modifier = modifier
            .padding(all = 4.dp)
            .height(200.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = styledText,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            maxLines = 5,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xF0000FF,
    widthDp = 410,
    heightDp = 1000
)
@Composable
fun CalculatorPreview() {
    ScientificCalculatorTheme {
        Calculator()
    }
}
