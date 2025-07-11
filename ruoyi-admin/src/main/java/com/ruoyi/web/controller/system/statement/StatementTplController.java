package com.ruoyi.web.controller.system.statement;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.system.domain.statement.vo.StatementDataTplVo;
import com.ruoyi.system.domain.statement.vo.StatementTplVo;
import com.ruoyi.system.service.StatementTplService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/stmt-tpl")
public class StatementTplController extends BaseController {
    @Resource
    private StatementTplService statementTplService;

    /**
     * 更新模板文件
     * @param tplId
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/{tplId}")
    public AjaxResult editTplFile(@PathVariable Integer tplId, MultipartFile file) throws Exception {
        StatementTpl byTplId = checkStmtTpl(tplId);
        checkSuffix(file, byTplId.getSuffix());
        return success(statementTplService.editTplFile(byTplId, file));
    }

    /**
     * 加载报表模板文件列表
     * @param stmtCode
     * @param corpCode
     * @param tplFilename
     * @return
     */
    @GetMapping
    public AjaxResult loadStatementTplList(String stmtCode, String corpCode, String tplFilename) {
        List<StatementTplVo> list = statementTplService.loadStatementTplList(stmtCode, corpCode, tplFilename);
        return success(list);
    }

    /**
     * 下载报表文件
     * @param response
     * @param tplId
     * @throws IOException
     */
    @GetMapping("/{tplId}/download")
    public void downloadTpl(HttpServletResponse response, @PathVariable Integer tplId) throws IOException {
        StatementTpl byTplId = checkStmtTpl(tplId);
        statementTplService.downloadTpl(response, byTplId);
    }

    /**
     * 加载报表数据模板文件
     * @param stmtCode
     * @return
     */
    @GetMapping("/{stmtCode}/data")
    public AjaxResult loadStatementDataTpl(@PathVariable String stmtCode) {
        StatementType byCode = StatementType.getByCode(stmtCode);
        if (byCode == null) {
            throw new ServiceException(String.format(MsgConstants.UNKNOWN_STMT_CODE_V1, stmtCode));
        }
        List<StatementDataTplVo> list = statementTplService.loadStatementDataTpl(byCode);
        return success(list);
    }

    /**
     * 上传报表数据模板文件
     * @param stmtCode
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/{stmtCode}/data/upload")
    public AjaxResult uploadDataTplFile(@PathVariable String stmtCode, MultipartFile file) throws Exception {
        String filename = checkFilename(file.getOriginalFilename());
        StatementType byCode = checkStmtCode(stmtCode);

        Path rootPath = Paths.get(RuoYiConfig.getStatementDataTempPath(byCode));
        if (Files.notExists(rootPath)) {
            Files.createDirectories(rootPath);
        }
        File tplFile = rootPath.resolve(filename).toFile();
        return success(statementTplService.editDataTplFile(tplFile, file));
    }

    /**
     * 更新报表数据模板文件
     * @param stmtCode
     * @param filename
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/{stmtCode}/data/edit")
    public AjaxResult editStatementDataTpl(@PathVariable String stmtCode, String filename, MultipartFile file) throws IOException {

        checkFilename(filename);
        checkFilename(file.getOriginalFilename());
        StatementType byCode = checkStmtCode(stmtCode);

        String dataRootPath = RuoYiConfig.getStatementDataTempPath(byCode);
        File dataFile = new File(dataRootPath, filename);
        if (!dataFile.exists()) {
            throw new ServiceException(String.format(MsgConstants.DATA_TPL_NOT_EXISTS, filename));
        }

        checkSuffix(file, FileUtils.getNameSuffix(filename));

        String path = statementTplService.editDataTplFile(dataFile, file);
        return success(path);
    }

    /**
     * 下载报表数据模板文件
     * @param response
     * @param stmtCode
     * @param filename
     * @return
     */
    @GetMapping("/{stmtCode}/data/download")
    public void downloadDataTpl(HttpServletResponse response, @PathVariable String stmtCode, String filename) throws IOException {
        checkFilename(filename);
        StatementType statementType = checkStmtCode(stmtCode);
        statementTplService.downloadDataTpl(response, statementType, filename);
    }

    /**
     * 校验模板文件后缀名
     * @param file
     * @param originalSuffix
     */
    private void checkSuffix(MultipartFile file, String originalSuffix) {
        String suffix = FileUtils.getNameSuffix(file.getOriginalFilename());
        if (!originalSuffix.equals(suffix)) {
            throw new ServiceException(String.format(MsgConstants.TPL_SUFFIX_NEQ, originalSuffix));
        }
    }

    private String checkFilename(String filename) {
        filenameRequired(filename);
        String suffix = FileUtils.getNameSuffix(filename, false);
        if (!MimeTypeUtils.STATEMENT_EXTENSION.contains(suffix)) {
            throw new ServiceException(MsgConstants.STMT_TPL_FILE_SUFFIX_NOT_EXCEL);
        }
        return filename;
    }

    private void filenameRequired(String filename) {
        if (StringUtils.isEmpty(filename)) {
            throw new ServiceException(MsgConstants.FILENAME_REQUIRED);
        }
    }

    private StatementType checkStmtCode(String stmtCode) {
        StatementType byCode = StatementType.getByCode(stmtCode);
        if (byCode == null) {
            throw new ServiceException(String.format(MsgConstants.UNKNOWN_STMT_CODE_V1, stmtCode));
        }
        return byCode;
    }

    private StatementTpl checkStmtTpl(Integer tplId) {
        StatementTpl byTplId = StatementTpl.getByTplId(tplId);
        if (byTplId == null) {
            throw new ServiceException(MsgConstants.UNKNOWN_STMT_TPL_ID);
        }
        return byTplId;
    }
}
