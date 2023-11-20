package com.example.quickresto.controller;

import com.example.quickresto.model.Cell;
import com.example.quickresto.service.MathEvaluatorService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TableControllerTest {
    private MockMvc mockMvc;
    private MathEvaluatorService mathEvaluatorService;

    @BeforeEach
    void setUp() {
        mathEvaluatorService = mock(MathEvaluatorService.class);
        mockMvc = standaloneSetup(new TableController(mathEvaluatorService)).build();
    }

    @SneakyThrows
    @Test
    void getTable()  {
        Cell[][] table = new Cell[4][4];
        when(mathEvaluatorService.getTable()).thenReturn(table);
        mockMvc.perform(get("/table"))
                .andExpect(status().isOk())
                .andExpect(view().name("tableView"))
                .andExpect(model().attributeExists("table"))
                .andExpect(model().attribute("table", table));
        verify(mathEvaluatorService, times(1)).getTable();
    }

    @SneakyThrows
    @Test
    void updateCell() {
        Cell[][] table = new Cell[4][4];
        when(mathEvaluatorService.getTable()).thenReturn(table);
        mockMvc.perform(post("/updateCell/1/1?value=12"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/table"));
        verify(mathEvaluatorService, times(1)).updateCell(1, 1, "12");
    }
}