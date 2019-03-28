package com.hand.hcf.app.mdata.contact.enums;

import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.enums.SysEnum;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import org.springframework.util.StringUtils;

/**
 * Created by qi.yang on 17/1/9.
 */
public enum CardType implements SysEnum {
    ID_CARD(101),//身份证
    PASSPORT(102),//护照
    MTP(103),//台胞证
    HOME_RETURN(104),//回乡证
    TWO_WAY_PERMIT(105);//港澳通行证

    private Integer id;

    CardType(Integer id) {
        this.id = id;
    }

    public static CardType parse(Integer id) {
        for (CardType fieldType : CardType.values()) {
            if (fieldType.getId().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    public static CardType getCardTypeByName(String cardName) {
        if (!StringUtils.isEmpty(cardName)) {
            switch (cardName) {
                case "身份证":
                    return ID_CARD;
                case "护照":
                    return PASSPORT;
                case "台胞证":
                    return MTP;
                case "回乡证":
                    return HOME_RETURN;
                case "港澳通行证":
                    return TWO_WAY_PERMIT;
                case "China ID":
                    return ID_CARD;
                case "Passport":
                    return PASSPORT;
                case "Mainland Travel Permit":
                    return MTP;
                case "Home Return permit":
                    return HOME_RETURN;
                case "Hong Kong-Macau laissez-passer":
                    return TWO_WAY_PERMIT;
            }
        }
        throw new ValidationException(new ValidationError("CardType", "cardType error"));
    }

    public static String getCardNameByType(CardType cardType, String language) {
        // 英文
        if (language.equals(LanguageEnum.EN_US.getKey())) {
            switch (cardType) {
                case ID_CARD:
                    return "China ID";
                case PASSPORT:
                    return "Passport";
                case MTP:
                    return "Mainland Travel Permit";
                case HOME_RETURN:
                    return "Home Return permit";
                case TWO_WAY_PERMIT:
                    return "Hong Kong-Macau laissez-passer";
            }
        } else if (language.equals(LanguageEnum.ZH_CN.getKey())) {
            switch (cardType) {
                case ID_CARD:
                    return "身份证";
                case PASSPORT:
                    return "护照";
                case MTP:
                    return "台胞证";
                case HOME_RETURN:
                    return "回乡证";
                case TWO_WAY_PERMIT:
                    return "港澳通行证";
            }
        }
        throw new ValidationException(new ValidationError("CardType", "cardType error"));
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
