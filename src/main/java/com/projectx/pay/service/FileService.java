package com.projectx.pay.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.projectx.pay.entity.Employee;
import com.projectx.pay.repository.EmployeeRepository;
import com.projectx.pay.utils.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileService {
    @Autowired
    EmployeeRepository employeeRepository;

    public void save(MultipartFile file,String filename) {
        try {
            List<Employee> employee = ExcelHelper.excelToEmployee(file.getInputStream(),filename);
            employeeRepository.saveAll(employee);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load() {
        List<Employee> emp = employeeRepository.findAll();
        ByteArrayInputStream in = ExcelHelper.employeeList(emp,"Employee");
        return in;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}