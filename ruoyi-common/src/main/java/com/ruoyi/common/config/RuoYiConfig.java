package com.ruoyi.common.config;

import com.ruoyi.common.enums.statement.StatementType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 读取项目相关配置
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "ruoyi")
public class RuoYiConfig {
    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;

    /**
     * 上传路径
     */
    private static String profile;

    /**
     * 获取地址开关
     */
    private static boolean addressEnabled;

    /**
     * 验证码类型
     */
    private static String captchaType;

    private static InitCache initCache;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyrightYear() {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear) {
        this.copyrightYear = copyrightYear;
    }

    public static String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        RuoYiConfig.profile = profile;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        RuoYiConfig.addressEnabled = addressEnabled;
    }

    public static String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        RuoYiConfig.captchaType = captchaType;
    }

    /**
     * 获取导入上传路径
     */
    public static String getImportPath() {
        return getProfile() + "/import";
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }

    /**
     * 获取报表模板跟路径
     *
     * @return
     */
    public static String getStatementTempRootPath() {
        Path path = Paths.get(getProfile(), "statement-template");
        return path.toAbsolutePath().toString();
    }

    /**
     * 获取生成的临时报表根目录
     *
     * @return
     */
    public static String getStatementGenRootPath() {
        Path path = Paths.get(getProfile(), "statement-gen-temp");
        return path.toAbsolutePath().toString();
    }

    /**
     * 通过报表类型获取具体的报表模板的路径
     *
     * @param statementType
     * @return
     */
    public static String getStatementTempPath(StatementType statementType) {
        Path path = Paths.get(getStatementTempRootPath(), statementType.getStatementName());
        return path.toAbsolutePath().toString();
    }

    /**
     * 通过报表类型获取具体报表的数据模板的路径
     *
     * @param statementType
     * @return
     */
    public static String getStatementDataTempPath(StatementType statementType) {
        Path path = Paths.get(getStatementTempPath(statementType), "数据模板");
        return path.toAbsolutePath().toString();
    }

    public static String getKhDzdTempPath() {
        String statementGenRootPath = getStatementGenRootPath();
        return "C:\\Users\\ling\\Desktop\\客户对账单.xlsx";
    }

    /**
     * 初始化缓存
     */
    public static class InitCache {
        private static Boolean initStmtCfg; // 初始化报表配置缓存
        private static Boolean initSubj;    // 初始化科目缓存

        public static Boolean getInitStmtCfg() {
            return initStmtCfg;
        }

        public void setInitStmtCfg(Boolean initStmtCfg) {
            InitCache.initStmtCfg = initStmtCfg;
        }

        public static Boolean getInitSubj() {
            return initSubj;
        }

        public void setInitSubj(Boolean initSubj) {
            InitCache.initSubj = initSubj;
        }
    }

    public static InitCache getInitCache() {
        return initCache;
    }

    public void setInitCache(InitCache initCache) {
        RuoYiConfig.initCache = initCache;
    }
}
