# 泛微 E10 公共类库说明
本项目为本人开发的公共类库，包含了通用的工具类和组件，为二次开发提供便利，你可以把它引入到自己的二开项目中。
大家可以上传自己的代码到本项目中，完善此项目。

## 本项目宗旨
我希望它能打造成一个大家都可以用到的公共类库，每个人都可以上传自己的代码，这样大家都可以用到。

## 代码上传说明
代码需符合规范，例如阿里巴巴规范，谷歌规范。不得修改现有方法签名，防止破坏正在使用到的项目。不能使用外部依赖。

## 使用方法
### 一、将源码放入你的项目
本项目属于泛微二次开发，需自行搭建泛微二次开发环境。  
下载此项目，将源码放入你的项目中，建议单独放入一个模块，打包部署到系统时合并此模块的代码到你的 jar 包中。
### 二、最简单方法-使用本项目下的jar包
下载本项目下的 `secondev-liuzhou-common-1.0.0.jar` 包，在你的项目中引入此 jar 包，将它作为外部依赖部署到系统中。

## 主要功能 👏
### SqlExecuteClient - sql执行客户端

**路径**：`com.weaver.seconddev.hnweaver.common.SqlExecuteClient`  
**说明**：sql 执行的封装。E10 的 sql 执行相比 E9 更复杂一些，`SqlExecuteClient` 可以简化 sql 执行操作，可以像 E9 的
`RecordSet` 一样传入 sql 语句和参数即可执行 sql

**功能**：

- SQL执行封装：简化E10复杂的SQL执行操作，提供类似E9 RecordSet的使用体验
- 参数化查询：支持SqlParam对象数组和Object数组两种参数传递方式
- 查询和修改支持：同时支持SELECT查询和INSERT/UPDATE/DELETE修改操作
- 字段值获取：提供忽略大小写的字段值获取工具方法

**使用示例**：  
```java
String sql = "SELECT " + fieldName + " FROM " + tableName + " WHERE id = ?";
SqlExecuteResult result = sqlExecuteClient.executeSql(groupType, sql, dataId);
if (!result.isSuccess()) {
    log.error("查询字段值失败，sql：{}，错误信息：{}", sql, result.getMessage());
    throw new SqlExecuteException("查询字段值失败，sql：" + sql + ",错误信息：" + result.getMessage());
}
// 获取查询数据
List<Map<String, Object>> records = result.getRecords();
```
### AbstractEsbAction - ESB 动作流抽象类

**路径**：`com.weaver.seconddev.hnweaver.common.AbstractEsbAction`  
**说明**：可实现此抽象类来替换标准的 `EsbServerlessRpcRemoteInterface` 接口，可明确传入参数和返回参数类型，
替换原来的 Map 形式。在原来的标准接口中传入参数和返回类型是 Map ，在使用时会让类型变得不明确，在 `AbstractEsbAction`
类这些都可以为明确的对象类型，你可以创建传入的参数和返回结果的类型。

**功能**：

- 类型安全：支持明确的输入参数类型T和输出参数类型R，避免Map类型的不确定性
- 参数转换：自动将Map类型的输入参数转换为具体的对象类型
- 异常处理：内置异常捕获和日志记录，提供详细的错误信息
- 结果封装：统一的结果返回格式，简化错误处理

**使用示例**：  
```java
@Slf4j
@RequiredArgsConstructor
@Service("MessagePushToWiseduHelpAction")
public class MessagePushToWiseduHelpAction extends AbstractEsbAction<MessagePushToWiseduHelpAction.InputParams,
        MessagePushToWiseduHelpAction.OutputParams> {

    private final JinzhiTokenManager tokenManager;
    private final HrmCommonEmployeeService employeeService;

    @Override
    protected WeaResult<OutputParams> doExecute(InputParams params) {
        if (StrUtil.isBlank(params.getReceiverIds())) {
            log.error("[receiverIds] 参数为空");
            return WeaResult.fail("[receiverIds] 参数为空");
        }
        // ...业务代码
        return WeaResult.success(outputParams);
    }

    @Override
    protected InputParams convertToParamObj(Map<String, Object> params) {
        return convertToParamObj(params, InputParams.class);
    }

    /**
     * 自定义传入参数类型
     */
    @Data
    public static class InputParams {
        private String receiverIds;
    }

    /**
     * 自定义输出参数类型
     */
    @Data
    public static class OutputParams {
        private String token;
        private String sign;
        private List<String> receiverJobNums;
    }
}
```

### EbFormDataChangeClient - EB 表单数据修改客户端

**路径**：`com.weaver.seconddev.hnweaver.common.EbFormDataChangeClient`  
**说明**：EB表单数据修改客户端，用于导入和修改ebuilder表单数据。支持批量插入、更新表单数据，可根据ID、条件或条件字段进行批量更新，支持字段类型转换、文件字段转换、异步处理等功能。适用于需要批量操作表单数据的场景。

**功能**：

- 批量插入：支持同一表单的批量数据插入操作
- 多种更新方式：支持按ID更新、按条件更新、按条件字段更新
- 字段转换：支持自定义字段类型转换和文件字段转换
- 明细表处理：支持主表和明细表数据的级联更新
- 异步处理：支持同步和异步的数据处理模式

### FieldInfoService - 字段信息接口

**路径**：`com.weaver.seconddev.hnweaver.common.service.FieldInfoService`  
**说明**：表单字段信息服务接口，提供获取表单字段信息的功能。支持根据表单ID获取所有字段信息、根据字段名称或ID获取特定字段信息，以及获取字段选项名称等操作。用于表单字段的元数据查询和管理。

**功能**：

- 字段信息查询：根据表单ID获取所有字段信息，支持主表和明细表字段
- 字段名称查询：根据表单ID和字段名称获取特定字段信息
- 字段ID查询：根据字段ID直接获取字段信息
- 选项名称获取：根据字段ID和值获取字段选项的显示名称

### FormDataService - 表单数据接口

**路径**：`com.weaver.seconddev.hnweaver.common.service.FormDataService`  
**说明**：工作流表单数据服务接口，提供表单字段值的查询、更新和存在性检查功能。支持根据表名和字段名进行字段值的CRUD操作，用于在工作流中对表单数据进行基本的读写操作。

**功能**：

- 字段值查询：根据表名、字段名和数据ID查询字段值
- 字段值更新：根据表名、字段名和数据ID更新字段值
- 存在性检查：检查指定字段值是否在数据库中存在
- 数据源支持：支持不同数据源组的数据库操作

### FormInfoService - 表单信息接口

**路径**：`com.weaver.seconddev.hnweaver.common.service.FormInfoService`  
**说明**：表单信息服务接口，提供获取表单基本信息的功能。支持根据表单ID或表名查询表单信息，以及获取明细表和主表之间的关联关系。用于表单元数据的查询和管理。

**功能**：

- 表单ID查询：根据表单ID获取完整的表单信息
- 表名查询：根据数据库表名获取表单信息
- 明细表查询：根据表名获取明细表ID
- 主从表关联：根据明细表名查询对应的主表ID

### WorkflowInfoService - 工作流信息接口

**路径**：`com.weaver.seconddev.hnweaver.common.service.WorkflowInfoService`  
**说明**：工作流信息服务接口，提供工作流相关的数据查询功能。支持根据流程请求ID查询对应的表单数据ID，用于在工作流处理过程中获取表单数据的关联信息。

**功能**：

- 请求ID映射：根据工作流请求ID查询对应的表单数据ID
- 数据关联查询：支持工作流与表单数据的关联关系获取

## 更多功能
详细信息请看源码

## 欢迎贡献 🎉
如果你有好的工具和功能，欢迎贡献代码
