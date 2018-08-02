package czc.wxphonenumberhelper.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;
import czc.wxphonenumberhelper.util.ToastUtil;

/**
 * Created by czc on 2017/6/21.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
    }

    protected abstract void setContentView();
    protected abstract void initData();

    protected void initView() {

    }

    protected void setListener(){

    };

    protected void toast(String msg){
        ToastUtil.showToast(this,msg);
    }

    protected void log(String log){
        Log.i("czc",log);
    }

    protected void redirect(Class targetClass){
        startActivity(new Intent(this,targetClass));
    }

    protected void back(){
        finish();
    }

    protected void hideSystemKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
