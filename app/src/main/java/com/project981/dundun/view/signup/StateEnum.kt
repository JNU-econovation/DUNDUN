package com.project981.dundun.view.signup

enum class IdStateEnum() {
    NONE, DUPLICATE, ERROR, TYPE, CORRECT
}

enum class PwStateEnum() {
    NONE, TYPE, LENGTH, CORRECT
}

enum class NameStateEnum {
    NONE, LENGTH, TYPE, CORRECT
}

enum class PwRepeatStateEnum {
    NONE, DIFF, CORRECT
}