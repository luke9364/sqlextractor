package cn.sqlextract.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SqlFileUtil {

    public static List<Path> findSqlFiles(String directoryPath) {
        List<Path> sqlFilePaths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    if (isSqlSuffix(entry.toString())){
                        sqlFilePaths.add(entry);
                    }
                } else if (Files.isDirectory(entry)) {
                    // 如果是目录，则递归查找该目录下的.sql文件
                    sqlFilePaths.addAll(findSqlFilesInSubdirectory(entry));
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while searching for SQL files: " + e.getMessage());
        }

        return sqlFilePaths;
    }

    private static List<Path> findSqlFilesInSubdirectory(Path dir) throws IOException {
        List<Path> foundFiles = new ArrayList<>();
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file) && isSqlSuffix(file.toString())) {
                    foundFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return foundFiles;
    }

    private static boolean isSqlSuffix(String name){
        return name.endsWith(".sql") || name.endsWith(".SQL");
    }

    /**
     * 文件转BufferedReader
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static BufferedReader file2Reader(String filePath) throws IOException {
        return file2Reader(filePath, StandardCharsets.UTF_8);
    }

    /**
     * 文件转BufferedReader
     *
     * @param filePath
     * @param charset
     * @return
     * @throws IOException
     */
    public static BufferedReader file2Reader(String filePath, Charset charset) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), charset));
    }


    /**
     * 读取文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String file2Str(String filePath) throws IOException {
        return file2Str(filePath, StandardCharsets.UTF_8);
    }


    /**
     * 读取文件
     *
     * @param filePath
     * @param charset
     * @return
     * @throws IOException
     */
    public static String file2Str(String filePath, Charset charset) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), charset);
    }

    /**
     * 字符串转BufferedReader
     *
     * @param str
     * @return
     */
    public static BufferedReader str2Reader(String str) {
        return new BufferedReader(new StringReader(str));
    }
}
