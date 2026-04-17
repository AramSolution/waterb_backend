package arami.common.util;

import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TsidUtil {

    private static TsidFactory tsidFactory;

    @Autowired
    public TsidUtil(TsidFactory tsidFactory) {
        TsidUtil.tsidFactory = tsidFactory;
    }

    public static Long generateLong() {
        return tsidFactory.create().toLong();
    }

    public static String generateString() {
        return tsidFactory.create().toString();
    }
}
