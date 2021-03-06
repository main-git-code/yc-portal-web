package com.ai.yc.protal.web.controller;

import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.sdk.components.dss.DSSClientFactory;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.opt.sdk.util.CollectionUtil;
import com.ai.paas.ipaas.dss.base.interfaces.IDSSClient;
import com.ai.paas.ipaas.i18n.ResWebBundle;
import com.ai.yc.protal.web.constants.Constants;
import com.ai.yc.protal.web.constants.LoginConstants;
import com.ai.yc.protal.web.model.DocParagraphTrans;
import com.ai.yc.protal.web.service.YeekitService;
import com.ai.yc.protal.web.utils.ChardetUtil;
import com.ai.yc.protal.web.utils.UserUtil;
import com.ai.yc.protal.web.utils.WordUtil;
import com.ai.yc.user.api.usercollectiontranslation.interfaces.IYCUserCollectionSV;
import com.ai.yc.user.api.usercollectiontranslation.param.UserCollectionInfo;
import com.ai.yc.user.api.usercollectiontranslation.param.UserCollectionInfoListResponse;
import com.ai.yc.user.api.usercollectiontranslation.param.UserCollectionPageInfoRequest;
import com.alibaba.dubbo.rpc.protocol.dubbo.DubboCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ai.opt.sdk.web.model.ResponseData;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static com.ai.yc.protal.web.utils.WordUtil.readWord;
import static org.apache.hadoop.io.WritableUtils.toByteArray;


/**
 * 译库机器翻译
 */
@Controller
public class YeekitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(YeekitController.class);
    private static final String SESSION_DOC_TRANS = "docTrans";

    @Autowired
    YeekitService yeekitService;
    @Autowired
    ResWebBundle rb;

    /**
     * 机器翻译
     * @param from
     * @param to
     * @param text
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/mt")  
    public ResponseData<String> mt(String from, String to, String text) {  
        ResponseData<String> resData = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        try {
            String fromTmp = from;
            //判断是否是自动检测
            if ("auto".equals(from)) {
                fromTmp = yeekitService.detection(text);
            }
            String result = null;
            //若用户已登录,则查询译文信息
            if(UserUtil.getSsoUser() != null){
                result = queryCollection(from,to,text);
            }
            //若没查询都收藏译文，则进行机器翻译
            if (StringUtils.isBlank(result)) {
                result = yeekitService.dotranslate(fromTmp, to, text);
            }
            if (result.startsWith("error:")) {
                //机器返回错误
                resData = new ResponseData<String>(ResponseData.AJAX_STATUS_FAILURE, rb.getMessage(""));
            }
            resData.setData(result);
            //若获取收藏译文，则返回收藏编码

        } catch (Exception e) {
            LOGGER.error("机器翻译失败：", e);
            resData = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS, rb.getMessage(""));
        }
        
        return resData;
    }

    /**
     * 文档机器翻译，上传文件
     * @param from
     * @param to
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/mtUpload")
    public ResponseData<String> docMt(String from, String to, MultipartFile file,HttpSession session) {
        ResponseData<String> resData = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        try {
            List<DocParagraphTrans> paragraphTransList;
            if (file.getOriginalFilename().toLowerCase().endsWith("txt")) {
                paragraphTransList = readTxt(file);
            } else {
                paragraphTransList = readDoc(file);
            }

            /*分段翻译*/
            //机器翻译单次传输的最大长度
            int offfset = 2000;
            String fromTmp = from;
            String sourceText;
            StringBuilder sb;
            for (DocParagraphTrans paragraphTrans:paragraphTransList){
                sourceText = paragraphTrans.getSourceText();
                //若是"自动检测",则进行语言检测
                if ("auto".equals(from)) {
                    fromTmp = yeekitService.detection(sourceText);
                }
                //若源语言和目标语言一致，则不进行翻译
                if(fromTmp.equals(to) || StringUtils.isBlank(sourceText)){
                    paragraphTrans.setTranslation(sourceText);
                    continue;
                }
                //默认一段调用机器翻译的次数
                int transNum = 1;
                int sourceLen = paragraphTrans.getSourceLen();
                //若原文长度超过单次长度限制，则分多次进行调用
                if (sourceLen>offfset){
                    transNum = sourceLen/offfset;
                    if(sourceLen%offfset>0){
                        transNum += 1;
                    }
                }
                sb = new StringBuilder();
                for (int i = 0; i <transNum ; i++) {
                    sb.append(yeekitService.doTranslateNoFormat(fromTmp, to, sourceText));
                }
                paragraphTrans.setTranslation(sb.toString());
            }

            //TODO... 模拟数据
//            if (!CollectionUtil.isEmpty(paragraphTransList)) {
//                for (DocParagraphTrans paragraphTrans:paragraphTransList) {
//                    paragraphTrans.setTranslation("hello word" +System.currentTimeMillis());
//                }
//            }

            session.setAttribute(SESSION_DOC_TRANS,paragraphTransList);
        } catch (Exception e) {
            resData = new ResponseData<String>(ResponseData.AJAX_STATUS_FAILURE,
                    rb.getMessage("home.trans.fail"));
            LOGGER.error("文档翻译失败:", e);
        }
        return resData;
    }

    /**
     * 显示文档机器翻译结果
     * @return
     */
    @RequestMapping(value = "/docMt")
    public String showDocMt(){
        return "docTrans";
    }
    /**
     * 文档机器翻译，译文下载
     * @param fileType
     * @param response
     */
    @RequestMapping("/downloadDoc")
    public void downloadDoc(String fileType, HttpSession session,HttpServletResponse response) {
        byte[] b;
        try {
            List<DocParagraphTrans> paragraphTransList =
                    (List<DocParagraphTrans>)session.getAttribute(SESSION_DOC_TRANS);
            //若没有译文信息，不做任何操作。
            if(CollectionUtil.isEmpty(paragraphTransList))
                return;
            if ("doc" == fileType.toLowerCase()) {
                b = genDoc(paragraphTransList);
            } else {
                b = genTxt(paragraphTransList);
            }

            OutputStream os = response.getOutputStream();
//            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=translation."+fileType);
            response.setHeader("Content-Length", b.length+"");
            os.write(b);
            os.close();

        } catch (Exception e) {
            LOGGER.info("下载文件异常：", e);
        }
    }

    /**
     * 机器翻译语言检测
     * @param text 文本
     * @return 
     * @author mimw
     */
    @ResponseBody
    @RequestMapping(value = "/translateLan")  
    public ResponseData<String> translateLan(String text) {  
        ResponseData<String> resData = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        //TODO 
        String lan;
        try {
            lan = yeekitService.detection(text);
            resData.setData(lan);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("",e.getMessage());
            resData = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS, "FAIL");
        }
        //TODO
//        resData = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
//        resData.setData(lan);
        return resData;
    }

    /**
     * 查询用户收藏译文
     * @return
     */
    private String queryCollection(String from,String to,String text){
        StringBuilder sb = new StringBuilder();
        IYCUserCollectionSV userCollectionSV = DubboConsumerFactory.getService(IYCUserCollectionSV.class);
        UserCollectionPageInfoRequest request = new UserCollectionPageInfoRequest();
        request.setUserId(UserUtil.getUserId());
        request.setSourceLanguage(from);
        request.setTargetLanguage(to);
        request.setOriginal(text);
        request.setPageNo(1);
        request.setPageSize(1);
        UserCollectionInfoListResponse response = userCollectionSV.queryCollectionInfo(request);
        PageInfo<UserCollectionInfo> pageInfo = response==null?null:response.getCollectionList();

        if (pageInfo != null && !CollectionUtil.isEmpty(pageInfo.getResult())) {
            UserCollectionInfo collectionInfo = pageInfo.getResult().get(0);

            /* 返回信息示例
            {
            "translation": [
                {
                    "translated": [
                        {
                            "alignment-raw": [
                                {
                                    "src-start": 0,
                                    "tgt-start": 0,
                                    "src-end": 0,
                                    "tgt-end": 0
                                }
                            ],
                            "text": "China",
                            "rank": 0,
                            "tgt-tokenized": "China ",
                            "score": -0.8170589804649353,
                            "src-tokenized": "中国"
                        }
                    ],
                    "translationId": "b68b0ab9f9ca44ce962b6d26a613eca6"
                }
            ],
            "collectionId":"" //收藏记录标识
            }
             */

            sb.append("{\"translation\": [{\"translated\":[{ ")
                    .append("\"text\":\"").append(collectionInfo.getTranslation()).append("\",\"rank\": 0")
                    .append(",\"score\": 1,").append("\"src-tokenized\":\"").append(text).append("\"")
                    .append("}],")
                    .append("\"translationId\": \"123\"").append("}]")
                    .append("\"collectionId\":").append(collectionInfo.getId()).append("}");
        }
        //TODO... 模拟数据
//        sb.append("{\"translation\": [{\"translated\":[{ ")
//                .append("\"text\":\"").append("Hello main").append("\",\"rank\": 0").append(",\"score\": 1,")
//                .append("\"src-tokenized\":\"").append(text).append("\"")
//                .append("}],")
//                .append("\"translationId\": \"123\"").append("}],")
//                .append("\"collectionId\":").append("345345").append("}");
        return sb.toString();
    }

    /**
     * 将上传txt文件进行分段读取
     * @param file
     * @return
     */
    private List<DocParagraphTrans> readTxt(MultipartFile file) throws IOException {
        List<DocParagraphTrans> paragraphTransList = new LinkedList<>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            //做数据流拷贝
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            InputStream inputStream = file.getInputStream();
            while ((len = inputStream.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            //检测文件编码
            ChardetUtil chardetUtil = new ChardetUtil();
            String chart = chardetUtil.detectAllCharset(new ByteArrayInputStream(baos.toByteArray()));
            // 拼接读取的内容
            StringBuilder sb = new StringBuilder();
            isr = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()),chart);
            br = new BufferedReader(isr);
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp.trim() + "\n");
            }
            // \s A whitespace character: [ \t\n\x0B\f\r]
            String p[] = sb.toString().split("\\s*\n");
            for (String string : p) {
                DocParagraphTrans paragraphTrans = new DocParagraphTrans();
                paragraphTrans.setSourceText(string.replaceAll("\\s*", ""));
                paragraphTransList.add(paragraphTrans);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read TXT file.",e);
            e.printStackTrace();
        } finally {
            if (br != null)
                br.close();
            if (isr != null)
                isr.close();
        }

        return paragraphTransList;
    }

    /**
     * 将上传word文档进行分段读取
     * @param file
     * @return
     * @throws IOException
     */
    private List<DocParagraphTrans> readDoc(MultipartFile file) throws IOException {
        List<DocParagraphTrans> paragraphTransList =
                WordUtil.getTextByParagraph(file.getOriginalFilename(),file.getInputStream());
        return paragraphTransList;
    }
    /**
     * 返回txt格式的byte[]
     * @param transList
     * @return
     */
    private byte[] genTxt(List<DocParagraphTrans> transList) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (DocParagraphTrans paragraphTran:transList){
            //首行缩进
            sb.append("    "+paragraphTran.getTranslation()+"\r\n");
        }
        return sb.toString().getBytes("utf-8");
    }
    /**
     * 返回doc格式的byte[]
     * @param transList
     * @return
     */
    private byte[] genDoc(List<DocParagraphTrans> transList) throws IOException {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para;
        XWPFRun run;
        for (DocParagraphTrans paragraphTran:transList){
            para = doc.createParagraph();
            para.setAlignment(ParagraphAlignment.LEFT);//设置左对齐
            para.setIndentationFirstLine(450);//设置首行缩进
            run = para.createRun();
            run.setFontFamily("仿宋");
            run.setFontSize(13);
            run.setText(paragraphTran.getTranslation());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.write(baos);
        byte[] b = baos.toByteArray();
        baos.close();
        return b;
    }
}
