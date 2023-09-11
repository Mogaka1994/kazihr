package com.projectx.pay.utils;

import com.projectx.pay.entity.Employee;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {
            "Employee Number",
            "FirstName",
            "Surname",
            "LastName",
            "Email",
            "Job Title",
            "Department",
            "Employment Date",
            "Age" };

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static ByteArrayInputStream employeeList(List<Employee> ls,String filename) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(filename);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (Employee employee : ls) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(employee.getId());
                row.createCell(1).setCellValue(employee.getFirstname());
                row.createCell(2).setCellValue(employee.getLastname());
                row.createCell(3).setCellValue(employee.getEmployment_date());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static List<Employee> excelToEmployee(InputStream is,String filename) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<Employee> employee = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Employee emp = new Employee();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 4:
                            emp.setAge(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 7:
                            emp.setDepartment(currentCell.getStringCellValue());
                            break;
//                        case 4:
//                            emp.setEmployment_date(String.valueOf(currentCell.getNumericCellValue()));
//                            break;
                        case 2:
                            emp.setFirstname(currentCell.getStringCellValue());
                            break;
                        case 6:
                            emp.setGender(currentCell.getStringCellValue());
                            break;
//                        case 7:
//                            emp.setKra_pin(String.valueOf(currentCell.getNumericCellValue()));
//                            break;
                        case 8:
                            emp.setLastname(currentCell.getStringCellValue());
                            break;
                        case 9:
                            emp.setMsisdn(String.valueOf(currentCell.getStringCellValue()));
                            break;
                        case 10:
                            emp.setNational_id(String.valueOf(currentCell.getStringCellValue()));
                            break;
                        case 11:
                            emp.setNhif(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 12:
                            emp.setNssf(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 13:
                            emp.setPassword(currentCell.getStringCellValue());
                            break;
                        case 14:
                            emp.setStatus(Integer.valueOf(currentCell.getStringCellValue()));
                            break;
                        case 16:
                            emp.setUsername(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }

                employee.add(emp);
            }

            workbook.close();

            return employee;
        } catch (IOException e) {
            System.out.println("is = " + e.getLocalizedMessage());
        }
        return null;
    }
}