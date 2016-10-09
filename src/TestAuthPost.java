
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class TestAuthPost
{

    public TestAuthPost()
    {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args)
            throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException
    {
        sendPOST();
        System.out.println("POST DONE");
    }

    private static void sendPOST()
            throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException
    {

        //proxy details
        //  String user = "smadmin";
        //  String passwd = "M3sh@dmin!";
        //  String hostname = "192.168.33.148";
        int port = 3128;
        //azure auth
        /* String ARMResource = "https://management.core.windows.net/";
        String client_id = "0eaa67e7-ff11-4dd0-b735-62661e32cce2";
        String tenant_id = "04ead8fa-c4ad-4361-a1a3-c2034f687ae4";
        String password = "M3sh@dmin!";*/

        String ARMResource = "https://management.core.windows.net/";
        String client_id = "cec62bad-b4fa-4cd4-9f03-d4e37e91072c";
        String tenant_id = "04ead8fa-c4ad-4361-a1a3-c2034f687ae4";
        String password = "5kiFlnm1nuzRki7UCHO1p+itvuKQ6JrkjRo7rHvyvR8=";

        /*  File file = new File("azurerm.properties");
        if (file != null)
        {
            Properties properties = new Properties();
            try
            {
                FileInputStream is = new FileInputStream(file);
                properties.load(is);
            }
            catch (Exception ex)
            {
                System.out.println("Could not load azurerm.properties" + ex.getMessage());

                return;
            }

            Object value = null;
            Object v = properties.get("proxy");
            value = v;
            if (v != null)
            {
                hostname = value.toString();
            }
            if ((value = properties.get("port")) != null)
            {
                port = Integer.parseInt(value.toString());
            }
            if ((value = properties.get("proxy-user")) != null)
            {
                user = value.toString();
            }
            if ((value = properties.get("proxy-password")) != null)
            {
                passwd = value.toString();
            }
            if ((value = properties.get("ClientId")) != null)
            {
                client_id = value.toString();
            }
            if ((value = properties.get("ClientSecret")) != null)
            {
                password = value.toString();
            }
            if ((value = properties.get("TenantId")) != null)
            {
                tenant_id = value.toString();
            }
        }*/

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //  credsProvider.setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(user, passwd));

        //    System.out.println("proxy host is " + hostname);
        //  CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        // Trust own CA and all self-signed certs
        TrustManager trustManager = new EasyTrustManager();
        //  KeyManager[] keyManagers = null;

        SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
        // SSLContext sslcontext = SSLContexts.custom().build();
        sslcontext.init(new KeyManager[0], new TrustManager[] { trustManager }, new SecureRandom());
        //SSLContext sslcontext = SSLContexts.custom()
        //      .loadTrustMaterial(new File("my.keystore"), "nopassword".toCharArray(), new TrustSelfSignedStrategy()).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" }, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        clientBuilder.setSSLSocketFactory(sslsf);
        clientBuilder.setDefaultCredentialsProvider(credsProvider);

        // CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://login.windows.net/" + tenant_id + "/oauth2/token");

        //  HttpHost proxy = new HttpHost(hostname, port, "http");
        //    clientBuilder.setProxy(proxy);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("resource", ARMResource));
        urlParameters.add(new BasicNameValuePair("client_id", client_id));
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_secret", password));
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(postParams);

        //  RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        // httpPost.setConfig(config);
        CloseableHttpClient httpclient = clientBuilder.build();
        CloseableHttpResponse httpResponse = httpclient.execute(httpPost);

        System.out.println("POST Response Status:: " + httpResponse.getStatusLine().getStatusCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null)
        {
            response.append(inputLine);
        }
        reader.close();

        // print result
        System.out.println(response.toString());
        httpclient.close();

    }

}
