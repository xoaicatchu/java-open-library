package com.example.hateoas.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.hateoas.controller.EmployeeController;
import com.example.hateoas.dto.EmployeeDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<EmployeeDto, EntityModel<EmployeeDto>> {

    @Override
    public EntityModel<EmployeeDto> toModel(EmployeeDto employee) {
        // Tạo các liên kết self và liên kết danh sách cho từng DTO (HATEOAS Links)
        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).one(employee.id())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).all()).withRel("employees"),
                linkTo(methodOn(EmployeeController.class).byDepartment(employee.department())).withRel("department")
        );
    }
}
