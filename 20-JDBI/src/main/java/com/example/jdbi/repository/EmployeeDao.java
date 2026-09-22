package com.example.jdbi.repository;

import com.example.jdbi.config.EmailColumnMapper;
import com.example.jdbi.domain.Employee;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;
import java.util.Optional;

// Hỗ trợ ánh xạ trực tiếp từ SQL ResultSet sang Java Record
@RegisterConstructorMapper(Employee.class)
// Hỗ trợ ánh xạ cho kiểu dữ liệu Email tuỳ chỉnh
@RegisterColumnMapper(EmailColumnMapper.class)
public interface EmployeeDao {

    @SqlUpdate("INSERT INTO employee (name, department, email) VALUES (:name, :department, :email)")
    @GetGeneratedKeys
    int insert(@Bind("name") String name, @Bind("department") String department, @Bind("email") String email);

    @SqlQuery("SELECT * FROM employee WHERE id = :id")
    Optional<Employee> findById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM employee")
    List<Employee> findAll();

    @SqlUpdate("UPDATE employee SET name = :name WHERE id = :id")
    void updateName(@Bind("id") int id, @Bind("name") String name);

    // Batch operations
    @SqlBatch("INSERT INTO employee (name, department, email) VALUES (:name, :department, :email)")
    void insertBatch(@Bind("name") List<String> names, @Bind("department") List<String> departments, @Bind("email") List<String> emails);

    // Xử lý Transaction thông qua annotation
    @Transaction
    default void updateMultipleInTransaction(int id1, String name1, int id2, String name2) {
        updateName(id1, name1);
        updateName(id2, name2);
    }
}
