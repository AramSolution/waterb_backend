package arami.common.config;

import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TsidConfig {

    @Value("${tsid.node.id:0}")
    private int nodeId;

    @Bean
    public TsidFactory tsidFactory() {
        return TsidFactory.builder()
                .withNode(nodeId)
                .build();
    }
}
