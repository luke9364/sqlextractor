package cn.sqlextract.env;

import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class CustomCommandLinePropertySource extends SimpleCommandLinePropertySource {

    public CustomCommandLinePropertySource(String... args) {
        super(args);
    }

    public CustomCommandLinePropertySource(String name, String[] args) {
        super(name, args);
    }

    public List<String> getArgValues(String arg){
        return super.getOptionValues(arg);
    }

    public String getArgValue(String arg){
        List<String> vList = getArgValues(arg);
        return CollectionUtils.isEmpty(vList) ? null : vList.get(vList.size() - 1);
    }
}
