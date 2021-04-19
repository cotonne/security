package com.example.sqlizap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/employees")
    List<Employee> all() {
        return jdbcTemplate.query("SELECT * FROM customers", (resultSet, i) -> new Employee(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        ));
    }

    @GetMapping("/employees/{name}")
    Optional<Employee> one(@PathVariable String name) {
        List<Employee> result = jdbcTemplate.query(
                String.format(
                        "SELECT * FROM customers where first_name='%s'", name), (resultSet, i) -> new Employee(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        ));
        if(result.isEmpty())
            return Optional.empty();
        return Optional.of(result.get(0));
    }

    @GetMapping("/employees/_search")
    Optional<Employee> two(@RequestParam("name") String name) {
        List<Employee> result = jdbcTemplate.query(
                String.format(
                        "SELECT * FROM customers where first_name='%s'", name), (resultSet, i) -> new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                ));
        if(result.isEmpty())
            return Optional.empty();
        return Optional.of(result.get(0));
    }
}
