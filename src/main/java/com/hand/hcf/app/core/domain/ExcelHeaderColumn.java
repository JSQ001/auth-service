package com.hand.hcf.app.core.domain;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class ExcelHeaderColumn {
    private List<ColumnInfo> columnInfo;
    private int headerDeep = 0;
    private int currColumn = 0;
    private List<ExcelHeader> list = new ArrayList();
    private List<ColumnInfo> queryColumn = new ArrayList();

    public List<ColumnInfo> getQueryColumn() {
        return this.queryColumn;
    }

    public int getHeaderDeep() {
        return this.headerDeep;
    }

    public int getCurrColumn() {
        return this.currColumn;
    }

    public ExcelHeaderColumn(List<ColumnInfo> columnInfo) {
        this.columnInfo = columnInfo;
    }

    private void parseColumnInfo(ColumnInfo column, ExcelHeader parentHeader, int row) {
        ExcelHeader excelHeader = new ExcelHeader();
        excelHeader.row = row;
        excelHeader.title = StringUtils.hasText(column.getTitle()) ? column.getTitle() : "";
        excelHeader.columnName = StringUtils.hasText(column.getName()) ? column.getName() : "";
        excelHeader.width = column.getWidth();

        if (parentHeader != null) {
            excelHeader.parentEh = parentHeader;
        }

        excelHeader.column = this.currColumn;

        this.list.add(excelHeader);

        if (!CollectionUtils.isEmpty(column.getColumnsInfo())) {
            excelHeader.endNode = false;
            List<ColumnInfo> infos = column.getColumnsInfo();
            for (int i = 0; i < infos.size(); i++) {
                parseColumnInfo(infos.get(i), excelHeader, row + 1);
            }
        }else {
            excelHeader.endNode = true;
        }
        if (row > this.headerDeep) {
            this.headerDeep = row;
        }
        if (excelHeader.endNode) {
            this.currColumn += 1;
            excelHeader.setCol();
            this.queryColumn.add(column);
        }
    }

    private List<ExcelHeader> parseColumnInfo(){
        for (int i = 0; i < this.columnInfo.size(); i++) {
            ColumnInfo column = this.columnInfo.get(i);
            parseColumnInfo(column, null, 0);
        }
        return this.list;
    }

    public void createHeaderRow(Workbook wb, Sheet sheet) {
        // 设置列字体align
        CellStyle cellStyle = wb.createCellStyle();
        // 设置前置背景色
        //cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
        //cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = wb.createFont();
        font.setFontName("宋体");
        //设置字体大小
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        List arr = parseColumnInfo();
        Map map = new HashMap();
        List listCRA = new ArrayList();
        for (int i = 0; i < arr.size(); i++) {
            ExcelHeader eh = (ExcelHeader)arr.get(i);
            Row row;
            if (map.get(Integer.valueOf(eh.row)) == null) {
                row = sheet.createRow(eh.row);
                // 设置列宽度
                map.put(Integer.valueOf(eh.row), row);
            } else {
                row = (Row)map.get(Integer.valueOf(eh.row));
            }
            Cell cell = row.createCell(eh.column);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(eh.title);
            if (eh.combColumn != 0) {
                CellRangeAddress cra = new CellRangeAddress(eh.row, eh.row, eh.column, eh.combColumn + eh.column - 1);
                sheet.addMergedRegion(cra);
                listCRA.add(cra);
            }
            if ((eh.endNode) && (eh.row < getHeaderDeep())) {
                CellRangeAddress cra = new CellRangeAddress(eh.row, getHeaderDeep(), eh.column, eh.column);
                sheet.addMergedRegion(cra);
                listCRA.add(cra);
            }
        }

        for (Iterator itr = listCRA.iterator(); itr.hasNext(); ) {
            CellRangeAddress cra = (CellRangeAddress)itr.next();
            RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
        }
        for (int i = 0; i < queryColumn.size(); i++) {
            sheet.setColumnWidth(i, queryColumn.get(i).getWidth() * 60);
        }
        sheet.createFreezePane(0, getHeaderDeep() + 1);
        sheet.setAutoFilter(new CellRangeAddress(getHeaderDeep(), getHeaderDeep(), 0, this.currColumn - 1));
    }

    public static class ExcelHeader {
        int row = 0;
        int column = 0;
        boolean endNode = false;
        int combColumn = 0;
        String title;
        String columnName;
        int width = 80;
        ExcelHeader parentEh;

        private void setCol() {
            if (this.parentEh != null) {
                this.parentEh.setCol();
                this.parentEh.combColumn += 1;
            }
        }
    }

}