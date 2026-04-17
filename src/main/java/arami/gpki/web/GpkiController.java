package arami.gpki.web;

import arami.common.CommonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import console.util.Client;
import console.util.NewGpkiUtil;
import console.util.ShareGpki;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@Slf4j
@Tag(name = "비대면인증", description = "비대면인증 - 행정정보조회 API")
@RestController
@RequestMapping("/api/v1")
public class GpkiController extends CommonService {

    @Operation(summary = "거주지행정코드(관내주민) 자격여부 조회", description = "거주지행정코드(관내주민) 자격여부를 조회합니다.")
    @ResponseBody
    @PostMapping(value = "/ResideInsttCnfirm", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> ResideInsttCnfirm(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        String resultData = "";

        long startTime = System.currentTimeMillis();
        String rnd1 = Double.toString(java.lang.Math.random()).substring(2, 6);
        String rnd2 = Double.toString(java.lang.Math.random()).substring(2, 6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        String cur = sdf.format(new Date());
        String transactionUniqueId = cur + rnd1 + rnd2;

        NewGpkiUtil g = null;
        String xml = null;

        boolean useGPKI = true;

        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\t\n");
        sb.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        sb.append("   <Header>\n");
        sb.append("      <commonHeader xmlns=\"http://ccais.mopas.go.kr/dh/jmn/services/jumin/ResideInsttCnfirm/types\">\n");
        sb.append("         <serviceName>ResideInsttCnfirmService</serviceName>\n");
        sb.append("         <useSystemCode>FD2601220080004</useSystemCode>\n");
        sb.append("         <certServerId>SVR4671125001</certServerId>\n");
        sb.append("         <transactionUniqueId>" + transactionUniqueId + "</transactionUniqueId>\n");
        sb.append("         <userDeptCode>0000000</userDeptCode>\n");
        sb.append("         <userName>홍길동</userName>\n");
        sb.append("      </commonHeader>\n");
        sb.append("   </Header>\n");
        sb.append("   <Body>\n");
        sb.append("      <getResideInsttCnfirm xmlns=\"http://ccais.mopas.go.kr/dh/jmn/services/jumin/ResideInsttCnfirm/types\">\n");
        sb.append("         <orgCode>1174000001</orgCode>\n");	//수정금지
        sb.append("         <id>" + model.get("id").toString() + "</id>\n");
        sb.append("         <name>" + model.get("name").toString() + "</name>\n");
        sb.append("      </getResideInsttCnfirm>\n");
        sb.append("   </Body>\n");
        sb.append("</Envelope>\n");

        xml = sb.toString();

        // 행정망
        //String serviceUrl = "http://10.188.225.25:29001/cmc/infoservice/jumin/ResideInsttCnfirmService";

        // 인터넷망
        String serviceUrl = "http://116.67.73.153:29001/cmc/infoservice/jumin/ResideInsttCnfirmService";

        if (useGPKI) {
            String encoded = null;
            String requestXml = null;

            try {
                String targetServerId = "SVR1311000030"; // 수정금지
                g = ShareGpki.getGpkiUtil(targetServerId);

                String charset = "UTF-8";

                String original = xml.split("<getResideInsttCnfirm xmlns=\"http://ccais.mopas.go.kr/dh/jmn/services/jumin/ResideInsttCnfirm/types\">")[1].split("</getResideInsttCnfirm>")[0];

                byte[] encrypted = g.encrypt(original.getBytes(charset), targetServerId);
                byte[] signed = g.sign(encrypted);
                encoded = g.encode(signed);

                //System.out.println("XML==>" + xml);

                requestXml = xml;
                {
                    requestXml = requestXml.replace(original, encoded);
                }

                //System.out.println(requestXml);

            } catch (Throwable e) {
                e.printStackTrace();
            }

            String responseMsg = Client.doService(serviceUrl, requestXml);
            //System.out.println(responseMsg);
            String responseEncData = responseMsg.split("<getResideInsttCnfirmResponse xmlns=\"http://ccais.mopas.go.kr/dh/jmn/services/jumin/ResideInsttCnfirm/types\">")[1]
                    .split("</getResideInsttCnfirmResponse>")[0];

            //System.out.println("결과==>" + responseMsg);

            String decrypted = "";
            {
                byte[] decoded;
                try {
                    decoded = g.decode(responseEncData);
                    byte[] validated = g.validate(decoded);
                    decrypted = new String(g.decrypt(validated), "UTF-8");
                    decrypted = decrypted.replace("><", ">\n<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resultData = responseMsg.replace(responseEncData, decrypted);
            
            //System.out.println(resultData);

            System.out.println("응답시간 : " + (System.currentTimeMillis() - startTime) + " ms");

        } else {
            resultData = Client.doService(serviceUrl, xml);
            //System.out.println(resultData);
        }

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(this.XmlToJson(resultData));

            JSONObject body = (JSONObject) jsonObj.get("Body");
            JSONObject jsonData = (JSONObject) body.get("getResideInsttCnfirmResponse");

            jsonMap.put("resultCode", "0000");
            jsonMap.put("resultData", jsonData);
        } catch (Exception e) {
            jsonMap.put("resultCode", "0001");
            jsonMap.put("resultData", "");
            e.printStackTrace();
        }

        return jsonMap;
    }

    @Operation(summary = "기초생활수급자사실여부 조회", description = "기초생활수급자사실여부를 조회합니다.")
    @ResponseBody
    @PostMapping(value = "/ReductionBscLivYnService", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> ReductionBscLivYnService(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        String resultData = "";

        long startTime = System.currentTimeMillis();
        String rnd1 = Double.toString(java.lang.Math.random()).substring(2, 6);
        String rnd2 = Double.toString(java.lang.Math.random()).substring(2, 6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        String cur = sdf.format(new Date());
        String transactionUniqueId = cur + rnd1 + rnd2;

        NewGpkiUtil g = null;
        String xml = null;

        boolean useGPKI = true;

        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\t\n");
        sb.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        sb.append("   <Header>\n");
        sb.append("      <commonHeader xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionBscLivYn/types\">\n");
        sb.append("         <serviceName>ReductionBscLivYnService</serviceName>\n");
        sb.append("         <useSystemCode>FD2601220080004</useSystemCode>\n");
        sb.append("         <certServerId>SVR4671125001</certServerId>\n");
        sb.append("         <transactionUniqueId>" + transactionUniqueId+ "</transactionUniqueId>\n");
        sb.append("         <userDeptCode>0000000</userDeptCode>\n");
        sb.append("         <userName>홍길동</userName>\n");
        sb.append("      </commonHeader>\n");
        sb.append("   </Header>\n");
        sb.append("   <Body>\n");
        sb.append("      <getReductionBscLivYn xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionBscLivYn/types\">\n");
        sb.append("		      <ReqOrgCd>4671000</ReqOrgCd>\n");
        sb.append("		      <ReqBizCd>ERGSKE65SSI638W25027</ReqBizCd>\n");
        sb.append("		      <TGTR_RRN>" + model.get("id").toString() + "</TGTR_RRN>\n");
        sb.append("		      <TGTR_NM>" + model.get("name").toString() + "</TGTR_NM>\n");
        sb.append("      </getReductionBscLivYn>\n");
        sb.append("   </Body>\n");
        sb.append("</Envelope>\n");

        xml = sb.toString();

        // 행정망
        //String serviceUrl = "http://10.188.225.25:29001/cmc/ynservice/swsdn/ReductionBscLivYnService";

        // 인터넷망
        String serviceUrl = "http://116.67.73.153:29001/cmc/ynservice/swsdn/ReductionBscLivYnService";

        if (useGPKI) {

            String encoded = null;
            String requestXml = null;
            try {
                String targetServerId = "SVR1311000030"; // 수정금지
                g = ShareGpki.getGpkiUtil(targetServerId);

                String charset = "UTF-8";

                String original = xml
                        .split("<getReductionBscLivYn xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionBscLivYn/types\">")[1]
                        .split("</getReductionBscLivYn>")[0];

                byte[] encrypted = g.encrypt(original.getBytes(charset),
                        targetServerId);
                byte[] signed = g.sign(encrypted);
                encoded = g.encode(signed);

                //System.out.println(xml);

                requestXml = xml;
                {
                    requestXml = requestXml.replace(original, encoded);
                }

                //System.out.println(requestXml);

            } catch (Throwable e) {
                e.printStackTrace();
            }

            String responseMsg = Client.doService(serviceUrl, requestXml);
            //System.out.println(responseMsg);
            String responseEncData = responseMsg
                    .split("<getReductionBscLivYnResponse xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionBscLivYn/types\">")[1]
                    .split("</getReductionBscLivYnResponse>")[0];

            //System.out.println(responseMsg);

            String decrypted = "";
            {
                byte[] decoded;
                try {
                    decoded = g.decode(responseEncData);
                    byte[] validated = g.validate(decoded);
                    decrypted = new String(g.decrypt(validated), "UTF-8");
                    decrypted = decrypted.replace("><", ">\n<");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            log.info("decrypted : " + decrypted);


            String dcriptMsg = responseMsg.replace(responseEncData, decrypted);

            dcriptMsg = dcriptMsg.replaceAll("&amp;", "&");
            dcriptMsg = dcriptMsg.replaceAll("&lt;", "<");
            dcriptMsg = dcriptMsg.replaceAll("&gt;", ">");
            dcriptMsg = dcriptMsg.replaceAll("&nbsp;", "''");

            resultData = dcriptMsg;
            //System.out.println(dcriptMsg);

            System.out.println("응답시간 : " + (System.currentTimeMillis() - startTime) + " ms");

        }else{
            resultData = Client.doService(serviceUrl, xml);
            //System.out.println(resultData);
        }

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(this.XmlToJson(resultData));

            JSONObject body = (JSONObject) jsonObj.get("Body");
            JSONObject jsonData = (JSONObject) body.get("getReductionBscLivYnResponse");

            jsonMap.put("resultCode", "0000");
            jsonMap.put("resultData", jsonData);
        } catch (Exception e) {
            jsonMap.put("resultCode", "0001");
            jsonMap.put("resultData", "");
            e.printStackTrace();
        }

        return jsonMap;
    }

    @Operation(summary = "차상위사실여부 조회", description = "차상위사실여부를 조회합니다.")
    @ResponseBody
    @PostMapping(value = "/ReductionPoorYnService", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> ReductionPoorYnService(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        String resultData = "";

        long startTime = System.currentTimeMillis();
        String rnd1 = Double.toString(java.lang.Math.random()).substring(2, 6);
        String rnd2 = Double.toString(java.lang.Math.random()).substring(2, 6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        String cur = sdf.format(new Date());
        String transactionUniqueId = cur + rnd1 + rnd2;

        NewGpkiUtil g = null;
        String xml = null;

        boolean useGPKI = true;

        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\t\n");
        sb.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        sb.append("   <Header>\n");
        sb.append("      <commonHeader xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionPoorYn/types\">\n");
        sb.append("         <serviceName>ReductionPoorYnService</serviceName>\n");
        sb.append("         <useSystemCode>FD2601220080004</useSystemCode>\n");
        sb.append("         <certServerId>SVR4671125001</certServerId>\n");
        sb.append("         <transactionUniqueId>" + transactionUniqueId+ "</transactionUniqueId>\n");
        sb.append("         <userDeptCode>0000000</userDeptCode>\n");
        sb.append("         <userName>홍길동</userName>\n");
        sb.append("      </commonHeader>\n");
        sb.append("   </Header>\n");
        sb.append("   <Body>\n");
        sb.append("      <getReductionPoorYn xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionPoorYn/types\">\n");
        sb.append("		      <ReqOrgCd>4671000</ReqOrgCd>\n");
        sb.append("		      <ReqBizCd>ERGSKE65SSI638W25029</ReqBizCd>\n");
        sb.append("		      <TGTR_RRN>" + model.get("id").toString() + "</TGTR_RRN>\n");
        sb.append("		      <TGTR_NM>" + model.get("name").toString() + "</TGTR_NM>\n");
        sb.append("      </getReductionPoorYn>\n");
        sb.append("   </Body>\n");
        sb.append("</Envelope>\n");

        xml = sb.toString();

        // 행정망
        //String serviceUrl = "http://10.188.225.25:29001/cmc/ynservice/swsdn/ReductionPoorYnService";

        // 인터넷망
        String serviceUrl = "http://116.67.73.153:29001/cmc/ynservice/swsdn/ReductionPoorYnService";

        if (useGPKI) {

            String encoded = null;
            String requestXml = null;
            try {
                String targetServerId = "SVR1311000030"; // 수정금지
                g = ShareGpki.getGpkiUtil(targetServerId);

                String charset = "UTF-8";

                String original = xml
                        .split("<getReductionPoorYn xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionPoorYn/types\">")[1]
                        .split("</getReductionPoorYn>")[0];

                byte[] encrypted = g.encrypt(original.getBytes(charset),
                        targetServerId);
                byte[] signed = g.sign(encrypted);
                encoded = g.encode(signed);

                //System.out.println(xml);

                requestXml = xml;
                {
                    requestXml = requestXml.replace(original, encoded);
                }

                //System.out.println(requestXml);

            } catch (Throwable e) {
                e.printStackTrace();
            }

            String responseMsg = Client.doService(serviceUrl, requestXml);
            //System.out.println(responseMsg);
            String responseEncData = responseMsg
                    .split("<getReductionPoorYnResponse xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionPoorYn/types\">")[1]
                    .split("</getReductionPoorYnResponse>")[0];

            //System.out.println(responseMsg);

            String decrypted = "";
            {
                byte[] decoded;
                try {
                    decoded = g.decode(responseEncData);
                    byte[] validated = g.validate(decoded);
                    decrypted = new String(g.decrypt(validated), "UTF-8");
                    decrypted = decrypted.replace("><", ">\n<");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            log.info("decrypted : " + decrypted);


            String dcriptMsg = responseMsg.replace(responseEncData, decrypted);

            dcriptMsg = dcriptMsg.replaceAll("&amp;", "&");
            dcriptMsg = dcriptMsg.replaceAll("&lt;", "<");
            dcriptMsg = dcriptMsg.replaceAll("&gt;", ">");
            dcriptMsg = dcriptMsg.replaceAll("&nbsp;", "''");

            resultData = dcriptMsg;

            //System.out.println("응답메시지[원문]=====>\n" + resultData);

            System.out.println("응답시간 : " + (System.currentTimeMillis() - startTime) + " ms");

        }else{
            resultData = Client.doService(serviceUrl, xml);
            //System.out.println(resultData);
        }

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(this.XmlToJson(resultData));

            JSONObject body = (JSONObject) jsonObj.get("Body");
            JSONObject jsonData = (JSONObject) body.get("getReductionPoorYnResponse");

            jsonMap.put("resultCode", "0000");
            jsonMap.put("resultData", jsonData);
        } catch (Exception e) {
            jsonMap.put("resultCode", "0001");
            jsonMap.put("resultData", "");
            e.printStackTrace();
        }

        return jsonMap;
    }

    @Operation(summary = "한부모가족사실여부 조회", description = "한부모가족사실여부를 조회합니다.")
    @ResponseBody
    @PostMapping(value = "/ReductionSingleParentYnService", produces="application/json;charset=UTF-8")
    public HashMap<String, Object> ReductionSingleParentYnService(HttpServletRequest request, ModelMap model) throws Exception {
        this.setCommon(request, model);

        String resultData = "";

        long startTime = System.currentTimeMillis();
        String rnd1 = Double.toString(java.lang.Math.random()).substring(2, 6);
        String rnd2 = Double.toString(java.lang.Math.random()).substring(2, 6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        String cur = sdf.format(new Date());
        String transactionUniqueId = cur + rnd1 + rnd2;

        NewGpkiUtil g = null;
        String xml = null;
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>	\n");
        sb.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">	\n");
        sb.append("	<Header>	\n");
        sb.append("		<commonHeader xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionSingleParentYn/types\">	\n");
        sb.append("			<serviceName>ReductionSingleParentYnService</serviceName>	\n");
        sb.append("			<useSystemCode>FD2601220080004</useSystemCode>	\n");
        sb.append("			<certServerId>SVR4671125001</certServerId>	\n");
        sb.append("			<transactionUniqueId>" + transactionUniqueId + "</transactionUniqueId>	\n");
        sb.append("			<userDeptCode>0000000</userDeptCode>	\n");
        sb.append("			<userName>홍길동</userName>	\n");
        sb.append("		</commonHeader>	\n");
        sb.append("	</Header>	\n");
        sb.append("	<Body>	\n");
        sb.append("		<getReductionSingleParentYn xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionSingleParentYn/types\">	\n");
        sb.append("		      <ReqOrgCd>4671000</ReqOrgCd>\n");
        sb.append("		      <ReqBizCd>ERGSKE65SSI638W25028</ReqBizCd>\n");
        sb.append("		      <TGTR_RRN>" + model.get("id").toString() + "</TGTR_RRN>\n");
        sb.append("		      <TGTR_NM>" + model.get("name").toString() + "</TGTR_NM>\n");
        sb.append("		</getReductionSingleParentYn>	\n");
        sb.append("	</Body>	\n");
        sb.append("</Envelope>	\n");

        xml = sb.toString();

        // 행정망
        //String serviceUrl = "http://10.188.225.25:29001/cmc/ynservice/swsdn/ReductionSingleParentYnService";

        // 인터넷망
        String serviceUrl = "http://116.67.73.153:29001/cmc/ynservice/swsdn/ReductionSingleParentYnService";

        String encoded = null;
        String requestXml = null;
        try {
            String targetServerId = "SVR1311000030"; // 수정금지

            g = ShareGpki.getGpkiUtil(targetServerId);

            String charset = "UTF-8";
            //System.out.println(xml);

            String original = xml.split("<getReductionSingleParentYn xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionSingleParentYn/types\">")[1].split("</getReductionSingleParentYn>")[0];

            byte[] encrypted = g.encrypt(original.getBytes(charset), targetServerId);
            byte[] signed = g.sign(encrypted);
            encoded = g.encode(signed);

            //System.out.println("요청메시지[원본]");
            //System.out.println(xml);

            // 조회 조건 설정
            requestXml = xml;
            {
                requestXml = requestXml.replace(original, encoded);
            }

            //System.out.println("요청메시지[암호화]");
            //System.out.println(requestXml);

        } catch (Throwable e) {
            System.out.println(e.getStackTrace());
        }

        String responseMsg = Client.doService(serviceUrl, requestXml);
        //System.out.println(responseMsg);
        String responseEncData = responseMsg
                .split("<getReductionSingleParentYnResponse xmlns=\"http://ccais.mopas.go.kr/dh/rid/services/swsdn/ReductionSingleParentYn/types\">")[1]
                .split("</getReductionSingleParentYnResponse>")[0];

        //System.out.println("응답메시지[암호화]");
        //System.out.println(responseMsg);

        // 수신
        String decrypted = "";
        {
            byte[] decoded;
            try {
                decoded = g.decode(responseEncData);
                byte[] validated = g.validate(decoded);
                decrypted = new String(g.decrypt(validated), "UTF-8");
                decrypted = decrypted.replace("><", ">\n<");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String dcriptMsg = responseMsg.replace(responseEncData, decrypted);

        dcriptMsg = dcriptMsg.replaceAll("&amp;", "&");
        dcriptMsg = dcriptMsg.replaceAll("&lt;", "<");
        dcriptMsg = dcriptMsg.replaceAll("&gt;", ">");
        dcriptMsg = dcriptMsg.replaceAll("&nbsp;", "''");

        resultData = dcriptMsg;

        //System.out.println("응답메시지[원문]=====>\n" + resultData);

        System.out.println("소요시간 : " + (System.currentTimeMillis() - startTime) + " ms");

        HashMap<String, Object> jsonMap = new HashMap<String,Object>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(this.XmlToJson(resultData));

            JSONObject body = (JSONObject) jsonObj.get("Body");
            JSONObject jsonData = (JSONObject) body.get("getReductionSingleParentYnResponse");

            jsonMap.put("resultCode", "0000");
            jsonMap.put("resultData", jsonData);
        } catch (Exception e) {
            jsonMap.put("resultCode", "0001");
            jsonMap.put("resultData", "");
            e.printStackTrace();
        }

        return jsonMap;
    }

    public String XmlToJson(String xml) throws Exception{
        XmlMapper xmlMapper = new XmlMapper();
        ObjectMapper jsonMapper = new ObjectMapper();

        JsonNode node = xmlMapper.readTree(xml.getBytes());

        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }

}
