package com.technicalTest.supermarket.service.impl;

import com.technicalTest.supermarket.dto.BranchDTO;
import com.technicalTest.supermarket.entity.Branch;
import com.technicalTest.supermarket.exception.NotFoundException;
import com.technicalTest.supermarket.mapper.BranchMapperImpl;
import com.technicalTest.supermarket.mockFactory.MockFactory;
import com.technicalTest.supermarket.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@Import({BranchMapperImpl.class})
@ExtendWith(SpringExtension.class)
public class BranchServiceImplTest {

    @Autowired
    private BranchMapperImpl mapper;
    @Mock
    private BranchRepository repository;

    @InjectMocks
    private BranchServiceImpl branchServiceImpl;

    @BeforeEach
    void setUp() {
        this.repository = mock(BranchRepository.class);
        this.branchServiceImpl = new BranchServiceImpl(repository, mapper);
    }

    @Test
    public void shouldGetBranches(){
        //given
        List<Branch> mockBranches = MockFactory.getBranches();

        //when
        when(repository.findAll())
                .thenReturn(mockBranches);

        //then
        List<BranchDTO> branch = branchServiceImpl.getBranches();
        assertEquals(1, branch.size());
        var firstBranch = branch.get(0);
        assertThat(firstBranch)
                .extracting(
                        BranchDTO::getId,
                        BranchDTO::getName,
                        BranchDTO::getAddress
                ).containsExactly(
                        1L,
                        "sucursal 1",
                        "mz a casa 1"
                );

        verify(repository).findAll();
    }

    @Test
    void shouldCreateBranch(){
        //given
        BranchDTO mockBranchRequestDto = MockFactory.buildBranchRequestDto();
        Branch mockBranch = MockFactory.buildBranchEntity();

        //when
        when(repository.save(any(Branch.class))).thenReturn(mockBranch);

        //then
        BranchDTO branchCreated = branchServiceImpl.createBranch(mockBranchRequestDto);
        assertEquals(1L, branchCreated.getId());
        assertEquals("sucursal 1", branchCreated.getName());
        assertEquals("mz a casa 1", branchCreated.getAddress());

        verify(repository, times(1)).save(any(Branch.class));
    }

    @Test
    void shouldUpdateBranch() {
        //given
        Long id = 1L;
        Branch mockUpdatedBranch = MockFactory.buildBranchEntity();
        BranchDTO mockBranchRequestDto = MockFactory.buildBranchRequestDto();
        mockUpdatedBranch.setId(id);
        mockUpdatedBranch.setName(mockBranchRequestDto.getName());
        mockUpdatedBranch.setAddress(mockBranchRequestDto.getAddress());

        //when
        when(repository.findById(id)).thenReturn(Optional.of(mockUpdatedBranch));
        when(repository.save(mockUpdatedBranch)).thenReturn(mockUpdatedBranch);

        //then
        var updated = branchServiceImpl.updateBranch(id, mockBranchRequestDto);
        assertThat(updated)
                .extracting(
                        BranchDTO::getId,
                        BranchDTO::getName,
                        BranchDTO::getAddress
                ).containsExactly(
                        mockUpdatedBranch.getId(),
                        mockUpdatedBranch.getName(),
                        mockUpdatedBranch.getAddress()
                );

        verify(repository).findById(id);
        verify(repository,times(1)).save(any(Branch.class));
    }

    @Test
    void shouldThrowErrorWhenBranchIsNotFoundForUpdate() {
        //given
        Long id = 1L;
        BranchDTO mockBranchRequestDto = MockFactory.buildBranchRequestDto();

        //when
        when(repository.findById(id)).thenReturn(Optional.empty());

        //then
        var error = assertThrows(NotFoundException.class, () -> branchServiceImpl.updateBranch(id, mockBranchRequestDto));
        verify(repository, times(0)).save(any(Branch.class));
        assertEquals("la sucursal no existe.", error.getMessage());

        verify(repository,times(0)).save(any(Branch.class));
    }

    @Test
    void shouldDeleteBranch() {
        // given
        Long id = 1L;

        // when
        branchServiceImpl.deleteBranch(id);

        // then
        verify(repository, times(1)).deleteById(id);
    }
}
