package czc.wxphonenumberhelper.activity;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.base.BaseActivity;
import czc.wxphonenumberhelper.model.FileManager;

public class HelpActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView mWebView;
    private String mFileLink;
    private ProgressDialog mDialog;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_help);
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("加载中，请稍等···");
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initData() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mDialog.dismiss();
                } else {
                    mDialog.show();
                }
            }
        });

        BmobQuery<FileManager> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("fileType", "help_html");
        bmobQuery.findObjects(new FindListener<FileManager>() {
            @Override
            public void done(List<FileManager> list, BmobException e) {
                if (e == null && list != null && list.size() >= 0) {
                    FileManager fm = list.get(0);
                    mFileLink = fm.fileLink;
                    if (!TextUtils.isEmpty(mFileLink)) {
                        mWebView.loadUrl(mFileLink);
                    }
                } else {
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    mWebView.loadUrl("file:///android_asset/help.html");
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }else{
            mWebView.stopLoading();
            finish();
        }
    }
}
