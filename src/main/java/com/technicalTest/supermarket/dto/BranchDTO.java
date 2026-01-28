package com.technicalTest.supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BranchDTO {
    private Long id;
    private String name;
    private String address;

    public static BranchDTOBuilder builder() {
        return new BranchDTOBuilder();
    }

    public static class BranchDTOBuilder {
        private Long id;
        private String name;
        private String address;

        BranchDTOBuilder() {
        }

        public BranchDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BranchDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BranchDTOBuilder address(String address) {
            this.address = address;
            return this;
        }

        public BranchDTO build() {
            return new BranchDTO(this.id, this.name, this.address);
        }

        public String toString() {
            return "BranchDTO.BranchDTOBuilder(id=" + this.id + ", name=" + this.name + ", address=" + this.address + ")";
        }
    }
}
