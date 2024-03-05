package cn.sqlextract.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提取SQL语句工具类
 */
public class SqlExtractorUtil {

    public static final Pattern INSERT_PATTERN = Pattern.compile("(?i)\\s*insert\\s+into\\s+[^;]*;", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern UPDATE_PATTERN = Pattern.compile("(?i)\\s*update\\s+[^;]*;", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static final Pattern DELETE_PATTERN = Pattern.compile("(?i)(^|;)\\s*delete\\s+from\\s+([a-z0-9_]+)\\s*(where\\s+.+)?[^;]*;", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static String removeNonLetterStart(String str) {
        return str.replaceAll("^[^a-zA-Z]*", "");
    }

    /**
     * 清理数据
     *
     * @param str
     * @return
     */
    private static String cleanStr(String str) {
        if (str == null) {
            return null;
        }
        str = str.
                replace("\r\n", " ").
                replace("\r", " ").
                replace("\n", " ").
                trim();
        return removeNonLetterStart(str);
    }

    /**
     * 提取SQL语句函数
     *
     * @param filePath
     * @param pattern
     * @return
     * @throws IOException
     */
    public static List<String> extractStatements(String filePath, Pattern pattern) throws IOException {
        return extractStatements(SqlFileUtil.file2Reader(filePath), pattern);
    }

    /**
     * 提取SQL语句函数
     *
     * @param reader1
     * @param pattern
     * @return
     * @throws IOException
     */
    public static List<String> extractStatements(BufferedReader reader1, Pattern pattern) throws IOException {
        List<String> statementList = new ArrayList<>();
        try (BufferedReader reader = reader1) {
            String line;
            StringBuilder sqlStatement = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sqlStatement.append(line).append("\n");

                // 查找整个SQL语句（包括跨越多行的"SQL"语句）
                Matcher matcher = pattern.matcher(sqlStatement.toString());
                if (matcher.find()) {
                    statementList.add(cleanStr(matcher.group()));
                    // 清空已找到的"SQL"语句后的StringBuilder内容
                    sqlStatement.setLength(0);
                }
            }

            // 检查最后一部分是否遗留了"SQL"语句
            if (sqlStatement.length() > 0) {
                // 这里假设SQL语句末尾一定有分号
                if (pattern.matcher(sqlStatement.toString()).find()) {
                    statementList.add(cleanStr(sqlStatement.toString()));
                }
            }
        }
        return statementList;
    }

    /**
     * 提取 insert SQL语句函数
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<String> extractInsertStatements(String filePath) throws IOException {
        return extractStatements(filePath, INSERT_PATTERN);
    }

    /**
     * 提取 update SQL语句函数
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<String> extractUpdateStatements(String filePath) throws IOException {
        return extractStatements(filePath, UPDATE_PATTERN);
    }

    /**
     * 提取 delete SQL语句函数
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<String> extractDeleteStatements(String filePath) throws IOException {
        return extractStatements(filePath, DELETE_PATTERN);
    }

}
