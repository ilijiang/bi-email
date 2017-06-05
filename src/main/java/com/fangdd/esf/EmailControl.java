package com.fangdd.esf;

import org.apache.log4j.Logger;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by lijiang on 5/25/17.
 */
public class EmailControl {
    public static final Logger logger = Logger.getLogger(EmailExecutor.class);

    public static void main(String[] args) throws Exception {
        try {
            // 以下两个的取值
            String reportType = args[0];
            String sendType = args[1];
            String selectDate = args[2];
            String sql = null;

            logger.info("reportType : " + reportType);
            logger.info("sendType : " + sendType);
            logger.info("selectDate : " + selectDate);


            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date sd = sdf.parse(selectDate);
            String jobTime = sdf.format(sd);


            Calendar sendDate = Calendar.getInstance();
            sendDate.setTime(sd);


            String sqlCondition = "";

            if (sendType.equals("预发")) {

                sqlCondition = "send_type = 2";

            } else {

                sqlCondition = "send_type = 1";

            }

            if (reportType.equals("周报")) {

                sqlCondition = sqlCondition + " and cycle_type = 0 and find_in_set(" + sendDate.get(Calendar.DAY_OF_WEEK) + ",week_day)";

            } else if (reportType.equals("月报")) {

                sendDate.add(sendDate.DATE, 1);
                sqlCondition = sqlCondition + " and cycle_type = 1 and find_in_set(" + sendDate.get(Calendar.DAY_OF_MONTH) + ",day_of_month)";

            } else if (reportType.equals("全部")) {

                sqlCondition = sqlCondition + " and ((cycle_type = 0 and find_in_set(" + sendDate.get(Calendar.DAY_OF_WEEK) + ",week_day))";
                sendDate.add(sendDate.DATE, 1);
                sqlCondition = sqlCondition + " or (cycle_type = 1 and find_in_set(" + sendDate.get(Calendar.DAY_OF_MONTH) + ",day_of_month)))";

            }

            sql = "select * from finereport_task_mapping where status = 1 and " + sqlCondition;

            logger.info("sql : " + sql);

            //根据参数获取每日邮件任务
            List<TaskMapping> tm = getTaskMappingInfo(sql);

            logger.info("TaskMaping size : " + tm.size());


            if (tm.size() > 0) {
                // 创建线程池,最多5个
                Integer i = Math.min(5,tm.size());
                ExecutorService es = Executors.newFixedThreadPool(i);

                List<Future<TaskStatus>> future = new ArrayList<Future<TaskStatus>>();


                for (TaskMapping item : tm) {
                    EmailExecutor emailExecutor = new EmailExecutor(item, selectDate);
                    future.add(es.submit(emailExecutor));
                }

                //获取每个邮件任务线程的结果
                for (Future<TaskStatus> f : future) {
                    if (f.get().getStatus()) {
                        logger.info(f.get().getItem().getReportName() + "发送成功");
                    } else {
                        logger.warn(f.get().getItem().getReportName() + "发送失败");
                    }
                }

                //关闭线程池
                es.shutdown();
            }

        } catch (Exception e) {

            logger.error(e.getMessage().toString());
            throw e;

        }
    }

    public static List<TaskMapping> getTaskMappingInfo(String sql) throws Exception {
        List<TaskMapping> tm = new ArrayList<TaskMapping>();

        DBHelper db = new DBHelper(sql);
        ResultSet rs = null;

        rs = db.pst.executeQuery();

        logger.info("database connection success");
        while (rs.next()) {
            TaskMapping itm = new TaskMapping();
            itm.setContentAdditional(rs.getString("content_additional"));

            itm.setDtFormat(rs.getString("dt_format"));

            itm.setEmailCc(rs.getString("email_Cc"));

            itm.setEmailReceiver(rs.getString("email_receiver"));

            itm.setFinereportContentUrl(rs.getString("finereport_content_url"));

            itm.setFinereportCptNameUrl(rs.getString("finereport_cpt_name_url"));

            itm.setFinereportTaskName(rs.getString("finereport_task_name"));

            itm.setIsAttach(rs.getString("is_attach"));

            itm.setIsContent(rs.getString("is_content"));

            itm.setReportDemandPart(rs.getString("report_demand_part"));

            itm.setReportDeveloper(rs.getString("report_developer"));

            itm.setReportName(rs.getString("report_name"));

            itm.setReprotDockingPeople(rs.getString("reprot_docking_people"));

            itm.setSendType(rs.getString("send_type"));

            itm.setCycleType(rs.getInt("cycle_type"));

            itm.setWeekDay(rs.getString("week_day"));

            itm.setAttachName(rs.getString("attach_name"));

            tm.add(itm);

        }

        rs.close();
        db.close();
        return tm;
    }
}
