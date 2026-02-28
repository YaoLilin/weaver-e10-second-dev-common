package com.weaver.seconddev.hnweaver.common.constants;

/**
 * @author 姚礼林
 * @desc RPC服务分组枚举类，用于统一管理@RpcReference注解中的group值
 * @date 2025/9/26
 **/
public enum RpcGroup {
    /**
     * 工作流相关服务
     */
    WORKFLOW(RpcGroupConstants.WORKFLOW, "工作流服务"),
    
    /**
     * 表单数据报告服务
     */
    FORMDATAREPORT(RpcGroupConstants.FORMDATAREPORT, "表单数据报告服务"),
    
    /**
     * e-builder表单相关服务
     */
    EBUILDER_FORM(RpcGroupConstants.EBUILDER_FORM, "e-builder表单服务"),
    
    /**
     * e-builder卡片服务
     */
    EBUILDER_CARD(RpcGroupConstants.EBUILDER_CARD, "e-builder卡片服务"),
    
    /**
     * CRM服务
     */
    CRM(RpcGroupConstants.CRM, "CRM服务"),
    
    /**
     * 任务服务
     */
    TASK(RpcGroupConstants.TASK, "任务服务"),
    
    /**
     * 项目服务
     */
    PROJECT(RpcGroupConstants.PROJECT, "项目服务"),
    
    /**
     * HR服务
     */
    HR(RpcGroupConstants.HR, "HR服务"),
    
    /**
     * 博客服务
     */
    BLOG(RpcGroupConstants.BLOG, "博客服务"),
    
    /**
     * 云服务
     */
    CLOUD(RpcGroupConstants.CLOUD, "云服务"),
    
    /**
     * 文档服务
     */
    DOC(RpcGroupConstants.DOC, "文档服务"),
    
    /**
     * 文档管理服务
     */
    DOCUMENT(RpcGroupConstants.DOCUMENT, "文档管理服务"),
    
    /**
     * e-builder工作流服务
     */
    EBUILDER_WORKFLOW(RpcGroupConstants.EBUILDER_WORKFLOW, "e-builder工作流服务"),
    
    /**
     * 工作报告服务
     */
    WORKREPORT(RpcGroupConstants.WORKREPORT, "工作报告服务"),
    
    /**
     * 公文服务
     */
    ODOC(RpcGroupConstants.ODOC, "公文服务"),
    
    /**
     * 财务服务
     */
    FNA(RpcGroupConstants.FNA, "财务服务"),
    
    /**
     * 人力资源服务
     */
    HRM(RpcGroupConstants.HRM, "人力资源服务"),
    
    /**
     * 人力资源薪酬服务
     */
    HRM_SALARY(RpcGroupConstants.HRM_SALARY, "人力资源薪酬服务"),
    
    /**
     * 集成中心服务
     */
    INTCENTER(RpcGroupConstants.INTCENTER, "集成中心服务"),
    
    /**
     * 目标管理服务
     */
    GOAL(RpcGroupConstants.GOAL, "目标管理服务"),
    
    /**
     * 客户服务
     */
    CUSTOMER_SERVICE(RpcGroupConstants.CUSTOMER_SERVICE, "客户服务"),
    
    /**
     * 会议服务
     */
    MEETING(RpcGroupConstants.MEETING, "会议服务"),
    
    /**
     * 发票云服务
     */
    INC(RpcGroupConstants.INC, "发票云服务"),
    
    /**
     * 治理服务
     */
    GOVERN(RpcGroupConstants.GOVERN, "治理服务"),
    
    /**
     * BCW服务
     */
    BCW(RpcGroupConstants.BCW, "BCW服务"),
    
    /**
     * SCRM服务
     */
    SCRM(RpcGroupConstants.SCRM, "SCRM服务"),
    
    /**
     * 合同服务
     */
    CONTRACT(RpcGroupConstants.CONTRACT, "合同服务"),
    
    /**
     * 订单表单服务
     */
    ORDERFORM(RpcGroupConstants.ORDERFORM, "订单表单服务"),
    
    /**
     * 客户服务
     */
    CUSTOMER(RpcGroupConstants.CUSTOMER, "客户服务"),
    
    /**
     * 销售机会服务
     */
    SALE_CHANCE(RpcGroupConstants.SALE_CHANCE, "销售机会服务"),
    
    /**
     * 线索服务
     */
    CLUE(RpcGroupConstants.CLUE, "线索服务"),
    
    /**
     * 竞争对手服务
     */
    COMPETITOR(RpcGroupConstants.COMPETITOR, "竞争对手服务"),
    
    /**
     * 联系人服务
     */
    CONTACT(RpcGroupConstants.CONTACT, "联系人服务"),
    
    /**
     * 产品服务
     */
    PRODUCTION(RpcGroupConstants.PRODUCTION, "产品服务"),
    
    /**
     * 报价服务
     */
    QUOTE(RpcGroupConstants.QUOTE, "报价服务"),
    
    /**
     * 市场服务
     */
    MARKET(RpcGroupConstants.MARKET, "市场服务"),
    
    /**
     * 议程服务
     */
    AGENDA(RpcGroupConstants.AGENDA, "议程服务"),
    
    /**
     * 主线服务
     */
    MAINLINE(RpcGroupConstants.MAINLINE, "主线服务"),
    
    /**
     * KPI服务
     */
    KPI(RpcGroupConstants.KPI, "KPI服务"),
    
    /**
     * 考勤服务
     */
    ATTEND(RpcGroupConstants.ATTEND, "考勤服务"),
    
    /**
     * 标签服务
     */
    TAG(RpcGroupConstants.TAG, "标签服务");

    private final String value;
    private final String description;

    RpcGroup(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
