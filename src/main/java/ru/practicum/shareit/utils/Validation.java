package ru.practicum.shareit.utils;

public class Validation {
    public static void isValidBorders(int from, int size) {
        if (from < 0) {
            throw new IllegalArgumentException("Точка начала не может быть отрицательным числом!");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Количество элементов должно быть больше 0!");
        }
    }
}
