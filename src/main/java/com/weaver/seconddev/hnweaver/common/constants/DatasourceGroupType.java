package com.weaver.seconddev.hnweaver.common.constants;

/**
 * @author 姚礼林
 * @desc 数据源组类型，在 sql 执行会用到，在微服务中匹配到对应的数据库中
 * @date 2025/7/25
 **/
public enum DatasourceGroupType {
    WEAVER_FORMREPORT_SERVICE("weaver-formreport-service", "业务表单", "form"),
    WEAVER_WORKFLOW_REPORT_SERVICEWORKFLOWREPORT("weaver-workflow-report-serviceworkflowreport", "流程", "workflow"),
    WEAVER_PROJECT_SERVICETASK("weaver-project-servicetask", "任务", "task"),
    WEAVER_ELOG_SERVICE("weaver-elog-service", "日志", "elog"),
    WEAVER_MEETING_SERVICE("weaver-meeting-service", "会议", "meeting"),
    WEAVER_BLOG_SERVICE("weaver-blog-service", "日报", "blog"),
    WEAVER_WR_GOAL_SERVICE("weaver-wr-goal-service", "OKR", "goal"),
    WEAVER_WORKFLOW_LIST_SERVICE("weaver-workflow-list-service", "流程列表", "workflow"),
    WEAVER_ATTEND_SERVICE("weaver-attend-service", "考勤", "attend"),
    WEAVER_CRM_SERVICE("weaver-crm-service", "CRM", "customer"),
    WEAVER_PORTRAIT_SERVICE("weaver-portrait-service", "组织画像", "portrait"),
    WEAVER_WORKFLOW_REPORT_SERVICEWORKFLOW_REPORT("weaver-workflow-report-serviceworkflow_report", "工作流报告", "workflow"),
    WEAVER_WR_PERFORMANCE_SERVICE("weaver-wr-performance-service", "绩效", "kpi"),
    WEAVER_PROJECT_SERVICE("weaver-project-service", "项目", "mainline"),
    WEAVER_BASIC_SCHEDULE_SERVICE("weaver-basic-schedule-service", "基础定时模块", "escheduler"),
    WEAVER_DOC_SERVICE("weaver-doc-service", "文档", "document"),
    WEAVER_HRM_SERVICE("weaver-hrm-service", "HRM", "hrm"),
    WEAVER_EDCREPORTD_SERVICE("weaver-edcreportd-service", "e-builder报表", ""),
    WEAVER_ESB_SETTING_SERVICE("weaver-esb-setting-service", "esb", "esb"),
    WEAVER_EBUILDER_APP_SERVICE("weaver-ebuilder-app-service", "e-builder应用", "ebuilder"),
    WEAVER_EBUILDER_FORM_SERVICE("weaver-ebuilder-form-service", "e-builder表单", "ebuilder"),
    WEAVER_CUSTOMER_SERVICE_SERVICE("weaver-customer-service-service", "客服", "customerservice"),
    WEAVER_CALENDAR_SERVICE("weaver-calendar-service", "日程", "calendar"),
    WEAVER_DOC_SERVICEDOCUMENT("weaver-doc-servicedocument", "文档", "document"),
    WEAVER_FILE_SERVICE("weaver-file-service", "文件", "file"),
    WEAVER_ODOC_SERVICE("weaver-odoc-service", "公文管理", "odoc"),
    WEAVER_CRM_MARKET_SERVICE("weaver-crm-market-service", "SCRM", "scrm"),
    WEAVER_COWORK_SERVICE("weaver-cowork-service", "协作区", "cowork"),
    WEAVER_DATASOURCE_SERVICE("weaver-datasource-service", "数据源", "datasource"),
    WEAVER_WR_PLAN_SERVICE("weaver-wr-plan-service", "计划报告", "workreport"),
    WEAVER_SIGNCENTER_SERVICE("weaver-signcenter-service", "电子签", "signcontract"),
    WEAVER_I18N_SERVICE("weaver-i18n-service", "i18n", ""),
    WEAVER_ESB_SETTING_SERVICEESB("weaver-esb-setting-serviceesb", "动作流监控", "esb"),
    WEAVER_DATASECURITY("weaver-datasecurity", "表单加密", "datasecurity"),
    WEAVER_SALARY_REPORT("weaver-salary-report", "薪酬", null),
    WEAVER_HR_SERVICE("weaver-hr-service", "人事", null),
    WEAVER_ESB_SETTING_SERVICEESBCUSTOM("weaver-esb-setting-serviceesbCustom", "动作流", "esb"),
    WEAVER_INC_BIZ_SERVICE("weaver-inc-biz-service", "发票云", "inc"),
    WEAVER_TENANT_SERVICE("weaver-tenant-service", "租户", null),
    WEAVER_EBUILDER_CONTRACT_SERVICECMDATAUFJXHS("weaver-ebuilder-contract-servicecmdatauFJXHS", "绩效核算", "ebuilder"),
    WEAVER_EBUILDER_CONTRACT_SERVICE("weaver-ebuilder-contract-service", "EB合同管理", null),
    WEAVER_MY_SERVICE("weaver-my-service", "待办事项", null),
    WEAVER_FNA_EXPENSE_SERVICE("weaver-fna-expense-service", "电子费控", "fna"),
    WEAVER_SIGNATURE_SERVICE("weaver-signature-service", "签名", null),
    WEAVER_MAIL_BASE_SERVICE("weaver-mail-base-service", "邮件", "email"),
    WEAVER_HRM_SALARY("weaver-hrm-salary", "薪酬", "hrmsalary"),
    WEAVER_SECURITY_FRAMEWORK_SERVICE("weaver-security-framework-service", "系统安全", ""),
    WEAVER_ODOCEXCHANGE_SERVICE("weaver-odocexchange-service", "公文交换中心", "odocexchange"),
    WEAVER_ARCHIVE_CORE_SERVICE("weaver-archive-core-service", "档案管理", "archive"),
    WEAVER_RECRUIT_SERVICE("weaver-recruit-service", "招聘管理", "recruit"),
    WEAVER_BASIC_ONLINE_WEB_SERVICE("weaver-basic-online-web-service", "基础在线服务", null),
    WEAVER_ESEARCH_SEARCH_SERVICE("weaver-esearch-search-service", "微搜", null),
    WEAVER_INTUNIFYTODO_SERVER_CONFIG_SERVICE("weaver-intunifytodo-server-config-service", "统一审批中心", "intunifytodos"),
    WEAVER_INTUNIFYTODO_CLIENT_CONFIG_SERVICE("weaver-intunifytodo-client-config-service", "统一待办推送", "intunifytodoc"),
    WEAVER_COMPONENT_WEB_SERVICE("weaver-component-web-service", "公共数据源", "common"),
    WEAVER_ANALYZE_SERVICE("weaver-analyze-service", "数据分析", "analyze"),
    WEAVER_FNA_EXPENSE_SERVICEFEXS("weaver-fna-expense-servicefexs", "财务模块", "fna"),
    WEAVER_I18N_COMMON_SERVICE("weaver-i18n-common-service", "i18n", "i18n"),
    WEAVER_ESB_SETTING_SERVICEESBCONNECT("weaver-esb-setting-serviceesbConnect", "动作流", "esb"),
    WEAVER_PASSPORT_SERVICE("weaver-passport-service", "登录服务", "passport"),
    WEAVER_WORKFLOW_REPORT_SERVICEWORKFLOWFORMREPORT("weaver-workflow-report-serviceworkflowFormReport", "工作流表单", "workflow"),
    WEAVER_INTUNIFYAUTH_SERVER_BASE_SERVICE("weaver-intunifyauth-server-base-service", "统一认证中心", null),
    WEAVER_INTSAP_WEB_SERVICE("weaver-intsap-web-service", "sap数据源", "intsap");

    private final String id;
    private final String name;
    private final String serviceMark;

    DatasourceGroupType(String id, String name, String serviceMark) {
        this.id = id;
        this.name = name;
        this.serviceMark = serviceMark;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getServiceMark() {
        return serviceMark;
    }

    // 根据ID查找枚举
    public static DatasourceGroupType fromId(String id) {
        for (DatasourceGroupType service : DatasourceGroupType.values()) {
            if (service.id.equals(id)) {
                return service;
            }
        }
        return null;
    }
}
