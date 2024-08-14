package cn.sqlextract;

import cn.sqlextract.env.CustomCommandLinePropertySource;
import cn.sqlextract.util.SqlFileUtil;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                if (listItemInclude(ignoredSqlList, sql)){
                    okList.add(sql);
                }
                else if ( listItemInclude(filterSqlList, sql)
                        || sql.toLowerCase().contains("drop column")
                        || sql.toLowerCase().contains("add constraint")
                        || sql.toLowerCase().contains("drop constraint")
                        || sql.toLowerCase().contains("set not null")
                        || sql.toLowerCase().contains("drop not null") ){
                    failList.add(sql);
                }
                else {
                    okList.add(sql);
                }
            }
        }

        okList = okList.stream().map(this::filter).collect(Collectors.toList());

        Files.write(Paths.get(dir, "ok.sql"), okList, StandardCharsets.UTF_8);
        Files.write(Paths.get(dir, "fail.sql"), failList, StandardCharsets.UTF_8);
        System.out.println("======>输出正常的SQL语句：" + okList.size());
        System.out.println("======>输出错误的SQL语句：" + failList.size());
        System.out.println("-------------------end----------------------");
    }

    private String filter(String sql){
        return sql.replace("COLLATE \"pg_catalog\".\"default\"", "").replace("\"public\".", "");
    }

    private static boolean listItemInclude(List<String> list, String text){
        for (String t : list) {
            if (text.contains(t)){
                return true;
            }
        }
        return false;
    }

    private static class SqlFilterAppManage {
        private static final SqlFilterApp APP = new SqlFilterApp();
    }

    // 过滤的语句
    private static final List<String> filterSqlList = Arrays.asList(
            "ALTER TABLE \"public\".\"sms_message\" ALTER COLUMN \"schedule_time\" TYPE timestamp(6) USING \"schedule_time\"::timestamp(6)"
    );

    // 忽略过滤的语句
    private static final List<String> ignoredSqlList = Arrays.asList(
            "ALTER TABLE \"public\".\"dip_operation_dict\" DROP CONSTRAINT \"dip_operation_dict_pk\"",
            "ALTER TABLE \"public\".\"dip_operation_dict\" ADD CONSTRAINT \"dip_operation_dict_pk\" PRIMARY KEY (\"id\")",
            "ALTER TABLE \"public\".\"dwm_report_gz_ip_match_his_fee\" DROP CONSTRAINT \"pk_dwm_report_gz_ip_match_his_\"",
            "ALTER TABLE \"public\".\"dwm_report_pi_ip_match_his_fee\" DROP CONSTRAINT \"pk_dwm_report_pi_ip_match_his_\"",
            "ALTER TABLE \"public\".\"dws_report_gz_ip\" DROP CONSTRAINT \"pk_dws_report_gz_ip\"",
            "ALTER TABLE \"public\".\"dws_report_pi_ip\" DROP CONSTRAINT \"pk_dws_report_pi_ip\""
    );
}
