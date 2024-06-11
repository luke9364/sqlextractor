package cn.sqlextract;


import cn.sqlextract.env.CustomCommandLinePropertySource;

/**
 * main App
 *
 */
public class App 
{
    public static void main(String[] args) throws Exception {
        CustomCommandLinePropertySource source = new CustomCommandLinePropertySource(args);
        String execType = source.getArgValue("execType");
        if ("extract".equals(execType)){
            SqlExtractApp.getInstance().execute(source);
        }
        else if ("filter".equals(execType)){
            SqlFilterApp.getInstance().execute(source);
        }
        else if ("merge".equals(execType)){
            SqlMergeApp.getInstance().execute(source);
        }
    }
}
