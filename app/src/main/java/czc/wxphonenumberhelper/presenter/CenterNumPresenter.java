package czc.wxphonenumberhelper.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import czc.wxphonenumberhelper.constant.Const;
import czc.wxphonenumberhelper.view.ICenterNumView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by czc on 2017/6/25.
 */

public class CenterNumPresenter implements BasePresenter {
    private List<String> mPhoneList = new ArrayList<>();
    private ICenterNumView mView;

    public CenterNumPresenter(ICenterNumView view) {
        mView = view;
    }

    @Override
    public void initData() {

    }

    public void getDateFromNet(Bundle bundle){
        if (bundle != null) {
            String cityPinYin = bundle.getString(Const.KEY_CITY_PIN_YIN);
            String hd = bundle.getString(Const.KEY_HD);
            String url = String.format("http://www.guisd.com/hd/%s/%s/", hd, cityPinYin);
            Log.i("czc",url);
            OkHttpUtils.get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            mView.error();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Document doc = Jsoup.parse(response);
                            Element dl = doc.getElementById("heilongjiang");
                            Elements a = dl.getElementsByTag("a");
                            for (Element link : a) {
                                String text = link.text();
                                if (!TextUtils.isEmpty(text)) {
                                    Log.i("czc", text);
                                    if (text.length() >= 7) {
                                        mPhoneList.add(text.substring(3, 7));
                                    }
                                }
                            }
                            Log.i("czc", mPhoneList.size() + "个");
                            mView.success(mPhoneList);
                        }

                        @Override
                        public void onBefore(Request request, int id) {
                            mView.begin();
                        }

                        @Override
                        public void onAfter(int id) {
                            mView.end();
                        }
                    });
        }
    }
}
