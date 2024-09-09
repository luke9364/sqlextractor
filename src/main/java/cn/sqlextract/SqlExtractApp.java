package cn.sqlextract;

import cn.sqlextract.env.CustomCommandLinePropertySource;
import cn.sqlextract.parser.CCJSqlExtractorManage;
import cn.sqlextract.parser.DateExtractor;
import cn.sqlextract.parser.RegexpSqlExtractorManage;
import cn.sqlextract.parser.SqlExtractorSubject;
import cn.sqlextract.util.SqlExtractorUtil;
import cn.sqlextract.util.SqlFileUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 执行SQL语句提取操作的工具类
 */
public final class SqlExtractApp {

    private SqlExtractApp() {
    }

    public static SqlExtractApp getInstance(){
        return SqlExtractAppManage.APP;
    }

    public void execute(CustomCommandLinePropertySource source) throws IOException {
        String dir = source.getArgValue("dir");
        String targetDir = source.getArgValue("targetDir");
        String minDate = source.getArgValue("minDate");
        System.out.println("-------------------start----------------------");
        System.out.println("======>解析SQL文件目录："+dir);
        System.out.println("======>SQL语句结果目录："+targetDir);
        System.out.println("======>文件名称的最小日期："+minDate);

        List<Path> pathList = SqlFileUtil.findSqlFiles(dir);
        if (CollectionUtils.isEmpty(pathList)){
            System.out.println("没找到SQL文件");
            System.out.println("-------------------end----------------------");
            return;
        }
        pathList.sort(Comparator.comparing(t -> t.toFile().getName()));

        List<Class<? extends Statement>> stmtClassList = new ArrayList<>();
        stmtClassList.add(Insert.class);
        stmtClassList.add(Update.class);
        stmtClassList.add(Delete.class);

        List<Pattern> patternList = new ArrayList<>();
        patternList.add(SqlExtractorUtil.INSERT_PATTERN);
        patternList.add(SqlExtractorUtil.UPDATE_PATTERN);
        patternList.add(SqlExtractorUtil.DELETE_PATTERN);

        FilenameFilter filenameFilter = null;
        if (minDate != null){
            DateExtractor dateExtractor = new DateExtractor(DateExtractor.yyyyMMdd1);
            filenameFilter = (file, filename) -> {
                String extract = dateExtractor.extract(filename);
                return extract != null && extract.compareTo(minDate) >= 0;
            };
        }

        SqlExtractorSubject sqlExtractorSubject = new SqlExtractorSubject(filenameFilter,
                new CCJSqlExtractorManage(stmtClassList),
                new RegexpSqlExtractorManage(patternList, stmtClassList));


        int okCount = 0;
        int failCount = 0;
        BufferedWriter okWriter = Files.newBufferedWriter(Paths.get(targetDir, "extract_ok.sql"), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        BufferedWriter failWriter = Files.newBufferedWriter(Paths.get(targetDir, "extract_fail.sql"), StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        for (Path path : pathList) {
            String fileName = path.toFile().getName();
            String mark = "------------------------------- 来自“"+fileName+"”文件的SQL语句 开始位置 -------------------------------------------";
            List<String> okList = new ArrayList<>();
            List<String> failList = new ArrayList<>();
            sqlExtractorSubject.extract(path, okList, failList);
            if (!CollectionUtils.isEmpty(okList)){
                //okCount += okList.size();
                okWriter.write(mark);
                okWriter.newLine();
                for (String sql : okList) {
                    if (sql.toLowerCase().contains("delete from ")){
                        failList.add(sql);
                        continue;
                    }
                    okCount ++;
                    okWriter.write(sql);
                    okWriter.newLine();
                }
                okWriter.write("------------------------------- 来自“"+fileName+"”文件的SQL语句 结束位置 -------------------------------------------");
                okWriter.newLine();
                okWriter.newLine();
                okWriter.newLine();
                okWriter.flush();
            }
            if (!CollectionUtils.isEmpty(failList)){
                failCount += failList.size();
                failWriter.write(mark);
                failWriter.newLine();
                for (String sql : failList) {
                    failWriter.write(sql);
                    failWriter.newLine();
                }
                failWriter.write("------------------------------- 来自“"+fileName+"”文件的SQL语句 结束位置 -------------------------------------------");
                failWriter.newLine();
                failWriter.newLine();
                failWriter.newLine();
                failWriter.flush();
            }
        }
        okWriter.close();
        failWriter.close();

        System.out.println("======>输出正常的SQL语句：" + okCount);
        System.out.println("======>输出错误的SQL语句：" + failCount);
        System.out.println("-------------------end----------------------");
        System.exit(0);
    }

    private static class SqlExtractAppManage {
        private static final SqlExtractApp APP = new SqlExtractApp();
    }
}
