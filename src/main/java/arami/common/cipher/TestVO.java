package arami.common.cipher;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TestVO {

    @Id @GeneratedValue
    private Long id;

    @Convert(converter = LeaAttributeConverter.class)
    private String email;

}
