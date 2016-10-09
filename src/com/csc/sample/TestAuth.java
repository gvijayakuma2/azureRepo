package com.csc.sample;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.Future;

import com.servicemesh.io.http.HttpClientFactory;
import com.servicemesh.io.http.HttpMethod;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;

public class TestAuth
{

    public static void main(String args[]) throws Exception
    {

        org.apache.log4j.BasicConfigurator.configure();

        IHttpClient httpClient = null;
        try
        {
            // Proxy information:
            /*  String user = "smadmin";
            String passwd = "M3sh@dmin!";
            String hostname = "192.168.33.148";
            int port = 3128;*/

            //  Azure information:
            // String ARMResource = "https://management.core.windows.net/";

            String ARMResource = "https://management.azure.com/";

            InputStream is = getContentAsStream(
                    "resource=https://management.azure.com/&client_id=cec62bad-b4fa-4cd4-9f03-d4e37e91072c&grant_type=client_credentials&client_secret=5kiFlnm1nuzRki7UCHO1p+itvuKQ6JrkjRo7rHvyvR8=");
            String con = getStringFromInputStream(is);
            System.out.println("convered content is " + con);

            //  We need to prompt for these:
            /* String client_id = "0eaa67e7-ff11-4dd0-b735-62661e32cce2";
            String tenant_id = "04ead8fa-c4ad-4361-a1a3-c2034f687ae4";
            String password = "M3sh@dmin!";*/

            // String ARMResource = "https://management.core.windows.net/";
            String client_id = "cec62bad-b4fa-4cd4-9f03-d4e37e91072c";
            String tenant_id = "04ead8fa-c4ad-4361-a1a3-c2034f687ae4";
            String password = "5kiFlnm1nuzRki7UCHO1p+itvuKQ6JrkjRo7rHvyvR8=";

            /* File file = new File("azurerm.properties");
            Properties properties = new Properties();
            try
            {
                InputStream is = new FileInputStream(file);
                properties.load(is);
            }
            catch (Exception ex)
            {
                System.out.println("Could not load azurerm.properties");
                return; // no config - we're out of here
            }
            
            Object value = null;
            if ((value = properties.get("proxy")) != null)
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
            }*/

            /* String SPNPayload = "resource=" + ARMResource + "&client_id=" + client_id
                    + "&grant_type=client_credentials&client_secret=" + password;*/

            String SPNPayload = "resource=" + URLEncoder.encode(ARMResource) + "&client_id=" + URLEncoder.encode(client_id)
                    + "&grant_type=" + URLEncoder.encode("client_credentials") + "&client_secret=" + URLEncoder.encode(password);

            String TokenEndpoint = "https://login.windows.net/" + tenant_id + "/oauth2/token";

            IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.POST, new URI(TokenEndpoint));
            IHttpHeader contentTypeHeaderOut =
                    HttpClientFactory.getInstance().createHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setHeader(contentTypeHeaderOut);
            request.setContent(SPNPayload);

            //  ProxyType proxyType = ProxyType.HTTP_PROXY;
            //    Proxy proxy = new Proxy(hostname, port, proxyType, null, user, passwd);

            IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
            //    builder.setProxy(proxy);
            httpClient = HttpClientFactory.getInstance().getClient(builder.build());
            Future<IHttpResponse> future = httpClient.execute(request);
            IHttpResponse httpResponse = future.get();
            if (httpResponse.getStatusCode() == 200)
            {
                String data = httpResponse.getContent();
                System.out.println("Http Status: ********" + data);
            }
            else
            {
                System.out.println("Http Status: failure  *****************" + httpResponse.getStatusCode());
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
        finally
        {
            if (httpClient != null)
            {
                httpClient.close();
            }
        }
    }

    public static InputStream getContentAsStream(String _stringContent)
    {
        InputStream is = null;
        long _contentLength = 0;
        byte[] stringBytes = _stringContent.getBytes();
        _contentLength = stringBytes.length;
        is = new ByteArrayInputStream(stringBytes);
        System.out.println("content lenght " + _contentLength);
        return is;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is)
    {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

}