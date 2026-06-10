package com.weaver.seconddev.hnweaver.common.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.weaver.common.form.dto.data.FormDataOptionDto;
import com.weaver.common.form.metadata.ModuleSource;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.exception.FieldConvertException;
import com.weaver.seconddev.hnweaver.common.exception.FormDataImportException;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;

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
            path = resolveCaseInsensitivePath(path);
        }
        log.debug("转换附件字段，文件路径:{}", path);
        List<FormDataOptionDto> options = new ArrayList<>();
        if (Files.isDirectory(path)) {
            // 如果路径是目录，则将目录下的所有文件添加到字段中
            options.addAll(addDirAllFilesToField(path));
        }else {
            // 如果路径是文件，则将文件添加到字段中
            FormDataOptionDto optionDto = new FormDataOptionDto();
            byte[] fileBytes = Files.readAllBytes(path);
            log.info("读取附件文件: {}, 大小: {} bytes", path.getFileName(), fileBytes.length);
            if (fileBytes.length > 20 * 1024 * 1024) {
                log.warn("单文件超过 20MB: {}, 大小: {} bytes", path.getFileName(), fileBytes.length);
            }
            optionDto.setFileByte(fileBytes);
            optionDto.setContent(path.getFileName().toString());
            optionDto.setType(ModuleSource.file);
            options.add(optionDto);
        }

        return options;
    }

    /**
     * 在父目录中查找与给定文件名大小写不匹配的实际文件路径<br>
     * 由于用户上传的文件名与实际存储在磁盘上的文件名可能存在大小写差异，
     * 该方法通过遍历父目录下的所有文件进行大小写不敏感匹配来修正路径
     *
     * @param path 原始文件路径
     * @return 修正后的实际文件路径
     * @throws FileNotFoundException 如果父目录中不存在任何大小写不敏感匹配的文件
     */
    private static Path resolveCaseInsensitivePath(Path path) throws FileNotFoundException {
        Path parent = path.getParent();
        if (parent == null || !Files.isDirectory(parent)) {
            throw new FileNotFoundException("该文件不存在，文件路径：" + path);
        }
        String fileName = path.getFileName().toString();
        try (Stream<Path> files = Files.list(parent)) {
            Path matched = files.filter(f -> f.getFileName().toString().equalsIgnoreCase(fileName))
                    .findFirst()
                    .orElseThrow(() -> new FileNotFoundException("该文件不存在，文件路径：" + path));
            log.debug("通过大小写不敏感匹配修正文件路径: {} -> {}", path, matched);
            return matched;
        } catch (IOException e) {
            throw new FileNotFoundException("查找文件时发生异常，文件路径：" + path);
        }
    }

    private static List<FormDataOptionDto> addDirAllFilesToField(Path path) {
        List<FormDataOptionDto> options = new ArrayList<>();
        try (Stream<Path> files = Files.walk(path)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                log.debug("将此文件添加到字段，文件：{}", file);
                FormDataOptionDto option = new FormDataOptionDto();
                try {
                    byte[] fileBytes = Files.readAllBytes(file);
                    log.info("读取目录附件文件: {}, 大小: {} bytes", file.getFileName(), fileBytes.length);
                    if (fileBytes.length > 20 * 1024 * 1024) {
                        log.warn("目录下文件超过 20MB: {}, 大小: {} bytes", file.getFileName(), fileBytes.length);
                    }
                    option.setFileByte(fileBytes);
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
