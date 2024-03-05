package cn.sqlextract.parser;

import cn.sqlextract.util.CCJSqlExtractorUtil;
import cn.sqlextract.util.SqlExtractorUtil;
import cn.sqlextract.util.SqlFileUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexpSqlExtractorManage implements SqlExtractor {

    private final List<Pattern> patternList;

    private List<Class<? extends Statement>> stmtClassList;

    public RegexpSqlExtractorManage(List<Pattern> patternList, List<Class<? extends Statement>> stmtClassList) {
        Assert.notEmpty(patternList, "patternList参数为空");
        this.patternList = new ArrayList<>();
        this.patternList.addAll(patternList);

        if (!CollectionUtils.isEmpty(stmtClassList)){
            this.stmtClassList = new ArrayList<>();
            this.stmtClassList.addAll(stmtClassList);
        }
    }

    @Override
    public List<String> parse(String sql) throws IOException, JSQLParserException {
        List<String> sqlList = new ArrayList<>();
        for (Pattern pattern : this.patternList) {
            List<String> sqls = SqlExtractorUtil.extractStatements(SqlFileUtil.str2Reader(sql), pattern);
            sqls = CCJSqlExtractorUtil.getStatementSql(sqls, this.stmtClassList);
            if (!CollectionUtils.isEmpty(sqls)){
                sqlList.addAll(sqls);
            }
        }
        return sqlList;
    }

    @Override
    public List<String> parseFile(String filePath) throws Exception {
        return parse(SqlFileUtil.file2Str(filePath));
    }
}
