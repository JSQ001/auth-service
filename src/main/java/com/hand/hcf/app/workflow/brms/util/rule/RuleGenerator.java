package com.hand.hcf.app.workflow.brms.util.rule;

import com.hand.hcf.app.workflow.brms.domain.RuleCondition;
import com.hand.hcf.app.workflow.brms.dto.RuleConditionDTO;
import com.hand.hcf.app.workflow.brms.dto.SimpleValueDetailDTO;
import com.hand.hcf.app.workflow.brms.dto.SimpleValueSymbolDTO;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.workflow.enums.FieldType;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class RuleGenerator {

    private RuleGenerator.eventType eventType;

    public RuleGenerator.eventType getEventType() {
        return eventType;
    }

    public void setEventType(RuleGenerator.eventType eventType) {
        this.eventType = eventType;
    }

    private RuleCondition ruleCondition;

    private RuleConditionDTO ruleConditionDTO;

    private String ruleDrl;

    private static Set<String> Expense_Operator_Symbol_List = new HashSet<>();

    {
        Expense_Operator_Symbol_List.add("9007");
        Expense_Operator_Symbol_List.add("9008");
        Expense_Operator_Symbol_List.add("9015");
        Expense_Operator_Symbol_List.add("9016");
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("fieldOid.equals( UUID.fromString(").append("\"").append(ruleCondition.getRuleField()).append("\"))");
        statementBuilder.append(" && ");
        if(ruleConditionDTO.getRefCostCenterOid() != null) {
            statementBuilder.append("refCostCenterOid.equals( UUID.fromString(").append("\"").append(ruleConditionDTO.getRefCostCenterOid()).append("\"))");
            statementBuilder.append(" && ");
        }
        if (null != ruleConditionDTO.getValue() && ruleConditionDTO.getValue().split(":").length > 1 && ruleConditionDTO.getValue().contains(":")) {
//            if (!Expense_Operator_Symbol_List.contains(ruleCondition.getSymbol())) {
//                throw new ValidationException(new ValidationError("Symbol", "should be in, not in , null , is not null"));
//            }

            if ((ruleConditionDTO.getFieldTypeId() == Integer.parseInt("106") || ruleConditionDTO.getFieldTypeId() == 106)) {
                String[] _values = ruleConditionDTO.getValue().split(":");
                statementBuilder.append("(");
                for (String s : _values) {
                    statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId()));
                    //in or not in is null or is not null and only support string


                    if (ruleCondition.getSymbol().equals(9007)) {
                        statementBuilder.append("").append("==").append("\"").append(s).append("\"").append(" || ");
                    } else if (ruleCondition.getSymbol().equals(9008)) {
                        statementBuilder.append("!=").append("\"").append(s).append("\"").append("").append(" && ");
                    }

//                if (ruleCondition.getSymbol().equals(9015)) {
//                    statementBuilder.append("").append("==").append("\"").append(s).append("\"").append(" || ");
//                } else if (ruleCondition.getSymbol().equals(9016)) {
//                    statementBuilder.append("!=").append("\"").append(s).append("\"").append(")").append(" && ");
//                }

                }
                statementBuilder.setLength(statementBuilder.length() - 4);
                statementBuilder.append(")");
            } else if((ruleConditionDTO.getFieldTypeId() == Integer.parseInt("109") || ruleConditionDTO.getFieldTypeId() == 109)) {
                String[] _values = ruleConditionDTO.getValue().split(":");
                statementBuilder.append("(");
                for (String s : _values) {
                    statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId()));
                    if (ruleCondition.getSymbol().equals(9007)) {
                        statementBuilder.append("").append("==").append("\"").append(s).append("\"").append(" || ");
                    } else if (ruleCondition.getSymbol().equals(9008)) {
                        statementBuilder.append("!=").append("\"").append(s).append("\"").append("").append(" && ");
                    }
                }
                statementBuilder.setLength(statementBuilder.length() - 4);
                statementBuilder.append(")");
            }
        } else {
            // FieldType is Boolean
            if (ruleConditionDTO.getFieldTypeId() == Integer.parseInt("108") || ruleConditionDTO.getFieldTypeId() == 108) {
                if (ruleCondition.getSymbol() == RuleConstants.SYMBOL_IS_TRUE) {
                    statementBuilder.append("value ").append("== true");
                } else if (ruleCondition.getSymbol() == RuleConstants.SYMBOL_IS_FALSE) {
                    statementBuilder.append("value ").append("== false ");
                }
            } else {
                SimpleValueDetailDTO simpleValueDetailDTO = ruleConditionDTO.getValueDetail();
                if (null != simpleValueDetailDTO) {
                    boolean isStringValue = false;

                    if (ruleConditionDTO.getFieldTypeId().equals(FieldType.TEXT.getId())
                        || ruleConditionDTO.getFieldTypeId().equals(FieldType.DATE.getId())
                        || ruleConditionDTO.getFieldTypeId().equals(FieldType.DATETIME.getId())) {
                        isStringValue = true;
                    } else if (null != simpleValueDetailDTO.getFieldType() && (simpleValueDetailDTO.getFieldType().equals(String.valueOf(FieldType.TEXT.getId()))
                        || simpleValueDetailDTO.getFieldType().equals(String.valueOf(FieldType.DATE.getId()))
                        || simpleValueDetailDTO.getFieldType().equals(String.valueOf(FieldType.DATETIME.getId())))) {
                        isStringValue = true;
                    }

                    String result = buildRangeCondition(ruleConditionDTO.getValueDetail(), isStringValue, ruleConditionDTO.getFieldTypeId(),
                        ruleConditionDTO.getSymbol().toString());

                    if (ruleCondition.getSymbol().equals(9007)) {
                        statementBuilder.append("(").append(result).append(")");
                    } else if (ruleCondition.getSymbol().equals(9008)) {
                        statementBuilder.append("!(").append(result).append(")");
                    } else {
                        statementBuilder.append("(").append(result).append(")");
                    }
                    return statementBuilder.toString();
                }

                if (ruleConditionDTO.getSymbol() != Integer.parseInt("9015")
                    && ruleConditionDTO.getSymbol() != Integer.parseInt("9016")) {
                    if (null == ruleConditionDTO.getValueDetail() && null == ruleConditionDTO.getValue()) {
                        throw new RuntimeException("Value or ValueDetail must not be null");
                    }
                }

//            statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId())).append(getOperateor(ruleCondition.getSymbol())).append(" ");

                if (ruleConditionDTO.getSymbol() != Integer.parseInt("9015")
                    && ruleConditionDTO.getSymbol() != Integer.parseInt("9016")) {
                    statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId())).append(getOperateor(ruleCondition.getSymbol())).append(" ");
                    if (ruleCondition.getFieldTypeId().equals(FieldType.TEXT.getId())
                        || ruleCondition.getFieldTypeId().equals(FieldType.DATE.getId())
                        || ruleCondition.getFieldTypeId().equals(FieldType.DATETIME.getId())) {
                        statementBuilder.append("\"").append(ruleCondition.getRuleValue()).append("\"");
                    } else {
                        statementBuilder.append(ruleCondition.getRuleValue());
                    }
                } else if (ruleConditionDTO.getSymbol() != Integer.parseInt("9015")
                    || ruleConditionDTO.getSymbol() != Integer.parseInt("9016")) {
                    // if contains 9015 or 9016 ,
                    statementBuilder.append(getValueType(103)).append(getOperateor(ruleCondition.getSymbol())).append(" ");
                }
            }
        }
        return statementBuilder.toString();
    }

    //ruleConditionDTO.getFieldTypeId()
    private String getValueType(Integer filedTypeId) {
        String value = "";
        switch (filedTypeId) {
            case 101:
                value = "String.valueOf(value) ";
                break;
            case 102:
                value = "Long.valueOf(value) ";
                break;
            case 103:
                value = "value";
                break;
            case 104:
                value = "Double.valueOf(value)";
                break;
            case 105:
                value = "value";
                break;
            case 106:
                value = "String.valueOf(value) ";
                break;
            case 109:
                value = "String.valueOf(value) ";
                break;
        }
        return value;
    }

    private String getOperateor(Integer symbol) {
        String operator = "";
        switch (symbol) {
            case 9001:
                operator = ">";
                break;
            case 9002:
                operator = ">=";
                break;
            case 9003:
                operator = "<";
                break;
            case 9004:
                operator = "<=";
                break;
            case 9005:
                operator = " == ";
                break;
            case 9006:
                operator = " != ";
                break;
            case 9007:
                operator = "contains";
                break;
            case 9008:
                operator = "not contains";
                break;
            case 9009:
                operator = "==";
                break;
            case 9010:
                operator = "!=";
                break;
            case 9011:
                operator = "range";
                break;
            case 9012:
                operator = "== True";
                break;
            case 9013:
                operator = "== False";
                break;
            case 9015:
                operator = "== null";
                break;
            case 9016:
                operator = "!= null";
                break;
        }
        return operator;
    }

    private String buildRangeCondition(SimpleValueDetailDTO simpleValueDetailDTO, boolean isStringValue, Integer typeId, String symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        //in conditions
        if (null != simpleValueDetailDTO.getFieldType()) {
            int fieldTypeId = Integer.valueOf(simpleValueDetailDTO.getFieldType());
            if (symbol.equals(String.valueOf(RuleConstants.SYMBOL_MEMBEROF))) {
                String opearor = getOperateor(Integer.valueOf(simpleValueDetailDTO.getFieldType()));
                for (String value : simpleValueDetailDTO.getValue()) {
                    stringBuilder.append(getValueType(fieldTypeId) + "==" + " ");
                    stringBuilder.append("\"");
                    stringBuilder.append(value);
                    stringBuilder.append("\"");
                    stringBuilder.append(" || ");
                }
            }

            if (symbol.equals(String.valueOf(RuleConstants.SYMBOL_NOT_MEMBEROF))) {
                String opearor = getOperateor(Integer.valueOf(simpleValueDetailDTO.getFieldType()));
                for (String value : simpleValueDetailDTO.getValue()) {
                    stringBuilder.append(getValueType(fieldTypeId) + "!=" + " ");
                    stringBuilder.append("\"");
                    stringBuilder.append(value);
                    stringBuilder.append("\"");
                    stringBuilder.append(" && ");
                }
            }

            //current we only support string contains
            if (symbol.equals(String.valueOf(RuleConstants.SYMBOL_CONTAINS)) || symbol.equals(String.valueOf(RuleConstants.SYMBOL_NOT_CONTAINS))) {
                String opearor = getOperateor(Integer.valueOf(simpleValueDetailDTO.getFieldType()));
                for (String value : simpleValueDetailDTO.getValue()) {
                    if (fieldTypeId == 101 || fieldTypeId == 106 || fieldTypeId == 109) {
                        stringBuilder.append(getValueType(fieldTypeId) + ".contains(" + "");
                        stringBuilder.append("\"");
                        stringBuilder.append(value);
                        stringBuilder.append("\"");
                        stringBuilder.append(")");
                        stringBuilder.append(" || ");
                    }
                }
            }

        } else {
            if (simpleValueDetailDTO.getList() != null) {
                for (SimpleValueSymbolDTO simpleValueSymbolDTO : simpleValueDetailDTO.getList()) {
                    if (null != simpleValueSymbolDTO.getValue()) {
                        stringBuilder.append("(").append(getValueType(typeId)).append(getOperateor(Integer.valueOf(simpleValueSymbolDTO.getSymbol())));
                        if (isStringValue) {
                            if (typeId.equals(String.valueOf(FieldType.DATE.getId()))) {
                                stringBuilder.append("\"").append(simpleValueSymbolDTO.getValue()).append("\"").append(")");
                            } else {
                                stringBuilder.append("\"").append(simpleValueSymbolDTO.getValue()).append("\"").append(")");
                            }
                        } else {
                            stringBuilder.append(simpleValueSymbolDTO.getValue()).append(")");
                        }
                        stringBuilder.append(" && ");
                    }
                }
            }
        }
        stringBuilder.setLength(stringBuilder.length() - 4);
        return stringBuilder.toString();
    }

    public static enum eventType {
        ORDER("ORDER"),
        INVOICE("INVOICE");
        private final String value;
        private static Map<String, RuleGenerator.eventType> constants = new HashMap<String, RuleGenerator.eventType>();

        static {
            for (RuleGenerator.eventType c : values()) {
                constants.put(c.value, c);
            }
        }

        private eventType(String value) {
            this.value = value;
        }

        public static RuleGenerator.eventType fromValue(String value) {
            RuleGenerator.eventType constant = constants.get(value);

            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }
}
