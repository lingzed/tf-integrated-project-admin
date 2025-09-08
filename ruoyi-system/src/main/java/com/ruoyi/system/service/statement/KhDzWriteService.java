package com.ruoyi.system.service.statement;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.enums.u8c.SubjDirection;
import com.ruoyi.common.u8c.AuxAcctProject;
import com.ruoyi.common.u8c.AuxBalance;
import com.ruoyi.common.u8c.subj.Subject;
import com.ruoyi.common.u8c.warpper.DetailWrapper;
import com.ruoyi.common.utils.ExcelUtils;
import com.ruoyi.common.utils.json.JsonUtil;
import com.ruoyi.system.service.KhDzCfgService;
import com.ruoyi.system.service.statement.impl.LrFjStmtGen;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户对账写入业务
 */
@Component
public class KhDzWriteService extends BaseStmtExtractor {
    private static final Logger log = LoggerFactory.getLogger(KhDzWriteService.class);
    private static final List<Subject> subjList = new ArrayList<>();

    private static final String[] SHEET1_PRE_HEAD = new String[]{"日期", "公司", "客户名称", "项目名称"}; // sheet1前缀表头
    private static final int[] SHEET1_PRE_HEAD_WIDTH = new int[]{14, 14, 38, 86};   // sheet1前缀表头宽度
    private static final int PER_HEAD1_LEN = SHEET1_PRE_HEAD.length;

    private static final String[] SHEET2_PRE_HEAD = new String[]{"日期", "账套号", "证号-分录", "客户编码", "客户名称", "项目编码", "项目名称", "摘要"}; // sheet2前缀表头
    private static final int[] SHEET2_PRE_HEAD_WIDTH = new int[]{11, 12, 10, 21, 22, 10, 74, 34};   // sheet2前缀表头宽度
    private static final int PER_HEAD2_LEN = SHEET2_PRE_HEAD.length;

    private static final String[] SUBJ_SUB_HEAD = new String[]{"支付（借）", "结算（贷）", "余额"}; // 科目表头子表头
    private static final int[] SUBJ_SUB_HEAD_WIDTH = new int[]{12, 12, 15}; // 科目表头子表头宽度
    private static final int SUB_LEN = SUBJ_SUB_HEAD.length;

    private static final String SUFFIX_HEAD = "全口径应收账款合计";  // 后缀表头
    private static final String SHEET1_NAME = "客户余额";
    private static final String SHEET2_NAME = "明细";
    private static final String COUNT_STR = "合计";

    private static final int START_ROW = 3; // 数据开始行
    private static final int ROW_HEIGHT = 18;   // 数据行行高
    // sheet1中科目列开始的列字母
    private static final String SHEET1_SUBJ_START_LETTER = ExcelUtils.getExcelColumnLetter(PER_HEAD1_LEN);
    // sheet2中科目列开始的列字母
    private static final String SHEET2_SUBJ_START_LETTER = ExcelUtils.getExcelColumnLetter(PER_HEAD2_LEN);

    private byte[] khDzTemplate; // 客户对账excel模板
    private int sheet1ColTotal;   // sheet1总列数，不包含后缀合计列
    private int sheet2ColTotal;   // sheet2总列数，不包含后缀合计列
    private int subjColTotal;   // 科目列占据的列总数

    @Resource
    private KhDzCfgService khDzCfgService;
    @Resource
    private LrFjStmtGen lrFjStmtGen;

    @PostConstruct
    private void initWorkBook() {
        try (Workbook wk = new XSSFWorkbook()) {
            // 初始化样式
            Font font12 = getFont(wk, true, (short) 12);    // 字号12
            Font font10 = getFont(wk, true, (short) 10);    // 字号10
            CellStyle head1Style = getHead1Style(wk, font12);       // 第一个表头样式
            CellStyle head2Style = getHead2Style(wk, head1Style, font10);   // 第二个表头样式
            // 初始化sheet
            Sheet sheet1 = wk.createSheet(SHEET1_NAME);
            Sheet sheet2 = wk.createSheet(SHEET2_NAME);
            // 初始化科目列表
            try {
                List<Subject> sList = JsonUtil.getListFromJson(khDzCfgService.findByCode("stmt:khdz:commonSubjList"), Subject.class);
                subjList.addAll(sList);
            } catch (Exception e) {
                log.error("科目列表初始化失败", e);
            }
            // 计算总列数
            subjColTotal = SUB_LEN * subjList.size();
            sheet1ColTotal = PER_HEAD1_LEN + subjColTotal;
            sheet2ColTotal = PER_HEAD2_LEN + subjColTotal;
            // 两个sheet创建表头
            createSheetHead(sheet1, SHEET1_PRE_HEAD, SHEET1_PRE_HEAD_WIDTH, head1Style, head2Style);
            createSheetHead(sheet2, SHEET2_PRE_HEAD, SHEET2_PRE_HEAD_WIDTH, head1Style, head2Style);
            // 写入workbook字节数据，存储为模板
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            wk.write(bao);
            khDzTemplate = bao.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取科目set，作为查询的参数
     *
     * @return
     */
    public Set<String> getSubjSet() {
        return subjList.stream().map(Subject::getSubjCode).collect(Collectors.toSet());
    }

    public byte[] write(List<String> corpCodes, String ctrName, List<String> periodList, Map<String, Object> result) throws IOException {
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
             Workbook wk = getWorkBook()) {

            // 初始化相关变量
            Sheet sheet1 = wk.getSheet(SHEET1_NAME);
            Sheet sheet2 = wk.getSheet(SHEET2_NAME);
            CellStyle ds = getDataStyle(wk);
            CellStyle ns = getNumberStyle(wk, ds);
            CellStyle cs = getCountStyle(wk);

            List<String> codeList = new ArrayList<>();   // 科目编码
            Map<String, Integer> codeIdxCache = new HashMap<>();    // code索引缓存
//            Map<String, BigDecimal> initRowMap = new HashMap<>();  // 期初统计
            codeProcess(codeList, codeIdxCache);

            int rowI = START_ROW - 1;   // 从第3行开始
            // 写入期初数，并统计initRowMap
//            List<AuxBalance> abInitResult = (List<AuxBalance>) result.get(KhDzQueryService.AB_INIT_RESULT);
//            rowI = initAbWriteCell(rowI, sheet1, sheet2, ds, ns, abInitResult, codeIdxCache);
            // 写入辅助余额数据
            Map<String, List<AuxBalance>> abResult = (Map<String, List<AuxBalance>>) result.get(KhDzQueryService.AB_RESULT);
            writeAData(rowI, sheet1, ds, ns, cs, corpCodes, ctrName, abResult, codeIdxCache, periodList);
            // 写入凭证分录数据
            List<DetailWrapper> dwResult = (List<DetailWrapper>) result.get(KhDzQueryService.DW_RESULT);
            writeDetail(rowI, sheet2, ds, ns, cs, dwResult, codeIdxCache);
            wk.write(bao);
            return bao.toByteArray();
        }
    }

    private void codeProcess(List<String> codeList, Map<String, Integer> codeIdxCache) {
        int idx = 0;
        for (Subject subject : subjList) {
            String code = subject.getSubjCode();
            codeList.add(code);
            codeIdxCache.put(code, idx);
            idx++;
        }
    }

    public String outputFile(byte[] bytes, String ctrName, boolean isNewSys) throws IOException {
        String outFilename = (isNewSys ? "新系统" : "老系统") + "-客户对账单-" + ctrName + ".xlsx";
        return lrFjStmtGen.outputFile(bytes, outFilename);
    }

    /**
     * 设置两个sheet的期初数，并统计initRowMap
     *
     * @param rowI
     * @param sheet1
     * @param sheet2
     * @param nds
     * @param nns
     * @param abInitResult
     * @return
     */
    private int initAbWriteCell(int rowI, Sheet sheet1, Sheet sheet2, CellStyle nds, CellStyle nns, List<AuxBalance> abInitResult,
                                Map<String, Integer> codeIdxCache) {
        if (CollectionUtils.isEmpty(abInitResult)) return rowI;
        Row row1 = createDataRow(rowI, sheet1, ROW_HEIGHT);
        Row row2 = createDataRow(rowI, sheet2, ROW_HEIGHT);
        // 前缀列
        for (int i = 0; i < PER_HEAD1_LEN; i++) {
            createCell(row1, i, i == 0 ? "期初" : "", nds);
        }
        for (int i = 0; i < PER_HEAD2_LEN; i++) {
            createCell(row2, i, i == 0 ? "期初" : "", nds);
        }
        // 科目列
        for (int i = 0; i < subjColTotal; i++) {
            createCell(row1, i + PER_HEAD1_LEN, null, nns);
            createCell(row2, i + PER_HEAD2_LEN, null, nns);
        }
        // 后缀合计列汇总
        BigDecimal aRowCount = null;
        // 填充科目列的数据
        for (AuxBalance ab : abInitResult) {
            if (CollectionUtils.isEmpty(ab.getGlqueryassvo())) continue;
            String code = ab.getPk_accsubj_code();  // 科目编码
            BigDecimal init = code.equals("1122") || code.equals("122102")  // 期初数
                    ? initBalance(ab, SubjDirection.DEBIT)  // 1122 | 122102 为借
                    : initBalance(ab, SubjDirection.CREDIT);    // 其他为贷方
            int perLength1 = codePerLength(PER_HEAD1_LEN, codeIdxCache, code); // sheet1的科目前置长度
            int perLength2 = codePerLength(PER_HEAD2_LEN, codeIdxCache, code); // sheet2的科目前置长度
            for (int i = 0; i < SUB_LEN; i++) {
                BigDecimal val = i == SUB_LEN - 1 ? init : null;    // 科目子列头中最后一列才有值，前面全是null
                setCellVal(row1.getCell(perLength1 + i), val, false);
                setCellVal(row2.getCell(perLength2 + i), val, false);
            }
            // 本行汇总
            aRowCount = bdAdd(aRowCount, init);
        }
        // 后缀合计列
        for (int i = 0; i < SUB_LEN; i++) {
            BigDecimal val = i == SUB_LEN - 1 ? aRowCount : null;
            createCell(row1, sheet1ColTotal + i, val, nns);
            createCell(row2, sheet2ColTotal + i, val, nns);
        }
        return rowI + 1;
    }

    /**
     * sheet1写入汇总数据
     *
     * @param rowI
     * @param sheet
     * @param ds
     * @param ns
     * @param cs
     * @param corpCodes
     * @param ctrName
     * @param abResource
     * @param codeIdxCache
     * @param periodList
     */
    private void writeAData(int rowI, Sheet sheet, CellStyle ds, CellStyle ns, CellStyle cs, List<String> corpCodes, String ctrName,
                            Map<String, List<AuxBalance>> abResource, Map<String, Integer> codeIdxCache, List<String> periodList) {
        if (MapUtils.isEmpty(abResource)) return;
        Collections.sort(corpCodes);    // 按字典排序
        String[] formulaFormat = getFormulaFormat(sheet1ColTotal, SHEET1_SUBJ_START_LETTER);
        // 相同期间、相同客户、相同项目下不同公司组成一组
        int periodOffsetIndex = 0;    // 期间相较于第一组期间的偏移量索引
        int jobPjtOffsetIndex = 0;    // 项目相较于第一组项目的偏移量索引，用于计算这组项目中需要写入的行索引
        for (String period : periodList) {
            // 同一个客户同一个期间下可能对应多个项目。所以这里按项目分组
            List<AuxBalance> auxBalances = abResource.get(period);
            Map<String, Map<String, AuxBalance>> byJobPjtMap = new HashMap<>();    // 按项目名称分组
            auxBalances.forEach(ab -> {
                List<AuxAcctProject> aapjList = ab.getGlqueryassvo();
                if (CollectionUtils.isEmpty(aapjList)) return;

                Map<String, AuxBalance> aABMap = byJobPjtMap.computeIfAbsent(aapjList.get(1).getAssname(), k -> new HashMap<>());
                // 相同科目合并
                aABMap.merge(ab.getPk_accsubj_code(), ab, (o, n) -> {
                    BigDecimal nBqj = n.getDebitLocAmount();      // 新本期借
                    BigDecimal nBqd = n.getCreditLocAmount();    // 新本期贷
                    BigDecimal oBqj = o.getDebitLocAmount();    // 老本期借
                    BigDecimal oBqd = o.getCreditLocAmount();    // 老本期贷
                    o.setDebitLocAmount(bdAdd(oBqj, nBqj)); // 本期借新旧相加
                    o.setCreditAccumAmount(bdAdd(oBqd, nBqd)); // 本期贷新旧相加
                    log.info("合并后，本期借：{}，本期贷：{}", bdAdd(oBqj, nBqj), bdAdd(oBqd, nBqd));
                    return o;
                });
            });

            // 按项目组遍历
            int groupFirstIndex = 0;    // 记录组内第1行的索引
            for (Map.Entry<String, Map<String, AuxBalance>> e : byJobPjtMap.entrySet()) {
                String jobPjtName = e.getKey(); // 项目名称
                Map<String, AuxBalance> value = e.getValue();
                // 组内按公司编码遍历
                for (String corpCode : corpCodes) {
                    Row row = createDataRow(rowI, sheet, ROW_HEIGHT);
                    // 前缀列
                    for (int j = 0; j < PER_HEAD1_LEN; j++) {
                        String val = j == 0 ? period : j == 1 ? corpCode : j == 2 ? ctrName : jobPjtName;
                        createCell(row, j, val, ds);
                    }
                    // 判断是否为当前项目组内的第一行
                    groupFirstIndex = START_ROW - 1 + (jobPjtOffsetIndex * corpCodes.size());
                    boolean isGroupFirst = rowI == groupFirstIndex;
                    log.info("rowI:{},组内第1行：{}", rowI, groupFirstIndex);
                    // 科目列
                    for (Subject subject : subjList) {
                        String code = subject.getSubjCode();
                        AuxBalance ab = value.get(code);
                        BigDecimal bqj = null;  // 本期借
                        BigDecimal bqd = null;  // 本期贷
                        if (ab != null && isGroupFirst) {
                            // 第1列 = 借方，第2列 = 贷方
                            bqj = currBalance(ab, SubjDirection.DEBIT);
                            bqd = currBalance(ab, SubjDirection.CREDIT);
                        }
                        int perLength = codePerLength(PER_HEAD1_LEN, codeIdxCache, code);
                        for (int m = 0; m < SUB_LEN; m++) {
                            int colI = perLength + m;
                            Object val;
                            if (m == 0) {
                                val = bqj;
                            } else if (m == 1) {
                                val = bqd;
                            } else {
                                val = bqj != null && bqd != null ? ppSubtractPFormula(rowI, colI) : null;
                            }
                            createCell(row, colI, val, ns, val instanceof String);
                        }
                    }
                    rowI++; // 公式用的是行号，比索引多一，所以索引+1提前
                    // 后缀合计列
                    for (int n = 0; n < SUB_LEN; n++) {
                        String val = isGroupFirst ? formulaFormat[n].replace("%d", String.valueOf(rowI)) : null;
                        createCell(row, sheet1ColTotal + n, val, ns, true);
                    }
                }
                // 合并单元格，跳过第1、2列
                int mergeEndRowI = groupFirstIndex + corpCodes.size() - 1;
                for (int i = 2; i < sheet1ColTotal + SUB_LEN; i++) {
                    sheet.addMergedRegion(new CellRangeAddress(groupFirstIndex, mergeEndRowI, i, i));
                }
                jobPjtOffsetIndex++;  // 按项目组，每组项目遍历完让偏移量索引+1
            }
            // 合并单元格，第一列期间列
            int periodStep = byJobPjtMap.keySet().size() * corpCodes.size();   // 当前期间的步长，也就是一个期间内的行数
            int startMergeRowI = periodOffsetIndex + START_ROW - 1;
            int endMergeRowI = startMergeRowI + periodStep - 1;
            log.info("startMergeRowI:{},endMergeRowI:{}", startMergeRowI, endMergeRowI);
            sheet.addMergedRegion(new CellRangeAddress(startMergeRowI, endMergeRowI, 0, 0));
            periodOffsetIndex++;
        }
        // 最后一行合计行
        Row lastRow = createDataRow(rowI, sheet, ROW_HEIGHT);
        for (int i = 0; i < sheet1ColTotal + SUB_LEN; i++) {
            String val = null;
            boolean isF = false;
            if (i == 0) {
                val = COUNT_STR;
            } else if (i >= PER_HEAD1_LEN) {
                String letter = i == SUB_LEN ? SHEET1_SUBJ_START_LETTER : ExcelUtils.getExcelColumnLetter(i);
                val = String.format("SUM(%s%d:%s%d)", letter, START_ROW, letter, rowI);
                isF = true;
            }
            createCell(lastRow, i, val, cs, isF);
        }
    }

    /**
     * sheet2写入凭证分录数据
     *
     * @param rowI
     * @param sheet
     * @param ds
     * @param ns
     * @param cs
     * @param dwResult
     * @param codeIdxCache
     */
    private void writeDetail(int rowI, Sheet sheet, CellStyle ds, CellStyle ns, CellStyle cs, List<DetailWrapper> dwResult,
                             Map<String, Integer> codeIdxCache) {
        if (CollectionUtils.isEmpty(dwResult)) return;
        String[] formulaFormat = getFormulaFormat(sheet2ColTotal, SHEET2_SUBJ_START_LETTER);
        for (DetailWrapper detailWrapper : dwResult) {
            Row row = createDataRow(rowI, sheet, ROW_HEIGHT);
            String date = detailWrapper.getDate();
            String bookCode = detailWrapper.getgBookCode();
            String noAndIndex = detailWrapper.getNoAndIndex();
            String ctrCode = detailWrapper.getCtrCode();
            String ctrName = detailWrapper.getCtrName();
            String jobPjtCode = detailWrapper.getJobPjtCode();
            String jobPjtName = detailWrapper.getJobPjtName();
            String explanation = detailWrapper.getExplanation();
            String[] arr = new String[]{date, bookCode, noAndIndex, ctrCode, ctrName, jobPjtCode, jobPjtName, explanation};
            // 前置列
            for (int i = 0; i < PER_HEAD2_LEN; i++) {
                createCell(row, i, arr[i], ds);
            }
            String currCode = detailWrapper.getAccsubj_code();  // 当前科目编码
            // 科目列
            for (Subject subject : subjList) {
                String code = subject.getSubjCode();
                BigDecimal bqj = null;
                BigDecimal bqd = null;
                String balance = null;
                int prevLen = codePerLength(PER_HEAD2_LEN, codeIdxCache, code);
                boolean isCurr = code.equals(currCode);
                if (isCurr) {
                    bqj = detailWrapper.getLocaldebitamount();   // 本期借
                    bqd = detailWrapper.getLocalcreditamount();  // 本期贷
                    balance = balanceFormula(rowI, prevLen + 2);
                }
                for (int i = 0; i < SUB_LEN; i++) {
                    createCell(row, prevLen + i, i == 0 ? bqj : i == 1 ? bqd : balance, ns, i == SUB_LEN - 1);
                }
            }
            rowI++;
            // 合计列
            for (int i = 0; i < SUB_LEN; i++) {
                String val = formulaFormat[i].replace("%d", String.valueOf(rowI));
                createCell(row, sheet2ColTotal + i, val, ns, true);
            }
        }
        // 合计行
        Row lastRow = createDataRow(rowI, sheet, ROW_HEIGHT);
        for (int i = 0; i < sheet2ColTotal + SUB_LEN; i++) {
            if (i < PER_HEAD2_LEN) {
                createCell(lastRow, i, i == 0 ? COUNT_STR : "", cs);
            } else {
                String val = (i - PER_HEAD2_LEN + 1) % 3 == 0 ? colNotEmptyLastVal(rowI, i, true) : null;
                createCell(lastRow, i, val, cs, true);
            }
        }
    }

    /**
     * excel公式
     * 返回当前单元格以上到第3行的列中(不包括当前单元格)最后一个不为空或0的值，若整个列是空或0，则返回空或0
     *
     * @param rowI    当前单元格行索引
     * @param colI    当前单元格列索引
     * @param isEmpty 是否为空，是则当整个列是空或0，时返回空否则返回0
     * @return
     */
    private String colNotEmptyLastVal(int rowI, int colI, boolean isEmpty) {
        String currLetter = ExcelUtils.getExcelColumnLetter(colI);  // 当前列字母
        String ret = isEmpty ? "\"\"" : "0";
        String colRangeTpl = String.format("%s%d:%s%d", currLetter, START_ROW, currLetter, rowI);   // 如D1:D4
        return String.format("IFERROR(LOOKUP(2,1/((%s<>\"\")*(%s<>0)),%s),%s)",
                colRangeTpl, colRangeTpl, colRangeTpl, ret);
    }

    /**
     * 余额的excel公式
     * 计算当前单元格前前一个单元格中的值-前一个单元格的值+当前单元格所在列上一个不为0或空的值
     * 若当前行为数据行的第一行，则公司为前前一个单元格-前一个单元格
     *
     * @param rowI
     * @param colI
     * @return
     */
    private String balanceFormula(int rowI, int colI) {
        // 前前一个-前一个
        String formula = ppSubtractPFormula(rowI, colI);
        int rowNum = rowI + 1;
        return rowNum == START_ROW ? formula : formula + "-" + colNotEmptyLastVal(rowI, colI, false);
    }

    /**
     * 前前一个-前一个Excel公司
     *
     * @param rowI
     * @param colI
     * @return
     */
    private String ppSubtractPFormula(int rowI, int colI) {
        String currPerv = ExcelUtils.getExcelColumnLetter(colI - 1);  // 前一个字母
        String currPp = ExcelUtils.getExcelColumnLetter(colI - 2);    // 前前一个字母
        int rowNum = rowI + 1;
        return String.format("%s%d-%s%d", currPp, rowNum, currPerv, rowNum);
    }

    /**
     * 最后一个合计列的excel公式
     *
     * @param prevColTotal    前置列总数
     * @param subjStartLetter 科目列开始字母
     * @return
     */
    private String[] getFormulaFormat(int prevColTotal, String subjStartLetter) {
        String[] arr = new String[SUB_LEN];
        String endSubjLetter = ExcelUtils.getExcelColumnLetter(prevColTotal - 1);   // 科目列最后一列的字母
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < SUB_LEN; i++) {
            if (i == SUB_LEN - 1) {
                arr[i] = sb.toString();
            } else {
                String letter = ExcelUtils.getExcelColumnLetter(prevColTotal + i);
                sb.append(letter).append("%d").append(i == SUB_LEN - 2 ? "" : "-");
                String val = String.format("SUMIF($%s$2:$%s$2,$%s$2,%s:%s)", subjStartLetter, endSubjLetter,
                        letter, subjStartLetter + "%d", endSubjLetter + "%d");
                arr[i] = val;
            }
        }
        return arr;
    }

    /**
     * 当前科目编码较第一列(索引0)的偏移量
     *
     * @param perHeadLen
     * @param codeIdxCache
     * @param code
     * @return
     */
    private int codePerLength(int perHeadLen, Map<String, Integer> codeIdxCache, String code) {
        return perHeadLen + codeIdxCache.get(code) * SUB_LEN;
    }

    /**
     * 创建数据行，可设置行高
     *
     * @param rowI
     * @param sheet
     * @param height
     * @return
     */
    private Row createDataRow(int rowI, Sheet sheet, int height) {
        Row row = sheet.createRow(rowI);
        row.setHeightInPoints(height);
        return row;
    }

    /**
     * 从模板中拷贝workbook
     *
     * @return
     * @throws IOException
     */
    private Workbook getWorkBook() throws IOException {
        return WorkbookFactory.create(new ByteArrayInputStream(khDzTemplate));
    }

    /**
     * 设置单元格的值，只接受 String | BigDecimal，若其他类型，则写入"not a number or string"
     *
     * @param cell
     * @param val
     */
    private void setCellVal(Cell cell, Object val, boolean isFormula) {
        if (val == null) return;
        if (isFormula) {
            cell.setCellFormula((String) val);
        } else {
            if (val instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) val).doubleValue());
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else {
                cell.setCellValue("not a number or string");
            }
        }
    }

    /**
     * 创建科目表头
     *
     * @param sheet
     * @param row1
     * @param row2
     * @param start
     */
    private void createSubjHead(Sheet sheet, Row row1, Row row2, int start, CellStyle head1Style, CellStyle head2Style) {
        if (CollectionUtils.isEmpty(subjList)) return;
        int i = 0;
        int len = SUB_LEN;
        for (Subject subject : subjList) {
            int coli = start + i * len;
            String code = subject.getSubjCode();
            String name = subject.getSubjName();
            // 第一行（合并单元格）
            createCell(row1, coli, name + "(" + code + ")", head1Style);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, coli, coli + len - 1));

            // 第二行
            for (int j = 0; j < len; j++) {
                createCell(row2, coli + j, SUBJ_SUB_HEAD[j], head2Style);
                sheet.setColumnWidth(coli + j, SUBJ_SUB_HEAD_WIDTH[j] * 256);    // 设置科目表头宽度，以子表头宽度为准
            }
            i++;
        }
    }

    /**
     * 创建表头
     *
     * @param sheet
     * @param preHead
     */
    private void createSheetHead(Sheet sheet, String[] preHead, int[] preHeadWidth, CellStyle head1Style, CellStyle head2Style) {
        // 创建两行表头
        Row headRow1 = sheet.createRow(0);
        Row headRow2 = sheet.createRow(1);
        // 设置行高
        headRow1.setHeightInPoints(25);
        headRow2.setHeightInPoints(20);
        // 前缀表头
        int len = preHead.length;
        for (int i = 0; i < len; i++) {
            createCell(headRow1, i, preHead[i], head2Style);
            createCell(headRow2, i, null, head2Style);
            sheet.setColumnWidth(i, preHeadWidth[i] * 256);  // 设置对应列的宽度
            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }
        // 科目表头
        createSubjHead(sheet, headRow1, headRow2, len, head1Style, head2Style);
        // 后缀表头
        int totalCol = subjList.size() * SUB_LEN + len;
        for (int i = 0; i < SUB_LEN; i++) {
            createCell(headRow1, totalCol + i, i == 0 ? SUFFIX_HEAD : null, head2Style);
            createCell(headRow2, totalCol + i, SUBJ_SUB_HEAD[i], head2Style);
            sheet.setColumnWidth(totalCol + i, SUBJ_SUB_HEAD_WIDTH[i] * 256);    // 设置后缀表头宽度，以子表头宽度为准
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, totalCol, totalCol + SUB_LEN - 1));
        // 冻结表头
        sheet.createFreezePane(0, 2);
    }

    /**
     * 设置边框样式
     *
     * @param cellStyle
     * @param borderStyle
     * @param top
     * @param right
     * @param bottom
     * @param left
     */
    private void setBorder(CellStyle cellStyle, BorderStyle borderStyle, boolean top, boolean right, boolean bottom, boolean left) {
        if (top) cellStyle.setBorderTop(borderStyle);
        if (right) cellStyle.setBorderRight(borderStyle);
        if (bottom) cellStyle.setBorderBottom(borderStyle);
        if (left) cellStyle.setBorderLeft(borderStyle);
    }

    /**
     * 设置4个方向瘦体边框
     *
     * @param cellStyle
     */
    private void setAllBorderThin(CellStyle cellStyle) {
        setBorder(cellStyle, BorderStyle.THIN, true, true, true, true);
    }

    /**
     * 水平垂直方向居中
     *
     * @param cellStyle
     */
    private void xyAlignmentCenter(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    /**
     * 获取字号
     *
     * @param workbook
     * @param bold
     * @param fontPoint
     * @return
     */
    private Font getFont(Workbook workbook, boolean bold, short fontPoint) {
        Font font = workbook.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints(fontPoint);
        return font;
    }

    /**
     * 填充单元格背景颜色为25%°灰色
     *
     * @param cellStyle
     */
    private void fillBgColorGray25Pt(CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    /**
     * 获取数据行样式
     *
     * @param workbook
     * @return
     */
    private CellStyle getDataStyle(Workbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();
        setAllBorderThin(dataStyle);    // 设置瘦体边框
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);   // 垂直居中
        return dataStyle;
    }

    /**
     * 获取数字样式
     *
     * @param workbook
     * @return
     */
    private CellStyle getNumberStyle(Workbook workbook, CellStyle dataStyle) {
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.cloneStyleFrom(dataStyle);
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));   // 数字格式千分符+保留2位小数
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);    // 水平居右
        return numberStyle;
    }

    /**
     * 获取合计样式
     *
     * @param workbook
     * @return
     */
    private CellStyle getCountStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFont(workbook, true, (short) 11);
        cellStyle.setFont(font);
        setAllBorderThin(cellStyle);    // 设置瘦体边框
        fillBgColorGray25Pt(cellStyle); // 填充单元格背景颜色为25%°灰色
        return cellStyle;
    }

    /**
     * 表头1样式
     *
     * @param workbook
     * @return
     */
    private CellStyle getHead1Style(Workbook workbook, Font font) {
        CellStyle headStyle = workbook.createCellStyle();
        setAllBorderThin(headStyle);    // 设置瘦体边框
        xyAlignmentCenter(headStyle);   // 水平垂直居中
        fillBgColorGray25Pt(headStyle); // 填充单元格背景颜色为25%°灰色
        headStyle.setFont(font); // 字号12
        return headStyle;
    }

    /**
     * 表头2样式
     *
     * @param workbook
     * @return
     */
    private CellStyle getHead2Style(Workbook workbook, CellStyle resource, Font font) {
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.cloneStyleFrom(resource);   // 复制第一行表头样式
        headStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());    // 颜色为屎黄色
        headStyle.setFont(font); // 字号10
        return headStyle;
    }

    /**
     * 创建单元格
     *
     * @param row
     * @param coli
     * @param val
     * @param cellStyle
     */
    private Cell createCell(Row row, int coli, Object val, CellStyle cellStyle) {
        return createCell(row, coli, val, cellStyle, false);
    }

    /**
     * 创建单元格
     *
     * @param row
     * @param coli
     * @param val
     * @param cellStyle
     * @param isFormula
     * @return
     */
    private Cell createCell(Row row, int coli, Object val, CellStyle cellStyle, boolean isFormula) {
        Cell cell = row.createCell(coli);
        setCellVal(cell, val, isFormula);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }
}
