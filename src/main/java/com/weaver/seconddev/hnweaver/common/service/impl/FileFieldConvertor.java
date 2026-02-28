package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.common.form.dto.data.FormDataOptionDto;
import com.weaver.common.form.metadata.ModuleSource;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.exception.FieldConvertException;
import com.weaver.seconddev.hnweaver.common.exception.FormDataImportException;
import com.weaver.seconddev.hnweaver.common.service.FieldConvertor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author 姚礼林
 * @desc 文件上传字段转换
 * @date 2025/9/18
 **/
@Slf4j
@Component("fileFieldConvertor")
public class FileFieldConvertor extends AbstractFileFieldConvertor {

    /**
     * 将字段数据转换为附件字段
     *
     * @param fieldData 字段数据
     * @param field     字段对象
     * @throws FieldConvertException 如果转换过程发生异常，如文件读取错误，则抛出此异常
     * @return 转换后的字段对象
     */
    @Override
    public EBDataReqDetailDto convert(FormFieldData fieldData, FormField field) throws FieldConvertException{
        EBDataReqDetailDto dto = new EBDataReqDetailDto(field.getId().toString(), fieldData.getValue());
        List<FormDataOptionDto> options;
        try {
            options = handleFileField(fieldData);
        } catch (IOException e) {
            throw new FieldConvertException("处理文件类型字段异常", e);
        }
        dto.setDataOptions(options);
        return dto;
    }

    private static @NotNull List<FormDataOptionDto> handleFileField(FormFieldData fieldData) throws IOException {
        if (CharSequenceUtil.isBlank(fieldData.getFilePath())) {
            log.debug("该字段没有文件路径，字段：{}", fieldData.getFieldName());
            return Collections.emptyList();
        }
        Path path = Paths.get(fieldData.getFilePath());
        if (!Files.exists(path)) {
            throw new FileNotFoundException("该文件不存在，文件路径：" + path);
        }
        log.info("转换附件字段，文件路径:{}", path);
        List<FormDataOptionDto> options = new ArrayList<>();
        if (Files.isDirectory(path)) {
            // 如果路径是目录，则将目录下的所有文件添加到字段中
            options.addAll(addDirAllFilesToField(path));
        }else {
            // 如果路径是文件，则将文件添加到字段中
            FormDataOptionDto optionDto = new FormDataOptionDto();
            optionDto.setFileByte(Files.readAllBytes(path));
            optionDto.setContent(path.getFileName().toString());
            optionDto.setType(ModuleSource.file);
            options.add(optionDto);
        }

        return options;
    }

    private static List<FormDataOptionDto> addDirAllFilesToField(Path path) {
        List<FormDataOptionDto> options = new ArrayList<>();
        try (Stream<Path> files = Files.walk(path)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                log.info("将此文件添加到字段，文件：{}", file);
                FormDataOptionDto option = new FormDataOptionDto();
                try {
                    option.setFileByte(Files.readAllBytes(file));
                } catch (IOException e) {
                    throw new FormDataImportException("将文件添加到字段发生异常，文件路径：" + file, e);
                }
                option.setContent(file.getFileName().toString());
                option.setType(ModuleSource.file);
                options.add(option);
            });
        } catch (IOException e) {
            throw new FormDataImportException("获取文件目录中的附件发生异常，文件路径："+ path, e);
        }
        log.info("文件数量：{}", options.size());
        return options;
    }
}
