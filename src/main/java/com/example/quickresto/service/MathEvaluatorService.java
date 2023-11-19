package com.example.quickresto.service;

import com.example.quickresto.model.Cell;

public interface MathEvaluatorService {

    Cell[][] getTable();

    void updateCell(int row, int col, String value);
}
