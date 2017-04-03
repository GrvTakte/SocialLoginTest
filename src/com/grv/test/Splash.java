package com.grv.test;

import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Toolbar;
import java.io.IOException;

/**
 *
 * @author gaurav
 */
public class Splash  extends Form{
    public Splash() throws IOException{
        Toolbar tb = new Toolbar();
        this.getTitleArea().setUIID("Container");
        this.getUnselectedStyle().setBgImage(Image.createImage("/SplashScreen.jpg"));
    }
}