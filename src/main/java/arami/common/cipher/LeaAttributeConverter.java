package arami.common.cipher;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LeaAttributeConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return LeaEncryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return LeaEncryptionUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}