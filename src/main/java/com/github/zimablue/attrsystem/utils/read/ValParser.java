package com.github.zimablue.attrsystem.utils.read;

import org.jetbrains.annotations.NotNull;

public class ValParser implements Parser<Value> {
    @NotNull
    @Override
    public Result<Value> parse(String text) {
        if (text.isEmpty()) {
            return new Result<>();
        }
        Result<Value> result = new Result<>(this);
        StringBuilder builder = new StringBuilder();
        int index = 0;
        boolean number = false;
        boolean digit = false;
        boolean end = false;
        char operator = '+';
        while (!end && index < text.length()) {
            char c = text.charAt(index++);
            switch (c) {
                case '.':
                    if (!number) {
                        break;
                    }
                    if (digit) {
                        end = true;
                        break;
                    }
                    digit = true;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    number = true;
                    builder.append(c);
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                case '^':
                    if (!number) {
                        operator = c;
                        break;
                    }
                default:
                    if (builder.length() == 0) {
                        break;
                    }
                    end = true;
            }
        }
        if (builder.length() == 0) {
            return new Result<>();
        }
        Value value = new Value(Double.parseDouble(builder.toString()), operator);
        return new Result<>(this).setResult(value).setRange(0, index);
    }
}

