package com.ruoyi.system.service;

import com.ruoyi.common.enums.statement.StatementTpl;
import com.ruoyi.common.enums.statement.StatementType;
import com.ruoyi.system.domain.statement.dto.StatementTplDto;
import com.ruoyi.system.domain.statement.vo.StatementDataTplVo;
import com.ruoyi.system.domain.statement.vo.StatementTplVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface StatementTplService {
    /**
     * 更新报表模板文件
     * @param statementTpl  报表模板
     * @param file          文件
     * @return
     */
    String editTplFile(StatementTpl statementTpl, MultipartFile file) throws IOException;

    /**
     * 加载报表模板文件列表
     * @param stmtCode
     * @param corpCode
     * @param tplFilename
     * @return
     */
    List<StatementTplVo> loadStatementTplList(String stmtCode, String corpCode, String tplFilename);

    /**
     * 下载报表配置文件
     * @param response
     * @param tpl
     */
    void downloadTpl(HttpServletResponse response, StatementTpl tpl) throws IOException;

    /**
     * 查找对应报表下的数据报表模板
     * @param statementType
     * @return
     */
    List<StatementDataTplVo> loadStatementDataTpl(StatementType statementType);

    /**
     * 更新数据模板文件
     * @param dataFile
     * @param file
     * @return
     * @throws IOException
     */
    String editDataTplFile(File dataFile, MultipartFile file) throws IOException;

    /**
     * 下载数据模板文件
     * @param response
     * @param statementType
     * @param filename
     */
    void downloadDataTpl(HttpServletResponse response, StatementType statementType, String filename) throws IOException;
}
