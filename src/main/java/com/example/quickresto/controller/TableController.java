package com.example.quickresto.controller;

import com.example.quickresto.model.Cell;
import com.example.quickresto.service.MathEvaluatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Класс контроллера для страницы Table
 */
@Controller
@RequiredArgsConstructor
public class TableController {

    private final MathEvaluatorService mathEvaluatorService;

    @GetMapping("/table")
    public String getTable(Model model) {
        Cell[][] table = mathEvaluatorService.getTable();
        model.addAttribute("table", table);
        return "tableView";
    }

    @PostMapping("/updateCell/{row}/{col}")
    public String updateCell(@PathVariable int row, @PathVariable int col, @RequestParam String value) {
        mathEvaluatorService.updateCell(row, col, value);
        return "redirect:/table";
    }

}