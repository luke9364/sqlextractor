package cn.sqlextract;

import cn.sqlextract.env.CustomCommandLinePropertySource;
import cn.sqlextract.util.SqlFileUtil;
import org.springframework.util.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SqlCheckApp {

    private SqlCheckApp() {
    }

    public static SqlCheckApp getInstance(){
        return SqlCheckAppManage.APP;
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

        for (Path path : pathList) {
            String fileName = path.toFile().getName();
            List<String> sqlList = Files.readAllLines(path);
            for (String sql : sqlList) {
                if (sql != null && sql.toLowerCase().contains("drop table")){
                    System.out.println("------------------------------- 严重警告：存在 drop table 语句的“"+fileName+"”文件 -------------------------------------------");
                    break;
                }
            }
        }

        System.out.println("-------------------end----------------------");
    }

    private static class SqlCheckAppManage {
        private static final SqlCheckApp APP = new SqlCheckApp();
    }
}
