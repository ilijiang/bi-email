package com.fangdd.esf;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

/**
 * Created by lijiang on 5/24/17.
 */
public class EmailExecutor implements Callable<TaskStatus> {

    public static final Logger logger = Logger.getLogger(EmailExecutor.class);


    private TaskMapping item;
    private String jobTime;


    public EmailExecutor(TaskMapping item, String jobTime) {
        this.item = item;
        this.jobTime = jobTime;
    }

    @Override
    public TaskStatus call() throws Exception {
        TaskStatus ts = new TaskStatus();
        try {

            String email_title = item.getReportName() + "_" + jobTime;
            String fileName = "." + File.separator + item.getAttachName() + "_" + jobTime;
            List<String> allFiles = new ArrayList<String>();
            String excelDestUrl = MessageFormat.format(item.getFinereportCptNameUrl(), jobTime);
            String imageDestUrl = MessageFormat.format(item.getFinereportContentUrl(), jobTime);



            logger.info("excelDestUrl : " + excelDestUrl);
            logger.info("imageDestUrl : " + imageDestUrl);
            logger.info("fileName : " + fileName);
            logger.info(item.toString());

            //下载文件

            HttpUtil.saveToFile(excelDestUrl, fileName + ".xls");
            HttpUtil.saveToFile(imageDestUrl, fileName + ".png");

            synchronized (allFiles) {
                allFiles.add(fileName + ".xls");
                allFiles.add(fileName + ".png");
            }

            logger.info("file download success :" + fileName);


            if (item.getIsContent().equals("0")) {
                MailHelper.sendMail(item.getEmailReceiver(), item.getEmailCc(), email_title,
                        contentAdditionalHtml(item.getContentAdditional()) + "", null, new File(fileName + ".xls"));
/*                MailHelper.SendMail("lijiang01@fangdd.com", "lijiang01@fangdd.com", email_title,
                        contentAdditionalHtml(item.getContentAdditional()) + "", null, new File(fileName + ".xls"));*/
            } else {
                MailHelper.sendMail(item.getEmailReceiver(), item.getEmailCc(), email_title,
                        contentAdditionalHtml(item.getContentAdditional()), new File(fileName + ".png"),
                        new File(fileName + ".xls"));
/*                MailHelper.SendMail("lijiang01@fangdd.com", "lijiang01@fangdd.com", email_title,
                        contentAdditionalHtml(item.getContentAdditional()), new File(fileName + ".png"),
                        new File(fileName + ".xls"));*/
            }

            deleteFiles(allFiles);
            ts.setItem(item);
            ts.setStatus(true);
        } catch (Exception e) {
            ts.setItem(item);
            ts.setStatus(false);
        }
        return ts;
    }


    public static String contentAdditionalHtml(String contentAdditional) {
        return "<pre>" + contentAdditional + "</pre>";
    }


    public static void deleteFiles(List<String> files) {
        for (String item : files) {
            File file = new File(item);
            if (file.isFile()) {
                file.delete();
            }
        }
    }

}
