package com.example.archunit.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.example.archunit", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTests {

    // 1. Layer dependency rules
    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controllers").definedBy("..controller..")
            .layer("Services").definedBy("..service..")
            .layer("Repositories").definedBy("..repository..")
            .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services");

    // 2. Naming conventions
    @ArchTest
    static final ArchRule controllers_should_be_suffixed = classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule services_should_be_suffixed = classes()
            .that().resideInAPackage("..service..")
            .should().haveSimpleNameEndingWith("Service");

    // 3. Annotation rules
    @ArchTest
    static final ArchRule rest_controllers_should_only_reside_in_controller_package = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule repositories_should_only_reside_in_repository_package = classes()
            .that().areAnnotatedWith(Repository.class)
            .should().resideInAPackage("..repository..");

    // 4. No cycle detection
    @ArchTest
    static final ArchRule no_cycles_in_packages = slices()
            .matching("com.example.archunit.(*)..")
            .should().beFreeOfCycles();

    // 5. Class rules (entities and dtos)
    @ArchTest
    static final ArchRule entities_must_reside_in_entity_package = classes()
            .that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().resideInAPackage("..entity..");

    @ArchTest
    static final ArchRule dtos_must_reside_in_dto_package = classes()
            .that().haveSimpleNameEndingWith("Dto")
            .should().resideInAPackage("..dto..");

    // 6. Custom rules - @Transactional only on service methods, not controllers
    @ArchTest
    static final ArchRule no_transactional_methods_in_controllers = com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods()
            .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
            .should().beAnnotatedWith(Transactional.class);

    @ArchTest
    static final ArchRule no_transactional_in_controllers = noClasses()
            .that().resideInAPackage("..controller..")
            .should().beAnnotatedWith(Transactional.class);

    // 7. Field rules - no field injection (@Autowired on fields)
    @ArchTest
    static final ArchRule no_field_injection = noFields()
            .should().beAnnotatedWith(Autowired.class);

    // 8. Package rules - domain entities shouldn't depend on spring
    @ArchTest
    static final ArchRule entities_should_not_depend_on_spring = noClasses()
            .that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..");

}
