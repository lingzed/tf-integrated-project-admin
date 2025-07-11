package com.ruoyi.system.service.impl;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.system.domain.statement.vo.StatementDataTplVo;
import com.ruoyi.system.domain.statement.vo.StatementTplVo;
import com.ruoyi.system.service.DownloadService;
import com.ruoyi.system.service.StatementTplService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatementTplServiceImpl implements StatementTplService {
    @Resource
    private DownloadService downloadService;

    @Override
    public String editTplFile(StatementTpl statementTpl, MultipartFile file) throws IOException {
        // 校验文件大小
        FileUploadUtils.checkFileLength(file);

        // 获取报表文件的根目录文件对象
        String rootPath = RuoYiConfig.getStatementTempPath(statementTpl.getStatementType());
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        File tplFile = new File(rootFile, statementTpl.getTplFilename());
        file.transferTo(tplFile);
        return tplFile.getAbsolutePath();
    }

    @Override
    public List<StatementTplVo> loadStatementTplList(String stmtCode, String corpCode, String tplFilename) {
        return Stream.of(StatementTpl.values())
                // 过滤报表编码
                .filter(tpl -> {
                    if (StringUtils.isEmpty(stmtCode)) return true;
                    return tpl.getStatementType().getStatementCode().equals(stmtCode);
                })
                .filter(tpl -> {
                    if (StringUtils.isEmpty(corpCode)) return true;
                    return tpl.getCorpCode().equals(corpCode);
                })
                // 过滤模板文件名
                .filter(tpl -> {
                    if (StringUtils.isEmpty(tplFilename)) return true;
                    return tpl.getTplFilename().contains(tplFilename);
                }).map(tpl -> {
                    StatementTplVo vo = new StatementTplVo();
                    BeanUtils.copyProperties(tpl, vo);
                    vo.setStatementCode(tpl.getStatementType().getStatementCode());
                    vo.setStatementName(tpl.getStatementType().getStatementName());
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public void downloadTpl(HttpServletResponse response, StatementTpl tpl) throws IOException {
        String filename = tpl.getTplFilename();
        String error = String.format(MsgConstants.TPL_NOT_EXISTS, filename);
        downloadTpl(response, RuoYiConfig.getStatementTempPath(tpl.getStatementType()), filename, error);
    }

    @Override
    public List<StatementDataTplVo> loadStatementDataTpl(StatementType statementType) {
        String dataTempPath = RuoYiConfig.getStatementDataTempPath(statementType);
        File file = new File(dataTempPath);
        if (!file.exists()) return Collections.emptyList();

        File[] files = file.listFiles();
        if (files == null || files.length == 0) return Collections.emptyList();

        return Stream.of(files)
                .filter(File::isFile)
                .filter(f -> {
                    String suffix = FileUtils.getNameSuffix(f.getName(), false);
                    return MimeTypeUtils.STATEMENT_EXTENSION.contains(suffix);
                })
                .map(f -> {
                    StatementDataTplVo vo = new StatementDataTplVo();
                    vo.setStatementCode(statementType.getStatementCode());
                    vo.setTplFilename(f.getName());
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public String editDataTplFile(File dataFile, MultipartFile file) throws IOException {
        // 校验文件大小
        FileUploadUtils.checkFileLength(file);

        file.transferTo(dataFile);
        return dataFile.getAbsolutePath();
    }

    @Override
    public void downloadDataTpl(HttpServletResponse response, StatementType statementType, String filename) throws IOException {
        String rootDataPath = RuoYiConfig.getStatementDataTempPath(statementType);
        String error = String.format(MsgConstants.DATA_TPL_NOT_EXISTS, filename);
        downloadTpl(response, rootDataPath, filename, error);
    }

    /**
     * 下载模板文件
     * @param response
     * @param rootPath
     * @param filename
     * @param error
     * @throws IOException
     */
    private void downloadTpl(HttpServletResponse response, String rootPath, String filename, String error) throws IOException {
        File file = new File(rootPath, filename);
        if (!file.exists()) {
            throw new ServiceException(error);
        }
        downloadService.download(response, file);
    }
}
