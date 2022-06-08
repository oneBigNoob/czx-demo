package cc.caozx.ware.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OperationType {
    LOCK("LOCK", "锁定"),
    DEDUCT_LOCKED("DEDUCT_LOCKED", "扣除锁定"),
    DEDUCE("DEDUCE", "直接扣除"),
    ADD("ADD", "增加库存"),
    UNKNOWN("UNKNOWN", "未知操作类型");
    @EnumValue
    private final String type;

    private final String description;
    @JsonValue
    public static OperationType convert(String type) {
        return Stream.of(values())
                .filter(operationType -> operationType.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
