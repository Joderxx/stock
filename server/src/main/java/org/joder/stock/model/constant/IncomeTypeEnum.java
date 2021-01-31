package org.joder.stock.model.constant;

public enum IncomeTypeEnum {

    SPEND("支出", (byte) 1),
    INCOME("收入", (byte) 0);


    private String text;
    private byte value;

    IncomeTypeEnum(String text, byte value) {
        this.text = text;
        this.value = value;
    }

    public static IncomeTypeEnum valueOf(byte v) {
        for (IncomeTypeEnum type : values()) {
            if (type.value == v) {
                return type;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public byte getValue() {
        return value;
    }
}
