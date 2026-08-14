package com.weaver.seconddev.hnweaver.common.util;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.constants.ActionParam;
import com.weaver.seconddev.hnweaver.common.constants.ParamType;
import com.weaver.seconddev.hnweaver.common.domain.dto.ActionParamDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WorkflowActionParamParser 单元测试<br>
 * <p>
 * 验证 AbstractEsbAction 子类能正确获取输入、输出参数类型，并通过解析器生成对应的参数结构。
 * </p>
 *
 * @author 姚礼林
 * @date 2026/7/9
 */
@DisplayName("WorkflowActionParamParser 参数类型解析测试")
class WorkflowActionParamParserTest {

    @Test
    @DisplayName("AbstractEsbAction子类应当能获取输入和输出参数类型")
    void shouldGetInputAndOutputTypesFromActionSubclass() {
        DemoAction action = new DemoAction();

        assertEquals(DemoInput.class, action.getParamType());
        assertEquals(DemoOutput.class, action.getResultType());
    }

    @Test
    @DisplayName("应当解析AbstractEsbAction子类的输入参数结构")
    void shouldParseInputParamsFromActionSubclass() {
        List<ActionParamDTO> inputParams = WorkflowActionParamParser.parseInputParams(DemoAction.class);

        assertEquals(6, inputParams.size());

        ActionParamDTO requestId = inputParams.get(0);
        assertEquals("requestId", requestId.getName());
        assertEquals("请求ID", requestId.getShowName());
        assertEquals(ParamType.STRING, requestId.getType());

        ActionParamDTO amount = inputParams.get(1);
        assertEquals("amount", amount.getName());
        assertEquals("amount", amount.getShowName());
        assertEquals(ParamType.NUMBER, amount.getType());
        assertEquals(Integer.class.getName(), amount.getJavaType());

        ActionParamDTO detail = inputParams.get(2);
        assertEquals("detail", detail.getName());
        assertEquals("明细对象", detail.getShowName());
        assertEquals(ParamType.OBJECT, detail.getType());
        assertNotNull(detail.getChildren());
        assertEquals(2, detail.getChildren().size());
        assertEquals("code", detail.getChildren().get(0).getName());
        assertEquals("detailName", detail.getChildren().get(1).getName());

        ActionParamDTO names = inputParams.get(3);
        assertEquals("names", names.getName());
        assertEquals(ParamType.STRING, names.getType());
        assertEquals(String.class.getName(), names.getJavaType());
        assertEquals(Boolean.TRUE, names.getArray());

        ActionParamDTO scores = inputParams.get(4);
        assertEquals("scores", scores.getName());
        assertEquals(ParamType.NUMBER, scores.getType());
        assertEquals(Double.class.getName(), scores.getJavaType());
        assertEquals(Boolean.TRUE, scores.getArray());

        ActionParamDTO recordIds = inputParams.get(5);
        assertEquals("recordIds", recordIds.getName());
        assertEquals(ParamType.STRING, recordIds.getType());
        assertEquals(Long.class.getName(), recordIds.getJavaType());
        assertEquals(Boolean.TRUE, recordIds.getArray());
    }

    @Test
    @DisplayName("应当解析AbstractEsbAction子类的输出参数结构")
    void shouldParseOutputParamsFromActionSubclass() {
        List<ActionParamDTO> outputParams = WorkflowActionParamParser.parseOutputParams(DemoAction.class);

        assertEquals(2, outputParams.size());

        ActionParamDTO success = outputParams.get(0);
        assertEquals("success", success.getName());
        assertEquals(ParamType.BOOLEAN, success.getType());

        ActionParamDTO resultItems = outputParams.get(1);
        assertEquals("resultItems", resultItems.getName());
        assertEquals("结果列表", resultItems.getShowName());
        assertEquals(ParamType.OBJECT, resultItems.getType());
        assertEquals(Boolean.TRUE, resultItems.getArray());
        assertNotNull(resultItems.getChildren());
        assertFalse(resultItems.getChildren().isEmpty());
        assertEquals("itemCode", resultItems.getChildren().get(0).getName());
    }

    @Test
    @DisplayName("无无参构造器的Action应当能解析输入和输出参数")
    void shouldParseParamsWithoutInstantiatingAction() {
        List<ActionParamDTO> inputParams = WorkflowActionParamParser.parseInputParams(RequiredArgumentAction.class);
        List<ActionParamDTO> outputParams = WorkflowActionParamParser.parseOutputParams(RequiredArgumentAction.class);

        assertEquals(1, inputParams.size());
        assertEquals("input", inputParams.get(0).getName());
        assertEquals(1, outputParams.size());
        assertEquals("output", outputParams.get(0).getName());
    }

    @Test
    @DisplayName("未标注的嵌套对象和对象列表应当解析子属性")
    void shouldParseChildrenOfUnannotatedNestedFields() {
        List<ActionParamDTO> inputParams = WorkflowActionParamParser.parseInputParams(UnannotatedNestedAction.class);

        assertEquals(2, inputParams.size());
        assertEquals("obj", inputParams.get(0).getName());
        assertEquals(ParamType.OBJECT, inputParams.get(0).getType());
        assertNotNull(inputParams.get(0).getChildren());
        assertEquals("code", inputParams.get(0).getChildren().get(0).getName());
        assertEquals("objs", inputParams.get(1).getName());
        assertEquals(ParamType.OBJECT, inputParams.get(1).getType());
        assertEquals(Boolean.TRUE, inputParams.get(1).getArray());
        assertNotNull(inputParams.get(1).getChildren());
        assertEquals("name", inputParams.get(1).getChildren().get(1).getName());
    }

    @Test
    @DisplayName("Map类型输入参数应当解析为对象且不解析键值类型")
    void shouldParseMapInputParamAsObjectWithoutChildren() {
        List<ActionParamDTO> inputParams = WorkflowActionParamParser.parseInputParams(MapInputAction.class);

        assertEquals(1, inputParams.size());
        ActionParamDTO attributes = inputParams.get(0);
        assertEquals("attributes", attributes.getName());
        assertEquals(ParamType.OBJECT, attributes.getType());
        assertEquals(Map.class.getName(), attributes.getJavaType());
        assertNull(attributes.getChildren());
    }

    @Test
    @DisplayName("Long类型输入参数应当解析为文本")
    void shouldParseLongInputParamAsString() {
        List<ActionParamDTO> inputParams = WorkflowActionParamParser.parseInputParams(LongInputAction.class);

        assertEquals(1, inputParams.size());
        assertEquals("recordId", inputParams.get(0).getName());
        assertEquals(ParamType.STRING, inputParams.get(0).getType());
        assertEquals(Long.class.getName(), inputParams.get(0).getJavaType());
    }

    /**
     * 用于测试参数解析的示例 Action。
     */
    static class DemoAction extends AbstractEsbAction<DemoInput, DemoOutput> {

        @Override
        protected WeaResult<DemoOutput> doExecute(DemoInput params) {
            return WeaResult.success(new DemoOutput());
        }

        @Override
        protected DemoInput convertToParamObj(Map<String, Object> params) {
            return new DemoInput();
        }
    }

    /**
     * 模拟依赖注入构造器的 Action。
     */
    static class RequiredArgumentAction extends AbstractEsbAction<RequiredArgumentInput, RequiredArgumentOutput> {
        private final String dependency;

        RequiredArgumentAction(String dependency) {
            this.dependency = dependency;
        }

        @Override
        protected WeaResult<RequiredArgumentOutput> doExecute(RequiredArgumentInput params) {
            return WeaResult.success(new RequiredArgumentOutput());
        }

        @Override
        protected RequiredArgumentInput convertToParamObj(Map<String, Object> params) {
            return new RequiredArgumentInput();
        }
    }

    static class UnannotatedNestedAction extends AbstractEsbAction<UnannotatedNestedInput, DemoOutput> {
        @Override
        protected WeaResult<DemoOutput> doExecute(UnannotatedNestedInput params) {
            return WeaResult.success(new DemoOutput());
        }

        @Override
        protected UnannotatedNestedInput convertToParamObj(Map<String, Object> params) {
            return new UnannotatedNestedInput();
        }
    }

    static class MapInputAction extends AbstractEsbAction<MapInput, DemoOutput> {
        @Override
        protected WeaResult<DemoOutput> doExecute(MapInput params) {
            return WeaResult.success(new DemoOutput());
        }

        @Override
        protected MapInput convertToParamObj(Map<String, Object> params) {
            return new MapInput();
        }
    }

    static class LongInputAction extends AbstractEsbAction<LongInput, DemoOutput> {
        @Override
        protected WeaResult<DemoOutput> doExecute(LongInput params) {
            return WeaResult.success(new DemoOutput());
        }

        @Override
        protected LongInput convertToParamObj(Map<String, Object> params) {
            return new LongInput();
        }
    }

    /**
     * 示例输入参数。
     */
    static class DemoInput {
        @ActionParam(displayName = "请求ID", required = true)
        private String requestId;
        private Integer amount;
        @ActionParam(displayName = "明细对象")
        private DemoDetail detail;
        private List<String> names;
        private List<Double> scores;
        private List<Long> recordIds;
    }

    /**
     * 示例输出参数。
     */
    static class DemoOutput {
        private Boolean success;
        @ActionParam(displayName = "结果列表")
        private List<DemoItem> resultItems;
    }

    static class RequiredArgumentInput {
        private String input;
    }

    static class RequiredArgumentOutput {
        private String output;
    }

    static class UnannotatedNestedInput {
        private NestedParam obj;
        private List<NestedParam> objs;
    }

    static class MapInput {
        private Map<String, NestedParam> attributes;
    }

    static class LongInput {
        private Long recordId;
    }

    static class NestedParam {
        private String code;
        private String name;
    }

    /**
     * 示例嵌套对象。
     */
    static class DemoDetail {
        private String code;
        @ActionParam(displayName = "明细名称")
        private String detailName;
    }

    /**
     * 示例列表元素对象。
     */
    static class DemoItem {
        private String itemCode;
    }
}
