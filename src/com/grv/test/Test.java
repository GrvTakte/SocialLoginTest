package com.grv.test;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.ShareButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.AccessToken;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.io.Oauth2;
import com.codename1.social.FacebookConnect;
import com.codename1.social.GoogleConnect;
import com.codename1.social.Login;
import com.codename1.social.LoginCallback;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextArea;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import com.codename1.ui.Toolbar;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.table.TableLayout;
import com.grv.test.Splash;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Test {

    private Form current;
    private Resources theme;
    Form facebook;
    Toolbar tb;
    Image user;
    Label userLabel;
    Label username;
    Login userlogin;
    String clientId = "158093724691158";
    String redirectURI = "http://www.codenameone.com/";
    String clientSecret = "b71fac5700b5c42ec53616249f22f60b";
    Label proLabel;
    Label fname;
    Label fmail;
    Label fgender;
    Container container12;
    String TOKEN, token;
    String authToken;
    String googleApiKey = "AIzaSyAQPisfuXqGcS0LZ3bVYNgrV2KO6KNU17I";
    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature, uncomment if you have a pro subscription
        // Log.bindCrashProtection(true);
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        try{
        Splash spl = new Splash();
        spl.show();
        
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
            @Override
            public void run() {
                facebook.show();
            }
        } , 4000);
        }catch(IOException e){
            e.printStackTrace();
        }
       facebook = new Form("Facebook", new BoxLayout(BoxLayout.Y_AXIS));
        tb = new Toolbar();
        facebook.setToolbar(tb);
        
       user = theme.getImage("user.png");
        userLabel = new Label(user);
        username = new Label("Visitor");
        Button facebooklogin = new Button("Login with Facebook");
        Button linked = new Button("Login with LinkedIn");
        Button google = new Button("Login with Google");
        //linked.setUIID("linkedButton");
        
        linked.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {                
               Oauth2 auth2 = new Oauth2("https://www.linkedin.com/oauth/v2/authorization", 
                "814y00eawbr8p0", 
                "https://www.codenameone.com","r_basicprofile,r_emailaddress", 
                "https://www.linkedin.com/oauth/v2/accessToken", 
                "U5PL709CLg2lzxn3");

       //auth2.authenticate();
        auth2.showAuthentication(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent evt) {
                       AccessToken token = (AccessToken) evt.getSource();
                       
                       TOKEN = token.getToken();
                       ConnectionRequest req = new ConnectionRequest(){
                           @Override
                           protected void readResponse(InputStream input) throws IOException {
                               
                               InputStreamReader reader = new InputStreamReader(input);
                               JSONParser parser = new JSONParser();
                               Map<String, Object> parsed = parser.parseJSON(reader);
                               
                               String firstname = (String) parsed.get("firstName");
                               String lastname = (String) parsed.get("lastName");
                               String picture = (String) parsed.get("pictureUrl");
                               
                               username.setText(firstname + lastname);
                               userLabel.setIcon(URLImage.createToStorage((EncodedImage) user, picture, picture, URLImage.RESIZE_SCALE));
                               
                           }
                           
                       };
                       req.setPost(false);
                       req.setUrl("https://api.linkedin.com/v1/people/~:(first-name,last-name,id,num-connections,picture-url)");
                       req.addArgument("oauth2_access_token", TOKEN);
                       req.addArgument("format", "json");
                       NetworkManager.getInstance().addToQueue(req);
                       
//
                   }
               });
                Oauth2.setBackToParent(true);

       }
        });
        
        google.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                   String clientId = "1069114991038-0vq1apulom218vflkekpd61nfvhonfjn.apps.googleusercontent.com";
                String redirectURI = "https://www.codenameone.com/oauth2callback";
                String clientSecret = "8gViL8pLClkUgP0mp_QE1quZ";
                Login gc = GoogleConnect.getInstance();
                gc.setClientId(clientId);
                gc.setRedirectURI(redirectURI);
                gc.setClientSecret(clientSecret);
                gc.setCallback(new LoginCallback() {
                    public void loginSuccessful()
                    {
                        AccessToken token = gc.getAccessToken();
                        showGoogleUser(token.getToken());
                    }
                    
                    public void loginFailed(String errorMessage){
                        Dialog.show("Login Failed", errorMessage, "Ok", null);
                    }
                });
                
                if(!gc.nativeIsLoggedIn()){
                    gc.nativelogin();
                }else{
                    //get the token and now you can query the gplus API
                    showGoogleUser(gc.getAccessToken().getToken());
                }
               

            }
        });
        
        facebooklogin.addActionListener((evt) -> {
        
              Login fb = FacebookConnect.getInstance();
                fb.setClientId(clientId);
                fb.setRedirectURI(redirectURI);
                fb.setClientSecret(clientSecret);
                userlogin = fb;
            fb.setCallback(new LoginListener(LoginListener.FACEBOOK));
                if(!fb.isUserLoggedIn()){
                    fb.doLogin();
                }else{
                    showFacebookUser(fb.getAccessToken().getToken());
                }
        });
        
         Container container1 = BoxLayout.encloseY(userLabel,username);
        container1.setUIID("container1");
        tb.addComponentToSideMenu(container1);
        tb.addCommandToSideMenu("Home", FontImage.createMaterial(FontImage.MATERIAL_HOME, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
         tb.addCommandToSideMenu("Shop by Category", FontImage.createMaterial(FontImage.MATERIAL_ADD_SHOPPING_CART, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
          tb.addCommandToSideMenu("Todays Deals", FontImage.createMaterial(FontImage.MATERIAL_LOCAL_OFFER, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
           tb.addCommandToSideMenu("Your Orders", FontImage.createMaterial(FontImage.MATERIAL_BOOKMARK_BORDER, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
            tb.addCommandToSideMenu("Your Wish List", FontImage.createMaterial(FontImage.MATERIAL_LIST, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
             tb.addCommandToSideMenu("Your Account", FontImage.createMaterial(FontImage.MATERIAL_ACCOUNT_BOX, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
              tb.addCommandToSideMenu("Share", FontImage.createMaterial(FontImage.MATERIAL_CARD_GIFTCARD, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
//                  FacebookShare post = new FacebookShare();
//                  post.share("Hello from codename one");

                        final ShareButton share = new ShareButton();
                            share.setTextToShare("Hello from codename one");
                  
        });
               tb.addCommandToSideMenu("Setting", FontImage.createMaterial(FontImage.MATERIAL_SETTINGS, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
        });
                tb.addCommandToSideMenu("Logout", FontImage.createMaterial(FontImage.MATERIAL_BACKSPACE, UIManager.getInstance().getComponentStyle("TitleCommand")), (evt) -> {
                    
        });
        
                
        Tabs tab = new Tabs();
        Style s = UIManager.getInstance().getComponentStyle("Tab");
        FontImage icon1 = FontImage.createMaterial(FontImage.MATERIAL_VPN_KEY, s);
        FontImage icon2 = FontImage.createMaterial(FontImage.MATERIAL_LIST, s);
        FontImage icon3 = FontImage.createMaterial(FontImage.MATERIAL_ACCOUNT_BOX, s);

        Container container11 = BoxLayout.encloseY(facebooklogin,linked,google);
container12 = BoxLayout.encloseY(new SpanLabel("Some text directly in the tab2"));
Image pro = theme.getImage("user.png");
proLabel = new Label(pro);
Label uname = new Label("Name: ");
fname = new Label("");
Label umail = new Label("Email: ");
fmail = new Label("");
Label ugender = new Label("Gender: ");
fgender = new Label("");
Container profileContainer = TableLayout.encloseIn(2, uname,fname,umail,fmail,ugender,fgender);

Container container13 = BoxLayout.encloseY(proLabel,profileContainer);
        
        tab.addTab("Log In",icon1,container11 );
        tab.addTab("Wall",icon2, container12 );
        tab.addTab("User Profile",icon3, container13);
        facebook.add(tab);
    }
    
    
    private void showGoogleUser(String token){
        Dialog.show("Login","Login successful","Ok","");
        ConnectionRequest req = new ConnectionRequest();
        req.addRequestHeader("Authorization", "Bearer " + token);
        req.setUrl("https://www.googleapis.com/plus/v1/people/me");
        req.setPost(false);
        InfiniteProgress ip = new InfiniteProgress();
        Dialog d = ip.showInifiniteBlocking();
        NetworkManager.getInstance().addToQueueAndWait(req);
        d.dispose();
        byte[] data = req.getResponseData();
        JSONParser parser = new JSONParser();
        Map map = null;
        try {
            map = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String name = (String) map.get("displayName");
        Map im = (Map) map.get("image");
        String url = (String) im.get("url");
        username.setText(name);
       userLabel.setIcon(URLImage.createToStorage((EncodedImage) user, url, url, URLImage.RESIZE_SCALE)); 
       
       
        TextArea search = new TextArea();
        Button searchbutton = new Button("Search");
         container12.add(search);
         container12.add(searchbutton);
         
         searchbutton.addActionListener((evt) -> {
         String searchstr = search.getText();
         ConnectionRequest req2 = new ConnectionRequest()
         {
             @Override
             protected void readResponse(InputStream input) throws IOException {
                InputStreamReader reader = new InputStreamReader(input);
                JSONParser parser1 = new JSONParser();
                Map<String, Object> parsed2 = parser1.parseJSON(reader);
                Log.p(parsed2.toString());
                ArrayList array = (ArrayList)parsed2.get("items");
                JSONArray items_array = new JSONArray(array);
                
                
                ArrayList<String> arrayList = new ArrayList<>();
        try{
        JSONArray array2 = new JSONArray(items_array.toString());
        for(int i =0; i<array2.length() ; i++){
            JSONObject jsonobject = array2.getJSONObject(i);
                
               String name = jsonobject.getString("displayName");
               String image = (String) ((JSONObject) jsonobject.get("image")).getString("url");
               
                       Image searchimage = theme.getImage("user.png");
                        Label searchLabel = new Label(searchimage);
                        searchLabel.setIcon(URLImage.createToStorage((EncodedImage) user, image, image, URLImage.RESIZE_SCALE)); 
                        Label namelabel = new Label(name);
                       
                        container12.add(namelabel);
                        container12.add(searchLabel);
                    } 
        }catch (JSONException ex) {
                       ex.printStackTrace();
                    }
                }
             
             
         };
         req2.setPost(false);
         req.addRequestHeader("Authorization", "Bearer " + token);
         req2.setUrl("https://www.googleapis.com/plus/v1/people");
         req2.addArgument("query", search.getText());
         req2.addArgument("key", googleApiKey);
        InfiniteProgress ip2 = new InfiniteProgress();
        Dialog d2 = ip2.showInifiniteBlocking();
        NetworkManager.getInstance().addToQueueAndWait(req2);
        d2.dispose();
             
         });
         
       
       
       
    }
    
    
    
    public void showFacebookUser(String token){
        ConnectionRequest conn = new ConnectionRequest(){
            @Override
            protected void readResponse(InputStream input) throws IOException {
                JSONParser parser = new JSONParser();
                Map<String, Object> parsed = parser.parseJSON(new InputStreamReader(input, "UTF-8"));
                String email = null;
                   if(email == null){
                        email=" ";
                    }
              email = (String) parsed.get("email");
               String name = (String) parsed.get("name");
                String first_name = (String) parsed.get("first_name");
                String last_name = (String) parsed.get("last_name");
                  String gender = (String) parsed.get("gender");
                  String id = (String) parsed.get("id");
                String image = (String) ((Map) ((Map) parsed.get("picture")).get("data")).get("url").toString();
//                 ArrayList<String>  data_arr1= (ArrayList) ((Map) parsed.get("feed")).get("data");
//                JSONArray array = new JSONArray(data_arr1);
                ArrayList<String> array = (ArrayList) ((Map) parsed.get("taggable_friends")).get("data");
                JSONArray j_array = new JSONArray(array);
                
                  Log.p("First NAme : " + first_name);
                Log.p("Last Name : " + last_name);
       Log.p("Email : " + email);
        Log.p("Full Name : " + name);
          Log.p("Gender : " + gender);
             Log.p("Picture : " +image);
             Log.p("ID: " +id);
             Log.p("FRIENDS: " + j_array.toString());
         username.setText(name);
        userLabel.setIcon(URLImage.createToStorage((EncodedImage) user, "Small_"+image, image, URLImage.RESIZE_SCALE));     
      proLabel.setIcon(URLImage.createToStorage((EncodedImage) user, image, image, URLImage.RESIZE_SCALE)); 
        fname.setText(name);
        fmail.setText(email);
        fgender.setText(gender);
            }
        };
         conn.setPost(false);
        conn.setUrl("https://graph.facebook.com/v2.8/me");
        conn.addArgumentNoEncoding("access_token", token); //this statement is used to patch access token with url
        conn.addArgumentNoEncoding("fields", "email,name,first_name,last_name,gender,picture.width(512).height(512),feed{name,full_picture,message,story},posts,taggable_friends");
        //above statement is used to provide permission through url so server send data with respect ot permissions.
        NetworkManager.getInstance().addToQueue(conn);
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = Display.getInstance().getCurrent();
        }
    }
    
    public void destroy() {
    }
    
    public class LoginListener extends LoginCallback {
    public static final int FACEBOOK = 0;

        public static final int GOOGLE = 1;

        private int loginType;

        public LoginListener(int loginType) {
            this.loginType = loginType;
        }

        public void loginSuccessful() {

            try {
                AccessToken token = userlogin.getAccessToken();
                if (loginType == FACEBOOK) {
                    showFacebookUser(token.getToken());
                } else if (loginType == GOOGLE) {
                    showGoogleUser(token.getToken());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void loginFailed(String errorMessage) {
            Dialog.show("Login Failed", errorMessage, "Ok", null);
        }
    }
}