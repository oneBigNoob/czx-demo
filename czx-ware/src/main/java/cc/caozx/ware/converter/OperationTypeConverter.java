package cc.caozx.ware.converter;

import cc.caozx.ware.enums.OperationType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OperationTypeConverter implements AttributeConverter<OperationType, String>{
    @Override
    public String convertToDatabaseColumn(OperationType operationType) {
        return operationType.getType();
    }

    @Override
    public OperationType convertToEntityAttribute(String type) {
        return OperationType.convert(type);
    }
}
