package cn.sqlextract.parser;

import java.util.List;

public interface SqlExtractor {

    /**
     * 解析SQL语句
     * @param sql SQL字符串
     * @return SQL语句列表
     */
    List<String> parse(String sql) throws Exception;

    /**
     * 解析SQL语句
     * @param filePath SQL文件
     * @return SQL语句列表
     */
    List<String> parseFile(String filePath) throws Exception;
}
