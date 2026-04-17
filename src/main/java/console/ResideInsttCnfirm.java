package console;

import console.util.Client;
import console.util.NewGpkiUtil;
import console.util.ShareGpki;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResideInsttCnfirm {

    public static void main(String[] args) {
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
        sb.append("         <id></id>\n");
        sb.append("         <name></name>\n");
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

                System.out.println(xml);

                requestXml = xml;
                {
                    requestXml = requestXml.replace(original, encoded);
                }

                System.out.println(requestXml);

            } catch (Throwable e) {
                e.printStackTrace();
            }

            String responseMsg = Client.doService(serviceUrl, requestXml);
            System.out.println(responseMsg);
            String responseEncData = responseMsg.split("<getResideInsttCnfirmResponse xmlns=\"http://ccais.mopas.go.kr/dh/jmn/services/jumin/ResideInsttCnfirm/types\">")[1]
                    .split("</getResideInsttCnfirmResponse>")[0];

            System.out.println(responseMsg);

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
            String dcriptMsg = responseMsg.replace(responseEncData, decrypted);
            System.out.println(dcriptMsg);

            System.out.println("응답시간 : " + (System.currentTimeMillis() - startTime) + " ms");

        } else {
            String responseMsg = Client.doService(serviceUrl, xml);
            System.out.println(responseMsg);
        }
    }

}
