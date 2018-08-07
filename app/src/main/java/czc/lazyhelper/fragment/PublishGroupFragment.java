package czc.lazyhelper.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import czc.lazyhelper.R;
import czc.lazyhelper.base.BaseFragment;
import czc.lazyhelper.constant.Const;
import czc.lazyhelper.model.GroupInfo;
import czc.lazyhelper.util.ToolsUtil;
import rx.Subscription;

/**
 * Created by czc on 2017/8/2.
 */

public class PublishGroupFragment extends BaseFragment {

    @BindView(R.id.btn_choose_photo)
    Button mBtnChoose;

    @BindView(R.id.iv_ercode)
    ImageView mIvIcon;

    @BindView(R.id.et_group_title)
    EditText mEtGroupTitle;

    @BindView(R.id.et_group_wx_number)
    EditText mEtGroupWxNumber;

    @BindView(R.id.et_group_wx_introduce)
    EditText mEtGroupIntroduce;

    private List<Uri> mImageUriList;
    private ProgressDialog mDialog;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_publish_group, null);
        ButterKnife.bind(this, view);
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("正在发布···");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    if (subscription != null && subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                    }
                }
            }
        });
        return view;
    }

    @OnClick({R.id.btn_choose_photo, R.id.iv_ercode})
    void onClickChoosePhoto() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .theme(R.style.Matisse_Dracula)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(1001);
    }

    @OnClick(R.id.btn_publish)
    void onClickPublish() {
        final String title = mEtGroupTitle.getText().toString().trim();
        final String wxNumber = mEtGroupWxNumber.getText().toString().trim();
        final String introduce = mEtGroupIntroduce.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            toast("请输入标题");
        } else if (TextUtils.isEmpty(wxNumber)) {
            toast("请输入群主微信号");
        } else if (TextUtils.isEmpty(introduce)) {
            toast("请输入群介绍");
        } else if (mImageUriList.isEmpty()) {
            toast("请选择照片");
        } else {
            String realFilePath = ToolsUtil.getRealFilePath(getContext(), mImageUriList.get(0));
            File file = new File(realFilePath);
            if (file.exists()) {
                final BmobFile bmobFile = new BmobFile(file);
                subscription = bmobFile.uploadblock(new UploadFileListener() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mDialog.show();
                    }

                    @Override
                    public void onFinish() {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void done(BmobException e) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (e == null) {
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            GroupInfo groupInfo = new GroupInfo();
                            groupInfo.title = title;
                            groupInfo.wxNumber = wxNumber;
                            groupInfo.groupIntroduce = introduce;
                            groupInfo.photo = bmobFile;
                            groupInfo.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        toast("发布成功");
                                        mHost.changeFragment(Const.GROUP_LIST_FRAMENT);
                                        clearAllStatus();
                                    } else {
                                        toast("发布失败");
                                    }
                                }
                            });
                        } else {
                            toast("上传文件失败");
                        }
                    }
                });
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            mImageUriList = Matisse.obtainResult(data);
            Log.i("czc", mImageUriList.toString());
            mIvIcon.setImageURI(mImageUriList.get(0));
            mIvIcon.setVisibility(View.VISIBLE);
            mBtnChoose.setVisibility(View.GONE);
        }
    }

    public void clearAllStatus() {
        Bitmap drawingCache = mIvIcon.getDrawingCache();
        if (drawingCache != null) {
            drawingCache.recycle();
        }

        mIvIcon.setImageBitmap(null);
        mIvIcon.setVisibility(View.GONE);
        mBtnChoose.setVisibility(View.VISIBLE);

        mImageUriList.clear();
        mEtGroupTitle.setText("");
        mEtGroupWxNumber.setText("");
        mEtGroupIntroduce.setText("");
    }
}
