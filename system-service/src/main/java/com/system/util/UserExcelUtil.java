package com.system.util;

import com.system.common.BusinessException;
import com.system.dto.UserExcelDTO;
import com.system.vo.UserPageVO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户 Excel 导入导出工具。
 */
public class UserExcelUtil {

    private static final String[] HEADERS = {"账号", "密码", "昵称", "手机号", "邮箱", "状态"};
    private static final DataFormatter FORMATTER = new DataFormatter();

    private UserExcelUtil() {
    }

    public static byte[] buildTemplate() {
        return writeExcel(List.of(sampleRow()));
    }

    public static byte[] writeUsers(List<UserPageVO> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("用户列表");
            writeHeader(sheet);
            for (int i = 0; i < users.size(); i++) {
                UserPageVO user = users.get(i);
                Row row = sheet.createRow(i + 1);
                writeCell(row, 0, user.getUsername());
                writeCell(row, 1, "");
                writeCell(row, 2, user.getNickname());
                writeCell(row, 3, user.getPhone());
                writeCell(row, 4, user.getEmail());
                writeCell(row, 5, formatStatus(user.getStatus()));
            }
            autoSizeColumns(sheet);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("导出用户Excel失败");
        }
    }

    public static List<UserExcelDTO> readUsers(MultipartFile file) {
        validateExcelFile(file);
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<UserExcelDTO> users = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isBlankRow(row)) {
                    continue;
                }
                UserExcelDTO dto = new UserExcelDTO();
                dto.setRowNum(rowIndex + 1);
                dto.setUsername(readCell(row, 0));
                dto.setPassword(readCell(row, 1));
                dto.setNickname(readCell(row, 2));
                dto.setPhone(readCell(row, 3));
                dto.setEmail(readCell(row, 4));
                dto.setStatus(readCell(row, 5));
                users.add(dto);
            }
            return users;
        } catch (IOException e) {
            throw new BusinessException("读取用户Excel失败");
        }
    }

    private static byte[] writeExcel(List<UserExcelDTO> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("用户导入模板");
            writeHeader(sheet);
            for (int i = 0; i < users.size(); i++) {
                UserExcelDTO user = users.get(i);
                Row row = sheet.createRow(i + 1);
                writeCell(row, 0, user.getUsername());
                writeCell(row, 1, user.getPassword());
                writeCell(row, 2, user.getNickname());
                writeCell(row, 3, user.getPhone());
                writeCell(row, 4, user.getEmail());
                writeCell(row, 5, user.getStatus());
            }
            autoSizeColumns(sheet);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("生成用户Excel失败");
        }
    }

    private static UserExcelDTO sampleRow() {
        UserExcelDTO dto = new UserExcelDTO();
        dto.setUsername("zhangsan");
        dto.setPassword("123456");
        dto.setNickname("张三");
        dto.setPhone("13800138000");
        dto.setEmail("zhangsan@example.com");
        dto.setStatus("1");
        return dto;
    }

    private static void writeHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setWrapText(true);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(style);
        }
    }

    private static void writeCell(Row row, int index, String value) {
        row.createCell(index).setCellValue(value == null ? "" : value);
    }

    private static String readCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? "" : FORMATTER.formatCellValue(cell).trim();
    }

    private static boolean isBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < HEADERS.length; i++) {
            if (!readCell(row, i).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 14 * 256));
        }
    }

    private static String formatStatus(Integer status) {
        if (status == null) {
            return "";
        }
        return status == 1 ? "1" : "0";
    }

    private static void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Excel文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            throw new BusinessException("只支持xls或xlsx文件");
        }
    }
}
