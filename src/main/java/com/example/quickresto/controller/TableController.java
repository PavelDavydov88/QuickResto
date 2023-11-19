package com.example.quickresto.controller;

import com.example.quickresto.service.MathEvaluatorServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@AllArgsConstructor
public class TableController {

    MathEvaluatorServiceImpl mathEvaluatorService;

    @GetMapping("/table")
    public String getTable(Model model) {
        model.addAttribute("table", mathEvaluatorService.getTable());
        return "table";
    }

    @PostMapping("/updateCell/{row}/{col}")
    public String updateCell(@PathVariable int row, @PathVariable int col, @RequestParam String value) {
        mathEvaluatorService.updateCell(row, col, value);
        return "redirect:/table";
    }

}