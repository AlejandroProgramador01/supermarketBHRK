package com.technicalTest.supermarket.controller;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.handler.GlobalExceptionHandler;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.service.BranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BranchControllerTest {

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private BranchService branchService;
    @InjectMocks
    private BranchController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void getBranches() throws Exception{
        //given
        List<BranchDTO> branches = MockFactory.getBranchesDTO();

        //when
        when(branchService.getBranches()).thenReturn(branches);

        //then
        mockMvc.perform(
                get("/api/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(branches))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("sucursal 1"))
                .andExpect(jsonPath("$[0].address").value("mz a casa 1"));

        verify(branchService).getBranches();
    }

    @Test
    void testCreateBranch() throws Exception {
        //given
        BranchDTO branchDTORequest = MockFactory.buildBranchRequestDto();
        BranchDTO branchDTOResponse = MockFactory.buildBranchResponseDto();

        //when
        when(branchService.createBranch(any(BranchDTO.class))).thenReturn(branchDTOResponse);

        //then
        mockMvc.perform(
                post("/api/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(branchDTORequest))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("sucursal 1"))
                .andExpect(jsonPath("$.address").value("mz a casa 1"));

        verify(branchService).createBranch(any(BranchDTO.class));
    }

    @Test
    void testUpdateBranch() throws Exception{
        //given
        Branch branch = MockFactory.buildBranchEntity();
        BranchDTO branchDTORequest = MockFactory.buildBranchRequestDto();
        BranchDTO branchDTOResponse = MockFactory.buildBranchResponseDto();

        //when
        when(branchService.updateBranch(eq(1L), any(BranchDTO.class))).thenReturn(branchDTOResponse);

        //then
        mockMvc.perform(
                        put("/api/branches/{id}", branch.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(branchDTORequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("sucursal 1"))
                .andExpect(jsonPath("$.address").value("mz a casa 1"));

        verify(branchService).updateBranch(anyLong(), any(BranchDTO.class));
    }

    @Test
    void testDeleteBranch() throws Exception {
        //given
        Branch branch = MockFactory.buildBranchEntity();

        //when
        doNothing().when(branchService).deleteBranch(branch.getId());

        //then
        mockMvc.perform(
                delete("/api/branches/{id}", branch.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(branchService).deleteBranch(branch.getId());
    }
}
