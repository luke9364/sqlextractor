package cn.sqlextract.parser;

import cn.sqlextract.util.CCJSqlExtractorUtil;
import cn.sqlextract.util.SqlFileUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CCJSqlExtractorManage implements SqlExtractor {

    private List<Class<? extends Statement>> stmtClassList;

    public CCJSqlExtractorManage(List<Class<? extends Statement>> stmtClassList) {
        if (CollectionUtils.isEmpty(stmtClassList)){
            return;
        }
        this.stmtClassList = new ArrayList<>();
        this.stmtClassList.addAll(stmtClassList);
    }

    @Override
    public List<String> parse(String sql) throws JSQLParserException {
        Statements statements = CCJSqlParserUtil.parseStatements(sql);
        return CCJSqlExtractorUtil.getStatementSql(statements, this.stmtClassList);
    }

    @Override
    public List<String> parseFile(String filePath) throws Exception {
        return parse(SqlFileUtil.file2Str(filePath));
    }

}
