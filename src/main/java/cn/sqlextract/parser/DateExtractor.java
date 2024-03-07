package cn.sqlextract.parser;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateExtractor {

    public static final Pattern yyyyMMdd1 = Pattern.compile("(\\d{4}.\\d{2}.\\d{2})");

    private Pattern pattern;

    public DateExtractor(Pattern pattern) {
        this.pattern = pattern;
        Assert.notNull(this.pattern, "pattern参数为空");
    }

    public DateExtractor(String regex) {
        this(Pattern.compile(regex));
    }

    public String extract(String str){
        if (StringUtils.isEmpty(str)){
            throw new IllegalArgumentException("str is empty.");
        }
        Matcher matcher = this.pattern.matcher(str);
        if (matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

}
