package com.fangdd.esf;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import java.io.File;

/**
 * Created by lijiang on 5/24/17.
 */
public class MailHelper {

    private static String mailHost = "mail.fangdd.com";
    private static String mailFrom = "bigdata_notice@fangdd.com";
    private static String mailUserName = "bigdata_notice@fangdd.com";
    private static String mailPwd = "data@FDD2016";
    private static int port = 587;

/*    public static void SendMail(String mailList, String subject, String body) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setAuthentication(mailUserName, mailPwd);
        email.setHostName(mailHost);
        email.setSmtpPort(port);
        for (String mail : mailList.split(",")) {
            email.addTo(mail);
        }
        email.setFrom(mailFrom);
        email.setSubject(subject);
        email.setCharset("GB2312");
        email.setHtmlMsg(body);
        email.send();
    }*/

    /**
     * @param mailList
     *            mail address separated by comma
     * @param subject
     *            mail subject
     * @param body
     *            mail body
     * @throws EmailException
     *             exception
     */
    public static void sendMail(String mailList, String ccList, String subject, String body, File image, File attachement) throws EmailException {

        try {
            HtmlEmail email = new HtmlEmail();
            email.setAuthentication(mailUserName, mailPwd);
            email.setHostName(mailHost);
            email.setSmtpPort(port);

            for (String mail : mailList.split(",")) {
                email.addTo(mail);
            }

            for (String mail : ccList.split(",")) {
                email.addCc(mail);
            }


            if(attachement!=null){
                email.attach(attachement);
            }

            email.setFrom(mailFrom);
            email.setSubject(subject);
            email.setCharset("UTF-8");

            if(image!=null){
                String cid = email.embed(image, "Fine Report Image");
                String bodyContent = "<html>" + body + "</br></br><img src=\"cid:" + cid + "\"></html>";
                email.setHtmlMsg(bodyContent);
            }else{
                String bodyContent = "<html>" + body + "</html>";
                email.setHtmlMsg(bodyContent);
            }
            email.send();
        } catch (EmailException e) {
            throw e;
        }
    }
}
