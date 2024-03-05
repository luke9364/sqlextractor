package cn.sqlextract.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CCJSqlExtractorUtil {

    public static List<String> getStatementSql(Statements statements, List<Class<? extends Statement>> stmtClassList){
        if (CollectionUtils.isEmpty(statements)){
            return null;
        }
        if (CollectionUtils.isEmpty(stmtClassList)){
            return stmt2Str(statements);
        }
        Statements statements1 = new Statements();
        for (Statement stmt : statements) {
            for (Class<? extends Statement> stmtClass : stmtClassList) {
                if (stmt.getClass() == stmtClass){
                    statements1.add(stmt);
                }
            }
        }
        return stmt2Str(statements1);
    }

    public static List<String> getStatementSql(List<String> sqlList, List<Class<? extends Statement>> stmtClassList) throws JSQLParserException {
        return getStatementSql(getStatements(sqlList), stmtClassList);
    }

    public static Statements getStatements(List<String> sqlList) throws JSQLParserException {
        Statements statements = new Statements();
        for (String sql : sqlList) {
            statements.add(CCJSqlParserUtil.parse(sql));
        }
        return statements;
    }

    public static List<String> stmt2Str(Statements statements){
        return CollectionUtils.isEmpty(statements) ? null : statements.stream().map(t -> Objects.toString(t) + ";").collect(Collectors.toList());
    }
}
