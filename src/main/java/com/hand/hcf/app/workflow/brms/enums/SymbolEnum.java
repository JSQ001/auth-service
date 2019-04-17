package com.hand.hcf.app.workflow.brms.enums;

import com.hand.hcf.app.core.enums.SysEnum;
import lombok.AllArgsConstructor;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@AllArgsConstructor
public enum SymbolEnum implements SysEnum {
    GREAT_THAN(9001,">",">"),
    GEEAT_EQUAL(9002,">=","≥"),
    LESS_THAN(9003,"<","<"),
    LESS_EQUAL(9004,"<=","≤"),
    EQUAL(9005," == ","="),
    NOT_EQUAL(9006," != ","!="),
    CONTAIN(9007,"contains","contains"),
    NOT_CONTAIN(9008,"not contains","not contains"),
    IN(9009,"==","in"),
    NOT_IN(9010,"!=","not in"),
    RANGE(9011,"range","range"),
    TRUE(9012,"== True","isTrue"),
    FALSE(9013,"== False","isFalse"),
    NULL(9015,"== nul","isNull"),
    NOT_NULL(9016,"!= nul","isNotNull")
    ;
    private Integer id;
    private String value;
    private String symbol;

    public static SymbolEnum parse(Integer id) {
        for (SymbolEnum fieldType : SymbolEnum.values()) {
            if (fieldType.getId().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public String getValue(){
        return this.value;
    }


    public String getSymbol(){
        return this.symbol;
    }


}
