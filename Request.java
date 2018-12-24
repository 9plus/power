import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Request {
    public static  void main(String[] args){
        try {
            URL url = new URL("http://bbs.byr.cn/oauth2/authorize?" +
                    "response_type=token&" +
                    "client_id=a698acf94ee3d402f983a01fdb34e601&" +
                    "redirect_uri=http://bbs.byr.cn/oauth2/callback&" +
                    "state=35f7879b051b0bcb77a015977f5aeeeb&" +
                    "username=2014210304&" +
                    "password=gyxzd5200");
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) { // 读取数据
                result.append(line + "\n");
            }
            connection.disconnect();

            System.out.println(result.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
