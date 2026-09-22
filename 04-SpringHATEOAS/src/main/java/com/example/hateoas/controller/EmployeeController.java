package com.example.hateoas.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.hateoas.assembler.EmployeeModelAssembler;
import com.example.hateoas.dto.EmployeeDto;
import com.example.hateoas.service.EmployeeService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeService service, EmployeeModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<EmployeeDto>> all() {
        List<EntityModel<EmployeeDto>> employees = service.findAll().stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }
    
    @GetMapping("/department/{department}")
    public CollectionModel<EntityModel<EmployeeDto>> byDepartment(@PathVariable String department) {
        List<EntityModel<EmployeeDto>> employees = service.findByDepartment(department).stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).byDepartment(department)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).all()).withRel("all_employees"));
    }

    @GetMapping("/{id}")
    public EntityModel<EmployeeDto> one(@PathVariable Long id) {
        EmployeeDto employee = service.findById(id);
        return assembler.toModel(employee);
    }

    @PostMapping
    public ResponseEntity<?> newEmployee(@RequestBody EmployeeDto employeeDto) {
        EntityModel<EmployeeDto> entityModel = assembler.toModel(service.save(employeeDto));

        // Trả về URI của resource vừa tạo qua Location header
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceEmployee(@RequestBody EmployeeDto newEmployee, @PathVariable Long id) {
        EntityModel<EmployeeDto> entityModel = assembler.toModel(service.update(id, newEmployee));

        return ResponseEntity
                .ok()
                .location(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
