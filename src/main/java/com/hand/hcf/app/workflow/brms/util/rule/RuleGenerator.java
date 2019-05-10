package com.hand.hcf.app.workflow.brms.util.rule;

import com.hand.hcf.app.workflow.brms.constant.RuleConstants;
import com.hand.hcf.app.workflow.brms.domain.RuleCondition;
import com.hand.hcf.app.workflow.brms.dto.RuleConditionDTO;
import com.hand.hcf.app.workflow.brms.dto.SimpleValueDetailDTO;
import com.hand.hcf.app.workflow.brms.dto.SimpleValueSymbolDTO;
import com.hand.hcf.app.workflow.brms.enums.FieldType;
import com.hand.hcf.app.workflow.brms.enums.SymbolEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("fieldOid.equals( UUID.fromString(").append("\"").append(ruleCondition.getRuleField()).append("\"))");
        statementBuilder.append(" && ");

        if (null != ruleConditionDTO.getValue() && ruleConditionDTO.getValue().split(RuleConstants.RULE_CONDITION_VALUE_SPLIT).length > 1 && ruleConditionDTO.getValue().contains(RuleConstants.RULE_CONDITION_VALUE_SPLIT)) {

            if (ruleConditionDTO.getFieldTypeId().equals(FieldType.CUSTOM_ENUMERATION.getId()) || ruleConditionDTO.getFieldTypeId().equals(FieldType.LIST.getId()) ) {
                String[] _values = ruleConditionDTO.getValue().split(RuleConstants.RULE_CONDITION_VALUE_SPLIT);
                statementBuilder.append("(");
                for (String s : _values) {
                    statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId()));
                    if (ruleCondition.getSymbol().equals(SymbolEnum.CONTAIN.getId())) {
                        statementBuilder.append("==").append("\"").append(s).append("\"").append(" || ");
                    } else if (ruleCondition.getSymbol().equals(SymbolEnum.NOT_CONTAIN.getId())) {
                        statementBuilder.append("!=").append("\"").append(s).append("\"").append(" && ");
                    }

                }
                statementBuilder.setLength(statementBuilder.length() - 4);
                statementBuilder.append(")");
            }
        } else {
            // FieldType is Boolean
            if (ruleConditionDTO.getFieldTypeId().equals(FieldType.BOOLEAN.getId())) {
                if (ruleCondition.getSymbol().equals(SymbolEnum.TRUE.getId())) {
                    statementBuilder.append("value ").append("== true");
                } else if (ruleCondition.getSymbol().equals(SymbolEnum.FALSE.getId())) {
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

                    if (ruleCondition.getSymbol().equals(SymbolEnum.CONTAIN.getId())) {
                        statementBuilder.append("(").append(result).append(")");
                    } else if (ruleCondition.getSymbol().equals(SymbolEnum.NOT_CONTAIN.getId())) {
                        statementBuilder.append("!(").append(result).append(")");
                    } else {
                        statementBuilder.append("(").append(result).append(")");
                    }
                    return statementBuilder.toString();
                }

                if (!ruleConditionDTO.getSymbol().equals(SymbolEnum.NULL.getId())
                    && !ruleConditionDTO.getSymbol().equals(SymbolEnum.NOT_NULL.getId())) {
                    if (null == ruleConditionDTO.getValueDetail() && null == ruleConditionDTO.getValue()) {
                        throw new RuntimeException("Value or ValueDetail must not be null");
                    }
                }

//            statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId())).append(getOperateor(ruleCondition.getSymbol())).append(" ");

                if (!ruleConditionDTO.getSymbol().equals(SymbolEnum.NULL.getId())
                    && !ruleConditionDTO.getSymbol().equals(SymbolEnum.NOT_NULL.getId())) {
                    statementBuilder.append(getValueType(ruleConditionDTO.getFieldTypeId())).append(getOperateor(ruleCondition.getSymbol())).append(" ");
                    if (ruleCondition.getFieldTypeId().equals(FieldType.TEXT.getId())
                        || ruleCondition.getFieldTypeId().equals(FieldType.DATE.getId())
                        || ruleCondition.getFieldTypeId().equals(FieldType.DATETIME.getId())) {
                        statementBuilder.append("\"").append(ruleCondition.getRuleValue()).append("\"");
                    } else {
                        statementBuilder.append(ruleCondition.getRuleValue());
                    }
                } else if (ruleConditionDTO.getSymbol().equals(SymbolEnum.NULL.getId())
                    || ruleConditionDTO.getSymbol().equals(SymbolEnum.NOT_NULL.getId())) {
                    // if contains 9015 or 9016 ,
                    statementBuilder.append(FieldType.DATETIME.getValue()).append(getOperateor(ruleCondition.getSymbol())).append(" ");
                }
            }
        }
        return statementBuilder.toString();
    }

    //ruleConditionDTO.getFieldTypeId()
    private String getValueType(Integer filedTypeId) {
        return FieldType.parse(filedTypeId).getValue();
    }

    private String getOperateor(Integer symbol) {
        return SymbolEnum.parse(symbol).getValue();
    }

    private String buildRangeCondition(SimpleValueDetailDTO simpleValueDetailDTO, boolean isStringValue, Integer typeId, String symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        //in conditions
        if (null != simpleValueDetailDTO.getFieldType()) {
            int fieldTypeId = Integer.valueOf(simpleValueDetailDTO.getFieldType());
            if (symbol.equals(String.valueOf(SymbolEnum.IN.getId()))) {
                //String opearor = getOperateor(Integer.valueOf(simpleValueDetailDTO.getFieldType()));
                for (String value : simpleValueDetailDTO.getValue()) {
                    stringBuilder.append(getValueType(fieldTypeId) + "==" + " ");
                    stringBuilder.append("\"");
                    stringBuilder.append(value);
                    stringBuilder.append("\"");
                    stringBuilder.append(" || ");
                }
            }

            if (symbol.equals(String.valueOf(SymbolEnum.NOT_IN.getId()))) {
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
            if (symbol.equals(String.valueOf(SymbolEnum.CONTAIN.getId())) || symbol.equals(String.valueOf(SymbolEnum.NOT_CONTAIN.getId()))) {
                String opearor = getOperateor(Integer.valueOf(simpleValueDetailDTO.getFieldType()));
                for (String value : simpleValueDetailDTO.getValue()) {
                    if (fieldTypeId == FieldType.TEXT.getId() || fieldTypeId == FieldType.CUSTOM_ENUMERATION.getId() || fieldTypeId == FieldType.LIST.getId()) {
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
