package arami.shared.neis.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;

/**
 * One-time utility: fetches open.neis.go.kr server certificate and saves to neis.cer
 * for importing into Java truststore (cacerts). Run once, then run keytool -importcert.
 */
public class NeisCertExporter {

    public static void main(String[] args) throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] c, String s) { }
                public void checkServerTrusted(X509Certificate[] c, String s) { }
            }
        };
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(null, trustAll, new java.security.SecureRandom());

        String host = "open.neis.go.kr";
        int port = 443;
        try (java.net.Socket raw = new java.net.Socket(host, port)) {
            javax.net.ssl.SSLSocketFactory sf = ssl.getSocketFactory();
            try (javax.net.ssl.SSLSocket sock = (javax.net.ssl.SSLSocket) sf.createSocket(raw, host, port, true)) {
                sock.startHandshake();
                java.security.cert.Certificate[] chain = sock.getSession().getPeerCertificates();
                if (chain == null || chain.length == 0) {
                    System.err.println("No certificates received");
                    return;
                }
                // Export first cert (server) and also try root if chain length > 1
                X509Certificate serverCert = (X509Certificate) chain[0];
                Path out = Paths.get("neis.cer").toAbsolutePath();
                try (OutputStream os = Files.newOutputStream(out)) {
                    os.write(serverCert.getEncoded());
                }
                System.out.println("Saved: " + out);
                System.out.println("Subject: " + serverCert.getSubjectX500Principal().getName());
            }
        }
    }
}
