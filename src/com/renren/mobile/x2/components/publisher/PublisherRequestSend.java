package com.renren.mobile.x2.components.publisher;

import android.text.TextUtils;
import android.util.Log;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.network.mas.*;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: xiaoguang
 * Date: 12-11-7
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public class PublisherRequestSend {

    private static UGC makeFeedRequests(INetResponse response, String textContent, byte[] bitmapBytes, String voiceFile,
                                       UGCTagModel tagModel, UGCPlaceModel placeModel) {
        INetRequest voiceRequest = TextUtils.isEmpty(voiceFile)? null : getVoiceRequest(voiceFile);
        INetRequest imageRequest = bitmapBytes != null? UGCManager.getInstance().getImageUploadRequest(bitmapBytes) : null;
        INetRequest textRequest;
        if(tagModel != null || placeModel != null) {
            textRequest = UGCManager.getInstance().getUGCUploadRequest(textContent, tagModel, placeModel);
        } else {
            textRequest = TextUtils.isEmpty(textContent)? null : UGCManager.getInstance().getTextUploadRequest(textContent);
        }
        UGC feed = new UGC(LoginManager.getInstance().getLoginInfo().mSchool_id,UGC.UGC_TYPE_FEED, null ,voiceRequest, imageRequest, textRequest, response);

        return feed;
    }

    public static void sendUGC(INetResponse response, String textContent, byte[] bitmapBytes, String voiceFile,
                               UGCTagModel tagModel, UGCPlaceModel placeModel) {
        UGCManager.getInstance().sendUGC(makeFeedRequests(response, textContent, bitmapBytes, voiceFile,
                tagModel, placeModel));
    }

//    public static UGCTagModel getTagModel(String name, String id, String icon, String desc) {
//        return new UGCTagModel(name, id, icon, desc);
//    }

    public static UGCPlaceModel getPlaceModel(String name, String latLon) {
        return new UGCPlaceModel(name, latLon);
    }

    private static INetRequest getVoiceRequest(String mFileName) {
        if(!TextUtils.isEmpty(mFileName) && mFileName.length() > 0 ) {
            Log.d("jason", "send");
            File a = new File(mFileName);
            byte[] bytes = new byte[(int)a.length()];
            FileInputStream fis;
            try {
                fis = new FileInputStream(a);
                fis.read(bytes);
                fis.close();
                Log.d("jason","length" + a.length() +"|" + bytes.length);
            } catch(Exception e){
                Log.d("jason","sss exception");
                e.printStackTrace();
            }

            long toId =      3000010698L;

            return UGCManager.getInstance().getVoiceUploadRequest(toId, "1", 1, "end", 20, bytes);
        }
        return null;
    }
}
