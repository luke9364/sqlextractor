package cn.sqlextract;

import cn.sqlextract.env.CustomCommandLinePropertySource;
import cn.sqlextract.util.SqlFileUtil;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class SqlMergeApp {

    private SqlMergeApp() {
    }

    public static SqlMergeApp getInstance(){
        return SqlMergeAppManage.APP;
    }

    public void execute(CustomCommandLinePropertySource source) throws Exception {

        String dir = source.getArgValue("dir");
        String targetDir = source.getArgValue("targetDir");
        System.out.println("-------------------start----------------------");
        System.out.println("======>解析SQL文件目录："+dir);
        System.out.println("======>合并SQL文件结果目录："+targetDir);

        List<Path> pathList = SqlFileUtil.findSqlFiles(dir);
        if (CollectionUtils.isEmpty(pathList)){
            System.out.println("没找到SQL文件");
            System.out.println("-------------------end----------------------");
            return;
        }

        BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(targetDir, "merge.sql"), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        for (Path path : pathList) {
            String mark = "------------------------------- 来自“"+path.toFile().getName()+"”文件的SQL语句 开始位置 -------------------------------------------";
            System.out.println(mark);
            bufferedWriter.write(mark);
            bufferedWriter.newLine();
            List<String> sqlList = Files.readAllLines(path);
            for (String sql : sqlList) {
                bufferedWriter.write(sql);
                bufferedWriter.newLine();
            }
            bufferedWriter.write("------------------------------- 来自“"+path.toFile().getName()+"”文件的SQL语句 结束位置 -------------------------------------------");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        bufferedWriter.close();
        System.out.println("======>merge.sql文件合并结束");

        System.out.println("-------------------end----------------------");
    }

    private static class SqlMergeAppManage {
        private static final SqlMergeApp APP = new SqlMergeApp();
    }
}
