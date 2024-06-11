package cn.sqlextract;

import cn.sqlextract.env.CustomCommandLinePropertySource;
import cn.sqlextract.util.SqlFileUtil;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SqlFilterApp {

    private SqlFilterApp() {
    }

    public static SqlFilterApp getInstance(){
        return SqlFilterAppManage.APP;
    }

    public void execute(CustomCommandLinePropertySource source) throws Exception {

        String dir = source.getArgValue("dir");
        System.out.println("-------------------start----------------------");
        System.out.println("======>解析SQL文件目录："+dir);

        List<Path> pathList = SqlFileUtil.findSqlFiles(dir);
        if (CollectionUtils.isEmpty(pathList)){
            System.out.println("没找到SQL文件");
            System.out.println("-------------------end----------------------");
            return;
        }

        List<String> okList = new ArrayList<>();
        List<String> failList = new ArrayList<>();

        for (Path path : pathList) {
            List<String> sqlList = Files.readAllLines(path);
            for (String sql : sqlList) {
                if ( sql.toLowerCase().contains("drop column") || sql.toLowerCase().contains("add constraint") || sql.toLowerCase().contains("drop constraint") ){
                    failList.add(sql);
                }
                else {
                    okList.add(sql);
                }
            }
        }

        Files.write(Paths.get(dir, "ok.sql"), okList, StandardCharsets.UTF_8);
        Files.write(Paths.get(dir, "fail.sql"), failList, StandardCharsets.UTF_8);
        System.out.println("======>输出正常的SQL语句：" + okList.size());
        System.out.println("======>输出错误的SQL语句：" + failList.size());
        System.out.println("-------------------end----------------------");
    }

    private static class SqlFilterAppManage {
        private static final SqlFilterApp APP = new SqlFilterApp();
    }
}
