package cn.sqlextract.parser;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlExtractorSubject {

    private List<SqlExtractor> sqlExtractorList;

    private FilenameFilter filenameFilter;

    public SqlExtractorSubject(FilenameFilter filenameFilter, SqlExtractor ... sqlExtractors) {
        setFilenameFilter(filenameFilter);
        this.sqlExtractorList = new ArrayList<>();
        if (sqlExtractors != null && sqlExtractors.length > 0){
            this.sqlExtractorList.addAll(Arrays.asList(sqlExtractors));
        }
    }

    public SqlExtractorSubject setFilenameFilter(FilenameFilter filenameFilter){
        this.filenameFilter = filenameFilter;
        return this;
    }

    public SqlExtractorSubject addSqlExtractor(SqlExtractor sqlExtractor){
        if (sqlExtractor != null){
            this.sqlExtractorList.add(sqlExtractor);
        }
        return this;
    }

    public void extract(Path path, List<String> okList, List<String> failList){
        Assert.notNull(path, "path参数为空");
        Assert.notNull(okList, "okList参数为空");
        Assert.notNull(failList, "failList参数为空");

        if (!filter(path)){
            System.out.println("过滤文件：" + path);
            return;
        }

        String filePath = path.toString();
        int maxIdx = this.sqlExtractorList.size() - 1;
        for (int i = 0; i <= maxIdx; i++) {
            SqlExtractor sqlExtractor = this.sqlExtractorList.get(i);
            try {
                List<String> sqls = sqlExtractor.parseFile(filePath);
                if (!CollectionUtils.isEmpty(sqls)){
                    okList.addAll(sqls);
                }
                break;
            }catch (Exception e){
                if (i == maxIdx){
                    failList.add("解析出错了：" + filePath);
                    failList.add(e.getMessage());
                }
            }
        }
    }

    private boolean filter(Path path){
        return this.filenameFilter == null || this.filenameFilter.accept(path.toFile(), path.getFileName().toString());
    }
}
