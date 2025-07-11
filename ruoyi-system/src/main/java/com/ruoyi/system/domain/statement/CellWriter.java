package com.ruoyi.system.domain.statement;

/**
 * 单元格写入封装
 * @param <V>
 */
public class CellWriter<V> {
    private Integer row;    // 行坐标
    private Integer col;    // 列坐标
    private V val; // 写入的值
    private Boolean isFormula;  // 是否为公式

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public V getVal() {
        return val;
    }

    public void setVal(V val) {
        this.val = val;
    }

    public Boolean getFormula() {
        return isFormula;
    }

    public void setFormula(Boolean formula) {
        isFormula = formula;
    }

    public static <V> CellWriter<V> of(Integer row, Integer col, V val, Boolean isFormula) {
        CellWriter<V> objectCellWriter = new CellWriter<>();
        objectCellWriter.setRow(row);
        objectCellWriter.setCol(col);
        objectCellWriter.setVal(val);
        objectCellWriter.setFormula(isFormula);
        return objectCellWriter;
    }

    public static <V> CellWriter<V> of(Integer row, Integer col, V val) {
        return of(row, col, val, false);
    }
}
